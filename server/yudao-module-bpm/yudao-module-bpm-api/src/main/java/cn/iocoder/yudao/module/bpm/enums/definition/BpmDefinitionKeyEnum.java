package cn.iocoder.yudao.module.bpm.enums.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：业务模块对应流程标识枚举
 * @Author：du
 * @Date：2024/1/21 17:40
 */

@Getter
@AllArgsConstructor
public enum BpmDefinitionKeyEnum {
    /**
     * 客户资料审批流程标识
     */
    CUST_INFO_DEF("oa_cust"),

    /**
     * 供应商资料审批流程标识
     */
    VENDER_INFO_DEF("oa_vender"),

    /**
     * 借款申请单审批流程标识
     */
    LOAN_APP_DEF("");

    private final String definitionKey;

}
