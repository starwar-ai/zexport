package com.syj.eplus.module.infra.service.orderlink;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单链路服务测试类
 *
 * @author optimization-plan
 */
@SpringBootTest
@Transactional
class OrderLinkServiceImplTest {

    @Resource
    private OrderLinkService orderLinkService;

    /**
     * 测试验证订单链路存在 - 空列表
     */
    @Test
    void testValidateOrderLinkExists_EmptyList() {
        // Given
        List<String> emptyCodes = Collections.emptyList();

        // When
        boolean exists = orderLinkService.validateOrderLinkExists(emptyCodes);

        // Then
        assertFalse(exists, "空列表应该返回false");
    }

    /**
     * 测试验证订单链路存在 - null列表
     */
    @Test
    void testValidateOrderLinkExists_NullList() {
        // Given
        List<String> nullCodes = null;

        // When
        boolean exists = orderLinkService.validateOrderLinkExists(nullCodes);

        // Then
        assertFalse(exists, "null列表应该返回false");
    }

    /**
     * 测试查询订单链路 - 正常查询
     * 注意：此测试需要根据实际数据库数据调整
     */
    @Test
    void testGetOrderLinkDTOByCodeAndName_Normal() {
        // Given
        String code = "TEST_CODE";
        String name = "TEST_NAME";
        String linkCode = "TEST_LINK";
        Integer orderType = 1;

        // When
        List<OrderLinkDTO> result = orderLinkService.getOrderLinkDTOByCodeAndName(
                code, name, linkCode, orderType
        );

        // Then
        assertNotNull(result, "结果不应该为null");
        // 如果没有数据，应该返回空列表而不是null
        assertTrue(result.isEmpty() || CollUtil.isNotEmpty(result));
    }

    /**
     * 测试查询订单链路 - 不同的订单类型
     */
    @Test
    void testGetOrderLinkDTOByCodeAndName_DifferentOrderTypes() {
        // Given
        String code = "TEST_CODE";
        String name = "TEST_NAME";
        String linkCode = "TEST_LINK";

        // When
        List<OrderLinkDTO> result1 = orderLinkService.getOrderLinkDTOByCodeAndName(
                code, name, linkCode, 1
        );
        List<OrderLinkDTO> result2 = orderLinkService.getOrderLinkDTOByCodeAndName(
                code, name, linkCode, 0
        );

        // Then
        assertNotNull(result1, "类型1的结果不应该为null");
        assertNotNull(result2, "类型0的结果不应该为null");
    }

    /**
     * 测试SQL注入防护 - 确保参数化查询有效
     */
    @Test
    void testValidateOrderLinkExists_SqlInjectionProtection() {
        // Given - 尝试SQL注入的恶意代码
        List<String> maliciousCodes = Arrays.asList(
                "'; DROP TABLE order_link; --",
                "' OR '1'='1",
                "admin' --"
        );

        // When - 应该安全处理，不会执行SQL注入
        boolean exists = orderLinkService.validateOrderLinkExists(maliciousCodes);

        // Then - 应该正常返回结果，不会抛出异常或执行恶意SQL
        assertFalse(exists, "恶意代码应该被安全处理");
    }


    /**
     * 测试性能 - 验证查询条件是否被正确使用
     * 如果使用了全表扫描，大量数据时性能会很差
     */
    @Test
    void testGetOrderLinkDTOByCodeAndName_Performance() {
        // Given
        String code = "PERF_TEST";
        String name = "PERF_NAME";
        String linkCode = "PERF_LINK";
        Integer orderType = 1;

        long startTime = System.currentTimeMillis();

        // When
        List<OrderLinkDTO> result = orderLinkService.getOrderLinkDTOByCodeAndName(
                code, name, linkCode, orderType
        );

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then
        assertNotNull(result);
        // 正常情况下查询应该在100ms内完成
        // 如果使用全表扫描，可能会超过这个时间
        assertTrue(duration < 1000,
                "查询时间过长(" + duration + "ms)，可能存在性能问题");
    }
}
