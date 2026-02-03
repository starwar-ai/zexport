package com.syj.eplus.module.infra.controller.admin.paymentitem;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemPageReqVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemRespVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.paymentitem.PaymentItemDO;
import com.syj.eplus.module.infra.service.paymentitem.PaymentItemService;
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

@Tag(name = "管理后台 - 付款方式")
@RestController
@RequestMapping("/system/payment-item")
@Validated
public class PaymentItemController {

    @Resource
    private PaymentItemService paymentitemService;

    @PostMapping("/create")
    @Operation(summary = "创建付款方式")
    @PreAuthorize("@ss.hasPermission('system:payment-item:create','scm:purchase-contract:create','scm:vender:create')")
    public CommonResult<Long> createPayment(@Valid @RequestBody PaymentItemSaveReqVO createReqVO) {
        return success(paymentitemService.createPayment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新付款方式")
    @PreAuthorize("@ss.hasPermission('system:payment-item:update','scm:purchase-contract:update','scm:vender:update')")
    public CommonResult<Boolean> updatePayment(@Valid @RequestBody PaymentItemSaveReqVO updateReqVO) {
        paymentitemService.updatePayment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除付款方式")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:payment-item:delete')")
    public CommonResult<Boolean> deletePayment(@RequestParam("id") Long id) {
        paymentitemService.deletePayment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得付款方式")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:payment-item:query','scm:purchase-contract:query','scm:vender:query')")
    public CommonResult<PaymentItemRespVO> getPayment(@RequestParam("id") Long id) {
        PaymentItemRespVO payment = paymentitemService.getPayment(id);
        return success(payment);
    }

    @GetMapping("/page")
    @Operation(summary = "获得付款方式分页")
//    @PreAuthorize("@ss.hasPermission('system:payment-item:query')")
    public CommonResult<PageResult<PaymentItemRespVO>> getPaymentPage(@Valid PaymentItemPageReqVO pageReqVO) {
        PageResult<PaymentItemDO> pageResult = paymentitemService.getPaymentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PaymentItemRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得精简付款方式分页")
    public CommonResult<PageResult<PaymentItemRespVO>> getPaymentSimpleList(@Valid PaymentItemPageReqVO pageReqVO) {
        pageReqVO.setPageSize(-1);
        PageResult<PaymentItemDO> pageResult = paymentitemService.getPaymentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PaymentItemRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出付款方式 Excel")
    @PreAuthorize("@ss.hasPermission('system:payment-item:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentExcel(@Valid PaymentItemPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PaymentItemDO> list = paymentitemService.getPaymentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "付款方式.xls", "数据", PaymentItemRespVO.class,
                BeanUtils.toBean(list, PaymentItemRespVO.class));
    }

}