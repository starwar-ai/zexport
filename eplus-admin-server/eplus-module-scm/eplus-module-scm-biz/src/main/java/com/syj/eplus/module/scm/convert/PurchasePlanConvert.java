package com.syj.eplus.module.scm.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractSaveInfoReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemToContractSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractSaveDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mapper
public interface PurchasePlanConvert {


        PurchasePlanConvert INSTANCE = Mappers.getMapper(PurchasePlanConvert.class);

        PurchasePlanRespVO convert(PurchasePlanDO purchasePlanDO);
        PurchasePlanDO convertPurchasePlanDO(PurchasePlanInfoSaveReqVO purchasePlanDO);
        PurchasePlanInfoRespVO convertInfoResp(PurchasePlanDO purchasePlanDO);

        default PurchasePlanInfoRespVO convertPurchasePlanRespVO(PurchasePlanDO purchasePlanDO){
            return BeanUtils.toBean(purchasePlanDO, PurchasePlanInfoRespVO.class);
        }

    default List<PurchasePlanInfoRespVO> convertPurPlanInfoVOList(List<PurchasePlanDO> purchasePlanDOList, Map<Long, AdminUserRespDTO>  userRespDTOMap ,Map<Long, List<PurchasePlanItemRespVO>> purchaseItemMap){
                List<PurchasePlanInfoRespVO> resultList = new ArrayList<>();
                if(CollUtil.isEmpty(purchaseItemMap))
                        return  resultList;
                for (PurchasePlanDO purchaseplan : purchasePlanDOList){
                        List<PurchasePlanItemRespVO> purchasePlanItemRespVOS = purchaseItemMap.get(purchaseplan.getId());
                        if(CollUtil.isNotEmpty(purchasePlanItemRespVOS)){
                                PurchasePlanInfoRespVO purchasePlanInfoRespVO = convertPurchasePlanRespVO(purchaseplan);
                                purchasePlanInfoRespVO.setChildren( BeanUtils.toBean(purchasePlanItemRespVOS, PurchasePlanItemInfoRespVO.class ));
                                //计算采购总数
                                Integer sumTotal = purchasePlanItemRespVOS.stream().mapToInt(PurchasePlanItemRespVO::getNeedPurQuantity).sum();
                                purchasePlanInfoRespVO.setItemCountTotal(sumTotal);

                                //名称赋值
                                Long l = null;
                                try {
                                        l = Long.parseLong(purchasePlanInfoRespVO.getCreator());
                                }
                                catch (Exception e ){  }
                                if(CollUtil.isNotEmpty(userRespDTOMap)){
                                        AdminUserRespDTO adminUserRespDTO = userRespDTOMap.get(l);
                                        if(Objects.nonNull(adminUserRespDTO))
                                                purchasePlanInfoRespVO.setCreatorName(adminUserRespDTO.getNickname());
                                }
                                resultList.add(purchasePlanInfoRespVO);
                        }


                }




                return  resultList;
        }


        PurchasePlanDO convert(PurchasePlanInfoSaveReqVO updateReqVO);
        @Mapping(target = "quantity", source = "needPurQuantity")
        PurchaseContractItemSaveReqVO convert(PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO);

         default List<PurchaseContractItemSaveReqVO> convertToContractSaveReqVOList(List<PurchasePlanItemToContractSaveReqVO> purchasePlanItemToContractSaveReqVOList){
                 return CollectionUtils.convertList(purchasePlanItemToContractSaveReqVOList, this::convert);
         }

    @Mapping(target = "unitPrice", source = "withTaxPrice")
         @Mapping(target = "taxRefundRate", source = "taxRate")
         @Mapping(target = "id", source = "saleContractItemId")
        SaleContractItemSaveDTO convertSaleContractItemSaveDTO(PurchaseContractItemSaveReqVO purchaseContractItemSaveReqVO);
        default List<SaleContractItemSaveDTO> convertSaleContractItemSaveDTOList(List<PurchaseContractItemSaveReqVO> purchaseContractItemSaveReqVOList){
                return CollectionUtils.convertList(purchaseContractItemSaveReqVOList, this::convertSaleContractItemSaveDTO);
        }

    default SaleContractSaveDTO convertSaleContractSaveDTO(PurchaseContractSaveInfoReqVO purchaseContractSaveInfoReqVO){
                SaleContractSaveDTO saleContractSaveDTO = BeanUtils.toBean(purchaseContractSaveInfoReqVO, SaleContractSaveDTO.class);
                List<PurchaseContractItemSaveReqVO> planChildren = purchaseContractSaveInfoReqVO.getChildren();
                saleContractSaveDTO.setChildren(convertSaleContractItemSaveDTOList(planChildren));
                return saleContractSaveDTO;
        }

        @Mapping(target = "unitPrice", source = "withTaxPrice")
        @Mapping(target = "taxRefundRate", source = "taxRate")
        SaleContractItemSaveDTO purchasePlanItemToContractSaveReqVOToSaleContractItemSaveDTO(PurchasePlanItemToContractSaveReqVO purchaseContractItemSaveReqVO);
}