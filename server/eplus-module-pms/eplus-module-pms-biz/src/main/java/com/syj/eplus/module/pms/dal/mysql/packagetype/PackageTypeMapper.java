package com.syj.eplus.module.pms.dal.mysql.packagetype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.PackageTypePageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 包装方式 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PackageTypeMapper extends BaseMapperX<PackageTypeDO> {

    default PageResult<PackageTypeDO> selectPage(PackageTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PackageTypeDO>()
                .eqIfPresent(PackageTypeDO::getCode, reqVO.getCode())
                .likeIfPresent(PackageTypeDO::getName, reqVO.getName())
                .eqIfPresent(PackageTypeDO::getNameEng, reqVO.getNameEng())
                .betweenIfPresent(PackageTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PackageTypeDO::getId));
    }

}