package com.syj.eplus.module.oa.controller.admin.loanapp;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.enums.VenderLoanSourceEnum;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.*;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 借款申请单")
@RestController
@RequestMapping("/oa/loan-app")
@Validated
public class LoanAppController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private LoanAppService loanAppService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long loanAppId) {
        loanAppService.submitTask(loanAppId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        loanAppService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        loanAppService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建借款申请单")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:create')")
    public CommonResult<Long> createLoanApp(@Valid @RequestBody LoanAppSaveReqVO createReqVO) {
        createReqVO.setLoanSource(VenderLoanSourceEnum.PERSON.getValue());
        return success(loanAppService.createLoanApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新借款申请单")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:update')")
    public CommonResult<Boolean> updateLoanApp(@Valid @RequestBody LoanAppSaveReqVO updateReqVO) {
        try {
            updateReqVO.setLoanSource(VenderLoanSourceEnum.PERSON.getValue());
            loanAppService.updateLoanApp(updateReqVO);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return error(new ErrorCode(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg() + "：" + e.getMessage()));
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除借款申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:loan-app:delete')")
    public CommonResult<Boolean> deleteLoanApp(@RequestParam("id") Long id) {
        LoanAppDO appDo = loanAppService.getLoanAppById(id);
        if (Objects.isNull(appDo)) {
            return success(false);
        }
        if (Objects.equals(VenderLoanSourceEnum.PERSON.getValue(), appDo.getLoanSource())) {
            loanAppService.deleteLoanApp(id);
            return success(true);
        }
        return success(false);
    }

    @GetMapping("/detail")
    @Operation(summary = "通过借款单id获得借款申请单")
    @Parameter(name = "id", description = "借款申请单id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:query')")
    public CommonResult<LoanAppInfoRespVo> getLoanAppDetail(@RequestParam("id") Long id) {
        LoanAppInfoRespVo loanAppInfoRespVo = loanAppService.getLoanApp(new LoanAppDetailReq().setLoanAppId(id));
        if (Objects.isNull(loanAppInfoRespVo)) {
            return success(null);
        }
        if (Objects.equals(loanAppInfoRespVo.getLoanSource(), VenderLoanSourceEnum.PERSON.getValue())) {
            return success(loanAppInfoRespVo);
        }
        return success(null);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "通过流程id获得借款申请单")
    @Parameter(name = "id", description = "流程id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:query')")
    public CommonResult<LoanAppRespVO> getProcessLoanApp(@RequestParam("id") String id) {
        LoanAppRespVO loanAppInfoRespVo = loanAppService.getLoanApp(new LoanAppDetailReq().setProcessInstanceId(id));
        return success(loanAppInfoRespVo);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得借款申请单精简列表")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleLoanAppRespDTO>> getLoanApp(LoanAppPageReqVO pageReqVO) {
        pageReqVO.setLoanSource(VenderLoanSourceEnum.PERSON.getValue());
        PageResult<SimpleLoanAppRespDTO> simpleLoanAppRespDTOListByApplyerId = loanAppService.getSimpleLoanAppRespDTOListByApplyerId(pageReqVO);
        return success(simpleLoanAppRespDTOListByApplyerId);
    }

    @GetMapping("/page")
    @Operation(summary = "获得借款申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:query')")
    public CommonResult<PageResult<LoanAppRespVO>> getLoanAppPage(@Valid LoanAppPageReqVO pageReqVO) {
        pageReqVO.setLoanSource(VenderLoanSourceEnum.PERSON.getValue());
        PageResult<LoanAppRespVO> loanAppPage = loanAppService.getLoanAppPage(pageReqVO);
        return success(loanAppPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出借款申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:loan-app:export')")
    @OperateLog(type = EXPORT)
    public void exportLoanAppExcel(@Valid LoanAppPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setLoanSource(VenderLoanSourceEnum.PERSON.getValue());
        List<LoanAppRespVO> list = loanAppService.getLoanAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "借款申请单.xls", "数据", LoanAppRespVO.class,
                list);
    }

    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "外销合同id", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "打印类型", example = "1"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('oa:loan-app:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId) {
        String pdfFile = loanAppService.print(id, reportCode, reportId, companyId);
        return success(pdfFile);
    }
}