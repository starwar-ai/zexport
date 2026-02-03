package com.syj.eplus.module.home.service.hometab;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.card.CardApi;
import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabPageReqVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabRespVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabSaveReqVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabSortReqVO;
import com.syj.eplus.module.home.convert.hometab.HomeTabConvert;
import com.syj.eplus.module.home.dal.dataobject.hometab.HomeTabDO;
import com.syj.eplus.module.home.dal.mysql.hometab.HomeTabMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.UNASSIGNED_ROLE;
import static com.syj.eplus.module.home.ErrorCodeConstants.HOME_TAB_NOT_EXISTS;

/**
 * 系统首页 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class HomeTabServiceImpl implements HomeTabService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private HomeTabMapper homeTabMapper;

    @Resource
    private CardApi cardApi;

    @Resource
    private PermissionApi permissionApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHomeTab(HomeTabSaveReqVO createReqVO) {
        HomeTabDO homeTab = HomeTabConvert.INSTANCE.convert(createReqVO);
        // 插入
        homeTabMapper.insert(homeTab);
        // 返回
        return homeTab.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHomeTab(HomeTabSaveReqVO updateReqVO) {
        // 校验存在
        validateHomeTabExists(updateReqVO.getId());
        // 更新
        HomeTabDO updateObj = HomeTabConvert.INSTANCE.convert(updateReqVO);
        homeTabMapper.updateById(updateObj);
    }

    @Override
    public void deleteHomeTab(Long id) {
        // 校验存在
        validateHomeTabExists(id);
        // 删除
        homeTabMapper.deleteById(id);
        // 删除对应卡片
        cardApi.deleteCardByTabId(id);
    }

    private void validateHomeTabExists(Long id) {
        if (homeTabMapper.selectById(id) == null) {
            throw exception(HOME_TAB_NOT_EXISTS);
        }
    }

    @Override
    public HomeTabRespVO getHomeTab(Long id) {
        HomeTabDO homeTabDO = homeTabMapper.selectById(id);
        if (homeTabDO == null) {
            return null;
        }
        return HomeTabConvert.INSTANCE.convert(homeTabDO);
    }

    @Override
    public PageResult<HomeTabDO> getHomeTabPage(HomeTabPageReqVO pageReqVO) {
        return homeTabMapper.selectPage(pageReqVO);
    }

    @Override
    public List<HomeTabRespVO> getHomeTabList(Long userId, Long roleId) {
        List<HomeTabDO> homeTabDOList;
        // 获取当前用户id
        if (Objects.nonNull(userId)) {
            homeTabDOList = homeTabMapper.selectList(new LambdaQueryWrapperX<HomeTabDO>().eq(HomeTabDO::getUserId, userId).orderByAsc(HomeTabDO::getSort));
            if (CollUtil.isNotEmpty(homeTabDOList)) {
                return HomeTabConvert.INSTANCE.convertList(homeTabDOList);
            }
        }
        List<Long> roleIdList = new ArrayList<>();
        if (Objects.nonNull(roleId)) {
            roleIdList.add(roleId);
        } else {
            // 用户未配置则查找角色
            roleIdList = permissionApi.getRoleIdListByUserId(userId);
            if (CollUtil.isEmpty(roleIdList)) {
                throw exception(UNASSIGNED_ROLE);
            }
        }
        if (CollUtil.isEmpty(roleIdList)) {
            throw exception(UNASSIGNED_ROLE);
        }
        homeTabDOList = homeTabMapper.selectList(new LambdaQueryWrapperX<HomeTabDO>().in(HomeTabDO::getRoleId, roleIdList).orderByAsc(HomeTabDO::getSort));
        if (CollUtil.isNotEmpty(homeTabDOList)) {
            return HomeTabConvert.INSTANCE.convertList(homeTabDOList);
        }
        return List.of();
    }

    @Override
    public void sort(HomeTabSortReqVO sortReqVO) {
        List<Long> tabIds = sortReqVO.getTabIds();
        List<HomeTabDO> homeTabDOList = new ArrayList<>();
        for (int i = 0; i < tabIds.size(); i++) {
            homeTabDOList.add(new HomeTabDO().setId(tabIds.get(i)).setSort(i));
        }
        homeTabMapper.updateBatch(homeTabDOList);
    }

    @Override
    public List<CardRespDTO> getHomeTabCardList(Long id) {
        return cardApi.getCardListByTabId(id);
    }

    @Override
    public List<Long> getCardListByRole(Long id) {
        return cardApi.getCardIdListByRoleId(id);
    }

}