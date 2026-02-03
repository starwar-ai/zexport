package com.syj.eplus.module.wms.controller.admin.stock.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-库存明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockPageReqVO extends PageParam {

    @Schema(description = "客户名称", example = "芋艿")
    private String custName;

    @Schema(description = "产品编码", example = "王五")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "公司名称")
    private String  companyName;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    private Long purchaseUserId;

    private Long purchaseUserDeptId;

    @Schema(description = "入库时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] receiptTime;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

    @Schema(description = "批次编号")
    private String batchCode;

    @Schema(description = "仓库id")
    private Set<Long> warehouseIdSet;
}
