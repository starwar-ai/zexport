package com.syj.eplus.module.scm.convert.quoteitem;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.JsonVenderTax;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemRespVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.quoteitem.QuoteItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Mapper
public interface QuoteItemConvert {

    QuoteItemConvert INSTANCE = Mappers.getMapper(QuoteItemConvert.class);

    QuoteItemRespVO convert(QuoteItemDO quoteItemDO);

    default QuoteItemRespVO convertQuoteItemRespVO(QuoteItemDO quoteItemDO) {
        QuoteItemRespVO quoteItemRespVO = convert(quoteItemDO);
        return quoteItemRespVO;
    }
    default List<QuoteItemRespVO> convertQuoteItemRespVOList(List<QuoteItemDO> quoteItemDOList){
        return quoteItemDOList.stream().map(this::convert).collect(Collectors.toList());
    }

    QuoteItemDO convertQuoteItemDO(QuoteItemSaveReqVO saveReqVO);

    List<QuoteItemDO> convertQuoteItenDOList(List<QuoteItemSaveReqVO> quoteItemSaveReqVOList);

    List<QuoteItemSaveReqVO> convertQuoteItemSaveReqVOList(List<QuoteitemDTO> quoteitemDTOList);

    default List<QuoteitemDTO> convertQuoteItemDTOListById(List<QuoteItemDO> quoteItemDOList, Map<String, SimpleVenderRespDTO> venderMap){
        List<QuoteitemDTO> quoteItemDOS = BeanUtils.toBean(quoteItemDOList, QuoteitemDTO.class);
        if(CollUtil.isEmpty(venderMap)){
            return List.of();
        }
           return quoteItemDOS.stream().filter(s->venderMap.containsKey(s.getVenderCode())).map(quote->{
                String venderCode = quote.getVenderCode();
                if(StrUtil.isEmpty(venderCode)){
                    return quote;
                }
                SimpleVenderRespDTO simpleVenderRespDTO = venderMap.get(venderCode);
                if(Objects.isNull(simpleVenderRespDTO)){
                    return quote;
                }
                quote.setBuyers(simpleVenderRespDTO.getBuyers());
               List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
               if (CollUtil.isNotEmpty(taxMsg)){
                   taxMsg.stream().filter(s-> BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                       quote.setTaxType(s.getTaxType());
                   });
                   quote.setTaxMsg(taxMsg);
               }
                quote.setVenderId(simpleVenderRespDTO.getId());
                quote.setVenderCode(simpleVenderRespDTO.getCode());
                quote.setVenderName(simpleVenderRespDTO.getName());
                return quote;
            }).toList();
    }


    default List<QuoteitemDTO> convertQuoteItemDTOListByCode(List<QuoteItemDO> quoteItemDOList, Map<String, SimpleVenderRespDTO> venderMap){
        List<QuoteitemDTO> quoteItemDOS = BeanUtils.toBean(quoteItemDOList, QuoteitemDTO.class);
        if(CollUtil.isNotEmpty(venderMap))
        {
            quoteItemDOS.forEach(quote->{
                String venderCode = quote.getVenderCode();
                if(Objects.nonNull(venderCode)){
                    SimpleVenderRespDTO simpleVenderRespDTO = venderMap.get(venderCode);
                    if(Objects.nonNull(simpleVenderRespDTO)){
//                        quote.setPaymentId(simpleVenderRespDTO.getPaymentId());
//                        quote.setPaymentName(simpleVenderRespDTO.getPaymentName());
                        quote.setVenderId(simpleVenderRespDTO.getId());
                        List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                        if (CollUtil.isNotEmpty(taxMsg)){
                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                quote.setTaxType(s.getTaxType());
                            });
                        }
                        quote.setVenderId(simpleVenderRespDTO.getId());
                    }
                }
            });
        }
        return  quoteItemDOS;
    }

    List<QuoteItemDO> convertQuoteItemDOByDTOList(List<QuoteitemDTO> quoteItemDOList);

    List<QuoteitemDTO> convertQuoteItemDTOByDOList(List<QuoteItemDO> quoteItemDOList);

}