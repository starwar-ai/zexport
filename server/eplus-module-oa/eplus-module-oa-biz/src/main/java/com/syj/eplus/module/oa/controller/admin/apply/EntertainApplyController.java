package com.syj.eplus.module.oa.controller.admin.apply;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.framework.common.enums.ReimbTypeEnum;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyDetailReq;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplySaveReqVO;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - OA申请单")
@RestController
@RequestMapping("/oa/entertain-apply")
public class EntertainApplyController {

    @Resource
    private ApplyService applyService;

    @PostMapping("/create")
    @Operation(summary = "创建招待报销申请单")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:query')")
    public CommonResult<Long> createApply(@Valid @RequestBody ApplySaveReqVO createReqVO) {
        // 设置申请单类型为招待费类型
        createReqVO.setApplyType(ReimbTypeEnum.ENTERTAIN.getValue());
        return success(applyService.createApply(createReqVO,FeeShareSourceTypeEnum.ENTERTAIN_APPLY));
    }

    @PutMapping("/update")
    @Operation(summary = "更新招待费报销申请单")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:update')")
    public CommonResult<Boolean> updateApply(@Valid @RequestBody ApplySaveReqVO updateReqVO) {
        // 设置申请单类型为招待费类型
        updateReqVO.setApplyType(ReimbTypeEnum.ENTERTAIN.getValue());
        applyService.updateApply(updateReqVO,FeeShareSourceTypeEnum.ENTERTAIN_APPLY);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除招待费报销申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:delete')")
    public CommonResult<Boolean> deleteApply(@RequestParam("id") Long id) {
        applyService.deleteApply(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得招待费报销申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:query')")
    public CommonResult<ApplyRespVO> getApply(@RequestParam("id") Long id) {
            ApplyRespVO apply = applyService.getApply (new ApplyDetailReq().setApplyId(id),FeeShareSourceTypeEnum.ENTERTAIN_APPLY);
            return success(apply);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得招待费报销申请单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult  <ApplyRespVO> getApplyByProcessId(@RequestParam("id") String id) {
        ApplyRespVO apply = applyService.getApply(new ApplyDetailReq().setProcessInstanceId(id),FeeShareSourceTypeEnum.ENTERTAIN_APPLY);
        return success(apply);
    }
    @GetMapping("/page")
    @Operation(summary = "获得招待费报销申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:query')")
    public CommonResult<PageResult<ApplyRespVO>> getApplyPage(@Valid ApplyPageReqVO pageReqVO) {
        // 设置申请单类型为招待费类型进行过滤
        pageReqVO.setApplyType(ReimbTypeEnum.ENTERTAIN.getValue());
        return success( applyService.getApplyPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出招待费报销申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:export')")
    @OperateLog(type = EXPORT)
    public void exportApplyExcel(@Valid ApplyPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        // 设置申请单类型为招待费类型进行过滤
        pageReqVO.setApplyType(ReimbTypeEnum.ENTERTAIN.getValue());
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApplyRespVO> list = applyService.getApplyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "招待费报销申请单.xls", "数据", ApplyRespVO.class,list);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        applyService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        applyService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        applyService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId(),ReimbTypeEnum.ENTERTAIN.getValue());
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('oa:entertain-apply:close')")
    public CommonResult<Boolean> close(@RequestParam("id") Long id) {
        applyService.closepApply(id);
        return success(true);
    }
}