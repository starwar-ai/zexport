package com.syj.eplus.module.dal.dataobject.send;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 寄件 DO
 *
 * @author zhangcm
 */

@TableName(value = "ems_send",autoResultMap = true)
@KeySequence("ems_send_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendDO extends BaseDO {

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
     * 录入人id
     */
    private Long inputUserId;
    /**
     * 录入人姓名
     */
    private String inputUserName;
    /**
     * 录入人部门id
     */
    private Long inputDeptId;
    /**
     * 录入人部门姓名
     */
    private String inputDeptName;
    /**
     * 寄件区域
     */
    @CompareField(value = "寄件区域")
    private Integer sendRegion;
    /**
     * 物件类型
     */
    @CompareField(value = "物件类型")
    private Integer goodsType;
    /**
     * 付款方式
     */
    @CompareField(value = "付款方式")
    private Integer payType;
    /**
     * 收件人信息
     */
    @CompareField(value = "收件人信息")
    private String receiveMsg;

    /**
     * 快递公司id
     */
    @CompareField(value = "快递公司id")
    private Long venderId;
    /**
     * 快递公司编号
     */
    @CompareField(value = "快递公司编号")
    private String venderCode;
    /**
     * 快递公司名称
     */
    @CompareField(value = "快递公司名称")
    private String venderName;
    /**
     * 快递公司名称
     */
    @CompareField(value = "快递公司名称")
    private String venderShortName;
    /**
     * 预估费用
     */
    @CompareField(value = "预估费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount estCost;
    /**
     * 备注
     */
    @CompareField(value = "备注")
    private String remark;
    /**
     * 寄件状态
     */
    private Integer sendStatus;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 物流单号
     */
    @CompareField(value = "物流单号")
    private String expressCode;
    /**
     * 实际费用
     */
    @CompareField(value = "实际费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount cost;
    /**
     * 是否归属费用
     */
    @CompareField(value = "是否归属费用")
    private Integer belongFlag;
    /**b
     * 付款状态
     */
    @CompareField(value = "付款状态")
    private Integer payStatus;
    /**
     * 提交时间
     */
    @CompareField(value = "提交时间")
    private LocalDateTime submitTime;
    /**
     * 寄件时间
     */
    @CompareField(value = "寄件时间")
    private LocalDateTime sendTime;
    /**
     * 费用写入时间
     */
    @CompareField(value = "费用写入时间")
    private LocalDateTime costTime;
    /**
     * 完成时间
     */
    @CompareField(value = "完成时间")
    private LocalDateTime doneTime;
    /**
     * 付款时间
     */
    @CompareField(value = "付款时间")
    private LocalDateTime payTime;
    /**
     * 收件人类型
     */
    @CompareField(value = "收件人类型")
    private Integer receiveType;
    /**
     * 收件人编号
     */
    @CompareField(value = "收件人编号")
    private String receiveCode;
    /**
     * 收件人姓名
     */
    @CompareField(value = "收件人姓名")
    private String receiveName;

    /**
     * 处理人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "处理人")
    private UserDept dealUser;

    /**
     * 处理人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "实际寄件人")
    private UserDept actualUser;
}