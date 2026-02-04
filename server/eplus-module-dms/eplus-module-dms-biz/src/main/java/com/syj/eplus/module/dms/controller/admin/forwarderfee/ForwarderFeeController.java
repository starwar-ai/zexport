package com.syj.eplus.module.dms.controller.admin.forwarderfee;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeePageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.service.forwarderfee.ForwarderFeeService;
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

@Tag(name = "管理后台 - 船代费用")
@RestController
@RequestMapping("/dms/forwarder-fee")
@Validated
public class ForwarderFeeController {

    @Resource
    private ForwarderFeeService forwarderFeeService;

    @PostMapping("/create")
    @Operation(summary = "创建船代费用")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:create')")
    public CommonResult<Long> createForwarderFee(@Valid @RequestBody ForwarderFeeSaveReqVO createReqVO) {
        return success(forwarderFeeService.createForwarderFee(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新船代费用")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:update')")
    public CommonResult<Boolean> updateForwarderFee(@Valid @RequestBody ForwarderFeeSaveReqVO updateReqVO) {
        forwarderFeeService.updateForwarderFee(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除船代费用")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:delete')")
    public CommonResult<Boolean> deleteForwarderFee(@RequestParam("id") Long id) {
        forwarderFeeService.deleteForwarderFee(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得船代费用")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:query')")
    public CommonResult<ForwarderFeeRespVO> getForwarderFee(@RequestParam("id") Long id) {
        ForwarderFeeRespVO forwarderFee = forwarderFeeService.getForwarderFee(id);
        return success(forwarderFee);
    }
    @GetMapping("/page")
    @Operation(summary = "获得船代费用分页")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:query')")
    public CommonResult<PageResult<ForwarderFeeRespVO>> getForwarderFeePage(@Valid ForwarderFeePageReqVO pageReqVO) {
        PageResult<ForwarderFeeDO> pageResult = forwarderFeeService.getForwarderFeePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ForwarderFeeRespVO.class));
    }

    @GetMapping("/apply-page")
    @Operation(summary = "获得船代费用申请分页")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:query')")
    public CommonResult<PageResult<ForwarderFeeDO>> getForwarderFeeApplyPage(@Valid ForwarderFeePageReqVO pageReqVO) {
        PageResult<ForwarderFeeDO> pageResult = forwarderFeeService.getForwarderFeeApplyPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出船代费用 Excel")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-fee:export')")
    @OperateLog(type = EXPORT)
    public void exportForwarderFeeExcel(@Valid ForwarderFeePageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ForwarderFeeDO> list = forwarderFeeService.getForwarderFeePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "船代费用.xls", "数据", ForwarderFeeRespVO.class,
                BeanUtils.toBean(list, ForwarderFeeRespVO.class));
    }

}