package com.syj.eplus.module.scm.dal.mysql.qualification;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资质 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QualificationMapper extends BaseMapperX<QualificationDO> {

    default PageResult<QualificationDO> selectPage(QualificationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QualificationDO>()
                .likeIfPresent(QualificationDO::getName, reqVO.getName())
                .eqIfPresent(QualificationDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(QualificationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(QualificationDO::getId));
    }

}