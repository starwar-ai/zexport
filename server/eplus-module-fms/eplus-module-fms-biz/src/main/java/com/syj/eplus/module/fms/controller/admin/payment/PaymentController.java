package com.syj.eplus.module.fms.controller.admin.payment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.*;
import com.syj.eplus.module.fms.service.payment.PaymentService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 财务付款表")
@RestController
@RequestMapping("/fms/payment")
@Validated
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @PostMapping("/create")
    @Operation(summary = "创建财务付款表")
    @PreAuthorize("@ss.hasPermission('fms:payment:create')")
    public CommonResult<Long> createPayment(@Valid @RequestBody PaymentSaveReqVO createReqVO) {
        return success(paymentService.createPayment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新财务付款表")
    @PreAuthorize("@ss.hasPermission('fms:payment:update')")
    public CommonResult<Boolean> updatePayment(@Valid @RequestBody PaymentSaveReqVO updateReqVO) {
        paymentService.updatePayment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除财务付款表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fms:payment:delete')")
    public CommonResult<Boolean> deletePayment(@RequestParam("id") Long id) {
        paymentService.deletePayment(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得财务付款表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fms:payment:query')")
    public CommonResult<PaymentRespVO> getPayment(@RequestParam("id") Long id) {
        PaymentRespVO payment = paymentService.getPayment(new PaymentDetailReq().setPaymentId(id));
        return success(payment);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得财务付款详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<PaymentRespVO> getCustByProcessId(@RequestParam("id") String id) {
        PaymentRespVO payment = paymentService.getPayment(new PaymentDetailReq().setProcessInstanceId(id));
        return success(payment);
    }

    @GetMapping("/page")
    @Operation(summary = "获得财务付款表分页")
    @PreAuthorize("@ss.hasPermission('fms:payment:query')")
    public CommonResult<PageResult<PaymentRespVO>> getPaymentPage(@Valid PaymentPageReqVO pageReqVO) {
        PageResult<PaymentRespVO> pageResult = paymentService.getPaymentPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出财务付款表 Excel")
    @PreAuthorize("@ss.hasPermission('fms:payment:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentExcel(@Valid PaymentPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PaymentRespVO> pageResult = paymentService.getPaymentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "财务付款表.xls", "数据", PaymentRespVO.class,
                pageResult);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('fms:payment:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        paymentService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('fms:payment:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        paymentService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('fms:payment:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        paymentService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "付款确认")
    @PreAuthorize("@ss.hasPermission('fms:payment:confirm')")
    public CommonResult<Boolean> confirmPayment(@Valid @RequestBody PaymentConfirmReqVO reqVO) throws Exception {
        paymentService.confirmPayment(reqVO);
        return success(true);
    }

    @PutMapping("/plan-payment")
    @Operation(summary = "计划付款")
    @PreAuthorize("@ss.hasPermission('fms:payment:plan-payment')")
    public CommonResult<Boolean> planPayment(@Valid @RequestBody PlanPaymentReqVO reqVO) {
        paymentService.planPayment(reqVO);
        return success(true);
    }

    @PutMapping("/plan-payment-cancel")
    @Operation(summary = "计划付款取消")
    @PreAuthorize("@ss.hasPermission('fms:payment:plan-payment')")
    public CommonResult<Boolean> planPaymentCancel(@RequestParam Long id) {
        paymentService.planPaymentCancel(id);
        return success(true);
    }
    @PutMapping("/batch-plan-payment")
    @Operation(summary = "批量计划付款")
    @PreAuthorize("@ss.hasPermission('fms:payment:plan-payment')")
    public CommonResult<Boolean> batchPlanPayment(@Valid @RequestBody List<PlanPaymentReqVO> reqVO) {
        paymentService.batchPlanPayment(reqVO);
        return success(true);
    }


    @PutMapping("/batch-confirm")
    @Operation(summary = "直接付款，确认付款-支持单个公司多笔付款")
    @PreAuthorize("@ss.hasPermission('fms:payment:confirm')")
    public CommonResult<Boolean> batchConfirmPayment(@Valid @RequestBody BatchPaymentConfirmReqVO reqVO) {
        paymentService.batchConfirmPayment(reqVO);
        return success(true);
    }

    @PutMapping("/batch-payment")
    @Operation(summary = "批量付款操作-支持不同公司多笔付款")
    @PreAuthorize("@ss.hasPermission('fms:payment:confirm')")
    public CommonResult<Boolean> batchPayment(@Valid @RequestBody List<BatchPaymentReqVO> reqVO) {
        paymentService.batchPayment(reqVO);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "付款单作废",description = "付款单作废")
    @PreAuthorize("@ss.hasPermission('fms:payment:close')")
    public CommonResult<Boolean> closePayment(@RequestParam Long id) {
        paymentService.closePayment(id);
        return success(true);
    }
}