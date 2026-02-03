package cn.iocoder.yudao.module.infra.api.rate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/18 18:11
 */
public interface RateApi {
    /**
     * 获取动态汇率缓存
     * @return 动态汇率缓存
     */
    Map<String, BigDecimal> getDailyRateMap();

    Map<String, BigDecimal> getDailyRateMapByDate(LocalDateTime dateTime);

   Map<String, Map<String, BigDecimal>> getDailyRateMapByDate(Map<String,LocalDateTime> saleMap);

    Set<String> getAllCurrencyByDate();

}
