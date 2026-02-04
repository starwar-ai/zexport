package com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.*;

/**
 * 采购合同辅料分摊 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_purchase_auxiliary_allocation",autoResultMap = true)
@KeySequence("scm_purchase_auxiliary_allocation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAuxiliaryAllocationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购合同主键
     */
    private Long purchaseContractId;
    /**
     * 采购合同编号
     */
    private String purchaseContractCode;
    /**
     * 采购合同明细主键
     */
    private Long purchaseContractItemId;
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
    private Integer quantity;
    /**
     * 分摊金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount allocationAmount;

}