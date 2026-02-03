package com.syj.eplus.module.pjms.controller.admin.project;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.pjms.controller.admin.project.vo.*;
import com.syj.eplus.module.pjms.service.project.ProjectService;
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

@Tag(name = "管理后台 - 项目")
@RestController
@RequestMapping("/pjms/project")
@Validated
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    @PreAuthorize("@ss.hasPermission('pjms:project:create')")
    public CommonResult<Long> createProject(@Valid @RequestBody ProjectSaveReqVO createReqVO) {
        return success(projectService.createProject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目")
    @PreAuthorize("@ss.hasPermission('pjms:project:update')")
    public CommonResult<Boolean> updateProject(@Valid @RequestBody ProjectSaveReqVO updateReqVO) {
        projectService.updateProject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pjms:project:delete')")
    public CommonResult<Boolean> deleteProject(@RequestParam("id") Long id) {
        projectService.deleteProject(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得项目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pjms:project:query')")
    public CommonResult<ProjectRespVO> getProject(@RequestParam("id") Long id) {
        ProjectRespVO project = projectService.getProject
                (new ProjectDetailReq().setProjectId(id));
        return success(project);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得项目详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <ProjectRespVO> getProjectByProcessId(@RequestParam("id")
                                                  String id) {
        ProjectRespVO project = projectService.getProject
                (new ProjectDetailReq().setProcessInstanceId(id));
        return success(project);
    }
    @GetMapping("/page")
    @Operation(summary = "获得项目分页")
    @PreAuthorize("@ss.hasPermission('pjms:project:query')")
    public CommonResult<PageResult<ProjectRespVO>> getProjectPage(@Valid ProjectPageReqVO pageReqVO) {
        PageResult<ProjectRespVO> pageResult = projectService.getProjectPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得项目精简列表分页")
    public CommonResult<PageResult<ProjectSimpleRespVO>> getSimpleProjectPage(@Valid ProjectPageReqVO pageReqVO) {
        PageResult<ProjectSimpleRespVO> pageResult = projectService.getSimpleProjectPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目 Excel")
    @PreAuthorize("@ss.hasPermission('pjms:project:export')")
    @OperateLog(type = EXPORT)
    public void exportProjectExcel(@Valid ProjectPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectRespVO> list = projectService.getProjectPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目.xls", "数据", ProjectRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('pjms:project:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        projectService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('pjms:project:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        projectService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('pjms:project:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        projectService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    @PostMapping("/finish")
    @Operation(summary = "作废项目")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "desc", description = "说明", required = true)
    @PreAuthorize("@ss.hasPermission('pjms:project:finish')")
    public CommonResult<Boolean> finishManufacture(@RequestParam("id") Long id) {
        projectService.finishManufacture(id);
        return success(true);
    }

    @PostMapping("/rollback-finish")
    @Operation(summary = "反作废项目")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pjms:project:rollback-finish')")
    public CommonResult<Boolean> rollbackFinishManufacture(@RequestParam("id") Long id) {
        projectService.rollbackFinishManufacture(id);
        return success(true);
    }

    @PostMapping("/done")
    @Operation(summary = "完成项目")
    @PreAuthorize("@ss.hasPermission('pjms:project:done')")
    public CommonResult<Boolean> doneManufacture(@Valid @RequestBody  ProjectDoneReqVO reqVo ) {
        projectService.doneManufacture(reqVo);
        return success(true);
    }

}