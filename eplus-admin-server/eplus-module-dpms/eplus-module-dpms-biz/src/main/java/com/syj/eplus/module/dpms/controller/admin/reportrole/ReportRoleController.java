package com.syj.eplus.module.dpms.controller.admin.reportrole;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.PermissionAssignRoleReportReqVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRolePageReqVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleRespVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;
import com.syj.eplus.module.dpms.service.reportrole.ReportRoleService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 报表角色关系表")
@RestController
@RequestMapping("/dpms/report-role")
@Validated
public class ReportRoleController {

    @Resource
    private ReportRoleService reportRoleService;

    @PostMapping("/create")
    @Operation(summary = "创建报表角色关系表")
    @PreAuthorize("@ss.hasPermission('dpms:report-role:create')")
    public CommonResult<Long> createReportRole(@Valid @RequestBody ReportRoleSaveReqVO createReqVO) {
        return success(reportRoleService.createReportRole(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报表角色关系表")
    @PreAuthorize("@ss.hasPermission('dpms:report-role:update')")
    public CommonResult<Boolean> updateReportRole(@Valid @RequestBody ReportRoleSaveReqVO updateReqVO) {
        reportRoleService.updateReportRole(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报表角色关系表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dpms:report-role:delete')")
    public CommonResult<Boolean> deleteReportRole(@RequestParam("id") Long id) {
        reportRoleService.deleteReportRole(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得报表角色关系表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dpms:report-role:query')")
    public CommonResult<ReportRoleRespVO> getReportRole(@RequestParam("id") Long id) {
            ReportRoleRespVO reportRole = reportRoleService.getReportRole
            (id);
            return success(reportRole);
    }
    @GetMapping("/page")
    @Operation(summary = "获得报表角色关系表分页")
    @PreAuthorize("@ss.hasPermission('dpms:report-role:query')")
    public CommonResult<PageResult<ReportRoleRespVO>> getReportRolePage(@Valid ReportRolePageReqVO pageReqVO) {
        PageResult<ReportRoleDO> pageResult = reportRoleService.getReportRolePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReportRoleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报表角色关系表 Excel")
    @PreAuthorize("@ss.hasPermission('dpms:report-role:export')")
    @OperateLog(type = EXPORT)
    public void exportReportRoleExcel(@Valid ReportRolePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReportRoleDO> list = reportRoleService.getReportRolePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "报表角色关系表.xls", "数据", ReportRoleRespVO.class,
                        BeanUtils.toBean(list, ReportRoleRespVO.class));
    }

    @PostMapping("/assign-role-card")
    @Operation(summary = "赋予角色报表")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-report')")
    public CommonResult<Boolean> assignRoleReport(@Validated @RequestBody PermissionAssignRoleReportReqVO reqVO) {
        // 执行报表的分配
        reportRoleService.assignRoleReport(reqVO.getRoleId(), reqVO.getReportIds());
        return success(true);
    }
}