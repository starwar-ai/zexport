package com.syj.eplus.module.infra.api.card.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页卡片 Response DTO
 * 用于 API 接口返回
 *
 * @author du
 */
@Data
public class CardRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    /**
     * 接口url
     */
    private String url;

    /**
     * 是否启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 唯一标识
     */
    private String uniqueCode;

    /**
     * 页签id
     */
    private Long tabId;

    /**
     * X轴
     */
    private Integer x;

    /**
     * Y轴
     */
    private Integer y;

    /**
     * 高度
     */
    private Integer h;

    /**
     * 宽度
     */
    private Integer w;

    /**
     * 条件
     */
    private String filterCondition;

    /**
     * 类型
     */
    private String type;

    /**
     * 组件
     */
    private String currentComponent;

    /**
     * 标题标识
     */
    private Boolean titleFlag;

    /**
     * 是否基础卡片
     */
    private Integer basicFlag;
}
