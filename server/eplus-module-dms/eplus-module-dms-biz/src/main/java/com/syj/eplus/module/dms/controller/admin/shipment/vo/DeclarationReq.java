package com.syj.eplus.module.dms.controller.admin.shipment.vo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class DeclarationReq {
    /**
     * 主键
     */
    private Long id;

     /**本次报关数量*/
    private Integer declarationQuantityCurrent;
}