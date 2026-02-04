package com.syj.eplus.module.home.dal.mysql.hometab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabPageReqVO;
import com.syj.eplus.module.home.dal.dataobject.hometab.HomeTabDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统首页 Mapper
 *
 * @author du
 */
@Mapper
public interface HomeTabMapper extends BaseMapperX<HomeTabDO> {

    default PageResult<HomeTabDO> selectPage(HomeTabPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HomeTabDO>()
                .eqIfPresent(HomeTabDO::getUserId, reqVO.getUserId())
                .eqIfPresent(HomeTabDO::getRoleId, reqVO.getRoleId())
                .betweenIfPresent(HomeTabDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HomeTabDO::getId));
    }

}