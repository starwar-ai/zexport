package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum FeeShareSourceTypeEnum {
    TRAVEL_REIMBURSE(1, "差旅费报销"),
    GENERAL_REIMBURSE(2, "一般费用报销"),
    ENTERTAIN_REIMBURSE(3, "招待费报销"),
    PAYMENT_APP(4, "公对公支付"),
    EMS_SEND(5, "寄件"),
    SHIPMENT_FORWARDER_FEE(6, "出运-船代费用"),
    AUXILIARY_PURCHASE(7, "包材采购合同"),
    ENTERTAIN_APPLY(8, "招待费申请"),
    TRAVEL_APPLY(9, "差旅费申请"),
    GENERAL_APPLY(10, "费用申请"),
    OTHER_REIMBURSE(11, "其他费用报销");


    private Integer value;

    private String name;
}
