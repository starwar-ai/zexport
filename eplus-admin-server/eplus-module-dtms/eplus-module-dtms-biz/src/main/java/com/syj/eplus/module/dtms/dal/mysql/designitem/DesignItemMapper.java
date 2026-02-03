package com.syj.eplus.module.dtms.dal.mysql.designitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemPageReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设计-认领明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface DesignItemMapper extends BaseMapperX<DesignItemDO> {

    default PageResult<DesignItemDO> selectPage(DesignItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DesignItemDO>()
                .eqIfPresent(DesignItemDO::getDesignId, reqVO.getDesignId())
                .eqIfPresent(DesignItemDO::getDesignerId, reqVO.getDesignerId())
                .likeIfPresent(DesignItemDO::getDesignerName, reqVO.getDesignerName())
                .eqIfPresent(DesignItemDO::getDesignerDeptId, reqVO.getDesignerDeptId())
                .likeIfPresent(DesignItemDO::getDesignerDeptName, reqVO.getDesignerDeptName())
                .eqIfPresent(DesignItemDO::getItemType, reqVO.getItemType())
                .eqIfPresent(DesignItemDO::getDesignFilePath, reqVO.getDesignFilePath())
                .betweenIfPresent(DesignItemDO::getAcceptDate, reqVO.getAcceptDate())
                .betweenIfPresent(DesignItemDO::getCompleteDate, reqVO.getCompleteDate())
                .eqIfPresent(DesignItemDO::getEvaluateResult, reqVO.getEvaluateResult())
                .eqIfPresent(DesignItemDO::getEvaluateDesc, reqVO.getEvaluateDesc())
                .eqIfPresent(DesignItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(DesignItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DesignItemDO::getId));
    }

}
