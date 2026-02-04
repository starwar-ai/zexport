package com.syj.eplus.module.scm.controller.admin.venderpoc.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 供应商联系人分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VenderPocPageReqVO extends PageParam {

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "供应商id", example = "10693")
    private Long venderId;

    @Schema(description = "供应商版本")
    private Integer venderVer;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "职位")
    private String pocTypes;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "是否默认联系人")
    private Integer defaultFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "备注")
    private String remark;
}