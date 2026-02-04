package com.syj.eplus.module.infra.code;

import com.syj.eplus.module.infra.service.sn.SnService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编码生成器测试类
 *
 * @author optimization-plan
 */
@SpringBootTest
class CodeGeneratorTest {

    @Resource
    private SnService snService;

    /**
     * 测试常量规则
     */
    @Test
    void testConstantCodeRule() {
        // Given
        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new ConstantCodeRule("PO"))
                .build();

        // When
        String code = generator.execute(0);

        // Then
        assertNotNull(code);
        assertEquals("PO", code);
    }

    /**
     * 测试日期时间规则
     */
    @Test
    void testDateTimeCodeRule() {
        // Given
        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new DateTimeCodeRule())
                .build();

        // When
        String code = generator.execute(0);

        // Then
        assertNotNull(code);
        assertEquals(8, code.length()); // yyyyMMdd格式
        assertTrue(code.matches("\\d{8}")); // 全是数字
    }

    /**
     * 测试序列号规则
     */
    @Test
    void testSerialNumberCodeRule() {
        // Given
        String type = "TEST_SN_" + System.currentTimeMillis();
        String prefix = "SN";
        int length = 4;

        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new SerialNumberCodeRule(snService, type, prefix, length))
                .build();

        // When
        String code1 = generator.execute(length);
        String code2 = generator.execute(length);

        // Then
        assertNotNull(code1);
        assertNotNull(code2);
        assertEquals(4, code1.length());
        assertEquals(4, code2.length());
        assertTrue(code1.matches("\\d{4}"));
        assertTrue(code2.matches("\\d{4}"));
        assertNotEquals(code1, code2); // 序列号应该递增
    }

    /**
     * 测试组合规则生成编码
     */
    @Test
    void testCombinedCodeGeneration() {
        // Given
        String type = "TEST_COMBINED_" + System.currentTimeMillis();
        String prefix = "P";

        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new ConstantCodeRule("PO"))
                .addRule(new DateTimeCodeRule())
                .addRule(new SerialNumberCodeRule(snService, type, prefix, 4))
                .build();

        // When
        String code = generator.execute(4);

        // Then
        assertNotNull(code);
        // 格式: PO + yyyyMMdd + 4位序列号 = 14位
        assertEquals(14, code.length());
        assertTrue(code.startsWith("PO"));
        assertTrue(code.matches("PO\\d{12}"));
    }

    /**
     * 测试编码生成的连续性
     */
    @Test
    void testCodeGenerationSequence() {
        // Given
        String type = "TEST_SEQ_" + System.currentTimeMillis();
        String prefix = "SQ";

        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new ConstantCodeRule("TEST"))
                .addRule(new SerialNumberCodeRule(snService, type, prefix, 4))
                .build();

        // When
        String code1 = generator.execute(4);
        String code2 = generator.execute(4);
        String code3 = generator.execute(4);

        // Then
        assertNotNull(code1);
        assertNotNull(code2);
        assertNotNull(code3);

        // 提取序列号部分（去除TEST前缀）
        int sn1 = Integer.parseInt(code1.substring(4));
        int sn2 = Integer.parseInt(code2.substring(4));
        int sn3 = Integer.parseInt(code3.substring(4));

        // 验证连续性
        assertEquals(sn1 + 1, sn2);
        assertEquals(sn2 + 1, sn3);
    }

    /**
     * 测试不同长度的序列号
     */
    @Test
    void testDifferentSerialNumberLengths() {
        // Given
        String type = "TEST_LENGTH";

        CodeGenerator generator4 = CodeGeneratorBuilder.create()
                .addRule(new SerialNumberCodeRule(snService, type, "L4", 4))
                .build();

        CodeGenerator generator6 = CodeGeneratorBuilder.create()
                .addRule(new SerialNumberCodeRule(snService, type + "_6", "L6", 6))
                .build();

        // When
        String code4 = generator4.execute(4);
        String code6 = generator6.execute(6);

        // Then
        assertEquals(4, code4.length());
        assertEquals(6, code6.length());
        assertTrue(code4.matches("\\d{4}"));
        assertTrue(code6.matches("\\d{6}"));
    }

    /**
     * 测试自定义日期格式
     */
    @Test
    void testCustomDateTimeFormat() {
        // Given
        CodeGenerator generator = CodeGeneratorBuilder.create()
                .addRule(new DateTimeCodeRule("yyyy-MM-dd"))
                .build();

        // When
        String code = generator.execute(0);

        // Then
        assertNotNull(code);
        assertEquals(10, code.length()); // yyyy-MM-dd格式
        assertTrue(code.matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}
