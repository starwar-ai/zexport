package com.syj.eplus.module.dms.enums.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class DmsForwarderFeeDTO {

   private List<String> codeList;

   private Integer Status;

   private String paymentAppCode;
   private Long paymentAppId;
}
