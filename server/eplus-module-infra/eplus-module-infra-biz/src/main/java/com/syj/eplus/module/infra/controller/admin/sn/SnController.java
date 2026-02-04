package com.syj.eplus.module.infra.controller.admin.sn;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnPageReqVO;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnRespVO;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import com.syj.eplus.module.infra.service.sn.SnService;
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


@Tag(name = "管理后台 - 序列号记录")
@RestController
@RequestMapping("/system/sn")
@Validated
public class SnController {

    @Resource
    private SnService snService;

    @PostMapping("/create")
    @Operation(summary = "创建序列号记录")
    @PreAuthorize("@ss.hasPermission('system:sn:create')")
    public CommonResult<Long> createSn(@Valid @RequestBody SnSaveReqVO createReqVO) {
        return success(snService.createSn(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新序列号记录")
    @PreAuthorize("@ss.hasPermission('system:sn:update')")
    public CommonResult<Boolean> updateSn(@Valid @RequestBody SnSaveReqVO updateReqVO) {
        snService.updateSn(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除序列号记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sn:delete')")
    public CommonResult<Boolean> deleteSn(@RequestParam("id") Long id) {
        snService.deleteSn(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得序列号记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sn:query')")
    public CommonResult<SnRespVO> getSn(@RequestParam("id") Long id) {
        SnDO sn = snService.getSn(id);
        return success(BeanUtils.toBean(sn, SnRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得序列号记录分页")
    @PreAuthorize("@ss.hasPermission('system:sn:query')")
    public CommonResult<PageResult<SnRespVO>> getSnPage(@Valid SnPageReqVO pageReqVO) {
        PageResult<SnDO> pageResult = snService.getSnPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SnRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出序列号记录 Excel")
    @PreAuthorize("@ss.hasPermission('system:sn:export')")
    @OperateLog(type = EXPORT)
    public void exportSnExcel(@Valid SnPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SnDO> list = snService.getSnPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "序列号记录.xls", "数据", SnRespVO.class,
                        BeanUtils.toBean(list, SnRespVO.class));
    }

}