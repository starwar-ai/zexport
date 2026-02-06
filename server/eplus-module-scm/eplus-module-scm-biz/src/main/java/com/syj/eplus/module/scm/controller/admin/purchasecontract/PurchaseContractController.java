package com.syj.eplus.module.scm.controller.admin.purchasecontract;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ConfirmFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryPurchaseContractDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.CompleteOrderReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetSimplePageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractSimpleDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemBoardRespVO;
import com.syj.eplus.module.scm.convert.PurchaseContractConvert;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.Obsolete;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 采购合同")
@RestController
@RequestMapping("/scm/purchase-contract")
@Validated
public class PurchaseContractController {

    @Resource
    private PurchaseContractService purchaseContractService;

    @Resource
    private FileApi fileApi;

    @PostMapping("/create")
    @Operation(summary = "创建采购合同",description = "新增")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:create')")
    public CommonResult<List<CreatedResponse>> createPurchaseContract(@Valid @RequestBody PurchaseContractSaveInfoReqVO createInfoReqVO) {
        createInfoReqVO.setAuxiliaryFlag(BooleanEnum.NO.getValue());
        return success(purchaseContractService.createPurchaseContract(createInfoReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购合同",description = "修改")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:update')")
    public CommonResult<Boolean> updatePurchaseContract(@Valid @RequestBody PurchaseContractSaveInfoReqVO updateReqVO) {
        updateReqVO.setAuxiliaryFlag(BooleanEnum.NO.getValue());
        purchaseContractService.updatePurchaseContract(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-design")
    @Operation(summary = "更新采购合同设计稿",description = "修改")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:update')")
    public CommonResult<Boolean> updatePurchaseContractDesign(@Valid @RequestBody PurchaseContractDesignSaveReqVO updateReqVO) {
        purchaseContractService.updatePurchaseDesignContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购合同",description = "删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:delete')")
    public CommonResult<Boolean> deletePurchaseContract(@RequestParam("id") Long id) {
        CommonResult<PurchaseContractInfoRespVO> purchaseContract = getPurchaseContract(id);
        if (Objects.equals(purchaseContract.getData().getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            purchaseContractService.deletePurchaseContract(id, BooleanEnum.NO.getValue());
            return success(true);
        } else {
            return success(false);
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "获得采购合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:query')")
    public CommonResult<PurchaseContractInfoRespVO> getPurchaseContract(@RequestParam("id") Long id) {
        PurchaseContractInfoRespVO purchaseContract = purchaseContractService.getPurchaseContractContainRelations(new PurchaseContractDetailReq().setPurchaseContractId(id));
        if (Objects.equals(purchaseContract.getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            return success(purchaseContract);
        } else {
            return success(null);
        }
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购合同详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<PurchaseContractInfoRespVO> getPurchaseContractByProcessId(@RequestParam("id")
                                                                                   String id) {
        PurchaseContractInfoRespVO purchaseContract = purchaseContractService.getPurchaseContractContainRelations(new PurchaseContractDetailReq().setProcessInstanceId(id));
        if (Objects.equals(purchaseContract.getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            return success(purchaseContract);
        } else {
            return success(null);
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购合同分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:query')")
    public CommonResult<PageResult<?>> getPurchaseContractPage(@Valid PurchaseContractPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(BooleanEnum.NO.getValue());
        Integer queryMode = pageReqVO.getQueryMode();
        if (queryMode != null && queryMode == 2) {
            // Product mode: flat structure with product details
            PageResult<PurchaseContractProductModeRespVO> pageResult = purchaseContractService.getProductModePage(pageReqVO);
            return success(pageResult);
        } else {
            // Document mode (default): contract-level data only
            PageResult<PurchaseContractInfoRespVO> pageResult = purchaseContractService.getPurchaseContractPage(pageReqVO);
            return success(pageResult);
        }
    }


    @GetMapping("/page-board")
    @Operation(summary = "获得采购合同看板分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:board')")
    public CommonResult<PageResult<PurchaseContractItemBoardRespVO>> getPurchaseContractBoardPage(@Valid PurchaseContractPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(BooleanEnum.NO.getValue());
        PageResult<PurchaseContractItemBoardRespVO> pageResult = purchaseContractService.getPurchaseContractBoardPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购合同 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchaseContractExcel(@Valid PurchaseContractPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setAuxiliaryFlag(BooleanEnum.NO.getValue());
        pageReqVO.setExportFlag(BooleanEnum.YES.getValue());
        Integer queryMode = pageReqVO.getQueryMode();
        if (queryMode != null && queryMode == 2) {
            PageResult<PurchaseContractProductModeRespVO> pageResult = purchaseContractService.getProductModePage(pageReqVO);
            List<PurchaseContractProductModeRespVO> records = pageResult.getList();
            List<PurchaseContractProductExportVO> exportList = CollUtil.isEmpty(records)
                    ? new ArrayList<>()
                    : PurchaseContractConvert.INSTANCE.convertPurchaseContractProductExportVOList(records);
            if (CollUtil.isNotEmpty(exportList) && CollUtil.isNotEmpty(records)) {
                for (int i = 0; i < exportList.size(); i++) {
                    PurchaseContractProductExportVO vo = exportList.get(i);
                    PurchaseContractProductModeRespVO source = records.get(i);
                    vo.setProductImage(buildThumbnailImage(source.getThumbnail()));
                }
            }
            ExcelUtils.write(response, "采购合同.xls", "数据", PurchaseContractProductExportVO.class, exportList);
            return;
        }

        List<PurchaseContractInfoRespVO> list = purchaseContractService.getPurchaseContractPage(pageReqVO).getList();
        List<PurchaseContractRespVO> exportList = BeanUtils.toBean(list, PurchaseContractRespVO.class);
        exportList.forEach(PurchaseContractConvert.INSTANCE::fillAmountSplitFields);
        ExcelUtils.write(response, "采购合同.xls", "数据", PurchaseContractRespVO.class, exportList);
    }

    private WriteCellData<Void> buildThumbnailImage(String thumbnail) {
        if (StringUtils.isBlank(thumbnail)) {
            return null;
        }
        String inputPath = thumbnail;
        String filePath = inputPath;
        int index = inputPath.lastIndexOf("get/");
        if (index >= 0) {
            filePath = inputPath.substring(index + 4);
        }
        try {
            byte[] content = fileApi.getFileContent(filePath);
            File inputFile = FileUtils.createTempFile(content);
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                inputFile.delete();
                return null;
            }
            Double width = Double.valueOf(image.getWidth());
            Double height = Double.valueOf(image.getHeight());
            WriteCellData<Void> cellData = ExcelUtils.imageCells(content, width, height, 2.0, 2.0, 0, 0);
            inputFile.delete();
            return cellData;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchaseContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO,false);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchaseContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO,false);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务",description = "提交")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        purchaseContractService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId(), purchaseContractService.getProcessDefinitionKey(), Map.of());
        return success(true);
    }

    @PutMapping("/batch-finish")
    @Operation(summary = "批量作废",description = "作废")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:finish')")
    public CommonResult<Boolean> finishTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.finishPurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/batch-rollback-finish")
    @Operation(summary = "批量反作废",description = "反作废")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:rollback-finish')")
    public CommonResult<Boolean> rollBackFinishTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.rollBackFinishPurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/batch-order")
    @Operation(summary = "批量下单",description = "下单")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:order')")
    public CommonResult<Boolean> placeOrderTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.placeOrderPurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/complete-order")
    @Operation(summary = "采购合同-生产完成",description = "生产完成")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:produced')")
    public CommonResult<Boolean> completeOrderTask(@RequestBody CompleteOrderReqDTO completeOrderReq) {
        completeOrderReq.setCheckStatus(true);
        purchaseContractService.completeOrderTask(completeOrderReq);
        return success(true);
    }


    //回签需要上传附件  暂不支持批量回签
//    @PutMapping("/batch-sign-back")
//    @Operation(summary = "批量回签")
//    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:sign-back')")
//    public CommonResult<Boolean> signBackTask(@RequestBody PurchaseContractSignBackReqVO reqVO) {
//        purchaseContractService.signBackPurchaseContract(reqVO);
//        return success(true);
//    }

    @PutMapping("/sign-back")
    @Operation(summary = "采购合同回签",description = "回签")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:sign-back')")
    public CommonResult<Boolean> signBackContract(@Valid @RequestBody SignBackReq signBackReq) {
        purchaseContractService.signBackContract(signBackReq);
        return success(true);
    }

    @PutMapping("/place-order")
    @Operation(summary = "采购合同下单",description = "下单")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:order')")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<Boolean> placeOrderContract(@Valid Long id) {
        purchaseContractService.placeOrderContract(id);
        return success(true);
    }

    @PutMapping("/batch-done")
    @Operation(summary = "批量整单完成")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:done')")
    public CommonResult<Boolean> doneTask(@RequestParam List<Long> contractIdList) {
        purchaseContractService.donePurchaseContract(contractIdList);
        return success(true);
    }

    @PutMapping("/batch-setArrivedDate")
    @Operation(summary = "设置到料时间完成",description = "设置到料时间")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:setArrivedDate')")
    public CommonResult<Boolean> doneTask(@Valid @RequestBody PurchaseContractSetArrivedDateReqVO voReq) {
        purchaseContractService.SetArrivedDatePurchaseContract(voReq);
        return success(true);
    }

    @PutMapping("/change")
    @Operation(summary = "变更采购合同",description = "变更")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:update')")
    public CommonResult<List<CreatedResponse>> changePurchaseContract(@RequestBody ChangePurchaseContract updateReqVO) {
        return success( purchaseContractService.changePurchaseContract(updateReqVO));
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得采购合同变更分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:query')")
    public CommonResult<PageResult<PurchaseContractChange>> getPurchaseContractPage(ChangePurchasePageReq queryReq) {
        PageResult<PurchaseContractChange> pageResult = purchaseContractService.getChangePurchaseContractPage(queryReq);
        List<PurchaseContractChange> purchaseContractChangeList = pageResult == null ? null : pageResult.getList();
        if (CollUtil.isEmpty(purchaseContractChangeList)) {
            return success(PageResult.empty());
        }
        return success(pageResult);
    }

//    @PutMapping("/change-submit")
//    @Operation(summary = "提交变更任务")
//    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:submit')")
//    public CommonResult<Boolean> submitChangeTask(@RequestParam Long id) {
//        purchaseContractService.submitChangeTask(id, WebFrameworkUtils.getLoginUserId());
//        return success(true);
//    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchaseContractService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO,true);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchaseContractService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO,true);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:query')")
    public CommonResult<PurchaseContractChange> getChangePurchaseContract(@RequestParam("id") Long id) {
        PurchaseContractChange changePurchaseContract = purchaseContractService.getPurchaseContractContainChangeRelations
                (new PurchaseContractDetailReq().setPurchaseContractId(id));
        return success(changePurchaseContract);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得采购合同变更详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<PurchaseContractChange> getChangePurchaseContractByProcessId(@RequestParam("id") String id) {
        PurchaseContractChange changePurchaseContract = purchaseContractService.getPurchaseContractContainChangeRelations
                (new PurchaseContractDetailReq().setProcessInstanceId(id));
        return success(changePurchaseContract);
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新采购合同变更单",description = "修改")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:update')")
    public CommonResult<Boolean> updateChangePurchaseContract(@Valid @RequestBody PurchaseContractChange updateReqVO) {
        purchaseContractService.updateChangePurchaseContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除采购合同变更",description = "删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:delete')")
    public CommonResult<Boolean> deleteChangePurchaseContract(@RequestParam("id") Long id) {
        purchaseContractService.deleteChangePurchaseContract(id);
        return success(true);
    }


    @GetMapping("/to-notice-by-id")
    @Obsolete
    @Operation(summary = "采购合同转入库通知单(主键)")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<CreatedResponse> toStockNoticeById(@RequestParam("id") Long id) {
        return success(purchaseContractService.toStockNoticeById(id));
    }

    @PostMapping("/to-notice")
    @Operation(summary = "采购合同转入库通知单(中间页面)",description = "转入库通知")
    public CommonResult<CreatedResponse> toStockNotice(@Valid @RequestBody StockNoticeReqVO stockNoticeReqVO) {
        return success(purchaseContractService.toStockNotice(stockNoticeReqVO));
    }

    @GetMapping("/print")
    @Operation(summary = "打印",description = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = purchaseContractService.print(id, reportCode, sourceCode, sourceType, companyId,false);
        return success(pdfFile);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得采购合同精简列表分页")
    @DataPermission(enable = false)
    public CommonResult<PageResult<PurchaseContractSimpleDTO>> getPurchaseContractPage(@Valid PurchaseContractGetSimplePageReqDTO pageReqVO) {
        PageResult<PurchaseContractSimpleDTO> pageResult = purchaseContractService.getPurchaseContractSimplePage(pageReqVO);
        return success(pageResult);
    }


    @GetMapping("/rePurchase")
    @Obsolete
    @Operation(summary = "采购合同重构")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<Boolean> rePurchase(@Valid @RequestBody RePurchaseSaveReqVO reqVO) {
        purchaseContractService.rePurchaseById(reqVO);
        return success(true);
    }

    @GetMapping("/get-payment-list")
    @Operation(summary = "获取付款计划")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:query')")
    public CommonResult<List<PurchasePaymentPlan>> getPurchaseContractItemPage(@RequestParam("contractIdList") List<Long> contractIdList) {
        List<PurchasePaymentPlan> purchasePaymentPlanList = purchaseContractService.getPurchasePaymentPlanList(contractIdList);
        return success(purchasePaymentPlanList);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认",description = "确认")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:confirm')")
    public CommonResult<Boolean> confirm(@RequestParam Long id) {
        purchaseContractService.updateConfirmFlag(ConfirmFlagEnum.YES.getValue(), id);
        return success(true);
    }

    @GetMapping("/confirm-source")
    @Operation(summary = "获取确认来源列表")
//    @PreAuthorize("@ss.hasPermission('scm:purchase-contract-change:confirm-source')")
    public CommonResult<List<ConfirmSourceEntity>> getConfirmSource(@RequestParam Long id) {
        List<ConfirmSourceEntity> confirmSourceEntities = purchaseContractService.getConfirmSourceListByPurchaseContractId(id, EffectRangeEnum.SCM);
        return success(confirmSourceEntities);
    }

    @GetMapping("/related-num")
    @Operation(summary = "关联单据数量")
    public CommonResult<RelatedPurchaseContractRespVO> getRelatedNum(@RequestParam Long id) {
        return success(purchaseContractService.getRelatedNum(id));
    }

    @GetMapping("/batch-to-notice-mid")
    @Operation(summary = "批量转入库通知单中间页")
    public CommonResult<List<TransformNoticeMidVO>> getToNoticeMid(@RequestParam List<Long> ids) {
        return success(purchaseContractService.getToNoticeMid(ids));
    }

    @PostMapping("/batch-to-notice")
    @Operation(summary = "批量转入库通知单",description = "转入库通知")
    public CommonResult<List<CreatedResponse>> batchToStockNotice(@Valid @RequestBody List<TransformNoticeMidVO> req) {
        return success(purchaseContractService.batchToStockNotice(req));
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核",description = "反审核")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long custId) {
        return success(purchaseContractService.antiAudit(custId));
    }

    @GetMapping("/batch-to-auxiliary-mid")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:auxiliary')")
    @Operation(summary = "批量转辅料采购中间页")
    public CommonResult<List<TransformAuxiliaryMidVO>> getToAuxiliaryMid(@RequestParam List<Long> ids) {
        return success(purchaseContractService.getToAuxiliaryMid(ids));
    }


    @PostMapping("/batch-to-auxiliary")
    @Operation(summary = "批量转辅料采购",description = "辅料采购")
    @Parameter(name = "auxiliaryMidVOList", description = "参数", required = true, example = "uuid")
    public CommonResult<List<CreatedResponse>> batchToSAuxiliary(@Valid @RequestBody List<TransformAuxiliaryMidVO> auxiliaryMidVOList) {
        return success(purchaseContractService.batchToSAuxiliary(auxiliaryMidVOList));
    }


    @GetMapping("/get-auxiliary-allocation")
    @Operation(summary = "获得采购合同辅料列表")
    @Parameter(name = "code", description = "采购合同编号", required = true)
    public CommonResult<List<AuxiliaryPurchaseContractDTO>> getAuxiliaryAllocationList(@Valid String code) {
        List<AuxiliaryPurchaseContractDTO> result = purchaseContractService.getAuxiliaryAllocationListByContractCode(code);
        return success(result);
    }

    @GetMapping("/to-invoice-notice-mid")
    @Operation(summary = "开票通知中间页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:to-invoice-notice')")
    public CommonResult<InvoiceNoticeVO> toInvoiceNoticedMid(@RequestParam Long id) {
        return success(purchaseContractService.toInvoiceNoticedMid(id));
    }

    @PostMapping("/to-invoice-notice")
    @Operation(summary = "转开票通知")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:to-invoice-notice')")
    public CommonResult<List<CreatedResponse>> toInvoiceNoticed(@Valid @RequestBody InvoiceNoticeVO req) {
        return success(purchaseContractService.toInvoiceNoticed(req));
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:change')")
    @DataPermission(enable = false)
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody PurchaseContractSaveInfoReqVO changeReqVO) {
        PurchaseContractInfoRespVO purchaseContractInfoRespVO = BeanUtils.toBean(changeReqVO, PurchaseContractInfoRespVO.class);
        return success(purchaseContractService.getChangeEffect(purchaseContractInfoRespVO));
    }

    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出采购合同 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        purchaseContractService.exportExcel(id, reportCode,response);
    }

    @GetMapping("/export-word")
    @Operation(summary = "导出采购合同 word")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", required = true, description = "账套id", example = "tudou"),
            @Parameter(name = "sourceCode", required = true, description = "来源编码", example = "tudou"),
            @Parameter(name = "sourceType", required = true, description = "来源ID", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:export')")
    public void exportWord(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType,
            HttpServletResponse response) {
        purchaseContractService.exportWord(id, reportCode,sourceCode,sourceType, companyId,response);
    }


    @PutMapping("/order-done")
    @Operation(summary = "完成单据",description = "完成单据")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:order-done')")
    public CommonResult<Boolean> orderDone(@RequestParam Long id) {
        purchaseContractService.setOrderDone( id);
        return success(true);
    }

    @GetMapping("/get-payment-plan-list")
    @Operation(summary = "获得采购合同付款计划列表")
    @Parameter(name = "code", description = "编号", required = true, example = "1024")
    public CommonResult<List<PurchasePaymentPlan>> getPurchaseContractPaymentPlanList(@RequestParam("code") String code) {
        return success(purchaseContractService.getPurchasePaymentPlanListByCode(code));
    }

    @PutMapping("/update-thumbnail")
    @Operation(summary = "更新缩略图")
    public CommonResult<Boolean> updateThumbnail() {
        purchaseContractService.updateThumbnail();
        return success(true);
    }

    @PutMapping("/update-code")
    @Operation(summary = "更改编号",description = "更改编号")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:update-code')")
    public CommonResult<Boolean> updateCode(@RequestParam("id")Long id,@RequestParam("code")String code) {
        purchaseContractService.updateCode(id, code);
        return success(true);
    }

    @PutMapping("/update-payment-plan")
    @Operation(summary = "修改付款计划")
    @PreAuthorize("@ss.hasPermission('scm:purchase-contract:update-payment-plan')")
    public CommonResult<Boolean> updatePaymentPlan(@RequestBody UpdatePaymentPlanReq req) {
        purchaseContractService.updatePaymentPlan(req);
        return success(true);
    }
}
