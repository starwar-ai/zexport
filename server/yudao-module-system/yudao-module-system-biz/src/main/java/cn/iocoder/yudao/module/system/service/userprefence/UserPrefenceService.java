package cn.iocoder.yudao.module.system.service.userprefence;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefencePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceRespVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import com.syj.eplus.framework.common.entity.JsonSetPreference;

import javax.validation.Valid;

/**
 * 用户偏好设置 Service 接口
 *
 * @author zhangcm
 */
public interface UserPrefenceService {

    /**
     * 创建用户偏好设置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUserPrefence(@Valid UserPrefenceSaveReqVO createReqVO);

    /**
     * 更新用户偏好设置
     *
     * @param updateReqVO 更新信息
     */
    void updateUserPrefence(@Valid UserPrefenceSaveReqVO updateReqVO);

    /**
     * 删除用户偏好设置
     *
     * @param id 编号
     */
    void deleteUserPrefence(Long id);

    /**
     * 获得用户偏好设置
     *
     * @param  id 编号
     * @return 用户偏好设置
     */
    UserPrefenceRespVO getUserPrefence(Long id);

    /**
     * 获得用户偏好设置分页
     *
     * @param pageReqVO 分页查询
     * @return 用户偏好设置分页
     */
    PageResult<UserPrefenceDO> getUserPrefencePage(UserPrefencePageReqVO pageReqVO);

    UserPrefenceDO getListByUser(Long loginUserId, String pageKey, Integer tabIndex);

    void updateListByUser(Long loginUserId, JsonSetPreference jsonSetPreference);
}