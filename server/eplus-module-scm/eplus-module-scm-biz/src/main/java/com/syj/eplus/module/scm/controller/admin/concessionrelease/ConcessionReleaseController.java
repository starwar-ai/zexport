package com.syj.eplus.module.scm.controller.admin.concessionrelease;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseDetailReq;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleasePageReqVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseRespVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.concessionrelease.ConcessionReleaseDO;
import com.syj.eplus.module.scm.service.concessionrelease.ConcessionReleaseService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 让步放行")
@RestController
@RequestMapping("/scm/concession-release")
@Validated
public class ConcessionReleaseController {

    @Resource
    private ConcessionReleaseService concessionReleaseService;

    @PostMapping("/create")
    @Operation(summary = "创建让步放行")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:create')")
    public CommonResult<List<CreatedResponse>> createConcessionRelease(@Valid @RequestBody ConcessionReleaseSaveReqVO createReqVO) {
        return success(concessionReleaseService.createConcessionRelease(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新让步放行")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:update')")
    public CommonResult<Boolean> updateConcessionRelease(@Valid @RequestBody ConcessionReleaseSaveReqVO updateReqVO) {
        concessionReleaseService.updateConcessionRelease(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除让步放行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:concession-release:delete')")
    public CommonResult<Boolean> deleteConcessionRelease(@RequestParam("id") Long id) {
        concessionReleaseService.deleteConcessionRelease(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得让步放行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:query')")
    public CommonResult<ConcessionReleaseRespVO> getConcessionRelease(@RequestParam("id") Long id) {
        ConcessionReleaseRespVO concessionRelease = concessionReleaseService.getConcessionRelease(new ConcessionReleaseDetailReq().setConcessionReleaseId(id));
        return success(concessionRelease);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得让步放行详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<ConcessionReleaseRespVO> getConcessionReleaseByProcessId(@RequestParam("id")String id) {
        ConcessionReleaseRespVO concessionRelease = concessionReleaseService.getConcessionRelease
                (new ConcessionReleaseDetailReq().setProcessInstanceId(id));
        return success(concessionRelease);
    }
    @GetMapping("/page")
    @Operation(summary = "获得让步放行分页")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:query')")
    public CommonResult<PageResult<ConcessionReleaseRespVO>> getConcessionReleasePage(@Valid ConcessionReleasePageReqVO pageReqVO) {
        PageResult<ConcessionReleaseRespVO> pageResult = concessionReleaseService.getConcessionReleasePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出让步放行 Excel")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:export')")
    @OperateLog(type = EXPORT)
    public void exportConcessionReleaseExcel(@Valid ConcessionReleasePageReqVO pageReqVO,HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ConcessionReleaseRespVO> list = concessionReleaseService.getConcessionReleasePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "让步放行.xls", "数据", ConcessionReleaseRespVO.class,list );
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        concessionReleaseService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        concessionReleaseService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:concession-release:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        ConcessionReleaseDO concessionReleaseDO = concessionReleaseService.getConcessionReleaseById(id);
        if(Objects.isNull(concessionReleaseDO)){
            return success(false);
        }
        Map<String,Object> variable = new HashMap<>();
        if (Objects.equals(concessionReleaseDO.getAnnexFlag(), BooleanEnum.YES.getValue())){
            variable.put(concessionReleaseService.getProcessDefinitionKey(), 1);
        }else {
            variable.put(concessionReleaseService.getProcessDefinitionKey(), 0);
        }
        concessionReleaseService.submitTask(id, WebFrameworkUtils.getLoginUserId(),variable);
        return success(true);
    }


}