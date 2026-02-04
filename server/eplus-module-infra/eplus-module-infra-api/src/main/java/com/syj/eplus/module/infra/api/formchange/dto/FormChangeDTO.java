package com.syj.eplus.module.infra.api.formchange.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FormChangeDTO {
    private Long id;

    private Integer mainInstanceFlag;

    private String description;

    private String name;

    /**
     * 流程实例id
     */
    private String modelKey;

    private Integer instanceFlag;

    private LocalDateTime createTime;

    private List<FormChangeItemDTO> children;
}
