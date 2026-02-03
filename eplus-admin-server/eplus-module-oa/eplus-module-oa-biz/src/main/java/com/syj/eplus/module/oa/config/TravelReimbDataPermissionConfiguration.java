package com.syj.eplus.module.oa.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：差旅费报销数据权限控制类
 * @Author：du
 * @Date：2024/3/4 15:09
 */
@Configuration(proxyBeanMethods = false)
public class TravelReimbDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer travelReimbPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(ReimbDO.class, "reimb_user", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(ReimbDO.class, "actual_user", PermissionTypeEnum.SINGLE_JSON.getType());
            rule.addUserColumn(ReimbDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
            rule.addUserColumn(ReimbDO.class, "approve_user", PermissionTypeEnum.SINGLE_JSON.getType());
        };
    }

}