package com.syj.eplus.module.dtms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/4 15:09
 */
@Configuration(proxyBeanMethods = false)
public class DesignDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer designDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(DesignDO.class, "apply_designer_id", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(DesignDO.class, "designer_ids", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(DesignDO.class, "specific_designers", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(DesignDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
