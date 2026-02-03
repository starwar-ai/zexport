package com.syj.eplus.module.pms.api.sku.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Desc——
 * Create by Rangers at  2024-06-15 14:58
 */
@Data
public class SkuDTO {
    /**
     * 主键
     */
    private Long id;

    private Integer ver;
    /**
     * 审核状态
     *
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
    /**
     * spuid
     */
    private Long spuId;
    /**
     * 名称
     */
    @CompareField(value = "名称")
    private String name;
    /**
     * 英文名称
     */
    @CompareField(value = "英文名称")
    private String nameEng;
    /**
     * 商品编码
     */
    @CompareField(value = "商品编码")
    private String code;

    @CompareField(value = "自营产品编号")
    private String oskuCode;

    /**
     * 父级产品主键
     */
    @CompareField(value = "父级产品主键")
    private Long parentSkuId;

    /**
     * 父级产品编码
     */
    @CompareField(value = "父级产品编码")
    private String parentCode;
    /**
     * 条码
     */
    @CompareField(value = "条码")
    private String barcode;
    /**
     * 客户编号
     */
    @CompareField(value = "客户编号")
    private String custCode;
    @CompareField(value = "基础产品编号")
    private String basicSkuCode;


    /**
     * 客户货号
     */
    @CompareField(value = "客户货号")
    private String cskuCode;
    /**
     * 产品状态
     *
     * 枚举 {@link TODO onshelf_flag 对应的类}
     */
    @CompareField(value = "产品状态", enumType = "onshelf_flag")
    private Integer onshelfFlag;
    /**
     * 产品说明
     */
    @CompareField(value = "产品说明")
    private String description;
    /**
     * 产品说明英文
     */
    @CompareField(value = "产品说明英文")
    private String descriptionEng;
    /**
     * 是否自主品牌
     *
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    @CompareField(value = "是否自主品牌", enumType = "confirm_type")
    private Integer ownBrandFlag;
    /**
     * 品牌id
     */
    @CompareField(value = "品牌")
    private Long brandId;
    /**
     * 产品分类
     */
    @CompareField(value = "产品分类")
    private Long categoryId;
    /**
     * 海关编码id
     */
    @CompareField(value = "海关编码")
    private Long hsCodeId;
    /**
     * 产品来源
     * <p>
     * 枚举 {@link TODO source_flag 对应的类}
     */
    @CompareField(value = "产品来源", enumType = "source_flag")
    private Integer sourceFlag;
    /**
     * 产品类型
     *
     * 枚举 {@link TODO sku_type 对应的类}
     */
    @CompareField(value = "产品类型", enumType = "sku_type")
    private Integer skuType;
    /**
     * 产品材质
     */
    @CompareField(value = "产品材质")
    private String material;
    /**
     * 计量单位
     * <p>
     * 枚举 {@link TODO measure_unit 对应的类}
     */
    @CompareField(value = "计量单位", enumType = "measure_unit")
    private Integer measureUnit;
    /**
     * 单品规格长
     */
    @CompareField(value = "单品规格长")
    private BigDecimal specLength;
    /**
     * 单品规格宽
     */
    @CompareField(value = "单品规格宽")
    private BigDecimal specWidth;
    /**
     * 单品规格高
     */
    @CompareField(value = "单品规格高")
    private BigDecimal specHeight;
    /**
     * 单品净重
     */
    @CompareField(value = "单品净重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleNetweight;
    /**
     * 单位加工费
     */
    @CompareField(value = "单位加工费")
    private BigDecimal singleProcessFee;
    /**
     * 加工备注
     */
    @CompareField(value = "加工备注")
    private String processRemark;
    /**
     * 销售单价
     */
    @CompareField(value = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount price;
    /**
     * 公司定价
     */
    @CompareField(value = "公司定价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount companyPrice;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 是否优势产品
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    @CompareField(value = "是否优势产品", enumType = "confirm_type")
    private Integer advantageFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 客户产品标识
     */
    private Integer custProFlag;

    /**
     * 来源id
     */
    private Long sourceId;

    /**
     * 来源code
     */
    private String sourceCode;
    /**
     * 是否通用辅料
     */
    private Integer auxiliarySkuType;
    /**
     * 是否代理产品
     */
    private Integer agentFlag;
}
