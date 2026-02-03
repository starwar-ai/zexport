package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/11 11:05
 */
@Getter
@AllArgsConstructor
public enum SubmitFlagEnum {

    /**
     * 提交
     */
    SUBMIT(1),
    /**
     * 仅保存草搞
     */
    ONLY_SAVE(0);

    private final Integer status;

}
