package com.syj.eplus.module.system.controller.admin.report;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.deepoove.poi.XWPFTemplate;
import com.syj.eplus.module.system.controller.admin.report.vo.*;
import com.syj.eplus.module.system.dal.dataobject.report.ReportDO;
import com.syj.eplus.module.system.service.report.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 打印模板")
@RestController
@RequestMapping("/system/poi-report")
@Validated
public class ReportController {

    @Resource
    private ReportService reportService;

    @PostMapping("/create")
    @Operation(summary = "创建打印模板")
    @PreAuthorize("@ss.hasPermission('system:poi-report:create')")
    public CommonResult<Long> createReport(@Valid @RequestBody ReportSaveReqVO createReqVO) {
        return success(reportService.createReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新打印模板")
    @PreAuthorize("@ss.hasPermission('system:poi-report:update')")
    public CommonResult<Boolean> updateReport(@Valid @RequestBody ReportSaveReqVO updateReqVO) {
        reportService.updateReport(updateReqVO);
        return success(true);
    }


    @PutMapping("/directly-update")
    @Operation(summary = "更新打印模板")
    @PreAuthorize("@ss.hasPermission('system:poi-report:directly-update')")
    public CommonResult<Boolean> changeReport(@Valid @RequestBody ReportChangeSaveReqVO updateReqVO) {
        reportService.directlyUpdateReport(updateReqVO);
        return success(true);
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除打印模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:poi-report:delete')")
    public CommonResult<Boolean> deleteReport(@RequestParam("id") Long id) {
        reportService.deleteReport(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得打印模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('system:poi-report:query')")
    public CommonResult<ReportInfoRespVO> getReport(@RequestParam("id") Long id) {
        ReportInfoRespVO report = reportService.getReport(new SystemReportDetailReq().setReportId(id));
        return success(report);
    }


    @GetMapping("/company-specific-report")
    @Operation(summary = "获得账套可选打印模板")
    @Parameter(name = "companyId", description = "账套id", required = true, example = "1024")
    @Parameter(name = "reportCode", description = "模板code", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('system:poi-report:query')")
    public CommonResult<List<ReportDO>> getCompanySpecificReport(@RequestParam("reportCode") String reportCode, @RequestParam("companyId") Long companyId) {
        List<ReportDO> report = reportService.getCompanySpecificReport(companyId,reportCode);
        return success(report);
    }


    @GetMapping("/audit-detail")
    @Operation(summary = "获得客户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<ReportInfoRespVO> getReportByProcessId(@RequestParam("id") String id) {
        ReportInfoRespVO report = reportService.getReport(new SystemReportDetailReq().setProcessInstanceId(id));
        return success(report);
    }

    @GetMapping("/page")
    @Operation(summary = "获得打印模板分页")
    @PreAuthorize("@ss.hasPermission('system:poi-report:query')")
    public CommonResult<PageResult<ReportRespVO>> getReportPage(@Valid ReportPageReqVO pageReqVO) {
        PageResult<ReportDO> pageResult = reportService.getReportPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReportRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出打印模板 Excel")
//    @PreAuthorize("@ss.hasPermission('system:poi-report:export')")
    @OperateLog(type = EXPORT)
    public void exportReportExcel(@Valid ReportPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReportDO> list = reportService.getReportPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "打印模板.xls", "数据", ReportRespVO.class,
                        BeanUtils.toBean(list, ReportRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        reportService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        reportService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('system:poi-report:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long reportId) {
        reportService.submitTask(reportId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @GetMapping("/test")
    @Operation(summary = "测试")
    public CommonResult test() {
        try{
            XWPFTemplate template = XWPFTemplate.compile("/Users/chengbo/Desktop/采购合同模板.docx").render(
                    new HashMap<String, Object>(){{
                        put("code", "20000000001");
                        put("venderName", "XXXXXXXXX工厂");
                    }});
            template.writeAndClose(new FileOutputStream("/Users/chengbo/Desktop/output.docx"));
        }catch(Exception e) {
            return error(1, "失败" + e.getMessage());
        }
        return success("成功");
    }






}