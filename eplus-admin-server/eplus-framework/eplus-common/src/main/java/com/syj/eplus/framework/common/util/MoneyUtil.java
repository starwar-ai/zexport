package com.syj.eplus.framework.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {

    private static final String[] CHINESE_NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] UNITS = {"", "拾", "佰", "仟"};
    private static final String[] BIG_UNITS = {"", "万", "亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载", "极"}; // 根据需要可以增加更多大单位

    public static String convertToChinese(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("价格不能为空或小于0");
        }

        // 转换为字符串并截取小数点前后部分
        String priceStr = price.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String integerPart = priceStr.split("\\.")[0]; // 整数部分
        String decimalPart = priceStr.split("\\.")[1]; // 小数部分（角、分）

        // 处理整数部分
        String integerChinese = convertToChinesePart(integerPart, UNITS, BIG_UNITS);

        // 处理小数部分
        String decimalChinese = "";
        if (!"00".equals(decimalPart)) {
            decimalChinese = convertToDecimalChinese(decimalPart);
        }

        // 合并结果
        return integerChinese + "元" + decimalChinese;
    }

    private static String convertToChinesePart(String numberStr, String[] units, String[] bigUnits) {
        if ("0".equals(numberStr)) {
            return CHINESE_NUMBERS[0];
        }

        StringBuilder sb = new StringBuilder();
        int unitIndex = 0;
//        int zeroCount = 0;

        for (int i = numberStr.length() - 1; i >= 0; i--) {
            int digit = numberStr.charAt(i) - '0';
            String chineseDigit = CHINESE_NUMBERS[digit];

            if (digit == 0) {
//                zeroCount++;
                // 连续零的处理：只在非零数字后面、单位变化处或字符串开始处添加一个零
                if (sb.length() > 0 && (sb.charAt(sb.length() - 1) != CHINESE_NUMBERS[0].charAt(0) || unitIndex == 0 || i == 0)) {
                    sb.insert(0, chineseDigit);
                }
            } else {
                // 添加非零数字和对应单位
                sb.insert(0, chineseDigit + units[unitIndex]);
//                zeroCount = 0; // 重置连续零的计数
            }

            // 切换到下一个单位
            if (++unitIndex == units.length) {
                unitIndex = 0; // 循环使用单位数组
                if (sb.length() > 0 && i > 0) {
                    sb.insert(0, bigUnits[(numberStr.length()-i) / units.length]);
                }
            }
        }

        // 去除末尾可能多余的零
        while (sb.length() > 0 && sb.charAt(0) == CHINESE_NUMBERS[0].charAt(0)) {
            sb.deleteCharAt(0);
        }

        return sb.toString();
    }

    private static String convertToDecimalChinese(String decimalPart) {
        StringBuilder sb = new StringBuilder();
        if (decimalPart.charAt(0) != '0') {
            sb.append(CHINESE_NUMBERS[decimalPart.charAt(0) - '0']).append("角");
        }
        if (decimalPart.length() > 1 && decimalPart.charAt(1) != '0') {
            sb.append(CHINESE_NUMBERS[decimalPart.charAt(1) - '0']).append("分");
        }
        return sb.toString();
    }
}
