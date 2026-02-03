package com.syj.eplus.framework.common.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.syj.eplus.framework.common.dict.CalculationDict;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Description：numberutil 拓展类
 * @Author：du
 * @Date：2024/6/20 10:13
 */
public class NumUtil extends NumberUtil {
    /**
     * 比较两个BigDecimal类型是否相等
     * @param val1 值1
     * @param val2 值2
     * @return 比较结果
     */
    public static boolean bigDecimalsEqual(BigDecimal val1, BigDecimal val2) {
        if (Objects.isNull(val1) || Objects.isNull(val2)){
            return false;
        }
        return val1.compareTo(val2) == CalculationDict.ZERO;
    }

    /**
     * int转BigDecimal (包含null值处理)
     * @param var Integer值
     * @return BigDecimal值
     */
    public static BigDecimal parseToBigDecimal(Integer var){
        if (Objects.isNull(var)){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(var);
    }

    /**
     * 补充数据到指定的位数
     *
     * @param code 字符串
     * @param num  需要补充到的位数
     * @return 补充后的字符串
     */
    public static String padCode(String code, int num) {
        if (StrUtil.isEmpty(code)) {
            return CommonDict.HYPHEN;
        }
        if (num <= 0) {
            return code;
        }
        return StrUtil.padPre(code, num, '0');
    }

}
