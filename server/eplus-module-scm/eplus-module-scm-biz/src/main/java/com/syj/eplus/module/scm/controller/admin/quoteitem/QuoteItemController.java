package com.syj.eplus.module.scm.controller.admin.quoteitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemRespVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;
import com.syj.eplus.module.scm.service.quoteitem.QuoteItemService;
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
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 供应商报价明细")
@RestController
@RequestMapping("/scm/quote-item")
@Validated
public class QuoteItemController {

    @Resource
    private QuoteItemService quoteItemService;

    @PostMapping("/create")
    @Operation(summary = "创建供应商报价明细")
//    @PreAuthorize("@ss.hasPermission('scm:quote-item:create')")
    public CommonResult<Long> createQuoteItem(@Valid @RequestBody QuoteItemSaveReqVO createReqVO) {
        return success(quoteItemService.createQuoteItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商报价明细")
    @PreAuthorize("@ss.hasPermission('scm:quote-item:update')")
    public CommonResult<Boolean> updateQuoteItem(@Valid @RequestBody QuoteItemSaveReqVO updateReqVO) {
        quoteItemService.updateQuoteItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商报价明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:quote-item:delete')")
    public CommonResult<Boolean> deleteQuoteItem(@RequestParam("id") Long id) {
        quoteItemService.deleteQuoteItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得供应商报价明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:quote-item:query')")
    public CommonResult<QuoteItemRespVO> getQuoteItem(@RequestParam("id") Long id) {
        QuoteItemRespVO quoteItem = quoteItemService.getQuoteItem(id);
        return success(quoteItem);
    }

    @GetMapping("/list-by-skuid")
    @Operation(summary = "根据sku查询报价详情")
    @Parameter(name = "skuIdList", description = "skuIdList", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('scm:quote-item:query')")
    public CommonResult<List<QuoteItemRespVO>> getQuoteItemListBySkuId(@RequestParam("skuIdList") List<Long> skuIdList) {
        List<QuoteitemDTO> quoteItemDTOListBySkuIdList = quoteItemService.getQuoteItemDTOListBySkuIdList(skuIdList);
        if(CollUtil.isEmpty(quoteItemDTOListBySkuIdList)){
            return  success(null);
        }
       List<QuoteItemRespVO> result = new ArrayList<>();
        quoteItemDTOListBySkuIdList.forEach(item-> {
            QuoteItemRespVO quoteItemRespVO = BeanUtils.toBean(item, QuoteItemRespVO.class);
            result.add(quoteItemRespVO);
        });
        return success( result);
    }


    @GetMapping("/list-by-skucode")
    @Operation(summary = "根据sku查询报价详情")
    @Parameter(name = "skuCodeList", description = "skuCodeList", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('scm:quote-item:query')")
    public CommonResult<List<QuoteItemRespVO>> getQuoteItemListBySkuCode(@RequestParam("skuCodeList") List<String> skuCodeList) {
        List<QuoteitemDTO> quoteitemDTOList = quoteItemService.getQuoteItemDTOListBySkuCodeList(skuCodeList);
        if(CollUtil.isEmpty(quoteitemDTOList)){
            return  success(null);
        }
        List<QuoteItemRespVO> result = new ArrayList<>();
        quoteitemDTOList.forEach(item-> {
            QuoteItemRespVO quoteItemRespVO = BeanUtils.toBean(item, QuoteItemRespVO.class);
            result.add(quoteItemRespVO);
        });
        return success( result);
    }

    @GetMapping("/page")
    @Operation(summary = "获得供应商报价明细分页")
    @PreAuthorize("@ss.hasPermission('scm:quote-item:query')")
    public CommonResult<PageResult<QuoteItemRespVO>> getQuoteItemPage(@Valid QuoteItemPageReqVO pageReqVO) {
        PageResult<QuoteItemRespVO> pageResult = quoteItemService.getQuoteItemPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商报价明细 Excel")
    @PreAuthorize("@ss.hasPermission('scm:quote-item:export')")
    @OperateLog(type = EXPORT)
    public void exportQuoteItemExcel(@Valid QuoteItemPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<QuoteItemRespVO> list = quoteItemService.getQuoteItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "供应商报价明细.xls", "数据", QuoteItemRespVO.class,list );
    }

    @GetMapping("/get-same-vender-quote-list")
    @Operation(summary = "根据产品编号列表获取相同供应商报价")
    public CommonResult<List<QuoteitemDTO>> getSameVenderQuoteList(@RequestParam("skuCodeList")List<String> skuCodeList) {
        List<QuoteitemDTO> sameVenderQuoteList = quoteItemService.getSameVenderQuoteList(skuCodeList);
        return success(sameVenderQuoteList);
    }

}