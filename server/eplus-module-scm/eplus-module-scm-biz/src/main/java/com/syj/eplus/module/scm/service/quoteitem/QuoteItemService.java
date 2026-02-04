package com.syj.eplus.module.scm.service.quoteitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemRespVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 供应商报价明细 Service 接口
 *
 * @author ePlus
 */
public interface QuoteItemService {

    /**
     * 创建供应商报价明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuoteItem(@Valid QuoteItemSaveReqVO createReqVO);

    /**
     * 更新供应商报价明细
     *
     * @param updateReqVO 更新信息
     */
    void updateQuoteItem(@Valid QuoteItemSaveReqVO updateReqVO);

    /**
     * 删除供应商报价明细
     *
     * @param id 编号
     */
    void deleteQuoteItem(Long id);

    /**
     * 获得供应商报价明细
     *
     * @param id 编号
     * @return 供应商报价明细
     */
    QuoteItemRespVO getQuoteItem(Long id);

    /**
     * 获得供应商报价明细分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商报价明细分页
     */
    PageResult<QuoteItemRespVO> getQuoteItemPage(QuoteItemPageReqVO pageReqVO);

    /**
     * 批量插入报价明细
     *
     * @param quoteItemSaveReqVOList 报价明细DTO列表
     */
    void batchInsertQuoteItem(List<QuoteItemSaveReqVO> quoteItemSaveReqVOList);

    /**
     * 根据skuId获取报价明细
     *
     * @param skuId sku主键
     * @return 报价明细列表
     */
    List<QuoteitemDTO> getQuoteitemDTOBySkuId(Long skuId);

    /**
     * 根据skuId获取报价明细
     *
     * @param packageTypeId packageType主键
     * @return 报价明细列表
     */
    List<QuoteitemDTO> getQuoteitemDTOByPackageTypeId(Long packageTypeId);

    /**
     * 根据sku编号批量删除报价明细
     *
     * @param skuCode sku编号
     */
    void batchDeleteQuoteitem(String skuCode,Long skuId);

    /**
     * 根据sku编号列表获取报价明细列表
     *
     * @param skuCodeList sku编号列表
     * @return 报价明细列表
     */
//    List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList);

    /**
     * 根据skuId列表获取报价明细列表
     *
     * @param skuIdList sku编号列表
     * @return 报价明细列表
     */
    List<QuoteitemDTO> getQuoteItemDTOListBySkuIdList(List<Long> skuIdList);

    /**
     * 批量修改供应商报价
     *
     * @param quoteitemList 报价列表
     */
    void batchUpdateQuoteItem(List<QuoteitemDTO> quoteitemList);

    /**
     * 根据供应商编号获取sku编号列表
     *
     * @param venderCode 供应商编号
     * @return sku编号列表
     */
    List<String> getSkuCodeListByVenderCode(String venderCode);

    /**
     * 根据sku编号列表获取供应商报价map
     *
     * @param skuCodeList sku编号列表
     * @return 报价明细map
     */
//    Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuCodeList(List<String> skuCodeList);

    /**
     * 根据skuId列表获取供应商报价map
     *
     * @param skuIdList skuId列表
     * @return 报价明细map
     */
    Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuIdList(List<Long> skuIdList);

    /**
     * 根据skuId列表获取对应默认报价
     * @param skuIdList skuId列表
     * @return 报价map
     */
    Map<String, JsonAmount> getPriceMapBySkuIdList(List<Long> skuIdList);

    List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList);

    List<QuoteitemDTO> getQuoteitemDTOByVenderCodesOrPurchaseUserId(List<String> venderCodeList,Long purchaseUserId);

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
     * 根据产品编号列表获取相同供应商报价
     * @param skuCodeList 产品编号列表
     * @return 报价列表
     */
    List<QuoteitemDTO> getSameVenderQuoteList(List<String> skuCodeList);

    /**
     * 通过币种和金额获取产品编号列表
     * @param currency 币种
     * @param amountMax 金额上限
     * @param amountMin 金额下限
     * @return 产品编号列表
     */
    Set<Long> getSkuIdListByCurrencyAndAmount(String currency, BigDecimal amountMax,BigDecimal amountMin);
}