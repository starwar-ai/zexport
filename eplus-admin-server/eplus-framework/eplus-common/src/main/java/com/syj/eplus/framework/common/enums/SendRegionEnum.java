package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SendRegionEnum {

    IN(1,"国内"),

    OUT(2,"国际");

    private Integer value;

    private String name;
}
