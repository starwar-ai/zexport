package com.syj.eplus.module.scm.framework.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PurchaseContractDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer purchaseContractDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(PurchaseContractDO.class, "manager", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "purchase_user_id", PermissionTypeEnum.FIND_IN_SET.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "sales", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "auxiliary_purchase_user", PermissionTypeEnum.JSON_CONTAINS.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "auxiliary_sales", PermissionTypeEnum.JSON_CONTAINS.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(PurchaseContractDO.class, "auxiliary_manager", PermissionTypeEnum.JSON_CONTAINS.getType(), BooleanEnum.YES.getValue());
        };
    }
}