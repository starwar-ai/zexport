package com.syj.eplus.framework.common.util;

import java.math.BigDecimal;

/**
 * @Description：BigDecimal函数类
 * @Author：du
 * @Date：2024/4/25 9:59
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> {
    BigDecimal applyAsBigDecimal(T value);
}