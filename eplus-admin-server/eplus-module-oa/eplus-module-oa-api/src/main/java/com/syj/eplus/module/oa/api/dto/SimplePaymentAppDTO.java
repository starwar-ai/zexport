package com.syj.eplus.module.oa.api.dto;


import lombok.Data;

import java.util.List;

@Data
public class SimplePaymentAppDTO {

    private String code;


    private Integer relationType;
    private List<String> relationCode;

}
