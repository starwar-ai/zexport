package com.syj.eplus.module.infra.dal.mysql.formchange;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangePageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.formchange.FormChangeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单字段变更管理 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface FormChangeMapper extends BaseMapperX<FormChangeDO> {

    default PageResult<FormChangeDO> selectPage(FormChangePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FormChangeDO>()
                .likeIfPresent(FormChangeDO::getName, reqVO.getName())
                .likeIfPresent(FormChangeDO::getDescription, reqVO.getDescription())
                .eqIfPresent(FormChangeDO::getModelKey, reqVO.getModelKey())
                .eqIfPresent(FormChangeDO::getInstanceFlag, reqVO.getInstanceFlag())
                .betweenIfPresent(FormChangeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FormChangeDO::getId));
    }
}