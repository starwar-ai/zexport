package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/26 11:31
 */
@Getter
@AllArgsConstructor
public enum CollectionPlanStepEnum {
    /**
     * 预收
     */
    ADVAMCE_RECEIPT(1,"预收"),

    /**
     * 第一笔
     */
    FIRST_PAYMENT(2, "第一笔"),

    /**
     * 第二笔
     */
    SECOND_PAYMENT(3, "第二笔"),

    /**
     * 第三笔
     */
    THIRD_PAYMENT(4, "第三笔"),

    /**
     * 第四笔
     */
    FOURTH_PAYMENT(5, "第四笔"),

    /**
     * 第五笔
     */
    FIFTH_PAYMENT(6, "第五笔");

    /**
     * 步骤
     */
    private Integer step;

    /**
     * 名称
     */
    private String name;

    /**
     * 获取步骤列表
     * @return 步骤列表
     */
    public static List<Integer> getStepList() {
        return Arrays.asList(ADVAMCE_RECEIPT.step,FIRST_PAYMENT.step,
                SECOND_PAYMENT.step,THIRD_PAYMENT.step,FOURTH_PAYMENT.step,FIFTH_PAYMENT.step);
    }
}
