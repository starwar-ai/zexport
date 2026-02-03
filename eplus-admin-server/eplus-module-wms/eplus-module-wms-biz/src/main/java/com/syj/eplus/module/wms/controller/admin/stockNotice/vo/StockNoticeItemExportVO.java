
package com.syj.eplus.module.wms.controller.admin.stockNotice.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class StockNoticeItemExportVO {
    @ExcelProperty(value = "客户PO号")
    private String custPo;

    @ExcelProperty(value = "合同号")
    private String purchaseContractCode;

    @ExcelProperty(value = "工厂名称")
    private String venderName;

    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    @ExcelProperty(value = "中文品名")
    private String skuName;

    @ExcelProperty(value = "数量")
    private String orderQuantity;

    @ExcelProperty(value ="外箱装量")
    private String qtyPerOuterbox;

    @ExcelProperty(value ="箱数")
    private Integer orderBoxQuantity;

    private JsonWeight outerboxGrossweight;
    @ExcelProperty(value ="单箱毛重")
    private BigDecimal outerboxGrossweightweight;

    private JsonWeight totalWeight;
    @ExcelProperty(value ="总毛重")
    private BigDecimal totalWeightWeight;

    @ExcelProperty(value ="长")
    private BigDecimal outerboxLength;

    @ExcelProperty(value ="宽")
    private BigDecimal outerboxWidth;

    @ExcelProperty(value ="高")
    private BigDecimal outerboxHeight;

    @ExcelProperty(value ="体积")
    private BigDecimal totalVolume;

    private UserDept manager;

    @ExcelProperty(value ="跟单员")
    private String managerName;

}