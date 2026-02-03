package com.syj.eplus.module.dtms.controller.admin.designsummary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryRespVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummarySaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;
import com.syj.eplus.module.dtms.service.designsummary.DesignSummaryService;
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
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 设计-工作总结")
@RestController
@RequestMapping("/dtms/design-summary")
@Validated
public class DesignSummaryController {

    @Resource
    private DesignSummaryService designSummaryService;

    @PostMapping("/create")
    @Operation(summary = "创建设计-工作总结")
    @PreAuthorize("@ss.hasPermission('dtms:design:speed')")
    public CommonResult<Long> createDesignSummary(@RequestBody DesignSummarySaveReqVO createReqVO) {
        return success(designSummaryService.createDesignSummary(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设计-工作总结")
    public CommonResult<Boolean> updateDesignSummary(@Valid @RequestBody DesignSummarySaveReqVO updateReqVO) {
        designSummaryService.updateDesignSummary(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设计-工作总结")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dtms:design-summary:delete')")
    public CommonResult<Boolean> deleteDesignSummary(@RequestParam("id") Long id) {
        designSummaryService.deleteDesignSummary(id);
        return success(true);
    }

    @PostMapping("/detail")
    @Operation(summary = "获得设计-工作总结")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dtms:design-summary:query')")
    public CommonResult<DesignSummaryRespVO> getDesignSummary(@RequestBody DesignSummarySaveReqVO createReqVO) {
        DesignSummaryRespVO designSummary = designSummaryService.getDesignSummary(createReqVO.getDesignId(),createReqVO.getDesignerId());
        return success(designSummary);
    }

    @GetMapping("/page")
    @Operation(summary = "获得设计-工作总结")
    public CommonResult<List<DesignSummaryRespVO>> getDesignSummaryPage(@Valid DesignSummaryPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DesignSummaryDO> designSummaryDOList = designSummaryService.getDesignSummaryPage(pageReqVO).getList();
        designSummaryDOList.sort(Comparator.comparing(DesignSummaryDO::getCreateTime).reversed().thenComparing(DesignSummaryDO::getDesignerId));
        return success(BeanUtils.toBean(designSummaryDOList, DesignSummaryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设计-工作总结 Excel")
    @PreAuthorize("@ss.hasPermission('dtms:design-summary:export')")
    @OperateLog(type = EXPORT)
    public void exportDesignSummaryExcel(@Valid DesignSummaryPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DesignSummaryDO> list = designSummaryService.getDesignSummaryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设计-工作总结.xls", "数据", DesignSummaryRespVO.class,
                BeanUtils.toBean(list, DesignSummaryRespVO.class));
    }


}
