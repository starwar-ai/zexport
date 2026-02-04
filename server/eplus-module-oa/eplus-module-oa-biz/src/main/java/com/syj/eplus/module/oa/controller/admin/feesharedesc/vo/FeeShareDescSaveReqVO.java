package com.syj.eplus.module.oa.controller.admin.feesharedesc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 费用归集具体名称新增/修改 Request VO")
@Data
public class FeeShareDescSaveReqVO {



    @Schema(description = "费用归集类型", example = "1")
    private Integer feeShareType;

    @Schema(description = "相关方类别", example = "2")
    private Integer relationType;

    @Schema(description = "名称", example = "张三")
    private List<FeeShareDescItemSaveReqVO> children;

}