package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 外销合同-明细-分配利润信息")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class SaleContractItemAllocation {

    /**
     * 销售合同明细主键
     */
    @Schema(description = "销售合同明细主键")
    private Long saleContractItemId;

    /**
     * 销售公司-主键
     */
    @Schema(description = "销售公司-主键")
    private Long saleCompanyId;

    /**
     * 销售公司-名称
     */
    @Schema(description = "销售公司-名称")
    private String saleCompanyName;

    /**
     * 销售公司-销售价格
     */
    @Schema(description = "销售公司-销售价格")
    private JsonAmount salePrice;

    /**
     * 销售公司-采购价格
     */
    @Schema(description = "销售公司-采购价格")
    private JsonAmount purchasePrice;

    /**
     * 销售公司-利润
     */
    @Schema(description = "销售公司-利润")
    private JsonAmount profit;

    /**
     * 销售公司-原利润点
     */
    @Schema(description = "销售公司-原利润点")
    private JsonAmount originProfitRatio;

    /**
     * 销售公司-分配利润点
     */
    @Schema(description = "销售公司-分配利润点")
    private JsonAmount allocateProfitRatio;

    /**
     * 泛太真实采购价格-末尾节点采购价格
     */
    @Schema(description = "泛太真实采购价格-末尾节点采购价格")
    private JsonAmount finalPurchasePrice;

    /**
     * 采购公司-主键
     */
    @Schema(description = "采购公司-主键")
    private Long purchaseCompanyId;

    /**
     * 采购公司-名称
     */
    @Schema(description = "采购公司-名称")
    private String purchaseCompanyName;

    /**
     * 利润分配类型(allocate_type)：1-不分配 2-固定利润率 3-随机利润率
     */
    private Integer allocateType;

    /**
     * 分配范围-低(包含)
     */
    private Double rangeMinRatio;

    /**
     * 分配范围-高(不包含)
     */
    private Double rangeMaxRatio;

    /**
     * 固定利润率(范围随机时取此值)
     */
    private Double fixRatio;

    /**
     * 利润分配条件-类型(allocate_condition_type) 1-无 2-> 3->= 4-< 5-<=
     */
    private Integer allocateConditionType;

    /**
     * 分配条件-值
     */
    private Double allocateConditionValue;
}
