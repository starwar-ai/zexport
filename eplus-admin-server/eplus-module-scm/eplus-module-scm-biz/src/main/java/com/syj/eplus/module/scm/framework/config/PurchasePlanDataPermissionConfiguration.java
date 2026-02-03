package com.syj.eplus.module.scm.framework.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PurchasePlanDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer purchasePlanDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(PurchasePlanDO.class, "sales", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(PurchasePlanDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(PurchasePlanDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(PurchasePlanDO.class, "auxiliary_manager", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(PurchasePlanDO.class, "auxiliary_sales", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(PurchasePlanDO.class, "auxiliary_purchase_user", PermissionTypeEnum.JSON_CONTAINS.getType());
        };
    }
}