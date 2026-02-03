package com.syj.eplus.module.infra.dal.dataobject.orderlink;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.syj.eplus.framework.common.config.handler.JsonObjectListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonObjectTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

@TableName(value = "system_order_link",autoResultMap = true)
@KeySequence("system_order_link_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLink extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务主键
     */
    private Long businessId;

    /**
     * 业务编号
     */
    private String code;

    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 业务名称
     */
    private String name;

    /**
     * 链路编号
     */
    private String linkCode;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 子项
     */
    @TableField(typeHandler = JsonObjectListTypeHandler.class)
    private Object item;

    /**
     * 父节点编号
     */
    private String parentCode;

    /**
     * 主体编号
     */
    private Long companyId;

    /**
     * 主体名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 业务对象名称
     */
    private String businessSubjectName;

    /**
     * 订单信息
     */
    @TableField(typeHandler = JsonObjectTypeHandler.class)
    private Object orderMsg;
}
