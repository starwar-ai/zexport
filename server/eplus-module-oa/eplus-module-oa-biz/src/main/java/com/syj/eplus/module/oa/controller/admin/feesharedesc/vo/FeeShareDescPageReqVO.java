package com.syj.eplus.module.oa.controller.admin.feesharedesc.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 费用归集具体名称分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FeeShareDescPageReqVO extends PageParam {



    @Schema(description = "费用归集类型", example = "1")
    private Integer feeShareType;

    @Schema(description = "相关方类别", example = "2")
    private Integer relationType;



}