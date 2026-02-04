package com.syj.eplus.module.infra.controller.admin.formchange.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 表单字段变更管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FormChangePageReqVO extends PageParam {

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "表单名称", example = "李四")
    private String name;

    @Schema(description = "表单描述", example = "李四")
    private String description;

    @Schema(description = "流程模型标识", example = "8511")
    private String modelKey;

    @Schema(description = "流程是否开启")
    private Integer instanceFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}