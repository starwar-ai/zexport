package com.syj.eplus.module.wms.controller.admin.adjustment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentDetailReq;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentDO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import com.syj.eplus.module.wms.service.adjustment.AdjustmentService;
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

@Tag(name = "管理后台 - 仓储管理-盘库调整单")
@RestController
@RequestMapping("/wms/adjustment")
@Validated
public class AdjustmentController {

    @Resource
    private AdjustmentService adjustmentService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-盘库调整单")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:create')")
    public CommonResult<Long> createAdjustment(@Valid @RequestBody AdjustmentSaveReqVO createReqVO) {
        return success(adjustmentService.createAdjustment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-盘库调整单")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:update')")
    public CommonResult<Boolean> updateAdjustment(@Valid @RequestBody AdjustmentSaveReqVO updateReqVO) {
        adjustmentService.updateAdjustment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-盘库调整单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:adjustment:delete')")
    public CommonResult<Boolean> deleteAdjustment(@RequestParam("id") Long id) {
        adjustmentService.deleteAdjustment(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-盘库调整单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:query')")
    public CommonResult<AdjustmentRespVO> getAdjustment(@RequestParam("id") Long id) {
        AdjustmentRespVO adjustment = adjustmentService.getAdjustment(new AdjustmentDetailReq().setAdjustmentId(id));
        return success(adjustment);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-盘库调整单分页")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:query')")
    public CommonResult<PageResult<AdjustmentRespVO>> getAdjustmentPage(@Valid AdjustmentPageReqVO pageReqVO) {
        PageResult<AdjustmentDO> pageResult = adjustmentService.getAdjustmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdjustmentRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-盘库调整单 Excel")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:export')")
    @OperateLog(type = EXPORT)
    public void exportAdjustmentExcel(@Valid AdjustmentPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdjustmentDO> list = adjustmentService.getAdjustmentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-盘库调整单.xls", "数据", AdjustmentRespVO.class,
                BeanUtils.toBean(list, AdjustmentRespVO.class));
    }

    // ==================== 子表（仓储管理-盘库调整单-明细） ====================

    @GetMapping("/adjustment-item/page")
    @Operation(summary = "获得仓储管理-盘库调整单-明细分页")
    @Parameter(name = "adjustmentId", description = "调整单主键")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:query')")
    public CommonResult<PageResult<AdjustmentItemDO>> getAdjustmentItemPage(PageParam pageReqVO,
                                                                            @RequestParam("adjustmentId") Long adjustmentId) {
        return success(adjustmentService.getAdjustmentItemPage(pageReqVO, adjustmentId));
    }

    @PostMapping("/adjustment-item/create")
    @Operation(summary = "创建仓储管理-盘库调整单-明细")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:create')")
    public CommonResult<Long> createAdjustmentItem(@Valid @RequestBody AdjustmentItemDO adjustmentItem) {
        return success(adjustmentService.createAdjustmentItem(adjustmentItem));
    }

    @PutMapping("/adjustment-item/update")
    @Operation(summary = "更新仓储管理-盘库调整单-明细")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:update')")
    public CommonResult<Boolean> updateAdjustmentItem(@Valid @RequestBody AdjustmentItemDO adjustmentItem) {
        adjustmentService.updateAdjustmentItem(adjustmentItem);
        return success(true);
    }

    @DeleteMapping("/adjustment-item/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除仓储管理-盘库调整单-明细")
    @PreAuthorize("@ss.hasPermission('wms:adjustment:delete')")
    public CommonResult<Boolean> deleteAdjustmentItem(@RequestParam("id") Long id) {
        adjustmentService.deleteAdjustmentItem(id);
        return success(true);
    }

    @GetMapping("/adjustment-item/get")
    @Operation(summary = "获得仓储管理-盘库调整单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:adjustment:query')")
    public CommonResult<AdjustmentItemDO> getAdjustmentItem(@RequestParam("id") Long id) {
        return success(adjustmentService.getAdjustmentItem(id));
    }

}
