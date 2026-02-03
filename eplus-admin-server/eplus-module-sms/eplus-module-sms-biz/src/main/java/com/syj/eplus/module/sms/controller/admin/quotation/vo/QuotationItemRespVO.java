package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.jackson.core.databind.LocalDateTimeSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuotationItemRespVO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 序号
     */
    @Schema(description = "序号")
    private String sequence;
    /**
     * 供应商名称
     */
    @CompareField(value = "供应商名称")
    @Schema(description = "供应商名称")
    private String venderName;

    /**
     * 供应商编号
     */
    @Schema(description = "供应商工厂货号")
    private String venderProdCode;

    /**
     * 供应商编号
     */
    @Schema(description = "供应商编号")
    private String venderCode;
    /**
     * 供应商ID
     */
    @Schema(description = "供应商id")
    private Long venderId;
    /**
     * 付款方式ID
     */
    @Schema(description = "付款方式id")
    private Long paymentId;
    /**
     * 付款方式名称
     */
    @Schema(description = "付款方式名称")
    private String paymentName;
    /**
     * sku编号
     */
    @Schema(description = "sku编号")
    private String skuCode;

    /**
     * 客户货号
     */
    @Schema(description = "客户货号")
    private String cskuCode;


    /**
     * 基础产品编号
     */
    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    /**
     * 是否含运费
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    @Schema(description = "是否含运费")
    @CompareField(value = "是否含运费", enumType = "confirm_type")
    private Integer freightFlag;

    /**
     * 是否含包装
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    @Schema(description = "是否含包装")
    @CompareField(value = "是否含包装", enumType = "confirm_type")
    private Integer packageFlag;

    /**
     * 包装方式
     * <p>
     * 枚举 {@link TODO package_type 对应的类}
     */
    @Schema(description = "包装方式")
    @CompareField(value = "包装方式", enumType = "package_type")
    private List<Long> packageType;

    @Schema(description = "包装方式中文名", example = "1")
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    private String packageTypeEngName;
    /**
     * 币种
     */
    @Schema(description = "币种")
    @CompareField(value = "币种")
    private String currency;
    /**
     * 是否含税
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    @Schema(description = "是否含税")
    @CompareField(value = "是否含税", enumType = "confirm_type")
    private Integer faxFlag;
    /**
     * 税率
     */
    @Schema(description = "税率")
    @CompareField(value = "税率")
    private BigDecimal taxRate;
    /**
     * 发票类型
     */
    @Schema(description = "发票类型")
    @CompareField(value = "发票类型")
    private Integer taxType;
    /**
     * 最小起购量
     */
    @Schema(description = "最小起购量")
    @CompareField(value = "最小起购量")
    private Integer moq;

    /**
     * 采购单价
     *
     */
    @Schema(description = "采购单价")
    @CompareField(value = "采购单价")
    private JsonAmount unitPrice;
    /**
     * 总价
     */
    @Schema(description = "总价")
    @CompareField(value = "总价")
    private JsonAmount totalPrice;
    /**
     * 含税总价
     */
    @Schema(description = "含税总价")
    @CompareField(value = "含税总价")
    private JsonAmount withTaxPrice;
    /**
     * 包装价
     */
    @Schema(description = "包装价")
    @CompareField(value = "包装价")
    private JsonAmount packagingPrice;
    /**
     * 运费
     */
    @Schema(description = "运费")
    @CompareField(value = "运费")
    private JsonAmount shippingPrice;
    /**
     * 交期
     */
    @Schema(description = "交期")
    @CompareField(value = "交期")
    private Integer delivery;
    /**
     * 采购链接
     */
    @Schema(description = "采购链接")
    @CompareField(value = "采购链接")
    private String purchaseUrl;
    /**
     * 内箱装量
     */
    @Schema(description = "内箱装量")
    @CompareField(value = "内箱装量")
    private Integer qtyPerInnerbox;

    /**
     * 外箱装量
     */
    @Schema(description = "外箱装量")
    @CompareField(value = "外箱装量")
    private Integer qtyPerOuterbox;
    /**
     * 包装规格长度
     */
    @Schema(description = "包装规格长度")
    @CompareField(value = "包装规格长度")
    private BigDecimal packageLength;
    /**
     * 包装规格宽度
     */
    @Schema(description = "包装规格宽度")
    @CompareField(value = "包装规格宽度")
    private BigDecimal packageWidth;
    /**
     * 包装规格高度
     */
    @Schema(description = "包装规格高度")
    @CompareField(value = "包装规格高度")
    private BigDecimal packageHeight;
    /**
     * 单品毛重
     */
    @Schema(description = "单品毛重")
    @CompareField(value = "单品毛重")
    private JsonWeight singleGrossweight;
    /**
     * 是否默认
     */
    @Schema(description = "是否默认")
    @CompareField(value = "是否默认", enumType = "confirm_type")
    private Integer defaultFlag;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @CompareField(value = "备注")
    private String remark;

    @Schema(description = "报价日期")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime quoteDate;

    @Schema(description = "采购员")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    @CompareField(value = "采购员")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "是否变更")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

    private Long skuId;

    @Schema(description = "40尺柜装量")
    private Long fortyFootContainerCapacity;

    @Schema(description = "40高柜装量")
    private Long fortyFootHighContainerCapacity;

    @Schema(description = "20尺柜装量")
    private Long twentyFootContainerCapacity;
    @Schema(description = "价格条款", example = "2")
    private String settlementTermType;

    @Schema(description = "客户名称", example = "张三")
    private String custName;
    /**
     * 报价单id
     */
    private Long smsQuotationId;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;
    /**
     * 中文名称
     */
    private String name;
    /**
     * 英文名称
     */
    private String nameEng;
    /**
     * 报价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount quotation;
    /**
     * 产品规格
     */
    private String spec;
    /**
     * 箱数
     */
    private Integer boxCount;
    /**
     * 外箱单位
     */
    private Integer unitPerOuterbox;
    /**
     * 中文说明
     */
    private String description;
    /**
     * 英文说明
     */
    private String descriptionEng;
    /**
     * HS编码
     */
    private String hsCode;

    /**
     * 利润率
     */
    @CompareField(value = "利润率")
    private BigDecimal profitRate;

    /**
     * 20尺柜
     */
    private Integer twentyFootCabinetNum;

    /**
     * 40尺柜
     */
    private Integer fortyFootCabinetNum;

    /**
     * 40尺高柜
     */
    private Integer fortyFootContainerNum;

    /**
     * 散货
     */
    private BigDecimal bulkHandlingVolume;
    /**
     * 客户产品标记
     */
    private Integer custProFlag;
    /**
     * 自营产品标记
     */
    private Integer ownBrandFlag;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;


}
