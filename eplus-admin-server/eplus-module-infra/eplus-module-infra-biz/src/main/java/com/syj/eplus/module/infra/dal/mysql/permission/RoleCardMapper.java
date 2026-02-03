package com.syj.eplus.module.infra.dal.mysql.permission;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.infra.dal.dataobject.permission.RoleCard;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/20/11:25
 * @Description:
 */
@Mapper
public interface RoleCardMapper extends BaseMapperX<RoleCard> {

    default List<RoleCard> selectListByRoleId(Long roleId) {
        return selectList(RoleCard::getRoleId, roleId);
    }

    default List<RoleCard> selectListByRoleId(Collection<Long> roleIds) {
        return selectList(RoleCard::getRoleId, roleIds);
    }

    default List<RoleCard> selectListByCardId(Long cardId) {
        return selectList(RoleCard::getCardId, cardId);
    }

    default void deleteListByRoleIdAndCardIds(Long roleId, Collection<Long> cardIds) {
        delete(new LambdaQueryWrapper<RoleCard>()
                .eq(RoleCard::getRoleId, roleId)
                .in(RoleCard::getCardId, cardIds));
    }

    default void deleteListByCardId(Long cardId) {
        delete(new LambdaQueryWrapper<RoleCard>().eq(RoleCard::getCardId, cardId));
    }

    default void deleteListByRoleId(Long roleId) {
        delete(new LambdaQueryWrapper<RoleCard>().eq(RoleCard::getRoleId, roleId));
    }
}
