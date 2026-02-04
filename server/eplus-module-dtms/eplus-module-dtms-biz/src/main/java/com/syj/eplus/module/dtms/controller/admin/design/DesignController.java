package com.syj.eplus.module.dtms.controller.admin.design;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignDetailReq;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignRespVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignSaveReqVO;
import com.syj.eplus.module.dtms.enums.DesignStatusEnum;
import com.syj.eplus.module.dtms.service.design.DesignService;
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

@Tag(name = "管理后台 - 设计-任务单")
@RestController
@RequestMapping("/dtms/design")
@Validated
public class DesignController {

    @Resource
    private DesignService designService;

    @PostMapping("/create")
    @Operation(summary = "创建设计-任务单")
    @PreAuthorize("@ss.hasPermission('dtms:design:create')")
    public CommonResult<Long> createDesign(@Valid @RequestBody DesignSaveReqVO createReqVO) {
        return success(designService.createDesign(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设计-任务单")
    @PreAuthorize("@ss.hasPermission('dtms:design:update')")
    public CommonResult<Boolean> updateDesign(@Valid @RequestBody DesignSaveReqVO updateReqVO) {
        designService.updateDesign(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设计-任务单")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteDesign(@RequestParam("id") Long id) {
        designService.deleteDesign(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得设计-任务单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    public CommonResult<DesignRespVO> getDesign(@RequestParam("id") Long id) {
        DesignRespVO design = designService.getDesign(new DesignDetailReq().setDesignId(id));
        return success(design);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得设计-任务单详情")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult <DesignRespVO> getDesignByProcessId(@RequestParam("id")String id) {
        DesignRespVO design = designService.getDesign(new DesignDetailReq().setProcessInstanceId(id));
        return success(design);
    }

    @GetMapping("/page")
    @Operation(summary = "获得设计-任务单分页")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    public CommonResult<PageResult<DesignRespVO>> getDesignPage(@Valid DesignPageReqVO pageReqVO) {
        pageReqVO.setClaimFlag(BooleanEnum.NO.getValue());
        PageResult<DesignRespVO> pageResult = designService.getDesignPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/claimPage")
    @Operation(summary = "获得设计-认领分页")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    public CommonResult<PageResult<DesignRespVO>> claimPage(@Valid DesignPageReqVO pageReqVO) {
        pageReqVO.setDesignStatus(DesignStatusEnum.IN_PROCESS.getValue());
        PageResult<DesignRespVO> pageResult = designService.getClaimDesignPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设计-任务单 Excel")
    @PreAuthorize("@ss.hasPermission('dtms:design:export')")
    @OperateLog(type = EXPORT)
    public void exportDesignExcel(@Valid DesignPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DesignRespVO> list = designService.getDesignPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设计-任务单.xls", "数据", DesignRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('dtms:design:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        designService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('dtms:design:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        designService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('dtms:design:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        designService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/cancleClaim")
    @Operation(summary = "取消认领")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> cancleClaim(@RequestParam("id") Long id) {
        designService.cancleClaim(id);
        return success(true);
    }
}
