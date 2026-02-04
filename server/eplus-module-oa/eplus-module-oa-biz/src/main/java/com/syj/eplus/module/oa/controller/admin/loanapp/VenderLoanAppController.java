package com.syj.eplus.module.oa.controller.admin.loanapp;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.enums.VenderLoanSourceEnum;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.*;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 供应商借款申请单")
@RestController
@RequestMapping("/scm/vender-loan")
@Validated
public class VenderLoanAppController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private LoanAppService loanAppService;

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long loanAppId) {
        loanAppService.submitTask(loanAppId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        loanAppService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        loanAppService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建供应商借款申请单")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:create')")
    public CommonResult<Long> createLoanApp(@Valid @RequestBody LoanAppSaveReqVO createReqVO) {
        Long loanId;
        try {
            createReqVO.setLoanSource(VenderLoanSourceEnum.VENDER.getValue());
            loanId = loanAppService.createLoanApp(createReqVO);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return error(new ErrorCode(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg() + "：" + e.getMessage()));
        }
        return success(loanId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商借款申请单")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:update')")
    public CommonResult<Boolean> updateLoanApp(@Valid @RequestBody LoanAppSaveReqVO updateReqVO) {
        try {
            updateReqVO.setLoanSource(VenderLoanSourceEnum.VENDER.getValue());
            loanAppService.updateLoanApp(updateReqVO);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return error(new ErrorCode(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg() + "：" + e.getMessage()));
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商借款申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:delete')")
    public CommonResult<Boolean> deleteLoanApp(@RequestParam("id") Long id) {
        LoanAppDO appDo = loanAppService.getLoanAppById(id);
        if(Objects.isNull(appDo)){
            return success(false);
        }
        if(Objects.equals(VenderLoanSourceEnum.VENDER.getValue(),appDo.getLoanSource() )){
            loanAppService.deleteLoanApp(id);
            return success(true);
        }
        return success(false);
    }

    @GetMapping("/detail")
    @Operation(summary = "通过供应商借款单id获得借款申请单")
    @Parameter(name = "id", description = "借款申请单id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:query')")
    public CommonResult<LoanAppInfoRespVo> getLoanAppDetail(@RequestParam("id") Long id) {
        LoanAppInfoRespVo loanAppInfoRespVo = loanAppService.getLoanApp(new LoanAppDetailReq().setLoanAppId(id));
        if(Objects.isNull(loanAppInfoRespVo)){
            return success(null);
        }
        if(Objects.equals(loanAppInfoRespVo.getLoanSource(),VenderLoanSourceEnum.VENDER.getValue())){
            return success(loanAppInfoRespVo);
        }
      return  success(null);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "通过流程id获得供应商借款申请单")
    @Parameter(name = "id", description = "流程id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:query')")
    public CommonResult<LoanAppRespVO> getProcessLoanApp(@RequestParam("id") String id) {
        LoanAppRespVO loanAppInfoRespVo = loanAppService.getLoanApp(new LoanAppDetailReq().setProcessInstanceId(id));
        return success(loanAppInfoRespVo);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得供应商借款申请单精简列表")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleLoanAppRespDTO>> getLoanApp(LoanAppPageReqVO pageReqVO) {
        pageReqVO.setLoanSource(VenderLoanSourceEnum.VENDER.getValue());
        PageResult<SimpleLoanAppRespDTO> simpleLoanAppRespDTOListByApplyerId = loanAppService.getSimpleLoanAppRespDTOListByApplyerId(pageReqVO);
        return success(simpleLoanAppRespDTOListByApplyerId);
    }

    @GetMapping("/page")
    @Operation(summary = "获得供应商借款申请单分页")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:query')")
    public CommonResult<PageResult<LoanAppRespVO>> getLoanAppPage(@Valid LoanAppPageReqVO pageReqVO) {
        pageReqVO.setLoanSource(VenderLoanSourceEnum.VENDER.getValue());
        PageResult<LoanAppRespVO> loanAppPage = loanAppService.getLoanAppPage(pageReqVO);
        return success(loanAppPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商借款申请单 Excel")
    @PreAuthorize("@ss.hasPermission('scm:vender-loan:export')")
    @OperateLog(type = EXPORT)
    public void exportLoanAppExcel(@Valid LoanAppPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setLoanSource(VenderLoanSourceEnum.VENDER.getValue());
        List<LoanAppRespVO> list = loanAppService.getLoanAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "借款申请单.xls", "数据", LoanAppRespVO.class,
                list);
    }
}