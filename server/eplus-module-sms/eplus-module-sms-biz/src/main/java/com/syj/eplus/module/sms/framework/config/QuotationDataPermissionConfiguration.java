package com.syj.eplus.module.sms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.sms.dal.dataobject.quotation.QuotationDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/4 15:09
 */
@Configuration(proxyBeanMethods = false)
public class QuotationDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer quotationDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(QuotationDO.class, "manager", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(QuotationDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}