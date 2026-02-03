package com.syj.eplus.module.dms.controller.admin.commodityinspection;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionPageReqVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionRespVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;
import com.syj.eplus.module.dms.service.commodityinspection.CommodityInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 商检单")
@RestController
@RequestMapping("/dms/commodity-inspection")
@Validated
public class CommodityInspectionController {

    @Resource
    private CommodityInspectionService commodityInspectionService;

    @PostMapping("/create")
    @Operation(summary = "创建商检单")
    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:create')")
    public CommonResult<CreatedResponse> createCommodityInspection(@Valid @RequestBody CommodityInspectionSaveReqVO createReqVO) {
        return success(commodityInspectionService.createCommodityInspection(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商检单")
    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:update')")
    public CommonResult<Boolean> updateCommodityInspection(@Valid @RequestBody CommodityInspectionSaveReqVO updateReqVO) {
        commodityInspectionService.updateCommodityInspection(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商检单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:delete')")
    public CommonResult<Boolean> deleteCommodityInspection(@RequestParam("id") Long id) {
        commodityInspectionService.deleteCommodityInspection(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得商检单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:query')")
    public CommonResult<CommodityInspectionRespVO> getCommodityInspection(@RequestParam("id") Long id) {
            CommodityInspectionRespVO commodityInspection = commodityInspectionService.getCommodityInspection(id);
            return success(commodityInspection);
    }
    @GetMapping("/page")
    @Operation(summary = "获得商检单分页")
    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:query')")
    public CommonResult<PageResult<CommodityInspectionRespVO>> getCommodityInspectionPage(@Valid CommodityInspectionPageReqVO pageReqVO) {
        PageResult<CommodityInspectionRespVO> pageResult = commodityInspectionService.getCommodityInspectionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CommodityInspectionRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出商检单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('dms:commodity-inspection:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        commodityInspectionService.exportExcel(id, reportCode,response);
    }

}