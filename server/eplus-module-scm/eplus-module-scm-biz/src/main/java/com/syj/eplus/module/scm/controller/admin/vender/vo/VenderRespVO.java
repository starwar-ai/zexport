package com.syj.eplus.module.scm.controller.admin.vender.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptListConverter;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonVenderTax;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 供应商信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class VenderRespVO {

    private Long id;

    @Schema(description = "是否变更")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    private Integer ver;

    @Schema(description = "供应商编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("供应商编码")
    private String code;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("供应商名称")
    private String name;

    @Schema(description = "供应商英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("供应商英文名称")
    private String nameEng;

    @Schema(description = "供应商简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "供应商简称")
    private String nameShort;

    @Schema(description = "注册资本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "注册资本")
    private String registeredCapital;

    @Schema(description = "法定代表人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "法定代表人")
    private String legalPerson;

    @Schema(description = "主营业务", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "主营业务")
    private List<Long> businessScope;

    @Schema(description = "应付供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "应付供应商编号")
    private List<String> venderLinkCode;

    @Schema(description = "应付供应商名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "应付供应商名称")
    private List<SimpleData> venderlink;

    @Schema(description = "营业执照号")
    @ExcelProperty(value = "营业执照号")
    private String licenseNo;

    @Schema(description = "公司所在地区名称", example = "6751")
    @ExcelProperty("公司所在地区名称")
    private String companyAreaName;

    @Schema(description = "工厂所在地区名称", example = "6751")
    @ExcelProperty("工厂所在地区名称")
    private String factoryAreaName;

    @Schema(description = "公司所在地区id", example = "6751")
    private List<Integer> companyAreaIdList;

    @Schema(description = "工厂所在地区id", example = "6751")
    private List<Integer> factoryAreaIdList;

    @Schema(description = "公司地址")
    @ExcelProperty("公司地址")
    private String companyAddress;

    @Schema(description = "工厂地址")
    @ExcelProperty("工厂地址")
    private String factoryAddress;

    @Schema(description = "供应商类型")
    @ExcelProperty("供应商类型")
    @DictFormat(DictTypeConstants.VENDER_TYPE)
    private Integer venderType;

    @Schema(description = "供应商级别")
    @ExcelProperty("供应商级别")
    @DictFormat(DictTypeConstants.VENDER_LEVEL)
    private Integer venderLevel;

    @Schema(description = "是否境外供应商")
    @ExcelProperty("是否境外供应商")
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer abroadFlag;

    @Schema(description = "国家编号")
    private Long countryId;

    @Schema(description = "国家")
    @ExcelProperty("国家")
    private String countryName;

    @Schema(description = "供应商联系人")
    @ExcelProperty("供应商联系人")
    private String venderPocName;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String venderPocPhone;

    @Schema(description = "企业电话")
    @ExcelProperty("企业电话")
    private String phone;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    private String creatorName;

    @Schema(description = "创建人部门")
    @ExcelProperty("创建人部门")
    private String creatorDeptName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "采购员", requiredMode = Schema.RequiredMode.REQUIRED, example = "remark")
    @ExcelProperty(value = "采购员", converter = UserDeptListConverter.class)
    private List<BaseValue> buyerList;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "快递所在地区id", example = "6751")
    private List<Integer> deliveryAreaIdList;

    @Schema(description = "快递所在地址名字")
    @ExcelProperty("快递所在地址名字")
    private String deliveryAreaName;

    @Schema(description = "快递地址")
    private String deliveryAddress;

    @Schema(description = "供应商阶段")
    @ExcelProperty(value = "供应商阶段", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.VENDER_STAGEE)
    private Integer stageType;

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

    @Schema(description = "传真")
    private String fax;

    @Schema(description = "付款方式")
    private List<PaymentItemDTO> paymentList;

    @Schema(description = "资质ids")
    private List<Long> qualificationIds;

    @Schema(description = "资质名称")
    @ExcelProperty(value = "资质名称")
    private List<QualificationDO> qualificationlink;
    /**
     * 内部企业标识 0-否 1-是
     */
    private Integer internalFlag;

    /**
     * 内部企业主键
     */
    private Long internalCompanyId;

    @Schema(description = "旧供应商")
    private VenderRespVO oldVender;

    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "行政供应商类型")
    private Integer administrationVenderType;

    /**
     * 工厂所在地区
     */
    private Integer factoryAreaId;

    private List<JsonVenderTax> taxMsg;
}
