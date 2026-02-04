package com.syj.eplus.framework.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class BaseValue {

    private String name;

    private String value;
    private Long deptId;
    private String deptName;

    private Integer defaultFlag;

}
