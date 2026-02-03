package com.syj.eplus.module.wms.dal.dataobject.transferorder;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

/**
 * 调拨 DO
 *
 * @author du
 */

@TableName("wms_transfer_order")
@KeySequence("wms_transfer_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferOrderDO extends BaseDO {

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
     * 库存主体主键
     */
    private Long companyId;
    /**
     * 库存主体名称
     */
    private String companyName;
    /**
     * 调拨类型
     */
    private Integer transferType;
    /**
     * 拨入订单号
     */
    private String saleContractCode;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;
    /**
     * 状态
     */
    private Integer status;

}