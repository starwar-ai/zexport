package cn.iocoder.yudao.module.system.api.logger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 16:40
 */
@Data
public class OperateLogRespDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", required = true)
    private Long id;

    /**
     * 链路追踪编号，用于跟踪一次请求的完整调用链路
     */
    @Schema(description = "链路追踪编号", example = "123e4567-e89b-12d3-a456-426614174000")
    private String traceId;

    /**
     * 用户编号
     */
    @Schema(description = "用户编号", required = true, example = "1")
    private Long userId;

    /**
     * 用户名称
     */
    private String userNickName;
    /**
     * 用户类型
     */
    @Schema(description = "用户类型", required = true, example = "1")
    private Integer userType;

    /**
     * 操作模块名称
     */
    @Schema(description = "操作模块", required = true, example = "用户管理")
    private String module;

    /**
     * 操作名称
     */
    @Schema(description = "操作名", required = true, example = "创建用户")
    private String name;

    /**
     * 操作分类编码
     */
    @Schema(description = "操作分类", required = true, example = "1")
    private String type;

    /**
     * 操作详细描述
     */
    @Schema(description = "操作明细")
    private String content;

    /**
     * 拓展字段，存储额外信息
     */
    @Schema(description = "拓展字段")
    private Map<String, Object> exts;

    /**
     * HTTP请求方法名，如GET、POST等
     */
    @Schema(description = "请求方法名", required = true, example = "POST")
    private String requestMethod;

    /**
     * HTTP请求URL地址
     */
    @Schema(description = "请求地址", required = true, example = "/api/users")
    private String requestUrl;

    /**
     * 发起请求的用户IP地址
     */
    @Schema(description = "用户 IP", required = true, example = "192.168.1.1")
    private String userIp;

    /**
     * 客户端浏览器User-Agent信息
     */
    @Schema(description = "浏览器 UserAgent", required = true, example = "Mozilla/5.0 ...")
    private String userAgent;

    /**
     * 被调用的Java方法全名
     */
    @Schema(description = "Java 方法名", required = true, example = "com.example.service.UserService.createUser")
    private String javaMethod;

    /**
     * 调用Java方法时传递的参数
     */
    @Schema(description = "Java 方法的参数")
    private String javaMethodArgs;

    /**
     * 操作开始时间
     */
    @Schema(description = "开始时间", required = true, example = "2023-04-01T12:00:00")
    private LocalDateTime startTime;

    /**
     * 操作执行消耗的时长（毫秒）
     */
    @Schema(description = "执行时长（毫秒）", required = true, example = "500")
    private Integer duration;

    /**
     * 操作结果码，通常用于表示操作成功或失败的状态
     */
    @Schema(description = "结果码", required = true, example = "0")
    private Integer resultCode;

    /**
     * 操作结果的简短提示信息
     */
    @Schema(description = "结果提示")
    private String resultMsg;

    /**
     * 操作返回的结果数据，格式依据具体操作而定
     */
    @Schema(description = "结果数据")
    private String resultData;
}
