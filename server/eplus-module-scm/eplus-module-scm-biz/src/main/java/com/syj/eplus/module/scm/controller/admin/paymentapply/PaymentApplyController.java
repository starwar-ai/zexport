package com.syj.eplus.module.scm.controller.admin.paymentapply;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import com.syj.eplus.module.scm.service.paymentapply.PaymentApplyService;
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
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 付款申请")
@RestController
@RequestMapping("/scm/payment-apply")
@Validated
public class PaymentApplyController {

    @Resource
    private PaymentApplyService paymentApplyService;

    @PostMapping("/create")
    @Operation(summary = "创建付款申请")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:create')")
    public CommonResult<List<CreatedResponse>> createPaymentApply(@Valid @RequestBody PaymentApplySaveReqVO createReqVO) {
        return success(paymentApplyService.createPaymentApply(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新付款申请")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:update')")
    public CommonResult<Boolean> updatePaymentApply(@Valid @RequestBody PaymentApplySaveReqVO updateReqVO) {
        paymentApplyService.updatePaymentApply(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除付款申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:delete')")
    public CommonResult<Boolean> deletePaymentApply(@RequestParam("id") Long id) {
        paymentApplyService.deletePaymentApply(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得付款申请详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:query')")
    public CommonResult<PaymentApplyRespVO> getPaymentApply(@RequestParam("id") Long id) {
        PaymentApplyRespVO paymentApply = paymentApplyService.getPaymentApply
                (new PaymentApplyDetailReq().setPaymentApplyId(id));
        return success(paymentApply);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得付款申请详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult
            <PaymentApplyRespVO> getPaymentApplyByProcessId(@RequestParam("id")
                                                            String id) {
        PaymentApplyRespVO paymentApply = paymentApplyService.getPaymentApply
                (new PaymentApplyDetailReq().setProcessInstanceId(id));
        return success(paymentApply);
    }

    @GetMapping("/page")
    @Operation(summary = "获得付款申请主分页")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:query')")
    public CommonResult<PageResult<PaymentApplyRespVO>> getPaymentApplyPage(@Valid PaymentApplyPageReqVO pageReqVO) {
        PageResult<PaymentApplyDO> pageResult = paymentApplyService.getPaymentApplyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PaymentApplyRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出付款申请主 Excel")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentApplyExcel(@Valid PaymentApplyPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PaymentApplyDO> list = paymentApplyService.getPaymentApplyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "付款申请主.xls", "数据", PaymentApplyRespVO.class,
                BeanUtils.toBean(list, PaymentApplyRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        paymentApplyService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        paymentApplyService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        paymentApplyService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @GetMapping("/message")
    @Operation(summary = "付款申请中间页")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:query')")
    public CommonResult<PaymentApplyRespVO> getPaymentApply(@RequestParam("idList") List<Long> idList,@RequestParam("step")Integer step) {
        PaymentApplyRespVO result = paymentApplyService.getPaymentApplyMessage
                (idList,step);
        return success(result);
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "取消流程实例", description = "撤回发起的流程")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:cancel')")
    public CommonResult<Boolean> cancelProcessInstance(@RequestParam("processInstanceId") String processInstanceId) {
        paymentApplyService.cancelProcessInstance(getLoginUserId(), processInstanceId);
        return success(true);
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long id) {
        paymentApplyService.antiAudit(id);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:close')")
    public CommonResult<Boolean> closePaymentApply(@RequestBody ClosePaymentApplyReq closePaymentApplyReq) {
        closePaymentApplyReq.setBusinessType(BusinessTypeEnum.PURCHASE_PAYMENT.getCode());
        paymentApplyService.closePaymentApply(closePaymentApplyReq);
        return success(true);
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
    @PreAuthorize("@ss.hasPermission('scm:payment-apply:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = paymentApplyService.print(id, reportCode, sourceCode, sourceType, companyId);
        return success(pdfFile);
    }
}