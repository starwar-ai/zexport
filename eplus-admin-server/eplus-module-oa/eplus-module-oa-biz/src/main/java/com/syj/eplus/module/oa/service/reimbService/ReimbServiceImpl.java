package com.syj.eplus.module.oa.service.reimbService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.definition.BpmDefinitionApi;
import cn.iocoder.yudao.module.bpm.api.definition.dto.BpmNodeUser;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;

import com.deepoove.poi.data.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.MoneyUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.home.api.InvoiceHolderApi;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.RepayAppApi;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import com.syj.eplus.module.oa.api.dto.RepayAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbCloseReq;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.convert.reimb.ReimbConvert;
import com.syj.eplus.module.oa.converter.ReimbConverter;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import com.syj.eplus.module.oa.dal.mysql.expenseapp.ExpenseAppMapper;
import com.syj.eplus.module.oa.dal.mysql.loanapp.LoanAppMapper;
import com.syj.eplus.module.oa.dal.mysql.reimb.ReimbMapper;
import com.syj.eplus.module.oa.dal.mysql.subject.SubjectMapper;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import com.syj.eplus.module.oa.enums.RepayTypeEnum;
import com.syj.eplus.module.oa.enums.traveldetail.TravelExpenseTypeEnum;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import com.syj.eplus.module.oa.service.dictsubject.DictSubjectService;
import com.syj.eplus.module.oa.service.reimbrepaydetail.ReimbRepayDetailService;
import com.syj.eplus.module.oa.service.reimbshare.ReimbShareService;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

