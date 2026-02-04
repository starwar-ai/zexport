package cn.iocoder.yudao.module.system.dal.dataobject.userprefence;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.handle.SetPreferenceObjListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.entity.SetPreferenceObj;
import lombok.*;

import java.util.List;

/**
 * 用户偏好设置 DO
 *
 * @author zhangcm
 */

@TableName(value = "system_user_prefence",autoResultMap = true)
@KeySequence("system_user_prefence_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrefenceDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户编号
     */
    private String userCode;
    /**
     * 页面类型
     */
    private String pageKey;
    /**
     * 页面tab
     */
    private Integer tabIndex;
    /**
     * 主表配置
     */
    @TableField(typeHandler = SetPreferenceObjListTypeHandler.class)
    private List<SetPreferenceObj>  parent;
    /**
     * 子表配置
     */
    @TableField(typeHandler = SetPreferenceObjListTypeHandler.class)
    private List<SetPreferenceObj> children;

}