package com.syj.eplus.module.infra.api.card;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;
import com.syj.eplus.module.infra.convert.card.CardConvert;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;
import com.syj.eplus.module.infra.dal.dataobject.permission.RoleCard;
import com.syj.eplus.module.infra.dal.mysql.card.CardMapper;
import com.syj.eplus.module.infra.dal.mysql.permission.RoleCardMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页卡片 API 实现类
 *
 * @author du
 */
@Service
public class CardApiImpl implements CardApi {

    @Resource
    private CardMapper cardMapper;

    @Resource
    private RoleCardMapper roleCardMapper;

    @Override
    public List<CardRespDTO> getCardListByTabId(Long tabId) {
        List<CardDO> cardDOList = cardMapper.selectList(new LambdaQueryWrapperX<CardDO>().eq(CardDO::getTabId, tabId));
        if (CollUtil.isEmpty(cardDOList)) {
            return List.of();
        }
        return CardConvert.INSTANCE.convertToDTOList(cardDOList);
    }

    @Override
    public List<Long> getCardIdListByRoleId(Long roleId) {
        List<RoleCard> roleCards = roleCardMapper.selectList(RoleCard::getRoleId, roleId);
        if (CollUtil.isEmpty(roleCards)) {
            return List.of();
        }
        return roleCards.stream().map(RoleCard::getCardId).toList();
    }

    @Override
    public void deleteCardByTabId(Long tabId) {
        cardMapper.delete(CardDO::getTabId, tabId);
    }
}
