package com.syj.eplus.module.wms.controller.admin.stock.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 查询产品批次库存 Request VO")
@Data
public class StockQueryVO extends PageParam {

    @Schema(description = "归属公司主键", example = "21354")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "芋艿")
    private String companyName;

    @Schema(description = "仓库主键", example = "4730")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "芋艿")
    private String warehouseName;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "客户名称", example = "芋艿")
    private String custName;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "产品名称", example = "李四")
    private String skuName;

    @Schema(description = "自营产品标记")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标记")
    private Integer custProFlag;

    @Schema(description = "归属公司")
    private Long companyid;

    @Schema(description = "产品编码", example = "4758")
    private String basicSkuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "剩余库存max")
    private Integer remainderQuantityMax;

    @Schema(description = "剩余库存min")
    private Integer remainderQuantityMin;

    @Schema(description = "客户编码", example = "31999")
    private String custCode;

    @Schema(description = "入库时间")
    private LocalDateTime[] receiptTime;
}
