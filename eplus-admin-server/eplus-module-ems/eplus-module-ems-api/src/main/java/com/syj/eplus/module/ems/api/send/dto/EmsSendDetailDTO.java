package com.syj.eplus.module.ems.api.send.dto;

import lombok.Data;


@Data
public class EmsSendDetailDTO {

    private String expressCompany;

    private String expressCode;

    private String amount;

    private String userName;
    private String actualUserName;

    private String receiveName;

    private String deptName;

    private String contractCodes;

    private String remark;

    private String month;
}