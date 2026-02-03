package com.syj.eplus.module.wms.controller.admin.stockNoticeItem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeItemService;
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

@Tag(name = "仓储管理-入(出)库通知单-明细")
@RestController
@RequestMapping("/wms/stockNotice-item")
@Validated
public class StockNoticeItemController {

    @Resource
    private StockNoticeItemService stockNoticeItemService;

    @PostMapping("/create")
    @Operation(summary = "创建入(出)库通知单-明细")
    @PreAuthorize("@ss.hasPermission('wms:notice-item:create')")
    public CommonResult<Long> createNoticeItem(@Valid @RequestBody StockNoticeItemSaveReqVO createReqVO) {
        return success(stockNoticeItemService.createNoticeItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新入(出)库通知单-明细")
    @PreAuthorize("@ss.hasPermission('wms:notice-item:update')")
    public CommonResult<Boolean> updateNoticeItem(@Valid @RequestBody StockNoticeItemSaveReqVO updateReqVO) {
        stockNoticeItemService.updateNoticeItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除入(出)库通知单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:notice-item:delete')")
    public CommonResult<Boolean> deleteNoticeItem(@RequestParam("id") Long id) {
        stockNoticeItemService.deleteNoticeItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得入(出)库通知单-明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:notice-item:query')")
    public CommonResult<StockNoticeItemRespVO> getNoticeItem(@RequestParam("id") Long id) {
        StockNoticeItemRespVO noticeItem = stockNoticeItemService.getNoticeItem(id);
        return success(noticeItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得入(出)库通知单-明细分页")
    @PreAuthorize("@ss.hasPermission('wms:notice-item:query')")
    public CommonResult<PageResult<StockNoticeItemRespVO>> getNoticeItemPage(@Valid StockNoticeItemPageReqVO pageReqVO) {
        PageResult<StockNoticeItemDO> pageResult = stockNoticeItemService.getNoticeItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StockNoticeItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出入(出)库通知单-明细 Excel")
    @PreAuthorize("@ss.hasPermission('wms:notice-item:export')")
    @OperateLog(type = EXPORT)
    public void exportNoticeItemExcel(@Valid StockNoticeItemPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StockNoticeItemDO> list = stockNoticeItemService.getNoticeItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "入(出)库通知单-明细.xls", "数据", StockNoticeItemRespVO.class,
                BeanUtils.toBean(list, StockNoticeItemRespVO.class));
    }


}
