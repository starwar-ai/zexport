package com.syj.eplus.module.crm.controller.admin.cust.vo;

import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountRespVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocRespVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementRespVO;
import com.syj.eplus.module.crm.dal.dataobject.custcompanypath.CustCompanyPath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/7 12:40
 */
@Data
@Accessors(chain = false)
public class CustInfoRespVo extends CustRespVO {



    @Schema(description = "客户银行信息列表")
    private List<CustBankaccountRespVO> bankaccountList;

    @Schema(description = "客户联系人列表")
    private List<CustPocRespVO> pocList;

    @Schema(description = "客户结汇方式列表")
    private List<CustSettlementRespVO> settlementList;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "变更前客户信息")
    private CustInfoRespVo oldCust;

    @Schema(description = "收款方式")
    private List<CollectionAccountRespVO> collectionAccountList;

    @Schema(description = "客户公司路径列表")
    private List<CustCompanyPath> custCompanyPathList;

}
