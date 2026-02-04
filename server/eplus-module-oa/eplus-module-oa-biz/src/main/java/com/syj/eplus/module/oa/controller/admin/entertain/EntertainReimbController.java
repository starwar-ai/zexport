package com.syj.eplus.module.oa.controller.admin.entertain;

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
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbCloseReq;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.service.entertain.EntertainReimbService;
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
@Tag(name = "管理后台 - 招待费报销")
@RestController
@RequestMapping("/oa/reimb/entertain")
@Validated
public class EntertainReimbController {
    @Resource
    private EntertainReimbService entertainReimbService;
    @Resource
    private ReimbService reimbService;

    @PostMapping("/create")
    @Operation(summary = "创建招待费报销")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:create')")
    public CommonResult<Long> createEntertainReimb(@Valid @RequestBody EntertainReimbSaveReqVO createReqVO) {
        return success(entertainReimbService.createEntertainReimb(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新招待费报销")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:update')")
    public CommonResult<Boolean> updateEntertainReimb(@Valid @RequestBody EntertainReimbSaveReqVO updateReqVO) {
        int result = entertainReimbService.updateEntertainReimb(updateReqVO);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_UPDATE_ERROR);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除招待费报销")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:delete')")
    public CommonResult<Boolean> deleteEntertainReimb(@RequestParam("id") Long id) {
        int result = entertainReimbService.deleteEntertainReimb(id);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_DELETE_ERROR);
    }

    @GetMapping("/detail")
    @Operation(summary = "通过报销单id获得招待费报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:detail')")
    public CommonResult<EntertainReimbRespVO> getEntertainReimb(@RequestParam("id") Long id) {
        EntertainReimbRespVO entertainReimb = entertainReimbService.getEntertainReimb(new EntertainReimbDetailReq().setEntertainReimbId(id));
        return success(entertainReimb);
    }

    @GetMapping("/process-detail")
    @Operation(summary = "通过流程id获得招待费报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<EntertainReimbRespVO> getProcessEntertainReimb(@RequestParam("id") String id) {
        EntertainReimbRespVO entertainReimb = entertainReimbService.getEntertainReimb(new EntertainReimbDetailReq().setProcessInstanceId(id));
        return success(entertainReimb);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        entertainReimbService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        entertainReimbService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long entertainReimbId) {
        entertainReimbService.submitTask(entertainReimbId);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得出差报销分页")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:query')")
    public CommonResult<PageResult<EntertainReimbRespVO>> getEntertainReimbPage(@Valid ReimbPageReqVO pageReqVO) {
        PageResult<EntertainReimbRespVO> entertainReimbPage = entertainReimbService.getEntertainReimbPage(pageReqVO);
        return success(entertainReimbPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报销 Excel")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:export')")
    @OperateLog(type = EXPORT)
    public void exportEntertainReimbExcel(@Valid ReimbPageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EntertainReimbRespVO> list = entertainReimbService.getEntertainReimbPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出差报销.xls", "数据", EntertainReimbRespVO.class, list);
    }

    @PutMapping("/print")
    @Operation(summary = "打印报销回调")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:print')")
    public CommonResult<Boolean> printEntertainReimb(@RequestParam Long id, @RequestParam Integer printStatus) {
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
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = reimbService.print(id, reportCode, sourceCode, sourceType, companyId, FeeShareSourceTypeEnum.ENTERTAIN_REIMBURSE);
        return success(pdfFile);
    }


    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('oa:entertain-reimb:close')")
    public CommonResult<Boolean> close(@RequestBody ReimbCloseReq reimbCloseReq) {
        reimbCloseReq.setBusinessType(BusinessTypeEnum.ENTERTAIN_APP.getCode());
        reimbService.closeReimb(reimbCloseReq);
        return success(true);
    }
}
