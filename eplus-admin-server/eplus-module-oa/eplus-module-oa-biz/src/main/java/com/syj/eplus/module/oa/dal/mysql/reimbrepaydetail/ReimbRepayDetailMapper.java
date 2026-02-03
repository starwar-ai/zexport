package com.syj.eplus.module.oa.dal.mysql.reimbrepaydetail;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报销还款详情 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ReimbRepayDetailMapper extends BaseMapperX<ReimbRepayDetailDO> {

    default PageResult<ReimbRepayDetailDO> selectPage(ReimbRepayDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReimbRepayDetailDO>()
                .eqIfPresent(ReimbRepayDetailDO::getReimbId, reqVO.getReimbId())
                .eqIfPresent(ReimbRepayDetailDO::getLoanId, reqVO.getLoanId())
                .eqIfPresent(ReimbRepayDetailDO::getRepayStatus, reqVO.getRepayStatus())
                .eqIfPresent(ReimbRepayDetailDO::getRepayAmount, reqVO.getRepayAmount())
                .eqIfPresent(ReimbRepayDetailDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(ReimbRepayDetailDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReimbRepayDetailDO::getId));
    }

}