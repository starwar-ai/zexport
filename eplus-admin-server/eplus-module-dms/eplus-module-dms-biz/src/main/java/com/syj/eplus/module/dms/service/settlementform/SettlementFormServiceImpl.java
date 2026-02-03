package com.syj.eplus.module.dms.service.settlementform;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.settlement.SettlementApi;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.framework.common.enums.OrderLinkTypeEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.SplitUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.*;
import com.syj.eplus.module.dms.convert.settlementform.SettlementFormConvert;
import com.syj.eplus.module.dms.dal.dataobject.settlementform.SettlementFormDO;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import com.syj.eplus.module.dms.dal.mysql.settlementform.SettlementFormMapper;
import com.syj.eplus.module.dms.dal.mysql.settlementformitem.SettlementFormItemMapper;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.sms.api.AddSubItemApi;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 结汇单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class SettlementFormServiceImpl implements SettlementFormService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SettlementFormMapper settlementFormMapper;
    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    private SettlementFormItemMapper settlementFormItemMapper;

    @Resource
    private AddSubItemApi addSubItemApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private ReportApi reportApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    CustApi custApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private CompanyApi companyApi;

    @Resource
    private SkuApi skuApi;

    @Resource
    private RateApi rateApi;

    @Resource
    private SettlementApi settlementApi;



    private static final String SN_TYPE = "settlement_form";
    private static final String CODE_PREFIX = "JH";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedResponse createSettlementForm(SettlementFormSaveReqVO createReqVO) {
        SettlementFormDO settlementForm = SettlementFormConvert.INSTANCE.convertSettlementFormDO(createReqVO);
        // 生成 结汇单 编号
        settlementForm.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 插入
        settlementFormMapper.insert(settlementForm);
        // 插入明细
        List<SettlementFormItem> settlementFormItemList = createReqVO.getChildren();
        String custName = null;
        if (CollUtil.isNotEmpty(settlementFormItemList)) {
            settlementFormItemList.forEach(s -> {
                s.setSettlementFormId(settlementForm.getId());
            });
            settlementFormItemMapper.insertBatch(settlementFormItemList);
            custName = settlementFormItemList.get(0).getCustName();
        }
        // 计算合计信息
        settlementFormItemList.stream().map(SettlementFormItem::getTotalGrossweight).reduce(JsonWeight::add).ifPresent(settlementForm::setTotalGrossweight);
        settlementFormItemList.stream().map(SettlementFormItem::getTotalVolume).reduce(BigDecimal::add).ifPresent(settlementForm::setTotalVolume);
        settlementFormItemList.stream().map(SettlementFormItem::getTotalNetweight).reduce(JsonWeight::add).ifPresent(settlementForm::setTotalWeight);
        settlementFormMapper.updateById(settlementForm);
        List<String> linkCodeList = settlementForm.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)) {
            String finalCustName = custName;
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(settlementForm.getId())
                    .setCode(settlementForm.getCode())
                    .setName(BusinessNameDict.SETTLEMENT_NAME)
                    .setType(OrderLinkTypeEnum.SETTLEMENT.getValue())
                    .setLinkCode(s)
                    .setItem(settlementFormItemList)
                    .setParentCode(settlementForm.getShipmentCode())
                    .setStatus(settlementForm.getStatus())
                    .setCompanyId(settlementForm.getForeignTradeCompanyId())
                    .setBusinessSubjectName(finalCustName)
                    .setOrderMsg(settlementForm)).toList();
            // 插入单据链路
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        // 返回
        return new CreatedResponse(settlementForm.getId(),settlementForm.getCode());
    }

    @Override
    public void updateSettlementForm(SettlementFormSaveReqVO updateReqVO) {
        // 校验存在
        validateSettlementFormExists(updateReqVO.getId());
        // 更新
        SettlementFormDO updateObj = SettlementFormConvert.INSTANCE.convertSettlementFormDO(updateReqVO);
        // 更新明细
        List<SettlementFormItem> settlementFormList = updateReqVO.getChildren();
        settlementFormItemMapper.delete(SettlementFormItem::getSettlementFormId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(settlementFormList)) {
            settlementFormList.forEach(s -> {
                s.setSettlementFormId(updateReqVO.getId());
                s.setId(null);
            });
            settlementFormItemMapper.insertBatch(settlementFormList);
        }

        // 更新加减项
        List<AddSubItemDTO> addSubItemList = updateReqVO.getAddSubItemList();
        addSubItemApi.deleteByContractCode(updateReqVO.getSaleContractCode());
        if (CollUtil.isNotEmpty(addSubItemList)) {
            addSubItemList.forEach(s -> {
                s.setId(null);
                s.setContractCode(updateReqVO.getSaleContractCode());
            });
            addSubItemApi.insertBatch(addSubItemList);
        }
        settlementFormMapper.updateById(updateObj);
    }

    @Override
    @Deprecated
    public void deleteSettlementForm(Long id) {
        // 校验存在
        SettlementFormDO settlementFormDO = settlementFormMapper.selectById(id);
        if (settlementFormDO == null) {
            throw exception(SETTLEMENT_FORM_NOT_EXISTS);
        }
        ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(settlementFormDO.getShipmentCode());
        if (Objects.nonNull(shipmentDTO)) {
            List<ShipmentItemDTO> shipmentItemDTOList = shipmentApi.getShipmentItemDTOListByShipmentId(shipmentDTO.getId());
            if (CollUtil.isNotEmpty(shipmentItemDTOList)) {
                shipmentItemDTOList.forEach(item -> {
                    item.setIsToSettlementForm(BooleanEnum.NO.getValue());
                });
                shipmentApi.updateShipmentItem(shipmentItemDTOList, false);
            }
        }
        // 删除
        settlementFormMapper.deleteById(id);
        orderLinkApi.updateOrderLinkStatus(settlementFormDO.getCode(), BusinessNameDict.SETTLEMENT_NAME, settlementFormDO.getLinkCodeList(), BooleanEnum.NO.getValue());
        // 删除明细
        settlementFormItemMapper.delete(SettlementFormItem::getSettlementFormId, id);
    }

    @Override
    public void batchDeleteSettlementForm(List<Long> idList) {
        if (CollUtil.isEmpty(idList)){
            return;
        }
        settlementFormMapper.deleteBatchIds(idList);
        List<SettlementFormItem> settlementFormItems = settlementFormItemMapper.selectList(SettlementFormItem::getSettlementFormId, idList);
        if (CollUtil.isEmpty(settlementFormItems)){
            return;
        }
        List<Long> shipmentItemIdList = settlementFormItems.stream().map(SettlementFormItem::getShipmentItemId).distinct().toList();
        if (CollUtil.isEmpty(shipmentItemIdList)){
            return;
        }
        // 回写出运明细
        shipmentApi.rollbackShipmentByDeletedSettlement(shipmentItemIdList);
        settlementFormItemMapper.deleteBatchIds(settlementFormItems);

    }

    private void validateSettlementFormExists(Long id) {
        if (settlementFormMapper.selectById(id) == null) {
            throw exception(SETTLEMENT_FORM_NOT_EXISTS);
        }
    }

    @Override
    public SettlementFormRespVO getSettlementForm(Long id) {
        SettlementFormDO settlementFormDO = settlementFormMapper.selectById(id);
        if (settlementFormDO == null) {
            return null;
        }
        SettlementFormRespVO settlementFormRespVO = SettlementFormConvert.INSTANCE.convertSettlementFormRespVO(settlementFormDO);
        if (Objects.nonNull(settlementFormDO.getShipmentCode())) {
            List<String> shipmentCodeList = SplitUtil.splitToStringList(CommonDict.COMMA,settlementFormDO.getShipmentCode());
            List<ShipmentDTO> shipmentDTOList = shipmentApi.getShipmentListByCode(shipmentCodeList);
            if (CollUtil.isNotEmpty(shipmentDTOList)) {
                String inputUserName = shipmentDTOList.stream().map(ShipmentDTO::getInputUser).filter(Objects::nonNull).map(UserDept::getNickname).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
                settlementFormRespVO.setInputUserName(inputUserName);
                String documenterUserName = shipmentDTOList.stream().map(ShipmentDTO::getDocumenter).filter(Objects::nonNull).map(UserDept::getNickname).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
                settlementFormRespVO.setDocumenterUserName(documenterUserName);
                String shipNum = shipmentDTOList.stream().map(ShipmentDTO::getShipNum).collect(Collectors.joining(CommonDict.COMMA));
                settlementFormRespVO.setShipNum(shipNum);
                String billLadingNum = shipmentDTOList.stream().map(ShipmentDTO::getBillLadingNum).collect(Collectors.joining(CommonDict.COMMA));
                settlementFormRespVO.setBillLadingNum(billLadingNum);
                List<JsonAmount> totalGoodsValue = sumAmountsByCurrency(shipmentDTOList, ShipmentDTO::getTotalGoodsValue);
                List<JsonAmount> totalDeclaration = sumAmountsByCurrency(shipmentDTOList, ShipmentDTO::getTotalDeclaration);
                List<JsonAmount> totalPurchase = sumAmountsByCurrency(shipmentDTOList, ShipmentDTO::getTotalPurchase);
                settlementFormRespVO.setTotalGoodsValue(totalGoodsValue);
                settlementFormRespVO.setTotalDeclaration(totalDeclaration);
                settlementFormRespVO.setTotalPurchase(totalPurchase);
            }
        }

        //获取明细
        List<SettlementFormItem> settlementFormItemList = settlementFormItemMapper.selectList(SettlementFormItem::getSettlementFormId, id);
        settlementFormRespVO.setChildren(settlementFormItemList);
        // 获取加减项
        List<String> saleContractCodeList = settlementFormItemList.stream().map(SettlementFormItem::getSaleContractCode).filter(Objects::nonNull).toList();
        settlementFormRespVO.setAddSubItemList(addSubItemApi.getAddSubItemListByContractCodeList(saleContractCodeList));
//        settlementFormRespVO.setChildren(groupItem(settlementFormRespVO.getChildren()));
        return settlementFormRespVO;
    }
    /**
     * 根据货币类型对金额进行汇总
     * @param shipmentDTOList 出运列表
     * @param mapper 映射函数
     * @return 结果
     */
    private List<JsonAmount> sumAmountsByCurrency(List<ShipmentDTO> shipmentDTOList, Function<ShipmentDTO, List<JsonAmount>> mapper) {
        return shipmentDTOList.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        JsonAmount::getCurrency,
                        Collectors.reducing(BigDecimal.ZERO, JsonAmount::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .map(entry -> new JsonAmount().setAmount(entry.getValue()).setCurrency(entry.getKey()))
                .toList();
    }

    private List<SettlementFormItem> groupItem(List<SettlementFormItem> children) {
        if (CollUtil.isEmpty(children)) {
            return children;
        }
        Map<String, List<SettlementFormItem>> groupedItems = children.stream()
                .collect(Collectors.groupingBy(item ->
                        String.format("%s|%s|%s|%s|%s|%s",
                                item.getCustomsDeclarationNameEng(),
                                item.getTaxRefundRate(),
                                item.getHsCode(),
                                item.getDeclarationName(),
                                item.getHsMeasureUnit(),
                                item.getPurchaseContractCode()
                        )
                ));
        List<SettlementFormItem> res = new ArrayList<>();
        groupedItems.forEach((key, value) -> {
            SettlementFormItem aggregatedItem = new SettlementFormItem();
            org.springframework.beans.BeanUtils.copyProperties(value.get(0), aggregatedItem);

            aggregatedItem.setShippingQuantity(value.stream().mapToInt(SettlementFormItem::getShippingQuantity).sum());        //出运数量
            if (aggregatedItem.getShippingQuantity() == 0) {
                throw exception(SHIPPING_QUANTITY_ZERO);
            }
            aggregatedItem.setDeclarationQuantity(value.stream().mapToInt(SettlementFormItem::getDeclarationQuantity).sum());  //报关数量
            if (aggregatedItem.getDeclarationQuantity() == 0) {
                throw exception(DECLARATION_QUANTITY_ZERO);
            }
            aggregatedItem.setPurchaseTotalQuantity(value.stream().mapToInt(SettlementFormItem::getPurchaseTotalQuantity).sum());  //采购数量
            aggregatedItem.setDeclaredQuantity(value.stream().mapToInt(SettlementFormItem::getDeclaredQuantity).sum());        //已报关数量
            aggregatedItem.setBoxCount(value.stream().mapToInt(SettlementFormItem::getBoxCount).sum());                        //箱数
            aggregatedItem.setTotalVolume(value.stream().map(SettlementFormItem::getTotalVolume).reduce(BigDecimal.ZERO, BigDecimal::add));      //总体积

            //佣金金额
            List<JsonAmount> commAmounts = value.stream().map(SettlementFormItem::getCommissionAmount).filter(Objects::nonNull).toList();
            if (!commAmounts.isEmpty()) {
                String currency = commAmounts.get(0).getCurrency();
                BigDecimal total = commAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setCommissionAmount(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //销售金额
            List<JsonAmount> saleAmounts = value.stream().map(SettlementFormItem::getSaleAmount).filter(Objects::nonNull).toList();
            if (!saleAmounts.isEmpty()) {
                String currency = saleAmounts.get(0).getCurrency();
                BigDecimal total = saleAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setSaleAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                BigDecimal unitPrice = total.divide(BigDecimal.valueOf(aggregatedItem.getDeclarationQuantity()), 2, RoundingMode.HALF_UP);
                aggregatedItem.setSaleUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
            }

            //退税金额
            List<JsonAmount> refundAmounts = value.stream().map(SettlementFormItem::getTaxRefundPrice).filter(Objects::nonNull).toList();
            if (!refundAmounts.isEmpty()) {
                String currency = refundAmounts.get(0).getCurrency();
                BigDecimal total = refundAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTaxRefundPrice(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //保险费
            List<JsonAmount> insuranceAmounts = value.stream().map(SettlementFormItem::getInsuranceFee).filter(Objects::nonNull).toList();
            if (!insuranceAmounts.isEmpty()) {
                String currency = insuranceAmounts.get(0).getCurrency();
                BigDecimal total = insuranceAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setInsuranceFee(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //报关金额
            List<JsonAmount> declarationAmounts = value.stream().map(SettlementFormItem::getDeclarationAmount).filter(Objects::nonNull).toList();
            if (!declarationAmounts.isEmpty()) {
                String currency = declarationAmounts.get(0).getCurrency();
                BigDecimal total = declarationAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setDeclarationAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                BigDecimal unitPrice = total.divide(BigDecimal.valueOf(aggregatedItem.getDeclarationQuantity()), 2, RoundingMode.HALF_UP);
                aggregatedItem.setDeclarationUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
            }

            //总净重
            List<JsonWeight> grossWeight = value.stream().map(SettlementFormItem::getTotalGrossweight).filter(Objects::nonNull).toList();
            if (!grossWeight.isEmpty()) {
                String unit = grossWeight.get(0).getUnit();
                BigDecimal total = grossWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTotalGrossweight(new JsonWeight().setWeight(total).setUnit(unit));
            }

            //总毛重
            List<JsonWeight> netWeight = value.stream().map(SettlementFormItem::getTotalNetweight).filter(Objects::nonNull).toList();
            if (!netWeight.isEmpty()) {
                String unit = netWeight.get(0).getUnit();
                BigDecimal total = netWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTotalNetweight(new JsonWeight().setWeight(total).setUnit(unit));
            }

            res.add(aggregatedItem);

        });
        return res;
    }


    @Override
    public PageResult<SettlementFormRespVO> getSettlementFormPage(SettlementFormPageReqVO pageReqVO) {
        PageResult<SettlementFormDO> settlementFormDOPageResult = settlementFormMapper.selectPage(pageReqVO);
        List<SettlementFormDO> settlementFormDOList = settlementFormDOPageResult.getList();
        if (CollUtil.isEmpty(settlementFormDOList)) {
            return null;
        }
        List<Long> idList = settlementFormDOList.stream().map(SettlementFormDO::getId).distinct().toList();
        Map<Long, List<SettlementFormItem>> settlementFormItemMap = settlementFormItemMapper.getSettlementFormItemMapBySettlementId(idList);
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        List<SettlementFormRespVO> settlementFormRespVOList = SettlementFormConvert.INSTANCE.convertSettlementFormRespVO(settlementFormDOList, settlementFormItemMap, packageTypeList);
//        settlementFormRespVOList.forEach(vo -> {
//            vo.setChildren(groupItem(vo.getChildren()));
//        });
        return new PageResult<SettlementFormRespVO>().setTotal(settlementFormDOPageResult.getTotal()).setList(settlementFormRespVOList);
    }

    @Override
    public void exportExcel(Long id, String reportCode, String exportType, String sourceCode, Integer sourceType, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        SettlementFormDO settlementFormDO = settlementFormMapper.selectById(id);
        if (settlementFormDO == null) {
            throw exception(SETTLEMENT_FORM_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (settlementFormDO.getForeignTradeCompanyId() != null) {
            reportDTO = reportApi.getSourceReport(reportCode, sourceCode, sourceType, settlementFormDO.getForeignTradeCompanyId());
        }
        if (reportDTO == null) {
            throw exception(REPORT_NULL, "reportCode-" + reportCode);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        List<SettlementFormItem> settlementFormItemList = settlementFormItemMapper.selectList(new LambdaQueryWrapperX<SettlementFormItem>().eq(SettlementFormItem::getSettlementFormId, id));
        List<SettlementFormItemExportVO> settlementFormItemExportVOList = SettlementFormConvert.INSTANCE.convertSettlementFormItemExportVOList(settlementFormItemList);
        List<Long> purchaseUserIds = settlementFormItemExportVOList.stream().map(SettlementFormItemExportVO::getPurchaseUserId).distinct().toList();
        Map<Long, AdminUserRespDTO> adminUserRespDTOMap = adminUserApi.getUserMapByIdList(purchaseUserIds);
        List<Long> managerIds = settlementFormItemExportVOList.stream().map(SettlementFormItemExportVO::getManager).map(UserDept::getUserId).distinct().toList();
        Map<Long, AdminUserRespDTO> managerRespDTOMap = adminUserApi.getUserMapByIdList(managerIds);
        List<String> skuCodeList = settlementFormItemExportVOList.stream().map(SettlementFormItemExportVO::getSkuCode).distinct().toList();
        Map<String, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMapByCode(skuCodeList);
        AtomicInteger sortIndex = new AtomicInteger(1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, BigDecimal> currencyRateMap = rateApi.getDailyRateMapByDate(settlementFormDO.getInputDate());
        settlementFormItemExportVOList.forEach(item -> {
            item.setSortNum(sortIndex.getAndIncrement());
            if (item.getHsMeasureUnit() != null) {
                item.setHsMeasureUnitName("PCS");
            }
            if (item.getTaxRefundPrice() != null && item.getTaxRefundPrice().getAmount() != null) {
                item.setTaxRefundPriceAmountAmount(NumberFormatUtil.formatAmount(item.getTaxRefundPrice().getAmount() ));
            }
            if (item.getSaleUnitPrice() != null && item.getSaleUnitPrice().getAmount() != null) {

                item.setSaleUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getSaleUnitPrice().getAmount()));
            }
            if (item.getSaleAmount() != null && item.getSaleAmount().getAmount() != null) {
                item.setSaleAmountAmount(NumberFormatUtil.formatAmount(item.getSaleAmount().getAmount()));
            }
            if (item.getDeliveryDate() != null) {
                item.setDeliveryDateFormat(dtf.format(item.getDeliveryDate()));
            }
            if (item.getPurchaseUnitPrice() != null && item.getPurchaseUnitPrice().getAmount() != null) {
                item.setPurchaseUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getPurchaseUnitPrice().getAmount()));
            }
            if (item.getOuterboxGrossweight() != null && item.getOuterboxGrossweight().getWeight() != null) {
                item.setOuterboxGrossweightWeight(NumberFormatUtil.formatWeight(item.getOuterboxGrossweight().getWeight()));
            }
            if (item.getTotalGrossweight() != null && item.getTotalGrossweight().getWeight() != null) {
                item.setTotalGrossweightWeight(NumberFormatUtil.formatWeight(item.getTotalGrossweight().getWeight()));
            }
            if (item.getOuterboxNetweight() != null && item.getOuterboxNetweight().getWeight() != null) {
                item.setOuterboxNetweightWeight(NumberFormatUtil.formatWeight(item.getOuterboxNetweight().getWeight()));
            }
            if (item.getTotalNetweight() != null && item.getTotalNetweight().getWeight() != null) {
                item.setTotalNetweightWeight(NumberFormatUtil.formatWeight(item.getTotalNetweight().getWeight()));
            }
            if (item.getTaxRefundRate() != null) {
                item.setTaxRefundRate(NumberFormatUtil.formatRate(item.getTaxRefundRate()));
            }
            if (item.getManager() != null && item.getManager().getUserId() != null) {
                item.setManagerName(item.getManager().getNickname());
                AdminUserRespDTO managerRespDTO = managerRespDTOMap.get(item.getManager().getUserId());
                if (managerRespDTO != null) {
                    item.setManagerCode(managerRespDTO.getCode());
                }
            }
            if (item.getOuterboxHeight() != null) {
                item.setOuterboxHeight(NumberFormatUtil.format(item.getOuterboxHeight(), CalculationDict.TWO));
            }
            if (item.getOuterboxLength() != null) {
                item.setOuterboxLength(NumberFormatUtil.format(item.getOuterboxLength(), CalculationDict.TWO));
            }
            if (item.getOuterboxWidth() != null) {
                item.setOuterboxWidth(NumberFormatUtil.format(item.getOuterboxWidth(), CalculationDict.TWO));
            }
            if (item.getOuterboxVolume() != null) {
                item.setOuterboxVolume(VolumeUtil.processVolume(item.getOuterboxVolume()));
            }
            if (item.getTotalVolume() != null) {
                item.setTotalVolume(VolumeUtil.processVolume(item.getTotalVolume()));
            }
            if (Objects.nonNull(item.getPurchaseUserId())) {
                AdminUserRespDTO adminUserRespDTO = adminUserRespDTOMap.get(item.getPurchaseUserId());
                item.setPurchaseUserCode(adminUserRespDTO.getCode());
            }
            if ((exportType.equals("KANE PL") || exportType.equals("KANE INV"))  && Objects.nonNull(item.getSkuCode())) {
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuCode());
                item.setBasicSkuCode(simpleSkuDTO.getBasicSkuCode());
            }
            if(exportType.equals("KANE INV")){
                if(Objects.nonNull(item.getSaleUnitPrice().getCurrency()) && Objects.nonNull(item.getSaleUnitPrice().getAmount())){
                    BigDecimal currencyRate = currencyRateMap.get(item.getSaleUnitPrice().getCurrency());
                    if (Objects.nonNull(currencyRate)) {
                        BigDecimal saleUnitPriceRMB =  NumUtil.mul(item.getSaleUnitPrice().getAmount(), currencyRate);
                        BigDecimal saleUnitPriceUSD =  NumUtil.div(saleUnitPriceRMB,currencyRateMap.get("USD"));
                        item.setSaleUnitPriceAmountUSD(NumberFormatUtil.formatUnitAmount(saleUnitPriceUSD));
                    }
                }
                if(Objects.nonNull(item.getSaleAmount().getCurrency()) && Objects.nonNull(item.getSaleAmount().getAmount())){
                    BigDecimal currencyRate = currencyRateMap.get(item.getSaleAmount().getCurrency());
                    if (Objects.nonNull(currencyRate)) {
                        BigDecimal saleAmountRMB =  NumUtil.mul(item.getSaleAmount().getAmount(), currencyRate);
                        BigDecimal saleAmountUSD =  NumUtil.div(saleAmountRMB,currencyRateMap.get("USD"));
                        item.setSaleAmountUSD(NumberFormatUtil.formatUnitAmount(saleAmountUSD));
                    }
                }
            }
        });
        AtomicReference<HashMap<String, Object>> params = new AtomicReference<>(new HashMap());
        HashMap<String, Object> tableParams = new HashMap();
        switch (exportType) {
            case "detail": {
                params.set(getExportParams(settlementFormDO, settlementFormItemList));
                //加减项
                List<AddSubItemDTO> addSubItemList = new ArrayList<>();
                if (CollUtil.isNotEmpty(settlementFormItemList)) {
                    addSubItemList = addSubItemApi.getAddSubItemListByContractCodeList(List.of(settlementFormItemList.get(0).getSaleContractCode()));
                    if (addSubItemList == null) {
                        addSubItemList = new ArrayList<>();
                    }
                }
                List<SettlementFormAddSubItemExportVO> settlementFormAddSubItemExportVOList = SettlementFormConvert.INSTANCE.convertSettlementFormAddSubItemExportVOList(addSubItemList);
                HashMap<String, Object> finalParams = params.get();
                settlementFormAddSubItemExportVOList.forEach(item -> {
                    if (item.getAmount() != null) {
                        item.setAmountValue(NumberFormatUtil.formatAmount(item.getAmount().getAmount()));
                    }
                    if (item.getCalculationType() != null) {
                        String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.CALCULATION_TYPE, String.valueOf(item.getCalculationType()));
                        finalParams.put("calculationType", transportType.replace("(有订舱杂费)","").replace("(无订舱杂费)",""));
                        params.set(finalParams);
                    }
                });
                tableParams.put("addSubItem", settlementFormAddSubItemExportVOList);
                tableParams.put("childItem", settlementFormItemExportVOList);
                break;
            }
            case "KANE PL": {
                params.set(getExportOtherParams(settlementFormDO, settlementFormItemList));
                break;
            }
            case "KANE INV": {
                params.set(getExportOtherParams(settlementFormDO, settlementFormItemList));
                HashMap<String, Object> finalParams = params.get();
                BigDecimal saleAmountUSD = settlementFormItemExportVOList.stream().map(SettlementFormItemExportVO::getSaleAmountUSD).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                finalParams.put("saleAmountUSD", NumberFormatUtil.formatUnitAmount(saleAmountUSD));
                params.set(finalParams);
                break;
            }
        }

        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path-" + path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        List<String> custNameList = settlementFormItemList.stream().map(SettlementFormItem::getCustName).toList().stream().distinct().collect(Collectors.toList());
        String name = "";
        if (CollUtil.isNotEmpty(custNameList)) {
            name = String.join(",", custNameList);
        }
        long count = settlementFormItemList.stream().map(SettlementFormItem::getSaleContractCode).distinct().count();
        if (count == 1) {
            Optional<String> first = settlementFormItemList.stream().map(SettlementFormItem::getCustPo).distinct().findFirst();
            if (first.isPresent() && Objects.nonNull(first.get())) {
                name = first.get();
            }
        }
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params.get(),  name+".xlsx", settlementFormItemExportVOList, tableParams, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    public HashMap<String, Object> getExportParams(SettlementFormDO settlementFormDO , List<SettlementFormItem> settlementFormItemList){
        HashMap<String, Object> params = new HashMap();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (Objects.nonNull(settlementFormDO.getShipmentCode())) {
            ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(settlementFormDO.getShipmentCode());
            if (Objects.nonNull(shipmentDTO) && Objects.nonNull(shipmentDTO.getInputUser())) {
                params.put("inputUserName", shipmentDTO.getInputUser().getNickname());
            }
            params.put("shipmentCode", settlementFormDO.getShipmentCode());
        }
        params.put("invoiceCode", settlementFormDO.getInvoiceCode());
        params.put("code", settlementFormDO.getCode());
        if (settlementFormDO.getInputDate() != null) {
            String inputDate = dtf.format(settlementFormDO.getInputDate());
            params.put("inputDate", inputDate);
        }
        //params.put("status", settlementFormDO.getStatus());
        if (settlementFormDO.getShipDate() != null) {
            String shipDate = dtf.format(settlementFormDO.getShipDate());
            params.put("shipDate", shipDate);
        }
        params.put("tradeType", settlementFormDO.getTradeType());
        params.put("tradeCountryName", settlementFormDO.getTradeCountryName());
        // 根据 settlementId 查询 system_settlement 表获取英文名称
        if (Objects.nonNull(settlementFormDO.getSettlementId())) {
            SettlementDTO settlementDTO = settlementApi.getSettlementDTOById(settlementFormDO.getSettlementId());
            if (Objects.nonNull(settlementDTO) && Objects.nonNull(settlementDTO.getNameEng())) {
                params.put("settlementName", settlementDTO.getNameEng());
            }
        }
        params.put("forwarderCompanyName", settlementFormDO.getForwarderCompanyName());
        params.put("containerQuantity", settlementFormDO.getContainerQuantity());
        params.put("settlementTermType", settlementFormDO.getSettlementTermType());
        params.put("transportType", settlementFormDO.getTransportType());
        params.put("invoicePackingList", settlementFormDO.getInvoicePackingList());
        params.put("departurePortName", settlementFormDO.getDeparturePortName());
        params.put("destinationPortName", settlementFormDO.getDestinationPortName());
        params.put("totalQuantity", settlementFormDO.getTotalQuantity());
        Integer boxCount = settlementFormItemList.stream().map(s -> {
            List<JsonSpecificationEntity> specificationList = s.getSpecificationList();
            if (CollUtil.isEmpty(specificationList)) {
                return 0;
            }
            return NumUtil.mul(BigDecimal.valueOf(s.getBoxCount()), specificationList.size()).intValue();
        }).reduce(Integer::sum).orElse(0);
        params.put("totalBoxes", boxCount);
        params.put("totalGrossweight", settlementFormDO.getTotalGrossweight().getWeight());
        params.put("totalWeight", settlementFormDO.getTotalGrossweight().getWeight());
        params.put("receivePerson", settlementFormDO.getReceivePerson());
        params.put("notifyPerson", settlementFormDO.getNotifyPerson());
        params.put("frontShippingMark", settlementFormDO.getFrontShippingMark());
        if (settlementFormDO.getTotalVolume() != null) {
            params.put("totalVolume", VolumeUtil.processVolume(settlementFormDO.getTotalVolume()));
        }
        Long companyId = settlementFormDO.getForeignTradeCompanyId();
        if (Objects.nonNull(companyId)) {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            Optional.ofNullable(companyDTO).ifPresent(s -> {
                params.put("companyTitle", companyDTO.getCompanyName());
            });
        }
        if (settlementFormDO.getTransportType() != null) {
            String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.TRANSPORT_TYPE, String.valueOf(settlementFormDO.getTransportType()));
            params.put("transportType", transportType.replace("(有订舱杂费)","").replace("(无订舱杂费)",""));
        }
        if (settlementFormDO.getTotalGrossweight() != null && settlementFormDO.getTotalGrossweight().getWeight() != null) {
            params.put("totalGrossweight", NumberFormatUtil.formatWeight(settlementFormDO.getTotalGrossweight().getWeight()));
        }
        if (settlementFormDO.getTotalWeight() != null && settlementFormDO.getTotalWeight().getWeight() != null) {
            params.put("totalWeight", NumberFormatUtil.formatWeight(settlementFormDO.getTotalWeight().getWeight()));
        }
        if (settlementFormDO.getTotalAmount() != null && settlementFormDO.getTotalAmount().getAmount() != null) {
            params.put("totalAmount", NumberFormatUtil.formatAmount(settlementFormDO.getTotalAmount().getAmount()));
        }
        if (CollUtil.isNotEmpty(settlementFormItemList)) {
            List<String> currencyList = settlementFormItemList.stream().map(SettlementFormItem::getCurrency).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(currencyList)) {
                params.put("currency", String.join(",", currencyList));
            }
            List<String> custCodeList = settlementFormItemList.stream().map(SettlementFormItem::getCustCode).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(custCodeList)) {
                params.put("custCode", String.join(",", custCodeList));
            }
            List<String> custNameList = settlementFormItemList.stream().map(SettlementFormItem::getCustName).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(custNameList)) {
                params.put("custName", String.join(",", custNameList));
            }
            List<String> custPoList = settlementFormItemList.stream().map(SettlementFormItem::getCustPo).distinct().toList();
            if (!custPoList.isEmpty()) {
                params.put("custPo", String.join(",", custPoList));
            }
            // 根据 settlementId 批量查询获取英文名称
            List<Long> settlementIdList = settlementFormItemList.stream()
                    .map(SettlementFormItem::getSettlementId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (CollUtil.isNotEmpty(settlementIdList)) {
                List<SettlementDTO> settlementDTOList = settlementApi.selectSettlementTermById(settlementIdList);
                if (CollUtil.isNotEmpty(settlementDTOList)) {
                    List<String> settlementNameEngList = settlementDTOList.stream()
                            .map(SettlementDTO::getNameEng)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(settlementNameEngList)) {
                        params.put("settlementName", String.join(",", settlementNameEngList));
                    }
                }
            }
            List<String> saleContractCodeList = settlementFormItemList.stream().map(SettlementFormItem::getSaleContractCode).distinct().toList();
            if (!saleContractCodeList.isEmpty()) {
                params.put("saleContractCode", String.join(",", saleContractCodeList));
            }
            Map<String, CustAllDTO> custAllDTOMap = custApi.getCustByCodeList(custCodeList);
            BigDecimal sumAmount = BigDecimal.valueOf(0);
            List<String> custAddressList = new ArrayList<>();
            for (SettlementFormItem s : settlementFormItemList) {
                if (Objects.nonNull(s.getSaleAmount())
                        && Objects.nonNull(s.getDeclarationQuantity())
                        && Objects.nonNull(s.getSaleAmount().getAmount())) {
                    BigDecimal total = s.getSaleAmount().getAmount().multiply(BigDecimal.valueOf(s.getDeclarationQuantity()));
                    sumAmount = sumAmount.add(total);
                }
                if (Objects.nonNull(custAllDTOMap)) {
                    CustAllDTO custAllDTO = custAllDTOMap.get(s.getCustCode());
                    if (Objects.nonNull(custAllDTO)) {
                        if(!custAddressList.contains(custAllDTO.getAddress())){
                            custAddressList.add(custAllDTO.getAddress());
                        }
                    }
                }
            }
            params.put("totalAmount", NumberFormatUtil.formatAmount(sumAmount));
            params.put("custAddress", String.join(",", custAddressList));
        }
        return params;
    }

    public HashMap<String, Object> getExportOtherParams(SettlementFormDO settlementFormDO , List<SettlementFormItem> settlementFormItemList){
        HashMap<String, Object> params = new HashMap();
        params.put("transportType", settlementFormDO.getTransportType());
        params.put("invoicePackingList", settlementFormDO.getInvoicePackingList());
        params.put("departurePortName", settlementFormDO.getDeparturePortName());
        params.put("destinationPortName", settlementFormDO.getDestinationPortName());
        params.put("invoiceCode", settlementFormDO.getInvoiceCode());
        params.put("departureCountryName", settlementFormDO.getDepartureCountryName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (settlementFormDO.getInputDate() != null) {
            String inputDate = dtf.format(settlementFormDO.getInputDate());
            params.put("inputDate", inputDate);
        }
        Integer boxCount = settlementFormItemList.stream().map(s -> {
            List<JsonSpecificationEntity> specificationList = s.getSpecificationList();
            if (CollUtil.isEmpty(specificationList)) {
                return 0;
            }
            return NumUtil.mul(BigDecimal.valueOf(s.getBoxCount()), specificationList.size()).intValue();
        }).reduce(Integer::sum).orElse(0);
        params.put("totalBoxes", boxCount);
        params.put("totalGrossweight", settlementFormDO.getTotalGrossweight().getWeight());
        params.put("totalWeight", settlementFormDO.getTotalGrossweight().getWeight());
        if (settlementFormDO.getTotalVolume() != null) {
            params.put("totalVolume", VolumeUtil.processVolume(settlementFormDO.getTotalVolume()));
        }
        Integer shippingQuantity = settlementFormItemList.stream().map(SettlementFormItem::getShippingQuantity).reduce(Integer::sum).orElse(0);
        params.put("shippingQuantity", shippingQuantity);
        Long companyId = settlementFormDO.getForeignTradeCompanyId();
        if (Objects.nonNull(companyId)) {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            Optional.ofNullable(companyDTO).ifPresent(s -> {
                params.put("companyTitle", companyDTO.getCompanyName());
                params.put("companyTitleEng", companyDTO.getCompanyNameEng());
                params.put("companyAddress", companyDTO.getCompanyAddress());
            });
        }
        if (settlementFormDO.getTransportType() != null) {
            String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.TRANSPORT_TYPE, String.valueOf(settlementFormDO.getTransportType()));
            params.put("transportType", transportType.replace("(有订舱杂费)","").replace("(无订舱杂费)",""));
        }
        if (settlementFormDO.getTotalGrossweight() != null && settlementFormDO.getTotalGrossweight().getWeight() != null) {
            params.put("totalGrossweight", NumberFormatUtil.formatWeight(settlementFormDO.getTotalGrossweight().getWeight()));
        }
        if (settlementFormDO.getTotalWeight() != null && settlementFormDO.getTotalWeight().getWeight() != null) {
            params.put("totalWeight", NumberFormatUtil.formatWeight(settlementFormDO.getTotalWeight().getWeight()));
        }
        if (settlementFormDO.getTotalAmount() != null && settlementFormDO.getTotalAmount().getAmount() != null) {
            params.put("totalAmount", NumberFormatUtil.formatAmount(settlementFormDO.getTotalAmount().getAmount()));
        }
        if (CollUtil.isNotEmpty(settlementFormItemList)) {
            List<String> custPoList = settlementFormItemList.stream().map(SettlementFormItem::getCustPo).distinct().toList();
            if (!custPoList.isEmpty()) {
                params.put("custPo", String.join(",", custPoList));
            }
            List<String> custNameList = settlementFormItemList.stream().map(SettlementFormItem::getCustName).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(custNameList)) {
                params.put("custName", String.join(",", custNameList));
            }
        }
        return params;
    }
}