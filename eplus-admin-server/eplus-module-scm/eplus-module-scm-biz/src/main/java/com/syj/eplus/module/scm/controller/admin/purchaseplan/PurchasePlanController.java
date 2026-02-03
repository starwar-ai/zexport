package com.syj.eplus.module.scm.controller.admin.purchaseplan;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemToContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemToContractSaveInfoReqVO;
import com.syj.eplus.module.scm.service.purchaseplan.PurchasePlanService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 采购计划")
@RestController
@RequestMapping("/scm/purchase-plan")
@Validated
public class PurchasePlanController {

    @Resource
    private PurchasePlanService purchasePlanService;

    @PostMapping("/create")
    @Operation(summary = "创建采购计划")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:create')")
    public CommonResult<List<CreatedResponse>> createPurchasePlan(@Valid @RequestBody PurchasePlanInfoSaveReqVO createReqVO) {
        createReqVO.setAuxiliaryFlag(0);
        return success(purchasePlanService.createPurchasePlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购计划")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:update')")
    public CommonResult<Boolean> updatePurchasePlan(@Valid @RequestBody PurchasePlanInfoSaveReqVO updateReqVO) {
        updateReqVO.setAuxiliaryFlag(0);
        purchasePlanService.updatePurchasePlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:delete')")
    public CommonResult<Boolean> deletePurchasePlan(@RequestParam("id") Long id) {
        CommonResult<PurchasePlanInfoRespVO> purchasePlan = getPurchasePlan(id);
        if (purchasePlan.getData().getAuxiliaryFlag() == 0) {
            purchasePlanService.deletePurchasePlan(id);
            return success(true);
        } else {
            return success(null);
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "获得采购计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:query')")
    public CommonResult<PurchasePlanInfoRespVO> getPurchasePlan(@RequestParam("id") Long id) {
        PurchasePlanInfoRespVO purchasePlan = purchasePlanService.getPurchasePlanContainsContract
                (new PurchasePlanDetailReq().setPurchasePlanId(id));
        if (purchasePlan.getAuxiliaryFlag() == 0) {
            return success(purchasePlan);
        } else {
            return success(null);
        }

    }


    @GetMapping("/productMixdetail")
    @Operation(summary = "获取组套件拆分产品列表")
    @Parameter(name = "planId", description = "编号", required = true, example = "1024")
    @Parameter(name = "purchaseCompanyId", description = "编号", required = true, example = "1024")
    @Parameter(name = "planItemSkuId", description = "编号", required = true, example = "1024")
    @Parameter(name = "demergeQuantity", description = "产品待拆分数量", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:query')")
    public CommonResult<List<PurchasePlanItemToContractItemRespVO>> getPurchaseMixPlanItem(@RequestParam("planId") Long planId,
                                                                                           @RequestParam("purchaseCompanyId") Long purchaseCompanyId,
                                                                                           @RequestParam("planItemSkuCode") String planItemSkuCode,
                                                                                           @RequestParam("demergeQuantity") Integer demergeQuantity) {
        List<PurchasePlanItemToContractItemRespVO> purchasePlanItemRespVOList = purchasePlanService.getPurchaseMixPlanItem(planId,purchaseCompanyId,planItemSkuCode,demergeQuantity);
        return success(purchasePlanItemRespVOList);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得采购计划详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PurchasePlanRespVO> getPurchasePlanByProcessId(@RequestParam("id")
                                                            String id) {
        PurchasePlanRespVO purchasePlan = purchasePlanService.getPurchasePlanContainsContract
                (new PurchasePlanDetailReq().setProcessInstanceId(id));
        if (purchasePlan.getAuxiliaryFlag() == 0) {
            return success(purchasePlan);
        } else {
            return success(null);
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购计划分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:query')")
    public CommonResult<PageResult<PurchasePlanInfoRespVO>> getPurchasePlanPage(@Valid PurchasePlanPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(0);
        PageResult<PurchasePlanInfoRespVO> pageResult = purchasePlanService.getPurchasePlanPage(pageReqVO);
        return success(pageResult);
    }


    @GetMapping("/page-board")
    @Operation(summary = "获得采购计划看板分页")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:board')")
    public CommonResult<PageResult<PurchasePlanItemInfoRespVO>> getPurchasePlanBoardPage(@Valid PurchasePlanPageReqVO pageReqVO) {
        pageReqVO.setAuxiliaryFlag(0);
        PageResult<PurchasePlanItemInfoRespVO> pageResult = purchasePlanService.getPurchasePlanBoardPage(pageReqVO);
        return success(pageResult);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出采购计划 Excel")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:export')")
    @OperateLog(type = EXPORT)
    public void exportPurchasePlanExcel(@Valid PurchasePlanPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setAuxiliaryFlag(0);
        List<PurchasePlanInfoRespVO> list = purchasePlanService.getPurchasePlanPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购计划.xls", "数据", PurchasePlanInfoRespVO.class,
                BeanUtils.toBean(list, PurchasePlanInfoRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        purchasePlanService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        purchasePlanService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        boolean ownBrandFlag = purchasePlanService.checkOwnBrandFlag(paymentId);
        Map<String,Object> variables = new HashMap<>();
        if (ownBrandFlag){
            variables.put(purchasePlanService.getConditionKey(), 1);
        }else {
            variables.put(purchasePlanService.getConditionKey(), 0);
        }
        purchasePlanService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId(), purchasePlanService.getProcessDefinitionKey(),variables);
        return success(true);
    }

    @PutMapping("/batch-finish")
    @Operation(summary = "批量作废")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:finish')")
    public CommonResult<Boolean> submitTask(@RequestParam List<Long> planIdList) {
        purchasePlanService.finishPurchasePlan(planIdList,true);
        return success(true);
    }

    @PostMapping("/to-contract-list")
    @Operation(summary = "采购计划通过id列表转采购合同")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:contract')")
    public CommonResult<Boolean> toContractTask(@RequestParam List<Long> planItemIdList) {
        purchasePlanService.purchasePlanItemToContract(planItemIdList);
        return success(true);
    }

    /*
    @GetMapping("/to-contract-list")
    @Operation(summary = "获取转采购合同列表")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:contract')")
    public CommonResult<List<PurchasePlanItemToContractRespVO>> getPurchasePlanToContractList(@RequestParam List<Long> planItemIdList) {
        List<PurchasePlanItemToContractRespVO> pageResult = purchasePlanService.getPurchasePlanToContractList(planItemIdList);
        return success(pageResult);
    }*/


    @PostMapping("/to-contract-save")
    @Operation(summary = "采购计划通过列表转采购合同")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:contract')")
    @DataPermission(enable = false)
    public CommonResult<List<CreatedResponse>> toContractByListTask(@RequestBody PurchasePlanItemToContractSaveInfoReqVO reqVOList) {
        return success( purchasePlanService.purchasePlanToContract(reqVOList));
    }

    @GetMapping("/related-num")
    @Operation(summary = "关联单据数量")
    public CommonResult<RelatedOrderRespVO> getRelatedNum(@RequestParam Long id) {
        return success(purchasePlanService.getRelatedOrderNum(id));
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('scm:purchase-plan:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long id) {
        return success(purchasePlanService.antiAudit(id));
    }
}
