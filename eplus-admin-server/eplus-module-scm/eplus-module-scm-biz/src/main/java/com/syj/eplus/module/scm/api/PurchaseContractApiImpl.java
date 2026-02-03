package com.syj.eplus.module.scm.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.qms.enums.InspectionItemStatusEnum;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryContractItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.*;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractPageReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractSaveInfoReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.mysql.purchasecontract.PurchaseContractMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.enums.PurchaseCheckStatusEmums;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
import com.syj.eplus.module.scm.service.purchaseplan.PurchasePlanService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Service
public class PurchaseContractApiImpl implements PurchaseContractApi {

    @Resource
    @Lazy
    PurchaseContractService purchaseContractService;
    @Resource
    @Lazy
    PurchaseContractItemService purchaseContractItemService;
    @Resource
    PurchaseContractItemMapper purchaseContractItemMapper;

    @Resource
    PurchaseContractMapper purchaseContractMapper;
    @Resource
    @Lazy
    PurchasePlanService purchasePlanService;
    @Resource
    CustApi custApi;

    @Resource
    VenderApi venderApi;
    @Resource
    private ShipmentApi shipmentApi;
    @Resource
    private SkuApi skuApi;
    public List<PurchaseContractAllDTO> getUnCompletedPurchaseContractDetail(List<Long> purchaseContractIds) {
        Integer[] statusList = {
                PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode(),
                PurchaseContractStatusEnum.AWAITING_APPROVAL.getCode(),
                PurchaseContractStatusEnum.REJECTED.getCode(),
                PurchaseContractStatusEnum.AWAITING_ORDER.getCode(),
                PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode()
        };
        PurchaseContractPageReqVO purchaseContractPageReqVO = new PurchaseContractPageReqVO();
        purchaseContractPageReqVO.setPageNo(0).setPageSize(-1);
        purchaseContractPageReqVO.setIdList(purchaseContractIds.stream().toArray(Long[]::new));
        purchaseContractPageReqVO.setContractStatusList(statusList);
        PageResult<PurchaseContractInfoRespVO> purchaseContractInfoRespVOPageResult = purchaseContractService.getPurchaseContractPage(purchaseContractPageReqVO);
        if (purchaseContractInfoRespVOPageResult == null) {
            return null;
        }
        List<PurchaseContractInfoRespVO> doList = purchaseContractInfoRespVOPageResult.getList();
        List<PurchaseContractAllDTO> dtoList = BeanUtils.toBean(doList, PurchaseContractAllDTO.class);
        return dtoList;
    }

    @Override
    public List<PurchaseContractAllDTO> getUnCompletedPurchaseContractByVenderCode(String venderCode) {
        List<Long> purchaseContractIds = purchaseContractService.getPurchaseContractIdsByVenderCode(venderCode);
        if (CollUtil.isEmpty(purchaseContractIds)) {
            return null;
        }
        return getUnCompletedPurchaseContractDetail(purchaseContractIds);
    }

    @Override
    public List<PurchaseContractAllDTO> getUnCompletedPurchaseContractBySkuCode(String skuCode) {
        List<Long> purchaseContractIds = purchaseContractService.getPurchaseContractIdsBySkuCode(skuCode);
        if (CollUtil.isEmpty(purchaseContractIds)) {
            return null;
        }
        return getUnCompletedPurchaseContractDetail(purchaseContractIds);
    }

    @Override
    public CommonResult<PageResult<PurchaseContractSimpleDTO>> getSimplePage(PurchaseContractGetSimplePageReqDTO pageReqVO) {
        PageResult<PurchaseContractSimpleDTO> pageResult = purchaseContractService.getPurchaseContractSimplePage(pageReqVO);
        return success(pageResult);
    }

    @Override
    public PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO reqDTO) {
        return purchaseContractItemService.getAuxiliarySkuPurchasePage(reqDTO);
    }

    @Override
    public Map<Long, PurchaseContractSimpleDTO> getContractMap(List<Long> idList) {
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractService.selectByIdList(idList);
        if (CollectionUtils.isEmpty(purchaseContractDOList)) {
            return Map.of();
        }
        List<PurchaseContractSimpleDTO> contractSimpleDTOList = BeanUtils.toBean(purchaseContractDOList, PurchaseContractSimpleDTO.class);
        List<String> codeList = contractSimpleDTOList.stream().map(PurchaseContractSimpleDTO::getCustCode).distinct().toList();
        Map<String, CustAllDTO> custByCodeList = custApi.getCustByCodeList(codeList);
        contractSimpleDTOList.forEach(s->{
            if(CollUtil.isNotEmpty(custByCodeList)){
                CustAllDTO custAllDTO = custByCodeList.get(s.getCustCode());
                if(Objects.nonNull(custAllDTO)){
                    s.setCustName(custAllDTO.getName());
                }
            }
        });
        return contractSimpleDTOList.stream().collect(Collectors.toMap(PurchaseContractSimpleDTO::getId, Function.identity()));
    }

    @Override
    public Map<String, UserDept> getPurManagerMapByCodeList(List<String> codeList) {
        return purchaseContractService.getPurManagerMapByCodeList(codeList);
    }

    @Override
    public Collection<String> validatePurContractExists(Collection<String> codeList) {
        return purchaseContractService.validatePurContractExists(codeList);
    }

