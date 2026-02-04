package com.syj.eplus.module.crm.controller.admin.collectionaccount.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 收款账号分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CollectionAccountPageReqVO extends PageParam {

//    @Schema(description = "默认标记")
//    private Integer defaultFlag;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "内部公司主键", example = "24894")
    private Long companyId;

    @Schema(description = "内部公司银行账号主键", example = "7066")
    private Long companyBankId;

    @Schema(description = "客户主键", example = "24894")
    private Long custId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}