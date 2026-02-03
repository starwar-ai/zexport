package com.syj.eplus.module.oa.service.entertain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import com.syj.eplus.framework.common.util.CollectorsUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.convert.entertain.EntertainConvert;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.dal.mysql.expenseapp.ExpenseAppMapper;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 15:53
 */
@Service
public class EntertainReimbServiceImpl implements EntertainReimbService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String PROCESS_DEFINITION_KEY = "oa_entertain_reimb";
    @Resource
    private ReimbService reimbService;
    @Resource
    private ReimbShareService reimbShareService;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    private CustApi custApi;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;
    @Resource
    private RateApi rateApi;

    @Resource
    private ExpenseAppMapper expenseAppMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ApplyService applyService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntertainReimb(EntertainReimbSaveReqVO createReqVO) {

        Map<String, BigDecimal> stringBigDecimalMap = calcTotalAmount(createReqVO.getReimbDetailList());
        List<JsonAmount> totalAmountList = new ArrayList<>();
        if (CollUtil.isNotEmpty(stringBigDecimalMap)) {
            stringBigDecimalMap.forEach((k, v) -> {
                totalAmountList.add(new JsonAmount().setCurrency(k).setAmount(v));
            });
        }
        List<JsonAmount> totalAmountListReq = createReqVO.getTotalAmountList();
        if (CollUtil.isEmpty(totalAmountListReq)) {
            throw exception(TRAVEL_AMOUNT_NOT_NULL);
        }
        Map<String, BigDecimal> loanAmountMap = getLoanAmountMap(createReqVO.getRepayAmountList());
        // 如果计算出的总报销金额跟前台传入不一致则抛出异常
        long count = totalAmountListReq.stream().filter(amount -> {
            BigDecimal anountValue = stringBigDecimalMap.get(amount.getCurrency());
            BigDecimal loanAmount = Objects.isNull(loanAmountMap.get(amount.getCurrency()))?BigDecimal.ZERO:loanAmountMap.get(amount.getCurrency());
            return Objects.isNull(anountValue) || !NumUtil.sub(anountValue,loanAmount).equals(amount.getAmount());
        }).count();
        if (count > 0) {
            throw exception(TRAVEL_AMOUNT_EXCEPTION);
        }
        ReimbDO reimbDO = EntertainConvert.INSTANCE.convertReimbDO(createReqVO);
        Long actualUserId = createReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        Long entertainReimbId = reimbService.createReimb(reimbDO);

        List<ReimbRepayDetailSaveReqVO> batchInsertList = dealReimbRepayDetail(createReqVO, entertainReimbId);
        if (CollUtil.isNotEmpty(batchInsertList)) {
            batchInsertList.forEach(s -> {
                s.setReimbId(entertainReimbId);
                s.setRepaySourceType(RepaySourceTypeEnum.ENTERTAIN_REIMB.getType());
                s.setRepaySourceCode(reimbDO.getCode());
                s.setRepayUser(actualUser);
            });
            reimbRepayDetailService.batchCreateReimbRepayDetail(batchInsertList);
        }

        // 人名币金额
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        Integer auxiliaryType = createReqVO.getAuxiliaryType();
        List<FeeShareReqDTO> feeShareList = createReqVO.getFeeShare();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            dealReimbShare(feeShareList, auxiliaryType, entertainReimbId, feeShareAmount, reimbDO);
            submitTask(entertainReimbId);
        }

        //更新费用申请状态
        List<Long> applyIdList = createReqVO.getApplyIdList();
        if (CollUtil.isNotEmpty(applyIdList)) {
            applyService.batchUpdateIsApplyExpense(applyIdList,ApplyExpenseEnum.CREATE.getValue());
        }
        return entertainReimbId;
    }

    /**
     * 获得还款金额
     * @param repayAmountList 还款金额列表
     * @return 还款金额
     */
    private Map<String, BigDecimal> getLoanAmountMap(List<JsonAmount> repayAmountList){
        if (CollUtil.isEmpty(repayAmountList)){
            return Map.of();
        }
        return repayAmountList.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(JsonAmount::getCurrency,Collectors.reducing(BigDecimal.ZERO,JsonAmount::getAmount,BigDecimal::add)));
    }
    private List<ReimbRepayDetailSaveReqVO> dealReimbRepayDetail(EntertainReimbSaveReqVO createReqVO, Long reimbId) {
        List<ReimbRepayDetailSaveReqVO> batchInsertList = new ArrayList<>();
        List<SimpleLoanAppRespDTO> loanAppRespDTOList = createReqVO.getLoanAppList();
        // 计算当前报销总金额
        List<JsonAmount> totalAmountList = createReqVO.getAmountList();
        if (Objects.isNull(totalAmountList) || totalAmountList.isEmpty()) {
            throw exception(TOTAL_AMOUNT_NULL);
        }
         AtomicReference<BigDecimal> totalAmountAto = new AtomicReference<>(totalAmountList.get(0).getAmount());
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
                        .setRepayAmount(simpleLoanAppRespDTO.getOutstandingAmount())
                        .setRepaySourceType(RepaySourceTypeEnum.ENTERTAIN_REIMB.getType())
                        .setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
                // 当前报销总额大于等于借款单未还款金额
                if (totalAmountAto.get().compareTo(outstandingAmount) >= 0) {
                    // 实际报销金额为当前报销总额-当前借款单未还款金额
                    totalAmountAto.set(totalAmountAto.get().subtract(outstandingAmount));
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(outstandingAmount));
                } else {
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(totalAmountAto.get()));
                }
                batchInsertList.add(reimbRepayDetailSaveReqVO);
            });
        }

        return batchInsertList;
    }

    private void dealReimbShare(List<FeeShareReqDTO> feeShareList, Integer auxiliaryType, Long entertainReimbId, JsonAmount feeShareAmount, ReimbDO reimbDO) {
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        //新增费用归属
        if (BooleanEnum.YES.getValue().equals(auxiliaryType)) {
            if (CollUtil.isEmpty(feeShareList)) {
                throw exception(FEE_SHARE_NOT_EXISTS);
            }
            for (FeeShareReqDTO feeShare : feeShareList) {
                feeShare.setBusinessType(FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE.getValue());
                feeShare.setBusinessCode(reimbDO.getCode());
                feeShare.setCompanyId(reimbDO.getCompanyId());
                feeShare.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
                feeShare.setBusinessId(entertainReimbId);
                feeShare.setInputUser(reimbDO.getReimbUser());
//                feeShare.setAmount(feeShareAmount);
                feeShare.setStatus(BooleanEnum.YES.getValue());
                if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                    SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(reimbDO.getCompanyId());
                    if (Objects.isNull(simpleCompanyDTO)) {
                        throw exception(COMPANY_NOT_EXISTS);
                    }
                    feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
            feeShareApi.updateFeeShareByDTOList(feeShareList);
            reimbDO.setAuxiliaryType(BooleanEnum.YES.getValue());
            reimbService.updateReimb(reimbDO);
        } else {

            FeeShareReqDTO feeShare = new FeeShareReqDTO();
//            feeShare.setAmount(feeShareAmount);
            feeShare.setBusinessType(FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE.getValue());
            feeShare.setBusinessCode(reimbDO.getCode());
            feeShare.setCompanyId(reimbDO.getCompanyId());
            feeShare.setBusinessId(entertainReimbId);
            feeShare.setInputUser(reimbDO.getReimbUser());
            feeShare.setStatus(BooleanEnum.NO.getValue());
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(reimbDO.getCompanyId());
                if (Objects.isNull(simpleCompanyDTO)) {
                    throw exception(COMPANY_NOT_EXISTS);
                }
                feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
                feeShareApi.updateFeeShareByDTO(feeShare);
                reimbDO.setAuxiliaryType(BooleanEnum.NO.getValue());
                reimbService.updateReimb(reimbDO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEntertainReimb(EntertainReimbSaveReqVO updateReqVO) {
//        List<ReimbShareSaveReqVO> auxiliaryList = updateReqVO.getAuxiliaryList();
        // 处理报销分摊（先删后增）
//        reimbShareService.dealReimbShare(auxiliaryList, updateReqVO.getId());
        ReimbDO reimbDO = EntertainConvert.INSTANCE.convertReimbDO(updateReqVO);
        Long actualUserId = updateReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        int result = reimbService.updateReimb(reimbDO);
        //处理费用归属
        List<FeeShareReqDTO> feeShareList = updateReqVO.getFeeShare();
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        dealReimbShare(feeShareList, updateReqVO.getAuxiliaryType(), updateReqVO.getId(), feeShareAmount, reimbDO);
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
    public int deleteEntertainReimb(Long id) {
        // 删除
        int result = reimbService.deleteReimb(id);
        // 清除报销还款信息
        reimbRepayDetailService.batchDeleteReimbRepayDetailByReimbId(id);
        if (result <= 0) {
            throw exception(GENERAL_REIMVB_DELETE_ERROR);
        }
        // 删除费用归属
        reimbShareService.deleteReimbShareByReimbId(id,FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE);
        return result;
    }

    @Override
    public EntertainReimbRespVO getEntertainReimb(EntertainReimbDetailReq req) {
        //将不同入口的参数转换为entertainReimbId
        Long entertainReimbId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getEntertainReimbId();
        if (Objects.isNull(entertainReimbId)) {
            logger.error("[招待费报销详情]未获取到招待费报销id");
            return null;
        }
        ReimbRespVO reimbResp = reimbService.getReimbResp(entertainReimbId);
        if (Objects.isNull(reimbResp)) {
            return null;
        }
        EntertainReimbRespVO entertainEntertainReimbRespVO = EntertainConvert.INSTANCE.convertEntertainReimbResp(reimbResp);
        // 获取招待费申请单信息
        List<Long> applyIdList = reimbResp.getApplyIdList();
        if (CollUtil.isNotEmpty(applyIdList)) {
            List<ApplyRespVO> applyList = applyService.getApplyListByIdList(applyIdList);
            entertainEntertainReimbRespVO.setApplyList(applyList);
        }
        //未提交直接返回即可
        if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(reimbResp.getAuditStatus())) {
            return entertainEntertainReimbRespVO;
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(entertainReimbId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            entertainEntertainReimbRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        //查询费用归属信息
        List<FeeShareRespDTO> feeShare = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE, entertainEntertainReimbRespVO.getCode());
        entertainEntertainReimbRespVO.setFeeShare(feeShare);

        return entertainEntertainReimbRespVO;
    }

    @Override
    public PageResult<EntertainReimbRespVO> getEntertainReimbPage(ReimbPageReqVO pageReqVO) {
        PageResult<ReimbRespVO> entertainReimbPage = reimbService.getReimbPage(pageReqVO, FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE.getValue());

        PageResult<EntertainReimbRespVO> entertainReimbRespVOPageResult = EntertainConvert.INSTANCE.convertEntertainPageResult(entertainReimbPage);
        List<EntertainReimbRespVO> list = entertainReimbRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            List<String> codeList = list.stream().map(EntertainReimbRespVO::getCode).distinct().toList();
            Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue(), codeList);
            if (CollUtil.isNotEmpty(feeShareMap)) {
                list.forEach(s -> {
                    List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                    s.setFeeShare(feeShareRespDTO);
                });
            }
        }

        return entertainReimbRespVOPageResult.setList(list);
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
    public Map<String, BigDecimal> calcTotalAmount(List<JsonReimbDetail> reimbDetailList) {
        if (CollUtil.isEmpty(reimbDetailList)) {
            return Collections.emptyMap();
        }
        Map<String, BigDecimal> groupedAndSummedAmountMap = reimbDetailList.stream()
                .collect(Collectors.groupingBy(
                        // 根据币种分组
                        JsonReimbDetail::getExpenseCurrency,
                        // 对每个小项金额求和
                        CollectorsUtil.summingBigDecimal(JsonReimbDetail::getExpenseAmount)
                ));
        return groupedAndSummedAmountMap;
    }

    @Override
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        return reimbService.getFeeShareAmountByCode(businessCode);
    }
}
