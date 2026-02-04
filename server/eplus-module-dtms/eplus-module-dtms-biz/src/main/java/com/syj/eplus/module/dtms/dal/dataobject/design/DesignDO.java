package com.syj.eplus.module.dtms.dal.dataobject.design;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设计-任务单 DO
 *
 * @author ePlus
 */

@TableName(value = "dtms_design",autoResultMap = true)
@KeySequence("dtms_design_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 单号
     */
    private String code;
    /**
     * 设计任务名称
     */
    private String name;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 任务状态：1：待提交，2：待审批，3：待完成，4：待评价，5：已完成，6：已作废，7：已驳回
     */
    private Integer designStatus;
    /**
     * 是否特批:0-否 1-是
     */
    private Integer specialPermissionFlag;
    /**
     * 特批原因
     */
    private String specialPermissionReason;
    /**
     * 设计任务类型（多选）1：亚马逊，2：阿里，3：拍照抠图P图，4：包材设计，5：不干胶设计及打印，6：视频拍摄制作，7：效果图设计，8：样本宣传页
     */
    private String designType;

    /**
     * 合同类型
     */
    private Integer contractType;

    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 期望完成时间
     */
    private LocalDateTime expectCompleteDate;
    /**
     * 预计完成日期
     */
    private LocalDateTime estimatedCompleteDate;
    /**
     * 实际完成时间
     */
    private LocalDateTime completeDate;
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    /**
     * 申请人主键
     */
    private Long applyDesignerId;
    /**
     * 申请人姓名
     */
    private String applyDesignerName;
    /**
     * 申请人部门主键
     */
    private Long applyDesignerDeptId;
    /**
     * 申请人部门名称
     */
    private String applyDesignerDeptName;
    /**
     * 设计要求
     */
    private String designRequirement;
    /**
     * 素材说明
     */
    private String materialDesc;
    /**
     * 认领人主键
     */
    private String designerIds;
    /**
     * 审批人主键
     */
    private Long auditId;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 结案原因
     */
    private String closeReason;
    /**
     * 备注
     */
    private String remark;

    /**
     * 指定设计师
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> specificDesigners;

    /**
     * 是否补单
     */
    private Integer isSupplementOrder;

}
