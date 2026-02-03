package com.syj.eplus.module.wms.controller.admin.bill.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-入(出)库单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BillPageReqVO extends PageParam {

    @Schema(description = "单号")
    private String code;

    @Schema(description = "入/出库类型 1-入库单、2-出库单", example = "2")
    private Integer billType;

    @Schema(description = "单据状态1-未确认 2-已确认 3-作废", example = "1")
    private Integer billStatus;

    @Schema(description = "入/出库通知单号")
    private String noticeCode;

    @Schema(description = "采购合同主键", example = "5957")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "14862")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "入/出库时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] billTime;

    @Schema(description = "采购员主键", example = "23823")
    private Long purchaserId;

    @Schema(description = "采购员部门主键", example = "15257")
    private Long purchaserDeptId;

    @Schema(description = "销售人员主键", example = "5333")
    private Long salesId;

    @Schema(description = "跟单员主键", example = "5333")
    private Long managerId;

    @Schema(description = "销售员部门主键", example = "19929")
    private Long salesDeptId;

    @Schema(description = "仓库主键", example = "23417")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "25234")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "产品编号", example = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "id数组")
    private Long[] idList;

    @Schema(description = "供应商名称")
    private String venderName;
}
