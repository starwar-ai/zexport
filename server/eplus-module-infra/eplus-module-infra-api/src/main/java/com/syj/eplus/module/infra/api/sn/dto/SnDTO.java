package com.syj.eplus.module.infra.api.sn.dto;

import lombok.Data;

/**
 * 序列号 DTO
 */
@Data
public class SnDTO {

    /**
     * 序列号
     */
    private Integer sn;

    /**
     * 类型
     */
    private String type;

    /**
     * 编码前缀
     */
    private String codePrefix;
} 