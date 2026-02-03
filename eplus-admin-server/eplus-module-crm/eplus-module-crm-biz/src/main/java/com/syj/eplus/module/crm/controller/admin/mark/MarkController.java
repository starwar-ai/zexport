package com.syj.eplus.module.crm.controller.admin.mark;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkPageReqVO;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkRespVO;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import com.syj.eplus.module.crm.service.mark.MarkService;
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

@Tag(name = "管理后台 - 唛头")
@RestController
@RequestMapping("/crm/mark")
@Validated
public class MarkController {

    @Resource
    private MarkService markService;

    @PostMapping("/create")
    @Operation(summary = "创建唛头")
    @PreAuthorize("@ss.hasPermission('crm:mark:create')")
    public CommonResult<Long> createMark(@Valid @RequestBody MarkSaveReqVO createReqVO) {
        return success(markService.createMark(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新唛头")
    @PreAuthorize("@ss.hasPermission('crm:mark:update')")
    public CommonResult<Boolean> updateMark(@Valid @RequestBody MarkSaveReqVO updateReqVO) {
        markService.updateMark(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除唛头")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:mark:delete')")
    public CommonResult<Boolean> deleteMark(@RequestParam("id") Long id) {
        markService.deleteMark(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得唛头")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:mark:query')")
    public CommonResult<MarkRespVO> getMark(@RequestParam("id") Long id) {
        MarkDO mark = markService.getMark(id);
        return success(BeanUtils.toBean(mark, MarkRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得唛头分页")
    @PreAuthorize("@ss.hasPermission('crm:mark:query')")
    public CommonResult<PageResult<MarkRespVO>> getMarkPage(@Valid MarkPageReqVO pageReqVO) {
        PageResult<MarkDO> pageResult = markService.getMarkPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarkRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出唛头 Excel")
    @PreAuthorize("@ss.hasPermission('crm:mark:export')")
    @OperateLog(type = EXPORT)
    public void exportMarkExcel(@Valid MarkPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MarkDO> list = markService.getMarkPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "唛头.xls", "数据", MarkRespVO.class,
                        BeanUtils.toBean(list, MarkRespVO.class));
    }

}