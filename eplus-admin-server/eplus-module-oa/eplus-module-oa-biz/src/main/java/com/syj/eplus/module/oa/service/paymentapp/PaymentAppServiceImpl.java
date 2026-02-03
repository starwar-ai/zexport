package com.syj.eplus.module.oa.service.paymentapp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.definition.BpmDefinitionApi;
import cn.iocoder.yudao.module.bpm.api.definition.dto.BpmNodeUser;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;

import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.AmountToChineseUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.dms.enums.api.ForwarderFeeApi;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.DmsForwarderFeeDTO;
import com.syj.eplus.module.dms.enums.api.dto.ForwarderFeeDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.ems.api.send.EmsSendApi;
import com.syj.eplus.module.ems.api.send.dto.EmsSendDetailDTO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.*;
import com.syj.eplus.module.oa.converter.PaymentAppConvert;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import com.syj.eplus.module.oa.dal.mysql.paymentapp.PaymentAppMapper;
import com.syj.eplus.module.oa.dal.mysql.subject.SubjectMapper;
import com.syj.eplus.module.oa.entity.ForwarderFeeExportVO;
import com.syj.eplus.module.oa.enums.PrintFlagEnum;
import com.syj.eplus.module.oa.service.dictsubject.DictSubjectService;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 公对公申请 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class PaymentAppServiceImpl implements PaymentAppService {

    @Resource
    private PaymentAppMapper paymentAppMapper;
    @Resource
    private DictSubjectService dictSubjectService;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private BpmDefinitionApi bpmDefinitionApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private CompanyApi companyApi;
    @Resource
    protected ShipmentApi shipmentApi;
    @Resource
    private CustApi custApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private EmsSendApi emsSendApi;
    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    private LoanAppService loanAppService;

    @Resource
    private ReportApi reportApi;

    @Resource
    private ForwarderFeeApi forwarderFeeApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private SubjectMapper subjectMapper;
    @Resource
    private AdminUserApi adminUserApi;
    private static final String CODE_PREFIX = "PA";
    private static final String SN_TYPE = "SN_PAYMENTAPP";

    private static final String PROCESS_DEFINITION_KEY = "oa_payment_app";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createPaymentApp(PaymentAppSaveReqVO createReqVO) throws Exception {
        List<CreatedResponse> responses = new ArrayList<>();
        // 校验主体
        List<String> paymentCodeList = createReqVO.getPaymentAppList();
        Long companyId = createReqVO.getCompanyId();
        if (!PaymentAppTypeEnum.PRE_PAID.getType().equals(createReqVO.getPrepaidFlag()) && CollUtil.isNotEmpty(paymentCodeList)) {
            List<SimplePaymentAppResp> paymentAppList = getPaymentAppList(paymentCodeList, true);
            if (CollUtil.isNotEmpty(paymentAppList)) {
                boolean checkResult = paymentAppList.stream().anyMatch(paymentApp -> !companyId.equals(paymentApp.getCompanyId()));
                if (checkResult) {
                    throw exception(COMPANY_NOT_MATCH);
                }
            }
        }
        // 插入
        PaymentAppDO paymentApp = BeanUtils.toBean(createReqVO, PaymentAppDO.class);
        paymentApp.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        paymentApp.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        paymentApp.setPrintFlag(PrintFlagEnum.NO.getValue());
        paymentApp.setPrintTimes(0);
        paymentApp.setPaymentStatus(BooleanEnum.NO.getValue());
        paymentApp.setApplyer(adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId()));
        paymentApp.setLinkFlag(BooleanEnum.NO.getValue());
        if (Objects.isNull(createReqVO.getFeeShareFlag())) {
            paymentApp.setFeeShareFlag(BooleanEnum.YES.getValue());
        }
        paymentAppMapper.insert(paymentApp);
        // 处理预付申请单
        List<String> paymentAppList = createReqVO.getPaymentAppList();
        if (CollUtil.isNotEmpty(paymentAppList)) {
            paymentAppMapper.update(new PaymentAppDO().setLinkFlag(BooleanEnum.YES.getValue()), new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getCode, paymentAppList));
        }

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())
                && Objects.equals(paymentApp.getFeeShareFlag(), BooleanEnum.YES.getValue())
                && Objects.equals(createReqVO.getAuxiliaryType(), BooleanEnum.YES.getValue())) {
            List<FeeShareReqDTO> feeShareList = createReqVO.getFeeShare();
            dealFeeShare(feeShareList, paymentApp);
        }

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(paymentApp.getId(), WebFrameworkUtils.getLoginUserId());
        }
        //判断类型为寄件，反写数据
        if (Objects.equals(paymentApp.getRelationType(), OaPaymentAppRealtionTypeEnum.EMS_SEND.getValue())) {
            emsSendApi.updateSendStatusByCodeList(
                    new EmsSendUpdateDTO().setCodeList(paymentApp.getRelationCode())
                            .setStatus(EmsSendStatusEnum.COMPLETED.getCode()));
        }
        //判断类型为船代，反写数据
        if (Objects.equals(paymentApp.getRelationType(), OaPaymentAppRealtionTypeEnum.DMS_FORWARDER.getValue())) {
            forwarderFeeApi.updateStatusByCodeList(
                    new DmsForwarderFeeDTO().setCodeList(paymentApp.getRelationCode())
                            .setPaymentAppId(paymentApp.getId())
                            .setPaymentAppCode(paymentApp.getCode())
                            .setStatus(PayStatusEnum.APPLY.getValue()));
        }
        //判断类型为包材，反写数据
        if (Objects.equals(paymentApp.getRelationType(), OaPaymentAppRealtionTypeEnum.AUXILIARY_PURCHASE.getValue())) {
            purchaseContractApi.updateAuxiliaryPaymentStatusByCodes(paymentApp.getRelationCode(), BooleanEnum.YES.getValue());
        }
        // 返回
        responses.add(new CreatedResponse(paymentApp.getId(), paymentApp.getCode()));
        return responses;
    }

    private void dealFeeShare(List<FeeShareReqDTO> feeShareList, PaymentAppDO paymentApp) {
        // 人名币金额
//        JsonAmount feeShareAmount = feeShareApi.getFeeShareAmount(List.of(paymentApp.getAmount()));
        if (CollUtil.isNotEmpty(feeShareList)) {
            for (FeeShareReqDTO feeShare : feeShareList) {
                if (Objects.nonNull(feeShare)) {
                    feeShare.setBusinessType(FeeShareSourceTypeEnum.PAYMENT_APP.getValue());
                    feeShare.setBusinessCode(paymentApp.getCode());
                    feeShare.setCompanyId(paymentApp.getCompanyId());
                    feeShare.setInputUser(paymentApp.getApplyer());
                    feeShare.setStatus(BooleanEnum.YES.getValue());
                    feeShare.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                    feeShare.setBusinessId(paymentApp.getId());
//                    feeShare.setAmount(feeShareAmount);
                    Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
                    if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(paymentApp.getCompanyId());
                        if (Objects.isNull(simpleCompanyDTO)) {
                            throw exception(COMPANY_NOT_EXISTS);
                        }
                        feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
                    }
                }
            }
            paymentAppMapper.updateById(new PaymentAppDO().setId(paymentApp.getId()).setAuxiliaryType(BooleanEnum.YES.getValue()));
            feeShareApi.updateFeeShareByDTOList(feeShareList);
        } else {
            throw exception(REIMBER_SHARE_NOT_NULL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentApp(PaymentAppSaveReqVO updateReqVO) {
        // 校验存在
        PaymentAppDO paymentAppDO = validatePaymentAppExists(updateReqVO.getId());
        List<String> paymentAppList = CollUtil.isEmpty(paymentAppDO.getPaymentAppList())?new ArrayList<>():paymentAppDO.getPaymentAppList();
        List<String> newPaymentAppList = CollUtil.isEmpty(updateReqVO.getPaymentAppList())?new ArrayList<>():updateReqVO.getPaymentAppList();
        if (CollUtil.isNotEmpty(paymentAppList)){
            List<String> subtractToList = CollUtil.subtractToList(paymentAppList, newPaymentAppList);
            if (CollUtil.isNotEmpty(subtractToList)){
                paymentAppMapper.update(new PaymentAppDO().setLinkFlag(BooleanEnum.NO.getValue()), new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getCode, subtractToList));
            }
        }
        // 更新
        PaymentAppDO updateObj = BeanUtils.toBean(updateReqVO, PaymentAppDO.class);
        //处理费用归属
        List<FeeShareReqDTO> feeShareList = updateReqVO.getFeeShare();
        int result = paymentAppMapper.updateById(updateObj);
        if (result > 0 && SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            if (BooleanEnum.YES.getValue().equals(updateReqVO.getAuxiliaryType())) {
                dealFeeShare(feeShareList, updateObj);
            }
            submitTask(updateReqVO.getId(), WebFrameworkUtils.getLoginUserId());
        }
    }

    @Override
    public void deletePaymentApp(Long id) throws Exception {
        // 校验存在
        PaymentAppDO paymentAppDO = validatePaymentAppExists(id);
        List<String> paymentAppList = paymentAppDO.getPaymentAppList();
        if (CollUtil.isNotEmpty(paymentAppList)) {
            paymentAppMapper.update(new PaymentAppDO().setPaymentStatus(PaymentStatusEnum.UNPAID.getValue()), new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getCode, paymentAppList));
        }

        // 删除
        paymentAppMapper.deleteById(id);

        //删除处理船代公司回写状态
        if (Objects.equals(paymentAppDO.getRelationType(), OaPaymentAppRealtionTypeEnum.DMS_FORWARDER.getValue())) {
            forwarderFeeApi.updateStatusByCodeList(
                    new DmsForwarderFeeDTO().setCodeList(paymentAppDO.getRelationCode())
                            .setStatus(PayStatusEnum.NOT_APPLY.getValue()));
        }

        //删除快递公司回写状态
        if (Objects.equals(paymentAppDO.getRelationType(), OaPaymentAppRealtionTypeEnum.EMS_SEND.getValue())) {
            emsSendApi.updateSendStatusByCodeList(
                    new EmsSendUpdateDTO().setCodeList(paymentAppDO.getRelationCode())
                            .setStatus(EmsSendStatusEnum.PENGDING_PAY.getCode()));
        }

        //删除包材合同回写状态
        if (Objects.equals(paymentAppDO.getRelationType(), OaPaymentAppRealtionTypeEnum.AUXILIARY_PURCHASE.getValue())) {
            purchaseContractApi.updateAuxiliaryPaymentStatusByCodes(paymentAppDO.getRelationCode(), BooleanEnum.NO.getValue());
        }
        // 重置冲账标识
        if (CollUtil.isNotEmpty(paymentAppDO.getPaymentAppList())){
            paymentAppMapper.update(new PaymentAppDO().setLinkFlag(BooleanEnum.NO.getValue()), new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getCode, paymentAppDO.getPaymentAppList()));
        }

        //删除费用归集数据
        feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.PAYMENT_APP,List.of(paymentAppDO.getCode()));
    }

    private PaymentAppDO validatePaymentAppExists(Long id) {
        PaymentAppDO paymentAppDO = paymentAppMapper.selectById(id);
        if (paymentAppDO == null) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        return paymentAppDO;
    }

    @Override
    public PaymentAppRespVO getPaymentApp(PaymentAppDetailReq req) {
        Long paymentId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getPaymentAppId();
        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        Map<String, String> custNameCache = custApi.getCustNameCache(null);
        Map<String, String> venderNameCache = venderApi.getVenderNameCache(null);
        PaymentAppDO paymentAppDO = paymentAppMapper.selectById(paymentId);
        if (paymentAppDO == null) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        List<SubjectDO> subjectDOList = subjectMapper.selectList();
        Map<Long, DictSubjectDO> dictSubjectMap = dictSubjectService.getDictSubjectMap();
        Map<Long, String> subjectMap = subjectDOList.stream().collect(Collectors.toMap(SubjectDO::getId, SubjectDO::getName));
        PaymentAppRespVO paymentAppRespVO = PaymentAppConvert.Instance.convert(paymentAppDO, simpleCompanyDTO, subjectMap, dictSubjectMap,adminUserApi.getUserDeptCache(null));
        // 预付申请单列表
        List<String> paymentAppList = paymentAppDO.getPaymentAppList();
        List<SimplePaymentAppResp> simplePaymentAppRespList = getPaymentAppList(paymentAppList, true);
        List<SimplePaymentAppResp> simplePaymentLinkList = getPaymentAppList(paymentAppList, false);
        paymentAppRespVO.setPaymentAppLinkList(simplePaymentLinkList);
        paymentAppRespVO.setPaymentAppDTOList(simplePaymentAppRespList);
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            paymentAppRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        List<FeeShareRespDTO> feeShareDTO = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.PAYMENT_APP, paymentAppDO.getCode());
        paymentAppRespVO.setFeeShare(feeShareDTO);
        return paymentAppRespVO;
    }


    @Override
    public PageResult<PaymentAppRespVO> getPaymentAppPage(PaymentAppPageReqVO pageReqVO) {
        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        PageResult<PaymentAppDO> paymentAppDOPageResult = paymentAppMapper.selectPage(pageReqVO);
        Map<String, String> custNameCache = custApi.getCustNameCache(null);
        Map<String, String> venderNameCache = venderApi.getVenderNameCache(null);
        List<SubjectDO> subjectDOList = subjectMapper.selectList();
        Map<Long, DictSubjectDO> dictSubjectMap = dictSubjectService.getDictSubjectMap();
        Map<Long, String> subjectMap = subjectDOList.stream().collect(Collectors.toMap(SubjectDO::getId, SubjectDO::getName));
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        PageResult<PaymentAppRespVO> paymentAppRespVOPageResult = PaymentAppConvert.Instance.convertPageResultVO(paymentAppDOPageResult, simpleCompanyDTO, subjectMap, dictSubjectMap,userDeptCache);
        List<PaymentAppRespVO> list = paymentAppRespVOPageResult == null ? null : paymentAppRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            Set<String> paymentCodeSet = list.stream().map(PaymentAppRespVO::getPaymentAppList).filter(CollUtil::isNotEmpty).flatMap(List::stream).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(paymentCodeSet)){
                List<PaymentAppDO> paymentAppDOS = paymentAppMapper.selectList(PaymentAppDO::getCode, paymentCodeSet);
                Map<String, PaymentAppDO> paymentMap = paymentAppDOS.stream().collect(Collectors.toMap(PaymentAppDO::getCode, s -> s, (o, n) -> n));
                list.forEach(s -> {
                    List<String> paymentAppList = s.getPaymentAppList();
                    if (CollUtil.isEmpty(paymentAppList)||CollUtil.isEmpty(paymentMap)){
                        return;
                    }
                    List<PaymentAppDO> paymentAppDTOList = paymentAppList.stream().map(paymentMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                    s.setPaymentAppDTOList(transformSimplePaymentAppRespList(paymentAppDTOList));
                });
            }
            List<String> codeList = list.stream().map(PaymentAppRespVO::getRelationCode).filter(Objects::nonNull).flatMap(List::stream).distinct().toList();
            List<String> code1List = list.stream().map(PaymentAppRespVO::getCode).distinct().toList();
            codeList = new ArrayList<>(codeList);
            if (CollUtil.isNotEmpty(code1List)) {
                codeList.addAll(code1List);
            }
            Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(0, codeList);
            if (CollUtil.isNotEmpty(feeShareMap)) {
                list.forEach(s -> {
                    if (s.getFeeShare() == null) {
                        s.setFeeShare(new ArrayList<>());
                    }
                    if (CollUtil.isNotEmpty(s.getRelationCode())) {
                        s.getRelationCode().forEach(f -> {
                            List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(f);
                            if (CollUtil.isNotEmpty(feeShareRespDTO)) {
                                s.getFeeShare().addAll(feeShareRespDTO);
                            }
                        });
                    }
                    List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                    if (CollUtil.isNotEmpty(feeShareRespDTO)) {
                        s.getFeeShare().addAll(feeShareRespDTO);
                    }
                });
            }
            
            // 计算支付金额和发票金额的合计（参考出运明细实现）
            Map<String, Object> summary = new HashMap<>();
            
            // 按币种分组统计支付金额
            Map<String, List<PaymentAppRespVO>> amountGrouped = list.stream()
                    .filter(item -> item.getAmount() != null && item.getAmount().getCurrency() != null)
                    .collect(Collectors.groupingBy(item -> item.getAmount().getCurrency()));
            if (CollUtil.isNotEmpty(amountGrouped)) {
                List<JsonAmount> amountSumList = amountGrouped.entrySet().stream().map(entry -> {
                    BigDecimal sum = entry.getValue().stream()
                            .map(item -> item.getAmount().getAmount())
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new JsonAmount().setAmount(sum).setCurrency(entry.getKey());
                }).collect(Collectors.toList());
                summary.put("amountSum", amountSumList);
            }
            
            // 按币种分组统计发票金额
            Map<String, List<PaymentAppRespVO>> invoiceAmountGrouped = list.stream()
                    .filter(item -> item.getInvoiceAmount() != null && item.getInvoiceAmount().getCurrency() != null)
                    .collect(Collectors.groupingBy(item -> item.getInvoiceAmount().getCurrency()));
            if (CollUtil.isNotEmpty(invoiceAmountGrouped)) {
                List<JsonAmount> invoiceAmountSumList = invoiceAmountGrouped.entrySet().stream().map(entry -> {
                    BigDecimal sum = entry.getValue().stream()
                            .map(item -> item.getInvoiceAmount().getAmount())
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new JsonAmount().setAmount(sum).setCurrency(entry.getKey());
                }).collect(Collectors.toList());
                summary.put("invoiceAmountSum", invoiceAmountSumList);
            }
            
            paymentAppRespVOPageResult.setSummary(summary);
        }
        
        return paymentAppRespVOPageResult == null ? null : paymentAppRespVOPageResult.setList(list);
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long paymentAppId, Long userId) {
        Map<String,Object> variable = new HashMap<>();
        UserDept userDept = adminUserApi.getUserDeptByUserId(userId);
        variable.put("deptCode", userDept.getDeptCode());
        PaymentAppDO paymentAppDO = validatePaymentAppExists(paymentAppId);
        JsonAmount amount = paymentAppDO.getAmount();
        if (Objects.nonNull(amount)){
            variable.put("amount", amount.getAmount());
        }
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, paymentAppId,variable,Map.of());
        updateAuditStatus(paymentAppId, BpmProcessInstanceResultEnum.PROCESS.getResult());
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
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        paymentAppMapper.updateById(PaymentAppDO.builder().id(auditableId).auditStatus(auditStatus).build());
        //审批通过费用归集status写1
        feeShareApi.updateSourceStatus (FeeShareSourceTypeEnum.PAYMENT_APP.getValue(),auditableId, auditStatus);
    }

    @Override
    public void updatePaymentApp(PaymentAppDTO paymentAppDTO) {
        paymentAppMapper.update(PaymentAppDO.builder().code(paymentAppDTO.getCode())
                .paymentAmount(paymentAppDTO.getPaymentAmount())
                .paymentStatus(paymentAppDTO.getPaymentStatus())
                .paymentDate(paymentAppDTO.getPaymentDate())
                .cashier(paymentAppDTO.getCashier()).build(), new LambdaQueryWrapperX<PaymentAppDO>().eq(PaymentAppDO::getCode, paymentAppDTO.getCode()));
    }

    @Override
    public void batchUpdatePaymentApp(List<PaymentAppDTO> paymentAppDTOList) {
        if (CollUtil.isEmpty(paymentAppDTOList)) {
            return;
        }
        List<String> codeList = paymentAppDTOList.stream().map(PaymentAppDTO::getCode).distinct().toList();
        if (CollUtil.isEmpty(codeList)) {
            return;
        }
        Map<String, PaymentAppDTO> paymentAppDTOMap = paymentAppDTOList.stream().collect(Collectors.toMap(PaymentAppDTO::getCode, s -> s, (o, n) -> o));
        List<PaymentAppDO> paymentAppDOList = paymentAppMapper.selectList(new LambdaQueryWrapperX<PaymentAppDO>().select(PaymentAppDO::getId, PaymentAppDO::getCode).in(PaymentAppDO::getCode, codeList));
        if (CollUtil.isEmpty(paymentAppDOList)) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        paymentAppDOList.forEach(s -> {
            PaymentAppDTO paymentAppDTO = paymentAppDTOMap.get(s.getCode());
            if (Objects.isNull(paymentAppDTO)){
                return;
            }
            s.setPaymentAmount(paymentAppDTO.getPaymentAmount());
            s.setPaymentStatus(paymentAppDTO.getPaymentStatus());
            s.setPaymentDate(paymentAppDTO.getPaymentDate());
            s.setCashier(paymentAppDTO.getCashier());
            List<SimpleFile> paymentAnnex = paymentAppDTO.getAnnex();
            if (CollUtil.isNotEmpty(paymentAnnex)){
                List<SimpleFile> paymentAppAnnex = s.getAnnex();
                if (CollUtil.isEmpty(paymentAppAnnex)){
                    s.setAnnex(paymentAnnex);
                }else {
                    paymentAppAnnex.addAll(paymentAnnex);
                    s.setAnnex(paymentAppAnnex);
                }
            }
        });
        paymentAppMapper.updateBatch(paymentAppDOList);
    }

    @Override
    public PaymentAppDO getPaymentAppByCode(String code) {
        return paymentAppMapper.selectOne(PaymentAppDO::getCode, code);
    }

    @Override
    public LoanVO getNotPayLoanList(String venderCode) {
        LoanVO result = new LoanVO();

        List<LoanDescVO> loanList = new ArrayList<>();
        List<LoanSumVO> loanSumList = new ArrayList<>();

        //查询借款单信息
        LoanSumVO sunVO = new LoanSumVO().setName("历史借款还款情况");
        List<LoanDescVO> list = loanAppService.getNotDoneVenderLoanList(venderCode);
        if (CollUtil.isNotEmpty(list)) {
            loanList.addAll(list);
            loanSumList.add(sunVO.setExitFlag(BooleanEnum.YES.getValue()));
        } else {
            loanSumList.add(sunVO.setExitFlag(BooleanEnum.NO.getValue()));
        }

        result.setLoanDescList(loanList);
        result.setLoanSumList(loanSumList);

        return result;

    }

    @Override
    public List<SimplePaymentAppResp> getPaymentAppList(List<String> codeList, boolean singleFlag) {
        List<PaymentAppDO> paymentAppDOList = paymentAppMapper.selectList(PaymentAppDO::getCode, codeList);
        if (singleFlag) {
            return transformSimplePaymentAppRespList(paymentAppDOList);
        }
        List<PaymentAppDO> result = recursionGetPaymentDO(paymentAppDOList, paymentAppDOList);
        return transformSimplePaymentAppRespList(result);
    }

    private List<PaymentAppDO> recursionGetPaymentDO(List<PaymentAppDO> paymentAppDOList, List<PaymentAppDO> result) {
        if (CollUtil.isEmpty(paymentAppDOList)) {
            return result;
        }
        List<String> list = paymentAppDOList.stream().flatMap(s -> s.getPaymentAppList().stream()).distinct().toList();
        if (CollUtil.isEmpty(list)) {
            return result;
        }
        List<PaymentAppDO> paymentAppList = paymentAppMapper.selectList(PaymentAppDO::getCode, list);
        result.addAll(paymentAppList);
        recursionGetPaymentDO(paymentAppList, result);
        return result;
    }

    @Override
    public List<SimplePaymentAppResp> getSimplePaymentAppList(Integer businessSubjectType, String businessSubjectCode, List<String> codeList,Long companyId,Integer prepaidFlag) {
        if (StrUtil.isEmpty(businessSubjectCode) || Objects.isNull(businessSubjectType)) {
            return List.of();
        }
        LambdaQueryWrapperX<PaymentAppDO> queryWrapperX = new LambdaQueryWrapperX<PaymentAppDO>().eq(PaymentAppDO::getBusinessSubjectType, businessSubjectType)
                .eq(PaymentAppDO::getBusinessSubjectCode, businessSubjectCode)
                .eq(PaymentAppDO::getLinkFlag, BooleanEnum.NO.getValue())
                .eq(PaymentAppDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult())
                .eq(PaymentAppDO::getCompanyId, companyId)
                .eqIfPresent(PaymentAppDO::getPrepaidFlag, prepaidFlag);
        if (CollUtil.isNotEmpty(codeList)){
            queryWrapperX.or().in(PaymentAppDO::getCode, codeList);
        }
        List<PaymentAppDO> paymentAppDOList = paymentAppMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(paymentAppDOList)) {
            return List.of();
        }
        return transformSimplePaymentAppRespList(paymentAppDOList);
    }

    /**
     * 转换对公精简列表
     *
     * @param paymentAppDOList 对公列表
     * @return 对公精简列表
     */
    private List<SimplePaymentAppResp> transformSimplePaymentAppRespList(List<PaymentAppDO> paymentAppDOList) {
        if (CollUtil.isEmpty(paymentAppDOList)) {
            return List.of();
        }
        List<String> custCodeList = paymentAppDOList.stream().filter(s -> BusinessSubjectTypeEnum.CUST.getCode().equals(s.getBusinessSubjectType())).map(PaymentAppDO::getBusinessSubjectCode).toList();
        List<String> venderCodeList = paymentAppDOList.stream().filter(s -> BusinessSubjectTypeEnum.VENDER.getCode().equals(s.getBusinessSubjectType())).map(PaymentAppDO::getBusinessSubjectCode).toList();
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        Map<String, CustAllDTO> custByCodeList = custApi.getCustByCodeList(custCodeList);
        Map<String, VenderAllDTO> venderByCodeList = venderApi.getVenderByCodeList(venderCodeList);
        return PaymentAppConvert.Instance.convertSimplePaymentList(paymentAppDOList, simpleCompanyDTOMap, custByCodeList, venderByCodeList);
    }

    @Override
    public String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId) {
        if (Objects.isNull(id)) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        if (Objects.isNull(reportCode)) {
            throw exception(REPORTCODE_NULL);
        }
        if (Objects.nonNull(sourceType) && Objects.isNull(sourceCode)) {
            throw exception(SOURCECODE_NULL);
        }
        if (Objects.isNull(sourceType) && Objects.nonNull(sourceCode)) {
            throw exception(SOURCETYPE_NULL);
        }
        PaymentAppDO paymentAppDO = paymentAppMapper.selectById(id);
        if (Objects.isNull(paymentAppDO)) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }

        ReportDTO reportDTO;
        if (companyId != null) {
            if (sourceType != null) {
                reportDTO = reportApi.getSourceReport(reportCode, sourceCode, sourceType, companyId);
            } else {
                reportDTO = reportApi.getCompanyReport(reportCode, companyId);
            }
        } else {
            reportDTO = reportApi.getReport(reportCode);
        }

        if (reportDTO == null || CollUtil.isEmpty(reportDTO.getAnnex())) {
            throw exception(REPORT_NULL);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        String projectPath = System.getProperty("user.dir");
        HashMap<String, Object> params = new HashMap<>();
        //采购主体
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.nonNull(companyDTO)) {
            params.put("companyName", companyDTO.getCompanyName());
        }

        params.put("code", paymentAppDO.getCode());
        LocalDateTime now = LocalDateTime.now();
        params.put("year", now.getYear());
        params.put("month", now.getMonthValue());
        params.put("day", now.getDayOfMonth());

        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{20, 15, 27, 38}).create();
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();

        String name = "";
        String deptName = "";
        UserDept applyer = paymentAppDO.getApplyer();
        if (Objects.nonNull(applyer)) {
            if (Objects.nonNull(applyer.getNickname())) {
                name = applyer.getNickname();
            }
            if (Objects.nonNull(applyer.getDeptName())) {
                deptName = applyer.getDeptName();
            }

        }

        params.put("appName", name);

        String currency = "";
        if (Objects.nonNull(paymentAppDO.getAmount())) {
            currency = paymentAppDO.getAmount().getCurrency();
        }

        String payName = "";
        String payCode = paymentAppDO.getBusinessSubjectCode();
        if (Objects.equals(paymentAppDO.getBusinessSubjectType(), BusinessSubjectTypeEnum.VENDER.getCode())
                || Objects.equals(paymentAppDO.getBusinessSubjectType(), BusinessSubjectTypeEnum.VENDER_BIZ.getCode())) {
            payName = venderApi.getBankPocByVenderCode(payCode);
        } else if (Objects.equals(paymentAppDO.getBusinessSubjectType(), BusinessSubjectTypeEnum.CUST.getCode())) {
            payName = custApi.getBankPocByCustCode(payCode);
        }
        //支付类型
        String empty = "□";
        String checkedString = "✓";
        Integer prepaidFlag = paymentAppDO.getPrepaidFlag();
        String typePre = "";
        String typePayment = "";
        String typeOff = "";
        if (Objects.nonNull(prepaidFlag)) {
            if(prepaidFlag.equals(PaymentAppTypeEnum.PRE_PAID.getType())){
                typePre = checkedString + " 预付 ";
            }else{
                typePre = empty + " 预付 ";
            }
            if(prepaidFlag.equals(PaymentAppTypeEnum.PAYMENT.getType())){
                typePayment = checkedString + " 付款 ";
            }else{
                typePayment = empty + " 付款 ";
            }
            if(prepaidFlag.equals(PaymentAppTypeEnum.OFF_SET.getType())){
                typeOff = checkedString + " 冲账 ";
            }else{
                typeOff = empty + " 冲账 ";
            }
        } else {
            typePre = empty + " 预付 ";
            typePayment = empty + " 付款 ";
            typeOff = empty + " 冲账 ";
        }

        RowRenderData row0 = Rows.of("收款单位:" + payName, null, typePre + typePayment + typeOff, "币种：" + currency).rowExactHeight(1).verticalCenter().create();
        table.addRow(row0);

        ruleBuilder.map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, 1));

        RowRenderData row1 = Rows.of("开户银行:" + paymentAppDO.getBank(), null, "账号：" + paymentAppDO.getBankAccount(), null). rowAtleastHeight(1).verticalCenter().create();
        ruleBuilder.map(MergeCellRule.Grid.of(1, 0), MergeCellRule.Grid.of(1, 1));
        ruleBuilder.map(MergeCellRule.Grid.of(1, 2), MergeCellRule.Grid.of(1, 3));
        table.addRow(row1);

        RowRenderData row2 = Rows.of("发票内容", "发票金额", "支付金额", "费用归集").rowExactHeight(1).center().create();
        table.addRow(row2);

        DictSubjectPageReqVO param = new DictSubjectPageReqVO();
        param.setPageSize(-1);
        List<DictSubjectRespVO> dictSubjectList = dictSubjectService.getDictSubjectPage(param).getList();
        if (CollUtil.isEmpty(dictSubjectList)) {
            throw exception(DICT_SUBJECT_NOT_EXISTS);
        }

        //添加发票明细
        BigDecimal invoiceAmount = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(paymentAppDO.getInvoiceAmountList())) {
            Optional<JsonAmount> first = paymentAppDO.getInvoiceAmountList().stream().findFirst();
            if (first.isPresent()) {
                invoiceAmount = first.get().getAmount();
            }
        }

        List<PaymentPrintDetailVO> invoiceList = new ArrayList<>();
        StringBuilder desc = new StringBuilder();
        List<FeeShareRespDTO> feeShareDTO = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.PAYMENT_APP, paymentAppDO.getCode());
        if (CollUtil.isNotEmpty(feeShareDTO)) {
            for (var s : feeShareDTO) {
                if (Objects.nonNull(s.getFeeShareDetail())) {
                    desc.append(s.getFeeShareDetail().replace(",", "\r\n")).append("\r\n");
                }
            }
        }

        String feeName = "";
        Long dictSubjectId = paymentAppDO.getDictSubjectId();
        if (Objects.nonNull(dictSubjectId)) {
            DictSubjectRespVO dictSubject = dictSubjectService.getDictSubject(dictSubjectId);
            if (Objects.nonNull(dictSubject)) {
                feeName = dictSubject.getFeeName();
            }
        }
        invoiceList.add(new PaymentPrintDetailVO()
                .setFeeName(feeName)
                .setInvoiceAmount(invoiceAmount)
                .setAmount(paymentAppDO.getAmount().getAmount())
                .setFeeShareDesc(desc.toString())
        );
