package com.syj.eplus.module.wms.controller.admin.adjustment.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-盘库调整单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AdjustmentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21313")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "调整单单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("调整单单号")
    private String code;

    @Schema(description = "盘点单主键", example = "28160")
    @ExcelProperty("盘点单主键")
    private Long stocktakeId;

    @Schema(description = "盘点单单号", example = "28160")
    private String stocktakeCode;

    @Schema(description = "盘点人主键", example = "28160")
    private Long stocktakeUserId;

    @Schema(description = "盘点人姓名", example = "28160")
    private String stocktakeUserName;

    @Schema(description = "调整类型  1-盘盈单 2-盘亏单", example = "1")
    @ExcelProperty("调整类型  1-盘盈单 2-盘亏单")
    private Integer adjustmentType;

    @Schema(description = "采购合同主键", example = "12549")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "15329")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    @ExcelProperty("销售合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "24225")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "调整日期")
    @ExcelProperty("调整日期")
    private LocalDateTime adjustmentDate;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    @ExcelProperty("打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "20666")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "李四")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "盘库调整单-明细")
    private List<AdjustmentItemRespVO> adjustmentItems;
}
