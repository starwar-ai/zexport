package com.syj.eplus.module.oa.service.generalreimb;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbFeeShareRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.convert.general.GeneralReimbConvert;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import com.syj.eplus.module.oa.service.reimbrepaydetail.ReimbRepayDetailService;
import com.syj.eplus.module.oa.service.reimbshare.ReimbShareService;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.FEE_SHARE_NOT_EMPTY;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 15:53
 */
@Service
public class GeneralReimbServiceImpl implements GeneralReimbService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String PROCESS_DEFINITION_KEY = "oa_general_reimb";
    @Resource
    private ReimbService reimbService;
    @Resource
    private ReimbShareService reimbShareService;

    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    private CustApi custApi;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private VenderApi venderApi;

    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CompanyApi companyApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ApplyService applyService;
    @Resource
    private RateApi rateApi;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGeneralReimb(GeneralReimbSaveReqVO createReqVO) {
        ReimbDO reimbDO = GeneralReimbConvert.INSTANCE.convertReimbDO(createReqVO);
        Long actualUserId = createReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        reimbDO.setAuxiliaryType(BooleanEnum.NO.getValue());
        Long generalReimbId = reimbService.createReimb(reimbDO);
        List<ReimbRepayDetailSaveReqVO> batchInsertList = dealReimbRepayDetail(createReqVO, generalReimbId);
        if (CollUtil.isNotEmpty(batchInsertList)) {
            batchInsertList.forEach(s -> {
                s.setReimbId(generalReimbId);
                s.setRepaySourceType(RepaySourceTypeEnum.GENERAL_REIMB.getType());
                s.setRepaySourceCode(reimbDO.getCode());
                s.setRepayUser(actualUser);
            });
            reimbRepayDetailService.batchCreateReimbRepayDetail(batchInsertList);
        }
        //新增费用归属
        List<FeeShareReqDTO> feeShareList = createReqVO.getFeeShare();
        // 人民币金额
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        Integer auxiliaryType = createReqVO.getAuxiliaryType();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            dealFeeShare(feeShareList, auxiliaryType, feeShareAmount, createReqVO.getCompanyId(), generalReimbId, reimbDO);
            submitTask(generalReimbId);
        }
        //更新费用申请状态
        List<Long> applyIdList = createReqVO.getApplyIdList();
        if (CollUtil.isNotEmpty(applyIdList)) {
            applyService.batchUpdateIsApplyExpense(applyIdList,ApplyExpenseEnum.CREATE.getValue());
        }
        return generalReimbId;
    }

    private List<ReimbRepayDetailSaveReqVO> dealReimbRepayDetail(GeneralReimbSaveReqVO createReqVO, Long reimbId) {
        List<ReimbRepayDetailSaveReqVO> batchInsertList = new ArrayList<>();
        List<SimpleLoanAppRespDTO> loanAppRespDTOList = createReqVO.getLoanAppList();
        // 计算当前报销总金额
        List<JsonAmount> totalAmountList = createReqVO.getAmountList();
        if (Objects.isNull(totalAmountList) || totalAmountList.isEmpty()) {
            throw exception(TOTAL_AMOUNT_NULL);
        }
        final BigDecimal[] totalAmount = {totalAmountList.get(0).getAmount()};
        String totalCurrency = totalAmountList.get(0).getCurrency();
        // 如果选择还款单则分别创建对应报销还款详情
        if (CollUtil.isNotEmpty(loanAppRespDTOList)) {
            loanAppRespDTOList.forEach(simpleLoanAppRespDTO -> {
                JsonAmount outstandingJsonAmount = simpleLoanAppRespDTO.getOutstandingAmount();
                // 当前借款单没有剩余未还款金额不处理
                if (Objects.isNull(outstandingJsonAmount)) {
                    return;
                }
                String currency = outstandingJsonAmount.getCurrency();
                if (!Objects.equals(currency, totalCurrency)) {
                    throw exception(CURRENCY_ERROR);
                }
                BigDecimal outstandingAmount = outstandingJsonAmount.getAmount();
                ReimbRepayDetailSaveReqVO reimbRepayDetailSaveReqVO = new ReimbRepayDetailSaveReqVO()
                        .setLoanId(simpleLoanAppRespDTO.getId())
                        .setReimbId(reimbId)
                        .setRepaySourceType(RepaySourceTypeEnum.ENTERTAIN_REIMB.getType())
                        .setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
                // 当前报销总额大于等于借款单未还款金额
                if (totalAmount[0].compareTo(outstandingAmount) > 0) {
                    // 实际报销金额为当前报销总额-当前借款单未还款金额
                    totalAmount[0] = totalAmount[0].subtract(outstandingAmount);
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(outstandingAmount));
                } else {
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(totalAmount[0]));
                }
                batchInsertList.add(reimbRepayDetailSaveReqVO);
            });
        }

        return batchInsertList;
    }

    private void dealFeeShare(List<FeeShareReqDTO> feeShareList, Integer auxiliaryType, JsonAmount feeShareAmount, Long companyId, Long generalReimbId, ReimbDO reimbDO) {
        if (BooleanEnum.YES.getValue().equals(auxiliaryType)) {
            if (CollUtil.isEmpty(feeShareList)) {
                throw exception(FEE_SHARE_NOT_EMPTY);
            }
            Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
            List<FeeShareReqDTO> feeList = new ArrayList<>();

            for (FeeShareReqDTO feeShare : feeShareList) {
                feeShare.setCompanyId(companyId);
                if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                    SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
                    if (Objects.isNull(simpleCompanyDTO)) {
                        throw exception(COMPANY_NOT_EXISTS);
                    }
                    feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
//                feeShare.setAmount(feeShareAmount);
                feeShare.setBusinessType(FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue());
                feeShare.setBusinessCode(reimbDO.getCode());
                feeShare.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
                feeShare.setBusinessId(generalReimbId);
                feeShare.setInputUser(reimbDO.getReimbUser());
                feeShare.setStatus(BooleanEnum.YES.getValue());
                reimbDO.setAuxiliaryType(BooleanEnum.YES.getValue());
                feeList.add(feeShare);
            }
            reimbService.updateReimb(reimbDO);
            feeShareApi.updateFeeShareByDTOList(feeList);
        } else {
            var feeShare = new GeneralReimbFeeShareRespVO();
            Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
            feeShare.setCompanyId(companyId);
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
                if (Objects.isNull(simpleCompanyDTO)) {
                    throw exception(COMPANY_NOT_EXISTS);
                }
                feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
            feeShare.setBusinessType(FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue());
            feeShare.setBusinessCode(reimbDO.getCode());
            feeShare.setInputUser(reimbDO.getReimbUser());
            feeShare.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
            feeShare.setBusinessId(generalReimbId);
            feeShare.setStatus(BooleanEnum.NO.getValue());
            feeShareApi.updateFeeShareByDTO(feeShare);
            reimbDO.setAuxiliaryType(BooleanEnum.NO.getValue());
        }
        reimbService.updateReimb(reimbDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateGeneralReimb(GeneralReimbSaveReqVO updateReqVO) {
        ReimbDO reimbDO = GeneralReimbConvert.INSTANCE.convertReimbDO(updateReqVO);
        Long actualUserId = updateReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        int result = reimbService.updateReimb(reimbDO);
        //处理费用归属
        List<FeeShareReqDTO> feeShareList = updateReqVO.getFeeShare();
        // 人民币金额
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        dealFeeShare(feeShareList, updateReqVO.getAuxiliaryType(), feeShareAmount, updateReqVO.getCompanyId(), updateReqVO.getId(), reimbDO);
        // 处理报销还款详情
        reimbRepayDetailService.batchDeleteReimbRepayDetailByReimbId(reimbDO.getId());
        // 如果有还款记录则重新插入
        if (BooleanEnum.YES.getValue().equals(reimbDO.getRepayFlag())) {
            List<ReimbRepayDetailSaveReqVO> reimbRepayDetailSaveReqVOS = dealReimbRepayDetail(updateReqVO, updateReqVO.getId());
            if (CollUtil.isNotEmpty(reimbRepayDetailSaveReqVOS)) {
                reimbRepayDetailSaveReqVOS.forEach(s -> {
                    s.setRepaySourceCode(reimbDO.getCode());
                    s.setRepayUser(actualUser);
                });
                reimbRepayDetailService.batchCreateReimbRepayDetail(reimbRepayDetailSaveReqVOS);
            }
        }

        if (result > 0 && SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateReqVO.getId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteGeneralReimb(Long id) {
        // 删除
        int result = reimbService.deleteReimb(id);
        // 清除报销还款信息
        reimbRepayDetailService.batchDeleteReimbRepayDetailByReimbId(id);
        if (result <= 0) {
            throw exception(GENERAL_REIMVB_DELETE_ERROR);
        }
        // 删除费用归属
        reimbShareService.deleteReimbShareByReimbId(id,FeeShareSourceTypeEnum.GENERAL_REIMBURSE);
        return result;
    }

    @Override
    public GeneralReimbRespVO getGeneralReimb(GeneralReimbDetailReq req) {
        //将不同入口的参数转换为generalReimbId
        Long generalReimbId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getGeneralReimbId();
        if (Objects.isNull(generalReimbId)) {
            logger.error("[一般费用报销详情]未获取到一般费用报销id");
            return null;
        }
        ReimbRespVO reimbResp = reimbService.getReimbResp(generalReimbId);
        if (Objects.isNull(reimbResp)) {
            return null;
        }
        GeneralReimbRespVO generalGeneralReimbRespVO = GeneralReimbConvert.INSTANCE.convertGeneralReimbRespByReimbResp(reimbResp);
        List<ApplyRespVO> applyList = applyService.getApplyListByIdList(reimbResp.getApplyIdList());
        if (CollUtil.isNotEmpty(applyList)){
            generalGeneralReimbRespVO.setApplyList(applyList);
        }
        //查询费用归属信息
        List<FeeShareRespDTO> feeShare = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.GENERAL_REIMBURSE, generalGeneralReimbRespVO.getCode());
        generalGeneralReimbRespVO.setFeeShare(feeShare);

        //未提交直接返回即可
        if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(reimbResp.getAuditStatus())) {
            return generalGeneralReimbRespVO;
        }
        return generalGeneralReimbRespVO;
    }

    @Override
    public PageResult<GeneralReimbRespVO> getGeneralReimbPage(ReimbPageReqVO pageReqVO) {
        PageResult<ReimbRespVO> generalReimbPage = reimbService.getReimbPage(pageReqVO, FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue());

        PageResult<GeneralReimbRespVO> generalReimbRespVOPageResult = GeneralReimbConvert.INSTANCE.convertGeneralPageResult(generalReimbPage);

        List<GeneralReimbRespVO> list = generalReimbRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            List<String> codeList = list.stream().map(GeneralReimbRespVO::getCode).distinct().toList();
            Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue(), codeList);
            if (CollUtil.isNotEmpty(feeShareMap)) {
                list.forEach(s -> {
                    List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                    s.setFeeShare(feeShareRespDTO);
                });
            }
        }

        return generalReimbRespVOPageResult.setList(list);
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));

    }

    @Override
    public void submitTask(Long reimbId) {
        reimbService.submitTask(reimbId, PROCESS_DEFINITION_KEY);
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));

    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        return reimbService.getFeeShareAmountByCode(businessCode);
    }
}
