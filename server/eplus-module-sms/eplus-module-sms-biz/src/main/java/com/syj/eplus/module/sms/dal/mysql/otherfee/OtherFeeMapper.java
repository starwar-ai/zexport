package com.syj.eplus.module.sms.dal.mysql.otherfee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeePageReqVO;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 其他费用 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface OtherFeeMapper extends BaseMapperX<OtherFeeDO> {

    default PageResult<OtherFeeDO> selectPage(OtherFeePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OtherFeeDO>()
                .eqIfPresent(OtherFeeDO::getSmsQuotationId, reqVO.getSmsQuotationId())
                .likeIfPresent(OtherFeeDO::getFeeName, reqVO.getFeeName())
                .eqIfPresent(OtherFeeDO::getAmount, reqVO.getAmount())
                .betweenIfPresent(OtherFeeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OtherFeeDO::getId));
    }

}