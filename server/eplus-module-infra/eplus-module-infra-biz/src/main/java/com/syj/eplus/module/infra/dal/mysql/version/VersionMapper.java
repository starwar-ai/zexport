package com.syj.eplus.module.infra.dal.mysql.version;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.version.VersionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 版本记录 Mapper
 *
 * @author Zhangcm
 */
@Mapper
public interface VersionMapper extends BaseMapperX<VersionDO> {

    default PageResult<VersionDO> selectPage(VersionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VersionDO>()
                .eqIfPresent(VersionDO::getFrontVer, reqVO.getFrontVer())
                .eqIfPresent(VersionDO::getServerVer, reqVO.getServerVer())
                .eqIfPresent(VersionDO::getPublishVer, reqVO.getPublishVer())
                .likeIfPresent(VersionDO::getPublishName, reqVO.getPublishName())
                .eqIfPresent(VersionDO::getFrontDesc, reqVO.getFrontDesc())
                .eqIfPresent(VersionDO::getServerDesc, reqVO.getServerDesc())
                .eqIfPresent(VersionDO::getPublishDesc, reqVO.getPublishDesc())
                .eqIfPresent(VersionDO::getEnabled, reqVO.getEnabled())
                .betweenIfPresent(VersionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(VersionDO::getId));
    }

    @Select("SELECT * FROM infra_version WHERE enabled = 1 ORDER BY id DESC LIMIT 1")
    VersionDO selectLatestEnabledVersion();

}