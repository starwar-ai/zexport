package com.syj.eplus.module.scm.service.paymentapply;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.scm.api.paymentapply.dto.ApplyerPurchaseContractItemDTO;
import com.syj.eplus.module.scm.api.paymentapply.dto.PaymentApplyDTO;
import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.*;
import com.syj.eplus.module.scm.convert.paymentapply.PaymentApplyConvert;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistration.PurchaseRegistrationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import com.syj.eplus.module.scm.dal.mysql.addsubterm.PurchaseAddSubTermMapper;
import com.syj.eplus.module.scm.dal.mysql.paymentapply.PaymentApplyMapper;
import com.syj.eplus.module.scm.dal.mysql.paymentplan.PurchasePaymentPlanMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontract.PurchaseContractMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistration.PurchaseRegistrationMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistrationitem.PurchaseRegistrationItemMapper;
import com.syj.eplus.module.scm.dal.mysql.vender.VenderMapper;
import com.syj.eplus.module.scm.dal.mysql.venderbankaccount.VenderBankaccountMapper;
import com.syj.eplus.module.scm.dal.mysql.venderpayment.VenderPaymentMapper;
import com.syj.eplus.module.scm.entity.ApplyPaymentPlan;
import com.syj.eplus.module.scm.entity.ApplyerPurchaseContractItem;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.ANTI_AUDIT_EXCEPT;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.PAYMENT_APPLY_IS_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 付款申请主 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class PaymentApplyServiceImpl implements PaymentApplyService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PaymentApplyMapper paymentApplyMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private PurchaseContractMapper purchaseContractMapper;
    @Resource
    private PurchasePaymentPlanMapper purchasePaymentPlanMapper;
    @Resource
    private VenderMapper venderMapper;
    @Resource
    private VenderPaymentMapper venderPaymentMapper;
    @Resource
    private CompanyApi companyApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DictDataApi dictDataApi;
    @Resource
    private PurchaseContractItemMapper purchaseContractItemMapper;
    @Resource
    private PurchaseAddSubTermMapper purchaseAddSubTermMapper;
    @Resource
    private PaymentApi paymentApi;
    @Resource
    private PurchaseRegistrationItemMapper registrationItemMapper;
    @Resource
    private PurchaseRegistrationMapper purchaseRegistrationMapper;
    @Resource
    private PaymentItemApi paymentItemApi;
    @Resource
    private VenderBankaccountMapper venderBankaccountMapper;
    @Resource
    private OrderLinkApi orderLinkApi;
    @Resource
    private ReportApi reportApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private RateApi rateApi;
    @Resource
    private SaleContractApi saleContractApi;

    private static final String SN_TYPE = "payment_apply";

    private static final String CODE_PREFIX = "PAP";

    private static final String PROCESS_DEFINITION_KEY = "scm_payment_apply";
    private static final String OPERATOR_EXTS_KEY = "scm_payment_apply";
    @Resource
    private ShipmentApi shipmentApi;
    @Resource
    private IStockApi stockApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createPaymentApply(PaymentApplySaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        // 校验最后一笔付款是否全额付
        List<ApplyPaymentPlan> applyPaymentPlanList = createReqVO.getApplyPaymentPlanList();
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            throw exception(PAYMENT_PLAN_EMPTY);
        }
        validateLastPaymentPlan(applyPaymentPlanList);
        List<PurchasePaymentPlan> purchasePaymentPlanIdList = applyPaymentPlanList.stream().map(s->{
            PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
            purchasePaymentPlan.setId(s.getId());
            purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.IN_PAID.getValue());
            return purchasePaymentPlan;
        }).distinct().toList();
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = createReqVO.getApplyerPurchaseItemList();
        if (CollUtil.isNotEmpty(applyerPurchaseItemList)){
            applyerPurchaseItemList.forEach(s->s.setRealApplyAmount(s.getApplyAmount()));
        }
        if (CollUtil.isNotEmpty(purchasePaymentPlanIdList)){
            purchasePaymentPlanMapper.updateBatch(purchasePaymentPlanIdList);
        }
        PaymentApplyDO paymentApply = PaymentApplyConvert.INSTANCE.convertPaymentApplyDO(createReqVO);
        // 生成 付款申请主 编号
        paymentApply.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        paymentApply.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        // 插入
        paymentApplyMapper.insert(paymentApply);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())){
            submitTask(paymentApply.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 插入链路编号
        Map<String, List<String>> contractLinkCodeMap = getContractLinkCodeMap(createReqVO.getPurchaseContractCodeList());
        if (CollUtil.isNotEmpty(contractLinkCodeMap)) {
            List<String> linkCodeList = contractLinkCodeMap.values().stream().filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
            paymentApplyMapper.updateById(new PaymentApplyDO().setId(paymentApply.getId()).setLinkCodeList(linkCodeList));
            List<OrderLinkDTO> createList = new ArrayList<>();
            contractLinkCodeMap.forEach((k, v) -> {
                // 插入订单链路
                if (CollUtil.isNotEmpty(v)) {
                    List<OrderLinkDTO> orderLinkDTOList = v.stream().map(s -> new OrderLinkDTO()
                            .setBusinessId(paymentApply.getId())
                            .setCode(paymentApply.getCode())
                            .setName(BusinessNameDict.PAYMENT_APPLY_ORDER)
                            .setType(OrderLinkTypeEnum.PAYMENT_APPLY_ORDER.getValue())
                            .setLinkCode(s)
                            .setItem(paymentApply.getApplyerPurchaseItemList())
                            .setParentCode(k)
                            .setStatus(paymentApply.getAuditStatus())
                            .setCompanyId(paymentApply.getCompanyId())
                            .setBusinessSubjectName(paymentApply.getVenderName())
                            .setOrderMsg(paymentApply)).toList();
                    if (CollUtil.isNotEmpty(orderLinkDTOList)) {
                        createList.addAll(orderLinkDTOList);
                    }
                }
            });
            orderLinkApi.batchCreateOrderLink(createList);
        }
        responses.add(new CreatedResponse(paymentApply.getId(),paymentApply.getCode()));
        return  responses;
    }

    /**
     * 校验付款计划最后一笔是否全部付完
     *
     * @param applyPaymentPlanList 付款计划列表
     */
    private void validateLastPaymentPlan(List<ApplyPaymentPlan> applyPaymentPlanList) {
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            return;
        }
        List<String> contractList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getContractCode).toList();
        if (CollUtil.isEmpty(contractList)) {
            return;
        }
        // 已经创建过的付款申请不可重复创建
        List<PaymentApplyDO> paymentApplyDOS = paymentApplyMapper.selectList(new LambdaQueryWrapperX<PaymentApplyDO>().select(PaymentApplyDO::getApplyPaymentPlanList).eq(PaymentApplyDO::getPaymentStatus, BooleanEnum.NO.getValue()).ne(PaymentApplyDO::getAuditStatus,BpmProcessInstanceResultEnum.CLOSE.getResult()));
        if (CollUtil.isNotEmpty(paymentApplyDOS)) {
            List<Long> basePlanIdList = paymentApplyDOS.stream().map(PaymentApplyDO::getApplyPaymentPlanList).filter(Objects::nonNull).flatMap(List::stream).map(ApplyPaymentPlan::getId).toList();
            List<Long> planIdList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getId).toList();
            Set<Long> result = CollUtil.intersectionDistinct(basePlanIdList, planIdList);
            if (CollUtil.isNotEmpty(result)) {
                throw exception(PAYMENT_APPLY_IS_EXISTS);
            }
        }

        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(new LambdaQueryWrapperX<PurchasePaymentPlan>().in(PurchasePaymentPlan::getContractCode, contractList));
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            throw exception(PURCHASE_PAYMENT_EXCEPTION);
        }
        // 采购合同编号对应的最后一笔付款计划步骤
        Map<String, Integer> planStepMap = new HashMap<>();
        purchasePaymentPlans.forEach(s -> {
            Integer step = planStepMap.get(s.getContractCode());
            if (Objects.isNull(step)) {
                planStepMap.put(s.getContractCode(), s.getStep());
            } else {
                if (s.getStep() > step) {
                    planStepMap.put(s.getContractCode(), s.getStep());
                }
            }
        });
        applyPaymentPlanList.forEach(s -> {
            BigDecimal applyAmount = s.getApplyAmount();
            JsonAmount jsonReceivableAmount = s.getReceivableAmount();
            BigDecimal receivableAmount = jsonReceivableAmount.getAmount();
            BigDecimal paymentRatio = s.getPaymentRatio();
            BigDecimal realPaymentRatio = NumUtil.div(applyAmount, NumUtil.div(receivableAmount, paymentRatio)).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
            s.setRealPaymentRatio(realPaymentRatio);
//            s.setAppliedAmount(new JsonAmount().setAmount(applyAmount).setCurrency(jsonReceivableAmount.getCurrency()));
        });
    }

    /**
     * 批量获取采购合同链路编号map
     *
     * @param codeList 采购合同编号
     * @return 链路编号map
     */
    private Map<String, List<String>> getContractLinkCodeMap(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getLinkCodeList).in(PurchaseContractDO::getCode, codeList));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        return purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getLinkCodeList, (o, n) -> o));
    }

    private void dealPaymentPlan(List<ApplyerPurchaseContractItem> applyerPurchaseItemList, List<ApplyPaymentPlan> applyPaymentPlanList, boolean isDelete, BigDecimal totalAmount) {
        if (CollUtil.isEmpty(applyPaymentPlanList) || CollUtil.isEmpty(applyerPurchaseItemList)) {
            return;
        }
        // 采购明细本次申请金额缓存
        Map<String, BigDecimal> purchaseAmountMap = applyerPurchaseItemList.stream()
                .collect(Collectors.groupingBy(ApplyerPurchaseContractItem::getPurchaseContractCode,
                        Collectors.mapping(ApplyerPurchaseContractItem::getApplyAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        List<PurchasePaymentPlan> updatePlanList = applyPaymentPlanList.stream().map(s -> {
            PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
            purchasePaymentPlan.setId(s.getId());
            purchasePaymentPlan.setContractCode(s.getContractCode());
            purchasePaymentPlan.setStep(s.getStep());
            purchasePaymentPlan.setPaymentRatio(s.getPaymentRatio());
            purchasePaymentPlan.setReceivableAmount(s.getReceivableAmount());
            purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.IN_PAID.getValue());
            purchasePaymentPlan.setApplyAmount(s.getApplyAmount());
            // 已申请金额
            BigDecimal appliedAmount = Objects.isNull(s.getAppliedAmount()) ? BigDecimal.ZERO : s.getAppliedAmount().getAmount();
            // 本次申请金额
            if (CollUtil.isNotEmpty(purchaseAmountMap)) {
                BigDecimal applyAmount = purchaseAmountMap.get(s.getContractCode());
                if (Objects.nonNull(applyAmount)) {
                    if (isDelete) {
                        purchasePaymentPlan.setApplyAmount(NumUtil.sub(s.getApplyAmount(),applyAmount));
                        purchasePaymentPlan.setAppliedAmount(new JsonAmount().setAmount(NumUtil.sub(appliedAmount, applyAmount)).setCurrency(s.getAppliedAmount().getCurrency()));
                    } else {
                        purchasePaymentPlan.setApplyAmount(NumUtil.add(s.getApplyAmount(),applyAmount));
                        purchasePaymentPlan.setAppliedAmount(new JsonAmount().setAmount(NumUtil.add(appliedAmount, applyAmount)).setCurrency(s.getAppliedAmount().getCurrency()));
                    }
                }
            }
            return purchasePaymentPlan;
        }).toList();
        // 重新计算预计付款比例及金额
        if (!isDelete) {
            updatePaymentPlanInfo(updatePlanList, totalAmount);
        }
        purchasePaymentPlanMapper.updateBatch(updatePlanList);
    }

    /**
     * 重新计算采购明细计划付款比例及金额
     *
     * @param plan 付款计划
     */
    private PurchasePaymentPlan calcPaymentRatioAndAmount(PurchasePaymentPlan plan, BigDecimal applyTotalAmount, Integer status) {
        if (Objects.isNull(applyTotalAmount)) {
            return plan;
        }
        plan.setExeStatus(status);
        // 重新计算实际付款比例
        if (PaymentPlanExeStatusEnum.PAID.getValue().equals(status)) {
            calcRealPaymentRatio(plan);
        }
        return plan;
    }

    private void calcRealPaymentRatio(PurchasePaymentPlan plan) {
        String contractCode = plan.getContractCode();
        BigDecimal amount = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, contractCode).getTotalAmount().getAmount();
        BigDecimal appliedAmount = plan.getAppliedAmount().getAmount();
        BigDecimal paymentRatio = NumUtil.mul(NumUtil.div(appliedAmount, amount), CalculationDict.ONE_HUNDRED).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        plan.setRealPaymentRatio(paymentRatio);
    }

    /**
     * 重写采购付款计划预计付款比例及金额
     *
     * @param updatePlanList
     */
    private void updatePaymentPlanInfo(List<PurchasePaymentPlan> updatePlanList, BigDecimal totalAmount) {
        if (CollUtil.isEmpty(updatePlanList)) {
            return;
        }
        List<String> contractCodeList = updatePlanList.stream().map(PurchasePaymentPlan::getContractCode).toList();
        if (CollUtil.isEmpty(contractCodeList)) {
            return;
        }
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, contractCodeList);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return;
        }
        Map<String, List<PurchasePaymentPlan>> planMap = purchasePaymentPlans.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        if (CollUtil.isEmpty(planMap)) {
            return;
        }
        List<PurchasePaymentPlan> updateResult = new ArrayList<>();
        updatePlanList.forEach(s -> {
            List<PurchasePaymentPlan> linkPaymentPlan = planMap.get(s.getContractCode());
            if (CollUtil.isEmpty(linkPaymentPlan)) {
                return;
            }
            linkPaymentPlan.forEach(plan -> {
                // 进处理上一条付款计划
                if (plan.getStep() < s.getStep()) {
                    // 重新计算预计付款比例及金额
                    PurchasePaymentPlan purchasePaymentPlan = calcPaymentRatioAndAmount(plan, totalAmount, PaymentPlanExeStatusEnum.PAID.getValue());
                    updateResult.add(purchasePaymentPlan);
                } else if (plan.getStep().equals(s.getStep())) {
                    // 当输入金额大于等于应付金额时，重新计算预计付款比例及金额
                    if (s.getApplyAmount().compareTo(s.getReceivableAmount().getAmount()) >= 0) {
                        // 重新计算预计付款比例及金额
                        PurchasePaymentPlan purchasePaymentPlan = calcPaymentRatioAndAmount(s, totalAmount, PaymentPlanExeStatusEnum.IN_PAID.getValue());
                        updateResult.add(purchasePaymentPlan);
                    }
                }

            });
        });
        if (CollUtil.isNotEmpty(updateResult)) {
            purchasePaymentPlanMapper.updateBatch(updateResult);
        }
    }

    @Override
    public void updatePaymentApply(PaymentApplySaveReqVO updateReqVO) {
        // 校验存在
        PaymentApplyDO oldPaymentApplyDO = validatePaymentApplyExists(updateReqVO.getId());
        List<ApplyerPurchaseContractItem> oldApplyerPurchaseItemList = oldPaymentApplyDO.getApplyerPurchaseItemList();
        // 更新
        PaymentApplyDO updateObj = PaymentApplyConvert.INSTANCE.convertPaymentApplyDO(updateReqVO);
//        submitTask(updateReqVO.getId(), WebFrameworkUtils.getLoginUserId());
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = updateObj.getApplyerPurchaseItemList();
        applyerPurchaseItemList.forEach(s->{
            s.setRealApplyAmount(s.getApplyAmount());
        });
        Map<String, BigDecimal> applyAmountMapByCode = applyerPurchaseItemList.stream().collect(Collectors.groupingBy(ApplyerPurchaseContractItem::getPurchaseContractCode, // 按照 code 分组
                Collectors.reducing(BigDecimal.ZERO, // 初始值
                        ApplyerPurchaseContractItem::getApplyAmount,
                        BigDecimal::add)
        ));
        List<ApplyPaymentPlan> applyPaymentPlanList = updateReqVO.getApplyPaymentPlanList();
        applyPaymentPlanList.forEach(s-> {
            BigDecimal applyAmount = applyAmountMapByCode.get(s.getContractCode());
            if (Objects.nonNull(applyAmount)){
                s.setApplyAmount(NumUtil.add(s.getApplyAmount(),applyAmount));
            }
        });
        updateObj.setApplyPaymentPlanList(applyPaymentPlanList);
        paymentApplyMapper.updateById(updateObj);
        // 更新采购明细已申请金额
//        dealPurchaseContractItem(applyerPurchaseItemList, true, false, false);
        // 更新付款计划已申请金额
//        List<String> purchaseContractCodeList = oldPaymentApplyDO.getPurchaseContractCodeList();
//        if (CollUtil.isNotEmpty(purchaseContractCodeList)) {
//            PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, purchaseContractCodeList.get(CalculationDict.ZERO));
//            if (Objects.nonNull(purchaseContractDO)) {
//                // 更新付款计划已申请金额
//                dealPaymentPlan(applyerPurchaseItemList, oldPaymentApplyDO.getApplyPaymentPlanList(), false, purchaseContractDO.getTotalAmount().getAmount());
//            }
//        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateReqVO.getId(), WebFrameworkUtils.getLoginUserId());
            }
    }

    @Override
    public void deletePaymentApply(Long id) {
        // 校验存在
        PaymentApplyDO paymentApplyDO = validatePaymentApplyExists(id);
        // 删除
        paymentApplyMapper.deleteById(id);
        paymentApi.validatePayment(BusinessTypeEnum.PURCHASE_PAYMENT.getCode(), paymentApplyDO.getCode());
        LocalDateTime cancelTime = LocalDateTime.now();
        ClosePaymentApplyReq closePaymentApplyReq = new ClosePaymentApplyReq();
        closePaymentApplyReq.setCancelTime(cancelTime);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept cancelUser = adminUserApi.getUserDeptByUserId(loginUserId);
        closePaymentApplyReq.setCancelUser(cancelUser);
        orderLinkApi.updateOrderLinkStatus(paymentApplyDO.getCode(),BusinessNameDict.PAYMENT_APPLY_ORDER, paymentApplyDO.getLinkCodeList(), BpmProcessInstanceResultEnum.CLOSE.getResult());
        // 回写付款计划
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            return;
        }
        List<Long> paymentPalnIdList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getId).distinct().toList();
        if (CollUtil.isEmpty(paymentPalnIdList)) {
            return;
        }
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectBatchIds(paymentPalnIdList);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return;
        }
        ClosePaymentDTO closePaymentDTO = BeanUtils.toBean(closePaymentApplyReq, ClosePaymentDTO.class);
        closePaymentDTO.setBusinessCode(paymentApplyDO.getCode());
        paymentApi.closePayment(closePaymentDTO);
        Map<String, ApplyPaymentPlan> applyPaymentPlanMap = applyPaymentPlanList.stream().collect(Collectors.toMap(ApplyPaymentPlan::getContractCode, s -> s));
        Map<String, List<PurchasePaymentPlan>> purchasePaymentMap = purchasePaymentPlans.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        List<PurchasePaymentPlan> updateList = new ArrayList<>();
        purchasePaymentMap.forEach((k, v) -> {
            if (CollUtil.isEmpty(v)) {
                return;
            }
            ApplyPaymentPlan applyPaymentPlan = applyPaymentPlanMap.get(k);
            if (Objects.isNull(applyPaymentPlan)) {
                return;
            }
            Integer step = applyPaymentPlan.getStep();
            // 本次申请金额
            AtomicReference<BigDecimal> applyAmountAtomic = new AtomicReference<>(applyPaymentPlan.getApplyAmount());
            List<PurchasePaymentPlan> purchasePaymentPlanList = v.stream().sorted(Comparator.comparing(PurchasePaymentPlan::getStep).reversed()).toList();
            purchasePaymentPlanList.forEach(s -> {
                BigDecimal applyAmount = applyAmountAtomic.get();
                // 本次申请金额全部回退完则不再处理
                if (applyAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return;
                }
                // 当前付款计划步骤并不影响后续步骤
                if (s.getStep() > step) {
                    return;
                }
                JsonAmount appliedAmount = s.getAppliedAmount();
                // 如果当前付款计划还未申请则不需要回退
//                if (Objects.isNull(appliedAmount) || Objects.isNull(appliedAmount.getAmount()) || appliedAmount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
//                    return;
//                }
                // 如果付款计划已申请金额大于等于当前计划申请金额则直接扣减
                if (appliedAmount.getAmount().compareTo(applyAmount) >= 0) {
                    s.setAppliedAmount(new JsonAmount().setAmount(NumUtil.sub(appliedAmount.getAmount(), applyAmount)).setCurrency(appliedAmount.getCurrency()));
                    // 如果扣减之后已申请金额为0则修改状态为未支付  否则为部分支付
                    if (s.getAppliedAmount().getAmount().compareTo(BigDecimal.ZERO) == 0) {
                        s.setExeStatus(PaymentPlanExeStatusEnum.UNPAID.getValue());
                    } else {
                        s.setExeStatus(PaymentPlanExeStatusEnum.PART_PAID.getValue());
                    }
                } else {
                    // 如果付款计划已申请金额小于当前计划申请金额则置空付款计划已申请金额  状态为未支付
                    s.setAppliedAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(appliedAmount.getCurrency()));
                    s.setExeStatus(PaymentPlanExeStatusEnum.UNPAID.getValue());
                    applyAmountAtomic.set(NumUtil.sub(applyAmount, appliedAmount.getAmount()));
                }
                updateList.add(s);
            });
        });
        // 更新付款计划
        if (CollUtil.isNotEmpty(updateList)) {
            purchasePaymentPlanMapper.updateBatch(updateList);
        }
    }

    private PaymentApplyDO validatePaymentApplyExists(Long id) {
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectById(id);
        if (paymentApplyDO == null) {
            throw exception(PAYMENT_APPLY_NOT_EXISTS);
        }
        return paymentApplyDO;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long paymentApplyId, Long userId) {
        PaymentApplyDO paymentApplyDO = validatePaymentApplyExists(paymentApplyId);
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        AtomicReference<Integer> prepaymentFlag = new AtomicReference<>();
        AtomicReference<Integer> stockPurchaseFlag = new AtomicReference<>(0);
        BigDecimal prepayment = paymentApplyDO.getApplyTotalAmount();
        applyPaymentPlanList.forEach(s -> {
            Integer step = s.getStep();
            if (CollectionPlanStepEnum.ADVAMCE_RECEIPT.getStep().equals(step)) {
                prepaymentFlag.set(1);
            } else {
                prepaymentFlag.set(0);
            }
        });
        // 是否库存采购
        applyerPurchaseItemList.forEach(s -> {
            if (Objects.isNull(s.getSaleContractItemId())&&StrUtil.isEmpty(s.getSaleContractCode())) {
                stockPurchaseFlag.set(1);
            }
        });
        Map<String, Object> variable = new HashMap<>();
        BigDecimal applyTotalAmount = Objects.isNull(paymentApplyDO.getApplyTotalAmount())?BigDecimal.ZERO:paymentApplyDO.getApplyTotalAmount();
        variable.put("paymentMarkType",String.valueOf(paymentApplyDO.getPaymentMarkType()));
        variable.put("applyTotalAmount",String.valueOf(applyTotalAmount));
        variable.put("prepayment", prepayment);
        variable.put("stockPurchaseFlag", stockPurchaseFlag.get());
        variable.put("prepaymentFlag", prepaymentFlag.get());
        UserDept userDept = adminUserApi.getUserDeptByUserId(userId);
        String deptCode = userDept.getDeptCode();
        variable.put("deptCode", deptCode);
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, paymentApplyId, variable, null);
        updateAuditStatus(paymentApplyId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);

        // 更新采购明细已申请金额
        dealPurchaseContractItem(applyerPurchaseItemList, true, false, false);
        List<String> purchaseContractCodeList = paymentApplyDO.getPurchaseContractCodeList();
        if (CollUtil.isNotEmpty(purchaseContractCodeList)) {
            PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, purchaseContractCodeList.get(CalculationDict.ZERO));
            if (Objects.nonNull(purchaseContractDO)) {
                // 更新付款计划已申请金额
                dealPaymentPlan(applyerPurchaseItemList, applyPaymentPlanList, false, purchaseContractDO.getTotalAmount().getAmount());
            }
        }
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        PaymentApplyDO paymentApplyDO = validatePaymentApplyExists(auditableId);
        paymentApplyDO.setAuditStatus(auditStatus);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            paymentApplyDO.setProcessInstanceId(processInstanceId);
        }
        paymentApplyMapper.updateById(paymentApplyDO);
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
            if (CollUtil.isNotEmpty(applyPaymentPlanList)) {
                List<PurchasePaymentPlan> purchasePaymentPlans = applyPaymentPlanList.stream().map(s -> {
                    PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
                    purchasePaymentPlan.setId(s.getId());
                    purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.IN_PAID.getValue());
                    return purchasePaymentPlan;
                }).toList();
                purchasePaymentPlanMapper.updateBatch(purchasePaymentPlans);
            }
        }
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            // 更改关联单据信息
            orderLinkApi.updateOrderLink(new OrderLinkDTO().setOrderMsg(paymentApplyDO).setCode(paymentApplyDO.getCode()).setStatus(auditStatus).setName(BusinessNameDict.PAYMENT_APPLY_ORDER).setType(OrderLinkTypeEnum.PAYMENT_APPLY_ORDER.getValue()));
        }
    }

    @Override
    public PaymentApplyRespVO getPaymentApplyMessage(List<Long> idList,Integer step) {
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return null;
        }
        List<String> purchaseContractCodeList = purchasePaymentPlans.stream().map(PurchasePaymentPlan::getContractCode).distinct().toList();
        if (CollUtil.isEmpty(purchaseContractCodeList)) {
            return null;
        }
        String purchaseContractCode = purchaseContractCodeList.get(CalculationDict.ZERO);
        // 通过合同获取应付供应商信息
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getCode, purchaseContractCodeList);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        Map<String,String> paymentNameMap = new HashMap<>();
        purchaseContractDOList.forEach(s -> paymentNameMap.put(s.getCode(), s.getPaymentName()));
        List<UserDept> purchaseUserList = purchaseContractDOList.stream().map(PurchaseContractDO::getPurchaseUserId).distinct().map(s -> adminUserApi.getUserDeptByUserId(s)).toList();
        PurchaseContractDO purchaseContractDO = purchaseContractDOList.get(0);
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        PaymentApplyRespVO paymentApplyRespVO = new PaymentApplyRespVO();
        paymentApplyRespVO.setStep(step);
        paymentApplyRespVO.setPurchaseUserList(purchaseUserList);
        Long companyId = purchaseContractDO.getCompanyId();
        if (Objects.isNull(companyId)) {
            throw exception(PURCHASE_CONTRACT_PAYMENT_EMPTY, purchaseContractCode);
        }
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.isNull(companyDTO)) {
            throw exception(PURCHASE_COMPANY_EMPTY, purchaseContractCode);
        }
        // 付款主体
        paymentApplyRespVO.setCompanyId(companyDTO.getId());
        paymentApplyRespVO.setCompanyName(companyDTO.getCompanyName());
        String paymentVenderCode = purchaseContractDO.getPaymentVenderCode();
        if (StrUtil.isEmpty(paymentVenderCode)) {
            paymentVenderCode = purchaseContractDO.getVenderCode();
        }
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, paymentVenderCode).eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));

        if (Objects.isNull(venderDO)) {
            throw exception(PAYMENT_VENDER_NOT_EXISTS);
        }
        paymentApplyRespVO.setVenderId(venderDO.getId());
        paymentApplyRespVO.setVenderCode(venderDO.getCode());
        paymentApplyRespVO.setVenderName(venderDO.getName());
        List<JsonVenderTax> taxMsg = venderDO.getTaxMsg();
        if (CollUtil.isNotEmpty(taxMsg)){
            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s-> {
                paymentApplyRespVO.setTaxRate(s.getTaxRate());
                paymentApplyRespVO.setCurrency(s.getCurrency());
            });
        }
        String paymentName = purchaseContractDOList.stream().map(PurchaseContractDO::getPaymentName).distinct().collect(Collectors.joining(CommonDict.COMMA));
        paymentApplyRespVO.setPaymentName(paymentName);
        VenderBankaccountDO venderBankaccountDO = venderBankaccountMapper.selectOne(new LambdaQueryWrapperX<VenderBankaccountDO>().eq(VenderBankaccountDO::getVenderId, venderDO.getId()).eq(VenderBankaccountDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (Objects.nonNull(venderBankaccountDO)) {
            paymentApplyRespVO.setVenderBank(venderBankaccountDO.getBank());
            paymentApplyRespVO.setVenderBankAccount(venderBankaccountDO.getBankAccount());
            paymentApplyRespVO.setVenderBankPoc(venderBankaccountDO.getBankPoc());
        }

        purchaseContractDOList.stream().map(PurchaseContractDO::getTotalAmount).map(JsonAmount::getAmount).reduce(BigDecimal::add).ifPresent(paymentApplyRespVO::setGoodsTotalAmount);
        // 申请人信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept applyer = adminUserApi.getUserDeptByUserId(loginUserId);
        paymentApplyRespVO.setApplyer(applyer);
        paymentApplyRespVO.setApplyDate(LocalDateTime.now());
        // 查出采购合同对应所有付款计划
        List<PurchasePaymentPlan> purchasePaymentPlanList = purchasePaymentPlanMapper.selectList(new LambdaQueryWrapperX<PurchasePaymentPlan>().in(PurchasePaymentPlan::getContractCode, purchaseContractCodeList));
        if (CollUtil.isEmpty(purchasePaymentPlanList)) {
            throw exception(PURCHASE_PAYMENT_EXCEPTION);
        }
        purchasePaymentPlans.forEach(s->{
            if (StrUtil.isEmpty(s.getPaymentName())){
                s.setPaymentName(paymentNameMap.get(s.getContractCode()));
            }
        });
        //最后一笔付款返回加减项信息
        List<PurchasePaymentPlan> UNPAIDList = purchasePaymentPlanList.stream().filter(i -> i.getExeStatus().equals(PaymentPlanExeStatusEnum.UNPAID.getValue())).distinct().toList();
        if (CollUtil.isNotEmpty(UNPAIDList) && UNPAIDList.size() == 1) {
            List<PurchaseAddSubTerm> purchaseAddSubTermList = purchaseAddSubTermMapper.selectList(new LambdaQueryWrapperX<PurchaseAddSubTerm>().in(PurchaseAddSubTerm::getContractCode, purchaseContractCodeList));
            paymentApplyRespVO.setPurchaseAddSubTermList(purchaseAddSubTermList);
        }

        // 计算付款计划信息
        Map<String, BigDecimal> ratioMap = dealApplyPaymentPlan(purchasePaymentPlans, paymentApplyRespVO, purchaseContractCodeList, purchasePaymentPlanList);
        // 采购明细信息
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCodeList));
        purchasePaymentPlans.forEach(s -> {
            calcPurchaseContractItem(purchaseContractItemDOList, paymentApplyRespVO, purchasePaymentPlanList, s, ratioMap);
        });
        // 计算货值总金额
        Optional<BigDecimal> opt = paymentApplyRespVO.getApplyerPurchaseItemList().stream().map(ApplyerPurchaseContractItem::getApplyAmount).reduce(BigDecimal::add);
        // 计算申请总额 申请总额 + 加项- 减项
        paymentApplyRespVO.setApplyTotalAmount(calcApplyTotalAmount(purchaseContractCodeList, opt.get()));
        // 关联发票
        List<PurchaseRegistrationItem> purchaseRegistrationItemList = registrationItemMapper.selectList(new LambdaQueryWrapperX<PurchaseRegistrationItem>().in(PurchaseRegistrationItem::getPurchaseContractCode, purchaseContractCodeList));
        if (CollUtil.isNotEmpty(purchaseRegistrationItemList)){
            List<Long> registrationIdList = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getRegistrationId).distinct().toList();
            List<PurchaseRegistrationDO> purchaseRegistrationDOS = purchaseRegistrationMapper.selectList(new LambdaQueryWrapperX<PurchaseRegistrationDO>().in(PurchaseRegistrationDO::getId, registrationIdList).eq(PurchaseRegistrationDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()));
            if (CollUtil.isNotEmpty(purchaseRegistrationDOS)){
                Set<Long> auditRegistrationIdSet = purchaseRegistrationDOS.stream().map(PurchaseRegistrationDO::getId).collect(Collectors.toSet());
                List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyRespVO.getApplyerPurchaseItemList();
                applyerPurchaseItemList.forEach(s->{
                    List<PurchaseRegistrationItem> purchaseItemRegistrationItems = purchaseRegistrationItemList.stream().filter(registrationItem -> auditRegistrationIdSet.contains(registrationItem.getRegistrationId()) && registrationItem.getPurchaseContractItemId().equals(s.getId())).toList();
                    if (CollUtil.isNotEmpty(purchaseItemRegistrationItems)){
                        purchaseItemRegistrationItems.stream().map(pri->NumUtil.mul(pri.getInvoicPrice(),pri.getInveicRegisteredQuantity())).reduce(BigDecimal::add).ifPresent(s::setInvoicedAmount);
                        purchaseItemRegistrationItems.stream().map(PurchaseRegistrationItem::getInveicRegisteredQuantity).reduce(Integer::sum).ifPresent(s::setInvoicedQuantity);
                        purchaseItemRegistrationItems.stream().map(PurchaseRegistrationItem::getPurchaseCurrency).findFirst().ifPresent(s::setInvoicedCurrency);
                        s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
                    }
                });
                paymentApplyRespVO.setApplyerPurchaseItemList(applyerPurchaseItemList);
            }
        }
        paymentApplyRespVO.setRegistrationItemList(purchaseRegistrationItemList);
        return paymentApplyRespVO;
    }


    /**
     * 计算付款计划信息
     *
     * @param contractCodeList 采购合同编号列表
     * @param goodsTotalAmount 货款总金额
     * @return 付款比例
     */
    private BigDecimal calcApplyTotalAmount(List<String> contractCodeList, BigDecimal goodsTotalAmount) {
        AtomicReference<BigDecimal> applyTotalAmount = new AtomicReference<>(goodsTotalAmount);
        List<PurchaseAddSubTerm> purchaseAddSubTerms = purchaseAddSubTermMapper.selectList(PurchaseAddSubTerm::getContractCode, contractCodeList);
        if (CollUtil.isEmpty(purchaseAddSubTerms)) {
            return applyTotalAmount.get();
        }
        purchaseAddSubTerms.forEach(s -> {
            if (CalclationTypeEnum.ADD.getType().equals(s.getCalculationType())) {
                applyTotalAmount.set(NumUtil.add(applyTotalAmount.get(), s.getAmount().getAmount()));
            } else if (CalclationTypeEnum.DEDUCTION.getType().equals(s.getCalculationType())) {
                applyTotalAmount.set(NumUtil.sub(applyTotalAmount.get(), s.getAmount().getAmount()));
            }
        });
        return applyTotalAmount.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchWriteBackPaymentMsg(List<PaymentAppDTO> paymentAppDTOList) {
        if (CollUtil.isEmpty(paymentAppDTOList)) {
            return;
        }
        Map<String, PaymentAppDTO> paymentAppDTOMap = paymentAppDTOList.stream().collect(Collectors.toMap(PaymentAppDTO::getCode, Function.identity(), (o, n) -> n));
        List<PaymentApplyDO> paymentApplyDOList = paymentApplyMapper.selectList(new LambdaQueryWrapperX<PaymentApplyDO>().in(PaymentApplyDO::getCode, paymentAppDTOMap.keySet()));
        if (CollUtil.isEmpty(paymentApplyDOList)) {
            throw exception(PAYMENT_APPLY_NOT_EXISTS);
        }
        // TODO 待优化 批量操作
        paymentApplyDOList.forEach(s -> {
            List<ApplyPaymentPlan> applyPaymentPlanList = s.getApplyPaymentPlanList();
            if (CollUtil.isNotEmpty(applyPaymentPlanList)){
                applyPaymentPlanList.forEach(applyPaymentPlan->applyPaymentPlan.setAppliedAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(s.getCurrency())));
                s.setApplyPaymentPlanList(applyPaymentPlanList);
            }
            PaymentAppDTO paymentAppDTO = paymentAppDTOMap.get(s.getCode());
            if (Objects.isNull(paymentAppDTO)) {
                return;
            }
            s.setPaymentDate(paymentAppDTO.getPaymentDate());
            s.setPaymentUser(paymentAppDTO.getCashier());
            s.setBank(paymentAppDTO.getBank());
            s.setBankAccount(paymentAppDTO.getBankAccount());
            //回写实际付款信息为申请总金额
            BigDecimal realPaymentAmount = Objects.isNull(s.getRealPaymentAmount())? BigDecimal.ZERO : s.getRealPaymentAmount();
            s.setRealPaymentAmount(NumUtil.add(paymentAppDTO.getPaymentAmount().getAmount(), realPaymentAmount));
            s.setPaymentStatus(BooleanEnum.YES.getValue());
            // 回写供应商信息
            dealPaymentVender(s.getVenderCode(), s.getBank(), s.getBankAccount());
            // 回写加减项
            dealAddSubTerm(s.getPurchaseAddSubTermList());
            // 回写采购明细
            dealPurchaseContractItem(s.getApplyerPurchaseItemList(), false, true, false);
            // 回写付款计划
            dealPaymentPlan(s);
            if (CollUtil.isEmpty(paymentAppDTO.getAnnex())){
                return;
            }
            List<SimpleFile> annex = s.getAnnex();
            if (CollUtil.isEmpty(annex)){
                s.setAnnex(paymentAppDTO.getAnnex());
            }else {
                annex.addAll(paymentAppDTO.getAnnex());
                s.setAnnex(annex);
            }
        });
        // 回写采购计划
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDOList.stream()
                .map(PaymentApplyDO::getApplyPaymentPlanList)
                .flatMap(List::stream)
                .toList();
        rollbackPurchasePlan(applyPaymentPlanList);
        paymentApplyMapper.updateBatch(paymentApplyDOList);
    }

    private void dealPaymentPlan(PaymentApplyDO paymentApplyDO){
        if (Objects.isNull(paymentApplyDO)){
            return;
        }
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isEmpty(applyPaymentPlanList)){
            return;
        }
        if (paymentApplyDO.getApplyTotalAmount().compareTo(paymentApplyDO.getRealPaymentAmount()) == 0){
            applyPaymentPlanList.forEach(s->s.setAppliedAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB)));
        }
    }
    /**
     * 回写采购计划已支付金额及支付状态
     *
     * @param applyPaymentPlanList 付款申请中采购计划列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void rollbackPurchasePlan(List<ApplyPaymentPlan> applyPaymentPlanList) {
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            return;
        }
        // 提取采购合同编号
        List<String> contractCodeList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getContractCode).distinct().toList();
        // 获取采购合同所有计划缓存
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(new LambdaQueryWrapperX<PurchasePaymentPlan>().in(PurchasePaymentPlan::getContractCode, contractCodeList));
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            throw exception(PURCHASE_PAYMENT_EXCEPTION);
        }
        // 获取采购合同总金额缓存
        List<PurchaseContractDO> contractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getTotalAmount, PurchaseContractDO::getCode).in(PurchaseContractDO::getCode, contractCodeList));
        if (CollUtil.isEmpty(contractDOList)) {
            throw exception(PURCHASE_PAYMENT_EXCEPTION);
        }
        Map<String, JsonAmount> totalAmountMap = contractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getTotalAmount, (o, n) -> n));
        Map<String, List<PurchasePaymentPlan>> purchasePaymentPlanMap = purchasePaymentPlans.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        List<PurchasePaymentPlan> purchasePaymentPlanList = new ArrayList<>();
        Map<Long, PurchasePaymentPlan> purchasePaymentPlanMapById = purchasePaymentPlans.stream().collect(Collectors.toMap(PurchasePaymentPlan::getId, s -> s, (o, n) -> n));
        applyPaymentPlanList.forEach(s -> {
            JsonAmount jsonAmount = totalAmountMap.get(s.getContractCode());
            BigDecimal totalAmount;
            if (Objects.nonNull(jsonAmount)) {
                totalAmount = Objects.isNull(jsonAmount.getAmount()) ? BigDecimal.ONE : jsonAmount.getAmount();
            } else {
                totalAmount = BigDecimal.ONE;
            }
            PurchasePaymentPlan purchasePaymentPlan = purchasePaymentPlanMapById.get(s.getId());
            purchasePaymentPlan.setReceivableAmount(s.getReceivableAmount());
            purchasePaymentPlan.setPaymentRatio(s.getPaymentRatio());
            purchasePaymentPlan.setContractCode(s.getContractCode());
            purchasePaymentPlan.setPaymentTime(LocalDateTime.now());
            JsonAmount receivedAmount = s.getReceivedAmount();
            // 本次请款金额
            BigDecimal applyAmount = s.getApplyAmount();
            List<JsonPayment> paymentMsg = purchasePaymentPlan.getPaymentMsg();
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            UserDept paymentUser = adminUserApi.getUserDeptByUserId(loginUserId);
            JsonPayment jsonPayment = new JsonPayment().setPaymentAmount(new JsonAmount().setAmount(applyAmount).setCurrency(s.getReceivableAmount().getCurrency())).setPaymentUser(paymentUser).setPaymentTime(LocalDateTime.now());
            if (CollUtil.isEmpty(paymentMsg)) {
                purchasePaymentPlan.setPaymentMsg(List.of(jsonPayment));
            }else {
                paymentMsg.add(jsonPayment);
                purchasePaymentPlan.setPaymentMsg(paymentMsg);
            }

            // 如果付款计划没有支付过则直接回写已付金额
            if (Objects.isNull(receivedAmount) || receivedAmount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                purchasePaymentPlan.setReceivedAmount(new JsonAmount().setAmount(s.getApplyAmount()).setCurrency(s.getReceivableAmount().getCurrency()));
            } else {
                purchasePaymentPlan.setReceivedAmount(new JsonAmount().setAmount(NumUtil.add(receivedAmount.getAmount(), applyAmount)).setCurrency(receivedAmount.getCurrency()));
            }
            JsonAmount newAppliedAmount = new JsonAmount().setAmount(NumUtil.add(s.getAppliedAmount().getAmount(), applyAmount)).setCurrency(s.getAppliedAmount().getCurrency());
            purchasePaymentPlan.setAppliedAmount(newAppliedAmount);
            purchasePaymentPlan.setRealPaymentRatio(NumUtil.mul(NumUtil.div(purchasePaymentPlan.getReceivedAmount().getAmount(), totalAmount),CalculationDict.ONE_HUNDRED));
            List<PurchasePaymentPlan> purchaseContractPaymentPlans = purchasePaymentPlanMap.get(s.getContractCode());
            if (CollUtil.isEmpty(purchaseContractPaymentPlans)) {
                return;
            }
            // 当前计划之前所有应付
            AtomicReference<BigDecimal> allReceivableAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 当前计划之前所有已付
            AtomicReference<BigDecimal> allReceivedAmount = new AtomicReference<>(BigDecimal.ZERO);
            purchaseContractPaymentPlans.forEach(x->{
                if (x.getStep() > s.getStep()) {
                    return;
                }
                BigDecimal stepReceivableAmount = Objects.isNull(x.getReceivableAmount()) ? BigDecimal.ZERO : Objects.isNull(x.getReceivableAmount().getAmount()) ? BigDecimal.ZERO : x.getReceivableAmount().getAmount();
                BigDecimal stepReceivedAmount = Objects.isNull(x.getReceivedAmount()) ? BigDecimal.ZERO : Objects.isNull(x.getReceivedAmount().getAmount()) ? BigDecimal.ZERO : x.getReceivedAmount().getAmount();
                allReceivableAmount.set(NumUtil.add(allReceivableAmount.get(), stepReceivableAmount));
                allReceivedAmount.set(NumUtil.add(allReceivedAmount.get(), stepReceivedAmount));
            });
            if (allReceivableAmount.get().compareTo(allReceivedAmount.get()) <= 0) {
                purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.PAID.getValue());
            }else {
                purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.PART_PAID.getValue());
            }
            purchasePaymentPlanList.add(purchasePaymentPlan);
        });
        purchasePaymentPlanMapper.updateBatch(purchasePaymentPlanList);
    }

    @Override
    public void createPaymentOrder(Long paymnetApplyId) {
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectById(paymnetApplyId);
        if (Objects.isNull(paymentApplyDO)) {
            throw exception(PAYMENT_APPLY_NOT_EXISTS);
        }
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            throw exception(PAYMENT_PLAN_EMPTY);
        }
        PaymentSaveDTO paymentSaveDTO = new PaymentSaveDTO();
        paymentSaveDTO.setPaymentMethod(paymentApplyDO.getPaymentMethod());
        paymentSaveDTO.setAcceptanceDays(paymentApplyDO.getAcceptanceDays());
        paymentSaveDTO.setApplyCode(paymentApplyDO.getCode());
        paymentSaveDTO.setCompanyId(paymentApplyDO.getCompanyId());
        paymentSaveDTO.setBank(paymentApplyDO.getBank());
        paymentSaveDTO.setBankAccount(paymentApplyDO.getBankAccount());
        paymentSaveDTO.setTargetBank(paymentApplyDO.getVenderBank());
        paymentSaveDTO.setTargetBankPoc(paymentApplyDO.getVenderBankPoc());
        paymentSaveDTO.setTargetBankAccount(paymentApplyDO.getVenderBankAccount());
        paymentSaveDTO.setLinkCodeList(paymentApplyDO.getLinkCodeList());
        paymentSaveDTO.setStatus(PaymentStatusEnum.UNPAID.getValue());
        paymentSaveDTO.setAmount(new JsonAmount().setAmount(paymentApplyDO.getApplyTotalAmount()).setCurrency(paymentApplyDO.getCurrency()));
        paymentSaveDTO.setApplyPaymentDate(paymentApplyDO.getApplyPaymentDate());
        paymentSaveDTO.setBusinessType(BusinessTypeEnum.PURCHASE_PAYMENT.getCode());
        paymentSaveDTO.setBusinessCode(paymentApplyDO.getCode());
        // 查找供应商类型  业务供应商 or 行政供应商
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, paymentApplyDO.getVenderCode()).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (Objects.isNull(venderDO)) {
            throw exception(VENDER_NOT_EXISTS, paymentApplyDO.getVenderCode());
        }
        Integer venderType = venderDO.getVenderType();
        if (VenderTypeEnum.ADMINISTRATION_VENDER.getValue().equals(venderType)) {
            paymentSaveDTO.setBusinessSubjectType(BusinessSubjectTypeEnum.VENDER.getCode());
        } else if (VenderTypeEnum.BUSINESS_VENDER.getValue().equals(venderType)) {
            paymentSaveDTO.setBusinessSubjectType(BusinessSubjectTypeEnum.VENDER_BIZ.getCode());
        }
        paymentSaveDTO.setBusinessSubjectCode(paymentApplyDO.getVenderCode());
        paymentSaveDTO.setApplyer(paymentApplyDO.getApplyer());
        paymentSaveDTO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        paymentSaveDTO.setApplyAmount(Collections.singletonList(new JsonAmount().setAmount(paymentApplyDO.getApplyTotalAmount()).setCurrency(paymentApplyDO.getCurrency())));
        // 付款申请信息
        PaymentApplyEntity paymentApplyEntity = new PaymentApplyEntity();
        paymentApplyEntity.setPaymentApplyId(paymentApplyDO.getId());
        paymentApplyEntity.setPaymentApplyCode(paymentApplyDO.getCode());
        paymentApplyEntity.setCompanyName(paymentApplyDO.getCompanyName());
        paymentApplyEntity.setApplyDate(paymentApplyDO.getApplyDate());
        // TODO 计划付款日是否需要计算???
        //        paymentApplyEntity.setPlanPaymentDate(paymentApplyDO.getPlanPaymentDate());
        paymentApplyEntity.setApplyAmount(new JsonAmount().setAmount(paymentApplyDO.getApplyTotalAmount()).setCurrency(paymentApplyDO.getCurrency()));
        paymentApplyEntity.setPaymentType(paymentApplyDO.getStep());
        paymentApplyEntity.setBusinessObjectName(paymentApplyDO.getVenderName());
        paymentApplyEntity.setApplyer(paymentApplyDO.getApplyer());
        paymentApplyEntity.setStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        paymentSaveDTO.setPaymentApply(paymentApplyEntity);
        Long applyerId = paymentApplyDO.getApplyer().getUserId();
        Map<Long, UserBackAccountDTO> userBackAccountDTOMap = adminUserApi.getUserBackAccountDTOMap(applyerId);
        if (CollUtil.isNotEmpty(userBackAccountDTOMap)) {
            UserBackAccountDTO userBackAccountDTO = userBackAccountDTOMap.get(applyerId);
            if (Objects.nonNull(userBackAccountDTO)) {
                paymentSaveDTO.setBank(userBackAccountDTO.getBank())
                        .setBankAccount(userBackAccountDTO.getBankAccount())
                        .setBankPoc(userBackAccountDTO.getBankPoc())
                        .setBankAddress(userBackAccountDTO.getBankAddress())
                        .setBankCode(userBackAccountDTO.getBankCode());
            }
        }
        paymentApi.batchCreatePayment(List.of(paymentSaveDTO));
        // 回写采购计划已申请金额
        // 获取申请人采购合同项列表
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        // 按采购合同编码分组，计算每个合同的申请金额总和
        Map<String, Optional<BigDecimal>> applyAmountMap = applyerPurchaseItemList.stream().collect(Collectors.groupingBy(ApplyerPurchaseContractItem::getPurchaseContractCode, Collectors.mapping(ApplyerPurchaseContractItem::getApplyAmount, Collectors.reducing(BigDecimal::add))));
        // 提取付款计划ID列表并去重
        List<Long> paymentPlanIdList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getId).distinct().toList();
        // 构建需要更新的采购付款计划列表
        applyPaymentPlanList.forEach(s -> {
            String contractCode = s.getContractCode();
            Optional<BigDecimal> appliedAmountOpt = applyAmountMap.get(contractCode);
            if (Objects.isNull(appliedAmountOpt)) {
                return;
            }
            appliedAmountOpt.ifPresent(appliedAmount -> {
                BigDecimal oldAppliedAmount = Objects.isNull(s.getAppliedAmount()) ? BigDecimal.ZERO : Objects.isNull(s.getAppliedAmount().getAmount()) ? BigDecimal.ZERO : s.getAppliedAmount().getAmount();
                s.setAppliedAmount(new JsonAmount().setAmount(NumUtil.add(oldAppliedAmount, appliedAmount)).setCurrency(paymentApplyDO.getCurrency()));
            });
        });
        paymentApplyDO.setApplyPaymentPlanList(applyPaymentPlanList);
        paymentApplyMapper.updateById(paymentApplyDO);
    }


    @Override
    public void cancelProcessInstance(Long userId, String processInstanceId) {
        bpmProcessInstanceApi.cancelProcessInstance(userId, processInstanceId);
        // 根据流程实例编号哦查找付款申请
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectOne(PaymentApplyDO::getProcessInstanceId, processInstanceId);
        if (Objects.isNull(paymentApplyDO)) {
            throw exception(PAYMENT_APPLY_NOT_EXISTS);
        }
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isNotEmpty(applyerPurchaseItemList) && CollUtil.isNotEmpty(applyPaymentPlanList)) {
            dealPurchaseContractItem(applyerPurchaseItemList, true, false, true);
            // 更新付款计划已申请金额
            dealPaymentPlan(applyerPurchaseItemList, applyPaymentPlanList, true, paymentApplyDO.getApplyTotalAmount());
        }

    }

    @Override
    public void antiAudit(Long id) {
        // 校验存在
        PaymentApplyDO paymentApplyDO = validatePaymentApplyExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(paymentApplyDO.getCode());
        // 更改付款申请单状态
        paymentApplyMapper.updateById(PaymentApplyDO.builder().id(id).auditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult()).build());
        // 更新采购明细已申请金额
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        if (CollUtil.isEmpty(applyerPurchaseItemList)) {
            return;
        }
        // 更新采购合同明细已申请金额
        dealPurchaseContractItem(applyerPurchaseItemList, true, false, true);
        // 更新付款计划已申请金额
        dealPaymentPlan(applyerPurchaseItemList, paymentApplyDO.getApplyPaymentPlanList(), true, null);
        // 更新付款计划状态
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isNotEmpty(applyPaymentPlanList)) {
            List<PurchasePaymentPlan> purchasePaymentPlans = applyPaymentPlanList.stream().map(s -> {
                PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
                purchasePaymentPlan.setId(s.getId());
                purchasePaymentPlan.setExeStatus(PaymentPlanExeStatusEnum.UNPAID.getValue());
                return purchasePaymentPlan;
            }).toList();
            purchasePaymentPlanMapper.updateBatch(purchasePaymentPlans);
        }
    }

    @Override
    public PaymentApplyDTO getPaymentApplyByCode(String code) {
        if (Objects.isNull(code)) {
            logger.error("[付款申请主]未获取到付款申请主id");
            return null;
        }
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectOne(PaymentApplyDO::getCode, code);
        if (paymentApplyDO == null) {
            return null;
        }
        PaymentApplyDTO dto = BeanUtils.toBean(paymentApplyDO, PaymentApplyDTO.class);
        List<ApplyerPurchaseContractItemDTO> itemList = BeanUtils.toBean(paymentApplyDO.getApplyerPurchaseItemList(), ApplyerPurchaseContractItemDTO.class);
        dto.setApplyerPurchaseItemList(itemList);
        return dto;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePaymentApply(ClosePaymentApplyReq closePaymentApplyReq) {
        Long id = closePaymentApplyReq.getId();
        PaymentApplyDO paymentApplyDO = validatePaymentApplyExists(id);
        paymentApi.validatePayment(BusinessTypeEnum.PURCHASE_PAYMENT.getCode(), paymentApplyDO.getCode());
        paymentApplyDO.setAuditStatus(BpmProcessInstanceResultEnum.CLOSE.getResult());
        LocalDateTime cancelTime = LocalDateTime.now();
        closePaymentApplyReq.setCancelTime(cancelTime);
        paymentApplyDO.setCancelTime(cancelTime);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept cancelUser = adminUserApi.getUserDeptByUserId(loginUserId);
        closePaymentApplyReq.setCancelUser(cancelUser);
        paymentApplyDO.setCancelUser(cancelUser);
        paymentApplyDO.setCancelReason(closePaymentApplyReq.getCancelReason());
        paymentApplyMapper.updateById(paymentApplyDO);
        orderLinkApi.updateOrderLinkStatus(paymentApplyDO.getCode(),BusinessNameDict.PAYMENT_APPLY_ORDER, paymentApplyDO.getLinkCodeList(), BpmProcessInstanceResultEnum.CLOSE.getResult());
        // 回写付款计划
        List<ApplyPaymentPlan> applyPaymentPlanList = paymentApplyDO.getApplyPaymentPlanList();
        if (CollUtil.isEmpty(applyPaymentPlanList)) {
            return;
        }
        List<Long> paymentPalnIdList = applyPaymentPlanList.stream().map(ApplyPaymentPlan::getId).distinct().toList();
        if (CollUtil.isEmpty(paymentPalnIdList)) {
            return;
        }
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectBatchIds(paymentPalnIdList);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return;
        }
        ClosePaymentDTO closePaymentDTO = BeanUtils.toBean(closePaymentApplyReq, ClosePaymentDTO.class);
        closePaymentDTO.setBusinessCode(paymentApplyDO.getCode());
        paymentApi.closePayment(closePaymentDTO);
        Map<String, ApplyPaymentPlan> applyPaymentPlanMap = applyPaymentPlanList.stream().collect(Collectors.toMap(ApplyPaymentPlan::getContractCode, s -> s));
        Map<String, List<PurchasePaymentPlan>> purchasePaymentMap = purchasePaymentPlans.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        List<PurchasePaymentPlan> updateList = new ArrayList<>();
        purchasePaymentMap.forEach((k, v) -> {
            if (CollUtil.isEmpty(v)) {
                return;
            }
            ApplyPaymentPlan applyPaymentPlan = applyPaymentPlanMap.get(k);
            if (Objects.isNull(applyPaymentPlan)) {
                return;
            }
            Integer step = applyPaymentPlan.getStep();
            // 本次申请金额
            AtomicReference<BigDecimal> applyAmountAtomic = new AtomicReference<>(applyPaymentPlan.getApplyAmount());
            List<PurchasePaymentPlan> purchasePaymentPlanList = v.stream().sorted(Comparator.comparing(PurchasePaymentPlan::getStep).reversed()).toList();
            purchasePaymentPlanList.forEach(s -> {
                BigDecimal applyAmount = applyAmountAtomic.get();
                // 本次申请金额全部回退完则不再处理
                if (applyAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return;
                }
                // 当前付款计划步骤并不影响后续步骤
                if (s.getStep() > step) {
                    return;
                }
                JsonAmount appliedAmount = s.getAppliedAmount();
                // 如果当前付款计划还未申请则不需要回退
//                if (Objects.isNull(appliedAmount) || Objects.isNull(appliedAmount.getAmount()) || appliedAmount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
//                    return;
//                }
                // 如果付款计划已申请金额大于等于当前计划申请金额则直接扣减
                if (appliedAmount.getAmount().compareTo(applyAmount) >= 0) {
                    s.setAppliedAmount(new JsonAmount().setAmount(NumUtil.sub(appliedAmount.getAmount(), applyAmount)).setCurrency(appliedAmount.getCurrency()));
                    // 如果扣减之后已申请金额为0则修改状态为未支付  否则为部分支付
                    if (s.getAppliedAmount().getAmount().compareTo(BigDecimal.ZERO) == 0) {
                        s.setExeStatus(PaymentPlanExeStatusEnum.UNPAID.getValue());
                    } else {
                        s.setExeStatus(PaymentPlanExeStatusEnum.PART_PAID.getValue());
                    }
                } else {
                    // 如果付款计划已申请金额小于当前计划申请金额则置空付款计划已申请金额  状态为未支付
                    s.setAppliedAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(appliedAmount.getCurrency()));
                    s.setExeStatus(PaymentPlanExeStatusEnum.UNPAID.getValue());
                    applyAmountAtomic.set(NumUtil.sub(applyAmount, appliedAmount.getAmount()));
                }
                updateList.add(s);
            });
        });
        // 更新付款计划
        if (CollUtil.isNotEmpty(updateList)) {
            purchasePaymentPlanMapper.updateBatch(updateList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId) {
        if (id == null) {
            throw exception(INVOICING_NOTICES_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        reportCode = reportCode.trim();
        if (sourceType != null && sourceCode == null) {
            throw exception(SOURCECODE_NULL);
        }
        if (sourceType == null && sourceCode != null) {
            throw exception(SOURCETYPE_NULL);
        }
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectById(id);
        if (paymentApplyDO == null) {
            throw exception(INVOICING_NOTICES_NOT_EXISTS);
        }
        ReportDTO reportDTO;
        if (sourceCode != null && companyId != null) {
            reportDTO = reportApi.getSourceReport(reportCode, sourceCode, sourceType, companyId);
        } else if (companyId != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, companyId);
        } else {
            reportDTO = reportApi.getReport(reportCode);
        }
        if (reportDTO == null || CollectionUtils.isEmpty(reportDTO.getAnnex())) {
            throw exception(REPORT_NULL);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        String projectPath = System.getProperty("user.dir");
        HashMap<String, Object> params = new HashMap<>();
        //扣款金额
        JsonAmount subAddTotalAmount = paymentApplyDO.getSubAddTotalAmount();
        if (Objects.nonNull(subAddTotalAmount)&&Objects.nonNull(subAddTotalAmount.getAmount())){
            params.put("descAmount",subAddTotalAmount.getAmount());
        }
        //公司名称
        params.put("companyTitle", paymentApplyDO.getCompanyName());
        // 申请人
        params.put("applyer", paymentApplyDO.getApplyer().getNickname());
//        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(paymentApplyDO.getCompanyId());
//        if (Objects.nonNull(companyDTO)) {
//            // 我方公司
//            params.put("companyName", companyDTO.getCompanyName());
//        }
        // 付款方式
//        DictDataRespDTO paymentMethod = dictDataApi.getDictData("pay_method", paymentApplyDO.getPaymentMethod().toString());
//        params.put("paymentMethod",paymentMethod.getLabel());
        params.put("paymentMethod",paymentApplyDO.getPaymentName());
        // 收款单位
        params.put("venderName", paymentApplyDO.getVenderName());
        // 申请单号
        params.put("code", paymentApplyDO.getCode());
        // 申请部门
        params.put("deptName", paymentApplyDO.getApplyer().getDeptName());
        // 采购订单
        List<String> purchaseContractCodeList = paymentApplyDO.getPurchaseContractCodeList();
        if (CollUtil.isNotEmpty(purchaseContractCodeList)){
            params.put("purchaseContractCode", purchaseContractCodeList.stream().collect(Collectors.joining(CommonDict.COMMA)));
        }
        // 合同总额
        BigDecimal totalAmount = paymentApplyDO.getGoodsTotalAmount().setScale(2, RoundingMode.HALF_UP);
        params.put("totalAmount", totalAmount.stripTrailingZeros().toPlainString());
        // 付款类别
        Integer step = paymentApplyDO.getStep();
        // params.put("paymentType", CollectionPlanStepEnum.ADVAMCE_RECEIPT.getStep().equals(step)?"预付":"应付");
        // 出运日期 TODO 怎么取？？？
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        LocalDateTime latestEstDepartureTime = applyerPurchaseItemList.stream()
                .filter(item -> item.getEstDepartureTime() != null)
                .map(ApplyerPurchaseContractItem::getEstDepartureTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        if (Objects.nonNull(latestEstDepartureTime)){
            params.put("shipDate", latestEstDepartureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        // 外销订单
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getCode, purchaseContractCodeList));
        if(CollUtil.isNotEmpty(purchaseContractDOList)){
            List<String> saleContractCodeList = purchaseContractDOList.stream().filter(item->Objects.nonNull(item.getSaleContractCode())).map(PurchaseContractDO::getSaleContractCode).distinct().toList();
            if(!saleContractCodeList.isEmpty()){
                params.put("saleContractCode", String.join(",", saleContractCodeList));
            }
        }
        // 是否收汇 TODO 怎么取？？？
        params.put("settleFlag", "");
        // 申请日期
        params.put("applyDate", paymentApplyDO.getApplyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // 申请付款日期
        params.put("applyPaymentDate", paymentApplyDO.getApplyPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // 已付金额
        applyerPurchaseItemList.stream().map(ApplyerPurchaseContractItem::getPaymentAmount).reduce(BigDecimal::add).ifPresent(s->{
            params.put("paidAmount", s.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        });
        // 申请金额
        params.put("applyAmount", paymentApplyDO.getApplyTotalAmount().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        // 支付币种
        params.put("currency", paymentApplyDO.getCurrency());
        // 备注
        //String pay = "";
        if(Objects.nonNull(paymentApplyDO.getPaymentMarkType())){
            DictDataRespDTO scmPaymentApply = dictDataApi.getDictData("payment_mark_type", paymentApplyDO.getPaymentMarkType().toString());
           if(Objects.nonNull(scmPaymentApply)) {
               // pay = "付款类型：" + scmPaymentApply.getLabel();
               params.put("paymentType", scmPaymentApply.getLabel());
           }
        }
        params.put("remark", paymentApplyDO.getRemark() );
        // 审批信息
        String processInstanceId = paymentApplyDO.getProcessInstanceId();
        if (StrUtil.isNotEmpty(processInstanceId)){
            Map<String, String> approverMap = bpmProcessInstanceApi.getApproverByProcessInstance(processInstanceId);
            // 财务审批
            String fmsApprover = approverMap.get("财务总监");
            if (StrUtil.isNotEmpty(fmsApprover)){
                params.put("fmsApprover",fmsApprover);
            }
            // 总经理
            params.put("managerApprover", approverMap.get("业务总监"));
            // 部门审批
            params.put("deptApprover", approverMap.get("部门经理"));
        }
        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,paymentApplyDO.getCode());
        //首次打印更新打印状态和打印时间
        if (!paymentApplyDO.getPrintFlag().equals(PrintStatusEnum.PRINTED.getStatus())) {
            paymentApplyDO.setPrintFlag(PrintStatusEnum.PRINTED.getStatus());
            paymentApplyMapper.updateById(paymentApplyDO);
        }
        // 增加操作日志
        OperateLogUtils.addOperateLog(OPERATOR_EXTS_KEY, paymentApplyDO.getCode(), "打印");
        return result;
    }

    @Override
    public List<PaymentApplyDO> getListByPurchaseCode(String contractCode) {
        LambdaQueryWrapperX<PaymentApplyDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.apply("(purchase_contract_code_list = {0} OR purchase_contract_code_list LIKE CONCAT('%,', {0}, ',%') " +
                "OR purchase_contract_code_list LIKE CONCAT({0}, ',%') OR purchase_contract_code_list LIKE CONCAT('%,', {0}))", contractCode);
        return paymentApplyMapper.selectList(queryWrapper);
    }

    @Override
    public boolean validateTransformPaymentApply(String purchaseContractCode) {
        if (StrUtil.isEmpty(purchaseContractCode)){
            return false;
        }
        LambdaQueryWrapperX<PaymentApplyDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.ne(PaymentApplyDO::getPaymentStatus, BpmProcessInstanceResultEnum.CLOSE.getResult());
        String condition = "FIND_IN_SET('" + purchaseContractCode + "', purchase_contract_code_list)";
        queryWrapper.apply( condition);
        return paymentApplyMapper.exists(queryWrapper);
    }

    @Override
    public void batchWriteAnnex(List<PaymentAppDTO> paymentAppDTOList) {
        if (CollUtil.isEmpty(paymentAppDTOList)){
            return;
        }
        // 提取所有包含附件的支付申请，并按照code分组附件列表
        Map<String, List<SimpleFile>> annexMap = paymentAppDTOList.stream()
                .filter(s->CollUtil.isNotEmpty(s.getAnnex()))
                .collect(Collectors.groupingBy(
                        PaymentAppDTO::getCode,
                        Collectors.flatMapping(dto -> dto.getAnnex().stream(), Collectors.toList())
                ));
        if (CollUtil.isEmpty(annexMap)){
            return;
        }
        // 获取所有支付申请的code并去重
        List<String> paymentApplyCodeList = paymentAppDTOList.stream().map(PaymentAppDTO::getCode).distinct().toList();
        // 查询对应的付款计划信息
        List<PaymentApplyDO> paymentApplyDOS = paymentApplyMapper.selectList(new LambdaQueryWrapperX<PaymentApplyDO>().select(PaymentApplyDO::getApplyPaymentPlanList,PaymentApplyDO::getCode).in(PaymentApplyDO::getCode, paymentApplyCodeList));
        if (CollUtil.isEmpty(paymentApplyDOS)){
            return;
        }
        // 将付款计划ID按照支付申请code分组
        Map<String, List<Long>> paymentPlanMap = paymentApplyDOS.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        PaymentApplyDO::getCode,
                        Collectors.flatMapping(
                                paymentApplyDO -> {
                                    List<ApplyPaymentPlan> planList = paymentApplyDO.getApplyPaymentPlanList();
                                    return Objects.nonNull(planList) ? planList.stream().map(ApplyPaymentPlan::getId).distinct() : Stream.empty();
                                },
                                Collectors.toList()
                        )
                ));
        if (CollUtil.isEmpty(paymentPlanMap)){
            return;
        }
        // 构建付款计划ID到附件列表的映射，用于后续更新操作
        Map<Long,List<SimpleFile>> updateMap = new HashMap<>();
        paymentPlanMap.forEach((k,v)->{
            List<SimpleFile> annex = annexMap.get(k);
            if (CollUtil.isEmpty(annex)){
                return;
            }
            v.forEach(purchasePaymentPlanId->{
                updateMap.merge(purchasePaymentPlanId, annex, (oldList, newList) -> {
                    // 使用LinkedHashSet保持顺序并去重
                    Set<String> existingUrls = oldList.stream()
                            .map(SimpleFile::getFileUrl)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    List<SimpleFile> mergedList = new ArrayList<>(oldList);
                    newList.stream()
                            .filter(newFile -> Objects.nonNull(newFile.getFileUrl()))
                            .filter(newFile -> !existingUrls.contains(newFile.getFileUrl()))
                            .forEach(mergedList::add);
                    return mergedList;
                });
            });
        });
        // 构造需要批量更新的采购付款计划对象列表
        List<PurchasePaymentPlan> updateList = updateMap.entrySet().stream().map(entry -> {
            PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
            purchasePaymentPlan.setId(entry.getKey());
            List<SimpleFile> existingAnnex = purchasePaymentPlan.getAnnex();
            List<SimpleFile> newAnnex = entry.getValue();
            // 如果现有annex为空，则直接使用新的annex
            if (CollUtil.isEmpty(existingAnnex)) {
                purchasePaymentPlan.setAnnex(newAnnex);
                return purchasePaymentPlan;
            }
            // 如果新的annex为空，则保持现有annex不变
            if (CollUtil.isEmpty(newAnnex)) {
                return purchasePaymentPlan;
            }
            // 创建一个新的列表来存储合并结果
            List<SimpleFile> mergedAnnex = new ArrayList<>(existingAnnex);
            // 获取现有annex中的所有fileUrl
            Set<String> existingFileUrls = existingAnnex.stream()
                    .map(SimpleFile::getFileUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            // 只添加fileUrl不重复的新附件
            newAnnex.stream()
                    .filter(Objects::nonNull)
                    .filter(newFile -> Objects.nonNull(newFile.getFileUrl()))
                    .filter(newFile -> !existingFileUrls.contains(newFile.getFileUrl()))
                    .forEach(mergedAnnex::add);
            purchasePaymentPlan.setAnnex(mergedAnnex);
            return purchasePaymentPlan;
        }).toList();

        // 批量更新
        purchasePaymentPlanMapper.updateBatch(updateList);
    }


    /**
     * 校验反审核状态
     *
     * @param applyCode 编号
     */
    private void validateAntiAuditStatus(String applyCode) {
        Long count = paymentApplyMapper.validateAntiAuditStatus(applyCode);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    /**
     * 计算采购明细本次申请金额
     *
     * @param purchaseContractItemDOList 采购明细列表
     * @param paymentApplyRespVO         付款申请响应体
     */
    private void calcPurchaseContractItem(List<PurchaseContractItemDO> purchaseContractItemDOList, PaymentApplyRespVO paymentApplyRespVO, List<PurchasePaymentPlan> purchasePaymentPlanList, PurchasePaymentPlan purchasePaymentPlan, Map<String, BigDecimal> ratioMap) {
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return;
        }
        List<Long> saleItemIdList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getSaleContractItemId).filter(Objects::nonNull).distinct().toList();
        List<String> saleItemUniqueCodeList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getSaleItemUniqueCode).filter(Objects::nonNull).distinct().toList();
        Map<Long, Boolean> saleTypeMap = saleContractApi.getSaleItemSaleType(saleItemIdList);
        Map<Long, LocalDateTime> estDepartureTimeMap = shipmentApi.getEstDepartureTimeByPurchaseCodeList(saleItemIdList);
        Map<String, LocalDateTime> outStockTimeMap = stockApi.getOutStockTimeBySaleItems(saleItemUniqueCodeList);
        List<ApplyerPurchaseContractItem> itemList = new ArrayList<>();
        // 上笔计划剩余未申请金额
        AtomicReference<BigDecimal> notAppliedAmountCache = new AtomicReference<>(BigDecimal.ZERO);
        // 未付款的总比例
        AtomicReference<BigDecimal> unPaidRatioCache = new AtomicReference<>(BigDecimal.ZERO);
        // 当前步骤所占未付款比例
        AtomicReference<BigDecimal> stepRatioCache = new AtomicReference<>(BigDecimal.ZERO);
        // 是否存在部分付款
        AtomicReference<Boolean> paymentFlag = new AtomicReference<>(false);
        // 已付款实际比例
        AtomicReference<BigDecimal> paymentRatioCache = new AtomicReference<>(BigDecimal.ZERO);
        purchasePaymentPlanList.forEach(s->{
            BigDecimal receivableAmount = Objects.isNull(s.getReceivableAmount()) ? BigDecimal.ZERO : Objects.isNull(s.getReceivedAmount().getAmount()) ? BigDecimal.ZERO : s.getReceivedAmount().getAmount();
            BigDecimal receivedAmount = Objects.isNull(s.getReceivedAmount()) ? BigDecimal.ZERO : Objects.isNull(s.getReceivedAmount().getAmount()) ? BigDecimal.ZERO : s.getReceivedAmount().getAmount();
            BigDecimal realPaymentRatio = Objects.isNull(s.getRealPaymentRatio()) ? BigDecimal.ZERO : s.getRealPaymentRatio();
            // 存在部分付款情况走之前逻辑
            if (receivedAmount.compareTo(receivableAmount) != 0 && receivedAmount.compareTo(BigDecimal.ZERO) != 0){
                paymentFlag.set(true);
            }
            unPaidRatioCache.set(NumUtil.add(unPaidRatioCache.get(),s.getPaymentRatio()));
            if (s.getStep().equals(purchasePaymentPlan.getStep())){
                stepRatioCache.set(NumUtil.div(s.getPaymentRatio(),unPaidRatioCache.get()));
            }
            paymentRatioCache.set(NumUtil.add(paymentRatioCache.get(),realPaymentRatio));
        });
        purchasePaymentPlanList.forEach(s -> {
            if (!s.getContractCode().equals(purchasePaymentPlan.getContractCode())) {
                return;
            }
            if (s.getStep() < purchasePaymentPlan.getStep()) {
                JsonAmount receivableAmount = s.getReceivableAmount();
                JsonAmount appliedAmount = s.getAppliedAmount();
                BigDecimal appliedAmountValue = Objects.isNull(appliedAmount) ? BigDecimal.ZERO : appliedAmount.getAmount();
                notAppliedAmountCache.set(NumUtil.sub(receivableAmount.getAmount(), appliedAmountValue));
            }
        });
        List<String> skuCodeList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuNameDTO> skuNameCache = skuApi.getSkuNameCacheByCodeList(skuCodeList);
        purchaseContractItemDOList.forEach(s -> {
            if (!s.getPurchaseContractCode().equals(purchasePaymentPlan.getContractCode())) {
                return;
            }
            // 采购明细含税总价
            JsonAmount jsonWithTaxPrice = s.getWithTaxPrice();
            // 数量
            Integer quantity = s.getQuantity();
            if (Objects.isNull(quantity)) {
                throw exception(PURCHASE_CONTRACT_ITEM_QUANTITY_EMPTY, s.getId());
            }
            if (Objects.isNull(jsonWithTaxPrice)) {
                throw exception(PURCHASE_CONTRACT_ITEM_WITH_TAX_PRICE_EMPTY, s.getId());
            }
            BigDecimal withTaxPrice = jsonWithTaxPrice.getAmount();
            if (Objects.isNull(withTaxPrice)) {
                throw exception(PURCHASE_CONTRACT_ITEM_WITH_TAX_PRICE_EMPTY, s.getId());
            }
            BigDecimal totalAmount = NumUtil.mul(s.getWithTaxPrice().getAmount(), s.getQuantity());
            BigDecimal ratio;
            BigDecimal paidAmount = Objects.isNull(s.getPaymentAmount()) ? BigDecimal.ZERO : s.getPaymentAmount();
            
            if (paymentFlag.get()){
                ratio = ratioMap.get(s.getPurchaseContractCode());
                // 对于有部分付款的情况，直接使用比例计算，不减去已付金额
                // 因为已付金额已经在付款计划级别被考虑了
            }else {
                ratio = NumUtil.div(NumUtil.div(purchasePaymentPlan.getPaymentRatio(),CalculationDict.ONE_HUNDRED), stepRatioCache.get());
                // 计算该明细应付总额时，需要减去已付款金额
                // 这样可以确保合同变更后，剩余明细的付款金额是正确的
                totalAmount = NumUtil.sub(totalAmount, paidAmount);
                // 如果剩余金额为负数或零，说明该明细已完全付款，本次申请金额为0
                if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    totalAmount = BigDecimal.ZERO;
                }
            }

            BigDecimal applyAmount = NumUtil.mul(ratio,totalAmount);
            ApplyerPurchaseContractItem applyerPurchaseContractItem = PaymentApplyConvert.INSTANCE.convertApplyerPurchaseContractItem(s);
            if (CollUtil.isNotEmpty(skuNameCache)) {
                SkuNameDTO skuNameDTO = skuNameCache.get(s.getSkuCode());
                if (Objects.nonNull(skuNameDTO)){
                    applyerPurchaseContractItem.setSkuName(skuNameDTO.getName());
                }

            }
            applyerPurchaseContractItem.setApplyAmount(applyAmount);
            if (Objects.nonNull(s.getSaleContractItemId())){
                Boolean exportFlag = saleTypeMap.get(s.getSaleContractItemId());
                if (!exportFlag){
                    applyerPurchaseContractItem.setEstDepartureTime(outStockTimeMap.get(s.getSaleItemUniqueCode()));
                }else {
                    applyerPurchaseContractItem.setEstDepartureTime(estDepartureTimeMap.get(s.getSaleContractItemId()));
                }
            }
            applyerPurchaseContractItem.setInvoicedAmount(BigDecimal.ZERO);
            applyerPurchaseContractItem.setInvoicedQuantity(CalculationDict.ZERO);
            applyerPurchaseContractItem.setInvoicedCurrency(CommonDict.EMPTY_STR);
            applyerPurchaseContractItem.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
            itemList.add(applyerPurchaseContractItem);
        });
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyRespVO.getApplyerPurchaseItemList();
        if (CollUtil.isEmpty(applyerPurchaseItemList)) {
            paymentApplyRespVO.setApplyerPurchaseItemList(itemList);
        } else {
            applyerPurchaseItemList.addAll(itemList);
            paymentApplyRespVO.setApplyerPurchaseItemList(applyerPurchaseItemList);
        }
    }

    /**
     * 计算付款计划信息
     *
     * @param purchasePaymentPlans     选中的付款计划列表
     * @param paymentApplyRespVO       付款申请响应体
     * @param purchaseContractCodeList 采购合同编号列表
     * @return 重新计算的比例缓存
     */
    private Map<String, BigDecimal> dealApplyPaymentPlan(List<PurchasePaymentPlan> purchasePaymentPlans, PaymentApplyRespVO paymentApplyRespVO, List<String> purchaseContractCodeList, List<PurchasePaymentPlan> purchasePaymentPlanList) {
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return Map.of();
        }
        // 获取采购合同采购总金额
        List<PurchaseContractDO> purchaseContractTotalAmountList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getTotalAmount).in(PurchaseContractDO::getCode, purchaseContractCodeList));
        if (CollUtil.isEmpty(purchaseContractTotalAmountList)) {
            throw exception(PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY);
        }
        Map<String, BigDecimal> contractTotalAmountMap = purchaseContractTotalAmountList.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, s -> {
            JsonAmount jsonTotalAmount = s.getTotalAmount();
            if (Objects.isNull(jsonTotalAmount)) {
                throw exception(PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY, s.getCode());
            }
            BigDecimal totalAmount = jsonTotalAmount.getAmount();
            if (Objects.isNull(totalAmount)) {
                throw exception(PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY, s.getCode());
            }
            return totalAmount;
        }));
        Map<String, BigDecimal> ratioMap = new HashMap<>();
        Map<String, List<PurchasePaymentPlan>> paymnetPlanMap = purchasePaymentPlanList.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        for (PurchasePaymentPlan s : purchasePaymentPlans) {
            String contractCode = s.getContractCode();
            ratioMap.put(s.getContractCode(), s.getPaymentRatio());
            // 当前计划步骤之前的所有应付总和
            AtomicReference<BigDecimal> allReceivableAmount = new AtomicReference<>(s.getReceivableAmount().getAmount());
            List<PurchasePaymentPlan> contractPaymentPlan = paymnetPlanMap.get(contractCode);
            if (CollUtil.isEmpty(contractPaymentPlan)) {
                throw exception(PURCHASE_PAYMENT_PLAN_EMPTY, contractCode);
            }
            AtomicReference<BigDecimal> unpaidAmountAto = new AtomicReference<>(BigDecimal.ZERO);
            // 将付款步骤排序
            contractPaymentPlan.sort(Comparator.comparing(PurchasePaymentPlan::getStep));
            contractPaymentPlan.forEach(paymentPlan -> {
                if (paymentPlan.getStep() >= s.getStep()) {
                    return;
                }
                JsonAmount stepReceivableAmountJson = paymentPlan.getReceivableAmount();
                JsonAmount stepReceivedAmountJson = Objects.isNull(paymentPlan.getReceivedAmount()) ? new JsonAmount() : paymentPlan.getReceivedAmount();
                JsonAmount stepAppliedAmountJson = Objects.isNull(paymentPlan.getAppliedAmount()) ? new JsonAmount() : paymentPlan.getAppliedAmount();
                BigDecimal stepReceivableAmount = Objects.isNull(stepReceivableAmountJson.getAmount()) ? BigDecimal.ZERO : stepReceivableAmountJson.getAmount();
                BigDecimal stepReceivedAmount = Objects.isNull(stepReceivedAmountJson.getAmount()) ? BigDecimal.ZERO : stepReceivedAmountJson.getAmount();
                BigDecimal stepAppliedAmount = Objects.isNull(stepAppliedAmountJson.getAmount()) ? BigDecimal.ZERO : stepAppliedAmountJson.getAmount();
                BigDecimal unpaidAmount = NumUtil.add(unpaidAmountAto.get(), NumUtil.sub(stepReceivableAmount, NumUtil.add(stepReceivedAmount, stepAppliedAmount)));
                if (unpaidAmount.compareTo(BigDecimal.ZERO)>0){
                    unpaidAmountAto.set(unpaidAmount);
                }
                allReceivableAmount.set(NumUtil.add(allReceivableAmount.get(), paymentPlan.getReceivableAmount().getAmount()));
                JsonAmount jsonReceivableAmount = paymentPlan.getReceivableAmount();
                if (Objects.isNull(jsonReceivableAmount)) {
                    throw exception(PAYMENT_PLAN_RECEIVABLE_AMOUNT_EMPTY, paymentPlan.getId());
                }
                if (Objects.isNull(jsonReceivableAmount.getAmount())) {
                    throw exception(PAYMENT_PLAN_RECEIVABLE_AMOUNT_EMPTY, paymentPlan.getId());
                }
            });
            BigDecimal totalAmount = contractTotalAmountMap.get(contractCode);
            if (Objects.isNull(totalAmount)) {
                throw exception(PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY, contractCode);
            }
            JsonAmount jsonReceivableAmount = BeanUtil.copyProperties(s.getReceivableAmount(), JsonAmount.class);
            if (Objects.nonNull(jsonReceivableAmount)) {
                BigDecimal receivableAmount = jsonReceivableAmount.getAmount();
                jsonReceivableAmount.setAmount(NumUtil.add(receivableAmount, unpaidAmountAto.get()));
                s.setApplyAmount(jsonReceivableAmount.getAmount());
                if (unpaidAmountAto.get().compareTo(BigDecimal.ZERO)>0){
                    BigDecimal ratio = NumUtil.div(jsonReceivableAmount.getAmount(), totalAmount);
//                    s.setPaymentRatio(NumUtil.mul(ratio, CalculationDict.ONE_HUNDRED));
                    ratioMap.put(contractCode, ratio);
                }else {
                    ratioMap.put(contractCode, NumUtil.div(s.getPaymentRatio(),CalculationDict.ONE_HUNDRED));
                }
            }
        }
        List<ApplyPaymentPlan> applyPaymentPlans = PaymentApplyConvert.INSTANCE.convertApplyPaymentPlanList(purchasePaymentPlans);
        paymentApplyRespVO.setApplyPaymentPlanList(applyPaymentPlans);
        paymentApplyRespVO.setPurchaseContractCodeList(purchaseContractCodeList);
        return ratioMap;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public PaymentApplyRespVO getPaymentApply(PaymentApplyDetailReq paymentApplyDetailReq) {
        Long paymentApplyId = Objects.nonNull(paymentApplyDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(paymentApplyDetailReq.getProcessInstanceId()) : paymentApplyDetailReq.getPaymentApplyId();
        if (Objects.isNull(paymentApplyId)) {
            logger.error("[付款申请主]未获取到付款申请主id");
            return null;
        }
        PaymentApplyDO paymentApplyDO = paymentApplyMapper.selectById(paymentApplyId);
        if (paymentApplyDO == null) {
            return null;
        }
        List<ApplyerPurchaseContractItem> applyerPurchaseItemList = paymentApplyDO.getApplyerPurchaseItemList();
        if (CollUtil.isNotEmpty(applyerPurchaseItemList)) {
            List<String> skuCodeList = applyerPurchaseItemList.stream().map(ApplyerPurchaseContractItem::getSkuCode).distinct().toList();
            Map<String, SkuNameDTO> skuNameCache = skuApi.getSkuNameCacheByCodeList(skuCodeList);
            List<Long> purchaseContractItemIdList = applyerPurchaseItemList.stream().map(ApplyerPurchaseContractItem::getId).distinct().toList();
            List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getWarehousingType, PurchaseContractItemDO::getId,PurchaseContractItemDO::getAppliedAmount,PurchaseContractItemDO::getPaymentAmount).in(PurchaseContractItemDO::getId, purchaseContractItemIdList));
            Map<Long, PurchaseContractItemDO> purchaseContractItemDOMap = purchaseContractItemDOList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getId, s->s, (k1, k2) -> k1));
            applyerPurchaseItemList.forEach(s -> {
                if (CollUtil.isNotEmpty(skuNameCache)) {
                    SkuNameDTO skuNameDTO = skuNameCache.get(s.getSkuCode());
                    s.setSkuName(skuNameDTO.getName());
                }
                if (CollUtil.isEmpty(purchaseContractItemDOMap)){
                    return;
                }
                PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemDOMap.get(s.getId());
                if (Objects.isNull(purchaseContractItemDO)){
                    return;
                }
                s.setWarehousingType(purchaseContractItemDO.getWarehousingType());
                s.setAppliedAmount(purchaseContractItemDO.getAppliedAmount());
                s.setPaymentAmount(purchaseContractItemDO.getPaymentAmount());
            });

        }
        PaymentApplyRespVO paymentApplyRespVO = PaymentApplyConvert.INSTANCE.convertPaymentApplyRespVO(paymentApplyDO);
        List<String> purchaseContractCodeList = paymentApplyDO.getPurchaseContractCodeList();
        if (CollUtil.isNotEmpty(purchaseContractCodeList)) {
            List<PurchaseRegistrationItem> purchaseRegistrationItemList = registrationItemMapper.selectList(new LambdaQueryWrapperX<PurchaseRegistrationItem>().in(PurchaseRegistrationItem::getPurchaseContractCode, purchaseContractCodeList));
            paymentApplyRespVO.setRegistrationItemList(purchaseRegistrationItemList);
        }
        return paymentApplyRespVO;
    }

    @Override
    public PageResult<PaymentApplyDO> getPaymentApplyPage(PaymentApplyPageReqVO pageReqVO) {
        return paymentApplyMapper.selectPage(pageReqVO);
    }

    /**
     * 回写应付供应商逻辑
     *
     * @param venderCode  供应商编号
     * @param bank        银行
     * @param bankAccount 银行账号
     */
    private void dealPaymentVender(String venderCode, String bank, String bankAccount) {
        Long count = venderMapper.checkVenderBank(bank, venderCode, bankAccount);
        if (count > CalculationDict.ZERO) {
            return;
        }
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        venderMapper.insertVenderBank(bank, venderDO.getId(), bankAccount);
    }

    /**
     * 回写采购合同加减项
     *
     * @param purchaseAddSubTermList 采购合同加减项列表
     */
    private void dealAddSubTerm(List<PurchaseAddSubTerm> purchaseAddSubTermList) {
        if (CollUtil.isEmpty(purchaseAddSubTermList)) {
            return;
        }
        purchaseAddSubTermMapper.insertBatch(purchaseAddSubTermList);
    }

    /**
     * 回写采购明细
     *
     * @param applyerPurchaseItemList 申请明细列表
     * @param updateApplyAmount       是否更新已申请金额
     * @param updateAppliedAmount     是否更新已支付金额
     * @param isDelete                是否删除
     */
    private void dealPurchaseContractItem(List<ApplyerPurchaseContractItem> applyerPurchaseItemList, boolean updateApplyAmount, boolean updateAppliedAmount, boolean isDelete) {
        if (CollUtil.isEmpty(applyerPurchaseItemList)) {
            return;
        }
        // 申请明细缓存
        Map<Long, ApplyerPurchaseContractItem> applyerItemMap = applyerPurchaseItemList.stream().collect(Collectors.toMap(ApplyerPurchaseContractItem::getId, Function.identity(), (k1, k2) -> k1));
        List<Long> contractItemIdList = applyerPurchaseItemList.stream().map(ApplyerPurchaseContractItem::getId).distinct().toList();
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getId, contractItemIdList));
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        purchaseContractItemDOList.forEach(s -> {
            ApplyerPurchaseContractItem applyerPurchaseContractItem = applyerItemMap.get(s.getId());
            // 本次申请金额
            BigDecimal realApplyAmount = applyerPurchaseContractItem.getRealApplyAmount();
            if (updateApplyAmount) {
                // 已申请金额 = 已申请金额 + 本次申请金额
                if (isDelete) {
                    s.setAppliedAmount(NumUtil.sub(s.getAppliedAmount(), realApplyAmount));
                    s.setPaymentAmount(NumUtil.sub(s.getPaymentAmount(), realApplyAmount));
                } else {
                    s.setAppliedAmount(NumUtil.add(s.getAppliedAmount(), realApplyAmount));
                }
            }else {
               if (updateAppliedAmount) {
                // 已付金额 = 已付金额 + 本次申请金额
                s.setPaymentAmount(NumUtil.add(s.getPaymentAmount(), realApplyAmount));
            }
            }
        });
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOList);
    }


}