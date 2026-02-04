package com.syj.eplus.module.scm.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.TransformNoticeMidVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemAndContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeItemReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mapper
public interface PurchaseContractItemConvert {

    PurchaseContractItemConvert INSTANCE = Mappers.getMapper(PurchaseContractItemConvert.class);

    @Mapping(target = "effectRangeList",ignore = true)
    PurchaseContractItemRespVO convert(PurchaseContractItemDO purchaseContractItemDO);

    //        PurchaseContractItemInfoRespVO convert1(PurchaseContractItemDO itemDo);
    default List<PurchaseContractItemAndContractInfoRespVO> convert2(Map<Long, PurchaseContractDO> purchaseContractDOMap, List<PurchaseContractItemDO> itemDoList) {
        return CollectionUtils.convertList(itemDoList, itemDo -> {
            PurchaseContractItemRespVO resp = convert(itemDo);
            PurchaseContractItemAndContractInfoRespVO respVO = BeanUtils.toBean(resp, PurchaseContractItemAndContractInfoRespVO.class);
            Long parentId = itemDo.getPurchaseContractId();
            if (Objects.isNull(parentId) || CollUtil.isEmpty(purchaseContractDOMap)) {
                return respVO;
            }
            PurchaseContractDO purchaseContractDO = purchaseContractDOMap.get(parentId);
            if (Objects.isNull(purchaseContractDO)) {
                return respVO;
            }
            respVO.setCode(purchaseContractDO.getCode());
            respVO.setAuditStatus(purchaseContractDO.getAuditStatus());
            respVO.setContractStatus(purchaseContractDO.getContractStatus());
            respVO.setTotalAmount(purchaseContractDO.getTotalAmount());
            respVO.setQuantity(purchaseContractDO.getTotalQuantity());
            respVO.setPrintTimes(purchaseContractDO.getPrintTimes());
            respVO.setPrintFlag(purchaseContractDO.getPrintFlag());
            respVO.setPayStatus(purchaseContractDO.getPayStatus());
            respVO.setPayedAmount(purchaseContractDO.getPayedAmount());
            respVO.setPrepayStatus(purchaseContractDO.getPrepayStatus());
            respVO.setPrepayAmount(purchaseContractDO.getPrepayAmount());
            respVO.setInvoiceStatus(purchaseContractDO.getInvoiceStatus());
            JsonAmount totalAmount = purchaseContractDO.getTotalAmount();
            if (Objects.nonNull(totalAmount)) {
                respVO.setInvoicedAmount(totalAmount.getAmount());
            }

            respVO.setManager(purchaseContractDO.getManager());
            respVO.setPurchaseUserId(purchaseContractDO.getPurchaseUserId());
            respVO.setPurchaseUserName(purchaseContractDO.getPurchaseUserName());
            respVO.setPurchaseUserDeptId(purchaseContractDO.getPurchaseUserDeptId());
            respVO.setPurchaseUserDeptName(purchaseContractDO.getPurchaseUserDeptName());
            respVO.setVenderId(purchaseContractDO.getVenderId());
            respVO.setVenderCode(purchaseContractDO.getVenderCode());
            respVO.setVenderName(purchaseContractDO.getStockName());
            respVO.setStockId(purchaseContractDO.getStockId());
            respVO.setStockCode(purchaseContractDO.getStockCode());
            respVO.setStockName(purchaseContractDO.getStockName());
            respVO.setPurchasePlanId(purchaseContractDO.getPurchasePlanId());
            respVO.setPurchasePlanCode(purchaseContractDO.getPurchasePlanCode());
            respVO.setSaleContractId(purchaseContractDO.getSaleContractId());
            respVO.setSaleContractCode(purchaseContractDO.getSaleContractCode());
            respVO.setRemark(purchaseContractDO.getRemark());
            respVO.setCreateTime(purchaseContractDO.getCreateTime());
            respVO.setCreator(purchaseContractDO.getCreator());
            respVO.setCreatorName(purchaseContractDO.getStockName());
            respVO.setCompanyId(purchaseContractDO.getCompanyId());
            // respVO.setCompanyName(purchaseContractDO.getStockName());
            respVO.setPurchaseTime(purchaseContractDO.getPurchaseTime());
            respVO.setPortId(purchaseContractDO.getPortId());
            respVO.setFreight(purchaseContractDO.getFreight());
            respVO.setOtherCost(purchaseContractDO.getOtherCost());
            respVO.setDeliveryDate(purchaseContractDO.getDeliveryDate());
            respVO.setItemCountTotal(purchaseContractDO.getTotalQuantity());
            respVO.setSignBackFlag(purchaseContractDO.getSignBackFlag());
            respVO.setTaxType(purchaseContractDO.getTaxType());


            return respVO;
        });
    }

    PurchaseContractItemInfoRespVO convert3(PurchaseContractDO contractDO);

    PurchaseContractItemDO convertPurchaseContractItemDO(PurchaseContractItemSaveReqVO saveReqVO);

    List<PurchaseContractItemDO> convertPurchaseContractItemListRespVO(List<PurchaseContractItemDO> itemDOList);

    List<PurchaseContractItemRespVO> convertRespVOList(List<PurchaseContractItemDO> itemDOList);

    @Mapping(target = "purchaserId", source = "purchaseUserId")
    @Mapping(target = "purchaserDeptId", source = "purchaseUserDeptId")
    StockNoticeItemReqVO convertTransformNoticeMidVO(TransformNoticeMidVO transformNoticeMidVO);
    default List<StockNoticeItemReqVO> convertStockNoticeItemList(List<TransformNoticeMidVO> transformNoticeMidVO) {
        return CollectionUtils.convertList(transformNoticeMidVO, s -> {
            return convertTransformNoticeMidVO(s);
        });
    }

    PurchaseContractItemDTO convertPurchaseContractItemDTO(PurchaseContractItemDO purchaseContractItemDO);

   default List<PurchaseContractItemDTO> convertPurchaseContractItemDTOList(List<PurchaseContractItemDO> purchaseContractItemDOList,Map<String, VenderDO> venderNameCache,Map<String, SkuNameDTO> skuNameCache) {
        return CollectionUtils.convertList(purchaseContractItemDOList, s->{
            PurchaseContractItemDTO dto = convertPurchaseContractItemDTO(s);
            if (CollUtil.isNotEmpty(venderNameCache)){
                VenderDO venderDO = venderNameCache.get(s.getVenderCode());
                if (Objects.nonNull(venderDO)) {
                    dto.setVenderName(venderDO.getName());
                }
            }
            if (CollUtil.isNotEmpty(skuNameCache)){
                SkuNameDTO skuNameDTO = skuNameCache.get(s.getSkuCode());
                if (Objects.nonNull(skuNameDTO)) {
                    dto.setSkuName(skuNameDTO.getName());
                }
            }

            return dto;
        });
    }
}