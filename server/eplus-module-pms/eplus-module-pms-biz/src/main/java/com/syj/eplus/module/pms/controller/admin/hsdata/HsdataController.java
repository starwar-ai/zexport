package com.syj.eplus.module.pms.controller.admin.hsdata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataPageReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataRespVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import com.syj.eplus.module.pms.service.hsdata.HsdataService;
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


@Tag(name = "管理后台 - 海关编码")
@RestController
@RequestMapping("/pms/hsdata")
@Validated
public class HsdataController {

    @Resource
    private HsdataService hsdataService;

    @PostMapping("/create")
    @Operation(summary = "创建海关编码")
//    @PreAuthorize("@ss.hasPermission('pms:hsdata:create')")
    public CommonResult<Long> createHsdata(@Valid @RequestBody HsdataSaveReqVO createReqVO) {
        return success(hsdataService.createHsdata(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海关编码")
//    @PreAuthorize("@ss.hasPermission('pms:hsdata:update')")
    public CommonResult<Boolean> updateHsdata(@Valid @RequestBody HsdataSaveReqVO updateReqVO) {
        hsdataService.updateHsdata(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海关编码")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:hsdata:delete')")
    public CommonResult<Boolean> deleteHsdata(@RequestParam("id") Long id) {
        hsdataService.deleteHsdata(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海关编码")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:hsdata:query')")
    public CommonResult<HsdataRespVO> getHsdata(@RequestParam("id") Long id) {
        return success(hsdataService.getHsdata(id));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得海关编码")
    public CommonResult<List<HsdataSimpleRespVO>> getSimpleHsdata(@RequestParam String code) {
        List<HsdataSimpleRespVO> hsdataSimpleList = hsdataService.getHsdataSimpleList(code);
        return success(hsdataSimpleList);
    }


    @GetMapping("/get-unit-list")
    @Operation(summary = "获得海关编码单位列表")
    public CommonResult<List<String>> getSimpleUnitHsdata() {
        List<String> hsdataSimpleList = hsdataService.getSimpleUnitHsdata();
        return success(hsdataSimpleList);
    }



    @GetMapping("/page")
    @Operation(summary = "获得海关编码分页")
//    @PreAuthorize("@ss.hasPermission('pms:hsdata:query')")
    public CommonResult<PageResult<HsdataRespVO>> getHsdataPage(@Valid HsdataPageReqVO pageReqVO) {
        PageResult<HsdataDO> pageResult = hsdataService.getHsdataPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HsdataRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出海关编码 Excel")
    @PreAuthorize("@ss.hasPermission('pms:hsdata:export')")
    @OperateLog(type = EXPORT)
    public void exportHsdataExcel(@Valid HsdataPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HsdataDO> list = hsdataService.getHsdataPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "海关编码.xls", "数据", HsdataRespVO.class,
                BeanUtils.toBean(list, HsdataRespVO.class));
    }

    @GetMapping("/get-hsdata-by-code")
    @Operation(summary = "通过编号获得海关编码")
    @PreAuthorize("@ss.hasPermission({'pms:hsdata:query','pms:sku:query'})")
    public CommonResult<HsdataSimpleRespVO> getHsdataByCode(@RequestParam String code) {
        return success(hsdataService.getHsdataByCode(code));
    }


}