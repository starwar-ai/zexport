package cn.iocoder.yudao.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.UserMobileVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.*;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.syj.eplus.framework.common.entity.JsonSetPreference;
import com.syj.eplus.framework.common.entity.JsonTimePreference;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wechat.entity.user.WechatUserExcelVO;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

/**
 * 后台用户 Service 接口
 *
 * @author 芋道源码
 */
public interface AdminUserService {

    /**
     * 创建用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createUser(@Valid UserSaveReqVO createReqVO);

    /**
     * 修改用户
     *
     * @param updateReqVO 用户信息
     */
    void updateUser(@Valid UserSaveReqVO updateReqVO);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id      用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 修改用户个人信息
     *
     * @param id    用户编号
     * @param reqVO 用户个人信息
     */
    void updateUserProfile(Long id, @Valid UserProfileUpdateReqVO reqVO);

    /**
     * 修改用户个人密码
     *
     * @param id    用户编号
     * @param reqVO 更新用户个人密码
     */
    void updateUserPassword(Long id, @Valid UserProfileUpdatePasswordReqVO reqVO);

    /**
     * 更新用户头像
     *
     * @param id         用户 id
     * @param avatarFile 头像文件
     */
    String updateUserAvatar(Long id, InputStream avatarFile) throws Exception;

    /**
     * 修改密码
     *
     * @param id       用户编号
     * @param password 密码
     */
    void updateUserPassword(Long id, String password);

    /**
     * 修改状态
     *
     * @param id     用户编号
     * @param status 状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 修改设置偏好
     *
     * @param setPreferencesReqVO 设置偏好
     */
    void updateLoginUserSetPreferences(SetPreferencesReqVO setPreferencesReqVO);

    /**
     * 获取设置偏好
     */
    JsonSetPreference getLoginUserSetPreferences(String pageKey,Integer tabIndex);

    /**
     * 修改时间偏好
     *
     * @param jsonTimePreferenceList 时间偏好
     */
    void updateLoginUserTimePreferences(List<JsonTimePreference> jsonTimePreferenceList);

    /**
     * 获取设置偏好
     */
    List<JsonTimePreference> getLoginUserTimePreferences();

    /**
     * 删除用户
     *
     * @param id 用户编号
     */
    void deleteUser(Long id);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    AdminUserDO getUserByUsername(String username);

    /**
     * 通过手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    AdminUserDO getUserByMobile(String mobile);

    /**
     * 获得用户分页列表
     *
     * @param reqVO 分页条件
     * @return 分页列表
     */
    PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO,Boolean containsAdminFlag);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserDO getUser(Long id);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 获得用户列表
     *
     * @param ids 用户编号数组
     * @return 用户列表
     */
    List<AdminUserDO> getUserList(Collection<Long> ids);

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserDO> getUserMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return CollectionUtils.convertMap(getUserList(ids), AdminUserDO::getId);
    }

    /**
     * 获得用户列表，基于昵称模糊匹配
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    List<AdminUserDO> getUserListByNickname(String nickname);

    /**
     * 批量导入用户
     *
     * @param importUsers     导入用户列表
     * @param isUpdateSupport 是否支持更新
     * @return 导入结果
     */
    UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport);

    /**
     * 获得指定状态的用户们
     *
     * @param status 状态
     * @return 用户们
     */
    List<AdminUserDO> getUserListByStatus(Integer status);
    List<AdminUserDO> getAllUserList();

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 获取用户id，name的缓存
     *
     * @return
     */
    Map<Long, String> getUserIdNameCache();

    /**
     * 获取用户银行信息模板基础数据
     *
     * @return
     */
    List<UserBankImportExcelVO> getUserBankList();

    /**
     * 批量导入用户银行信息
     *
     * @param importUsers 导入用户列表
     * @return 导入结果
     */
    List<UserBankExportVO> importUserBankList(List<UserBankImportExcelVO> importUsers);

    /**
     * 更新用户银行账户信息
     */
    void UpdateUserBank(String mobile, String bank, String bankAccount, String bankAddress, String bankPoc, String bankCode);

    /**
     * 根据部门id列表获取用户id列表
     *
     * @param deptIdList
     * @return
     */
    List<Long> getUserIdByDeptIdList(Set<Long> deptIdList);

    /**
     * 获取模块间业务员缓存
     *
     * @return
     */
    Map<Long, UserDept> getUserDeptCache(Long id);

    Map<Long, UserDept> getUserDeptListCache(List<Long> id);

    /**
     * 获取模块间业务员精简信息
     *
     * @return
     */
    UserDept getUserDeptByUserId(Long id);

    /**
     * 根据企微id获取系统用户id
     *
     * @param wechatUserId 企微用户id
     * @return 系统用户
     */
    AdminUserDO getAdminUserIdByWechatUserId(String wechatUserId);

    /**
     * 企微用户id插入admin_users表
     */
    String insertWechatUserId();

    /**
     * 获取用户银行信息
     *
     * @return 用户银行信息
     */
    Map<Long, UserBackAccountDTO> getUserBackAccountDTOMap(Long userId);

    /**
     * 获取所有用户信息
     *
     * @return 用户信息列表
     */
    List<UserMobileVO> getAllUserMobileList();

    /**
     * 导入企微用户id
     *
     * @param list
     */
    String importWechatUserIdExcel(List<WechatUserExcelVO> list);

    /**
     * 获取导入用户模板信息
     *
     * @return 导入用户模板信息
     */
    List<UserImportExcelVO> getImportTemplate();

    /**
     * 通过第三方id获取用户精简信息
     *
     * @param id
     * @return
     */
    UserDept getUserDeptBySocialUserId(String id);

    List<AdminUserDO> getUserListByUserCodeList(List<String> codeList);

    AdminUserDO getUserById(Long loginUserId);

    /**
     * 批量替换离职人员为上级领导
     * @param userIds 用户id列表
     * @return  替换结果
     */
    Set<Long> getSuperiorLeader(Set<Long> userIds);

    UserDept getUserDeptByUserCode(String userCode);

    /**
     * 根据部门负责人列表获取部门用户id列表
     * @param leaderUserId 部门负责人id
     * @return 部门用户id列表
     */
    Set<Long> getDeptUserIdsByUserId(Long leaderUserId);

    UserDept getUserDeptByUserName(String name);

    Map<String, UserDept> getUserDeptToUserName();
}
