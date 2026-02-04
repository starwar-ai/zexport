package com.syj.eplus.module.dpms.controller.admin.reportrole.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/26/17:38
 * @Description:
 */
@Data
@Schema(description = "报表权限请求体")
public class PermissionAssignRoleReportReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "卡片编号列表", example = "1,3,5")
    private Set<Long> reportIds = Collections.emptySet(); // 兜底
}
