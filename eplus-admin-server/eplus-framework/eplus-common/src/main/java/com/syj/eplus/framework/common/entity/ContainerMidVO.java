package com.syj.eplus.framework.common.entity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/28/15:12
 * @Description:
 */
@Data
public class ContainerMidVO {
    /**
     * 归属公司
     */
    private Long companyId;

    /**
     * 进仓日期
     */
    private LocalDateTime inboundDate;

    /**
     * 提单号
     */
    private String referenceNumber;

    /**
     * 申请人
     */
    private UserDept applyer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 库存明细列表
     */
    private List<StockChildVO> children;

    /**
     * 出运编号
     */
    private String shipmentCode;

    /**
     * 出运发票号
     */
    private String invoiceCode;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;

    /**
     * 出运明细主键
     */
    private List<Long> shipmentItemIdList;

    /**
     * 出运方式
     */
    private Integer shipmentType;

    /**
     * 是否工厂出库
     */
    private Integer factoryOutboundFlag;
}
