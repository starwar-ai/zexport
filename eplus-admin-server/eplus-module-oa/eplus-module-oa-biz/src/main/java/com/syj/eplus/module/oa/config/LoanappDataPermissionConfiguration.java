package com.syj.eplus.module.oa.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：借款申请单数据权限配置类
 * @Author：du
 * @Date：2024/4/22 15:24
 */
@Configuration(proxyBeanMethods = false)
public class LoanappDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer loanappPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(LoanAppDO.class, "applyer", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(LoanAppDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
