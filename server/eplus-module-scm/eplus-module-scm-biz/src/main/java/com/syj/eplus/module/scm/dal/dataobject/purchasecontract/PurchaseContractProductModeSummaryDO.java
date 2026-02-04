package com.syj.eplus.module.scm.dal.dataobject.purchasecontract;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 采购合同产品模式汇总数据对象
 * 用于产品维度查询时的统计汇总
 *
 * @author 波波
 */
@Data
@Accessors(chain = true)
public class PurchaseContractProductModeSummaryDO {

    /**
     * 数量合计
     */
    private Long sumQuantity;

    /**
     * 含税金额合计（按币种分组时的金额）
     */
    private BigDecimal sumWithTaxTotal;

    /**
     * 含税总价人民币合计
     */
    private BigDecimal sumTotalPriceRmb;

    /**
     * 币种
     */
    private String currency;
}
