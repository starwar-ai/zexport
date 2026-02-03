package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.wms.api.stock.dto.StockDetailRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6813")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "版本")
    @ExcelProperty("版本")
    private Integer ver;

    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;

    @Schema(description = "一级拆分序号")
    private Integer oneSplitNum;

    @Schema(description = "二级拆分序号")
    private Integer twoSplitNum;

    @Schema(description = "三级拆分序号")
    private Integer threeSplitNum;

    @Schema(description = "组合产品父级主键")
    private Long sourceId;

    @Schema(description = "明细层级")
    private Integer levels;

    @Schema(description = "产品id", example = "28278")
    @ExcelProperty("产品id")
    private Long skuId;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    private String skuName;

    @Schema(description = "客户id", example = "6417")
    @ExcelProperty("客户id")
    private Long custId;

    @Schema(description = "客户编号")
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @Schema(description = "销售数量")
    @ExcelProperty("销售数量")
    private Integer saleQuantity;

    @Schema(description = "可用库存")
    @ExcelProperty("可用库存")
    private Integer availableQuantity;

    @CompareField(value = "锁定数量")
    private Integer currentLockQuantity;

    @CompareField(value = "待采购数量")
    private Integer needPurQuantity;

    @Schema(description = "采购金额")
    @ExcelProperty(value = "采购金额", converter = AmountConvert.class)
    private JsonAmount amount;

    @Schema(description = "下单主体主键", example = "7954")
    private Long purchaseCompanyId;

    @Schema(description = "下单主体名称")
    private String purchaseCompanyName;

    @Schema(description = "供应商id", example = "7954")
    @ExcelProperty("供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    @ExcelProperty("供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "仓库id", example = "6505")
    @ExcelProperty("仓库id")
    private Long stockId;

    @Schema(description = "仓库编号")
    @ExcelProperty("仓库编号")
    private String stockCode;

    @Schema(description = "采购计划id", example = "2226")
    @ExcelProperty("采购计划id")
    private Long purchasePlanId;

    @Schema(description = "采购计划编号")
    @ExcelProperty("采购计划编号")
    private String purchasePlanCode;

    @Schema(description = "包装价", example = "5763")
    @ExcelProperty(value = "包装价", converter = AmountConvert.class)
    private JsonAmount packagingPrice;

    @Schema(description = "运费", example = "17301")
    @ExcelProperty(value = "运费", converter = AmountConvert.class)
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "25479")
    @ExcelProperty(value = "采购单价", converter = AmountConvert.class)
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "13785")
    @ExcelProperty(value = "总价", converter = AmountConvert.class)
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "3671")
    @ExcelProperty(value = "含税总价", converter = AmountConvert.class)
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("采购类型")
    private Integer purchaseType;

    @Schema(description = "内箱装量")
    @ExcelProperty("内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    @ExcelProperty("包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    @ExcelProperty("包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    @ExcelProperty("包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    @ExcelProperty("包装规格单位")
    private Integer packageUnit;

    @Schema(description = "单品毛重")
    @ExcelProperty("单品毛重")
    private JsonWeight singleGrossweight;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "转采购计划状态")
    @ExcelProperty("转采购计划状态")
    private Integer convertedFlag;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "采购员id")
    @ExcelProperty("采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    @ExcelProperty("采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    @ExcelProperty("采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    @ExcelProperty("采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    @ExcelProperty("工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    @ExcelProperty("报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    @ExcelProperty("是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    @ExcelProperty("是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式")
    @ExcelProperty("包装方式")
    private List<Long> packageType;


    @Schema(description = "包装方式中文名", example = "1")
    @ExcelProperty("包装方式中文名")
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    @ExcelProperty("包装方式英文名")
    private String packageTypeEngName;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currency;

    @Schema(description = "采购员部门名称")
    @ExcelProperty("采购员部门名称")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    @ExcelProperty("最小起购量")
    private Integer moq;

    @Schema(description = "是否赠品")
    @ExcelProperty("是否赠品")
    private Integer freeFlag;

    @Schema(description = "箱数")
    @ExcelProperty("箱数")
    private Integer boxCount;

    @Schema(description = "图片")
    @ExcelProperty("图片")
    private SimpleFile mainPicture;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    private String productName;

    @Schema(description = "产品类型")
    @ExcelProperty("产品类型")
    private Integer skuType;


    @Schema(description = "审核状态")
    @ExcelIgnore
    private Integer auditStatus;

    @Schema(description = "采购计划状态")
    @ExcelIgnore
    private Integer planStatus;

    @Schema(description = "采购模式", example = "1")
    private Integer purchaseModel;


    @Schema(description = "采购链接", example = "1")
    @ExcelProperty("采购链接")
    private String purchaseUrl;

    @Schema(description = "外销合同主键")
    @ExcelProperty("外销合同主键")
    @ExcelIgnore
    private Integer saleContractId;

    @Schema(description = "外销合同编号")
    @ExcelProperty("外销合同编号")
    private String saleContractCode;

    @Schema(description = "是否通用辅料")
    @ExcelProperty("是否通用辅料")
    private Integer auxiliarySkuFlag;

    @Schema(description = "辅料采购类型")
    @ExcelProperty("辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "规格描述")
    @ExcelProperty("规格描述")
    private String specRemark;

    @Schema(description = "附件")
    @ExcelIgnore
    @ExcelProperty("附件")
    private List<SimpleFile> annex;

    @Schema(description = "辅料属于的销售合同")
    @ExcelProperty("辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    @Schema(description = "辅料属于的采购合同编号")
    @ExcelProperty("辅料属于的采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的采购合同产品id")
    @ExcelProperty("辅料属于的采购合同产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料属于的采购合同产品编号")
    @ExcelProperty("辅料属于的采购合同产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料属于的采购合同产品名称")
    @ExcelProperty("辅料属于的采购合同产品名称")
    private String auxiliarySkuName;


    @Schema(description = "辅料属于的采购合同产品客户货号")
    @ExcelProperty("辅料属于的采购合同产品客户货号")
    private String auxiliaryCskuCode;


    @Schema(description = "供应商对应的采购id列表")
    @ExcelProperty("供应商对应的采购id列表")
    private List<UserDept> purchaseUserList;


    @Schema(description = "计量单位")
    @ExcelProperty("计量单位")
    private String skuUnit;

    @Schema(description = "自营产品标记")
    @ExcelProperty("自营产品标记")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标记")
    @ExcelProperty("客户产品标记")
    private Integer custProFlag;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "库存锁定-请求参数")
    @TableField(exist = false)
    private List<StockLockSaveReqVO> stockLockSaveReqVOList;

    @Schema(description = "库存锁定-响应参数")
    @TableField(exist = false)
    private List<StockDetailRespVO> stockDetailRespVOList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "唯一编号")
    private String uniqueCode;

    @Schema(description = "来源编号")
    private String sourceUniqueCode;

    @Schema(description = "销售合同明细编号")
    private String saleItemUniqueCode;

    @Schema(description = "拆分主体id")
    private Long splitCompanyId;

    @Schema(description = "拆分主体名称")
    private String splitCompanyName;

    @Schema(description = "外销合同锁定库存数量")
    private Integer saleLockQuantity;

    @Schema(description = "采购计划锁定库存数量")
    private Integer lockQuantity;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "父物料编号")
    private String parentSkuCode;

    @Schema(description = "父物料名称")
    private String parentSkuName;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "已转合同数量")
    private Integer convertedQuantity;

    @Schema(description = "来源计划明细编号")
    private Long sourcePlanItemId;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    @Schema(description = "包材采购计划合同数量", example = "13785")
    private Integer totalQuantity;

    @Schema(description = "客户po号")
    private String custPo;

    @Schema(description = "缩略图")
    private String thumbnail;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

    @ExcelProperty("交货日期")
    private LocalDateTime venderDeliveryDate;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;

}
