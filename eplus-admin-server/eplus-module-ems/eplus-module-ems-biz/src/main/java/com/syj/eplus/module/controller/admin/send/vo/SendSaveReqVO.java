package com.syj.eplus.module.controller.admin.send.vo;

import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class SendSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25593")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "录入人id", example = "27702")
    private Long inputUserId;

    @Schema(description = "录入人姓名", example = "张三")
    private String inputUserName;

    @Schema(description = "录入人部门id", example = "27702")
    private Long inputDeptId;

    @Schema(description = "录入人部门姓名", example = "张三")
    private String inputDeptName;

    @Schema(description = "寄件区域")
    private Integer sendRegion;

    @Schema(description = "物件类型", example = "1")
    private Integer goodsType;

    @Schema(description = "付款方式", example = "1")
    private Integer payType;

    @Schema(description = "收件人信息", example = "王五")
    private String receiveMsg;


    @Schema(description = "快递公司id", example = "7392")
    private Long venderId;

    @Schema(description = "快递公司编号", example = "7392")
    private String venderCode;

    @Schema(description = "快递公司名称", example = "王五")
    private String venderName;

    @Schema(description = "快递公司名称", example = "王五")
    private String venderShortName;

    @Schema(description = "预估费用")
    private JsonAmount estCost;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "寄件状态", example = "1")
    private Integer sendStatus;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "物流单号")
    private String expressCode;

    @Schema(description = "实际费用")
    private JsonAmount cost;

    @Schema(description = "是否归属费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否归属费用不能为空")
    private Integer belongFlag;

    @Schema(description = "付款状态", example = "2")
    private Integer payStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "寄件时间")
    private LocalDateTime sendTime;

    @Schema(description = "费用写入时间")
    private LocalDateTime costTime;

    @Schema(description = "完成时间")
    private LocalDateTime doneTime;

    @Schema(description = "付款时间")
    private LocalDateTime payTime;

    @Schema(description = "收件人类型")
    private Integer receiveType;

    @Schema(description = "收件人编号")
    private String receiveCode;

    @Schema(description = "收件人姓名")
    private String receiveName;

    @CompareField(value = "实际寄件人Id")
    private Long actualUserId;
}