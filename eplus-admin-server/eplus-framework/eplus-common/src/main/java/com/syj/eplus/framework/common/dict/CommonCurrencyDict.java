package com.syj.eplus.framework.common.dict;

import java.util.List;

/**
 * @Description：项目中使用到的币种 "CZK","CNY","HKD","JPY","GBY","EUR","USD","RMB"
 * @Author：du
 * @Date：2024/6/18 18:39
 */
public class CommonCurrencyDict {
    /**
     * CZK
     */
    public static final String CZK = "CZK";

    /**
     *CNY
     */
    public static final String CNY = "CNY";

    /**
     *HKD
     */
    public static final String HKD = "HKD";


    /**
     *JPY
     */
    public static final String JPY = "JPY";


    /**
     *GBP
     */
    public static final String GBP = "GBP";


    /**
     *EUR
     */
    public static final String EUR = "EUR";


    /**
     *USD
     */
    public static final String USD = "USD";


    /**
     *RMB
     */
    public static final String RMB = "RMB";

    /**
     * RUB
     */
    public static final String RUB = "RUB";

    /**
     * PLN
     */
    public static final String PLN = "PLN";

    public static List<String> getDictList(){
        return List.of(CZK,CNY,HKD,JPY,GBP,EUR,USD,RMB,RUB,PLN);
    }

}
