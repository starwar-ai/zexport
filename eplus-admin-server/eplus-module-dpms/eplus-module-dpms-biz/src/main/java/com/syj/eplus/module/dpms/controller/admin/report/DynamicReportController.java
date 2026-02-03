package com.syj.eplus.module.dpms.controller.admin.report;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dpms.controller.admin.report.vo.BatchReportSaveReqVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportRespVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.report.ReportDO;
import com.syj.eplus.module.dpms.service.report.DynamicReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 报表配置")
@RestController
@RequestMapping("/dpms/report")
@Validated
public class DynamicReportController {

    @Resource
    private DynamicReportService dynamicReportService;

    @PostMapping("/create")
    @Operation(summary = "创建报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:create')")
    public CommonResult<Long> createReport(@Valid @RequestBody ReportSaveReqVO createReqVO) {
        return success(dynamicReportService.createReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:update')")
    public CommonResult<Boolean> updateReport(@Valid @RequestBody ReportSaveReqVO updateReqVO) {
        dynamicReportService.updateReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报表配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dpms:report:delete')")
    public CommonResult<Boolean> deleteReport(@RequestParam("id") Long id) {
        dynamicReportService.deleteReport(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得报表配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dpms:report:query')")
    public CommonResult<ReportRespVO> getReport(@RequestParam("id") Long id) {
        ReportRespVO report = dynamicReportService.getReport(id);
        return success(report);
    }

    @GetMapping("/page")
    @Operation(summary = "获得报表配置分页")
    @PreAuthorize("@ss.hasPermission('dpms:report:query')")
    public CommonResult<PageResult<ReportRespVO>> getReportPage(@Valid ReportPageReqVO pageReqVO) {
        PageResult<ReportDO> pageResult = dynamicReportService.getReportPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReportRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报表配置 Excel")
    @PreAuthorize("@ss.hasPermission('dpms:report:export')")
    @OperateLog(type = EXPORT)
    public void exportReportExcel(@Valid ReportPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReportDO> list = dynamicReportService.getReportPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "报表配置.xls", "数据", ReportRespVO.class,
                BeanUtils.toBean(list, ReportRespVO.class));
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:batch-create')")
    public CommonResult<Boolean> batchCreateReport(@RequestBody BatchReportSaveReqVO saveReqVO) {
        List<Long> reportIds = saveReqVO.getReportIds();
        if (Objects.isNull(reportIds)){
            throw exception(BAD_REQUEST);
        }
        return success(dynamicReportService.batchCreateReport(reportIds));
    }

    @GetMapping("/list")
    @Operation(summary = "获得报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:query')")
    public CommonResult<List<ReportRespVO>> getReportList() {
    return success(dynamicReportService.getReportList());
    }

    @GetMapping("/role-list")
    @Operation(summary = "获得角色报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:query')")
    public CommonResult<List<ReportRespVO>> getRoleReportList() {
        return success(dynamicReportService.getRoleReportList());
    }

    @PostMapping("/get-all-list")
    @Operation(summary = "获取全部报表配置")
    @PreAuthorize("@ss.hasPermission('dpms:report:query')")
    public CommonResult<List<ReportRespVO>> getAllReportList() {
        return success(dynamicReportService.getAllReportList());
    }

}