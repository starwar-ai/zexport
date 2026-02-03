package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/15 15:27
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class JsonCompanyPath {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 利润分配类型(allocate_type)：1-不分配 2-固定利润率 3-随机利润率
     */
    private Integer allocateType;

    /**
     * 分配范围-低(包含)
     */
    private Double rangeMinRatio;

    /**
     * 分配范围-高(不包含)
     */
    private Double rangeMaxRatio;

    /**
     * 固定利润率
     */
    private Double fixRatio;

    /**
     * 利润分配条件-类型(allocate_condition_type) 1-无 2-> 3->= 4-< 5-<=
     */
    private Integer allocateConditionType;

    /**
     * 分配条件-值
     */
    private Double allocateConditionValue;

    /**
     * 可加工标记
     */
    private Integer processedFlag;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 父节点
     */
    private Long parentId;

    /**
     * 下一个节点
     */
    private JsonCompanyPath next;
}
