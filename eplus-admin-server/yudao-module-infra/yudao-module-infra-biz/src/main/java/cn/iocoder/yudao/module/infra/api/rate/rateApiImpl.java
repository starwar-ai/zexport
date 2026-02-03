package cn.iocoder.yudao.module.infra.api.rate;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.infra.service.rate.CurrencysRateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/18 18:34
 */
@Service
public class rateApiImpl implements RateApi{
    @Resource
    private CurrencysRateService currencysRateService;
    @Override
    public Map<String, BigDecimal> getDailyRateMap() {
        return currencysRateService.getDailyRateMap();
    }

    @Override
    public Map<String, BigDecimal> getDailyRateMapByDate(LocalDateTime dateTime) {
        String dateStr = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return currencysRateService.getDailyRateMapByDate(dateStr);
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getDailyRateMapByDate(Map<String,LocalDateTime> saleMap) {
        //入参为销售合同编号和合同时间的map
        //查询获取时间字符串和当天币种 的map
//        Map<String, Map<String, BigDecimal>>  dateMap = currencysRateService.getDailyRateMapByDates(saleMap.values().stream().toList());
        Map<String, Map<String, BigDecimal>> contractDateMap = new HashMap<>();
        saleMap.forEach((k,v)->{
            contractDateMap.put(k,getDailyRateMapByDate(v));
        });
        //返回销售合同编号 和销售合同时间当天的币种 map
        return contractDateMap;
    }

    @Override
    public Set<String> getAllCurrencyByDate() {
        Map<String, BigDecimal> dailyRateMap = currencysRateService.getDailyRateMap();
        if(CollUtil.isEmpty(dailyRateMap))
            return null;
        return dailyRateMap.keySet();
    }
}
