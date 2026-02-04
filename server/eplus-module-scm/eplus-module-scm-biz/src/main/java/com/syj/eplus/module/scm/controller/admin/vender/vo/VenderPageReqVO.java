package com.syj.eplus.module.scm.controller.admin.vender.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 供应商信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VenderPageReqVO extends PageParam {

    @Schema(description = "是否变更")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "供应商编码")
    private String code;

    @Schema(description = "供应商名称", example = "赵六")
    private String name;

    @Schema(description = "供应商英文名称")
    private String nameEng;

    @Schema(description = "供应商简称")
    private String nameShort;

    @Schema(description = "城市", example = "6751")
    private Integer areaId;

    @Schema(description = "企业地址")
    private String address;

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "企业电话")
    private String phone;

    @Schema(description = "采购员编号", example = "7438")
    private Long buyerId;

    @Schema(description = "采购员部门", example = "7438")
    private Long buyerDeptId;

    @Schema(description = "是否境外供应商")
    private Integer abroadFlag;

    @Schema(description = "国家编号", example = "16607")
    private Long countryId;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "税率")
    private Double taxRate;

    @Schema(description = "发票类型", example = "2")
    private Integer taxType;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "银行")
    private String bank;

    @Schema(description = "银行账号", example = "7772")
    private String bankAccount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> buyerIds;

    @Schema(description = "供应商阶段")
    private Integer stageType;

    @Schema(description = "是否启用")
    private Integer enableFlag;

    @Schema(description = "是否过滤税率为空")
    private Integer rateFlag;

    @Schema(description = "供应商类型")
    private Integer venderType;

    @Schema(description = "行政供应商类型")
    private Integer administrationVenderType;

    @Schema(description = "采购员编码", example = "27774")
    private Long purchaseUserId;

    @Schema(description = "采购员部门编码", example = "4724")
    private Long purchaseUserDeptId;

    @Schema(description = "是否SKU报价")
    private Integer skuQuoteFlag;

    @Schema(description = "供应商阶段标识")
    private Integer stageTypeFlag;
}