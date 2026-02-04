package com.syj.eplus.module.sms.controller.admin.quotation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.*;
import com.syj.eplus.module.sms.service.quotation.QuotationService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 报价单主")
@RestController
@RequestMapping("/sms/quotation")
@Validated
public class QuotationController {

    @Resource
    private QuotationService quotationService;

    @PostMapping("/create")
    @Operation(summary = "创建报价单")
    @PreAuthorize("@ss.hasPermission('sms:quotation:create')")
    public CommonResult<Long> createQuotation(@Valid @RequestBody QuotationSaveReqVO createReqVO) {
        return success(quotationService.createQuotation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报价单")
    @PreAuthorize("@ss.hasPermission('sms:quotation:update')")
    public CommonResult<Boolean> updateQuotation(@Valid @RequestBody QuotationSaveReqVO updateReqVO) {
        quotationService.updateQuotation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报价单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:quotation:delete')")
    public CommonResult<Boolean> deleteQuotation(@RequestParam("id") Long id) {
        quotationService.deleteQuotation(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得报价单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:quotation:query')")
    public CommonResult<QuotationRespVO> getQuotation(@RequestParam("id") Long id) {
            QuotationRespVO quotation = quotationService.getQuotation (new QuotationDetailReq().setQuotationId(id));
            return success(quotation);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得报价单主详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult <QuotationRespVO> getQuotationByProcessId(@RequestParam("id") String id) {
        QuotationRespVO quotation = quotationService.getQuotation (new QuotationDetailReq().setProcessInstanceId(id));
        return success(quotation);
    }

    @GetMapping("/page")
    @Operation(summary = "获得报价单主分页")
    @PreAuthorize("@ss.hasPermission('sms:quotation:query')")
    public CommonResult<PageResult<QuotationRespVO>> getQuotationPage(@Valid QuotationPageReqVO pageReqVO) {
        PageResult<QuotationRespVO> pageResult = quotationService.getQuotationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, QuotationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报价单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportId", required = false, description = "模板id", example = "tudou"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "unit", required = true, description = "计量单位", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('sms:quotation:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("unit") String unit,
            HttpServletResponse response) {
        quotationService.exportExcel(id, reportId,reportCode,unit,response);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('sms:quotation:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        quotationService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('sms:quotation:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        quotationService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('sms:quotation:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long quotationId) {
        quotationService.submitTask(quotationId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    @PutMapping("/finish")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('sms:quotation:finish')")
    public CommonResult<Boolean> finish(@RequestParam Long id) {
        quotationService.finish(id);
        return success(true);
    }

    @GetMapping("/item-page")
    @Operation(summary = "获得报价单子分页")
    public CommonResult<PageResult<QuotationItemRespVO>> getQuotationItemPage(@Valid QuotationItemPageReqVO pageReqVO) {
        PageResult<QuotationItemRespVO> quotationItemPage = quotationService.getQuotationItemPage(pageReqVO);
        return success(quotationItemPage);
    }

    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "报价单id", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "打印类型", example = "1"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "1"),
            @Parameter(name = "unit", required = true, description = "计量单位", example = "tudou")
    })
    @PreAuthorize("@ss.hasPermission('sms:quotation:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "totalPurchaseFlag") Integer totalPurchaseFlag,
            @RequestParam(value = "unit") String unit) {
        String pdfFile = quotationService.print(id, reportCode, reportId, companyId, totalPurchaseFlag, unit);
        return success(pdfFile);
    }
}