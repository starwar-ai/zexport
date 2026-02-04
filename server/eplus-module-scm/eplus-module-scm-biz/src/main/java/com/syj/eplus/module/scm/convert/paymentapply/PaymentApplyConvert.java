package com.syj.eplus.module.scm.convert.paymentapply;

import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.PaymentApplyRespVO;
import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.PaymentApplySaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.entity.ApplyPaymentPlan;
import com.syj.eplus.module.scm.entity.ApplyerPurchaseContractItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface PaymentApplyConvert {

    PaymentApplyConvert INSTANCE = Mappers.getMapper(PaymentApplyConvert.class);

    PaymentApplyRespVO convert(PaymentApplyDO paymentApplyDO);

    default PaymentApplyRespVO convertPaymentApplyRespVO(PaymentApplyDO paymentApplyDO) {
        PaymentApplyRespVO paymentApplyRespVO = convert(paymentApplyDO);
        return paymentApplyRespVO;
    }

    PaymentApplyDO convertPaymentApplyDO(PaymentApplySaveReqVO saveReqVO);

    List<ApplyPaymentPlan> convertApplyPaymentPlanList(List<PurchasePaymentPlan> purchasePaymentPlan);

    ApplyerPurchaseContractItem convertApplyerPurchaseContractItem(PurchaseContractItemDO paymentApplyItemDO);
}