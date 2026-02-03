package com.syj.eplus.module.infra.service.card;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardPageReqVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardPositionUpdateReqVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardRespVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 首页卡片 Service 接口
 *
 * @author du
 */
public interface CardService {

    /**
     * 创建首页卡片
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCard(@Valid CardSaveReqVO createReqVO);

    /**
     * 更新首页卡片
     *
     * @param updateReqVO 更新信息
     */
    void updateCard(@Valid CardSaveReqVO updateReqVO);

    /**
     * 删除首页卡片
     *
     * @param id 编号
     */
    void deleteCard(Long id);

    /**
     * 获得首页卡片
     *
     * @param id 编号
     * @return 首页卡片
     */
    CardRespVO getCard(Long id);

    /**
     * 获得首页卡片分页
     *
     * @param pageReqVO 分页查询
     * @return 首页卡片分页
     */
    PageResult<CardDO> getCardPage(CardPageReqVO pageReqVO);

    /**
     * 启用首页卡片
     *
     * @param id 卡片主键
     */
    void enableCard(Long id);

    /**
     * 禁用首页卡片
     *
     * @param id 卡片主键
     */
    void disEnableCard(Long id);

    /**
     * 获得首页卡片列表
     *
     * @return 首页卡片列表
     */
    List<CardRespVO> getCardList(List<Long> ids);

    /**
     * 更改卡片位置
     *
     * @param updateVo 卡片位置
     */
    void updateCardPosition(CardPositionUpdateReqVO updateVo);

    /**
     * 获得首页卡片列表
     *
     * @return 首页卡片列表
     */
    List<CardRespVO> getCardInfoList(Long tabId);
}