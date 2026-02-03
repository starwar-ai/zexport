package com.syj.eplus.module.scm.controller.admin.purchaseplan.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购计划分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchasePlanPageReqVO extends PageParam {

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "编号列表")
    private List<String> codeList;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "计划状态", example = "2")
    private Integer planStatus;


    @Schema(description = "客户id", example = "14265")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "来源单类型", example = "2")
    private Integer sourceType;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


    @Schema(description = "计划日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] planDate;

    @Schema(description = "预计交期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] estDeliveryDate;

    @Schema(description = "采购主体")
    private Long companyId;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "是否包含删除数据")
    private  boolean isIncludeDeleted =false;

    @Schema(description = "是否辅料采购")
    private Integer auxiliaryFlag;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "自动生成标记")
    private Integer autoFlag;

    @Schema(description = "状态")
    private Integer neStatus;

    @Schema(description = "id数组")
    private List<Long> idList;

    private String creator;

    private String skuName;

    private String venderCode;

    private Long creatorDeptId;

    /**
     * 辅料属于的销售合同
     */
    private String auxiliarySaleContractCode;

    /**
     * 辅料属于的采购合同
     */
    private String auxiliaryPurchaseContractCode;
}