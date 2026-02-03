package com.syj.eplus.module.oa.controller.admin.travelreimb;

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
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbCloseReq;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbStandardRespVO;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import com.syj.eplus.module.oa.service.travelreimb.TravelReimbService;
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
import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_REIMVB_DELETE_ERROR;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_REIMVB_UPDATE_ERROR;

@Tag(name = "管理后台 - 出差报销")
@RestController
@RequestMapping("/oa/reimb/travel")
@Validated
public class TravelReimbController {

    @Resource
    private TravelReimbService travelReimbService;
    @Resource
    private ReimbService reimbService;

    @PostMapping("/create")
    @Operation(summary = "创建出差报销")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:create')")
    public CommonResult<Long> createTravelReimb(@Valid @RequestBody TravelReimbSaveReqVO createReqVO) {
        return success(travelReimbService.createTravelReimb(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出差报销")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:update')")
    public CommonResult<Boolean> updateTravelReimb(@Valid @RequestBody TravelReimbSaveReqVO updateReqVO) {
        int result = travelReimbService.updateTravelReimb(updateReqVO);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_UPDATE_ERROR);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出差报销")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:delete')")
    public CommonResult<Boolean> deleteTravelReimb(@RequestParam("id") Long id) {
        int result = travelReimbService.deleteTravelReimb(id);
        if (result > 0) {
            return success(true);
        }
        return error(TRAVEL_REIMVB_DELETE_ERROR);
    }

    @GetMapping("/detail")
    @Operation(summary = "通过报销单id获得出差报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:query')")
    public CommonResult<TravelReimbRespVO> getTravelReimb(@RequestParam("id") Long id) {
        TravelReimbRespVO travelReimb = travelReimbService.getTravelReimb(new TravelReimbDetailReq().setTravelReimbId(id));
        return success(travelReimb);
    }

    @GetMapping("/process-detail")
    @Operation(summary = "通过流程id获得出差报销详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<TravelReimbRespVO> getProcessTravelReimb(@RequestParam("id") String id) {
        TravelReimbRespVO travelReimb = travelReimbService.getTravelReimb(new TravelReimbDetailReq().setProcessInstanceId(id));
        return success(travelReimb);
    }

    @GetMapping("/get-lodging-subsidy")
    @Operation(summary = "获取住宿参考")
    public CommonResult<String> getLodgingSubsidy() {
        return success(travelReimbService.getLodgingSubsidy());
    }

    @GetMapping("/get-travel-standard")
    @Operation(summary = "获得出差标准")
    public CommonResult<List<ReimbStandardRespVO>> getTravelStandard() {
        List<ReimbStandardRespVO> travelStandard = travelReimbService.getTravelStandard();
        return success(travelStandard);
    }

    @GetMapping("/get-mileage-standard")
    @Operation(summary = "获得出差标准")
    public CommonResult<BigDecimal> getMileageStandard() {
        BigDecimal travelStandard = travelReimbService.getMileageStandard();
        return success(travelStandard);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        travelReimbService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        travelReimbService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long travelReimbId) {
        travelReimbService.submitTask(travelReimbId);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得出差报销分页")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:query')")
    public CommonResult<PageResult<ReimbRespVO>> getTravelReimbPage(@Valid ReimbPageReqVO pageReqVO) {
        PageResult<ReimbRespVO> reimbPage = reimbService.getReimbPage(pageReqVO, FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue());
        return success(reimbPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报销 Excel")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:export')")
    @OperateLog(type = EXPORT)
    public void exportTravelReimbExcel(@Valid ReimbPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReimbRespVO> list = reimbService.getReimbPage(pageReqVO,FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue()).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出差报销.xls", "数据", ReimbRespVO.class, list);
    }

    @PutMapping("/print")
    @Operation(summary = "打印报销回调")
    @PreAuthorize("@ss.hasPermission('oa:general-reimb:print')")
    public CommonResult<Boolean> printTravelReimb(@RequestParam Long id, @RequestParam Integer printStatus) {
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
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType) {
        String pdfFile = reimbService.print(id, reportCode, sourceCode, sourceType, companyId, FeeShareSourceTypeEnum.TRAVEL_REIMBURSE);
        return success(pdfFile);
    }

    @PutMapping("/close")
    @Operation(summary = "作废")
    @PreAuthorize("@ss.hasPermission('oa:travel-reimb:close')")
    public CommonResult<Boolean> close(@RequestBody ReimbCloseReq reimbCloseReq) {
        reimbCloseReq.setBusinessType(BusinessTypeEnum.TRAVEL_APP.getCode());
        reimbService.closeReimb(reimbCloseReq);
        return success(true);
    }

}
