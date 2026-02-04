package com.syj.eplus.module.oa.service.loanapp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.AmountToChineseUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.dto.SimpleRepayRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.*;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.LoanDescVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailRespVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.converter.LoanConverter;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanappSumDO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import com.syj.eplus.module.oa.dal.mysql.loanapp.LoanAppMapper;
import com.syj.eplus.module.oa.enums.PrintFlagEnum;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import com.syj.eplus.module.oa.service.reimbrepaydetail.ReimbRepayDetailService;
import com.syj.eplus.module.oa.service.repayapp.RepayAppService;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.UNDERREVIEW_EDITTING_NOT_ALLOWED;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.CURRENCY_RATE_NOT_EXISTS;

/**
 * 借款申请单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class LoanAppServiceImpl implements LoanAppService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private LoanAppMapper loanAppMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private DeptApi deptApi;

    private static final String PROCESS_DEFINITION_KEY = "oa_loan_app";

    private static final String PROCESS_VENDER_DEFINITION_KEY = "vender_loan";
    public final static String CODE_PREFIX = "JK";
    public final static String CODE_TYPE = "LOAN_APP";

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;

    @Resource
    private ReimbService reimbService;

    @Resource
    private RepayAppService repayAppService;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private CompanyApi companyApi;
    @Resource
    private RateApi rateApi;

    @Resource
    private ReportApi reportApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLoanApp(LoanAppSaveReqVO createReqVO) {
        // 插入
        LoanAppDO loanApp = BeanUtils.toBean(createReqVO, LoanAppDO.class);
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        loanApp.setCode(code);
        loanApp.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        loanApp.setPrintFlag(PrintFlagEnum.NO.getValue());
        loanApp.setPrintTimes(0);
        loanApp.setLoanStatus(VenderLoanStatusEnum.PRE_SUBMIT.getValue());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        // 获取申请人信息
        loanApp.setApplyer(adminUserApi.getUserDeptByUserId(loginUserId));
        loanApp.setOutstandingAmount(createReqVO.getAmount());
        loanApp.setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
        loanApp.setLoanDate(LocalDateTime.now());
        loanApp.setPaymentStatus(PaymentStatusEnum.UNPAID.getValue());
        Map<Long, UserBackAccountDTO> userBackAccountDTOMap = adminUserApi.getUserBackAccountDTOMap(loginUserId);
        if (CollUtil.isNotEmpty(userBackAccountDTOMap)){
            UserBackAccountDTO userBackAccountDTO = userBackAccountDTOMap.get(loginUserId);
            if (Objects.nonNull(userBackAccountDTO)){
                loanApp.setBank(userBackAccountDTO.getBank());
                loanApp.setBankAccount(userBackAccountDTO.getBankAccount());
                loanApp.setBankPoc(userBackAccountDTO.getBankPoc());
                loanApp.setBankCode(userBackAccountDTO.getBankCode());
                loanApp.setBankAddress(userBackAccountDTO.getBankAddress());
            }
        }
        loanAppMapper.insert(loanApp);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            if (Objects.equals(VenderLoanSourceEnum.PERSON.getValue(), createReqVO.getLoanSource())) {
                submitTask(loanApp.getId(), WebFrameworkUtils.getLoginUserId());
            } else if (Objects.equals(VenderLoanSourceEnum.VENDER.getValue(), createReqVO.getLoanSource())) {
                submitVenderTask(loanApp.getId(), WebFrameworkUtils.getLoginUserId());
            }
        }
        // 返回
        return loanApp.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoanApp(LoanAppSaveReqVO updateReqVO) {
        // 校验存在
        validateLoanAppExists(updateReqVO.getId());

        // 更新
        LoanAppDO updateObj = BeanUtils.toBean(updateReqVO, LoanAppDO.class);

        loanAppMapper.updateById(updateObj);

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            if (Objects.equals(VenderLoanSourceEnum.PERSON.getValue(), updateReqVO.getLoanSource())) {
                submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
            } else if (Objects.equals(VenderLoanSourceEnum.VENDER.getValue(), updateReqVO.getLoanSource())) {
                submitVenderTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
            }
        }
    }

    @Override
    public void deleteLoanApp(Long id) {
        // 校验存在
        validateLoanAppExists(id);
        // 删除
        loanAppMapper.deleteById(id);
    }

    private LoanAppDO validateLoanAppExists(Long id) {
        LoanAppDO loanAppDO = loanAppMapper.selectById(id);
        if (loanAppDO == null) {
            throw exception(LOAN_APP_NOT_EXISTS);
        }
        Integer auditStatus = loanAppDO.getAuditStatus();
        //审核中的数据不允许修改,删除
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            throw exception(UNDERREVIEW_EDITTING_NOT_ALLOWED);
        }
        return loanAppDO;
    }

    @Override
    public LoanAppInfoRespVo getLoanApp(LoanAppDetailReq req) {
//        return loanAppMapper.selectById(id);
        //将不同入口的参数转换为loanAppId
        Long loanAppId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getLoanAppId();
        if (Objects.isNull(loanAppId)) {
            logger.error("[借款申请单详情]未获取到借款申请id");
            return null;
        }
        LoanAppDO loanAppDO = loanAppMapper.selectById(loanAppId);
        if (Objects.isNull(loanAppDO)) {
            return null;
        }
        LoanAppInfoRespVo loanAppInfoRespVo = BeanUtils.toBean(loanAppDO, LoanAppInfoRespVo.class);
        String processString = PROCESS_DEFINITION_KEY;
        if (Objects.equals(loanAppDO.getLoanSource(), VenderLoanSourceEnum.VENDER.getValue())) {
            processString = PROCESS_VENDER_DEFINITION_KEY;
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(loanAppId, processString);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            loanAppInfoRespVo.setProcessInstanceId(bpmProcessInstanceId);
        }
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOList = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isNotEmpty(simpleCompanyDTOList)) {
            SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOList.get(loanAppInfoRespVo.getCompanyId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                loanAppInfoRespVo.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
        }
        //获取还款列表-包含"报销还款"和"实际还款"两部分
        List<SimpleRepayRespDTO> repayList = new ArrayList<>();
        //--实际还款-根据借款借款id查询RepayApp(不包含已取消状态)
        List<RepayAppDO> repayAppDOList = repayAppService.getRepayAppListWithOutCancel(loanAppId);
        Map<Long, ReimbRespVO> reimbRespMap = new HashMap<>();
        //--报销还款-根据借款借款id查询RepayApp(不包含已取消状态)
        List<ReimbRepayDetailDO> reimbRepayDetailDOList = reimbRepayDetailService.getReimbRepayDetailListWithOutCancel(loanAppId);
        if (CollUtil.isNotEmpty(reimbRepayDetailDOList)) {
            List<Long> reimbIdList = reimbRepayDetailDOList.stream().map(ReimbRepayDetailDO::getReimbId).toList();
            reimbRespMap = reimbService.getBatchReimbResp(reimbIdList);
        }
        // 转换还款记录
        repayList = LoanConverter.INSTANCE.convertSimpleRepayRespDTO(repayAppDOList, reimbRepayDetailDOList, reimbRespMap);
        if (CollUtil.isNotEmpty(repayList)) {
            Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
            repayList.forEach(s -> {
                s.setVenderCode(loanAppDO.getVenderCode()).setVenderName(loanAppDO.getVenderName());
            });
            repayList.stream().map(SimpleRepayRespDTO::getRepayAmount).map(s -> {
                if (StrUtil.isEmpty(s.getCurrency()) || Objects.isNull(s.getAmount())) {
                    return BigDecimal.ZERO;
                }
                return s.getAmount();
            }).reduce(BigDecimal::add).map(s -> NumUtil.sub(loanAppInfoRespVo.getAmount().getAmount(), s)).ifPresent(s -> {
                loanAppInfoRespVo.setOutstandingAmount(new JsonAmount().setAmount(s).setCurrency(loanAppInfoRespVo.getAmount().getCurrency()));
            });
            //根据还款时间排序
            Collections.sort(repayList, Comparator.comparing(SimpleRepayRespDTO::getRepayTime));
        }
        loanAppInfoRespVo.setRepayList(repayList);
        return loanAppInfoRespVo;
    }

    @Override
    public PageResult<LoanAppRespVO> getLoanAppPage(LoanAppPageReqVO pageReqVO) {
        PageResult<LoanAppDO> loanAppDOPageResult = loanAppMapper.selectPage(pageReqVO);
        PageResult<LoanAppRespVO> loanAppRespVOPageResult = LoanConverter.INSTANCE.convertLoanAppPageResult(loanAppDOPageResult, companyApi.getSimpleCompanyDTO());
        List<LoanAppRespVO> list = loanAppRespVOPageResult.getList();
        if (CollUtil.isEmpty(list)) {
            return loanAppRespVOPageResult;
        }
        calcRepayAmountToPage(list, null, null);
        Map<String, Object> loanSummary = getLoanSummary(pageReqVO);
        return loanAppRespVOPageResult.setSummary(loanSummary);
    }

    private Map<String, Object> getLoanSummary(LoanAppPageReqVO pageReqVO) {
        LocalDateTime loanDateStart = null;
        LocalDateTime loanDateEnd = null;
        if (pageReqVO.getLoanDate() != null && pageReqVO.getLoanDate().length == 2) {
            loanDateStart = pageReqVO.getLoanDate()[0];
            loanDateEnd = pageReqVO.getLoanDate()[1];
        }
        List<LoanappSumDO> loanappSumDOS = loanAppMapper.selectLoanappSum(pageReqVO.getApplyerId(),pageReqVO.getLoanDeptId(),pageReqVO.getRepayStatus(),pageReqVO.getPaymentStatus(),loanDateStart,loanDateEnd);
        if (CollUtil.isEmpty(loanappSumDOS)) {
            return Map.of();
        }
        Map<String,Object> summary = new HashMap<>();
        sumAmountsByCurrency(loanappSumDOS, LoanappSumDO::getAmountSum, "amountSum", summary);
        sumAmountsByCurrency(loanappSumDOS, LoanappSumDO::getPaymentAmountSum, "paymentAmountSum", summary);
        sumAmountsByCurrency(loanappSumDOS, LoanappSumDO::getRepayAmountSum, "repayAmountSum", summary);
        sumAmountsByCurrency(loanappSumDOS, LoanappSumDO::getOutAmountSum, "outAmountSum", summary);
        sumAmountsByCurrency(loanappSumDOS, LoanappSumDO::getInRepayAmountSum, "inRepaymentAmountSum", summary);
        return summary;
    }

    /**
     * 根据币种汇总金额数据
     * 
     * @param loanappSumDOS    需要汇总的借款应用数据列表
     * @param amountExtractor  金额提取器函数，用于从LoanappSumDO中提取特定金额字段
     * @param summaryKey       汇总结果在summary中的键名
     * @param summary          存放汇总结果的Map
     */
    private void sumAmountsByCurrency(List<LoanappSumDO> loanappSumDOS,
                                      Function<LoanappSumDO, BigDecimal> amountExtractor,
                                      String summaryKey,
                                      Map<String, Object> summary
    ) {
        // 如果数据列表为空或键名为空则直接返回
        if (CollUtil.isEmpty(loanappSumDOS) || StrUtil.isEmpty(summaryKey)) {
            return;
        }
        // 按币种对数据进行分组
        Map<String, List<LoanappSumDO>> loanappSumMap = loanappSumDOS.stream()
                .collect(Collectors.groupingBy(LoanappSumDO::getCurrency));
        // 将分组后的数据转换为JsonAmount列表
        List<JsonAmount> list = loanappSumMap.entrySet().stream().map(entry -> entry.getValue().stream().map(amountExtractor)
                .filter(s->s.compareTo(BigDecimal.ZERO)>0)
                .map(s -> new JsonAmount().setCurrency(entry.getKey()).setAmount(s))
                .toList()).flatMap(List::stream).toList();
        summary.put(summaryKey, list);
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
    public void submitTask(Long loanAppId, Long userId) {
        LoanAppDO loanAppDO = loanAppMapper.selectById(loanAppId);
        String processString = PROCESS_DEFINITION_KEY;
        if (Objects.equals(loanAppDO.getLoanSource(), VenderLoanSourceEnum.VENDER.getValue())) {
            processString = PROCESS_VENDER_DEFINITION_KEY;
        }
        updateAuditStatus(loanAppId, BpmProcessInstanceResultEnum.PROCESS.getResult());
        updateLoanDateAndApplyerId(loanAppId);
        Map<String, Object> variables = new HashMap<>();
        JsonAmount jsonLoanAppAmount = loanAppDO.getAmount();
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        if (Objects.isNull(jsonLoanAppAmount) || Objects.isNull(jsonLoanAppAmount.getAmount())) {
            throw exception(LOAN_APP_AMOUNT_IS_NULL);
        }
        if (Objects.isNull(jsonLoanAppAmount.getCurrency())) {
            throw exception(LOAN_APP_CURRENCY_IS_NULL);
        }
        BigDecimal rate = dailyRateMap.get(jsonLoanAppAmount.getCurrency());
        if (Objects.isNull(rate)) {
            throw exception(CURRENCY_RATE_NOT_EXISTS, jsonLoanAppAmount.getCurrency());
        }
        variables.put("loanAmount", jsonLoanAppAmount.getAmount());
        UserDept userDept = adminUserApi.getUserDeptByUserId(userId);
        Optional.ofNullable(userDept).ifPresent(s -> variables.put("deptCode", userDept.getDeptCode()));
        bpmProcessInstanceApi.createProcessInstance(userId, processString, loanAppId,variables,null);
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public void approveVenderTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectVenderTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitVenderTask(Long loanAppId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_VENDER_DEFINITION_KEY, loanAppId);
        updateAuditStatus(loanAppId, BpmProcessInstanceResultEnum.PROCESS.getResult());
        updateLoanDateAndApplyerId(loanAppId);
    }

    @Override
    public String getVenderProcessDefinitionKey() {
        return PROCESS_VENDER_DEFINITION_KEY;
    }

    @Override
    public String print(Long id, String reportCode, Long reportId, Long companyId) {
        if (id == null) {
            throw exception(SALE_CONTRACT_ID_NULL);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }

        LoanAppDO loanAppDO = loanAppMapper.selectById(id);
        if (loanAppDO == null) {
            throw exception(SALECONTRACT_NULL);
        }
        ReportDTO reportDTO;
        if (reportId != null) {
            reportDTO = reportApi.getReportById(reportId);
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
        HashMap<String, Object> params = getWordParams(loanAppDO);
        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params , loanAppDO.getCode());
        if (StrUtil.isNotEmpty(result)) {
            // 更新暂支单打印状态
            LoanAppDO updateDO = new LoanAppDO();
            updateDO.setId(id);
            updateDO.setPrintFlag(BooleanEnum.YES.getValue());
            int printTimes = Objects.isNull(loanAppDO.getPrintTimes()) ? CalculationDict.ZERO : loanAppDO.getPrintTimes();
            updateDO.setPrintTimes(printTimes + 1);
            loanAppMapper.updateById(updateDO);
        }
        return result;
    }

    public HashMap<String, Object> getWordParams(LoanAppDO loanAppDO){
        HashMap<String, Object> params = new HashMap<>();
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(loanAppDO.getCompanyId());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("code", loanAppDO.getCode());
        if (loanAppDO.getLoanDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年M月d日");
            String loanDate = dtf.format(loanAppDO.getLoanDate());
            params.put("date", loanDate);
        }
        //暂支人
        if(Objects.nonNull(loanAppDO.getApplyer())){
            params.put("userName", loanAppDO.getApplyer().getNickname());
        }
        if (VenderLoanTypeEnum.CASH.getValue().equals(loanAppDO.getLoanType())){
            params.put("cash", new TextRenderData("\uF0FE",new Style("Wingdings",14)));
            params.put("transfer",new TextRenderData("\uF0A8",new Style("Wingdings",14)));
        } else if (VenderLoanTypeEnum.TRANSFER.getValue().equals(loanAppDO.getLoanType())) {
            params.put("cash", new TextRenderData("\uF0A8",new Style("Wingdings",14)));
            params.put("transfer", new TextRenderData("\uF0FE",new Style("Wingdings",14)));
        }
        params.put("deptName", loanAppDO.getApplyer().getDeptName());
        params.put("currency", loanAppDO.getAmount().getCurrency());
        params.put("bank", loanAppDO.getBank());
        params.put("bankAccount", loanAppDO.getBankAccount());
        params.put("resource", loanAppDO.getPurpose());
        params.put("applyerName", loanAppDO.getApplyer().getNickname());
        params.put("loanAmount", loanAppDO.getAmount().getAmount());
        String chineseUpper = AmountToChineseUtil.toChineseUpper(loanAppDO.getAmount().getAmount());
        params.put("chineseUpper", chineseUpper);
        // 审批信息
        String processInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(loanAppDO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(processInstanceId)){
            Map<String, String> approverMap = bpmProcessInstanceApi.getApproverByProcessInstance(processInstanceId);
            if (CollUtil.isNotEmpty(approverMap)){
                // 部门审批
                params.put("deptApprover", approverMap.get("部门经理"));
            }
        }
        return params;
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        Integer status = 0;
        if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.UNSUBMITTED.getResult())) {
            status = VenderLoanStatusEnum.PRE_SUBMIT.getValue();
        } else if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.PROCESS.getResult())) {
            status = VenderLoanStatusEnum.PRE_APPROVE.getValue();
        } else if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            status = VenderLoanStatusEnum.APPROVE.getValue();
        } else if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.REJECT.getResult())) {
            status = VenderLoanStatusEnum.REJECT.getValue();
        } else if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.CANCEL.getResult())) {
            status = VenderLoanStatusEnum.PRE_SUBMIT.getValue();
        }

        loanAppMapper.updateById(LoanAppDO.builder().id(auditableId).auditStatus(auditStatus).loanStatus(status).build());
    }

    @Override
    public void updateLoanDateAndApplyerId(Long loanAppId) {
        UserDept userDept = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
        loanAppMapper.updateById(LoanAppDO.builder().id(loanAppId).loanDate(LocalDateTime.now()).applyer(userDept).build());
    }

    @Override
    public List<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOList(List<Long> idList, Long travelReimbId, Long repayAppId) {
        LambdaQueryWrapper<LoanAppDO> queryWrapper = new LambdaQueryWrapperX<LoanAppDO>().select(LoanAppDO::getId, LoanAppDO::getCode, LoanAppDO::getCompanyId, LoanAppDO::getApplyer, LoanAppDO::getAmount, LoanAppDO::getRepayAmount, LoanAppDO::getOutstandingAmount, LoanAppDO::getPurpose);
        if (CollUtil.isNotEmpty(idList)) {
            queryWrapper.in(LoanAppDO::getId, idList);
        }
        return getSimpleLoanAppRespList(queryWrapper, travelReimbId, repayAppId);
    }

    @Override
    public PageResult<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOListByApplyerId(LoanAppPageReqVO pageReqVO) {
        PageResult<SimpleLoanAppRespDTO> simpleLoanAppRespDTOPageResult = getSimpleLoanAppRespDTOS(pageReqVO);
        if (Objects.isNull(simpleLoanAppRespDTOPageResult)) {
            return null;
        }
        List<SimpleLoanAppRespDTO> loanAppRespDTOList = simpleLoanAppRespDTOPageResult.getList();
        if (CollUtil.isNotEmpty(loanAppRespDTOList)) {
            //计算当前还款单还款金额
            calcRepayAmount(loanAppRespDTOList, null, null);
        }
        return simpleLoanAppRespDTOPageResult;
    }

    @Override
    public SimpleLoanAppRespDTO getsimpleLoanAppDTO(LoanAppDetailReq req) {
        LoanAppRespVO loanApp = getLoanApp(req);
        if (Objects.isNull(loanApp)) {
            return null;
        }
        SimpleLoanAppRespDTO simpleLoanAppRespDTO = LoanConverter.INSTANCE.convertSimpleLoanAppRespDTOByVO(loanApp);
        calcRepayAmount(Collections.singletonList(simpleLoanAppRespDTO), null, null);
        return simpleLoanAppRespDTO;
    }

    @Override
    public List<Long> getIdListByCode(String code) {
        List<LoanAppDO> loanAppDOList = loanAppMapper.selectList(new LambdaQueryWrapperX<LoanAppDO>().like(LoanAppDO::getCode, code));
        if (CollUtil.isEmpty(loanAppDOList)) {
            return null;
        }
        return loanAppDOList.stream().map(LoanAppDO::getId).toList();
    }

    @Override
    public void updatePaymentStatus(PaymentAppDTO paymentAppDTO) {
        loanAppMapper.update(LoanAppDO.builder().code(paymentAppDTO.getCode())
                .paymentAmount(paymentAppDTO.getPaymentAmount())
                .paymentStatus(paymentAppDTO.getPaymentStatus())
                .paymentDate(paymentAppDTO.getPaymentDate())
                .cashier(paymentAppDTO.getCashier()).build(), new LambdaQueryWrapperX<LoanAppDO>().eq(LoanAppDO::getCode, paymentAppDTO.getCode()));
    }

    @Override
    public void updateRepayAmount(JsonAmount repayAmount, Long id) {
        LoanAppDO loanAppDO = validateLoanAppExists(id);
        JsonAmount repayAmount_res = new JsonAmount();
        JsonAmount outstandingAmount_res = new JsonAmount();
        // 已还金额
        JsonAmount base_repayAmount = loanAppDO.getRepayAmount();
        // 借款金额
        JsonAmount amount = loanAppDO.getAmount();
        // 当库中已还金额为空时当前还款金额即已还金额
        if (Objects.isNull(base_repayAmount)) {
            repayAmount_res = repayAmount;
        } else {
            // 当已经还过款则 已还金额 = 已还金额 + 当前还款金额
            repayAmount_res.setAmount(base_repayAmount.getAmount().add(repayAmount.getAmount())).setCurrency(base_repayAmount.getCurrency());
        }
        // 剩余未还款金额 = 借款总额 - 已还金额
        outstandingAmount_res.setAmount(amount.getAmount().subtract(repayAmount_res.getAmount())).setCurrency(repayAmount.getCurrency());
        loanAppDO.setRepayAmount(repayAmount_res).setOutstandingAmount(outstandingAmount_res);
        if (BigDecimal.ZERO.compareTo(outstandingAmount_res.getAmount()) == 0) {
            loanAppDO.setRepayStatus(BooleanEnum.YES.getValue());
        }
        loanAppMapper.updateById(loanAppDO);
    }

    @Override
    public void batchUpdaPaymentStatus(List<PaymentAppDTO> paymentAppDTOList) {
        if (CollUtil.isEmpty(paymentAppDTOList)) {
            return;
        }
        List<String> codeList = paymentAppDTOList.stream().map(PaymentAppDTO::getCode).distinct().toList();
        if (CollUtil.isEmpty(codeList)) {
            return;
        }
        Map<String, PaymentAppDTO> paymentAppDTOMap = paymentAppDTOList.stream().collect(Collectors.toMap(PaymentAppDTO::getCode, s -> s, (o, n) -> o));
        List<LoanAppDO> loanAppDOList = loanAppMapper.selectList(new LambdaQueryWrapperX<LoanAppDO>().select(LoanAppDO::getId, LoanAppDO::getCode, LoanAppDO::getAnnex).in(LoanAppDO::getCode, codeList));
        if (CollUtil.isEmpty(loanAppDOList)) {
            throw exception(LOAN_APP_NOT_EXISTS);
        }
        loanAppDOList.forEach(s -> {
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
            List<SimpleFile> loanAnnex = s.getAnnex();
            if (CollUtil.isEmpty(loanAnnex)) {
                s.setAnnex(paymentAnnex);
            } else {
                loanAnnex.addAll(paymentAnnex);
                s.setAnnex(loanAnnex);
            }
        });
        loanAppMapper.updateBatch(loanAppDOList);
    }

    @Override
    public LoanAppDO getLoanAppById(Long id) {
        return loanAppMapper.selectById(id);

    }

    @Override
    public List<LoanDescVO> getNotDoneVenderLoanList(String code) {
        List<LoanAppDO> doList = loanAppMapper.selectList(new LambdaQueryWrapperX<LoanAppDO>()
                .eq(LoanAppDO::getLoanSource, VenderLoanSourceEnum.VENDER.getValue())
                .eq(LoanAppDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult())
                .eq(LoanAppDO::getRepayStatus, 2) //加枚举
                .eq(LoanAppDO::getVenderCode, code)
        );
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        List<LoanDescVO> result = new ArrayList<>();

        doList.forEach(s -> {
            LoanDescVO item = BeanUtils.toBean(s, LoanDescVO.class);
            if (Objects.nonNull(s.getAmount()) && Objects.nonNull(s.getPaymentAmount())) {
                BigDecimal notAmount = s.getAmount().getAmount().subtract(s.getPaymentAmount().getAmount());
                item.setNotRepayAmount(new JsonAmount().setCurrency(s.getAmount().getCurrency()).setAmount(notAmount));
            }
            result.add(item);
        });

        return result;
    }

    private PageResult<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOS(LoanAppPageReqVO pageReqVO) {
        pageReqVO.setRepayStatus(BooleanEnum.NO.getValue());
        pageReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        pageReqVO.setPaymentStatus(BooleanEnum.YES.getValue());
        PageResult<LoanAppDO> loanAppDOPageResult = loanAppMapper.selectsimplePage(pageReqVO);
        if (CollUtil.isEmpty(loanAppDOPageResult.getList())) {
            return null;
        }
        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(null);
        return LoanConverter.INSTANCE.convertSimpleLoanAppPageDTO(loanAppDOPageResult, deptMap, companyApi.getSimpleCompanyDTO());
    }

    private List<SimpleLoanAppRespDTO> getSimpleLoanAppRespList(LambdaQueryWrapper<LoanAppDO> queryWrapper, Long travelReimbId, Long repayAppId) {
        List<LoanAppDO> loanAppDOList = loanAppMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(loanAppDOList)) {
            return Collections.emptyList();
        }
        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(null);
        List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = LoanConverter.INSTANCE.convertSimpleLoanAppList(loanAppDOList, deptMap, companyApi.getSimpleCompanyDTO());
        if (CollUtil.isNotEmpty(simpleLoanAppRespDTOList)) {
            //计算当前还款单还款金额
            calcRepayAmount(simpleLoanAppRespDTOList, travelReimbId, repayAppId);
        }
        return simpleLoanAppRespDTOList;
    }

    public void calcRepayAmountToPage(List<LoanAppRespVO> loanAppDOList, Long travelReimbId, Long repayappId) {
        List<Long> loanAppIds = loanAppDOList.stream().map(LoanAppRespVO::getId).toList();
        //根据借款单id批量获取报销还款详情(非已还款) result->map
        Map<Long, ReimbRepayDetailRespVO> reimbRepayDetailMap = reimbRepayDetailService.getReimbRepayDetailMap(loanAppIds, travelReimbId);
        //根据借款单id批量获取还款详情(非已还款) result->map
        Map<Long, List<RepayAppRespVO>> repayAppMap = repayAppService.getRepayAppMap(loanAppIds, repayappId);
        Map<Long, BigDecimal> result = new HashMap<>();
        //计算报销还款明细金额
        if (CollUtil.isNotEmpty(reimbRepayDetailMap)) {
            reimbRepayDetailMap.forEach((loanAppId, reimbRepayDetailRespVO) -> {
                JsonAmount repayAmount = reimbRepayDetailRespVO.getRepayAmount();
                if (Objects.nonNull(repayAmount)) {
                    result.merge(loanAppId, repayAmount.getAmount(), BigDecimal::add);
                }
            });
        }
        //计算还款单金额
        if (CollUtil.isNotEmpty(repayAppMap)) {
            repayAppMap.forEach((loanAppId, repayAppRespVOList) -> {
                if (CollUtil.isNotEmpty(repayAppRespVOList)) {
                    repayAppRespVOList.forEach(s -> {
                        JsonAmount repayAmount = s.getAmount();
                        if (Objects.nonNull(repayAmount)) {
                            result.merge(loanAppId, repayAmount.getAmount(), BigDecimal::add);
                        }
                    });
                }
            });
        }
        // 当前待还款金额 = 借款单借款金额 - （还款明细中还款金额+还款单还款金额）
        loanAppDOList.forEach(loanAppRespDTO -> {
            Long loanAppId = loanAppRespDTO.getId();
            BigDecimal repayAmount = result.get(loanAppId);
            if (Objects.nonNull(repayAmount)) {
                // 借款单中待还款金额
                JsonAmount outstandingAmount = loanAppRespDTO.getOutstandingAmount();
                if (Objects.nonNull(outstandingAmount)) {
                    BigDecimal amount = loanAppRespDTO.getAmount().getAmount();
                    loanAppRespDTO.setInRepaymentAmount(new JsonAmount().setAmount(repayAmount).setCurrency(outstandingAmount.getCurrency()));
                    // 已还金额
                    JsonAmount dbRepayJsonAmount = loanAppRespDTO.getRepayAmount();
                    BigDecimal dbRepayAmount = Optional.ofNullable(dbRepayJsonAmount)
                            .map(JsonAmount::getAmount)
                            .orElse(BigDecimal.ZERO);
                    loanAppRespDTO.setOutstandingAmount(new JsonAmount().setAmount(amount.subtract(repayAmount.add(dbRepayAmount))).setCurrency(outstandingAmount.getCurrency()));
                }
            }
        });
    }

    /**
     * 计算还款中金额信息
     *
     * @param loanAppRespDTOList 借款单
     */
    public void calcRepayAmount(List<SimpleLoanAppRespDTO> loanAppRespDTOList, Long travelReimbId, Long repayappId) {
        List<Long> loanAppIds = loanAppRespDTOList.stream().map(SimpleLoanAppRespDTO::getId).toList();
        //根据借款单id批量获取报销还款详情(非已还款) result->map
        Map<Long, ReimbRepayDetailRespVO> reimbRepayDetailMap = reimbRepayDetailService.getReimbRepayDetailMap(loanAppIds, travelReimbId);
        //根据借款单id批量获取还款详情(非已还款) result->map
        Map<Long, List<RepayAppRespVO>> repayAppMap = repayAppService.getRepayAppMap(loanAppIds, repayappId);
        Map<Long, BigDecimal> result = new HashMap<>();
        //计算报销还款明细金额
        if (CollUtil.isNotEmpty(reimbRepayDetailMap)) {
            reimbRepayDetailMap.forEach((loanAppId, reimbRepayDetailRespVO) -> {
                JsonAmount repayAmount = reimbRepayDetailRespVO.getRepayAmount();
                if (Objects.nonNull(repayAmount)) {
                    result.merge(loanAppId, repayAmount.getAmount(), BigDecimal::add);
                }
            });
        }
        //计算还款单金额
        if (CollUtil.isNotEmpty(repayAppMap)) {
            repayAppMap.forEach((loanAppId, repayAppRespVOList) -> {
                if (CollUtil.isNotEmpty(repayAppRespVOList)) {
                    repayAppRespVOList.forEach(s -> {
                        JsonAmount repayAmount = s.getAmount();
                        if (Objects.nonNull(repayAmount)) {
                            result.merge(loanAppId, repayAmount.getAmount(), BigDecimal::add);
                        }
                    });
                }
            });
        }
        // 当前待还款金额 = 借款单借款金额 - （还款明细中还款金额+还款单还款金额）
        loanAppRespDTOList.forEach(loanAppRespDTO -> {
            Long loanAppId = loanAppRespDTO.getId();
            BigDecimal repayAmount = result.get(loanAppId);
            if (Objects.nonNull(repayAmount)) {
                // 借款单中待还款金额
                JsonAmount outstandingAmount = loanAppRespDTO.getOutstandingAmount();
                if (Objects.nonNull(outstandingAmount)) {
                    BigDecimal amount = loanAppRespDTO.getAmount().getAmount();
                    loanAppRespDTO.setInRepaymentAmount(new JsonAmount().setAmount(repayAmount).setCurrency(outstandingAmount.getCurrency()));
                    // 已还金额
                    JsonAmount dbRepayJsonAmount = loanAppRespDTO.getRepayAmount();
                    BigDecimal dbRepayAmount = Optional.ofNullable(dbRepayJsonAmount)
                            .map(JsonAmount::getAmount)
                            .orElse(BigDecimal.ZERO);
                    loanAppRespDTO.setOutstandingAmount(new JsonAmount().setAmount(amount.subtract(repayAmount.add(dbRepayAmount))).setCurrency(outstandingAmount.getCurrency()));
                }
            }
        });
    }
}