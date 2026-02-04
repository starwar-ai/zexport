package com.syj.eplus.module.infra.dal.dataobject.countryinfo;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 国家信息 DO
 *
 * @author du
 */
@TableName("system_country_info")
@KeySequence("system_country_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfoDO extends BaseDO {

    /**
     * 国家id
     */
    @TableId
    private Long id;
    /**
     * 国家名称
     */
    private String name;
    /**
     * 国家编码
     */
    private String code;
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区号
     */
    private String areaCode;

}
