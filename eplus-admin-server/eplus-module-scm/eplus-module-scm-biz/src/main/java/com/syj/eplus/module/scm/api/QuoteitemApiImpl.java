package com.syj.eplus.module.scm.api;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.scm.api.quoteitem.QuoteitemApi;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;
import com.syj.eplus.module.scm.convert.quoteitem.QuoteItemConvert;
import com.syj.eplus.module.scm.service.quoteitem.QuoteItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 15:25
 */
@Service
public class QuoteitemApiImpl implements QuoteitemApi {
    @Resource
    private QuoteItemService quoteItemService;

    @Override
    public void batchInsertQuoteitem(List<QuoteitemDTO> quoteitemList) {
        List<QuoteItemSaveReqVO> quoteItemSaveReqVOList = QuoteItemConvert.INSTANCE.convertQuoteItemSaveReqVOList(quoteitemList);
        quoteItemService.batchInsertQuoteItem(quoteItemSaveReqVOList);
    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOBySkuId(Long skuId) {
        return quoteItemService.getQuoteitemDTOBySkuId(skuId);
    }

    @Override
    public List<String> getSkuCodeListByVenderCode(String venderCode) {
        return quoteItemService.getSkuCodeListByVenderCode(venderCode);
    }

    @Override
    public void batchDeleteQuoteitem(String skuCode,Long skuId) {
        quoteItemService.batchDeleteQuoteitem(skuCode,skuId);
    }

//    @Override
//    public List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList) {
//        return quoteItemService.getQuoteItemDTOListBySkuCodeList(skuCodeList);
//    }

    @Override
    public List<QuoteitemDTO> getQuoteItemDTOListBySkuIdList(List<Long> skuIdList) {
        return quoteItemService.getQuoteItemDTOListBySkuIdList(skuIdList);
    }

    @Override
    public void batchUpdateQuoteItem(List<QuoteitemDTO> quoteitemList) {
        quoteItemService.batchUpdateQuoteItem(quoteitemList);
    }

//    @Override
//    public Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuCodeList(List<String> skuCodeList) {
//        return quoteItemService.getQuoteItemDTOMapBySkuCodeList(skuCodeList);
//    }

    @Override
    public Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuIdList(List<Long> skuIdList) {
        return quoteItemService.getQuoteItemDTOMapBySkuIdList(skuIdList);
    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOByPackageTypeId(Long packageTypeId) {
        return quoteItemService.getQuoteitemDTOByPackageTypeId(packageTypeId);
    }

    @Override
    public Map<String, JsonAmount> getPriceMapBySkuIdList(List<Long> skuIdList) {
        if (CollUtil.isEmpty(skuIdList)){
            return Map.of();
        }
        return quoteItemService.getPriceMapBySkuIdList(skuIdList);
    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOByVenderCodesOrPurchaseUserId(List<String> venderCodeList,Long purchaseUserId) {

        return quoteItemService.getQuoteitemDTOByVenderCodesOrPurchaseUserId(venderCodeList,purchaseUserId);
    }

    @Override
    public Map<String, BigDecimal> getTaxRateBySkuIdList(List<Long> skuIds) {
       return quoteItemService.getTaxRateBySkuIdList(skuIds);
    }

    @Override
    public Set<String> getAvailableSkuIdSetBySkuCodeList(Collection<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)){
            return Set.of();
        }
        return quoteItemService.getAvailableSkuIdSetBySkuCodeList(skuCodeList);
    }

    @Override
    public Set<Long> getSkuIdListByCurrencyAndAmount(String currency, BigDecimal amountMax, BigDecimal amountMin) {
        return quoteItemService.getSkuIdListByCurrencyAndAmount(currency,amountMax,amountMin);
    }
}