//        invoiceList.add(new PaymentPrintDetailVO());

        if (CollUtil.isNotEmpty(invoiceList)) {
            invoiceList.forEach(invoice -> {
                RowRenderData row = Rows.of(invoice.getFeeName(),
                        invoice.getInvoiceAmount() == null ? null : invoice.getInvoiceAmount().toString(),
                        invoice.getAmount() == null ? null : invoice.getAmount().toString(),
                        invoice.getFeeShareDesc()).rowAtleastHeight(1).center().create();
                table.addRow(row);
            });
        }

        int count = table.getRows().size();
        if (count > 4) {
            ruleBuilder.map(MergeCellRule.Grid.of(3, 3), MergeCellRule.Grid.of(count - 1, 3));
        }

        BigDecimal loanAmount = BigDecimal.ZERO;
        //判断不是预付款查询核销已付金额
        List<String> paymentAppList = paymentAppDO.getPaymentAppList();
        if (!Objects.equals(paymentAppDO.getPrepaidFlag(), PaymentAppTypeEnum.PRE_PAID.getType()) && CollUtil.isNotEmpty(paymentAppList)) {
            List<SimplePaymentAppResp> simplePaymentAppRespList = getPaymentAppList(paymentAppList, true);
            if (CollUtil.isNotEmpty(simplePaymentAppRespList)) {
                for (var pay : simplePaymentAppRespList) {
                    if (Objects.nonNull(pay.getAmount())) {
                        loanAmount = loanAmount.add(pay.getAmount().getAmount());
                    }
                }
            }
        }

        RowRenderData rowEnd2 = Rows.of("支付总金额：" + (invoiceAmount == null ? "0" : invoiceAmount.toString()),
                "核销借款金额：" + loanAmount,
                "本次支付金额：" + paymentAppDO.getAmount().getAmount(),
                null).rowExactHeight(1).verticalCenter().create();
        table.addRow(rowEnd2);
        count = table.getRows().size();
        ruleBuilder.map(MergeCellRule.Grid.of(count - 1, 2), MergeCellRule.Grid.of(count - 1, 3));

        String amountString = AmountToChineseUtil.toChineseUpper(paymentAppDO.getAmount().getAmount());
        RowRenderData rowEnd1 = Rows.of("本次支付金额大写：" + amountString, null, null, null).rowExactHeight(1).verticalCenter().create();
        table.addRow(rowEnd1);
        count = table.getRows().size();
        ruleBuilder.map(MergeCellRule.Grid.of(count - 1, 0), MergeCellRule.Grid.of(count - 1, 3));

        //添加审批人
