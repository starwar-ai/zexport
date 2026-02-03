package com.syj.eplus.module.infra.dal.dataobject.version;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 版本记录 DO
 *
 * @author Zhangcm
 */

@TableName("infra_version")
@KeySequence("infra_version_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 前端版本
     */
    private String frontVer;
    /**
     * 后端版本
     */
    private String serverVer;
    /**
     * 发布版本
     */
    private String publishVer;
    /**
     * 发布更新明细
     */
    private String publishName;
    /**
     * 前端更新明细
     */
    private String frontDesc;
    /**
     * 前端更新明细
     */
    private String serverDesc;
    /**
     * 发布更新明细
     */
    private String publishDesc;
    /**
     * 是否显示
     */
    private Integer enabled;

}