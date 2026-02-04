package com.syj.eplus.module.crm.controller.admin.custpoc.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 客户联系人新增/修改 Request VO")
@Data
public class CustPocSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "联系人姓名不能为空")
    private String name;

    @Schema(description = "联系人职位")
    private String pocPosts;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "住宅地址")
    private String address;

    @Schema(description = "客户版本")
    private Integer ver;

    @Schema(description = "客户id")
    private Long custId;

    @Schema(description = "客户联系人名片")
    private List<SimpleFile> cardList;

    @Schema(description = "默认联系人")
    private Integer defaultFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "座机")
    private String telephone;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "QQ")
    private String qq;
}