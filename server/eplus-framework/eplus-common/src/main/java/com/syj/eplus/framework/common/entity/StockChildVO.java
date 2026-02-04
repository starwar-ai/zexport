package com.syj.eplus.framework.common.entity;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import com.syj.eplus.framework.common.annotations.CompareField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/28/15:16
 * @Description:
 */
@Data
public class StockChildVO {

    /**
     * 销售合同单号
     */
    private String saleContractCode;

    /**
     *产品主键
     */
    private Long skuId;

    /**
     *产品编号
     */
    private String skuCode;

    /**
     *产品名称
     */
    private String skuName;

    /**
     *产品类型
     */
    private Integer skuType;

    /**
     *采购模式
     */
    private Integer purchaseModel;

    /**
     *出运数量
     */
    private Integer shippingQuantity;

    /**
     *进仓日期
     */
    private LocalDateTime estPickupTime;

    /**
     *提单号
     */
    private String referenceNumber;

    /**
     *采购合同id
     */
    private Long purchaseContractId;

    /**
     *采购合同编号
     */
    private String purchaseContractCode;

    /**
     *供应商编号
     */
    private String venderName;

    /**
     *箱数
     */
    private Integer boxCount;

    /**
     *总体积
     */
    private BigDecimal totalVolume;

    /**
     *总毛重
     */
    private JsonWeight totalGrossweight;

    /**
     *库存批次明细
     */
    private List<BatchChildVO> children;

    /**
     *出运明细主键
     */
    private Long shipmentItemId;

    /**
     *转拉柜通知单标识 0-否 1-是
     */
    private Integer converNoticeFlag;

    /**
     * 采购员id
     */
    private Long purchaseUserId;

    /**
     * 采购员姓名
     */
    private String purchaseUserName;

    /**
     * 采购员部门id
     */
    private Long purchaseUserDeptId;

    /**
     * 采购员部门名称
     */
    private String purchaseUserDeptName;

    /**
     * 跟单员
     */
    private UserDept manager;

    /**
     * 客户货号
     */
    @CompareField(value = "客户货号")
    private String cskuCode;

    /**
     * 基础产品编号
     */
    @CompareField(value = "基础产品编号")
    private String basicSkuCode;


    /**
     *客户主键
     */
    @CompareField(value = "客户主键")
    private Long custId;


    /**
     *客户编号
     */
    @CompareField(value = "客户编号")
    private String custCode;

    /**
     *客户名称
     */
    @CompareField(value = "客户名称")
    private String custName;

    /**
     *外销员
     */
    @CompareField(value = "外销员")
    private UserDept sales;

    /**
     *可拉柜数量
     */
    private Integer availableCabinetQuantity;

    /**
     *在制数量
     */
    private Integer producingQuantity;

    /**
     *采购合同CodeList
     */
    private List<String> purchaseContractCodeList;

    /**
     *出运明细唯一标识
     */
    private String sourceUniqueCode;

    /**
     *销售明细主键
     */
    private Long saleContractItemId;

    /**
     *自动加工标识
     */
    private Integer manufactureFlag;
    /**
     *出库数量
     */
    private Integer quantity;

    /**
     * 客户po号
     */
    private String custPo;

    /**
     * 外箱装量
     */
    @ChangeIgnore
    private Integer qtyPerOuterbox;
}
