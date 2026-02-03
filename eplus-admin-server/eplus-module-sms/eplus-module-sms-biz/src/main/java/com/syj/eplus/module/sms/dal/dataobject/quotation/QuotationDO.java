package com.syj.eplus.module.sms.dal.dataobject.quotation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 报价单主 DO
 *
 * @author ePlus
 */

@TableName(value= "sms_quotation", autoResultMap = true)
@KeySequence("sms_quotation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 内部法人单位主键
     */
    private Long companyId;
    /**
     * 内部法人单位名称
     */
    private String companyName;
    /**
     * 客户主键
     */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 是否新客户
     */
    private Integer isNewCust;
    /**
     * 价格条款
     */
    private String settlementTermType;

    /**
     * 币种
     */
    private String currency;
    /**
     * 客户联系人
     */
    private Long custPocId;

    /**
     * 客户联系人名称
     */
    private String custPocName;
    /**
     * 国家id
     */
    private Long countryId;

    /**
     * 国家名称
     */
    private String countryName;
    /**
     * 出运口岸主键
     */
    private Long departurePortId;
    /**
     * 出运口岸名称
     */
    private String departurePortName;
    /**
     * 有效期止
     */
    private LocalDateTime validPeriod;
    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 流程id
     */
    private String processInstanceId;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 报价单号
     */
    private String code;

    /**
     * 打印标识
     */
    private Integer printFlag;

}