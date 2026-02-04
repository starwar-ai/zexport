package com.syj.eplus.module.scm.dal.mysql.paymentapply;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.PaymentApplyPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Objects;

/**
 * 付款申请主 Mapper
 *
 * @author du
 */
@Mapper
public interface PaymentApplyMapper extends BaseMapperX<PaymentApplyDO> {

    default PageResult<PaymentApplyDO> selectPage(PaymentApplyPageReqVO reqVO) {
        LambdaQueryWrapperX<PaymentApplyDO> queryWrapperX = new LambdaQueryWrapperX<PaymentApplyDO>()
                .eqIfPresent(PaymentApplyDO::getPaymentPlanId, reqVO.getPaymentPlanId())
                .eqIfPresent(PaymentApplyDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(PaymentApplyDO::getCode, reqVO.getCode())
                .likeIfPresent(PaymentApplyDO::getCompanyName, reqVO.getCompanyName())
                .betweenIfPresent(PaymentApplyDO::getApplyDate, reqVO.getApplyDate())
                .eqIfPresent(PaymentApplyDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(PaymentApplyDO::getStep, reqVO.getStep())
                .eqIfPresent(PaymentApplyDO::getGoodsTotalAmount, reqVO.getGoodsTotalAmount())
                .betweenIfPresent(PaymentApplyDO::getApplyPaymentDate, reqVO.getApplyPaymentDate())
                .eqIfPresent(PaymentApplyDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PaymentApplyDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(PaymentApplyDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(PaymentApplyDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(PaymentApplyDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(PaymentApplyDO::getPaymentId, reqVO.getPaymentId())
                .likeIfPresent(PaymentApplyDO::getPaymentName, reqVO.getPaymentName())
                .eqIfPresent(PaymentApplyDO::getTaxRate, reqVO.getTaxRate())
                .eqIfPresent(PaymentApplyDO::getBank, reqVO.getBank())
                .eqIfPresent(PaymentApplyDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(PaymentApplyDO::getPaymentPlan, reqVO.getPaymentPlan())
                .betweenIfPresent(PaymentApplyDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PaymentApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .leIfPresent(PaymentApplyDO::getApplyTotalAmount, reqVO.getApplyTotalAmountMax())
                .geIfPresent(PaymentApplyDO::getApplyTotalAmount, reqVO.getApplyTotalAmountMin())
                .eqIfPresent(PaymentApplyDO::getPaymentStatus, reqVO.getPaymentStatus())
                .eqIfPresent(PaymentApplyDO::getPaymentMethod, reqVO.getPaymentMethod())
                .eqIfPresent(PaymentApplyDO::getPaymentMarkType, reqVO.getPaymentMarkType())
                .likeIfPresent(PaymentApplyDO::getPurchaseContractCodeList, reqVO.getPurchaseContractCode())
                .orderByDesc(PaymentApplyDO::getId);
        if (CollUtil.isNotEmpty(reqVO.getExcludeAuditStatus())){
            queryWrapperX.notIn(PaymentApplyDO::getAuditStatus, reqVO.getExcludeAuditStatus());
        }
        if (Objects.nonNull(reqVO.getApplyerId())){
            queryWrapperX.apply("JSON_EXTRACT(applyer,'$.userId') = {0}", reqVO.getApplyerId());
        }
        if (Objects.nonNull(reqVO.getApplyerDeptId())){
            queryWrapperX.apply("JSON_EXTRACT(applyer,'$.deptId') = {0}", reqVO.getApplyerDeptId());
        }
        return selectPage(reqVO,queryWrapperX);
    }

    @Select("""
            select count(1) from fms_payment where apply_code = #{applyCode} and deleted = 0
            """)
    Long validateAntiAuditStatus(@Param("applyCode") String applyCode);
}