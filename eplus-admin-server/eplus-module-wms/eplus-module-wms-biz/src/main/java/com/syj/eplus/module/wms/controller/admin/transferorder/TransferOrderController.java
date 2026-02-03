package com.syj.eplus.module.wms.controller.admin.transferorder;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderPageReqVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderRespVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.transferorder.TransferOrderDO;
import com.syj.eplus.module.wms.service.transferorder.TransferOrderService;
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

@Tag(name = "管理后台 - 调拨")
@RestController
@RequestMapping("/wms/transfer-order")
@Validated
public class TransferOrderController {

    @Resource
    private TransferOrderService transferOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建调拨")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:create')")
    public CommonResult<Long> createTransferOrder(@Valid @RequestBody TransferOrderSaveReqVO createReqVO) {
        return success(transferOrderService.createTransferOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新调拨")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:update')")
    public CommonResult<Boolean> updateTransferOrder(@Valid @RequestBody TransferOrderSaveReqVO updateReqVO) {
        transferOrderService.updateTransferOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除调拨")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:delete')")
    public CommonResult<Boolean> deleteTransferOrder(@RequestParam("id") Long id) {
        transferOrderService.deleteTransferOrder(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得调拨")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:query')")
    public CommonResult<TransferOrderRespVO> getTransferOrder(@RequestParam("id") Long id) {
        TransferOrderRespVO transferOrder = transferOrderService.getTransferOrder(id);
        return success(transferOrder);
    }

    @GetMapping("/page")
    @Operation(summary = "获得调拨分页")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:query')")
    public CommonResult<PageResult<TransferOrderRespVO>> getTransferOrderPage(@Valid TransferOrderPageReqVO pageReqVO) {
        PageResult<TransferOrderDO> pageResult = transferOrderService.getTransferOrderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TransferOrderRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出调拨 Excel")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:export')")
    @OperateLog(type = EXPORT)
    public void exportTransferOrderExcel(@Valid TransferOrderPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TransferOrderDO> list = transferOrderService.getTransferOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "调拨.xls", "数据", TransferOrderRespVO.class,
                BeanUtils.toBean(list, TransferOrderRespVO.class));
    }

    @PutMapping("/submit")
    @Operation(summary = "提交")
    @PreAuthorize("@ss.hasPermission('wms:transfer-order:submit')")
    public CommonResult<Boolean> submitTransferOrder(@RequestParam("id") Long id) {
        transferOrderService.submitTransferOrder(id);
        return success(true);
    }

}