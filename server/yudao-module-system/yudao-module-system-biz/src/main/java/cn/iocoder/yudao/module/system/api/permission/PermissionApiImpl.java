package cn.iocoder.yudao.module.system.api.permission;

import cn.iocoder.yudao.module.infra.api.table.DataBaseTableApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.api.permission.dto.FieldPermissionDTO;
import cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权限 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PermissionApiImpl implements PermissionApi {

    @Resource
    private PermissionService permissionService;
    @Resource
    private DataBaseTableApi dataBaseTableApi;

    @Override
    public Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
        return permissionService.getUserRoleIdListByRoleId(roleIds);
    }

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        return permissionService.hasAnyPermissions(userId, permissions);
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        return permissionService.hasAnyRoles(userId, roles);
    }

    @Override
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        return permissionService.getDeptDataPermission(userId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.FIELD_PERMISSION, key = "#userId")
    public List<FieldPermissionDTO> getFieldPermission(Long userId) {
        return permissionService.getNoPermissionFields(userId);
    }

    @Override
    public List<FieldPermissionDTO> getNonPermissionFieldByRoleId(Long roleId) {
        return permissionService.getNoPermissionFieldsByRoleId(roleId);
    }

    @Override
    public List<Long> getUserIdByDeptIdList(Set<Long> deptIdList) {
        return permissionService.getUserIdByDeptIdList(deptIdList);
    }

    @Override
    public Set<Long> getDeptUserIdsByUserId(Long leaderUserId) {
        if (Objects.isNull(leaderUserId)){
            return Set.of();
        }
        return permissionService.getDeptUserIdsByUserId(leaderUserId);
    }

    @Override
    public List<Long> getRoleIdListByUserId(Long userId) {
        return new ArrayList<>(permissionService.getUserRoleIdListByUserId(userId));
    }

}
