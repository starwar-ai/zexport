package com.syj.eplus.module.scm.controller.admin.purchaseplanitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.service.purchaseplanitem.PurchasePlanItemService;
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

@Tag(name = "管理后台 - 采购计划明细")
@RestController
@RequestMapping("/scm/purchase-plan-item")
@Validated
public class PurchasePlanItemController {

    @Resource
    private PurchasePlanItemService purchasePlanItemService;

    @PostMapping("/create")
    @Operation(summary = "创建采购计划明细")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:create')")
    public CommonResult<Long> createPurchasePlanItem(@Valid @RequestBody PurchasePlanItemSaveReqVO createReqVO) {
        return success(purchasePlanItemService.createPurchasePlanItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购计划明细")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:update')")
    public CommonResult<Boolean> updatePurchasePlanItem(@Valid @RequestBody PurchasePlanItemSaveReqVO updateReqVO) {
        purchasePlanItemService.updatePurchasePlanItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购计划明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:delete')")
    public CommonResult<Boolean> deletePurchasePlanItem(@RequestParam("id") Long id) {
        purchasePlanItemService.deletePurchasePlanItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得采购计划明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:query')")
    public CommonResult<PurchasePlanItemRespVO> getPurchasePlanItem(@RequestParam("id") Long id) {
        PurchasePlanItemRespVO purchasePlanItem = purchasePlanItemService.getPurchasePlanItem
                (new PurchasePlanItemDetailReq().setPurchasePlanItemId(id));
        return success(purchasePlanItem);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购计划明细详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PurchasePlanItemRespVO> getPurchasePlanItemByProcessId(@RequestParam("id")
                                                                    String id) {
        PurchasePlanItemRespVO purchasePlanItem = purchasePlanItemService.getPurchasePlanItem
                (new PurchasePlanItemDetailReq().setProcessInstanceId(id));
        return success(purchasePlanItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购计划明细分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:query')")
    public CommonResult<PageResult<PurchasePlanItemRespVO>> getPurchasePlanItemPage(@Valid PurchasePlanItemPageReqVO pageReqVO) {
        PageResult<PurchasePlanItemDO> pageResult = purchasePlanItemService.getPurchasePlanItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchasePlanItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购计划明细 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchasePlanItemExcel(@Valid PurchasePlanItemPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchasePlanItemDO> list = purchasePlanItemService.getPurchasePlanItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购计划明细.xls", "数据", PurchasePlanItemRespVO.class,
                BeanUtils.toBean(list, PurchasePlanItemRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchasePlanItemService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchasePlanItemService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan-item:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        purchasePlanItemService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


}
