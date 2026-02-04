package cn.iocoder.yudao.module.infra.job.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.infra.dal.dataobject.rate.CurrencysRateDO;
import cn.iocoder.yudao.module.infra.job.job.entity.RateResponse;
import cn.iocoder.yudao.module.infra.service.rate.CurrencysRateService;
import com.syj.eplus.framework.common.dict.CalculationDict;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description：自动获取币种汇率
 * @Author：du
 * @Date：2024/1/12 9:09
 */

@Slf4j
@Component
public class AutoGetCurrencyRateJob implements JobHandler {
    @Resource
    CurrencysRateService currencysRateService;

    private static final Set<String> CURRENCY_SET =Set.of("CZK","CNY","USD","EUR","JPY","HKD","GBP","RUB","PLN");
    @Override
    public String execute(String param) throws Exception {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //目前写死 只为出版可以获得汇率 后期其他模块引入时需可配置
        List<CurrencysRateDO> currencysRateDOList = new ArrayList<>();
        currencysRateDOList.add(CurrencysRateDO.builder()
                .dailyCurrName("RMB")
                .dailyCurrRate(BigDecimal.ONE)
                .dailyCurrMidRate(BigDecimal.ONE)
                .dailyCurrSource(1)
                .dailyCurrDate(todayFormat)
                .build());
        CURRENCY_SET.forEach(s->{
            BigDecimal exchangeRate = getExchangeRate(todayFormat, s);
            CurrencysRateDO currencysRateDO = CurrencysRateDO.builder()
                    .dailyCurrName(s)
                    .dailyCurrRate(exchangeRate)
                    .dailyCurrMidRate(exchangeRate)
                    .dailyCurrSource(1)
                    .dailyCurrDate(todayFormat)
                    .build();
            currencysRateDOList.add(currencysRateDO);
        });
        currencysRateService.batchCreateCurrencysRate(currencysRateDOList,todayFormat);
        return String.format("定时获取汇率数量 %s 个", currencysRateDOList.size());
    }
    private String getSearchEnHtml(String lsToday, String lsSourceCurrency, String liPage) {
        String url = "https://srh.bankofchina.com/search/whpj/searchen.jsp?" + "erectDate=" + lsToday +
                "&nothing=" + lsToday +
                "&pjname=" + lsSourceCurrency +
                "&page=" + liPage;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpPost.setHeader("Accept", "Accept: text/plain, */*");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
        httpPost.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
        String html = "";
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = response.getEntity();
                html = EntityUtils.toString(httpEntity, "utf-8");
            } else {
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (ClientProtocolException e) {
           log.info("[实时获取汇率]出错{}",e.getMessage());
        } catch (IOException e) {
            log.info("[实时获取汇率]出错{}",e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }

        return html;
    }

    /**
     * 获取交易所汇率网页
     * @param date
     * @return
     */
    private BigDecimal getExchangeRate(String date,String currency) {
        return getExchangeRateFromApi(date,currency);
    }

    /**
     * 从API获取汇率
     * @param date
     * @return
     */
    private BigDecimal getExchangeRateFromApi(String date,String currency) {
//        String url = "https://v2.xxapi.cn/api/allrates";
        BigDecimal result = BigDecimal.ZERO;
        String url = "https://v6.exchangerate-api.com/v6/b23e58cd445c6d3ff3d621ed/latest/"+currency;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpGet.setHeader("Accept", "Accept: text/plain, */*");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
        httpGet.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
        httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
        String jsonResponse = "";
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = response.getEntity();
                jsonResponse = EntityUtils.toString(httpEntity, "utf-8");
            } else {
                log.info("[实时获取汇率]出错{}", EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (ClientProtocolException e) {
            log.info("[实时获取汇率]出错{}", e.getMessage());
        } catch (IOException e) {
            log.info("[实时获取汇率]出错{}", e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        RateResponse rateResponse = JsonUtils.parseObject(jsonResponse, RateResponse.class);
        if (rateResponse != null && Objects.equals(rateResponse.getResult(), "success")) {
            Map<String, BigDecimal> conversionRates = rateResponse.getConversion_rates();
            if (CollUtil.isNotEmpty(conversionRates)){
                BigDecimal cnyRate = conversionRates.get("CNY");
                if (Objects.nonNull(cnyRate)){
                    // 获取人民币汇率
                    result =cnyRate;
                }
            }
        }
        return result;
    }

    /**
     * 网页内容读取
     * @param html
     * @return
     */
    private int assembleObjByHtml(String html) {
        Document document = Jsoup.parse(html);
        Elements tables = document.getElementsByTag("table");
        int tableIndex = -1;
        for (int i = 0; i < tables.size(); i++) {
            Element element = tables.get(i);
            String text = element.text();
            if (text.indexOf("Currency Name") > -1) {
                tableIndex = i;
                break;
            }
        }
        if (tableIndex > -1) {
            Element table = tables.get(tableIndex);
            Elements trs = table.select("tr");
            //空页面不做同步
            return currencysRateService.createCurrencysRate(Optional.ofNullable(trs.get(1)).map(this::transformCurrencysRate).get());
        }
        return 0;
    }

    /**
     * 转换汇率表
     * @param element
     * @return
     */
    private CurrencysRateDO transformCurrencysRate(Element element) {
        Elements tds = element.select("td");
        if (CollUtil.isNotEmpty(tds)) {
            BigDecimal realRate = StrUtil.isNotEmpty(tds.get(1).text()) ? NumberUtil.div(new BigDecimal(tds.get(1).text()), new BigDecimal(CommonDict.ONE_HUNDRED), CalculationDict.DECIMAL_PRECISION) : BigDecimal.ZERO;
            BigDecimal middleRate = StrUtil.isNotEmpty(tds.get(5).text()) ? NumberUtil.div(new BigDecimal(tds.get(5).text()), new BigDecimal(CommonDict.ONE_HUNDRED), CalculationDict.DECIMAL_PRECISION) : BigDecimal.ZERO;
            return CurrencysRateDO.builder().dailyCurrName(tds.get(0).text())
                    .dailyCurrRate(realRate)
                    .dailyCurrMidRate(middleRate)
                    .dailyCurrSource(1)
                    .dailyCurrDate(tds.get(6).text()).build();
        }
        return new CurrencysRateDO();
    }


}
