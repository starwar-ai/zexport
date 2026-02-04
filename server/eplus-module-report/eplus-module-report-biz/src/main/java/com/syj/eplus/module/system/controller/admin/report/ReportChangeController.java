package com.syj.eplus.module.system.controller.admin.report;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportRespVO;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportSaveReq;
import com.syj.eplus.module.system.service.report.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 打印模板")
@RestController
@RequestMapping("/system/poi-report")
@Validated
public class ReportChangeController {

    @Resource
    private ReportService reportService;

    @PutMapping("/change")
    @Operation(summary = "更新打印变更模板")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:change')")
    public CommonResult<Boolean> changeReport(@Valid @RequestBody ChangeReportSaveReq updateReqVO) {
        reportService.changeReport(updateReqVO);
        return success(true);
    }


    @GetMapping("/change-detail")
    @Operation(summary = "获得打印变更模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:query')")
    public CommonResult<ChangeReportRespVO> getChangeReport(@RequestParam("id") Long id) {
        ChangeReportRespVO report = reportService.getChangeReport(id);
        return success(report);
    }


    @GetMapping("/change-page")
    @Operation(summary = "获得打印模板变更分页")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:query')")
    public CommonResult<PageResult<ChangeReportRespVO>> getReportChangePage(@Valid ReportPageReqVO pageReqVO) {
        PageResult<ChangeReportRespVO> pageResult = reportService.getReportChangePage(pageReqVO);
        return success(pageResult);
    }


    @PutMapping("/change-approve")
    @Operation(summary = "变更通过任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        reportService.approveChangeTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "变更不通过任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        reportService.rejectChangeTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-submit")
    @Operation(summary = "变更提交任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report-change:submit')")
    public CommonResult<Boolean> submitChangeTask(@RequestParam Long reportId) {
        reportService.submitChangeTask(reportId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }



}