package com.syj.eplus.module.pjms.api.dto;

import lombok.Data;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-31
 * @Description: 项目dto
 */
@Data
public class ProjectDTO {

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
     * 主体id
     */
    private Long companyId;

}
