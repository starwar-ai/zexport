package com.syj.eplus.module.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/14 15:45
 */

@Getter
@AllArgsConstructor
public enum RecordTypeEnum {
    // 请假
    LEAVE("1"),
    // 打卡补卡
    PUNCH_CARD_SUBSTITUTION("2"),
    // 出差
    BUSINESS_TRIP("3"),
    // 外出
    OUTGOING("4"),
    // 加班
    OVERTIME("5"),
    // 调班
    SHIFT_CHANGE("6"),
    // 会议室预定
    MEETING_ROOM_RESERVATION("7"),
    // 退款审批
    REFUND_APPROVAL("8"),
    // 红包报销审批
    RED_PACKET_REIMBURSEMENT_APPROVAL("9");

    private String value;
}
