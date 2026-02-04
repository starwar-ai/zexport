package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品模式扁平响应 VO（合并主表和明细字段）
 * 用于产品视图的扁平化展示，按明细分页返回数据
 *
 * @author 波波
 */
@Schema(description = "管理后台 - 采购合同产品模式 Response VO")
@Data
@Accessors(chain = true)
public class PurchaseContractProductModeRespVO {

    // ===== 主表字段（来自 PurchaseContractDO / PurchaseContractInfoRespVO） =====

    @Schema(description = "明细主键（用于前端表格唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "合同主键")
    private Long contractId;
    
    @Schema(description = "合同编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "自动生成标识")
    private Integer autoFlag;

    @Schema(description = "采购主体主键")
    private Long companyId;

    @Schema(description = "采购主体名称")
    private String companyName;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "供应商主键")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "交易币种")
    private String currency;

    @Schema(description = "付款方式主键")
    private Long paymentId;

    @Schema(description = "付款方式名称")
    private String paymentName;

    @Schema(description = "业务员")
    private UserDept sales;

    @Schema(description = "业务员昵称（扁平化字段）")
    private String salesNickname;

    @Schema(description = "业务员部门名称（扁平化字段）")
    private String salesDeptName;

    @Schema(description = "跟单员")
    private UserDept manager;

    @Schema(description = "跟单员昵称（扁平化字段）")
    private String managerNickname;

    @Schema(description = "跟单员部门名称（扁平化字段）")
    private String managerDeptName;

    @Schema(description = "采购日期")
    private LocalDateTime purchaseTime;

    @Schema(description = "交货日期")
    private LocalDateTime deliveryDate;

    @Schema(description = "回签日期")
    private LocalDateTime signBackTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建人部门")
    private String creatorDeptName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "合同状态")
    private Integer contractStatus;

    @Schema(description = "验货状态")
    private Integer checkStatus;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "采购总金额")
    private JsonAmount totalAmount;

    @Schema(description = "采购总数量")
    private Integer totalQuantity;

    @Schema(description = "订单路径")
    private JsonCompanyPath companyPath;

    // ===== 明细字段（来自 PurchaseContractItem） =====

    @Schema(description = "明细主键")
    private Long itemId;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "采购数量")
    private Integer quantity;

    @Schema(description = "采购单价（含税）")
    private JsonAmount withTaxPrice;

    @Schema(description = "采购金额（含税总计）")
    private JsonAmount withTaxTotal;

    @Schema(description = "含税总价（人民币）")
    private JsonAmount totalPriceRmb;

    @Schema(description = "采购类型")
    private Integer purchaseType;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "供应商交货日期")
    private LocalDateTime itemVenderDeliveryDate;

    @Schema(description = "入库状态")
    private Integer itemStatus;

    @Schema(description = "自主品牌标记")
    private Integer ownBrandFlag;

    @Schema(description = "规格列表")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "采购员")
    private UserDept purchaseUser;

    @Schema(description = "采购员昵称（扁平化字段）")
    private String purchaseUserNickname;

    @Schema(description = "采购员部门名称（扁平化字段）")
    private String purchaseUserDeptName;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "产品类型")
    private Integer skuType;

    @Schema(description = "到料时间")
    private LocalDateTime planArriveDate;

    @Schema(description = "验货结果")
    private Integer itemCheckStatus;

    @Schema(description = "入库地点")
    private Integer billStatus;

    @Schema(description = "已入库数量")
    private Integer billQuantity;

    @Schema(description = "已转入库通知数量")
    private Integer noticedQuantity;

    @Schema(description = "待转入库通知数量")
    private Integer unNoticedQuantity;

    @Schema(description = "生产完成标记")
    private Integer produceCompleted;

    @Schema(description = "入库通知转换标记")
    private Integer convertNoticeFlag;

    @Schema(description = "开票通知状态")
    private Integer invoiceStatus;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "是否翻单")
    private Integer reorderFlag;

    @Schema(description = "海关编码")
    private String hsCode;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "唯一编码")
    private String uniqueCode;
}