@Service
public class ReimbServiceImpl implements ReimbService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ReimbMapper reimbMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private BpmDefinitionApi bpmDefinitionApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private ReimbShareService reimbShareService;
    @Resource
    private SubjectMapper subjectMapper;
    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    private ReportApi reportApi;
    @Resource
    private DictSubjectService dictSubjectService;
    @Resource
    private InvoiceHolderApi invoiceHolderApi;

    @Resource
    private ExpenseAppMapper expenseAppMapper;

    @Resource
    private OperateLogApi operateLogApi;

    @Resource
    private RepayAppApi repayAppApi;

    @Resource
    private ApplyService applyService;
    @Resource
    private RateApi rateApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;
    @Resource
    private PaymentApi paymentApi;
    @Resource
    private LoanAppMapper loanAppMapper;
    private static final String OPERATE_EXTS_KEY = "reimbKey";
    private static final String SN_TYPE = "SN_REIMB";
    private static final String TRAVEL_CODE_PREFIX = "BSC";
    private static final String ENTERTAIN_CODE_PREFIX = "BSZ";
    private static final String GENERAL_CODE_PREFIX = "BST";
    private static final String OTHER_CODE_PREFIX = "BSM";
    private static final String CODE_PREFIX = "BS";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReimb(ReimbDO reimbDO) {
        Integer reimbType = reimbDO.getReimbType();
        String CODE_PREFIX = CommonDict.EMPTY_STR;
        if (ReimbTypeEnum.TRAVEL.getValue().equals(reimbType)) {
            CODE_PREFIX = TRAVEL_CODE_PREFIX;
        } else if (ReimbTypeEnum.ENTERTAIN.getValue().equals(reimbType)) {
            CODE_PREFIX = ENTERTAIN_CODE_PREFIX;
        } else if (ReimbTypeEnum.GENERAL.getValue().equals(reimbType)) {
            CODE_PREFIX = GENERAL_CODE_PREFIX;
        } else if (ReimbTypeEnum.OTHER.getValue().equals(reimbType)){
            CODE_PREFIX = OTHER_CODE_PREFIX;
        }else {
            throw exception(REIMB_TYPE_NOT_EXISTS, reimbType);
        }
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        reimbDO.setCode(code);
        reimbDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        reimbDO.setReimbUser(userDept);
        //财务打款状态默认未打款
        reimbDO.setPaymentStatus(PaymentStatusEnum.UNPAID.getValue());
        reimbDO.setApproveUser(userDept);
        reimbMapper.insert(reimbDO);
        // 处理草稿
        updateDraft(reimbDO.getReimbDetailList());
        // 更新发票夹状态
        updateInvoiceStatus(null, reimbDO.getReimbDetailList());
        return reimbDO.getId();
    }

    private void updateDraft(List<JsonReimbDetail> reimbDetails) {
        if (CollUtil.isEmpty(reimbDetails)) {
            return;
        }
        List<String> reimbDetailIdList = reimbDetails.stream().filter(s -> Objects.nonNull(s.getId())).map(JsonReimbDetail::getId).toList();
        ReimbDO reimbDO = reimbMapper.selectOne(ReimbDO::getDraftFlag, BooleanEnum.YES.getValue());
        if (Objects.isNull(reimbDO)) {
            return;
        }
        List<JsonReimbDetail> reimbDetailList = reimbDO.getReimbDetailList();
        if (CollUtil.isEmpty(reimbDetailIdList)) {
            return;
        }
        reimbDetailList.removeIf(s -> reimbDetailIdList.contains(s.getId()));
        reimbMapper.updateById(new ReimbDO().setId(reimbDO.getId()).setReimbDetailList(reimbDetailList));
    }

    @Override
    public int updateReimb(ReimbDO reimbDO) {
        Long reimbId = reimbDO.getId();
        // 校验存在
        ReimbDO dbReimbDO = validateReimbExists(reimbId);
        int result = reimbMapper.updateById(reimbDO);
        updateInvoiceStatus(dbReimbDO.getReimbDetailList(), reimbDO.getReimbDetailList());
        return result;
    }

    private void updateInvoiceStatus(List<JsonReimbDetail> oldDetailList, List<JsonReimbDetail> detailList) {
        if (CollUtil.isEmpty(oldDetailList) && CollUtil.isEmpty(detailList)) {
            return;
        }
        // 库里无小项数据 新增小项直接更新状态已使用
        if (CollUtil.isEmpty(oldDetailList) && CollUtil.isNotEmpty(detailList)) {
            List<Long> invoiceHolderIdList = getInvoiceHolderIdList(detailList);
            if (CollUtil.isNotEmpty(invoiceHolderIdList)) {
                invoiceHolderApi.updateInvoiceHolderStatus(invoiceHolderIdList, ReimbStatusEnum.REIMBURSED.getValue());
            }
        }
        // 库里有小项且前端传入小项
        if (CollUtil.isNotEmpty(oldDetailList) && CollUtil.isNotEmpty(detailList)) {
            List<Long> dbInvoiceHolderIdList = getInvoiceHolderIdList(oldDetailList);
            List<Long> invoiceHolderIdList = getInvoiceHolderIdList(detailList);
            if (CollUtil.isEmpty(dbInvoiceHolderIdList) && CollUtil.isNotEmpty(invoiceHolderIdList)) {
                invoiceHolderApi.updateInvoiceHolderStatus(invoiceHolderIdList, ReimbStatusEnum.REIMBURSED.getValue());
            }
            if (CollUtil.isNotEmpty(dbInvoiceHolderIdList) && CollUtil.isNotEmpty(invoiceHolderIdList)) {
                List<Long> unUsedList = dbInvoiceHolderIdList.stream().filter(s -> !invoiceHolderIdList.contains(s)).toList();
                List<Long> usedList = invoiceHolderIdList.stream().filter(s -> !dbInvoiceHolderIdList.contains(s)).toList();
                invoiceHolderApi.updateInvoiceHolderStatus(unUsedList, ReimbStatusEnum.NOT_REIMBURSED.getValue());
                invoiceHolderApi.updateInvoiceHolderStatus(usedList, ReimbStatusEnum.REIMBURSED.getValue());
            }
        }
        // 库里有小项且前端未传入
        if (CollUtil.isNotEmpty(oldDetailList) && CollUtil.isEmpty(detailList)) {
            List<Long> invoiceHolderIdList = getInvoiceHolderIdList(oldDetailList);
            invoiceHolderApi.updateInvoiceHolderStatus(invoiceHolderIdList, ReimbStatusEnum.NOT_REIMBURSED.getValue());
        }
    }

    private List<Long> getInvoiceHolderIdList(List<JsonReimbDetail> detailList) {
        List<Long> invoiceHolderIdList = new ArrayList<>();
        if (CollUtil.isEmpty(detailList)) {
            return List.of();
        }
        List<Long> invoiceIdList = detailList.stream().filter(s -> CollUtil.isNotEmpty(s.getInvoice())).flatMap(s -> s.getInvoice().stream()).map(SimpleFile::getSourceId).filter(Objects::nonNull).toList();
        if (CollUtil.isNotEmpty(invoiceIdList)) {
            invoiceHolderIdList.addAll(invoiceIdList);
        }
        List<Long> pictureIdList = detailList.stream().filter(s -> CollUtil.isNotEmpty(s.getPicture())).flatMap(s -> s.getPicture().stream()).map(SimpleFile::getSourceId).filter(Objects::nonNull).toList();
        if (CollUtil.isNotEmpty(pictureIdList)) {
            invoiceHolderIdList.addAll(pictureIdList);
        }
        return invoiceHolderIdList;
    }

    @Override
    public int deleteReimb(Long id) {
        // 校验存在
        ReimbDO reimbDO = validateReimbExists(id);
        // 删除
        int result = reimbMapper.deleteById(id);
        updateInvoiceStatus(reimbDO.getReimbDetailList(), null);
        //关联申请单是否申请报销状态修改为否
        if (CollUtil.isNotEmpty(reimbDO.getApplyIdList())) {
            applyService.batchUpdateIsApplyExpense(reimbDO.getApplyIdList(), ApplyExpenseEnum.CLOSE.getValue());
        }
        return result;
    }

    @Override
    public ReimbRespVO getReimbResp(Long id) {
        ReimbDO reimbDO = reimbMapper.selectById(id);
        if (Objects.isNull(reimbDO)) {
            return null;
        }
        List<SubjectDO> subjectDOList = subjectMapper.selectList();
        Map<Long, String> subjectMap = subjectDOList.stream().collect(Collectors.toMap(SubjectDO::getId, SubjectDO::getName));
        Map<Long, DictSubjectDO> dictSubjectMap = dictSubjectService.getDictSubjectMap();
        ReimbRespVO reimbRespVO = ReimbConverter.INSTANCE.convertReimbRespVO(reimbDO, adminUserApi.getUserIdNameCache(), deptApi.getDeptMap(null), companyApi.getSimpleCompanyDTO(), dictSubjectService.getDictSubjectMap());
        List<JsonReimbDetail> reimbDetailList = reimbRespVO.getReimbDetailList();
        if (CollUtil.isNotEmpty(reimbDetailList)) {
            reimbDetailList.forEach(s -> {
                if (CollUtil.isNotEmpty(dictSubjectMap)){
                    DictSubjectDO dictSubjectDO = dictSubjectMap.get(s.getDictSubjectId());
                    if (Objects.nonNull(dictSubjectDO)){
                        s.setFeeName(dictSubjectDO.getFeeName());
                    }
                }
                if (CollUtil.isNotEmpty(subjectMap)){
                    String financialSubjectName = subjectMap.get(s.getFinancialSubjectId());
                    s.setFinancialSubjectName(financialSubjectName);
                }
            });
        }
        Long financialSubjectId = reimbDO.getFinancialSubjectId();
        reimbRespVO.setFinancialSubjectName(subjectMap.get(financialSubjectId));
        // 获取费用归属列表
        List<ReimbShareRespVO> reimbShareByReimbId = reimbShareService.getReimbShareByReimbId(id, reimbRespVO.getAuxiliaryType());
        reimbRespVO.setAuxiliaryList(reimbShareByReimbId);
        operateLogApi.getOperateLogRespDTOList(OPERATE_EXTS_KEY, reimbRespVO.getCode());
        return reimbRespVO;
    }

    @Override
    public PageResult<ReimbRespVO> getReimbPage(ReimbPageReqVO pageReqVO, Integer freeShareSourceType) {
        DeptDataPermissionRespDTO deptDataPermission = permissionApi.getDeptDataPermission(WebFrameworkUtils.getLoginUserId());
        pageReqVO.setDeptDataPermission(deptDataPermission);
        PageResult<ReimbDO> reimbDOPageResult = reimbMapper.selectPage(pageReqVO);
        Map<Long, DictSubjectDO> dictSubjectMap = dictSubjectService.getDictSubjectMap();
        PageResult<ReimbRespVO> reimbRespVOPageResult = ReimbConverter.INSTANCE.convertPageResult(reimbDOPageResult, adminUserApi.getUserIdNameCache(), deptApi.getDeptMap(null), companyApi.getSimpleCompanyDTO(), dictSubjectMap);
        List<ReimbRespVO> list = reimbRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            List<String> codeList = list.stream().map(ReimbRespVO::getCode).toList();
            Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(freeShareSourceType, codeList);
            if (CollUtil.isNotEmpty(feeShareMap)) {
                list.forEach(s -> {
                    List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                    s.setFeeShare(feeShareRespDTO);
                });
            }
        }
        return reimbRespVOPageResult;

    }

    private ReimbDO validateReimbExists(Long id) {
        ReimbDO reimbDO = reimbMapper.selectById(id);
        if (reimbDO == null) {
            throw exception(REIMB_NOT_EXISTS);
        }
        return reimbDO;
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        ReimbDO reimbDO = ReimbDO.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            reimbDO.setProcessInstanceId(processInstanceId);
        }
        reimbMapper.updateById(reimbDO);
        feeShareApi.updateSourceStatus(FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE.getValue(), auditableId, auditStatus);
    }

    @Override
    public void updatePrintTimes(Long id, Integer printStatus) {
        //如果打印状态为空或者未打印则无需更新打印次数
        if (Objects.isNull(printStatus) || PrintStatusEnum.NOT_PRINTED.getStatus().equals(printStatus)) {
            return;
        }
        reimbMapper.updatePrintTimes(id);
    }

    @Override
    public Map<Long, ReimbRespVO> getBatchReimbResp(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyMap();
        }
        List<ReimbDO> reimbDOList = reimbMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(reimbDOList)) {
            return Collections.emptyMap();
        }
        return reimbDOList.stream().map(s -> ReimbConverter.INSTANCE.convertReimbRespVO(s, adminUserApi.getUserIdNameCache(), deptApi.getDeptMap(null), companyApi.getSimpleCompanyDTO(), dictSubjectService.getDictSubjectMap()))
                .collect(Collectors.toMap(ReimbRespVO::getId, s -> s));
    }

    @Override
    public void updatePaymentMessage(PaymentAppDTO paymentAppDTO) {
        reimbMapper.update(ReimbDO.builder().code(paymentAppDTO.getCode())
                .paymentAmount(paymentAppDTO.getPaymentAmount())
                .paymentStatus(paymentAppDTO.getPaymentStatus())
                .paymentDate(paymentAppDTO.getPaymentDate())
                .cashier(paymentAppDTO.getCashier()).build(), new LambdaQueryWrapperX<ReimbDO>().eq(ReimbDO::getCode, paymentAppDTO.getCode()));
    }

    @Override
    public void batchUpdatePaymentStatus(List<PaymentAppDTO> paymentAppDTOList) {
        if (CollUtil.isEmpty(paymentAppDTOList)) {
            return;
        }
        List<String> codeList = paymentAppDTOList.stream().map(PaymentAppDTO::getCode).distinct().toList();
        if (CollUtil.isEmpty(codeList)) {
            return;
        }
        Map<String, PaymentAppDTO> paymentAppDTOMap = paymentAppDTOList.stream().collect(Collectors.toMap(PaymentAppDTO::getCode, s -> s, (o, n) -> n));
        List<ReimbDO> reimbDOList = reimbMapper.selectList(new LambdaQueryWrapperX<ReimbDO>().select(ReimbDO::getId, ReimbDO::getCode, ReimbDO::getAnnex,ReimbDO::getRepayFlag).in(ReimbDO::getCode, codeList));
        if (CollUtil.isEmpty(reimbDOList)) {
            throw exception(REIMB_NOT_EXISTS);
        }
        reimbDOList.forEach(s -> {
            PaymentAppDTO paymentAppDTO = paymentAppDTOMap.get(s.getCode());
            if (Objects.isNull(paymentAppDTO)) {
                return;
            }
            s.setPaymentAmount(paymentAppDTO.getPaymentAmount());
            s.setPaymentStatus(paymentAppDTO.getPaymentStatus());
            s.setPaymentDate(paymentAppDTO.getPaymentDate());
            s.setCashier(paymentAppDTO.getCashier());
            List<SimpleFile> paymentAnnex = paymentAppDTO.getAnnex();
            if (CollUtil.isEmpty(paymentAnnex)) {
                return;
            }
            List<SimpleFile> reimbAnnex = s.getAnnex();
            if (CollUtil.isEmpty(reimbAnnex)) {
                s.setAnnex(paymentAnnex);
            } else {
                reimbAnnex.addAll(paymentAnnex);
                s.setAnnex(reimbAnnex);
            }
        });
        reimbMapper.updateBatch(reimbDOList);
        // 回写还款信息
        Set<Long> repayReimbIdList = reimbDOList.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getRepayFlag())).map(ReimbDO::getId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(repayReimbIdList)){
            return;
        }
        Map<Long, List<ReimbRepayDetailDO>> reimbRepayDetailMap = reimbRepayDetailService.batchGetReimbRepayDetailMap(repayReimbIdList);
        if (CollUtil.isEmpty(reimbRepayDetailMap)){
            return;
        }
        Map<Long, List<ReimbRepayDetailDO>> loanReimbRepayDetailMap = reimbRepayDetailMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(ReimbRepayDetailDO::getLoanId));
        Set<Long> loanIdSet = loanReimbRepayDetailMap.keySet();
        List<LoanAppDO> loanAppDOS = loanAppMapper.selectBatchIds(loanIdSet);
        if (CollUtil.isEmpty(loanAppDOS)){
            return;
        }
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        List<ReimbRepayDetailDO> updateReimbRepayDetailDOList = new ArrayList<>();
        loanAppDOS.forEach(s->{
            JsonAmount amount = s.getAmount();
            String currency = amount.getCurrency();
            if (StrUtil.isEmpty(currency)){
                throw exception(LOAN_CURRENCY_IS_EMPTY);
            }
            List<ReimbRepayDetailDO> reimbRepayDetailDOList = loanReimbRepayDetailMap.get(s.getId());
            if (CollUtil.isEmpty(reimbRepayDetailDOList)){
                return;
            }
            reimbRepayDetailDOList.forEach(reimbRepayDetailDO -> {
                JsonAmount jsonRepayAmount = reimbRepayDetailDO.getRepayAmount();
                if (Objects.isNull(jsonRepayAmount)||Objects.isNull(jsonRepayAmount.getAmount())||Objects.isNull(jsonRepayAmount.getCurrency())){
                    return;
                }
                BigDecimal repayAmount = NumUtil.div(NumUtil.mul(jsonRepayAmount.getAmount(), dailyRateMap.get(jsonRepayAmount.getCurrency())), dailyRateMap.get(currency));
                JsonAmount baseRepayAmount = s.getRepayAmount();
                JsonAmount outstandingAmount = s.getOutstandingAmount();
                if (Objects.isNull(baseRepayAmount)||Objects.isNull(baseRepayAmount.getAmount())||Objects.isNull(baseRepayAmount.getCurrency())){
                    baseRepayAmount = new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(currency);
                }
                s.setRepayAmount(baseRepayAmount.setAmount(baseRepayAmount.getAmount().add(repayAmount)));
                s.setOutstandingAmount(outstandingAmount.setAmount(outstandingAmount.getAmount().subtract(baseRepayAmount.getAmount())));
                reimbRepayDetailDO.setRepayStatus(RepayStatus.REPAID.getStatus());
            });
            updateReimbRepayDetailDOList.addAll(reimbRepayDetailDOList);
            if (s.getRepayAmount().getAmount().compareTo(s.getAmount().getAmount())>=0){
                s.setRepayStatus(RepayStatus.REPAID.getStatus());
            }
            s.setRepayDate(LocalDateTime.now());
        });
        loanAppMapper.updateBatch(loanAppDOS);
        reimbRepayDetailService.batchUpdateReimbRepayDetail(updateReimbRepayDetailDOList);
    }

    @Override
    public void updateInvoiceHolderStatus(Long id, Integer status) {
        reimbMapper.updateHomeInvoiceHolderStatus(id, status);
    }


    @Override
    public String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId, FeeShareSourceTypeEnum busType) {
        if (id == null) {
            throw exception(REIMB_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        if (sourceType != null && sourceCode == null) {
            throw exception(SOURCECODE_NULL);
        }
        if (sourceType == null && sourceCode != null) {
            throw exception(SOURCETYPE_NULL);
        }
        ReimbDO reimbDO = reimbMapper.selectById(id);
        if (reimbDO == null) {
            throw exception(REIMB_NOT_EXISTS);
        }

        ReportDTO reportDTO;
        //上面的代码已经判断了sourceCode 和sourceType 都为null 或者都不为null   所以只需要判断一个不为null
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

        params.put("code", reimbDO.getCode());
        LocalDateTime now = LocalDateTime.now();
        params.put("year", now.getYear());
        params.put("month", now.getMonthValue());
        params.put("day", now.getDayOfMonth());

        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{20, 22, 20, 38}).create();
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();

        String name = "";
        String deptName = "";
        if (Objects.nonNull(reimbDO.getCreator())) {
            long creatorId = Long.parseLong(reimbDO.getCreator());
            UserDept userDept = adminUserApi.getUserDeptByUserId(creatorId);
            if (Objects.nonNull(userDept)) {
                name = userDept.getNickname();
                deptName = userDept.getDeptName();
            }
        }

        params.put("appName", name);
        String actualUserName = "";
        String actualUserBank = "";
        String actualUserBankAccount = "";
        if (Objects.nonNull(reimbDO.getActualUser())) {
            actualUserName = reimbDO.getActualUser().getNickname();
            // 从申请人获取银行信息（从user表获取）
            Long actualUserId = reimbDO.getActualUser().getUserId();
            if (Objects.nonNull(actualUserId)) {
                Map<Long, UserBackAccountDTO> userBackAccountMap = adminUserApi.getUserBackAccountDTOMap(actualUserId);
                if (Objects.nonNull(userBackAccountMap) && userBackAccountMap.containsKey(actualUserId)) {
                    UserBackAccountDTO userBankInfo = userBackAccountMap.get(actualUserId);
                    actualUserBank = Objects.nonNull(userBankInfo.getBank()) ? userBankInfo.getBank() : "";
                    actualUserBankAccount = Objects.nonNull(userBankInfo.getBankAccount()) ? userBankInfo.getBankAccount() : "";
                }
            }
        }

        RowRenderData row0 = Rows.of("报销人(开户姓名):" + actualUserName, null, "所属部门：" + deptName, "币种：" + reimbDO.getCurrency()).rowExactHeight(1).verticalCenter().create();
        table.addRow(row0);
        ruleBuilder.map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, 1));

        RowRenderData row1 = Rows.of("开户银行:" + actualUserBank, null, "账号：" + actualUserBankAccount, null).rowAtleastHeight(1).verticalCenter().create();
        ruleBuilder.map(MergeCellRule.Grid.of(1, 0), MergeCellRule.Grid.of(1, 1));
        ruleBuilder.map(MergeCellRule.Grid.of(1, 2), MergeCellRule.Grid.of(1, 3));
        table.addRow(row1);


        RowRenderData row2 = Rows.of("发票内容", "发票金额", "报销金额", "费用归集").rowExactHeight(1).center().create();
        table.addRow(row2);

        List<JsonReimbDetail> detailList = reimbDO.getReimbDetailList();
        if (CollUtil.isEmpty(detailList)) {
            throw exception(REIMB_REPAY_DETAIL_NOT_EXISTS);
        }

        DictSubjectPageReqVO param = new DictSubjectPageReqVO();
        param.setPageSize(-1);
        List<DictSubjectRespVO> dictSubjectList = dictSubjectService.getDictSubjectPage(param).getList();
        if (CollUtil.isEmpty(dictSubjectList)) {
            throw exception(DICT_SUBJECT_NOT_EXISTS);
        }
        List<FeeShareRespDTO> feeShareDTO = feeShareApi.getFeeShareDTO(busType, reimbDO.getCode());
        StringBuilder feeShareName = new StringBuilder();
        if (Objects.nonNull(feeShareDTO)) {
            for (var fee : feeShareDTO) {
                if (Objects.nonNull(fee.getFeeShareDetail())) {
                    feeShareName.append(fee.getFeeShareDetail().replace(",", "\r\n")).append("\r\n");
                }
            }
        }

        List<JsonReimbDetail> otherList;
        //出差报销的时候单独处理补助
        if (Objects.equals(reimbDO.getReimbType(), ReimbTypeEnum.TRAVEL.getValue())) {
            List<JsonReimbDetail> btList = detailList.stream().filter(s -> Objects.equals(s.getExpenseType(), TravelExpenseTypeEnum.TRAVEL_ALLOWANCE.getCode())).toList();
            if (CollUtil.isNotEmpty(btList)) {
                BigDecimal btInvoiceAmount = btList.stream().map(JsonReimbDetail::getInvoiceAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal btAmount = btList.stream().map(JsonReimbDetail::getExpenseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

                RowRenderData row = Rows.of("补贴", btInvoiceAmount.toString(), btAmount.toString(), feeShareName.toString()).rowAtleastHeight(1).center().create();
                table.addRow(row);
                feeShareName = null;
                otherList = detailList.stream().filter(s -> !Objects.equals(s.getExpenseType(), TravelExpenseTypeEnum.TRAVEL_ALLOWANCE.getCode())).toList();
            } else {
                otherList = detailList;
            }
        } else {
            otherList = detailList;
        }
        Map<Long, List<JsonReimbDetail>> otherListMap = otherList.stream().filter(s -> Objects.nonNull(s) && Objects.nonNull(s.getDictSubjectId())).collect(Collectors.groupingBy(JsonReimbDetail::getDictSubjectId));
        if (CollUtil.isNotEmpty(otherListMap)) {
            for (var entry : otherListMap.entrySet()) {
                BigDecimal invoiceAmount = entry.getValue().stream().map(JsonReimbDetail::getInvoiceAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal amount = entry.getValue().stream().map(JsonReimbDetail::getExpenseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

                String feeName = "";
                Optional<DictSubjectRespVO> first = dictSubjectList.stream().filter(s -> Objects.equals(s.getId(), entry.getKey())).findFirst();
                if (first.isPresent()) {
                    feeName = first.get().getFeeName();
                }
                if (Objects.equals(reimbDO.getReimbType(), ReimbTypeEnum.OTHER.getValue())) {
                    feeShareName = null;
                }
                RowRenderData row = Rows.of(feeName, invoiceAmount.toString(), amount.toString(), (feeShareName == null ? null : feeShareName.toString())).rowAtleastHeight(1).center().create();
                table.addRow(row);
                feeShareName = null;
            }
        }
        List<JsonReimbDetail> otherSubjectIdIsNullList = otherList.stream().filter(s -> Objects.nonNull(s) && Objects.isNull(s.getDictSubjectId())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(otherSubjectIdIsNullList)) {
            otherSubjectIdIsNullList.forEach(s -> {
                BigDecimal btAmount = Objects.isNull(s.getExpenseAmount()) ? BigDecimal.ZERO : s.getExpenseAmount();
                RowRenderData row = Rows.of(s.getFeeName(), CommonDict.EMPTY_STR, btAmount.toString(), s.getFeeName()).rowAtleastHeight(1).center().create();
                table.addRow(row);
            });
        }

        int count = table.getRows().size();
        if (count > 4) {
            ruleBuilder.map(MergeCellRule.Grid.of(3, 3), MergeCellRule.Grid.of(count - 1, 3));
        }

        BigDecimal totalAmount = detailList.stream().map(JsonReimbDetail::getExpenseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal loanAmount = BigDecimal.ZERO;
        List<SimpleLoanAppRespDTO> loanAppList = reimbDO.getLoanAppList();
        if (CollUtil.isNotEmpty(loanAppList)) {
            loanAmount = loanAppList.stream().map(s ->
                    s.getOutstandingAmount() != null && s.getOutstandingAmount().getAmount() != null ?
                            s.getOutstandingAmount().getAmount()
                            : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (loanAmount.compareTo(totalAmount) > 0) {
            loanAmount = totalAmount;
        }

        RowRenderData rowEnd2 = Rows.of("报销总金额：" + totalAmount,
                "核销借款金额：" + loanAmount,
                "本次报销金额：" + totalAmount.subtract(loanAmount),
                null).rowExactHeight(1).verticalCenter().create();
        table.addRow(rowEnd2);
        count = table.getRows().size();
        ruleBuilder.map(MergeCellRule.Grid.of(count - 1, 2), MergeCellRule.Grid.of(count - 1, 3));

        String amountString = MoneyUtil.convertToChinese(totalAmount.subtract(loanAmount));
        RowRenderData rowEnd1 = Rows.of("本次报销金额大写：" + amountString, null, null, null).rowExactHeight(1).verticalCenter().create();
        table.addRow(rowEnd1);
        count = table.getRows().size();
        ruleBuilder.map(MergeCellRule.Grid.of(count - 1, 0), MergeCellRule.Grid.of(count - 1, 3));

        //添加审批人
//        params.put("leader", getLeader(reimbDO));
//        params.put("deptName", getApproveDeptMan(reimbDO));
        // 审批信息
        String processInstanceId = reimbDO.getProcessInstanceId();
        if (StrUtil.isNotEmpty(processInstanceId)){
            Map<String, String> approverMap = bpmProcessInstanceApi.getApproverByProcessInstance(processInstanceId);
            if (CollUtil.isNotEmpty(approverMap)){
                // 部门审批
                params.put("deptName", approverMap.get("部门经理"));
            }
        }
        table.setMergeRule(ruleBuilder.build());
        params.put("feeTable", table);
        // 增加操作日志
        OperateLogUtils.addOperateLog(OPERATE_EXTS_KEY, reimbDO.getCode(), "打印");
        reimbDO.setPrintDate(LocalDateTime.now());
        reimbMapper.updateById(reimbDO);
        return reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params, reimbDO.getCode());
    }

    @Override
    public void createReimbDetail(ReimbDetailDTO reimbDetailDTO) {
        if (Objects.isNull(reimbDetailDTO)) {
            return;
        }
        JsonReimbDetail jsonReimbDetail = ReimbConvert.INSTANCE.convertJsonReimbDetail(reimbDetailDTO);
        jsonReimbDetail.setId(IdUtil.randomUUID());
        ReimbDO reimbDO = reimbMapper.selectOne(ReimbDO::getDraftFlag, BooleanEnum.YES.getValue());
        if (Objects.isNull(reimbDO)) {
            reimbDO = new ReimbDO();
            reimbDO.setReimbDetailList(List.of(jsonReimbDetail));
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            UserDept userDeptByUserId = adminUserApi.getUserDeptByUserId(loginUserId);
            reimbDO.setReimbUser(userDeptByUserId);
            reimbDO.setDraftFlag(BooleanEnum.YES.getValue());
            reimbMapper.insert(reimbDO);
        } else {
            List<JsonReimbDetail> reimbDetailList = reimbDO.getReimbDetailList();
            if (CollUtil.isEmpty(reimbDetailList)) {
                reimbDetailList = List.of(jsonReimbDetail);
            } else {
                reimbDetailList.add(jsonReimbDetail);
            }
            reimbMapper.updateById(new ReimbDO().setId(reimbDO.getId()).setReimbDetailList(reimbDetailList));
        }
    }

    @Override
    public List<ReimbDetailDTO> getReimbDetail() {
        ReimbDO reimbDO = reimbMapper.selectOne(ReimbDO::getDraftFlag, BooleanEnum.YES.getValue());
        List<JsonReimbDetail> reimbDetailList = reimbDO.getReimbDetailList();
        if (CollUtil.isEmpty(reimbDetailList)) {
            return List.of();
        }
        return ReimbConvert.INSTANCE.convertReimbDetailDTOList(reimbDetailList);
    }

    @Override
    @DataPermission(enable = false)
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        List<ReimbDO> reimbDOS = reimbMapper.selectList(ReimbDO::getCode, businessCode);
        if (Objects.isNull(reimbDOS) || reimbDOS.size() < 1) {
            throw exception(REIMB_NOT_EXISTS);
        }
        ReimbDO reimbDO = reimbDOS.get(0);
        if (CollUtil.isNotEmpty(reimbDO.getAmountList())) {
            return reimbDO.getAmountList().get(0);
        } else {
            return new JsonAmount();
        }
    }

    @Override
    public Long getActualUserByReimbId(Long reimbId) {
        ReimbDO reimbDO = validateReimbExists(reimbId);
        UserDept actualUser = reimbDO.getActualUser();
        if (Objects.isNull(actualUser) || Objects.isNull(actualUser.getUserId())) {
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        return actualUser.getUserId();
    }

    @Override

    public void dealRepayApp(ReimbRespVO reimbResp) {
        // 已选择还款则需计算报销金额和还款金额
        List<SimpleLoanAppRespDTO> loanAppList = reimbResp.getLoanAppList();
        // 还款列表为空则无需还款
        if (CollUtil.isEmpty(loanAppList)) {
            return;
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
            List<JsonAmount> totalAmountList = reimbResp.getTotalAmountList();
            if (CollUtil.isEmpty(totalAmountList)) {
                return;
            }
            Map<String, BigDecimal> totalAmountMap = totalAmountList.stream().collect(Collectors.groupingBy(JsonAmount::getCurrency, Collectors.reducing(BigDecimal.ZERO, JsonAmount::getAmount, BigDecimal::add)));
            BigDecimal travelReimbAmount = totalAmountMap.get(currency);
            if (Objects.isNull(travelReimbAmount)) {
                return;
            }
            RepayAppDTO repayAppDTO = new RepayAppDTO()
                    // 还款人为报销人
                    .setReparer(reimbResp.getReimbUser())
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
        if (CollUtil.isNotEmpty(repayAppDTOList)) {
            repayAppDTOList.forEach(repayAppDTO -> {
                repayAppApi.createRepayApp(repayAppDTO);
            });
        }
    }


    private String getReimbString(ReimbDO reimbDO) {

        String travel = "oa_travel_reimb";
        String general = "oa_general_reimb";
        String entertain = "oa_entertain_reimb";

        String pmsString = "";
        if (Objects.equals(reimbDO.getReimbType(), ReimbTypeEnum.TRAVEL.getValue())) {
            pmsString = travel;
        }
        if (Objects.equals(reimbDO.getReimbType(), ReimbTypeEnum.GENERAL.getValue())) {
            pmsString = general;
        }
        if (Objects.equals(reimbDO.getReimbType(), ReimbTypeEnum.ENTERTAIN.getValue())) {
            pmsString = entertain;
        }

        return pmsString;
    }

    private String getApproveDeptMan(ReimbDO reimbDO) {
        String reimbString = getReimbString(reimbDO);
        List<BpmNodeUser> userList;
        try {
            String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(reimbDO.getId(), reimbString);
            if (Objects.isNull(bpmProcessInstanceId)) {
                return "";
            }
            userList = bpmDefinitionApi.getAllAssigneeByModuleId(reimbString, bpmProcessInstanceId);
            if (CollUtil.isEmpty(userList)) {
                return "";
            }
        } catch (Exception exception) {
            return "";
        }
        String creator = reimbDO.getCreator();
        if (Objects.isNull(creator)) {
            return "";
        }
        long cretorId = Long.parseLong(creator);
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

    private String getLeader(ReimbDO reimbDO) {
        String reimbString = getReimbString(reimbDO);
        List<BpmNodeUser> userList;
        try {
            String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(reimbDO.getId(), reimbString);
            if (Objects.isNull(bpmProcessInstanceId)) {
                return "";
            }
            userList = bpmDefinitionApi.getAllAssigneeByModuleId(reimbString, bpmProcessInstanceId);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeReimb(ReimbCloseReq reimbCloseReq) {
        Long id = reimbCloseReq.getId();
        ReimbDO reimbDO = validateReimbExists(id);
        // 付过款的不可作废
        if (PaymentStatusEnum.PAID.getValue().equals(reimbDO.getPaymentStatus()) || PaymentStatusEnum.PART_PAID.getValue().equals(reimbDO.getPaymentStatus())) {
            throw exception(REIMB_IS_PAYMENT);
        }
        //审核状态修改为已作废
        reimbDO.setAuditStatus(BpmProcessInstanceResultEnum.CLOSE.getResult());
        reimbDO.setCancelReason(reimbCloseReq.getCancelReason());
        LocalDateTime cancelTime = LocalDateTime.now();
        reimbDO.setCancelTime(cancelTime);
        reimbCloseReq.setCancelTime(cancelTime);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept cancelUser = adminUserApi.getUserDeptByUserId(loginUserId);
        reimbDO.setCancelUser(cancelUser);
        reimbCloseReq.setCancelUser(cancelUser);
        reimbMapper.updateById(reimbDO);
        List<Long> applyIdList = reimbDO.getApplyIdList();
        // 处理报销还款详情，如果有已还款的记录需要恢复借款单金额
        rollbackLoanAppAmount(id);
        // 清除还款信息
        reimbRepayDetailService.batchDeleteReimbRepayDetailByReimbId(id);
        // 同步作废未付款的付款单
        ClosePaymentDTO closePaymentDTO = BeanUtils.toBean(reimbCloseReq, ClosePaymentDTO.class);
        closePaymentDTO.setBusinessCode(reimbDO.getCode());
        paymentApi.closePayment(closePaymentDTO);
        if (applyIdList.isEmpty()) {
            return;
        }
        //修改报销次数
        applyService.batchUpdateIsApplyExpense(applyIdList, ApplyExpenseEnum.CLOSE.getValue());
    }

    /**
     * 作废报销单时恢复借款单金额
     * 如果报销还款详情状态是已还款，需要将借款单的已还金额减去还款金额，待还金额加上还款金额
     * @param reimbId 报销单id
     */
    private void rollbackLoanAppAmount(Long reimbId) {
        // 获取报销还款详情列表
        List<ReimbRepayDetailDO> repayDetailList = reimbRepayDetailService.getReimbRepayDetailListByReimbId(reimbId);
        if (CollUtil.isEmpty(repayDetailList)) {
            return;
        }
        // 筛选出已还款状态的记录
        List<ReimbRepayDetailDO> repaidDetailList = repayDetailList.stream()
                .filter(s -> RepayStatus.REPAID.getStatus().equals(s.getRepayStatus()))
                .toList();
        if (CollUtil.isEmpty(repaidDetailList)) {
            return;
        }
        // 按借款单id分组汇总还款金额
        Map<Long, JsonAmount> repayAmountMap = repaidDetailList.stream()
                .collect(Collectors.toMap(
                        ReimbRepayDetailDO::getLoanId,
                        ReimbRepayDetailDO::getRepayAmount,
                        JsonAmount::add
                ));
        // 批量查询借款单
        List<LoanAppDO> loanAppDOS = loanAppMapper.selectBatchIds(repayAmountMap.keySet());
        if (CollUtil.isEmpty(loanAppDOS)) {
            return;
        }
        // 恢复借款单金额
        loanAppDOS.forEach(loanApp -> {
            JsonAmount rollbackAmount = repayAmountMap.get(loanApp.getId());
            if (Objects.isNull(rollbackAmount) || Objects.isNull(rollbackAmount.getAmount())) {
                return;
            }
            // 待还金额 = 待还金额 + 回滚金额
            JsonAmount outstandingAmount = loanApp.getOutstandingAmount();
            if (Objects.nonNull(outstandingAmount)) {
                loanApp.setOutstandingAmount(outstandingAmount.add(rollbackAmount));
            } else {
                loanApp.setOutstandingAmount(rollbackAmount);
            }
            // 已还金额 = 已还金额 - 回滚金额
            JsonAmount repayAmount = loanApp.getRepayAmount();
            if (Objects.nonNull(repayAmount)) {
                loanApp.setRepayAmount(repayAmount.sub(rollbackAmount));
            }
            // 还款状态改为未还款
            loanApp.setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
        });
        loanAppMapper.updateBatch(loanAppDOS);
    }

    @Override
    public void submitTask(Long reimbId, String definitionKey) {
        ReimbDO reimbDO = validateReimbExists(reimbId);
        UserDept actualUser = reimbDO.getActualUser();
        if (Objects.isNull(actualUser) || Objects.isNull(actualUser.getUserId())) {
            throw exception(ACTUAL_USER_NOT_EXISTS);
        }
        Map<String, Object> variables = new HashMap<>();
        List<JsonAmount> totalAmountList = reimbDO.getTotalAmountList();
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        if (CollUtil.isNotEmpty(totalAmountList)) {
            totalAmountList.stream().filter(Objects::nonNull).map(s -> {
                if (Objects.isNull(s.getAmount())) {
                    s.setAmount(BigDecimal.ZERO);
                }
                if (StrUtil.isEmpty(s.getCurrency())) {
                    return s.getAmount();
                }
                BigDecimal amountRmbRate = dailyRateMap.get(s.getCurrency());
                if (Objects.isNull(amountRmbRate)) {
                    return s.getAmount();
                }
                return NumUtil.mul(s.getAmount(), amountRmbRate);
            }).reduce(BigDecimal::add).ifPresent(s -> variables.put("reimbAmount", s));
        }
        variables.put("reimbId", reimbId);
        UserDept userDept = adminUserApi.getUserDeptByUserId(actualUser.getUserId());
        Optional.ofNullable(userDept).ifPresent(s -> variables.put("deptCode", userDept.getDeptCode()));
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(actualUser.getUserId(), definitionKey, reimbId, variables, Map.of());
        updateAuditStatus(reimbId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void updateApproveUserByInstanceId(Long reimbId, Long userId) {
        if (Objects.isNull(userId) || Objects.isNull(reimbId)) {
            return;
        }
        UserDept approveUser = adminUserApi.getUserDeptByUserId(userId);
        reimbMapper.updateById(new ReimbDO().setApproveUser(approveUser).setId(reimbId));
    }

    @Override
    public void repayLoanApp(Long reimbId) {
        if (Objects.isNull(reimbId)){
            return;
        }
        List<ReimbRepayDetailDO> repayDetailList = reimbRepayDetailService.getReimbRepayDetailListByReimbId(reimbId);
        if (CollUtil.isEmpty(repayDetailList)){
            return;
        }
        // 将报销还款详情状态改为已还款
        repayDetailList.forEach(s->s.setRepayStatus(RepayStatus.REPAID.getStatus()));
        reimbRepayDetailService.batchUpdateReimbRepayDetail(repayDetailList);
        // 按借款单id分组汇总还款金额
        Map<Long, JsonAmount> repayAmountMap = repayDetailList.stream()
                .collect(Collectors.toMap(
                        ReimbRepayDetailDO::getLoanId,
                        ReimbRepayDetailDO::getRepayAmount,
                        JsonAmount::add
                ));
        // 更新借款单：清除未还款金额，增加已还款金额
        List<LoanAppDO> loanAppDOS = loanAppMapper.selectBatchIds(repayAmountMap.keySet());
        loanAppDOS.forEach(s->{
            JsonAmount outstandingAmount = s.getOutstandingAmount();
            JsonAmount repayAmount = repayAmountMap.get(s.getId());
            s.setOutstandingAmount(outstandingAmount.sub(repayAmount));
            s.setRepayAmount(repayAmount.add(s.getRepayAmount()));
            if (s.getRepayAmount().getAmount().compareTo(s.getAmount().getAmount()) >= 0){
                s.setRepayStatus(RepayStatus.REPAID.getStatus());
            }
        });
        loanAppMapper.updateBatch(loanAppDOS);
        // 更新报销单状态为已支付，支付时间为当前时间（审批通过时间）
        LocalDateTime now = LocalDateTime.now();
        reimbMapper.updateById(new ReimbDO()
                .setId(reimbId)
                .setPaymentStatus(PaymentStatusEnum.PAID.getValue())
                .setPaymentDate(now));
    }

}