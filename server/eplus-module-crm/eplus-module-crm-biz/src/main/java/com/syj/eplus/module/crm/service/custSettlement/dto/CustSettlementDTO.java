package com.syj.eplus.module.crm.service.custSettlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description：客户结汇方式dto
 * @Author：du
 * @Date：2024/1/25 10:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustSettlementDTO {
    /**
     * 编号
     */
    private Long id;
    /**
     * 结汇方式编号
     */
    private Long settlementId;
    /**
     * 结汇名称
     */
    private String name;
    /**
     * 结汇英文名称
     */
    private String nameEng;
    /**
     * 是否缺省
     */
    private Integer defaultFlag;
}
