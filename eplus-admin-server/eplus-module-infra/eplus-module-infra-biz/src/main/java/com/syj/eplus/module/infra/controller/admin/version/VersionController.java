package com.syj.eplus.module.infra.controller.admin.version;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionPageReqVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionRespVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.version.VersionDO;
import com.syj.eplus.module.infra.service.version.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 版本记录")
@RestController
@RequestMapping("/infra/version")
@Validated
public class VersionController {

    @Resource
    private VersionService versionService;

    @PostMapping("/create")
    @Operation(summary = "创建版本记录")
    public CommonResult<Long> createVersion(@Valid @RequestBody VersionSaveReqVO createReqVO) {
        return success(versionService.createVersion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新版本记录")
    public CommonResult<Boolean> updateVersion(@Valid @RequestBody VersionSaveReqVO updateReqVO) {
        versionService.updateVersion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除版本记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteVersion(@RequestParam("id") Long id) {
        versionService.deleteVersion(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得版本记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<VersionRespVO> getVersion(@RequestParam("id") Long id) {
        VersionRespVO version = versionService.getVersion(id);
        return success(version);
    }

    @GetMapping("/get-last")
    @Operation(summary = "获得版本记录")
    public CommonResult<VersionRespVO> getVersion() {
        VersionRespVO version = versionService.getLastVersion();
        return success(version);
    }


    @GetMapping("/page")
    @Operation(summary = "获得版本记录分页")
    public CommonResult<PageResult<VersionRespVO>> getVersionPage(@Valid VersionPageReqVO pageReqVO) {
        PageResult<VersionDO> pageResult = versionService.getVersionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, VersionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出版本记录 Excel")
    @OperateLog(type = EXPORT)
    public void exportVersionExcel(@Valid VersionPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<VersionDO> list = versionService.getVersionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "版本记录.xls", "数据", VersionRespVO.class,
                BeanUtils.toBean(list, VersionRespVO.class));
    }




}