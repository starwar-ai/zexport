package com.syj.eplus.module.scm.service.invoicingnotices;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.CompanyBankRespDTO;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dict.DictTypeApi;
import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.InvoiceQuantityDTO;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.*;
import com.syj.eplus.module.scm.convert.invoicingnotices.InvoicingNoticesConvert;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnotices.InvoicingNoticesDO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistration.PurchaseRegistrationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import com.syj.eplus.module.scm.dal.mysql.invoicingnotices.InvoicingNoticesMapper;
import com.syj.eplus.module.scm.dal.mysql.invoicingnoticesitem.InvoicingNoticesItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistration.PurchaseRegistrationMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseregistrationitem.PurchaseRegistrationItemMapper;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import com.syj.eplus.module.scm.service.quoteitem.QuoteItemService;
import com.syj.eplus.module.scm.service.vender.VenderService;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SaleContractItemDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.HSDATA_UNIT_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.INVOICING_NOTICES_CLOSE_ERROR;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 开票通知 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class InvoicingNoticesServiceImpl implements InvoicingNoticesService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private InvoicingNoticesMapper invoicingNoticesMapper;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private InvoicingNoticesItemMapper invoicingNoticesItemMapper;

    @Resource
    private VenderService venderService;
    @Resource
    private ReportApi reportApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DictDataApi dictDataApi;

    @Resource
    private DictTypeApi dictTypeApi;

    @Resource
    @Lazy
    private PurchaseContractService purchaseContractService;

    @Resource
    private PurchaseContractItemMapper purchaseContractItemMapper;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private OperateLogApi operateLogApi;

    @Resource
    private QuoteItemService quoteItemService;

    @Resource
    private SkuApi skuApi;
    @Resource
    private PurchaseRegistrationItemMapper purchaseRegistrationItemMapper;
    @Resource
    private PurchaseRegistrationMapper purchaseRegistrationMapper;
    
    @Resource
    private SaleContractApi saleContractApi;

    private static final String SN_TYPE = "invoicing_notices";

    private static final String CODE_PREFIX = "KP";

    private static final String PROCESS_DEFINITION_KEY = "scm_invoicing_notices";

    private static final String OPERATOR_EXTS_KEY = "invoicingNotices";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createInvoicingNotices(InvoicingNoticesSaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        InvoicingNoticesDO invoicingNotices = InvoicingNoticesConvert.INSTANCE.convertInvoicingNoticesDO(createReqVO);
        invoicingNotices.setStatus(RegisteredStatusEnum.NOT_REGISTERED.getValue());
        // 生成 开票通知 编号
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        invoicingNotices.setCode(code);
        if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(createReqVO.getSourceType())) {
            invoicingNotices.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        } else if (InvoiceNoticeSourceTypeEnum.SHIPMENT.getValue().equals(createReqVO.getSourceType())) {
            invoicingNotices.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        }
        // 插入开票明细
        List<InvoicingNoticesItem> children = createReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            // 插入
            invoicingNoticesMapper.insert(invoicingNotices);
            responses.add(new CreatedResponse(invoicingNotices.getId(),invoicingNotices.getCode()));
            return responses;
        }
        //子产品全部是代理产品不需要审核
        List<String> skuCodeList = createReqVO.getChildren().stream().map(InvoicingNoticesItem::getSkuCode).distinct().toList();
        List<SkuDTO> skuDTOListByCodeList = skuApi.getSkuDTOListByCodeList(skuCodeList);
        List<SkuDTO> notAgentSkuList = skuDTOListByCodeList.stream().filter(s -> s.getAgentFlag().equals(BooleanEnum.NO.getValue())).distinct().toList();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag()) && InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(createReqVO.getSourceType()) && !CollectionUtils.isEmpty(notAgentSkuList)) {
            invoicingNoticesMapper.insert(invoicingNotices);
            submitTask(invoicingNotices.getId(), WebFrameworkUtils.getLoginUserId());
            if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(createReqVO.getSourceType())) {
                updatePurchaseContractItemStatus(children, createReqVO.getCompanyId(), false);
            }
        }else{
            //invoicingNotices.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
//            invoicingNotices.setStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            invoicingNoticesMapper.insert(invoicingNotices);
        }
        Map<String, BigDecimal> taxRateMap = skuApi.getTaxRateBySkuCodeList(skuCodeList);
        children.forEach(s -> {
            if (StrUtil.isNotEmpty(s.getSourceUniqueCode()) && StrUtil.isNotEmpty(s.getUniqueCode())) {
                s.setSourceUniqueCode(s.getUniqueCode());
            }
            if (Objects.isNull(s.getBaseInvoiceQuantity())||(s.getBaseInvoiceQuantity() == 0 && BigDecimal.ZERO.compareTo(s.getInvoicNoticesQuantity())>0)){
                s.setBaseInvoiceQuantity(s.getInvoicNoticesQuantity().intValue());
            }
            s.setTaxRate(taxRateMap.get(s.getSkuCode()));
            s.setUniqueCode(IdUtil.fastSimpleUUID());
            s.setInvoicNoticesCode(code);
            s.setId(null);
            s.setInveicRegisteredStatus(RegisteredStatusEnum.NOT_REGISTERED.getValue());
        });
        invoicingNoticesItemMapper.insertBatch(children);
        List<String> linkCodeList = createReqVO.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)) {
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(invoicingNotices.getId())
                    .setCode(invoicingNotices.getCode())
                    .setName(BusinessNameDict.INVOICING_NOTICES)
                    .setType(OrderLinkTypeEnum.INVOICING_NOTICES.getValue())
                    .setLinkCode(s)
                    .setOrderMsg(invoicingNotices)
                    .setItem(children)
                    .setParentCode(invoicingNotices.getShipmentCode())
                    .setStatus(invoicingNotices.getStatus())
                    .setCompanyId(invoicingNotices.getCompanyId())).toList();
            if (CollUtil.isNotEmpty(orderLinkDTOList)) {
                orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
            }
        }
        if (InvoiceNoticeSourceTypeEnum.SHIPMENT.getValue().equals(createReqVO.getSourceType())) {
            // 修改出运单明细开票状态
            updateShipmentItemInvoiceData(children);
        }
        // 返回
        responses.add(new CreatedResponse(invoicingNotices.getId(),invoicingNotices.getCode()));
        return responses;
    }

    /**
     * 更新出运单明细开票状态
     *
     * @param invoicingNoticesItems 开票通知明细
     */
    private void updateShipmentItemInvoiceData(List<InvoicingNoticesItem> invoicingNoticesItems) {
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return;
        }
        Map<Long, BigDecimal> invoiceQuantityMap = invoicingNoticesItems.stream().collect(Collectors.toMap(InvoicingNoticesItem::getShipmentItemId, InvoicingNoticesItem::getInvoicNoticesQuantity, (o, n) -> o));
        shipmentApi.updateShipmentIItemInvoiceQuantity(invoiceQuantityMap);
    }

    /**
     * 更新采购合同开票状态
     *
     * @param invoicingNoticesItems 开票通知明细
     */
    private void updatePurchaseContractItemStatus(List<InvoicingNoticesItem> invoicingNoticesItems, Long lastCompanyId, boolean isListener) {
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return;
        }
        List<Long> purchaseItemIds = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getPurchaseContractItemId).toList();
        if (CollUtil.isEmpty(purchaseItemIds)) {
            throw exception(INVOICE_ITEM_SOURCE_CODE_EMPTY);
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectBatchIds(purchaseItemIds);

//        List<PurchaseContractItemDO> purchaseContractItemDOS = getPurchaseByPurchaseCodeAndSaleItemId(invoicingNoticesItems);

        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        List<Long> purchaseContractIdList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractService.selectByIdList(purchaseContractIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        List<Long> filterIdList = purchaseContractDOS.stream().filter(s -> lastCompanyId.equals(s.getCompanyId())).map(PurchaseContractDO::getId).toList();
        if (CollUtil.isEmpty(filterIdList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        purchaseContractItemDOS = purchaseContractItemDOS.stream().filter(s -> filterIdList.contains(s.getPurchaseContractId())).toList();
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        Map<Long, BigDecimal> invoiceQuantityMap = invoicingNoticesItems.stream().collect(Collectors.toMap(InvoicingNoticesItem::getPurchaseContractItemId, InvoicingNoticesItem::getInvoicNoticesQuantity, NumUtil::add));
        Map<Long, Integer> baseInvoiceQuantityMap = invoicingNoticesItems.stream().collect(Collectors.toMap(InvoicingNoticesItem::getPurchaseContractItemId, InvoicingNoticesItem::getBaseInvoiceQuantity, Integer::sum));
        purchaseContractItemDOS.forEach(s -> {
            if (!isListener) {
                s.setInvoiceStatus(InvoiceStatusEnum.TRANSFER_ORDER.getValue());
                return;
            }
            Long purchaseItemId = s.getId();
            BigDecimal thisInvoiceQuantityDecimal = invoiceQuantityMap.get(purchaseItemId);
            Integer baseInvoiceQuantity = baseInvoiceQuantityMap.get(purchaseItemId);
            if (Objects.isNull(thisInvoiceQuantityDecimal)||Objects.isNull(baseInvoiceQuantity)) {
                return;
            }
            int invoicedQuantity = Objects.isNull(s.getInvoicedQuantity()) ? CalculationDict.ZERO : s.getInvoicedQuantity();
            // 转换单位
            // 未开票数量
            int unInvoicedQuantity = s.getQuantity() - invoicedQuantity;
            // 本次开票数量>未开票数量 抛异常
            if (baseInvoiceQuantity > unInvoicedQuantity) {
                throw exception(INVOICE_QUANTITY_NOT_ENOUGH);
            } else if (baseInvoiceQuantity == unInvoicedQuantity) {
                // 本次开票=未开票数量  已开票
                s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
                s.setInvoicedQuantity(invoicedQuantity +baseInvoiceQuantity);
            } else {
                // 本次开票<未开票数量  部分开票
                s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
                // 已开票数量+=本次开票数量
                s.setInvoicedQuantity(invoicedQuantity + baseInvoiceQuantity);
            }
        });
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOS);
    }

    private List<PurchaseContractItemDO> getPurchaseByPurchaseCodeAndSaleItemId(List<InvoicingNoticesItem> invoicingNoticesItems) {
        List<Long> saleContractItemIds = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getSaleContractItemId).toList();
        if (CollUtil.isEmpty(saleContractItemIds)) {
            throw exception(INVOICE_ITEM_SOURCE_CODE_EMPTY);
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleContractItemIds);
//        List<PurchaseContractItemDO> result = new ArrayList<>();
//        invoicingNoticesItems.forEach(s->{
//            List<PurchaseContractItemDO> itemDOS = purchaseContractItemDOS.stream().filter(d -> Objects.equals(d.getSaleContractItemId(), s.getSaleContractItemId())
//                    && Objects.equals(d.getPurchaseContractCode(), s.getPurchaseContractCode())).toList();
//            if(CollUtil.isEmpty(itemDOS)){
//                throw exception(PURCHASE_ITEM_NOT_EXISTS_BY_CODE);
//            }
//            if(itemDOS.size() > 1){
//                throw exception(PURCHASE_ITEM_MORE_EXISTS_BY_CODE);
//            }
//            result.add(itemDOS.get(0));
//        });
        return purchaseContractItemDOS;
    }

    /**
     * 转换开票通知数量单位
     *
     * @param purchaseContractItemDO 采购明细
     */
    private Tuple transformInvoiceQuantity(PurchaseContractItemDO purchaseContractItemDO, String invoiceUnit, BigDecimal thisInvoiceQuantity) {
        //海关计量单位为"千克" 开票单价和开票数量计算
        Integer purchaseQuantity = purchaseContractItemDO.getQuantity().intValue();
        JsonAmount jsonPurchaseWithTaxPrice = Objects.isNull(purchaseContractItemDO.getWithTaxPrice()) ? new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(purchaseContractItemDO.getCurrency()) : purchaseContractItemDO.getWithTaxPrice();
        BigDecimal purchaseWithTaxPrice = Objects.isNull(jsonPurchaseWithTaxPrice.getAmount()) ? BigDecimal.ZERO : jsonPurchaseWithTaxPrice.getAmount();
        //  本次开票金额 = 报关数量 * 采购单价
        BigDecimal invoiceThisPrice = NumUtil.mul(purchaseQuantity, purchaseWithTaxPrice);
        String hsMeasureUnit = skuApi.getHsMeasureUnitBySkuCode(purchaseContractItemDO.getSkuCode());
        boolean hsMeasureUnitFlag = StrUtil.isEmpty(hsMeasureUnit) || (!hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue()) && !hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()));
        boolean invoiceUnitFlag = StrUtil.isNotEmpty(invoiceUnit) && (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) || invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()));
        BigDecimal rollBackQuantity = BigDecimal.ZERO;
        JsonWeight netWeight = CalcSpecificationUtil.calcSpecificationTotalNetweight(purchaseContractItemDO.getSpecificationList());
        String unit = netWeight.getUnit();
        if (hsMeasureUnitFlag && invoiceUnitFlag) { // 采购数量/外箱装量 * 外箱净重 = 开票数量
            if (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) && CalculationDict.GRAM.equals(unit)) {
                //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.mul(thisInvoiceQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), purchaseContractItemDO.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
            } else if (invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()) && CalculationDict.KILOGRAM.equals(unit)) {
                //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.div(thisInvoiceQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), purchaseContractItemDO.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
            } else {
                rollBackQuantity = NumUtil.mul(NumUtil.div(thisInvoiceQuantity, netWeight.getWeight()), purchaseContractItemDO.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
            }
        } else {

            if (HsMeasureUnitEnum.KG.getValue().equals(hsMeasureUnit) && HsMeasureUnitEnum.KE.getValue().equals(invoiceUnit)) {
                rollBackQuantity = NumUtil.div(thisInvoiceQuantity, CalculationDict.ONE_THOUSAND);
            } else if (HsMeasureUnitEnum.KE.getValue().equals(hsMeasureUnit) && HsMeasureUnitEnum.KG.getValue().equals(invoiceUnit)) {
                rollBackQuantity = NumUtil.mul(thisInvoiceQuantity, CalculationDict.ONE_THOUSAND);
            } else {
                // 单位相同则直接回退数量
                rollBackQuantity = thisInvoiceQuantity;
            }
        }
        // 开票单价 =（采购单价*出运数量）/ 外箱总净重
        JsonAmount noticePrice = new JsonAmount().setAmount(NumUtil.div(invoiceThisPrice, netWeight.getWeight())).setCurrency(jsonPurchaseWithTaxPrice.getCurrency());
        // 开票数量=（采购单价*出运数量）/开票单价
        return new Tuple(noticePrice, rollBackQuantity);
    }

    @Override
    public void updateInvoicingNotices(InvoicingNoticesSaveReqVO updateReqVO) {
        // 校验存在
        validateInvoicingNoticesExists(updateReqVO.getId());
        // 更新
        InvoicingNoticesDO updateObj = InvoicingNoticesConvert.INSTANCE.convertInvoicingNoticesDO(updateReqVO);
        invoicingNoticesMapper.updateById(updateObj);
        List<InvoicingNoticesItem> children = updateReqVO.getChildren();
        if (CollUtil.isNotEmpty(children)){
            //处理聚合数据
            List<InvoicingNoticesItem> updateItems = new ArrayList<>(children);
            children.forEach(s->{
                List<Long> ids = s.getIds();
                if (CollUtil.isEmpty(ids)){
                    return;
                }
                ids.stream()
                        .filter(id -> !Objects.equals(id, s.getId()))
                        .forEach(id -> {
                            InvoicingNoticesItem updateDO = new InvoicingNoticesItem();
                            updateDO.setId(id);
                            updateDO.setInvoicSkuName(s.getInvoicSkuName());
                            updateDO.setHsMeasureUnit(s.getHsMeasureUnit());
                            updateDO.setHsCode(s.getHsCode());
                            updateItems.add(updateDO);
                        });
            });
            invoicingNoticesItemMapper.updateBatch(updateItems);
        }
        //子产品全部是代理产品不需要审核
        List<String> skuCodeList = updateReqVO.getChildren().stream().map(InvoicingNoticesItem::getSkuCode).distinct().toList();
        List<SkuDTO> skuDTOListByCodeList = skuApi.getSkuDTOListByCodeList(skuCodeList);
        List<SkuDTO> notAgentSkuList = skuDTOListByCodeList.stream().filter(s -> s.getAgentFlag().equals(BooleanEnum.NO.getValue())).distinct().toList();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag()) && InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(updateReqVO.getSourceType()) && !CollectionUtils.isEmpty(notAgentSkuList)) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
            if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(updateReqVO.getSourceType())) {
                updatePurchaseContractItemStatus(children, updateReqVO.getCompanyId(), false);
            }
        }
    }

    @Override
    public void deleteInvoicingNotices(Long id) {
        // 校验存在
        validateInvoicingNoticesExists(id);
        // 删除
        invoicingNoticesMapper.deleteById(id);
    }

    private InvoicingNoticesDO validateInvoicingNoticesExists(Long id) {
        InvoicingNoticesDO invoicingNoticesDO = invoicingNoticesMapper.selectById(id);
        if (invoicingNoticesDO == null) {
            throw exception(INVOICING_NOTICES_NOT_EXISTS);
        }
        return invoicingNoticesDO;
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
    public void submitTask(Long invoicingNoticesId, Long userId) {
        InvoicingNoticesDO invoicingNoticesDO = validateInvoicingNoticesExists(invoicingNoticesId);
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesDO.getCode());
        // 获取审批变量：采购类型和销售类型
        Map<String, Object> variables = new HashMap<>();
        Integer purchaseType = getPurchaseTypeFromItems(invoicingNoticesItems);
        Integer saleType = getSaleTypeFromItems(invoicingNoticesItems);
        
        // 采购类型和销售类型不能为空，必须抛出异常
        if (purchaseType == null) {
            throw exception(PURCHASE_TYPE_NULL);
        }
        saleType = Objects.isNull(saleType)?0:saleType;
        variables.put("purchaseType", purchaseType);
        variables.put("saleType", saleType);
        variables.put("deptCode",invoicingNoticesDO.getInputUser().getDeptCode());
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, invoicingNoticesId, variables, null);
        
        if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(invoicingNoticesDO.getSourceType())) {
            updatePurchaseContractItemStatus(invoicingNoticesItems, invoicingNoticesDO.getCompanyId(), false);
        }
        updateAuditStatus(invoicingNoticesId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        InvoicingNoticesDO updateDO = InvoicingNoticesDO.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            updateDO.setProcessInstanceId(processInstanceId);
        }
        invoicingNoticesMapper.updateById(updateDO);
        // 审核通过回写开票数量
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            InvoicingNoticesDO invoicingNoticesDO = validateInvoicingNoticesExists(auditableId);
            List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesDO.getCode());
            if (CollUtil.isEmpty(invoicingNoticesItems)) {
                return;
            }
            // 更新采购合同明细开票状态
            if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(invoicingNoticesDO.getSourceType())) {
                updatePurchaseContractItemStatus(invoicingNoticesItems, invoicingNoticesDO.getCompanyId(), true);
            }
