package com.syj.eplus.module.controller.admin.send.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 寄件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SendRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25593")
    private Long id;

    @Schema(description = "快递公司id", example = "7392")
    private Long venderId;

    @Schema(description = "快递公司编号", example = "7392")
    private String venderCode;

    @Schema(description = "快递公司", example = "王五")
    @ExcelProperty("快递")
    private String venderName;

    @Schema(description = "快递公司", example = "王五")
    private String venderShortName;

    @Schema(description = "物流单号")
    @ExcelProperty("面单号")
    private String expressCode;

    @Schema(description = "实际费用")
    @ExcelProperty(value = "价格",converter = AmountConvert.class)
    private JsonAmount cost;

    @Schema(description = "寄件时间")
    private LocalDateTime sendTime;

    @Schema(description = "录入人姓名", example = "张三")
    @ExcelProperty("经手人")
    private String inputUserName;

    @Schema(description = "收件人姓名")
    @ExcelProperty("对方公司名称/收件人")
    private String receiveName;


    @Schema(description = "录入人id", example = "27702")
    private Long inputUserId;
    


    @Schema(description = "录入人部门id", example = "27702")
    private Long inputDeptId;

    @Schema(description = "录入人部门名称", example = "张三")
    private String inputDeptName;

    @Schema(description = "费用归集", example = "你说的对")
    @ExcelProperty("费用归集")
    private String feeShareDesc;
    @Schema(description = "编号")
    @ExcelProperty("订单号")
    private String code;

    @Schema(description = "寄件区域")
    private Integer sendRegion;
    
    @Schema(description = "物件类型", example = "1")
    @DictFormat(DictTypeConstants.SEND_PRODUCT_TYPE)
    private Integer goodsType;
    
    @Schema(description = "付款方式", example = "1")
    @DictFormat(DictTypeConstants.SEND_PAY_TYPE)
    private Integer payType;
    
    @Schema(description = "收件人信息", example = "王五")
    private String receiveMsg;


    
    @Schema(description = "预估费用")
    private JsonAmount estCost;
    
    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "月份")
    @ExcelProperty("月份")
    private String sendMonth;

    @Schema(description = "寄件状态", example = "1")
    private Integer sendStatus;
    
    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "是否归属费用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer belongFlag;
    
    @Schema(description = "付款状态", example = "2")
    private Integer payStatus;
    
    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "费用写入时间")
    private LocalDateTime costTime;
    
    @Schema(description = "完成时间")
    private LocalDateTime doneTime;
    
    @Schema(description = "付款时间")
    private LocalDateTime payTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "收件人类型")
    private Integer receiveType;

    @Schema(description = "收件人编号")
    private String receiveCode;

    @Schema(description = "处理人姓名")
    private String dealUserName;
    @CompareField(value = "实际寄件人")
    private UserDept actualUser;
}