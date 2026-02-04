package com.syj.eplus.module.dms.dal.dataobject.forwarderfee;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 船代费用 DO
 *
 * @author zhangcm
 */

@TableName(value = "dms_forwarder_fee",autoResultMap = true)
@KeySequence("dms_forwarder_fee_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class ForwarderFeeDO extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 出运费用主键
     */
    private Long shipmentId;
    /**
     * 编号
     */
    private String code;
    /**
     * 出运费用编号
     */
    private String shipmentCode;
    /**
     * 发票号
     */
    private String invoiceCode;
    /**
     * 主体主键
     */
    private Long companyId;
    /**
     * 主体名称
     */
    private String companyName;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 费用名称主键
     */
    private Long dictSubjectId;
    /**
     * 费用名称
     */
    private String dictSubjectName;
    /**
     * 费用类型
     */
    private Integer feeType;
    /**
     * 金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 付款状态
     */
    private Integer payStatus;
    /**
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;


    @TableField(exist = false)
    private String codeList;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    /**
     * 变更标识
     */
    @TableField(exist = false)
    @Schema(description = "变更标识")
    @CompareField(value = "变更标识")
    private Integer changeFlag;

    /**
     * 采购员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> purchaseUserList;

    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> managerList;

    /**
     * 对公申请主键
     */
    private Long paymentAppId;

    /**
     * 对公申请编号
     */
    private String paymentAppCode;

    /**
     * 支付状态
     */
    @TableField(exist = false)
    private Integer paymentStatus;

    /**
     * 支付日期
     */
    @TableField(exist = false)
    private LocalDateTime paymentDate;
}