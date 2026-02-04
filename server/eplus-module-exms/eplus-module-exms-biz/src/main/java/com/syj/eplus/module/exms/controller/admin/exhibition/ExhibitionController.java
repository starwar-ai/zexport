package com.syj.eplus.module.exms.controller.admin.exhibition;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.exms.controller.admin.exhibition.vo.*;
import com.syj.eplus.module.exms.service.exhibition.ExhibitionService;
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

@Tag(name = "管理后台 - 展会")
@RestController
@RequestMapping("/exms/exhibition")
@Validated
public class ExhibitionController {

    @Resource
    private ExhibitionService exhibitionService;

    @PostMapping("/create")
    @Operation(summary = "创建展会")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:create')")
    public CommonResult<Long> createExhibition(@Valid @RequestBody ExhibitionSaveReqVO createReqVO) {
        return success(exhibitionService.createExhibition(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新展会")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:update')")
    public CommonResult<Boolean> updateExhibition(@Valid @RequestBody ExhibitionSaveReqVO updateReqVO) {
        exhibitionService.updateExhibition(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除展会")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('exms:exhibition:delete')")
    public CommonResult<Boolean> deleteExhibition(@RequestParam("id") Long id) {
        exhibitionService.deleteExhibition(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得展会")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:query')")
    public CommonResult<ExhibitionRespVO> getExhibition(@RequestParam("id") Long id) {
        ExhibitionRespVO exhibition = exhibitionService.getExhibition
                (new ExhibitionDetailReq().setExhibitionId(id));
        return success(exhibition);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得展会详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <ExhibitionRespVO> getExhibitionByProcessId(@RequestParam("id")
                                                        String id) {
        ExhibitionRespVO exhibition = exhibitionService.getExhibition
                (new ExhibitionDetailReq().setProcessInstanceId(id));
        return success(exhibition);
    }
    @GetMapping("/page")
    @Operation(summary = "获得展会分页")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:query')")
    public CommonResult<PageResult<ExhibitionRespVO>> getExhibitionPage(@Valid ExhibitionPageReqVO pageReqVO) {
        PageResult<ExhibitionRespVO> pageResult = exhibitionService.getExhibitionPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得展会精简列表分页")
    public CommonResult<PageResult<ExhibitionSimpleRespVO>> getSimpleExhibitionPage(@Valid ExhibitionPageReqVO pageReqVO) {
        PageResult<ExhibitionSimpleRespVO> pageResult = exhibitionService.getSimpleExhibitionPage(pageReqVO);
        return success(pageResult);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出展会 Excel")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:export')")
    @OperateLog(type = EXPORT)
    public void exportExhibitionExcel(@Valid ExhibitionPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExhibitionRespVO> list = exhibitionService.getExhibitionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "展会.xls", "数据", ExhibitionRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        exhibitionService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        exhibitionService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        exhibitionService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PostMapping("/finish")
    @Operation(summary = "作废展会")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "desc", description = "说明", required = true)
    @PreAuthorize("@ss.hasPermission('exms:exhibition:finish')")
    public CommonResult<Boolean> finishManufacture(@RequestParam("id") Long id) {
        exhibitionService.finishManufacture(id);
        return success(true);
    }

    @PostMapping("/rollback-finish")
    @Operation(summary = "反作废展会")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('exms:exhibition:rollback-finish')")
    public CommonResult<Boolean> rollbackFinishManufacture(@RequestParam("id") Long id) {
        exhibitionService.rollbackFinishManufacture(id);
        return success(true);
    }

    @PostMapping("/done")
    @Operation(summary = "完成展会")
    @PreAuthorize("@ss.hasPermission('exms:exhibition:done')")
    public CommonResult<Boolean> doneManufacture(@Valid @RequestBody  ExhibitionDoneReqVO reqVo ) {
        exhibitionService.doneManufacture(reqVo);
        return success(true);
    }

}
