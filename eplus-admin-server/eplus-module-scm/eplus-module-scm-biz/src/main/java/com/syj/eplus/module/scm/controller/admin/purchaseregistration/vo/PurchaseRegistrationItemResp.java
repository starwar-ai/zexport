package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/02/17:26
 * @Description:
 */
@Data
@ExcelIgnoreUnannotated
public class PurchaseRegistrationItemResp extends PurchaseRegistrationRespVO {
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 本次登票数
     */
    private BigDecimal invoicThisQuantity;
    /**
     * 出运数量
     */
    private BigDecimal shippingQuantity;
    /**
     * 海关计量单位
     */
    private String hsMeasureUnit;
    /**
     * 报关数量
     */
    @ExcelProperty("出运数量")
    private Integer declarationQuantity;
    /**
     * 开票单价
     */
    private BigDecimal invoicPrice;
    /**
     * 报关品名
     */
    private String declarationName;
    /**
     * 税票编号
     */
    private String invoiceCode;
    /**
     * 采购数量
     */
    private Integer purchaseTotalQuantity;
    /**
     * 采购含税单价
     */
    private JsonAmount purchaseWithTaxPrice;
    /**
     * 采购币种
     */
    private String purchaseCurrency;
    /**
     * 采购金额
     */
    private JsonAmount purchaseTotalAmount;
    /**
     * 采购合同号
     */
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;
    /**
     * 跟单员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;
    /**
     * 销售合同号
     */
    private String saleContractCode;
    /**
     * 中文品名
     */
    private String skuName;
    /**
     * 产品编号
     */
    @ExcelProperty("产品编号")
    private String skuCode;
    /**
     * HS编码
     */
    private String hsCode;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 采购税率
     */
    private BigDecimal purchaseTaxRate;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;


    /**
     * 通知开票数量
     */
    private BigDecimal invoicNoticesQuantity;

    /**
     * 出运发票号
     */
    @ExcelProperty("出运发票号")
    private String shipInvoiceCode;

    /**
     * 产品名称
     */
    @ExcelProperty("产品名称")
    private String invoicSkuName;

    /**
     * 本次开票金额
     */
    @ExcelProperty("本次开票金额")
    private BigDecimal invoicThisPrice;

}
