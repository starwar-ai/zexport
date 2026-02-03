package com.syj.eplus.module.oa.dal.mysql.paymentapp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.PaymentAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 公对公申请 Mapper
 *
 * @author du
 */
@Mapper
public interface PaymentAppMapper extends BaseMapperX<PaymentAppDO> {

    default PageResult<PaymentAppDO> selectPage(PaymentAppPageReqVO reqVO) {
        LambdaQueryWrapperX<PaymentAppDO> queryWrapperX = new LambdaQueryWrapperX<PaymentAppDO>()
                .likeIfPresent(PaymentAppDO::getCode, reqVO.getCode())
                .eqIfPresent(PaymentAppDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(PaymentAppDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(PaymentAppDO::getAuditStatus, reqVO.getAuditStatus())
                .likeIfPresent(PaymentAppDO::getReason, reqVO.getReason())
                .eqIfPresent(PaymentAppDO::getLinkFlag, reqVO.getLinkFlag())
                .eqIfPresent(PaymentAppDO::getCreator, reqVO.getCreator())
                .eqIfPresent(PaymentAppDO::getPrepaidFlag, reqVO.getPrepaidFlag())
                .eqIfPresent(PaymentAppDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(PaymentAppDO::getBusinessSubjectType, reqVO.getBusinessSubjectType())
                .eqIfPresent(PaymentAppDO::getBusinessSubjectCode, reqVO.getBusinessSubjectCode())
                .eqIfPresent(PaymentAppDO::getBank, reqVO.getBank())
                .likeIfPresent(PaymentAppDO::getPaymentAppList, reqVO.getPaymentAppCode())
                .eqIfPresent(PaymentAppDO::getPaymentStatus, reqVO.getPaymentStatus())
                .eqIfPresent(PaymentAppDO::getBankAddress, reqVO.getBankAddress())
                .eqIfPresent(PaymentAppDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(PaymentAppDO::getBankPoc, reqVO.getBankPoc())
                .eqIfPresent(PaymentAppDO::getBankCode, reqVO.getBankCode())
                .betweenIfPresent(PaymentAppDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(PaymentAppDO::getPaymentDate, reqVO.getPaymentDate())
                .likeIfPresent(PaymentAppDO::getRemark, reqVO.getRemark())
                .inIfPresent(PaymentAppDO::getBusinessSubjectType, reqVO.getBusinessSubjectTypeList())
                .orderByDesc(PaymentAppDO::getId);
        if (StrUtil.isNotEmpty(reqVO.getCurrency())){
            queryWrapperX.apply("JSON_EXTRACT(amount,'$.currency') = {0}", reqVO.getCurrency());
        }
        if (Objects.nonNull(reqVO.getPayAmountMin())){
            queryWrapperX.apply("JSON_EXTRACT(amount,'$.amount') >= {0}", reqVO.getPayAmountMin());
        }
        if (Objects.nonNull(reqVO.getPayAmountMax())){
            queryWrapperX.apply("JSON_EXTRACT(amount,'$.amount') <= {0}", reqVO.getPayAmountMax());
        }
        if (Objects.nonNull(reqVO.getDeptId())){
            queryWrapperX.apply("JSON_EXTRACT(applyer,'$.deptId') = {0}", reqVO.getDeptId());
        }
        return selectPage(reqVO, queryWrapperX);
    }

}