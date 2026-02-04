package com.syj.eplus.module.wms.controller.admin.stock;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.api.stock.dto.QueryStockReqVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.*;
import com.syj.eplus.module.wms.service.stock.StockService;
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

@Tag(name = "管理后台 - 仓储管理-库存明细")
@RestController
@RequestMapping("/wms/stock")
@Validated
public class StockController {

    @Resource
    private StockService stockService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-库存明细")
    @PreAuthorize("@ss.hasPermission('wms:stock:create')")
    public CommonResult<Long> createStock(@Valid @RequestBody StockSaveReqVO createReqVO) {
        return success(stockService.createStock(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-库存明细")
    @PreAuthorize("@ss.hasPermission('wms:stock:update')")
    public CommonResult<Boolean> updateStock(@Valid @RequestBody StockSaveReqVO updateReqVO) {
        stockService.updateStock(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-库存明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stock:delete')")
    public CommonResult<Boolean> deleteStock(@RequestParam("id") Long id) {
        stockService.deleteStock(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-库存明细")
    @Parameter(name = "skuId", description = "sku编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stock:query')")
    public CommonResult<StockRespVO> getStock(@RequestParam("skuId") Long skuId) {
        StockRespVO stock = stockService.getStock(skuId);
        return success(stock);
    }

//    @GetMapping("/page")
//    @Operation(summary = "获得仓储管理-库存明细分页")
//    @PreAuthorize("@ss.hasPermission('wms:stock:query')")
//    public CommonResult<PageResult<StockRespVO>> getStockPage(@Valid StockPageReqVO pageReqVO) {
//        return success(stockService.getStockPage(pageReqVO));
//    }
//
//    @GetMapping("/listPage")
//    @Operation(summary = "获得仓储管理-库存明细分页")
//    @PreAuthorize("@ss.hasPermission('wms:stock:query')")
//    public CommonResult<PageResult<StockRespVO>> listPage(@Valid @RequestBody StockPageReqVO pageReqVO) {
//        return success(stockService.listPage(pageReqVO));
//    }


    @GetMapping("/pageBySku")
    @Operation(summary = "获得仓储管理产品-库存明细分页")
    @PreAuthorize("@ss.hasPermission('wms:stock:query')")
    public CommonResult<PageResult<StockRespVO>> listPageBySku(@Valid StockPageReqVO pageReqVO) {
        return success(stockService.listPageBySku(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-库存明细 Excel")
    @PreAuthorize("@ss.hasPermission('wms:stock:export')")
    @OperateLog(type = EXPORT)
    public void exportStockExcel(@Valid StockPageReqVO pageReqVO,
                                 HttpServletResponse response) throws IOException {
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-库存明细.xls", "数据", StockRespVO.class,
                stockService.getStockPage(pageReqVO).getList());
    }

    @PostMapping("/queryBatch")
    @Operation(summary = "条件查询批次库存信息(盘点)")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:query')")
    public CommonResult<PageResult<StockDetailRespVO>> queryBatch(@Valid @RequestBody StockQueryVO stockQueryVO) {
        return success(stockService.queryBatch(stockQueryVO));
    }

    @PostMapping("/queryTotalStock")
    @Operation(summary = "批量查询可用库存")
    public CommonResult queryTotalStock(@Valid @RequestBody List<QueryStockReqVO> queryStockReqVOList) {
        return success(stockService.queryTotalStock(queryStockReqVOList));
    }

    @PostMapping("/listBatch")
    @Operation(summary = "根据产品编码查询批次库存信息")
    public CommonResult<List<StockDetailRespVO>> listBatch(@Valid @RequestBody QueryStockReqVO stockForSaleQueryVO) {
        return success(stockService.listBatch(stockForSaleQueryVO));
    }

    @PostMapping("/import")
    @Parameter(name = "importCode", description = "导入编号", required = true, example = "1024")
    @Operation(summary = "导入的excel保存库存表")
    public CommonResult<Boolean> importByExcel(@RequestParam("importCode") String importCode) throws ServiceException {
        stockService.importStock(importCode);
        return success(true);
    }

    @GetMapping("/get-simple-stock")
    @Operation(summary = "获得库存产品信息")
    public CommonResult<PageResult<SimpleStockResp>> getSimpleStock(@Valid StockPageReqVO pageReqVO) {
        return success(stockService.getSimpleStock(pageReqVO));
    }
}
