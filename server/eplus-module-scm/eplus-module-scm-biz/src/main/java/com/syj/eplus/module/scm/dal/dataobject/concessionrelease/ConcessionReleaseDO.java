package com.syj.eplus.module.scm.dal.dataobject.concessionrelease;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.util.List;

/**
 * 让步放行 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_concession_release",autoResultMap = true)
@KeySequence("scm_concession_release_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcessionReleaseDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 验货单主键
     */
    private Long qualityInspectionId;

    /**
     * 验货单明细主键
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> qualityInspectionItemIds;
    /**
     * 保函标记
     */
    private Integer annexFlag;
    /**
     * 保函
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 让步描述
     */
    private String description;

    /**
     * 流程实例id
     */
    private String processInstanceId;

}