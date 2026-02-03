package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {
    TRAVEL_APP(1, "出差报销支付"),

    GENERAL_APP(2, "一般费用报销支付"),

    ENTERTAIN_APP(3, "招待费报销支付"),

    PAYMENT_APP(4, "公对公支付"),

    LOAN_APP(5, "个人借款"),
    CLAIM(6, "回款认领"),
    PURCHASE_PAYMENT(7,"采购付款"),
    OTHER_APP(8,"其他报销");

    private final Integer code;

    private final String name;
}
