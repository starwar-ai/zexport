package com.syj.eplus.module.sms.controller.admin.otherfee;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeePageReqVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeRespVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import com.syj.eplus.module.sms.service.otherfee.OtherFeeService;
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

@Tag(name = "管理后台 - 其他费用")
@RestController
@RequestMapping("/sms/other-fee")
@Validated
public class OtherFeeController {

    @Resource
    private OtherFeeService otherFeeService;

    @PostMapping("/create")
    @Operation(summary = "创建其他费用")
    @PreAuthorize("@ss.hasPermission('sms:other-fee:create')")
    public CommonResult<Long> createOtherFee(@Valid @RequestBody OtherFeeSaveReqVO createReqVO) {
        return success(otherFeeService.createOtherFee(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新其他费用")
    @PreAuthorize("@ss.hasPermission('sms:other-fee:update')")
    public CommonResult<Boolean> updateOtherFee(@Valid @RequestBody OtherFeeSaveReqVO updateReqVO) {
        otherFeeService.updateOtherFee(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除其他费用")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('sms:other-fee:delete')")
    public CommonResult<Boolean> deleteOtherFee(@RequestParam("id") Long id) {
        otherFeeService.deleteOtherFee(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得其他费用")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('sms:other-fee:query')")
    public CommonResult<OtherFeeRespVO> getOtherFee(@RequestParam("id") Long id) {
            OtherFeeRespVO otherFee = otherFeeService.getOtherFee (id);
            return success(otherFee);
    }
    @GetMapping("/page")
    @Operation(summary = "获得其他费用分页")
    @PreAuthorize("@ss.hasPermission('sms:other-fee:query')")
    public CommonResult<PageResult<OtherFeeRespVO>> getOtherFeePage(@Valid OtherFeePageReqVO pageReqVO) {
        PageResult<OtherFeeDO> pageResult = otherFeeService.getOtherFeePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OtherFeeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出其他费用 Excel")
    @PreAuthorize("@ss.hasPermission('sms:other-fee:export')")
    @OperateLog(type = EXPORT)
    public void exportOtherFeeExcel(@Valid OtherFeePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OtherFeeDO> list = otherFeeService.getOtherFeePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "其他费用.xls", "数据", OtherFeeRespVO.class,
                        BeanUtils.toBean(list, OtherFeeRespVO.class));
    }

}