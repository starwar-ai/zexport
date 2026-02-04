package cn.iocoder.yudao.module.infra.service.rate;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRatePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRateRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.rate.CurrencysRateDO;
import cn.iocoder.yudao.module.infra.dal.mysql.rate.CurrencysRateMapper;
import cn.iocoder.yudao.module.infra.pojo.CurrencysRate;
import com.syj.eplus.framework.common.dict.CommonCurrencyDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/12 14:34
 */

@Service
@Validated
public class CurrencysRateServiceImpl implements CurrencysRateService {
    @Resource
    private CurrencysRateMapper currencysRateMapper;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CurrencysRate> currencysRateCache = new ConcurrentHashMap<>();

    @Override
    public int createCurrencysRate(CurrencysRateDO currencysRateDO) {
        //此时说明库中已经存在网站同一时间刷新的汇率
        if (getCurrencysCountByDate(currencysRateDO.getDailyCurrDate(), currencysRateDO.getDailyCurrName())) {
            return 0;
        }
        int insert = currencysRateMapper.insert(currencysRateDO);
        //新增成功则刷新缓存
        if (insert > 0) {
            currencysRateCache.clear();
            currencysRateCache.put(currencysRateDO.getDailyCurrName(), BeanUtils.toBean(currencysRateDO, CurrencysRate.class));
        }
        return insert;
    }

    private Set<String> getCurrencyByData(String dailyCurrIndate){
        List<CurrencysRateDO> currencysRateDOList = currencysRateMapper.selectList(new LambdaQueryWrapperX<CurrencysRateDO>()
                .eq(CurrencysRateDO::getDailyCurrDate, dailyCurrIndate));
        if (CollUtil.isEmpty(currencysRateDOList)){
            return Set.of();
        }
        return currencysRateDOList.stream().map(CurrencysRateDO::getDailyCurrName).collect(Collectors.toSet());
    }

    @Override
    public int batchCreateCurrencysRate(List<CurrencysRateDO> currencysRateDOList,String date) {
        Set<String> currencyByData = getCurrencyByData(date);
        int result =currencysRateDOList.size();
        currencysRateDOList.stream().map(CurrencysRateDO::getDailyCurrName)
                .forEach(currencysRateCache::remove);
        if (CollUtil.isEmpty(currencyByData)){
            Boolean isSuccess = currencysRateMapper.insertBatch(currencysRateDOList);
            if (isSuccess){
                currencysRateDOList.forEach(s->{
                    currencysRateCache.put(s.getDailyCurrName(), BeanUtils.toBean(s, CurrencysRate.class));
                });
            }
        }else {
            List<CurrencysRateDO> insertList = currencysRateDOList.stream().filter(s -> !currencyByData.contains(s.getDailyCurrName())).toList();
            if (CollUtil.isNotEmpty(insertList)){
                Boolean isSuccess = currencysRateMapper.insertBatch(insertList);
                if (isSuccess){
                    insertList.forEach(s->{
                        currencysRateCache.put(s.getDailyCurrName(), BeanUtils.toBean(s, CurrencysRate.class));
                    });
                }
                result = insertList.size();
            }
        }
        return result;
    }

    @Override
    public boolean getCurrencysCountByDate(String dailyCurrIndate, String dailyCurrName) {
        Long count = currencysRateMapper.selectCount(new LambdaQueryWrapperX<CurrencysRateDO>().eq(CurrencysRateDO::getDailyCurrName, dailyCurrName)
                .eq(CurrencysRateDO::getDailyCurrDate, dailyCurrIndate));
        return count > 0;
    }

    @Override
    public Map<String, CurrencysRate> getTheLatestCurrencyRate() {
        if (CollUtil.isNotEmpty(currencysRateCache)) {
            return currencysRateCache;
        }
        List<CurrencysRateDO> currencysRateDOList = currencysRateMapper.getCurrencysRate();
        if (CollUtil.isEmpty(currencysRateDOList)) {
            logger.warn("[实时汇率]未查询到实时汇率");
            return null;
        }
        List<CurrencysRate> currencysRateList = BeanUtils.toBean(currencysRateDOList, CurrencysRate.class);
        currencysRateList.forEach(s -> currencysRateCache.put(s.getDailyCurrName(), s));
        return currencysRateCache;
    }

    @Override
    public CurrencysRate getCurrencyRateByCurrencyName(String currencyName) {
        return currencysRateCache.get(currencyName);
    }

    @Override
    public List<CurrencysRate> getCurrencysRateList() {
        List<CurrencysRate> currencysRateList = new ArrayList<>();
        if (CollUtil.isNotEmpty(currencysRateCache)) {
            return currencysRateCache.values().stream().toList();
        }
        List<CurrencysRateDO> currencysRateDOList = currencysRateMapper.getCurrencysRate();
        if (CollUtil.isNotEmpty(currencysRateDOList)) {
            currencysRateList = BeanUtils.toBean(currencysRateDOList, CurrencysRate.class);
        }
        return currencysRateList;
    }


    @Override
    public CurrencysRateDO getCurrencysRate(Long id) {
        return currencysRateMapper.selectById(id);
    }

