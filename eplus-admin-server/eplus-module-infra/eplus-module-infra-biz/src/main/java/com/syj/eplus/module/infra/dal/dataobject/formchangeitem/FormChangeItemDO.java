package com.syj.eplus.module.infra.dal.dataobject.formchangeitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.syj.eplus.framework.common.config.handler.IntegerListTypeHandler;
import lombok.*;

import java.util.List;

/**
 * 表单变更明细 DO
 *
 * @author ePlus
 */
@TableName(value = "system_form_change_item", autoResultMap = true)
@KeySequence("system_form_change_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormChangeItemDO extends BaseDO {

    /**
     * id
     */
    @TableId
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
//    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = JacksonTypeHandler.class)
    @TableField(typeHandler = IntegerListTypeHandler.class)
    private List<Integer> effectRange;

}
