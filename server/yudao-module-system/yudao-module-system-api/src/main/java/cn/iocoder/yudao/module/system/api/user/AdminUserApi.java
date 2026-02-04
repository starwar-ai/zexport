package cn.iocoder.yudao.module.system.api.user;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import com.syj.eplus.framework.common.entity.UserDept;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Admin 用户 API 接口
 *
 * @author 芋道源码
 */
public interface AdminUserApi {

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserRespDTO getUser(Long id);

    // TODO @puhui999：这里返回 List<AdminUserRespDTO> 方法名可以改成 getUserListBySubordinate

    /**
     * 通过用户 ID 查询用户下属
     *
     * @param id 用户编号
     * @return 用户下属用户编号列表
     */
    Set<Long> getSubordinateIds(Long id);

    /**
     * 通过用户 ID 查询用户们
     *
     * @param ids 用户 ID 们
     * @return 用户对象信息
     */
    List<AdminUserRespDTO> getUserList(Collection<Long> ids);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserRespDTO> getUserMap(Collection<Long> ids) {
        List<AdminUserRespDTO> users = getUserList(ids);
        return CollectionUtils.convertMap(users, AdminUserRespDTO::getId);
    }

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 获取用户id，name的缓存
     *
     * @return
     */
    Map<Long, String> getUserIdNameCache();

    /**
     * 获取模块间业务员缓存
     *
     * @return
     */
    Map<Long, UserDept> getUserDeptCache(Long id);

    Map<Long, UserDept> getUserDeptListCache(List<Long> id);

    /**
     * 获取模块间业务员缓存
     *
     * @return
     */
    UserDept getUserDeptByUserId(Long id);

    /**
     * 获取用户银行信息
     *
     * @return 用户银行信息
     */
    Map<Long, UserBackAccountDTO> getUserBackAccountDTOMap(Long userId);

    /**
     * 通过用户编号获取下节点部门领导编号
     *
     * @param userId 用户编号
     * @return 下节点部门负责人编号
     */
    Long getNextDeptLeaderIdByUserId(Long userId);

    Map<String, AdminUserRespDTO> getUserMapByStringIdList(List<String> userIdList);

    Map<Long, AdminUserRespDTO> getUserMapByIdList(List<Long> userIdList);

    Map<String, AdminUserRespDTO> getUserListByUserCodeList(List<String> codeList);

    /**
     * 批量替换离职人员为上级领导
     * @param userIds 用户id列表
     * @return  替换结果
     */
    Set<Long> getSuperiorLeader(Set<Long> userIds);


    UserDept getUserDeptByUserCode(String userCode);

    UserDept getUserDeptByUserName(String name);

    Map<String, UserDept> getUserDeptToUserName();

}
