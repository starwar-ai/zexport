//package com.syj.eplus.module.scm.framework.config;
//
//import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
//import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
//import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @Description：
// * @Author：du
// * @Date：2024/3/13 9:30
// */
//@Configuration(proxyBeanMethods = false)
//public class VenderDataPermissionConfiguration {
//    @Bean
//    public DeptDataPermissionRuleCustomizer venderDeptDataPermissionRuleCustomizer() {
//        return rule -> {
//            // user
//            rule.addUserColumn(VenderDO.class, "buyer_ids", PermissionTypeEnum.FIND_IN_SET.getType());
//            rule.addUserColumn(VenderDO.class, "creator", PermissionTypeEnum.FIND_IN_SET.getType());
//        };
//    }
//}
