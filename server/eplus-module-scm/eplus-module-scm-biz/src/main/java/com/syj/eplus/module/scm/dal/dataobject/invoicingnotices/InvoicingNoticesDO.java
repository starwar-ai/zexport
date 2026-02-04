package com.syj.eplus.module.scm.dal.dataobject.invoicingnotices;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 开票通知 DO
 *
 * @author du
 */

@TableName(value = "scm_invoicing_notices", autoResultMap = true)
@KeySequence("scm_invoicing_notices_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicingNoticesDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 归属公司主键
     */
    private Long companyId;
    /**
     * 归属公司名称
     */
    private String companyName;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 跟单员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;
    /**
     * 出运单号
     */
    private String shipmentCode;
    /**
     * 出运发票号
     */
    private String invoiceCode;
    /**
     * 出运日期
     */
    private LocalDateTime shipDate;
    /**
     * 打印状态
     */
    private Integer printFlag;
    /**
     * 采购单号
     */
    private String purOrderCode;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否手动生成
     */
    private Integer manuallyFlag;

    /**
     * 出运发票号
     */
    private String shipInvoiceCode;

    /**
     * 来源类型
     */
    private Integer sourceType;

    /**
     * 打印日期
     */
    private LocalDateTime printDate;
    
    /* 
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 流程实例主键
     */
    private String processInstanceId;

    /**
     * 登票日期
     */
    private LocalDateTime registrationDate;
}