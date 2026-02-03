package com.syj.eplus.module.oa.controller.admin.travelreimb.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class TravelReimbSaveReqVO extends ReimbSaveReqVO {

    @Schema(description = "交通费")
    private List<JsonReimbDetail> trafficFeeList;

    @Schema(description = "住宿费")
    private List<JsonReimbDetail> accommodationFeeList;

    @Schema(description = "自驾")
    private List<JsonReimbDetail> selfDrivingList;

    @Schema(description = "其他")
    private List<JsonReimbDetail> otherDescList;

    @Schema(description = "出差补贴")
    private List<JsonReimbDetail> travelAllowanceList;

    @Schema(description = "借款单列表")
    private List<SimpleLoanAppRespDTO> loanAppList;

    @Schema(description = "发票")
    private List<SimpleFile> invoiceList;

    private Integer submitFlag;

    @Schema(description = "票夹子id")
    private Long invoiceHolderId;

    @Schema(description = "备注")
    private String remark;
}
