package com.syj.eplus.module.wms.controller.admin.stocktake;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeDetailReq;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import com.syj.eplus.module.wms.service.stocktake.StocktakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

@Tag(name = "管理后台 - 仓储管理-盘点单")
@RestController
@RequestMapping("/wms/stocktake")
@Validated
public class StocktakeController {

    @Resource
    private StocktakeService stocktakeService;

    @PostMapping("/create")
    @Operation(summary = "创建仓储管理-盘点单")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:create')")
    public CommonResult<Long> createStocktake(@Valid @RequestBody StocktakeSaveReqVO createReqVO) {
        return success(stocktakeService.createStocktake(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓储管理-盘点单")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:update')")
    public CommonResult<Boolean> updateStocktake(@Valid @RequestBody StocktakeSaveReqVO updateReqVO) {
        stocktakeService.updateStocktake(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓储管理-盘点单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stocktake:delete')")
    public CommonResult<Boolean> deleteStocktake(@RequestParam("id") Long id) {
        stocktakeService.deleteStocktake(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓储管理-盘点单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:query')")
    public CommonResult<StocktakeRespVO> getStocktake(@RequestParam("id") Long id) {
        StocktakeRespVO stocktake = stocktakeService.getStocktake(new StocktakeDetailReq().setStocktakeId(id));
        return success(stocktake);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得仓储管理-盘点单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<StocktakeRespVO> getStocktakeByProcessId(@RequestParam("id")String id) {
        StocktakeRespVO stocktake = stocktakeService.getStocktake(new StocktakeDetailReq().setProcessInstanceId(id));
        return success(stocktake);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓储管理-盘点单分页")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:query')")
    public CommonResult<PageResult<StocktakeRespVO>> getStocktakePage(@Valid StocktakePageReqVO pageReqVO) {
        PageResult<StocktakeRespVO> pageResult = stocktakeService.getStocktakePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓储管理-盘点单 Excel")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:export')")
    @OperateLog(type = EXPORT)
    public void exportStocktakeExcel(@Valid StocktakePageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StocktakeRespVO> list = stocktakeService.getStocktakePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓储管理-盘点单.xls", "数据", StocktakeRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        stocktakeService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        stocktakeService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        stocktakeService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    // ==================== 子表（仓储管理-盘点单-明细） ====================

    @GetMapping("/stocktake-item/page")
    @Operation(summary = "获得仓储管理-盘点单-明细分页")
    @Parameter(name = "stocktakeId", description = "盘点单-主键")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:query')")
    public CommonResult<PageResult<StocktakeItemDO>> getStocktakeItemPage(PageParam pageReqVO,
                                                                          @RequestParam("stocktakeId") Long stocktakeId) {
        return success(stocktakeService.getStocktakeItemPage(pageReqVO, stocktakeId));
    }

    @PostMapping("/stocktake-item/create")
    @Operation(summary = "创建仓储管理-盘点单-明细")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:create')")
    public CommonResult<Long> createStocktakeItem(@Valid @RequestBody StocktakeItemDO stocktakeItem) {
        return success(stocktakeService.createStocktakeItem(stocktakeItem));
    }

    @PutMapping("/stocktake-item/update")
    @Operation(summary = "更新仓储管理-盘点单-明细")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:update')")
    public CommonResult<Boolean> updateStocktakeItem(@Valid @RequestBody StocktakeItemDO stocktakeItem) {
        stocktakeService.updateStocktakeItem(stocktakeItem);
        return success(true);
    }

    @DeleteMapping("/stocktake-item/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除仓储管理-盘点单-明细")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:delete')")
    public CommonResult<Boolean> deleteStocktakeItem(@RequestParam("id") Long id) {
        stocktakeService.deleteStocktakeItem(id);
        return success(true);
    }

    @GetMapping("/stocktake-item/get")
    @Operation(summary = "获得仓储管理-盘点单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:stocktake:query')")
    public CommonResult<StocktakeItemDO> getStocktakeItem(@RequestParam("id") Long id) {
        return success(stocktakeService.getStocktakeItem(id));
    }

    @PutMapping("/counting")
    @Operation(summary = "开始盘点")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:counting')")
    public CommonResult<Boolean> counting(@Valid @RequestBody StocktakeSaveReqVO updateReqVO) {
        boolean result = stocktakeService.counting(updateReqVO);
        return success(result);
    }

    @PutMapping("/complete")
    @Operation(summary = "盘点录入")
    @PreAuthorize("@ss.hasPermission('wms:stocktake:complete')")
    public CommonResult<Boolean> complete(@Valid @RequestBody StocktakeSaveReqVO updateReqVO) {
        boolean result = stocktakeService.complete(updateReqVO);
        return success(result);
    }

    @GetMapping("/export-detail-excel")
    @Operation(summary = "导出Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('wms:stocktake:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        stocktakeService.exportExcel(id, reportCode,response);
    }
}
