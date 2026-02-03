package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import com.syj.eplus.module.scm.service.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationService;
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

@Tag(name = "管理后台 - 采购合同辅料分摊")
@RestController
@RequestMapping("/scm/purchase-auxiliary-allocation")
@Validated
public class PurchaseAuxiliaryAllocationController {

    @Resource
    private PurchaseAuxiliaryAllocationService purchaseAuxiliaryAllocationService;

    @PostMapping("/create")
    @Operation(summary = "创建采购合同辅料分摊")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:create')")
    public CommonResult<Long> createPurchaseAuxiliaryAllocation(@Valid @RequestBody PurchaseAuxiliaryAllocationSaveReqVO createReqVO) {
        return success(purchaseAuxiliaryAllocationService.createPurchaseAuxiliaryAllocation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购合同辅料分摊")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:update')")
    public CommonResult<Boolean> updatePurchaseAuxiliaryAllocation(@Valid @RequestBody PurchaseAuxiliaryAllocationSaveReqVO updateReqVO) {
        purchaseAuxiliaryAllocationService.updatePurchaseAuxiliaryAllocation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购合同辅料分摊")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:delete')")
    public CommonResult<Boolean> deletePurchaseAuxiliaryAllocation(@RequestParam("id") Long id) {
        purchaseAuxiliaryAllocationService.deletePurchaseAuxiliaryAllocation(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得采购合同辅料分摊")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:query')")
    public CommonResult<PurchaseAuxiliaryAllocationRespVO> getPurchaseAuxiliaryAllocation(@RequestParam("id") Long id) {
            PurchaseAuxiliaryAllocationRespVO purchaseAuxiliaryAllocation = purchaseAuxiliaryAllocationService.getPurchaseAuxiliaryAllocation
            (new PurchaseAuxiliaryAllocationDetailReq().setPurchaseAuxiliaryAllocationId(id));
            return success(purchaseAuxiliaryAllocation);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购合同辅料分摊详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PurchaseAuxiliaryAllocationRespVO> getPurchaseAuxiliaryAllocationByProcessId(@RequestParam("id")
                                                                                          String id) {
        PurchaseAuxiliaryAllocationRespVO purchaseAuxiliaryAllocation = purchaseAuxiliaryAllocationService.getPurchaseAuxiliaryAllocation
                (new PurchaseAuxiliaryAllocationDetailReq().setProcessInstanceId(id));
        return success(purchaseAuxiliaryAllocation);
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购合同辅料分摊分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:query')")
    public CommonResult<PageResult<PurchaseAuxiliaryAllocationRespVO>> getPurchaseAuxiliaryAllocationPage(@Valid PurchaseAuxiliaryAllocationPageReqVO pageReqVO) {
        PageResult<PurchaseAuxiliaryAllocationDO> pageResult = purchaseAuxiliaryAllocationService.getPurchaseAuxiliaryAllocationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchaseAuxiliaryAllocationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购合同辅料分摊 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchaseAuxiliaryAllocationExcel(@Valid PurchaseAuxiliaryAllocationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchaseAuxiliaryAllocationDO> list = purchaseAuxiliaryAllocationService.getPurchaseAuxiliaryAllocationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购合同辅料分摊.xls", "数据", PurchaseAuxiliaryAllocationRespVO.class,
                        BeanUtils.toBean(list, PurchaseAuxiliaryAllocationRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
    purchaseAuxiliaryAllocationService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
    return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
    purchaseAuxiliaryAllocationService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
    return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
    purchaseAuxiliaryAllocationService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
    return success(true);
    }

    @PutMapping("/update-allocation")
    @Operation(summary = "单独更新采购合同辅料分摊")
    @PreAuthorize("@ss.hasPermission('scm:purchase-auxiliary-allocation:update')")
    public CommonResult<Boolean> updatePurchaseAuxiliaryAllocationAllocation(@Valid @RequestBody PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO) {
        purchaseAuxiliaryAllocationService.updatePurchaseAuxiliaryAllocationAllocation(updateReqVO);
        return success(true);
    }


}