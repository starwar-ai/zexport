package com.syj.eplus.module.scm.convert;

import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemToContractSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface PurchasePlanItemConvert {

    PurchasePlanItemConvert INSTANCE = Mappers.getMapper(PurchasePlanItemConvert.class);

    PurchasePlanItemRespVO convert(PurchasePlanItemDO purchasePlanItemDO);

    List<PurchasePlanItemRespVO> convertRespVOList(List<PurchasePlanItemDO> purchasePlanItemDOList);

    PurchasePlanItemDO convertPurchasePlanItemDO(PurchasePlanItemSaveReqVO saveReqVO);

    List<PurchasePlanItemDO> convert(List<PurchasePlanItemSaveReqVO> children);

    List<PurchasePlanItemDO> convertPurchasePlanItemListBySaveList(List<PurchasePlanItemToContractSaveReqVO> saveReqVOList);
}