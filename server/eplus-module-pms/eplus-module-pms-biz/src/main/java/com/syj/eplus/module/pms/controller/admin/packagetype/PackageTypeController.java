package com.syj.eplus.module.pms.controller.admin.packagetype;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.*;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;
import com.syj.eplus.module.pms.service.packagetype.PackageTypeService;
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

@Tag(name = "管理后台 - 包装方式")
@RestController
@RequestMapping("/pms/package-type")
@Validated
public class PackageTypeController {

    @Resource
    private PackageTypeService packageTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建包装方式")
    @PreAuthorize("@ss.hasPermission('pms:package-type:create')")
    public CommonResult<Long> createPackageType(@Valid @RequestBody PackageTypeSaveReqVO createReqVO) {
        return success(packageTypeService.createPackageType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新包装方式")
    @PreAuthorize("@ss.hasPermission('pms:package-type:update')")
    public CommonResult<Boolean> updatePackageType(@Valid @RequestBody PackageTypeSaveReqVO updateReqVO) {
        packageTypeService.updatePackageType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除包装方式")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:package-type:delete')")
    public CommonResult<Boolean> deletePackageType(@RequestParam("id") Long id) {
        packageTypeService.deletePackageType(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得包装方式")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:package-type:query')")
    public CommonResult<PackageTypeRespVO> getPackageType(@RequestParam("id") Long id) {
            PackageTypeRespVO packageType = packageTypeService.getPackageType
            (new PackageTypeDetailReq().setPackageTypeId(id));
            return success(packageType);
    }
                @GetMapping("/audit-detail")
                @Operation(summary = "获得包装方式详情")
                @Parameter(name = "id", description = "编号", required = true, example = "uuid")
                public CommonResult
            <PackageTypeRespVO> getPackageTypeByProcessId(@RequestParam("id")
                String id) {
                PackageTypeRespVO packageType = packageTypeService.getPackageType
                (new PackageTypeDetailReq().setProcessInstanceId(id));
                return success(packageType);
                }
    @GetMapping("/page")
    @Operation(summary = "获得包装方式分页")
    @PreAuthorize("@ss.hasPermission('pms:package-type:query')")
    public CommonResult<PageResult<PackageTypeRespVO>> getPackageTypePage(@Valid PackageTypePageReqVO pageReqVO) {
        PageResult<PackageTypeDO> pageResult = packageTypeService.getPackageTypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PackageTypeRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得包装方式全数据")
    public CommonResult<List<PackageTypeRespVO>> getSimpleList(@Valid PackageTypeSimplePageReqVO pageReqVO) {
        List<PackageTypeDO> pageResult = packageTypeService.getList(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PackageTypeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出包装方式 Excel")
    @PreAuthorize("@ss.hasPermission('pms:package-type:export')")
    @OperateLog(type = EXPORT)
    public void exportPackageTypeExcel(@Valid PackageTypePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PackageTypeDO> list = packageTypeService.getPackageTypePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "包装方式.xls", "数据", PackageTypeRespVO.class,
                        BeanUtils.toBean(list, PackageTypeRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('pms:package-type:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
    packageTypeService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
    return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('pms:package-type:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
    packageTypeService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
    return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('pms:package-type:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
    packageTypeService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
    return success(true);
    }


}