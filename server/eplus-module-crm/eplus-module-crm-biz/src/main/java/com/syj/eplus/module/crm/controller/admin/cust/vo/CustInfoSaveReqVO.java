package com.syj.eplus.module.crm.controller.admin.cust.vo;

import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountSaveReqVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocSaveReqVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import com.syj.eplus.module.crm.dal.dataobject.custcompanypath.CustCompanyPath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/7 13:15
 */
@Data
public class CustInfoSaveReqVO extends CustSaveReqVO {

    @Schema(description = "客户银行信息列表")
    private List<CustBankaccountSaveReqVO> bankaccountList;

    @Schema(description = "客户联系人列表")
//    @NotEmpty(message = "联系人不能为空")
    private List<CustPocSaveReqVO> pocList;

    @Schema(description = "客户结汇方式列表")
    private List<CustSettlementSaveReqVO> settlementList;

    @Schema(description = "客户公司路径列表")
    private List<CustCompanyPath> custCompanyPathList;
    private Integer submitFlag;

    @Schema(description = "客户组列表")
    private List<String> custCodeList;

    @Schema(description = "收款方式")
    private List<CollectionAccountDO> collectionAccountList;
}
