package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MmsManufactureStatusEnum {
    PENDING_DONE(1, "待完成"),
    DONE(2, "已完成"),
    FINISH(3, "已结案") ;

    private final Integer code;

    private final String name;


}
