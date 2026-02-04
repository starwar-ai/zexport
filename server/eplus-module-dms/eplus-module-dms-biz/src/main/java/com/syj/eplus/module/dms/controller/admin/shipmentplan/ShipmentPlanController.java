package com.syj.eplus.module.dms.controller.admin.shipmentplan;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.*;
import com.syj.eplus.module.dms.service.shipmentplan.ShipmentPlanService;
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

@Tag(name = "管理后台 - 出运计划单")
@RestController
@RequestMapping("/dms/shipment-plan")
@Validated
public class ShipmentPlanController {

    @Resource
    private ShipmentPlanService shipmentPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建出运计划单", description = "新增")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:create','sms:export-sale-contract:to-shipment-plan')")
    public CommonResult<List<CreatedResponse>> createShipmentPlan(@Valid @RequestBody ShipmentPlanSaveReqVO createReqVO) {
        return success(shipmentPlanService.createShipmentPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出运计划单")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:update')")
    public CommonResult<Boolean> updateShipmentPlan(@Valid @RequestBody ShipmentPlanSaveReqVO updateReqVO) {
        shipmentPlanService.updateShipmentPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出运计划单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:delete')")
    public CommonResult<Boolean> deleteShipmentPlan(@RequestParam("id") Long id) {
        shipmentPlanService.deleteShipmentPlan(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得出运计划单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:query')")
    public CommonResult<ShipmentPlanRespVO> getShipmentPlan(@RequestParam("id") Long id) {
        ShipmentPlanRespVO shipmentPlan = shipmentPlanService.getShipmentPlan(id, false);
        return success(shipmentPlan);
    }

    @GetMapping("/update-detail")
    @Operation(summary = "获取编辑中间页")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:update')")
    public CommonResult<ShipmentPlanRespVO> getShipmentPlanUpdateDetail(@RequestParam("id") Long id) {
        ShipmentPlanRespVO shipmentPlan = shipmentPlanService.getShipmentPlan(id, true);
        return success(shipmentPlan);
    }

    @GetMapping("/page")
    @Operation(summary = "获得出运计划单分页")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:query')")
    public CommonResult<PageResult<ShipmentPlanRespVO>> getShipmentPlanPage(@Valid ShipmentPlanPageReqVO pageReqVO) {
        PageResult<ShipmentPlanRespVO> pageResult = shipmentPlanService.getShipmentPlanPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出出运计划单 Excel")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:export')")
    @OperateLog(type = EXPORT)
    public void exportShipmentPlanExcel(@Valid ShipmentPlanPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ShipmentPlanRespVO> list = shipmentPlanService.getShipmentPlanPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出运计划单.xls", "数据", ShipmentPlanRespVO.class, list);
    }

    @GetMapping("/export-detail-excel")
    @Operation(summary = "导出出运单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })
    @OperateLog(type = EXPORT)
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:detail-export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        shipmentPlanService.exportExcel(id,reportCode,response);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('dms:shipment-plan:close')")
    public CommonResult<Boolean> closeShipmentPlan(@RequestBody CloseShipmentPlanReq closeShipmentPlanReq) {
        shipmentPlanService.closeShipmentPlan(closeShipmentPlanReq);
        return success(true);
    }

    @GetMapping("/related-num")
    @Operation(summary = "关联单据数量")
    public CommonResult<RelatedShipmentPlanRespVO> getRelatedNum(@RequestParam Long id) {
        return success(shipmentPlanService.getRelatedNum(id));
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('crm:shipment-plan:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long planId) {
        return success(shipmentPlanService.antiAudit(planId));
    }
}