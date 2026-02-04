package com.syj.eplus.module.oa.controller.admin.otherreimb.vo;

import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class OtherReimbSaveReqVO extends ReimbSaveReqVO {
    @Schema(description = "一般费用名称")
    private Integer generalExpense;

    @Schema(description = "实际操作人")
    private Integer userFlag;

    @Schema(description = "借款单列表")
    private List<SimpleLoanAppRespDTO> loanAppList;

    private Integer submitFlag;
}
