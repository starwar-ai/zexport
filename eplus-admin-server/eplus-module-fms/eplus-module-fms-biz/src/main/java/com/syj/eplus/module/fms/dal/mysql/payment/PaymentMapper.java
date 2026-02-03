package com.syj.eplus.module.fms.dal.mysql.payment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.fms.controller.admin.payment.vo.PaymentPageReqVO;
import com.syj.eplus.module.fms.dal.dataobject.payment.PaymentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 财务付款表 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface PaymentMapper extends BaseMapperX<PaymentDO> {

    default PageResult<PaymentDO> selectPage(PaymentPageReqVO reqVO) {
        LambdaQueryWrapperX<PaymentDO> queryWrapperX  =  new LambdaQueryWrapperX<PaymentDO>()
                .likeIfPresent(PaymentDO::getCode, reqVO.getCode())
                .eqIfPresent(PaymentDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(PaymentDO::getBank, reqVO.getBank())
                .eqIfPresent(PaymentDO::getBankAccount, reqVO.getBankAccount())
                .likeIfPresent(PaymentDO::getBankAddress, reqVO.getBankAddress())
                .inIfPresent(PaymentDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PaymentDO::getAmount, reqVO.getAmount())
                .eqIfPresent(PaymentDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(PaymentDO::getDate, reqVO.getDate())
                .eqIfPresent(PaymentDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PaymentDO::getBusinessType, reqVO.getBusinessType())
                .eqIfPresent(PaymentDO::getBusinessCode, reqVO.getBusinessCode())
                .betweenIfPresent(PaymentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PaymentDO::getId);
        //-- todo:需要该成max和min
        if (Objects.nonNull(reqVO.getApplyAmount())){
            queryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(apply_amount, '$[*].amount'), CAST({0} AS JSON))", reqVO.getApplyAmount());
        }
        if(Objects.nonNull(reqVO.getApplierId())){
            queryWrapperX.apply("JSON_EXTRACT(applyer,'$.userId') = {0}", reqVO.getApplierId());
        }
        return selectPage(reqVO,queryWrapperX);
    }
}