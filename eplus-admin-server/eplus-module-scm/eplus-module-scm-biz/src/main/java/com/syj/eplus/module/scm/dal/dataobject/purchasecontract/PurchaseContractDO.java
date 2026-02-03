package com.syj.eplus.module.scm.dal.dataobject.purchasecontract;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.handler.JsonVenderPocHandler;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 采购合同 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_purchase_contract", autoResultMap = true)
@KeySequence("scm_purchase_contract_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseContractDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
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
     * 确认状态
     */
    private Integer confirmFlag;
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
    @CompareField(value = "采购总金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;
    /**
     * 采购总数量
     */
    @CompareField(value = "采购总数量")
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
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount prepayAmount;
    /**
     * 付款状态
     */
    private Integer payStatus;
    /**
     * 已付款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount payedAmount;
    /**
     * 开票状态
     */
    private Integer invoiceStatus;
    /**
     * 转入库通知单标识
     */
    @CompareField(value = "转入库通知单标识",enumType = "convert_notice_flag")
    private Integer convertNoticeFlag;
    /**
     * 已开票金额
     */
    @CompareField(value = "已开票金额")
    private BigDecimal invoicedAmount;

    @CompareField(value = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

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
    @CompareField(value = "备注")
    private String remark;
    /**
     * 附件
     */
    @CompareField(value = "附件")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
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
    @CompareField(value = "付款方式")
    private String paymentName;


    /**
     * 目的口岸
     */
    private Long portId;
    /**
     * 运费
     */
    @CompareField(value = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount freight;
    /**
     * 其他费用
     */
    @CompareField(value = "其他费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount otherCost;
    /**
     * 交货日期
     */
    @CompareField(value = "交货日期")
    private LocalDateTime deliveryDate;

    @CompareField(value = "初始交货日期")
    private LocalDateTime initDeliveryDate;

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
    @CompareField(value = "分摊方式",enumType = "equally_type")
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
     * 生产完成标识:0-否 1-是
     */
    private Integer produceCompleted;
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
    @CompareField(value = "应付供应商")
    private String paymentVenderName;

    /**
     * 已开票数量
     */
    private BigDecimal invoicedQuantity;

    /**
     * 币种
     */
    private String currency;
    /**
     * 登票币种
     */
    private String invoicedCurrency;
    /**
     * 回签描述
     */
    private String signBackDesc;

    private Integer changeStatus;
    /**
     * 自动生成标识 0-否 1-是
     */
    private Integer autoFlag;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 出片文件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> designDraftList;

    /**
     * 回签附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> signBackAnnexList;
    /**
     * 下单标记
     */
    private Integer placeOrderFlag;
    /**
     * 下单时间
     */
    private LocalDateTime placeOrderTime;
    /**
     * 采购计划来源方式
     */
    private Integer planSourceType;
    /**
     * 验货状态：0-未验货、1-部分通过、2-全部通过
     */
    private Integer checkStatus;
    /**
     * 翻单标记
     */
    private Integer repeatFlag;
    /**
     * 供应商联系人
     */
    @TableField(typeHandler = JsonVenderPocHandler.class)
    private VenderPocDO venderPoc;

    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    /**
     * 箱带颜色
     */
    @CompareField(value = "箱带颜色")
   private Integer boxWithColor;
    /**
     * 样品套数
     */
    @CompareField(value = "样品套数")
    private Integer sampleCount;

    /**
     * 内部生成编号
     */
    private String genContractUniqueCode;

    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 包材转对公标记
     */
    private Integer auxiliaryPaymentFlag;

    /**
     * 最低备品比例
     */
    private BigDecimal minimumBaseQuantity;

    /**
     * 乙方补货时限
     */
    private Integer restockingDeadline;

    /**
     * 辅料属于的采购员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> auxiliaryPurchaseUser;

    /**
     * 辅料属于的销售员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> auxiliarySales;

    /**
     * 辅料属于的跟单员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> auxiliaryManager;

    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 同步标记
     */
    private  long syncCode;

    /**
     * 采购总额人民币
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmountRmb;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    @TableField(exist = false)
    private List<PurchaseContractItemDO> purchaseContractItem;
}
