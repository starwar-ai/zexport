package com.syj.eplus.module.fms.service.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRespDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.BusinessSubjectTypeDict;
import com.syj.eplus.framework.common.dict.BusinessTypeDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.ems.api.send.EmsSendApi;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentDTO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.*;
import com.syj.eplus.module.fms.convert.PaymentConvert;
import com.syj.eplus.module.fms.dal.dataobject.payment.PaymentDO;
import com.syj.eplus.module.fms.dal.mysql.payment.PaymentMapper;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.oa.api.LoanAppApi;
import com.syj.eplus.module.oa.api.PaymentAppApi;
import com.syj.eplus.module.oa.api.ReimbApi;
import com.syj.eplus.module.oa.api.dto.SimplePaymentAppDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.scm.api.paymentapply.PaymentApplyApi;
import com.syj.eplus.module.scm.api.paymentapply.dto.PaymentApplyDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.PARAM_ERROR;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.PAYMENT_NOT_EXISTS;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.ANTI_AUDIT_EXCEPT;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.PAYMENT_PAID_NOT_CLOSE;
import static com.syj.eplus.module.fms.api.payment.enums.ErrorCodeConstants.*;

/**
 * 财务付款表 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class PaymentServiceImpl implements PaymentService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private PaymentMapper paymentMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;


    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private CustApi custApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private PaymentAppApi paymentAppApi;

    @Resource
    private EmsSendApi emsSendSpi;
    @Resource
    private ReimbApi reimbApi;

    @Resource
    private LoanAppApi loanAppApi;
    @Resource
    private PaymentApplyApi paymentApplyApi;

    @Resource
    private  PaymentAppApi oaPaymentAppApi;
    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private OrderLinkApi orderLinkApi;

    private static final String SN_TYPE = "sn_payment";
    private static final String CODE_PREFIX = "FK";

    private static final String PROCESS_DEFINITION_KEY = "fms_payment";

    @Override
    public Long createPayment(PaymentSaveReqVO createReqVO) {
        // 插入
        PaymentDO payment = BeanUtils.toBean(createReqVO, PaymentDO.class);
        payment.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        payment.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        Long applyerId = createReqVO.getApplyerId();
        if (Objects.isNull(applyerId)) {
            applyerId = getLoginUserId();
        }
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        UserDept userDept = userDeptCache.get(applyerId);
        if (Objects.nonNull(userDept)) {
            payment.setApplyer(BeanUtils.toBean(userDept, UserDept.class));
        }
        paymentMapper.insert(payment);
//        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
//            submitTask(payment.getId(), getLoginUserId());
//        }
        // 返回
        return payment.getId();
    }

    @Override
    public void batchCreatePayment(List<PaymentSaveReqVO> createReqVOList) {
        if (CollUtil.isEmpty(createReqVOList)) {
            return;
        }
        List<PaymentDO> paymentDOList = createReqVOList.stream().map(s -> {
            // 插入
            PaymentDO payment = BeanUtils.toBean(s, PaymentDO.class);
            payment.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
            return payment;
        }).toList();
        paymentMapper.insertBatch(paymentDOList);
        paymentDOList.forEach(paymentDO -> {
            List<String> linkCodeList = paymentDO.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                        .setBusinessId(paymentDO.getId())
                        .setCode(paymentDO.getCode())
                        .setName(BusinessNameDict.PAYMENT_ORDER)
                        .setType(OrderLinkTypeEnum.PAYMENT.getValue())
                        .setLinkCode(s)
                        .setItem(List.of())
                        .setParentCode(StrUtil.isEmpty(paymentDO.getApplyCode()) ? CommonDict.HYPHEN : paymentDO.getApplyCode())
                        .setStatus(paymentDO.getAuditStatus())
                        .setCompanyId(paymentDO.getCompanyId())
                        .setBusinessSubjectName(paymentDO.getBusinessSubjectCode())
                        .setOrderMsg(paymentDO)).toList();
                orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
            }
        });

    }


    @Override
    public void updatePayment(PaymentSaveReqVO updateReqVO) {
        // 校验存在
        validatePaymentExists(updateReqVO.getId());
        // 更新
        PaymentDO updateObj = BeanUtils.toBean(updateReqVO, PaymentDO.class);
        paymentMapper.updateById(updateObj);
    }

    @Override
    public void deletePayment(Long id) {
        // 校验存在
        validatePaymentExists(id);
        // 删除
        paymentMapper.deleteById(id);
    }

    private PaymentDO validatePaymentExists(Long id) {
        PaymentDO paymentDO = paymentMapper.selectById(id);
        if (paymentDO == null) {
            throw exception(PAYMENT_NOT_EXISTS);
        }
        return paymentDO;
    }

    @Override
    public PaymentRespVO getPayment(PaymentDetailReq req) {
        Long paymentId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getPaymentId();
        PaymentDO paymentDO = paymentMapper.selectById(paymentId);
        Map<Long, UserDept> userDeptMap = adminUserApi.getUserDeptCache(null);
        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        PaymentRespVO paymentRespVO = PaymentConvert.INSTANCE.convertPaymentRespVO(paymentDO, userDeptMap, simpleCompanyDTO);
        // 根据不同的业务类型返回对应付款对象名称
        String businessSubjectName = getNameByBusinessCode(paymentRespVO.getBusinessSubjectType(), paymentRespVO.getBusinessSubjectCode());
        paymentRespVO.setBusinessSubjectName(businessSubjectName);
//        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentId, PROCESS_DEFINITION_KEY);
//        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
//            paymentRespVO.setProcessInstanceId(bpmProcessInstanceId);
//        }
        return paymentRespVO;
    }

    @Override
    public PageResult<PaymentRespVO> getPaymentPage(PaymentPageReqVO pageReqVO) {
        PageResult<PaymentRespVO> paymentRespVOPageResult = PaymentConvert.INSTANCE.convertPaymentPageVO(
                paymentMapper.selectPage(pageReqVO),
                adminUserApi.getUserDeptCache(null),
                companyApi.getSimpleCompanyDTO()
        );
        List<PaymentRespVO> paymentRespVOList = paymentRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(paymentRespVOList)) {
            paymentRespVOList.forEach(paymentRespVO -> {
                String businessSubjectName = getNameByBusinessCode(paymentRespVO.getBusinessSubjectType(), paymentRespVO.getBusinessSubjectCode());
                paymentRespVO.setBusinessSubjectName(businessSubjectName);
            });
            paymentRespVOPageResult.setList(paymentRespVOList);

            Map<String, Object> summary = new HashMap<>();
            pageReqVO.setPageSize(-1);
            PageResult<PaymentDO> paymentDOPageResult = paymentMapper.selectPage(pageReqVO);
            if(Objects.isNull(paymentDOPageResult) || CollUtil.isEmpty(paymentDOPageResult.getList())){
                return  paymentRespVOPageResult;
            }
            Map<String, BigDecimal> aggregatedAmountsByCurrency = paymentDOPageResult.getList().stream()
                    .map(PaymentDO::getApplyAmount)
                    .filter(CollUtil::isNotEmpty)
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(
                            JsonAmount::getCurrency,
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    JsonAmount::getAmount,
                                    BigDecimal::add
                            )
                    ));
            if (!aggregatedAmountsByCurrency.isEmpty()) {
                List<JsonAmount> aggregatedAmountList = aggregatedAmountsByCurrency.entrySet().stream()
                        .map(entry -> new JsonAmount()
                                .setCurrency(entry.getKey())
                                .setAmount(entry.getValue()))
                        .toList();
                summary.put("amountSum", aggregatedAmountList);
            }
            paymentRespVOPageResult.setSummary(summary);
        }
        return paymentRespVOPageResult;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long paymentId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, paymentId);
        updateAuditStatus(paymentId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        paymentMapper.updateById(PaymentDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public void confirmPayment(PaymentConfirmReqVO reqVO) throws Exception {
        PaymentDO paymentDO = paymentMapper.selectById(reqVO.getId());
        if (Objects.isNull(paymentDO)) {
            return;
        }
        UserDept cashier = adminUserApi.getUserDeptByUserId(getLoginUserId());
        int result = paymentMapper.updateById(PaymentDO.builder().id(reqVO.getId()).amount(reqVO.getAmount()).date(LocalDateTime.now()).cashier(cashier).status(PaymentStatusEnum.PAID.getValue()).companyId(reqVO.getCompanyId()).build());
        if (result <= 0) {
            throw exception(UPDATE_PAYMENT_STATUS_FAIL);
        }
        LocalDateTime paymentDate = LocalDateTime.now();
        PaymentAppDTO paymentAppDTO = new PaymentAppDTO().setCode(paymentDO.getBusinessCode()).setPaymentAmount(reqVO.getAmount()).setPaymentDate(paymentDate)
                .setPaymentStatus(PaymentStatusEnum.PAID.getValue()).setCashier(cashier);
        switch (paymentDO.getBusinessType()) {
            // 更新公对公申请单付款信息
            case BusinessTypeDict.PAYMENT_APP -> {
                paymentAppApi.updatePaymentApp(paymentAppDTO);
                //判断寄件反写数据
                EmsSendUpdateDTO emsSendUpdateDTO = new EmsSendUpdateDTO();
                SimplePaymentAppDTO simplePaymentAppDTO = paymentAppApi.getPaymentApp(paymentAppDTO.getCode());
                if (Objects.equals(simplePaymentAppDTO.getRelationType(), FmsOaPaymentRelationTypeEnum.EMSSEND.getCode())) {
                    emsSendUpdateDTO.setCodeList(simplePaymentAppDTO.getRelationCode()).setStatus(BooleanEnum.YES.getValue());
                    emsSendSpi.updatePayStatusByCodeList(emsSendUpdateDTO);
                }

            }
            // 更新报销单付款信息
            case BusinessTypeDict.ENTERTAIN_APP -> reimbApi.updatePaymentStatus(paymentAppDTO);
            case BusinessTypeDict.GENERAL_APP -> reimbApi.updatePaymentStatus(paymentAppDTO);
            case BusinessTypeDict.TRAVEL_APP -> reimbApi.updatePaymentStatus(paymentAppDTO);
            // 更新借款单付款信息
            case BusinessTypeDict.LOAN_APP -> loanAppApi.updatePaymentStatus(paymentAppDTO);
            case BusinessTypeDict.PAYMENT_APPLY -> paymentApplyApi.batchWriteBackPaymentMsg(List.of(paymentAppDTO));
            default -> {
            }
        }
    }

    @Override
    public void updateApprovalMessage(Long id, String processInstanceId) {
        BpmTaskRespDTO bpmTask = bpmProcessInstanceApi.getBpmTask(processInstanceId);
        paymentMapper.updateById(PaymentDO.builder().id(id).approver(bpmTask.getApprover()).approvalTime(bpmTask.getEndTime()).build());
    }

    @Override
    public void planPayment(PlanPaymentReqVO reqVO) {
        Long paymentId = reqVO.getPaymentId();
        if (Objects.isNull(paymentId)) {
            throw exception(PAYMENT_CODE_EMPTY);
        }
        PaymentDO paymentDO = validatePaymentExists(paymentId);
        paymentMapper.updateById(paymentDO.setAuditStatus(PaymentOrderStatusEnum.ALREADY_PLANNED.getStatus()).setPaymentBank(reqVO.getBank()).setPaymentBankAccount(reqVO.getBankAccount()).setPaymentMethod(reqVO.getPaymentMethod()).setAmount(reqVO.getAmount()).setAmount(reqVO.getAmount()).setAnnex(reqVO.getAnnex()).setAcceptanceDays(reqVO.getAcceptanceDays()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planPaymentCancel(Long id) {
        PaymentDO paymentDO = validatePaymentExists(id);
        paymentDO.setAuditStatus(PaymentOrderStatusEnum.APPROVE.getStatus()).setBank(null).setBankAccount(null).setPaymentMethod(null).setAmount(null).setAmount(null);
        paymentMapper.updateById(paymentDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchConfirmPayment(BatchPaymentConfirmReqVO reqVO) {
        List<SimplePayment> paymentIds = reqVO.getPaymentIds();
        if (CollUtil.isEmpty(paymentIds)) {
            throw exception(PAYMENT_CODE_EMPTY);
        }
        Map<Long, SimplePayment> paymentAmountMap = paymentIds.stream().collect(Collectors.toMap(SimplePayment::getId, s->s, (o, n) -> o));
        List<PaymentDO> paymentDOList = paymentMapper.selectBatchIds(paymentIds.stream().map(SimplePayment::getId).toList());
        if (CollUtil.isEmpty(paymentDOList)) {
            throw exception(PAYMENT_NOT_EXIST);
        }
        Long loginUserId = getLoginUserId();
        UserDept cashier = adminUserApi.getUserDeptByUserId(loginUserId);
        paymentDOList.forEach(s -> {
            // 已付金额
            BigDecimal paidAmount = Optional.ofNullable(Optional.ofNullable(s.getPaidAmount()).orElse(new JsonAmount()).getAmount()).orElse(BigDecimal.ZERO);
            // 本次支付金额
            SimplePayment simplePayment = paymentAmountMap.get(s.getId());
            if (Objects.isNull(simplePayment)){
                return;
            }
            BigDecimal amount = simplePayment.getAmount().getAmount();
            paidAmount = NumUtil.add(amount, paidAmount);
            BigDecimal applyAmount = s.getApplyAmount().stream().map(JsonAmount::getAmount).reduce(BigDecimal::add).get();
            s.setPaidAmount(new JsonAmount().setAmount(paidAmount).setCurrency(simplePayment.getAmount().getCurrency()));
            List<SimpleFile> annex = simplePayment.getAnnex();
            if (CollUtil.isNotEmpty(annex)){
                List<SimpleFile> baseAnnex = s.getAnnex();
                if (CollUtil.isEmpty(baseAnnex)){
                    s.setAnnex(annex);
                }else {
                    baseAnnex.addAll(annex);
                    s.setAnnex(baseAnnex);
                }
            }
            if (paidAmount.compareTo(applyAmount) >= 0) {
                s.setStatus(PaymentStatusEnum.PAID.getValue());
                s.setAuditStatus(PaymentOrderStatusEnum.ALREADY_PAID.getStatus());
            } else {
                s.setStatus(PaymentStatusEnum.PART_PAID.getValue());
                s.setAuditStatus(PaymentOrderStatusEnum.APPROVE.getStatus());
            }
            LocalDateTime paymentDate = Objects.isNull(simplePayment.getPaymentDate()) ? reqVO.getPaymentDate() : simplePayment.getPaymentDate();
            Long companyId = Objects.isNull(simplePayment.getCompanyId()) ? reqVO.getCompanyId() : simplePayment.getCompanyId();
            Integer paymentMethod = Objects.isNull(simplePayment.getPaymentMethod()) ? reqVO.getPaymentMethod() : simplePayment.getPaymentMethod();
            String bank = StrUtil.isEmpty(simplePayment.getBank()) ? reqVO.getBank() : simplePayment.getBank();
            String bankAccount = StrUtil.isEmpty(simplePayment.getBankAccount()) ? reqVO.getBankAccount() : simplePayment.getBankAccount();
            s.setId(s.getId()).setDate(paymentDate).setCompanyId(companyId)
                    .setCashier(cashier).setAmount(simplePayment.getAmount()).setPaymentMethod(paymentMethod)
                    .setPaymentBank(bank).setPaymentBankAccount(bankAccount);
            simplePayment.setAmount(s.getPaidAmount());
            paymentAmountMap.put(s.getId(), simplePayment);
        });
        // 更新付款单状态
        paymentMapper.updateBatch(paymentDOList);

        paymentDOList.stream().filter(s -> PaymentStatusEnum.PAID.getValue().equals(s.getStatus())).forEach(s -> {
            //采购合同回写已支付金额
            if (Objects.equals(BusinessTypeDict.PAYMENT_APPLY, s.getBusinessType())) {
                PaymentApplyDTO dto = paymentApplyApi.getPaymentApply(s.getBusinessCode());
                if (Objects.isNull(dto) || CollUtil.isEmpty(dto.getApplyerPurchaseItemList())) {
                    return;
                }
                dto.getApplyerPurchaseItemList().forEach(i -> {
                    purchaseContractApi.updatePurchaseAmount(i.getPurchaseContractCode(), i.getApplyAmount());
                });
            }
            // 公对公付款包含特殊情况的包材采购合同付款
            if(Objects.equals(BusinessTypeDict.PAYMENT_APP, s.getBusinessType())){
                SimplePaymentAppDTO paymentApp = oaPaymentAppApi.getPaymentApp(s.getBusinessCode());
                if(Objects.isNull(paymentApp)){
                    return;
                }
                if(!Objects.equals(paymentApp.getRelationType(), OaPaymentAppRealtionTypeEnum.AUXILIARY_PURCHASE.getValue())){
                    return;
                }

                paymentApp.getRelationCode().forEach(pay->{
                    purchaseContractApi.updateAuxiliaryPurchaseAmount(pay, s.getAmount());
                });
            }


        });
        // 更新费用归属支付状态
        paymentDOList.stream().filter(s -> PaymentStatusEnum.PAID.getValue().equals(s.getStatus())).forEach(s -> feeShareApi.updatePaymentStatus(s.getBusinessType(), s.getBusinessCode(), BooleanEnum.YES.getValue()));
        // 根据businessType汇总
        Map<Integer, List<PaymentAppDTO>> paymentMap = paymentDOList.stream().filter(s -> PaymentStatusEnum.PAID.getValue().equals(s.getStatus())).collect(Collectors.groupingBy(PaymentDO::getBusinessType, Collectors.mapping(s -> new PaymentAppDTO().setCode(s.getBusinessCode()).setPaymentAmount(paymentAmountMap.get(s.getId()).getAmount()).setPaymentDate(reqVO.getPaymentDate())
                .setPaymentStatus(PaymentStatusEnum.PAID.getValue()).setCashier(cashier).setBank(s.getBank()).setBankAccount(s.getBankAccount()).setAnnex(s.getAnnex()), Collectors.toList())));
        // 回写采购付款申请水单(是否支付完成均回写)
        List<PaymentAppDTO> paymentAppDTOList = paymentDOList.stream().filter(s -> BusinessTypeDict.PAYMENT_APPLY == s.getBusinessType()).map(s -> new PaymentAppDTO().setCode(s.getBusinessCode()).setAnnex(s.getAnnex())).toList();
        paymentApplyApi.batchWriteAnnex(paymentAppDTOList);
        if (CollUtil.isEmpty(paymentMap)) {
            return;
        }
        paymentMap.forEach((k, v) -> {
            switch (k) {
                // 批量更新公对公申请单付款信息
                case BusinessTypeDict.PAYMENT_APP -> paymentAppApi.batchUpdatePaymentApp(v);
                // 批量更新报销单付款信息
                case BusinessTypeDict.ENTERTAIN_APP -> reimbApi.batchUpdatePaymentStatus(v);
                // 批量更新一半费用报销
                case BusinessTypeDict.GENERAL_APP -> reimbApi.batchUpdatePaymentStatus(v);
                // 批量更新出差报销
                case BusinessTypeDict.TRAVEL_APP -> reimbApi.batchUpdatePaymentStatus(v);
                // 批量更新其他报销
                case BusinessTypeDict.OTHER_APP -> reimbApi.batchUpdatePaymentStatus(v);
                // 批量更新借款单付款信息
                case BusinessTypeDict.LOAN_APP -> loanAppApi.batchUpdaPaymentStatus(v);
                // 批量更新付款申请信息
                case BusinessTypeDict.PAYMENT_APPLY -> paymentApplyApi.batchWriteBackPaymentMsg(v);
                default -> {
                }
            }
        });
    }

    @Override
    public void updateFeeShareStatus(Integer type, String code, Integer status) {

    }

    @Override
    public Long getPaymentNumByPurchaseContractCode(Integer businessType, String code) {
        return paymentMapper.selectCount(new LambdaQueryWrapperX<PaymentDO>().eq(PaymentDO::getBusinessType, businessType)
                .eq(PaymentDO::getBusinessSubjectCode, code));
    }

    @Override
    public void validatePayment(Integer businessType, String businessCode) {
        if (Objects.isNull(businessType) || StrUtil.isEmpty(businessCode)) {
            throw exception(PAYMENT_CODE_EMPTY);
        }
        if (paymentMapper.selectCount(new LambdaQueryWrapperX<PaymentDO>().eq(PaymentDO::getBusinessType, businessType)
                .eq(PaymentDO::getBusinessCode, businessCode).ne(PaymentDO::getStatus, PaymentStatusEnum.UNPAID.getValue()).ne(PaymentDO::getStatus, PaymentStatusEnum.IN_PAYMENT.getValue())) > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePayment(Long id) {
        PaymentDO paymentDO = validatePaymentExists(id);
        if (Objects.equals(paymentDO.getAuditStatus(), PaymentOrderStatusEnum.ALREADY_PAID.getStatus())) {
            throw exception(PAYMENT_PAID_NOT_CLOSE);
        }
        paymentDO.setAuditStatus(PaymentOrderStatusEnum.CLOSE.getStatus());
        orderLinkApi.updateOrderLinkStatus(paymentDO.getCode(), BusinessNameDict.PAYMENT_ORDER, paymentDO.getLinkCodeList(), PaymentOrderStatusEnum.CLOSE.getStatus());
        paymentMapper.updateById(paymentDO);
    }

    @Override
    public Map<String, PaymentDTO> getListByCodes(List<String> applyCodes) {
        List<PaymentDO> paymentDOS = paymentMapper.selectList(PaymentDO::getApplyCode, applyCodes);
        if (CollUtil.isEmpty(paymentDOS)) {
            return null;
        }
        List<PaymentDTO> paymentDTOS = BeanUtils.toBean(paymentDOS, PaymentDTO.class);
        return paymentDTOS.stream().collect(Collectors.toMap(PaymentDTO::getApplyCode, s -> s, (s1, s2) -> s2));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPlanPayment(List<PlanPaymentReqVO> reqVO) {
        if(CollUtil.isEmpty(reqVO)){
            throw exception(PARAM_ERROR);
        }
        reqVO.forEach(this::planPayment);
    }

    @Override
    public void batchPayment(List<BatchPaymentReqVO> reqVO) {
        if(CollUtil.isEmpty(reqVO)){
            throw exception(PARAM_ERROR);
        }
        reqVO.forEach(s->{
            BatchPaymentConfirmReqVO req = BeanUtils.toBean(s, BatchPaymentConfirmReqVO.class);
            req.setPaymentIds(Collections.singletonList(
                    new SimplePayment()
                            .setId(s.getPaymentId())
                            .setAmount(s.getAmount())
            ));
            batchConfirmPayment(req);
        });
    }

    @Override
    public void closePayment(ClosePaymentDTO closePaymentDTO) {
        if (Objects.isNull(closePaymentDTO)){
            return;
        }
        Integer businessType = closePaymentDTO.getBusinessType();
        String businessCode = closePaymentDTO.getBusinessCode();
        if (Objects.isNull(businessType)||StrUtil.isEmpty(businessCode)){
            return;
        }
        List<PaymentDO> paymentDOS = paymentMapper.selectList(new LambdaQueryWrapperX<PaymentDO>().eq(PaymentDO::getBusinessType, businessType).eq(PaymentDO::getBusinessCode, businessCode).eq(PaymentDO::getStatus, PaymentStatusEnum.UNPAID.getValue()));
        if (CollUtil.isEmpty(paymentDOS)){
            return;
        }
        paymentDOS.forEach(s->{
            s.setCancelReason(closePaymentDTO.getCancelReason());
            s.setCancelTime(closePaymentDTO.getCancelTime());
            s.setCancelUser(closePaymentDTO.getCancelUser());
            s.setAuditStatus(PaymentOrderStatusEnum.CLOSE.getStatus());
        });
        paymentMapper.updateBatch(paymentDOS);
    }

    private String getNameByBusinessCode(Integer businessSubjectType, String businessSubjectCode) {
        switch (businessSubjectType) {
            case BusinessSubjectTypeDict.CUST:
                return custApi.getCustNameCache(businessSubjectCode).get(businessSubjectCode);
            case BusinessSubjectTypeDict.BUSINESS_VENDER:
            case BusinessSubjectTypeDict.ADMINISTRATION_VENDER:
                return venderApi.getVenderNameCache(businessSubjectCode).get(businessSubjectCode);
            case BusinessSubjectTypeDict.PERSON:
                return dealUserDept(Long.parseLong(businessSubjectCode));
            default: {
                logger.warn("[付款详情]根据编号:{}未获取到{}对应名称", businessSubjectCode, businessSubjectType);
                return null;
            }
        }
    }

    private String dealUserDept(Long userId) {
        UserDept userDept = adminUserApi.getUserDeptCache(userId).get(userId);
        if (Objects.isNull(userDept)) {
            return null;
        }
        return userDept.getNickname();
    }
}