package com.syj.eplus.module.infra.controller.admin.sn.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 序列号记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SnPageReqVO extends PageParam {

    @Schema(description = "类型", example = "2")
    private String type;

    @Schema(description = "序列号")
    private Integer sn;

    @Schema(description = "编号前缀")
    private String codePrefix;
    @Schema(description = "长度")
    private Integer length;


}