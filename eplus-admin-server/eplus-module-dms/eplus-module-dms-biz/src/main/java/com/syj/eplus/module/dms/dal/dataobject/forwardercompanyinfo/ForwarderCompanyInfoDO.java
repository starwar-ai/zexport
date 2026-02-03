package com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 船代公司 DO
 *
 * @author du
 */

@TableName(value = "dms_forwarder_company_info",autoResultMap = true)
@KeySequence("dms_forwarder_company_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwarderCompanyInfoDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhoneNumber;
    /**
     * 状态
     *
     * 枚举 {@link TODO is_int 对应的类}
     */
    private Integer status;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;

    /**
     * 归属公司主键
     */
    private Long companyId;

    /**
     * 归属公司名称
     */
    private String companyName;

    /**
     * 备注
     **/
    private String remark;
}