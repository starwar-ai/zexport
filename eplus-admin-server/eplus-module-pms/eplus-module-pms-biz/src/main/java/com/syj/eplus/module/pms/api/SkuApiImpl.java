package com.syj.eplus.module.pms.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.*;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.pms.service.sku.SkuService;
import com.syj.eplus.module.pms.service.skuauxiliary.SkuAuxiliaryService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/11 16:42
 */
@Service
public class SkuApiImpl implements SkuApi {
    @Resource
    @Lazy
    private SkuService service;
    @Resource
    @Lazy
    private SkuAuxiliaryService auxiliaryService;


    @Override
    public Map<Long, SimpleSkuDTO> getSimpleSkuDTOMap(List<Long> idList) {
        return service.getSimpleSkuDTOMap(idList);
    }

    @Override
    public Map<String, SimpleSkuDTO> getSimpleSkuDTOMapByCode(List<String> codeList) {
        return service.getSimpleSkuDTOMapByCode(codeList);
    }

    @Override
    public Map<Long, SkuDTO> getSkuDTOMap(List<Long> idList) {
        return service.getSkuMap(idList);
    }

    @Override
    public Map<String, SkuDTO> getSkuDTOMapByCodeList(List<String> codeList) {
        return service.getSkuMapByCodeList(codeList);
    }

    @Override
    public List<SkuDTO> getSkuDTOListByCodeList(List<String> codeList){
        return service.getSkuDTOListByCodeList(codeList);
    }

    @Override
    public SimpleSkuDTO getSimpleSkuDTO(Long id) {
        return service.getSimpleSkuDTO(id);
    }

    @Override
    public SkuDTO getSkuDTO(Long id) {
        return service.getSkuDTO(id);
    }

    @Override
    public Map<String, List<SimpleSkuDTO>> getSubSkuMap(List<String> codeList) {
        return service.getSubSkuMap(codeList);
    }

    @Override
    public Map<String, SimpleData> validateOwnbrandSku(Map<String, Tuple> skuCodeMap, String custCode) {
        return service.validateOwnbrandSku(skuCodeMap, custCode);
    }

    @Override
    public Map<String, List<UserDept>> getPurchaseUserListBySkuIdList(List<Long> skuIdList) {
        return service.getPurchaseUserListBySkuIdList(skuIdList);
    }

    @Override
    public List<SkuBomDTO> getSonSkuListBySkuCode(String skuCode) {
        List<SkuBomDO> skuBomDOList = service.getSonSkuListBySkuCode(skuCode);
        if(CollUtil.isEmpty(skuBomDOList)){
            return List.of();
        }
        List<SkuBomDTO> skuBomDTOList = BeanUtils.toBean(skuBomDOList, SkuBomDTO.class);
        if (CollUtil.isEmpty(skuBomDTOList)){
            return List.of();
        }
        List<String> skuCodeList = skuBomDTOList.stream().map(SkuBomDTO::getSkuCode).distinct().toList();
        Map<String, List<String>> ownSkuCodeMap = getOwnSkuCodeListBySkuCode(skuCodeList);
        if (CollUtil.isEmpty(ownSkuCodeMap)){
            return skuBomDTOList;
        }
        skuBomDTOList.forEach(s->{
            s.setOwnBrandSkuCodeList(ownSkuCodeMap.get(s.getSkuCode()));
        });
        return skuBomDTOList;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        return service.getConfirmSourceList(targetCode, effectRangeType);
    }

    @Override
    public Map<String, SkuNameDTO> getSkuNameCacheByCodeList(List<String> codeList) {
        return service.getSkuNameCacheByCodeList(codeList);
    }

