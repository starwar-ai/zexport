package com.syj.eplus.module.qms.api.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class QualityInspectionReqDTO {

    private Long skuId;
    private String skuCode;
    private String skuName;

    private Long venderId;

    private String venderCode;

    private String venderName;

    private String purchaseContractCode;

    private Long inspectorId;

    private LocalDateTime[] checkTime;

}
