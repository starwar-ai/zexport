package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContainerTypeEnum {

    TWENTY(1,"20尺柜"),


    FORTY(2, "40尺柜"),


    FORTY_HIGH(3, "40高柜") ;

    private Integer code;

    private String value;

}
