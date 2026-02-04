package com.syj.eplus.module.infra.service.card;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import com.syj.eplus.module.infra.controller.admin.card.vo.*;
import com.syj.eplus.module.infra.convert.card.CardConvert;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;
import com.syj.eplus.module.infra.dal.dataobject.permission.RoleCard;
import com.syj.eplus.module.infra.dal.mysql.card.CardMapper;
import com.syj.eplus.module.infra.dal.mysql.permission.RoleCardMapper;
import com.syj.eplus.module.infra.enums.ErrorCodeConstants;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.UNASSIGNED_ROLE;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.*;


/**
 * 首页卡片 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CardServiceImpl implements CardService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CardMapper cardMapper;

    @Resource
    private RoleCardMapper roleCardMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCard(CardSaveReqVO createReqVO) {
        CardDO card = CardConvert.INSTANCE.convertCardDO(createReqVO);
        card.setStatus(BooleanEnum.YES.getValue());
        // 插入
        cardMapper.insert(card);
        // 返回
        return card.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCard(CardSaveReqVO updateReqVO) {
        // 校验存在
        CardDO cardDO = validateCardExists(updateReqVO.getId());
        // 更新
        CardDO updateObj = CardConvert.INSTANCE.convertCardDO(updateReqVO);
        // 原则上启停只能通过启停按钮操作
        updateObj.setStatus(cardDO.getStatus());
        cardMapper.updateById(updateObj);
    }

    @Override
    public void deleteCard(Long id) {
        // 校验存在
        validateCardExists(id);
        // 删除
        cardMapper.deleteById(id);
    }

    private CardDO validateCardExists(Long id) {
        CardDO cardDO = cardMapper.selectById(id);
        if (cardDO == null) {
            throw ServiceExceptionUtil.exception(CARD_NOT_EXISTS);
        }
        return cardDO;
    }

    @Override
    public CardRespVO getCard(Long id) {
        CardDO cardDO = cardMapper.selectById(id);
        if (cardDO == null) {
            return null;
        }
        return CardConvert.INSTANCE.convertCardRespVO(cardDO);
    }

    @Override
    public PageResult<CardDO> getCardPage(CardPageReqVO pageReqVO) {
        return cardMapper.selectPage(pageReqVO);
    }

    @Override
    public void enableCard(Long id) {
        validateCardStatus(id, BooleanEnum.YES.getValue());
        cardMapper.updateById(new CardDO().setId(id).setStatus(BooleanEnum.YES.getValue()));
    }

    @Override
    public void disEnableCard(Long id) {
        validateCardStatus(id, BooleanEnum.NO.getValue());
        cardMapper.updateById(new CardDO().setId(id).setStatus(BooleanEnum.NO.getValue()));
    }

    @Override
    public List<CardRespVO> getCardList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw ServiceExceptionUtil.exception(UNASSIGNED_ROLE);
        }
        List<RoleCard> roleCards = roleCardMapper.selectListByRoleId(ids);
        if (CollUtil.isEmpty(roleCards)) {
            return null;
        }
        List<CardDO> cardDOS = cardMapper.selectList(CardDO::getId, roleCards.stream().map(RoleCard::getCardId).toList());
        return CardConvert.INSTANCE.convertCardRespVOList(cardDOS);
    }

    @Override
    public void updateCardPosition(CardPositionUpdateReqVO updateVo) {
        if (Objects.isNull(updateVo)) {
            return;
        }
        List<CardPosition> cardPositions = updateVo.getCardPositions();
        if (CollUtil.isEmpty(cardPositions)) {
            return;
        }
        List<CardDO> cardDOList = cardPositions.stream().map(s -> BeanUtils.toBean(s, CardDO.class)).toList();
        cardMapper.updateBatch(cardDOList);
    }

    @Override
    public List<CardRespVO> getCardInfoList(Long tabId) {
        if (Objects.isNull(tabId)) {
            throw ServiceExceptionUtil.exception(PARAM_ERROR);
        }
        List<CardDO> cardDOList = cardMapper.selectList(CardDO::getTabId, tabId);
        if (CollUtil.isEmpty(cardDOList)) {
            return List.of();
        }
        return CardConvert.INSTANCE.convertCardRespVOList(cardDOList);
    }

    private void validateCardStatus(Long id, Integer status) {
        CardDO cardDO = cardMapper.selectById(id);
        if (Objects.isNull(cardDO)) {
            throw ServiceExceptionUtil.exception(CARD_NOT_EXISTS);
        }
        // 启用
        if (BooleanEnum.YES.getValue().intValue() == status.intValue() && BooleanEnum.YES.getValue().intValue() == cardDO.getStatus().intValue()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CARD_ENABLE);
        }
        // 禁用
        if (BooleanEnum.NO.getValue().intValue() == status.intValue() && BooleanEnum.NO.getValue().intValue() == cardDO.getStatus().intValue()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CARD_DISABLE);
        }
    }
}