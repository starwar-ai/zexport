package com.syj.eplus.module.oa.dal.dataobject.feeshare;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareDescItemDTO;
import lombok.*;

import java.util.List;

/**
 * 费用归集 DO
 *
 * @author zhangcm
 */

@TableName(value = "oa_fee_share",autoResultMap = true)
@KeySequence("oa_fee_share_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeShareDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 来源类型
     */
    private Integer businessType;
    /**
     * 来源单编号
     */
    private String businessCode;
    /**
     * 业务部门id
     */
    private Long deptId;
    /**
     * 业务部门名称
     */
    private String deptName;
    /**
     * 费用归属类别
     */
    private Integer feeShareType;
    /**
     * 相关方类别
     */
    private Integer relationType;
    /**
     * 具体名称id
     */
    private Long descId;
    /**
     * 具体名称
     */
    private String descName;
    /**
     * 状态
     */
    private Integer auditStatus;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 编号
     */
    private String code;

    /**
     * 来源单id
     */
    private Long businessId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 支付状态
     */
    private Integer paymentStatus;
    /**
     * 主体主键
     */
    private Long companyId;
    /**
     * 主体名称
     */
    private String companyName;
    /**
     * 来源单据状态
     */
    private Integer sourceStatus;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;
    /**
     * 金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 归属用户
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept shareUser;

    /**
     * 预归集标记
     */
    private Integer  preCollectionFlag;

    @TableField(exist = false)
    private String feeShareDetail;

    @TableField(exist = false)
    private List<FeeShareDescItemDTO> detailList;

    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 展会id
     */
    private Long exhibitionId;
    /**
     * 品牌名称
     */
    private Integer brandType;
}