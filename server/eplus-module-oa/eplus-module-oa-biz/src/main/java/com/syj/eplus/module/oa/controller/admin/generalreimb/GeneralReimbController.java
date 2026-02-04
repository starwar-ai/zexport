package com.syj.eplus.module.oa.controller.admin.generalreimb;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbCloseReq;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.service.generalreimb.GeneralReimbService;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_REIMVB_DELETE_ERROR;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_REIMVB_UPDATE_ERROR;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 11:27
 */
@Tag(name = "管理后台 - 一般费用报销")
@RestController
@RequestMapping("/oa/reimb/general")
@Validated
public class GeneralReimbController {
    @Resource
    private GeneralReimbService generalReimbService;
    @Resource
    private ReimbService reimbService;

    @PostMapping("/create")
    @Operation(summary = "创建一般费用报销")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:create')")
    public CommonResult<Long> createGeneralReimb(@Valid @RequestBody GeneralReimbSaveReqVO createReqVO) {
        return success(generalReimbService.createGeneralReimb(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新一般费用报销")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:update')")
    public CommonResult<Boolean> updateGeneralReimb(@Valid @RequestBody GeneralReimbSaveReqVO updateReqVO) {
        int result = generalReimbService.updateGeneralReimb(updateReqVO);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_UPDATE_ERROR);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除一般费用报销")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:delete')")
    public CommonResult<Boolean> deleteGeneralReimb(@RequestParam("id") Long id) {
        int result = generalReimbService.deleteGeneralReimb(id);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_DELETE_ERROR);
    }

    @GetMapping("/detail")
    @Operation(summary = "通过报销单id获得一般费用报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:detail')")
    public CommonResult<GeneralReimbRespVO> getGeneralReimb(@RequestParam("id") Long id) {
        GeneralReimbRespVO generalReimb = generalReimbService.getGeneralReimb(new GeneralReimbDetailReq().setGeneralReimbId(id));
        return success(generalReimb);
    }

    @GetMapping("/process-detail")
    @Operation(summary = "通过流程id获得一般费用报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<GeneralReimbRespVO> getProcessGeneralReimb(@RequestParam("id") String id) {
        GeneralReimbRespVO generalReimb = generalReimbService.getGeneralReimb(new GeneralReimbDetailReq().setProcessInstanceId(id));
        return success(generalReimb);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        generalReimbService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        generalReimbService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long generalReimbId) {
        generalReimbService.submitTask(generalReimbId);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得出差报销分页")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:query')")
    public CommonResult<PageResult<GeneralReimbRespVO>> getGeneralReimbPage(@Valid ReimbPageReqVO pageReqVO) {
        PageResult<GeneralReimbRespVO> generalReimbPage = generalReimbService.getGeneralReimbPage(pageReqVO);
        return success(generalReimbPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报销 Excel")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:export')")
    @OperateLog(type = EXPORT)
    public void exportGeneralReimbExcel(@Valid ReimbPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeneralReimbRespVO> list = generalReimbService.getGeneralReimbPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出差报销.xls", "数据", GeneralReimbRespVO.class, list);
    }

    @PutMapping("/print")
    @Operation(summary = "打印报销回调")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:print')")
    public CommonResult<Boolean> printGeneralReimb(@RequestParam Long id, @RequestParam Integer printStatus) {
        reimbService.updatePrintTimes(id, printStatus);
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
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = reimbService.print(id, reportCode, sourceCode, sourceType, companyId, FeeShareSourceTypeEnum.GENERAL_REIMBURSE);
        return success(pdfFile);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:close')")
    public CommonResult<Boolean> close(@RequestBody ReimbCloseReq reimbCloseReq) {
        reimbCloseReq.setBusinessType(BusinessTypeEnum.GENERAL_APP.getCode());
        reimbService.closeReimb(reimbCloseReq);
        return success(true);
    }
}
