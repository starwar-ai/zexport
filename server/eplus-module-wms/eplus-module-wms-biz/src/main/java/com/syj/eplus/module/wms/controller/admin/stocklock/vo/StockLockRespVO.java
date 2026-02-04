package com.syj.eplus.module.wms.controller.admin.stocklock.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 仓储管理-库存明细-锁定库存 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockLockRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27594")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "产品库存主键", example = "8212")
    @ExcelProperty("产品库存主键")
    private Long stockId;

    @Schema(description = "销售合同单号")
    @ExcelProperty("原单据单号")
    private String saleContractCode;

    @Schema(description = "原单据类型  1-销售订单 2-加工单", example = "2")
    @ExcelProperty("原单据类型  1-销售订单 2-加工单")
    private Integer sourceOrderType;

    @Schema(description = "占用类型  1-可用数量 2-未到货库存", example = "1")
    @ExcelProperty("占用类型  1-可用数量 2-未到货库存")
    private Integer lockType;

    @Schema(description = "占用数量")
    @ExcelProperty("占用数量")
    private Integer lockQuantity;

    @Schema(description = "占用时间")
    @ExcelProperty("占用时间")
    private LocalDateTime lockTime;

    @Schema(description = "占用人主键", example = "9891")
    @ExcelProperty("占用人主键")
    private Long lockByUserId;

    @Schema(description = "占用人名称", example = "王五")
    @ExcelProperty("占用人名称")
    private String lockByUserName;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
