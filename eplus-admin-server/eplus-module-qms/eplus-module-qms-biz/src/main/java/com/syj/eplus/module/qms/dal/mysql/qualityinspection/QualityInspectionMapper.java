package com.syj.eplus.module.qms.dal.mysql.qualityinspection;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionPageReqVO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验货单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QualityInspectionMapper extends BaseMapperX<QualityInspectionDO> {

    default PageResult<QualityInspectionDO> selectPage(QualityInspectionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QualityInspectionDO>()
                .likeIfPresent(QualityInspectionDO::getCode, reqVO.getCode())
                .likeIfPresent(QualityInspectionDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .inIfPresent(QualityInspectionDO::getQualityInspectionStatus, reqVO.getQualityInspectionStatus())
                .eqIfPresent(QualityInspectionDO::getInspectionType, reqVO.getInspectionType())
                .eqIfPresent(QualityInspectionDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(QualityInspectionDO::getVenderCode, reqVO.getVenderCode())
                .eqIfPresent(QualityInspectionDO::getApplyInspectorId, reqVO.getApplyInspectorId())
                .eqIfPresent(QualityInspectionDO::getInspectorId, reqVO.getInspectorId())
                .betweenIfPresent(QualityInspectionDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(QualityInspectionDO::getInspectionTime, reqVO.getInspectionTime())
                .orderByDesc(QualityInspectionDO::getId));
    }

}
