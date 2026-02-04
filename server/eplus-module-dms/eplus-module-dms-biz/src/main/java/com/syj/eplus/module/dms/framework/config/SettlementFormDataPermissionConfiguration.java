package com.syj.eplus.module.dms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.dms.dal.dataobject.settlementform.SettlementFormDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SettlementFormDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer settlementFormDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(SettlementFormDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(SettlementFormDO.class, "manager_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(SettlementFormDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }
}