package com.syj.eplus.module.infra.service.permission;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.dal.dataobject.permission.RoleCard;
import cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.syj.eplus.module.infra.dal.mysql.permission.RoleCardMapper;
import org.springframework.cache.annotation.CacheEvict;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;


public class PermissionServiceImpl implements PermissionService {

    @Resource
    private RoleCardMapper roleCardMapper;

    @Override
    @DSTransactional
    @CacheEvict(value = RedisKeyConstants.CARD_ROLE_ID_LIST,
            allEntries = true)
    public void assignRoleCard(Long roleId, Set<Long> cardIds) {
        // 获得角色拥有菜单编号
        Set<Long> dbCardIds = convertSet(roleCardMapper.selectListByRoleId(roleId), RoleCard::getCardId);
        // 计算新增和删除的菜单编号
        Set<Long> cardIdList = CollUtil.emptyIfNull(cardIds);
        Collection<Long> createCardIds = CollUtil.subtract(cardIdList, dbCardIds);
        Collection<Long> deleteCardIds = CollUtil.subtract(dbCardIds, cardIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createCardIds)) {
            roleCardMapper.insertBatch(CollectionUtils.convertList(createCardIds, cardId -> {
                RoleCard entity = new RoleCard();
                entity.setRoleId(roleId);
                entity.setCardId(cardId);
                return entity;
            }));
        }
        if (CollUtil.isNotEmpty(deleteCardIds)) {
            roleCardMapper.deleteListByRoleIdAndCardIds(roleId, deleteCardIds);
        }
    }
}
