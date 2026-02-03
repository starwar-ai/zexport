package com.syj.eplus.module.scm.dal.mysql.paymentplan;

import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购合同付款计划 Mapper
 *
 * @author du
 */
@Mapper
public interface PurchasePaymentPlanMapper extends BaseMapperX<PurchasePaymentPlan>, ChangeExInterface {


}