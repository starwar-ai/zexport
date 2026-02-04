package com.syj.eplus.module.pms.controller.admin.sku;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
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
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.framework.common.enums.SkuTypeEnum;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.service.sku.SkuService;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.UPDATE_SKU_FAIL;

@Tag(name = "管理后台 - AuxiliarySku")
@RestController
@RequestMapping("/pms/auxiliary/sku")
@Validated
public class AuxiliarySkuController {

    @Resource
    private SkuService skuService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @Operation(summary = "创建辅料sku")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:create')")
    public CommonResult<Long> createSku(@Valid @RequestBody SkuInfoSaveReqVO createReqVO) {
        createReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        return success(skuService.createSku(createReqVO,true).getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新辅料sku")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:update')")
    public CommonResult<Boolean> updateSku(@Valid @RequestBody SkuInfoSaveReqVO updateReqVO) {
        updateReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        skuService.updateSku(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除辅料sku")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:delete')")
    public CommonResult<Boolean> deleteSku(@RequestParam("id") Long id) {
        SkuDTO skuDTO = skuService.getSkuDTO(id);
        if(Objects.equals(skuDTO.getSkuType(), SkuTypeEnum.AUXILIARY_MATERIALS.getValue())){
            skuService.deleteSku(id, false);
            return success(true);
        }
       return  success(false);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得辅料sku")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SkuInfoRespVO> getSku(@RequestParam("id") Long id) {
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setSkuId(id));
        if(SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(sku.getSkuType())){
            return success(sku);
        }
        return success(null);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得辅料sku详情")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:detail')")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SkuInfoRespVO> getSkuByProcessId(@RequestParam("id") String id) {
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setProcessInstanceId(id));
        if(SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(sku.getSkuType())){
            return success(sku);
        }
        return success(null);
    }
    @GetMapping("/page")
    @Operation(summary = "获得辅料sku分页")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:query')")
    public CommonResult<PageResult<SkuRespVO>> getCskuPage(@Valid SkuPageReqVO pageReqVO) {
        pageReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        PageResult<SkuRespVO> pageResult = skuService.getSkuPage(pageReqVO, false);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出辅料sku Excel")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:export')")
    @OperateLog(type = EXPORT)
    public void exportSkuExcel(@Valid SkuPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        List<SkuRespVO> list = skuService.getSkuPage(pageReqVO, false).getList();
        // 导出 Excel
        ExcelUtils.write(response, "sku.xls", "数据", SkuRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "辅料通过任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        skuService.approveAuxiliarySkuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "辅料不通过任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        skuService.rejectAuxiliarySkuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "辅料提交任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        skuService.submitAuxiliarySkuTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

//    @GetMapping("/simple-sku-name")
//    @Operation(summary = "获得辅料sku精简名称列表")
//    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:query')")
//    public CommonResult<List<String>> getSkuFuzzySearchName(@RequestParam String name) {
//        List<String> skuNameFuzzySearchByName = skuService.getSkuNameFuzzySearchByName(name);
//        return success(skuNameFuzzySearchByName);
//    }

    @GetMapping("/simple-sku")
    @Operation(summary = "获得精简辅料sku列表")
    public CommonResult<PageResult<SimpleSkuDTO>> getSimpleSkuDTO(SimpleSkuPageReqVO simpleSkuPageReqVO) {
        simpleSkuPageReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        PageResult<SimpleSkuDTO> simpleSkuDTO = skuService.getSimpleSkuDTO(simpleSkuPageReqVO);
        return success(simpleSkuDTO);
    }


    @GetMapping("/purchase-page")
    @Operation(summary = "获得辅料采购记录分页")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:query')")
    public CommonResult<PageResult<PurchaseContractItemDTO>> getSkuPurchasePage(@Valid PurchaseContractGetItemPageReqDTO reqDTO      ) {
        PageResult<PurchaseContractItemDTO> pageResult = skuService.getAuxiliarySkuPurchasePage(reqDTO);
        return success(pageResult);
    }

    @PutMapping("/change")
    @Operation(summary = "变更辅料")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:change')")
    public CommonResult<Boolean> changeSku(@Valid @RequestBody SkuInfoSaveReqVO changeReqVO) {
        try {
            changeReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
            skuService.changeSku(changeReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(UPDATE_SKU_FAIL.getCode(), UPDATE_SKU_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得变更辅料sku分页")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:query')")
    public CommonResult<PageResult<SkuRespVO>> getSkuChangePage(@Valid SkuPageReqVO pageReqVO) {
        pageReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        PageResult<SkuRespVO> pageResult = skuService.getSkuChangePage(pageReqVO, false);
        List<SkuRespVO> skuList = pageResult == null ? null : pageResult.getList();
        if (CollUtil.isEmpty(skuList)) {
            return success(PageResult.empty());
        }
        return success(pageResult);
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku:change')")
    @DataPermission(enable = false)
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody SkuInfoSaveReqVO changeReqVO) {
        changeReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        SkuInfoRespVO sku = BeanUtils.toBean(changeReqVO, SkuInfoRespVO.class);
        sku.setVer(sku.getVer() + 1);
        return success(skuService.getChangeEffect(sku));
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新变更辅料")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:update')")
    public CommonResult<Boolean> updateChangeSku(@Valid @RequestBody SkuInfoSaveReqVO updateReqVO) {
        updateReqVO.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        return updateSku(updateReqVO);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除变更辅料")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:delete')")
    public CommonResult<Boolean> deleteChangeSku(@RequestParam("id") Long id) {
        SkuDTO skuDTO = skuService.getSkuDTO(id);
        if(Objects.equals(skuDTO.getSkuType(), SkuTypeEnum.AUXILIARY_MATERIALS.getValue())){
            skuService.deleteChangeSku(id);
            return success(true);
        }
        return  success(false);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得变更辅料详情")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SkuInfoRespVO> getChangeSkuById(@RequestParam("id") Long id) {
        SkuInfoRespVO sku = skuService.getSkuChange(new SkuDetailReq().setSkuId(id));
        if(SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(sku.getSkuType())){
            return success(sku);
        }
        return success(null);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得变更辅料详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SkuInfoRespVO> getChangeSkuByProcessId(@RequestParam("id") String id) {
        SkuInfoRespVO sku = skuService.getSkuChange(new SkuDetailReq().setProcessInstanceId(id));
        if(SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(sku.getSkuType())){
            return success(sku);
        }
        return success(null);
    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        skuService.approveChangeAuxiliarySkuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        skuService.rejectChangeAuxiliarySkuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-submit")
    @Operation(summary = "提交变更任务")
    @PreAuthorize("@ss.hasPermission('pms:auxiliary-sku-change:submit')")
    public CommonResult<Boolean> submitChangeTask(@RequestParam Long skuId) {
        skuService.submitChangeAuxiliarySkuTask(skuId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }



}