package com.syj.eplus.framework.common.util;

import cn.hutool.core.util.NumberUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @Description：体积处理工具类
 * @Author：du
 * @Date：2024/12/19 10:00
 */
public class VolumeUtil {

    /**
     * 将体积从立方毫米转换为立方米并保留三位小数
     * @param volume 体积（立方毫米）
     * @return 转换后的体积（立方米），保留三位小数
     */
    public static BigDecimal convertToCubicMeter(BigDecimal volume) {
        if (Objects.isNull(volume)) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.div(volume, CalculationDict.ONE_MILLION,CalculationDict.THREE, RoundingMode.HALF_UP).stripTrailingZeros();
    }
    
    /**
     * 将体积从立方米转换为立方毫米
     * @param volume 体积（立方米）
     * @return 转换后的体积（立方毫米）
     */
    public static BigDecimal convertToCubicMillimeter(BigDecimal volume) {
        if (Objects.isNull(volume)) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.mul(volume, CalculationDict.ONE_MILLION).stripTrailingZeros();
    }

    /**
     * 格式化体积显示，保留两位小数
     * @param volume 体积值
     * @return 格式化后的体积，保留两位小数
     */
    public static BigDecimal formatVolume(BigDecimal volume) {
        if (Objects.isNull(volume)) {
            return BigDecimal.ZERO;
        }
        return volume.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 格式化体积显示，保留指定小数位数
     * @param volume 体积值
     * @param scale 保留小数位数
     * @return 格式化后的体积
     */
    public static BigDecimal formatVolume(BigDecimal volume, int scale) {
        if (Objects.isNull(volume)) {
            return BigDecimal.ZERO;
        }
        return volume.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 通用体积处理方法：从立方毫米转换为立方米并保留两位小数
     * 适用于OuterboxVolume和TotalVolume的处理
     * @param volume 体积值（立方毫米）
     * @return 转换后的体积（立方米），保留两位小数
     */
    public static BigDecimal processVolume(BigDecimal volume) {
        return convertToCubicMeter(volume);
    }
} 