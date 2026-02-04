package com.syj.eplus.module.controller.admin.send.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 寄件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SendPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "录入人id", example = "27702")
    private Long inputUserId;

    @Schema(description = "录入人姓名", example = "张三")
    private String inputUserName;

    @Schema(description = "寄件区域")
    private Integer sendRegion;

    @Schema(description = "物件类型", example = "1")
    private Integer goodsType;

    @Schema(description = "付款方式", example = "1")
    private Integer payType;

    @Schema(description = "收件人信息", example = "王五")
    private String receiveMsg;


    @Schema(description = "快递公司编号", example = "7392")
    private String venderCode;

    @Schema(description = "快递公司名称", example = "王五")
    private String venderName;

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

    @Schema(description = "是否归属费用")
    private Integer belongFlag;

    @Schema(description = "付款状态", example = "2")
    private Integer payStatus;

    @Schema(description = "提交时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] submitTime;

    @Schema(description = "寄件时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] sendTime;

    @Schema(description = "费用写入时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] costTime;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] doneTime;

    @Schema(description = "付款时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] payTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "收件人编号")
    private String receiveCode;
    @Schema(description = "收件人姓名")
    private String receiveName;

    @Schema(description = "实际寄件人")
    private Long actualUserId;

    @Schema(description = "不包含的状态")
    private List<Integer> excludeStatus;
}