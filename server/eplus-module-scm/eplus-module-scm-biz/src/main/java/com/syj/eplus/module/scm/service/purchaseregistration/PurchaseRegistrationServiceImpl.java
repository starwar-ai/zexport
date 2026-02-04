package com.syj.eplus.module.scm.service.purchaseregistration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.*;
import com.syj.eplus.module.scm.convert.purchaseregistration.PurchaseRegistrationConvert;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnotices.InvoicingNoticesDO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistration.PurchaseRegistrationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import com.syj.eplus.module.scm.dal.mysql.invoicingnotices.InvoicingNoticesMapper;
import com.syj.eplus.module.scm.dal.mysql.invoicingnoticesitem.InvoicingNoticesItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistration.PurchaseRegistrationMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistrationitem.PurchaseRegistrationItemMapper;
import com.syj.eplus.module.scm.service.vender.VenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 采购跟单登记 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class PurchaseRegistrationServiceImpl implements PurchaseRegistrationService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PurchaseRegistrationMapper purchaseRegistrationMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private PurchaseRegistrationItemMapper itemMapper;

    @Resource
    private InvoicingNoticesItemMapper invoicingNoticesItemMapper;

    @Resource
    private InvoicingNoticesMapper invoicingNoticesMapper;
    @Resource
    private PurchaseContractItemMapper purchaseContractItemMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private VenderService venderService;
    @Resource
    private SkuApi skuApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    private static final String SN_TYPE = "PurchaseRegistration";
    private static final String CODE_PREFIX = "PR";
    private static final String PROCESS_DEFINITION_KEY = "purchase_registration";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchaseRegistration(PurchaseRegistrationSaveReqVO createReqVO) {
        PurchaseRegistrationDO purchaseRegistration = PurchaseRegistrationConvert.INSTANCE.convertPurchaseRegistrationDO(createReqVO);
        // 写入登记人
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDeptByUserId = adminUserApi.getUserDeptByUserId(loginUserId);
        purchaseRegistration.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        purchaseRegistration.setInputUser(userDeptByUserId);
        purchaseRegistration.setInputDate(LocalDateTime.now());
        // 生成 采购跟单登记 编号
        purchaseRegistration.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 插入
        purchaseRegistrationMapper.insert(purchaseRegistration);
        List<PurchaseRegistrationItem> children = createReqVO.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            children.forEach(s -> {
                //业务判断  登票数量不大于开票通知数
                if(s.getInvoicThisQuantity().compareTo(s.getInvoicNoticesQuantity()) > 0){
                    throw exception(REGISTRATION_ITEM_QUANTITY_WRONG);
                }
                s.setRegistrationId(purchaseRegistration.getId());
                s.setInvoicingNoticesItemId(s.getInvoicingNoticesItemId());
                s.setManager(userDeptByUserId);
                s.setId(null);
            });
            itemMapper.insertBatch(children);
            updateInvoicingNoticesStatus(children);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(purchaseRegistration.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return purchaseRegistration.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseRegistration(PurchaseRegistrationSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseRegistrationExists(updateReqVO.getId());
        // 更新
        PurchaseRegistrationDO updateObj = PurchaseRegistrationConvert.INSTANCE.convertPurchaseRegistrationDO(updateReqVO);
        purchaseRegistrationMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
        }
    }

    @Override
    public void deletePurchaseRegistration(Long id) {
        // 校验存在
        validatePurchaseRegistrationExists(id);
        // 删除
        purchaseRegistrationMapper.deleteById(id);
    }

    private PurchaseRegistrationDO validatePurchaseRegistrationExists(Long id) {
        PurchaseRegistrationDO purchaseRegistrationDO = purchaseRegistrationMapper.selectById(id);
        if (purchaseRegistrationDO == null) {
            throw exception(PURCHASE_REGISTRATION_NOT_EXISTS);
        }
        return purchaseRegistrationDO;
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
    public void submitTask(Long purchaseRegistrationId, Long userId) {
        PurchaseRegistrationDO purchaseRegistration = validatePurchaseRegistrationExists(purchaseRegistrationId);
        Map<String, Object> variables = new HashMap<>();
        UserDept inputUser = purchaseRegistration.getInputUser();
        if (Objects.isNull(inputUser)) {
            throw exception(PURCHASE_REGISTRATION_INPUT_USER_EMPTY);
        }
        variables.put("deptCode", inputUser.getDeptCode());
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, purchaseRegistrationId, variables, null);  
        updateAuditStatus(purchaseRegistrationId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId, PurchaseRegistrationStatusEnum.WAITING_FOR_APPROVAL.getValue());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId, Integer status) {
        PurchaseRegistrationDO updateDO = new PurchaseRegistrationDO();
        updateDO.setId(auditableId);
        updateDO.setAuditStatus(auditStatus);
        updateDO.setStatus(status);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            updateDO.setProcessInstanceId(processInstanceId);
        }
        purchaseRegistrationMapper.updateById(updateDO);
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            PurchaseRegistrationDO purchaseRegistrationDO = purchaseRegistrationMapper.selectById(auditableId);
            List<PurchaseRegistrationItem> purchaseRegistrationItemList = itemMapper.selectList(PurchaseRegistrationItem::getRegistrationId, purchaseRegistrationDO.getId());
            // 回写已登票数量
//            List<String> saleContractCodeList = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getSaleContractCode).toList();
//            LambdaQueryWrapperX<PurchaseRegistrationItem> queryWrapperX = new LambdaQueryWrapperX<PurchaseRegistrationItem>()
//                    .in(PurchaseRegistrationItem::getSaleContractCode, saleContractCodeList);
//            Map<Long ,List<PurchaseRegistrationItem>>  purchaseRegistrationItemMap = itemMapper.selectList(queryWrapperX).stream().toList().stream().collect(Collectors.groupingBy(PurchaseRegistrationItem::getPurchaseContractItemId));
            purchaseRegistrationItemList.forEach(s->{
                //业务判断  登票数量不大于开票通知数
                if(s.getInvoicThisQuantity().compareTo(s.getInvoicNoticesQuantity()) > 0){
                    throw exception(REGISTRATION_ITEM_QUANTITY_WRONG);
                }
                Integer registeredQuantity = s.getInvoicThisQuantity();
//                // 已登票数 = 本次开票数 + 库里面开票数和
//                if(CollUtil.isNotEmpty(purchaseRegistrationItemMap)){
//                    List<PurchaseRegistrationItem> itemList = purchaseRegistrationItemMap.get(s.getPurchaseContractItemId());
//                    if(CollUtil.isNotEmpty(itemList)){
//                        registeredQuantity += itemList.stream().map(PurchaseRegistrationItem::getInveicRegisteredQuantity).reduce(0, Integer::sum);
//                    }
//                }
                s.setInveicRegisteredQuantity(registeredQuantity);
            });
            itemMapper.updateBatch(purchaseRegistrationItemList);
            updatePurchaseContractItem(purchaseRegistrationItemList, purchaseRegistrationDO.getCurrency());
            Set<Long> invoiceNoticeItemIds = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getInvoicingNoticesItemId).collect(Collectors.toSet());
            List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().select(InvoicingNoticesItem::getInvoicNoticesCode).in(InvoicingNoticesItem::getId, invoiceNoticeItemIds));
            if (CollUtil.isNotEmpty(invoicingNoticesItems)){
                Set<String> invoiceCodes = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getInvoicNoticesCode).collect(Collectors.toSet());
                // 回写开票通知登票日期
                invoicingNoticesMapper.update(new InvoicingNoticesDO().setRegistrationDate(purchaseRegistrationDO.getInputDate()),new LambdaQueryWrapperX<InvoicingNoticesDO>().in(InvoicingNoticesDO::getCode,invoiceCodes));
            }
            //回写登票状态
            updatePurchaseRegisterNoticeStatus(auditableId);
            // 更新采购合同付款日期
            List<Long> saleContractItemIds = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getSaleContractItemId).distinct().toList();
            List<Integer> dateTypes = new ArrayList<>();
            dateTypes.add(DateTypeEnum.TICKET_DATE.getValue());
            purchaseContractApi.updateShipmentDate(saleContractItemIds,dateTypes,LocalDateTime.now());
        }
    }

    //回写登票状态
    private void updatePurchaseRegisterNoticeStatus(Long auditableId) {
        List<PurchaseRegistrationItem> purchaseRegistrationItemList = itemMapper.selectList(PurchaseRegistrationItem::getRegistrationId, auditableId);
        if(CollUtil.isEmpty(purchaseRegistrationItemList)){
            return;
        }
        List<Long> itemIdList = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getPurchaseContractItemId).distinct().toList();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, itemIdList);
        if(CollUtil.isEmpty(purchaseContractItemDOS)){
            return;
        }
        Map<Long, PurchaseContractItemDO> itemMap = purchaseContractItemDOS.stream().collect(Collectors.toMap(PurchaseContractItemDO::getId, s -> s));

        purchaseRegistrationItemList.forEach(s->{
            String purchaseContractCode = s.getPurchaseContractCode();
            if(StrUtil.isEmpty(purchaseContractCode)){
                return;
            }
            PurchaseContractItemDO purchaseContractItemDO = itemMap.get(s.getPurchaseContractItemId());
            if(Objects.isNull(purchaseContractItemDO)){
                return;
            }
            Integer registerNoticeQuantity = purchaseContractItemDO.getRegisterNoticeQuantity();
            if(Objects.isNull(registerNoticeQuantity)){
                registerNoticeQuantity = 0;
            }
            Integer  invoicThisQuantity = s.getInvoicThisQuantity();

            String skuCode = purchaseContractItemDO.getSkuCode();
            String hsMeasureUnit = skuApi.getHsMeasureUnitBySkuCode(skuCode);
            String invoiceUnit = s.getHsMeasureUnit();
            boolean hsMeasureUnitFlag = Objects.isNull(hsMeasureUnit) || (!hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue())&&  !hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()));
            boolean invoiceUnitFlag = StrUtil.isNotEmpty(invoiceUnit) && (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) || invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()));
            if (hsMeasureUnitFlag && invoiceUnitFlag) {
                JsonWeight netweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(purchaseContractItemDO.getSpecificationList());
                String unit = netweight.getUnit();
                BigDecimal weight = netweight.getWeight();
                if (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) && CalculationDict.GRAM.equals(unit)) {
                    //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                    weight = NumUtil.mul(weight, CalculationDict.ONE_THOUSAND);
                }else if(invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()) && CalculationDict.KILOGRAM.equals(unit)){
                    //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                    weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
                }
                invoicThisQuantity = NumUtil.mul(NumUtil.div(invoicThisQuantity, weight),purchaseContractItemDO.getQtyPerOuterbox()).intValue();
            }
            int quantity = registerNoticeQuantity + invoicThisQuantity;
            if(purchaseContractItemDO.getQuantity() <quantity ){
                throw exception(PURCHASE_ITEM_QUANTITY_NOT_ENOUGH);
            }else if(purchaseContractItemDO.getQuantity()  == quantity ){
                purchaseContractItemDO.setRegisterNoticeStatus(RegisterInvoiceStatusEnum.REGISTERED.getValue());
            }else {
                purchaseContractItemDO.setRegisterNoticeStatus(RegisterInvoiceStatusEnum.PART_REGISTERED.getValue());
            }
            purchaseContractItemDO.setRegisterNoticeQuantity(quantity);
            purchaseContractItemMapper.updateById(purchaseContractItemDO);
        });
    }

    @Override
    public void updateInvoicingNoticesStatus(List<PurchaseRegistrationItem> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        List<Long> purchaseContractItemIds = itemList.stream().map(PurchaseRegistrationItem::getPurchaseContractItemId).distinct().toList();
        Map<Long ,List<InvoicingNoticesItem>> invoicingNoticesItemMap = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().in(InvoicingNoticesItem::getPurchaseContractItemId, purchaseContractItemIds)).stream().collect(Collectors.groupingBy(InvoicingNoticesItem::getPurchaseContractItemId));
        List<InvoicingNoticesItem> invoicingNoticesItemList = new ArrayList<>();
        itemList.forEach(it->{
            InvoicingNoticesItem item =  new InvoicingNoticesItem();
            item.setId(it.getInvoicingNoticesItemId());
            Integer inveicRegisteredQuantity = 0;
            if(CollUtil.isNotEmpty(invoicingNoticesItemMap)){
                List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMap.get(it.getPurchaseContractItemId());
                if(CollUtil.isNotEmpty(invoicingNoticesItems)){
                    inveicRegisteredQuantity = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getInveicRegisteredQuantity).reduce(0, Integer::sum);
                }
            }
            if(inveicRegisteredQuantity +  it.getInvoicThisQuantity() < it.getInvoicNoticesQuantity()){
                item.setInveicRegisteredStatus(RegisteredStatusEnum.PARTP_REGISTERED.getValue());
            }else{
                item.setInveicRegisteredStatus(RegisteredStatusEnum.REGISTERED.getValue());
            }
            item.setInveicRegisteredQuantity(inveicRegisteredQuantity +  it.getInvoicThisQuantity());
            invoicingNoticesItemList.add(item);
        });
        invoicingNoticesItemMapper.updateBatch(invoicingNoticesItemList);
        checkInvoicingNoticesStatus(itemList);
    }

    /**
     * 校验开票通知状态 当开票通知明细全部登记成功则开票通知状态变更为已登记
     *
     * @param itemList 采购跟单登记明细
     */
    private void checkInvoicingNoticesStatus(List<PurchaseRegistrationItem> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        Optional<Long> itemIdOpt = itemList.stream().map(PurchaseRegistrationItem::getInvoicingNoticesItemId).findFirst();
        if (itemIdOpt.isPresent()) {
            InvoicingNoticesItem invoicingNoticesItem = invoicingNoticesItemMapper.selectById(itemIdOpt.get());
            if (Objects.isNull(invoicingNoticesItem)) {
                return;
            }
            List<InvoicingNoticesItem> invoicingNoticesItemList = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().select(InvoicingNoticesItem::getInveicRegisteredStatus).eq(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesItem.getInvoicNoticesCode()));
            if (CollUtil.isEmpty(invoicingNoticesItemList)) {
                return;
            }
            boolean matchResult = invoicingNoticesItemList.stream().map(InvoicingNoticesItem::getInveicRegisteredStatus).allMatch(s -> RegisteredStatusEnum.REGISTERED.getValue().equals(s));
            if (matchResult) {
                invoicingNoticesMapper.update(new InvoicingNoticesDO().setStatus(RegisteredStatusEnum.REGISTERED.getValue()), new LambdaQueryWrapperX<InvoicingNoticesDO>().eq(InvoicingNoticesDO::getCode, invoicingNoticesItem.getInvoicNoticesCode()));
            }else{
                boolean partMatchResult = invoicingNoticesItemList.stream().map(InvoicingNoticesItem::getInveicRegisteredStatus).anyMatch(s -> RegisteredStatusEnum.PARTP_REGISTERED.getValue().equals(s));
                if(partMatchResult){
                    invoicingNoticesMapper.update(new InvoicingNoticesDO().setStatus(RegisteredStatusEnum.PARTP_REGISTERED.getValue()), new LambdaQueryWrapperX<InvoicingNoticesDO>().eq(InvoicingNoticesDO::getCode, invoicingNoticesItem.getInvoicNoticesCode()));
                }
            }
        }
    }

    @Override
    public void updatePurchaseContractItem(List<PurchaseRegistrationItem> itemList, String currency) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        // 根据采购合同编号批量查出对应未登票完成的合同明细
        List<String> purchaseContractCodeList = itemList.stream().map(PurchaseRegistrationItem::getPurchaseContractCode).distinct().toList();
        LambdaQueryWrapperX<PurchaseContractItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .in(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCodeList)
                .in(PurchaseContractItemDO::getInvoiceStatus, Arrays.asList(InvoiceStatusEnum.NOT_ISSUED.getValue(), InvoiceStatusEnum.PART_ISSUED.getValue()));
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return;
        }
        Map<Long, PurchaseContractItemDO> purchaseContractItemDOMap = purchaseContractItemDOList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId, s -> s,(s1,s2)->s2));

        List<PurchaseContractItemDO> contractItemDOList = itemList.stream().filter(s -> purchaseContractItemDOMap.containsKey(s.getSaleContractItemId())).map(s -> {
            Long saleContractItemId = s.getSaleContractItemId();
            PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemDOMap.get(saleContractItemId);
            // 本次登票数量
            Integer invoicThisQuantity = s.getInvoicThisQuantity();
            String hsMeasureUnit = s.getHsMeasureUnit();
            // 开票单价
            BigDecimal invoicPrice = s.getInvoicPrice();
            if (StrUtil.isNotEmpty(hsMeasureUnit) && (hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getName()) || hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getName()) )) {
                JsonWeight netweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(purchaseContractItemDO.getSpecificationList());
                String unit = netweight.getUnit();
                BigDecimal weight = netweight.getWeight();
                if (hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getName()) && CalculationDict.GRAM.equals(unit)) {
                    //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                    weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
                }else if(hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getName()) && CalculationDict.KILOGRAM.equals(unit)) {
                    //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                    weight = NumUtil.mul(weight, CalculationDict.ONE_THOUSAND);
                }
                // 海关计量单位为千克 本次登票数量 = 本次登票数量/外箱净重
                invoicThisQuantity = NumUtil.div(invoicThisQuantity, weight).intValue();
                // 海关计量单位为千克 开票单价 = 采购含税单价
                invoicPrice = purchaseContractItemDO.getTaxRate();
            }
            // 本次开票金额 = 本次登票数量 * 开票单价
            BigDecimal invoicThisAmount = NumUtil.mul(invoicThisQuantity, invoicPrice);
            // 已登票金额
            BigDecimal invoicedAmount = Objects.isNull(purchaseContractItemDO.getInvoicedAmount()) ? BigDecimal.ZERO : purchaseContractItemDO.getInvoicedAmount();
            // 采购数量
            Integer quantity = purchaseContractItemDO.getQuantity();
            // 已登票金额 = 已登票金额 + 本次开票金额
            invoicedAmount = NumUtil.add(invoicedAmount, invoicThisAmount);
            purchaseContractItemDO.setInvoicedAmount(invoicedAmount);
            // 已登票数量
            Integer invoicedQuantity = Objects.isNull(purchaseContractItemDO.getInvoicedQuantity()) ? CalculationDict.ZERO : purchaseContractItemDO.getInvoicedQuantity();
            // 已登票数量 = 已登票数量 + 本次登票数量
            invoicedQuantity = NumUtil.add(BigDecimal.valueOf(invoicedQuantity), invoicThisQuantity).intValue();
            purchaseContractItemDO.setInvoicedQuantity(invoicedQuantity);
            String purchaseInvoiceCurrency = purchaseContractItemDO.getInvoicedCurrency();
            if (Objects.nonNull(purchaseInvoiceCurrency) && !currency.equals(purchaseInvoiceCurrency)) {
                // TODO 采购明细中已登票币种与当前登票币种不一致？
                return purchaseContractItemDO;
            }
            purchaseContractItemDO.setInvoicedCurrency(currency);
            //已登票数量 < 采购数量 开票通知状态设置为部分开票
            if (invoicedQuantity < quantity) {
                purchaseContractItemDO.setRegisterNoticeStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
            } else {
                purchaseContractItemDO.setRegisterNoticeStatus(InvoiceStatusEnum.ISSUED.getValue());
            }
            return purchaseContractItemDO;
        }).toList();
        if (CollUtil.isNotEmpty(contractItemDOList)){
            purchaseContractItemMapper.updateBatch(contractItemDOList);
        }
    }

    public void rollBackPurchaseContractItem(List<PurchaseRegistrationItem> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        // 根据采购合同编号批量查出对应未登票完成的合同明细
        List<String> purchaseContractCodeList = itemList.stream().map(PurchaseRegistrationItem::getPurchaseContractCode).distinct().toList();
        LambdaQueryWrapperX<PurchaseContractItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .in(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCodeList);
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return;
        }
        Map<Long, PurchaseContractItemDO> purchaseContractItemDOMap = purchaseContractItemDOList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId, s -> s,(s1,s2)->s2));
        List<PurchaseContractItemDO> contractItemDOList = itemList.stream().filter(s -> purchaseContractItemDOMap.containsKey(s.getSaleContractItemId())).map(s -> {
            Long saleContractItemId = s.getSaleContractItemId();
            PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemDOMap.get(saleContractItemId);
            Integer invoicedQuantity = 0;
            if(purchaseContractItemDO.getInvoicedQuantity() > s.getInvoicNoticesQuantity()){
                invoicedQuantity = NumUtil.sub(BigDecimal.valueOf(purchaseContractItemDO.getInvoicedQuantity()), s.getInvoicNoticesQuantity()).intValue();
            }
            purchaseContractItemDO.setInvoicedQuantity(invoicedQuantity);
            //已登票数量=0 未开票，否则部分开票
            if (invoicedQuantity == 0) {
                purchaseContractItemDO.setRegisterNoticeStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
            } else {
                purchaseContractItemDO.setRegisterNoticeStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
            }
            return purchaseContractItemDO;
        }).toList();
        purchaseContractItemMapper.updateBatch(contractItemDOList);
    }

    @Override
    public void batchReviewPurchaseRegistration(ReviewRegistrationReq reviewReq) {
        List<Long> ids = reviewReq.getIds();
        if (CollUtil.isEmpty(ids)){
            return;
        }
        UserDept userDept = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
        List<PurchaseRegistrationDO> updateList = ids.stream().map(s -> {
            PurchaseRegistrationDO purchaseRegistrationDO = new PurchaseRegistrationDO();
            purchaseRegistrationDO.setId(s);
            purchaseRegistrationDO.setReviewUser(userDept);
            purchaseRegistrationDO.setInvoicedDate(reviewReq.getInvoicedDate());
            purchaseRegistrationDO.setReviewDate(LocalDateTime.now());
            return purchaseRegistrationDO;
        }).toList();
        if (CollUtil.isNotEmpty(updateList)){
            purchaseRegistrationMapper.updateBatch(updateList);
        }
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public PurchaseRegistrationRespVO getPurchaseRegistration(PurchaseRegistrationDetailReq purchaseRegistrationDetailReq) {
        Long purchaseRegistrationId = Objects.nonNull(purchaseRegistrationDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchaseRegistrationDetailReq.getProcessInstanceId()) : purchaseRegistrationDetailReq.getPurchaseRegistrationId();
        if (Objects.isNull(purchaseRegistrationId)) {
            logger.error("[采购跟单登记]未获取到采购跟单登记id");
            return null;
        }
        PurchaseRegistrationDO purchaseRegistrationDO = purchaseRegistrationMapper.selectById(purchaseRegistrationId);
        if (purchaseRegistrationDO == null) {
            return null;
        }
        PurchaseRegistrationRespVO purchaseRegistrationRespVO = PurchaseRegistrationConvert.INSTANCE.convertPurchaseRegistrationRespVO(purchaseRegistrationDO);
        List<PurchaseRegistrationItem> purchaseRegistrationItemList = itemMapper.selectList(PurchaseRegistrationItem::getRegistrationId, purchaseRegistrationId);
        purchaseRegistrationRespVO.setChildren(purchaseRegistrationItemList);
        return purchaseRegistrationRespVO;
    }

    @Override
    public PageResult<PurchaseRegistrationRespVO> getPurchaseRegistrationPage(PurchaseRegistrationPageReqVO pageReqVO) {
        if(StrUtil.isNotEmpty(pageReqVO.getShipInvoiceCode()) || StrUtil.isNotEmpty(pageReqVO.getInvoicSkuName()) || StrUtil.isNotEmpty(pageReqVO.getHsCode()) || StrUtil.isNotEmpty(pageReqVO.getSaleContractCode()) || StrUtil.isNotEmpty(pageReqVO.getPurchaseContractCode()) || StrUtil.isNotEmpty(pageReqVO.getHsMeasureUnit())|| StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode()) || Objects.nonNull(pageReqVO.getManagerId())){
            LambdaQueryWrapperX<PurchaseRegistrationItem> queryWrapperX = new LambdaQueryWrapperX<PurchaseRegistrationItem>()
                    .likeIfPresent(PurchaseRegistrationItem::getShipInvoiceCode, pageReqVO.getShipInvoiceCode())
                    .likeIfPresent(PurchaseRegistrationItem::getInvoicSkuName, pageReqVO.getInvoicSkuName())
                    .likeIfPresent(PurchaseRegistrationItem::getHsCode, pageReqVO.getHsCode())
                    .likeIfPresent(PurchaseRegistrationItem::getSaleContractCode, pageReqVO.getSaleContractCode())
                    .likeIfPresent(PurchaseRegistrationItem::getBasicSkuCode, pageReqVO.getBasicSkuCode())
                    .likeIfPresent(PurchaseRegistrationItem::getPurchaseContractCode, pageReqVO.getPurchaseContractCode())
                    .eqIfPresent(PurchaseRegistrationItem::getHsMeasureUnit, pageReqVO.getHsMeasureUnit());
            if (Objects.nonNull(pageReqVO.getManagerId())) {
                queryWrapperX.apply("JSON_EXTRACT(manager, '$.userId') = {0}", pageReqVO.getManagerId());
            }
            List<PurchaseRegistrationItem> purchaseRegistrationItemList = itemMapper.selectList(queryWrapperX);
            if (CollUtil.isEmpty(purchaseRegistrationItemList)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getRegistrationId).distinct().toArray(Long[]::new));
        }
        //全部状态下不查询已作废数据
        if(pageReqVO.getStatus()==null){
            pageReqVO.setNeStatus(PurchaseRegistrationStatusEnum.CASE_CLOSED.getValue());
        }
        PageResult<PurchaseRegistrationDO> purchaseRegistrationDOPageResult = purchaseRegistrationMapper.selectPage(pageReqVO);
        List<PurchaseRegistrationDO> list = purchaseRegistrationDOPageResult.getList();
        if (CollUtil.isEmpty(list)) {
            return PageResult.empty();
        }
        List<PurchaseRegistrationRespVO> voList = BeanUtils.toBean(list, PurchaseRegistrationRespVO.class);
        List<Long> idList = voList.stream().map(PurchaseRegistrationRespVO::getId).distinct().toList();
        Map<Long, List<PurchaseRegistrationItem>> purchaseRegistrationItemMap = itemMapper.getPurchaseRegistrationItemMapByRegistrationIdList(idList);
        if (CollUtil.isNotEmpty(purchaseRegistrationItemMap)) {
            voList.forEach(s -> {
                List<PurchaseRegistrationItem> purchaseRegistrationItemList = purchaseRegistrationItemMap.get(s.getId());
                if (Objects.nonNull(purchaseRegistrationItemList)) {
                    s.setChildren(purchaseRegistrationItemList);
                }
            });
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        LocalDateTime[] createTime = pageReqVO.getCreateTime();
        if (createTime != null && createTime.length > 1) {
            startTime = pageReqVO.getCreateTime()[0];
            endTime = pageReqVO.getCreateTime()[1];
        }
        // 根据筛选条件获取汇总数据
        Map<String, Object> totalMsg = itemMapper.getTotalMsgByConditions(pageReqVO.getVenderName(), pageReqVO.getCompanyId(),pageReqVO.getInvoiceCode(),startTime,endTime,pageReqVO.getStatus(),pageReqVO.getNeStatus()
                    ,pageReqVO.getIdList(),pageReqVO.getShipInvoiceCode(),pageReqVO.getInvoicSkuName(),pageReqVO.getHsCode(),pageReqVO.getSaleContractCode(),pageReqVO.getPurchaseContractCode(),pageReqVO.getHsMeasureUnit(),pageReqVO.getBasicSkuCode(),pageReqVO.getManagerId());
        return new PageResult<PurchaseRegistrationRespVO>().setList(voList).setTotal(purchaseRegistrationDOPageResult.getTotal()).setSummary(totalMsg);
    }

    @Override
    public PageResult<PurchaseRegistrationItemResp> getPurchaseRegistrationItemPage(PurchaseRegistrationPageReqVO pageReqVO) {
        PageResult<PurchaseRegistrationItem> itemPageResult = itemMapper.selectPage(pageReqVO, null);
        List<PurchaseRegistrationItem> registrationItemList = itemPageResult.getList();
        if (CollUtil.isEmpty(registrationItemList)) {
            return PageResult.empty();
        }
        List<Long> registrationIdList = registrationItemList.stream().map(PurchaseRegistrationItem::getRegistrationId).distinct().toList();
        if (CollUtil.isEmpty(registrationIdList)) {
            throw exception(PURCHASE_REGISTRATION_NOT_EXISTS);
        }
        List<PurchaseRegistrationDO> purchaseRegistrationDOList = purchaseRegistrationMapper.selectBatchIds(registrationIdList);
        if (CollUtil.isEmpty(purchaseRegistrationDOList)) {
            throw exception(PURCHASE_REGISTRATION_NOT_EXISTS);
        }
        Map<Long, PurchaseRegistrationDO> registrationDOMap = purchaseRegistrationDOList.stream().collect(Collectors.toMap(PurchaseRegistrationDO::getId, s -> s));
        List<PurchaseRegistrationItemResp> itemRespList = PurchaseRegistrationConvert.INSTANCE.convertRegistrationItemRespList(registrationItemList, registrationDOMap);
        return new PageResult<PurchaseRegistrationItemResp>().setList(itemRespList).setTotal(itemPageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePurchaseRegistration(Long id) {
        PurchaseRegistrationDO purchaseRegistrationDO = validatePurchaseRegistrationExists(id);
        purchaseRegistrationDO.setStatus(PurchaseRegistrationStatusEnum.CASE_CLOSED.getValue());
        List<PurchaseRegistrationItem> purchaseRegistrationItemList = itemMapper.selectList(PurchaseRegistrationItem::getRegistrationId, purchaseRegistrationDO.getId());
        purchaseRegistrationItemList.forEach(s->{
            if(s.getInveicRegisteredQuantity() > s.getInvoicNoticesQuantity()){
                s.setInveicRegisteredQuantity(NumUtil.sub(s.getInveicRegisteredQuantity(),s.getInvoicNoticesQuantity()).intValue());
            }else {
                s.setInveicRegisteredQuantity(0);
            }
        });
        itemMapper.updateBatch(purchaseRegistrationItemList);
        //回写开票通知
        List<Long> purchaseContractItemIds = purchaseRegistrationItemList.stream().map(PurchaseRegistrationItem::getPurchaseContractItemId).distinct().toList();
        Map<Long ,List<InvoicingNoticesItem>> invoicingNoticesItemMap = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().in(InvoicingNoticesItem::getPurchaseContractItemId, purchaseContractItemIds)).stream().collect(Collectors.groupingBy(InvoicingNoticesItem::getPurchaseContractItemId));
        List<InvoicingNoticesItem> invoicingNoticesItemList = new ArrayList<>();
        purchaseRegistrationItemList.forEach(it->{
            InvoicingNoticesItem item =  new InvoicingNoticesItem();
            item.setId(it.getInvoicingNoticesItemId());
            Integer inveicRegisteredQuantity = 0;
            if(CollUtil.isNotEmpty(invoicingNoticesItemMap)){
                List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMap.get(it.getPurchaseContractItemId());
                if(CollUtil.isNotEmpty(invoicingNoticesItems)){
                    inveicRegisteredQuantity = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getInveicRegisteredQuantity).reduce(0, Integer::sum);
                }
            }
            if(inveicRegisteredQuantity > it.getInvoicNoticesQuantity()){
                inveicRegisteredQuantity = NumUtil.sub(inveicRegisteredQuantity,it.getInvoicNoticesQuantity()).intValue();
                item.setInveicRegisteredStatus(RegisteredStatusEnum.PARTP_REGISTERED.getValue());
            }else{
                inveicRegisteredQuantity = 0;
                item.setInveicRegisteredStatus(RegisteredStatusEnum.NOT_REGISTERED.getValue());
            }
            item.setInveicRegisteredQuantity(inveicRegisteredQuantity);
            invoicingNoticesItemList.add(item);
        });
        invoicingNoticesItemMapper.updateBatch(invoicingNoticesItemList);
        checkInvoicingNoticesStatus(purchaseRegistrationItemList);
        //回写采购合同
        rollBackPurchaseContractItem(purchaseRegistrationItemList);
        purchaseRegistrationMapper.updateById(purchaseRegistrationDO);
    }
}