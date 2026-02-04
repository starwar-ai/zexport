package cn.iocoder.yudao.module.system.framework.datapermission.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(AdminUserDO.class, "id", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(AdminUserDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }

}
