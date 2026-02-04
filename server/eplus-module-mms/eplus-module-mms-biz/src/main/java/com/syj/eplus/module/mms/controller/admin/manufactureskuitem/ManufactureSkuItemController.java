package com.syj.eplus.module.mms.controller.admin.manufactureskuitem;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemRespVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;
import com.syj.eplus.module.mms.service.manufactureskuitem.ManufactureSkuItemService;
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

@Tag(name = "管理后台 - 加工单子产品")
@RestController
@RequestMapping("/mms/manufacture-sku-item")
@Validated
public class ManufactureSkuItemController {

    @Resource
    private ManufactureSkuItemService manufactureSkuItemService;

    @PostMapping("/create")
    @Operation(summary = "创建加工单子产品")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:create')")
    public CommonResult<Long> createManufactureSkuItem(@Valid @RequestBody ManufactureSkuItemSaveReqVO createReqVO) {
        return success(manufactureSkuItemService.createManufactureSkuItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新加工单子产品")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:update')")
    public CommonResult<Boolean> updateManufactureSkuItem(@Valid @RequestBody ManufactureSkuItemSaveReqVO updateReqVO) {
        manufactureSkuItemService.updateManufactureSkuItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除加工单子产品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:delete')")
    public CommonResult<Boolean> deleteManufactureSkuItem(@RequestParam("id") Long id) {
        manufactureSkuItemService.deleteManufactureSkuItem(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得加工单子产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:query')")
    public CommonResult<ManufactureSkuItemRespVO> getManufactureSkuItem(@RequestParam("id") Long id) {
            ManufactureSkuItemRespVO manufactureSkuItem = manufactureSkuItemService.getManufactureSkuItem(id);
            return success(manufactureSkuItem);
    }
    @GetMapping("/page")
    @Operation(summary = "获得加工单子产品分页")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:query')")
    public CommonResult<PageResult<ManufactureSkuItemRespVO>> getManufactureSkuItemPage(@Valid ManufactureSkuItemPageReqVO pageReqVO) {
        PageResult<ManufactureSkuItemDO> pageResult = manufactureSkuItemService.getManufactureSkuItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ManufactureSkuItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出加工单子产品 Excel")
    @PreAuthorize("@ss.hasPermission('mms:manufacture-sku-item:export')")
    @OperateLog(type = EXPORT)
    public void exportManufactureSkuItemExcel(@Valid ManufactureSkuItemPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ManufactureSkuItemDO> list = manufactureSkuItemService.getManufactureSkuItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "加工单子产品.xls", "数据", ManufactureSkuItemRespVO.class,
                        BeanUtils.toBean(list, ManufactureSkuItemRespVO.class));
    }


}