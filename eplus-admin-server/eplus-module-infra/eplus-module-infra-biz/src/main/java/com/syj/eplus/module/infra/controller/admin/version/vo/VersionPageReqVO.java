package com.syj.eplus.module.infra.controller.admin.version.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 版本记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VersionPageReqVO extends PageParam {

    @Schema(description = "前端版本")
    private String frontVer;

    @Schema(description = "后端版本")
    private String serverVer;

    @Schema(description = "发布版本")
    private String publishVer;

    @Schema(description = "发布更新明细", example = "芋艿")
    private String publishName;

    @Schema(description = "前端更新明细")
    private String frontDesc;

    @Schema(description = "前端更新明细")
    private String serverDesc;

    @Schema(description = "发布更新明细")
    private String publishDesc;

    @Schema(description = "是否显示")
    private Integer enabled;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}