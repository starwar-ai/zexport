package com.syj.eplus.module.home.controller.admin.invoiceholder;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderPageReqVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderRespVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderSaveReqVO;
import com.syj.eplus.module.home.service.invoiceholder.InvoiceHolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 发票夹")
@RestController
@RequestMapping("/home/invoice-holder")
@Validated
public class InvoiceHolderController {

    @Resource
    private InvoiceHolderService invoiceHolderService;

    @PostMapping("/create")
    @Operation(summary = "创建发票夹")
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:create')")
    public CommonResult<Long> createInvoiceHolder(@Valid @RequestBody InvoiceHolderSaveReqVO createReqVO) {
        return success(invoiceHolderService.createInvoiceHolder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新发票夹")
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:update')")
    public CommonResult<Boolean> updateInvoiceHolder(@Valid @RequestBody InvoiceHolderSaveReqVO updateReqVO) {
        invoiceHolderService.updateInvoiceHolder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除发票夹")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:delete')")
    public CommonResult<Boolean> deleteInvoiceHolder(@RequestParam("id") Long id) {
        invoiceHolderService.deleteInvoiceHolder(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得发票夹")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:query')")
    public CommonResult<InvoiceHolderRespVO> getInvoiceHolder(@RequestParam("id") Long id) {
        InvoiceHolderRespVO invoiceHolder = invoiceHolderService.getInvoiceHolder(id);
        return success(invoiceHolder);
    }

    @GetMapping("/page")
    @Operation(summary = "获得发票夹分页")
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:query')")
    public CommonResult<PageResult<InvoiceHolderRespVO>> getInvoiceHolderPage(@Valid InvoiceHolderPageReqVO pageReqVO) {
        PageResult<InvoiceHolderRespVO> pageResult = invoiceHolderService.getInvoiceHolderPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出发票夹 Excel")
//    @PreAuthorize("@ss.hasPermission('home:invoice-holder:export')")
    @OperateLog(type = EXPORT)
    public void exportInvoiceHolderExcel(@Valid InvoiceHolderPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<InvoiceHolderRespVO> list = invoiceHolderService.getInvoiceHolderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "发票夹.xls", "数据", InvoiceHolderRespVO.class,list);
    }


}