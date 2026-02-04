package com.syj.eplus.module.infra.service.permission;

import java.util.Set;

public interface PermissionService {

    /**
     * 设置角色卡片
     *
     * @param roleId  角色编号
     * @param cardIds 卡片编号集合
     */
    void assignRoleCard(Long roleId, Set<Long> cardIds);

}
