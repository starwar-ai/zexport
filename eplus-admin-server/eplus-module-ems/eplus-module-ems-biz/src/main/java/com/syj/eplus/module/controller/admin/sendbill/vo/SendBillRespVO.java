package com.syj.eplus.module.controller.admin.sendbill.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 寄件导入单据 Response VO")
@Data

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false)
public class SendBillRespVO{

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "可以覆盖导入的数据")
    private List<SendBillImportVO> overList;
    @Schema(description = "可以成功首次导入的数据")
    private List<SendBillImportVO> successList;

    @Schema(description = "不能处理的数据")
    private List<SendBillImportVO> finishList;

    @Schema(description = "数据异常不能导入的数据")
    private List<SendBillImportVO> errorList;
}