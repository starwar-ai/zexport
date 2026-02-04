package com.syj.eplus.module.scm.service.purchasecontractitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CurrencyUtil;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryContractItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseBillSimpleDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.convert.PurchaseContractItemConvert;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.dal.mysql.purchasecontract.PurchaseContractMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.service.vender.VenderService;
import com.syj.eplus.module.sms.api.SaleAuxiliaryAllocationApi;
import com.syj.eplus.module.sms.api.dto.SaleAuxiliaryAllocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.PURCHASE_CONTRACT_ITEM_NOT_EXISTS;

/**
 * 采购合同明细 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class PurchaseContractItemServiceImpl extends ServiceImpl<PurchaseContractItemMapper, PurchaseContractItemDO> implements PurchaseContractItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PurchaseContractItemMapper purchaseContractItemMapper;

    @Resource
    private PurchaseContractMapper purchaseContractMapper;

    @Resource
    private VenderApi venderApi;

    @Resource
    private SkuApi skuApi;

    @Resource
    private VenderService venderService;

    @Resource
    private SaleAuxiliaryAllocationApi saleAuxiliaryAllocationApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private RateApi rateApi;
    private static final String PROCESS_DEFINITION_KEY = "${table.processKey}";


    @Override
    public Long createPurchaseContractItem(PurchaseContractItemSaveReqVO createReqVO) {
        createReqVO.setCheckStatus(0);

        PurchaseContractItemDO purchaseContractItem = PurchaseContractItemConvert.INSTANCE.convertPurchaseContractItemDO(createReqVO);
        // 插入
        purchaseContractItemMapper.insert(purchaseContractItem);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(purchaseContractItem.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return purchaseContractItem.getId();
    }

    @Override
    public void updatePurchaseContractItem(PurchaseContractItemSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseContractItemExists(updateReqVO.getId());
        // 更新
        PurchaseContractItemDO updateObj = PurchaseContractItemConvert.INSTANCE.convertPurchaseContractItemDO(updateReqVO);
        purchaseContractItemMapper.updateById(updateObj);
    }

    @Override
    public void deletePurchaseContractItem(Long id) {
        // 校验存在
        validatePurchaseContractItemExists(id);
        // 删除
        purchaseContractItemMapper.deleteById(id);
    }

    private void validatePurchaseContractItemExists(Long id) {
        if (purchaseContractItemMapper.selectById(id) == null) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long purchaseContractItemId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, purchaseContractItemId);
        updateAuditStatus(purchaseContractItemId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        purchaseContractItemMapper.updateById(PurchaseContractItemDO.builder().id(auditableId).build());
    }

    @Override
    public void batchSavePlanItem(List<PurchaseContractItemSaveReqVO> itemList) {
        if (CollUtil.isEmpty(itemList))
            return;
        List<Long> skuIdList = itemList.stream().map(PurchaseContractItemSaveReqVO::getSkuId).distinct().toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
        // 获取汇率用于计算含税总价（人民币）
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        itemList.forEach(item -> {
            item.setVer(1);
            item.setCheckStatus(BooleanEnum.NO.getValue());
            item.setReceiveStatus(BooleanEnum.NO.getValue());
            item.setId(null);
            item.setSyncQuoteFlag(0);
            item.setReceivedQuantity(0);
            item.setReturnQuantity(0);
            item.setCheckedQuantity(0);
            item.setExchangeQuantity(0);
            item.setPurchaseType(PurchaseTypeEnum.STANDARD.getCode());
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)){
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuId());
                item.setDescription(Objects.isNull(simpleSkuDTO)? CommonDict.EMPTY_STR:simpleSkuDTO.getDescription());
            }
            if (Objects.nonNull(item.getWithTaxPrice())) {
                if( Objects.nonNull(item.getTaxRate())){
                    BigDecimal unitAmount = item.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(100)).divide(item.getTaxRate().add(BigDecimal.valueOf(100)),2, RoundingMode.HALF_UP);
                    item.setUnitPrice(new JsonAmount().setCurrency(item.getWithTaxPrice().getCurrency()).setAmount(unitAmount));
                }
                if (Objects.nonNull(item.getPackagingPrice())) {
                    BigDecimal totalAmount = item.getWithTaxPrice().getAmount().add(item.getPackagingPrice().getAmount());
                    item.setTotalPrice(new JsonAmount().setCurrency(item.getWithTaxPrice().getCurrency()).setAmount(totalAmount));
                }else {
                    item.setTotalPrice(item.getWithTaxPrice());
                }
                // 计算含税总价（人民币） = 含税单价 * 数量，并转换为RMB
                if (Objects.nonNull(item.getQuantity()) && item.getQuantity() > 0) {
                    try {
                        // 计算含税总价（原币种）
                        BigDecimal totalPriceAmount = item.getWithTaxPrice().getAmount().multiply(new BigDecimal(item.getQuantity()));
                        JsonAmount totalPriceOriginal = new JsonAmount()
                            .setCurrency(item.getWithTaxPrice().getCurrency())
                            .setAmount(totalPriceAmount);
                        // 转换为RMB
                        JsonAmount totalPriceRmb = CurrencyUtil.changeCurrency(
                            totalPriceOriginal,
                            CalculationDict.CURRENCY_RMB,
                            dailyRateMap
                        );
                        item.setTotalPriceRmb(totalPriceRmb);
                    } catch (Exception e) {
                        logger.error("计算采购明细含税总价RMB失败, 产品编号: {}, 错误: {}", item.getSkuCode(), e.getMessage());
                    }
                }
            }
            var itemDO = BeanUtils.toBean(item, PurchaseContractItemDO.class);
            purchaseContractItemMapper.insert(itemDO);
        });
    }

    @Override
    public List<PurchaseContractItemDO> getPurchaseContractItemListByPurchaseContractId(Long id) {
        return purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().eq(PurchaseContractItemDO::getPurchaseContractId, id));
    }

    @Override
    public List<PurchaseContractItemDO> getPurchaseContractItemListByPurchaseContractId(List<Long> idList) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(
                new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, idList)
        );
        if (CollUtil.isEmpty(itemDOList))
            return null;
        return PurchaseContractItemConvert.INSTANCE.convertPurchaseContractItemListRespVO(itemDOList);
    }

    @Override
    public void deletePurchaseContractItemListByPurchaseContractId(Long id) {
        purchaseContractItemMapper.delete(PurchaseContractItemDO::getPurchaseContractId, id);
    }

    @Override
    public List<PurchaseContractItemRespVO> getPurchaseContractItemListByPurchaseContractIdList(List<Long> idList) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(
                new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, idList));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return PurchaseContractItemConvert.INSTANCE.convertRespVOList(itemDOList);
    }

    @Override
    public List<PurchaseContractItemRespVO> getContractItemListByContractCodeList(List<String> parentCodeList) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(
                new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractCode, parentCodeList));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return PurchaseContractItemConvert.INSTANCE.convertRespVOList(itemDOList);
    }

    @Override
    public PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO reqDTO) {
        PageResult<PurchaseContractItemDTO> result = new PageResult<>();
        List<VenderDO> venderDOList = venderService.getSimpleListByCodeAndName(reqDTO.getVenderName());
        List<String> venderCodeList = venderDOList.stream().map(VenderDO::getCode).distinct().toList();
        reqDTO.setVenderCodeList(venderCodeList);
        PageResult<PurchaseContractItemDO> purchaseContractItemDOPageResult = purchaseContractItemMapper.selectPage(reqDTO);
        List<PurchaseContractItemDO> list = purchaseContractItemDOPageResult.getList();
        if (CollUtil.isEmpty(list)){
            return PageResult.empty();
        }
        Map<String, VenderDO> venderNameCache = venderDOList.stream().collect(Collectors.toMap(VenderDO::getCode, s -> s, (s1, s2) -> s1));
        Map<String, SkuNameDTO> skuNameCache = skuApi.getSkuNameCacheByCodeList(List.of(reqDTO.getSkuCode()));
        List<PurchaseContractItemDTO> itemDTOS = PurchaseContractItemConvert.INSTANCE.convertPurchaseContractItemDTOList(list, venderNameCache, skuNameCache);
        return result.setList(itemDTOS).setTotal(purchaseContractItemDOPageResult.getTotal());
    }

    @Override
    public Map<Long, PurchaseContractItemDO> getByIdList(List<Long> list) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, list);
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return itemDOList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getId, s -> s, (s1, s2) -> s1));
    }

    @Override
    public List<AuxiliaryContractItemDTO> getAuxiliaryContractItemListBySaleCode(String code) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .eq(PurchaseContractItemDO::getAuxiliarySaleContractCode, code)
                .eq(PurchaseContractItemDO::getAuxiliarySkuType, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        Map<Long, PurchaseContractDO> doMap = purchaseContractMapper.selectList(PurchaseContractDO::getId,
                        itemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList())
                .stream().collect(Collectors.toMap(PurchaseContractDO::getId, s -> s, (s1, s2) -> s1));
        if (CollUtil.isEmpty(doMap)) {
            return null;
        }
        List<AuxiliaryContractItemDTO> dtoList = new ArrayList<>();
        //处理skuName
        List<String> skuCodeList = itemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuNameDTO> skuMap = skuApi.getSkuNameCacheByCodeList(skuCodeList);
        //处理venderName
        List<Long> venderIdList = itemDOList.stream().map(PurchaseContractItemDO::getVenderId).distinct().toList();
        Map<Long, SimpleVenderRespDTO> venderMap = venderApi.getSimpleVenderRespDTOMap(venderIdList);
        //查询分摊列表数据
        Map<Long, List<SaleAuxiliaryAllocationDTO>> allocationDTOMap = saleAuxiliaryAllocationApi.getListBySaleCode(code);
        itemDOList.forEach(s -> {
            //过滤审核通过的数据
            PurchaseContractDO purchaseContractDO = doMap.get(s.getPurchaseContractId());
            if (Objects.isNull(purchaseContractDO) || !Objects.equals(purchaseContractDO.getAuditStatus(), BpmProcessInstanceResultEnum.APPROVE.getResult())) {
                return;
            }
            AuxiliaryContractItemDTO dto = BeanUtils.toBean(s, AuxiliaryContractItemDTO.class);
            dto.setContractItemId(s.getId());
            dto.setContractId(s.getPurchaseContractId()).setContractCode(s.getPurchaseContractCode());
            //设置产品
            if (CollUtil.isNotEmpty(skuMap)) {
                SkuNameDTO skuNameDTO = skuMap.get(s.getSkuCode());
                if (Objects.nonNull(skuNameDTO)){
                    dto.setSkuName(skuNameDTO.getName());
                }
            }
            //设置供应商
            if (CollUtil.isNotEmpty(venderMap)) {
                SimpleVenderRespDTO simpleVenderRespDTO = venderMap.get(s.getVenderId());
                if (Objects.nonNull(simpleVenderRespDTO)) {
                    dto.setVenderName(simpleVenderRespDTO.getName());
                }
            }
            //设置分摊列表
            if(CollUtil.isNotEmpty(allocationDTOMap)){
                List<SaleAuxiliaryAllocationDTO> allocationList = allocationDTOMap.get(s.getId());
                if(CollUtil.isNotEmpty(allocationList)){
                    dto.setAllocationFlag(BooleanEnum.YES.getValue());
                    dto.setAllocationDTOList(allocationList);
                } else {//该明细没有数据，设置为未分摊
                    dto.setAllocationFlag(BooleanEnum.NO.getValue());
                }
            } else { //整个合同没有数据，设置为未分摊
                dto.setAllocationFlag(BooleanEnum.NO.getValue());
            }
            dtoList.add(dto);
        });
        return dtoList;
    }

    @Override
    public void batchUpdateBillStatus(List<PurchaseBillSimpleDTO> billSimpleDTOList) {
        if (CollUtil.isEmpty(billSimpleDTOList)) {
            return;
        }
        List<String> list = billSimpleDTOList.stream().map(PurchaseBillSimpleDTO::getUniqueCode).toList();
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getUniqueCode, list);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        Map<String, PurchaseContractItemDO> itemMap = purchaseContractItemDOList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getUniqueCode, s -> s));
        List<PurchaseContractItemDO> updateItems = billSimpleDTOList.stream().map(s -> {
            PurchaseContractItemDO purchaseContractItemDO = itemMap.get(s.getUniqueCode());
            if (Objects.isNull(purchaseContractItemDO)) {
                throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
            }
            // 已入库数量
            Integer billQuantity = purchaseContractItemDO.getBillQuantity();
            // 采购数量
            Integer quantity = purchaseContractItemDO.getQuantity();
            // 本次入库数量
            Integer thisBillQuantity = s.getQuantity();
            if (quantity.equals(billQuantity)) {
                purchaseContractItemDO.setBillStatus(BillStatusEnum.BILLED.getStatus());
            }
            billQuantity += thisBillQuantity;
            if (quantity.equals(billQuantity)) {
                purchaseContractItemDO.setBillStatus(BillStatusEnum.BILLED.getStatus());
            } else {
                purchaseContractItemDO.setBillStatus(BillStatusEnum.PART_BILL.getStatus());
            }
            purchaseContractItemDO.setAbnormalStatus(s.getAbnormalStatus());
            purchaseContractItemDO.setAbnormalRemark(s.getAbnormalRemark());
            return purchaseContractItemDO;
        }).toList();
        purchaseContractItemMapper.updateBatch(updateItems);
    }

    @Override
    public List<PurchaseContractItemDO> getItemDOListByIds(Set<Long> saleItemIds) {
        if (CollUtil.isEmpty(saleItemIds)){
            return List.of();
        }
        return purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId,saleItemIds);
    }

    @Override
    @DataPermission(enable = false)
    public List<Long> validateSplitSales(List<Long> saleItemIds) {
        if (CollUtil.isEmpty(saleItemIds)){
            return List.of();
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleItemIds);
        if (CollUtil.isEmpty(purchaseContractItemDOS)){
            return List.of();
        }
        List<Long> purchaseContractIdList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getId).in(PurchaseContractDO::getId, purchaseContractIdList).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        return purchaseContractItemDOS.stream().filter(s->{
            if (CollUtil.isEmpty(purchaseContractDOS)){
                return false;
            }else {
                List<Long> filterContractIdList = purchaseContractDOS.stream().map(PurchaseContractDO::getId).distinct().toList();
                if (CollUtil.isEmpty(filterContractIdList)){
                    return false;
                }
                return filterContractIdList.contains(s.getPurchaseContractId());
            }
        }).map(PurchaseContractItemDO::getSaleContractItemId).collect(Collectors.toList());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public PurchaseContractItemRespVO getPurchaseContractItem(PurchaseContractItemDetailReq purchaseContractItemDetailReq) {
        Long purchaseContractItemId = Objects.nonNull(purchaseContractItemDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchaseContractItemDetailReq.getProcessInstanceId()) : purchaseContractItemDetailReq.getPurchaseContractItemId();
        if (Objects.isNull(purchaseContractItemId)) {
            logger.error("[采购合同明细]未获取到采购合同明细id");
            return null;
        }
        PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemMapper.selectById(purchaseContractItemId);
        if (purchaseContractItemDO == null) {
            return null;
        }
        return PurchaseContractItemConvert.INSTANCE.convert(purchaseContractItemDO);
    }

    @Override
    public PageResult<PurchaseContractItemDO> getPurchaseContractItemPage(PurchaseContractGetItemPageReqDTO pageReqVO) {
        return purchaseContractItemMapper.selectPage(pageReqVO);
    }

}
