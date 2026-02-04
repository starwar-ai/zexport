package com.syj.eplus.module.infra.api.card;

import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;

import java.util.List;

/**
 * 首页卡片 API 接口
 *
 * @author du
 */
public interface CardApi {

    /**
     * 根据页签ID获取卡片列表
     *
     * @param tabId 页签ID
     * @return 卡片列表
     */
    List<CardRespDTO> getCardListByTabId(Long tabId);

    /**
     * 根据角色ID获取卡片ID列表
     *
     * @param roleId 角色ID
     * @return 卡片ID列表
     */
    List<Long> getCardIdListByRoleId(Long roleId);

    /**
     * 根据页签ID删除卡片
     *
     * @param tabId 页签ID
     */
    void deleteCardByTabId(Long tabId);
}
