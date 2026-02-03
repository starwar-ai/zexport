package com.syj.eplus.module.dtms.controller.admin.designitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemRespVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemSaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import com.syj.eplus.module.dtms.service.designitem.DesignItemService;
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

@Tag(name = "管理后台 - 设计-认领明细")
@RestController
@RequestMapping("/dtms/design-item")
@Validated
public class DesignItemController {

    @Resource
    private DesignItemService designItemService;

    @PostMapping("/create")
    @Operation(summary = "创建设计-认领明细")
    public CommonResult<Boolean> createDesignItem(@Valid @RequestBody List<DesignItemSaveReqVO> createReqVOList) {
        return success(designItemService.createDesignItem(createReqVOList));
    }

    @PutMapping("/evaluate")
    @Operation(summary = "评价-认领明细")
    @PreAuthorize("@ss.hasPermission('dtms:design:evaluate')")
    public CommonResult<Boolean> evaluate(@Valid @RequestBody List<DesignItemSaveReqVO> updateReqVOList) {
        return success(designItemService.evaluate(updateReqVOList));
    }

    @PutMapping("/changeDesigner")
    @Operation(summary = "变更认领人")
    @PreAuthorize("@ss.hasPermission('dtms:design:changeDesigner')")
    public CommonResult<Boolean> changeDesigner(@Valid @RequestBody List<DesignItemSaveReqVO> updateReqVOList) {
        return success(designItemService.evaluate(updateReqVOList));
    }

    @PutMapping("/updateDesigner")
    @Operation(summary = "更新设计-认领明细")
    public CommonResult<Boolean> updateDesigner(@Valid @RequestBody DesignItemSaveReqVO updateReqVO) {
        designItemService.updateDesigner(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设计-认领明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dtms:design-item:delete')")
    public CommonResult<Boolean> deleteDesignItem(@RequestParam("id") Long id) {
        designItemService.deleteDesignItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得设计-认领明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dtms:design-item:query')")
    public CommonResult<DesignItemRespVO> getDesignItem(@RequestParam("id") Long id) {
        DesignItemRespVO designItem = designItemService.getDesignItem(id);
        return success(designItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得设计-认领明细分页")
    public CommonResult<List<DesignItemRespVO>> getDesignItemPage(@Valid DesignItemPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DesignItemDO> designItemDOList = designItemService.getDesignItemPage(pageReqVO).getList();
        return success(BeanUtils.toBean(designItemDOList, DesignItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设计-认领明细 Excel")
    @PreAuthorize("@ss.hasPermission('dtms:design-item:export')")
    @OperateLog(type = EXPORT)
    public void exportDesignItemExcel(@Valid DesignItemPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DesignItemDO> list = designItemService.getDesignItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设计-认领明细.xls", "数据", DesignItemRespVO.class,
                BeanUtils.toBean(list, DesignItemRespVO.class));
    }

    @PutMapping("/complate")
    @Operation(summary = "完成设计")
    @PreAuthorize("@ss.hasPermission('dtms:design:complete')")
    public CommonResult<Boolean> complate(@Valid @RequestBody DesignItemSaveReqVO updateReqVO) {
        return success(designItemService.complate(updateReqVO));
    }

}
