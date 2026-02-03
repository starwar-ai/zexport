package com.syj.eplus.module.oa.controller.admin.reimbshare;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbSharePageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbshare.ReimbShareDO;
import com.syj.eplus.module.oa.service.reimbshare.ReimbShareService;
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


@Tag(name = "管理后台 - 报销分摊")
@RestController
@RequestMapping("/oa/reimb-share")
@Validated
public class ReimbShareController {

    @Resource
    private ReimbShareService reimbShareService;

    @PostMapping("/create")
    @Operation(summary = "创建报销分摊")
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:create')")
    public CommonResult<Long> createReimbShare(@Valid @RequestBody ReimbShareSaveReqVO createReqVO) {
        return success(reimbShareService.createReimbShare(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报销分摊")
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:update')")
    public CommonResult<Boolean> updateReimbShare(@Valid @RequestBody ReimbShareSaveReqVO updateReqVO) {
        reimbShareService.updateReimbShare(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报销分摊")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:delete')")
    public CommonResult<Boolean> deleteReimbShare(@RequestParam("id") Long id) {
        reimbShareService.deleteReimbShare(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得报销分摊")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:query')")
    public CommonResult<ReimbShareRespVO> getReimbShare(@RequestParam("id") Long id) {
            ReimbShareRespVO reimbShare = reimbShareService.getReimbShare(id);
            return success(reimbShare);
    }
    @GetMapping("/page")
    @Operation(summary = "获得报销分摊分页")
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:query')")
    public CommonResult<PageResult<ReimbShareRespVO>> getReimbSharePage(@Valid ReimbSharePageReqVO pageReqVO) {
        PageResult<ReimbShareDO> pageResult = reimbShareService.getReimbSharePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReimbShareRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报销分摊 Excel")
    @PreAuthorize("@ss.hasPermission('oa:reimb-share:export')")
    @OperateLog(type = EXPORT)
    public void exportReimbShareExcel(@Valid ReimbSharePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReimbShareDO> list = reimbShareService.getReimbSharePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "报销分摊.xls", "数据", ReimbShareRespVO.class,
                        BeanUtils.toBean(list, ReimbShareRespVO.class));
    }


}