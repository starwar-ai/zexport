package com.syj.eplus.module.wms.controller.admin.stocktakeitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import com.syj.eplus.module.wms.service.stocktake.StocktakeItemService;
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

@Tag(name = "管理后台 - 仓储管理-盘点单-明细")
@RestController
@RequestMapping("/wms/stocktake-item")
@Validated
public class StocktakeItemController {

    @Resource
    private StocktakeItemService stocktakeItemService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-盘点单-明细")
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:create')")
    public CommonResult<Long> createStocktakeItem(@Valid @RequestBody StocktakeItemSaveReqVO createReqVO) {
        return success(stocktakeItemService.createStocktakeItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-盘点单-明细")
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:update')")
    public CommonResult<Boolean> updateStocktakeItem(@Valid @RequestBody StocktakeItemSaveReqVO updateReqVO) {
        stocktakeItemService.updateStocktakeItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-盘点单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:delete')")
    public CommonResult<Boolean> deleteStocktakeItem(@RequestParam("id") Long id) {
        stocktakeItemService.deleteStocktakeItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-盘点单-明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:query')")
    public CommonResult<StocktakeItemRespVO> getStocktakeItem(@RequestParam("id") Long id) {
        StocktakeItemRespVO stocktakeItem = stocktakeItemService.getStocktakeItem(id);
        return success(stocktakeItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-盘点单-明细分页")
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:query')")
    public CommonResult<PageResult<StocktakeItemRespVO>> getStocktakeItemPage(@Valid StocktakeItemPageReqVO pageReqVO) {
        PageResult<StocktakeItemDO> pageResult = stocktakeItemService.getStocktakeItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StocktakeItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-盘点单-明细 Excel")
    @PreAuthorize("@ss.hasPermission('wms:stocktake-item:export')")
    @OperateLog(type = EXPORT)
    public void exportStocktakeItemExcel(@Valid StocktakeItemPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StocktakeItemDO> list = stocktakeItemService.getStocktakeItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-盘点单-明细.xls", "数据", StocktakeItemRespVO.class,
                BeanUtils.toBean(list, StocktakeItemRespVO.class));
    }


}
