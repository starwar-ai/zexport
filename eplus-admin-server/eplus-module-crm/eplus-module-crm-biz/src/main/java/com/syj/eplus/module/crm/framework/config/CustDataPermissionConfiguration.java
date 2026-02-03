package com.syj.eplus.module.crm.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/4 15:09
 */
@Configuration(proxyBeanMethods = false)
public class CustDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer custDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(CustDO.class, "manager_ids", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(CustDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}