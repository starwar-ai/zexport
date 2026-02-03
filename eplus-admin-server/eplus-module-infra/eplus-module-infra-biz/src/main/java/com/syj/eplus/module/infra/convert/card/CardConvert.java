package com.syj.eplus.module.infra.convert.card;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardRespVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface CardConvert {

    CardConvert INSTANCE = Mappers.getMapper(CardConvert.class);

    CardRespVO convert(CardDO cardDO);

    default CardRespVO convertCardRespVO(CardDO cardDO) {
        CardRespVO cardRespVO = convert(cardDO);
        return cardRespVO;
    }

    CardDO convertCardDO(CardSaveReqVO saveReqVO);

    List<CardDO> convertCardDOList(List<CardSaveReqVO> cardSaveReqVOList);

    default List<CardRespVO> convertCardRespVOList(List<CardDO> cardDOList) {
        return CollectionUtils.convertList(cardDOList, CardConvert.INSTANCE::convertCardRespVO);
    }

    // ========== API DTO 转换 ==========

    /**
     * 转换为 API DTO
     */
    CardRespDTO convertToDTO(CardDO cardDO);

    /**
     * 批量转换为 API DTO
     */
    default List<CardRespDTO> convertToDTOList(List<CardDO> cardDOList) {
        return CollectionUtils.convertList(cardDOList, CardConvert.INSTANCE::convertToDTO);
    }

    /**
     * SimpleFile 转换为 String (文件 URL)
     */
    default String map(SimpleFile simpleFile) {
        return simpleFile != null ? simpleFile.getFileUrl() : null;
    }
}