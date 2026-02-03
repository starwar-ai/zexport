package com.syj.eplus.framework.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;

import java.math.BigDecimal;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.enums.CommonErrorCodeConstants.RATE_NOT_EXISTS;

/**
 * 货币转换工具类
 *
 * @author 波波
 */
public class CurrencyUtil {

    /**
     * 根据币种汇率转换币种金额
     *
     * @param amount   原金额
     * @param currency 目标币种
     * @return 转换后金额
     */
    public static JsonAmount changeCurrency(JsonAmount amount, String currency, Map<String, BigDecimal> dailyRateMap) {
        if (StrUtil.isEmpty(currency) || amount == null) {
            return amount;
        }
        if (CollUtil.isEmpty(dailyRateMap)) {
            throw exception(RATE_NOT_EXISTS);
        }
        String baseCurrency = amount.getCurrency();
        BigDecimal baseAmount = amount.getAmount();
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
            return amount;
        }
        if (currency.equals(baseCurrency)) {
            return amount;
        }
        BigDecimal baseRate = dailyRateMap.get(baseCurrency);
        BigDecimal rate = dailyRateMap.get(currency);
        BigDecimal resultAmount = NumberUtil.div(NumberUtil.mul(baseAmount, baseRate), rate);
        return new JsonAmount().setAmount(resultAmount).setCurrency(currency);
    }
} 