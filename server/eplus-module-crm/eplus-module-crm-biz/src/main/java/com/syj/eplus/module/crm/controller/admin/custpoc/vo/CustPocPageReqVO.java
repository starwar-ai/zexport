package com.syj.eplus.module.crm.controller.admin.custpoc.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户联系人分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustPocPageReqVO extends PageParam {

    @Schema(description = "版本号")
    private Integer ver;

    @Schema(description = "客户id", example = "32037")
    private Integer custId;

    @Schema(description = "联系人姓名", example = "李四")
    private String name;

    @Schema(description = "联系人职位")
    private String pocPosts;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "住宅地址")
    private String address;

    @Schema(description = "默认联系人")
    private Integer defaultFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}