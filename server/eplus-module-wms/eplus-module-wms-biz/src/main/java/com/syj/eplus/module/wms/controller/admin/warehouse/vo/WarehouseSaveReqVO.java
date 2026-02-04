package com.syj.eplus.module.wms.controller.admin.warehouse.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 仓库管理-仓库新增/修改 Request VO")
@Data
public class WarehouseSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28362")
    private Long id;

    @Schema(description = "仓库编码")
    private String code;

    @Schema(description = "仓库名称", example = "李四")
    private String name;

    @Schema(description = "仓库地址")
    private String address;

    @Schema(description = "启用标识  0-否 1-是")
    private Integer enableFlag;

    @Schema(description = "仓管主键", example = "17026")
    private List<Long> managerIds;

    @Schema(description = "供应仓标识0-否 1-是")
    private Integer venderFlag;

    @Schema(description = "供应商编码", example = "6393")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}