    @Override
    public PageResult<CurrencysRateDO> getCurrencysRatePage(CurrencysRatePageReqVO pageReqVO) {
        return currencysRateMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<String, BigDecimal> getDailyRateMap() {
        List<String> currencyKeys = CommonCurrencyDict.getDictList();
        // 先将configKeys中的每一个key初始化为BigDecimal.ZERO 减少后续判空逻辑
        Map<String, BigDecimal> defaultMap = currencyKeys.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> {
                            if (CommonCurrencyDict.CNY.equals(key)) {
                                return BigDecimal.ONE;
                            }
                            return BigDecimal.ZERO;
                        }
                ));
        if (CollUtil.isEmpty(currencysRateCache)||currencysRateCache.size()==1) {
            initCache();
        }
        // 合并原始configMap中的数据，如果有值则覆盖默认值
        currencysRateCache.forEach((key, value) -> {
            if (currencyKeys.contains(key) && value != null) {
                defaultMap.put(key, value.getDailyCurrRate());
            }
        });
        return defaultMap;
    }

    @Override
    public List<CurrencysRateRespVO> getCurrencysRateByDate(String dailyCurrDate) {
        List<CurrencysRateDO> currencysRateDOS = currencysRateMapper.selectList(new LambdaQueryWrapperX<CurrencysRateDO>().likeIfPresent(CurrencysRateDO::getDailyCurrDate, dailyCurrDate));
        if(CollUtil.isEmpty(currencysRateDOS)){
            List<String> strings = currencysRateMapper.selectList().stream().map(CurrencysRateDO::getDailyCurrDate).distinct().toList();
            currencysRateDOS = currencysRateMapper.selectList(new LambdaQueryWrapperX<CurrencysRateDO>().likeIfPresent(CurrencysRateDO::getDailyCurrDate, findClosestDate(dailyCurrDate,strings)));

        }
        return BeanUtils.toBean(currencysRateDOS, CurrencysRateRespVO.class);
    }

    private String findClosestDate(String targetDate, List<String> allDates) {
        // 存储日期差值（key：日期，value：与目标日期的差值，正数表示大于目标日期，负数表示小于）
        Map<String, Long> dateDiffMap = new HashMap<>();
        long targetTime = parseDateToTimestamp(targetDate);

        for (String date : allDates) {
            long dateTime = parseDateToTimestamp(date);
            dateDiffMap.put(date, dateTime - targetTime);
        }

        // 优先筛选出>=目标日期的日期（差值>=0），取最小差值对应的日期
        Optional<Map.Entry<String, Long>> closestFuture = dateDiffMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .min(Comparator.comparingLong(Map.Entry::getValue));

        if (closestFuture.isPresent()) {
            return closestFuture.get().getKey();
        }

        // 若无未来日期，筛选<=目标日期的日期（差值<0），取最大差值（最接近目标日期）对应的日期
        return dateDiffMap.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(allDates.get(0)); // 兜底：取最新的日期
    }
    private long parseDateToTimestamp(String dateStr) {
        try {
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue(); // Calendar月份从0开始
            int day = now.getDayOfMonth();
            String[] split = dateStr.split("-");
            if(split.length == 3){
                year =Integer.parseInt(split[0]);
                month =Integer.parseInt(split[1]);
                day =Integer.parseInt(split[2]);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            throw new IllegalArgumentException("日期解析失败，输入格式应为8位数字（如20250906）", e);
        }
    }
    @Override
    public Map<String, BigDecimal> getDailyRateMapByDate(String dateStr) {
        List<CurrencysRateDO> currenciesRateDOS = currencysRateMapper.selectList(new LambdaQueryWrapperX<CurrencysRateDO>().likeIfPresent(CurrencysRateDO::getDailyCurrDate, dateStr));
        if(CollUtil.isEmpty(currenciesRateDOS)){
            List<String> strings = currencysRateMapper.selectList().stream().map(CurrencysRateDO::getDailyCurrDate).distinct().toList();
            String closestDate = findClosestDate(dateStr, strings);
            currenciesRateDOS = currencysRateMapper.selectList(new LambdaQueryWrapperX<CurrencysRateDO>().likeIfPresent(CurrencysRateDO::getDailyCurrDate, closestDate));
        }
        if(CollUtil.isEmpty(currenciesRateDOS)){
            return  null;
        }
       return  currenciesRateDOS.stream().collect(Collectors.toMap(CurrencysRateDO::getDailyCurrName,CurrencysRateDO::getDailyCurrRate,(s1,s2)->s1));
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getDailyRateMapByDates(List<LocalDateTime> dateTimes) {
        if (CollUtil.isEmpty(dateTimes)) {
            return Map.of();
        }
        List<String> dateStrs = dateTimes.stream()
                .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toList();
        List<CurrencysRateDO> currenciesRateDOS = currencysRateMapper.selectList(
                new LambdaQueryWrapperX<CurrencysRateDO>()
                        .inIfPresent(CurrencysRateDO::getDailyCurrDate, dateStrs)
                        .isNotNull(CurrencysRateDO::getDailyCurrDate)
                        .isNotNull(CurrencysRateDO::getDailyCurrName)
                        .isNotNull(CurrencysRateDO::getDailyCurrRate)
        );

        return currenciesRateDOS.stream().collect(Collectors.groupingBy(
                        CurrencysRateDO::getDailyCurrDate,
                        Collectors.toMap(
                                CurrencysRateDO::getDailyCurrName,
                                CurrencysRateDO::getDailyCurrRate,
                                (existingRate, newRate) -> newRate
                        )
                ));
    }



    private void initCache() {
        List<CurrencysRateDO> currencysRateDOList = currencysRateMapper.selectList();
        if (CollUtil.isEmpty(currencysRateDOList)) {
            return;
        }
        currencysRateDOList.forEach(s -> {
            currencysRateCache.put(s.getDailyCurrName(), BeanUtils.toBean(s, CurrencysRate.class));
        });
    }
}
