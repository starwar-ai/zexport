package com.syj.eplus.module.mms.controller.admin.manufacture;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufacturePageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureRespInfoVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureSaveInfoReqVO;
import com.syj.eplus.module.mms.service.manufacture.ManufactureService;
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

@Tag(name = "管理后台 - 加工单")
@RestController
@RequestMapping("/mms/manufacture")
@Validated
public class ManufactureController {

    @Resource
    private ManufactureService manufactureService;

    @PostMapping("/create")
    @Operation(summary = "创建加工单")
    @PreAuthorize("@ss.hasPermission('mms:manufacture:create')")
    public CommonResult<Long> createManufacture(@Valid @RequestBody ManufactureSaveInfoReqVO createReqVO) throws Exception {
        return success(manufactureService.createManufacture(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新加工单")
    @PreAuthorize("@ss.hasPermission('mms:manufacture:update')")
    public CommonResult<Boolean> updateManufacture(@Valid @RequestBody ManufactureSaveInfoReqVO updateReqVO) {
        manufactureService.updateManufacture(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除加工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture:delete')")
    public CommonResult<Boolean> deleteManufacture(@RequestParam("id") Long id) {
        manufactureService.deleteManufacture(id);
        return success(true);
    }

    @PostMapping("/done")
    @Operation(summary = "完成加工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture:done')")
    public CommonResult<Boolean> doneManufacture(@RequestParam("id") Long id) {
        manufactureService.doneManufacture(id);
        return success(true);
    }
    @PostMapping("/done-batch")
    @Operation(summary = "批量完成加工")
    @Parameter(name = "idList", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture:done')")
    public CommonResult<Boolean> doneBatchManufacture(@RequestParam("idList") List<Long> idList ) {
        manufactureService.doneBatchManufacture(idList);
        return success(true);
    }


    @PostMapping("/finish")
    @Operation(summary = "作废加工单")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "desc", description = "说明", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture:finish')")
    public CommonResult<Boolean> finishManufacture(@RequestParam("id") Long id,@RequestParam("desc")String desc) {
        manufactureService.finishManufacture(id,desc);
        return success(true);
    }

    @PostMapping("/rollback-finish")
    @Operation(summary = "反作废加工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mms:manufacture:rollback-finish')")
    public CommonResult<Boolean> unfinishManufacture(@RequestParam("id") Long id) {
        manufactureService.unfinishManufacture(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得加工单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mms:manufacture:query')")
    public CommonResult<ManufactureRespInfoVO> getManufacture(@RequestParam("id") Long id) {
        ManufactureRespInfoVO manufacture = manufactureService.getManufacture(id);
        return success(manufacture);
    }
    @GetMapping("/page")
    @Operation(summary = "获得加工单分页")
    @PreAuthorize("@ss.hasPermission('mms:manufacture:query')")
    public CommonResult<PageResult<ManufactureRespInfoVO>> getManufacturePage(@Valid ManufacturePageReqVO pageReqVO) {
        PageResult<ManufactureRespInfoVO> pageResult = manufactureService.getManufacturePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ManufactureRespInfoVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出加工单 Excel")
    @PreAuthorize("@ss.hasPermission('mms:manufacture:export')")
    @OperateLog(type = EXPORT)
    public void exportManufactureExcel(@Valid ManufacturePageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ManufactureRespInfoVO> list = manufactureService.getManufacturePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "加工单.xls", "数据", ManufactureRespInfoVO.class,
                BeanUtils.toBean(list, ManufactureRespInfoVO.class));
    }


}