//            // 回写出运单明细开票数量  (出运数据在创建时已经回写)
//            updateShipmentItemInvoiceData(invoicingNoticesItems);

        }
    }

    @Override
    public PageResult<InvoicingNoticesItemResp> getInvoicingNoticesItemList(InvoicingNoticesPageReqVO pageReqVO) {
        String venderCode = pageReqVO.getVenderCode();
        LambdaQueryWrapperX<InvoicingNoticesDO> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eqIfPresent(InvoicingNoticesDO::getVenderCode, venderCode);
        queryWrapperX.eq(InvoicingNoticesDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        List<InvoicingNoticesDO> invoicingNoticesDOList = invoicingNoticesMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(invoicingNoticesDOList)) {
            return PageResult.empty();
        }
        List<String> parentCodeList = invoicingNoticesDOList.stream().map(InvoicingNoticesDO::getCode).toList();
        LambdaQueryWrapperX<InvoicingNoticesItem> itemQueryWrapperX = new LambdaQueryWrapperX<>();
        itemQueryWrapperX.in(InvoicingNoticesItem::getInvoicNoticesCode, parentCodeList);
        itemQueryWrapperX.neIfPresent(InvoicingNoticesItem::getInveicRegisteredStatus, RegisteredStatusEnum.REGISTERED.getValue());
        if (StrUtil.isNotEmpty(pageReqVO.getInvoiceCode())) {
            itemQueryWrapperX.likeIfPresent(InvoicingNoticesItem::getShipInvoiceCode, pageReqVO.getInvoiceCode());
        }
        if (StrUtil.isNotEmpty(pageReqVO.getPurchaseContractCode())) {
            itemQueryWrapperX.likeIfPresent(InvoicingNoticesItem::getPurchaseContractCode, pageReqVO.getPurchaseContractCode());
        }
        PageResult<InvoicingNoticesItem> invoicingNoticesDOPageResult = invoicingNoticesItemMapper.selectPage(pageReqVO, itemQueryWrapperX);
        if (CollUtil.isEmpty(invoicingNoticesDOPageResult.getList())) {
            return PageResult.empty();
        }
        Map<String, InvoicingNoticesDO> invoicingNoticesDOMap = invoicingNoticesDOList.stream().collect(Collectors.toMap(InvoicingNoticesDO::getCode, s -> s));
        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        Map<String, String> venderNameCache = venderService.getVenderNameCache(CommonDict.EMPTY_STR);
        return InvoicingNoticesConvert.INSTANCE.convertPageResult(invoicingNoticesDOPageResult, invoicingNoticesDOMap, simpleCompanyDTO, venderNameCache);
    }


    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public InvoicingNoticesRespVO getInvoicingNotices(InvoicingNoticesDetailReq invoicingNoticesDetailReq) {
        Long invoicingNoticesId = Objects.nonNull(invoicingNoticesDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(invoicingNoticesDetailReq.getProcessInstanceId()) : invoicingNoticesDetailReq.getInvoicingNoticesId();
        if (Objects.isNull(invoicingNoticesId)) {
            logger.error("[开票通知]未获取到开票通知id");
            return null;
        }
        InvoicingNoticesDO invoicingNoticesDO = invoicingNoticesMapper.selectById(invoicingNoticesId);
        if (invoicingNoticesDO == null) {
            return null;
        }
        String invoicingNoticesCode = invoicingNoticesDO.getCode();
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesCode);
        InvoicingNoticesRespVO invoicingNoticesRespVO = InvoicingNoticesConvert.INSTANCE.convertInvoicingNoticesRespVO(invoicingNoticesDO);
        ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(invoicingNoticesRespVO.getShipmentCode());
        if (Objects.nonNull(shipmentDTO)) {
            //invoicingNoticesRespVO.setShipInvoiceCode(shipmentDTO.getInvoiceCode());
            invoicingNoticesRespVO.setShipDate(shipmentDTO.getShipDate());
        }

        //采购序号  purchaseSortNum
        List<Long> itemIdList = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getPurchaseContractItemId).distinct().toList();
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, itemIdList);
        if (Objects.nonNull(itemDOList)) {
            invoicingNoticesItems.forEach(s -> {
                Optional<PurchaseContractItemDO> first = itemDOList.stream().filter(i -> Objects.equals(i.getId(), s.getPurchaseContractItemId())).findFirst();
                first.ifPresent(purchaseContractItemDO -> {
                    s.setPurchaseSortNum(purchaseContractItemDO.getSortNum());
                    s.setInvoicedQuantity(purchaseContractItemDO.getInvoicedQuantity());
                });
            });
        }
        invoicingNoticesRespVO.setChildren(invoicingNoticesItems);
        List<OperateLogRespDTO> operateLogRespDTOList = operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, invoicingNoticesRespVO.getCode());
        invoicingNoticesRespVO.setOperateLogRespDTOList(operateLogRespDTOList);
        invoicingNoticesRespVO.setChildren(groupItem(invoicingNoticesRespVO.getChildren()));
        return invoicingNoticesRespVO;
    }

    private List<InvoicingNoticesItem> groupItem(List<InvoicingNoticesItem> children) {
        if (CollUtil.isEmpty(children)) {
            return children;
        }
        Map<String, List<InvoicingNoticesItem>> groupedItems = children.stream()
                .collect(Collectors.groupingBy(item ->
                        String.format("%s|%s|%s|%s",
                                item.getInvoicSkuName(),
                                item.getInvoicPrice(),
                                item.getHsMeasureUnit(),
                                item.getPurchaseContractCode()
                        )
                ));
        List<InvoicingNoticesItem> res = new ArrayList<>();
        groupedItems.forEach((key, value) -> {
            InvoicingNoticesItem aggregatedItem = new InvoicingNoticesItem();
            org.springframework.beans.BeanUtils.copyProperties(value.get(0), aggregatedItem);

            aggregatedItem.setBaseInvoiceQuantity(value.stream().mapToInt(InvoicingNoticesItem::getBaseInvoiceQuantity).sum());        //转化前开票数量
            aggregatedItem.setInvoicNoticesQuantity(value.stream().map(InvoicingNoticesItem::getInvoicNoticesQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));        //开票数量
            aggregatedItem.setTotalAmount(value.stream().map(InvoicingNoticesItem::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));        //总金额
            aggregatedItem.setPurchaseTotalQuantity(value.stream().mapToInt(InvoicingNoticesItem::getPurchaseTotalQuantity).sum());  //采购数量
            aggregatedItem.setDeclarationQuantity(value.stream().mapToInt(InvoicingNoticesItem::getDeclarationQuantity).sum());  //报关数量
            aggregatedItem.setInveicRegisteredQuantity(value.stream().mapToInt(InvoicingNoticesItem::getInveicRegisteredQuantity).sum());  //已登票数量

            //采购单价，含税单价
            AtomicReference<BigDecimal> withTaxTotal = new AtomicReference<>(BigDecimal.ZERO);
            String purchaseCurrency = aggregatedItem.getPurchaseCurrency();
            value.forEach(s -> {
                BigDecimal withTaxAmount = Optional.ofNullable(s.getPurchaseWithTaxPrice()).map(JsonAmount::getAmount).orElse(BigDecimal.ZERO);
                BigDecimal quantity = Optional.ofNullable(s.getPurchaseTotalQuantity()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                withTaxTotal.set(withTaxTotal.get().add(withTaxAmount.multiply(quantity)));
            });
            if (aggregatedItem.getPurchaseTotalQuantity() == 0) {
                aggregatedItem.setPurchaseWithTaxPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(purchaseCurrency));
            } else {
                BigDecimal quantity = BigDecimal.valueOf(aggregatedItem.getPurchaseTotalQuantity());
                // 此处不进行缩进  交给前端处理
                BigDecimal withTaxPrice = withTaxTotal.get().divide(quantity, CalculationDict.THREE, RoundingMode.HALF_UP);
                aggregatedItem.setPurchaseWithTaxPrice(new JsonAmount().setAmount(withTaxPrice).setCurrency(purchaseCurrency));
            }
            List<Long> ids = value.stream().map(InvoicingNoticesItem::getId).distinct().toList();
            aggregatedItem.setIds(ids);
            res.add(aggregatedItem);

        });
        return res;
    }

    @Override
    public PageResult<InvoicingNoticesRespVO> getInvoicingNoticesPage(InvoicingNoticesPageReqVO pageReqVO) {
        PageResult<InvoicingNoticesDO> invoicingNoticesDOPageResult = invoicingNoticesMapper.selectPage(pageReqVO);
        List<InvoicingNoticesDO> list = invoicingNoticesDOPageResult.getList();
        if (CollUtil.isEmpty(list)) {
            return PageResult.empty();
        }
        List<InvoicingNoticesRespVO> voList = BeanUtils.toBean(list, InvoicingNoticesRespVO.class);
        List<String> shipmentCodeList = voList.stream().map(InvoicingNoticesRespVO::getShipmentCode).distinct().toList();
        Map<String, ShipmentDTO> sihpmentMap = shipmentApi.getByShipmentCodeList(shipmentCodeList);
        if (CollUtil.isNotEmpty(sihpmentMap)) {
            voList.forEach(s -> {
                ShipmentDTO shipmentDTO = sihpmentMap.get(s.getShipmentCode());
                if (Objects.nonNull(shipmentDTO)) {
                    s.setShipDate(shipmentDTO.getShipDate());
//                     s.setInputUser(shipmentDTO.getInputUser());
                    s.setShipInvoiceCode(shipmentDTO.getInvoiceCode());
                }
            });
        }
        return new PageResult<InvoicingNoticesRespVO>().setList(voList).setTotal(invoicingNoticesDOPageResult.getTotal());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId,boolean mergeFlag) {
        if (id == null) {
            throw exception(INVOICING_NOTICES_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        if (sourceType != null && sourceCode == null) {
            throw exception(SOURCECODE_NULL);
        }
        if (sourceType == null && sourceCode != null) {
            throw exception(SOURCETYPE_NULL);
        }
        InvoicingNoticesDO invoicingNoticesDO = invoicingNoticesMapper.selectById(id);
        if (invoicingNoticesDO == null) {
            throw exception(INVOICING_NOTICES_NOT_EXISTS);
        }
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesDO.getCode());
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            throw exception(INVOICING_NOTICES_ITEM_NOT_EXISTS);
        }
        invoicingNoticesItems.forEach(s->{
            BigDecimal invoicePrice = Objects.isNull(s.getInvoicPrice()) ? BigDecimal.ZERO : s.getInvoicPrice();
            BigDecimal quantity = Objects.isNull(s.getInvoicNoticesQuantity()) ? BigDecimal.ZERO : s.getInvoicNoticesQuantity();
            s.setTotalAmount(NumUtil.mul(invoicePrice, quantity));
        });
        ReportDTO reportDTO;
        if (sourceCode != null && companyId != null) {
            reportDTO = reportApi.getSourceReport(reportCode, sourceCode, sourceType, companyId);
        } else if (companyId != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, companyId);
        } else {
            reportDTO = reportApi.getReport(reportCode);
        }
        if (reportDTO == null || CollectionUtils.isEmpty(reportDTO.getAnnex())) {
            throw exception(REPORT_NULL);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        String projectPath = System.getProperty("user.dir");
        HashMap<String, Object> params = new HashMap<>();

        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(invoicingNoticesDO.getCompanyId());
        if (Objects.nonNull(companyDTO)) {
            params.put("companyName", companyDTO.getCompanyName());
            params.put("companyTel", companyDTO.getCompanyTel());
            params.put("fax", companyDTO.getCompanyFax());
            params.put("companyAddress", companyDTO.getCompanyAddress());
            params.put("taxNumb", companyDTO.getTaxNumb());
            CompanyBankRespDTO companyBankRespDTO = companyDTO.getCompanyBankRespDTO();
            if (Objects.nonNull(companyBankRespDTO)) {
                params.put("bankName", companyBankRespDTO.getBankName());
                params.put("bankCode", companyBankRespDTO.getBankCode());
                params.put("bankAddress", companyBankRespDTO.getBankAddress());
            }
        }
        Tuple tuple = venderService.getVenderNameAndTelByCode(invoicingNoticesDO.getVenderCode());
        String venderName = tuple.get(0);
        params.put("venderName", venderName);
        params.put("shipmentCode", invoicingNoticesDO.getShipInvoiceCode());
        String format = "yyyy/MM/dd/";
        String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
        params.put("date", dateString);
        if (invoicingNoticesDO.getShipDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            params.put("shipDate", dtf.format(invoicingNoticesDO.getShipDate()));
        }
        params.put("printTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 hh点mm分ss秒")));
        params.put("user", invoicingNoticesDO.getManager().getNickname());
        AdminUserRespDTO user = adminUserApi.getUser(invoicingNoticesDO.getManager().getUserId());
        if (Objects.nonNull(user)) {
            params.put("userTel", user.getMobile());
        }

        //货物明细
        RowRenderData row0 = Rows.of("采购合同", "发票品名", "计量单位", "数量", "单价", "税率%", "总金额").rowExactHeight(1).center().create();
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{20, 20, 10, 10, 10, 15, 15}).create();
        table.addRow(row0);

        List<String> purchaseContractCodeList = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getPurchaseContractCode).distinct().toList();
        Map<String, String> custPOMap = purchaseContractService.getSaleCustPOByPurchaseCodes(purchaseContractCodeList);

        List<DictDataRespDTO> unitList = dictTypeApi.getHsDataUnitList();
        if (CollUtil.isEmpty(unitList)) {
            throw exception(HSDATA_UNIT_NOT_EXISTS);
        }
        List<InvoicingNoticesItem> printItems = new ArrayList<>();
        if (mergeFlag){
            Map<String, Map<String, Map<String, List<InvoicingNoticesItem>>>> groupList = invoicingNoticesItems.stream().collect(Collectors.groupingBy(InvoicingNoticesItem::getHsMeasureUnit, Collectors.groupingBy(InvoicingNoticesItem::getInvoicSkuName, Collectors.groupingBy(InvoicingNoticesItem::getHsCode))));
            groupList.forEach((hsMeasureUnit, hsMeasureUnitMap) -> {
                hsMeasureUnitMap.forEach((invoicSkuName, invoiceSkuNameMap) -> {
                    invoiceSkuNameMap.forEach((hsCode, hsCodeList) -> {
                        if (CollUtil.isNotEmpty(hsCodeList)){
                            InvoicingNoticesItem invoicingNoticesItem = new InvoicingNoticesItem();
                            org.springframework.beans.BeanUtils.copyProperties(hsCodeList.get(0), invoicingNoticesItem);
                            BigDecimal allPrice = hsCodeList.stream().map(s->{
                                BigDecimal invoicPrice = Objects.isNull(s.getInvoicPrice())?BigDecimal.ZERO:s.getInvoicPrice();
                                BigDecimal invoiceQuantity = Objects.isNull(s.getInvoicNoticesQuantity())?BigDecimal.ZERO:s.getInvoicNoticesQuantity();
                                return NumUtil.mul(invoicPrice, invoiceQuantity);
                            }).reduce(BigDecimal::add).orElseGet(()->BigDecimal.ZERO);
                            BigDecimal allQuantity = hsCodeList.stream().map(InvoicingNoticesItem::getInvoicNoticesQuantity).reduce(BigDecimal::add).orElseGet(()->BigDecimal.ZERO);
                            invoicingNoticesItem.setInvoicPrice(NumUtil.div(allPrice,allQuantity).setScale(CalculationDict.THREE,RoundingMode.HALF_UP));
                            invoicingNoticesItem.setInvoicNoticesQuantity(allQuantity);
                            invoicingNoticesItem.setTotalAmount(allPrice);
                            printItems.add(invoicingNoticesItem);
                        }
                    });
                });
            });
        }else {
            printItems.addAll(invoicingNoticesItems);
        }
        printItems.forEach(it -> {
            InvoicingNoticesPrintSku sku = new InvoicingNoticesPrintSku();
            String purchaseContractCode = it.getPurchaseContractCode();
            sku.setPurchaseContractCode(purchaseContractCode);
            if (CollUtil.isNotEmpty(custPOMap)) {
                sku.setCustPo(custPOMap.get(purchaseContractCode));
            }
            sku.setTaxRate(it.getTaxRate());
            sku.setSkuName(it.getInvoicSkuName());
            String hsMeasureUnit = Objects.isNull(it.getHsMeasureUnit()) ? CommonDict.EMPTY_STR : it.getHsMeasureUnit();
            Optional<DictDataRespDTO> first = unitList.stream().filter(s -> Objects.equals(s.getValue(), hsMeasureUnit)).findFirst();
            first.ifPresent(dictDataRespDTO -> sku.setUnit(dictDataRespDTO.getLabel()));

            sku.setQuantity(it.getInvoicNoticesQuantity().intValue());

            sku.setPrice(it.getInvoicPrice().setScale(3, RoundingMode.HALF_UP));

            sku.setTotalAmount((NumberUtil.mul(it.getInvoicPrice(), it.getInvoicNoticesQuantity())).setScale(3, RoundingMode.HALF_UP));

            invoicingNoticesDO.setPrintDate(LocalDateTime.now());

            RowRenderData dataRow = Rows.of(
                    sku.getPurchaseContractCode(),
                    sku.getSkuName(),
                    sku.getUnit(),
                    sku.getQuantity().toString(),
                    String.format("%.3f", sku.getPrice()),
                    sku.getTaxRate().stripTrailingZeros().toPlainString(),
                    String.format("%.3f", sku.getTotalAmount())
            ).rowExactHeight(1).center().create();
            table.addRow(dataRow);
        });
        
        // 使用Stream计算总金额
        BigDecimal totalAmount = invoicingNoticesItems.stream()
                .map(InvoicingNoticesItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, RoundingMode.HALF_UP);
        
        // 添加总金额合计行（合并单元格显示）
        RowRenderData totalRow = Rows.of(
                "总金额合计",
                "",
                "",
                "",
                "",
                "",
                String.format("%.3f", totalAmount)
        ).rowExactHeight(1).center().create();
        List<ParagraphRenderData> totalParagraphs = totalRow.getCells().get(0).getParagraphs();
        ParagraphStyle totalStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.RIGHT) // 右对齐
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        totalParagraphs.get(0).setParagraphStyle(totalStyle);
        table.addRow(totalRow);
        int lastRowIndex = table.getRows().size() - 1;
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        ruleBuilder.map(MergeCellRule.Grid.of(lastRowIndex, 0), MergeCellRule.Grid.of(lastRowIndex, 5));
        table.setMergeRule(ruleBuilder.build());
        params.put("table", table);

        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params, invoicingNoticesDO.getCode()+"开票通知");
        //首次打印更新打印状态和打印时间
        if (!invoicingNoticesDO.getPrintFlag().equals(PrintStatusEnum.PRINTED.getStatus())) {
            invoicingNoticesDO.setPrintFlag(PrintStatusEnum.PRINTED.getStatus());
            invoicingNoticesMapper.updateById(invoicingNoticesDO);
        }
        // 增加操作日志
        OperateLogUtils.addOperateLog(OPERATOR_EXTS_KEY, invoicingNoticesDO.getCode(), "打印");
        return result;
    }

    @Override
    public List<SimpleVenderRespDTO> getVenderListByShipInvoiceCode(String shipInvoiceCode) {
        LambdaQueryWrapperX<InvoicingNoticesDO> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.likeIfPresent(InvoicingNoticesDO::getShipInvoiceCode, shipInvoiceCode);
        List<InvoicingNoticesDO> invoicingNoticesDOList = invoicingNoticesMapper.selectList(queryWrapperX);
        List<SimpleVenderRespDTO> respList = new ArrayList<>();
        if (!invoicingNoticesDOList.isEmpty()) {
            Map<String, List<InvoicingNoticesDO>> groupMap = invoicingNoticesDOList.stream().collect(Collectors.groupingBy(InvoicingNoticesDO::getVenderCode));
            groupMap.keySet().forEach(s -> {
                List<InvoicingNoticesDO> tmpList = groupMap.get(s);
                if (!tmpList.isEmpty()) {
                    respList.add(new SimpleVenderRespDTO().setCode(s).setName(tmpList.get(0).getVenderName()));
                }
            });
        }
        return respList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeInvoicingNotices(Long id) {
        InvoicingNoticesDO invoicingNoticesDO = validateInvoicingNoticesExists(id);
        invoicingNoticesDO.setAuditStatus(BpmProcessInstanceResultEnum.CLOSE.getResult());
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().eq(InvoicingNoticesItem::getInvoicNoticesCode, invoicingNoticesDO.getCode()));
        if (CollUtil.isNotEmpty(invoicingNoticesItems)) {
            List<Long> itemIdList = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getId).distinct().toList();
            List<PurchaseRegistrationItem> purchaseRegistrationItems = purchaseRegistrationItemMapper.selectList(new LambdaQueryWrapperX<PurchaseRegistrationItem>().select(PurchaseRegistrationItem::getRegistrationId).in(PurchaseRegistrationItem::getInvoicingNoticesItemId, itemIdList));
            if (CollUtil.isNotEmpty(purchaseRegistrationItems)){
                Set<Long> registrationIdSet = purchaseRegistrationItems.stream().map(PurchaseRegistrationItem::getRegistrationId).collect(Collectors.toSet());
                Long count = purchaseRegistrationMapper.selectCount(new LambdaQueryWrapperX<PurchaseRegistrationDO>().in(PurchaseRegistrationDO::getId, registrationIdSet).ne(PurchaseRegistrationDO::getStatus, PurchaseRegistrationStatusEnum.CASE_CLOSED.getValue()));
                if (count > 0) {
                    // 该开票通知已经进行登票，不可作废
                    throw exception(INVOICING_NOTICES_CLOSE_ERROR);
                }
            }
        }
        orderLinkApi.updateOrderLinkStatus(invoicingNoticesDO.getCode(), BusinessNameDict.INVOICING_NOTICES, invoicingNoticesDO.getLinkCodeList(), BpmProcessInstanceResultEnum.CLOSE.getResult());
        invoicingNoticesMapper.updateById(invoicingNoticesDO);
        Integer sourceType = invoicingNoticesDO.getSourceType();
        if (InvoiceNoticeSourceTypeEnum.PURCHASE.getValue().equals(sourceType)) {
            // 回写采购
            closePurchaseItem(invoicingNoticesDO);
        } else if (InvoiceNoticeSourceTypeEnum.SHIPMENT.getValue().equals(sourceType)) {
            // 回写出运
            closeShipmentItem(invoicingNoticesDO);
        }
    }

    @Override
    public Map<Long, Integer> getInvoiceQuantityByPurchaseItemIds(List<Long> purchaseItemIds) {
        if (CollUtil.isEmpty(purchaseItemIds)){
            return Map.of();
        }
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().select(InvoicingNoticesItem::getPurchaseContractItemId, InvoicingNoticesItem::getBaseInvoiceQuantity, InvoicingNoticesItem::getInvoicNoticesCode).in(InvoicingNoticesItem::getPurchaseContractItemId, purchaseItemIds));
        if (CollUtil.isEmpty(invoicingNoticesItems)){
            return Map.of();
        }
        List<String> invoiceNoticeCodes = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getInvoicNoticesCode).distinct().toList();
        LambdaQueryWrapper<InvoicingNoticesDO> queryWrapper = new LambdaQueryWrapperX<InvoicingNoticesDO>().select(InvoicingNoticesDO::getCode).in(InvoicingNoticesDO::getCode, invoiceNoticeCodes).ne(InvoicingNoticesDO::getAuditStatus, BpmProcessInstanceResultEnum.CLOSE.getResult());
        List<InvoicingNoticesDO> invoicingNoticesDOS = invoicingNoticesMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(invoicingNoticesDOS)){
            return Map.of();
        }
        Set<String> availableInvoiceCodeSet = invoicingNoticesDOS.stream().map(InvoicingNoticesDO::getCode).collect(Collectors.toSet());
        return invoicingNoticesItems.stream().filter(s->availableInvoiceCodeSet.contains(s.getInvoicNoticesCode()))
                .collect(Collectors.groupingBy(InvoicingNoticesItem::getPurchaseContractItemId,Collectors.summingInt(InvoicingNoticesItem::getBaseInvoiceQuantity)));
    }

    /**
     * 作废开票通知回写出运开票信息
     *
     * @param invoicingNoticesDO 开票通知单据信息
     */
    private void closeShipmentItem(InvoicingNoticesDO invoicingNoticesDO) {
        String invoiceCode = invoicingNoticesDO.getCode();
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().eq(InvoicingNoticesItem::getInvoicNoticesCode, invoiceCode));
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return;
        }
        List<Long> shipmentItemIdList = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getShipmentItemId).distinct().toList();
        List<ShipmentItemDTO> shipmentItemDTOList = shipmentApi.getShipmentItemDTOListByIdList(shipmentItemIdList);
        if (CollUtil.isEmpty(shipmentItemDTOList)) {
            return;
        }
        Map<Long, InvoicingNoticesItem> invoiceMap = invoicingNoticesItems.stream().collect(Collectors.toMap(InvoicingNoticesItem::getShipmentItemId, s -> s, (o, n) -> n));
        List<InvoiceQuantityDTO> updateList = new ArrayList<>();
        shipmentItemDTOList.forEach(s -> {
            InvoicingNoticesItem invoicingNoticesItem = invoiceMap.get(s.getId());
            if (Objects.isNull(invoicingNoticesItem)) {
                return;
            }
            String invoiceUnit = invoicingNoticesItem.getHsMeasureUnit();
            BigDecimal baseInvoiceQuantity = BigDecimal.valueOf(invoicingNoticesItem.getBaseInvoiceQuantity());
            String hsMeasureUnit = s.getHsMeasureUnit();
            boolean hsMeasureUnitFlag = Objects.isNull(hsMeasureUnit) || (!hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue()) && !hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()));
            boolean invoiceUnitFlag = StrUtil.isNotEmpty(invoiceUnit) && (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) || invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()));
            BigDecimal rollBackQuantity = BigDecimal.ZERO;
            JsonWeight netWeight = CalcSpecificationUtil.calcSpecificationTotalNetweight(s.getSpecificationList());
            String unit = netWeight.getUnit();
            if (hsMeasureUnitFlag && invoiceUnitFlag) { // 采购数量/外箱装量 * 外箱净重 = 开票数量
                if (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) && CalculationDict.GRAM.equals(unit)) {
                    //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                    rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.div(baseInvoiceQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                } else if (invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()) && CalculationDict.KILOGRAM.equals(unit)) {
                    //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                    rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.mul(baseInvoiceQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                } else {
                    rollBackQuantity = NumUtil.mul(NumUtil.div(baseInvoiceQuantity, netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                }
            } else {
                rollBackQuantity = baseInvoiceQuantity;
            }
            BigDecimal invoicedquantity = s.getInvoicedQuantity();
            // 本次开票数量>未开票数量 抛异常
            if (BigDecimal.ZERO.compareTo(invoicedquantity) == 0) {
                s.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
            } else if (rollBackQuantity.compareTo(invoicedquantity) > 0) {
                throw exception(CLASE_INVOICE_QUANTITY_ENOUGH);
            } else if (rollBackQuantity.compareTo(invoicedquantity) == 0) {
                // 本次开票=未开票数量  已开票
                s.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
                s.setInvoicedQuantity(NumUtil.sub(invoicedquantity, rollBackQuantity));
            } else {
                // 本次开票<未开票数量  部分开票
                s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
                // 已开票数量+=本次开票数量
                s.setInvoicedQuantity(NumUtil.sub(invoicedquantity, rollBackQuantity));
            }
            InvoiceQuantityDTO invoiceQuantityDTO = new InvoiceQuantityDTO();
            invoiceQuantityDTO.setSaleContractItemId(s.getSaleContractItemId());
            invoiceQuantityDTO.setInvoiceQuantity(rollBackQuantity);
            invoiceQuantityDTO.setPurchaseContractCode(s.getPurchaseContractCode());
            invoiceQuantityDTO.setVenderCode(s.getVenderCode());
            updateList.add(invoiceQuantityDTO);
        });
        shipmentApi.updateShipmentItem(shipmentItemDTOList, true);
        if (CollUtil.isNotEmpty(updateList)) {
            purchaseContractService.updateInvoiceData(updateList,true);
        }
    }

    /**
     * 作废回写采购明细开票信息
     *
     * @param invoicingNoticesDO 开票通知单据信息
     */
    private void closePurchaseItem(InvoicingNoticesDO invoicingNoticesDO) {
        String invoiceCode = invoicingNoticesDO.getCode();
        List<InvoicingNoticesItem> invoicingNoticesItems = invoicingNoticesItemMapper.selectList(new LambdaQueryWrapperX<InvoicingNoticesItem>().eq(InvoicingNoticesItem::getInvoicNoticesCode, invoiceCode));
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return;
        }
        List<Long> purchaseItemIdList = invoicingNoticesItems.stream().map(InvoicingNoticesItem::getPurchaseContractItemId).distinct().toList();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectBatchIds(purchaseItemIdList);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return;
        }
        Map<Long, InvoicingNoticesItem> invoiceMap = invoicingNoticesItems.stream().collect(Collectors.toMap(InvoicingNoticesItem::getPurchaseContractItemId, s -> s, (o, n) -> n));
        purchaseContractItemDOS.forEach(s -> {
            InvoicingNoticesItem invoicingNoticesItem = invoiceMap.get(s.getId());
            if (Objects.isNull(invoicingNoticesItem)) {
                return;
            }
            String invoiceUnit = invoicingNoticesItem.getHsMeasureUnit();
            BigDecimal invoicNoticesQuantity = invoicingNoticesItem.getInvoicNoticesQuantity();
            String skuCode = s.getSkuCode();
            String hsMeasureUnit = skuApi.getHsMeasureUnitBySkuCode(skuCode);
            boolean hsMeasureUnitFlag = StrUtil.isEmpty(hsMeasureUnit) || (!hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue()) && !hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()));
            boolean invoiceUnitFlag = StrUtil.isNotEmpty(invoiceUnit) && (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) || invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()));
            BigDecimal rollBackQuantity = BigDecimal.ZERO;
            JsonWeight netWeight = CalcSpecificationUtil.calcSpecificationTotalNetweight(s.getSpecificationList());
            String unit = netWeight.getUnit();
            if (hsMeasureUnitFlag && invoiceUnitFlag) { // 采购数量/外箱装量 * 外箱净重 = 开票数量
                if (invoiceUnit.equals(HsMeasureUnitEnum.KG.getName()) && CalculationDict.GRAM.equals(unit)) {
                    //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                    rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.mul(invoicNoticesQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                } else if (invoiceUnit.equals(HsMeasureUnitEnum.KE.getName()) && CalculationDict.KILOGRAM.equals(unit)) {
                    //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                    rollBackQuantity = NumUtil.mul(NumUtil.div(NumUtil.div(invoicNoticesQuantity, CalculationDict.ONE_THOUSAND), netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                } else {
                    rollBackQuantity = NumUtil.mul(NumUtil.div(invoicNoticesQuantity, netWeight.getWeight()), s.getQtyPerOuterbox()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
                }
            } else {

                if (HsMeasureUnitEnum.KG.getValue().equals(hsMeasureUnit) && HsMeasureUnitEnum.KE.getValue().equals(invoiceUnit)) {
                    rollBackQuantity = NumUtil.div(invoicNoticesQuantity, CalculationDict.ONE_THOUSAND);
                } else if (HsMeasureUnitEnum.KE.getValue().equals(hsMeasureUnit) && HsMeasureUnitEnum.KG.getValue().equals(invoiceUnit)) {
                    rollBackQuantity = NumUtil.mul(invoicNoticesQuantity, CalculationDict.ONE_THOUSAND);
                } else {
                    // 单位相同则直接回退数量
                    rollBackQuantity = invoicNoticesQuantity;
                }
            }
            Integer invoicedquantity = s.getInvoicedQuantity();
            // 本次开票数量>未开票数量 抛异常
            if (invoicedquantity == null || invoicedquantity == 0) {
                s.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
            } else if (rollBackQuantity.intValue() > invoicedquantity) {
                throw exception(CLASE_INVOICE_QUANTITY_ENOUGH);
            } else if (rollBackQuantity.intValue() == invoicedquantity) {
                // 本次开票=未开票数量  已开票
                s.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
                s.setInvoicedQuantity(invoicedquantity - rollBackQuantity.intValue());
            } else {
                // 本次开票<未开票数量  部分开票
                s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
                // 已开票数量+=本次开票数量
                s.setInvoicedQuantity(invoicedquantity - rollBackQuantity.intValue());
            }
        });
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOS);
    }

    /**
     * 获取第一条明细对应的采购合同的采购类型
     *
     * @param invoicingNoticesItems 开票通知明细列表
     * @return 采购类型，如果获取失败则返回null
     */
    private Integer getPurchaseTypeFromItems(List<InvoicingNoticesItem> invoicingNoticesItems) {
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return null;
        }
        InvoicingNoticesItem firstItem = invoicingNoticesItems.get(0);
        if (firstItem.getPurchaseContractItemId() == null) {
            return null;
        }
        try {
            PurchaseContractItemDO purchaseContractItemDO = purchaseContractItemMapper.selectById(firstItem.getPurchaseContractItemId());
            if (purchaseContractItemDO == null) {
                return null;
            }    
            return purchaseContractItemDO.getPurchaseType();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取明细对应第一条销售合同的销售类型
     *
     * @param invoicingNoticesItems 开票通知明细列表
     * @return 销售类型，如果获取失败则返回null
     */
    private Integer getSaleTypeFromItems(List<InvoicingNoticesItem> invoicingNoticesItems) {
        if (CollUtil.isEmpty(invoicingNoticesItems)) {
            return null;
        }
        InvoicingNoticesItem firstItem = invoicingNoticesItems.get(0);
        if (firstItem.getSaleContractItemId() == null) {
            return null;
        }  
        try {
            SaleContractItemDTO saleContractItemDTO = saleContractApi.getSaleContractItemById(firstItem.getSaleContractItemId());
            if (saleContractItemDTO == null || saleContractItemDTO.getSaleType() == null) {
                return null;
            }
            return saleContractItemDTO.getSaleType();
        } catch (Exception e) {
            return null;
        }
    }
}