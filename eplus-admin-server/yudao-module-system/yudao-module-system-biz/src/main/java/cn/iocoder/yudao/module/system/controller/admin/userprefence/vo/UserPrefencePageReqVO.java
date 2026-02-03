package cn.iocoder.yudao.module.system.controller.admin.userprefence.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SetPreferenceObj;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户偏好设置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserPrefencePageReqVO extends PageParam {

    @Schema(description = "用户id", example = "23952")
    private Long userId;

    @Schema(description = "用户编号")
    private String userCode;

    @Schema(description = "页面类型")
    private String pageKey;

    @Schema(description = "页面tab")
    private Integer tabIndex;

    @Schema(description = "主表配置")
    private List<SetPreferenceObj> parent;

    @Schema(description = "子表配置")
    private List<SetPreferenceObj> children;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}