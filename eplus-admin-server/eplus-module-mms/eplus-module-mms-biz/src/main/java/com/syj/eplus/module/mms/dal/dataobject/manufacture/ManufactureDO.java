package com.syj.eplus.module.mms.dal.dataobject.manufacture;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 加工单 DO
 *
 * @author zhangcm
 */

@TableName("mms_manufacture")
@KeySequence("mms_manufacture_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufactureDO extends BaseDO {

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
     * 录入人id
     */
    private Long inputUserId;
    /**
     * 录入人姓名
     */
    private String inputUserName;
    /**
     * 录入时间
     */
    private LocalDateTime inputTime;

    /**
     * 仓库id
     */
    private Long stockId;
    /**
     * 仓库名称
     */
    private String stockName;
    /**
     * 主体id
     */
    private Long companyId;
    /**
     * 主体名称
     */
    private String companyName;
    /**
     * 客户id
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
     * 加工单状态
     */
    private Integer manufactureStatus;
    /**
     * 是否自动生成
     */
    private Integer autoFlag;
    /**
     * 完成加工时间
     */
    private LocalDateTime doneTime;
    /**
     * 结案时间
     */
    private LocalDateTime finishTime;
    /**
     * 结案原因
     */
    private String finishDesc;

    /**
     * 销售合同号
     */
    private String saleContractCode;
}