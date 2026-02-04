package com.syj.eplus.module.wms.dal.dataobject.warehouse;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonLongListTypeHandler;
import lombok.*;

import java.util.List;

/**
 * 仓库管理-仓库 DO
 *
 * @author Rangers
 */

@TableName(value ="wms_warehouse", autoResultMap = true)
@KeySequence("wms_warehouse_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 仓库编码
     */
    private String code;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 仓库地址
     */
    private String address;
    /**
     * 启用标识  0-否 1-是
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Integer enableFlag;
    /**
     * 仓管主键
     */
    @TableField(typeHandler = JsonLongListTypeHandler.class)
    private List<Long> managerIds;
    /**
     * 供应仓标识0-否 1-是
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Integer venderFlag;
    /**
     * 供应商编码
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 默认仓库标记
     */
    private Integer defaultFlag;
}
