package cn.iocoder.yudao.module.system.controller.admin.userprefence;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefencePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceRespVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import cn.iocoder.yudao.module.system.service.userprefence.UserPrefenceService;
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


@Tag(name = "管理后台 - 用户偏好设置")
@RestController
@RequestMapping("/system/user-prefence")
@Validated
public class UserPrefenceController {

    @Resource
    private UserPrefenceService userPrefenceService;

    @PostMapping("/create")
    @Operation(summary = "创建用户偏好设置")
    @PreAuthorize("@ss.hasPermission('system:user-prefence:create')")
    public CommonResult<Long> createUserPrefence(@Valid @RequestBody UserPrefenceSaveReqVO createReqVO) {
        return success(userPrefenceService.createUserPrefence(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户偏好设置")
    @PreAuthorize("@ss.hasPermission('system:user-prefence:update')")
    public CommonResult<Boolean> updateUserPrefence(@Valid @RequestBody UserPrefenceSaveReqVO updateReqVO) {
        userPrefenceService.updateUserPrefence(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户偏好设置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:user-prefence:delete')")
    public CommonResult<Boolean> deleteUserPrefence(@RequestParam("id") Long id) {
        userPrefenceService.deleteUserPrefence(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得用户偏好设置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user-prefence:query')")
    public CommonResult<UserPrefenceRespVO> getUserPrefence(@RequestParam("id") Long id) {
        UserPrefenceRespVO userPrefence = userPrefenceService.getUserPrefence(id);
        return success(userPrefence);
    }
    @GetMapping("/page")
    @Operation(summary = "获得用户偏好设置分页")
    @PreAuthorize("@ss.hasPermission('system:user-prefence:query')")
    public CommonResult<PageResult<UserPrefenceRespVO>> getUserPrefencePage(@Valid UserPrefencePageReqVO pageReqVO) {
        PageResult<UserPrefenceDO> pageResult = userPrefenceService.getUserPrefencePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserPrefenceRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户偏好设置 Excel")
    @PreAuthorize("@ss.hasPermission('system:user-prefence:export')")
    @OperateLog(type = EXPORT)
    public void exportUserPrefenceExcel(@Valid UserPrefencePageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserPrefenceDO> list = userPrefenceService.getUserPrefencePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户偏好设置.xls", "数据", UserPrefenceRespVO.class,
                BeanUtils.toBean(list, UserPrefenceRespVO.class));
    }


}