package com.syj.eplus.module.sms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SaleContractDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer saleContractDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(SaleContractDO.class, "sales", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(SaleContractDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(SaleContractDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(SaleContractDO.class, "manager", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
        };
    }

}