package com.syj.eplus.module.dtms.api.designitem.dto;

import lombok.Data;

/**
 * 设计任务明细 Response DTO
 * 用于 API 接口返回
 *
 * @author du
 */
@Data
public class DesignItemRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 设计任务ID
     */
    private Long designId;

    /**
     * 评价结果：0-无，1-点赞，2-投诉
     */
    private Integer evaluateResult;
}
