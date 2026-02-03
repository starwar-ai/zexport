package com.syj.eplus.module.dms.controller.admin.forwarderfee.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 船代费用分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ForwarderFeePageReqVO extends PageParam {

    @Schema(description = "出运费用主键", example = "26547")
    private Long shipmentId;

    @Schema(description = "出运费用编号")
    private String shipmentCode;

    @Schema(description = "主体主键", example = "26547")
    private Long companyId;

    @Schema(description = "主体名称")
    private String companyName;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "供应商主键", example = "2865")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "费用名称主键", example = "26063")
    private Long dictSubjectId;

    @Schema(description = "费用名称", example = "芋艿")
    private String dictSubjectName;

    @Schema(description = "费用类型", example = "2")
    private Integer feeType;

    @Schema(description = "金额")
    private JsonAmount amount;

    @Schema(description = "付款状态", example = "2")
    private Integer payStatus;

    @Schema(description = "申请人")
    private UserDept applyer;

    @Schema(description = "申请人")
    private Long applyerId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}