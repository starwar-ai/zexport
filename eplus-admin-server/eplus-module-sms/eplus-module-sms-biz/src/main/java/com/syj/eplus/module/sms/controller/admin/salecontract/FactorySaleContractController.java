package com.syj.eplus.module.sms.controller.admin.salecontract;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.ContainerMidVO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.ConfirmFlagEnum;
import com.syj.eplus.framework.common.enums.SaleTabEnum;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
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
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 外币采购合同")
@RestController
@RequestMapping("/sms/factory/sale-contract")
@Validated
public class FactorySaleContractController {
    @Resource
    private SaleContractService saleContractService;

    @PostMapping("/create")
    @Operation(summary = "创建外币采购合同")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:create')")
    public CommonResult<List<CreatedResponse>> createSaleContract(@Valid @RequestBody SaleContractSaveReqVO createReqVO) {
        return success(saleContractService.createSaleContract(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新外币采购合同",description = "修改")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:update')")
    public CommonResult<Boolean> updateSaleContract(@Valid @RequestBody SaleContractSaveReqVO updateReqVO) {
        saleContractService.updateSaleContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除外币采购合同",description = "删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:delete')")
    public CommonResult<Boolean> deleteSaleContract(@RequestParam("id") Long id) {
        saleContractService.deleteSaleContract(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得外币采购合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:query')")
    public CommonResult<SaleContractRespVO> getSaleContract(@RequestParam("id") Long id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract
                (new SaleContractDetailReq().setSaleContractId(id), SaleTabEnum.SALE_DETAIL);
        return success(saleContract);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得外币采购合同详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SaleContractRespVO> getSaleContractByProcessId(@RequestParam("id") String id) {
        SaleContractRespVO saleContract = saleContractService.getSaleContract(new SaleContractDetailReq().setProcessInstanceId(id),SaleTabEnum.SALE_DETAIL);
        return success(saleContract);
    }

    @GetMapping("/page")
    @Operation(summary = "获得外币采购合同分页")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:query')")
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

    @PutMapping("/approve")
    @Operation(summary = "通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        saleContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO, false);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        saleContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO, false);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long contactId) {
        saleContractService.submitfactoryTask(contactId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/change")
    @Operation(summary = "变更外币采购合同",description = "变更")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:change')")
    public CommonResult<List<CreatedResponse>> changeSaleContract(@RequestBody ChangeSmsContractSaveReqVO updateReqVO) {
        return success( saleContractService.changeSaleContract(updateReqVO));
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得外币采购合同变更分页")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:query')")
    public CommonResult<PageResult<SaleContractChange>> getSaleContractPage(SaleContractChangePageReq queryReq) {
        PageResult<SaleContractChange> pageResult = saleContractService.getChangeSaleContractPage(queryReq);
        return success(pageResult);
    }

//    @PutMapping("/change-submit")
//    @Operation(summary = "提交变更任务")
//    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:submit')")
//    public CommonResult<Boolean> submitChangeTask(@RequestParam Long id) {
//        saleContractService.submitChangeTask(id, WebFrameworkUtils.getLoginUserId());
//        return success(true);
//    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        saleContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO, true);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        saleContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO, true);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得外币采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:query')")
    public CommonResult<SaleContractChange> getChangeSaleContract(@RequestParam("id") Long id) {
        SaleContractChange containChangeRelations = saleContractService.getSaleContractChangeDetail
                (new SaleContractDetailReq().setSaleContractId(id));
        return success(containChangeRelations);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得外币采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SaleContractChange> getChangeSaleContractByProcessId(@RequestParam("id") String id) {
        SaleContractChange containChangeRelations = saleContractService.getSaleContractChangeDetail
                (new SaleContractDetailReq().setProcessInstanceId(id));
        return success(containChangeRelations);
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新外币采购合同变更单")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:update')")
    public CommonResult<Boolean> updateChangeSaleContract(@Valid @RequestBody SaleContractChange updateReqVO) {
//        saleContractService.updateChangeSaleContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除外币采购合同变更")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:delete')")
    public CommonResult<Boolean> deleteChangeSaleContract(@RequestParam("id") Long id) {
        saleContractService.deleteChangeSaleContract(id);
        return success(true);
    }

    @GetMapping("/confirm-source")
    @Operation(summary = "获取确认来源列表")
//    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:confirm-source')")
    public CommonResult<List<ConfirmSourceEntity>> getConfirmSource(@RequestParam Long id) {
        List<ConfirmSourceEntity> confirmSourceEntities = saleContractService.getConfirmSourceListBySaleContractId(id);
        return success(confirmSourceEntities);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract-change:confirm')")
    public CommonResult<Boolean> confirm(@RequestParam Long id) {
        saleContractService.updateConfirmFlag(ConfirmFlagEnum.YES.getValue(), id);
        return success(true);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得销售合同精简列表分页")
    public CommonResult<PageResult<SaleContractSimpleRespVO>> getSaleContractSimplePage(@Valid SaleContractPageReqVO pageReqVO) {
        PageResult<SaleContractSimpleRespVO> pageResult = saleContractService.getSaleContractSimplePage(pageReqVO);
        return success(pageResult);
    }


    @PostMapping("/to-purchase-plan")
    @Operation(summary = "销售合同下推采购计划",description = "下推采购计划")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:to-purchase-plan')")
    public CommonResult<Boolean> toContractTask(@RequestParam Long saleContractId) {
        saleContractService.toContract(saleContractId);
        return success(true);
    }

    @PutMapping("/sign-back")
    @Operation(summary = "外币采购合同回签",description = "回签")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:sign-back')")
    public CommonResult<Boolean> signBackSaleContract(@Valid @RequestBody SignBackReq signBackReq) {
        saleContractService.signBackSaleContract(signBackReq);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "外币采购合同作废")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:close')")
    public CommonResult<Boolean> closeSaleContract(@RequestBody CloseSaleContractReq closeSaleContractReq) {
        saleContractService.closeSaleContract(closeSaleContractReq);
        return success(true);
    }

    @GetMapping("/out-notice-mid")
    @Operation(summary = "获得转出库通知单中间页")
//    @PreAuthorize("@ss.hasPermission('sms:container-transportation:query')")
    public CommonResult<ContainerMidVO> getNoticeMidPage(@RequestParam("ids") List<Long> ids) {
        return success(saleContractService.getOutNoticeMid(ids));
    }

    @PostMapping("/transform-notice")
    @Operation(summary = "转出库通知单")
//    @PreAuthorize("@ss.hasPermission('sms:container-transportation:create')")
    public CommonResult<List<CreatedResponse>> transformContainerTransportation(@Valid @RequestBody ContainerMidVO containerMidVO) {
        return success(saleContractService.createNotice(containerMidVO));
    }


    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "外币采购id", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "打印类型", example = "1"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "1"),
            @Parameter(name = "printType", required = false, description = "打印类型(hsCode-海关编码打印)", example = "hsCode")
    })
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "printType", required = false) String printType) {
        String pdfFile = saleContractService.print(Collections.singletonList(id), reportCode, reportId, companyId,printType);
        return success(pdfFile);
    }


    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:export')")
    public void exportWord(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            HttpServletResponse response) {
        saleContractService.exportWord(id, reportCode,reportId, companyId,response);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出外币采购销售合同 Excel")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:export')")
    @OperateLog(type = EXPORT)
    public void exportSaleContractExcel(@Valid SaleContractPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        saleContractService.exportSaleContractExcel(pageReqVO, "外币采购销售合同", response);
    }

    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出外币采购销售合同 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "exportType", required = false, description = "导出类型", example = "1024"),
    })

    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("exportType") String exportType,
            HttpServletResponse response) throws IOException {
        saleContractService.exportExcel(Collections.singletonList(id), exportType, reportCode, response);
    }

    @PutMapping("/update-collection-plan")
    @Operation(summary = "修改收款计划")
    @PreAuthorize("@ss.hasPermission('sms:factory-sale-contract:update-collection-plan')")
    public CommonResult<Boolean> updateCollectionPlan(@RequestBody UpdateCollectionPlanReq req) {
        saleContractService.updateCollectionPlan(req);
        return success(true);
    }

}
