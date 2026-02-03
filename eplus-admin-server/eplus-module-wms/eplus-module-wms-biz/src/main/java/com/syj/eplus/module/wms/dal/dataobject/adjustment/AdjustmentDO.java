package com.syj.eplus.module.wms.dal.dataobject.adjustment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 仓储管理-盘库调整单 DO
 *
 * @author ePlus
 */

@TableName("wms_adjustment")
@KeySequence("wms_adjustment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 调整单单号
     */
    private String code;
    /**
     * 盘点单主键
     */
    private Long stocktakeId;
    /**
     * 盘点单单号
     */
    private String stocktakeCode;
    /**
     * 盘点人主键
     */
    private Long stocktakeUserId;
    /**
     * 盘点人姓名
     */
    private String stocktakeUserName;
    /**
     * 调整类型  1-盘盈单 2-盘亏单
     */
    private Integer adjustmentType;
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
     * 调整日期
     */
    private LocalDateTime adjustmentDate;
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
