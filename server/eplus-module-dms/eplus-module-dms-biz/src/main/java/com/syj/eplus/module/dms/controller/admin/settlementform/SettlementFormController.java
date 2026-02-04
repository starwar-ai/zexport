package com.syj.eplus.module.dms.controller.admin.settlementform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormPageReqVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormRespVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormSaveReqVO;
import com.syj.eplus.module.dms.service.settlementform.SettlementFormService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 结汇单")
@RestController
@RequestMapping("/dms/settlement-form")
@Validated
public class SettlementFormController {

    @Resource
    private SettlementFormService settlementFormService;

    @PostMapping("/create")
    @Operation(summary = "创建结汇单")
    @PreAuthorize("@ss.hasPermission('dms:settlement-form:create')")
    public CommonResult<CreatedResponse> createSettlementForm(@Valid @RequestBody SettlementFormSaveReqVO createReqVO) {
        return success(settlementFormService.createSettlementForm(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新结汇单")
    @PreAuthorize("@ss.hasPermission('dms:settlement-form:update')")
    public CommonResult<Boolean> updateSettlementForm(@Valid @RequestBody SettlementFormSaveReqVO updateReqVO) {
        settlementFormService.updateSettlementForm(updateReqVO);
        return success(true);
    }

//    @DeleteMapping("/delete")
//    @Operation(summary = "删除结汇单")
//    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('dms:settlement-form:delete')")
//    public CommonResult<Boolean> deleteSettlementForm(@RequestParam("id") Long id) {
//        settlementFormService.deleteSettlementForm(id);
//        return success(true);
//    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除结汇单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:settlement-form:batch-delete')")
    public CommonResult<Boolean> deleteSettlementForm(@RequestParam("idList") List<Long> idList) {
        settlementFormService.batchDeleteSettlementForm(idList);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得结汇单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:settlement-form:query')")
    public CommonResult<SettlementFormRespVO> getSettlementForm(@RequestParam("id") Long id) {
            SettlementFormRespVO settlementForm = settlementFormService.getSettlementForm(id);
            return success(settlementForm);
    }
    @GetMapping("/page")
    @Operation(summary = "获得结汇单分页")
    @PreAuthorize("@ss.hasPermission('dms:settlement-form:query')")
    public CommonResult<PageResult<SettlementFormRespVO>> getSettlementFormPage(@Valid SettlementFormPageReqVO pageReqVO) {
        PageResult<SettlementFormRespVO> pageResult = settlementFormService.getSettlementFormPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SettlementFormRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出结汇单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "sourceCode", required = false, description = "来源编码"),
            @Parameter(name = "sourceType", required = false, description = "来源类型"),
    })

    @PreAuthorize("@ss.hasPermission('dms:settlement-form:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("exportType") String exportType,
            @RequestParam(value = "sourceCode", required = false) String sourceCode,
            @RequestParam(value = "sourceType", required = false) Integer sourceType,
            HttpServletResponse response) {
        settlementFormService.exportExcel(id, reportCode, exportType, sourceCode, sourceType, response);
    }
}