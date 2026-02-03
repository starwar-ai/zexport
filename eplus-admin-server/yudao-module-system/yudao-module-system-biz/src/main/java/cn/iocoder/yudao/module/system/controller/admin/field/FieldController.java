package cn.iocoder.yudao.module.system.controller.admin.field;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import cn.iocoder.yudao.module.system.service.field.FieldService;
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

@Tag(name = "管理后台 - 系统字段")
@RestController
@RequestMapping("/system/field")
@Validated
public class FieldController {

    @Resource
    private FieldService fieldService;

    @PostMapping("/create")
    @Operation(summary = "创建系统字段")
    @PreAuthorize("@ss.hasPermission('system:field:create')")
    public CommonResult<Long> createField(@Valid @RequestBody FieldSaveReqVO createReqVO) {
        return success(fieldService.createField(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统字段")
    @PreAuthorize("@ss.hasPermission('system:field:update')")
    public CommonResult<Boolean> updateField(@Valid @RequestBody FieldSaveReqVO updateReqVO) {
        fieldService.updateField(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统字段")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:field:delete')")
    public CommonResult<Boolean> deleteField(@RequestParam("id") Long id) {
        fieldService.deleteField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统字段")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<List<FieldRespVO>> getField(FieldReqVO fieldReqVO) {
        List<FieldRespVO> fieldList = fieldService.getFieldList(fieldReqVO);
        return success(fieldList);
    }

    @GetMapping("/sync")
    @Operation(summary = "同步系统字段")
//    @PreAuthorize("@ss.hasPermission('system:field:query')")  暂未定义权限
    public CommonResult<Boolean> getField() {
        fieldService.syncFieldData();
        return success(true);
    }


    @GetMapping("/page")
    @Operation(summary = "获得系统字段分页")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<PageResult<FieldRespVO>> getFieldPage(@Valid FieldPageReqVO pageReqVO) {
        PageResult<FieldDO> pageResult = fieldService.getFieldPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FieldRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统字段 Excel")
    @PreAuthorize("@ss.hasPermission('system:field:export')")
    @OperateLog(type = EXPORT)
    public void exportFieldExcel(@Valid FieldPageReqVO pageReqVO,
                                 HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<FieldDO> list = fieldService.getFieldPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统字段.xls", "数据", FieldRespVO.class,
                BeanUtils.toBean(list, FieldRespVO.class));
    }

    @GetMapping("/table")
    @Operation(summary = "获得系统表")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<List<TableInfoRespVO>> getTableInfo() {
        List<TableInfoRespVO> tableInfo = fieldService.getTableInfo();
        return success(tableInfo);
    }

}