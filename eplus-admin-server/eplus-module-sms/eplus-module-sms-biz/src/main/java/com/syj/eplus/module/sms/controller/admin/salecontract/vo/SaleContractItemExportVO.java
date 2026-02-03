package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.excel.core.convert.VolumeConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class SaleContractItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Long id;

    /**
     * 客户货号
     */
    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    /**
     * 产品编号
     */
    @ExcelProperty(value = "产品编号")
    private String skuCode;

    @ExcelProperty(value = "中文品名")
    private String name;
    
    @ExcelProperty(value = "英文品名")
    private String nameEng;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    private Integer unit;

    /**
     * 销售单价
     */
    private JsonAmount unitPrice;

    /**
     * 销售单价
     */
    @ExcelProperty(value = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private BigDecimal unitPriceAmount;


    private JsonAmount totalSaleAmount;

    @ExcelProperty(value = "金额")
    private BigDecimal totalSaleAmountAmount;

    /**
     * 含税单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    @ExcelProperty(value = "采购单价不含包装")
    private BigDecimal purchaseWithTaxPriceAmount;

    /**
     * 含税含包装单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseTotalPrice;

    @ExcelProperty(value = "采购单价含包装")
    private BigDecimal purchaseTotalPriceAmount;

    @ExcelProperty(value = "采购总价")
    private BigDecimal purchaseTotalAmount;

    @ExcelProperty(value = "采购币种")
    private String purchaseCurrency;

    /**
     * 中文说明
     */
    @ExcelProperty(value = "中文说明")
    private String description;

    /**
     * 英文说明
     */
    @ExcelProperty(value = "英文说明")
    private String descriptionEng;

    /**
     * 供应商code
     */
    @ExcelProperty(value = "供应商code")
    private String venderCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    private String venderName;


    /**
     * 订单毛利
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount orderGrossProfit;

    @ExcelProperty(value = "订单毛利")
    private BigDecimal orderGrossProfitAmount;

    /**
     * 毛利率
     */
    @ExcelProperty(value = "毛利率")
    private BigDecimal orderGrossProfitRate;

    /**
     * 工厂交期
     */
    private LocalDateTime venderDeliveryDate;

    @ExcelProperty(value = "工厂交期")
    private String venderDeliveryDateFormat;

    /**
     * 外箱装量
     */
    @ExcelProperty(value = "外箱装量")
    private Integer qtyPerOuterbox;

    /**
     * 内盒装量
     */
    @ExcelProperty(value = "内盒装量")
    private Integer qtyPerInnerbox;

    /**
     * 箱数
     */
    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    /**
     * 外箱长度
     */
    @ExcelProperty(value = "外箱长度")
    private BigDecimal outerboxLength;

    /**
     * 外箱宽度
     */
    @ExcelProperty(value = "外箱宽度")
    private BigDecimal outerboxWidth;

    /**
     * 外箱高度
     */
    @ExcelProperty(value = "外箱高度")
    private BigDecimal outerboxHeight;

    /**
     * 外箱体积
     */
    @ExcelProperty(value = "外箱体积", converter = VolumeConvert.class)
    private BigDecimal outerboxVolume;

    /**
     * 外箱净重
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight outerboxNetweight;

    @ExcelProperty(value = "外箱净重")
    private BigDecimal outerboxNetweightWeight;

    /**
     * 外箱毛重
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight outerboxGrossweight;

    @ExcelProperty(value = "外箱毛重")
    private BigDecimal outerboxGrossweightWeight;

    /**
     * 是否翻单
     */
    @ChangeIgnore
    private Integer reorderFlag;

    /**
     * 是否翻单
     */
    @ExcelProperty(value = "是否翻单")
    private String reorderFlagValue;

    /**
     * 采购员
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept purchaseUser;

    /**
     * 采购员名称
     */
    @ExcelProperty(value = "采购员名称")
    private String purchaseUserName;

    /**
     * 采购员编号
     */
    @ExcelProperty(value = "采购员编号")
    private String purchaseUserCode;

    /**
     * 海关编码
     */
    @ExcelProperty(value = "海关编码")
    private String hsCode;

    /**
     * 包装方式名称
     */
    @ExcelProperty(value = "包装方式名称")
    private String packageTypeName;

    /**
     * 退税率
     */
    @ExcelProperty(value = "退税率")
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;
    /**
     * 退税金额
     */
    @ExcelProperty(value = "退税金额")
    private BigDecimal taxRefundPriceAmount;

    /**
     * 是否负利润
     */
    @ExcelProperty(value = "是否负利润")
    private String negativeProfitValue;


    /**
     * 验货费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount inspectionFee;

    /**
     * 验货费用
     */
    @ExcelProperty(value = "验货费用")
    private BigDecimal inspectionFeeAmount;

    /**
     * 资金占用费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount fundOccupancyFee;
    /**
     * 验货费用
     */
    @ExcelProperty(value = "资金占用费")
    private BigDecimal fundOccupancyFeeAmount;

    /**
     * 佣金比例
     */
    @ExcelProperty(value = "佣金比例")
    private BigDecimal commissionRate;

    /**
     * 佣金金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;

    /**
     * 佣金金额
     */
    @ExcelProperty(value = "佣金金额")
    private BigDecimal commissionAmountAmount;

    /**
     * 拖柜费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount trailerFee;

    /**
     * 拖柜费
     */
    @ExcelProperty(value = "拖柜费")
    private BigDecimal trailerFeeAmount;

    /**
     * 保险费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    /**
     * 保险费
     */
    @ExcelProperty(value = "保险费")
    private BigDecimal insuranceFeeAmount;

    /**
     * 包装价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchasePackagingPrice;

    /**
     * 包装价
     */
    @ExcelProperty(value = "包装价")
    private BigDecimal purchasePackagingPriceAmount;


    /**
     * 平台费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount platformFee;

    /**
     * 平台费
     */
    @ExcelProperty(value = "平台费")
    private BigDecimal platformFeeAmount;

    /**
     * 预估总费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount forecastTotalCost;
    /**
     * 预估总费用
     */
    @ExcelProperty(value = "预估总费用")
    private BigDecimal forecastTotalCostAmount;

    /**
     * 内部核算单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount innerCalcPrice;
    /**
     * 内部核算单价
     */
    @ExcelProperty(value = "内部核算单价")
    private BigDecimal innerCalcPriceAmount;

    /**
     * 中信保费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount sinosureFee;
    /**
     * 中信保费用
     */
    @ExcelProperty(value = "中信保费用")
    private BigDecimal sinosureFeeAmount;

    /**
     * 状态
     */
    @ChangeIgnore
    private Integer status;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String statusName;

    private Integer quantity;

    @ExcelProperty(value = "Vertak Product Image")
    private WriteCellData<Void> cptp;

    @ExcelProperty(value = "基础产品编号")
    private String basicSkuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    @ExcelProperty(value = "条形码")
    private String barcode;

}