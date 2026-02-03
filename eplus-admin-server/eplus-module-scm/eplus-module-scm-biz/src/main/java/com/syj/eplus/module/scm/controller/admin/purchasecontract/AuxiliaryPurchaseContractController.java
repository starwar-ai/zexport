package com.syj.eplus.module.scm.controller.admin.purchasecontract;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ConfirmFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationAllocationSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemBoardRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
import com.syj.eplus.module.scm.convert.PurchaseContractConvert;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 包材采购合同")
@RestController
@RequestMapping("/scm/auxiliary-purchase-contract")
@Validated
public class AuxiliaryPurchaseContractController {

    @Resource
    private PurchaseContractService purchaseContractService;

    @PostMapping("/create")
    @Operation(summary = "创建包材采购合同")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:create')")
    public CommonResult<List<CreatedResponse>> createPurchaseContract(@Valid @RequestBody PurchaseContractSaveInfoReqVO createInfoReqVO) {
        createInfoReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        return success(purchaseContractService.createPurchaseContract(createInfoReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新包材采购合同")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:update')")
    public CommonResult<Boolean> updatePurchaseContract(@Valid @RequestBody PurchaseContractSaveInfoReqVO updateReqVO) {
        updateReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        purchaseContractService.updatePurchaseContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除包材采购合同")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:delete')")
    public CommonResult<Boolean> deletePurchaseContract(@RequestParam("id") Long id) {
        purchaseContractService.deletePurchaseContract(id,BooleanEnum.YES.getValue());
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得包材采购合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:query')")
    public CommonResult<PurchaseContractInfoRespVO> getPurchaseContract(@RequestParam("id") Long id) {
        PurchaseContractInfoRespVO purchaseContract = purchaseContractService.getPurchaseContractContainRelations(new PurchaseContractDetailReq().setPurchaseContractId(id));
        if (Objects.equals(purchaseContract.getAuxiliaryFlag(), BooleanEnum.YES.getValue())) {
            return success(purchaseContract);
        } else {
            return success(null);
        }
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得包材采购合同详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<PurchaseContractInfoRespVO> getPurchaseContractByProcessId(@RequestParam("id") String id) {
        PurchaseContractInfoRespVO purchaseContract = purchaseContractService.getPurchaseContractContainRelations(new PurchaseContractDetailReq().setProcessInstanceId(id));
        if (Objects.equals(purchaseContract.getAuxiliaryFlag(), BooleanEnum.YES.getValue())) {
            return success(purchaseContract);
        } else {
            return success(null);
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获得包材采购合同分页")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:query')")
    public CommonResult<PageResult<PurchaseContractInfoRespVO>> getPurchaseContractPage(@Valid PurchaseContractPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        PageResult<PurchaseContractInfoRespVO> pageResult = purchaseContractService.getPurchaseContractPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/page-board")
    @Operation(summary = "获得包材采购合同看板分页")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:board')")
    public CommonResult<PageResult<PurchaseContractItemBoardRespVO>> getPurchaseContractBoardPage(@Valid PurchaseContractPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        PageResult<PurchaseContractItemBoardRespVO> pageResult = purchaseContractService.getPurchaseContractBoardPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出包材采购合同 Excel")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchaseContractExcel(@Valid PurchaseContractPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        List<PurchaseContractInfoRespVO> list = purchaseContractService.getPurchaseContractPage(pageReqVO).getList();
        
        // 转换为导出VO并填充金额拆分字段
        List<PurchaseContractRespVO> exportList = BeanUtils.toBean(list, PurchaseContractRespVO.class);
        exportList.forEach(PurchaseContractConvert.INSTANCE::fillAmountSplitFields);
        
        // 导出 Excel
        ExcelUtils.write(response, "采购合同.xls", "数据", PurchaseContractRespVO.class, exportList);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchaseContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO,false);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchaseContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO,false);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        purchaseContractService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId(), purchaseContractService.getProcessDefinitionKey(), Map.of());
        return success(true);
    }

    @PutMapping("/batch-finish")
    @Operation(summary = "批量作废")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:finish')")
    public CommonResult<Boolean> finishTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.finishPurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/batch-rollback-finish")
    @Operation(summary = "批量反作废")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:rollback-finish')")
    public CommonResult<Boolean> rollBackFinishTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.rollBackFinishPurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/batch-order")
    @Operation(summary = "批量下单")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:order')")
    public CommonResult<Boolean> placeOrderTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.placeOrderPurchaseContract(contractIdList);
        return success(true);
    }


//    @PutMapping("/batch-sign-back")
//    @Operation(summary = "批量回签")
//    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:sign-back')")
//    public CommonResult<Boolean> signBackTask(@RequestParam PurchaseContractSignBackReqVO contractIdList) {
//        purchaseContractService.signBackPurchaseContract(contractIdList);
//        return success(true);
//    }

    @PutMapping("/sign-back")
    @Operation(summary = "包材采购合同回签",description = "回签")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:sign-back')")
    public CommonResult<Boolean> signBackContract(@Valid @RequestBody SignBackReq signBackReq) {
        purchaseContractService.signBackContract(signBackReq);
        return success(true);
    }

    @PutMapping("/batch-done")
    @Operation(summary = "批量整单完成")
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:done')")
    public CommonResult<Boolean> doneTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.donePurchaseContract(contractIdList);
        return success(true);
    }


    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('scm:auxiliary-purchase-contract:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = purchaseContractService.print(id, reportCode, sourceCode, sourceType,companyId,true);
        return success(pdfFile);
    }

    @PostMapping("/rePurchase")
//    @Obsolete
    @Operation(summary = "包材采购合同重构")
//    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<Boolean> rePurchase(@Valid @RequestBody RePurchaseSaveReqVO reqVO) {
        purchaseContractService.rePurchaseById(reqVO);
        return success(true);
    }


