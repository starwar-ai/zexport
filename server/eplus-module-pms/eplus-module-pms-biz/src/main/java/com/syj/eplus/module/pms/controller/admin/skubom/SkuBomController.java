package com.syj.eplus.module.pms.controller.admin.skubom;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomRespVO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomSaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.pms.service.skubom.SkuBomService;
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


@Tag(name = "管理后台 - 产品SKU BOM")
@RestController
@RequestMapping("/pms/sku-bom")
@Validated
public class SkuBomController {

    @Resource
    private SkuBomService skuBomService;

    @PostMapping("/create")
    @Operation(summary = "创建产品SKU BOM")
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:create')")
    public CommonResult<Long> createSkuBom(@Valid @RequestBody SkuBomSaveReqVO createReqVO) {
        return success(skuBomService.createSkuBom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新产品SKU BOM")
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:update')")
    public CommonResult<Boolean> updateSkuBom(@Valid @RequestBody SkuBomSaveReqVO updateReqVO) {
        skuBomService.updateSkuBom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品SKU BOM")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:delete')")
    public CommonResult<Boolean> deleteSkuBom(@RequestParam("id") Long id) {
        skuBomService.deleteSkuBom(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品SKU BOM")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:query')")
    public CommonResult<SkuBomRespVO> getSkuBom(@RequestParam("id") Long id) {
        SkuBomDO skuBom = skuBomService.getSkuBom(id);
        return success(BeanUtils.toBean(skuBom, SkuBomRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品SKU BOM分页")
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:query')")
    public CommonResult<PageResult<SkuBomRespVO>> getSkuBomPage(@Valid SkuBomPageReqVO pageReqVO) {
        PageResult<SkuBomDO> pageResult = skuBomService.getSkuBomPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SkuBomRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品SKU BOM Excel")
    @PreAuthorize("@ss.hasPermission('pms:sku-bom:export')")
    @OperateLog(type = EXPORT)
    public void exportSkuBomExcel(@Valid SkuBomPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SkuBomDO> list = skuBomService.getSkuBomPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品SKU BOM.xls", "数据", SkuBomRespVO.class,
                BeanUtils.toBean(list, SkuBomRespVO.class));
    }

}