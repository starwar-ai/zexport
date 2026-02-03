package com.syj.eplus.module.infra.dal.dataobject.companypath;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonCompanyPathTypeHandler;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import lombok.*;

/**
 * 抛砖路径 DO
 *
 * @author du
 */

@TableName(value = "company_path", autoResultMap = true)
@KeySequence("company_path_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPathDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 路径
     */
    @TableField(typeHandler = JsonCompanyPathTypeHandler.class)
    private JsonCompanyPath path;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 描述
     */
    private String description;
}
