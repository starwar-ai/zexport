package com.syj.eplus.module.oa.dal.dataobject.subject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 科目 DO
 *
 * @author ePlus
 */

@TableName("oa_subject")
@KeySequence("oa_subject_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDO extends BaseDO {

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
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 层次
     */
    private Integer layer;
    /**
     * 科目性质
     */
    private Integer nature;
    /**
     * 科目类型
     */
    private String type;
    /**
     * 辅助核算
     */
    private String auxiliaryAccounting;
    /**
     * 核算编号
     */
    private String accountingNumber;
    /**
     * 是否外币核算
     */
    private Integer isForeignCurrencyAccounting;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否期末调汇
     */
    private Integer isFinalExchange;
    /**
     * 是否银行科目
     */
    private Integer isBank;
    /**
     * 是否现金科目
     */
    private Integer isCash;
    /**
     * 是否现金银行
     */
    private Integer isCashBank;
    /**
     * 父级科目id
     */
    private Long parentId;
    /**
     * 父级科目名称
     */
    private String parentName;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;
    /**
     * 是否现金流量相关
     */
    private Integer isCashFlowRelated;
    /**
     * 是否末级
     */
    private Integer isLast;
    /**
     * 现金流量编号
     */
    private String cashFlowCode;
    /**
     * 现金流量名称
     */
    private String cashFlowName;
    /**
     * 银行账户
     */
    private String bankAccount;
    /**
     * 银行账号
     */
    private String bankCode;
    /**
     * 余额方向
     */
    private Integer balanceDirection;
    /**
     * 科目余额
     */
    private Long balance;

}