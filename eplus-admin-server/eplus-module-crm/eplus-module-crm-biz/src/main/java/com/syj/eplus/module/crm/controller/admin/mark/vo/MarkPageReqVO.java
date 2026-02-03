package com.syj.eplus.module.crm.controller.admin.mark.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 唛头分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MarkPageReqVO extends PageParam {

    @Schema(description = "唛头名称", example = "王五")
    private String name;

    @Schema(description = "版本号")
    private Integer ver;

    @Schema(description = "唛头英文名称", example = "王五")
    private String engName;

    @Schema(description = "主文字唛")
    private String mainMarkText;

    @Schema(description = "主图形唛")
    private String mainMarkPic;

    @Schema(description = "主侧文字唛")
    private String mainMarkTextSide;

    @Schema(description = "主侧图形唛")
    private String mainMarkPicSide;

    @Schema(description = "内主文字唛")
    private String mainMarkTextIn;

    @Schema(description = "内主图形唛")
    private String mainMarkPicIn;

    @Schema(description = "内侧文字唛")
    private String mainMarkTextSideIn;

    @Schema(description = "内侧图形唛")
    private String mainMarkPicSideIn;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}