    @Override
    public Map<String, List<SkuAuxiliaryDTO>> getAuxiliaryListByCodeList(List<String> skuCodeList) {
        List<SkuAuxiliaryDO> doList = auxiliaryService.getAuxiliaryListByCodeList(skuCodeList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<SkuAuxiliaryDTO> dtoList = BeanUtils.toBean(doList, SkuAuxiliaryDTO.class);
        List<String> codeList = dtoList.stream() // 同时获取两个字段组成的List
                .map(d -> new String[]{d.getAuxiliarySkuCode(), d.getSkuCode()})
                .flatMap(Arrays::stream)
                .distinct()
                .toList();
        Map<String, SkuDTO> skuMap = service.getSkuMapByCodeList(codeList);
        if(CollUtil.isNotEmpty(skuMap)){
            dtoList.forEach(s->{
                SkuDTO skuDTO = skuMap.get(s.getSkuCode());
                if(Objects.nonNull(skuDTO)){
                    s.setSkuId(skuDTO.getId()).setCskuCode(skuDTO.getCskuCode());
                }
                SkuDTO auxiliarySkuDTO = skuMap.get(s.getAuxiliarySkuCode());
                if(Objects.nonNull(auxiliarySkuDTO)){
                    s.setAuxiliarySkuId(auxiliarySkuDTO.getId());
                    s.setAuxiliarySkuUnit(auxiliarySkuDTO.getMeasureUnit());
                    List<SimpleFile> picture = auxiliarySkuDTO.getPicture();
                    if(CollUtil.isNotEmpty(picture)){
                        Optional<SimpleFile> first = picture.stream().filter(sku -> sku.getMainFlag() == 1).findFirst();
                        first.ifPresent(s::setAuxiliarySkuPicture);
                    }
                }
            });
        }
        return dtoList.stream().collect(Collectors.groupingBy(SkuAuxiliaryDTO::getSkuCode));

    }

    @Override
    public Map<String, String> getHsDataUnitBySkuCodes(List<String> skuCodeList) {
        return  service.getHsDataUnitBySkuCodes(skuCodeList);

    }

    @Override
    public Map<String, HsDataDTO> getHsDataBySkuCodes(List<String> skuCodeList) {
        return  service.getHsDataBySkuCodes(skuCodeList);
    }

    @Override
    public Map<Long,List<SkuBomDTO>> getAllBomDTOMap() {
        return service.getAllBomDTOMap();
    }

    @Override
    public Map<String, Integer> getSkuTypeMap(List<String> skuCodeList) {
        return service.getSkuTypeMap(skuCodeList);
    }

    @Override
    public Map<Long, Boolean> getSkuExitsByIds(List<Long> skuIds) {
        return service.getSkuExitsByIds(skuIds);
    }

    @Override
    public JsonAmount getCombSkuPrice(Long skuId) {
        if (Objects.isNull(skuId)){
            return new JsonAmount();
        }
        return service.getCombSkuPrice(skuId);
    }

    @Override
    public Map<String, List<String>> getSkuSpec(List<String> skuCodeList) {
        return service.getSkuSpec(skuCodeList);
    }

    @Override
    public Map<String, BigDecimal> getTaxRateBySkuCodeList(Collection<String> skuCodes) {
        return service.getTaxRateBySkuCodeList(skuCodes);
    }

    @Override
    public Map<String, List<SkuBomDTO>> getSonSkuMapBySkuCode(List<String> skuCodes) {
        return service.getSonSkuMapBySkuCode(skuCodes);
    }

    @Override
    public Map<String,String> getBasicSkuCode(List<String> skuCodes) {
        if (CollUtil.isEmpty(skuCodes)){
            return Map.of();
        }
        return service.getBasicSkuCode(skuCodes);
    }

    @Override
    public Map<String, List<String>> getOwnSkuCodeListBySkuCode(List<String> skuCodeList) {
        return service.getOwnSkuCodeListBySkuCode(skuCodeList);
    }

    @Override
    public String getHsMeasureUnitBySkuCode(String skuCode) {
        return service.getHsMeasureUnitBySkuCode(skuCode);
    }

    @Override
    public Set<SimpleSkuDTO> transformIdByCode(Collection<String> codeList) {
        return service.transformIdByCode(codeList);
    }

    @Override
    public SkuDTO getSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode, String custCode) {
        return  service.getSkuByBasicCodeAndCskuCodeAndCustCode(basicSkuCode,cskuCode,custCode);
    }

    @Override
    public String processImageCompression(List<SimpleFile> pictureList) {
        return service.processImageCompression(pictureList);
    }

    @Override
    public Map<String, SkuDTO> getOwnSkuDTOMapByCodeList(List<String> skuCodes) {
        return service.getOwnSkuDTOMapByCodeList(skuCodes);
    }

    @Override
    public SkuDTO getOwnSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String oskuCode) {
        return  service.getOwnSkuByBasicCodeAndCskuCodeAndCustCode(basicSkuCode,oskuCode);
    }

    @Override
    public Map<String, SimpleSkuDTO> getCskuAndBasicSkuCodeMapByCodes(List<String> skuCodeList) {
        return service.getCskuAndBasicSkuCodeMapByCodes(skuCodeList);
    }

    @Override
    public void updateCSkuCodeByCode(String code, String cskuCode) {
        service.updateCSkuCodeByCode(code,cskuCode);
    }

    @Override
    public void updateBarcodeByCode(String code, String barcode) {
        service.updateBarcodeByCode(code,barcode);
    }

    /**
     * 根据优势产品标识获取skuId列表
     * @param advantageFlag 优势产品标识
     * @return skuId列表
     * @author 波波
     */
    @Override
    public List<Long> getSkuIdsByAdvantageFlag(Integer advantageFlag) {
        return service.getSkuIdsByAdvantageFlag(advantageFlag);
    }

}
