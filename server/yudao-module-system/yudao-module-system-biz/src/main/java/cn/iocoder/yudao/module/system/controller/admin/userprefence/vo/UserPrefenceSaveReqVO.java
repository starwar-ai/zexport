package cn.iocoder.yudao.module.system.controller.admin.userprefence.vo;

import com.syj.eplus.framework.common.entity.SetPreferenceObj;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 用户偏好设置新增/修改 Request VO")
@Data
public class UserPrefenceSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7386")
    private Long id;

    @Schema(description = "用户id", example = "23952")
    private Long userId;

    @Schema(description = "用户编号")
    private String userCode;

    @Schema(description = "页面类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "页面类型不能为空")
    private String pageKey;

    @Schema(description = "页面tab", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "页面tab不能为空")
    private Integer tabIndex;

    @Schema(description = "主表配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主表配置不能为空")
    private List<SetPreferenceObj> parent;

    @Schema(description = "子表配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "子表配置不能为空")
    private List<SetPreferenceObj> children;

}