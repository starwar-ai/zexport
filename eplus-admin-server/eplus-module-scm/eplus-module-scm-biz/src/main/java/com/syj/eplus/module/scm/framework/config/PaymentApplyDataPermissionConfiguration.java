package com.syj.eplus.module.scm.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PaymentApplyDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer paymentApplyDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(PaymentApplyDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(PaymentApplyDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }
}