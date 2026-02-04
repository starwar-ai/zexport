package cn.iocoder.yudao.module.system.controller.admin.port;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortRespVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.port.PortDO;
import cn.iocoder.yudao.module.system.service.port.PortService;
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

@Tag(name = "管理后台 - 口岸")
@RestController
@RequestMapping("/system/port")
@Validated
public class PortController {

    @Resource
    private PortService portService;

    @PostMapping("/create")
    @Operation(summary = "创建口岸")
//    @PreAuthorize("@ss.hasPermission('system:port:create')")
    public CommonResult<Long> createPort(@Valid @RequestBody PortSaveReqVO createReqVO) {
        return success(portService.createPort(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新口岸")
//    @PreAuthorize("@ss.hasPermission('system:port:update')")
    public CommonResult<Boolean> updatePort(@Valid @RequestBody PortSaveReqVO updateReqVO) {
        portService.updatePort(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除口岸")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('system:port:delete')")
    public CommonResult<Boolean> deletePort(@RequestParam("id") Long id) {
        portService.deletePort(id);
        return success(true);
    }

    //        @GetMapping("/detail")
//    @Operation(summary = "获得口岸")
//    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('system:port:query')")
//    public CommonResult<List<PortDTO>> getPort(@RequestParam("portDTO") PortDTO portDTO) {
//            List<PortDTO> port = portService.getPort(portDTO);
//            return success(port);
//    }
    @GetMapping("/detail")
    @Operation(summary = "获得口岸")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('system:port:query')")
    public CommonResult<PortDO> getPort(@RequestParam("id") Long id) {
        PortDO port = portService.getPort(id);
        return success(port);
    }

    @GetMapping("/page")
    @Operation(summary = "获得口岸分页")
//    @PreAuthorize("@ss.hasPermission('system:port:query')")
    public CommonResult<PageResult<PortRespVO>> getPortPage(@Valid PortPageReqVO pageReqVO) {
        PageResult<PortRespVO> pageResult = portService.getPortPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出口岸 Excel")
    @PreAuthorize("@ss.hasPermission('system:port:export')")
    @OperateLog(type = EXPORT)
    public void exportPortExcel(@Valid PortPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PortRespVO> list = portService.getPortPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "口岸.xls", "数据", PortRespVO.class,
                list);
    }


    @GetMapping("/get-simple-list")
    @Operation(summary = "获取口岸精简列表")
    public CommonResult<PageResult<PortRespVO>> getPortSimplePage(@Valid PortPageReqVO pageReqVO) {
        PageResult<PortRespVO> pageResult = portService.getPortPage(pageReqVO);
        return success(pageResult);
    }

    @PostMapping("/top")
    @Operation(summary = "口岸置顶")
    public CommonResult<Boolean> topPort(@Valid Long id) {
        return success(portService.topPort(id));
    }
    @PostMapping("/rollback-top")
    @Operation(summary = "口岸取消置顶")
    public CommonResult<Boolean> rollbackTopPort(@Valid Long id) {
        return success(portService.rollbackTopPort(id));
    }
}