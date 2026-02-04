package com.syj.eplus.module.infra.controller.admin.countryinfo;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoPageReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoRespVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoSaveReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.RegionRespVO;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;
import com.syj.eplus.module.infra.service.countryinfo.CountryInfoService;
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

@Tag(name = "管理后台 - 国家信息")
@RestController
@RequestMapping("/infra/country-info")
@Validated
public class CountryInfoController {

    @Resource
    private CountryInfoService countryInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建国家信息")
    @PreAuthorize("@ss.hasPermission('system:country-info:create')")
    public CommonResult<Long> createCountryInfo(@Valid @RequestBody CountryInfoSaveReqVO createReqVO) {
        return success(countryInfoService.createCountryInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新国家信息")
    @PreAuthorize("@ss.hasPermission('system:country-info:update')")
    public CommonResult<Boolean> updateCountryInfo(@Valid @RequestBody CountryInfoSaveReqVO updateReqVO) {
        countryInfoService.updateCountryInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除国家信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:country-info:delete')")
    public CommonResult<Boolean> deleteCountryInfo(@RequestParam("id") Long id) {
        countryInfoService.deleteCountryInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得国家信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<List<CountryInfoRespVO>> getCountryInfo() {
        List<CountryInfoDO> countryInfo = countryInfoService.getCountryInfo();
        return success(BeanUtils.toBean(countryInfo, CountryInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得国家信息分页")
    @PreAuthorize("@ss.hasPermission('system:country-info:query')")
    public CommonResult<PageResult<CountryInfoRespVO>> getCountryInfoPage(@Valid CountryInfoPageReqVO pageReqVO) {
        PageResult<CountryInfoDO> pageResult = countryInfoService.getCountryInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CountryInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出国家信息 Excel")
    @PreAuthorize("@ss.hasPermission('system:country-info:export')")
    @OperateLog(type = EXPORT)
    public void exportCountryInfoExcel(@Valid CountryInfoPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CountryInfoDO> list = countryInfoService.getCountryInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "国家信息.xls", "数据", CountryInfoRespVO.class,
                BeanUtils.toBean(list, CountryInfoRespVO.class));
    }

    @GetMapping("/get-region")
    @Operation(summary = "获得地区信息")
    public CommonResult<List<RegionRespVO>> getRegionList() {
        return success(countryInfoService.getRegionList());
    }

}
