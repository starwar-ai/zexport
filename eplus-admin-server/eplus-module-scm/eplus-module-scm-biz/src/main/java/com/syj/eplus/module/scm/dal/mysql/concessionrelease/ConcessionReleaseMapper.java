package com.syj.eplus.module.scm.dal.mysql.concessionrelease;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleasePageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.concessionrelease.ConcessionReleaseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 让步放行 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ConcessionReleaseMapper extends BaseMapperX<ConcessionReleaseDO> {

    default PageResult<ConcessionReleaseDO> selectPage(ConcessionReleasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConcessionReleaseDO>()
                .eqIfPresent(ConcessionReleaseDO::getAnnexFlag, reqVO.getAnnexFlag())
                .inIfPresent(ConcessionReleaseDO::getQualityInspectionId, reqVO.getQualityInspectionIdList())
                .eqIfPresent(ConcessionReleaseDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ConcessionReleaseDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ConcessionReleaseDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ConcessionReleaseDO::getId));
    }

}