package com.syj.eplus.module.oa.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.oa.dal.dataobject.apply.ApplyDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-08-01
 * @Description: 招待申请，费用支出申请，出差申请数据权限
 */
@Configuration(proxyBeanMethods = false)
public class ApplyDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer applyPermissionConfiguration() {
        return rule -> {
            // user
            rule.addUserColumn(ApplyDO.class, "actual_user", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(ApplyDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
