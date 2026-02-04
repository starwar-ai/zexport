package cn.iocoder.yudao.framework.security.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description：字段权限注解
 * @Author：du
 * @Date：2024/1/26 11:08
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPermission {
    String[] value() default "";
}
