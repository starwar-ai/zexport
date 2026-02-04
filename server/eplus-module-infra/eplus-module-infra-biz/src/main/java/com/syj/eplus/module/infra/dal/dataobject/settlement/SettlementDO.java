package com.syj.eplus.module.infra.dal.dataobject.settlement;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.syj.eplus.module.infra.api.settlement.dto.SystemCollectionPlanDTO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 结汇方式 DO
 *
 * @author eplus
 */
@TableName("system_settlement")
@KeySequence("system_settlement_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 结汇名称
     */
    private String name;
    /**
     * 结汇英文名称
     */
    private String nameEng;
    /**
     * 起始日类型
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer duration;

    @TableField(exist = false)
    private List<SystemCollectionPlanDTO> collectionPlanList;
}