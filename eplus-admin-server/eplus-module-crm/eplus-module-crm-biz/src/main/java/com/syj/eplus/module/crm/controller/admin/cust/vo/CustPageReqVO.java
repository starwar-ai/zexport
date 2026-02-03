package com.syj.eplus.module.crm.controller.admin.cust.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.crm.entity.AddressShipping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户资料分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustPageReqVO extends PageParam {

    @Schema(description = "是否变更")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本号")
    private Integer ver;

    @Schema(description = "客户名称")
    private String name;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "简称")
    private String shortname;

    @Schema(description = "客户编号")
    private String code;

    @Schema(description = "国家编码", example = "4067")
    private Long countryId;

    @Schema(description = "地区编码", example = "28843")
    private Integer regionId;

    @Schema(description = "官网")
    private String homepage;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "客户类型（电商,进口商,零售商,贸易商,批发商,售后公司,邮购商）")
    private List<Long> customerTypes;

    @Schema(description = "客户阶段（潜在客户，正式客户，退休客户）", example = "1")
    private Integer stageType;

    @Schema(description = "币种")
    private List<BaseValue> currencyList;

    @Schema(description = "支付方式")
    private Long paymentMethod;

    @Schema(description = "运输方式(海运、陆运、空运、供应商送货)", example = "2")
    private Integer transportType;

    @Schema(description = "营业地址")
    private String address;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "国外客户标志")
    private Integer abroadFlag;

    @Schema(description = "客户来源", example = "1")
    private Integer sourceType;

    @Schema(description = "启用信用额度：0：不启用，1：启用")
    private Integer creditFlag;

    @Schema(description = "信用额度")
    private JsonAmount creditLimit;

    @Schema(description = "是否是中信保：0：否，1：是")
    private Integer zxbquotaFlag;

    @Schema(description = "价格条款  CIF,FOB,CIP,DDP,DAP,DAT,CFR", example = "2")
    private String settlementTermType;

    @Schema(description = "开票抬头")
    private String invoiceHeader;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "唛头id", example = "28789")
    private Integer markId;

    @Schema(description = "是否代理  0:否， 1：是")
    private Integer agentFlag;

    @Schema(description = "业务员列表")
    private List<Long> managerIds;

    @Schema(description = "业务员部门")
    private String managerDeptId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "寄件地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private List<AddressShipping> addressShipping;

    @Schema(description = "备用信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "remark")
    private String remark;

    @Schema(description = "是否启用")
    private Integer enableFlag;

    @Schema(description = "是否包含内部客户")
    private Integer internalFlag;

    @Schema(description = "客户名称及编号")
    private String nameCode;

    @Schema(description = "客户阶段标识")
    private Integer stageTypeFlag;

}