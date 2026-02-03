package com.syj.eplus.module.pms.api.sku.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/10 16:20
 */
@Data
public class SimpleSkuDTO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "品名")
    private String skuName;

    @Schema(description = "产品材质")
    private String material;

    @Schema(description = "分类")
    private String categoryName;

    @Schema(description = "品牌")
    private String brandName;

    @Schema(description = "状态")
    private Integer skuType;

    @Schema(description = "销售单价")
    private JsonAmount price;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自营产品货号")
    private String oskuCode;

    @Schema(description = "组合产品配件数量")
    private Integer sonSkuCount;

    @Schema(description = "报价列表")
    private List<QuoteitemDTO> quoteitemList;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "海关编码")
    private String hsCode;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "公司定价")
    private JsonAmount companyPrice;

    @Schema(description = "产品说明")
    private String description;

    @Schema(description = "产品英文说明")
    private String descriptionEng;

    @Schema(description = "默认报价供应商Id")
    private Long defaultVenderId;

    @Schema(description = "默认供应商包含的采购员列表")
    private List<UserDept> purchaseUserList;

    private List<Long> buyerIds;

    @Schema(description = "是否客户产品标记")
    private Integer custProFlag;

    @Schema(description = "是否通用辅料")
    private Integer auxiliarySkuFlag;

    @Schema(description = "计量单位")
    private Integer measureUnit;

    @Schema(description = "是否自营")
    private Integer ownBrandFlag;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编码")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "子产品所需数量")
    private Integer subQutity;

    @Schema(description = "是否翻单")
    private Integer reorderFlag;

    @Schema(description = "是否商检")
    private Integer commodityInspectionFlag;

    @Schema(description = "子产品列表")
    private List<SimpleSkuDTO> sonSkuList;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    @Schema(description = "辅料配比列表")
    private List<SkuAuxiliaryDTO> skuAuxiliaryDOList;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "海关编码id")
    private Long hsCodeId;

    @Schema(description = "海关信息")
    private HsDataDTO hsdata;
    @Schema(description = "条形码")
    private String barcode;
}
