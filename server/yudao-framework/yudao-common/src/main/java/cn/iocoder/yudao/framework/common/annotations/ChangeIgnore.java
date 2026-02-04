package cn.iocoder.yudao.framework.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeIgnore {
    /**
     * 是否忽略
     */
    boolean ignore() default true;
}
