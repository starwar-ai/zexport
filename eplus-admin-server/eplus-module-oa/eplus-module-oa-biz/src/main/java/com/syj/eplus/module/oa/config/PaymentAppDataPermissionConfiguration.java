package com.syj.eplus.module.oa.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PaymentAppDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer paymentAppPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(PaymentAppDO.class, "applyer", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(PaymentAppDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
