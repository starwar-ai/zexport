package com.syj.eplus.module.scm.controller.admin.quoteitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 供应商报价明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuoteItemPageReqVO extends PageParam {

    @Schema(description = "供应商报价单id", example = "22266")
    private Long quoteId;

    @Schema(description = "SKU编号")
    private String skuCode;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "采购员id", example = "7609")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "李四")
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "20825")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称", example = "赵六")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] quoteDate;

    @Schema(description = "是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    private List<Long> packageType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否含税")
    private Integer faxFlag;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "最小起购量")
    private Integer moq;

    @Schema(description = "包装价", example = "6631")
    private JsonAmount packagingPrice;

    @Schema(description = "运费", example = "17301")
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "1396")
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "7318")
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "12952")
    private JsonAmount withTaxPrice;

    @Schema(description = "交期")
    private Integer delivery;

    @Schema(description = "采购链接", example = "https://www.iocoder.cn")
    private String purchaseUrl;

    @Schema(description = "内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "是否默认")
    private Integer defaultFlag;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}