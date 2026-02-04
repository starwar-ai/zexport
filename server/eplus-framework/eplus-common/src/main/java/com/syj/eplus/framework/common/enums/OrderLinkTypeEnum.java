package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderLinkTypeEnum {
    SALE_CONTRACT(1, "销售合同"),
    SALES_CONTRACT_CHANGE(2, "销售合同变更"),
    PURCHASE_PLAN(3, "采购计划"),
    PURCHASE_CONTRACT(4, "采购合同"),
    PURCHASE_CONTRACT_CHANGE(5, "采购合同变更"),
    SHIPPING_PLAN(6, "出运计划"),
    SHIPPING_DETAIL(7, "出运明细"),
    SHIPPING_DETAIL_CHANGE(8, "出运明细变更"),
    SUPPLIER_CHANGE(9, "供应商变更"),
    CUSTOMER_CHANGE(10, "客户变更"),
    CABINET_NOTICE(11, "拉柜通知单"),
    CUSTOMS_DECLARATION(12, "报关单"),
    SETTLEMENT(13, "结汇单"),
    INSPECTION_CERTIFICATE(14, "商检单"),
    WAREHOUSING(15, "入库单"),
    INSPECTION(16, "验货单"),
    PAYMENT(17, "付款单"),
    RETURN(18, "退货单"),
    AUXILIARY_PURCHASE_CONTRACT(19, "辅料采购合同"),
    PAYMENT_APPLY_ORDER(20, "付款申请单"),
    INVOICING_NOTICES(21, "开票通知"),
    RECEIPT_ORDER(22, "收款单"),
    STOCK_IN_NOTICE_NAME(23, "入库通知单");

    private Integer value;

    private String name;
}
