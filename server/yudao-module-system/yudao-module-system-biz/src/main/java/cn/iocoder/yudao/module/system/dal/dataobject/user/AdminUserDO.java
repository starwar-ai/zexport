package cn.iocoder.yudao.module.system.dal.dataobject.user;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.enums.common.SexEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonLongSetTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSetPreferenceListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonTimePreferenceListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonSetPreference;
import com.syj.eplus.framework.common.entity.JsonTimePreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 管理后台的用户 DO
 *
 * @author 芋道源码
 */
@TableName(value = "system_users", autoResultMap = true) // 由于 SQL Server 的 system_user 是关键字，所以使用 system_users
@KeySequence("system_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDO extends BaseDO {

    /**
     * 用户ID
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private String code;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 加密后的密码
     * <p>
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
    private String password;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 备注
     */
    private String remark;
    /**
     * 部门 ID
     */
    private Long deptId;
    /**
     * 岗位编号数组
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> postIds;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户性别
     * <p>
     * 枚举类 {@link SexEnum}
     */
    private Integer sex;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行地址
     */
    private String bankAddress;

    /**
     * 开户行联系人
     */
    private String bankPoc;

    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 帐号状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 社交用户id
     */
    private String socialUserId;

    /**
     * 设置偏好
     */
    @CompareField(value = "设置偏好")
    @TableField(typeHandler = JsonSetPreferenceListTypeHandler.class)
    private List<JsonSetPreference> preferences;

    /**
     * 时间偏好
     */
    @CompareField(value = "时间偏好")
    @TableField(typeHandler = JsonTimePreferenceListTypeHandler.class)
    private List<JsonTimePreference> timePreferences;

    @Schema(description = "座机", example = "15601691300")
    private String tel;

}
