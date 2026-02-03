package com.syj.eplus.module.wms.dal.dataobject.stocklock;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 仓储管理-库存明细-锁定库存 DO
 *
 * @author ePlus
 */

@TableName("wms_stock_lock")
@KeySequence("wms_stock_lock_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLockDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 产品库存主键
     */
    private Long stockId;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 产品编码
     */
    private String skuCode;
    /**
     * 原单据主键
     */
//    private Long sourceOrderId;
    /**
    /**
     * 原单据-明细主键
     */
//    private Long sourceOrderItemId;
    /**
     * 原单据单号
     */
    private String sourceOrderCode;
    /**
     * 外销合同主键
     */
    private Long saleContractId;
    /**
     * 外销合同单号
     */
    private String saleContractCode;
    /**
     * 外销合同明细主键
     */
    private Long saleContractItemId;
    /**
     * 原单据类型  1-销售合同 2-加工单 3-调拨单 4-采购计划
     */
    private Integer sourceOrderType;
    /**
     * 锁定类型  1-可用数量 2-未到货库存
     */
    private Integer lockType;
    /**
     * 锁定数量
     */
    private Integer lockQuantity;
    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;
    /**
     * 锁定人主键
     */
    private Long lockByUserId;
    /**
     * 锁定人名称
     */
    private String lockByUserName;
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

    /**
     * 拉柜数量
     */
    private Integer cabinetQuantity;

}
