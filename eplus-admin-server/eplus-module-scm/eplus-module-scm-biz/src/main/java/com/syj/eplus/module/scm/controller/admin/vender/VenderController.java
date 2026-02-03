package com.syj.eplus.module.scm.controller.admin.vender;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.*;
import com.syj.eplus.module.scm.service.vender.VenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.UPDATE_VENDER_FAIL;


@Tag(name = "管理后台 - 供应商信息")
@RestController
@RequestMapping("/scm/vender")
@Validated
public class VenderController {

    @Resource
    private VenderService venderService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @Operation(summary = "创建供应商信息")
    @PreAuthorize("@ss.hasPermission('scm:vender:create')")
    public CommonResult<Long> createVender(@Valid @RequestBody VenderInfoSaveReqVO createReqVO) {
        return success(venderService.createVender(createReqVO, false));
    }

    @PutMapping("/convert")
    @Operation(summary = "转正式供应商")
    @PreAuthorize("@ss.hasPermission('scm:vender:convert')")
    public CommonResult<Boolean> convertVender(@Valid @RequestBody VenderInfoSaveReqVO updateReqVO) {
        ServiceException serviceException = venderService.convertVender(updateReqVO);
        if(Objects.isNull(serviceException)){
            return success(true);
        }else {
            return error(serviceException);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商信息")
    @PreAuthorize("@ss.hasPermission('scm:vender:update')")
    public CommonResult<Boolean> updateVender(@Valid @RequestBody VenderInfoSaveReqVO updateReqVO) {
        try {
            venderService.updateVender(updateReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(UPDATE_VENDER_FAIL);
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender:delete')")
    public CommonResult<Boolean> deleteVender(@RequestParam("id") Long id) {
        venderService.deleteVender(id, true);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得供应商信息")
    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<VenderInfoRespVO> getVenderById(@RequestParam("id") Long id) {
        VenderInfoRespVO vender = venderService.getVender(new ScmVenderDetailReq().setVenderId(id));
        return success(vender);
    }

    @GetMapping("/detail-by-code")
    @Operation(summary = "根据编号获得供应商信息")
    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
    @Parameter(name = "code", description = "编号", required = true, example = "1024")
    public CommonResult<VenderInfoRespVO> getVenderByCode(@RequestParam("code") String code) {
        VenderInfoRespVO vender = venderService.getVender(new ScmVenderDetailReq().setVenderCode(code));
        return success(vender);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得供应商信息")
    public CommonResult<VenderInfoRespVO> getVenderByProcessId(@RequestParam("id") String id) {
        VenderInfoRespVO vender = venderService.getVender(new ScmVenderDetailReq().setProcessInstanceId(id));
        return success(vender);
    }

    @GetMapping("/page")
    @Operation(summary = "获得供应商信息分页")
    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
    public CommonResult<PageResult<VenderRespVO>> getVenderPage(@Valid VenderPageReqVO pageReqVO) {
        PageResult<VenderRespVO> pageResult = venderService.getVenderPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/page-no-permission")
    @DataPermission(enable = false)
    @Operation(summary = "获得供应商信息分页无数据权限")
    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
    public CommonResult<PageResult<VenderRespVO>> getVenderPageNoPermission(@Valid VenderPageReqVO pageReqVO) {
        PageResult<VenderRespVO> pageResult = venderService.getVenderPage(pageReqVO);
        return success(pageResult);
    }

//    @GetMapping("/get-simple-list")
//    @Operation(summary = "获得供应商精简列表")
//    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
//    public CommonResult<PageResult<VenderRespVO>> getVenderPage(@Valid PageParam pageReqVO) {
//        PageResult<VenderRespVO> pageResult = venderService.getVenderPage(pageReqVO);
//        return success(pageResult);
//    }


    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('scm:vender:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        venderService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('scm:vender:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        venderService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商信息 Excel")
    @PreAuthorize("@ss.hasPermission('scm:vender:export')")
    @OperateLog(type = EXPORT)
    public void exportVenderExcel(@Valid VenderPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<VenderRespVO> list = venderService.getVenderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "供应商信息.xls", "数据", VenderRespVO.class,
                list);
    }

    @GetMapping("/get-payable-list")
    @Operation(summary = "获得应付供应商列表")
//    @PreAuthorize("@ss.hasPermission('scm:vender:query')")
    @DataPermission(enable = false)
    public CommonResult<List<SimpleVenderRespDTO>> getPayableVender(String venderName) {
        List<SimpleVenderRespDTO> simpleVenderRespDTO = venderService.getSimpleVenderRespDTO(venderName);
        return success(simpleVenderRespDTO);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得供应商精简信息")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleVenderRespDTO>> getSimpleVender(VenderPageReqVO pageParam) {
        PageResult<SimpleVenderRespDTO> simpleVender = venderService.getSimpleVender(pageParam);
        return success(simpleVender);
    }

    @GetMapping("/get-simple-list-buyer")
    @Operation(summary = "通过采购员获得供应商精简信息")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleVenderRespDTO>> getSimpleVenderByBuyerId(VenderSimpleReqVO reqVO) {
        PageResult<SimpleVenderRespDTO> simpleVender = venderService.getSimpleVenderByBuyerUserId(reqVO);
        return success(simpleVender);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('scm:vender:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long venderId) {
        venderService.submitTask(venderId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/enable")
    @Operation(summary = "启用")
    @PreAuthorize("@ss.hasPermission('scm:vender:enable')")
    public CommonResult<Boolean> enableVender(@RequestParam Long venderId) {
        venderService.enableVender(venderId);
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用")
    @PreAuthorize("@ss.hasPermission('scm:vender:disable')")
    public CommonResult<Boolean> disableVender(@RequestParam Long venderId) {
        venderService.disableVender(venderId);
        return success(true);
    }

    @PutMapping("/change")
    @Operation(summary = "变更供应商")
    @PreAuthorize("@ss.hasPermission('scm:vender:change')")
    public CommonResult<Boolean> changeVender(@Valid @RequestBody VenderInfoSaveReqVO changeReqVO) {
        Long venderId;
        try {
            venderId = venderService.changeVender(changeReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(UPDATE_VENDER_FAIL.getCode(), UPDATE_VENDER_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得变更供应商分页")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:query')")
    public CommonResult<PageResult<VenderRespVO>> getVenderChangePage(@Valid VenderPageReqVO pageReqVO) {
        PageResult<VenderRespVO> pageResult = venderService.getVenderChangePage(pageReqVO);
        List<VenderRespVO> venderList = pageResult.getList();
        if (CollUtil.isEmpty(venderList)) {
            return success(PageResult.empty());
        }
        return success(pageResult);
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('scm:vender:change')")
    @DataPermission(enable = false)
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody VenderInfoSaveReqVO changeReqVO) {
        VenderInfoRespVO vender = BeanUtils.toBean(changeReqVO, VenderInfoRespVO.class);
        vender.setVer(vender.getVer() + 1);
        return success(venderService.getChangeEffect(vender));
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新变更供应商")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:update')")
    public CommonResult<Boolean> updateChangeVender(@Valid @RequestBody VenderInfoSaveReqVO updateReqVO) {
        return updateVender(updateReqVO);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除变更供应商")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender-change:delete')")
    public CommonResult<Boolean> deleteChangeVender(@RequestParam("id") Long id) {
        venderService.deleteChangeVender(id);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得变更供应商详情")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<VenderRespVO> getChangeVenderById(@RequestParam("id") Long id) {
        VenderRespVO venderRespVo = venderService.getVenderChange(new ScmVenderDetailReq().setVenderId(id));
        return success(venderRespVo);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得变更供应商详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<VenderRespVO> getChangeVenderByProcessId(@RequestParam("id") String id) {
        VenderRespVO venderRespVo = venderService.getVenderChange(new ScmVenderDetailReq().setProcessInstanceId(id));
        return success(venderRespVo);
    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        venderService.approveChangeTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        venderService.rejectChangeTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-submit")
    @Operation(summary = "提交变更任务")
    @PreAuthorize("@ss.hasPermission('scm:vender-change:submit')")
    public CommonResult<Boolean> submitChangeTask(@RequestParam Long venderId) {
        venderService.submitChangeTask(venderId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @GetMapping("/check-name")
    @Operation(summary = "校验名称")
    public CommonResult<List<String>> getSimpleVenderName(@RequestParam("name") String name) {
        List<String> nameList = venderService.checkName(name);
        return success(nameList);
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('scm:vender:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long venderId) {
        return success(venderService.antiAudit(venderId));
    }

    @GetMapping("/validate-vender")
    @Operation(summary = "校验供应商是否可用")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<Boolean> validateVenderAvailable(@RequestParam("codeList") List<String> codeList) {
        venderService.validateVenderAvailable(codeList);
        return success(true);
    }

    @DeleteMapping("/manager-delete")
    @Operation(summary = "管理员删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender:manager-delete')")
    public CommonResult<Boolean> managerDeleteVender(@RequestParam("id") Long id) {
        venderService.managerDeleteVender(id);
        return success(true);
    }
}