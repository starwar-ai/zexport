package com.syj.eplus.module.oa.dal.dataobject.travelapp;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出差申请单 DO
 *
 * @author ePlus
 */
@TableName(value = "oa_travel_app", autoResultMap = true)
@KeySequence("oa_travel_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelAppDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 企微申请单id
     */
    private String wecomId;
    /**
     * 申请人
     */
    private Long applyerId;
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
     * 审核状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
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
    private Long duration;
    /**
     * 交通工具
     */
    private Integer transportationType;
    /**
     * 同行人员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> companions;

    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 拟达成目标
     */
    private String intendedObjectives;
}