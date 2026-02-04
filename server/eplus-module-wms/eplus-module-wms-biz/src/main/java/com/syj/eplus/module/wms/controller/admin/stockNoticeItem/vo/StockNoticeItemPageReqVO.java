package com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-入(出)库通知单-明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockNoticeItemPageReqVO extends PageParam {

    @Schema(description = "入/出库通知单主键", example = "6479")
    private Long noticeId;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "1")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "16463")
    private Long skuId;

    @Schema(description = "产品中文名称", example = "赵六")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "供应商主键", example = "5424")
    private Long venderId;

    @Schema(description = "供应商名称", example = "芋艿")
    private String venderName;

    @Schema(description = "客户主键", example = "24865")
    private Long custId;

    @Schema(description = "客户名称", example = "李四")
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

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
