package com.syj.eplus.module.dal.dataobject.sendbill;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 寄件导入单据 DO
 *
 * @author zhangcm
 */

@TableName("ems_send_bill")
@KeySequence("ems_send_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendBillDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 日期
     */
    private String billDate;
    /**
     * 运单号码
     */
    private String code;

    /**
     * 费用
     */
    private BigDecimal cost;

    /**
     * 备注
     */
    private String remark;
    /**
     * 快递公司id
     */
    private Long venderId;
    /**
     * 快递公司编号
     */
    private String venderCode;
    /**
     * 快递公司名称
     */
    private String venderName;
    /**
     * 导入序号
     */
    private Integer sortNum;
    /**
     * 导入类型
     */
    private Integer importType;
    /**
     * 异常描述
     */
    private String errorDesc;
    /**
     * 币种
     */
    private String currency;
}