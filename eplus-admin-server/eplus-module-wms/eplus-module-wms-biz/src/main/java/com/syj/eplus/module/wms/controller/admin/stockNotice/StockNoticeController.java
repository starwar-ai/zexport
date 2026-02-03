package com.syj.eplus.module.wms.controller.admin.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ShippedAddressEnum;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.enums.NoticeStatusEnum;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.PRINT;

@Tag(name = "仓储管理-入(出)库通知单")
@RestController
@RequestMapping("/wms/stockNotice")
@Validated
public class StockNoticeController {

    @Resource
    private StockNoticeService stockNoticeService;

    @PostMapping("/create")
    @Operation(summary = "创建入(出)库通知单")
    @PreAuthorize("@ss.hasPermission('wms:notice:create','wms:notice-out:create')")
    public CommonResult<CreatedResponse> createNotice(@Valid @RequestBody StockNoticeSaveReqVO createReqVO) {
        createReqVO.setIsContainerTransportation(BooleanEnum.NO.getValue());
        return success(stockNoticeService.createNotice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新入(出)库通知单")
    @PreAuthorize("@ss.hasPermission('wms:notice:update','wms:notice-out:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody StockNoticeSaveReqVO updateReqVO) {
        updateReqVO.setIsContainerTransportation(BooleanEnum.NO.getValue());
        stockNoticeService.updateNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除入(出)库通知单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:notice:delete','wms:notice-out:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        stockNoticeService.deleteNotice(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得入(出)库通知单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:notice:query','dms:container-transportation:query','wms:notice-out:query')")
    public CommonResult<StockNoticeRespVO> getNotice(@RequestParam("id") Long id) {
        StockNoticeRespVO notice = stockNoticeService.getNotice(id,null);
        return success(notice);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得入(出)库通知单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<StockNoticeRespVO> getAuditNoticeDetail(@RequestParam("id") String id) {
        StockNoticeRespVO notice = stockNoticeService.getNotice(null,id);
        return success(notice);
    }

    @GetMapping("/page")
    @Operation(summary = "获得入(出)库通知单分页")
    @PreAuthorize("@ss.hasPermission('wms:notice:query','wms:notice-out:query')")
    public CommonResult<PageResult<StockNoticeRespVO>> getNoticePage(@Valid StockNoticePageReqVO pageReqVO) {
        //入库单列表查询是否拉柜为否
        pageReqVO.setIsContainerTransportation(BooleanEnum.NO.getValue());
        return success(stockNoticeService.getNoticePage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出入(出)库通知单 Excel")
    @PreAuthorize("@ss.hasPermission('wms:notice:export','wms:notice-out:export')")
    @OperateLog(type = EXPORT)
    public void exportNoticeExcel(@Valid StockNoticePageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StockNoticeRespVO> list = stockNoticeService.getNoticePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "入(出)库通知单.xls", "数据", StockNoticeRespVO.class,
                BeanUtils.toBean(list, StockNoticeRespVO.class));
    }


    // ==================== 子表（入(出)库通知单-明细） ====================

    @GetMapping("/notice-item/list-by-notice-id")
    @Operation(summary = "获得入(出)库通知单-明细列表")
    @Parameter(name = "noticeId", description = "入/出库通知单主键")
    @PreAuthorize("@ss.hasPermission('wms:notice:query','wms:notice-out:query')")
    public CommonResult<List<StockNoticeItemDO>> getNoticeItemListByNoticeId(@RequestParam("noticeId") Long noticeId) {
        return success(stockNoticeService.getNoticeItemListByNoticeId(noticeId));
    }

    @GetMapping("/toBill")
    @Operation(summary = "入(出)库通知单转入(出)库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:notice:convert','wms:notice-out:convert','dms:container-transportation:convert')")
    public CommonResult<List<CreatedResponse>> toBill(@RequestParam("id") Long id) {
        return success(stockNoticeService.toBill(id, ShippedAddressEnum.BILL.getValue()));
    }
    @GetMapping("/factory-toBill")
    @Operation(summary = "工厂出库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:notice:convert','wms:notice-out:convert','dms:container-transportation:convert','wms:notice-out:factory','dms:container-transportation-out:factory')")
    public CommonResult<List<CreatedResponse>> toFactoryBill(@RequestParam("id") Long id) {
        return success(stockNoticeService.toBill(id,ShippedAddressEnum.FACTORY.getValue()));
    }
    @GetMapping("/container-transportation-page")
    @Operation(summary = "获得拉柜通知单分页")
    @PreAuthorize("@ss.hasPermission('wms:notice:query','dms:container-transportation:query')")
    public CommonResult<PageResult<StockNoticeRespVO>> getTransportationNoticePage(@Valid StockNoticePageReqVO pageReqVO) {
        pageReqVO.setNotInNoticeStatus(NoticeStatusEnum.CANCEL.getValue());
        return success(stockNoticeService.getTransportationNoticePage(pageReqVO));
    }
    @PutMapping("/container-transportation/close")
    @Operation(summary = "拉柜通知单作废",description = "拉柜通知单作废")
    @PreAuthorize("@ss.hasPermission('dms:container-transportation:close')")
    public CommonResult<Boolean> closeStockNotice(@RequestParam Long id) {
        stockNoticeService.closeStockNotice(id);
        return success(true);
    }

    @GetMapping("/container-transportation-export-excel")
    @Operation(summary = "导出拉柜通知单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })

    @PreAuthorize("@ss.hasPermission('dms:container-transportation:export')")
    public void containerTransportationExportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        stockNoticeService.containerTransportationExportExcel(id, reportCode,response);
    }

    @GetMapping("/print")
    @Operation(summary = "打印")
    @OperateLog(type = PRINT)
    @Parameters({
            @Parameter(name = "id", required = true, description = "id", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "打印类型", example = "1"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('wms:notice:print','wms:notice-out:print')")
    public CommonResult<String> print(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId) {
        String pdfFile = stockNoticeService.print(id, reportCode, reportId, companyId);
        return success(pdfFile);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('wms:notice:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        stockNoticeService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('wms:notice:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        stockNoticeService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('wms:notice:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long paymentId) {
        stockNoticeService.submitTask(paymentId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    @PutMapping("/close")
    @Operation(summary = "出库通知单作废",description = "出库通知单作废")
    @PreAuthorize("@ss.hasPermission('wms:notice:close')")
    public CommonResult<Boolean> closeNotice(@RequestParam Long id) {
        stockNoticeService.closeNotice(id);
        return success(true);
    }
}
