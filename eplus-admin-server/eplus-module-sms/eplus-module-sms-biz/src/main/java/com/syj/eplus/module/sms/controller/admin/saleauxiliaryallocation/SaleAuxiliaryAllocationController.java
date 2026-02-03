package com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;
import com.syj.eplus.module.sms.service.saleauxiliaryallocation.SaleAuxiliaryAllocationService;
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

@Tag(name = "管理后台 - 销售合同辅料分摊")
@RestController
@RequestMapping("/sms/sale-auxiliary-allocation")
@Validated
public class SaleAuxiliaryAllocationController {

    @Resource
    private SaleAuxiliaryAllocationService saleAuxiliaryAllocationService;

    @PostMapping("/create")
    @Operation(summary = "创建销售合同辅料分摊")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:create')")
    public CommonResult<Long> createSaleAuxiliaryAllocation(@Valid @RequestBody SaleAuxiliaryAllocationSaveReqVO createReqVO) {
        return success(saleAuxiliaryAllocationService.createSaleAuxiliaryAllocation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新销售合同辅料分摊")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:update')")
    public CommonResult<Boolean> updateSaleAuxiliaryAllocation(@Valid @RequestBody SaleAuxiliaryAllocationSaveReqVO updateReqVO) {
        saleAuxiliaryAllocationService.updateSaleAuxiliaryAllocation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除销售合同辅料分摊")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:delete')")
    public CommonResult<Boolean> deleteSaleAuxiliaryAllocation(@RequestParam("id") Long id) {
        saleAuxiliaryAllocationService.deleteSaleAuxiliaryAllocation(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得销售合同辅料分摊")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:query')")
    public CommonResult<SaleAuxiliaryAllocationRespVO> getSaleAuxiliaryAllocation(@RequestParam("id") Long id) {
        SaleAuxiliaryAllocationRespVO saleAuxiliaryAllocation = saleAuxiliaryAllocationService.getSaleAuxiliaryAllocation
                (new SaleAuxiliaryAllocationDetailReq().setSaleAuxiliaryAllocationId(id));
        return success(saleAuxiliaryAllocation);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得销售合同辅料分摊详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <SaleAuxiliaryAllocationRespVO> getSaleAuxiliaryAllocationByProcessId(@RequestParam("id")
                                                                                  String id) {
        SaleAuxiliaryAllocationRespVO saleAuxiliaryAllocation = saleAuxiliaryAllocationService.getSaleAuxiliaryAllocation
                (new SaleAuxiliaryAllocationDetailReq().setProcessInstanceId(id));
        return success(saleAuxiliaryAllocation);
    }
    @GetMapping("/page")
    @Operation(summary = "获得销售合同辅料分摊分页")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:query')")
    public CommonResult<PageResult<SaleAuxiliaryAllocationRespVO>> getSaleAuxiliaryAllocationPage(@Valid SaleAuxiliaryAllocationPageReqVO pageReqVO) {
        PageResult<SaleAuxiliaryAllocationDO> pageResult = saleAuxiliaryAllocationService.getSaleAuxiliaryAllocationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SaleAuxiliaryAllocationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出销售合同辅料分摊 Excel")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:export')")
    @OperateLog(type = EXPORT)
    public void exportSaleAuxiliaryAllocationExcel(@Valid SaleAuxiliaryAllocationPageReqVO pageReqVO,
                                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SaleAuxiliaryAllocationDO> list = saleAuxiliaryAllocationService.getSaleAuxiliaryAllocationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "销售合同辅料分摊.xls", "数据", SaleAuxiliaryAllocationRespVO.class,
                BeanUtils.toBean(list, SaleAuxiliaryAllocationRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        saleAuxiliaryAllocationService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        saleAuxiliaryAllocationService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        saleAuxiliaryAllocationService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/allocation")
    @Operation(summary = "销售合同辅料分摊操作")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:allocation')")
    public CommonResult<Boolean> allocationSaleAuxiliaryAllocation(@Valid @RequestBody AllocationReqVO reqVO) {
        saleAuxiliaryAllocationService.allocationSaleAuxiliaryAllocation(reqVO);
        return success(true);
    }

    @PutMapping("/allocation-cancel")
    @Operation(summary = "取消销售合同辅料分摊")
    @Parameter(name = "itemId", description = "辅料采购合同明细id", required = true)
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:allocation')")
    public CommonResult<Boolean> cancelAllocationSaleAuxiliaryAllocation(@Valid Long itemId) {
        saleAuxiliaryAllocationService.cancelAllocationSaleAuxiliaryAllocation(itemId);
        return success(true);
    }

    @GetMapping("/allocation-detail")
    @Operation(summary = "查看分摊信息")
    @Parameter(name = "saleId", description = "销售合同主键", required = true, example = "1024")
    @Parameter(name = "purchaseItemId", description = "采购合同明细主键", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:sale-auxiliary-allocation:allocation')")
    public CommonResult<List<SaleAuxiliaryAllocationRespVO>> getAllocation(@RequestParam("saleId") Long saleId,@RequestParam("purchaseItemId") Long purchaseItemId) {
        List<SaleAuxiliaryAllocationRespVO> saleAuxiliaryAllocation = saleAuxiliaryAllocationService.getAllocation(saleId,purchaseItemId );
        return success(saleAuxiliaryAllocation);
    }

}