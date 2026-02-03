package cn.iocoder.yudao.framework.security.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description：接口字段权限控制开关
 * @Author：du
 * @Date：2024/1/26 11:12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPermissionEnable {
}
