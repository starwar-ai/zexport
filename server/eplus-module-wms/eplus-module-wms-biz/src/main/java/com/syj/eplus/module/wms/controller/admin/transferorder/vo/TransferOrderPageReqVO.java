package com.syj.eplus.module.wms.controller.admin.transferorder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 调拨分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TransferOrderPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "库存主体主键", example = "29248")
    private Long companyId;

    @Schema(description = "库存主体名称", example = "芋艿")
    private String companyName;

    @Schema(description = "调拨类型", example = "1")
    private Integer transferType;

    @Schema(description = "拨入订单号")
    private String saleContractCode;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "王五")
    private String custName;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}