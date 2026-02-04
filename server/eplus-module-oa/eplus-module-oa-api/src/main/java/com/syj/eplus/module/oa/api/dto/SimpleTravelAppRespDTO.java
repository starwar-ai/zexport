package com.syj.eplus.module.oa.api.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimpleTravelAppRespDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 出差事由
     */
    private String purpose;

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
    private List<UserDept> companions;

    /**
     * 目的地
     */
    private String dest;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 拟达成目标
     */
    private String intendedObjectives;
}
