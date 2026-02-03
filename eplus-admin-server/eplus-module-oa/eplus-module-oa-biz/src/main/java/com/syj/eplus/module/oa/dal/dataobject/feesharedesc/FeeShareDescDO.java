package com.syj.eplus.module.oa.dal.dataobject.feesharedesc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 费用归集具体名称 DO
 *
 * @author zhangcm
 */

@TableName("oa_fee_share_desc")
@KeySequence("oa_fee_share_desc_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeShareDescDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 排序
     */
    private Integer sortNum;
    /**
     * 费用归属类别
     */
    private Integer feeShareType;
    /**
     * 相关方类别
     */
    private Integer relationType;
    /**
     * 名称
     */
    private String name;

}