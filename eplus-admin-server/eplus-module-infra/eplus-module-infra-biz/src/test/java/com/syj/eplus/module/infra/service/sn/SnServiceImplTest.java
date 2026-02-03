package com.syj.eplus.module.infra.service.sn;

import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import com.syj.eplus.module.infra.dal.mysql.sn.SnMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 序列号服务测试类
 *
 * @author optimization-plan
 */
@SpringBootTest
@Transactional
class SnServiceImplTest {

    @Resource
    private SnService snService;

    @Resource
    private SnMapper snMapper;

    /**
     * 测试正常生成序列号
     */
    @Test
    void testGetAndIncrementSn_Normal() {
        // Given
        String type = "TEST_NORMAL";
        String codePrefix = "TN";

        // When
        SnDO sn1 = snService.getAndIncrementSn(type, codePrefix);
        SnDO sn2 = snService.getAndIncrementSn(type, codePrefix);
        SnDO sn3 = snService.getAndIncrementSn(type, codePrefix);

        // Then
        assertNotNull(sn1);
        assertNotNull(sn2);
        assertNotNull(sn3);
        assertEquals(sn1.getSn() + 1, sn2.getSn());
        assertEquals(sn2.getSn() + 1, sn3.getSn());
        assertEquals(type, sn1.getType());
        assertEquals(codePrefix, sn1.getCodePrefix());
    }

    /**
     * 测试首次生成序列号
     */
    @Test
    void testGetAndIncrementSn_FirstTime() {
        // Given
        String type = "TEST_FIRST_" + System.currentTimeMillis();
        String codePrefix = "TF";

        // When
        SnDO sn = snService.getAndIncrementSn(type, codePrefix);

        // Then
        assertNotNull(sn);
        assertEquals(1, sn.getSn());
        assertEquals(type, sn.getType());
        assertEquals(codePrefix, sn.getCodePrefix());
    }

    /**
     * 测试并发场景下序列号唯一性
     */
    @Test
    void testGetAndIncrementSn_Concurrent() throws Exception {
        // Given
        String type = "TEST_CONCURRENT_" + System.currentTimeMillis();
        String codePrefix = "TC";
        int threadCount = 50;
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Integer> snSet = ConcurrentHashMap.newKeySet();
        List<Exception> exceptions = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    SnDO sn = snService.getAndIncrementSn(type, codePrefix);
                    snSet.add(sn.getSn());
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        assertTrue(exceptions.isEmpty(), "不应该有异常发生");
        assertEquals(threadCount, snSet.size(), "所有序列号应该唯一");
    }

    /**
     * 测试不同类型的序列号独立性
     */
    @Test
    void testGetAndIncrementSn_DifferentTypes() {
        // Given
        String type1 = "TEST_TYPE1";
        String type2 = "TEST_TYPE2";
        String codePrefix = "TT";

        // When
        SnDO sn1 = snService.getAndIncrementSn(type1, codePrefix);
        SnDO sn2 = snService.getAndIncrementSn(type2, codePrefix);

        // Then
        assertNotNull(sn1);
        assertNotNull(sn2);
        assertNotEquals(sn1.getId(), sn2.getId());
        assertEquals(type1, sn1.getType());
        assertEquals(type2, sn2.getType());
    }

    /**
     * 测试不同前缀的序列号独立性
     */
    @Test
    void testGetAndIncrementSn_DifferentPrefixes() {
        // Given
        String type = "TEST_PREFIX";
        String prefix1 = "P1";
        String prefix2 = "P2";

        // When
        SnDO sn1 = snService.getAndIncrementSn(type, prefix1);
        SnDO sn2 = snService.getAndIncrementSn(type, prefix2);

        // Then
        assertNotNull(sn1);
        assertNotNull(sn2);
        assertNotEquals(sn1.getId(), sn2.getId());
        assertEquals(prefix1, sn1.getCodePrefix());
        assertEquals(prefix2, sn2.getCodePrefix());
    }
}
