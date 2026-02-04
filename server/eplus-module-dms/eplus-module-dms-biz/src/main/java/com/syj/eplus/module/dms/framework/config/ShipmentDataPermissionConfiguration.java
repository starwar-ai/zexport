//package com.syj.eplus.module.dms.framework.config;
//
//import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
//import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
//import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
//import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration(proxyBeanMethods = false)
//public class ShipmentDataPermissionConfiguration {
//    @Bean
//    public DeptDataPermissionRuleCustomizer shipmentDataPermissionRuleCustomizer() {
//        return rule -> {
//            // user
//            rule.addUserColumn(ShipmentDO.class, "purchase_user_list", PermissionTypeEnum.JSON_CONTAINS.getType());
//            rule.addUserColumn(ShipmentDO.class, "manager_list", PermissionTypeEnum.JSON_CONTAINS.getType());
//            rule.addUserColumn(ShipmentDO.class, "documenter", PermissionTypeEnum.SINGLE_JSON.getType());
//            rule.addUserColumn(ShipmentDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
//        };
//    }
//}