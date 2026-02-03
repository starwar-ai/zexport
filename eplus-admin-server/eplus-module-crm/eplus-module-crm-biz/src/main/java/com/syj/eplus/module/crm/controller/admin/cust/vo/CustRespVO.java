package com.syj.eplus.module.crm.controller.admin.cust.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptListConverter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.crm.entity.AddressShipping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 客户资料 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class CustRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14045")
    @ExcelProperty("主键")
    @ExcelIgnore
    private Long id;

    @Schema(description = "是否变更")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    @ExcelIgnore
    private Integer ver;

    @Schema(description = "企业名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("企业名称")
    private String name;

    @Schema(description = "简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("简称")
    private String shortname;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    private String code;

    @Schema(description = "国家编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "4067")
    private Long countryId;

    @Schema(description = "国家名称")
    @ExcelProperty("国家")
    private String countryName;

    @Schema(description = "地区名称")
    @ExcelProperty("地区")
    private String areaName;

    @Schema(description = "官网")
    @ExcelProperty("官网")
    @ExcelIgnore
    private String homepage;

    @Schema(description = "电子邮件", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("电子邮件")
    private String email;

    @Schema(description = "客户类型（电商,进口商,零售商,贸易商,批发商,售后公司,邮购商）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "客户类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CUSTOM_TYPE)
    private List<Long> customerTypes;

    @Schema(description = "客户阶段（潜在客户，正式客户，退休客户）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "客户阶段", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CUSTOMER_STAGEE)
    private Integer stageType;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("币种")
    private List<BaseValue> currencyList;

    @Schema(description = "支付方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("支付方式")
    private Long paymentMethod;

//    @Schema(description = "运输方式(海运、陆运、空运、供应商送货)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @ExcelProperty(value = "运输方式(海运、陆运、空运、供应商送货)", converter = DictConvert.class)
//    @DictFormat(DictTypeConstants.TRANSPORT_TYPE)
//    private Integer transportType;

    @Schema(description = "营业地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("营业地址")
    private String address;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "国外客户标志", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "国外客户标志", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIRM_TYPE)
    private Integer abroadFlag;

    @Schema(description = "客户来源", example = "1")
    @ExcelProperty(value = "客户来源", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.SOURCE_TYPE)
    private Integer sourceType;

    @Schema(description = "启用信用额度：0：不启用，1：启用")
    @ExcelProperty(value = "启用信用额度", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIRM_TYPE)
    private Integer creditFlag;

    @Schema(description = "信用额度")
    @ExcelProperty(value = "信用额度", converter = AmountConvert.class)
    private JsonAmount creditLimit;

    @Schema(description = "是否是中信保：0：否，1：是", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "是否是中信保", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIRM_TYPE)
    private Integer zxbquotaFlag;

    @Schema(description = "价格条款  CIF,FOB,CIP,DDP,DAP,DAT,CFR", example = "2")
    @ExcelProperty(value = "价格条款")
    private String settlementTermType;

    @Schema(description = "开票抬头")
    @ExcelProperty("开票抬头")
    private String invoiceHeader;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @Schema(description = "唛头id", example = "28789")
    @ExcelProperty("唛头id")
    @ExcelIgnore
    private Integer markId;

    @Schema(description = "是否代理  0:否， 1：是")
    @ExcelProperty(value = "是否代理", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIRM_TYPE)
    private Integer agentFlag;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "寄件地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "寄件地址不能为空")
//    @ExcelProperty("寄件地址")
    private List<AddressShipping> addressShipping;

    @Schema(description = "备用信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "remark")
    @ExcelProperty("备用信息")
    private String remark;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED, example = "remark")
    @ExcelProperty(value = "业务员", converter = UserDeptListConverter.class)
    private List<BaseValue> managerList;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @ExcelIgnore
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    @ExcelIgnore
    @ChangeIgnore
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    @ExcelIgnore
    @ChangeIgnore
    private String creator;

    @Schema(description = "创建人名字")
    @ExcelProperty("创建人名字")
    @ExcelIgnore
    @ChangeIgnore
    private String creatorName;

    @Schema(description = "转正标识")
    @ExcelProperty(value = "转正标识", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer convertFlag;

    @Schema(description = "转正时间")
    private LocalDateTime convertTime;

    @Schema(description = "是否启用")
    @ExcelProperty(value = "是否启用", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer enableFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "图片")
    private List<SimpleFile> picture;

    @Schema(description = "关联客户编号")
    private List<String> custLinkCode;

    @Schema(description = "关联客户信息")
    private List<SimpleData> custLink;

    @Schema(description = "公司路径")
    private List<CompanyPath> companyPath;

    @Schema(description = "旧客户")
    private CustInfoRespVo oldCust;

    @Schema(description = "正面唛头")
    @ExcelProperty("正面唛头")
    private String mainMark;

    @Schema(description = "侧面唛头")
    @ExcelProperty("侧面唛头")
    private String sideMark;

    @Schema(description = "内部企业标识  0-否 1-是")
    private Integer internalFlag;

    @Schema(description = "内部企业主键")
    private Long internalCompanyId;

    @Schema(description = "内部企业名称")
    private String internalCompanyName;

    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "展会id")
    private Long exmsExhibitionId;

    @Schema(description = "展会名称")
    private String exmsExhibitionName;

    @Schema(description = "客户分类名称")
    private String customerTypesName;

    @Schema(description = "展会系列")
    private Long exmsEventCategoryId;

    @Schema(description = "展会系列名称")
    private String exmsEventCategoryName;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;
}
