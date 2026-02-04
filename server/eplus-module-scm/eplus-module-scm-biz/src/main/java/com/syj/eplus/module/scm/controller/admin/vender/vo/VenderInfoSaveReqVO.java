package com.syj.eplus.module.scm.controller.admin.vender.vo;


import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderpayment.VenderPayment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class VenderInfoSaveReqVO extends VenderSaveReqVO {

    @Schema(description = "供应商银行信息列表")
    private List<VenderBankaccountSaveReqVO> bankaccountList;

    @Schema(description = "供应商联系人列表")
    private List<VenderPocSaveReqVO> pocList;

    private Integer submitFlag;

    @Schema(description = "供应商付款方式列表")
    private List<VenderPayment> paymentList;
}
