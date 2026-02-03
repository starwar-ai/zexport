package com.syj.eplus.module.pms.controller.admin.category.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 产品分类分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CategoryPageReqVO extends PageParam {

    @Schema(description = "商品分类编号")
    private String code;

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "流水号长度")
    private Integer codeLen;

    @Schema(description = "海关编码")
    private Long hsCodeId;

    @Schema(description = "父节点编号", example = "18523")
    private Long parentId;

    @Schema(description = "种类", example = "0")
    private Integer categoryType;

    @Schema(description = "级别", example = "1")
    private Integer grade;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "海关编码编号")
    private String hsDataCode;
}