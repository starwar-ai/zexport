package com.syj.eplus.module.pms.dal.dataobject.category;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 产品分类 DO
 *
 * @author eplus
 */
@TableName("pms_category")
@KeySequence("pms_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 商品分类编号
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
     * 海关编码
     */
    private Long hsCodeId;
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

    /**
     * 海关编码编号
     */
    private String hsDataCode;
}