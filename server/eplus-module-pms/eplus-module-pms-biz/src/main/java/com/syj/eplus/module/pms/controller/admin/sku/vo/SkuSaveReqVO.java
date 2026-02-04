package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - sku新增/修改 Request VO")
@Data
public class SkuSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3315")
    private Long id;

    @Schema(description = "是否变更：0：否，1：是")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer ver;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "spuid", example = "17861")
    private Long spuId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
//    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "英文名称不能为空")
    private String nameEng;

    @Schema(description = "商品编码", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "商品编码不能为空")
    private String code;

    @Schema(description = "条码", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "条码不能为空")
    private String barcode;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "客户编号不能为空")
    private String custCode;

    @Schema(description = "客户货号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "客户货号不能为空")
    private String cskuCode;

    @Schema(description = "自营产品货号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "自营产品货号不能为空")
    private String oskuCode;

    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "产品状态不能为空")
    private Integer onshelfFlag;

    @Schema(description = "产品说明", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
//    @NotEmpty(message = "产品说明不能为空")
    private String description;

    @Schema(description = "产品说明英文", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "产品说明英文不能为空")
    private String descriptionEng;

    @Schema(description = "是否自主品牌", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "是否自主品牌不能为空")
    private Integer ownBrandFlag;

    @Schema(description = "品牌id", example = "3489")
    private Long brandId;

    @Schema(description = "产品分类", example = "10604")
    private Long categoryId;

    @Schema(description = "海关编码id", example = "22453")
    private Long hsCodeId;

    @Schema(description = "产品来源")
    private Integer sourceFlag;

    @Schema(description = "产品类型", example = "1")
    private Integer skuType;

    @Schema(description = "产品材质", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "产品材质不能为空")
    private String material;

    @Schema(description = "计量单位")
    private Integer measureUnit;

    @Schema(description = "单品规格长", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "单品规格长不能为空")
    private BigDecimal specLength;

    @Schema(description = "单品规格宽", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "单品规格宽不能为空")
    private BigDecimal specWidth;

    @Schema(description = "单品规格高", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "单品规格高不能为空")
    private BigDecimal specHeight;

    @Schema(description = "单品净重", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "单品净重不能为空")
    private JsonWeight singleNetweight;

    @Schema(description = "单位加工费", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "单位加工费不能为空")
    private BigDecimal singleProcessFee;

    @Schema(description = "加工备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
//    @NotEmpty(message = "加工备注不能为空")
    private String processRemark;

    @Schema(description = "销售单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "6673")
//    @NotEmpty(message = "销售单价不能为空")
    private JsonAmount price;

    @Schema(description = "公司定价", requiredMode = Schema.RequiredMode.REQUIRED, example = "10591")
//    @NotEmpty(message = "公司定价不能为空")
    private JsonAmount companyPrice;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "图片")
    private List<SimpleFile> picture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "是否优势产品")
    private Integer advantageFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "来源id")
    private Long sourceId;

    @Schema(description = "自动创建标识")
    private Integer autoCreateFlag;

//    @Schema(description = "是否通用辅料")
//    private Integer auxiliarySkuFlag;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "是否商检")
    private Integer commodityInspectionFlag;

    private String modelKey;

    @Schema(description = "辅料产品材质")
    private String auxiliaryMaterial;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    private SkuInfoRespVO oldData;

    @Schema(description = "基础sku编码")
    private String basicSkuCode;

    @Schema(description = "报关中文品名")
    private String declarationName;

    @Schema(description = "报关英文品名")
    private String customsDeclarationNameEng;

    @Schema(description = "是否代理")
    private Integer agentFlag;

    @Schema(description = "归属部门id")
    private Long belongingDeptId;

    @Schema(description = "归属部门名称")
    private String belongingDeptName;
}