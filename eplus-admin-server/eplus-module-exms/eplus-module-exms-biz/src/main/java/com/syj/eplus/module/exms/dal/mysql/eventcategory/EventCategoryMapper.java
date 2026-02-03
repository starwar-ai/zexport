package com.syj.eplus.module.exms.dal.mysql.eventcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryPageReqVO;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 展会系列 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface EventCategoryMapper extends BaseMapperX<EventCategoryDO> {

    default PageResult<EventCategoryDO> selectPage(EventCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EventCategoryDO>()
                .likeIfPresent(EventCategoryDO::getName, reqVO.getName())
                .eqIfPresent(EventCategoryDO::getIsDomestic, reqVO.getIsDomestic())
                .betweenIfPresent(EventCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EventCategoryDO::getId));
    }

}