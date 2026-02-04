package com.syj.eplus.module.qms.dal.mysql.qualityinspection;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验货单-明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QualityInspectionItemMapper extends BaseMapperX<QualityInspectionItemDO> {

    default PageResult<QualityInspectionItemDO> selectPage(PageParam reqVO, Long inspectionId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QualityInspectionItemDO>()
            .eq(QualityInspectionItemDO::getInspectionId, inspectionId)
            .orderByDesc(QualityInspectionItemDO::getId));
    }

    default int deleteByInspectionId(Long inspectionId) {
        return delete(QualityInspectionItemDO::getInspectionId, inspectionId);
    }

}