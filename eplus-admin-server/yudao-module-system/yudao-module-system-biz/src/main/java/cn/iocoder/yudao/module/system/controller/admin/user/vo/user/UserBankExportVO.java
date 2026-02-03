package cn.iocoder.yudao.module.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/28 18:34
 */
@Schema(description = "管理后台 - 用户银行导入 Response VO")
@Data
@Builder
public class UserBankExportVO {

    private String mobile;

    private String nickname;
    /**
     * 失败信息
     */
    private String failMessage;
}
