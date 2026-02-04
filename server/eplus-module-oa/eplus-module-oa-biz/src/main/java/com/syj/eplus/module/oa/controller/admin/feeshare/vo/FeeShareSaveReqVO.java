package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 费用归集新增/修改 Request VO")
@Data
public class FeeShareSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16564")
    private Long id;

    @Schema(description = "来源类型", example = "2")
    private Integer businessType;

    @Schema(description = "来源单编号")
    private String businessCode;

    @Schema(description = "订单类型")
    private Integer orderType;

    @Schema(description = "业务部门id", example = "26115")
    private Long deptId;

    @Schema(description = "业务部门名称", example = "李四")
    private String deptName;

    @Schema(description = "费用归属类别", example = "1")
    private Integer feeShareType;

    @Schema(description = "相关方类别", example = "2")
    private Integer relationType;

    @Schema(description = "具体名称id", example = "26364")
    private Long descId;

    @Schema(description = "具体名称", example = "芋艿")
    private String descName;

    @Schema(description = "流程实例编号", example = "芋艿")
    private String processInstanceId;

    private Integer submitFlag;

    @Schema(description = "支付状态", example = "芋艿")
    private Integer paymentStatus;

    @Schema(description = "主体主键", example = "芋艿")
    private Long companyId;

    @Schema(description = "主体名称", example = "芋艿")
    private String companyName;

    @Schema(description = "来源单据状态", example = "芋艿")
    private Integer sourceStatus;

    @Schema(description = "来源单据主键")
    private Long businessId;

    @Schema(description = "金额")
    private JsonAmount amount;

    @Schema(description = "带币种金额")
    private List<JsonAmount> totalAmountList;
    private List<FeeShareItemDTO> crmChildren;
    private List<FeeShareItemDTO> venderChildren;

    private List<FeeShareItemDTO> smsChildren;
    private List<FeeShareItemDTO> purchaseChildren;
    private List<FeeShareItemDTO> userChildren;

    private UserDept shareUser;
    private Long exhibitionId;
    private Long projectId;
    private Integer preCollectionFlag;
}