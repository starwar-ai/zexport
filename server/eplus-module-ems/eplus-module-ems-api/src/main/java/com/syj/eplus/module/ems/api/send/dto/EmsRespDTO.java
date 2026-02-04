package com.syj.eplus.module.ems.api.send.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;


@Data
public class EmsRespDTO {

    private String code;

    private JsonAmount cost;

    private  String desc;
}