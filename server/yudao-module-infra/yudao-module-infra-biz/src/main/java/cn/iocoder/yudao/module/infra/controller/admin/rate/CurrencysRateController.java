package cn.iocoder.yudao.module.infra.controller.admin.rate;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRatePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRateRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.rate.CurrencysRateDO;
import cn.iocoder.yudao.module.infra.pojo.CurrencysRate;
import cn.iocoder.yudao.module.infra.service.rate.CurrencysRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 币种费率")
@RestController
@RequestMapping("/infra/rate")
@Validated
@Slf4j
public class CurrencysRateController {
    @Resource
    private CurrencysRateService currencysRateService;


    @GetMapping("/get-simple-list")
    @Operation(summary = "获得税率列表")
    public CommonResult<List<CurrencysRate>> getCurrencysRateList() {
        List<CurrencysRate> currencysRateList = currencysRateService.getCurrencysRateList();
        return success(currencysRateList);
    }

    @GetMapping("/get-simple-map")
    @Operation(summary = "获得税率缓存")
    public CommonResult<Map<String, BigDecimal>> getCurrencysRateMap() {
        Map<String, BigDecimal> dailyRateMap = currencysRateService.getDailyRateMap();
        return success(dailyRateMap);
    }
    @GetMapping("/get")
    @Operation(summary = "获得动态汇率")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:currencys-rate:query')")
    public CommonResult<CurrencysRateRespVO> getCurrencysRate(@RequestParam("id") Long id) {
        CurrencysRateDO currencysRate = currencysRateService.getCurrencysRate(id);
        return success(BeanUtils.toBean(currencysRate, CurrencysRateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得动态汇率分页")
    @PreAuthorize("@ss.hasPermission('system:currencys-rate:query')")
    public CommonResult<PageResult<CurrencysRateRespVO>> getCurrencysRatePage(@Valid CurrencysRatePageReqVO pageReqVO) {
        PageResult<CurrencysRateDO> pageResult = currencysRateService.getCurrencysRatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CurrencysRateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出动态汇率 Excel")
    @PreAuthorize("@ss.hasPermission('system:currencys-rate:export')")
    public void exportCurrencysRateExcel(@Valid CurrencysRatePageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CurrencysRateDO> list = currencysRateService.getCurrencysRatePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "动态汇率.xls", "数据", CurrencysRateRespVO.class,
                BeanUtils.toBean(list, CurrencysRateRespVO.class));
    }

    @GetMapping("/get-by-date")
    @Operation(summary = "获取指定日期动态汇率")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('system:currencys-rate:query')")
    public CommonResult<List<CurrencysRateRespVO>> getCurrencysRateByDate(@RequestParam("dailyCurrDate") String dailyCurrDate) {
        List<CurrencysRateRespVO> currencysRate = currencysRateService.getCurrencysRateByDate(dailyCurrDate);
        return success(currencysRate);
    }
}
