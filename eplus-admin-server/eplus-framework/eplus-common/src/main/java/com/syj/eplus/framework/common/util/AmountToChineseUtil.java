package com.syj.eplus.framework.common.util;

import java.math.BigDecimal;

public class AmountToChineseUtil {

  // 定义数字对应的中文大写字符
  private static final String[] CN_NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
  private static final String[] CN_UNITS = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};
  private static final String CN_INTEGER = "元";
  private static final String CN_ZERO = "零";
  private static final String CN_TEN = "拾";
  private static final String CN_WHOLE = "整";
  private static final String CN_JIAO = "角";
  private static final String CN_FEN = "分";

  /**
   * 将 BigDecimal 金额转换为中文大写金额
   *
   * @param amount 输入的金额（BigDecimal 类型）
   * @return 中文大写金额字符串
   */
  public static String toChineseUpper(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("金额不能为负数或空！");
    }

    // 分离整数部分和小数部分
    long integerPart = amount.longValue(); // 整数部分
    int decimalPart = amount.subtract(new BigDecimal(integerPart)).multiply(new BigDecimal(100)).intValue(); // 小数部分（角、分）

    // 转换整数部分为中文大写
    String integerStr = integerPart == 0 ? CN_ZERO : convertIntegerPart(integerPart);

    // 转换小数部分为中文大写
    String decimalStr = convertDecimalPart(decimalPart);

    // 拼接结果
    StringBuilder result = new StringBuilder();
    result.append(integerStr).append(CN_INTEGER); // 整数部分加“元”
    if (decimalStr.isEmpty()) {
      result.append(CN_WHOLE); // 如果没有小数部分，加“整”
    } else {
      result.append(decimalStr); // 否则拼接小数部分
    }

    return result.toString();
  }

  /**
   * 转换整数部分为中文大写
   *
   * @param integerPart 整数部分
   * @return 中文大写字符串
   */
  private static String convertIntegerPart(long integerPart) {
    if (integerPart == 0) {
      return CN_ZERO;
    }

    StringBuilder result = new StringBuilder();
    int unitIndex = 0; // 单位索引
    boolean lastIsZero = false; // 上一位是否为零

    while (integerPart > 0) {
      int digit = (int) (integerPart % 10); // 取出当前位的数字
      if (digit != 0) {
        // 如果当前位不是零，则添加单位和数字
        if (lastIsZero) {
          result.insert(0, CN_ZERO); // 如果上一位是零，则补一个零
        }
        result.insert(0, CN_UNITS[unitIndex]); // 添加单位
        result.insert(0, CN_NUMBERS[digit]); // 添加数字
        lastIsZero = false;
      } else {
        // 当前位是零，标记为上一位是零
        lastIsZero = true;
      }
      integerPart /= 10; // 处理下一位
      unitIndex++;
    }

    // 去掉末尾多余的零
    while (result.toString().endsWith(CN_ZERO)) {
      result.deleteCharAt(result.length() - 1);
    }

    return result.toString();
  }

  /**
   * 转换小数部分为中文大写
   *
   * @param decimalPart 小数部分（角、分）
   * @return 中文大写字符串
   */
  private static String convertDecimalPart(int decimalPart) {
    if (decimalPart == 0) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    int jiao = decimalPart / 10; // 角
    int fen = decimalPart % 10; // 分

    if (jiao != 0) {
      result.append(CN_NUMBERS[jiao]).append(CN_JIAO); // 角部分
    }
    if (fen != 0) {
      result.append(CN_NUMBERS[fen]).append(CN_FEN); // 分部分
    }

    return result.toString();
  }

}
