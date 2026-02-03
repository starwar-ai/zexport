package com.syj.eplus.module.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：审批过滤枚举
 * @Author：du
 * @Date：2024/3/13 21:13
 */
@Getter
@AllArgsConstructor
public enum ApprovalFilterEnum {
    //模板类型
    TEMPLATE_ID("template_id"),
    //申请人
    CREATOR("creator"),
    //审批单提单者所在部门
    DEPARTMENT("department"),
    //审批状态
    SP_STATUS("sp_status"),
    //审批单类型属性
    RECORD_TYPE("record_type");

    private String key;
}
