package com.syj.eplus.module.sms.controller.admin.salecontract;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.ConfirmFlagEnum;
import com.syj.eplus.framework.common.enums.SaleTabEnum;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.annotation.Obsolete;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

/**
 * @Description：外销接口
 * @Author：du
 * @Date：2024/7/4 18:58
 */

@Tag(name = "管理后台 - 外销合同")
@RestController
@RequestMapping("/sms/export/sale-contract")
@Validated
public class ExportSaleContractController {
    @Resource
    private SaleContractService saleContractService;

    @PostMapping("/create")
    @Operation(summary = "创建外销合同")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:create')")
    public CommonResult<List<CreatedResponse>> createSaleContract(@Valid @RequestBody SaleContractSaveReqVO createReqVO) {
        return success(saleContractService.createSaleContract(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新外销合同",description = "修改")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:update')")
    public CommonResult<Boolean> updateSaleContract(@Valid @RequestBody SaleContractSaveReqVO updateReqVO) {
        saleContractService.updateSaleContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除外销合同",description = "删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:delete')")
    public CommonResult<Boolean> deleteSaleContract(@RequestParam("id") Long id) {
        saleContractService.deleteSaleContract(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得外销合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:query')")
    public CommonResult<SaleContractRespVO> getSaleContract(@RequestParam("id") Long id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract(new SaleContractDetailReq().setSaleContractId(id), SaleTabEnum.SALE_DETAIL);
        return success(saleContract);
    }

    @GetMapping("/get-id-by-code")
    @Operation(summary = "通过编号获得外销合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:query')")
    public CommonResult<Long> getSaleContract(@RequestParam("code") String code) {
        return success(saleContractService.getIdByCode(code));
    }
    @GetMapping("/detail-fee")
    @Operation(summary = "获得外销合同订单费用")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:query')")
    public CommonResult<SaleContractRespVO> getSaleContractFee(@RequestParam("id") Long id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract
                (new SaleContractDetailReq().setSaleContractId(id), SaleTabEnum.SALE_FEE);
        return success(saleContract);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得外销合同详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SaleContractRespVO> getSaleContractByProcessId(@RequestParam("id") String id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract(new SaleContractDetailReq().setProcessInstanceId(id),SaleTabEnum.SALE_DETAIL);
        return success(saleContract);
    }

    @GetMapping("/audit-detail-fee")
    @Operation(summary = "获得外销合同订单费用")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SaleContractRespVO> getSaleContractFeeByProcessId(@RequestParam("id") String id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract(new SaleContractDetailReq().setProcessInstanceId(id),SaleTabEnum.SALE_FEE);
        return success(saleContract);
    }
    @GetMapping("/page")
    @Operation(summary = "获得外销合同分页")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:query')")
    public CommonResult<PageResult<?>> getSaleContractPage(@Valid SaleContractPageReqVO pageReqVO) {
        // 根据 queryMode 分支处理：1-单据模式（按合同分页），2-产品模式（按明细分页）
        Integer queryMode = pageReqVO.getQueryMode();
        if (queryMode != null && queryMode == 2) {
            // 产品模式：按明细分页，返回扁平结构
            PageResult<SaleContractProductModeRespVO> pageResult = saleContractService.getProductModePage(pageReqVO, false);
            return success(pageResult);
        } else {
            // 单据模式（默认）：按合同分页，返回树形结构
            PageResult<SaleContractRespVO> pageResult = saleContractService.getSaleContractPage(pageReqVO, false);
            return success(pageResult);
        }
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出外销合同 Excel")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:export')")
    @OperateLog(type = EXPORT)
    public void exportSaleContractExcel(@Valid SaleContractPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        saleContractService.exportSaleContractExcel(pageReqVO, "销售合同", response);
    }

    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出外销合同 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "exportType", required = false, description = "导出类型", example = "1024"),
    })
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("exportType") String exportType,
            HttpServletResponse response) throws IOException {
        // 单个导出复用批量导出方法
        saleContractService.exportExcel(Collections.singletonList(id), exportType, reportCode, response);
    }

    @PostMapping("/batch-export-excel")
    @Operation(summary = "批量导出外销合同 Excel")
    @Parameters({
            @Parameter(name = "ids", required = true, description = "外销合同ID列表", example = "[1024, 1025]"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "exportType", required = false, description = "导出类型", example = "1024"),
    })
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:export')")
    @OperateLog(type = EXPORT)
    public void batchExportExcel(
            @RequestBody List<Long> ids,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "exportType", required = false) String exportType,
            HttpServletResponse response) throws IOException {
        saleContractService.exportExcel(ids, exportType, reportCode, response);
    }

    @PostMapping("/batch-print")
    @Operation(summary = "批量打印外销合同")
    @Parameters({
            @Parameter(name = "ids", required = true, description = "外销合同ID列表", example = "[1024, 1025]"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "export-sale-contract-detail"),
            @Parameter(name = "printType", required = false, description = "打印类型（domestic-内销，hsCode-海关编码打印，其他-外销/工厂）", example = "detail"),
    })
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:print')")
    @OperateLog(type = PRINT)
    public CommonResult<String> batchPrint(
            @RequestBody List<Long> ids,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "printType", required = false) String printType) {
        // 根据printType判断调用哪个打印方法（内部会合并数据）
        String pdfPath;
        if ("domestic".equals(printType)) {
            // 内销合同批量打印
            pdfPath = saleContractService.domesticPrint(ids, reportCode, null, null);
        } else {
            // 外销/工厂合同批量打印，传递printType参数
            pdfPath = saleContractService.print(ids, reportCode, null, null, printType);
        }

        return success(pdfPath);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        saleContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO, false);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        saleContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO, false);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long contactId) {
        saleContractService.submitExportTask(contactId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/change")
    @Operation(summary = "变更外销合同",description = "变更")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:change')")
    public CommonResult<List<CreatedResponse>> changeSaleContract(@RequestBody ChangeSmsContractSaveReqVO updateReqVO) {
        return success(saleContractService.changeSaleContract(updateReqVO));
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得外销合同变更分页")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:query')")
    public CommonResult<PageResult<SaleContractChange>> getSaleContractPage(SaleContractChangePageReq queryReq) {
        PageResult<SaleContractChange> pageResult = saleContractService.getChangeSaleContractPage(queryReq);
        return success(pageResult);
    }

//    @PutMapping("/change-submit")
//    @Operation(summary = "提交变更任务")
//    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:submit')")
//    public CommonResult<Boolean> submitChangeTask(@RequestParam Long id) {
//        saleContractService.submitChangeTask(id, WebFrameworkUtils.getLoginUserId());
//        return success(true);
//    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        saleContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO, true);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        saleContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO, true);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得外销合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:query')")
    public CommonResult<SaleContractChange> getChangeSaleContract(@RequestParam("id") Long id) {
        SaleContractChange containChangeRelations = saleContractService.getSaleContractChangeDetail
                (new SaleContractDetailReq().setSaleContractId(id));
        return success(containChangeRelations);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得外销合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SaleContractChange> getChangeSaleContractByProcessId(@RequestParam("id") String id) {
        SaleContractChange containChangeRelations = saleContractService.getSaleContractChangeDetail
                (new SaleContractDetailReq().setProcessInstanceId(id));
        return success(containChangeRelations);
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新外销合同变更单")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:update')")
    public CommonResult<Boolean> updateChangeSaleContract(@Valid @RequestBody SaleContractChange updateReqVO) {
//        saleContractService.updateChangeSaleContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除外销合同变更")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:delete')")
    public CommonResult<Boolean> deleteChangeSaleContract(@RequestParam("id") Long id) {
        saleContractService.deleteChangeSaleContract(id);
        return success(true);
    }

    @GetMapping("/confirm-source")
    @Operation(summary = "获取确认来源列表")
//    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:confirm-source')")
    public CommonResult<List<ConfirmSourceEntity>> getConfirmSource(@RequestParam Long id) {
        List<ConfirmSourceEntity> confirmSourceEntities = saleContractService.getConfirmSourceListBySaleContractId(id);
        return success(confirmSourceEntities);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认",description = "确认")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract-change:confirm')")
    public CommonResult<Boolean> confirm(@RequestParam Long id) {
        saleContractService.updateConfirmFlag(ConfirmFlagEnum.YES.getValue(), id);
        return success(true);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得销售合同精简列表分页")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SaleContractSimpleRespVO>> getSaleContractSimplePage(@Valid SaleContractPageReqVO pageReqVO) {
        PageResult<SaleContractSimpleRespVO> pageResult = saleContractService.getSaleContractSimplePage(pageReqVO);
        return success(pageResult);
    }


    @PostMapping("/to-purchase-plan")
    @Operation(summary = "销售合同下推采购计划",description = "下推采购计划")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:to-purchase-plan')")
    public CommonResult<List<CreatedResponse>> toContractTask(@RequestParam Long saleContractId) {
        return success(saleContractService.toContract(saleContractId));
    }

    @PostMapping("/batch-to-purchase-plan")
    @Operation(summary = "批量销售合同下推采购计划")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:to-purchase-plan')")
    public CommonResult<List<CreatedResponse> > batchToContractTask(@RequestParam List<Long> saleContractIdList) {
        return success(saleContractService.batchToContractTask(saleContractIdList));
    }

    @PostMapping("/batch-merge-to-purchase-plan")
    @Operation(summary = "批量销售合同合并下推采购计划")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:to-purchase-plan')")
    public CommonResult<List<CreatedResponse>> batchMergeToContractTask(@RequestParam List<Long> saleContractIdList) {
        return success(  saleContractService.batchMergeToContractTask(saleContractIdList));
    }

    @GetMapping("/history-trade-price")
    @Operation(summary = "获得历史成交价")
    public CommonResult<List<HistoryTradePriceResp>> getHistoryTradePrice(HistoryTradePriceReq req) {
        List<HistoryTradePriceResp> resp = saleContractService.getHistoryTradePriceBySkuCode(req);
        return success(resp);
    }

    @GetMapping("/check-contract-status")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:to-shipment-plan')")
    @Operation(summary = "校验合同状态")
    public CommonResult<Boolean> checkContractStatus(PushOutShipmentPlanReq req) {
        saleContractService.checkContractStatus(req);
        return success(true);
    }

    @GetMapping("/push-out-shipment-plan")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:to-shipment-plan')")
    @Operation(summary = "下推出运计划",description = "下推出运计划")
    public CommonResult<PushOutShipmentPlanResp> getPushOutShipmentPlanList(PushOutShipmentPlanReq req) {
        return success(saleContractService.getPushOutShipmentPlanResp(req));
    }

    @PutMapping("/sign-back")
    @Operation(summary = "外销合同回签",description = "回签")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:sign-back')")
    public CommonResult<Boolean> signBackSaleContract(@Valid @RequestBody SignBackReq signBackReq) {
        saleContractService.signBackSaleContract(signBackReq);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "外销合同作废",description = "作废")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:close')")
    public CommonResult<Boolean> closeSaleContract(@RequestBody CloseSaleContractReq closeSaleContractReq) {
        saleContractService.closeSaleContract(closeSaleContractReq);
        return success(true);
    }

    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "外销合同id", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "打印类型", example = "1"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "1"),
            @Parameter(name = "printType", required = false, description = "打印类型(hsCode-海关编码打印)", example = "hsCode")
    })
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "printType", required = false) String printType) {
        // 单个打印复用批量打印方法
        String pdfFile = saleContractService.print(Collections.singletonList(id), reportCode, reportId, companyId, printType);
        return success(pdfFile);
    }

    @GetMapping("/get-item-stock")
    @Operation(summary = "获取销售明细库存信息")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:query')")
    public CommonResult<PageResult<StockDTO>> getItemStock(ItemStockReq req) {
        return success(saleContractService.getItemStock(req));
    }

    @GetMapping("/get-cust-info-list")
    @Operation(summary = "获得销售合同精简列表分页")
    public CommonResult<List<SimpleContractRespVO>> getSaleContractSimplePage(@RequestParam("code") String code) {
        List<SimpleContractRespVO> result = saleContractService.getSimpleContractRespVoList(code);
        return success(result);
    }

    @GetMapping("/related-num")
    @Operation(summary = "关联单据数量")
    public CommonResult<RelatedSaleContractRespVO> getRelatedNum(@RequestParam Long id) {
        return success(saleContractService.getRelatedNum(id));
    }


    @PutMapping("/update-design")
    @Operation(summary = "更新采购合同设计稿")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:update')")
    public CommonResult<Boolean> updatePurchaseContractDesign(@Valid @RequestBody SaleContractDesignSaveReqVO updateReqVO) {
        saleContractService.updatePurchaseDesignContract(updateReqVO);
        return success(true);
    }

