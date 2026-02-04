package cn.iocoder.yudao.module.infra.service.rate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRatePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRateRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.rate.CurrencysRateDO;
import cn.iocoder.yudao.module.infra.pojo.CurrencysRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description：获取实时汇率逻辑层
 * @Author：du
 * @Date：2024/1/12 14:34
 */

public interface CurrencysRateService {
    /**
     * 创建动态汇率
     *
     * @param currencysRateDO 创建信息
     * @return 编号
     */
    int createCurrencysRate(CurrencysRateDO currencysRateDO);

    /**
     * 批量创建动态汇率
     * @param currencysRateDOList 创建信息
     * @return 条数
     */
    int batchCreateCurrencysRate(List<CurrencysRateDO> currencysRateDOList,String date);

    /**
     * 判断是否统一时间刷新的汇率
     *
     * @param dailyCurrIndate
     * @param dailyCurrName
     * @return
     */
    boolean getCurrencysCountByDate(String dailyCurrIndate, String dailyCurrName);

    /**
     * 获取最新的汇率
     *
     * @return
     */
    Map<String, CurrencysRate> getTheLatestCurrencyRate();

    /**
     * 根据币种名称获取对应汇率
     *
     * @param currencyName
     * @return
     */
    CurrencysRate getCurrencyRateByCurrencyName(String currencyName);

    List<CurrencysRate> getCurrencysRateList();

    /**
     * 获得动态汇率
     *
     * @param id 编号
     * @return 动态汇率
     */
    CurrencysRateDO getCurrencysRate(Long id);

    /**
     * 获得动态汇率分页
     *
     * @param pageReqVO 分页查询
     * @return 动态汇率分页
     */
    PageResult<CurrencysRateDO> getCurrencysRatePage(CurrencysRatePageReqVO pageReqVO);

    /**
     * 获取动态汇率缓存
     *
     * @return 动态汇率缓存
     */
    Map<String, BigDecimal> getDailyRateMap();

    /**
     * 获取指定日期动态汇率
     * @param dailyCurrDate 日期
     * @return 动态汇率
     */
    List<CurrencysRateRespVO> getCurrencysRateByDate(String dailyCurrDate);

    Map<String, BigDecimal> getDailyRateMapByDate(String dateStr);

    Map<String, Map<String, BigDecimal>> getDailyRateMapByDates(List<LocalDateTime> dateTimes);



}
