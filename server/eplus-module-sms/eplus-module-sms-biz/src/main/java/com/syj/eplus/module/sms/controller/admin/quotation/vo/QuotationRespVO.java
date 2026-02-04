package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import com.syj.eplus.module.sms.dal.dataobject.quotationitem.QuotationItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 报价单主 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QuotationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1381")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "内部法人单位主键", example = "31865")
    @ExcelProperty("内部法人单位主键")
    private Long companyId;
    
    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("内部法人单位名称")
    private String companyName;
    
    @Schema(description = "客户主键", example = "29787")
    @ExcelProperty("客户主键")
    private Long custId;
    
    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    private String custCode;
    
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("客户名称")
    private String custName;
    
    @Schema(description = "是否新客户", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否新客户")
    private Integer isNewCust;
    
    @Schema(description = "价格条款", example = "2")
    @ExcelProperty("价格条款")
    private String settlementTermType;

    @Schema(description = "币种", example = "2")
    private String currency;
    
    @Schema(description = "客户联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户联系人")
    private Long custPocId;

    @Schema(description = "客户联系人名称", example = "26126")
    @ExcelProperty("客户联系人名称")
    private String custPocName;
    
    @Schema(description = "国家id", example = "26126")
    @ExcelProperty("国家id")
    private Long countryId;

    @Schema(description = "国家名称", example = "26126")
    @ExcelProperty("国家名称")
    private String countryName;
    
    @Schema(description = "出运口岸主键", example = "29636")
    @ExcelProperty("出运口岸主键")
    private Long departurePortId;
    
    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("出运口岸名称")
    private String departurePortName;
    
    @Schema(description = "有效期止")
    @ExcelProperty("有效期止")
    private LocalDateTime validPeriod;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private UserDept manager;
    
    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "报价单明细")
    private List<QuotationItemDO> children;

    @Schema(description = "其他费用")
    private List<OtherFeeDO> otherFeeList;

    @Schema(description = "流程id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String processInstanceId;

    @Schema(description = "报价单号")
    private String code;

    @Schema(description = "创建人")
    private UserDept createUser;
}