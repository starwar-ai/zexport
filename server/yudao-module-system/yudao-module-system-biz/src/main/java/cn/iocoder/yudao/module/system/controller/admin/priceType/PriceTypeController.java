package cn.iocoder.yudao.module.system.controller.admin.priceType;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.priceType.PriceTypeDO;
import cn.iocoder.yudao.module.system.service.priceType.PriceTypeService;
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

@Tag(name = "管理后台 - 价格条款")
@RestController
@RequestMapping("/system/price-type")
@Validated
public class PriceTypeController {

    @Resource
    private PriceTypeService priceTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建价格条款")
    @PreAuthorize("@ss.hasPermission('system:price-type:create')")
    public CommonResult<Long> createPriceType(@Valid @RequestBody PriceTypeSaveReqVO createReqVO) {
        return success(priceTypeService.createPriceType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新价格条款")
    @PreAuthorize("@ss.hasPermission('system:price-type:update')")
    public CommonResult<Boolean> updatePriceType(@Valid @RequestBody PriceTypeSaveReqVO updateReqVO) {
        priceTypeService.updatePriceType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除价格条款")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:price-type:delete')")
    public CommonResult<Boolean> deletePriceType(@RequestParam("id") Long id) {
        priceTypeService.deletePriceType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得价格条款")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:price-type:query')")
    public CommonResult<PriceTypeRespVO> getPriceType(@RequestParam("id") Long id) {
        PriceTypeDO priceType = priceTypeService.getPriceType(id);
        return success(BeanUtils.toBean(priceType, PriceTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得价格条款分页")
    @PreAuthorize("@ss.hasPermission('system:price-type:query')")
    public CommonResult<PageResult<PriceTypeRespVO>> getPriceTypePage(@Valid PriceTypePageReqVO pageReqVO) {
        PageResult<PriceTypeDO> pageResult = priceTypeService.getPriceTypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PriceTypeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出价格条款 Excel")
    @PreAuthorize("@ss.hasPermission('system:price-type:export')")
    @OperateLog(type = EXPORT)
    public void exportPriceTypeExcel(@Valid PriceTypePageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PriceTypeDO> list = priceTypeService.getPriceTypePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "价格条款.xls", "数据", PriceTypeRespVO.class,
                BeanUtils.toBean(list, PriceTypeRespVO.class));
    }

}