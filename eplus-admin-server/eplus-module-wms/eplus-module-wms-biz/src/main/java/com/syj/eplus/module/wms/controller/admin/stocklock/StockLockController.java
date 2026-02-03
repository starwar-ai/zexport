package com.syj.eplus.module.wms.controller.admin.stocklock;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import com.syj.eplus.module.wms.service.stocklock.StockLockService;
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

@Tag(name = "管理后台 - 仓储管理-库存明细-锁定库存")
@RestController
@RequestMapping("/wms/stock-lock")
@Validated
public class StockLockController {

    @Resource
    private StockLockService stockLockService;

    @Resource
    private IStockApi stockApi;

    @PostMapping("/create")
    @Operation(summary = "锁定库存")
    public CommonResult<Long> createStockLock(@Valid @RequestBody StockLockSaveReqVO createReqVO) {
        return success(stockLockService.createStockLock(createReqVO));
    }

    @PostMapping("/createBatch")
    @Operation(summary = "锁定库存-批量")
    public CommonResult<Boolean> createBatch(@Valid @RequestBody List<StockLockSaveReqVO> stockLockSaveReqVOList) {
        return success(stockApi.batchLockStock(stockLockSaveReqVOList));
    }

    @GetMapping("/cancel")
    @Operation(summary = "释放库存")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> cancel(@RequestParam("id") Long id) {
        Boolean flag = stockLockService.cancel(id);
        return success(flag);
    }

    @GetMapping("/cancelBatch")
    @Operation(summary = "释放库存-批量")
    public CommonResult<Boolean> cancelBatch(@RequestParam(value = "saleContractCode",required = false) String saleContractCode,
                                             @RequestParam(value = "sourceOrderType",required = false)Integer sourceOrderType) {
        Boolean flag = stockApi.cancelStockLock(saleContractCode,null,sourceOrderType);
        return success(flag);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-库存明细-锁定库存")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stock-lock:delete')")
    public CommonResult<Boolean> deleteStockLock(@RequestParam("id") Long id) {
        stockLockService.deleteStockLock(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-库存明细-锁定库存")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stock-lock:query')")
    public CommonResult<StockLockRespVO> getStockLock(@RequestParam("id") Long id) {
        StockLockRespVO stockLock = stockLockService.getStockLock(id);
        return success(stockLock);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-库存明细-锁定库存分页")
    @PreAuthorize("@ss.hasPermission('wms:stock-lock:query')")
    public CommonResult<PageResult<StockLockRespVO>> getStockLockPage(@Valid StockLockPageReqVO pageReqVO) {
        PageResult<StockLockDO> pageResult = stockLockService.getStockLockPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StockLockRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得仓储管理-库存明细-锁定库存")
    public CommonResult<List<StockLockRespVO>> list(@Valid StockLockPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StockLockDO> list = stockLockService.getStockLockPage(pageReqVO).getList();
        return success(BeanUtils.toBean(list, StockLockRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-库存明细-锁定库存 Excel")
    @PreAuthorize("@ss.hasPermission('wms:stock-lock:export')")
    @OperateLog(type = EXPORT)
    public void exportStockLockExcel(@Valid StockLockPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StockLockDO> list = stockLockService.getStockLockPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-库存明细-锁定库存.xls", "数据", StockLockRespVO.class,
                BeanUtils.toBean(list, StockLockRespVO.class));
    }


}
