package com.syj.eplus.module.infra.dal.mysql.card;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首页卡片 Mapper
 *
 * @author du
 */
@Mapper
public interface CardMapper extends BaseMapperX<CardDO> {

    default PageResult<CardDO> selectPage(CardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CardDO>()
                .eqIfPresent(CardDO::getDescription, reqVO.getDescription())
                .eqIfPresent(CardDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CardDO::getBasicFlag,reqVO.getBasicFlag())
                .betweenIfPresent(CardDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CardDO::getId));
    }

}