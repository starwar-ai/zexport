package com.syj.eplus.module.qms.dal.dataobject.qualityinspection;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 验货单 DO
 *
 * @author ePlus
 */

@TableName(value = "qms_quality_inspection",autoResultMap = true)
@KeySequence("qms_quality_inspection_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 单号
     */
    private String code;
    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案
     */
    private Integer qualityInspectionStatus;
    /**
     * 是否重验单:0：否，1：是
     */
    private Integer reinspectionFlag;
    /**
     * 关联验货单id
     */
    private Long sourceId;
    /**
     * 关联验货单单号
     */
    private String sourceCode;


    /**
     * 单据来源:1：采购合同
     */
    private Integer sourceType;
    /**
     * 采购合同主键
     */
    private Long purchaseContractId;
    /**
     * 采购合同号
     */
    private String purchaseContractCode;

    /**
     * 验货仓库主键
     */
    private Long warehouseId;

    /**
     * 验货仓库名称
     */
    private String warehouseName;

    /**
     * 验货方式: 1： 泛太陪验（工厂）
                2：泛太陪验（公司内）
                3：泛太自验（工厂）
                4：泛太自验（公司内）
                5：客户自检
                6：客户指定第三方
                7：远程验货
     */
    private Integer inspectionType;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 申请验货人
     */
    private Long applyInspectorId;
    /**
     * 申请验货人姓名
     */
    private String applyInspectorName;
    /**
     * 申请验货人部门主键
     */
    private Long applyInspectorDeptId;
    /**
     * 申请验货人部门名称
     */
    private String applyInspectorDeptName;
    /**
     * 工厂联系人
     */
    private String factoryContacter;
    /**
     * 联系电话
     */
    private String factoryTelephone;
    /**
     * 验货地址
     */
    private String inspectionAddress;
    /**
     * 验货人
     */
    private Long inspectorId;
    /**
     * 验货人姓名
     */
    private String inspectorName;
    /**
     * 验货人部门主键
     */
    private Long inspectorDeptId;
    /**
     * 验货人部门名称
     */
    private String inspectorDeptName;
    /**
     * 期望验货时间
     */
    private LocalDateTime expectInspectionTime;
    /**
     * 计划验货时间
     */
    private LocalDateTime planInspectionTime;
    /**
     * 实际验货时间
     */
    private LocalDateTime inspectionTime;
    /**
     * 特别注意事项
     */
    private String specialAttentionNotice;
    /**
     * 验货费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 验货费用分摊方式：1-按金额分摊 2-按数量分摊
     */
    private Integer allocationType;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 结果附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> resultAnnex;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;
    /**
     * 工厂保函
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> guaranteeLetter;
    /**
     * 接受说明
     */
    private String acceptDesc;
    /**
     * 备注
     */
    private String remark;

    /**
     * 归属公司主键
     */
    private Long companyId;

    /**
     * 归属公司名称
     */
    private String companyName;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 验货节点
     */
    private Integer inspectionNode;

    /**
     * 采购员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept purchaseUser;

    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;
}
