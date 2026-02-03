package com.syj.eplus.module.scm.controller.admin.vender.vo;


import com.syj.eplus.module.pms.api.Category.dto.SimpleCategoryDTO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountRespVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class VenderInfoRespVO extends VenderRespVO {


    @Schema(description = "供应商银行信息列表")
    private List<VenderBankaccountRespVO> bankaccountList;

    @Schema(description = "供应商联系人列表")
    private List<VenderPocRespVO> pocList;

    @Schema(description = "主营业务列表")
    private List<SimpleCategoryDTO> businessScopeList;

    @Schema(description = "任务id")
    private String processInstanceId;
}
