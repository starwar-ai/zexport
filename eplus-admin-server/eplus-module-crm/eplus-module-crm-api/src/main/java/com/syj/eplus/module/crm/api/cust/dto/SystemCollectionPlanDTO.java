package com.syj.eplus.module.crm.api.cust.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/15 17:24
 */

@Data
public class SystemCollectionPlanDTO extends BaseDO {
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
