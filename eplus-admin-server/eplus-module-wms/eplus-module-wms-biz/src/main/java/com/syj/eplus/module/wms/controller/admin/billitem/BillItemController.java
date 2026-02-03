package com.syj.eplus.module.wms.controller.admin.billitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.service.bill.BillItemService;
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

@Tag(name = "仓储管理-入(出)库单-明细")
@RestController
@RequestMapping("/wms/bill-item")
@Validated
public class BillItemController {

    @Resource
    private BillItemService billItemService;

    @PostMapping("/create")
    @Operation(summary = "创建入(出)库单-明细")
    @PreAuthorize("@ss.hasPermission('wms:bill-item:create')")
    public CommonResult<Long> createBillItem(@Valid @RequestBody BillItemSaveReqVO createReqVO) {
        return success(billItemService.createBillItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新入(出)库单-明细")
    @PreAuthorize("@ss.hasPermission('wms:bill-item:update')")
    public CommonResult<Boolean> updateBillItem(@Valid @RequestBody BillItemSaveReqVO updateReqVO) {
        billItemService.updateBillItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除入(出)库单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:bill-item:delete')")
    public CommonResult<Boolean> deleteBillItem(@RequestParam("id") Long id) {
        billItemService.deleteBillItem(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得入(出)库单-明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:bill-item:query')")
    public CommonResult<BillItemRespVO> getBillItem(@RequestParam("id") Long id) {
        BillItemRespVO billItem = billItemService.getBillItem(id);
        return success(billItem);
    }

    @GetMapping("/page")
    @Operation(summary = "获得入(出)库单-明细分页")
    @PreAuthorize("@ss.hasPermission('wms:bill-item:query')")
    public CommonResult<PageResult<BillItemRespVO>> getBillItemPage(@Valid BillItemPageReqVO pageReqVO) {
        PageResult<BillItemDO> pageResult = billItemService.getBillItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BillItemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出入(出)库单-明细 Excel")
    @PreAuthorize("@ss.hasPermission('wms:bill-item:export')")
    @OperateLog(type = EXPORT)
    public void exportBillItemExcel(@Valid BillItemPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BillItemDO> list = billItemService.getBillItemPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "入(出)库单-明细.xls", "数据", BillItemRespVO.class,
                BeanUtils.toBean(list, BillItemRespVO.class));
    }


}
