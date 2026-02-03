package com.syj.eplus.module.infra.dal.mysql.paymentitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.paymentitem.PaymentItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 付款方式 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface PaymentItemMapper extends BaseMapperX<PaymentItemDO> {

    default PageResult<PaymentItemDO> selectPage(PaymentItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PaymentItemDO>()
                .eqIfPresent(PaymentItemDO::getCode, reqVO.getCode())
                .likeIfPresent(PaymentItemDO::getName, reqVO.getName())
                .eqIfPresent(PaymentItemDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(PaymentItemDO::getDateType, reqVO.getDateType())
                .eqIfPresent(PaymentItemDO::getDuration, reqVO.getDuration())
                .betweenIfPresent(PaymentItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PaymentItemDO::getId));
    }

}