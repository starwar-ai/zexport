package com.syj.eplus.module.fms.service.custclaim;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustResp;
import com.syj.eplus.module.fms.api.payment.api.custclaim.dto.CustClaimDTO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.SimpleRegistrationResp;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimPageReq;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimSaveReqVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.convert.custclaim.CustClaimConvert;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.PayeeEntity;
import com.syj.eplus.module.fms.dal.dataobject.receipt.ReceiptDO;
import com.syj.eplus.module.fms.dal.mysql.bankregistration.BankRegistrationMapper;
import com.syj.eplus.module.fms.dal.mysql.custclaim.CustClaimItemMapper;
import com.syj.eplus.module.fms.dal.mysql.payee.PayeeMapper;
import com.syj.eplus.module.fms.service.receipt.ReceiptService;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.WriteBackDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.fms.api.payment.enums.ErrorCodeConstants.BANK_REGISTRATION_NOT_EXISTS;
import static com.syj.eplus.module.fms.api.payment.enums.ErrorCodeConstants.RECEIPT_NOT_EXISTS;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 15:41
 */
@Service
public class CustClaimServiceImpl implements CustClaimService {

    @Resource
    private PayeeMapper payeeMapper;

    @Resource
    private BankRegistrationMapper bankRegistrationMapper;

    @Resource
    private CustApi custApi;

    @Resource
    private CustClaimItemMapper custClaimItemMapper;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private ReceiptService receiptService;

    @Resource
    private RateApi rateApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private OrderLinkApi orderLinkApi;
    @Override
    public PageResult<SimpleRegistrationResp> getCustClaimList(CustClaimPageReq custClaimPageReq) {
        Integer pageNo = custClaimPageReq.getPageNo();
        Integer pageSize = custClaimPageReq.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        List<SimpleRegistrationResp> custClaimList = bankRegistrationMapper.getCustRegistration(pageSize, skip, custClaimPageReq.getCustName(), custClaimPageReq.getCode(), custClaimPageReq.getCompanyTitle(),custClaimPageReq.getCreator(),custClaimPageReq.getCustCode(),custClaimPageReq.getManagerId(),custClaimPageReq.getCompanyId(),custClaimPageReq.getAmountMin(),custClaimPageReq.getAmountMax(),custClaimPageReq.getClaimStatus());
        if (CollUtil.isEmpty(custClaimList)) {
            return null;
        }
        Long count = bankRegistrationMapper.getCustRegistrationCount(custClaimPageReq.getCustName(), custClaimPageReq.getCode(), custClaimPageReq.getCompanyTitle(),custClaimPageReq.getCreator(),custClaimPageReq.getCustCode(),custClaimPageReq.getManagerId(),custClaimPageReq.getCompanyId(),custClaimPageReq.getAmountMin(),custClaimPageReq.getAmountMax(),custClaimPageReq.getClaimStatus());
        return new PageResult<SimpleRegistrationResp>().setList(custClaimList).setTotal(count);

    }

