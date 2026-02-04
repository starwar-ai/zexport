package com.syj.eplus.module.oa.controller.admin.repayapp;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppDetailReq;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppSaveReqVO;
import com.syj.eplus.module.oa.service.repayapp.RepayAppService;
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

@Tag(name = "管理后台 - 还款单")
@RestController
@RequestMapping("/oa/repay-app")
@Validated
public class RepayAppController {

    @Resource
    private RepayAppService repayAppService;


    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long repayAppId) {
        repayAppService.submitTask(repayAppId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        repayAppService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        repayAppService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建还款单")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:create')")
    public CommonResult<Long> createRepayApp(@Valid @RequestBody RepayAppSaveReqVO createReqVO) {
        return success(repayAppService.createRepayApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新还款单")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:update')")
    public CommonResult<Boolean> updateRepayApp(@Valid @RequestBody RepayAppSaveReqVO updateReqVO) {
        repayAppService.updateRepayApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除还款单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:repay-app:delete')")
    public CommonResult<Boolean> deleteRepayApp(@RequestParam("id") Long id) {
        repayAppService.deleteRepayApp(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得还款单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:query')")
    public CommonResult<RepayAppRespVO> getRepayApp(@RequestParam("id") Long id) {
        RepayAppRespVO repayApp = repayAppService.getRepayApp(new RepayAppDetailReq().setRepayAppId(id));
        return success(repayApp);
    }

    @GetMapping("/detail-by-code")
    @Operation(summary = "获得还款单")
    @Parameter(name = "code", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:query')")
    public CommonResult<RepayAppRespVO> getRepayAppByCode(@RequestParam("code") String code) {
        RepayAppRespVO repayApp = repayAppService.getRepayApp(new RepayAppDetailReq().setCode(code));
        return success(repayApp);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "通过流程id获得还款申请单")
    @Parameter(name = "id", description = "流程id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:query')")
    public CommonResult<RepayAppRespVO> getProcessrepayApp(@RequestParam("id") String id) {
        RepayAppRespVO repayAppInfoRespVo = repayAppService.getRepayApp(new RepayAppDetailReq().setProcessInstanceId(id));
        return success(repayAppInfoRespVo);
    }

    @GetMapping("/page")
    @Operation(summary = "获得还款单分页")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:query')")
    public CommonResult<PageResult<RepayAppRespVO>> getRepayAppPage(@Valid RepayAppPageReqVO pageReqVO) {
        PageResult<RepayAppRespVO> repayAppPage = repayAppService.getRepayAppPage(pageReqVO);
        return success(repayAppPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出还款单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:repay-app:export')")
    @OperateLog(type = EXPORT)
    public void exportRepayAppExcel(@Valid RepayAppPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RepayAppRespVO> list = repayAppService.getRepayAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "还款单.xls", "数据", RepayAppRespVO.class,
                list);
    }


}