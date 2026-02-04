package com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 船代公司分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ForwarderCompanyInfoPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", example = "赵六")
    private String name;

    @Schema(description = "联系人", example = "芋艿")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhoneNumber;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "录入日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inputDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称")
    private String companyName;
}