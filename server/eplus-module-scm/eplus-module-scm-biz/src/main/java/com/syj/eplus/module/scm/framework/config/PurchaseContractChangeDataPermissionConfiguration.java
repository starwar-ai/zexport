package com.syj.eplus.module.scm.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PurchaseContractChangeDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer purchaseContractChangeDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(PurchaseContractChange.class, "manager", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(PurchaseContractChange.class, "purchase_user_id", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(PurchaseContractChange.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }
}