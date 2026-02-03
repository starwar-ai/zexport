package com.syj.eplus.module.infra.dal.dataobject.formchange;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 表单字段变更管理 DO
 *
 * @author ePlus
 */

@TableName(value = "system_form_change", autoResultMap = true)
@KeySequence("system_form_change_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormChangeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 描述
     */
    private String description;
    /**
     * 表单名称
     */
    private String name;
    /**
     * 流程实例key
     */
    private String modelKey;
    /**
     * 流程是否开启
     */
    private Integer instanceFlag;

    /**
     * 是否参与主流程
     */
    private Integer mainInstanceFlag;

}