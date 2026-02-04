package com.syj.eplus.module.dtms.dal.dataobject.designitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 设计-认领明细 DO
 *
 * @author ePlus
 */

@TableName("dtms_design_item")
@KeySequence("dtms_design_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignItemDO extends BaseDO {

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
     * 计划开始时间
     */
    private LocalDateTime planStartDate;
    /**
     * 计划完成时间
     */
    private LocalDateTime planCompleteDate;
    /**
     * 完成标识 0-否 1-是
     */
    private Integer completeFlag;
    /**
     * 设计任务类型1：常规任务，2：临时任务
     */
    private Integer itemType;
    /**
     * 设计文件位置
     */
    private String designFilePath;
    /**
     * 认领时间
     */
    private LocalDateTime acceptDate;
    /**
     * 完成时间
     */
    private LocalDateTime completeDate;
    /**
     * 评价结果；1：投诉，2：优秀，3：点赞
     */
    private Integer evaluateResult;
    /**
     * 评价描述
     */
    private String evaluateDesc;
    /**
     * 备注
     */
    private String remark;

}