    @Override
    public SimpleRegistrationResp getCustClaim(Long id) {
        BankRegistrationDO bankRegistrationDO = bankRegistrationMapper.selectById(id);
        if (Objects.isNull(bankRegistrationDO)) {
            throw exception(BANK_REGISTRATION_NOT_EXISTS);
        }
        SimpleRegistrationResp simpleRegistrationResp = BeanUtils.toBean(bankRegistrationDO, SimpleRegistrationResp.class);
        simpleRegistrationResp.setUnclaimedAmount(NumUtil.sub(simpleRegistrationResp.getAmount(), simpleRegistrationResp.getClaimedAmount()));
        // 获取抬头
        String companyTitle = bankRegistrationDO.getCompanyTitle();
        // 通过抬头查找下单客户
        List<SimpleCustResp> simpleCustRespList = custApi.getSimpleCustRespByBankPoc(companyTitle);
        if (CollUtil.isEmpty(simpleCustRespList)) {
            return simpleRegistrationResp;
        }
        // 查找当前登记单是否有过认领记录 回显客户认领金额
        List<PayeeEntity> payeeEntities = payeeMapper.selectList(PayeeEntity::getRegistrationId, id);
        List<PayeeEntity> payeeEntityResult = transformSimpleCustRespList(simpleCustRespList, payeeEntities, id);
        simpleRegistrationResp.setPayeeEntityList(payeeEntityResult);
        List<String> custCodeList = payeeEntityResult.stream().map(PayeeEntity::getPayeeCode).toList();
        // 查找认领明细
        List<CustClaimItem> custClaimItemList = custClaimItemMapper.getCustClaimItemList(custCodeList,bankRegistrationDO.getCompanyId());
        if (CollUtil.isEmpty(custClaimItemList)){
            return simpleRegistrationResp;
        }
        //程波20251020 注释，收款认领的时候不需要根据出运明细来拆分
//        List<String> contractCodeList = custClaimItemList.stream().map(CustClaimItem::getContractCode).distinct().toList();
        // 根据出运发票号及出库单号拆分
//        Map<String, List<ShipmentPriceEntity>> shipmentPriceMap = saleContractApi.getShipmentPriceBySaleContractCode(contractCodeList);
//        custClaimItemList = splitCustClaimItem(custClaimItemList,shipmentPriceMap);
//        List<CustClaimItem> custClaimItems = custClaimItemMapper.selectList(CustClaimItem::getContractCode, contractCodeList);
//        if (CollUtil.isEmpty(custClaimItems)){
//            simpleRegistrationResp.setCustClaimItemList(custClaimItemList);
//            return simpleRegistrationResp;
//        }
//        Map<String, List<DifferenceReason>> hisDifferenceReasonMap = custClaimItems.stream()
//                .filter(item -> CollUtil.isNotEmpty(item.getDifferenceReason()))
//                .collect(Collectors.groupingBy(
//                        CustClaimItem::getContractCode,
//                        Collectors.flatMapping(
//                                item -> item.getDifferenceReason().stream(),
//                                Collectors.toList()
//                        )
//                ));
//        custClaimItemList.forEach(s->{
//            List<DifferenceReason> differenceReasons = hisDifferenceReasonMap.get(s.getContractCode());
//            s.setHisDifferenceReason(differenceReasons);
//        });
//        if (CollUtil.isNotEmpty(custClaimItemList)){
//            custClaimItemList.sort(Comparator
//                    .comparing(CustClaimItem::getCreateTime)
//                    .thenComparing(CustClaimItem::getContractCode)
//                    .thenComparingInt(CustClaimItem::getSourceType)
//            );
//        }
        simpleRegistrationResp.setCustClaimItemList(custClaimItemList);
        return simpleRegistrationResp;
    }

