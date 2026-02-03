package cn.iocoder.yudao.module.system.controller.admin.auth.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserMobileVO {
    @Schema(description = "编号")
    private Long userId;

    @Schema(description = "姓名")
    @ExcelProperty("姓名")
    private String nickname;

    @Schema(description = "部门名称")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "手机号码")
    @ExcelProperty("手机号码")
    private String mobile;

    @Schema(description = "失败信息")
    @ExcelProperty("失败信息")
    private String failMessage;

    @Schema(description = "社交用户id")
    private String socialUserId;
}
