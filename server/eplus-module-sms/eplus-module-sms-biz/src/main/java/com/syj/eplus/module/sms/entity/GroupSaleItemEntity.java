package com.syj.eplus.module.sms.entity;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

@Data
public class GroupSaleItemEntity {
    /**
     * 销售数量
     */
    private Integer quantity;
    /**
     * 待采购数量
     */
    private Integer needPurQuantity;

    /**
     * 真实采购数量
     */
    @ChangeIgnore
    private Integer realPurchaseQuantity;

    /**
     * 已出运数
     */
    @ChangeIgnore
    private Integer shippedQuantity;

    /**
     * 已转出运数
     */
    @ChangeIgnore
    private Integer transferShippedQuantity;

    private Integer shipmentTotalQuantity;

    /**
     * 真实采购单价
     */
    @ChangeIgnore
    private JsonAmount realPurchaseWithTaxPrice;
}
