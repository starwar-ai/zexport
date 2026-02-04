package com.syj.eplus.module.scm.controller.admin.concessionrelease.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 让步放行分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConcessionReleasePageReqVO extends PageParam {



    private List<Long> qualityInspectionIdList;

    @Schema(description = "保函标记")
    private Integer annexFlag;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "让步描述")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "供应商主键")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "验货人")
    private Long inspectorId;

    @Schema(description = "验货时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] checkTime;

    private Long skuId;
    private String skuCode;
    private String skuName;

}