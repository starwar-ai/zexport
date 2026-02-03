package com.syj.eplus.module.oa.controller.admin.entertain.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntertainReimbSaveReqVO extends ReimbSaveReqVO {
    @Schema(description = "餐饮费")
    private List<JsonReimbDetail> mealExpenseList;

    @Schema(description = "礼品费")
    private List<JsonReimbDetail> giftExpenseList;

    @Schema(description = "其他费用")
    private List<JsonReimbDetail> otherExpenseList;

    @Schema(description = "招待对象类型")
    private Integer entertainType;

    @Schema(description = "招待对象名称")
    private String entertainName;

    @Schema(description = "招待对象等级")
    private Integer entertainLevel;

    @Schema(description = "招待人数")
    private Integer entertainNum;

    @Schema(description = "招待陪同人员")
    private List<UserDept> entertainEntourage;

    @Schema(description = "招待时间")
    private LocalDateTime entertainTime;

    @Schema(description = "发票")
    private List<SimpleFile> invoiceList;

    @Schema(description = "借款单列表")
    private List<SimpleLoanAppRespDTO> loanAppList;

    private Integer submitFlag;
}
