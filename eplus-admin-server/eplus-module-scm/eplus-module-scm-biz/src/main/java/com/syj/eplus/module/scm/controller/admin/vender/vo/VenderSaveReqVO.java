package com.syj.eplus.module.scm.controller.admin.vender.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonVenderTax;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 供应商信息新增/修改 Request VO")
@Data
public class VenderSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15492")
    private Long id;

    @Schema(description = "是否变更：0：否，1：是")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "版本不能为空")
    private Integer ver;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "供应商名称不能为空")
    private String name;

    @Schema(description = "供应商英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhaoliu")
    private String nameEng;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String code;

    @Schema(description = "供应商简称", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "供应商简称不能为空")
    private String nameShort;

    @Schema(description = "注册资本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String registeredCapital;

    @Schema(description = "法定代表人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String legalPerson;

    @Schema(description = "主营业务", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> businessScope;

    @Schema(description = "应付供应商", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> venderLinkCode;

    @Schema(description = "公司所在地区", example = "6751")
    private Integer companyAreaId;

    @Schema(description = "工厂所在地区", example = "6751")
    private Integer factoryAreaId;

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "公司地址")
    private String companyAddress;

    @Schema(description = "工厂地址")
    private String factoryAddress;

    @Schema(description = "供应商类型")
    private Integer venderType;

    @Schema(description = "供应商级别")
    private Integer venderLevel;

    @Schema(description = "企业电话")
    private String phone;

    @Schema(description = "是否境外供应商")
    private Integer abroadFlag;

    @Schema(description = "国家编号")
    private Long countryId;

    @Schema(description = "采购员(前端弃用)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> buyerIds;
    @Schema(description = "采购员", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BaseValue> buyerList;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "备注", example = "备注")
    private String remark;


    @Schema(description = "快递所在地区id", example = "6751")
    private Integer deliveryAreaId;

    @Schema(description = "快递地址")
    private String deliveryAddress;

    @Schema(description = "供应商阶段")
    private Integer stageType;

    @Schema(description = "转正标识")
    private Integer convertFlag;

    @Schema(description = "转正时间")
    private LocalDateTime convertTime;

    @Schema(description = "是否启用")
    private Integer enableFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "传真")
    private String fax;

    @Schema(description = "资质ids")
    private List<Long> qualificationIds;
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

    @Schema(description = "行政供应商类型")
    private Integer administrationVenderType;

    private List<JsonVenderTax> taxMsg;
}
