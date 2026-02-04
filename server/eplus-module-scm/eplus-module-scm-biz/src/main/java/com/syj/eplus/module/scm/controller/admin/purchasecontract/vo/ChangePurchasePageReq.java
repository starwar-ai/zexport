package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/23 16:02
 */
@Schema(description = "管理后台 - 采购合同变更分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChangePurchasePageReq extends PageParam {
    @Schema(description = "变更单编号")
    private String code;

    @Schema(description = "采购合同编号")
    private String contractCode;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer auditStatus;

    private String skuName;

    private String venderName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    private String creator;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "产品编号")
    private String skuCode;
    
    @Schema(description = "包材标记")
    private Integer auxiliaryFlag;
    
    @Schema(description = "包材产品编号")
    private String auxiliarySkuCode;
    
    @Schema(description = "采购员ID")
    private Long purchaseUserId;
    
    @Schema(description = "采购员姓名")
    private String purchaseUserName;
    
    @Schema(description = "部门ID")
    private Long purchaseDeptId;
    
    @Schema(description = "部门名称")
    private String purchaseDeptName;
    
    @Schema(description = "销售合同号")
    private String saleContractCode;
    
    @Schema(description = "来源合同号")
    private String sourceCode;

    @Schema(description = "关联销售合同号")
    private String auxiliarySaleContractCode;

    @Schema(description = "关联采购合同号")
    private String auxiliaryPurchaseContractCode;
    
}
