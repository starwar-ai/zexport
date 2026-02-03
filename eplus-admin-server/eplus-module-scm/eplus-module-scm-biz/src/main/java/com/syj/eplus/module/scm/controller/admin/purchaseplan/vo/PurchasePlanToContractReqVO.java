package com.syj.eplus.module.scm.controller.admin.purchaseplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购计划转合同 Request VO")
@Data
public class PurchasePlanToContractReqVO {

  private   List<Long> planItemIdList;

}