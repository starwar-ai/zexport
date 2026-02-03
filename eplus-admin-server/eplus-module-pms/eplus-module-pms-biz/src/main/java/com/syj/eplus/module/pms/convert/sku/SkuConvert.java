package com.syj.eplus.module.pms.convert.sku;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SimpleSkuDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SkuDO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * sku实体转换类
 *
 * @author 过量
 * @date 2016/10/31
 */
@Mapper
public interface SkuConvert {

    SkuConvert INSTANCE = Mappers.getMapper(SkuConvert.class);

    @Mapping(target = "creatorName", ignore = true)
    SkuInfoRespVO convert(SkuDO skuDO);

    default SkuInfoRespVO convertSkuRespVO(SkuDO skuDO, QuoteitemDTO quoteitemDTO, Map<String, String> custNameCache,Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap,Map<Long, String> userDeptCache) {
        SkuInfoRespVO skuRespVO = convert(skuDO);
        if (CollUtil.isNotEmpty(userDeptCache)){
            if (StrUtil.isNotEmpty(skuDO.getCreator())){
                skuRespVO.setCreatorName(userDeptCache.get(Long.parseLong(skuDO.getCreator())));
            }
        }
        if (CollUtil.isNotEmpty(brandSimpleRespVOMap)&&Objects.nonNull(skuDO.getBrandId())){
            BrandSimpleRespVO brandSimpleRespVO = brandSimpleRespVOMap.get(skuDO.getBrandId());
            if (Objects.nonNull(brandSimpleRespVO)){
                skuRespVO.setBrandName(brandSimpleRespVO.getName());
            }
        }
        if (CollUtil.isNotEmpty(custNameCache)) {
            skuRespVO.setCustName(custNameCache.get(skuDO.getCustCode()));
        }
        if (Objects.isNull(quoteitemDTO)) {
            return skuRespVO;
        }
        skuRespVO.setDelivery(quoteitemDTO.getDelivery());
        skuRespVO.setPurchaseUserName(quoteitemDTO.getPurchaseUserName());
        skuRespVO.setDefaultFlag(quoteitemDTO.getDefaultFlag());
        skuRespVO.setWithTaxPrice(quoteitemDTO.getWithTaxPrice());
        skuRespVO.setCurrency(quoteitemDTO.getCurrency());
        skuRespVO.setSingleGrossweight(quoteitemDTO.getSingleGrossweight());
        skuRespVO.setVenderName(quoteitemDTO.getVenderName());
        List<SimpleFile> pictures = skuRespVO.getPicture();
        if (CollUtil.isEmpty(pictures)) {
            return skuRespVO;
        }
        SimpleFile simpleFile = pictures.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
        if (Objects.nonNull(simpleFile)) {
            skuRespVO.setMainPicture(simpleFile);
        }
        return skuRespVO;
    }

    SkuDO convertSkuDO(SkuSaveReqVO saveReqVO);

    @Mapping(target = "mainPicture", ignore = true)
    SimpleSkuDTO convertSimpleSkuDTO(SimpleSkuDO simpleSkuDO);

    SimpleSkuDO convertSimpleSkuDO(SkuDO skuDO);

