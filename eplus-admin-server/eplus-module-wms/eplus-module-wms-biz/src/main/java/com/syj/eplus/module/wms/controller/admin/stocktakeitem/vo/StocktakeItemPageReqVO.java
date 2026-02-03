package com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-盘点单-明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StocktakeItemPageReqVO extends PageParam {

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "盘点单-主键", example = "5150")
    private Long stocktakeId;

    @Schema(description = "产品主键", example = "10504")
    private Long skuId;

    @Schema(description = "产品中文名称", example = "李四")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "仓库主键", example = "22582")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "库存数量")
    private Integer stockQuantity;

    @Schema(description = "库存箱数")
    private Integer stockBoxQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "盘点位置")
    private String stocktakePosition;

    @Schema(description = "盘点数量")
    private Integer stocktakeStockQuantity;

    @Schema(description = "盘点箱数")
    private Integer stocktakeStockBoxQuantity;

    @Schema(description = "供应商主键", example = "16329")
    private Long venderId;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "客户主键", example = "17435")
    private Long custId;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "归属公司主键", example = "5937")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    private String companyName;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}