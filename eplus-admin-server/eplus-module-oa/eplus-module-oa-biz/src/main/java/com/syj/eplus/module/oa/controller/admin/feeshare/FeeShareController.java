package com.syj.eplus.module.oa.controller.admin.feeshare;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.*;
import com.syj.eplus.module.oa.service.feeshare.FeeShareService;
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


@Tag(name = "管理后台 - 费用归集")
@RestController
@RequestMapping("/oa/fee-share")
@Validated
public class FeeShareController {

    @Resource
    private FeeShareService feeShareService;

    @PostMapping("/create")
    @Operation(summary = "创建费用归集")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:create')")
    public CommonResult<Long> createFeeShare(@Valid @RequestBody FeeShareSaveReqVO createReqVO) {
        return success(feeShareService.createFeeShare(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新费用归集")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:update')")
    public CommonResult<Boolean> updateFeeShare(@Valid @RequestBody FeeShareSaveReqVO updateReqVO) {
        feeShareService.updateFeeShare(updateReqVO);
        return success(true);
    }

    @PutMapping("/batch-update")
    @Operation(summary = "更新费用归集")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:update')")
    public CommonResult<Boolean> updateFeeShareList(@Valid @RequestBody FeeShareListSaveReqVO updateReqVO) {
        feeShareService.updateFeeShareList(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除费用归集")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:fee-share:delete')")
    public CommonResult<Boolean> deleteFeeShare(@RequestParam("id") Long id) {
        feeShareService.deleteFeeShare(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得费用归集")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:query')")
    public CommonResult<FeeShareRespVO> getFeeShare(FeeShareDetailReq req) {
        FeeShareRespVO feeShare = feeShareService.getFeeShare(req);
        return success(feeShare);
    }

    @GetMapping("/page")
    @Operation(summary = "获得费用归集分页")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:query')")
    public CommonResult<PageResult<FeeSharePageRespVO>> getFeeSharePage(@Valid FeeSharePageReqVO pageReqVO) {
        PageResult<FeeSharePageRespVO> pageResult = feeShareService.getFeeSharePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出费用归集 Excel")
    @PreAuthorize("@ss.hasPermission('oa:fee-share:export')")
    @OperateLog(type = EXPORT)
    public void exportFeeShareExcel(@Valid FeeSharePageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<FeeSharePageRespVO> list = feeShareService.getFeeSharePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "费用归集.xls", "数据", FeeSharePageRespVO.class,list );
    }


    @GetMapping("/batch-get")
    @Operation(summary ="根据编号批量查询费用归集详细信息")
    @Parameter(name = "codes", description = "申请单编号列表", required = true, example = "")
    @Parameter(name = "type", description = "表单类型", required = true, example = "")
    @Parameter(name = "isPre", description = "是否预归集", required = true, example = "")
    public CommonResult<List<FeeShareRespDTO>> getLoanAppDetail(
            @RequestParam("isPre") Integer isPre,
            @RequestParam("type") Integer type,
            @RequestParam("codes") List<String> codes) {
        return  success(feeShareService.batchGetDetails(isPre,type,codes));
    }


}