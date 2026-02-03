package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import com.syj.eplus.module.sms.dal.dataobject.quotationitem.QuotationItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 报价单主新增/修改 Request VO")
@Data
public class QuotationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1381")
    private Long id;

    @Schema(description = "内部法人单位主键", example = "31865")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String companyName;

    @Schema(description = "客户主键", example = "29787")
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String custName;

    @Schema(description = "是否新客户", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isNewCust;

    @Schema(description = "价格条款", example = "2")
    private String settlementTermType;

    @Schema(description = "币种", example = "2")
    private String currency;

    @Schema(description = "客户联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long custPocId;

    @Schema(description = "客户联系人名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custPocName;

    @Schema(description = "国家id", example = "26126")
    private Long countryId;

    @Schema(description = "国家名称", example = "26126")
    private String countryName;

    @Schema(description = "出运口岸主键", example = "29636")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String departurePortName;

    @Schema(description = "有效期止")
    private LocalDateTime validPeriod;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept manager;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer auditStatus;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "报价单明细")
    private List<QuotationItemDO> children;

    @Schema(description = "其他费用")
    private List<OtherFeeDO> otherFeeList;


}