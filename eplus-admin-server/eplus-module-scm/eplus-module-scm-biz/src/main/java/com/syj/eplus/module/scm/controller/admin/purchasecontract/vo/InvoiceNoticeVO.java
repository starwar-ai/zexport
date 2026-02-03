package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceNoticeVO {

    @Schema(description = "付款主体id")
    private Long companyId;

    @Schema(description = "付款主体名称")
    private String companyName;

    @Schema(description = "供应商主键")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "跟单员id")
    private Long managerId;

    @Schema(description = "跟单员姓名")
    private String managerName;


    @Schema(description = "备注")
    private String remark;


    @Schema(description = "产品明细列表")
    private List<InvoiceNoticeItemVO> children;




    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept inputUser;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inputDate;

    @Schema(description = "采购单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purOrderCode;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;


    @Schema(description = "转换前数量")
    private Integer baseInvoiceQuantity;

}
