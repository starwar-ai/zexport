package com.syj.eplus.module.dms.service.commodityinspection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.framework.common.enums.OrderLinkTypeEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionItemExportVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionPageReqVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionRespVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;
import com.syj.eplus.module.dms.convert.commodityinspection.CommodityInspectionConvert;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspection.CommodityInspectionDO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import com.syj.eplus.module.dms.dal.mysql.commodityinspection.CommodityInspectionMapper;
import com.syj.eplus.module.dms.dal.mysql.commodityinspectionitem.CommodityInspectionItemMapper;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 商检单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CommodityInspectionServiceImpl implements CommodityInspectionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CommodityInspectionMapper commodityInspectionMapper;

    @Resource
    private CommodityInspectionItemMapper commodityInspectionItemMapper;


    @Resource
    private ReportApi reportApi;
    @Resource
    private PackageTypeApi packageTypeApi;


    @Resource
    private FileApi fileApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private ShipmentApi shipmentApi;
    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private CompanyApi companyApi;
    private static final String SN_TYPE = "commodityInspection";
    private static final String CODE_PREFIX = "SJ";

    @Override
    public CreatedResponse createCommodityInspection(CommodityInspectionSaveReqVO createReqVO) {
        CommodityInspectionDO commodityInspection = CommodityInspectionConvert.INSTANCE.convertCommodityInspectionDO(createReqVO);
        // 生成 商检单 编号
        commodityInspection.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        List<CommodityInspectionItem> commodityInspectionItemList = createReqVO.getChildren();
        // 计算合计
        calcCommodityInspection(commodityInspection,commodityInspectionItemList);
        // 插入
        commodityInspectionMapper.insert(commodityInspection);
        // 插入明细
        if (CollUtil.isNotEmpty(commodityInspectionItemList)) {
            commodityInspectionItemList.forEach(s -> {
                s.setCommodityInspectionId(commodityInspection.getId());
            });
            commodityInspectionItemMapper.insertBatch(commodityInspectionItemList);
        }



        // 创建链路编号
        List<String> linkCodeList = commodityInspection.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)) {
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(commodityInspection.getId())
                    .setCode(commodityInspection.getCode())
                    .setName(BusinessNameDict.COMMODITY_INSPECTION_NAME)
                    .setType(OrderLinkTypeEnum.INSPECTION_CERTIFICATE.getValue())
                    .setLinkCode(s)
                    .setItem(commodityInspectionItemList)
                    .setParentCode(commodityInspection.getShipmentCode())
                    .setStatus(commodityInspection.getStatus())
                    .setCompanyId(commodityInspection.getForeignTradeCompanyId())
                    .setOrderMsg(commodityInspection)).toList();
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        // 返回
        return new CreatedResponse(commodityInspection.getId(), commodityInspection.getCode());
    }

    private void calcCommodityInspection(CommodityInspectionDO commodityInspectionDO,List<CommodityInspectionItem> commodityInspectionItemList) {
        if (Objects.isNull(commodityInspectionDO)||CollUtil.isEmpty(commodityInspectionItemList)){
            return;
        }
        // 数量合计
        Integer totalQuantity = commodityInspectionItemList.stream().map(CommodityInspectionItem::getDeclarationQuantity).reduce(Integer::sum).get();
        // 箱数合计
        BigDecimal totalBoxQuantity = commodityInspectionItemList.stream().map(s -> NumUtil.div(s.getDeclarationQuantity(), s.getQtyPerOuterbox()).setScale(0, RoundingMode.UP)).reduce(BigDecimal::add).get();
        // 毛重合计
        JsonWeight totalGrossweight = commodityInspectionItemList.stream().map(s -> CalcSpecificationUtil.calcTotalGrossweightByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(JsonWeight::add).get();
        // 净重合计
        JsonWeight totalNeight = commodityInspectionItemList.stream().map(s -> CalcSpecificationUtil.calcTotalNetWeightByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(JsonWeight::add).get();
        // 体积合计
        BigDecimal totalVolume = commodityInspectionItemList.stream().map(s -> CalcSpecificationUtil.calcTotalVolumeByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(BigDecimal::add).get();
        // 报关合计
        List<JsonAmount> totalDecliaration = new ArrayList<>();
         commodityInspectionItemList.stream().filter(s->Objects.nonNull(s.getDeclarationAmount())&&Objects.nonNull(s.getDeclarationAmount().getAmount())&& StrUtil.isEmpty(s.getDeclarationAmount().getCurrency()))
                .map(s->new JsonAmount(NumUtil.mul(s.getDeclaredQuantity(),s.getDeclarationAmount().getAmount()),s.getDeclarationAmount().getCurrency()))
                .collect(Collectors.groupingBy(JsonAmount::getCurrency))
                .forEach((k,v)->{
                    if (CollUtil.isEmpty(v)){
                        return;
                    }
                    BigDecimal amount = v.stream().map(JsonAmount::getAmount).reduce(BigDecimal::add).get();
                    totalDecliaration.add(new JsonAmount(amount,k));
                });
        commodityInspectionDO.setTotalQuantity(totalQuantity);
        commodityInspectionDO.setTotalBoxes(totalBoxQuantity.intValue());
        commodityInspectionDO.setTotalGrossweight(totalGrossweight);
        commodityInspectionDO.setTotalWeight(totalNeight);
        commodityInspectionDO.setTotalVolume(totalVolume);
        commodityInspectionDO.setTotalDecliaration(totalDecliaration);
    }

    @Override
    public void updateCommodityInspection(CommodityInspectionSaveReqVO updateReqVO) {
        // 校验存在
        validateCommodityInspectionExists(updateReqVO.getId());
        // 更新
        CommodityInspectionDO updateObj = CommodityInspectionConvert.INSTANCE.convertCommodityInspectionDO(updateReqVO);
        // 更新明细
        List<CommodityInspectionItem> commodityInspectionItemList = updateReqVO.getChildren();
        commodityInspectionItemMapper.delete(CommodityInspectionItem::getCommodityInspectionId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(commodityInspectionItemList)) {
            commodityInspectionItemList.forEach(s -> {
                s.setCommodityInspectionId(updateReqVO.getId());
                s.setId(null);
            });
            commodityInspectionItemMapper.insertBatch(commodityInspectionItemList);
        }
        commodityInspectionMapper.updateById(updateObj);
    }

    @Override
    public void deleteCommodityInspection(Long id) {
        // 校验存在
        CommodityInspectionDO commodityInspectionDO = commodityInspectionMapper.selectById(id);
        if (commodityInspectionDO == null) {
            throw exception(COMMODITY_INSPECTION_NOT_EXISTS);
        }
        ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(commodityInspectionDO.getShipmentCode());
        if (Objects.nonNull(shipmentDTO)) {
            List<ShipmentItemDTO> shipmentItemDTOList = shipmentApi.getShipmentItemDTOListByShipmentId(shipmentDTO.getId());
            if (CollUtil.isNotEmpty(shipmentItemDTOList)) {
                shipmentItemDTOList.forEach(item -> {
                    item.setIsToCommodityInspection(BooleanEnum.NO.getValue());
                });
                shipmentApi.updateShipmentItem(shipmentItemDTOList, false);
            }
        }
        // 删除
        commodityInspectionMapper.deleteById(id);
        orderLinkApi.updateOrderLinkStatus(commodityInspectionDO.getCode(), BusinessNameDict.COMMODITY_INSPECTION_NAME, commodityInspectionDO.getLinkCodeList(), BooleanEnum.NO.getValue());
        // 删除明细
        commodityInspectionItemMapper.delete(CommodityInspectionItem::getCommodityInspectionId, id);
    }

    private void validateCommodityInspectionExists(Long id) {
        if (commodityInspectionMapper.selectById(id) == null) {
            throw exception(COMMODITY_INSPECTION_NOT_EXISTS);
        }
    }

    @Override
    public CommodityInspectionRespVO getCommodityInspection(Long id) {
        CommodityInspectionDO commodityInspectionDO = commodityInspectionMapper.selectById(id);
        if (commodityInspectionDO == null) {
            return null;
        }
        CommodityInspectionRespVO commodityInspectionRespVO = CommodityInspectionConvert.INSTANCE.convertCommodityInspectionRespVO(commodityInspectionDO);
        if (Objects.nonNull(commodityInspectionDO.getShipmentCode())) {
            ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(commodityInspectionDO.getShipmentCode());
            if (Objects.nonNull(shipmentDTO.getInputUser())) {
                commodityInspectionRespVO.setInputUserName(shipmentDTO.getInputUser().getNickname());
            }
            if (Objects.nonNull(shipmentDTO.getDocumenter())) {
                commodityInspectionRespVO.setDocumenterUserName(shipmentDTO.getDocumenter().getNickname());
            }
            commodityInspectionRespVO.setShipNum(shipmentDTO.getShipNum());
            commodityInspectionRespVO.setBillLadingNum(shipmentDTO.getBillLadingNum());
//            commodityInspectionRespVO.setTotalGoodsValue(shipmentDTO.getTotalGoodsValue());
//            commodityInspectionRespVO.setTotalDeclaration(shipmentDTO.getTotalDeclaration());
//            commodityInspectionRespVO.setTotalPurchase(shipmentDTO.getTotalPurchase());
        }
        //获取明细
        List<CommodityInspectionItem> commodityInspectionItemList = commodityInspectionItemMapper.selectList(CommodityInspectionItem::getCommodityInspectionId, id);
        commodityInspectionRespVO.setChildren(commodityInspectionItemList);
        commodityInspectionRespVO.setChildren(groupItem(commodityInspectionRespVO.getChildren()));
        return commodityInspectionRespVO;
    }

    private List<CommodityInspectionItem> groupItem(List<CommodityInspectionItem> children) {
        if (CollUtil.isEmpty(children)) {
            return children;
        }
        record DeclarationKey(
                String customsDeclarationNameEng,
                BigDecimal taxRefundRate,
                String hsCode,
                String declarationName,
                String hsMeasureUnit,
                String shipmentCode
        ) { }
        Map<DeclarationKey, List<CommodityInspectionItem>> groupedItems = children.stream()
                .collect(Collectors.groupingBy(item ->
                                new DeclarationKey(
                                        item.getCustomsDeclarationNameEng(),
                                        item.getTaxRefundRate(),
                                        item.getHsCode(),
                                        item.getDeclarationName(),
                                        item.getHsMeasureUnit(),
                                        item.getShipmentCode()),
                        LinkedHashMap::new,  // 使用LinkedHashMap保持顺序
                        Collectors.toList()
                ));
        List<CommodityInspectionItem> res = new ArrayList<>();
        groupedItems.forEach((key, value) -> {
            CommodityInspectionItem aggregatedItem = new CommodityInspectionItem();
            BeanUtils.copyProperties(value.get(0), aggregatedItem);

            aggregatedItem.setShippingQuantity(value.stream().mapToInt(CommodityInspectionItem::getShippingQuantity).sum());        //出运数量
            if (aggregatedItem.getShippingQuantity() == 0) {
                throw exception(SHIPPING_QUANTITY_ZERO);
            }
            aggregatedItem.setDeclarationQuantity(value.stream().mapToInt(CommodityInspectionItem::getDeclarationQuantity).sum());  //报关数量
            if (aggregatedItem.getDeclarationQuantity() == 0) {
                throw exception(DECLARATION_QUANTITY_ZERO);
            }
            aggregatedItem.setPurchaseTotalQuantity(value.stream().mapToInt(CommodityInspectionItem::getPurchaseTotalQuantity).sum());  //采购数量
            aggregatedItem.setDeclaredQuantity(value.stream().mapToInt(CommodityInspectionItem::getDeclaredQuantity).sum());        //已报关数量
//                aggregatedItem.setOrderGrossProfitRate(aggregatedItem.getOrderGrossProfitRate());  //毛利率是否需要合并计算？
            aggregatedItem.setBoxCount(value.stream().mapToInt(CommodityInspectionItem::getBoxCount).sum());                        //箱数
            aggregatedItem.setTotalVolume(value.stream().map(CommodityInspectionItem::getTotalVolume).reduce(BigDecimal.ZERO, BigDecimal::add));      //总体积

            //佣金金额
            List<JsonAmount> commAmounts = value.stream().map(CommodityInspectionItem::getCommissionAmount).filter(Objects::nonNull).toList();
            if (!commAmounts.isEmpty()) {
                String currency = commAmounts.get(0).getCurrency();
                BigDecimal total = commAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setCommissionAmount(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //销售金额
            List<JsonAmount> saleAmounts = value.stream().map(CommodityInspectionItem::getSaleAmount).filter(Objects::nonNull).toList();
            if (!saleAmounts.isEmpty()) {
                String currency = saleAmounts.get(0).getCurrency();
                BigDecimal total = saleAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setSaleAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                BigDecimal unitPrice = total.divide(BigDecimal.valueOf(aggregatedItem.getDeclarationQuantity()), 2, RoundingMode.HALF_UP);
                aggregatedItem.setSaleUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
            }

            //退税金额
            List<JsonAmount> refundAmounts = value.stream().map(CommodityInspectionItem::getTaxRefundPrice).filter(Objects::nonNull).toList();
            if (!refundAmounts.isEmpty()) {
                String currency = refundAmounts.get(0).getCurrency();
                BigDecimal total = refundAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTaxRefundPrice(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //保险费
            List<JsonAmount> insuranceAmounts = value.stream().map(CommodityInspectionItem::getInsuranceFee).filter(Objects::nonNull).toList();
            if (!insuranceAmounts.isEmpty()) {
                String currency = insuranceAmounts.get(0).getCurrency();
                BigDecimal total = insuranceAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setInsuranceFee(new JsonAmount().setAmount(total).setCurrency(currency));
            }

            //报关金额
            List<JsonAmount> declarationAmounts = value.stream().map(CommodityInspectionItem::getDeclarationAmount).filter(Objects::nonNull).toList();
            if (!declarationAmounts.isEmpty()) {
                String currency = declarationAmounts.get(0).getCurrency();
                BigDecimal total = declarationAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setDeclarationAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                BigDecimal unitPrice = total.divide(BigDecimal.valueOf(aggregatedItem.getDeclarationQuantity()), 2, RoundingMode.HALF_UP);
                aggregatedItem.setDeclarationUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
            }

            //采购单价，含税单价
            AtomicReference<BigDecimal> unitTotal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> withTaxTotal = new AtomicReference<>(BigDecimal.ZERO);
            String purchaseCurrency = aggregatedItem.getPurchaseCurrency();
            value.forEach(s -> {
                BigDecimal unitAmount = Optional.ofNullable(s.getPurchaseUnitPrice()).map(JsonAmount::getAmount).orElse(BigDecimal.ZERO);
                BigDecimal withTaxAmount = Optional.ofNullable(s.getPurchaseWithTaxPrice()).map(JsonAmount::getAmount).orElse(BigDecimal.ZERO);
                BigDecimal quantity = Optional.ofNullable(s.getPurchaseTotalQuantity()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                unitTotal.set(unitTotal.get().add(unitAmount.multiply(quantity)));
                withTaxTotal.set(withTaxTotal.get().add(withTaxAmount.multiply(quantity)));
            });
            if (aggregatedItem.getPurchaseTotalQuantity() == 0) {
                aggregatedItem.setPurchaseUnitPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(purchaseCurrency));
                aggregatedItem.setPurchaseWithTaxPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(purchaseCurrency));
            } else {
                BigDecimal quantity = BigDecimal.valueOf(aggregatedItem.getPurchaseTotalQuantity());
                BigDecimal unitPrice = unitTotal.get().divide(quantity, 2, RoundingMode.HALF_UP);
                BigDecimal withTaxPrice = withTaxTotal.get().divide(quantity, 2, RoundingMode.HALF_UP);
                aggregatedItem.setPurchaseUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(purchaseCurrency));
                aggregatedItem.setPurchaseWithTaxPrice(new JsonAmount().setAmount(withTaxPrice).setCurrency(purchaseCurrency));
            }

            //总净重
            List<JsonWeight> grossWeight = value.stream().map(CommodityInspectionItem::getTotalGrossweight).filter(Objects::nonNull).toList();
            if (!grossWeight.isEmpty()) {
                String unit = grossWeight.get(0).getUnit();
                BigDecimal total = grossWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTotalGrossweight(new JsonWeight().setWeight(total).setUnit(unit));
            }

            //总毛重
            List<JsonWeight> netWeight = value.stream().map(CommodityInspectionItem::getTotalNetweight).filter(Objects::nonNull).toList();
            if (!netWeight.isEmpty()) {
                String unit = netWeight.get(0).getUnit();
                BigDecimal total = netWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                aggregatedItem.setTotalNetweight(new JsonWeight().setWeight(total).setUnit(unit));
            }

            BigDecimal boxCount = BigDecimal.valueOf(aggregatedItem.getBoxCount());
            JsonSpecificationEntity jsonSpecificationEntity = new JsonSpecificationEntity();
            //体积
            jsonSpecificationEntity.setOuterboxVolume( NumUtil.div(aggregatedItem.getTotalVolume(), boxCount, CalculationDict.THREE, RoundingMode.HALF_UP));
            //毛重
            BigDecimal grossW = NumUtil.div(aggregatedItem.getTotalGrossweight().getWeight(), boxCount, CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
            jsonSpecificationEntity.setOuterboxGrossweight(new JsonWeight().setWeight(grossW).setUnit(aggregatedItem.getTotalGrossweight().getUnit()) );
            //净重
            BigDecimal grossN = NumUtil.div(aggregatedItem.getTotalNetweight().getWeight(), boxCount, CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
            jsonSpecificationEntity.setOuterboxNetweight(new JsonWeight().setWeight(grossN).setUnit(aggregatedItem.getTotalNetweight().getUnit()) );
            aggregatedItem.setSpecificationList(List.of(jsonSpecificationEntity));

            res.add(aggregatedItem);

        });
        return res;
    }


    @Override
    public PageResult<CommodityInspectionRespVO> getCommodityInspectionPage(CommodityInspectionPageReqVO pageReqVO) {
        PageResult<CommodityInspectionDO> commodityInspectionDOPageResult = commodityInspectionMapper.selectPage(pageReqVO);
        List<CommodityInspectionDO> commodityInspectionDOList = commodityInspectionDOPageResult.getList();
        if (CollUtil.isEmpty(commodityInspectionDOList)) {
            return null;
        }
        List<Long> idList = commodityInspectionDOList.stream().map(CommodityInspectionDO::getId).distinct().toList();
        Map<Long, List<CommodityInspectionItem>> commodityInspectionItemMap = commodityInspectionItemMapper.getCommoditInspectionItemMapByCommoditInspectionIdList(idList);

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        List<CommodityInspectionRespVO> commodityInspectionRespVOList = CommodityInspectionConvert.INSTANCE.convertCommodityInspectionRespVO(commodityInspectionDOList, commodityInspectionItemMap, packageTypeList);

        commodityInspectionRespVOList.forEach(vo -> {
            vo.setChildren(groupItem(vo.getChildren()));
        });

        return new PageResult<CommodityInspectionRespVO>().setTotal(commodityInspectionDOPageResult.getTotal()).setList(commodityInspectionRespVOList);
    }

    @Override
    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        CommodityInspectionDO commodityInspectionDO = commodityInspectionMapper.selectById(id);
        if (commodityInspectionDO == null) {
            throw exception(COMMODITY_INSPECTION_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (commodityInspectionDO.getForeignTradeCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, commodityInspectionDO.getForeignTradeCompanyId());
        }
        if (reportDTO == null) {
            reportDTO = reportApi.getReport(reportCode);
        }
        if (reportDTO == null) {
            throw exception(REPORT_NULL, "reportCode-" + reportCode);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        List<CommodityInspectionItem> commodityInspectionItemList = commodityInspectionItemMapper.selectList(new LambdaQueryWrapperX<CommodityInspectionItem>().eq(CommodityInspectionItem::getCommodityInspectionId, id));
        List<CommodityInspectionItemExportVO> commodityInspectionItemExportVOList = CommodityInspectionConvert.INSTANCE.convertDeclarationItemExportVOList(groupItem(commodityInspectionItemList));
        // 递增赋值 sortNum
        AtomicInteger sortIndex = new AtomicInteger(1);
        commodityInspectionItemExportVOList.forEach(item -> {
            item.setSortNum(sortIndex.getAndIncrement());
            if (item.getHsMeasureUnit() != null) {
                item.setHsMeasureUnitName(item.getHsMeasureUnit());
            }
            if (item.getSaleUnitPrice() != null && item.getSaleUnitPrice().getAmount() != null) {
                item.setSaleUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getSaleUnitPrice().getAmount()));
            }
            if (item.getSaleAmount() != null && item.getSaleAmount().getAmount() != null) {
                item.setSaleAmountAmount(NumberFormatUtil.formatAmount(item.getSaleAmount().getAmount()));
            }
            if (item.getPurchaseUnitPrice() != null && item.getPurchaseUnitPrice().getAmount() != null) {
                item.setPurchaseUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getPurchaseUnitPrice().getAmount()));
            }
            if (item.getTaxRefundPrice() != null && item.getTaxRefundPrice().getAmount() != null) {
                item.setTaxRefundPriceAmount(NumberFormatUtil.formatAmount(item.getTaxRefundPrice().getAmount()));
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
            if (item.getTotalVolume() != null) {
                item.setTotalVolume(VolumeUtil.processVolume(item.getTotalVolume()));
            }
        });
        HashMap<String, Object> params = new HashMap();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        params.put("invoiceCode", commodityInspectionDO.getInvoiceCode());
        params.put("code", commodityInspectionDO.getCode());
        //params.put("status", commodityInspectionDO.getStatus());
        if (commodityInspectionDO.getCreateTime() != null) {
            String inputDate = dtf.format(commodityInspectionDO.getCreateTime());
            params.put("inputDate", inputDate);
        }
        if (commodityInspectionDO.getShipDate() != null) {
            String shipDate = dtf.format(commodityInspectionDO.getShipDate());
            params.put("shipDate", shipDate);
        }
        List<String> saleContractCodeList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getSaleContractCode).distinct().toList();
        if (!saleContractCodeList.isEmpty()) {
            params.put("saleContractCode", String.join(",", saleContractCodeList));
        }
        List<String> currencyList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getCurrency).distinct().toList();
        if (!currencyList.isEmpty()) {
            params.put("currency", String.join(",", currencyList));
        }
        List<String> custPoList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getCustPo).distinct().toList();
        if (!custPoList.isEmpty()) {
            params.put("custPo", String.join(",", custPoList));
        }
        params.put("tradeType", commodityInspectionDO.getTradeType());
        params.put("tradeCountryName", commodityInspectionDO.getTradeCountryName());
        params.put("forwarderCompanyName", commodityInspectionDO.getForwarderCompanyName());
        params.put("containerQuantity", commodityInspectionDO.getContainerQuantity());
        params.put("settlementTermType", commodityInspectionDO.getSettlementTermType());
        if (commodityInspectionDO.getTransportType() != null) {
            String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.TRANSPORT_TYPE, String.valueOf(commodityInspectionDO.getTransportType()));
            params.put("transportType", transportType.replace("(有订舱杂费)","").replace("(无订舱杂费)",""));
        }
        params.put("departurePortName", commodityInspectionDO.getDeparturePortName());
        params.put("destinationPortName", commodityInspectionDO.getDestinationPortName());
        params.put("totalQuantity", commodityInspectionDO.getTotalQuantity());
        params.put("totalBoxes", commodityInspectionDO.getTotalBoxes());
        
        // 从明细汇总计算毛重合计和净重合计
        if (CollUtil.isNotEmpty(commodityInspectionItemList)) {
            // 计算总毛重
            BigDecimal sumGrossweight = commodityInspectionItemList.stream()
                    .filter(item -> item.getTotalGrossweight() != null && item.getTotalGrossweight().getWeight() != null)
                    .map(item -> item.getTotalGrossweight().getWeight())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sumGrossweight.compareTo(BigDecimal.ZERO) > 0) {
                params.put("totalGrossweight", NumberFormatUtil.formatWeight(sumGrossweight));
            }
            
            // 计算总净重
            BigDecimal sumNetweight = commodityInspectionItemList.stream()
                    .filter(item -> item.getTotalNetweight() != null && item.getTotalNetweight().getWeight() != null)
                    .map(item -> item.getTotalNetweight().getWeight())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sumNetweight.compareTo(BigDecimal.ZERO) > 0) {
                params.put("totalNetWeight", NumberFormatUtil.formatWeight(sumNetweight));
            }
        }
        
        if (commodityInspectionDO.getTotalVolume() != null) {
            params.put("totalVolume", VolumeUtil.processVolume(commodityInspectionDO.getTotalVolume()));
        }
        Long companyId = commodityInspectionDO.getForeignTradeCompanyId();
        if (Objects.nonNull(companyId)) {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            Optional.ofNullable(companyDTO).ifPresent(s -> {
                params.put("companyTitle", companyDTO.getCompanyName());
            });
        }
        if (CollUtil.isNotEmpty(commodityInspectionItemList)) {
            //客户编号和客户名称存在多个用逗号分割
            List<String> custCodeList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getCustCode).toList().stream().distinct().collect(Collectors.toList());
            String custCode = "";
            if (CollUtil.isNotEmpty(custCodeList)) {
                custCode = String.join(",", custCodeList);
            }
            params.put("custCode", custCode);
            List<String> custNameList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getCustName).toList().stream().distinct().collect(Collectors.toList());
            String custName = "";
            if (CollUtil.isNotEmpty(custNameList)) {
                custName = String.join(",", custNameList);
            }
            params.put("custName", custName);
            List<String> settlementNameList = commodityInspectionItemList.stream().map(CommodityInspectionItem::getSettlementName).distinct().toList();
            if (!settlementNameList.isEmpty()) {
                params.put("settlementName", String.join(",", settlementNameList));
            }
            params.put("receivePerson", commodityInspectionDO.getReceivePerson());
            params.put("notifyPerson", commodityInspectionDO.getNotifyPerson());
            params.put("frontShippingMark", commodityInspectionDO.getFrontShippingMark());
            BigDecimal sumAmount = BigDecimal.valueOf(0);
            for (CommodityInspectionItem s : commodityInspectionItemList) {
                if (Objects.nonNull(s.getSaleAmount())
                        && Objects.nonNull(s.getDeclarationQuantity())
                        && Objects.nonNull(s.getSaleAmount().getAmount())) {
                    BigDecimal total = s.getSaleAmount().getAmount().multiply(BigDecimal.valueOf(s.getDeclarationQuantity()));
                    sumAmount = sumAmount.add(total);
                }
            }
            params.put("totalAmount", NumberFormatUtil.formatAmount(sumAmount));
        }
        if (Objects.nonNull(commodityInspectionDO.getShipmentCode())) {
            params.put("shipmentCode", commodityInspectionDO.getShipmentCode());
            ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(commodityInspectionDO.getShipmentCode());
            if (Objects.nonNull(shipmentDTO.getInputUser())) {
                params.put("inputUserName", shipmentDTO.getInputUser().getNickname());
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
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "商检单.xlsx", commodityInspectionItemExportVOList, null, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }
}