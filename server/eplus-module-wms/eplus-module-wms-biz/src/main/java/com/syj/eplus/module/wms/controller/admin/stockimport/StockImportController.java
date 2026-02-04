package com.syj.eplus.module.wms.controller.admin.stockimport;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.stockimport.vo.*;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;
import com.syj.eplus.module.wms.service.stockimport.StockImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 仓储管理-库存明细导入")
@RestController
@RequestMapping("/wms/stock-import")
@Validated
public class StockImportController {

    @Resource
    private StockImportService stockImportService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-库存明细导入")
    @PreAuthorize("@ss.hasPermission('wms:stock-import:create')")
    public CommonResult<Long> createStockImport(@Valid @RequestBody StockImportSaveReqVO createReqVO) {
        return success(stockImportService.createStockImport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-库存明细导入")
    @PreAuthorize("@ss.hasPermission('wms:stock-import:update')")
    public CommonResult<Boolean> updateStockImport(@Valid @RequestBody StockImportSaveReqVO updateReqVO) {
        stockImportService.updateStockImport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-库存明细导入")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stock-import:delete')")
    public CommonResult<Boolean> deleteStockImport(@RequestParam("id") Long id) {
        stockImportService.deleteStockImport(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-库存明细导入")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stock-import:query')")
    public CommonResult<StockImportRespVO> getStockImport(@RequestParam("id") Long id) {
        StockImportRespVO stockImport = stockImportService.getStockImport(id);
        return success(stockImport);
    }
    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-库存明细导入分页")
    @PreAuthorize("@ss.hasPermission('wms:stock-import:query')")
    public CommonResult<PageResult<StockImportRespVO>> getStockImportPage(@Valid StockImportPageReqVO pageReqVO) {
        PageResult<StockImportDO> pageResult = stockImportService.getStockImportPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StockImportRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-库存明细导入 Excel")
    @PreAuthorize("@ss.hasPermission('wms:stock-import:export')")
    @OperateLog(type = EXPORT)
    public void exportStockImportExcel(@Valid StockImportPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StockImportDO> list = stockImportService.getStockImportPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-库存明细导入.xls", "数据", StockImportRespVO.class,
                BeanUtils.toBean(list, StockImportRespVO.class));
    }


//    @Resource
//    private StockService stockService;

    @PostMapping("/import-excel")
    @Operation(summary = "导入Excel")
    @Parameters({  @Parameter(name = "file", description = "Excel 文件", required = true) })
    @PreAuthorize("@ss.hasPermission('wms:stock:import')")
    public CommonResult<StockImportShowRespVO> importExcel(@RequestParam("file") MultipartFile file,
               @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<StockImportExcelVO> list = ExcelUtils.read(file, StockImportExcelVO.class, 1);
        StockImportShowRespVO stockImportShowRespVO = stockImportService.importExcelNotInsert(list);
//        stockService.importStock(stockImportShowRespVO.getImportCode());  测试
        return success(stockImportShowRespVO);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "库存导入模板")
    public void importBankTemplate(HttpServletResponse response) throws IOException {
       List<StockImportExcelVO> importList = stockImportService.getTestImport();
        ExcelUtils.write(response, "库存导入模板.xlsx", "库存列表", StockImportExcelVO.class, importList);
    }
}