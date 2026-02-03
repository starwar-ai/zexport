package com.syj.eplus.module.oa.controller.admin.feeshareitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemRespVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import com.syj.eplus.module.oa.service.feeshareitem.FeeShareItemService;
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

@Tag(name = "管理后台 - 费用归集明细")
@RestController
@RequestMapping("/oa/fee-share-item")
@Validated
public class FeeShareItemController {

    @Resource
    private FeeShareItemService feeShareItemService;

    @PostMapping("/create")
    @Operation(summary = "创建费用归集明细")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:create')")
    public CommonResult<Long> createFeeShareItem(@Valid @RequestBody FeeShareItemSaveReqVO createReqVO) {
        return success(feeShareItemService.createFeeShareItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新费用归集明细")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:update')")
    public CommonResult<Boolean> updateFeeShareItem(@Valid @RequestBody FeeShareItemSaveReqVO updateReqVO) {
        feeShareItemService.updateFeeShareItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除费用归集明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:delete')")
    public CommonResult<Boolean> deleteFeeShareItem(@RequestParam("id") Long id) {
        feeShareItemService.deleteFeeShareItem(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得费用归集明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:query')")
    public CommonResult<FeeShareItemRespVO> getFeeShareItem(@RequestParam("id") Long id) {
            FeeShareItemRespVO feeShareItem = feeShareItemService.getFeeShareItem(id);
            return success(feeShareItem);
    }
    @GetMapping("/page")
    @Operation(summary = "获得费用归集明细分页")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:query')")
    public CommonResult<PageResult<FeeShareItemRespVO>> getFeeShareItemPage(@Valid FeeShareItemPageReqVO pageReqVO) {
        PageResult<FeeShareItemDO> pageResult = feeShareItemService.getFeeShareItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FeeShareItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出费用归集明细 Excel")
    @PreAuthorize("@ss.hasPermission('oa:fee-share-item:export')")
    @OperateLog(type = EXPORT)
    public void exportFeeShareItemExcel(@Valid FeeShareItemPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<FeeShareItemDO> list = feeShareItemService.getFeeShareItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "费用归集明细.xls", "数据", FeeShareItemRespVO.class,
                        BeanUtils.toBean(list, FeeShareItemRespVO.class));
    }

}