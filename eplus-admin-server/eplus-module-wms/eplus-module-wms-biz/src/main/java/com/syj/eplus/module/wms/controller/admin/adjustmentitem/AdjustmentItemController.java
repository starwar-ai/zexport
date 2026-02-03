package com.syj.eplus.module.wms.controller.admin.adjustmentitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import com.syj.eplus.module.wms.service.adjustment.AdjustmentItemService;
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

@Tag(name = "管理后台 - 仓储管理-盘库调整单-明细")
@RestController
@RequestMapping("/wms/adjustment-item")
@Validated
public class AdjustmentItemController {

    @Resource
    private AdjustmentItemService adjustmentItemService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-盘库调整单-明细")
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:create')")
    public CommonResult<Long> createAdjustmentItem(@Valid @RequestBody AdjustmentItemSaveReqVO createReqVO) {
        return success(adjustmentItemService.createAdjustmentItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-盘库调整单-明细")
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:update')")
    public CommonResult<Boolean> updateAdjustmentItem(@Valid @RequestBody AdjustmentItemSaveReqVO updateReqVO) {
        adjustmentItemService.updateAdjustmentItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-盘库调整单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:delete')")
    public CommonResult<Boolean> deleteAdjustmentItem(@RequestParam("id") Long id) {
        adjustmentItemService.deleteAdjustmentItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-盘库调整单-明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:query')")
    public CommonResult<AdjustmentItemRespVO> getAdjustmentItem(@RequestParam("id") Long id) {
        AdjustmentItemRespVO adjustmentItem = adjustmentItemService.getAdjustmentItem(id);
        return success(adjustmentItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-盘库调整单-明细分页")
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:query')")
    public CommonResult<PageResult<AdjustmentItemRespVO>> getAdjustmentItemPage(@Valid AdjustmentItemPageReqVO pageReqVO) {
        PageResult<AdjustmentItemDO> pageResult = adjustmentItemService.getAdjustmentItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdjustmentItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-盘库调整单-明细 Excel")
    @PreAuthorize("@ss.hasPermission('wms:adjustment-item:export')")
    @OperateLog(type = EXPORT)
    public void exportAdjustmentItemExcel(@Valid AdjustmentItemPageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdjustmentItemDO> list = adjustmentItemService.getAdjustmentItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-盘库调整单-明细.xls", "数据", AdjustmentItemRespVO.class,
                BeanUtils.toBean(list, AdjustmentItemRespVO.class));
    }


}
