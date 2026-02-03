package cn.iocoder.yudao.module.bpm.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 20:06
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AuditInfo {
    @Schema(description = "任务id", example = "1")
    private String taskId;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "录入人", example = "")
    private String createUser;

    @Schema(description = "录入人部门", example = "")
    private String createUserDept;

    @Schema(description = "录入时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "审核人", example = "1")
    private List<String> auditUserList;

    @Schema(description = "录入时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime auditTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;
}
