package com.syj.eplus.module.wms.controller.admin.warehouse;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehousePageReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseRespVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSimpleRespVO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.syj.eplus.module.wms.service.warehouse.WarehouseService;
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

@Tag(name = "仓储管理 - 仓库信息")
@RestController
@RequestMapping("/wms/warehouse")
@Validated
public class WarehouseController {

    @Resource
    private WarehouseService warehouseService;

    @PostMapping("/create")
    @Operation(summary = "创建仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:create')")
    public CommonResult<Long> createWarehouse(@Valid @RequestBody WarehouseSaveReqVO createReqVO) {
        return success(warehouseService.createWarehouse(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:update')")
    public CommonResult<Boolean> updateWarehouse(@Valid @RequestBody WarehouseSaveReqVO updateReqVO) {
        warehouseService.updateWarehouse(updateReqVO);
        return success(true);
    }

    @PutMapping("/enable/{id}")
    @Operation(summary = "启用仓库")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:update')")
    public CommonResult<Boolean> enable(@PathVariable("id") Long id) {
        warehouseService.enable(id);
        return success(true);
    }

    @PutMapping("/disable/{id}")
    @Operation(summary = "停用仓库")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:update')")
    public CommonResult<Boolean> disable(@PathVariable("id") Long id) {
        warehouseService.disable(id);
        return success(true);
    }
    @PutMapping("/set-default/{id}")
    @Operation(summary = "设置默认仓库")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:update')")
    public CommonResult<Boolean> setDefault(@PathVariable("id") Long id) {
        warehouseService.setDefault(id);
        return success(true);
    }
    @DeleteMapping("/delete")
    @Operation(summary = "删除仓库信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:warehouse:delete')")
    public CommonResult<Boolean> deleteWarehouse(@RequestParam("id") Long id) {
        warehouseService.deleteWarehouse(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得仓库信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:query')")
    public CommonResult<WarehouseRespVO> getWarehouse(@RequestParam("id") Long id) {
        WarehouseRespVO warehouse = warehouseService.getWarehouse(id);
        return success(warehouse);
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:query')")
    public CommonResult<PageResult<WarehouseRespVO>> getWarehousePage(@Valid WarehousePageReqVO pageReqVO) {
        return success(warehouseService.getWarehousePage(pageReqVO));
    }

    @GetMapping("/listWarehouse")
    @Operation(summary = "获得仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:query')")
    public CommonResult<List<WarehouseRespVO>> listWarehouse(Integer venderFlag) {
        LambdaQueryWrapper<WarehouseDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseDO::getVenderFlag,venderFlag);
        List<WarehouseDO> list = warehouseService.list(queryWrapper);
        return success(BeanUtils.toBean(list, WarehouseRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得仓库信息精简列表")
    public CommonResult<List<WarehouseSimpleRespVO>> getSimpleList() {
        return success(warehouseService.getSimpleListWarehouse());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓库信息 Excel")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:export')")
    @OperateLog(type = EXPORT)
    public void exportWarehouseExcel(@Valid WarehousePageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<WarehouseRespVO> list = warehouseService.getWarehousePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓库管理.xls", "数据", WarehouseRespVO.class,
                BeanUtils.toBean(list, WarehouseRespVO.class));
    }


}
