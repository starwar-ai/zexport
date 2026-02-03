package com.syj.eplus.module.oa.service.travelreimb;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.oa.api.TravelAppApi;
import com.syj.eplus.module.oa.api.dto.RepayAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbStandardRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.convert.travel.TravelReimbConvert;
import com.syj.eplus.module.oa.converter.ReimbConverter;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import com.syj.eplus.module.oa.enums.RepayTypeEnum;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import com.syj.eplus.module.oa.service.reimbrepaydetail.ReimbRepayDetailService;
import com.syj.eplus.module.oa.service.reimbshare.ReimbShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;

/**
 * 出差报销 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class TravelReimbServiceImpl implements TravelReimbService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String PROCESS_DEFINITION_KEY = "oa_travel_reimb";
    @Resource
    private TravelAppApi travelAppApi;
    @Resource
    private ConfigApi configApi;
    @Resource
    private ReimbService reimbService;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;
    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private ReimbShareService reimbShareService;
    @Resource
    private CompanyApi companyApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ApplyService applyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTravelReimb(TravelReimbSaveReqVO createReqVO) {
        ReimbSaveReqVO reimbSaveReqVO = ReimbConverter.INSTANCE.convertReimbSaveReqVO(createReqVO);
        List<JsonReimbDetail> accommodationFeeList = createReqVO.getAccommodationFeeList() == null ? Collections.emptyList() : createReqVO.getAccommodationFeeList();
        List<JsonReimbDetail> travelAllowanceList = createReqVO.getTravelAllowanceList() == null ? Collections.emptyList() : createReqVO.getTravelAllowanceList();
        List<JsonReimbDetail> selfDrivingList = createReqVO.getSelfDrivingList() == null ? Collections.emptyList() : createReqVO.getSelfDrivingList();
        List<JsonReimbDetail> trafficFeeList = createReqVO.getTrafficFeeList() == null ? Collections.emptyList() : createReqVO.getTrafficFeeList();
        List<JsonReimbDetail> otherDescList = createReqVO.getOtherDescList() == null ? Collections.emptyList() : createReqVO.getOtherDescList();
        //将前台传入的费用明细归集为明细对象
        List<JsonReimbDetail> reimbDetailList = Stream.of(
                        accommodationFeeList,
                        travelAllowanceList,
                        selfDrivingList,
                        trafficFeeList,
                        otherDescList
                )
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //插入费用小项明细
        if (CollUtil.isNotEmpty(reimbDetailList)) {
            reimbSaveReqVO.setReimbDetailList(reimbDetailList);
        }
        ReimbDO reimbDO = ReimbConverter.INSTANCE.convertReimbDO(reimbSaveReqVO);
        Long actualUserId = createReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        reimbDO.setAuxiliaryType(BooleanEnum.NO.getValue());
        Long travelReimbId = reimbService.createReimb(reimbDO);
        List<ReimbRepayDetailSaveReqVO> batchInsertList = dealReimbRepayDetail(createReqVO, travelReimbId);
        //新增费用归属
        List<FeeShareReqDTO> feeShareList = createReqVO.getFeeShare();
        // 人民币金额
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        Integer auxiliaryType = createReqVO.getAuxiliaryType();
        if (CollUtil.isNotEmpty(batchInsertList)) {
            batchInsertList.forEach(s -> {
                s.setReimbId(travelReimbId);
                s.setRepaySourceType(RepaySourceTypeEnum.TRAVEL_REIMB.getType());
                s.setRepaySourceCode(reimbDO.getCode());
                s.setRepayUser(actualUser);
            });
            reimbRepayDetailService.batchCreateReimbRepayDetail(batchInsertList);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            dealFeeShare(feeShareList, auxiliaryType, travelReimbId, feeShareAmount, reimbDO);
            submitTask(travelReimbId);
        }
        //更新费用申请状态
        List<Long> applyIdList = createReqVO.getApplyIdList();
        if (CollUtil.isNotEmpty(applyIdList)) {
            applyService.batchUpdateIsApplyExpense(applyIdList,ApplyExpenseEnum.CREATE.getValue());
        }
        // 返回
        return travelReimbId;
    }
    private void dealFeeShare(List<FeeShareReqDTO> feeShareList,Integer auxiliaryType, Long travelReimbId,JsonAmount feeShareAmount,ReimbDO reimbDO) {
        if (BooleanEnum.YES.getValue().equals(auxiliaryType)) {
            if(CollUtil.isEmpty(feeShareList)){
                throw exception(FEE_SHARE_NOT_EXISTS);
            }
            for (FeeShareReqDTO feeShare : feeShareList) {
                feeShare.setBusinessType(FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue());
                feeShare.setBusinessCode(reimbDO.getCode());
                feeShare.setCompanyId(reimbDO.getCompanyId());
                feeShare.setInputUser(reimbDO.getReimbUser());
                feeShare.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
                feeShare.setBusinessId(travelReimbId);
//                feeShare.setAmount(feeShareAmount);
                feeShare.setStatus(BooleanEnum.YES.getValue());
                Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
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
            feeShare.setBusinessType(FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue());
            feeShare.setBusinessCode(reimbDO.getCode());
            feeShare.setInputUser(reimbDO.getReimbUser());
            feeShare.setCompanyId(reimbDO.getCompanyId());
            feeShare.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
            feeShare.setBusinessId(travelReimbId);
//            feeShare.setAmount(feeShareAmount);
            feeShare.setStatus(BooleanEnum.NO.getValue());
            Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(reimbDO.getCompanyId());
                if (Objects.isNull(simpleCompanyDTO)) {
                    throw exception(COMPANY_NOT_EXISTS);
                }
                feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
            feeShareApi.updateFeeShareByDTO(feeShare);
            reimbDO.setAuxiliaryType(BooleanEnum.NO.getValue());
            reimbService.updateReimb(reimbDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTravelReimb(TravelReimbSaveReqVO updateReqVO) {
        ReimbDO reimbDO = TravelReimbConvert.INSTANCE.convertReimbDO(updateReqVO);
        Long actualUserId = updateReqVO.getActualUserId();
        if (Objects.isNull(actualUserId)){
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        UserDept actualUser = adminUserApi.getUserDeptByUserId(actualUserId);
        reimbDO.setActualUser(actualUser);
        List<JsonReimbDetail> reimbDetailList = Stream.of(
                        updateReqVO.getAccommodationFeeList(),
                        updateReqVO.getTravelAllowanceList(),
                        updateReqVO.getSelfDrivingList(),
                        updateReqVO.getTrafficFeeList(),
                        updateReqVO.getOtherDescList()
                )
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //插入费用小项明细
        if (CollUtil.isNotEmpty(reimbDetailList)) {
            reimbDO.setReimbDetailList(reimbDetailList);
        }
        //处理费用归属
        List<FeeShareReqDTO> feeShareList = updateReqVO.getFeeShare();
        // 人民币金额
        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(reimbDO.getTotalAmountList());
        // 处理报销分摊
//        reimbShareService.dealReimbShare(auxiliaryList, updateReqVO.getId());
        int result = reimbService.updateReimb(reimbDO);
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
        dealFeeShare(feeShareList, updateReqVO.getAuxiliaryType(), updateReqVO.getId(), feeShareAmount, reimbDO);
        if (result > 0 && SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {

            submitTask(updateReqVO.getId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTravelReimb(Long id) {
        // 删除
        int result = reimbService.deleteReimb(id);
        // 清楚报销还款信息
        reimbRepayDetailService.batchDeleteReimbRepayDetailByReimbId(id);
        if (result <= 0) {
            throw exception(TRAVEL_REIMVB_DELETE_ERROR);
        }
        reimbShareService.deleteReimbShareByReimbId(id,FeeShareSourceTypeEnum.TRAVEL_REIMBURSE);
        return result;
    }


    @Override
    public TravelReimbRespVO getTravelReimb(TravelReimbDetailReq req) {
        //将不同入口的参数转换为travelReimbId
        Long travelReimbId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getTravelReimbId();
        if (Objects.isNull(travelReimbId)) {
            logger.error("[出差报销详情]未获取到出差报销id");
            return null;
        }
        ReimbRespVO reimbResp = reimbService.getReimbResp(travelReimbId);
        if (Objects.isNull(reimbResp)) {
            return null;
        }
        TravelReimbRespVO travelTravelReimbRespVO = TravelReimbConvert.INSTANCE.convertTravelReimbResp(reimbResp);
        // 获取出差申请表
        List<Long> applyIdList = reimbResp.getApplyIdList();
        if (CollUtil.isNotEmpty(applyIdList)) {
            List<ApplyRespVO> applyList = applyService.getApplyListByIdList(applyIdList);
            travelTravelReimbRespVO.setApplyList(applyList);
        }
        // travelTravelReimbRespVO.setSimpleTravelAppResp(travelAppApi.getSimpleTravelAppRespDTO(reimbResp.getReimbCode()));
//        // 获取报销还款详情
//        List<ReimbRepayDetailDO> reimbRepayDetailList = reimbRepayDetailService.getReimbRepayDetailListByReimbId(travelReimbId);
//        if (CollUtil.isNotEmpty(reimbRepayDetailList)) {
//            List<Long> loanIdList = reimbRepayDetailList.stream().map(ReimbRepayDetailDO::getLoanId).toList();
//            if (CollUtil.isNotEmpty(loanIdList)) {
//                List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = loanAppApi.getSimpleLoanAppRespDTOList(loanIdList, travelReimbId, null);
//                travelTravelReimbRespVO.setLoanAppList(simpleLoanAppRespDTOList);
//            }
//        }
        List<ReimbRepayDetailDO> repayDetailDOS = reimbRepayDetailService.getReimbRepayDetailListByReimbId(travelReimbId);
        if (CollUtil.isNotEmpty(repayDetailDOS)){
            Optional<BigDecimal> amountOpt = repayDetailDOS.stream().map(ReimbRepayDetailDO::getRepayAmount).map(JsonAmount::getAmount).reduce(BigDecimal::add);
            Optional<String> currencyOpt = repayDetailDOS.stream().map(ReimbRepayDetailDO::getRepayAmount).map(JsonAmount::getCurrency).findFirst();
            amountOpt.ifPresent(bigDecimal -> travelTravelReimbRespVO.setRepayAmountList(Collections.singletonList(new JsonAmount().setAmount(bigDecimal).setCurrency(currencyOpt.get()))));
        }
        //查询费用归属信息
        List<FeeShareRespDTO> feeShare = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.TRAVEL_REIMBURSE, travelTravelReimbRespVO.getCode());
        travelTravelReimbRespVO.setFeeShare(feeShare);

        //未提交直接返回即可
        if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(reimbResp.getAuditStatus())) {
            return travelTravelReimbRespVO;
        }
        return travelTravelReimbRespVO;
    }

    @Override
    public PageResult<TravelReimbRespVO> getTravelReimbPage(ReimbPageReqVO pageReqVO) {
        PageResult<ReimbRespVO> travelReimbPage = reimbService.getReimbPage(pageReqVO,FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue());
        return TravelReimbConvert.INSTANCE.convertTravelPageResult(travelReimbPage);
    }

    @Override
    public String getLodgingSubsidy() {
        return configApi.getValueByConfigKey("lodging.subsidy");
    }

    @Override
    public List<ReimbStandardRespVO> getTravelStandard() {
        String value = configApi.getValueByConfigKey("travel.standard");
        if (StrUtil.isEmpty(value)) {
            return Collections.emptyList();
        }
        List<ReimbStandardRespVO> reimbStandardRespVOList = new ArrayList<>();
        List<String> commaSplit = StrUtil.split(value, CommonDict.COMMA);
        commaSplit.forEach(s -> {
            List<String> colonSplit = StrUtil.split(s, CommonDict.COLON);
            if (CollUtil.isNotEmpty(colonSplit)) {
                String amountStr = colonSplit.get(1);
                BigDecimal amount = null;
                try {
                    amount = BigDecimal.valueOf(Long.parseLong(amountStr));
                } catch (NumberFormatException e) {
                    logger.error("[出差报销]出差补贴标准转换出错: amountStr-{}", amountStr);
                }
                reimbStandardRespVOList.add(new ReimbStandardRespVO().setAmount(amount).setSubsidyType(colonSplit.get(0)));
            }
        });
        return reimbStandardRespVOList;
    }

    @Override
    public BigDecimal getMileageStandard() {
        String value = configApi.getValueByConfigKey("mileage.standard");
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        return new BigDecimal(value);
    }


    public Map<String, BigDecimal> calcTotalAmount(TravelReimbSaveReqVO travelReimb) {
        // 用map存储各个小项计算的金额 方便按照币种归集
        Map<String, BigDecimal> travelReimbAmountMap = new HashMap<>();
        // 出差补贴
        List<JsonReimbDetail> travelAllowanceList = travelReimb.getTravelAllowanceList();
        if (CollUtil.isNotEmpty(travelAllowanceList)) {
            mergeTravelAllowance(travelAllowanceList, travelReimbAmountMap);
        }
        // 住宿费
        List<JsonReimbDetail> accommodationFeeList = travelReimb.getAccommodationFeeList();
        if (CollUtil.isNotEmpty(accommodationFeeList)) {
            mergeAccommodationFee(accommodationFeeList, travelReimbAmountMap);
        }
        // 自驾
        List<JsonReimbDetail> selfDrivingList = travelReimb.getSelfDrivingList();
        if (CollUtil.isNotEmpty(selfDrivingList)) {
            mergeSelfDrivingAndTrafficFee(selfDrivingList, travelReimbAmountMap);
        }
        // 交通费
        List<JsonReimbDetail> trafficFeeList = travelReimb.getTrafficFeeList();
        if (CollUtil.isNotEmpty(trafficFeeList)) {
            mergeSelfDrivingAndTrafficFee(trafficFeeList, travelReimbAmountMap);
        }
        List<JsonReimbDetail> otherDescList = travelReimb.getOtherDescList();
        if (CollUtil.isNotEmpty(otherDescList)) {
            mergeOtherAmount(otherDescList, travelReimbAmountMap);
        }
        //返回金额不为空且大于0的实际报销金额
        return travelReimbAmountMap.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().signum() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 计算报销金额
     */
    @Override
    public Tuple calcTravelAmount(TravelReimbSaveReqVO travelReimb) {
        Map<String, BigDecimal> totalAmountMap = calcTotalAmount(travelReimb);
        if (CollUtil.isEmpty(totalAmountMap)) {
            return new Tuple(null, null);
        }
        // 是否还款
        Integer repayFlag = travelReimb.getRepayFlag();
        // 未选择还款则只需计算报销金额
        if (Objects.equals(repayFlag, BooleanEnum.NO.getValue())) {
            return new Tuple(totalAmountMap, null);
        }
        // 已选择还款则需计算报销金额和还款金额
        List<SimpleLoanAppRespDTO> loanAppList = travelReimb.getLoanAppList();
        // 还款列表为空则无需还款
        if (CollUtil.isEmpty(loanAppList)) {
            return new Tuple(totalAmountMap, null);
        }
        List<RepayAppDTO> repayAppDTOList = new ArrayList<>();
        loanAppList.forEach(loanApp -> {
            // 剩余待还款金额
            JsonAmount outstandingJsonAmount = loanApp.getOutstandingAmount();
            if (Objects.isNull(outstandingJsonAmount)) {
                return;
            }
            String currency = outstandingJsonAmount.getCurrency();
            BigDecimal outstandingAmount = outstandingJsonAmount.getAmount();
            if (Objects.isNull(outstandingAmount)) {
                return;
            }
            // 还款中金额
            JsonAmount inRepaymentJsonAmount = loanApp.getInRepaymentAmount();
            if (Objects.nonNull(inRepaymentJsonAmount)) {
                BigDecimal inRepaymentAmount = inRepaymentJsonAmount.getAmount();
                if (Objects.nonNull(inRepaymentAmount)) {
                    outstandingAmount = outstandingAmount.add(inRepaymentAmount);
                }
            }
            BigDecimal travelReimbAmount = totalAmountMap.get(currency);
            if (Objects.isNull(travelReimbAmount)) {
                return;
            }
            RepayAppDTO repayAppDTO = new RepayAppDTO()
                    // 还款人为报销人
                    .setReparer(travelReimb.getReimbUser())
                    .setCurrency(currency)
                    .setLoanAppId(loanApp.getId())
                    .setRepayType(RepayTypeEnum.Reimb.getValue());
            // 报销金额小于剩余待还款金额
            if (travelReimbAmount.compareTo(outstandingAmount) < 0) {
                totalAmountMap.remove(currency);
                repayAppDTO.setAmount(new JsonAmount().setAmount(travelReimbAmount).setCurrency(currency));
                repayAppDTOList.add(repayAppDTO);
                // 报销金额大于等于剩余待还款金额
            } else {
                // 结清剩余还款
                repayAppDTO.setAmount(new JsonAmount().setAmount(outstandingAmount).setCurrency(currency));
                repayAppDTOList.add(repayAppDTO);
                if (travelReimbAmount.compareTo(outstandingAmount) > 0) {
                    // 实际报销金额减掉还款金额
                    totalAmountMap.put(currency, travelReimbAmount.subtract(outstandingAmount));
                } else {
                    // 实际报销为0即无需报销
                    totalAmountMap.remove(currency);
                }
            }
        });
        return new Tuple(totalAmountMap, repayAppDTOList);
    }

    private void mergeTravelAllowance(List<JsonReimbDetail> travelReimbDetailRespVOList, Map<String, BigDecimal> travelReimbAmountMap) {
        travelReimbDetailRespVOList.forEach(travelReimbDetailRespVO -> {
            // 币种
            String expenseCurrency = travelReimbDetailRespVO.getExpenseCurrency();
            // 出差补贴天数
            Double allowanceDay = travelReimbDetailRespVO.getAllowanceDay();
            // 出差补贴标准
            BigDecimal allowanceStandard = travelReimbDetailRespVO.getAllowanceStandard();
            if (Objects.nonNull(allowanceStandard)) {
                // 出差补贴 ： 出差补贴标准 * 出差补贴天数
                BigDecimal allowanceAmount = allowanceStandard.multiply(BigDecimal.valueOf(allowanceDay)).setScale(2);
                travelReimbAmountMap.merge(expenseCurrency, allowanceAmount, BigDecimal::add);
            }
        });
    }

    private void mergeAccommodationFee(List<JsonReimbDetail> travelReimbDetailRespVOList, Map<String, BigDecimal> travelReimbAmountMap) {
        travelReimbDetailRespVOList.forEach(travelReimbDetailRespVO -> {
            // 币种
            String expenseCurrency = travelReimbDetailRespVO.getExpenseCurrency();
            BigDecimal expenseAmount = travelReimbDetailRespVO.getExpenseAmount();
            travelReimbAmountMap.merge(expenseCurrency, expenseAmount, BigDecimal::add);
        });
    }

    private void mergeSelfDrivingAndTrafficFee(List<JsonReimbDetail> travelReimbDetailRespVOList, Map<String, BigDecimal> travelReimbAmountMap) {
        travelReimbDetailRespVOList.forEach(travelReimbDetailRespVO -> {
            // 币种
            String expenseCurrency = travelReimbDetailRespVO.getExpenseCurrency();
            // 小项金额
            BigDecimal expenseAmount = travelReimbDetailRespVO.getExpenseAmount();
            travelReimbAmountMap.merge(expenseCurrency, expenseAmount, BigDecimal::add);
        });
    }

    private void mergeOtherAmount(List<JsonReimbDetail> travelReimbDetailRespVOList, Map<String, BigDecimal> travelReimbAmountMap) {
        travelReimbDetailRespVOList.forEach(travelReimbDetailRespVO -> {
            // 币种
            String expenseCurrency = travelReimbDetailRespVO.getExpenseCurrency();
            // 小项金额
            BigDecimal expenseAmount = travelReimbDetailRespVO.getExpenseAmount();
            travelReimbAmountMap.merge(expenseCurrency, expenseAmount, BigDecimal::add);
        });
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

    private List<ReimbRepayDetailSaveReqVO> dealReimbRepayDetail(TravelReimbSaveReqVO createReqVO, Long reimbId) {
        List<ReimbRepayDetailSaveReqVO> batchInsertList = new ArrayList<>();
        List<SimpleLoanAppRespDTO> loanAppRespDTOList = createReqVO.getLoanAppList();
        // 计算当前报销总金额
        Map<String, BigDecimal> totalTravelAmountMap = calcTotalAmount(createReqVO);
        List<JsonAmount> totalAmountList = createReqVO.getTotalAmountList();
        // 如果选择还款单则分别创建对应报销还款详情
        if (CollUtil.isNotEmpty(loanAppRespDTOList)) {
            loanAppRespDTOList.forEach(simpleLoanAppRespDTO -> {
                // 剩余未还款金额
                JsonAmount outstandingJsonAmount = simpleLoanAppRespDTO.getOutstandingAmount();
                // 当前借款单没有剩余未还款金额不处理
                if (Objects.isNull(outstandingJsonAmount)) {
                    return;
                }
                String currency = outstandingJsonAmount.getCurrency();
                BigDecimal currencyAmount = totalTravelAmountMap.get(currency);
                // 当前报销单对应币种没有报销金额不处理
                if (Objects.isNull(currencyAmount)) {
                    return;
                }
                BigDecimal totalReimbAmount = currencyAmount;
                BigDecimal outstandingAmount = outstandingJsonAmount.getAmount();
                ReimbRepayDetailSaveReqVO reimbRepayDetailSaveReqVO = new ReimbRepayDetailSaveReqVO()
                        .setLoanId(simpleLoanAppRespDTO.getId())
                        .setReimbId(reimbId)
                        .setRepaySourceType(RepaySourceTypeEnum.ENTERTAIN_REIMB.getType())
                        .setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
                // 当前报销总额大于等于借款单未还款金额
                if (totalReimbAmount.compareTo(outstandingAmount) > -1) {
                    // 实际报销金额为当前报销总额-当前借款单未还款金额
                    totalReimbAmount = totalReimbAmount.subtract(outstandingAmount);
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(outstandingAmount));
                    totalTravelAmountMap.put(currency, totalReimbAmount);
                } else {
                    reimbRepayDetailSaveReqVO.setRepayAmount(new JsonAmount().setCurrency(currency).setAmount(totalReimbAmount));
                    totalTravelAmountMap.put(currency, BigDecimal.ZERO);
                }

                batchInsertList.add(reimbRepayDetailSaveReqVO);
            });
        }
        // 处理前端传入的报销总额为空时 后台计算结果中为0的情况
        if (CollUtil.isEmpty(totalAmountList)) {
            if (CollUtil.isNotEmpty(totalTravelAmountMap)) {
                boolean filter = totalTravelAmountMap.values().stream().filter(s -> s.compareTo(BigDecimal.ZERO) != 0).count() > 0;
                if (filter) {
                    throw exception(TRAVEL_AMOUNT_EXCEPTION);
                }
            }
        } else {
            // 如果计算出的总报销金额跟前台传入不一致则抛出异常
            long count = totalAmountList.stream().filter(amount -> {
                BigDecimal anountValue = totalTravelAmountMap.get(amount.getCurrency());
                return Objects.isNull(anountValue) || anountValue.compareTo(amount.getAmount()) != 0;
            }).count();
            if (count > 0) {
                throw exception(TRAVEL_AMOUNT_EXCEPTION);
            }
        }
        return batchInsertList;
    }
}