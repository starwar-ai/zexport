package com.syj.eplus.module.wms.controller.admin.bill;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillPageReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillRespVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.service.bill.BillService;
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
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "仓储管理-入(出)库单")
@RestController
@RequestMapping("/wms/bill")
@Validated
public class BillController {

    @Resource
    private BillService billService;

    @PostMapping("/create")
    @Operation(summary = "创建入(出)库单")
    @PreAuthorize("@ss.hasPermission('wms:bill:create','wms:bill-out:create')")
    public CommonResult<CreatedResponse> createBill(@Valid @RequestBody BillSaveReqVO createReqVO) {
        return success(billService.createBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新入(出)库单")
    @PreAuthorize("@ss.hasPermission('wms:bill:update','wms:bill-out:update')")
    public CommonResult<Boolean> updateBill(@Valid @RequestBody BillSaveReqVO updateReqVO) {
        boolean flag = billService.updateBill(updateReqVO);
        return success(flag);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除入(出)库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:bill:delete','wms:bill-out:delete')")
    public CommonResult<Boolean> deleteBill(@RequestParam("id") Long id) {
        billService.deleteBill(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得入(出)库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:bill:query','wms:bill-out:query')")
    public CommonResult<BillRespVO> getBill(@RequestParam("id") Long id) {
        BillRespVO bill = billService.getBill(id);
        return success(bill);
    }

    @GetMapping("/page")
    @Operation(summary = "获得入(出)库单分页")
    @PreAuthorize("@ss.hasPermission('wms:bill:query','wms:bill-out:query')")
    public CommonResult<PageResult<BillRespVO>> getBillPage(@Valid BillPageReqVO pageReqVO) {
        return success(billService.getBillPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出入(出)库单 Excel")
    @PreAuthorize("@ss.hasPermission('wms:bill:export','wms:bill-out:export')")
    @OperateLog(type = EXPORT)
    public void exportBillExcel(@Valid BillPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BillRespVO> list = billService.getBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "入(出)库单.xls", "数据", BillRespVO.class, list);
    }


    // ==================== 子表（入(出)库单-明细） ====================

    @GetMapping("/bill-item/list-by-source-id")
    @Operation(summary = "获得入(出)库单-明细列表")
    @Parameter(name = "sourceId", description = "来源单主键")
    @PreAuthorize("@ss.hasPermission('wms:bill:query','wms:bill-out:query')")
    public CommonResult<List<BillItemDO>> getBillItemListBySourceId(@RequestParam("sourceId") Long sourceId) {
        return success(billService.getBillItemListBySourceId(Collections.singletonList(sourceId)));
    }

    @GetMapping("/cancel")
    @Operation(summary = "反提交入库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:bill:cancel','wms:bill-out:cancel')")
    public CommonResult<Boolean> cancel(@RequestParam("id") Long id) {
        return success(billService.cancel(id));
    }

    @GetMapping("/getRecord")
    @Operation(summary = "获得出入库记录")
    @Parameter(name = "batchCode", description = "批次号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:stock:query')")
    public CommonResult<List<BillItemRespVO>> getRecord(@RequestParam("batchCode") String batchCode) {
        List<BillItemRespVO> billItemRespVOList = billService.getRecord(batchCode);
        return success(billItemRespVOList);
    }

    @PutMapping("/gen-contract")
    @Operation(summary = "生成内部合同",description = "生成内部合同")
    @PreAuthorize("@ss.hasPermission('wms:stock:gen-contract')")
    public CommonResult<Boolean> genContract(@RequestParam Long billId) {
        billService.genInternalContract(billId);
        return success(true);
    }

}
