package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购跟单登记分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseRegistrationPageReqVO extends PageParam {

    @Schema(description = "付款主体主键", example = "10997")
    private Long companyId;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "税票编号")
    private String invoiceCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


    @Schema(description = "id数组")
    private Long[] idList;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    @Schema(description = "开票品名")
    private String invoicSkuName;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    private Boolean isTree ;
    @Schema(description = "跟单员")
    private Long managerId;

    private Integer status;

    private Integer neStatus;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;
}