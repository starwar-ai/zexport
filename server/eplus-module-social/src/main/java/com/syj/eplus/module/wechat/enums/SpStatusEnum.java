package com.syj.eplus.module.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/14 15:50
 */
@Getter
@AllArgsConstructor
public enum SpStatusEnum {
    // 审批中
    PENDING(1),
    // 已通过
    APPROVED(2),
    // 已驳回
    REJECTED(3),
    // 已撤销
    CANCELLED(4),
    // 通过后撤销（已批准并随后被撤销）
    APPROVED_AND_CANCELLED(5),
    // 已删除
    DELETED(6),
    // 已支付
    PAID(7);

    private int status;

    public String getStrStatus() {
        return String.valueOf(status);
    }
}
