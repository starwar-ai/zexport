package com.syj.eplus.framework.common.util;
import com.syj.eplus.framework.common.dict.CalculationDict;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @Description：数值格式化工具类
 * @Author：du
 * @Date：2024/12/19 10:00
 */
public class NumberFormatUtil {
    /**
     * 格式化金额，保留三位小数
     * @param amount 金额值
     * @return 格式化后的金额
     */
    public static BigDecimal formatUnitAmount(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(CalculationDict.THREE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 格式化金额，保留两位小数
     * @param amount 金额值
     * @return 格式化后的金额
     */
    public static BigDecimal formatAmount(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 格式化重量，保留两位小数
     * @param weight 重量值
     * @return 格式化后的重量
     */
    public static BigDecimal formatWeight(BigDecimal weight) {
        if (Objects.isNull(weight)) {
            return BigDecimal.ZERO;
        }
        return weight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 格式化汇率，保留两位小数
     * @param rate
     * @return 格式化后的汇率
     */
    public static BigDecimal formatRate(BigDecimal rate) {
        if (Objects.isNull(rate)) {
            return BigDecimal.ZERO;
        }
        return rate.setScale(CalculationDict.FOUR, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 格式化数值，保留指定小数位数
     * @param number 数值
     * @param scale 保留小数位数
     * @return 格式化后的数值
     */
    public static BigDecimal format(BigDecimal number, int scale) {
        if (Objects.isNull(number)) {
            return BigDecimal.ZERO;
        }
        return number.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }
} 