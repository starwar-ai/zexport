package com.syj.eplus.module.scm.api.purchasecontract.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateInspectionDTO {

    private Integer quantity;

    private Integer handleFlag;

    private Integer checkStatus;

    private LocalDateTime inspectionTime;
}
