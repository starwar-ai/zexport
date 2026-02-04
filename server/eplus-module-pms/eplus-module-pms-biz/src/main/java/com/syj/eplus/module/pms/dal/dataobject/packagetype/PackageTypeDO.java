package com.syj.eplus.module.pms.dal.dataobject.packagetype;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 包装方式 DO
 *
 * @author zhangcm
 */

@TableName("pms_package_type")
@KeySequence("pms_package_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageTypeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 包装方式编号
     */
    private String code;
    /**
     * 包装方式名称
     */
    private String name;
    /**
     * 包装方式英文名称
     */
    private String nameEng;

}