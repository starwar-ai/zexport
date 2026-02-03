package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewRegistrationReq {

    private List<Long> ids;

    private LocalDateTime invoicedDate;
}
