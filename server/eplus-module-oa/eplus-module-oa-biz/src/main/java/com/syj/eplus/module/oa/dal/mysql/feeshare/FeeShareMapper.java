package com.syj.eplus.module.oa.dal.mysql.feeshare;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.FeeSharePageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshare.FeeShareDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 费用归集 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface FeeShareMapper extends BaseMapperX<FeeShareDO> {

    default PageResult<FeeShareDO> selectPage(FeeSharePageReqVO reqVO) {
        LambdaQueryWrapperX<FeeShareDO> queryWrapperX = new LambdaQueryWrapperX<FeeShareDO>()
                .eqIfPresent(FeeShareDO::getBusinessType, reqVO.getBusinessType())
                .likeIfPresent(FeeShareDO::getBusinessCode, reqVO.getBusinessCode())
                .eqIfPresent(FeeShareDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(FeeShareDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(FeeShareDO::getSourceStatus,reqVO.getSourceStatus())
                .eqIfPresent(FeeShareDO::getAuditStatus,reqVO.getAuditStatus())
                .eqIfPresent(FeeShareDO::getStatus,reqVO.getStatus())
                .likeIfPresent(FeeShareDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(FeeShareDO::getFeeShareType, reqVO.getFeeShareType())
                .eqIfPresent(FeeShareDO::getRelationType, reqVO.getRelationType())
                .eqIfPresent(FeeShareDO::getDescId, reqVO.getDescId())
                .likeIfPresent(FeeShareDO::getDescName, reqVO.getDescName())
                .betweenIfPresent(FeeShareDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FeeShareDO::getId);
            if(Objects.isNull(reqVO.getPreCollectionFlag()) || reqVO.getPreCollectionFlag() == 0){
                queryWrapperX.ne(FeeShareDO::getPreCollectionFlag,1);
            }
            if (Objects.nonNull(reqVO.getInputUserId())) {
//                queryWrapperX.apply("JSON_EXTRACT(input_user, JSON_OBJECT('userId', {0})", reqVO.getInputUserId());
                queryWrapperX.apply("input_user ->> '$.userId' = {0}" , reqVO.getInputUserId() );
            }
            if (Objects.nonNull(reqVO.getInputDeptId())) {
                queryWrapperX.apply("input_user ->> '$.deptId' = {0}", reqVO.getInputDeptId());
            }
            if(Objects.nonNull(reqVO.getNotAuditStatus())) {
                queryWrapperX.ne(FeeShareDO::getAuditStatus, reqVO.getNotAuditStatus()).isNotNull(FeeShareDO::getAuditStatus);
            }
        return selectPage(reqVO,queryWrapperX);
    }

}