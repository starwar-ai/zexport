package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/21/20:07
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum AuxiliaryStatusEnum {
    NOT_AUXILIARY(1, "未归集"),
    AUXILIARY(2, "已归集"),
    FINISHED(3, "已完成");

    private Integer value;

    private String name;
}
