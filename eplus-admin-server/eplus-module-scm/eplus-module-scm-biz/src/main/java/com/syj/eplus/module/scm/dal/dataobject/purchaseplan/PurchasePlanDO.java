package com.syj.eplus.module.scm.dal.dataobject.purchaseplan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购计划 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_purchase_plan",autoResultMap = true)
@KeySequence("scm_purchase_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanDO extends BaseDO {

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
     * 编号
     */
    @CompareField(value = "编号")
    private String code;
    /**
     * 待提交、待审核、已驳回、已完成、撤销
     */
    private Integer auditStatus;
    /**
     * 待审核、待采购、已驳回、已完成、已结案
     */
    private Integer planStatus;

    /**
     * 客户ID
     */

    private Long custId;
    /**
     * 客户编号
     */
    @CompareField(value = "客户编号")
    private String custCode;
    /**
     * 客户名称
     */
    @CompareField(value = "客户姓名")
    private String custName;
    /**
     * 来源单类型：1-手工创建  2-外销合同生成
     */
    private Integer sourceType;

    /**
     * 备注
     */
    @CompareField(value = "备注")
    private String remark;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    @CompareField(value = "附件")
    private List<SimpleFile> annex;
    /**
     * 采购主体
     */
    @CompareField(value = "采购主体")
    private Long companyId;
    /**
     * 预计交期
     */
    @CompareField(value = "预计交期")
    private LocalDateTime planDate;

    /**
     * 结案时间
     */
    @CompareField(value = "结案时间")
    private LocalDateTime finishTime;
    /**
     * 完成时间
     */
    @CompareField(value = "完成时间")
    private LocalDateTime doneTime;
    /**
     * 预计交期
     */
    @CompareField(value = "预计交期")
    private LocalDateTime estDeliveryDate;
    /**
     * 是否辅料采购
     */
    @CompareField(value = "是否辅料采购")
    private Integer auxiliaryFlag;

    /**
     * 销售合同主键
     */
    private Long saleContractId;

    /**
     * 销售合同编号
     */
    private String saleContractCode;

    /**
     * 订单路径
     */
    @CompareField(value = "订单路径")
    @TableField(typeHandler = JsonCompanyPathTypeHandler.class)
    private JsonCompanyPath companyPath;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    @Schema(description = "采购员")
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> purchaseUserList;

    @Schema(description = "来源计划id")
    private Long sourcePlanId;

    @Schema(description = "拆分标识")
    private Integer splitFlag;

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
     * 创建人部门id
     */
    private Long creatorDeptId;

    /**
     * 销售类型
     */
    private Integer saleType;
}
