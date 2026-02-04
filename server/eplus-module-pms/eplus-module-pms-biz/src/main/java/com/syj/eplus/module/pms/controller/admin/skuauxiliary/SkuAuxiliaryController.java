package com.syj.eplus.module.pms.controller.admin.skuauxiliary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryRespVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliarySaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.pms.service.skuauxiliary.SkuAuxiliaryService;
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

@Tag(name = "管理后台 - 产品辅料配比")
@RestController
@RequestMapping("/pms/sku-auxiliary")
@Validated
public class SkuAuxiliaryController {

    @Resource
    private SkuAuxiliaryService skuAuxiliaryService;

    @PostMapping("/create")
    @Operation(summary = "创建产品辅料配比")
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:create')")
    public CommonResult<Long> createSkuAuxiliary(@Valid @RequestBody SkuAuxiliarySaveReqVO createReqVO) {
        return success(skuAuxiliaryService.createSkuAuxiliary(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新产品辅料配比")
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:update')")
    public CommonResult<Boolean> updateSkuAuxiliary(@Valid @RequestBody SkuAuxiliarySaveReqVO updateReqVO) {
        skuAuxiliaryService.updateSkuAuxiliary(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品辅料配比")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:delete')")
    public CommonResult<Boolean> deleteSkuAuxiliary(@RequestParam("id") Long id) {
        skuAuxiliaryService.deleteSkuAuxiliary(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得产品辅料配比")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:query')")
    public CommonResult<SkuAuxiliaryRespVO> getSkuAuxiliary(@RequestParam("id") Long id) {
        SkuAuxiliaryRespVO skuAuxiliary = skuAuxiliaryService.getSkuAuxiliary( id);
        return success(skuAuxiliary);
    }
    @GetMapping("/page")
    @Operation(summary = "获得产品辅料配比分页")
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:query')")
    public CommonResult<PageResult<SkuAuxiliaryRespVO>> getSkuAuxiliaryPage(@Valid SkuAuxiliaryPageReqVO pageReqVO) {
        PageResult<SkuAuxiliaryDO> pageResult = skuAuxiliaryService.getSkuAuxiliaryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SkuAuxiliaryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品辅料配比 Excel")
    @PreAuthorize("@ss.hasPermission('pms:sku-auxiliary:export')")
    @OperateLog(type = EXPORT)
    public void exportSkuAuxiliaryExcel(@Valid SkuAuxiliaryPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SkuAuxiliaryDO> list = skuAuxiliaryService.getSkuAuxiliaryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品辅料配比.xls", "数据", SkuAuxiliaryRespVO.class,
                BeanUtils.toBean(list, SkuAuxiliaryRespVO.class));
    }


}