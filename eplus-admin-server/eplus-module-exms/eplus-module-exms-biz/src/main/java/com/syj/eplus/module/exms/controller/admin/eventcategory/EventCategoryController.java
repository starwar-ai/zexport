package com.syj.eplus.module.exms.controller.admin.eventcategory;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryPageReqVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryRespVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategorySaveReqVO;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;
import com.syj.eplus.module.exms.service.eventcategory.EventCategoryService;
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

@Tag(name = "管理后台 - 展会系列")
@RestController
@RequestMapping("/exms/event-category")
@Validated
public class EventCategoryController {

    @Resource
    private EventCategoryService eventCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建展会系列")
    @PreAuthorize("@ss.hasPermission('exms:event-category:create')")
    public CommonResult<Long> createEventCategory(@Valid @RequestBody EventCategorySaveReqVO createReqVO) {
        return success(eventCategoryService.createEventCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新展会系列")
    @PreAuthorize("@ss.hasPermission('exms:event-category:update')")
    public CommonResult<Boolean> updateEventCategory(@Valid @RequestBody EventCategorySaveReqVO updateReqVO) {
        eventCategoryService.updateEventCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除展会系列")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('exms:event-category:delete')")
    public CommonResult<Boolean> deleteEventCategory(@RequestParam("id") Long id) {
        eventCategoryService.deleteEventCategory(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得展会系列")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('exms:event-category:query')")
    public CommonResult<EventCategoryRespVO> getEventCategory(@RequestParam("id") Long id) {
            EventCategoryRespVO eventCategory = eventCategoryService.getEventCategory(id);
            return success(eventCategory);
    }

    @GetMapping("/page")
    @Operation(summary = "获得展会系列分页")
    @PreAuthorize("@ss.hasPermission('exms:event-category:query')")
    public CommonResult<PageResult<EventCategoryRespVO>> getEventCategoryPage(@Valid EventCategoryPageReqVO pageReqVO) {
        PageResult<EventCategoryDO> pageResult = eventCategoryService.getEventCategoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EventCategoryRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得展会系列列表")
    @DataPermission(enable = false)
    public CommonResult<PageResult<EventCategoryRespVO>> getSimpleCust(EventCategoryPageReqVO pageReqVO) {
        pageReqVO.setPageNo(0).setPageSize(-1);
        PageResult<EventCategoryDO> simpleEventCategory = eventCategoryService.getSimpleList(pageReqVO);
        return success(BeanUtils.toBean(simpleEventCategory, EventCategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出展会系列 Excel")
    @PreAuthorize("@ss.hasPermission('exms:event-category:export')")
    @OperateLog(type = EXPORT)
    public void exportEventCategoryExcel(@Valid EventCategoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EventCategoryDO> list = eventCategoryService.getEventCategoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "展会系列.xls", "数据", EventCategoryRespVO.class,
                        BeanUtils.toBean(list, EventCategoryRespVO.class));
    }


}