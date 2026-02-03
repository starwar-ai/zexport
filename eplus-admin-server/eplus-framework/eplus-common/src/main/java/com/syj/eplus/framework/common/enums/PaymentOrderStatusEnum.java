package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/15:21
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum PaymentOrderStatusEnum {
    UNSUBMITTED(0, "未提交"),
    PROCESS(1, "处理中"),
    APPROVE(2, "通过"),
    REJECT(3, "不通过"),
    CANCEL(4, "已取消"),
    BACK(5, "驳回"), // 退回
    DELEGATE(6, "委派"),
    SIGN_AFTER(7, "待后加签任务完成"),
    SIGN_BEFORE(8, "待前加签任务完成"),
    WAIT_BEFORE_TASK(9, "待前置任务完成"),
    ALREADY_PLANNED(10, "已计划"),
    ALREADY_PAID(11, "已支付"),
    CLOSE(12, "作废");
    private final Integer status;

    private final String name;


}
