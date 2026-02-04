package com.syj.eplus.module.wms.controller.admin.billitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-入(出)库单-明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BillItemPageReqVO extends PageParam {

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "来源单主键", example = "32336")
    private Long sourceId;

    @Schema(description = "来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库", example = "1")
    private Integer sourceType;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "2")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "9587")
    private Long skuId;

    @Schema(description = "产品中文名称", example = "芋艿")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "仓库主键", example = "2270")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "23271")
    private Long venderId;

    @Schema(description = "供应商名称", example = "赵六")
    private String venderName;

    @Schema(description = "客户主键", example = "13738")
    private Long custId;

    @Schema(description = "客户名称", example = "王五")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "应收/出数量")
    private Integer orderQuantity;

    @Schema(description = "应收/出箱数")
    private Integer orderBoxQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "实收/出数量")
    private Integer actQuantity;

    @Schema(description = "实收/出箱数")
    private Integer actBoxQuantity;

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "归属公司主键", example = "28431")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "张三")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "出运明细id")
    private Long shipmentItemId;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}
