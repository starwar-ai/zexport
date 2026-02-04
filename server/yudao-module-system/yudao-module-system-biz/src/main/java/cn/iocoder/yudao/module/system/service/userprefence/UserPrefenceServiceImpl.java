package cn.iocoder.yudao.module.system.service.userprefence;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefencePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceRespVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceSaveReqVO;
import cn.iocoder.yudao.module.system.convert.userprefence.UserPrefenceConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import cn.iocoder.yudao.module.system.dal.mysql.userprefence.UserPrefenceMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.syj.eplus.framework.common.entity.JsonSetPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_PREFENCE_NOT_EXISTS;

/**
 * 用户偏好设置 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class UserPrefenceServiceImpl implements UserPrefenceService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserPrefenceMapper userPrefenceMapper;

    @Lazy // 解决循环依赖
    @Resource
    private AdminUserService userService;

    @Override
    public Long createUserPrefence(UserPrefenceSaveReqVO createReqVO) {
        UserPrefenceDO userPrefence = UserPrefenceConvert.INSTANCE.convertUserPrefenceDO(createReqVO);
        // 插入
        userPrefenceMapper.insert(userPrefence);
        // 返回
        return userPrefence.getId();
    }

    @Override
    public void updateUserPrefence(UserPrefenceSaveReqVO updateReqVO) {
        // 校验存在
        validateUserPrefenceExists(updateReqVO.getId());
        // 更新
        UserPrefenceDO updateObj = UserPrefenceConvert.INSTANCE.convertUserPrefenceDO(updateReqVO);
        userPrefenceMapper.updateById(updateObj);
    }

    @Override
    public void deleteUserPrefence(Long id) {
        // 校验存在
        validateUserPrefenceExists(id);
        // 删除
        userPrefenceMapper.deleteById(id);
    }

    private void validateUserPrefenceExists(Long id) {
        if (userPrefenceMapper.selectById(id) == null) {
            throw exception(USER_PREFENCE_NOT_EXISTS);
        }
    }
    @Override
    public UserPrefenceRespVO getUserPrefence(Long id) {
        UserPrefenceDO userPrefenceDO = userPrefenceMapper.selectById(id);
        if (userPrefenceDO == null) {
            return null;
        }
        return UserPrefenceConvert.INSTANCE.convertUserPrefenceRespVO(userPrefenceDO);
    }

    @Override
    public PageResult<UserPrefenceDO> getUserPrefencePage(UserPrefencePageReqVO pageReqVO) {
        return userPrefenceMapper.selectPage(pageReqVO);
    }

    @Override
    public UserPrefenceDO getListByUser(Long loginUserId, String pageKey, Integer tabIndex) {
       return userPrefenceMapper.selectList(new LambdaQueryWrapperX<UserPrefenceDO>()
                .eq(UserPrefenceDO::getUserId, loginUserId)
                .eq(UserPrefenceDO::getPageKey, pageKey)
                .eq(UserPrefenceDO::getTabIndex, tabIndex)
        ).stream().findFirst().orElse(null);
    }

    @Override
    public void updateListByUser(Long loginUserId, JsonSetPreference  jsonSetPreference) {
        //删除现有数据
        userPrefenceMapper.delete(new LambdaQueryWrapperX<UserPrefenceDO>()
                .eq(UserPrefenceDO::getUserId, loginUserId)
                .eq(UserPrefenceDO::getPageKey, jsonSetPreference.getPageKey())
                .eq(UserPrefenceDO::getTabIndex, jsonSetPreference.getTabIndex()));

        UserPrefenceDO userPrefenceDO = BeanUtils.toBean(jsonSetPreference, UserPrefenceDO.class);
        userPrefenceDO.setId(null);
        AdminUserDO user = userService.getUserById(loginUserId);
        if(Objects.isNull(user)){
            throw exception(USER_NOT_EXISTS);
        }
        userPrefenceDO.setUserId(user.getId()).setUserCode(user.getCode());
        userPrefenceMapper.insert(userPrefenceDO);
    }

}
