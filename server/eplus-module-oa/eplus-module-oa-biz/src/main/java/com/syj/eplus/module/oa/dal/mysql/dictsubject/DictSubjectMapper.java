package com.syj.eplus.module.oa.dal.mysql.dictsubject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类别配置 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface DictSubjectMapper extends BaseMapperX<DictSubjectDO> {

    default PageResult<DictSubjectDO> selectPage(DictSubjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictSubjectDO>()
                .eqIfPresent(DictSubjectDO::getSubjectId, reqVO.getSubjectId())
                .likeIfPresent(DictSubjectDO::getSubjectName, reqVO.getSubjectName())
                .eqIfPresent(DictSubjectDO::getSubjectDescription, reqVO.getSubjectDescription())
                .eqIfPresent(DictSubjectDO::getSystemDictTypeList, reqVO.getSystemDictTypeList())
                .eqIfPresent(DictSubjectDO::getFeeDesc, reqVO.getFeeDesc())
                .eqIfPresent(DictSubjectDO::getFeeName, reqVO.getFeeName())
                .betweenIfPresent(DictSubjectDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DictSubjectDO::getId));
    }

}