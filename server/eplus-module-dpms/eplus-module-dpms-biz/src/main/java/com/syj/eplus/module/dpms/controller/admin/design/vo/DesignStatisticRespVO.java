package com.syj.eplus.module.dpms.controller.admin.design.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/16:23
 * @Description:
 */
@Data
@Schema(description = "设计统计信息")
public class DesignStatisticRespVO {
    /**
     * 设计总任务
     */
    private Long designCount;

    /**
     * 总点赞量
     */
    private Long thumbUpCount;

    /**
     * 处理任务
     */
    private Long finishCount;

    /**
     * 总投诉
     */
    private Long complaintCount;

    /**
     * 特批任务
     */
    private Long specialApproveCount;
}
