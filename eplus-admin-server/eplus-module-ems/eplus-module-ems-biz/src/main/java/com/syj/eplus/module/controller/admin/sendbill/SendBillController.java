package com.syj.eplus.module.controller.admin.sendbill;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.controller.admin.sendbill.vo.*;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;
import com.syj.eplus.module.service.sendbill.SendBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 寄件导入单据")
@RestController
@RequestMapping("/ems/send-bill")
@Validated
public class SendBillController {

    @Resource
    private SendBillService sendBillService;


    @PostMapping("/create")
    @Operation(summary = "创建寄件导入单据")
    @PreAuthorize("@ss.hasPermission('ems:send-bill:create')")
    public CommonResult<Long> createSendBill(@Valid @RequestBody SendBillSaveReqVO createReqVO) {
        return success(sendBillService.createSendBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新寄件导入单据")
    @PreAuthorize("@ss.hasPermission('ems:send-bill:update')")
    public CommonResult<Boolean> updateSendBill(@Valid @RequestBody SendBillSaveReqVO updateReqVO) {
        sendBillService.updateSendBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除寄件导入单据")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ems:send-bill:delete')")
    public CommonResult<Boolean> deleteSendBill(@RequestParam("id") Long id) {
        sendBillService.deleteSendBill(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得寄件导入单据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ems:send-bill:query')")
    public CommonResult<SendBillRespVO> getSendBill(@RequestParam("id") Long id) {
        SendBillRespVO sendBill = sendBillService.getSendBill(id);
        return success(sendBill);
    }
    @GetMapping("/page")
    @Operation(summary = "获得寄件导入单据分页")
    @PreAuthorize("@ss.hasPermission('ems:send-bill:query')")
    public CommonResult<PageResult<SendBillRespVO>> getSendBillPage(@Valid SendBillPageReqVO pageReqVO) {
        PageResult<SendBillDO> pageResult = sendBillService.getSendBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SendBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出寄件导入单据 Excel")
    @PreAuthorize("@ss.hasPermission('ems:send-bill:export')")
    @OperateLog(type = EXPORT)
    public void exportSendBillExcel(@Valid SendBillPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SendBillDO> list = sendBillService.getSendBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "寄件导出单据.xls", "数据", SendBillRespVO.class,
                BeanUtils.toBean(list, SendBillRespVO.class));
    }

    @PostMapping("/import")
    @Operation(summary = "导入execl")
    @Parameters({  @Parameter(name = "file", description = "Excel 文件", required = true)  })
    @PreAuthorize("@ss.hasPermission('ems:send:import')")
    public CommonResult<SendBillRespVO> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        int rowNumb = 1;
        List<SendBillImportVO> list = ExcelUtils.read(file, SendBillImportVO.class, rowNumb);
        SendBillImportRespVO sendBillImportRespVO = new SendBillImportRespVO();
        return success(sendBillService.importExcel(list));
    }

}