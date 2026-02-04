package com.syj.eplus.module.scm.api.quoteitem;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 14:49
 */
public interface QuoteitemApi {

    void batchInsertQuoteitem(List<QuoteitemDTO> quoteitemList);

    List<QuoteitemDTO> getQuoteitemDTOBySkuId(Long skuId);

    List<String> getSkuCodeListByVenderCode(String venderCode);

    void batchDeleteQuoteitem(String skuCode,Long skuId);

//    List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList);

    List<QuoteitemDTO> getQuoteItemDTOListBySkuIdList(List<Long> skuIdList);

    void batchUpdateQuoteItem(List<QuoteitemDTO> quoteitemList);

//    Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuCodeList(List<String> skuCodeList);

    Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuIdList(List<Long> skuIdList);

    List<QuoteitemDTO> getQuoteitemDTOByPackageTypeId(Long skuId);

    /**
     * 根据skuId列表获取对应默认报价
     * @param skuIdList skuId列表
     * @return 报价map
     */
    Map<String, JsonAmount> getPriceMapBySkuIdList(List<Long> skuIdList);

    List<QuoteitemDTO> getQuoteitemDTOByVenderCodesOrPurchaseUserId(List<String> venderCodeList,Long purchaseUserId );

    /**
     * 通过产品编号批量获取报价单中税率
     * @param skuIds 产品id列表
     * @return 报价单中税率缓存
     */
    Map<String, BigDecimal> getTaxRateBySkuIdList(List<Long> skuIds);

    /**
     * 校验默认报价中供应商审核通过的产品编号
     * @param skuCodeList 待校验产品编号列表
     * @return 有效产品编号列表
     */
    Set<String> getAvailableSkuIdSetBySkuCodeList(Collection<String> skuCodeList);

    /**
     * 通过币种和金额获取产品编号列表
     * @param currency 币种
     * @param amountMax 金额上限
     * @param amountMin 金额下限
     * @return 产品编号列表
     */
    Set<Long> getSkuIdListByCurrencyAndAmount(String currency, BigDecimal amountMax,BigDecimal amountMin);
}