    default SimpleSkuDTO convertSimpleSkuDTO(SimpleSkuDO simpleSkuDO, List<QuoteitemDTO> quoteitemDTOList) {
        SimpleSkuDTO simpleSkuDTO = convertSimpleSkuDTO(simpleSkuDO);
        if (CollUtil.isNotEmpty(quoteitemDTOList)) {
            List<QuoteitemDTO> filterQuoteItemDTOList = quoteitemDTOList.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).collect(Collectors.toList());
            if (CollUtil.isEmpty(filterQuoteItemDTOList)){
                return simpleSkuDTO;
            }
            filterQuoteItemDTOList.forEach(s->{
                List<JsonSpecificationEntity> specificationList = s.getSpecificationList();
                if (CollUtil.isEmpty(specificationList)){
                    return;
                }
                BigDecimal outerboxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(specificationList);
                JsonWeight grossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(specificationList);
                JsonWeight netweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(specificationList);
                s.setOuterboxVolume(outerboxVolume);
                s.setOuterboxGrossweight(grossweight);
                s.setOuterboxNetweight(netweight);
            });
            List<UserDept> userDeptList = quoteitemDTOList.stream().map(s -> new UserDept().setUserId(s.getPurchaseUserId()).setNickname(s.getPurchaseUserName()).setDeptId(s.getPurchaseUserDeptId()).setDeptName(s.getPurchaseUserDeptName())).toList();
            simpleSkuDTO.setPurchaseUserList(userDeptList);
            simpleSkuDTO.setQuoteitemList(quoteitemDTOList);
        }
        return simpleSkuDTO;
    }

    default List<SimpleSkuDTO> convertSimpleSkuDTOList(List<SimpleSkuDO> simpleSkuDOList, Map<String, List<QuoteitemDTO>> quoteItemMap, Map<String, SimpleVenderResp> venderMap, List<String> reorderSkuCodeList) {
        return CollectionUtils.convertList(simpleSkuDOList, s -> {
            List<QuoteitemDTO> dtoList = quoteItemMap.get(s.getCode());
            SimpleSkuDTO simpleSkuDTO = convertSimpleSkuDTO(s, dtoList);
            // 销售明细中sku编号为空则不存在翻单
            if (CollUtil.isEmpty(reorderSkuCodeList)) {
                simpleSkuDTO.setReorderFlag(BooleanEnum.NO.getValue());
            } else {
                // 产品存在销售明细中为翻单 否则不是翻单
                if (reorderSkuCodeList.contains(s.getCode())) {
                    simpleSkuDTO.setReorderFlag(BooleanEnum.YES.getValue());
                } else {
                    simpleSkuDTO.setReorderFlag(BooleanEnum.NO.getValue());
                }
            }
            //获取默认报价的供应商，赋值采购员
            if (CollUtil.isNotEmpty(dtoList)){
                Optional<QuoteitemDTO> first = dtoList.stream().filter(dto -> Objects.equals(dto.getDefaultFlag(), BooleanEnum.YES.getValue())).findFirst();
                if (first.isPresent() && CollUtil.isNotEmpty(venderMap)) {
                    SimpleVenderResp simpleVenderResp = venderMap.get(first.get().getVenderCode());
                    if (Objects.nonNull(simpleVenderResp)) {
                        simpleSkuDTO.setBuyerIds(simpleVenderResp.getBuyerIds());
                    }
                }
            }
            List<SimpleFile> pictures = s.getPictures();
            if (CollUtil.isEmpty(pictures)) {
                return simpleSkuDTO;
            }
            SimpleFile simpleFile = pictures.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
            if (Objects.nonNull(simpleFile)) {
                simpleSkuDTO.setMainPicture(simpleFile);
            }
            simpleSkuDTO.setCustProFlag(s.getCustProFlag());
            simpleSkuDTO.setMeasureUnit(s.getMeasureUnit());
            simpleSkuDTO.setChangeFlag(s.getChangeFlag());
            simpleSkuDTO.setThumbnail(s.getThumbnail());
            simpleSkuDTO.setBasicSkuCode(s.getBasicSkuCode());
            return simpleSkuDTO;
        }).stream().filter(Objects::nonNull).toList();
    }

    default List<SkuRespVO> convertSkuRespVOList(List<SkuDO> skuDOList, List<QuoteitemDTO> quoteitemDTOList, Map<String, String> custNameCache,Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap,Map<Long, String> userDeptCache) {
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        return CollectionUtils.convertList(skuDOList, s -> {

            if (CollUtil.isEmpty(quoteitemDTOList)) {
                return convertSkuRespVO(s, null, custNameCache,brandSimpleRespVOMap,userDeptCache);
            }
            Map<Long, QuoteitemDTO> quoteitemDTOMap = quoteitemDTOList.stream().filter(quoteitemDTO -> BooleanEnum.YES.getValue().equals(quoteitemDTO.getDefaultFlag()) && s.getCode().equals(quoteitemDTO.getSkuCode())).collect(Collectors.toMap(QuoteitemDTO::getSkuId, quoteitemDTO -> quoteitemDTO));
            if (CollUtil.isEmpty(quoteitemDTOMap)) {
                return convertSkuRespVO(s, null, custNameCache,brandSimpleRespVOMap,userDeptCache);
            }
            return convertSkuRespVO(s, quoteitemDTOMap.get(s.getId()), custNameCache,brandSimpleRespVOMap,userDeptCache);
        });
    }

    default PageResult<SkuRespVO> convertSkuRespVOPageResult(PageResult<SkuDO> pageResult, List<QuoteitemDTO> quoteitemDTOList, Map<String, String> custNameCache,Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap,Map<Long, String> userDeptCache, Map<Long, HsdataDO> hsCodeMap) {
        List<SkuDO> skuDOList = pageResult.getList();
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        List<SkuRespVO> skuRespVOList = convertSkuRespVOList(skuDOList, quoteitemDTOList, custNameCache,brandSimpleRespVOMap,userDeptCache);
        if (CollUtil.isEmpty(skuRespVOList)) {
            return null;
        }
        // 填充HS编码
        if (CollUtil.isNotEmpty(hsCodeMap)) {
            skuRespVOList.forEach(skuRespVO -> {
                Long hsCodeId = skuRespVO.getHsCodeId();
                if (hsCodeId != null) {
                    HsdataDO hsdataDO = hsCodeMap.get(hsCodeId);
                    if(Objects.nonNull(hsdataDO)){
                        skuRespVO.setHsCode(hsdataDO.getCode());
                        skuRespVO.setTaxRefundRate(hsdataDO.getTaxRefundRate());
                    }
                }
            });
        }
        return new PageResult<>(skuRespVOList, pageResult.getTotal());
    }

    List<CskuRespVO> convertCskuRespVOBySkuRespVO(List<SkuRespVO> skuRespVOList);

    default PageResult<CskuRespVO> convertCskuRespVOPageResult(PageResult<SkuRespVO> skuRespVOPageResult) {
        List<SkuRespVO> skuRespVOList = skuRespVOPageResult.getList();
        if (CollUtil.isEmpty(skuRespVOList)) {
            return null;
        }
        List<CskuRespVO> cskuRespVOList = convertCskuRespVOBySkuRespVO(skuRespVOList);
        return new PageResult<CskuRespVO>().setList(cskuRespVOList).setTotal(skuRespVOPageResult.getTotal());
    }

    default Map<String, List<SimpleSkuDTO>> convertSimpleSkuDTOMap(List<SkuBomDO> skuBomList, List<SkuDO> skuDOList, Map<String, List<QuoteitemDTO>> quoteItemMap, Map<Long, String> codeIdMap) {
        if (CollUtil.isEmpty(skuBomList) || CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        Map<Long, SkuDO> skuDOMap = CollectionUtils.convertMap(skuDOList, SkuDO::getId);
        Map<String, List<SimpleSkuDTO>> result = new HashMap<>();
        Map<Long, List<SimpleSkuDTO>> simpleSkuDTOMap = skuBomList.stream().collect(Collectors.groupingBy(
                SkuBomDO::getParentSkuId,
                Collectors.mapping(skuBomDO -> {
                    Long skuId = skuBomDO.getSkuId();
                    String skuCode = skuBomDO.getSkuCode();
                    SkuDO skuDO = skuDOMap.get(skuId);
                    List<QuoteitemDTO> quoteitemDTOList = null;
                    if (CollUtil.isNotEmpty(quoteItemMap)) {
                        quoteitemDTOList = quoteItemMap.get(skuCode);
                    }
                    SimpleSkuDTO simpleSkuDTO = convertSimpleSkuDTO(convertSimpleSkuDO(skuDO), quoteitemDTOList);
                    simpleSkuDTO.setSkuName(skuDO.getName());
                    simpleSkuDTO.setSonSkuCount(skuBomDO.getQty());
                    simpleSkuDTO.setCskuCode(skuDO.getCskuCode());
                    simpleSkuDTO.setCustCode(skuDO.getCustCode());
                    simpleSkuDTO.setSubQutity(skuBomDO.getQty());

                    List<SimpleFile> pictures = skuDO.getPicture();
                    if (CollUtil.isNotEmpty(pictures)) {
                        SimpleFile simpleFile = pictures.stream().filter(p -> BooleanEnum.YES.getValue().equals(p.getMainFlag())).findFirst().orElse(null);
                        if (Objects.nonNull(simpleFile)) {
                            simpleSkuDTO.setMainPicture(simpleFile);
                        }
                    }

                    return simpleSkuDTO;
                }, Collectors.toList())
        ));
        if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
            simpleSkuDTOMap.forEach((k, v) -> {
                if (CollUtil.isNotEmpty(v)) {
                    String code = codeIdMap.get(k);
                    result.put(code, v);
                }
            });
        }
        return result;
    }

    SkuInfoRespVO convertSkuInfoRespVO(SkuInfoSaveReqVO saveReqVO);

    List<SimpleSkuDTO> convertSimpleSkuDTOList(List<SkuDO> skuDOList);
}
