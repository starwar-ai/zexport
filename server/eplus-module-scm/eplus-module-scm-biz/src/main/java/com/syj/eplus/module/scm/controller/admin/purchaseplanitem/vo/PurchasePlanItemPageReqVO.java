package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购计划明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchasePlanItemPageReqVO extends PageParam {

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "产品id", example = "28278")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户id", example = "6417")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户货号")
    private String cskuCode;


    @Schema(description = "采购金额")
    private JsonAmount amount;

    @Schema(description = "供应商id", example = "7954")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "仓库id", example = "6505")
    private Long stockId;

    @Schema(description = "仓库编号")
    private String stockCode;

    @Schema(description = "采购计划id", example = "2226")
    private Long purchasePlanId;

    @Schema(description = "采购计划编号")
    private String purchasePlanCode;

    @Schema(description = "包装价", example = "5763")
    private JsonAmount packagingPrice;

    @Schema(description = "运费", example = "17301")
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "25479")
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "13785")
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "3671")
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型", example = "2")
    private Integer purchaseType;

    @Schema(description = "内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    private Integer packageUnit;

    @Schema(description = "单品毛重")
    private JsonWeight singleGrossweight;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "转合同状态")
    private Integer convertedFlag;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "采购员部门名称")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    private Integer moq;

    @Schema(description = "是否赠品")
    private Integer freeFlag;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "采购模式")
    private Integer purchaseModel;

    @Schema(description = "产品类型")
    private Integer skuType;

    @Schema(description = "采购链接")
    private String purchaseUrl;

    @Schema(description = "外销合同主键")
    private Integer saleContractId ;

    @Schema(description = "外销合同编号")
    private String saleContractCode;

    @Schema(description = "是否通用辅料")
    private Integer auxiliarySkuFlag ;

    @Schema(description = "辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "规格描述")
    private String specRemark ;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    @Schema(description = "辅料属于的采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的采购合同产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料属于的采购合同产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料属于的采购合同产品客户货号")
    private String auxiliaryCskuCode;


    @Schema(description = "是否包含删除数据")
    private  boolean isIncludeDeleted =false;

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