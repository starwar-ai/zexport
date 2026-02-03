package com.syj.eplus.framework.common.entity;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/7 18:07
 */
@Data
public class CompareEntity {
    /**
     * 字段
     */
    private String fieldKey;

    /**
     * 字段值
     */
    private Object fieldValue;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 是否图片
     */
    private Boolean pictureFlag;

    /**
     * 枚举类型
     */
    private String enumType;

}