//    @Override
//    public Map<String, PurchaseContractDTO> getPurchaseContractMap(List<String> codeList,Long companyId) {
//        return purchaseContractService.getPurchaseContractMap(codeList,companyId);
//    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, Long id) {
        purchaseContractService.updateConfirmFlag(confirmFlag, id);
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, String code) {
        purchaseContractService.updateConfirmFlag(confirmFlag, code);
    }

    @Override
    public void updateVenderId(Long venderId, String venderCode) {
        purchaseContractService.updateVenderId(venderId, venderCode);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, PurchaseContractAllDTO> getPurchaseByCodeList(List<String> codeList) {
        List<PurchaseContractDO> doList = purchaseContractService.getPurchaseByCodeList(codeList);
        List<PurchaseContractAllDTO> dtoList = BeanUtils.toBean(doList, PurchaseContractAllDTO.class);
        if (CollUtil.isNotEmpty(dtoList)) {
            List<String> custCodeList = dtoList.stream().map(PurchaseContractAllDTO::getCustCode).distinct().toList();
            Map<String, CustAllDTO> custAllDTOMap = custApi.getCustByCodeList(custCodeList);
            List<Long> venderIdList = dtoList.stream().map(PurchaseContractAllDTO::getVenderId).distinct().toList();
            Map<Long, SimpleVenderRespDTO> simpleVenderRespDTOMap = venderApi.getSimpleVenderRespDTOMap(venderIdList);
            dtoList.forEach(s -> {
                if (CollUtil.isNotEmpty(custAllDTOMap)) {
                    CustAllDTO custAllDTO = custAllDTOMap.get(s.getCustCode());
                    if (Objects.nonNull(custAllDTO)) {
                        s.setCustName(custAllDTO.getName());
                    }
                }
                if (CollUtil.isNotEmpty(simpleVenderRespDTOMap)) {
                    SimpleVenderRespDTO simpleVenderRespDTO = simpleVenderRespDTOMap.get(s.getVenderId());
                    if (Objects.nonNull(simpleVenderRespDTO)) {
                        s.setVenderName(simpleVenderRespDTO.getName());
                    }
                }
            });
            return dtoList.stream().collect(Collectors.toMap(PurchaseContractAllDTO::getCode, s -> s, (s1,s2)->s1));
        }
        return null;

    }

    @Override
    public PurchaseContractAllDTO getPurchaseContractByCode(String code) {
        PurchaseContractAllDTO purchaseContractAllDTO = new PurchaseContractAllDTO();
        Map<String, PurchaseContractAllDTO> purchaseContractMap = this.getPurchaseByCodeList(Collections.singletonList(code));
        if (Objects.nonNull(purchaseContractMap)) {
            purchaseContractAllDTO = purchaseContractMap.get(code);
        }
        return purchaseContractAllDTO;
    }

    @Override
    public  List<CreatedResponse> genPurchaseContract(SavePurchaseContractReqVO savePurchaseContractReqVO) {
        PurchaseContractSaveInfoReqVO purchaseContractSaveReqVO = BeanUtils.toBean(savePurchaseContractReqVO, PurchaseContractSaveInfoReqVO.class);
        purchaseContractSaveReqVO.setChildren(BeanUtils.toBean(savePurchaseContractReqVO. getChildren(), PurchaseContractItemSaveReqVO.class));
        return purchaseContractService.createPurchaseContract(purchaseContractSaveReqVO);
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        return purchaseContractService.getConfirmSourceList(targetCode, effectRangeType);
    }

    @Override
    public Boolean modifyContractItemStatus(String purchaseContractCode, Map<Long, Integer> contractItemMap,LocalDateTime inspectionTime) {
        if(CollUtil.isEmpty(contractItemMap)){
            return false;
        }
        LambdaQueryWrapper<PurchaseContractItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCode);
        List<PurchaseContractItemDO> contractItemDOList = purchaseContractItemService.list(queryWrapper);
        Map<Long,Integer> updateMap = new HashMap<>();
        List<Long> saleContractItemIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(contractItemDOList)) {
            contractItemDOList.forEach(x -> {
                x.setInspectionTime(inspectionTime);
                Integer checkStatus = contractItemMap.get(x.getId());
                saleContractItemIdList.add(x.getSaleContractItemId());
                if (Objects.isNull(checkStatus)){
                    x.setCheckStatus(InspectionItemStatusEnum.PENDING.getValue());
                }else {
                    if (Objects.nonNull(x.getSaleContractItemId())){
                        updateMap.put(x.getSaleContractItemId(), checkStatus);
                    }
                    x.setCheckStatus(checkStatus);
                }
            });
            // 如果全部为验货通过，则更新采购合同验货状态为通过，否则部分通过
            List<PurchaseContractItemDO> unSucessItemlist = contractItemDOList.stream().filter(x -> ObjectUtil.isNotNull(x.getCheckStatus()) && x.getCheckStatus().intValue() != InspectionItemStatusEnum.SUCESS.getValue()).toList();
            List<PurchaseContractItemDO> sucessItemlist = contractItemDOList.stream().filter(x -> ObjectUtil.isNotNull(x.getCheckStatus()) && x.getCheckStatus().intValue() == InspectionItemStatusEnum.SUCESS.getValue()).toList();
            LambdaUpdateWrapper<PurchaseContractDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PurchaseContractDO::getCode,purchaseContractCode);
            if (CollUtil.isNotEmpty(unSucessItemlist) && CollUtil.isNotEmpty(sucessItemlist)) {
                updateWrapper.set(PurchaseContractDO::getCheckStatus, PurchaseCheckStatusEmums.PART_CHECK.getValue());
                purchaseContractService.update(updateWrapper);
            }else if (CollectionUtils.isEmpty(unSucessItemlist)) {
                updateWrapper.set(PurchaseContractDO::getCheckStatus, PurchaseCheckStatusEmums.ALL_PASS.getValue());
                purchaseContractService.update(updateWrapper);
                // 更新采购合同付款方式为验货日日期
                List<Integer> dateTypes = new ArrayList<>();
                dateTypes.add(DateTypeEnum.INSPECTION_DATE.getValue());
                dateTypes.add(DateTypeEnum.INSPECTION_TICKET_DATE.getValue());
                purchaseContractService.updateShipmentDate(saleContractItemIdList,dateTypes,LocalDateTime.now());
            }
            purchaseContractItemService.updateBatchById(contractItemDOList);
            purchaseContractService.updateShipmentCheckStatus(updateMap);
            return true;

        }
        return false;
    }

    @Override
    public Long getOrderNumBySaleContractId(boolean auxiliaryFlag, Long id) {
        return purchaseContractService.getOrderNumBySaleContractId(auxiliaryFlag, id);
    }

    @Override
    public List<AuxiliaryContractItemDTO> getAuxiliaryContractItemListBySaleCode(String code) {
        return purchaseContractItemService.getAuxiliaryContractItemListBySaleCode(code);

    }

    @Override
    public PurchaseContractItemDTO getItemDetailByItemId(Long auxiliaryPurchaseContractItemId) {

        return BeanUtils.toBean(purchaseContractItemService.getById(auxiliaryPurchaseContractItemId), PurchaseContractItemDTO.class);
    }

    @Override
    public void batchUpdateBillStatus(List<PurchaseBillSimpleDTO> billSimpleDTOList) {
        purchaseContractItemService.batchUpdateBillStatus(billSimpleDTOList);
    }

    @Override
    public void updateShipmentDate(List<Long> saleContractItemIdList,List<Integer> dateTypes,LocalDateTime estDepartureTime) {
        purchaseContractService.updateShipmentDate(saleContractItemIdList,dateTypes,estDepartureTime);
    }

    @Override
    public Boolean updatePurchaseItem(Long saleContractId, Long  purchaseContractId, List<String> purchaseItemUniqueCodeList, Integer purchaseItemBillStatus, Map<String,Integer> wmsBillMap,Map<String, Integer> diffBillQuantity) {
        LambdaQueryWrapper<PurchaseContractItemDO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(saleContractId)) {
            queryWrapper.eq(PurchaseContractItemDO::getSaleContractId, saleContractId);
        }
        if (ObjectUtil.isNotNull(purchaseContractId)) {
            queryWrapper.eq(PurchaseContractItemDO::getPurchaseContractId, purchaseContractId);
        }
        queryWrapper.in(PurchaseContractItemDO::getUniqueCode, purchaseItemUniqueCodeList);
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(purchaseContractItemDOList)) {
            purchaseContractItemDOList.forEach(x -> {
                x.setBillStatus(purchaseItemBillStatus);
                if (ObjectUtil.isNotNull(wmsBillMap) && MapUtil.isNotEmpty(wmsBillMap)) {
                    Integer actQuantity = wmsBillMap.getOrDefault(x.getUniqueCode(), 0);
                    Integer billQuantity = ObjectUtil.isNull(x.getBillQuantity()) ? 0 : x.getBillQuantity() + actQuantity;
                    x.setBillQuantity(billQuantity);
                }
                if (CollUtil.isNotEmpty(diffBillQuantity)){
                    Integer diffQuantity = diffBillQuantity.getOrDefault(x.getUniqueCode(), 0);
                    x.setNoticedQuantity(x.getNoticedQuantity()+diffQuantity);
                }
            });
            boolean billFlag = purchaseContractItemDOList.stream().anyMatch(s -> s.getQuantity() > s.getBillQuantity());
            if (billFlag){
                purchaseContractMapper.updateById(new PurchaseContractDO().setId(purchaseContractId).setConvertNoticeFlag(ConvertNoticeFlagEnum.PART_ISSUED.getStatus()));
            }else {
                purchaseContractMapper.updateById(new PurchaseContractDO().setId(purchaseContractId).setConvertNoticeFlag(ConvertNoticeFlagEnum.ISSUED.getStatus()));
            }
            return purchaseContractItemMapper.updateBatch(purchaseContractItemDOList);
        }
        return false;
    }

    @Override
    public void updatePurchaseItemOutQuantity(Map<String, Map<String, Integer>> stockMap) {
        if (CollUtil.isEmpty(stockMap)){
            return;
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, stockMap.keySet());
        if (CollUtil.isEmpty(purchaseContractItemDOList)){
            return;
        }
        // 解决 java.lang.NullPointerException: element cannot be mapped to a null key
        // 重写分组逻辑，过滤掉 purchaseContractCode 或 skuCode 为 null 的对象
        Map<String, Map<String, List<PurchaseContractItemDO>>> itemMap = purchaseContractItemDOList.stream()
            .filter(x -> ObjectUtil.isNotNull(x.getPurchaseContractCode()) && ObjectUtil.isNotNull(x.getSkuCode()))
            .collect(
                Collectors.groupingBy(
                    PurchaseContractItemDO::getPurchaseContractCode,
                    Collectors.groupingBy(PurchaseContractItemDO::getSkuCode)
                )
            );
        List<PurchaseContractItemDO> updateList = new ArrayList<>();
        stockMap.forEach((k,v)->{
            Map<String, List<PurchaseContractItemDO>> baseItemMapBySku = itemMap.get(k);
            if (CollUtil.isEmpty(baseItemMapBySku)){
                return;
            }
            v.forEach((skuCode,quantity)->{
                List<PurchaseContractItemDO> baseItemListBySku = baseItemMapBySku.get(skuCode);
                if (CollUtil.isEmpty(baseItemListBySku)){
                    return;
                }
                baseItemListBySku.forEach(x->{
                    x.setOutQuantity(ObjectUtil.isNull(x.getOutQuantity())?quantity:x.getOutQuantity()+quantity);
                    if (x.getOutQuantity().equals(x.getQuantity())){
                        x.setOutFlag(BooleanEnum.YES.getValue());
                    }
                    updateList.add(x);
                });
            });
        });
        if (CollUtil.isNotEmpty(updateList)){
            purchaseContractItemMapper.updateBatch(updateList);
        }
    }

    @Override
    public Long getPurchaseItemIdBySaleContractItemId(Long saleContractItemId) {

        PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemMapper.selectOne(PurchaseContractItemDO::getSaleContractItemId, saleContractItemId);
        if(Objects.isNull(purchaseContractItemDO)){
            return null;
        }

        return purchaseContractItemDO.getId();
    }

    @Override
    public Map<Long, Long> getPurchaseItemIdBySaleContractItemIds(List<Long> idList) {
        List<PurchaseContractItemDO> purchaseContractItemDO = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, idList);
        if(Objects.isNull(purchaseContractItemDO)){
            return null;
        }

        return purchaseContractItemDO.stream().collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId,PurchaseContractItemDO::getId,(s1,s2)->s2));

    }

    @Override
    public Map<Long, PriceQuantityEntity> getPurchaseContractItemPriceMap(Map<Long, String> saleItemSkuMap,Long companyId,String saleContractCode) {
        if (CollUtil.isEmpty(saleItemSkuMap)) {
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>()
                        .select(PurchaseContractDO::getCode)
                .eq(PurchaseContractDO::getSaleContractCode, saleContractCode)
                .ne(PurchaseContractDO::getAutoFlag, BooleanEnum.YES.getValue())
                .ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        if (CollUtil.isEmpty(purchaseContractDOList)){
            return Map.of();
        }
        List<String> purchaseContractCodeList = purchaseContractDOList.stream().map(PurchaseContractDO::getCode).toList();
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .in(PurchaseContractItemDO::getSaleContractItemId,saleItemSkuMap.keySet())
                .in(PurchaseContractItemDO::getPurchaseContractCode,purchaseContractCodeList));
        if (CollUtil.isEmpty(purchaseContractItemDOList)){
            return Map.of();
        }
        // 如果采购明细中没有销售的产品 则替换为默认报价的税率
        Map<String, BigDecimal> taxRateBySkuCodeMap = skuApi.getTaxRateBySkuCodeList(saleItemSkuMap.values());
        purchaseContractItemDOList.forEach(s->{
            String saleSkuCode = saleItemSkuMap.get(s.getSaleContractItemId());
            if (StrUtil.isEmpty(saleSkuCode)||saleSkuCode.equals(s.getSkuCode())){
               return;
            }
            BigDecimal taxRate = Objects.isNull(taxRateBySkuCodeMap.get(saleSkuCode)) ? BigDecimal.ZERO : taxRateBySkuCodeMap.get(saleSkuCode);
            s.setTaxRate(taxRate);
        });
        Map<Long, List<PurchaseContractItemDO>> purchaseItemMap = purchaseContractItemDOList.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getSaleContractItemId));
        if (CollUtil.isEmpty(purchaseItemMap)){
            return Map.of();
        }
        Map<Long, PriceQuantityEntity> result = new HashMap<>();
        purchaseItemMap.forEach((k,v)->{
            PriceQuantityEntity priceQuantityEntity= new PriceQuantityEntity();
            // 总价
            BigDecimal totalPrice = v.stream().map(PurchaseContractItemDO::getWithTaxPrice).map(JsonAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalWithTaxPriceRemoveFree = v.stream().map(s->{
                JsonAmount withTaxPrice = s.getWithTaxPrice();
                if (Objects.isNull(withTaxPrice)){
                    return BigDecimal.ZERO;
                }
                BigDecimal amount = withTaxPrice.getAmount();
                return NumUtil.div(NumUtil.mul(amount,NumUtil.sub(s.getQuantity(),s.getFreeQuantity())),s.getQuantity());
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            String currency = CalculationDict.CURRENCY_RMB;
            Optional<String> firstOpt = v.stream().map(PurchaseContractItemDO::getCurrency).findFirst();
            if (firstOpt.isPresent()){
                currency = firstOpt.get();
            }
            // 总数
            Integer totalQuantity = v.stream().map(PurchaseContractItemDO::getQuantity).reduce(0, Integer::sum);
            if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && totalQuantity > 0) {
                result.put(k, priceQuantityEntity.setQuantity(totalQuantity).setWithTaxPrice(new JsonAmount().setAmount(totalPrice).setCurrency(currency)).setWithTaxPriceRemoveFree(new JsonAmount().setAmount(totalWithTaxPriceRemoveFree).setCurrency(currency)));
            }
            //税率
            if(CollUtil.isNotEmpty(v)){
                Optional<BigDecimal> taxRateOpt = v.stream().map(PurchaseContractItemDO::getTaxRate).filter(Objects::nonNull).findFirst();
                if (taxRateOpt.isPresent()) {
                    priceQuantityEntity.setTaxRate(taxRateOpt.get());
                }else {
                    priceQuantityEntity.setTaxRate(BigDecimal.ZERO);
                }
            }else{
                priceQuantityEntity.setTaxRate(BigDecimal.ZERO);
            }
        });
        return result;
    }

    @Override
    public void updatePurchaseAmount(String businessCode, BigDecimal amount) {
       purchaseContractService.updatePurchaseAmount(businessCode,amount);
    }

    @Override
    public Map<Long, Integer> getPurchaseQuantityBySaleContractItemIds(List<Long> saleItemIdList) {
        List<PurchaseContractItemDO> purchaseContractItemDO = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleItemIdList);
        if(Objects.isNull(purchaseContractItemDO)){
            return null;
        }

        return purchaseContractItemDO.stream().collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId,PurchaseContractItemDO::getQuantity,(s1,s2)->s2));

    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, PurchaseContractItemDTO> getPurchaseContractMap(List<Long> saleItemIdList, Map<Long,Long> lastCompanyPathMap ) {
        return  purchaseContractService.getPurchaseContractMap(saleItemIdList,lastCompanyPathMap);
    }

    @Override
    public Map<Long, String> getPurchaseContractCodeBySaleItemIdList(List<Long> saleItemIdList, Long companyId) {
        return purchaseContractService.getPurchaseContractCodeBySaleItemIdList(saleItemIdList, companyId);
    }

    @Override
    public void updateCheckStatus(String purchaseContractCode, Integer value) {
        purchaseContractService.updateCheckStatus(purchaseContractCode,value);
    }

    public boolean completeOrderTask(CompleteOrderReqDTO completeOrderReq){
        completeOrderReq.setCheckStatus(false);
        return purchaseContractService.completeOrderTask(completeOrderReq);
    }

    @Override
    public Collection<Long> validatePurContractItemExists(Collection<Long> saleContractItemIds) {
        return purchaseContractService.validatePurContractItemExists(saleContractItemIds);
    }

    @Override
    public List<PurchaseContractDTO> getUnCompletedDTOBySaleContractCode(String saleContractCode) {
        List<Long> purchaseContractIds = purchaseContractService.getPurchaseContractIdsBySaleContractCode(saleContractCode);
        if (CollUtil.isEmpty(purchaseContractIds)) {
            return null;
        }
        return getUnCompletedDTODetail(purchaseContractIds);
    }

    @Override
    public Map<Long, Map<String, Integer>> getItemQuantityMapBySaleItemIds(List<Long> saleItemIds) {
        return purchaseContractService.getItemQuantityMapBySaleItemIds(saleItemIds);
    }

    @Override
    public Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds) {
        Map<Long, List<Integer>> result = new HashMap<>();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleItemIds);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return result;
        }
        List<String> contractCodeList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractCode).toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getCode, contractCodeList).eq(PurchaseContractDO::getAutoFlag,BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return result;
        }
        Map<String, Integer> purchaseContractMap = purchaseContractDOS.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getContractStatus));
        purchaseContractItemDOS.stream().filter(s-> purchaseContractMap.containsKey(s.getPurchaseContractCode())).forEach(s -> {
            Integer status = purchaseContractMap.get(s.getPurchaseContractCode());
            Integer cancelFlag = PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(status) ? BooleanEnum.YES.getValue() : BooleanEnum.NO.getValue();
            result.computeIfAbsent(s.getSaleContractItemId(), k -> new ArrayList<>()).add(cancelFlag);
        });
        return result;
    }

    @Override
    public void cancelPurchasePlanItem(List<Long> saleItemIdList,Collection<Long> saleItemIds) {
        if (CollUtil.isEmpty(saleItemIdList)){
            return;
        }
        purchasePlanService.cancelPurchasePlanItem(saleItemIdList,saleItemIds);
    }

    @Override
    @DataPermission(enable = false)
    public List<String> getEffectPurchaseCode(String saleContractCode, List<Long> itemIdList) {
        if (StrUtil.isEmpty(saleContractCode)){
            return List.of();
        }
        LambdaQueryWrapper<PurchaseContractItemDO> queryWrapper = new LambdaQueryWrapper<PurchaseContractItemDO>().eq(PurchaseContractItemDO::getSaleContractCode, saleContractCode);
        if (CollUtil.isNotEmpty(itemIdList)){
            queryWrapper.in(PurchaseContractItemDO::getSaleContractItemId, itemIdList);
        }
        List<PurchaseContractItemDO> purchaseItems = purchaseContractItemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(purchaseItems)){
            return List.of();
        }
        List<Long> purchaseContractIdList = purchaseItems.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        if (CollUtil.isNotEmpty(purchaseContractIdList)){
            List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getId, purchaseContractIdList);
            if (CollUtil.isNotEmpty(purchaseContractDOList)){
                List<String> codeList = purchaseContractDOList.stream().map(PurchaseContractDO::getCode).distinct().toList();
                return codeList;
            }
        }
        return List.of();
    }

    @Override
    public void updateInvoiceData(List<InvoiceQuantityDTO> invoiceQuantityDTO) {
        purchaseContractService.updateInvoiceData(invoiceQuantityDTO,false);
    }

    @Override
    public Map<Long, Map<String, Integer>> getCheckStatusMap(Set<Long> saleContractItemIds) {
        return purchaseContractService.getCheckStatusMap(saleContractItemIds);
    }

    @Override
    public List<Long> getItemIdsBySaleItems(Long saleContractItemIds) {
        if (Objects.isNull(saleContractItemIds)){
            return List.of();
        }
        List<PurchaseContractItemDO> contractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleContractItemIds);
        if (CollUtil.isEmpty(contractItemDOS)){
            return List.of();
        }
        return contractItemDOS.stream().map(PurchaseContractItemDO::getId).distinct().toList();
    }

    @Override
    public Map<String, Map<String, PurchaseContractItemDTO>> getPurchaseContractItemMap(List<String> purchaseContractCodeList) {
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCodeList);
        if (CollUtil.isEmpty(purchaseContractItemDOS)){
            return Map.of();
        }
        return purchaseContractItemDOS.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getPurchaseContractCode, Collectors.toMap(PurchaseContractItemDO::getSkuCode, s->BeanUtils.toBean(s, PurchaseContractItemDTO.class), (o, n) -> o)));
    }

    @Override
    public void deleteGenContract(String genContractUniqueCode) {
        purchaseContractMapper.delete(new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getGenContractUniqueCode, genContractUniqueCode).eq(PurchaseContractDO::getAutoFlag,BooleanEnum.YES.getValue()));
    }

    @Override
    public Long getPlanIdBySaleItemId(Long saleContractItemId) {
        if (Objects.isNull(saleContractItemId)){
            return null;
        }
        return purchasePlanService.getPlanIdBySaleItemId(saleContractItemId);
    }

    @Override
    public Map<String, String> getVenderCodeMapByPurchaseContractCodeList(Collection<String> purchaseContractCodeList) {
        if (CollUtil.isEmpty(purchaseContractCodeList)){
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getVenderCode).in(PurchaseContractDO::getCode, purchaseContractCodeList));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return Map.of();
        }
        return purchaseContractDOS.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getVenderCode));
    }

    @Override
    public Map<String, Long> getPurchaseCompanyMap(Collection<String> purchaseContractCodes) {
        if (CollUtil.isEmpty(purchaseContractCodes)){
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getCompanyId).in(PurchaseContractDO::getCode, purchaseContractCodes));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return Map.of();
        }
        return purchaseContractDOS.stream().filter(s->Objects.nonNull(s.getCompanyId())).collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getCompanyId));
    }

    @Override
    public Map<Long, Map<String,List<SimpleContractDetailDTO>>> getContractCodeMapByItemId(Collection<Long> saleItemIds) {
        if (CollUtil.isEmpty(saleItemIds)){
            return Map.of();
        }
        MPJLambdaWrapperX<PurchaseContractItemDO> queryWrapperX = new MPJLambdaWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getSaleContractItemId, saleItemIds);
        String subSql = "SELECT 1 FROM SCM_PURCHASE_CONTRACT spc WHERE spc.CODE = t.purchase_contract_code and spc.contract_status != 7";
        queryWrapperX.exists(subSql);
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemDOS)){
            return Map.of();
        }
        Set<String> purchaseContractCodes = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractCode).collect(Collectors.toSet());
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getVenderName,PurchaseContractDO::getAutoFlag).in(PurchaseContractDO::getCode, purchaseContractCodes).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        Map<String,String> venderNameMap = new HashMap<>();
        Map<String,Integer> autoFlagMap = new HashMap<>();
        if (CollUtil.isNotEmpty(purchaseContractDOS)){
            purchaseContractDOS.forEach(s->{
                venderNameMap.put(s.getCode(),s.getVenderName());
                autoFlagMap.put(s.getCode(),s.getAutoFlag());
            });
        }
        Map<Long, Map<String,List<SimpleContractDetailDTO>>> result = new HashMap<>();
        purchaseContractItemDOS.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getSaleContractItemId)).forEach((k,v)->{
            Map<String,List<SimpleContractDetailDTO>> map = new HashMap<>();
            if (CollUtil.isEmpty(v)){
                return;
            }
            v.forEach(s->{
                SimpleContractDetailDTO simpleContractDetailDTO = new SimpleContractDetailDTO();
                simpleContractDetailDTO.setPurchaseContractType(ShipmentContractTypeEnum.CURRENT_PURCHASE.getName());
                simpleContractDetailDTO.setContractCode(s.getPurchaseContractCode());
                simpleContractDetailDTO.setUsedQuantity(s.getQuantity());
                simpleContractDetailDTO.setInvoicedItemName(s.getSkuName());
                simpleContractDetailDTO.setInvoicingStatus(InvoiceStatusEnum.getNameByValue(s.getInvoiceStatus()));
                simpleContractDetailDTO.setInvoicedQuantity(s.getInvoicedQuantity());
                simpleContractDetailDTO.setSupplierName(venderNameMap.get(s.getPurchaseContractCode()));
                simpleContractDetailDTO.setAutoFlag(autoFlagMap.get(s.getPurchaseContractCode()));
                // 本次采购：从采购合同明细中获取含税单价
                if(s.getWithTaxPrice() != null){
                    simpleContractDetailDTO.setPrice(s.getWithTaxPrice());
                }
                List<SimpleContractDetailDTO> addList = new ArrayList<>();
                List<SimpleContractDetailDTO> simpleContractDetailDTOS = map.get(s.getSkuCode());
                if (CollUtil.isEmpty(simpleContractDetailDTOS)){
                    map.put(s.getSkuCode(), List.of(simpleContractDetailDTO));
                }else {
                    addList.add(simpleContractDetailDTO);
                    addList.addAll(simpleContractDetailDTOS);
                    map.put(s.getSkuCode(), addList);
                }
            });
            result.put(k,map);
        });
        return result;
    }

    @Override
    public void updatePurchaseInspectionData(Map<Long, UpdateInspectionDTO> updateInspectionDTOMap) {
        purchaseContractService.updatePurchaseInspectionData(updateInspectionDTOMap);
    }

    @Override
    public Map<Long, List<SimplePurchaseContractItemDTO>> getSimplePurchaseContractItemMap(List<String> saleColeList) {
        if (CollUtil.isEmpty(saleColeList)){
            return Map.of();
        }
        return purchaseContractService.getSimplePurchaseContractItemMap(saleColeList);
    }

    @Override
    public void updateAuxiliaryPaymentStatusByCodes(List<String> relationCode, Integer value) {
        purchaseContractService.updateAuxiliaryPaymentStatusByCodes(relationCode,value);
    }

    @Override
    public void updateAuxiliaryPurchaseAmount(String pay, JsonAmount amount) {
        purchaseContractService.updateAuxiliaryPurchaseAmount(pay,amount);
    }

    @Override
    public Map<String, List<UserDept>> getPurchaseUserListBySaleContractCodeSet(Set<String> saleContractCodeSet) {
        if (CollUtil.isEmpty(saleContractCodeSet)){
            return Map.of();
        }
        return purchaseContractService.getPurchaseUserListBySaleContractCodeSet(saleContractCodeSet);
    }

    @Override
    public Set<String> filterInvoicedPurchaseContractCode(Collection<String> purchaseCodeList) {
        return purchaseContractService.filterInvoicedPurchaseContractCode(purchaseCodeList);
    }

    @Override
    public void updateLinkCodeList(Map<String, String> linkCodeMap) {
        purchaseContractService.updateLinkCodeList(linkCodeMap);
    }

    @Override
    public Map<Long, PurchaseContractItemDTO> getItemMapByItemIds(List<Long> purchaseItemIds) {
         List<PurchaseContractItemDO> items = purchaseContractService.getItemMapByItemIds(purchaseItemIds);
        if(CollUtil.isEmpty(items)){
            return null;
        }
        List<PurchaseContractItemDTO> dots = BeanUtils.toBean(items, PurchaseContractItemDTO.class);
        return dots.stream().collect(Collectors.toMap(PurchaseContractItemDTO::getId,s->s,(s1,s2)->s1));

    }

    @Override
    public Map<Long, JsonAmount> getiPurchasePricCache(Collection<String> purchaseCodeList) {
        return purchaseContractService.getiPurchasePricCache(purchaseCodeList);
    }

    @Override
    public void rollBackPurchaseNoticeQuantity(Map<String, Map<String, Integer>> updateMap) {
        purchaseContractService.rollBackPurchaseNoticeQuantity(updateMap);
    }

    private List<PurchaseContractDTO> getUnCompletedDTODetail(List<Long> purchaseContractIds) {
        Integer[] statusList = {
                PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode(),
                PurchaseContractStatusEnum.AWAITING_APPROVAL.getCode(),
                PurchaseContractStatusEnum.REJECTED.getCode(),
                PurchaseContractStatusEnum.AWAITING_ORDER.getCode(),
                PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode()
        };
        PurchaseContractPageReqVO purchaseContractPageReqVO = new PurchaseContractPageReqVO();
        purchaseContractPageReqVO.setPageNo(0).setPageSize(-1);
        purchaseContractPageReqVO.setIdList(purchaseContractIds.stream().toArray(Long[]::new));
        purchaseContractPageReqVO.setContractStatusList(statusList);
        PageResult<PurchaseContractInfoRespVO> purchaseContractInfoPage = purchaseContractService.getPurchaseContractPage(purchaseContractPageReqVO);
        if (purchaseContractInfoPage == null) {
            return null;
        }
        List<PurchaseContractInfoRespVO> doList = purchaseContractInfoPage.getList();
        List<PurchaseContractDTO> dtoList = BeanUtils.toBean(doList, PurchaseContractDTO.class);
        return dtoList;
    }
}
