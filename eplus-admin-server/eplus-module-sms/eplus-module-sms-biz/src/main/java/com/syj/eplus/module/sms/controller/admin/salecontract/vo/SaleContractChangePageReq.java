package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

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
 * @Date：2024/7/1 17:27
 */
@Schema(description = "管理后台 - 采购合同变更分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SaleContractChangePageReq extends PageParam {
    @Schema(description = "变更单编号")
    private String code;

    @Schema(description = "外销合同编号")
    private String contractCode;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer auditStatus;

    private String skuName;

    private String custName;

    private Integer saleType;
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

    @Schema(description = "销售合同号")
    private String sourceCode;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "下单主体")
    private String companyName;

    @Schema(description = "下单主体ID")
    private Long companyId;

    @Schema(description = "业务员ID")
    private Long salesUserId;

    @Schema(description = "业务员姓名")
    private String salesUserName;

    @Schema(description = "部门ID")
    private Long salesDeptId;

    @Schema(description = "部门名称")
    private String salesDeptName;
}

