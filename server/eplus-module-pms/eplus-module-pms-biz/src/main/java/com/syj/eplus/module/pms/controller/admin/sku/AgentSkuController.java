package com.syj.eplus.module.pms.controller.admin.sku;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.service.sku.SkuService;
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

@Tag(name = "管理后台 - agent-sku")
@RestController
@RequestMapping("/pms/agent-sku")
@Validated
public class AgentSkuController {

    @Resource
    private SkuService skuService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @Operation(summary = "创建代理产品csku")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:create')")
    public CommonResult<Long> createSku(@Valid @RequestBody SkuInfoSaveReqVO createReqVO) {
        createReqVO.setCode(null);
        return success(skuService.createSku(createReqVO,true).getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新代理产品agent-sku")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:update')")
    public CommonResult<Boolean> updateSku(@Valid @RequestBody SkuInfoSaveReqVO updateReqVO) {
        skuService.updateSku(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除代理产品agent-sku")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:delete')")
    public CommonResult<Boolean> deleteSku(@RequestParam("id") Long id) {
        skuService.deleteSku(id, false);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得代理产品agent-sku")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:query')")
    public CommonResult<SkuInfoRespVO> getSku(@RequestParam("id") Long id) {
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setSkuId(id));
        return success(sku);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得代理产品agent-sku详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SkuInfoRespVO> getSkuByProcessId(@RequestParam("id") String id) {
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setProcessInstanceId(id));
        return success(sku);
    }

    @GetMapping("/page")
    @Operation(summary = "获得代理产品agent-sku分页")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:query')")
    public CommonResult<PageResult<CskuRespVO>> getCskuPage(@Valid SkuPageReqVO pageReqVO) {
        pageReqVO.setAgentFlag(BooleanEnum.YES.getValue());
        PageResult<CskuRespVO> cSkuPage = skuService.getCskuPage(pageReqVO);
        if (Objects.isNull(cSkuPage)) {
            return success(PageResult.empty());
        }
        List<CskuRespVO> skuList = cSkuPage.getList();
        if (CollUtil.isEmpty(skuList)) {
            return success(PageResult.empty());
        }
        return success(cSkuPage);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出代理产品sku Excel")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:export')")
    @OperateLog(type = EXPORT)
    public void exportCskuExcel(@Valid SkuPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CskuRespVO> list = skuService.getCskuPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "代理产品.xls", "数据", CskuRespVO.class,
                list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过客户sku任务")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:audit')")
    public CommonResult<Boolean> approveCskuTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        skuService.approveCskuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过代理产品任务")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:audit')")
    public CommonResult<Boolean> rejectCskuTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        skuService.rejectCskuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交代理产品任务")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:submit')")
    public CommonResult<Boolean> submitCskuTask(@RequestParam Long id) {
        skuService.submitCskuTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }



    @PutMapping("/change")
    @Operation(summary = "变更代理产品资料")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:change')")
    public CommonResult<Boolean> changeSku(@Valid @RequestBody SkuInfoSaveReqVO changeReqVO) {
        try {
            skuService.changeSku(changeReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(UPDATE_SKU_FAIL.getCode(), UPDATE_SKU_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得变更代理产品资料分页")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:query')")
    public CommonResult<PageResult<CskuRespVO>> getCskuChangePage(@Valid SkuPageReqVO pageReqVO) {
        pageReqVO.setAgentFlag(BooleanEnum.YES.getValue());
        PageResult<CskuRespVO> cSkuPage = skuService.getCskuChangePage(pageReqVO);
        List<CskuRespVO> skuList = cSkuPage == null ? null : cSkuPage.getList();
        if (CollUtil.isEmpty(skuList)) {
            return success(PageResult.empty());
        }
        return success(cSkuPage);
    }


    @PutMapping("/change-update")
    @Operation(summary = "更新变更代理产品资料")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:update')")
    public CommonResult<Boolean> updateChangeSku(@Valid @RequestBody SkuInfoSaveReqVO updateReqVO) {
        return updateSku(updateReqVO);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除变更代理产品资料")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:csku-change:delete')")
    public CommonResult<Boolean> deleteChangeSku(@RequestParam("id") Long id) {
        skuService.deleteChangeSku(id);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得变更代理产品详情")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SkuInfoRespVO> getChangeSkuById(@RequestParam("id") Long id) {
        SkuInfoRespVO skuInfoRespVO = skuService.getSkuChange(new SkuDetailReq().setSkuId(id));
        return success(skuInfoRespVO);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得变更代理产品详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<SkuInfoRespVO> getChangeSkuByProcessId(@RequestParam("id") String id) {
        SkuInfoRespVO skuInfoRespVO = skuService.getSkuChange(new SkuDetailReq().setProcessInstanceId(id));
        return success(skuInfoRespVO);
    }


    @PutMapping("/change-approve")
    @Operation(summary = "通过代理产品变更任务")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:audit')")
    public CommonResult<Boolean> approveChangeCskuTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        skuService.approveChangeCskuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过代理产品变更任务")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:audit')")
    public CommonResult<Boolean> rejectChangeCskuTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        skuService.rejectChangeCskuTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-submit")
    @Operation(summary = "提交代理产品变更任务")
    @PreAuthorize("@ss.hasPermission('pms:csku-change:submit')")
    public CommonResult<Boolean> submitChangeCskuTask(@RequestParam Long skuId) {
        skuService.submitChangeCskuTask(skuId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    @PutMapping("/pricing")
    @Operation(summary = "更新公司定价")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:pricing')")
    public CommonResult<Boolean> updatePricing(@RequestParam Long id, @RequestBody JsonAmount price) {
        skuService.updateCompanyPrice(id, price);
        return success(true);
    }

    @PutMapping("/set-onshelf-flag")
    @Operation(summary = "修改销售状态")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:onshelfFlag')")
    public CommonResult<Boolean> updateEnableFlag(@RequestParam Long id, @RequestParam Integer status) {
        skuService.updateOnshelfFlag(id, status);
        return success(true);
    }

    @PutMapping("/set-advantage")
    @Operation(summary = "修改优势产品")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:advantage')")
    public CommonResult<Boolean> updateAdvantage(@RequestParam Long id, @RequestParam Integer advantageFlag) {
        skuService.updateAdvantage(id, advantageFlag);
        return success(true);
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('pms:agent-sku:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long skuId) {
        return success(skuService.antiAudit(skuId));
    }
}
