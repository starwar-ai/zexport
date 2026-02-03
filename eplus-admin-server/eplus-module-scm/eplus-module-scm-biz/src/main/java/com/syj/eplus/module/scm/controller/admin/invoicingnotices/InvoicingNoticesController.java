package com.syj.eplus.module.scm.controller.admin.invoicingnotices;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.*;
import com.syj.eplus.module.scm.service.invoicingnotices.InvoicingNoticesService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "管理后台 - 开票通知")
@RestController
@RequestMapping("/scm/invoicing-notices")
@Validated
public class InvoicingNoticesController {

    @Resource
    private InvoicingNoticesService invoicingNoticesService;

    @PostMapping("/create")
    @Operation(summary = "创建开票通知")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:create')")
    public CommonResult<List<CreatedResponse>> createInvoicingNotices(@Valid @RequestBody InvoicingNoticesSaveReqVO createReqVO) {
        createReqVO.setManuallyFlag(BooleanEnum.YES.getValue());
        return success(invoicingNoticesService.createInvoicingNotices(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开票通知")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:update')")
    public CommonResult<Boolean> updateInvoicingNotices(@Valid @RequestBody InvoicingNoticesSaveReqVO updateReqVO) {
        invoicingNoticesService.updateInvoicingNotices(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开票通知")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:delete')")
    public CommonResult<Boolean> deleteInvoicingNotices(@RequestParam("id") Long id) {
        invoicingNoticesService.deleteInvoicingNotices(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得开票通知")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:query')")
    public CommonResult<InvoicingNoticesRespVO> getInvoicingNotices(@RequestParam("id") Long id) {
        InvoicingNoticesRespVO invoicingNotices = invoicingNoticesService.getInvoicingNotices(new InvoicingNoticesDetailReq().setInvoicingNoticesId(id));
        return success(invoicingNotices);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得开票通知详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <InvoicingNoticesRespVO> getInvoicingNoticesByProcessId(@RequestParam("id") String id) {
        InvoicingNoticesRespVO invoicingNotices = invoicingNoticesService.getInvoicingNotices(new InvoicingNoticesDetailReq().setProcessInstanceId(id));
        return success(invoicingNotices);
    }

    @GetMapping("/page")
    @Operation(summary = "获得开票通知分页")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:query')")
    public CommonResult<PageResult<InvoicingNoticesRespVO>> getInvoicingNoticesPage(@Valid InvoicingNoticesPageReqVO pageReqVO) {
        PageResult<InvoicingNoticesRespVO> pageResult = invoicingNoticesService.getInvoicingNoticesPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开票通知 Excel")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:export')")
    @OperateLog(type = EXPORT)
    public void exportInvoicingNoticesExcel(@Valid InvoicingNoticesPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<InvoicingNoticesRespVO> list = invoicingNoticesService.getInvoicingNoticesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开票通知.xls", "数据", InvoicingNoticesRespVO.class, list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        invoicingNoticesService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        invoicingNoticesService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        invoicingNoticesService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @GetMapping("/get-list")
    @Operation(summary = "获得开票通知列表")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:query')")
    public CommonResult<PageResult<InvoicingNoticesItemResp>> getInvoicingNotices(@Valid InvoicingNoticesPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<InvoicingNoticesItemResp> invoicingNoticesItemList = invoicingNoticesService.getInvoicingNoticesItemList(pageReqVO);
        return success(invoicingNoticesItemList);
    }

    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = invoicingNoticesService.print(id, reportCode, sourceCode, sourceType, companyId,false);
        return success(pdfFile);
    }

    @GetMapping("/merge-print")
    @Operation(summary = "合并打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:merge-print')")
    public CommonResult<String> mergePrint(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = invoicingNoticesService.print(id, reportCode, sourceCode, sourceType, companyId,true);
        return success(pdfFile);
    }

    @GetMapping("/getVenderListByShipInvoiceCode")
    @Operation(summary = "根据出运发票号获取供应商列表")
    @Parameter(name = "shipInvoiceCode", description = "出运发票号", required = true, example = "1024")
    public CommonResult<List<SimpleVenderRespDTO>> getVenderListByShipInvoiceCode(@RequestParam("shipInvoiceCode") String shipInvoiceCode) {
        List<SimpleVenderRespDTO> list = invoicingNoticesService.getVenderListByShipInvoiceCode(shipInvoiceCode);
        return success(list);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('scm:invoicing-notices:close')")
    public CommonResult<Boolean> closePaymentApply(@RequestParam Long id) {
        invoicingNoticesService.closeInvoicingNotices(id);
        return success(true);
    }
}