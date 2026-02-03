package com.syj.eplus.module.infra.api.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/25 11:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementDTO {
    /**
     * 结汇方式id
     */
    private Long id;
    /**
     * 结汇方式名称
     */
    private String name;
    /**
     * 结汇方式英文名称
     */
    private String nameEng;

    /**
     * 收款计划列表
     */
    private List<SystemCollectionPlanDTO> collectionPlanList;
}
