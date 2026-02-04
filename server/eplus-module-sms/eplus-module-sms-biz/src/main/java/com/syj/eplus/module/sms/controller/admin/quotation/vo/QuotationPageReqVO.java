package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 报价单主分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuotationPageReqVO extends PageParam {

    @Schema(description = "内部法人单位主键", example = "31865")
    private Long companyId;

    @Schema(description = "内部法人单位名称", example = "李四")
    private String companyName;

    @Schema(description = "客户主键", example = "29787")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "是否新客户")
    private Integer isNewCust;

    @Schema(description = "价格条款", example = "2")
    private String settlementTermType;

    @Schema(description = "客户联系人")
    private String custPoc;

    @Schema(description = "国家id", example = "26126")
    private Long countryId;

    @Schema(description = "出运口岸主键", example = "29636")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", example = "张三")
    private String departurePortName;

    @Schema(description = "有效期止")
    private LocalDateTime validPeriod;

    @Schema(description = "业务员")
    private String manager;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "报价单号")
    private Integer code;

    /**
     * 客户货号
     */
    @Schema(description = "客户货号")
    private String cskuCode;


    /**
     * 基础产品编号
     */
    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "id数组")
    private Long[] idList;
}