package com.syj.eplus.framework.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/28 11:00
 */
@Data
@ToString
@EqualsAndHashCode
public class SimpleData {
    /**
     * 主键
     */
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 英文名称
     */
    private String nameEng;
}
