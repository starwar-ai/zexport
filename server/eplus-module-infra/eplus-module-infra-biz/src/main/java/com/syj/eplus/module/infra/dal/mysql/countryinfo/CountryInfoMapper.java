package com.syj.eplus.module.infra.dal.mysql.countryinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国家信息 Mapper
 *
 * @author du
 */
@Mapper
public interface CountryInfoMapper extends BaseMapperX<CountryInfoDO> {

    default PageResult<CountryInfoDO> selectPage(CountryInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CountryInfoDO>()
                .likeIfPresent(CountryInfoDO::getName, reqVO.getName())
                .eqIfPresent(CountryInfoDO::getCode, reqVO.getCode())
                .eqIfPresent(CountryInfoDO::getRegionCode, reqVO.getRegionCode())
                .likeIfPresent(CountryInfoDO::getRegionName, reqVO.getRegionName())
                .betweenIfPresent(CountryInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CountryInfoDO::getId));
    }

}
