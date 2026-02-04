package com.syj.eplus.module.scm.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.SimplePurchaseContractItemDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.entity.PurchaseAddSubTermChange;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.scm.entity.PurchasePaymentPlanChange;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.*;


@Mapper
public interface PurchaseContractConvert {

    PurchaseContractConvert INSTANCE = Mappers.getMapper(PurchaseContractConvert.class);

    PurchaseContractRespVO convert(PurchaseContractDO purchaseContractDO);

    default PurchaseContractInfoRespVO convertPurchaseContractRespVO(PurchaseContractDO purchaseContractDO) {
        return BeanUtils.toBean(purchaseContractDO, PurchaseContractInfoRespVO.class);
    }

    PurchaseContractDO convertPurchaseContractDO(PurchaseContractSaveReqVO saveReqVO);

    default List<PurchaseContractInfoRespVO> convertPurchaseContractInfoVOList(List<PurchaseContractDO> purchaseContractDOList,
                                                                               Map<Long, AdminUserRespDTO> userRespDTOMap, Map<Long, List<PurchaseContractItemRespVO>> purchaseItemMap,
                                                                               Map<Long, SimpleCompanyDTO> simpleCompanyMap, List<SimpleVenderRespDTO> venderRespDTOList,
                                                                               List<SimpleCustRespDTO> contractSimpleCustRespDTOList,
                                                                               Map<String, List<PurchasePaymentPlan>> purchasePaymentPlanMap) {
        List<PurchaseContractInfoRespVO> resultList = new ArrayList<>();
        if (CollUtil.isEmpty(purchaseItemMap))
            return resultList;
//        Map<Long, String> venderNameMap = venderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, SimpleVenderRespDTO::getName));
//        Map<Long, String> custNameMap = contractSimpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, SimpleCustRespDTO::getName));

        for (PurchaseContractDO purchaseContract : purchaseContractDOList) {
            List<PurchaseContractItemRespVO> purchaseContractItemRespVOS = purchaseItemMap.get(purchaseContract.getId());
            if (CollUtil.isNotEmpty(purchaseContractItemRespVOS)) {

                PurchaseContractInfoRespVO purchaseContractInfoRespVO = convertPurchaseContractRespVO(purchaseContract);
                List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMap.get(purchaseContract.getCode());
                purchaseContractInfoRespVO.setPurchasePaymentPlanList(purchasePaymentPlans);
                purchaseContractInfoRespVO.setChildren(BeanUtils.toBean(purchaseContractItemRespVOS, PurchaseContractItemInfoRespVO.class));
                //计算采购总数
                Integer sumTotal = purchaseContractItemRespVOS.stream().mapToInt(PurchaseContractItemRespVO::getQuantity).sum();
                purchaseContractInfoRespVO.setItemCountTotal(sumTotal);

                //名称赋值
                Long l = null;
                try {
                    l = Long.parseLong(purchaseContractInfoRespVO.getCreator());
                } catch (Exception e) {
                }
                if (CollUtil.isNotEmpty(userRespDTOMap)) {
                    AdminUserRespDTO adminUserRespDTO = userRespDTOMap.get(l);
                    if (Objects.nonNull(adminUserRespDTO))
                        purchaseContractInfoRespVO.setCreatorName(adminUserRespDTO.getNickname());
                }

                //公司主体赋值
                Long companyId = purchaseContract.getCompanyId();
                Optional.ofNullable(companyId).
                        filter(simpleCompanyMap::containsKey)
                        .map(simpleCompanyMap::get)
                        .map(SimpleCompanyDTO::getName)
                        .ifPresent(purchaseContractInfoRespVO::setCompanyName);

                //供应商
//                String venderName = venderNameMap.get(purchaseContractInfoRespVO.getVenderId());
//                if (Objects.nonNull(venderName)) {
//                    purchaseContractInfoRespVO.setVenderName(venderName);
//                }

                //客户
//                String custName = custNameMap.get(purchaseContractInfoRespVO.getCustId());
//                if (Objects.nonNull(custName)) {
//                    purchaseContractInfoRespVO.setCustName(custName);
//                }

                //设置子项求和数量
                List<PurchaseContractItemInfoRespVO> children = purchaseContractInfoRespVO.getChildren();
                if (Objects.isNull(children))
                    continue;
                int billQuantitySum = children.stream().filter(s -> s.getBillQuantity() != null).mapToInt(PurchaseContractItemInfoRespVO::getBillQuantity).sum();
                purchaseContractInfoRespVO.setTotalReceivedCount(billQuantitySum);
                int returnSum = children.stream().mapToInt(PurchaseContractItemInfoRespVO::getReturnQuantity).sum();
                purchaseContractInfoRespVO.setTotalReturnCount(returnSum);
                int checkSum = children.stream().mapToInt(PurchaseContractItemInfoRespVO::getCheckedQuantity).sum();
                purchaseContractInfoRespVO.setTotalCheckedCount(checkSum);
                int exchangeSum = children.stream().mapToInt(PurchaseContractItemInfoRespVO::getExchangeQuantity).sum();
                purchaseContractInfoRespVO.setTotalExchangeCount(exchangeSum);
                int quantitySum = children.stream().mapToInt(PurchaseContractItemInfoRespVO::getQuantity).sum();
                purchaseContractInfoRespVO.setTotalQuantity(quantitySum);

                List<JsonAmount> resultAmountList = new ArrayList<>();
                for (PurchaseContractItemInfoRespVO s : children) {
                    if (Objects.nonNull(s.getWithTaxPrice()) && Objects.nonNull(s.getWithTaxPrice().getAmount())
                            && Objects.nonNull(s.getWithTaxPrice().getCurrency())
                            && Objects.nonNull(s.getQuantity())) {
                        Optional<JsonAmount> first = resultAmountList.stream().filter(f -> Objects.equals(f.getCurrency(), s.getWithTaxPrice().getCurrency())).findFirst();
                        first.ifPresent(jsonAmount -> jsonAmount.getAmount().add(s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity()))));
                    }
                }

                resultList.add(purchaseContractInfoRespVO);
            }
        }
        return resultList;
    }


    default List<PurchaseContractItemDO> convertPurchaseContractItemDOList(List<PurchaseContractItemInfoRespVO> children) {
        return CollectionUtils.convertList(children, s -> {
            return BeanUtils.toBean(s, PurchaseContractItemDO.class);
        });
    }

    default List<PurchasePaymentPlan> convertPurchasePaymentPlanList(List<PurchasePaymentPlanChange> paymentPlanChanges) {
        return CollectionUtils.convertList(paymentPlanChanges, s -> {
            return convertPurchasePaymentPlan(s);
        });
    }

    default List<PurchaseAddSubTerm> convertPurchaseSubAddTermList(List<PurchaseAddSubTermChange> subTermChanges) {
        return CollectionUtils.convertList(subTermChanges, s -> {
            return convertPurchaseSubAddTerm(s);
        });
    }

    PurchaseAddSubTerm convertPurchaseSubAddTerm(PurchaseAddSubTermChange purchaseAddSubTermChange);

    PurchasePaymentPlan convertPurchasePaymentPlan(PurchasePaymentPlanChange purchasePaymentPlanChange);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "sourceCode")
    @Mapping(target = "confirmFlag", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "changeStatus", ignore = true)
    PurchaseContractDO convertPurchaseContractDO(PurchaseContractChange purchaseContractChange);

    PurchaseContractDO convert(PurchaseContractSaveInfoReqVO updateInfoReqVO);

    List<PurchaseContractItemDO> convert(List<PurchaseContractItemSaveReqVO> children);

    List<PurchasePaymentPlan> convertPurchasePaymentPlan(List<SystemPaymentPlanDTO> systemPaymentPlanDTOList);

    PurchaseContractChange convertPurchaseContractChange(PurchaseContractInfoRespVO updateInfoReqVO);

    PurchaseContractInfoRespVO convertPurchaseContractRespVO(PurchaseContractChange purchaseContractChange);

    TransformNoticeMidVO convertTransformNoticeMid(PurchaseContractItemDO itemDO);

    default List<TransformNoticeMidVO> convertTransformNoticeMidList(List<PurchaseContractItemDO> items) {
        return CollectionUtils.convertList(items, s -> {
            TransformNoticeMidVO transformNoticeMidVO = convertTransformNoticeMid(s);
            transformNoticeMidVO.setSaleItemUniqueCode(s.getSaleItemUniqueCode());
            transformNoticeMidVO.setPurchaseItemUniqueCode(s.getUniqueCode());
            return transformNoticeMidVO;
        });
    }

    @Mapping(source = "withTaxPrice", target = "price")
    @Mapping(source = "uniqueCode", target = "purchaseItemUniqueCode")
    @Mapping(source = "saleItemUniqueCode", target = "saleItemUniqueCode")
    BillItemSaveReqVO convertBillItemSaveReqVO(PurchaseContractItemInfoRespVO purchaseContractItemInfoRespVO);

    default List<BillItemSaveReqVO> convertBillItemSaveReqVOList(List<PurchaseContractItemInfoRespVO> purchaseContractItemInfoRespVOList) {
        return CollectionUtils.convertList(purchaseContractItemInfoRespVOList, this::convertBillItemSaveReqVO);
    }

    PurchaseContractItemExportVO convertPurchaseContractItemExportVO(PurchaseContractItemDO purchaseContractItemDO);

    default List<PurchaseContractItemExportVO> convertPurchaseContractItemExportVOList(List<PurchaseContractItemDO> purchaseContractItemDO) {
        return CollectionUtils.convertList(purchaseContractItemDO, s -> {
            PurchaseContractItemExportVO settlementFormItemExportVO = convertPurchaseContractItemExportVO(s);
            if (s.getQuantity() != null && s.getWithTaxPrice().getAmount() != null) {
                settlementFormItemExportVO.setPurchaseTotalAmount(NumUtil.mul(s.getQuantity(), s.getWithTaxPrice().getAmount()));
            }
            return settlementFormItemExportVO;
        });
    }

    @Mapping(target = "taxRefundRate",source = "taxRate")
    SimplePurchaseContractItemDTO convertSimplePurchaseContractItemDTO(PurchaseContractItemDO purchaseContractItemDO);

    default List<SimplePurchaseContractItemDTO> convertSimplePurchaseContractItemDTOList(List<PurchaseContractItemDO> purchaseContractItemDOList, Map<Long, PurchaseContractDO> contractDOMap) {
        return CollectionUtils.convertList(purchaseContractItemDOList, s -> {
            SimplePurchaseContractItemDTO simplePurchaseContractItemDTO = convertSimplePurchaseContractItemDTO(s);
            if (CollUtil.isNotEmpty(contractDOMap)) {
                PurchaseContractDO purchaseContractDO = contractDOMap.get(s.getPurchaseContractId());
                if (Objects.nonNull(purchaseContractDO)) {
                    simplePurchaseContractItemDTO.setVenderName(purchaseContractDO.getVenderName());
                    simplePurchaseContractItemDTO.setPurchaseCurrency(purchaseContractDO.getCurrency());
                }
            }
            simplePurchaseContractItemDTO.setPurchaseUser(new UserDept().setUserId(s.getPurchaseUserId()).setNickname(s.getPurchaseUserName()).setDeptId(s.getPurchaseUserDeptId()).setDeptName(s.getPurchaseUserDeptName()));
            return simplePurchaseContractItemDTO;
        });
    }

    /**
     * 填充金额拆分字段数据
     * @param purchaseContractRespVO 采购合同响应VO
     */
    default void fillAmountSplitFields(PurchaseContractRespVO purchaseContractRespVO) {
        if (purchaseContractRespVO == null) {
            return;
        }

        // 跟单员姓名
        if (purchaseContractRespVO.getManager() != null) {
            purchaseContractRespVO.setManagerNickName(purchaseContractRespVO.getManager().getNickname());
        }

        // 预付金额拆分
        if (purchaseContractRespVO.getPrepayAmount() != null) {
            purchaseContractRespVO.setPrepayAmountAmount(NumberFormatUtil.formatAmount(purchaseContractRespVO.getPrepayAmount().getAmount()));
            purchaseContractRespVO.setPrepayAmountCurrency(purchaseContractRespVO.getPrepayAmount().getCurrency());
        }

        // 已付款金额拆分
        if (purchaseContractRespVO.getPayedAmount() != null) {
            purchaseContractRespVO.setPayedAmountAmount(NumberFormatUtil.formatAmount(purchaseContractRespVO.getPayedAmount().getAmount()));
            purchaseContractRespVO.setPayedAmountCurrency(purchaseContractRespVO.getPayedAmount().getCurrency());
        }

        // 运费拆分
        if (purchaseContractRespVO.getFreight() != null) {
            purchaseContractRespVO.setFreightAmount(NumberFormatUtil.formatAmount(purchaseContractRespVO.getFreight().getAmount()));
            purchaseContractRespVO.setFreightCurrency(purchaseContractRespVO.getFreight().getCurrency());
        }

        // 其他费用拆分
        if (purchaseContractRespVO.getOtherCost() != null) {
            purchaseContractRespVO.setOtherCostAmount(NumberFormatUtil.formatAmount(purchaseContractRespVO.getOtherCost().getAmount()));
            purchaseContractRespVO.setOtherCostCurrency(purchaseContractRespVO.getOtherCost().getCurrency());
        }

        // 采购合同金额RMB拆分
        if (purchaseContractRespVO.getTotalAmountRmb() != null) {
            purchaseContractRespVO.setTotalAmountRmbAmount(NumberFormatUtil.formatAmount(purchaseContractRespVO.getTotalAmountRmb().getAmount()));
        }
    }

}
