package com.syj.eplus.module.scm.controller.admin.purchasecontractitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
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

@Tag(name = "管理后台 - 采购合同明细")
@RestController
@RequestMapping("/scm/purchase-contract-item")
@Validated
public class PurchaseContractItemController {

    @Resource
    private PurchaseContractItemService purchaseContractItemService;

    @PostMapping("/create")
    @Operation(summary = "创建采购合同明细")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:create')")
    public CommonResult<Long> createPurchaseContractItem(@Valid @RequestBody PurchaseContractItemSaveReqVO createReqVO) {
        return success(purchaseContractItemService.createPurchaseContractItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购合同明细")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:update')")
    public CommonResult<Boolean> updatePurchaseContractItem(@Valid @RequestBody PurchaseContractItemSaveReqVO updateReqVO) {
        purchaseContractItemService.updatePurchaseContractItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购合同明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:delete')")
    public CommonResult<Boolean> deletePurchaseContractItem(@RequestParam("id") Long id) {
        purchaseContractItemService.deletePurchaseContractItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得采购合同明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:query')")
    public CommonResult<PurchaseContractItemRespVO> getPurchaseContractItem(@RequestParam("id") Long id) {
        PurchaseContractItemRespVO purchaseContractItem = purchaseContractItemService.getPurchaseContractItem
                (new PurchaseContractItemDetailReq().setPurchaseContractItemId(id));
        return success(purchaseContractItem);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购合同明细详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PurchaseContractItemRespVO> getPurchaseContractItemByProcessId(@RequestParam("id")
                                                                            String id) {
        PurchaseContractItemRespVO purchaseContractItem = purchaseContractItemService.getPurchaseContractItem
                (new PurchaseContractItemDetailReq().setProcessInstanceId(id));
        return success(purchaseContractItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购合同明细分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:query')")
    public CommonResult<PageResult<PurchaseContractItemRespVO>> getPurchaseContractItemPage(@Valid PurchaseContractGetItemPageReqDTO pageReqVO) {
        PageResult<PurchaseContractItemDO> pageResult = purchaseContractItemService.getPurchaseContractItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchaseContractItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购合同明细 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchaseContractItemExcel(@Valid PurchaseContractGetItemPageReqDTO pageReqVO,
                                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchaseContractItemDO> list = purchaseContractItemService.getPurchaseContractItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购合同明细.xls", "数据", PurchaseContractItemRespVO.class,
                BeanUtils.toBean(list, PurchaseContractItemRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchaseContractItemService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchaseContractItemService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-item:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        purchaseContractItemService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

}