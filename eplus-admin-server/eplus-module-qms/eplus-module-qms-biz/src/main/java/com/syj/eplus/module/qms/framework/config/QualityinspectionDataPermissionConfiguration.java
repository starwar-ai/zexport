package com.syj.eplus.module.qms.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class QualityinspectionDataPermissionConfiguration {
    @Bean
    public DeptDataPermissionRuleCustomizer qualityinspectionDataPermissionRuleCustomizer() {
        return rule -> {
            // user
            rule.addUserColumn(QualityInspectionDO.class, "apply_inspector_id", PermissionTypeEnum.FIND_IN_SET.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(QualityInspectionDO.class, "inspector_id", PermissionTypeEnum.FIND_IN_SET.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(QualityInspectionDO.class, "purchase_user", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
            rule.addUserColumn(QualityInspectionDO.class, "sales", PermissionTypeEnum.SINGLE_JSON.getType(), BooleanEnum.YES.getValue());
        };
    }
}
