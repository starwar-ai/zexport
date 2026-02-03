package com.syj.eplus.module.oa.controller.admin.dictsubject.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 类别配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DictSubjectPageReqVO extends PageParam {

    @Schema(description = "科目id", example = "9323")
    private Long subjectId;

    @Schema(description = "科目名称", example = "芋艿")
    private String subjectName;

    @Schema(description = "科目描述", example = "随便")
    private String subjectDescription;

    @Schema(description = "字典值类型")
    private List<String> systemDictTypeList;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "费用名称")
    private String feeName;

    @Schema(description = "费用描述")
    private String feeDesc;

}