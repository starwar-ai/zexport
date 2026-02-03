package com.syj.eplus.module.dms.service.declaration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
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
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationItemExportVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationPageReqVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationRespVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.convert.declaration.DeclarationConvert;
import com.syj.eplus.module.dms.dal.dataobject.declaration.DeclarationDO;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import com.syj.eplus.module.dms.dal.mysql.declaration.DeclarationMapper;
import com.syj.eplus.module.dms.dal.mysql.declarationitem.DeclarationItemMapper;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.DeclarationItemDTO;
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
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 报关单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class DeclarationServiceImpl implements DeclarationService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DeclarationMapper declarationMapper;

    @Resource
    private DeclarationItemMapper declarationItemMapper;

    @Resource
    private ReportApi reportApi;
    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    private FileApi fileApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private ShipmentApi shipmentApi;
    @Resource
    private CompanyApi companyApi;
    private static final String SN_TYPE = "declaration";
    private static final String CODE_PREFIX = "BG";


    @Override
    public CreatedResponse createDeclaration(DeclarationSaveReqVO createReqVO) {
        DeclarationDO declaration = DeclarationConvert.INSTANCE.convertDeclarationDO(createReqVO);
        // 生成 报关单 编号
        declaration.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 获取明细列表
        List<DeclarationItem> declarationItemList = createReqVO.getChildren();
        // 根据实际转报关单数量重新计算合计信息（在insert之前计算，确保插入时数据正确）
        calcDeclaration(declaration, declarationItemList);
        // 插入报关单主表
        declarationMapper.insert(declaration);
        // 插入明细
        if (CollUtil.isNotEmpty(declarationItemList)) {
            declarationItemList.forEach(s -> {
                s.setDeclarationId(declaration.getId());
            });
            declarationItemMapper.insertBatch(declarationItemList);
        }
        // 插入订单链路
        List<String> linkCodeList = declaration.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)){
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(declaration.getId())
                    .setCode(declaration.getCode())
                    .setName(BusinessNameDict.DECLARATION_NAME)
                    .setType(OrderLinkTypeEnum.CUSTOMS_DECLARATION.getValue())
                    .setLinkCode(s)
                    .setItem(declarationItemList)
                    .setParentCode(declaration.getShipmentCode())
                    .setStatus(declaration.getStatus())
                    .setCompanyId(declaration.getForeignTradeCompanyId())
                    .setOrderMsg(declaration)).toList();
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        // 返回
        return  new CreatedResponse(declaration.getId(), declaration.getCode());
    }
    private void calcDeclaration(DeclarationDO declaration, List<DeclarationItem> declarationItemList) {
        if (Objects.isNull(declaration)||CollUtil.isEmpty(declarationItemList)){
            return;
        }
        declarationItemList.forEach(s->{
            Integer qtyPerOuterbox = s.getQtyPerOuterbox();
            BigDecimal boxCount = NumUtil.div(s.getDeclarationQuantity(), qtyPerOuterbox).setScale(0, RoundingMode.UP);
            s.setBoxCount(boxCount.intValue());
        });
        // 数量合计
        Integer totalQuantity = declarationItemList.stream().map(DeclarationItem::getDeclarationQuantity).reduce(Integer::sum).get();
        // 箱数合计
        BigDecimal totalBoxQuantity = declarationItemList.stream().map(s -> NumUtil.div(s.getDeclarationQuantity(), s.getQtyPerOuterbox()).setScale(0, RoundingMode.UP)).reduce(BigDecimal::add).get();
        // 毛重合计
        JsonWeight totalGrossweight = declarationItemList.stream().map(s -> CalcSpecificationUtil.calcTotalGrossweightByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(JsonWeight::add).get();
        // 净重合计
        JsonWeight totalNeight = declarationItemList.stream().map(s -> CalcSpecificationUtil.calcTotalNetWeightByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(JsonWeight::add).get();
        // 体积合计
        BigDecimal totalVolume = declarationItemList.stream().map(s -> CalcSpecificationUtil.calcTotalVolumeByBoxCount(s.getSpecificationList(), BigDecimal.valueOf(s.getBoxCount()))).reduce(BigDecimal::add).get();
        // 报关合计
        List<JsonAmount> totalDecliaration = new ArrayList<>();
        declarationItemList.stream().filter(s->Objects.nonNull(s.getDeclarationAmount())&&Objects.nonNull(s.getDeclarationAmount().getAmount())&& StrUtil.isEmpty(s.getDeclarationAmount().getCurrency()))
                .map(s->new JsonAmount(NumUtil.mul(s.getDeclaredQuantity(),s.getDeclarationAmount().getAmount()),s.getDeclarationAmount().getCurrency()))
                .collect(Collectors.groupingBy(JsonAmount::getCurrency))
                .forEach((k,v)->{
                    if (CollUtil.isEmpty(v)){
                        return;
                    }
                    BigDecimal amount = v.stream().map(JsonAmount::getAmount).reduce(BigDecimal::add).get();
                    totalDecliaration.add(new JsonAmount(amount,k));
                });
        declaration.setTotalQuantity(totalQuantity);
        declaration.setTotalBoxes(totalBoxQuantity.intValue());
        declaration.setTotalGrossweight(totalGrossweight);
        declaration.setTotalWeight(totalNeight);
        declaration.setTotalVolume(totalVolume);
    }
    @Override
    public void updateDeclaration(DeclarationSaveReqVO updateReqVO) {
        // 校验存在
        validateDeclarationExists(updateReqVO.getId());
        // 更新
        DeclarationDO updateObj = DeclarationConvert.INSTANCE.convertDeclarationDO(updateReqVO);
        // 更新明细
        List<DeclarationItem> declarationItemList = updateReqVO.getChildren();
        declarationItemMapper.delete(DeclarationItem::getDeclarationId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(declarationItemList)) {
            declarationItemList.forEach(s -> {
                s.setDeclarationId(updateReqVO.getId());
                s.setId(null);
            });
            declarationItemMapper.insertBatch(declarationItemList);
        }
        declarationMapper.updateById(updateObj);
    }

    @Override
    public void deleteDeclaration(Long id) {
        // 校验存在
        DeclarationDO declarationDO = validateDeclarationExists(id);
        List<DeclarationItem> declarationItemDOList = declarationItemMapper.selectList(new LambdaQueryWrapperX<DeclarationItem>().eq(DeclarationItem::getDeclarationId, id));
        if(CollUtil.isNotEmpty(declarationItemDOList)){
            List<Long> shipmentItemIdList = declarationItemDOList.stream().map(DeclarationItem::getShipmentItemId).distinct().toList();
            if(CollUtil.isNotEmpty(shipmentItemIdList)){
                List<DeclarationItem> declarationItemDOListUnDelete = declarationItemMapper.selectList(new LambdaQueryWrapperX<DeclarationItem>().ne(DeclarationItem::getDeclarationId, id).in(DeclarationItem::getShipmentItemId, shipmentItemIdList));
                Map<Long, List<DeclarationItem>> collectedMap = declarationItemDOListUnDelete.stream().collect(Collectors.groupingBy(x -> x.getShipmentItemId()));
                List<ShipmentItemDTO> shipmentItemDTOList = shipmentApi.getShipmentItemDTOListByIdList(shipmentItemIdList);
                if(CollUtil.isNotEmpty(shipmentItemDTOList)){
                    shipmentItemDTOList.forEach(si->{
                        //已报关数量查询
                        List<DeclarationItem> declarationQuantityOldList = declarationItemDOList.stream().filter(dil -> {
                            return dil.getShipmentItemId().equals(si.getId());
                        }).toList();
                        Integer declarationQuantityDeleteAll = declarationQuantityOldList.stream().map(DeclarationItem::getDeclarationQuantity).reduce(0, Integer::sum);
                        Integer declarationQuantityOldNew = NumberUtil.sub(si.getDeclarationQuantityOld(),declarationQuantityDeleteAll).intValue();
                        si.setDeclarationQuantityOld(declarationQuantityOldNew);
                        if (NumUtil.compare(si.getShippingQuantity(), declarationQuantityOldNew) == 0) {
                            si.setIsToDeclaration(ToDeclarationEnum.NOT_YET_TRANSFERRED.getStatus());
                        } else {
                            si.setIsToDeclaration(ToDeclarationEnum.PARTIAL_CONVERSION.getStatus());
                        }
                        si.setDeclaredQuantity(0);
                        if(CollUtil.isNotEmpty(collectedMap)){
                            List<DeclarationItem> declarationItemList = collectedMap.get(si.getId());
                            if(CollUtil.isNotEmpty(declarationItemList)){
                                si.setDeclaredQuantity(declarationItemList.get(0).getDeclaredQuantity());
                            }
                        }
                    });
                    shipmentApi.updateShipmentItem(shipmentItemDTOList,false);
                    shipmentApi.validateDeclarationFlag(List.of(declarationDO.getShipmentCode()));
                }
            }
        }
        // 删除
        declarationMapper.deleteById(id);
        orderLinkApi.updateOrderLinkStatus(declarationDO.getCode(), BusinessNameDict.DECLARATION_NAME, declarationDO.getLinkCodeList(), BooleanEnum.NO.getValue());
        // 删除明细
        declarationItemMapper.delete(DeclarationItem::getDeclarationId, id);
    }

    private DeclarationDO validateDeclarationExists(Long id) {
        DeclarationDO declarationDO = declarationMapper.selectById(id);
        if ( declarationDO== null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        return declarationDO;
    }

    @Override
    public DeclarationRespVO getDeclaration(Long id) {
        DeclarationDO declarationDO = declarationMapper.selectById(id);
        if (declarationDO == null) {
            return null;
        }
        DeclarationRespVO declarationRespVO = DeclarationConvert.INSTANCE.convertDeclarationRespVO(declarationDO);
        if(Objects.nonNull(declarationRespVO.getShipmentCode())){
            ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(declarationRespVO.getShipmentCode());
            if(Objects.nonNull(shipmentDTO.getInputUser())){
                declarationRespVO.setInputUserName(shipmentDTO.getInputUser().getNickname());
            }
            if(Objects.nonNull(shipmentDTO.getDocumenter())){
                declarationRespVO.setDocumenterUserName(shipmentDTO.getDocumenter().getNickname());
            }
            declarationRespVO.setShipNum(shipmentDTO.getShipNum());
            declarationRespVO.setBillLadingNum(shipmentDTO.getBillLadingNum());
//            declarationRespVO.setTotalGoodsValue(shipmentDTO.getTotalGoodsValue());
//            declarationRespVO.setTotalDeclaration(shipmentDTO.getTotalDeclaration());
//            declarationRespVO.setTotalPurchase(shipmentDTO.getTotalPurchase());
        }
        //获取明细
        List<DeclarationItem> declarationItemList = declarationItemMapper.selectList(DeclarationItem::getDeclarationId, id);
        declarationRespVO.setChildren(declarationItemList);
        declarationRespVO.setChildren(groupItem(declarationRespVO.getChildren()));
        return declarationRespVO;
    }

    private   List<DeclarationItem>  groupItem( List<DeclarationItem> children){
        if (CollUtil.isEmpty(children)) {
            return children;
        }

//        Map<DeclarationKey, List<DeclarationItem>> groupedItems = children.stream()
//                .collect(Collectors.groupingBy(item ->
//                        new DeclarationKey(
//                                item.getCustomsDeclarationNameEng(),
//                                item.getTaxRefundRate(),
//                                item.getHsCode(),
//                                item.getDeclarationName(),
//                                item.getHsMeasureUnit()),
//                        LinkedHashMap::new,  // 使用LinkedHashMap保持顺序
//                        Collectors.toList()
//                ));
        Map<String, Map<BigDecimal, Map<String, Map<String, Map<String, List<DeclarationItem>>>>>> groupedItemMap = children.stream().collect(Collectors.groupingBy(DeclarationItem::getCustomsDeclarationNameEng, Collectors.groupingBy(DeclarationItem::getTaxRefundRate, Collectors.groupingBy(DeclarationItem::getHsCode, Collectors.groupingBy(DeclarationItem::getDeclarationName, Collectors.groupingBy(DeclarationItem::getHsMeasureUnit))))));
        List<DeclarationItem> res = new ArrayList<>();
        groupedItemMap.forEach((customsDeclarationNameEng,customsDeclarationNameEngMap)->{
            if (CollUtil.isEmpty(customsDeclarationNameEngMap)){
                return;
            }
            customsDeclarationNameEngMap.forEach((taxRefundRate,taxRefundRateMap)->{
                if (CollUtil.isEmpty(taxRefundRateMap)){
                    return;
                }
                taxRefundRateMap.forEach((hsCode,hsCodeMap)->{
                    if (CollUtil.isEmpty(hsCodeMap)){
                        return;
                    }
                    hsCodeMap.forEach((declarationName,declarationNameMap)->{
                    if (CollUtil.isEmpty(declarationNameMap)){
                        return;
                    }
                        declarationNameMap.forEach((hsMeasureUnit,declarationItems)->{
                            DeclarationItem aggregatedItem = new DeclarationItem();
                            org.springframework.beans.BeanUtils.copyProperties(declarationItems.get(0),aggregatedItem);

                            aggregatedItem.setShippingQuantity(declarationItems.stream().mapToInt(DeclarationItem::getShippingQuantity).sum());        //出运数量
                            if(aggregatedItem.getShippingQuantity() == 0){
                                throw exception(SHIPPING_QUANTITY_ZERO);
                            }
                            aggregatedItem.setDeclarationQuantity(declarationItems.stream().mapToInt(DeclarationItem::getDeclarationQuantity).sum());  //报关数量
                            if(aggregatedItem.getDeclarationQuantity() == 0){
                                throw exception(DECLARATION_QUANTITY_ZERO);
                            }
                            aggregatedItem.setPurchaseTotalQuantity(declarationItems.stream().mapToInt(DeclarationItem::getPurchaseTotalQuantity).sum());  //采购数量
                            aggregatedItem.setDeclaredQuantity(declarationItems.stream().mapToInt(DeclarationItem::getDeclaredQuantity).sum());        //已报关数量
//                aggregatedItem.setOrderGrossProfitRate(aggregatedItem.getOrderGrossProfitRate());  //毛利率是否需要合并计算？
                            aggregatedItem.setBoxCount(declarationItems.stream().mapToInt(DeclarationItem::getBoxCount).sum());                        //箱数
                            aggregatedItem.setTotalVolume(declarationItems.stream().map(DeclarationItem::getTotalVolume).reduce(BigDecimal.ZERO,BigDecimal::add));      //总体积

                            //佣金金额
                            List<JsonAmount> commAmounts = declarationItems.stream().map(DeclarationItem::getCommissionAmount).filter(Objects::nonNull).toList();
                            if (!commAmounts.isEmpty()) {
                                String currency = commAmounts.get(0).getCurrency();
                                BigDecimal total = commAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setCommissionAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                            }

                            //销售金额
                            List<JsonAmount> saleAmounts = declarationItems.stream().map(DeclarationItem::getSaleAmount).filter(Objects::nonNull).toList();
                            if (!saleAmounts.isEmpty()) {
                                String currency = saleAmounts.get(0).getCurrency();
                                BigDecimal total = saleAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setSaleAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                                BigDecimal unitPrice = NumUtil.div(total, aggregatedItem.getDeclarationQuantity()).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                                aggregatedItem.setSaleUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
                            }

                            //退税金额
                            List<JsonAmount> refundAmounts = declarationItems.stream().map(DeclarationItem::getTaxRefundPrice).filter(Objects::nonNull).toList();
                            if (!refundAmounts.isEmpty()) {
                                String currency = refundAmounts.get(0).getCurrency();
                                BigDecimal total = refundAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setTaxRefundPrice(new JsonAmount().setAmount(total).setCurrency(currency));
                            }

                            //保险费
                            List<JsonAmount> insuranceAmounts = declarationItems.stream().map(DeclarationItem::getInsuranceFee).filter(Objects::nonNull).toList();
                            if (!insuranceAmounts.isEmpty()) {
                                String currency = insuranceAmounts.get(0).getCurrency();
                                BigDecimal total = insuranceAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setInsuranceFee(new JsonAmount().setAmount(total).setCurrency(currency));
                            }

                            //报关金额
                            List<JsonAmount> declarationAmounts = declarationItems.stream().map(DeclarationItem::getDeclarationAmount).filter(Objects::nonNull).toList();
                            if (!declarationAmounts.isEmpty()) {
                                String currency = declarationAmounts.get(0).getCurrency();
                                BigDecimal total = declarationAmounts.stream().map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setDeclarationAmount(new JsonAmount().setAmount(total).setCurrency(currency));
                                BigDecimal unitPrice = NumUtil.div(total, aggregatedItem.getDeclarationQuantity()).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                                aggregatedItem.setDeclarationUnitPrice(new JsonAmount().setAmount(unitPrice).setCurrency(currency));
                            }

                            //总净重
                            List<JsonWeight> grossWeight = declarationItems.stream().map(DeclarationItem::getTotalGrossweight).filter(Objects::nonNull).toList();
                            if (!grossWeight.isEmpty()) {
                                String unit = grossWeight.get(0).getUnit();
                                BigDecimal total = grossWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setTotalGrossweight(new JsonWeight().setWeight(total).setUnit(unit));
                            }

                            //总毛重
                            List<JsonWeight> netWeight = declarationItems.stream().map(DeclarationItem::getTotalNetweight).filter(Objects::nonNull).toList();
                            if (!netWeight.isEmpty()) {
                                String unit = netWeight.get(0).getUnit();
                                BigDecimal total = netWeight.stream().map(JsonWeight::getWeight).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                aggregatedItem.setTotalNetweight(new JsonWeight().setWeight(total).setUnit(unit));
                            }
                            BigDecimal boxCount = BigDecimal.valueOf(aggregatedItem.getBoxCount());
                            JsonSpecificationEntity jsonSpecificationEntity = new JsonSpecificationEntity();
                            if (BigDecimal.ZERO.compareTo(boxCount)>=0){
                                //体积
                                jsonSpecificationEntity.setOuterboxVolume(BigDecimal.ZERO);
                                //毛重
                                jsonSpecificationEntity.setOuterboxGrossweight(new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(aggregatedItem.getTotalGrossweight().getUnit()) );
                                //净重
                                jsonSpecificationEntity.setOuterboxNetweight(new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(aggregatedItem.getTotalNetweight().getUnit()) );
                            }else {
                                //体积
                                jsonSpecificationEntity.setOuterboxVolume( NumUtil.div(aggregatedItem.getTotalVolume(), boxCount, CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP));
                                //毛重
                                BigDecimal grossW = NumUtil.div(aggregatedItem.getTotalGrossweight().getWeight(), boxCount, CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                                jsonSpecificationEntity.setOuterboxGrossweight(new JsonWeight().setWeight(grossW).setUnit(aggregatedItem.getTotalGrossweight().getUnit()) );
                                //净重
                                BigDecimal grossN = NumUtil.div(aggregatedItem.getTotalNetweight().getWeight(), boxCount, CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                                jsonSpecificationEntity.setOuterboxNetweight(new JsonWeight().setWeight(grossN).setUnit(aggregatedItem.getTotalNetweight().getUnit()) );
                            }
                            aggregatedItem.setSpecificationList(List.of(jsonSpecificationEntity));
                            res.add(aggregatedItem);
                        });
                    });
                    });
                });
            });
        return res.stream().sorted(Comparator.comparingLong(DeclarationItem::getId)).collect(Collectors.toList());
//         return res;
    }


    @Override
    public PageResult<DeclarationRespVO> getDeclarationPage(DeclarationPageReqVO pageReqVO) {
        PageResult<DeclarationDO> declarationDOPageResult = declarationMapper.selectPage(pageReqVO);
        List<DeclarationDO> declarationDOList = declarationDOPageResult.getList();
        if (CollUtil.isEmpty(declarationDOList)) {
            return null;
        }
        List<Long> idList = declarationDOList.stream().map(DeclarationDO::getId).distinct().toList();
        Map<Long, List<DeclarationItem>> declarationItemMap = declarationItemMapper.getDeclarationItemMapByDeclarationIdList(idList);

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        List<DeclarationRespVO> declarationRespVOList = DeclarationConvert.INSTANCE.convertDeclarationRespVO(declarationDOList, declarationItemMap,packageTypeList);
        declarationRespVOList.forEach(vo->{
            vo.setChildren(groupItem(vo.getChildren()));
        });
        return new PageResult<DeclarationRespVO>().setTotal(declarationDOPageResult.getTotal()).setList(declarationRespVOList);
    }


    @Override
    public void exportExcel(Long id, String exportType ,String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        DeclarationDO declarationDO = declarationMapper.selectById(id);
        if (declarationDO == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (declarationDO.getForeignTradeCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, declarationDO.getForeignTradeCompanyId());
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
        List<DeclarationItem> declarationItemDOList = declarationItemMapper.selectList(new LambdaQueryWrapperX<DeclarationItem>().eq(DeclarationItem::getDeclarationId, id));
        declarationItemDOList = groupItem(declarationItemDOList);
        List<DeclarationItemExportVO> declarationItemExportVOList = new ArrayList<>();
        declarationItemDOList.forEach(item -> {
            DeclarationItemExportVO declarationItemExportVO = BeanUtils.toBean(item, DeclarationItemExportVO.class);
            if (item.getDeclarationUnitPrice() != null && item.getDeclarationUnitPrice().getAmount() != null) {
                declarationItemExportVO.setDeclarationUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getDeclarationUnitPrice().getAmount()).doubleValue());
            }
            if (item.getDeclarationAmount() != null && item.getDeclarationAmount().getAmount() != null) {
                declarationItemExportVO.setDeclarationTotalPrice(NumberFormatUtil.formatAmount(item.getDeclarationAmount().getAmount()).doubleValue());
            }
            BigDecimal grossightWeight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(item.getSpecificationList()).getWeight();
            declarationItemExportVO.setOuterboxGrossweightweight(NumberFormatUtil.formatWeight(grossightWeight));
            declarationItemExportVO.setTotalGrossweightweight(NumberFormatUtil.formatWeight(NumUtil.mul(grossightWeight, item.getBoxCount())));
            BigDecimal netweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(item.getSpecificationList()).getWeight();
            declarationItemExportVO.setOuterboxNetweightweight(NumberFormatUtil.formatWeight(netweight));
            declarationItemExportVO.setTotalNetweightweight(NumberFormatUtil.formatWeight(NumUtil.mul(netweight, item.getBoxCount())));
            declarationItemExportVO.setOuterboxVolume(CalcSpecificationUtil.calcSpecificationTotalVolume(item.getSpecificationList()));
            if (item.getTotalVolume() != null) {
                declarationItemExportVO.setTotalVolume(item.getTotalVolume());
            }
            if (item.getTaxRefundRate() != null) {
                declarationItemExportVO.setTaxRefundRate(NumberFormatUtil.formatRate(item.getTaxRefundRate()).doubleValue());
            }
            if(item.getUnitPerOuterbox()!=null){
                declarationItemExportVO.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getNameByValue(item.getUnitPerOuterbox()));
            }
            if(item.getTotalVolume()!=null){
                declarationItemExportVO.setTotalVolume(VolumeUtil.processVolume(item.getTotalVolume()));
            }
            declarationItemExportVOList.add((declarationItemExportVO));
        });
        AtomicInteger sortIndex = new AtomicInteger(1);
        declarationItemExportVOList.forEach(item-> {
            item.setSortNum(sortIndex.getAndIncrement());
        });
        HashMap<String, Object> params = new HashMap();
        switch (exportType) {
            case "detail": {
                params = getExportParams(declarationDO,declarationItemDOList);
                break;
            }
            case "ens": {
                //--导出类型为ENS
                declarationItemExportVOList.forEach(it->{
                    it.setHsMeasureUnit("CTNS");
                });
                processDeclarationItemExportData(declarationItemExportVOList,params);
                break;
            }
        }

        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content = null;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path-" + path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        String invoiceCode = declarationDO.getInvoiceCode();
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, invoiceCode + ".xlsx", declarationItemExportVOList, null, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }
    private void processDeclarationItemExportData(List<DeclarationItemExportVO> declarationItemExportVOList, Map<String, Object> params) {
        if (CollUtil.isEmpty(declarationItemExportVOList)) {
            return;
        }
        //  汇总数据
        Integer totalBoxCount = declarationItemExportVOList.stream()
                .map(DeclarationItemExportVO::getBoxCount)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        BigDecimal totalGrossweight = declarationItemExportVOList.stream()
                .map(DeclarationItemExportVO::getTotalGrossweightweight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume = declarationItemExportVOList.stream()
                .map(DeclarationItemExportVO::getTotalVolume)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 将汇总数据放入params
        params.put("totalBoxCount", totalBoxCount);
        params.put("totalGrossweight", totalGrossweight);
        params.put("totalVolume", totalVolume);
    }

    public HashMap<String, Object> getExportParams(DeclarationDO declarationDO ,  List<DeclarationItem> declarationItemDOList){
        HashMap<String, Object> params = new HashMap();
        if (Objects.nonNull(declarationDO.getShipmentCode())) {
            ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(declarationDO.getShipmentCode());
            if (Objects.nonNull(shipmentDTO.getInputUser())) {
                params.put("inputUserName", shipmentDTO.getInputUser().getNickname());
            }
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        params.put("invoiceCode", declarationDO.getInvoiceCode());
        params.put("code", declarationDO.getCode());
        if (declarationDO.getInputDate() != null) {
            String inputDate = dtf.format(declarationDO.getInputDate());
            params.put("inputDate", inputDate);
        }
        //params.put("status", declarationDO.getStatus());
        if (declarationDO.getShipDate() != null) {
            String shipDate = dtf.format(declarationDO.getShipDate());
            params.put("shipDate", shipDate);
        }
        List<String> saleContractCodeList = declarationItemDOList.stream().map(DeclarationItem::getSaleContractCode).distinct().toList();
        if (!saleContractCodeList.isEmpty()) {
            params.put("saleContractCode", declarationDO.getProtocolCode());   //2490
//            params.put("saleContractCode", String.join(",", saleContractCodeList));
        }
        params.put("tradeType", declarationDO.getTradeType());
        params.put("tradeCountryName", declarationDO.getTradeCountryName());
        List<String> settlementNameList = declarationItemDOList.stream().map(DeclarationItem::getSettlementName).distinct().toList();
        if (!saleContractCodeList.isEmpty()) {
            params.put("settlementName", String.join(",", settlementNameList));
        }
        params.put("forwarderCompanyName", declarationDO.getForwarderCompanyName());
        params.put("containerQuantity", declarationDO.getContainerQuantity());
        params.put("settlementTermType", declarationDO.getSettlementTermType());
        if (declarationDO.getTransportType() != null) {
            String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.TRANSPORT_TYPE, String.valueOf(declarationDO.getTransportType()));
            params.put("transportType", transportType.replace("(有订舱杂费)","").replace("(无订舱杂费)",""));
        }
        params.put("departurePortName", declarationDO.getDeparturePortName());
        params.put("destinationPortName", declarationDO.getDestinationPortName());
        params.put("totalQuantity", declarationDO.getTotalQuantity());
        params.put("totalBoxes", declarationDO.getTotalBoxes());
        params.put("receivePerson", declarationDO.getReceivePerson());
        params.put("notifyPerson", declarationDO.getNotifyPerson());
        params.put("frontShippingMark", declarationDO.getFrontShippingMark());
        if (declarationDO.getTotalGrossweight() != null && declarationDO.getTotalGrossweight().getWeight() != null) {
            params.put("totalGrossweight", NumberFormatUtil.formatWeight(declarationDO.getTotalGrossweight().getWeight()));
        }
        if (declarationDO.getTotalWeight() != null && declarationDO.getTotalWeight().getWeight() != null) {
            params.put("totalWeight", NumberFormatUtil.formatWeight(declarationDO.getTotalWeight().getWeight()));
        }
        if (declarationDO.getTotalVolume() != null) {
            params.put("totalVolume", VolumeUtil.processVolume(declarationDO.getTotalVolume()));
        }
        Long companyId = declarationDO.getForeignTradeCompanyId();
        if (Objects.nonNull(companyId)){
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            Optional.ofNullable(companyDTO).ifPresent(s->{
                params.put("companyTitle", companyDTO.getCompanyName());
            });
        }
        if (CollUtil.isNotEmpty(declarationItemDOList)) {
            //客户编号和客户名称存在多个用逗号分割
            List<String> custCodeList = declarationItemDOList.stream().map(DeclarationItem::getCustCode).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(custCodeList)) {
                params.put("custCode", String.join(",", custCodeList));
            }
            List<String> custNameList = declarationItemDOList.stream().map(DeclarationItem::getCustName).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(custNameList)) {
                params.put("custName", String.join(",", custNameList));
            }
            List<String> currencyList = declarationItemDOList.stream().map(DeclarationItem::getCurrency).toList().stream().distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(currencyList)) {
                params.put("currency", String.join(",", currencyList));
            }
            List<String> custPoList = declarationItemDOList.stream().map(DeclarationItem::getCustPo).distinct().toList();
            if (!custPoList.isEmpty()) {
                params.put("custPo", String.join(",", custPoList));
            }
            BigDecimal sumAmount = BigDecimal.valueOf(0);
            for (DeclarationItem s : declarationItemDOList) {
                if (Objects.nonNull(s.getSaleUnitPrice())
                        && Objects.nonNull(s.getDeclarationQuantity())
                        && Objects.nonNull(s.getSaleUnitPrice().getAmount())) {
                    BigDecimal total = s.getSaleUnitPrice().getAmount().multiply(BigDecimal.valueOf(s.getDeclarationQuantity()));
                    sumAmount = sumAmount.add(total);
                }
            }
            params.put("totalAmount", NumberFormatUtil.formatAmount(sumAmount));
        }
        return params;
    }

    @Override
    public Map<Long, DeclarationItemDTO> getDeclarationListByShipmentId(List<Long> list) {

        List<DeclarationItem> declarationItems = declarationItemMapper.selectList(DeclarationItem::getShipmentItemId, list);
        if(CollUtil.isEmpty(declarationItems)){
            return null;
        }
        List<DeclarationItemDTO> simpleDataList = BeanUtils.toBean(declarationItems, DeclarationItemDTO.class);
        return simpleDataList.stream().collect(Collectors.toMap(DeclarationItemDTO::getShipmentItemId,s->s,(s1,s2)->s2));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeclarationGroupBy(DeclarationSaveReqVO updateReqVO) {
        Long voId = updateReqVO.getId();
        validateDeclarationExists(voId);
        declarationMapper.updateById(DeclarationConvert.INSTANCE.convertDeclarationDO(updateReqVO));

        List<DeclarationItem> declarationItemList = updateReqVO.getChildren();
        if (CollUtil.isEmpty(declarationItemList)) {
          throw exception(DECLARATION_NOT_EXISTS);
        }
        List<DeclarationItem> declarationItems = declarationItemMapper.selectList(DeclarationItem::getDeclarationId, voId);
        declarationItems.forEach(item -> {
            Optional<DeclarationItem> first = declarationItemList.stream().filter(s ->
                    Objects.equals(s.getDeclarationName(), item.getDeclarationName()) &&
                    Objects.equals(s.getCustomsDeclarationNameEng(), item.getCustomsDeclarationNameEng()) &&
                    s.getTaxRefundRate().compareTo(item.getTaxRefundRate()) == 0  &&
                    Objects.equals(s.getHsCode(), item.getHsCode()) &&
                    Objects.equals(s.getHsMeasureUnit(), item.getHsMeasureUnit())).findFirst();
            if(first.isPresent()){
                DeclarationItem declarationItem = first.get();
                item.setSettlementTermType(declarationItem.getSettlementTermType());
                item.setCustomsDeclareElements(declarationItem.getCustomsDeclareElements());
            }else {
                throw exception(DECLARATION_NOT_EXISTS);
            }
        });
        declarationItemMapper.updateBatch(declarationItems);
    }
}