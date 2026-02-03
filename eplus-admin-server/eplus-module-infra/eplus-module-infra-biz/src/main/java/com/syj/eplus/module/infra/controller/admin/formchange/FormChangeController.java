package com.syj.eplus.module.infra.controller.admin.formchange;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangePageReqVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeRespVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.formchange.FormChangeDO;
import com.syj.eplus.module.infra.service.formchange.FormChangeService;
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

@Tag(name = "管理后台 - 表单字段变更管理")
@RestController
@RequestMapping("/system/form-change")
@Validated
public class FormChangeController {

    @Resource
    private FormChangeService formChangeService;

    @PostMapping("/create")
    @Operation(summary = "创建表单字段变更管理")
    @PreAuthorize("@ss.hasPermission('system:form-change:create')")
    public CommonResult<Long> createFormChange(@Valid @RequestBody FormChangeSaveReqVO createReqVO) {
        return success(formChangeService.createFormChange(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新表单字段变更管理")
    @PreAuthorize("@ss.hasPermission('system:form-change:update')")
    public CommonResult<Boolean> updateFormChange(@Valid @RequestBody FormChangeSaveReqVO updateReqVO) {
        formChangeService.updateFormChange(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除表单字段变更管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:form-change:delete')")
    public CommonResult<Boolean> deleteFormChange(@RequestParam("id") Long id) {
        formChangeService.deleteFormChange(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得表单字段变更管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:form-change:query')")
    public CommonResult<FormChangeRespVO> getFormChange(@RequestParam("id") Long id) {
        FormChangeRespVO formChange = formChangeService.getFormChange(id);
        return success(formChange);
    }


    @GetMapping("/getFormChangeByName")
    @Operation(summary = "根据表名获取变更配置")
    @Parameter(name = "name", description = "名称", required = true, example = "1024")
    public CommonResult<FormChangeRespVO> getFormChangeByName(@RequestParam("name") String name) {
        FormChangeRespVO formChangeRespVO = formChangeService.getFormChangeByName(name);
        return success(formChangeRespVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得表单字段变更管理分页")
    @PreAuthorize("@ss.hasPermission('system:form-change:query')")
    public CommonResult<PageResult<FormChangeRespVO>> getFormChangePage(@Valid FormChangePageReqVO pageReqVO) {
        PageResult<FormChangeDO> pageResult = formChangeService.getFormChangePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FormChangeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出表单字段变更管理 Excel")
    @PreAuthorize("@ss.hasPermission('system:form-change:export')")
    @OperateLog(type = EXPORT)
    public void exportFormChangeExcel(@Valid FormChangePageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<FormChangeDO> list = formChangeService.getFormChangePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "表单字段变更管理.xls", "数据", FormChangeRespVO.class,
                BeanUtils.toBean(list, FormChangeRespVO.class));
    }

    @GetMapping("/sync")
    @Operation(summary = "同步系统字段")
//    @PreAuthorize("@ss.hasPermission('system:field:query')")  暂未定义权限
    public CommonResult<Boolean> getField() {
        formChangeService.syncFieldData();
        return success(true);
    }
}