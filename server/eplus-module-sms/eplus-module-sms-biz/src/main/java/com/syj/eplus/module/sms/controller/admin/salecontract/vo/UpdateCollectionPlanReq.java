package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCollectionPlanReq {
    private List<CollectionPlan> collectionPlanList;
}
