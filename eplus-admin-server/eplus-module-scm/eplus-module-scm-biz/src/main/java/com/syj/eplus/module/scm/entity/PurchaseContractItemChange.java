package com.syj.eplus.module.scm.entity;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 18:33
 */
@Data
@Accessors(chain = false)
@Schema(description = "采购产品")
public class PurchaseContractItemChange implements ChangeExInterface {
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11767")
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "skuid", example = "31593")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "客户id", example = "18970")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "采购单价")
    private JsonAmount purchasePrice;

    @Schema(description = "采购数量")
    @CompareField(value = "采购数量")
    private Integer quantity;

    @Schema(description = "采购金额")
    private JsonAmount amount;

    @Schema(description = "供应商id", example = "27269")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "交货日期")
    @CompareField(value = "到料日期")
    private LocalDateTime deliveryDate;

    @Schema(description = "验货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer checkStatus;

    @Schema(description = "已验货数量")
    private Integer checkedQuantity;

    @Schema(description = "验收状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer receiveStatus;

    @Schema(description = "已收货数量")
    private Integer receivedQuantity;

    @Schema(description = "退货量")
    private Integer exchangeQuantity;

    @Schema(description = "换货量")
    private Integer returnQuantity;

    @Schema(description = "采购合同单号", example = "15766")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

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
    private LocalDateTime createTime;

    @Schema(description = "包装价", example = "17301")
    private JsonAmount packagingPrice;

    @Schema(description = "运费")
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "31058")
    @CompareField(value = "采购单价")
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "4346")
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "22972")
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    @CompareField(value = "税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @CompareField(value = "采购类型", enumType = "purchase_type")
    private Integer purchaseType;

    @Schema(description = "采购员id", example = "17679")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "张三")
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "11166")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门不名称", example = "王五")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    @CompareField(value = "是否含运费", enumType = "is_int")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    @CompareField(value = "是否含包装", enumType = "is_int")
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    private List<Long> packageType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否含包装")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    private Integer moq;

    @Schema(description = "箱数", example = "3726")
    @CompareField(value = "箱数")
    private Integer boxCount;

    @Schema(description = "入库状态", example = "2")
    private Integer warehousingType;

    @Schema(description = "仓库id列表")
    private String wmsIds;

    @Schema(description = "仓库名称列表")
    private String wmsNames;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品类型")
    private Integer skuType;

    @Schema(description = "旧skuid", example = "31593")
    private Long old_skuId;

    @Schema(description = "旧产品编号")
    private String old_skuCode;

    @Schema(description = "旧产品名称")
    private String old_skuName;

    @Schema(description = "旧客户id", example = "18970")
    private Long old_custId;

    @Schema(description = "旧客户编号")
    private String old_custCode;

    @Schema(description = "旧客户名称")
    private String old_custName;

    @Schema(description = "旧客户货号")
    private String old_cskuCode;

    @Schema(description = "旧采购单价")
    private JsonAmount old_purchasePrice;

    @Schema(description = "旧采购数量")
    private Integer old_quantity;

    @Schema(description = "旧采购金额")
    private JsonAmount old_amount;

    @Schema(description = "旧供应商id", example = "27269")
    private Long old_venderId;

    @Schema(description = "旧供应商编号")
    private String old_venderCode;

    @Schema(description = "旧交货日期")
    private LocalDateTime old_deliveryDate;

    @Schema(description = "旧验货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer old_checkStatus;

    @Schema(description = "旧已验货数量")
    private Integer old_checkedQuantity;

    @Schema(description = "旧验收状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer old_receiveStatus;

    @Schema(description = "旧已收货数量")
    private Integer old_receivedQuantity;

    @Schema(description = "旧退货量")
    private Integer old_exchangeQuantity;

    @Schema(description = "旧换货量")
    private Integer old_returnQuantity;

    @Schema(description = "旧内箱装量")
    private BigDecimal old_qtyPerInnerbox;

    @Schema(description = "旧外箱装量")
    private BigDecimal old_qtyPerOuterbox;

    @Schema(description = "旧包装规格长度")
    private BigDecimal old_packageLength;

    @Schema(description = "旧包装规格宽度")
    private BigDecimal old_packageWidth;

    @Schema(description = "旧包装规格高度")
    private BigDecimal old_packageHeight;

    @Schema(description = "旧包装规格单位")
    private Integer old_packageUnit;

    @Schema(description = "旧单品毛重")
    private JsonWeight old_singleGrossweight;

    @Schema(description = "旧创建时间")
    private LocalDateTime old_createTime;

    @Schema(description = "旧包装价", example = "17301")
    private JsonAmount old_packagingPrice;

    @Schema(description = "旧运费", example = "17301")
    private JsonAmount old_shippingPrice;

    @Schema(description = "旧采购单价", example = "31058")
    private JsonAmount old_unitPrice;

    @Schema(description = "旧总价", example = "4346")
    private JsonAmount old_totalPrice;

    @Schema(description = "旧含税总价", example = "22972")
    private JsonAmount old_withTaxPrice;

    @Schema(description = "旧税率")
    private BigDecimal old_taxRate;

    @Schema(description = "旧采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer old_purchaseType;

    @Schema(description = "旧采购员id", example = "17679")
    private Long old_purchaseUserId;

    @Schema(description = "旧采购员姓名", example = "张三")
    private String old_purchaseUserName;

    @Schema(description = "旧采购员部门id", example = "11166")
    private Long old_purchaseUserDeptId;

    @Schema(description = "旧采购员部门不名称", example = "王五")
    private String old_purchaseUserDeptName;

    @Schema(description = "旧工厂货号")
    private String old_venderProdCode;

    @Schema(description = "旧报价日期")
    private LocalDateTime old_quoteDate;

    @Schema(description = "旧是否含运费")
    private Integer old_freightFlag;

    @Schema(description = "旧是否含包装")
    private Integer old_packageFlag;

    @Schema(description = "旧包装方式", example = "1")
    private Integer old_packageType;

    @Schema(description = "旧币种")
    private String old_currency;

    @Schema(description = "旧是否含包装")
    private Integer old_faxFlag;

    @Schema(description = "旧最小起购量")
    private Integer old_moq;

    @Schema(description = "旧箱数", example = "3726")
    private Integer old_boxCount;

    @Schema(description = "旧入库状态", example = "2")
    private Integer old_warehousingType;

    @Schema(description = "旧仓库id列表")
    private String old_wmsIds;

    @Schema(description = "旧仓库名称列表")
    private String old_wmsNames;

    @Schema(description = "旧图片")
    private SimpleFile old_mainPicture;

    @Schema(description = "旧产品名称")
    private String old_productName;

    @Schema(description = "旧产品类型")
    private String old_skuType;

    private Integer changeFlag;

    @Schema(description = "是否赠品")
    @CompareField(value = "是否赠品", enumType = "is_int")
    private Integer freeFlag;

    @Schema(description = "是否赠品")
    private Integer old_freeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    @Schema(description = "旧规格")
    private List<JsonSpecificationEntity> old_specificationList;
}
