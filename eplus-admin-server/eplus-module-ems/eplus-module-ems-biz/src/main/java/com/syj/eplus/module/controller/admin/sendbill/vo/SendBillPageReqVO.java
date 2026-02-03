package com.syj.eplus.module.controller.admin.sendbill.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 寄件导入单据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SendBillPageReqVO extends PageParam {

    @Schema(description = "日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] billDate;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "运单号码")
    private String code;

    @Schema(description = "费用(元)")
    private BigDecimal cost;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "快递公司id")
    private Long venderId;

    @Schema(description = "快递公司编号")
    private String venderCode;

    @Schema(description = "快递公司名称")
    private String venderName;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}