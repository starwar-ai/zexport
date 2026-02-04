package com.syj.eplus.module.pms.controller.admin.packagetype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 包装方式分页 Request VO")
@Data
@ToString(callSuper = true)
public class PackageTypeSimplePageReqVO {



    @Schema(description = "包装方式名称", example = "赵六")
    private String name;


}