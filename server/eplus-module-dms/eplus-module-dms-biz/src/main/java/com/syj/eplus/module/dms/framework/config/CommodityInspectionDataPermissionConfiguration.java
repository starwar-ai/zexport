package com.syj.eplus.module.dms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspection.CommodityInspectionDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CommodityInspectionDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer commodityInspectionDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(CommodityInspectionDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(CommodityInspectionDO.class, "manager_list", PermissionTypeEnum.JSON_CONTAINS.getType());
            rule.addUserColumn(CommodityInspectionDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
        };
    }
}