    private List<CustClaimItem> splitCustClaimItem(List<CustClaimItem> custClaimItems,Map<String, List<ShipmentPriceEntity>> shipmentPriceMap){
        List<CustClaimItem> result = new ArrayList<>();
        if (CollUtil.isEmpty(custClaimItems)){
            return List.of();
        }
        List<Long> collectIdList = custClaimItems.stream().map(CustClaimItem::getItemId).distinct().toList();
        Map<Long, Map<String, JsonAmount>> collectionCalimAmountMap = saleContractApi.getCollectionCalimAmountMap(collectIdList);
        if (CollUtil.isEmpty(shipmentPriceMap)){
            custClaimItems.stream().filter(s -> CollectionPlanStepEnum.ADVAMCE_RECEIPT.getStep().equals(s.getSourceType())||s.getSourceType()==0||StrUtil.isNotEmpty(s.getInvoiceCode()))
                    .forEach(result::add);
            return result;
        }
        List<CustClaimItem> claimItems = new ArrayList<>();
        custClaimItems.forEach(s->{
            Integer sourceType = s.getSourceType();
            if (CollectionPlanStepEnum.ADVAMCE_RECEIPT.getStep().equals(sourceType)||sourceType == 0){
                claimItems.add(s);
                return;
            }
            List<ShipmentPriceEntity> shipmentPriceEntities = shipmentPriceMap.get(s.getContractCode());
            if (CollUtil.isEmpty(shipmentPriceEntities)){
                claimItems.add(s);
                return;
            }
            BigDecimal collectionRatio = s.getCollectionRatio();
            Map<String, JsonAmount> invoiceCalimMap = collectionCalimAmountMap.get(s.getItemId());
            shipmentPriceEntities.forEach(entity->{
                String invoiceCode = entity.getInvoiceCode();
                JsonAmount receivablePriceJson = entity.getReceivablePrice();
                if (StrUtil.isEmpty(invoiceCode)){
                    claimItems.add(s);
                    return;
                }
                CustClaimItem custClaimItem = BeanUtil.copyProperties(s, CustClaimItem.class);
                custClaimItem.setReceivedAmount(BigDecimal.ZERO);
                BigDecimal receivablePrice = Objects.isNull(receivablePriceJson.getAmount())?BigDecimal.ZERO:receivablePriceJson.getAmount();
                if (BigDecimal.ZERO.compareTo(collectionRatio) < 0){
                    receivablePrice = NumUtil.mul(receivablePrice, NumUtil.div(collectionRatio, CalculationDict.ONE_HUNDRED));
                }
                custClaimItem.setReceivableAmount(receivablePrice);
                if (CollUtil.isNotEmpty(invoiceCalimMap)){
                    JsonAmount receivedAmount = invoiceCalimMap.get(entity.getInvoiceCode());
                    if (Objects.nonNull(receivedAmount)){
                        custClaimItem.setReceivedAmount(receivedAmount.getAmount());
                        custClaimItem.setCompletedFlag(entity.getCompletedFlag());
                        return;
                    }
                }
                custClaimItem.setInvoiceCode(invoiceCode);
                custClaimItem.setCreateTime(s.getCreateTime());
                claimItems.add(custClaimItem);
            });
        });
        claimItems.stream().filter(s -> CollectionPlanStepEnum.ADVAMCE_RECEIPT.getStep().equals(s.getSourceType())||s.getSourceType() == 0||StrUtil.isNotEmpty(s.getInvoiceCode()))
                .forEach(result::add);
        return result;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCustClaim(CustClaimSaveReqVO custClaimSaveReqVO) {
        // 插入收款对象
        Long registrationId = custClaimSaveReqVO.getId();
        // 收款对象信息
        List<PayeeEntity> payeeEntityList = custClaimSaveReqVO.getPayeeEntityList();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept user = adminUserApi.getUserDeptByUserId(loginUserId);
        payeeEntityList.forEach(s -> {
            s.setId(null);
            s.setRegistrationId(registrationId);
        });
        payeeMapper.insertBatch(payeeEntityList);
        // 根据抬头获取客户编号
        List<String> custCodeList = payeeMapper.getNoCustCodeListByCompanyTitle(custClaimSaveReqVO.getCompanyTitle());
        // 筛选不存在当前抬头的客户编号
        Map<String,Long> notExistCustCodeMap = payeeEntityList.stream()
                .filter(s -> {
                    if (CollUtil.isNotEmpty(custCodeList)) {
                        return !custCodeList.contains(s.getPayeeCode());
                    }
                    return true;
                }).collect(Collectors.toMap(PayeeEntity::getPayeeCode, PayeeEntity::getId, (o, n) -> n));
        if (CollUtil.isNotEmpty(notExistCustCodeMap)) {
            // 回写抬头
            custApi.createCustTitle(notExistCustCodeMap, custClaimSaveReqVO.getCompanyTitle());
        }
        // 认领明细
        List<CustClaimItem> custClaimItemList = custClaimSaveReqVO.getCustClaimItemList();
        List<String> contractCodeList = custClaimItemList.stream().map(CustClaimItem::getContractCode).toList();
        Map<String, List<String>> linkCodeMap = saleContractApi.getLinkCodeMap(contractCodeList);
        custClaimItemList.forEach(s -> {
            s.setClaimPerson(user);
            s.setClaimDate(LocalDateTime.now());
            s.setRegistrationId(registrationId);
        });
        custClaimItemMapper.insertBatch(custClaimItemList);
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        // 回写收款计划跟加减项
        List<WriteBackDTO> writeBackDTOList = custClaimItemList.stream().filter(s->Objects.nonNull(s.getSourceType())).map(s -> new WriteBackDTO().setCurrency(s.getCurrency())
                .setCompletedFlag(s.getCompletedFlag())
                .setItemId(s.getItemId())
                .setSourceType(s.getSourceType())
                .setDifferenceReason(s.getDifferenceReason())
                .setCollectionPlanItem(new CollectionPlanItem().setCustClaimItemId(s.getId()).setDifferenceReason(s.getDifferenceReason()).setCompletedFlag(s.getCompletedFlag()).setSettlementCode(s.getReceiptCode()).setAmount(new JsonAmount().setAmount(s.getClaimedAmount()).setCurrency(s.getCurrency())).setInvoiceCode(s.getInvoiceCode()).setUser(s.getClaimPerson()).setDate(LocalDateTime.now()))
                .setContractAmount(NumUtil.add(s.getContractAmount(),s.getDifferenceAmount(),NumUtil.mul(NumUtil.div(Objects.isNull(s.getFinanceAmount())?BigDecimal.ZERO:Objects.isNull(s.getFinanceAmount().getAmount())?BigDecimal.ZERO:s.getFinanceAmount().getAmount(),dailyRateMap.get(s.getFinanceAmount().getCurrency())),dailyRateMap.get(s.getCurrency()))))).toList();
        saleContractApi.writeBackContract(writeBackDTOList,false);
        // 创建收款单
        List<ReceiptSaveReqVO> receiptSaveReqVOList = new ArrayList<>();
        payeeEntityList.forEach(s -> {
            ReceiptSaveReqVO receiptSaveReqVO = new ReceiptSaveReqVO().setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                    .setCompanyId(custClaimSaveReqVO.getCompanyId())
                    .setAmount(new JsonAmount().setAmount(s.getClaimTotalAmount()).setCurrency(s.getCurrency()))
                    .setRate(new JsonAmount().setAmount(dailyRateMap.get(s.getCurrency())).setCurrency(s.getCurrency()))
                    .setReceiptTime(LocalDateTime.now())
                    .setReceiptUser(custClaimSaveReqVO.getRegisteredBy())
                    .setBusinessType(BusinessTypeEnum.CLAIM.getCode())
                    .setBusinessCode(String.valueOf(custClaimSaveReqVO.getId()))
                    .setBusinessSubjectType(BusinessSubjectTypeEnum.CUST.getCode())
                    .setBusinessSubjectCode(s.getPayeeCode())
                    .setStatus(BooleanEnum.YES.getValue());
            receiptSaveReqVOList.add(receiptSaveReqVO);
        });
        List<ReceiptDO> receiptDOS = receiptService.batchCreateReceipt(receiptSaveReqVOList);
        if(CollUtil.isEmpty(receiptDOS)){
            throw exception(RECEIPT_NOT_EXISTS);
        }

        //回写收款单编号
        custClaimItemList.forEach(s->{
            Optional<ReceiptDO> first = receiptDOS.stream().filter(c -> Objects.equals(c.getBusinessSubjectCode(), s.getCustCode())).findFirst();
            first.ifPresent(receiptDO -> s.setReceiptCode(receiptDO.getCode()));
        });
        custClaimItemMapper.updateBatch(custClaimItemList);

        // 关联收款信息
        Map<String, ReceiptSaveReqVO> receiptSaveReqVOMap = receiptSaveReqVOList.stream().collect(Collectors.toMap(ReceiptSaveReqVO::getBusinessSubjectCode, s -> s, (o, n) -> n));
        List<OrderLinkDTO> orderLinkDTOList = custClaimItemList.stream().filter(s->Objects.nonNull(s.getContractCode())).map(s -> {
            {
                List<String> linkCodeList = linkCodeMap.get(s.getContractCode());
                if (CollUtil.isNotEmpty(linkCodeList)) {
                    return createOrderLink(s, linkCodeList, receiptSaveReqVOMap);
                }
                return null;
            }
        }).filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
        orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        // 回写登记单已认领金额
        writeBackRegistration(custClaimItemList,registrationId,payeeEntityList,false);

    }

    private List<OrderLinkDTO> createOrderLink(CustClaimItem custClaimItem, List<String> linkCodeList,Map<String, ReceiptSaveReqVO> receiptSaveReqVOMap) {
        ReceiptSaveReqVO receiptSaveReqVO = receiptSaveReqVOMap.get(custClaimItem.getCustCode());
        return linkCodeList.stream().map(s -> new OrderLinkDTO()
                .setBusinessId(receiptSaveReqVO.getId())
                .setCode(receiptSaveReqVO.getCode())
                .setName(BusinessNameDict.RECEIPT_ORDER)
                .setType(OrderLinkTypeEnum.RECEIPT_ORDER.getValue())
                .setLinkCode(s)
                .setItem(List.of(custClaimItem))
                .setParentCode(CommonDict.HYPHEN)
                .setStatus(receiptSaveReqVO.getStatus())
                .setCompanyId(receiptSaveReqVO.getCompanyId())
                .setBusinessSubjectName(custClaimItem.getCustCode())
                .setOrderMsg(custClaimItem)).toList();

    }
    @Override
    public SimpleRegistrationResp getCustClaimDetail(Long id) {
        BankRegistrationDO bankRegistrationDO = bankRegistrationMapper.selectById(id);
        if (Objects.isNull(bankRegistrationDO)) {
            throw exception(BANK_REGISTRATION_NOT_EXISTS);
        }
        SimpleRegistrationResp simpleRegistrationResp = BeanUtils.toBean(bankRegistrationDO, SimpleRegistrationResp.class);
        simpleRegistrationResp.setUnclaimedAmount(NumUtil.sub(simpleRegistrationResp.getAmount(), simpleRegistrationResp.getClaimedAmount()));
        List<PayeeEntity> payeeEntities = payeeMapper.selectList(PayeeEntity::getRegistrationId, id);
        simpleRegistrationResp.setPayeeEntityList(payeeEntities);
        List<CustClaimItem> custClaimItems = custClaimItemMapper.selectList(CustClaimItem::getRegistrationId, id);
        simpleRegistrationResp.setCustClaimItemList(custClaimItems);
        return simpleRegistrationResp;
    }

    @Override
    public List<CustClaimItem> getCustClaimItemList(List<String> custCodes,Long companyId) {
        List<CustClaimItem> custClaimItemList = custClaimItemMapper.getCustClaimItemList(custCodes, companyId);
        //程波20251020 注释，收款认领的时候不需要根据出运明细来拆分
//        List<String> contractCodeList = custClaimItemList.stream().map(CustClaimItem::getContractCode).distinct().toList();
//        // 根据出运发票号拆分
//        Map<String, List<ShipmentPriceEntity>> shipmentPriceMap = saleContractApi.getShipmentPriceBySaleContractCode(contractCodeList);
//        return splitCustClaimItem(custClaimItemList,shipmentPriceMap);
        return custClaimItemList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCustClaim(Long id) {
        SimpleRegistrationResp simpleRegistrationResp = getCustClaimDetail(id);
        if (Objects.isNull(simpleRegistrationResp)){
            return;
        }
        List<PayeeEntity> payeeEntityList = simpleRegistrationResp.getPayeeEntityList();
        if (CollUtil.isNotEmpty(payeeEntityList)){
            List<Long> payeeEntityIds = payeeEntityList.stream().map(PayeeEntity::getId).toList();
            payeeMapper.deleteBatchIds(payeeEntityIds);
            // 删除回写抬头
            custApi.deleteCustTitle(payeeEntityIds);
            // 删除收款单
            payeeEntityList.forEach(s->receiptService.batchDeletedReceipt(BusinessTypeEnum.CLAIM.getCode(),String.valueOf(simpleRegistrationResp.getId()),BusinessSubjectTypeEnum.CUST.getCode(),s.getPayeeCode()));
        }
        List<CustClaimItem> custClaimItemList = simpleRegistrationResp.getCustClaimItemList();
        // 删除认领明细
        if (CollUtil.isNotEmpty(custClaimItemList)){
            List<Long> custClaimIds = custClaimItemList.stream().map(CustClaimItem::getId).toList();
            custClaimItemMapper.deleteBatchIds(custClaimIds);
            // 回写收款计划跟加减项
            List<WriteBackDTO> writeBackDTOList = custClaimItemList.stream().filter(s->Objects.nonNull(s.getSourceType())).map(s -> new WriteBackDTO().setCurrency(s.getCurrency())
                    .setCompletedFlag(BooleanEnum.NO.getValue())
                    .setItemId(s.getItemId())
                    .setId(s.getId())
                    .setSourceType(s.getSourceType())
                    .setDifferenceReason(s.getDifferenceReason())
                    .setContractAmount(s.getContractAmount())).toList();
            saleContractApi.writeBackContract(writeBackDTOList,true);
        }
        // 回写登记单已认领金额
        writeBackRegistration(custClaimItemList,simpleRegistrationResp.getId(),payeeEntityList,true);
    }

    @Override
    public List<CustClaimDTO> getListByPlanIds(List<Long> planIds) {
        List<CustClaimItem> custClaimItems = custClaimItemMapper.selectList(CustClaimItem::getItemId, planIds);
        if(CollUtil.isEmpty(custClaimItems)){
            return null;
        }
        List<CustClaimDTO> custClaimDTOS = BeanUtils.toBean(custClaimItems, CustClaimDTO.class);
        List<Long> registerIds = custClaimDTOS.stream().map(CustClaimDTO::getRegistrationId).distinct().toList();
        List<BankRegistrationDO> bankRegistrationDOS = bankRegistrationMapper.selectList(BankRegistrationDO::getId, registerIds);
        if(CollUtil.isNotEmpty(bankRegistrationDOS)){
            custClaimDTOS.forEach(s->{
                Optional<BankRegistrationDO> first = bankRegistrationDOS.stream().filter(c -> Objects.equals(c.getId(), s.getRegistrationId())).findFirst();
                first.ifPresent(bankRegistrationDO -> s.setRegisteredBy(bankRegistrationDO.getRegisteredBy()).setReceiveDate(bankRegistrationDO.getRegistrationDate()));
            });
        }
        return  custClaimDTOS;

    }

    private void writeBackRegistration(List<CustClaimItem> custClaimItemList, Long registrationId,List<PayeeEntity> payeeEntityList,boolean rollbackFlag){
        if (CollUtil.isEmpty(custClaimItemList)||CollUtil.isEmpty(payeeEntityList)){
            return;
        }
        // 回写登记单已认领金额
        Optional<BigDecimal> claimedOpt = custClaimItemList.stream().map(CustClaimItem::getClaimedAmount).reduce(BigDecimal::add);
        if (claimedOpt.isPresent()){
            BankRegistrationDO bankRegistrationDO = bankRegistrationMapper.selectById(registrationId);
            BigDecimal claimedAmount = bankRegistrationDO.getClaimedAmount();
            if (Objects.isNull(claimedAmount)){
                claimedAmount = claimedOpt.get();
            }else {
                if (rollbackFlag){
                    claimedAmount = NumUtil.sub(claimedAmount,claimedOpt.get());
                }else {
                    claimedAmount = NumUtil.add(claimedAmount,claimedOpt.get());
                }

            }
            bankRegistrationDO.setClaimedAmount(claimedAmount);
            // 回写认领状态
            BigDecimal amount = bankRegistrationDO.getAmount();
            if (claimedAmount.compareTo(BigDecimal.ZERO) == 0){
                bankRegistrationDO.setClaimStatus(ClaimStatusEnum.NOT_CLAIM.getStatus());
            }else {
                if (amount.compareTo(claimedAmount) == 0){
                    bankRegistrationDO.setClaimStatus(ClaimStatusEnum.CLAIM.getStatus());
                }else {
                    bankRegistrationDO.setClaimStatus(ClaimStatusEnum.PART_CLAIM.getStatus());
                }
            }
            List<SimpleCustResp> simpleCustRespList = custApi.getSimpleCustRespByCode(payeeEntityList.stream().map(PayeeEntity::getPayeeCode).toList());
            if (rollbackFlag){
                bankRegistrationDO.setCustList(null);
                bankRegistrationDO.setManagerList(null);
            }else {
                if (CollUtil.isNotEmpty(simpleCustRespList)){
                    List<SimpleData> custList = BeanUtils.toBean(simpleCustRespList, SimpleData.class);
                    bankRegistrationDO.setCustList(custList);
                    List<UserDept> managerList = simpleCustRespList.stream().flatMap(s -> s.getManagerList().stream()).distinct().toList();
                    bankRegistrationDO.setManagerList(managerList);
                }
            }
            bankRegistrationMapper.updateById(bankRegistrationDO);
        }
    }
    /**
     * 处理认领页面收款对象 主要回显多批次认领时认领总金额
     *
     * @param simpleCustRespList 根据抬头查询的收款对象
     * @param payeeEntities      上一认领批次中收款对象
     * @param registrationId     登记单id
     * @return 当前页面收款对象
     */
    private List<PayeeEntity> transformSimpleCustRespList(List<SimpleCustResp> simpleCustRespList, List<PayeeEntity> payeeEntities, Long registrationId) {
        if (CollUtil.isEmpty(simpleCustRespList)) {
            return payeeEntities;
        }
        // 数据库中查询出的收款对象信息
        List<PayeeEntity> payeeEntityList = CustClaimConvert.INSTANCE.convertPayeeEntityList(simpleCustRespList, registrationId);
        if (CollUtil.isEmpty(payeeEntities)) {
            return payeeEntityList;
        }
//        Map<String, List<PayeeEntity>> payeeEntityMap = payeeEntities.stream().collect(Collectors.groupingBy(PayeeEntity::getPayeeCode));
//        payeeEntityList.forEach(s -> {
//            List<PayeeEntity> payeeEntitityList = payeeEntityMap.get(s.getPayeeCode());
//            if (CollUtil.isEmpty(payeeEntitityList)) {
//                return;
//            }
//            Optional<BigDecimal> reduce = payeeEntitityList.stream().map(PayeeEntity::getClaimTotalAmount).reduce(BigDecimal::add);
//            reduce.ifPresent(s::setClaimTotalAmount);
//        });
        return payeeEntityList;
    }
}
