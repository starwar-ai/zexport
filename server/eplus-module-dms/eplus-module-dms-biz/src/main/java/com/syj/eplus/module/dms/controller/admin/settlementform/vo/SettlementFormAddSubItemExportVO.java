
package com.syj.eplus.module.dms.controller.admin.settlementform.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class SettlementFormAddSubItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Long id;

    /**
     * 类型
     */
    @Schema(description = "加减项")
    @ExcelProperty(value = "加减项")
    private Integer calculationType;

    /**
     * 费用名称
     */
    @Schema(description = "项目")
    @ExcelProperty(value = "项目")
    private String feeName;

    /**
     * 金额
     */
    @Schema(description = "金额")
    private JsonAmount amount;

    /**
     * 金额
     */
    @Schema(description = "金额")
    @ExcelProperty(value = "金额")
    private BigDecimal amountValue;
}