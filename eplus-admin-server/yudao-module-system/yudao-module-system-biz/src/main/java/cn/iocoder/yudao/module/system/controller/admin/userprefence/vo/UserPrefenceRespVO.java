package cn.iocoder.yudao.module.system.controller.admin.userprefence.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SetPreferenceObj;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 用户偏好设置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserPrefenceRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7386")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "用户id", example = "23952")
    @ExcelProperty("用户id")
    private Long userId;
    
    @Schema(description = "用户编号")
    @ExcelProperty("用户编号")
    private String userCode;
    
    @Schema(description = "页面类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("页面类型")
    private String pageKey;
    
    @Schema(description = "页面tab", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("页面tab")
    private Integer tabIndex;
    
    @Schema(description = "主表配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主表配置")
    private List<SetPreferenceObj> parent;
    
    @Schema(description = "子表配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("子表配置")
    private List<SetPreferenceObj> children;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}