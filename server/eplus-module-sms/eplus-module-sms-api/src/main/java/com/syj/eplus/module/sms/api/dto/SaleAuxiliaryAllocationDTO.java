package com.syj.eplus.module.sms.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

@Data
public class SaleAuxiliaryAllocationDTO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 销售合同主键
     */
    private Long saleContractId;
    /**
     * 销售合同编号
     */
    private String saleContractCode;
    /**
     * 销售合同明细主键
     */
    private Long saleContractItemId;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 产品名称
     */
    private String skuName;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 辅料采购合同主键
     */
    private Long auxiliaryPurchaseContractId;
    /**
     * 辅料采购合同编号
     */
    private String auxiliaryPurchaseContractCode;
    /**
     * 辅料采购合同明细主键
     */
    private Long auxiliaryPurchaseContractItemId;
    /**
     * 辅料产品编号
     */
    private String auxiliarySkuCode;
    /**
     * 辅料产品名称
     */
    private String auxiliarySkuName;
    /**
     * 采购数量
     */
    private String quantity;
    /**
     * 分摊金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount allocationAmount;
}
