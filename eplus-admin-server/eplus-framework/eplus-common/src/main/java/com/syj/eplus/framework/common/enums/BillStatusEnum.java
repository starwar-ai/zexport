package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BillStatusEnum {
    BILLED(3, "已入库"),
    NOT_BILL(1, "未入库"),
    PART_BILL(2, "部分入库");

    private Integer status;

    private String name;
}
