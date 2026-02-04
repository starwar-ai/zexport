package com.syj.eplus.module.infra.controller.admin.companypath;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathPageReqVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathRespVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.companypath.CompanyPathDO;
import com.syj.eplus.module.infra.service.companypath.CompanyPathService;
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


@Tag(name = "管理后台 - 抛砖路径")
@RestController
@RequestMapping("/company/path")
@Validated
public class CompanyPathController {

    @Resource
    private CompanyPathService pathService;

    @PostMapping("/create")
    @Operation(summary = "创建抛砖路径")
    @PreAuthorize("@ss.hasPermission('company:path:create')")
    public CommonResult<Long> createPath(@Valid @RequestBody CompanyPathSaveReqVO createReqVO) {
        return success(pathService.createPath(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新抛砖路径")
    @PreAuthorize("@ss.hasPermission('company:path:update')")
    public CommonResult<Boolean> updatePath(@Valid @RequestBody CompanyPathSaveReqVO updateReqVO) {
        pathService.updatePath(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除抛砖路径")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('company:path:delete')")
    public CommonResult<Boolean> deletePath(@RequestParam("id") Long id) {
        pathService.deletePath(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得抛砖路径")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('company:path:query')")
    public CommonResult<CompanyPathRespVO> getPath(@RequestParam("id") Long id) {
        CompanyPathRespVO path = pathService.getPath
                (id);
        return success(path);
    }

    @GetMapping("/page")
    @Operation(summary = "获得抛砖路径分页")
    @PreAuthorize("@ss.hasPermission('company:path:query')")
    public CommonResult<PageResult<CompanyPathRespVO>> getPathPage(@Valid CompanyPathPageReqVO pageReqVO) {
        PageResult<CompanyPathDO> pageResult = pathService.getPathPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CompanyPathRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得抛砖路径")
    public CommonResult<PageResult<CompanyPathRespVO>> getSimplePath(@Valid CompanyPathPageReqVO pageReqVO) {
        pageReqVO.setPageNo(0).setPageSize(-1);
        PageResult<CompanyPathDO> pageResult = pathService.getPathPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CompanyPathRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出抛砖路径 Excel")
    @PreAuthorize("@ss.hasPermission('company:path:export')")
    @OperateLog(type = EXPORT)
    public void exportPathExcel(@Valid CompanyPathPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CompanyPathDO> list = pathService.getPathPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "抛砖路径.xls", "数据", CompanyPathRespVO.class,
                BeanUtils.toBean(list, CompanyPathRespVO.class));
    }


}