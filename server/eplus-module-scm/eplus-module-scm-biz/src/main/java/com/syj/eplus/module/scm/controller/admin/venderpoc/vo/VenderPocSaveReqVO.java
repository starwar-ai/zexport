package com.syj.eplus.module.scm.controller.admin.venderpoc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 供应商联系人新增/修改 Request VO")
@Data
public class VenderPocSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本不能为空")
    private Integer ver;

    @Schema(description = "供应商id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10693")
    @NotNull(message = "供应商id不能为空")
    private Long venderId;

    @Schema(description = "供应商版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "供应商版本不能为空")
    private Integer venderVer;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "职位")
    private String pocTypes;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "是否默认联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认联系人不能为空")
    private Integer defaultFlag;

    @Schema(description = "座机")
    private String telephone;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "qq")
    private String qq;

    @Schema(description = "备注")
    private String remark;

}