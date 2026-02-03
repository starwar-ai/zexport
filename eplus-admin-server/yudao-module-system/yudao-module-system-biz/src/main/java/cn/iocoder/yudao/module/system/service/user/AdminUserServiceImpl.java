package cn.iocoder.yudao.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.UserMobileVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.*;
import cn.iocoder.yudao.module.system.convert.user.UserConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.UserPostDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.UserPostMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.dept.PostService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.userprefence.UserPrefenceService;
import com.google.common.annotations.VisibleForTesting;
import com.syj.eplus.framework.common.entity.JsonSetPreference;
import com.syj.eplus.framework.common.entity.JsonTimePreference;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.wechat.entity.token.AccessToken;
import com.syj.eplus.module.wechat.entity.user.WechatUser;
import com.syj.eplus.module.wechat.entity.user.WechatUserExcelVO;
import com.syj.eplus.module.wechat.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 后台用户 Service 实现类
 *
 * @author 芋道源码
 */
@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Value("${sys.user.init-password:yudaoyuanma}")
    private String userInitPassword;

    @Resource
    private AdminUserMapper userMapper;

    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RoleService roleService;

    @Resource
    private UserPrefenceService userPrefenceService;

    @Resource
    private UserPostMapper userPostMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private WechatService wechatService;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    private static final String INITIAL_PASSWORD = "123456";

    private static final Pattern PATTERN = Pattern.compile("^u\\d{5}$");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserSaveReqVO createReqVO) {

        // 校验正确性
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(),
                createReqVO.getMobile(), createReqVO.getEmail(), createReqVO.getDeptId(), createReqVO.getPostIds(), createReqVO.getCode());
        // 插入用户
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(createReqVO.getPassword())); // 加密密码
        user.setCode(codeGeneratorApi.getUserCodeGenerator());
        userMapper.insert(user);
        // 插入关联岗位
        if (CollectionUtil.isNotEmpty(user.getPostIds())) {
            userPostMapper.insertBatch(convertList(user.getPostIds(),
                    postId -> new UserPostDO().setUserId(user.getId()).setPostId(postId)));
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserSaveReqVO updateReqVO) {
        updateReqVO.setPassword(null); // 特殊：此处不更新密码
        // 校验正确性
        validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getMobile(), updateReqVO.getEmail(), updateReqVO.getDeptId(), updateReqVO.getPostIds(), updateReqVO.getCode());
        // 更新用户
        AdminUserDO updateObj = BeanUtils.toBean(updateReqVO, AdminUserDO.class);
        userMapper.updateById(updateObj);
        // 更新岗位
        updateUserPost(updateReqVO, updateObj);
    }

    private void updateUserPost(UserSaveReqVO reqVO, AdminUserDO updateObj) {
        Long userId = reqVO.getId();
        Set<Long> dbPostIds = convertSet(userPostMapper.selectListByUserId(userId), UserPostDO::getPostId);
        // 计算新增和删除的岗位编号
        Set<Long> postIds = CollUtil.emptyIfNull(updateObj.getPostIds());
        Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIds);
        Collection<Long> deletePostIds = CollUtil.subtract(dbPostIds, postIds);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (!CollectionUtil.isEmpty(createPostIds)) {
            userPostMapper.insertBatch(convertList(createPostIds,
                    postId -> new UserPostDO().setUserId(userId).setPostId(postId)));
        }
        if (!CollectionUtil.isEmpty(deletePostIds)) {
            userPostMapper.deleteByUserIdAndPostId(userId, deletePostIds);
        }
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userMapper.updateById(new AdminUserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        // 执行更新
        userMapper.updateById(BeanUtils.toBean(reqVO, AdminUserDO.class).setId(id));
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        AdminUserDO updateObj = new AdminUserDO().setId(id);
        updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // 加密密码
        userMapper.updateById(updateObj);
    }

    @Override
    public String updateUserAvatar(Long id, InputStream avatarFile) {
        validateUserExists(id);
        // 存储文件
        String avatar = fileApi.createFile(IoUtil.readBytes(avatarFile));
        // 更新路径
        AdminUserDO sysUserDO = new AdminUserDO();
        sysUserDO.setId(id);
        sysUserDO.setAvatar(avatar);
        userMapper.updateById(sysUserDO);
        return avatar;
    }

    @Override
    public void updateUserPassword(Long id, String password) {
        // 校验用户存在
        validateUserExists(id);
        // 更新密码
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // 加密密码
        userMapper.updateById(updateObj);
    }

    @Override
    @DataPermission(enable = false)
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户存在
        validateUserExists(id);
        // 更新状态
        AdminUserDO updateObj = new AdminUserDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        userMapper.updateById(updateObj);
    }
    //优化->偏好设置独立成表
    @Override
    public void updateLoginUserSetPreferences(SetPreferencesReqVO setPreferencesReqVO) {
        JsonSetPreference jsonSetPreference = setPreferencesReqVO.getSetPreference();
        if (jsonSetPreference.getPageKey() == null) {
            throw exception(PAGEKEY_NOT_NULL);
        }
        if (jsonSetPreference.getTabIndex() == null) {
            throw exception(TABINDEX_NOT_NULL);
        }
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserDO adminUserDO = userMapper.selectById(loginUserId);
        if (adminUserDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        userPrefenceService.updateListByUser(loginUserId,setPreferencesReqVO.getSetPreference());

    }

//    @Override
//    public void updateLoginUserSetPreferences(SetPreferencesReqVO setPreferencesReqVO) {
//        JsonSetPreference jsonSetPreference = setPreferencesReqVO.getSetPreference();
//        if (jsonSetPreference.getPageKey() == null) {
//            throw exception(PAGEKEY_NOT_NULL);
//        }
//        if (jsonSetPreference.getTabIndex() == null) {
//            throw exception(TABINDEX_NOT_NULL);
//        }
//        Long loginUserId = WebFrameworkUtils.getLoginUserId();
//        AdminUserDO adminUserDO = userMapper.selectById(loginUserId);
//        if (adminUserDO == null) {
//            throw exception(USER_NOT_EXISTS);
//        }
//        List<JsonSetPreference> setPreferencesList = adminUserDO.getPreferences();
//        //用户没有该配置，新增
//        if(setPreferencesList == null || setPreferencesList.isEmpty()){
//            setPreferencesList = new ArrayList<>();
//            setPreferencesList.add(jsonSetPreference);
//            adminUserDO.setPreferences(setPreferencesList);
//        }else {
//            Optional<JsonSetPreference> first = setPreferencesList.stream().filter(s -> Objects.equals(s.getTabIndex(), jsonSetPreference.getTabIndex())
//                    && Objects.equals(s.getPageKey(), jsonSetPreference.getPageKey())).findFirst();
//            //改配置不存在
//            if(first.isEmpty()){
//                setPreferencesList.add(jsonSetPreference);
//                adminUserDO.setPreferences(setPreferencesList);
//            }else{
//                List<JsonSetPreference> finalSetPreferencesListNew = new ArrayList();
//                finalSetPreferencesListNew.add(jsonSetPreference);
//                setPreferencesList.forEach(item -> {
//                    if(Objects.isNull(item.getPageKey()) || Objects.isNull(item.getTabIndex())){
//                        return;
//                    }
//
//                    if (!(item.getPageKey().equals(jsonSetPreference.getPageKey()) && item.getTabIndex().equals(jsonSetPreference.getTabIndex())) ) {
//                        finalSetPreferencesListNew.add(item);
//                    }
//                });
//                adminUserDO.setPreferences(finalSetPreferencesListNew);
//            }
//        }
//        userMapper.updateById(adminUserDO);
//    }



//    @Override
//    public JsonSetPreference getLoginUserSetPreferences(String pageKey,Integer tabIndex) {
//        Long loginUserId = WebFrameworkUtils.getLoginUserId();
//        AdminUserDO adminUserDO = userMapper.selectById(loginUserId);
//        if (adminUserDO == null) {
//            throw exception(USER_NOT_EXISTS);
//        }
//        List<JsonSetPreference> list = adminUserDO.getPreferences();
//        if (CollUtil.isEmpty(list)) {
//            return null;
//        }
//        AtomicReference<JsonSetPreference> jsonSetPreference = new AtomicReference<>();
//        list.forEach(item -> {
//            if (item.getPageKey().equals(pageKey) && item.getTabIndex().equals(tabIndex)) {
//                jsonSetPreference.set(item);
//            }
//        });
//        return jsonSetPreference.get();
//    }

    @Override
    public JsonSetPreference getLoginUserSetPreferences(String pageKey,Integer tabIndex) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserPrefenceDO userPrefenceDO = userPrefenceService.getListByUser(loginUserId, pageKey, tabIndex);
        if(Objects.isNull(userPrefenceDO)){
            return null;
        }
        return BeanUtils.toBean(userPrefenceDO,JsonSetPreference.class);
    }

    @Override
    public void updateLoginUserTimePreferences(List<JsonTimePreference> jsonTimePreferenceList) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserDO adminUserDO = userMapper.selectById(loginUserId);
        if (adminUserDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        adminUserDO.setTimePreferences(jsonTimePreferenceList);
        userMapper.updateById(adminUserDO);
    }


    @Override
    public List<JsonTimePreference> getLoginUserTimePreferences() {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserDO adminUserDO = userMapper.selectById(loginUserId);
        if (adminUserDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        List<JsonTimePreference> list = adminUserDO.getTimePreferences();
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 校验用户存在
        validateUserExists(id);
        // 删除用户
        userMapper.deleteById(id);
        // 删除用户关联数据
        permissionService.processUserDeleted(id);
        // 删除用户岗位
        userPostMapper.deleteByUserId(id);
    }

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public AdminUserDO getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    @DataPermission(enable = false)
    public PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO,Boolean containsAdminFlag) {
        //不包含管理员的情况，查出所有非管理员角色id
        List<Long> userIdList = userMapper.selectList().stream().map(AdminUserDO::getId).distinct().toList();
        if(!containsAdminFlag){
            List<RoleDO> adminRoleIdList = roleService.getRoleList().stream().filter(s -> s.getType() == 1).toList();
            if(CollUtil.isNotEmpty(adminRoleIdList)){
                List<Long> roleIdList = adminRoleIdList.stream().map(RoleDO::getId).distinct().toList();
                Set<Long> adminUserIds = permissionService.getUserIdListByRoleIds(new HashSet<>(roleIdList));
                userIdList = CollUtil.subtractToList(userIdList,adminUserIds);
            }
        }
        return userMapper.selectPage(reqVO, getDeptCondition(reqVO.getDeptId()),userIdList);
    }

    @Override
    @DataPermission(enable = false)
    public AdminUserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectListByDeptIds(deptIds);
    }

    @Override
    public List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds) {
        if (CollUtil.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = convertSet(userPostMapper.selectListByPostIds(postIds), UserPostDO::getUserId);
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectBatchIds(userIds);
    }

    @Override
    @DataPermission(enable = false)
    public List<AdminUserDO> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userMapper.selectBatchIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<AdminUserDO> users = userMapper.selectBatchIds(ids);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users, AdminUserDO::getId);
        // 校验
        ids.forEach(id -> {
            AdminUserDO user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<AdminUserDO> getUserListByNickname(String nickname) {
        return userMapper.selectListByNickname(nickname);
    }

    /**
     * 获得部门条件：查询指定部门的子部门编号们，包括自身
     *
     * @param deptId 部门编号
     * @return 部门编号集合
     */
    private Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) {
            return Collections.emptySet();
        }
        Set<Long> deptIds = convertSet(deptService.getChildDeptList(deptId), DeptDO::getId);
        deptIds.add(deptId); // 包括自身
        return deptIds;
    }

    private void validateUserForCreateOrUpdate(Long id, String username, String mobile, String email,
                                               Long deptId, Set<Long> postIds, String code) {
        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        DataPermissionUtils.executeIgnore(() -> {
            // 校验用户存在
            validateUserExists(id);
            // 校验用户编号唯一
            validateUserCodeUnique(id, code);
            // 校验用户名唯一
            validateUsernameUnique(id, username);
            // 校验手机号唯一
            validateMobileUnique(id, mobile);
            // 校验邮箱唯一
            validateEmailUnique(id, email);
            // 校验部门处于开启状态
            deptService.validateDeptList(CollectionUtils.singleton(deptId));
            // 校验岗位处于开启状态
            postService.validatePostList(postIds);
        });
    }

    @VisibleForTesting
    @DataPermission(enable = false)
    AdminUserDO validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        AdminUserDO user = userMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            return;
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    void validateUserCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        AdminUserDO user = userMapper.selectByUserCode(code);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            return;
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERCODE_EXISTS);
        }
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        AdminUserDO user = userMapper.selectByEmail(email);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            return;
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        AdminUserDO user = userMapper.selectByMobile(mobile);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            return;
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * 校验雁行账户唯一
     *
     * @param mobile
     * @param bankAccount
     */
    @VisibleForTesting
    String validateBankAccountUnique(String mobile, String bankAccount) {
        AtomicReference<String> msg = new AtomicReference<>();
        if (StrUtil.isBlank(bankAccount)) {
            return msg.get();
        }
        List<AdminUserDO> adminUserDOList = userMapper.selectByBankAccount(bankAccount);
        if (CollUtil.isEmpty(adminUserDOList)) {
            msg.set(USER_NOT_EXISTS.getMsg());
        }
        if (adminUserDOList.size() > 1) {
            throw exception(BANKACCOUNT_REPEAT, bankAccount);
        }
        adminUserDOList.stream().findFirst().ifPresent(s -> {
            if (!s.getMobile().equals(mobile)) {
                msg.set(USER_BANK_ACCOUNT_EXISTS.getMsg());
            }
        });
        return msg.get();
    }

    /**
     * 校验旧密码
     *
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport) {
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        UserImportRespVO respVO = UserImportRespVO.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        importUsers.forEach(importUser -> {
            // 校验，判断是否有不符合的原因
            try {
                validateUserForCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null, importUser.getCode());
            } catch (ServiceException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }
            // 判断如果不存在，在进行插入
            AdminUserDO existUser = userMapper.selectByUsername(importUser.getUsername());
            if (existUser == null) {
                userMapper.insert(BeanUtils.toBean(importUser, AdminUserDO.class)
                        .setPassword(encodePassword(userInitPassword)).setPostIds(new HashSet<>())); // 设置默认密码及空岗位编号数组
                respVO.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                respVO.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            AdminUserDO updateUser = BeanUtils.toBean(importUser, AdminUserDO.class);
            updateUser.setId(existUser.getId());
            userMapper.updateById(updateUser);
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<AdminUserDO> getUserListByStatus(Integer status) {
        List<Long> superAdminRoleIdList = roleService.getSuperAdminRoleIdList();
        Set<Long> userIdListByRoleIds = permissionService.getUserIdListByRoleIds(superAdminRoleIdList);
        return userMapper.selectListByStatus(status, userIdListByRoleIds);
    }

    @Override
    public List<AdminUserDO> getAllUserList() {
        List<Long> superAdminRoleIdList = roleService.getSuperAdminRoleIdList();
        Set<Long> userIdListByRoleIds = permissionService.getUserIdListByRoleIds(superAdminRoleIdList);
        return userMapper.selectAllUser(userIdListByRoleIds);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, String> getUserIdNameCache() {
        List<AdminUserDO> adminUserDOList = userMapper.selectAllAdminUser();
        if (CollUtil.isEmpty(adminUserDOList)) {
            return null;
        }
        return adminUserDOList.stream().collect(Collectors.toMap(AdminUserDO::getId, AdminUserDO::getNickname));
    }

    @Override
    public List<UserBankImportExcelVO> getUserBankList() {
        List<AdminUserDO> adminUserDOList = userMapper.selectList();
        if (CollUtil.isEmpty(adminUserDOList)) {
            return null;
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserBankImportExcelVOList(adminUserDOList, deptMap);
    }

    @Override
    public List<UserBankExportVO> importUserBankList(List<UserBankImportExcelVO> importUsers) {
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        List<UserBankExportVO> userBankExportVOList = new ArrayList<>();
        importUsers.forEach(userBankImportExcelVO -> {
            UserBankExportVO userBankExportVO = UserBankExportVO.builder().nickname(userBankImportExcelVO.getNickname()).mobile(userBankImportExcelVO.getMobile()).build();
            String mobile = userBankImportExcelVO.getMobile();
            if (StrUtil.isBlank(mobile)) {
                userBankExportVO.setFailMessage(USER_ID_NOT_NULL.getMsg());
                return;
            }
            String bankAccount = userBankImportExcelVO.getBankAccount();
            //校验银行账号是否已存在
            String validateMsg = validateBankAccountUnique(mobile, bankAccount);
            if (StrUtil.isNotEmpty(validateMsg)) {
                userBankExportVO.setFailMessage(validateMsg);
                return;
            }
            UpdateUserBank(mobile, userBankImportExcelVO.getBank(), bankAccount, userBankImportExcelVO.getBankAddress(), userBankImportExcelVO.getBankPoc(), userBankImportExcelVO.getBankCode());
            if (StrUtil.isNotEmpty(userBankExportVO.getFailMessage())) {
                userBankExportVOList.add(userBankExportVO);
            }
        });
        return userBankExportVOList;
    }

    @Override
    public void UpdateUserBank(String mobile, String bank, String bankAccount, String bankAddress, String bankPoc, String bankCode) {
        userMapper.updateUserBankByMobile(bank, bankAccount, bankAddress, bankPoc, bankCode, mobile);
    }

    @Override
    @DataPermission(enable = false)
    public List<Long> getUserIdByDeptIdList(Set<Long> deptIdList) {
        return userMapper.selectUserIdByDeptIdList(deptIdList);
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, UserDept> getUserDeptCache(Long id) {
        List<AdminUserDO> adminUserDOList;
        if (Objects.isNull(id)) {
            adminUserDOList = userMapper.selectAllAdminUser();
        } else {
            adminUserDOList = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>().eq(AdminUserDO::getId, id));
        }

        if (CollUtil.isEmpty(adminUserDOList)) {
            return null;
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserDept(adminUserDOList, deptMap).stream().collect(Collectors.toMap(UserDept::getUserId, Function.identity()));
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, UserDept> getUserDeptListCache(List<Long> idList) {
        List<AdminUserDO> adminUserDOList;
        if (Objects.nonNull(idList) || idList.isEmpty()) {
            adminUserDOList = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>().inIfPresent(AdminUserDO::getId, idList));
        } else {
            adminUserDOList = null;
        }
        if (CollUtil.isEmpty(adminUserDOList)) {
            return null;
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserDept(adminUserDOList, deptMap).stream().collect(Collectors.toMap(UserDept::getUserId, Function.identity()));
    }

    @Override
    @DataPermission(enable = false)
    public UserDept getUserDeptByUserId(Long id) {
        AdminUserDO adminUserDO = userMapper.selectById(id);
        if (Objects.isNull(adminUserDO)) {
            return null;
        }
        UserDept userDept = BeanUtils.toBean(adminUserDO, UserDept.class);
        userDept.setUserId(adminUserDO.getId());
        userDept.setUserCode(adminUserDO.getCode());
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(Collections.singletonList(adminUserDO), AdminUserDO::getDeptId));
        if (CollUtil.isNotEmpty(deptMap)) {
            DeptDO deptDO = deptMap.get(adminUserDO.getDeptId());
            if (Objects.nonNull(deptDO)) {
                userDept.setDeptName(deptDO.getName());
                userDept.setDeptCode(deptDO.getCode());
            }
        }
        return userDept;
    }

    @Override
    @DataPermission(enable = false)
    public AdminUserDO getAdminUserIdByWechatUserId(String wechatUserId) {
        return userMapper.selectOne(new LambdaQueryWrapperX<AdminUserDO>().select(AdminUserDO::getId, AdminUserDO::getUsername).eq(AdminUserDO::getSocialUserId, wechatUserId));
    }

    @Override
    public String insertWechatUserId() {
        AdminUserDO adminUserDO = userMapper.selectOne(AdminUserDO::getId, WebFrameworkUtils.getLoginUserId());
        if (Objects.isNull(adminUserDO)) {
            return String.format("无效用户userId-%s", WebFrameworkUtils.getLoginUserId());
        }
        String mobile = adminUserDO.getMobile();
        AccessToken accessToken = wechatService.getAccessToken();
        if (Objects.isNull(accessToken)) {
            return "获取企微token失败";
        }
        WechatUser wechatUser = wechatService.getUserIdByMobile(accessToken.getAccess_token(), mobile);
        if (Objects.isNull(wechatUser)) {
            return String.format("企微用户获取失败-token-%s,mobile-%s", accessToken.getAccess_token(), mobile);
        }
        String userid = wechatUser.getUserid();
        int update = userMapper.update(new AdminUserDO().setSocialUserId(userid), new LambdaQueryWrapperX<AdminUserDO>().eq(AdminUserDO::getMobile, mobile));
        if (update > 0) {
            return "企业微信绑定成功";
        }
        return String.format("企业微信绑定失败-wechatId%s", userid);
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, UserBackAccountDTO> getUserBackAccountDTOMap(Long userId) {
        List<AdminUserDO> adminUserDOList = new ArrayList<>();
        if (Objects.nonNull(userId)) {
            AdminUserDO adminUserDO = userMapper.selectById(userId);
            if (Objects.isNull(adminUserDO)) {
                log.error("根据userId-{},未找到用户信息", userId);
                throw exception(USER_NOT_EXISTS);
            }
            adminUserDOList.add(adminUserDO);
        } else {
            adminUserDOList = userMapper.selectAllAdminUser();
        }
        if (CollUtil.isEmpty(adminUserDOList)) {
            return Collections.emptyMap();
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserBankAccountDTO(adminUserDOList).stream().collect(Collectors.toMap(UserBackAccountDTO::getId, Function.identity()));
    }

    @Override
    public List<UserMobileVO> getAllUserMobileList() {
        List<AdminUserDO> adminUserDOList = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>().eq(AdminUserDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
        if (CollUtil.isEmpty(adminUserDOList)) {
            return Collections.emptyList();
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserMobileVO(adminUserDOList, deptMap);
    }

    @Override
    public String importWechatUserIdExcel(List<WechatUserExcelVO> list) {
        if (CollUtil.isEmpty(list)){
            return "导入数据为空";
        }
        if (CollUtil.isNotEmpty(list)) {
            List<AdminUserDO> adminUserDOList = userMapper.selectList();
            Map<String, Long> idCodeMap = adminUserDOList.stream().filter(s -> StrUtil.isNotEmpty(s.getCode())).collect(Collectors.toMap(AdminUserDO::getCode, AdminUserDO::getId));
            List<AdminUserDO> updateList = list.stream().filter(s -> idCodeMap.containsKey(s.getCode())).map(s -> new AdminUserDO().setSocialUserId(s.getSocialId()).setNickname(s.getNickname()).setMobile(s.getMobile()).setId(idCodeMap.get(s.getCode())).setCode(codeGeneratorApi.getUserCodeGenerator())).toList();
            if (CollUtil.isNotEmpty(updateList)) {
                userMapper.updateBatch(updateList);
            }
            // 初始密码加密
            String password = encodePassword(INITIAL_PASSWORD);
            List<AdminUserDO> insertList = list.stream().filter(s -> !idCodeMap.containsKey(s.getCode())).map(s -> new AdminUserDO().setSocialUserId(s.getSocialId()).setNickname(s.getNickname()).setMobile(s.getMobile()).setUsername(s.getSocialId()).setPassword(password).setCode(codeGeneratorApi.getUserCodeGenerator())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                userMapper.insertBatch(insertList);
            }
        }
        return "同步成功";
    }

    @Override
    public List<UserImportExcelVO> getImportTemplate() {

        return null;
    }

    @Override
    public UserDept getUserDeptBySocialUserId(String id) {
        AdminUserDO adminUserDO = userMapper.selectOne(AdminUserDO::getSocialUserId, id);
        if (Objects.isNull(adminUserDO)) {
            return null;
        }
        UserDept userDept = BeanUtils.toBean(adminUserDO, UserDept.class);
        userDept.setUserId(adminUserDO.getId());
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(Collections.singletonList(adminUserDO), AdminUserDO::getDeptId));
        if (CollUtil.isNotEmpty(deptMap)) {
            DeptDO deptDO = deptMap.get(adminUserDO.getDeptId());
            if (Objects.nonNull(deptDO)) {
                userDept.setDeptName(deptDO.getName());
            }
        }
        return userDept;
    }

    @Override
    @DataPermission(enable = false)
    public List<AdminUserDO> getUserListByUserCodeList(List<String> codeList) {
        return userMapper.selectList(AdminUserDO::getCode,codeList);
    }

    @Override
    public AdminUserDO getUserById(Long loginUserId) {
        return userMapper.selectById(loginUserId);
    }

    @Override
    @DataPermission(enable = false)
    public Set<Long> getSuperiorLeader(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)){
            return Set.of();
        }
        List<AdminUserDO> adminUserDOS = userMapper.selectBatchIds(userIds);
        if (CollUtil.isEmpty(adminUserDOS)){
            return Set.of();
        }
        Set<Long> result = new HashSet<>();
        Map<Long, AdminUserDO> statusMap = adminUserDOS.stream().collect(Collectors.toMap(AdminUserDO::getId,s->s));

        userIds.forEach(s->{
            AdminUserDO adminUserDO = statusMap.get(s);
            if (Objects.isNull(adminUserDO)){
                throw exception(USER_NOT_EXISTS);
            }
            if (CommonStatusEnum.ENABLE.getStatus().equals(adminUserDO.getStatus())){
                result.add(s);
                return;
            }
            // 获取上级领导
            Set<Long> parentLeaderUserIds = deptService.getParentLeaderUserIds(adminUserDO.getDeptId());
            if (CollUtil.isNotEmpty(parentLeaderUserIds)){
                result.addAll(parentLeaderUserIds);
            }
        });
        return result;
    }

    @Override
    public UserDept getUserDeptByUserCode(String userCode) {
        List<AdminUserDO> adminUserDOs = userMapper.selectList(AdminUserDO::getCode,userCode).stream().toList();
        if (adminUserDOs.size() == 0) {
            return null;
        }
        AdminUserDO adminUserDO = adminUserDOs.get(0);
        UserDept userDept = BeanUtils.toBean(adminUserDO, UserDept.class);
        userDept.setUserId(adminUserDO.getId());
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(Collections.singletonList(adminUserDO), AdminUserDO::getDeptId));
        if (CollUtil.isNotEmpty(deptMap)) {
            DeptDO deptDO = deptMap.get(adminUserDO.getDeptId());
            if (Objects.nonNull(deptDO)) {
                userDept.setDeptName(deptDO.getName());
            }
        }
        return userDept;
    }

    @Override
    @DataPermission(enable = false)
    public Set<Long> getDeptUserIdsByUserId(Long leaderUserId) {
        AdminUserDO adminUserDO = validateUserExists(leaderUserId);
        if (Objects.isNull(adminUserDO)){
            return Set.of();
        }
        Long deptId = adminUserDO.getDeptId();
        if (Objects.isNull(deptId)){
            return Set.of();
        }
        Set<Long> leaderIdSet = deptService.getLeaderIdSetByDeptId(deptId);
        if (CollUtil.isEmpty(leaderIdSet)){
            return Set.of();
        }
        if (!leaderIdSet.contains(leaderUserId)){
            return Set.of();
        }
        // 如果当前用户是部门负责人，则返回部门下所有用户
        List<AdminUserDO> adminUserDOS = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>().select(AdminUserDO::getId).in(AdminUserDO::getDeptId, deptId));
        if (CollUtil.isEmpty(adminUserDOS)){
            return Set.of();
        }
        return adminUserDOS.stream().map(AdminUserDO::getId).collect(Collectors.toSet());
    }

    @Override
    public UserDept getUserDeptByUserName(String name) {
        List<AdminUserDO> adminUserDOS = userMapper.selectList(AdminUserDO::getNickname, name.trim());
        if(CollUtil.isEmpty(adminUserDOS)){
            return null;
        }
        UserDept user = new UserDept();
        AdminUserDO adminUserDO = adminUserDOS.get(0);
        user.setUserId(adminUserDO.getId()).setUserCode(adminUserDO.getCode()).setNickname(adminUserDO.getNickname()).setMobile(adminUserDO.getMobile());
        user.setDefaultFlag(1);
        DeptDO dept = deptService.getDept(adminUserDO.getDeptId());
        if(Objects.nonNull(dept)){
            user.setDeptId(dept.getId()).setDeptCode(dept.getCode()).setDeptName(dept.getName());
        }
        return user;
    }

    @Override
    public Map<String, UserDept> getUserDeptToUserName() {
        List<AdminUserDO> adminUserDOList = userMapper.selectList();
        if (CollUtil.isEmpty(adminUserDOList)) {
            return null;
        }
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(adminUserDOList, AdminUserDO::getDeptId));
        return UserConvert.INSTANCE.convertToUserDept(adminUserDOList, deptMap).stream().collect(Collectors.toMap(UserDept::getNickname, Function.identity()));
    }


    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
