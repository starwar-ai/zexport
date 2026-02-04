package com.syj.eplus.module.fms.controller.admin.receipt;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptDetailReq;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptPageReqVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptRespVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.service.receipt.ReceiptService;
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

@Tag(name = "管理后台 - 财务收款单")
@RestController
@RequestMapping("/fms/receipt")
@Validated
public class ReceiptController {

    @Resource
    private ReceiptService receiptService;

    @PostMapping("/create")
    @Operation(summary = "创建财务收款单")
    @PreAuthorize("@ss.hasPermission('fms:receipt:create')")
    public CommonResult<Long> createReceipt(@Valid @RequestBody ReceiptSaveReqVO createReqVO) {
        return success(receiptService.createReceipt(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新财务收款单")
    @PreAuthorize("@ss.hasPermission('fms:receipt:update')")
    public CommonResult<Boolean> updateReceipt(@Valid @RequestBody ReceiptSaveReqVO updateReqVO) {
        receiptService.updateReceipt(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除财务收款单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fms:receipt:delete')")
    public CommonResult<Boolean> deleteReceipt(@RequestParam("id") Long id) {
        receiptService.deleteReceipt(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得财务收款单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fms:receipt:query')")
    public CommonResult<ReceiptRespVO> getReceipt(@RequestParam("id") Long id) {
        ReceiptRespVO receipt = receiptService.getReceipt(new ReceiptDetailReq().setReceiptId(id));
        return success(receipt);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得财务收款单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<ReceiptRespVO> getReceiptByProcessId(@RequestParam("id") String id) {
        ReceiptRespVO receipt = receiptService.getReceipt(new ReceiptDetailReq().setProcessInstanceId(id));
        return success(receipt);
    }


    @GetMapping("/page")
    @Operation(summary = "获得财务收款单分页")
    @PreAuthorize("@ss.hasPermission('fms:receipt:query')")
    public CommonResult<PageResult<ReceiptRespVO>> getReceiptPage(@Valid ReceiptPageReqVO pageReqVO) {
        PageResult<ReceiptRespVO> receiptPage = receiptService.getReceiptPage(pageReqVO);
        return success(receiptPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出财务收款单 Excel")
    @PreAuthorize("@ss.hasPermission('fms:receipt:export')")
    @OperateLog(type = EXPORT)
    public void exportReceiptExcel(@Valid ReceiptPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReceiptRespVO> receiptRespVOList = receiptService.getReceiptPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "财务收款单.xls", "数据", ReceiptRespVO.class,
                receiptRespVOList);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('fms:receipt:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        receiptService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('fms:receipt:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        receiptService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('fms:receipt:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long receiptId) {
        receiptService.submitTask(receiptId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "收款确认")
    @PreAuthorize("@ss.hasPermission('fms:receipt:confirm')")
    public CommonResult<Boolean> confirmReceipt(@RequestParam Long receiptId) {
        receiptService.confirmReceipt(receiptId);
        return success(true);
    }
}