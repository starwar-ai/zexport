package com.syj.eplus.module.dms.dal.dataobject.shipmentplan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出运计划单 DO
 *
 * @author du
 */

@TableName(value = "dms_shipment_plan", autoResultMap = true)
@KeySequence("dms_shipment_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPlanDO extends BaseDO {

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
     * 单据状态
     *
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer status;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;
    /**
     * 预计出运时间
     */
    private LocalDateTime estShipDate;
    /**
     * 外销合同号
     */
    private String saleContractCode;
    /**
     * 出运国主键
     */
    private Long departureCountryId;
    /**
     * 出运国名称
     */
    private String departureCountryName;
    /**
     * 出运国区域
     */
    private String departureCountryArea;
    /**
     * 出运口岸主键
     */
    private Long departurePortId;
    /**
     * 出运口岸名称
     */
    private String departurePortName;
    /**
     * 贸易国别主键
     */
    private Long tradeCountryId;
    /**
     * 贸易国别名称
     */
    private String tradeCountryName;
    /**
     * 贸易国别区域
     */
    private String tradeCountryArea;
    /**
     * 目的口岸主键
     */
    private Long destinationPortId;
    /**
     * 目的口岸名称
     */
    private String destinationPortName;
    /**
     * 客户PO号
     */
    private String custPo;
    /**
     * 客户交期
     */
    private LocalDateTime custDeliveryDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 外贸公司主键
     */
    private Long foreignTradeCompanyId;
    /**
     * 外贸公司名称
     */
    private String foreignTradeCompanyName;
    /**
     * 20尺柜
     */
    private Integer twentyFootCabinetNum;
    /**
     * 40尺柜
     */
    private Integer fortyFootCabinetNum;
    /**
     * 40尺高柜
     */
    private Integer fortyFootContainerNum;
    /**
     * 散货
     */
    private BigDecimal bulkHandlingVolume;
    /**
     * 货值合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalGoodsValue;
    /**
     * 数量合计
     */
    private Integer totalQuantity;
    /**
     * 箱数合计
     */
    private Integer totalBoxes;
    /**
     * 毛重合计
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;
    /**
     * 净重合计
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;
    /**
     * 体积合计
     */
    private BigDecimal totalVolume;
    /**
     * 运输方式
     */
    private Integer transportType;

    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

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
     * 价格条款
     */
    private String settlementTermType;

    /**
     * 预计交货日期
     */
    private LocalDateTime estDeliveryDate;

    /**
     * 正面唛头
     */
    private String frontShippingMark;

    /**
     * 侧面唛头
     */
    private String sideShippingMark;

    /**
     * 收货人
     */
    private String receivePerson;

    /**
     * 通知人
     */
    private String notifyPerson;
}