package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PurchaseContractSignBackItemVO {

   @Schema(description = "回签描述")
   private String desc;

   @Schema(description = "采购合同id")
   private Long id;

   @Schema(description = "采购合同编号")
   private String code;

   @Schema(description = "回签时间")
   private LocalDateTime signBackTime;


}