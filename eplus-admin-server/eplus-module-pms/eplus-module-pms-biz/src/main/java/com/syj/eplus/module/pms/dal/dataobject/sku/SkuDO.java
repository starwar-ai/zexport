package com.syj.eplus.module.pms.dal.dataobject.sku;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuInfoRespVO;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuRespVO;
import com.syj.eplus.module.pms.handler.JsonSkuInfoRespHandler;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务收款单 DO
 *
 * @author du
 */

@TableName(value = "pms_sku", autoResultMap = true)
@KeySequence("pms_sku_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class SkuDO extends BaseDO implements ModelKeyHolder, ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 是否变更
     */
    private Integer changeFlag;

    /**
     * 变更状态
     */
    private Integer changeStatus;

    /**
     * 变更是否删除
     */
    private Integer changeDeleted;

    @TableField(exist = false)
    private SkuRespVO oldSku;
    /**
     * 版本号
     */
    private Integer ver;

    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;
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
    /**
     * 客户货号
     */
    @CompareField(value = "客户货号")
    private String cskuCode;
    /**
     * 自营产品货号
     */
    @CompareField(value = "自营产品货号")
    private String oskuCode;
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
     * 来源编号
     */
    private String sourceCode;
    /**
     * 自动创建标识
     */
    private Integer autoCreateFlag;
    /**
     * 是否通用辅料
     */
    private Integer auxiliarySkuFlag;

    /**
     * 辅料材质
     */
    private Integer auxiliaryMaterial;

    /**
     * 是否商检
     */
    private Integer commodityInspectionFlag;

    private String processInstanceId;

    @TableField(typeHandler = JsonSkuInfoRespHandler.class)
    private SkuInfoRespVO oldData;

    /**
     * 基础产品编号
     */
    private String basicSkuCode;

    /**
     * 报关中文名称
     */
    private String declarationName;

    /**
     * 报关英文名称
     */
    private String customsDeclarationNameEng;

    /**
     * 是否代理
     */
    private Integer agentFlag;

    /**
     * 归属部门id
     */
    private Long belongingDeptId;

    /**
     * 归属部门名称
     */
    private String belongingDeptName;
}