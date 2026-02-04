package com.syj.eplus.framework.common.entity;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 19:22
 */
@Data
public class ChangeRecord {
    /**
     * 序号
     */
    private String sequence;
    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 变更前值
     */
    private String oldValue;

    /**
     * 当前值
     */
    private String value;

    /**
     * 图片标识
     */
    private Boolean pictureFlag;

    /**
     * 操作标识
     */
    private Integer operateType;
}
