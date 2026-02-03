package com.syj.eplus.module.infra.dal.dataobject.card;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

/**
 * 首页卡片 DO
 *
 * @author du
 */

@TableName(value = "system_card", autoResultMap = true)
@KeySequence("system_card_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile picture;
    /**
     * 是否启用
     * <p>
     * 枚举 {@link TODO is_int 对应的类}
     */
    private Integer status;

    /**
     * 索引
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