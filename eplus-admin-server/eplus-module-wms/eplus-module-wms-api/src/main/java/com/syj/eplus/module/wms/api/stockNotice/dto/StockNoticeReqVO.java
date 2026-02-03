package com.syj.eplus.module.wms.api.stockNotice.dto;

import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Desc——采购合同转入库通知单-请求主表
 * Create by Rangers at  2024-06-11 22:09
 */
@Data
public class StockNoticeReqVO {


    /**
     * 预计到/出货日期
     */
    @Schema(description = "预计到/出货日期")
    private LocalDateTime expectDate;

    /**
     * 销售合同主键
     */
    @Schema(description = "销售合同主键", example = "828")
    private Long saleContractId;

    /**
     * 销售合同号
     */
    @Schema(description = "销售合同号")
    private String saleContractCode;

    /**
     * 单据总体积(cm³)
     */
    private BigDecimal totalVolume;

    /**
     * 单据总毛重（{数量,单位}）
     */
    private JsonWeight totalWeight;


    /**
     * 归属公司主键
     */
    @Schema(description = "归属公司主键", example = "5879")
    private Long companyId;

    /**
     * 归属公司名称
     */
    @Schema(description = "归属公司名称", example = "赵六")
    private String companyName;

    /**
     * 仓库主键
     */
    @Schema(description = "仓库主键", example = "10095")
    private Long warehouseId;


    /**
     * 仓库名称
     */
    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "小心")
    private String remark;


    private List<StockNoticeItemReqVO> stockNoticeItemReqVOList;

    /**
     * 采购合同明细编号
     */
    private String purchaseItemUniqueCode;

    /**
     * 销售合同明细编号
     */
    private String saleItemUniqueCode;

    /**
     * 订单链路编号
     */
    private List<String> linkCodeList;

    private List<String> purContractCodeList;
}
