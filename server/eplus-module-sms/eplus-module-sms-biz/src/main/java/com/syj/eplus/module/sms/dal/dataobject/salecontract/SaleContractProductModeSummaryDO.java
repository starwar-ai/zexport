package com.syj.eplus.module.sms.dal.dataobject.salecontract;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 产品模式汇总数据对象
 * 用于产品视图的汇总统计，基于筛选后的明细计算
 *
 * @author 波波
 */
@Data
@Accessors(chain = true)
public class SaleContractProductModeSummaryDO {

    /**
     * 数量合计
     */
    private Long sumQuantity;

    /**
     * 箱数合计
     */
    private Long sumBoxCount;

    /**
     * 体积合计
     */
    private BigDecimal sumVolume;

    /**
     * 外销总金额USD合计
     */
    private BigDecimal sumTotalSaleAmountUsd;

    /**
     * 采购数量合计
     */
    private Long sumRealPurchaseQuantity;

    /**
     * 锁定数量合计
     */
    private Long sumRealLockQuantity;

    /**
     * 出运数量合计
     */
    private Long sumShippedQuantity;

    /**
     * 转出运数量合计
     */
    private Long sumTransferShippedQuantity;

    /**
     * 上游变更数量（用于统计待确认的变更单数量）
     */
    private Long changeCount;
}
