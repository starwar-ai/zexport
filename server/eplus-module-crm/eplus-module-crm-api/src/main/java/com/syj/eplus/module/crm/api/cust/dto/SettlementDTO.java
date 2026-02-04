package com.syj.eplus.module.crm.api.cust.dto;

import lombok.Data;

import java.util.List;

/**
 * 结汇方式 DO
 *
 * @author eplus
 */
@Data
public class SettlementDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 结汇名称
     */
    private String name;
    /**
     * 结汇英文名称
     */
    private String nameEng;
    /**
     * 起始日类型
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer duration;

    private List<SystemCollectionPlanDTO> collectionPlanList;

    /**
     * 是否默认
     */
    private Integer defaultFlag;
}