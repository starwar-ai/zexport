package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - sku分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkuPageReqVO extends PageParam {

    private List<Long> idList;
    @Schema(description = "是否变更")
    private Integer changeFlag;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "版本号")
    private Integer ver;

    @Schema(description = "是否优势产品")
    private Integer advantageFlag;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "spuid", example = "17861")
    private Long spuId;

    @Schema(description = "名称", example = "李四")
    private String name;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "商品编码")
    private String code;

    @Schema(description = "商品列表")
    private List<String> codeList;
    private String codeListStr;

    @Schema(description = "名称列表")
    private List<String> nameList;
    private String nameListStr;

    @Schema(description = "条码")
    private String barcode;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户编号列表")
    private List<String> custCodeList;
    private String custCodeListStr;

    @Schema(description = "客户名称")
    private String custName;
    private String custNameListStr;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "客户货号列表")
    private List<String> cskuCodeList;
    private String cskuCodeListStr;

    @Schema(description = "自营产品货号")
    private String oskuCode;

    @Schema(description = "自营产品货号列表")
    private List<String> oskuCodeList;
    private String oskuCodeListStr;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "基础产品编号列表")
    private List<String> basicSkuCodeList;
    private String basicSkuCodeListStr;

    @Schema(description = "产品状态")
    private Integer onshelfFlag;

    @Schema(description = "产品说明", example = "你猜")
    private String description;

    @Schema(description = "产品说明英文")
    private String descriptionEng;

    @Schema(description = "是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "品牌id", example = "3489")
    private Long brandId;

    @Schema(description = "产品分类", example = "10604")
    private Long categoryId;

    @Schema(description = "海关编码id", example = "22453")
    private Long hsCodeId;

    @Schema(description = "海关编码id列表")
    private List<Long> hsCodeIdList;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "产品来源")
    private Integer sourceFlag;

    @Schema(description = "产品类型", example = "1")
    private Integer skuType;

    @Schema(description = "产品材质")
    private String material;

    @Schema(description = "计量单位")
    private Integer measureUnit;

    @Schema(description = "单品规格长")
    private BigDecimal specLength;

    @Schema(description = "单品规格宽")
    private BigDecimal specWidth;

    @Schema(description = "单品规格高")
    private BigDecimal specHeight;

    @Schema(description = "单位加工费")
    private BigDecimal singleProcessFee;

    @Schema(description = "加工备注", example = "你猜")
    private String processRemark;


    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "自动创建标识")
    private Integer autoCreateFlag;

    @Schema(description = "辅料材质")
    private Integer auxiliaryMaterial;

    @Schema(description = "skutype过滤条件")
    private List<Integer> skuTypeInList;

    @Schema(description = "国家编号")
    private Long countryId;

    @Schema(description = "区域")
    private String regionCode;

    @Schema(description = "供应商编号列表")
    private List<String> venderCodeList;

    @Schema(description = "区域")
    private Long purchaseUserId;

    @Schema(description = "排除skuCode")
    private String excludeSkuCode;

    @Schema(description = "是否代理产品")
    private Integer agentFlag;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "金额最大值")
    private BigDecimal amountMax;

    @Schema(description = "金额最小值")
    private BigDecimal amountMin;

    @Schema(description = "产品主键列表")
    private Set<Long> idSet;

    @Schema(description = "归属部门id")
    private Long belongingDeptId;

    @Schema(description = "归属部门名称")
    private String belongingDeptName;

    @Schema(description = "录入人")
    private Long creator;
}