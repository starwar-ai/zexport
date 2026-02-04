package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.WeightConvert;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 产品 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class SkuRespVO implements ChangeExInterface {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3315")
    @ChangeIgnore
    private Long id;

    @Schema(description = "spuid", example = "17861")
    @ChangeIgnore
    private Long spuId;

    @Schema(description = "是否变更")
    @ChangeIgnore
    private Integer changeFlag;

    @Schema(description = "变更状态")
    @ChangeIgnore
    private Integer changeStatus;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    @ExcelIgnore
    private Integer ver;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("中文品名")
    private String name;

    @Schema(description = "英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("英文品名")
    private String nameEng;

    @Schema(description = "商品编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("商品编码")
    @ChangeIgnore
    private String code;

    @Schema(description = "产品类型", example = "1")
    @ExcelProperty(value = "产品类型", converter = DictConvert.class)
    @DictFormat("sku_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer skuType;

    @Schema(description = "计量单位")
    @ExcelProperty(value = "计量单位", converter = DictConvert.class)
    @DictFormat("measure_unit") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer measureUnit;

    @Schema(description = "产品材质", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品材质")
    private String material;


    @Schema(description = "单品规格长", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单品规格长")
    private BigDecimal specLength;

    @Schema(description = "单品规格宽", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单品规格宽")
    private BigDecimal specWidth;

    @Schema(description = "单品规格高", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单品规格高")
    private BigDecimal specHeight;

    @Schema(description = "单品净重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "净重", converter = WeightConvert.class)
    private JsonWeight singleNetweight;

    @Schema(description = "供应商名称")
    @ExcelProperty("供应商")
    private String venderName;

    @ExcelProperty("交期")
    @Schema(description = "采购交期")
    private Integer delivery;

    @Schema(description = "采购含税单价")
    @ExcelProperty(value = "采购含税单价", converter = AmountConvert.class)
    private JsonAmount withTaxPrice;

    @Schema(description = "单品毛重")
    @ExcelProperty(value = "单品毛重", converter = WeightConvert.class)
    private JsonWeight singleGrossweight;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "条码", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("条码")
    private String barcode;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("客户编号")
    private String custCode;


    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "客户货号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "自营产品货号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("自营产品货号")
    private String oskuCode;

    @Schema(description = "产品说明", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
//    @ExcelProperty("产品说明")
    private String description;

    @Schema(description = "产品说明英文", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("产品说明英文")
    private String descriptionEng;

    @Schema(description = "是否自主品牌", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty(value = "是否自主品牌", converter = DictConvert.class)
//    @DictFormat("confirm_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer ownBrandFlag;

    @Schema(description = "品牌id", example = "3489")
    private Long brandId;

    @Schema(description = "品牌名称", example = "3489")
//    @ExcelProperty("品牌")
    private String brandName;


    @Schema(description = "产品分类", example = "10604")
    private Long categoryId;

    @Schema(description = "产品分类名称", example = "10604")
//    @ExcelProperty("产品分类")
    private String categoryName;

    @Schema(description = "海关编码id", example = "22453")
//    @ExcelProperty("海关编码id")
    private Long hsCodeId;

    @Schema(description = "海关编码id", example = "22453")
    private BigDecimal taxRefundRate;

    @Schema(description = "产品来源")
//    @ExcelProperty("产品来源")
    private Integer sourceFlag;

    @Schema(description = "单位加工费", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal singleProcessFee;

    @Schema(description = "加工备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    private String processRemark;

    @Schema(description = "销售单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "6673")
    @ExcelProperty(value = "销售单价", converter = AmountConvert.class)
    private JsonAmount price;

    @Schema(description = "公司定价", requiredMode = Schema.RequiredMode.REQUIRED, example = "10591")
    @ExcelProperty(value = "公司定价", converter = AmountConvert.class)
    private JsonAmount companyPrice;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    @ChangeIgnore
    private LocalDateTime updateTime;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "图片")
    private List<SimpleFile> picture;

    @Schema(description = "主图")
    @TableField(exist = false)
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "是否优势产品")
//    @ExcelProperty(value = "是否优势产品", converter = DictConvert.class)
//    @DictFormat("confirm_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer advantageFlag;

    @Schema(description = "是否默认报价")
    private Integer defaultFlag;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "产品状态", converter = DictConvert.class)
    @DictFormat("onshelf_flag") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer onshelfFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "来源id")
    private Long sourceId;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "自动创建标识")
    private Integer autoCreateFlag;

//    @Schema(description = "是否通用辅料")
//    private Integer auxiliarySkuFlag;

    @Schema(description = "是否商检")
    private Integer commodityInspectionFlag;

    @Schema(description = "HS编码")
    @ExcelProperty("HS编码")
    private String hsCode;

    @Schema(description = "旧产品")
    @ChangeIgnore
    private SkuInfoRespVO oldData;

    private String sourceCode;

    @ChangeIgnore
    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "流程实例主键")
    @ChangeIgnore
    private String processInstanceId;

    @Schema(description = "辅料产品材质")
    private String auxiliaryMaterial;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "采购员名称")
    private String purchaseUserName;

    @Schema(description = "报关中文品名")
    private String declarationName;

    @Schema(description = "报关英文品名")
    private String customsDeclarationNameEng;

    @Schema(description = "归属部门id")
    private Long belongingDeptId;

    @Schema(description = "归属部门名称")
    private String belongingDeptName;

    @Schema(description = "创建人")
    private String creatorName;
}