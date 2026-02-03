package com.syj.eplus.module.oa.controller.admin.travelapp;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.SimpleTravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppRespVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.travelapp.TravelAppDO;
import com.syj.eplus.module.oa.service.travelapp.TravelAppService;
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

@Tag(name = "管理后台 - 出差申请")
@RestController
@RequestMapping("/oa/travel-app")
@Validated
public class TravelAppController {

    @Resource
    private TravelAppService travelAppService;

    @PostMapping("/create")
    @Operation(summary = "创建出差申请")
    @PreAuthorize("@ss.hasPermission('oa:travel-app:create')")
    public CommonResult<Long> createTravelApp(@Valid @RequestBody TravelAppSaveReqVO createReqVO) {
        return success(travelAppService.createTravelApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出差申请")
    @PreAuthorize("@ss.hasPermission('oa:travel-app:update')")
    public CommonResult<Boolean> updateTravelApp(@Valid @RequestBody TravelAppSaveReqVO updateReqVO) {
        travelAppService.updateTravelApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出差申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:travel-app:delete')")
    public CommonResult<Boolean> deleteTravelApp(@RequestParam("id") Long id) {
        travelAppService.deleteTravelApp(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得出差申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:travel-app:query')")
    public CommonResult<TravelAppRespVO> getTravelApp(@RequestParam("id") Long id) {
        TravelAppDO travelApp = travelAppService.getTravelApp(id);
        return success(BeanUtils.toBean(travelApp, TravelAppRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得出差申请分页")
    @PreAuthorize("@ss.hasPermission('oa:travel-app:query')")
    public CommonResult<PageResult<TravelAppRespVO>> getTravelAppPage(@Valid TravelAppPageReqVO pageReqVO) {
        PageResult<TravelAppDO> pageResult = travelAppService.getTravelAppPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TravelAppRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得出差申请精简分页列表")
    public CommonResult<PageResult<SimpleTravelAppRespDTO>> getsimpleTravelAppPage(@Valid SimpleTravelAppPageReqVO pageReqVO) {
        PageResult<SimpleTravelAppRespDTO> pageResult = travelAppService.getSimpleTravelAppPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出出差申请 Excel")
    @PreAuthorize("@ss.hasPermission('oa:travel-app:export')")
    @OperateLog(type = EXPORT)
    public void exportTravelAppExcel(@Valid TravelAppPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TravelAppDO> list = travelAppService.getTravelAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出差申请.xls", "数据", TravelAppRespVO.class,
                BeanUtils.toBean(list, TravelAppRespVO.class));
    }

}