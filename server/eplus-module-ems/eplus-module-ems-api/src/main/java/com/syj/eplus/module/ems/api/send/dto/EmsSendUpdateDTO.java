package com.syj.eplus.module.ems.api.send.dto;

import lombok.Data;

import java.util.List;


@Data
public class EmsSendUpdateDTO {

    private List<String> codeList;

    private Integer status;
}