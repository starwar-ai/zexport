package com.syj.eplus.framework.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/7 18:06
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareField {

    /**
     * 字段名称
     */
    String value() default "";

    /**
     * 是否图片
     */
    boolean pictureFlag() default false;

    /**
     * 枚举类型
     */

    String enumType() default "";

    boolean ignore() default false;
}