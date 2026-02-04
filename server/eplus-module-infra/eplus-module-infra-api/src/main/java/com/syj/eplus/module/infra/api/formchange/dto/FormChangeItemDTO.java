package com.syj.eplus.module.infra.api.formchange.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FormChangeItemDTO {
    private Long id;
    /**
     * 主表名
     */
    private String tableName;

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段英文名称
     */
    private String nameEng;

    /**
     * 变更级别
     */
    private Integer changeLevel;

    /**
     * 是否影响主流程
     */
    private Integer effectMainInstanceFlag;

    /**
     * 影响范围 （销售，采购，出运）
     */
    private List<Integer> effectRange;

    private LocalDateTime createTime;
}
