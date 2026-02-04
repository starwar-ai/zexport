package com.syj.eplus.module.scm.controller.admin.vender.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 供应商信息新增/修改 Request VO")
@Data
public class VenderSimpleReqVO extends PageParam {

    @Schema(description = "采购员id")
    private Long buyerId;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "供应商类型")
    private Integer venderType;

    @Schema(description = "行政供应商类型")
    private Integer administrationVenderType;

}