package com.syj.eplus.module.crm.dal.dataobject.category;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 客户分类 DO
 *
 * @author eplus
 */
@TableName("crm_category")
@KeySequence("crm_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCategoryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 分类编号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 流水号长度
     */
    private Integer codeLen;
    /**
     * 父节点编号
     */
    private Long parentId;

    /**
     * 种类
     */
    private Integer categoryType;

    /**
     * 级别
     */
    private Integer grade;
}