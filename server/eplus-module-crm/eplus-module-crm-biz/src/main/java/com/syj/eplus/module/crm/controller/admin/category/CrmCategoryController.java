package com.syj.eplus.module.crm.controller.admin.category;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryPageReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryRespVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySaveReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySimpleRespVO;
import com.syj.eplus.module.crm.dal.dataobject.category.CrmCategoryDO;
import com.syj.eplus.module.crm.service.category.CrmCategoryService;
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


@Tag(name = "管理后台 - 客户分类")
@RestController
@RequestMapping("/crm/category")
@Validated
public class CrmCategoryController {

    @Resource
    private CrmCategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "创建客户分类")
    @PreAuthorize("@ss.hasPermission('crm:category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody CrmCategorySaveReqVO createReqVO) {
        return success(categoryService.createCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户分类")
    @PreAuthorize("@ss.hasPermission('crm:category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody CrmCategorySaveReqVO updateReqVO) {
        categoryService.updateCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:category:delete')")
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:category:query')")
    public CommonResult<CrmCategoryRespVO> getCategory(@RequestParam("id") Long id) {
        CrmCategoryDO category = categoryService.getCategory(id);
        return success(BeanUtils.toBean(category, CrmCategoryRespVO.class));
    }


    @GetMapping("/get-simple-list")
    @Operation(summary = "获得客户分类树")
    public CommonResult<List<CrmCategorySimpleRespVO>> getSimpleList() {
        List<CrmCategorySimpleRespVO> simpleList = categoryService.getSimpleList();
        return success(simpleList);
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户分类分页")
    @PreAuthorize("@ss.hasPermission('crm:category:query')")
    public CommonResult<PageResult<CrmCategoryRespVO>> getCategoryPage(@Valid CrmCategoryPageReqVO pageReqVO) {
        PageResult<CrmCategoryRespVO> pageResult = categoryService.getCategoryPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户分类 Excel")
    @PreAuthorize("@ss.hasPermission('crm:category:export')")
    @OperateLog(type = EXPORT)
    public void exportCategoryExcel(@Valid CrmCategoryPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CrmCategoryRespVO> list = categoryService.getCategoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户分类.xls", "数据", CrmCategoryRespVO.class, list);
    }

}