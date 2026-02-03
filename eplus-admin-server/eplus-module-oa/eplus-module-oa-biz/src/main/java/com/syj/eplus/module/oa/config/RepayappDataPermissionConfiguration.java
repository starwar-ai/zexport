package com.syj.eplus.module.oa.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：还款申请单数据权限配置类
 * @Author：du
 * @Date：2024/4/22 15:26
 */
@Configuration(proxyBeanMethods = false)
public class RepayappDataPermissionConfiguration  {

    @Bean
    public DeptDataPermissionRuleCustomizer repayappPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(RepayAppDO.class, "repayer", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(RepayAppDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
