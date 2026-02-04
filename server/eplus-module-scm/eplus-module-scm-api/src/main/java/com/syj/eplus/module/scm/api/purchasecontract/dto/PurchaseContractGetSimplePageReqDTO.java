package com.syj.eplus.module.scm.api.purchasecontract.dto;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseContractGetSimplePageReqDTO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "供应商编号列表")
    private List<String> venderCodeList;

    @Schema(description = "客户编号列表")
    private List<String> custCodeList;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "采购主体id")
    private Long companyId;

    @Schema(description = "辅料标记")
    private Integer auxiliaryFlag;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "采购员编码", example = "27774")
    private Long purchaseUserId;

    @Schema(description = "采购员部门编码", example = "4724")
    private Long purchaseUserDeptId;

    private String excludePurchaseContract;

    private String purchaseContractCode;

}
