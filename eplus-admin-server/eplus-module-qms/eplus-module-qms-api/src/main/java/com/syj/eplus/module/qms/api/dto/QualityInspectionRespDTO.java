package com.syj.eplus.module.qms.api.dto;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QualityInspectionRespDTO {

    private Long id;

    //单号
    private String code;

    //审核状态
    private Integer auditStatus;

    //验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案
    private Integer qualityInspectionStatus;

    //是否重验单:0：否，1：是
    private Integer reinspectionFlag;

    //关联验货单id
    private Long sourceId;

    //关联验货单单号
    private String sourceCode;

    //是否返工:0：否，1：是
    private Integer reworkFlag;

    //返工证据
    private List<SimpleFile> reworkPicture;

    //返工说明
    private String reworkDesc;

    //单据来源:1：采购合同
    private Integer sourceType;

    //采购合同主键
    private Long purchaseContractId;

    //采购合同号
    private String purchaseContractCode;

    //验货仓库主键
    private Long warehouseId;

    //验货仓库名称
    private String warehouseName;

    //验货方式:1： 泛太陪验（工厂） " +
    // "2：泛太陪验（公司内） \n" +
    // "3：泛太自验（工厂） \n" +
    // "4：泛太自验（公司内） \n" +
    // "5：客户自检 \n" +
    // "6：客户指定第三方 \n" +
    // "7：远程验货"
    private Integer inspectionType;

    //供应商id
    private Long venderId;

    //供应商编号
    private String venderCode;

    //供应商名称
    private String venderName;

    //申请验货人
    private Long applyInspectorId;

    //申请验货人姓名
    private String applyInspectorName;

    //申请验货人部门主键
    private Long applyInspectorDeptId;

    //申请验货人部门名称
    private String applyInspectorDeptName;

    //工厂联系人
    private String factoryContacter;

    //联系电话
    private String factoryTelephone;

    //验货地址
    private String inspectionAddress;

    //验货人
    private Long inspectorId;


    //验货人姓名
    private String inspectorName;

    //验货人部门主键
    private Long inspectorDeptId;

    //验货人部门名称
    private String inspectorDeptName;

    //期望验货时间
    private LocalDateTime expectInspectionTime;

    //计划验货时间
    private LocalDateTime planInspectionTime;

    //实际验货时间
    private LocalDateTime inspectionTime;

    //特别注意事项
    private String specialAttentionNotice;

    //验货费用
    private JsonAmount amount;

    //验货费用分摊方式：1-按金额分摊 2-按数量分摊
    private Integer allocationType;

    //附件
    private List<SimpleFile> annex;

    //工厂保函
//    private List<SimpleFile> guaranteeLetter;

    //接受说明
    private String acceptDesc;

    //备注
    private String remark;

    //创建时间
    private LocalDateTime createTime;

    //归属公司主键
    private Long companyId;

    //归属公司名称
    private String companyName;

    //任务id
    private String processInstanceId;

    //验货单-明细
    private List<QualityInspectionItemRespDTO> children;

    //操作日志
    private List<OperateLogRespDTO> operateLogRespDTOList;

    /**
     * 业务员
     */
    private UserDept sales;
}