//    @GetMapping("/related-page")
//    @Operation(summary = "关联单据列表")
//    public CommonResult<SimpleLinkSaleContractRespVO> getSimpleLinkSaleContract(@RequestParam List<String> codeList) {
//        return success(saleContractService.getRelatedNum(id));
//    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核",description = "反审核")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long contactId) {
        return success(saleContractService.antiAudit(contactId));
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:change')")
    @DataPermission(enable = false)
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody SaleContractSaveReqVO changeReqVO) {
        SaleContractRespVO purchaseContractInfoRespVO = BeanUtils.toBean(changeReqVO, SaleContractRespVO.class);
        return success(saleContractService.getChangeEffect(purchaseContractInfoRespVO));
    }

    @PutMapping("/re-lock")
    @Operation(summary = "重新分配锁定库存")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:re-lock')")
    public CommonResult<Boolean> reLockSaleContract(@Valid @RequestBody ReLockReq reLockReq) {
        saleContractService.reLockSaleContract(reLockReq);
        return success(true);
    }

    @PutMapping("/order-done")
    @Operation(summary = "完成单据",description = "完成单据")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:order-done')")
    public CommonResult<Boolean> orderDone(@RequestParam Long id) {
        saleContractService.setOrderDone( id);
        return success(true);
    }

    @GetMapping("/export-word")
    @Operation(summary = "导出销售合同 word")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "模版id", example = "tudou"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "tudou"),
    })
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:export')")
    @Obsolete
    public void exportWord(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            HttpServletResponse response) {
        saleContractService.exportWord(id, reportCode,reportId, companyId,response);
    }

    @PutMapping("/update-thumbnail")
    @Operation(summary = "更新缩略图")
    public CommonResult<Boolean> updateThumbnail() {
        saleContractService.updateThumbnail();
        return success(true);
    }

    @PutMapping("/update-collection-plan")
    @Operation(summary = "修改收款计划")
    @PreAuthorize("@ss.hasPermission('sms:export-sale-contract:update-collection-plan')")
    public CommonResult<Boolean> updateCollectionPlan(@RequestBody UpdateCollectionPlanReq req) {
        saleContractService.updateCollectionPlan(req);
        return success(true);
    }
}