package com.syj.eplus.module.scm.controller.admin.venderpoc;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocRespVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.service.venderpoc.VenderPocService;
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

@Tag(name = "管理后台 - 供应商联系人")
@RestController
@RequestMapping("/scm/vender-poc")
@Validated
public class VenderPocController {

    @Resource
    private VenderPocService venderPocService;

    @PostMapping("/create")
    @Operation(summary = "创建供应商联系人")
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:create')")
    public CommonResult<Long> createVenderPoc(@Valid @RequestBody VenderPocSaveReqVO createReqVO) {
        return success(venderPocService.createVenderPoc(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商联系人")
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:update')")
    public CommonResult<Boolean> updateVenderPoc(@Valid @RequestBody VenderPocSaveReqVO updateReqVO) {
        venderPocService.updateVenderPoc(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商联系人")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:delete')")
    public CommonResult<Boolean> deleteVenderPoc(@RequestParam("id") Long id) {
        venderPocService.deleteVenderPoc(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得供应商联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:query')")
    public CommonResult<VenderPocRespVO> getVenderPoc(@RequestParam("id") Long id) {
        VenderPocDO venderPoc = venderPocService.getVenderPoc(id);
        return success(BeanUtils.toBean(venderPoc, VenderPocRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得供应商联系人分页")
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:query')")
    public CommonResult<PageResult<VenderPocRespVO>> getVenderPocPage(@Valid VenderPocPageReqVO pageReqVO) {
        PageResult<VenderPocDO> pageResult = venderPocService.getVenderPocPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, VenderPocRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商联系人 Excel")
    @PreAuthorize("@ss.hasPermission('scm:vender-poc:export')")
    @OperateLog(type = EXPORT)
    public void exportVenderPocExcel(@Valid VenderPocPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<VenderPocDO> list = venderPocService.getVenderPocPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "供应商联系人.xls", "数据", VenderPocRespVO.class,
                        BeanUtils.toBean(list, VenderPocRespVO.class));
    }

}