//        params.put("leader", getLeader(paymentAppDO));
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentAppDO.getId(), PROCESS_DEFINITION_KEY);
        Map<String, String> approverMap = bpmProcessInstanceApi.getApproverByProcessInstance(bpmProcessInstanceId);
        params.put("deptManager", approverMap.get("部门经理"));

        table.setMergeRule(ruleBuilder.build());
        params.put("feeTable", table);
        paymentAppDO.setPrintDate(LocalDateTime.now());
        paymentAppMapper.updateById(paymentAppDO);
        return reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,paymentAppDO.getCode());

    }

    @Override
    @DataPermission(enable = false)
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        List<PaymentAppDO> paymentAppDOS = paymentAppMapper.selectList(PaymentAppDO::getCode, businessCode);
        if (Objects.isNull(paymentAppDOS) || paymentAppDOS.size() < 1) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        PaymentAppDO paymentAppDO = paymentAppDOS.get(0);
        return paymentAppDO.getAmount();
    }

    @Override
    public List<PaymentAppSendDetailRespVO> exportPaymentAppSendDetailExcel(Long id) {
        PaymentAppDO paymentAppDO = validatePaymentAppExists(id);
        List<EmsSendDetailDTO> details = emsSendApi.getDetailListByCodes(paymentAppDO.getRelationCode());
        return BeanUtils.toBean(details, PaymentAppSendDetailRespVO.class);
    }

    @Override
    public String printShipmentDetail(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId) {
        if (Objects.isNull(id)) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        if (Objects.isNull(reportCode)) {
            throw exception(REPORTCODE_NULL);
        }
        if (Objects.nonNull(sourceType) && Objects.isNull(sourceCode)) {
            throw exception(SOURCECODE_NULL);
        }
        if (Objects.isNull(sourceType) && Objects.nonNull(sourceCode)) {
            throw exception(SOURCETYPE_NULL);
        }
        PaymentAppDO paymentAppDO = paymentAppMapper.selectById(id);
        if (Objects.isNull(paymentAppDO)) {
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }

        ReportDTO reportDTO;
        if (companyId != null) {
            if (sourceType != null) {
                reportDTO = reportApi.getSourceReport(reportCode, sourceCode, sourceType, companyId);
            } else {
                reportDTO = reportApi.getCompanyReport(reportCode, companyId);
            }
        } else {
            reportDTO = reportApi.getReport(reportCode);
        }

        if (reportDTO == null || CollUtil.isEmpty(reportDTO.getAnnex())) {
            throw exception(REPORT_NULL);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }

        String projectPath = System.getProperty("user.dir");
        HashMap<String, Object> params = new HashMap<>();
        List<String> relationCodes = paymentAppDO.getRelationCode();
        Map<String, ShipmentDTO> shipmentMap = shipmentApi.getShipmentByForwarderFeeCodes(relationCodes);
        if (shipmentMap.isEmpty()) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }

        Map<String, ForwarderFeeDTO> forwarderFeeDTOMap = forwarderFeeApi.getListByCodes(relationCodes);
        if (forwarderFeeDTOMap.isEmpty()) {
            throw exception(SHIPMENT_FORWARD_FEE_NOT_EXISTS);
        }

        List<String> strings = forwarderFeeDTOMap.values().stream().map(ForwarderFeeDTO::getVenderName).toList();
        if (strings.isEmpty()) {
            throw exception(VENDER_NOT_EXISTS);
        }
        params.put("venderName", strings.get(0));

        
        // 1. 新增"付款抬头" = 公司主体信息
        if (paymentAppDO.getCompanyId() != null) {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(paymentAppDO.getCompanyId());
            if (companyDTO != null) {
                params.put("companyName", companyDTO.getCompanyName()); // 公司主体作为付款抬头
            }
        }
       // 2. 新增付款编号
        params.put("code", paymentAppDO.getCode());

        BigDecimal totalAmount = forwarderFeeDTOMap.values().stream().map(ForwarderFeeDTO::getAmount)
                .filter(Objects::nonNull).map(JsonAmount::getAmount)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        params.put("amount", totalAmount);
        // 审批信息
        String processInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentAppDO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(processInstanceId)){
            Map<String, String> approverMap = bpmProcessInstanceApi.getApproverByProcessInstance(processInstanceId);
            if (CollUtil.isNotEmpty(approverMap)){
                // 部门审批
                params.put("deptName", approverMap.get("部门经理"));
            }
        }
        // 去掉备注列，调整为7列表格结构 - 确保百分比总和为100
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{15, 15, 14, 18, 20, 10, 8}).create();
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();

        // 去掉备注列的表头结构
        RowRenderData row0 = Rows.of("发票号", "出运日期", "外销合同", "采购合同", "基础产品编号", "金额", "业务员签字").rowExactHeight(1).center().create();
        table.addRow(row0);
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> shipmentCodes = shipmentMap.values().stream().map(ShipmentDTO::getCode).distinct().toList();
        Map<String,List<JsonAmount>> amountMap = forwarderFeeApi.getAmountByShipmentCodes(shipmentCodes);
        if(CollUtil.isEmpty(amountMap)){
            throw exception(SHIPMENT_FORWARD_FEE_NOT_EXISTS);
        }

        for (Map.Entry<String, ShipmentDTO> entry : shipmentMap.entrySet()) {
            String forwarderFeeCode = entry.getKey();
            ShipmentDTO shipmentDTO = entry.getValue();
            List<ShipmentItemDTO> children = shipmentDTO.getChildren();
            if (children.isEmpty()) {
                continue;
            }
            ForwarderFeeDTO forwarderFeeDTO = forwarderFeeDTOMap.get(forwarderFeeCode);
            if(forwarderFeeDTO == null){
                continue;
            }
            BigDecimal thisAmount = Optional.ofNullable(forwarderFeeDTO)
                    .map(ForwarderFeeDTO::getAmount)
                    .map(JsonAmount::getAmount)
                    .orElse(BigDecimal.ZERO);
            List<ForwarderFeeExportVO> forwarderFeeExportVOList = new ArrayList<>();
            List<JsonAmount> shipmentTotalAmount = amountMap.get(shipmentDTO.getCode());
            children.forEach(item->{
                ForwarderFeeExportVO forwarderFeeExportVO = new ForwarderFeeExportVO();
                forwarderFeeExportVO.setSaleContractCode( item.getSaleContractCode());
                forwarderFeeExportVO.setInvoiceCode(shipmentDTO.getInvoiceCode());
                forwarderFeeExportVO.setPurchaseContractCode(item.getPurchaseContractCode());
                forwarderFeeExportVO.setBasicSkuCode(item.getBasicSkuCode());
                String currency = forwarderFeeDTO.getAmount().getCurrency();
                Optional<JsonAmount> first = shipmentTotalAmount.stream().filter(s -> Objects.equals(s.getCurrency(), currency)).findFirst();
                BigDecimal amount = BigDecimal.ZERO;
                if(first.isPresent()){
                    amount = item.getForwarderShareAmount().getAmount().multiply(thisAmount).divide(first.get().getAmount(),6, RoundingMode.HALF_UP) ;
                }
                forwarderFeeExportVO.setAmount(amount);
                forwarderFeeExportVO.setCskuCode(item.getCskuCode());
                forwarderFeeExportVO.setEstDepartureTime(shipmentDTO.getEstDepartureTime() == null ? "" : shipmentDTO.getEstDepartureTime().format(formatter));
                total.set(NumUtil.add(total.get(),amount));
                forwarderFeeExportVOList.add(forwarderFeeExportVO);
            });
            Map<String, Map<String, Map<String, Map<String, Map<String, List<ForwarderFeeExportVO>>>>>> groupMap = forwarderFeeExportVOList.stream().collect(Collectors.groupingBy(ForwarderFeeExportVO::getInvoiceCode, Collectors.groupingBy(ForwarderFeeExportVO::getSaleContractCode,
                    Collectors.groupingBy(ForwarderFeeExportVO::getPurchaseContractCode, Collectors.groupingBy(ForwarderFeeExportVO::getBasicSkuCode,
                            Collectors.groupingBy(ForwarderFeeExportVO::getCskuCode))))));
            groupMap.forEach((invoiceCode,invoiceCodeMap)->{
                if (CollUtil.isEmpty(invoiceCodeMap)){
                    return;
                }
                invoiceCodeMap.forEach((saleContractCode,saleMap)->{
                    if (CollUtil.isEmpty(saleMap)){
                        return;
                    }
                    saleMap.forEach((purchaseCOntractCode,purchaseMap)->{
                        if (CollUtil.isEmpty(purchaseMap)){
                            return;
                        }
                        purchaseMap.forEach((basicSkuCode,basicSkuMap)->{
                            if (CollUtil.isEmpty(basicSkuMap)){
                                return;
                            }
                            basicSkuMap.forEach((cskuCode,cskuList)->{
                                if (CollUtil.isEmpty(cskuList)){
                                    return;
                                }
                                Optional<BigDecimal> groupAmount = cskuList.stream().map(ForwarderFeeExportVO::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add);
                                Optional<ForwarderFeeExportVO> first = cskuList.stream().findFirst();
                                if (first.isPresent()&&groupAmount.isPresent()){
                                    // 去掉备注列，调整为7列结构
                                    RowRenderData dataRow = Rows.of(
                                            shipmentDTO.getInvoiceCode(),
                                            shipmentDTO.getEstDepartureTime() == null ? "" : shipmentDTO.getEstDepartureTime().format(formatter),
                                            first.get().getSaleContractCode(),
                                            first.get().getPurchaseContractCode(),
                                            first.get().getBasicSkuCode(),
                                            groupAmount.get().setScale(2, RoundingMode.HALF_UP).toString(),
                                            "" // 业务员签字字段
                                    ).rowExactHeight(1).center().create();
                                    table.addRow(dataRow);
                                }
                            });
                        });
                    });
                });
            });
        }

        // 4. 加金额合计行 - 前五列合并，后两列合并（去掉备注列）
        RowRenderData totalRow = Rows.of("合计("+paymentAppDO.getAmount().getCurrency()+")", "", "", "", "", total.get().setScale(2, RoundingMode.HALF_UP).toString(), "").rowExactHeight(1).verticalCenter().create();
        table.addRow(totalRow);
        
        // 设置合计行第一个单元格（"合计："）的样式为右对齐
        List<ParagraphRenderData> totalParagraphs = totalRow.getCells().get(0).getParagraphs();
        ParagraphStyle totalStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.RIGHT) // 右对齐
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        totalParagraphs.get(0).setParagraphStyle(totalStyle);
        
        // 设置合计行第六个单元格（金额）的样式为居中对齐
        List<ParagraphRenderData> amountParagraphs = totalRow.getCells().get(5).getParagraphs();
        ParagraphStyle amountStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.CENTER) // 居中对齐
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        amountParagraphs.get(0).setParagraphStyle(amountStyle);
        
        // 设置合并单元格规则：合计行的前五列合并，后两列合并（去掉备注列）
        int totalRowIndex = table.getRows().size() - 1; // 合计行的索引
        ruleBuilder.map(MergeCellRule.Grid.of(totalRowIndex, 0), MergeCellRule.Grid.of(totalRowIndex, 4)); // 合并前五列 (0-4)
        ruleBuilder.map(MergeCellRule.Grid.of(totalRowIndex, 5), MergeCellRule.Grid.of(totalRowIndex, 6)); // 合并后两列 (5-6)
