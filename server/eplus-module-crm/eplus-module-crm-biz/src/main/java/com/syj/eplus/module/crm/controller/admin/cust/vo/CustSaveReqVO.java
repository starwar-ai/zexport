package com.syj.eplus.module.crm.controller.admin.cust.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.crm.entity.AddressShipping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 客户资料新增/修改 Request VO")
@Data
public class CustSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14045")
    private Long id;

    @Schema(description = "是否变更：0：否，1：是")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer ver;

    @Schema(description = "企业名称", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "企业名称不能为空")
    private String name;

    @Schema(description = "简称", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "简称不能为空")
    private String shortname;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "国家编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "4067")
//    @NotNull(message = "国家编码不能为空")
    private Long countryId;

    @Schema(description = "地区编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "28843")
//    @NotNull(message = "地区编码不能为空")
    private Integer regionId;

    @Schema(description = "官网")
    private String homepage;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "邮箱不能为空")
    private String email;

    @Schema(description = "客户类型（电商,进口商,零售商,贸易商,批发商,售后公司,邮购商）", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "客户类型不能为空")
    private List<Long> customerTypes;

    @Schema(description = "客户阶段（潜在客户，正式客户，退休客户）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "客户阶段不能为空")
    private Integer stageType;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "币种不能为空")
    private List<BaseValue> currencyList;

    @Schema(description = "运输方式(海运、陆运、空运、供应商送货)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "运输方式(海运、陆运、空运、供应商送货)不能为空")
    private Integer transportType;

    @Schema(description = "营业地址", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "营业地址不能为空")
    private String address;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "联系电话不能为空")
    private String phone;

    @Schema(description = "国外客户标志", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "国外客户标志不能为空")
    private Integer abroadFlag;

    @Schema(description = "客户来源", example = "1")
//    @NotNull(message = "客户来源不能为空")
    private Integer sourceType;

    @Schema(description = "启用：0：不启用，1：启用")
    private Integer creditFlag;
    //    @NotEmpty(message = "信用额度不能为空")
    @Schema(description = "信用额度")

    private JsonAmount creditLimit;

    @Schema(description = "是否是中信保：0：否，1：是", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "是否是中信保：0：否，1：是不能为空")
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
//    @NotNull(message = "是否代理不能为空")
    private Integer agentFlag;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "寄件地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "寄件地址不能为空")
    private List<AddressShipping> addressShipping;

    @Schema(description = "备用信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "remark")
    private String remark;

    @Schema(description = "付款方式编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String settleCode;

    @Schema(description = "收款方式", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "收款方式不能为空")
    private String settleName;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> managerIds;
    private List<BaseValue> managerList;

    @Schema(description = "转正标识")
    private Integer convertFlag;

    @Schema(description = "转正时间")
    private LocalDateTime convertTime;

    @Schema(description = "是否启用")
    private Integer enableFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "图片")
    private List<SimpleFile> picture;

    @Schema(description = "关联客户")
    private List<String> custLinkCode;

    @Schema(description = "正面唛头")
    private String mainMark;

    @Schema(description = "侧面唛头")
    private String sideMark;

    /**
     * 内部企业标识 0-否 1-是
     */
    private Integer internalFlag;

    /**
     * 内部企业主键
     */
    private Long internalCompanyId;

    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    private Long exmsExhibitionId;

    private Long exmsEventCategoryId;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;
}
