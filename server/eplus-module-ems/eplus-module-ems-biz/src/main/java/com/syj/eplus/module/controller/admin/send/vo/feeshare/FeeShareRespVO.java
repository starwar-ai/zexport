//package com.syj.eplus.module.controller.admin.send.vo.feeshare;
//
//import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
//import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareDTO;
//import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
//import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
//import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.util.List;
//
//@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
//@Data
//public class FeeShareRespVO extends FeeShareDTO {
//
//
//    @Schema(description = "费用归集客户列表")
//    private List<CustAllDTO> crmChildren;
//
//    @Schema(description = "费用归集供应商列表")
//    private List<VenderAllDTO> venderChildren;
//
//    @Schema(description = "费用归集销售列表")
//    private List<SmsContractAllDTO> smsChildren;
//
//    @Schema(description = "费用归集采购列表")
//    private List<PurchaseContractAllDTO> purchaseChildren;
//
//}