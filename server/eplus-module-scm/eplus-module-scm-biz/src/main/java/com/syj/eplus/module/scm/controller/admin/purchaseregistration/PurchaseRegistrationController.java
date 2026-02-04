package com.syj.eplus.module.scm.controller.admin.purchaseregistration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.*;
import com.syj.eplus.module.scm.service.purchaseregistration.PurchaseRegistrationService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 采购跟单登记")
@RestController
@RequestMapping("/scm/purchase-registration")
@Validated
public class PurchaseRegistrationController {

    @Resource
    private PurchaseRegistrationService purchaseRegistrationService;

    @PostMapping("/create")
    @Operation(summary = "创建采购跟单登记")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:create')")
    public CommonResult<Long> createPurchaseRegistration(@Valid @RequestBody PurchaseRegistrationSaveReqVO createReqVO) {
        return success(purchaseRegistrationService.createPurchaseRegistration(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购跟单登记")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:update')")
    public CommonResult<Boolean> updatePurchaseRegistration(@Valid @RequestBody PurchaseRegistrationSaveReqVO updateReqVO) {
        purchaseRegistrationService.updatePurchaseRegistration(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购跟单登记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:delete')")
    public CommonResult<Boolean> deletePurchaseRegistration(@RequestParam("id") Long id) {
        purchaseRegistrationService.deletePurchaseRegistration(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得采购跟单登记")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:query')")
    public CommonResult<PurchaseRegistrationRespVO> getPurchaseRegistration(@RequestParam("id") Long id) {
        PurchaseRegistrationRespVO purchaseRegistration = purchaseRegistrationService.getPurchaseRegistration
                (new PurchaseRegistrationDetailReq().setPurchaseRegistrationId(id));
        return success(purchaseRegistration);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购跟单登记详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PurchaseRegistrationRespVO> getPurchaseRegistrationByProcessId(@RequestParam("id")
                                                                            String id) {
        PurchaseRegistrationRespVO purchaseRegistration = purchaseRegistrationService.getPurchaseRegistration
                (new PurchaseRegistrationDetailReq().setProcessInstanceId(id));
        return success(purchaseRegistration);
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购跟单登记分页-单据")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:query')")
    public CommonResult<PageResult<PurchaseRegistrationRespVO>> getPurchaseRegistrationPage(@Valid PurchaseRegistrationPageReqVO pageReqVO) {
        PageResult<PurchaseRegistrationRespVO> pageResult = purchaseRegistrationService.getPurchaseRegistrationPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/item-page")
    @Operation(summary = "获得采购跟单登记分页-发票登记明细")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:query')")
    public CommonResult<PageResult<PurchaseRegistrationItemResp>> getPurchaseRegistrationItemPage(@Valid PurchaseRegistrationPageReqVO pageReqVO) {
        PageResult<PurchaseRegistrationItemResp> result = purchaseRegistrationService.getPurchaseRegistrationItemPage(pageReqVO);
        return success(result);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "发票登记导出 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchaseRegistrationExcel(@Valid PurchaseRegistrationPageReqVO pageReqVO,
                                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<PurchaseRegistrationRespVO> pageResult = purchaseRegistrationService.getPurchaseRegistrationPage(pageReqVO);
        if(!pageReqVO.getIsTree()){
            // 导出 Excel
            ExcelUtils.write(response, "发票登记-单据.xls", "数据", PurchaseRegistrationRespVO.class, pageResult.getList());
        }else{
            List<PurchaseRegistrationItemExportResp> PurchaseRegistrationItemRespList = new ArrayList<>();
            AtomicInteger sortIndex = new AtomicInteger(1);
            pageResult.getList().forEach(item->{
                AtomicInteger childsortIndex = new AtomicInteger(1);
                item.getChildren().forEach(cl->{
                    PurchaseRegistrationItemExportResp itemResp = BeanUtils.toBean(cl, PurchaseRegistrationItemExportResp.class);
                    itemResp.setVenderCode(item.getVenderCode());
                    itemResp.setVenderName(item.getVenderName());
                    itemResp.setInvoiceAmount(item.getInvoiceAmount());
                    itemResp.setInputDate(item.getInputDate());
                    itemResp.setAuditStatusString(BpmProcessInstanceResultEnum.getDescByResult(item.getAuditStatus()));
                    itemResp.setInvoiceCode(item.getInvoiceCode());
                    itemResp.setCurrency(item.getCurrency());
                    itemResp.setSortNum(sortIndex.getAndIncrement());
                    itemResp.setChildSortNum(childsortIndex.getAndIncrement());
                    itemResp.setInvoicPrice(NumberFormatUtil.formatAmount(itemResp.getInvoicPrice()));
                    itemResp.setInvoicedDate(item.getInvoicedDate());
                    if(Objects.nonNull(cl.getInvoicPrice()) && Objects.nonNull(cl.getInvoicNoticesQuantity())){
                        itemResp.setInvoiceAmount(NumberFormatUtil.formatAmount(NumberUtil.mul(itemResp.getInvoicPrice(),itemResp.getInvoicNoticesQuantity())));
                    }
                    if(Objects.nonNull(cl.getInvoicPrice()) && Objects.nonNull(cl.getInvoicThisQuantity())){
                        itemResp.setInvoicThisAmount(NumberFormatUtil.formatAmount(NumberUtil.mul(itemResp.getInvoicPrice(),itemResp.getInvoicThisQuantity())));
                    }
                    if(item.getInputUser()!=null){
                        itemResp.setInputUserName(item.getInputUser().getNickname());
                    }
                    if(Objects.nonNull(cl.getInvoicPrice()) && Objects.nonNull(itemResp.getInvoicThisAmount())){
                        itemResp.setTaxAmount(NumberFormatUtil.formatAmount(NumberUtil.mul(itemResp.getTaxRate(),itemResp.getInvoicThisAmount())));
                    }
                    itemResp.setTaxRate(NumberFormatUtil.formatRate(itemResp.getTaxRate()));
                    if(Objects.nonNull(cl.getInvoicPrice()) && Objects.nonNull(cl.getInvoicThisQuantity())){
                        itemResp.setInvoicThisPrice(NumberUtil.mul(cl.getInvoicPrice(),cl.getInvoicNoticesQuantity()));
                    }
                    PurchaseRegistrationItemRespList.add(itemResp);
                });
            });
            ExcelUtils.write(response, "发票登记-产品.xls", "数据", PurchaseRegistrationItemExportResp.class, PurchaseRegistrationItemRespList);
        }
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        if(CollUtil.isNotEmpty(reqVO.getIds())){
            reqVO.getIds().stream().distinct().forEach(id->{
                BpmTaskApproveReqDTO requestVO = new BpmTaskApproveReqDTO();
                requestVO.setId(id);
                requestVO.setCode(reqVO.getCode());
                requestVO.setReason(reqVO.getReason());
                purchaseRegistrationService.approveTask(WebFrameworkUtils.getLoginUserId(), requestVO);
            });
        }else{
            purchaseRegistrationService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        }
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        if(CollUtil.isNotEmpty(reqVO.getIds())){
            reqVO.getIds().forEach(id->{
                BpmTaskRejectReqDTO requestVO = new BpmTaskRejectReqDTO();
                requestVO.setId(id);
                requestVO.setReason(reqVO.getReason());
                purchaseRegistrationService.rejectTask(WebFrameworkUtils.getLoginUserId(), requestVO);
            });
        }else{
            purchaseRegistrationService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        }
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        purchaseRegistrationService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/batch-review")
    @Operation(summary = "批量复核")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:batch-review')")
    public CommonResult<Boolean> batchReviewPurchaseRegistration(@Valid @RequestBody ReviewRegistrationReq reviewReq) {
        purchaseRegistrationService.batchReviewPurchaseRegistration(reviewReq);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "作废",description = "作废")
    @PreAuthorize("@ss.hasPermission('scm:purchase-registration:close')")
    public CommonResult<Boolean> closePurchaseRegistration(@RequestParam("id") Long id) {
        purchaseRegistrationService.closePurchaseRegistration(id);
        return success(true);
    }
}