//        String amountString = MoneyUtil.convertToChinese(total);
//        RowRenderData rowEnd2 = Rows.of("TOTAL：" + total.toString(),
//                "大写合计：" + amountString
//                    ).rowExactHeight(1).verticalCenter().create();
//        table.addRow(rowEnd2);
//        int count = table.getRows().size();
//        ruleBuilder.map(MergeCellRule.Grid.of(count-1, 0), MergeCellRule.Grid.of(count-1, 8));

        table.setMergeRule(ruleBuilder.build());
        params.put("table", table);
        paymentAppDO.setPrintDate(LocalDateTime.now());
        paymentAppMapper.updateById(paymentAppDO);
        return reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,paymentAppDO.getCode());

    }

    @Override
    public boolean validateRelationCodeExists(Collection<String> relationCodes) {
        if (CollUtil.isEmpty(relationCodes)){
            return false;
        }
        return paymentAppMapper.selectCount(new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getRelationCode, relationCodes).ne(PaymentAppDO::getAuditStatus,BpmProcessInstanceResultEnum.CLOSE.getResult()))>0;
    }

    @Override
    public boolean validateCustExists(String custCode) {
        Long count = paymentAppMapper.selectCount(new LambdaQueryWrapperX<PaymentAppDO>().eq(PaymentAppDO::getBusinessSubjectCode, custCode).ne(PaymentAppDO::getAuditStatus, BpmProcessInstanceResultEnum.CLOSE.getResult()));
        return count > 0;
    }

    @Override
    public Map<String, PaymentAppDTO> getSimplePaymentMsgByCodes(Collection<String> codes) {
        List<PaymentAppDO> paymentAppDOS = paymentAppMapper.selectList(new LambdaQueryWrapperX<PaymentAppDO>().in(PaymentAppDO::getCode, codes));
        if (CollUtil.isEmpty(paymentAppDOS)){
            return Map.of();
        }
        return paymentAppDOS.stream().map(s->new PaymentAppDTO().setCode(s.getCode()).setPaymentDate(s.getPaymentDate()).setPaymentStatus(s.getPaymentStatus())).collect(Collectors.toMap(PaymentAppDTO::getCode, paymentAppDTO -> paymentAppDTO));
    }

    private String getApproveDeptMan(PaymentAppDO paymentAppDO) {
        List<BpmNodeUser> userList;
        try {
            String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentAppDO.getId(), PROCESS_DEFINITION_KEY);
            if (Objects.isNull(bpmProcessInstanceId)) {
                return "";
            }
            userList = bpmDefinitionApi.getAllAssigneeByModuleId(PROCESS_DEFINITION_KEY, bpmProcessInstanceId);
            if (CollUtil.isEmpty(userList)) {
                return "";
            }
        } catch (Exception exception) {
            return "";
        }
        UserDept applyer = paymentAppDO.getApplyer();
        if (Objects.isNull(applyer)) {
            return "";
        }
        Optional<BpmNodeUser> first = userList.stream().findFirst();
        if (first.isPresent()) {
            if (Objects.equals(first.get().getApproveFlag(), BooleanEnum.NO.getValue())) {
                return "";
            }
            UserDept user = first.get().getUser();
            if (Objects.nonNull(user)) {
                DeptRespDTO dept = deptApi.getDept(user.getDeptId());
                if (Objects.nonNull(dept)) {
                    //判断是否为部门负责人
                    List<Long> list = dept.getLeaderUserIds().stream().toList();
                    if (CollUtil.isNotEmpty(list) && list.contains(user.getUserId())) {
                        return user.getNickname();
                    }
                }
            }
        }
        return "";
    }

    private String getLeader(PaymentAppDO paymentAppDO) {
        List<BpmNodeUser> userList;
        try {
            String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(paymentAppDO.getId(), PROCESS_DEFINITION_KEY);
            if (Objects.isNull(bpmProcessInstanceId)) {
                return "";
            }
            userList = bpmDefinitionApi.getAllAssigneeByModuleId(PROCESS_DEFINITION_KEY, bpmProcessInstanceId);
            if (CollUtil.isEmpty(userList)) {
                return "";
            }
        } catch (Exception exception) {
            return "";
        }
        //判断领导审批人员  去除财务人员，去除没有审批 的人员  去除 第一个审批人的最后一个审批人
        List<BpmNodeUser> list = userList.stream().filter(s ->
//                !Objects.equals(s.getUser().getDeptName(), "财务部") &&
                Objects.equals(s.getApproveFlag(), BooleanEnum.YES.getValue())).toList();
        if (list.size() > 1) {
            return list.get(list.size() - 1).getUser().getNickname();
        }
        return "";
    }

}