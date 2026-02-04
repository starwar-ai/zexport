package com.syj.eplus.module.wms.dal.dataobject.stocktake;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 仓储管理-盘点单 DO
 *
 * @author ePlus
 */

@TableName("wms_stocktake")
@KeySequence("wms_stocktake_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 盘点单号
     */
    private String code;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 盘库状态  1-未开始、2-盘库中、3-已结束
     */
    private Integer stocktakeStatus;
    /**
     * 预计盘点日期
     */
    private LocalDateTime planDate;
    /**
     * 实际开始时间
     */
    private LocalDateTime actStartTime;
    /**
     * 实际结束时间
     */
    private LocalDateTime actEndTime;
    /**
     * 采购合同主键
     */
    private Long purchaseContractId;
    /**
     * 采购合同号
     */
    private String purchaseContractCode;
    /**
     * 销售合同主键
     */
    private Long saleContractId;
    /**
     * 销售合同号
     */
    private String saleContractCode;
    /**
     * 仓库主键
     */
    private Long warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 盘点人主键
     */
    private Long stocktakeUserId;
    /**
     * 盘点人姓名
     */
    private String stocktakeUserName;
    /**
     * 打印状态 0-未打印 1-已打印
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 归属公司主键
     */
    private Long companyId;
    /**
     * 归属公司名称
     */
    private String companyName;
    /**
     * 备注
     */
    private String remark;

}
