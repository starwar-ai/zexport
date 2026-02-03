package com.syj.eplus.module.wms.dal.dataobject.bill;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓储管理-入(出)库单 DO
 *
 * @author ePlus
 */

@TableName("wms_bill")
@KeySequence("wms_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDO extends BaseDO {

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
     * 入/出库类型 1-入库单、2-出库单
     *
     * 枚举 {@link TODO in_out_type 对应的类}
     */
    private Integer billType;
    /**
     * 单据状态1-未确认 2-已确认 3-作废
     *
     * 枚举 {@link TODO stock_bill_status 对应的类}
     */
    private Integer billStatus;
    /**
     * 入/出库通知单号
     */
    private String noticeCode;
    /**
     * 销售合同主键
     */
    private Long saleContractId;
    /**
     * 销售合同号
     */
    private String saleContractCode;
    /**
     * 入/出库时间
     */
    private LocalDateTime billTime;
    /**
     * 仓库主键
     */
    private Long warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;
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
     * 产品编码
     */
    private String skuCodes;

    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> pictures;

    /**
     * 来源单类型
     */
    private Integer sourceType;

    /**
     * 来源单编号
     */
    private String sourceCode;

    /**
     * 来源单主键
     */
    private Long sourceId;

    /**
     * 出运发票号
     */
    private String invoiceCode;

    /**
     * 内部生成编号
     */
    private String genContractUniqueCode;
}
