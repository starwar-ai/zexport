package com.syj.eplus.module.oa.controller.admin.feesharedesc;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescRespVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;
import com.syj.eplus.module.oa.service.feesharedesc.FeeShareDescService;
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


@Tag(name = "管理后台 - 费用归集具体名称")
@RestController
@RequestMapping("/oa/fee-share-desc")
@Validated
public class FeeShareDescController {

    @Resource
    private FeeShareDescService feeShareDescService;



    @DeleteMapping("/delete")
    @Operation(summary = "删除费用归集具体名称")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('oa:fee-share-desc:delete')")
    public CommonResult<Boolean> deleteFeeShareDesc(@RequestParam("id") Long id) {
        feeShareDescService.deleteFeeShareDesc(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得费用归集具体名称")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-desc:query')")
    public CommonResult<FeeShareDescRespVO> getFeeShareDesc(@RequestParam("id") Long id) {
            FeeShareDescRespVO feeShareDesc = feeShareDescService.getFeeShareDesc( id);
            return success(feeShareDesc);
    }
    @GetMapping("/page")
    @Operation(summary = "获得费用归集具体名称分页")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-desc:query')")
    public CommonResult<PageResult<FeeShareDescRespVO>> getFeeShareDescPage(@Valid FeeShareDescPageReqVO pageReqVO) {
        PageResult<FeeShareDescDO> pageResult = feeShareDescService.getFeeShareDescPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FeeShareDescRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出费用归集具体名称 Excel")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-desc:export')")
    @OperateLog(type = EXPORT)
    public void exportFeeShareDescExcel(@Valid FeeShareDescPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<FeeShareDescDO> list = feeShareDescService.getFeeShareDescPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "费用归集具体名称.xls", "数据", FeeShareDescRespVO.class,
                        BeanUtils.toBean(list, FeeShareDescRespVO.class));
    }


    @PostMapping("/update-all")
    @Operation(summary = "重新创建费用归集具体名称")
    public CommonResult<Boolean> updateAllFeeShareDesc(@Valid @RequestBody FeeShareDescSaveReqVO createReqVO) {
        feeShareDescService.updateAllFeeShareDesc(createReqVO);
        return success(true);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得费用归集具体名称精简列表分页")
    public CommonResult<PageResult<FeeShareDescRespVO>> getSimpleFeeShareDescPage(@Valid FeeShareDescPageReqVO pageReqVO) {
        PageResult<FeeShareDescDO> pageResult = feeShareDescService.getFeeShareDescPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FeeShareDescRespVO.class));
    }


}