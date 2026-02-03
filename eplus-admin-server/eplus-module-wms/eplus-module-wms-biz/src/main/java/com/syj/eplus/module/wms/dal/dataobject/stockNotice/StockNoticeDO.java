package com.syj.eplus.module.wms.dal.dataobject.stockNotice;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓储管理-入(出)库通知单 DO
 *
 * @author ePlus
 */

@TableName(value = "wms_notice", autoResultMap = true)
@KeySequence("wms_notice_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockNoticeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 单号
     */
    private String code;
    /**
     * 通知类型 1-入库通知单、2-出库通知单
     *
     * 枚举 {@link TODO in_out_type 对应的类}
     */
    private Integer noticeType;
    /**
     * 通知单状态 1-未转 2-已转 3-作废
     *
     * 枚举 {@link TODO notice_status 对应的类}
     */
    private Integer noticeStatus;
    /**
     * 通知时间
     */
    private LocalDateTime noticeTime;
    /**
     * 预计到/出货日期
     */
    private LocalDateTime expectDate;
    /**
     * 归属公司主键
     */
    private Long companyId;
    /**
     * 归属公司名称
     */
    private String companyName;
    /**
     * 单据总体积(cm³)
     */
    private BigDecimal totalVolume;
    /**
     * 单据总毛重（{数量,单位}）
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;
    /**
     * 打印状态 0-未打印 1-已打印
     *
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 备注
     */
    private String remark;

    /**
     * 进仓日期
     */
    private LocalDateTime inboundDate;

    /**
     * 提单号
     */
    private String referenceNumber;

    /**
     * 是否拉柜通知单
     */
    private Integer isContainerTransportation;

    /**
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;

    /**
     * 销售合同编号列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> saleContractCodeList;

    /**
     * 采购合同编号列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> purContractCodeList;

    /**
     * 出运明细编号
     */
    private String shipmentCode;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 出运发票号
     */
    private String invoiceCode;

    /**
     * 出运方式
     */
    private Integer shipmentType;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 流程id
     */
    private String processInstanceId;

    /**
     * 手动标识
     */
    private Integer manualFlag;
}
