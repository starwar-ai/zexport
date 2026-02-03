package com.syj.eplus.module.oa.controller.admin.paymentapp;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.*;
import com.syj.eplus.module.oa.service.paymentapp.PaymentAppService;
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


@Tag(name = "管理后台 - 公对公申请")
@RestController
@RequestMapping("/oa/payment-app")
@Validated
public class PaymentAppController {

    @Resource
    private PaymentAppService paymentAppService;

    @PostMapping("/create")
    @Operation(summary = "创建公对公申请")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:create')")
    public CommonResult<List<CreatedResponse>> createPaymentApp(@Valid @RequestBody PaymentAppSaveReqVO createReqVO) throws Exception {
        return success(paymentAppService.createPaymentApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新公对公申请")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:update')")
    public CommonResult<Boolean> updatePaymentApp(@Valid @RequestBody PaymentAppSaveReqVO updateReqVO) {
        paymentAppService.updatePaymentApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公对公申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:payment-app:delete')")
    public CommonResult<Boolean> deletePaymentApp(@RequestParam("id") Long id) throws Exception {
        paymentAppService.deletePaymentApp(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得公对公申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:query')")
    public CommonResult<PaymentAppRespVO> getPaymentApp(@RequestParam("id") Long id) {
        PaymentAppRespVO paymentApp = paymentAppService.getPaymentApp(new PaymentAppDetailReq().setPaymentAppId(id));
        return success(paymentApp);
    }


    @GetMapping("/audit-detail")
    @Operation(summary = "获得公对公申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<PaymentAppRespVO> getPaymentApp(@RequestParam("id") String id) {
        PaymentAppRespVO paymentApp = paymentAppService.getPaymentApp(new PaymentAppDetailReq().setProcessInstanceId(id));
        return success(paymentApp);
    }

    @GetMapping("/page")
    @Operation(summary = "获得公对公申请分页")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:query')")
    public CommonResult<PageResult<PaymentAppRespVO>> getPaymentAppPage(@Valid PaymentAppPageReqVO pageReqVO) {
        PageResult<PaymentAppRespVO> pageResult = paymentAppService.getPaymentAppPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出公对公申请 Excel")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentAppExcel(@Valid PaymentAppPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PaymentAppRespVO> list = paymentAppService.getPaymentAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "公对公申请.xls", "数据", PaymentAppRespVO.class,list);
    }

    @GetMapping("/export-excel-send-detail")
    @Operation(summary = "导出寄件明细")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentAppSendDetailExcel(@Valid PaymentAppPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<PaymentAppSendDetailRespVO> list = paymentAppService.exportPaymentAppSendDetailExcel(pageReqVO.getId());
        ExcelUtils.write(response, "寄件导出明细.xls", "数据", PaymentAppSendDetailRespVO.class,list);
    }


    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        paymentAppService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        paymentAppService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:payment-app:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentAppId) {
        paymentAppService.submitTask(paymentAppId, WebFrameworkUtils.getLoginUserId());
        //更新为处理中
        paymentAppService.updateAuditStatus(paymentAppId, BpmProcessInstanceResultEnum.PROCESS.getResult());
        return success(true);
    }

    @GetMapping("/get-loan-list")
    @Operation(summary = "获取欠款单明细")
    public CommonResult<LoanVO> getPaymentAppPage(@RequestParam String venderCode) {
        LoanVO list = paymentAppService.getNotPayLoanList(venderCode);
        return success(list);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获取精简付款单列表")
    public CommonResult<List<SimplePaymentAppResp>> getSimplePaymentApp(@RequestParam Integer type,@RequestParam String code,@RequestParam List<String> codeList,@RequestParam Long companyId,@RequestParam Integer prepaidFlag) {
        List<SimplePaymentAppResp> simplePaymentAppList = paymentAppService.getSimplePaymentAppList(type, code,codeList,companyId,prepaidFlag);
        return success(simplePaymentAppList);
    }


    @GetMapping("/print")
    @Operation(summary = "打印")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @OperateLog(type = PRINT)
    @PreAuthorize("@ss.hasPermission('oa:payment-app:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = paymentAppService.print(id, reportCode, sourceCode, sourceType, companyId);
        return success(pdfFile);
    }



    @GetMapping("/print-shipment-fee-detail")
    @Operation(summary = "打印")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "companyId", description = "归属公司", example = "tudou"),
            @Parameter(name = "sourceCode", description = "来源code", example = "tudou"),
            @Parameter(name = "sourceType", description = "来源模板类型", example = "1")
    })
    @OperateLog(type = PRINT)
    @PreAuthorize("@ss.hasPermission('oa:payment-app:print')")
    public CommonResult<String> printShipmentDetail(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = paymentAppService.printShipmentDetail(id, reportCode, sourceCode, sourceType, companyId);
        return success(pdfFile);
    }
}