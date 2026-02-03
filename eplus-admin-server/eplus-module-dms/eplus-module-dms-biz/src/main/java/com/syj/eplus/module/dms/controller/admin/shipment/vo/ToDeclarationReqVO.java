package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ToDeclarationReqVO {
    private List<DeclarationReq> declarationReqList;
}