package com.syj.eplus.module.dms.controller.admin.declaration;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationPageReqVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationRespVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.service.declaration.DeclarationService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 报关单")
@RestController
@RequestMapping("/dms/declaration")
@Validated
public class DeclarationController {

    @Resource
    private DeclarationService declarationService;

    @PostMapping("/create")
    @Operation(summary = "创建报关单")
    @PreAuthorize("@ss.hasPermission('dms:declaration:create')")
    public CommonResult<CreatedResponse> createDeclaration(@Valid @RequestBody DeclarationSaveReqVO createReqVO) {
        return success(declarationService.createDeclaration(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报关单")
    @PreAuthorize("@ss.hasPermission('dms:declaration:update')")
    public CommonResult<Boolean> updateDeclaration(@Valid @RequestBody DeclarationSaveReqVO updateReqVO) {
        declarationService.updateDeclaration(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-group")
    @Operation(summary = "更新报关单聚合版本")
    @PreAuthorize("@ss.hasPermission('dms:declaration:update')")
    public CommonResult<Boolean> updateDeclarationGroupBy(@Valid @RequestBody DeclarationSaveReqVO updateReqVO) {
        declarationService.updateDeclarationGroupBy(updateReqVO);
        return success(true);
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除报关单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:declaration:delete')")
    public CommonResult<Boolean> deleteDeclaration(@RequestParam("id") Long id) {
        declarationService.deleteDeclaration(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得报关单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:declaration:query')")
    public CommonResult<DeclarationRespVO> getDeclaration(@RequestParam("id") Long id) {
            DeclarationRespVO declaration = declarationService.getDeclaration(id);
            return success(declaration);
    }
    @GetMapping("/page")
    @Operation(summary = "获得报关单分页")
    @PreAuthorize("@ss.hasPermission('dms:declaration:query')")
    public CommonResult<PageResult<DeclarationRespVO>> getDeclarationPage(@Valid DeclarationPageReqVO pageReqVO) {
        PageResult<DeclarationRespVO> pageResult = declarationService.getDeclarationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DeclarationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报关单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "exportType", required = false, description = "导出类型", example = "1024"),
    })

    @PreAuthorize("@ss.hasPermission('dms:declaration:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("exportType") String exportType,
            HttpServletResponse response) {
            declarationService.exportExcel(id,exportType, reportCode,response);
    }
}