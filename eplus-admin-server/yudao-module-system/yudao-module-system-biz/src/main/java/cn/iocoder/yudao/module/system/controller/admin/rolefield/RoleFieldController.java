package cn.iocoder.yudao.module.system.controller.admin.rolefield;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldRespVO;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.rolefield.RoleFieldDO;
import cn.iocoder.yudao.module.system.service.rolefield.RoleFieldService;
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

@Tag(name = "管理后台 - 字段角色关联")
@RestController
@RequestMapping("/system/role-field")
@Validated
public class RoleFieldController {

    @Resource
    private RoleFieldService roleFieldService;

    @PostMapping("/create")
    @Operation(summary = "创建字段角色关联")
    @PreAuthorize("@ss.hasPermission('system:role-field:create')")
    public CommonResult<Long> createRoleField(@Valid @RequestBody RoleFieldSaveReqVO createReqVO) {
        return success(roleFieldService.createRoleField(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新字段角色关联")
    @PreAuthorize("@ss.hasPermission('system:role-field:update')")
    public CommonResult<Boolean> updateRoleField(@Valid @RequestBody RoleFieldSaveReqVO updateReqVO) {
        roleFieldService.updateRoleField(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字段角色关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:role-field:delete')")
    public CommonResult<Boolean> deleteRoleField(@RequestParam("id") Long id) {
        roleFieldService.deleteRoleField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得字段角色关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:role-field:query')")
    public CommonResult<RoleFieldRespVO> getRoleField(@RequestParam("id") Long id) {
        RoleFieldDO roleField = roleFieldService.getRoleField(id);
        return success(BeanUtils.toBean(roleField, RoleFieldRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得字段角色关联分页")
    @PreAuthorize("@ss.hasPermission('system:role-field:query')")
    public CommonResult<PageResult<RoleFieldRespVO>> getRoleFieldPage(@Valid RoleFieldPageReqVO pageReqVO) {
        PageResult<RoleFieldDO> pageResult = roleFieldService.getRoleFieldPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RoleFieldRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出字段角色关联 Excel")
    @PreAuthorize("@ss.hasPermission('system:role-field:export')")
    @OperateLog(type = EXPORT)
    public void exportRoleFieldExcel(@Valid RoleFieldPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RoleFieldDO> list = roleFieldService.getRoleFieldPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "字段角色关联.xls", "数据", RoleFieldRespVO.class,
                BeanUtils.toBean(list, RoleFieldRespVO.class));
    }

}