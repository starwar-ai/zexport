package com.syj.eplus.module.scm.api.purchaseplan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonCompanyPathTypeHandler;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PurchasePlanInfoSaveReqDTO {

    @Schema(description = "采购计划明细列表")
    public List<PurchasePlanItemSaveReqDTO> children;


    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27260")
    public Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer auditStatus;

    @Schema(description = "计划状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer planStatus;

    @Schema(description = "客户id", example = "14265")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "来源单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer sourceType;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "采购主体")
    private Long companyId;

    @Schema(description = "采购主体")
    private String companyName;

    @Schema(description = "预计交期")
    private LocalDateTime estDeliveryDate;

    @Schema(description = "计划日期")
    private LocalDateTime planDate;

    @Schema(description = "是否辅料采购")
    private Integer auxiliaryFlag;

    @Schema(description = "销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    /**
     * 订单路径
     */
    @CompareField(value = "订单路径")
    @TableField(typeHandler = JsonCompanyPathTypeHandler.class)
    private JsonCompanyPath companyPath;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;

    /**
     * 业务员
     */
    private UserDept sales;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "销售类型")
    private Integer saleType;
}
