package com.syj.eplus.module.home.service.hometab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabPageReqVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabRespVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabSaveReqVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabSortReqVO;
import com.syj.eplus.module.home.dal.dataobject.hometab.HomeTabDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统首页 Service 接口
 *
 * @author du
 */
public interface HomeTabService {

    /**
     * 创建系统首页
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHomeTab(@Valid HomeTabSaveReqVO createReqVO);

    /**
     * 更新系统首页
     *
     * @param updateReqVO 更新信息
     */
    void updateHomeTab(@Valid HomeTabSaveReqVO updateReqVO);

    /**
     * 删除系统首页
     *
     * @param id 编号
     */
    void deleteHomeTab(Long id);

    /**
     * 获得系统首页
     *
     * @param id 编号
     * @return 系统首页
     */
    HomeTabRespVO getHomeTab(Long id);

    /**
     * 获得系统首页分页
     *
     * @param pageReqVO 分页查询
     * @return 系统首页分页
     */
    PageResult<HomeTabDO> getHomeTabPage(HomeTabPageReqVO pageReqVO);

    /**
     * 获得系统首页列表
     *
     * @return 系统首页列表
     */
    List<HomeTabRespVO> getHomeTabList(Long userId,Long roleId);

    /**
     * 页签排序
     */
    void sort(HomeTabSortReqVO sortReqVO);

    /**
     * 获取卡片列表
     *
     * @param id 页签id
     * @return 页签内卡片列表
     */
    List<CardRespDTO> getHomeTabCardList(Long id);

    List<Long> getCardListByRole(Long id);
}