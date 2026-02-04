package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开票通知分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvoicingNoticesPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "归属公司主键", example = "12936")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "李四")
    private String companyName;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "供应商编号", example = "张三")
    private String venderCode;

    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "跟单员")
    private String managerName;

    @Schema(description = "跟单员主键", example = "12936")
    private Long managerId;


    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "不包含的状态")
    private List<Integer> excludeAuditStatus;

    @Schema(description = "出运日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] shipDate;

    @Schema(description = "录入人", example = "12936")
    private Long inputUserId;

    @Schema(description = "采购单号")
    private String purOrderCode;

    @Schema(description = "开票状态")
    private Integer status;

}