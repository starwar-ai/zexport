package com.syj.eplus.module.crm.dal.dataobject.mark;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 唛头 DO
 *
 * @author du
 */
@TableName("crm_mark")
@KeySequence("crm_mark_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkDO extends BaseDO {

    /**
     * 唛头id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 唛头名称
     */
    private String name;
    /**
     * 版本号
     */
    private Integer ver;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 唛头英文名称
     */
    private String engName;
    /**
     * 主文字唛
     */
    private String mainMarkText;
    /**
     * 主图形唛
     */
    private String mainMarkPic;
    /**
     * 主侧文字唛
     */
    private String mainMarkTextSide;
    /**
     * 主侧图形唛
     */
    private String mainMarkPicSide;
    /**
     * 内主文字唛
     */
    private String mainMarkTextIn;
    /**
     * 内主图形唛
     */
    private String mainMarkPicIn;
    /**
     * 内侧文字唛
     */
    private String mainMarkTextSideIn;
    /**
     * 内侧图形唛
     */
    private String mainMarkPicSideIn;

}