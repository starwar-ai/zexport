package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.annotations.CompareField;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/16:05
 * @Description:
 */
@Data
public class PurchaseContractDTO {
    /**
     * 销售合同编号
     */
    private String saleContractCode;

    /**
     * 采购合同编号
     */
    private String code;

    /**
     * 采购员编码
     */
    private Long purchaseUserId;
    /**
     * 采购员名称
     */
    @CompareField(value = "采购员名称")
    private String purchaseUserName;
    /**
     * 采购员部门编码
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    @CompareField(value = "采购员部门名称")
    private String purchaseUserDeptName;

}
