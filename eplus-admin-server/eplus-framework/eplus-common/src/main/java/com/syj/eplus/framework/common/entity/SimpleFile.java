package com.syj.eplus.framework.common.entity;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 17:44
 */
@Data
public class SimpleFile {
    /**
     * 文件编号
     */
    private Long id;
    /**
     * 原文件名
     */
    private String name;
    /**
     * 文件 URL
     */
    private String fileUrl;
    /**
     * 是否主图
     */
    private Integer mainFlag;
    /**
     * 来源主键
     */
    private Long sourceId;
}
