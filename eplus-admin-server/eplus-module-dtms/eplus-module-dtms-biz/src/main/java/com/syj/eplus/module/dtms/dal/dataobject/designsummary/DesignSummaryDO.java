package com.syj.eplus.module.dtms.dal.dataobject.designsummary;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 设计-工作总结 DO
 *
 * @author ePlus
 */

@TableName("dtms_design_summary")
@KeySequence("dtms_design_summary_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignSummaryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 设计任务单主键
     */
    private Long designId;
    /**
     * 设计任务单-明细主键
     */
    private Long designItemId;
    /**
     * 认领人主键
     */
    private Long designerId;
    /**
     * 认领人姓名
     */
    private String designerName;
    /**
     * 认领人部门主键
     */
    private Long designerDeptId;
    /**
     * 认领人部门名称
     */
    private String designerDeptName;
    /**
     * 当前进度
     */
    private String progress;
    /**
     * 进度描述
     */
    private String progressDesc;
    /**
     * 备注
     */
    private String remark;

}
