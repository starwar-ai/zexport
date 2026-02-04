package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePaymentPlanReq {
    private List<PurchasePaymentPlan> paymentPlanList;
}
