package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class PurchaseContractSignBackReqVO {

   @Schema(description = "回签列表")
   List<SignBackReq> signBackList;


}