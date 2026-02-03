package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JsonPayment {

    private LocalDateTime paymentTime;

    private JsonAmount paymentAmount;

    private UserDept paymentUser;
}
