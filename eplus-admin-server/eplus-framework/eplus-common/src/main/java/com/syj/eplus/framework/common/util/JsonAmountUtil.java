package com.syj.eplus.framework.common.util;

import com.syj.eplus.framework.common.entity.JsonAmount;

import java.math.BigDecimal;
import java.util.Objects;

public class JsonAmountUtil {

    /**
     * 乘法运算
     * @param jsonAmount 待乘的量
     * @param value 乘数
     * @return 乘法结果
     */
    public static JsonAmount mul(JsonAmount jsonAmount,Integer value){
        if (Objects.isNull(jsonAmount) || Objects.isNull(value)) {
            return new JsonAmount(BigDecimal.ZERO, "RMB");
        }
        BigDecimal amount = jsonAmount.getAmount();
        String currency = jsonAmount.getCurrency();
        if (Objects.isNull(amount)) {
            return new JsonAmount(BigDecimal.ZERO, currency);
        }
        // 执行乘法运算
        BigDecimal newAmount = amount.multiply(BigDecimal.valueOf(value));
        return new JsonAmount().setAmount(newAmount).setCurrency(currency);
    }
}
