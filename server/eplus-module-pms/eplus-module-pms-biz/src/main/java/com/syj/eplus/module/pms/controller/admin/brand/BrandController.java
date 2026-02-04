package com.syj.eplus.module.pms.controller.admin.brand;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandPageReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandRespVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;
import com.syj.eplus.module.pms.service.brand.BrandService;
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


@Tag(name = "管理后台 - 品牌")
@RestController
@RequestMapping("/pms/brand")
@Validated
public class BrandController {

    @Resource
    private BrandService brandService;

    @PostMapping("/create")
    @Operation(summary = "创建品牌")
    @PreAuthorize("@ss.hasPermission('pms:brand:create')")
    public CommonResult<Long> createBrand(@Valid @RequestBody BrandSaveReqVO createReqVO) {
        return success(brandService.createBrand(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新品牌")
    @PreAuthorize("@ss.hasPermission('pms:brand:update')")
    public CommonResult<Boolean> updateBrand(@Valid @RequestBody BrandSaveReqVO updateReqVO) {
        brandService.updateBrand(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除品牌")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:brand:delete')")
    public CommonResult<Boolean> deleteBrand(@RequestParam("id") Long id) {
        brandService.deleteBrand(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得品牌")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:brand:query')")
    public CommonResult<BrandRespVO> getBrand(@RequestParam("id") Long id) {
        BrandDO brand = brandService.getBrand(id);
        return success(BeanUtils.toBean(brand, BrandRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得品牌精简列表")
    public CommonResult<List<BrandSimpleRespVO>> getBrand() {
        List<BrandSimpleRespVO> brandSimpleList = brandService.getBrandSimpleList();
        return success(brandSimpleList);
    }

    @GetMapping("/page")
    @Operation(summary = "获得品牌分页")
    @PreAuthorize("@ss.hasPermission('pms:brand:query')")
    public CommonResult<PageResult<BrandRespVO>> getBrandPage(@Valid BrandPageReqVO pageReqVO) {
        PageResult<BrandDO> pageResult = brandService.getBrandPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BrandRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出品牌 Excel")
    @PreAuthorize("@ss.hasPermission('pms:brand:export')")
    @OperateLog(type = EXPORT)
    public void exportBrandExcel(@Valid BrandPageReqVO pageReqVO,
                                 HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BrandDO> list = brandService.getBrandPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "品牌.xls", "数据", BrandRespVO.class,
                BeanUtils.toBean(list, BrandRespVO.class));
    }

}