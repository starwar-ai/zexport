package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 验货单新增/修改 Request VO")
@Data
public class QualityInspectionSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "归属公司主键", example = "5879")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "供应商id", example = "11989")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "关联验货单id")
    private Long sourceId;

    @Schema(description = "关联验货单单号")
    private String sourceCode;

    @Schema(description = "是否重验单:0：否，1：是")
    private Integer reinspectionFlag;

    @Schema(description = "期望验货时间")
    private LocalDateTime expectInspectionTime;

    @Schema(description = "计划验货时间")
    private LocalDateTime planInspectionTime;

    @Schema(description = "验货时间")
    private LocalDateTime inspectionTime;

    @Schema(description = "验货人")
    private Long inspectorId;

    @Schema(description = "验货人姓名")
    private String inspectorName;

    @Schema(description = "验货人部门主键")
    private Long inspectorDeptId;

    @Schema(description = "验货人部门名称")
    private String inspectorDeptName;

    @Schema(description = "验货方式:1： 泛太陪验（工厂） " +
            "2：泛太陪验（公司内） \n" +
            "3：泛太自验（工厂） \n" +
            "4：泛太自验（公司内） \n" +
            "5：客户自检 \n" +
            "6：客户指定第三方 \n" +
            "7：远程验货", example = "1")
    private Integer inspectionType;

    @Schema(description = "验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案", example = "1")
    @ExcelProperty("验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案")
    private Integer qualityInspectionStatus;

    @Schema(description = "验货仓库主键")
    private Long warehouseId;

    @Schema(description = "验货仓库名称")
    private String warehouseName;

    @Schema(description = "工厂联系人")
    private String factoryContacter;

    @Schema(description = "联系电话")
    private String factoryTelephone;

    @Schema(description = "验货地址")
    private String inspectionAddress;

    @Schema(description = "验货费用")
    @ExcelProperty("验货费用")
    private JsonAmount amount;

    @Schema(description = "验货费用分摊方式：1-按金额分摊 2-按数量分摊")
    private Integer allocationType;

    @Schema(description = "特别注意事项")
    private String specialAttentionNotice;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "图片")
    private List<SimpleFile> picture;

    @Schema(description = "是否返工:0：否，1：是")
    private Integer reworkFlag;

    @Schema(description = "返工证据")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> reworkPicture;

    @Schema(description = "结果附件")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> resultAnnex;


    @Schema(description = "返工说明")
    private String reworkDesc;

    @Schema(description = "重验时间")
    private LocalDateTime reworkInspectionTime;

    @Schema(description = "工厂保函")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> guaranteeLetter;

    @Schema(description = "接受说明")
    private String acceptDesc;

    @Schema(description = "验货单-明细列表")
    private List<QualityInspectionItemSaveReqVO> itemSaveReqVOList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "验货节点")
    private Integer inspectionNode;

    @Schema(description = "业务员")
    private UserDept sales;

    @Schema(description = "采购员")
    private Long purchaseUserId;
}