    @PostMapping("/get-auxiliary-allocation")
    @Operation(summary = "获取包材分摊列表")
    public CommonResult<List<PurchaseAuxiliaryAllocationDO>> getAuxiliaryAllocationList(@Valid @RequestBody AuxiliaryAllocationReq req) {
        List<PurchaseAuxiliaryAllocationDO> pageResult = purchaseContractService.getAuxiliaryAllocationList(req);
        return success(pageResult);
    }

    @PutMapping("/update-auxiliary-allocation")
    @Operation(summary = "新增或编辑包材分摊列表")
    public CommonResult<Boolean> updateAuxiliaryAllocationList(@Valid @RequestBody PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO) {
        purchaseContractService.updateAuxiliaryAllocationList(updateReqVO);
        return success(true);
    }

    @PutMapping("/delete-auxiliary-allocation")
    @Operation(summary = "删除包材分摊列表")
    @Parameter(name = "itemId", description = "辅料采购合同明细id", required = true, example = "uuid")
    public CommonResult<Boolean> deleteAuxiliaryAllocationList(@Valid  Long itemId) {
        purchaseContractService.deleteAuxiliaryAllocationList(itemId);
        return success(true);
    }

    @GetMapping("/confirm-source")
    @Operation(summary = "获取确认来源列表")
    public CommonResult<List<ConfirmSourceEntity>> getConfirmSource(@RequestParam Long id) {
        List<ConfirmSourceEntity> confirmSourceEntities = purchaseContractService.getConfirmSourceListByPurchaseContractId(id, EffectRangeEnum.AUXILIARY_PURCHASE);
        return success(confirmSourceEntities);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认",description = "确认")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:confirm')")
    public CommonResult<Boolean> confirm(@RequestParam Long id) {
        purchaseContractService.updateConfirmFlag(ConfirmFlagEnum.YES.getValue(), id);
        return success(true);
    }

    @PutMapping("/auxiliary-change")
    @Operation(summary = "变更包材采购合同",description = "变更")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:update')")
    public CommonResult<List<CreatedResponse>> changePurchaseContract(@RequestBody ChangePurchaseContract updateReqVO) {
        updateReqVO.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        return success( purchaseContractService.changePurchaseContract(updateReqVO));
    }

    @GetMapping("/auxiliary-change-page")
    @Operation(summary = "获得包材采购合同变更分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:query')")
    public CommonResult<PageResult<PurchaseContractChange>> getPurchaseContractPage(ChangePurchasePageReq queryReq) {
        queryReq.setAuxiliaryFlag(BooleanEnum.YES.getValue());
        PageResult<PurchaseContractChange> pageResult = purchaseContractService.getChangePurchaseContractPage(queryReq);
        List<PurchaseContractChange> purchaseContractChangeList = pageResult == null ? null : pageResult.getList();
        if (CollUtil.isEmpty(purchaseContractChangeList)) {
            return success(PageResult.empty());
        }
        return success(pageResult);
    }
    @PutMapping("/auxiliary-change-approve")
    @Operation(summary = "通过变更任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchaseContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO,true);
        return success(true);
    }

    @PutMapping("/auxiliary-change-reject")
    @Operation(summary = "不通过变更任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchaseContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO,true);
        return success(true);
    }

    @GetMapping("/auxiliary-change-detail")
    @Operation(summary = "获得采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:query')")
    public CommonResult<PurchaseContractChange> getChangePurchaseContract(@RequestParam("id") Long id) {
        PurchaseContractChange changePurchaseContract = purchaseContractService.getPurchaseContractContainChangeRelations
                (new PurchaseContractDetailReq().setPurchaseContractId(id));
        return success(changePurchaseContract);
    }

    @GetMapping("/audit-auxiliary-change-detail")
    @Operation(summary = "获得采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<PurchaseContractChange> getChangePurchaseContractByProcessId(@RequestParam("id") String id) {
        PurchaseContractChange changePurchaseContract = purchaseContractService.getPurchaseContractContainChangeRelations
                (new PurchaseContractDetailReq().setProcessInstanceId(id));
        return success(changePurchaseContract);
    }

    @PutMapping("/auxiliary-change-update")
    @Operation(summary = "更新采购合同变更单",description = "修改")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:update')")
    public CommonResult<Boolean> updateChangePurchaseContract(@Valid @RequestBody PurchaseContractChange updateReqVO) {
        purchaseContractService.updateChangePurchaseContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/auxiliary-change-delete")
    @Operation(summary = "删除采购合同变更",description = "删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:delete')")
    public CommonResult<Boolean> deleteChangePurchaseContract(@RequestParam("id") Long id) {
        purchaseContractService.deleteChangePurchaseContract(id);
        return success(true);
    }

    @GetMapping("/get-list-by-code")
    @Operation(summary = "根据编号字符串获得船代费用列表")
    public CommonResult<List<FeeShareResp>> getListByCodeList(@Valid String codeList) {
        List<FeeShareResp> pageResult = purchaseContractService.getListByCodeList(codeList);
        return success(pageResult);
    }

}
