package com.syj.eplus.module.mms.controller.admin.manufacturesku;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuRespVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import com.syj.eplus.module.mms.service.manufacturesku.ManufactureSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

@Tag(name = "管理后台 - 加工单产品")
@RestController
@RequestMapping("/mms/manufacture-sku")
@Validated
public class ManufactureSkuController {

    @Resource
    private ManufactureSkuService manufactureSkuService;

    @PostMapping("/create")
    @Operation(summary = "创建加工单产品")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:create')")
    public CommonResult<Long> createManufactureSku(@Valid @RequestBody ManufactureSkuSaveReqVO createReqVO) {
        return success(manufactureSkuService.createManufactureSku(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新加工单产品")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:update')")
    public CommonResult<Boolean> updateManufactureSku(@Valid @RequestBody ManufactureSkuSaveReqVO updateReqVO) {
        manufactureSkuService.updateManufactureSku(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除加工单产品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:delete')")
    public CommonResult<Boolean> deleteManufactureSku(@RequestParam("id") Long id) {
        manufactureSkuService.deleteManufactureSku(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得加工单产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:query')")
    public CommonResult<ManufactureSkuRespVO> getManufactureSku(@RequestParam("id") Long id) {
            ManufactureSkuRespVO manufactureSku = manufactureSkuService.getManufactureSku(id);
            return success(manufactureSku);
    }
    @GetMapping("/page")
    @Operation(summary = "获得加工单产品分页")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:query')")
    public CommonResult<PageResult<ManufactureSkuRespVO>> getManufactureSkuPage(@Valid ManufactureSkuPageReqVO pageReqVO) {
        PageResult<ManufactureSkuDO> pageResult = manufactureSkuService.getManufactureSkuPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ManufactureSkuRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出加工单产品 Excel")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku:export')")
    @OperateLog(type = EXPORT)
    public void exportManufactureSkuExcel(@Valid ManufactureSkuPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ManufactureSkuDO> list = manufactureSkuService.getManufactureSkuPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "加工单产品.xls", "数据", ManufactureSkuRespVO.class,
                        BeanUtils.toBean(list, ManufactureSkuRespVO.class));
    }


}