package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入（出）库通知单-状态
 */
@Getter
@AllArgsConstructor
public enum NoticeStatusEnum {

    UN_CONVERT(1,"未转"),

    CONVERTED(2,"已转"),

    CANCEL(3,"作废"),

    PART_CONVERT(4,"部分转"),

    IN_CONVERT(5,"转单中");

    private final Integer value;

    private final String desc;
}
