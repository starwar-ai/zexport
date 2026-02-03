package cn.iocoder.yudao.module.system.dal.mysql.userprefence;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefencePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import org.apache.ibatis.annotations.Mapper;
/**
 * 用户偏好设置 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface UserPrefenceMapper extends BaseMapperX<UserPrefenceDO> {

    default PageResult<UserPrefenceDO> selectPage(UserPrefencePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserPrefenceDO>()
                .eqIfPresent(UserPrefenceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(UserPrefenceDO::getUserCode, reqVO.getUserCode())
                .eqIfPresent(UserPrefenceDO::getPageKey, reqVO.getPageKey())
                .eqIfPresent(UserPrefenceDO::getTabIndex, reqVO.getTabIndex())
                .eqIfPresent(UserPrefenceDO::getParent, reqVO.getParent())
                .eqIfPresent(UserPrefenceDO::getChildren, reqVO.getChildren())
                .betweenIfPresent(UserPrefenceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserPrefenceDO::getId));
    }

}