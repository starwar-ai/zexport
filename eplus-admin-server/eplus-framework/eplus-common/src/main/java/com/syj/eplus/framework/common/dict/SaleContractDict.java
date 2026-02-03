package com.syj.eplus.framework.common.dict;

import java.util.List;

/**
 * @Description：销售合同部分字典
 * @Author：du
 * @Date：2024/6/18 17:02
 */
public class SaleContractDict {
    /**
     * 平台费比例
     */
    public static final String PLATFORM_FEE_RATE = "platform_fee_rate";

    /**
     *20尺柜费用
     */
    public static final String TWENTY_FOOT_CABINET_FEE = "twenty_foot_cabinet_fee";

    /**
     *40尺柜费用
     */
    public static final String FORTY_FOOT_CABINET_FEE = "forty_foot_cabinet_fee";

    /**
     *40尺高柜费用
     */
    public static final String FORTY_FOOT_CONTAINER_FEE = "forty_foot_container_fee";

    /**
     *散货/M3
     */
    public static final String BULK_HANDLING_FEE = "bulk_handling_fee";

    /**
     *散货起始费用/M3
     */
    public static final String BULK_HANDLING_START_FEE = "bulk_handling_start_fee";

    /**
     * 20尺柜
     */
    public static final String TWENTY_FOOT_CABINET = "twenty_foot_cabinet";

    /**
     * 40尺柜
     */
    public static final String FORTY_FOOT_CABINET = "forty_foot_cabinet";

    /**
     * 40尺高柜
     */
    public static final String FORTY_FOOT_CONTAINER = "forty_foot_container";

    /**
     * 散货
     */
    public static final String BULK_HANDLING = "bulk_handling";

    /**
     * 中信保费用配置
     */
    public static final String CREDIT_INSURANCE_RATE = "sinosure_fee_rate";

    public static List<String> getfeeDictList() {
        return List.of(PLATFORM_FEE_RATE,TWENTY_FOOT_CABINET_FEE,FORTY_FOOT_CABINET_FEE,FORTY_FOOT_CONTAINER_FEE,BULK_HANDLING_FEE,BULK_HANDLING_START_FEE,CREDIT_INSURANCE_RATE);
    }

    public static List<String> getbaseDictList() {
        return List.of(TWENTY_FOOT_CABINET, FORTY_FOOT_CABINET, FORTY_FOOT_CONTAINER, BULK_HANDLING);
    }
}
