package cn.iocoder.yudao.module.system.controller.admin.rolefield.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 字段角色关联新增/修改 Request VO")
@Data
public class RoleFieldSaveReqVO {

    @Schema(description = "字段id列表")
    private List<Long> fieldIds;

    @Schema(description = "角色id")
    private Long roleId;
}