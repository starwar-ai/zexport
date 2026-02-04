package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
public class PurchaseContractAllDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 版本
     */
    private Integer ver;
    /**
     * 采购合同编号
     */
    private String code;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 采购合同状态
     */
    private Integer contractStatus;
    /**
     * 采购总金额
     */
    private JsonAmount totalAmount;
    /**
     * 采购总数量
     */
    private Integer totalQuantity;
    /**
     * 打印状态
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 预付款状态
     */
    private Integer prepayStatus;
    /**
     * 预付款金额
     */
    private JsonAmount prepayAmount;
    /**
     * 付款状态
     */
    private Integer payStatus;
    /**
     * 已付款金额
     */
    private JsonAmount payedAmount;
    /**
     * 开票状态
     */
    private Integer invoiceStatus;
    /**
     * 已开票金额
     */
    private BigDecimal invoicedAmount;

    /**
     * 采购员编码
     */
    private Long purchaseUserId;
    /**
     * 采购员名称
     */
    private String purchaseUserName;
    /**
     * 采购员部门编码
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    private String purchaseUserDeptName;
    /**
     * 客户主键
     */
    private Long custId;
    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 仓库主键
     */
    private Long stockId;
    /**
     * 仓库编码
     */
    private String stockCode;
    /**
     * 供应商编码
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 客户编码
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 仓库名称
     */
    private String stockName;
    /**
     * 采购计划id
     */
    private Long purchasePlanId;
    /**
     * 采购计划编号
     */
    private String purchasePlanCode;
    /**
     * 销售合同id
     */
    private Long saleContractId;
    /**
     * 销售合同编号
     */

    private String saleContractCode;
    /**
     * 备注
     */

    private String remark;
    /**
     * 附件
     */

    private List<SimpleFile> annex;
    /**
     * 采购主体
     */

    private Long companyId;
    /**
     * 采购时间
     */
    private LocalDateTime purchaseTime;
    /**
     * 付款方式id
     */

    private Long paymentId;
    /**
     * 付款方式名称
     */

    private String paymentName;


    /**
     * 目的口岸
     */

    private Long portId;
    /**
     * 运费
     */
    private JsonAmount freight;
    /**
     * 其他费用
     */
    private JsonAmount otherCost;
    /**
     * 交货日期
     */
    private LocalDateTime deliveryDate;
    /**
     * 处理时间
     */
    private LocalDateTime dealTime;
    /**
     * 结案时间
     */
    private LocalDateTime finishTime;
    /**
     * 完成时间
     */

    private LocalDateTime doneTime;
    /**
     * 回签时间
     */

    private LocalDateTime signBackTime;
    /**
     * 下单时间
     */

    private LocalDateTime orderTime;
    /**
     * 回签
     */
    private Integer signBackFlag;
    /**
     * 发票类型
     */

    private Integer taxType;
    /**
     * 分摊方式
     */

    private Integer equallyType;
    /**
     * 是否辅料采购
     */

    private Integer auxiliaryFlag;

    /**
     * 重构标记
     */
    private Integer rePurchaseFlag;

    /**
     * 重构原因
     */
    private String rePurchaseDesc;

    /**
     * 重构时间
     */
    private LocalTime rePurchaseTime;

    /**
     * 重构旧版本标记
     */
    private Integer rePurchaseOldFlag;
    /**
     * 应付供应商主键
     */
    private Long paymentVenderId;

    /**
     * 应付供应商编号
     */
    private String paymentVenderCode;

    /**
     * 应付供应商名称
     */
    private String paymentVenderName;

    /**
     * 已开票数量
     */
    private BigDecimal invoicedQuantity;

    /**
     * 登票币种
     */
    private String invoicedCurrency;
    /**
     * 创建时间
     */

    private  LocalDateTime createTime;
    /**
     * 费用归属金额
     */
    private JsonAmount amount;


    /**
     * 跟单员
     */
    private UserDept manager;

}
