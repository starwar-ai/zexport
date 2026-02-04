package com.syj.eplus.framework.common.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MoneyUtil工具类单元测试
 *
 * @author eplus-framework
 */
class MoneyUtilTest {

    @Test
    void testConvertToChinese_整数() {
        BigDecimal amount = new BigDecimal("100");
        String result = MoneyUtil.convertToChinese(amount);
        assertEquals("壹佰元", result, "100元应转换为: 壹佰元");
    }

    @Test
    void testConvertToChinese_小数() {
        BigDecimal amount = new BigDecimal("42114.24");
        String result = MoneyUtil.convertToChinese(amount);
        assertTrue(result.contains("肆万"), "应包含'肆万'");
        assertTrue(result.contains("贰角"), "应包含'贰角'");
        assertTrue(result.contains("肆分"), "应包含'肆分'");
    }

    @Test
    void testConvertToChinese_零元() {
        BigDecimal amount = new BigDecimal("0");
        String result = MoneyUtil.convertToChinese(amount);
        assertEquals("零元", result, "0元应转换为: 零元");
    }

    @Test
    void testConvertToChinese_只有角() {
        BigDecimal amount = new BigDecimal("0.50");
        String result = MoneyUtil.convertToChinese(amount);
        assertTrue(result.contains("伍角"), "应包含'伍角'");
    }

    @Test
    void testConvertToChinese_只有分() {
        BigDecimal amount = new BigDecimal("0.05");
        String result = MoneyUtil.convertToChinese(amount);
        assertTrue(result.contains("伍分"), "应包含'伍分'");
    }

    @Test
    void testConvertToChinese_大额() {
        BigDecimal amount = new BigDecimal("123456789.12");
        String result = MoneyUtil.convertToChinese(amount);
        assertNotNull(result, "大额金额应能正常转换");
        assertTrue(result.contains("亿"), "应包含'亿'");
    }

    @Test
    void testConvertToChinese_空值() {
        assertThrows(IllegalArgumentException.class, () -> {
            MoneyUtil.convertToChinese(null);
        }, "null值应抛出异常");
    }

    @Test
    void testConvertToChinese_负数() {
        BigDecimal amount = new BigDecimal("-100");
        assertThrows(IllegalArgumentException.class, () -> {
            MoneyUtil.convertToChinese(amount);
        }, "负数应抛出异常");
    }

    @Test
    void testConvertToChinese_精度处理() {
        BigDecimal amount = new BigDecimal("100.999");
        String result = MoneyUtil.convertToChinese(amount);
        // 应该四舍五入到分
        assertTrue(result.contains("壹佰元"), "应正确处理精度");
    }

    @Test
    void testConvertToChinese_特殊数字() {
        // 测试包含0的数字
        BigDecimal amount1 = new BigDecimal("1001.01");
        String result1 = MoneyUtil.convertToChinese(amount1);
        assertNotNull(result1, "应能处理中间含0的数字");

        // 测试10的倍数
        BigDecimal amount2 = new BigDecimal("10000");
        String result2 = MoneyUtil.convertToChinese(amount2);
        assertTrue(result2.contains("壹万"), "应包含'壹万'");
    }
}
