package com.syj.eplus.module.oa.dal.dataobject.apply;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OA申请单 DO
 *
 * @author ePlus
 */

@TableName(value = "oa_apply", autoResultMap = true)
@KeySequence("oa_apply_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 拟达成目标
     */
    private String intendedObjectives;
    /**
     * 单据编号
     */
    private String code;
    /**
     * 企微申请单id
     */
    private String wecomId;
    /**
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    /**
     * 出差事由
     */
    private String purpose;
    /**
     * 出差地点
     */
    private String dest;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 出差时长(秒)
     */
    private Integer duration;
    /**
     * 交通工具
     */
    private Integer transportationType;
    /**
     * 同行人员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> entertainEntourage;
    /**
     * 招待对象等级
     */
    private Integer entertainLevel;
    /**
     * 招待人数
     */
    private Integer entertainNum;
    /**
     * 招待日期
     */
    private LocalDateTime entertainTime;
    /**
     * 招待对象名称
     */
    private String entertainName;
    /**
     * 招待对象类型
     */
    private Integer entertainType;
    /**
     * 一般费用名称
     */
    private String generalExpense;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 是否申请报销
     */
    private Integer isApplyExpense;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    @Schema(description = "申请单类型", example = "2")
    private Integer applyType;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

    private String amountDesc;
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept actualUser;


    private Long companyId;
    
    private String companyName;

    @Schema(description = "出差类型")
    private Integer travelType;

    /**
     * 申请报销次数
     */
    private Integer applyExpenseTimes;

}