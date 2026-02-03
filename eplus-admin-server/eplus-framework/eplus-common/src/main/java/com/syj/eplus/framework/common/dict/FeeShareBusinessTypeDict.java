package com.syj.eplus.framework.common.dict;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/21/19:59
 * @Description:
 */
@Data
public class FeeShareBusinessTypeDict {
    // 出差报销支付
    public static final int TRAVEL_APP = 1;
    // 一半费用报销支付
    public static final int GENERAL_APP = 2;
    // 招待费报销支付
    public static final int ENTERTAIN_APP = 3;
    //公对公支付
    public static final int PAYMENT_APP = 4;
    // 寄件
    public static final int SEND_APP = 5;
    // 船代费用
    public static final int FORWARDER_FEE = 6;
    // 其他费用报销
    public static final int OTHER_APP = 11;
}
