package com.syj.eplus.module.pms.api.sku;

import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.pms.api.sku.dto.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description：sku产品供其他模块调用api
 * @Author：du
 * @Date：2024/5/11 16:07
 */
public interface SkuApi {

    /**
     * 获取产品精简信息map
     * @param idList 产品id列表
     * @return 产品精简信息map
     */
    Map<Long, SimpleSkuDTO>  getSimpleSkuDTOMap(List<Long> idList);

    /**
     * 获取产品精简信息map
     * @param codeList 产品id列表
     * @return 产品精简信息map
     */
    Map<String, SimpleSkuDTO>  getSimpleSkuDTOMapByCode(List<String> codeList);

    Map<Long, SkuDTO>  getSkuDTOMap(List<Long> idList);

    Map<String, SkuDTO> getSkuDTOMapByCodeList(List<String> codeList);

    List<SkuDTO> getSkuDTOListByCodeList(List<String> codeList);

    SimpleSkuDTO  getSimpleSkuDTO(Long id);

    SkuDTO  getSkuDTO(Long id);

    /**
     * 根据组合产品id获取子产品精简列表
     *
     * @param codeList 组合产品codeList
     * @return 子产品map
     */
    Map<String, List<SimpleSkuDTO>> getSubSkuMap(List<String> codeList);

    /**
     * 验证自营产品是否存在当前客户对应的客户产品
     * @param skuCodeMap 待校验自营产品编号map
     * @param custCode 客户编号
     * @return key-待校验自营产品编号  value-对应客户产品的精简信息
     */
    Map<String, SimpleData> validateOwnbrandSku(Map<String, Tuple> skuCodeMap, String custCode);

    /**
     * 根据skuId列表获取对应默认报价中供应商业务员
     *
     * @param skuIdList skuId列表
     * @return 采购员缓存
     */
    Map<String, List<UserDept>> getPurchaseUserListBySkuIdList(List<Long> skuIdList);


    List<SkuBomDTO> getSonSkuListBySkuCode(String skuCode);

    /**
     * 获得sku影响变更
     *
     * @param targetCode 影响范围主键
     * @return sku影响变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    /**
     * 根据产品编号获取产品名称
     * @param codeList 产品编号列表
     * @return 产品名称缓存
     */
    Map<String,SkuNameDTO> getSkuNameCacheByCodeList(List<String> codeList);

    Map<String, List<SkuAuxiliaryDTO>> getAuxiliaryListByCodeList(List<String> skuCodeList);

    /**
     * 批量根据skuCOde获取海关单位
     * @param skuCodeList
     * @return
     */
    Map<String, String> getHsDataUnitBySkuCodes(List<String> skuCodeList);

    Map<String, HsDataDTO> getHsDataBySkuCodes(List<String> skuCodeList);

    /**
     * 获取组合产品树
     * @return 组合产品树
     */
    Map<Long,List<SkuBomDTO>> getAllBomDTOMap();

    /**
     * 获取产品类型缓存
     * @param skuCodeList 产品编号列表
     * @return 产品类型缓存
     */
    Map<String,Integer> getSkuTypeMap(List<String> skuCodeList);

    /**
     * 查询产品id是否存在
     * @param skuIds
     * @return
     */
    Map<Long, Boolean> getSkuExitsByIds(List<Long> skuIds);

    /**
     * 获取组合产品价格
     * @param skuId 产品编号
     * @return 价格
     */
    JsonAmount getCombSkuPrice(Long skuId);

    /**
     * 获取产品规格
     * @param skuCodeList 产品编号
     * @return 产品规格
     */
    Map<String,List<String>> getSkuSpec(List<String> skuCodeList);

    /**
     * 通过产品编号批量获取报价单中税率
     * @param skuCodes 产品编号列表
     * @return 报价单中税率缓存
     */
    Map<String, BigDecimal> getTaxRateBySkuCodeList(Collection<String> skuCodes);

    Map<String,List<SkuBomDTO>> getSonSkuMapBySkuCode(List<String> skuCodes);

    /**
     * 获取基础产品编号
     * @param skuCodes sku产品编号
     * @return 基础产品编号
     */
    Map<String,String> getBasicSkuCode(List<String> skuCodes);

    /**
     * 根据产品编号获取对应自营产品编号列表
     * @param skuCodeList 产品编号列表
     */
    Map<String,List<String>> getOwnSkuCodeListBySkuCode(List<String> skuCodeList);

    /**
     * 通过产品编号获取海关计量单位
     * @param skuCode 产品编号
     * @return 海关计量单位
     */
    String getHsMeasureUnitBySkuCode(String skuCode);

    /**
     * 通过产品编号转换最新id
     * @param codeList 产品编号列表
     * @return 最新id
     */
    Set<SimpleSkuDTO> transformIdByCode(Collection<String> codeList);

    SkuDTO getSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode, String custCode);

    /**
     * 压缩图片
     * @param pictureList 图片列表
     * @return 压缩图片
     */
    String processImageCompression(List<SimpleFile> pictureList);

    Map<String, SkuDTO> getOwnSkuDTOMapByCodeList(List<String> skuCodes);

    SkuDTO getOwnSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String oskuCode);

    Map<String,SimpleSkuDTO> getCskuAndBasicSkuCodeMapByCodes(List<String> skuCodeList);

    void updateCSkuCodeByCode(String code, String cskuCode);

    void updateBarcodeByCode(String code, String barcode);

    /**
     * 根据优势产品标识获取skuId列表
     * @param advantageFlag 优势产品标识
     * @return skuId列表
     * @author 波波
     */
    List<Long> getSkuIdsByAdvantageFlag(Integer advantageFlag);
}
