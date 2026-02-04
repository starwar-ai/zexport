package com.syj.eplus.module.infra.api.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 收款计划 DTO
 *
 * @author eplus
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemCollectionPlanDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 收款计划主键
     */
    private Long settlementId;

    /**
     * 步骤
     */
    private Integer step;

    /**
     * 起始点
     */
    private Integer dateType;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 收款比例
     */
    private BigDecimal collectionRatio;

    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;
    
    /**
     * 是否控制出运
     */
    private Integer controlShipmentFlag;

    /**
     * 状态
     */
    private Integer exeStatus;

    /**
     * 支付方式
     */
    private Integer paymentMethod;
}
