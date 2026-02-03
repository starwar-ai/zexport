package com.syj.eplus.module.dms.service.shipmentplan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.*;
import com.syj.eplus.module.dms.convert.shipmentplan.ShipmentPlanConvert;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import com.syj.eplus.module.dms.dal.mysql.shipment.ShipmentMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentplan.ShipmentPlanMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentplanitem.ShipmentPlanItemMapper;
import com.syj.eplus.module.dms.util.CalcShipmentPlanUtil;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuBomDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchaseplan.PurchasePlanApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractItemDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.ANTI_AUDIT_EXCEPT;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.SALE_CONTRACT_STATUS_EMPTY;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;

/**
 * 出运计划单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class ShipmentPlanServiceImpl implements ShipmentPlanService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ShipmentPlanMapper shipmentPlanMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    private ShipmentPlanItemMapper shipmentPlanItemMapper;
    private static final String SN_TYPE = "shipment_plan";
    private static final String CODE_PREFIX = "SP";

    /**
     * 操作日志标识
     */
    public static final String OPERATOR_EXTS_KEY = "shipmentPlanCode";

    /**
     * 结案
     */
    public static final String SIGN_BACK_OPERATOR_MESSAGE = "【结案】 {},【备注】 {}";
    @Resource
    private ConfigApi configApi;
    @Resource
    private RateApi rateApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    @Lazy
    private ShipmentMapper shipmentMapper;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private IStockApi stockApi;

    @Resource
    private SkuApi skuApi;

    @Resource
    private ReportApi reportApi;

    @Resource
    private FileApi fileApi;

    @Resource
    @Lazy
    private PurchasePlanApi purchasePlanApi;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createShipmentPlan(ShipmentPlanSaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<ShipmentPlanItem> oldShipmentPlanItemList = createReqVO.getChildren();
        Set<Long> saleItems = oldShipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).collect(Collectors.toSet());
        // 根据销售明细id获取采购模式
        Map<Long, Integer> purchaseModelCache = purchasePlanApi.getPurchaseModelBySaleItemIds(saleItems);
        // 校验采购合同号是否存在
        Set<String> purchaseContractCodes = oldShipmentPlanItemList.stream()
                .filter(s->s.getPurchaseQuantity()>0&&Objects.nonNull(purchaseModelCache.get(s.getSaleContractItemId()))&& PurchaseModelEnum.STANDARD.getCode().equals(purchaseModelCache.get(s.getSaleContractItemId())))
                .map(s->{
                    if (StrUtil.isEmpty(s.getPurchaseContractCode())){
                        throw exception(PURCHASE_CONTRACT_CODE_IS_EMPTY);
                    }else {
                        return s.getPurchaseContractCode();
                    }
                })
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(purchaseContractCodes)){
            Collection<String> notExistCodes = purchaseContractApi.validatePurContractExists(purchaseContractCodes);
            if (CollUtil.isNotEmpty(notExistCodes)){
                throw exception(PURCHASE_CONTRACT_NOT_EXISTS_OR_DELETED,notExistCodes.stream().collect(Collectors.joining(CommonDict.COMMA)));
            }
        }
        List<ShipmentPlanItem> shipmentPlanItemList = oldShipmentPlanItemList.stream().filter(s->s.getShippingQuantity()>0).toList();
        List<String> saleContractCodeList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractCode).distinct().toList();
        if (CollUtil.isEmpty(saleContractCodeList)){
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        Map<String, Map<String, Integer>> stockMap = stockApi.validateStockByContractCodeList(saleContractCodeList);
        if (CollUtil.isEmpty(stockMap)){
            throw exception(CONTRACT_ALL_NOT_STOCK);
        }
        List<Long> saleContractItemIdList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).distinct().toList();
        Map<Long, SaleContractItemDTO> saleItemMap = saleContractApi.getItemListByIds(saleContractItemIdList);
        Map<String, Integer> skuTypeMap = skuApi.getSkuTypeMap(shipmentPlanItemList.stream().map(ShipmentPlanItem::getSkuCode).toList());
        // 校验库存数
        validatedStockQuantity(shipmentPlanItemList, saleItemMap, skuTypeMap, stockMap);
        ShipmentPlanDO shipmentPlan = ShipmentPlanConvert.INSTANCE.convertShipmentPlanDO(createReqVO);
        // 生成 出运计划单 编号
        shipmentPlan.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 计算子项列表
        CalcShipmentPlanUtil.checkItemCost(createReqVO, configApi.getConfigMap(), rateApi.getDailyRateMap());
        // 当前用户为录入人
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        shipmentPlan.setInputUser(userDept);
        if (BooleanEnum.YES.getValue().equals(createReqVO.getSubmitFlag())){
            shipmentPlan.setStatus(ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue());
        }else {
            shipmentPlan.setStatus(ShippingPlanStatusEnum.PAD_SUBMIT.getValue());
        }

        // 插入
        shipmentPlanMapper.insert(shipmentPlan);
        // 重写加减项
        List<AddSubItemDTO> addSubItemList = createReqVO.getAddSubItemList();
        if (CollUtil.isNotEmpty(addSubItemList)) {
            saleContractApi.batchUpdateAddSubItem(addSubItemList);
        }

        // 插入明细
        if (CollUtil.isNotEmpty(shipmentPlanItemList)){
            shipmentPlanItemList.forEach(s->{
                s.setId(null);
                s.setShipmentPlanId(shipmentPlan.getId());
            });
            // 回写销售明细出运数量
            List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemList.stream().filter(s -> Objects.nonNull(s.getSaleContractItemId())).toList();
            if (CollUtil.isNotEmpty(shipmentPlanItems)) {
                Map<Long, Integer> shipmentQtyMap = new HashMap<>();
                shipmentPlanItems.forEach(s -> {
                    shipmentQtyMap.put(s.getSaleContractItemId(), s.getShippingQuantity());
                });
                if (CollUtil.isNotEmpty(shipmentQtyMap)) {
                    saleContractApi.updateShipmentQuantity(shipmentQtyMap, false);
                }
            }
            shipmentPlanItemMapper.insertBatch(shipmentPlanItemList);
        }
        // 插入订单链路
        List<String> linkCodeList = shipmentPlan.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)){
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(shipmentPlan.getId())
                    .setCode(shipmentPlan.getCode())
                    .setName(BusinessNameDict.SHIPMENT_PLAN_NAME)
                    .setType(OrderLinkTypeEnum.SHIPPING_PLAN.getValue())
                    .setLinkCode(s)
                    .setItem(shipmentPlanItemList)
                    .setParentCode(shipmentPlan.getSaleContractCode())
                    .setStatus(shipmentPlan.getStatus())
                    .setCompanyId(shipmentPlan.getForeignTradeCompanyId())
                    .setOrderMsg(shipmentPlan)).toList();
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        // 插入操作日志
        OperateLogUtils.setContent("【下推出运计划】");
        OperateLogUtils.addExt("saleContractCode", shipmentPlan.getSaleContractCode());
        // 返回
        responses.add(new CreatedResponse(shipmentPlan.getId(),shipmentPlan.getCode()));
        return responses;
    }

    /**
     * 校验明细库存是否可出运
     * @param shipmentPlanItemList 出运计划明细
     * @param saleItemMap 销售明细
     * @param skuTypeMap 产品类型
     * @param stockMap 库存缓存
     */
    private void validatedStockQuantity(List<ShipmentPlanItem> shipmentPlanItemList,Map<Long, SaleContractItemDTO> saleItemMap,Map<String, Integer> skuTypeMap,Map<String, Map<String, Integer>> stockMap){
        if (CollUtil.isEmpty(shipmentPlanItemList)){
            return;
        }
        Map<String, Long> skuCodeIdMap = shipmentPlanItemList.stream().collect(Collectors.toMap(ShipmentPlanItem::getSkuCode, ShipmentPlanItem::getSkuId, (o, n) -> n));
        Map<String, Map<String, Optional<Integer>>> shipmentPlanItemMap = shipmentPlanItemList.stream().collect(Collectors.groupingBy(ShipmentPlanItem::getSaleContractCode, Collectors.groupingBy(ShipmentPlanItem::getSkuCode, Collectors.mapping(ShipmentPlanItem::getShippingQuantity, Collectors.reducing(Integer::sum)))));
        Map<Long, List<SkuBomDTO>> allBomDTOMap = skuApi.getAllBomDTOMap();
        Map<String, ShipmentPlanItem> skuCodeMap = shipmentPlanItemList.stream().collect(Collectors.toMap(ShipmentPlanItem::getSkuCode, s -> s, (o, n) -> n));
        shipmentPlanItemMap.forEach((saleContractCode,shippingQuantityMap)->{
            if (CollUtil.isEmpty(shippingQuantityMap)){
                return;
            }
            shippingQuantityMap.forEach((skuCode,shippingQuantityOpt)->{
                if (CollUtil.isEmpty(shipmentPlanItemMap)){
                    return;
                }
                if (shippingQuantityOpt.isEmpty()){
                    return;
                }
                ShipmentPlanItem shipmentPlanItem = skuCodeMap.get(skuCode);
                Integer shippingQuantity = shippingQuantityOpt.get();
                Map<String, Integer> contractStockMap = stockMap.get(saleContractCode);
                if (CollUtil.isEmpty(contractStockMap)){
                    throw exception(CONTRACT_NOT_STOCK,saleContractCode);
                }
                Integer skuType = skuTypeMap.get(skuCode);
                // 普通产品直接校验库存
                if (SkuTypeEnum.GENERAL_PRODUCTS.getValue().equals(skuType)){
                    Integer realStockQuantity = contractStockMap.get(skuCode);
                    if (Objects.isNull(realStockQuantity)){
                        throw exception(CONTRACT_SKU_NOT_STOCK,shipmentPlanItem.getCskuCode(),shipmentPlanItem.getBasicSkuCode());
                    }
                    if (shippingQuantity>realStockQuantity){
                        throw exception(CONTRACT_SKU_STOCK_NOT_ENOUGH,shipmentPlanItem.getCskuCode(),shipmentPlanItem.getBasicSkuCode());
                    }
                }else if (SkuTypeEnum.PRODUCT_MIX.getValue().equals(skuType)){
                    // 组合产品先校验本身库存数
                    Integer realStockQuantity = Objects.isNull(contractStockMap.get(skuCode))?CalculationDict.ZERO:contractStockMap.get(skuCode);
                    if (shippingQuantity>realStockQuantity){
                        // 递归计算子产品库存数量
                        checkBomSkuStock(skuCodeIdMap.get(skuCode),shippingQuantity-realStockQuantity,allBomDTOMap,contractStockMap);
                    }
                }
            });
        });
    }

    /**
     * 递归计算子产品库存数量
     * @param skuId 产品主键
     * @param stockQuantity 应用库存数量
     * @param allBomDTOMap 组合产品列表
     * @param contractStockMap 库存缓存
     */
    private void checkBomSkuStock(Long skuId,Integer stockQuantity,Map<Long, List<SkuBomDTO>> allBomDTOMap,Map<String, Integer> contractStockMap){
        List<SkuBomDTO> skuBomDTOS = allBomDTOMap.get(skuId);
        // TODO 组合产品是否必须有bom信息?
        if (CollUtil.isEmpty(skuBomDTOS)){
            return;
        }
        List<String> skuCodeList = skuBomDTOS.stream().map(SkuBomDTO::getSkuCode).distinct().collect(Collectors.toList());
        Map<String, SimpleSkuDTO> cskuAndBasicSkuCodeMap = skuApi.getCskuAndBasicSkuCodeMapByCodes(skuCodeList);
        List<Long> parentSkuIdList = allBomDTOMap.values().stream().flatMap(List::stream).map(SkuBomDTO::getParentSkuId).distinct().toList();
        skuBomDTOS.forEach(s->{
            Integer qty = s.getQty();
            // 应用库存数量
            Integer subStockQuantity = stockQuantity * qty;
            Integer skuType = s.getSkuType();
            // 实际库存数量
            Integer realStockQuantity = Objects.isNull(contractStockMap.get(s.getSkuCode()))?CalculationDict.ZERO:contractStockMap.get(s.getSkuCode());
            if (parentSkuIdList.contains(s.getSkuId())){
                // 组合产品先校验本身库存是否满足
                if (realStockQuantity == CalculationDict.ZERO){
                    // 当组合产品本身没有库存则查找子节点库存数量
                    checkBomSkuStock(s.getSkuId(),subStockQuantity,allBomDTOMap,contractStockMap);
                }
                // 当组合产品本身有库存但是不等于销售数量则合并计算子节点库存数量
                if (realStockQuantity <= subStockQuantity){
                    // 应用库存数量 = 应用库存数量 - 实际库存数量
                    subStockQuantity = subStockQuantity - realStockQuantity;
                    // 应用库存用尽进入下一个循环
                    if (subStockQuantity <= CalculationDict.ZERO){
                        return;
                    }
                    checkBomSkuStock(s.getSkuId(),subStockQuantity,allBomDTOMap,contractStockMap);
                }
        }else {
                SimpleSkuDTO simpleSkuDTO = cskuAndBasicSkuCodeMap.get(s.getSkuCode());
                // 如果是普通产品未找到对应库存则终止下推
                if (realStockQuantity == CalculationDict.ZERO) {
                    if (Objects.nonNull(simpleSkuDTO)){
                        throw exception(CONTRACT_SKU_NOT_STOCK,simpleSkuDTO.getCskuCode(),simpleSkuDTO.getBasicSkuCode());
                    }else {
                        throw exception(CONTRACT_SKU_NOT_STOCK,null,s.getSkuCode());
                    }
                }
                // 实际库存数量与应用库存数量不一致则终止下推
                if (realStockQuantity < stockQuantity) {
                    if (Objects.nonNull(simpleSkuDTO)){
                        throw exception(CONTRACT_SKU_STOCK_NOT_EQUAL,simpleSkuDTO.getCskuCode(),simpleSkuDTO.getBasicSkuCode());
                    }else {
                        throw exception(CONTRACT_SKU_STOCK_NOT_EQUAL,null,s.getSkuCode());
                    }
                }
            }
        });
    }

    @Override
    public List<CreatedResponse> createShipmentPlan(String createReqVO) {
        ShipmentPlanSaveReqVO saveReqVO;
        try {
            saveReqVO = JsonUtils.parseObject(createReqVO, new TypeReference<>(){});
        } catch (Exception e) {
            throw exception(TRANSFORM_SHIPMENT_PLAN_ERR);
        }
        return createShipmentPlan(saveReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShipmentPlan(ShipmentPlanSaveReqVO updateReqVO) {
        // 校验存在
        validateShipmentPlanExists(updateReqVO.getId());
        // 原出运数量
        Map<String, Integer> shipmentQtyOldMap = new HashMap<>();
        LambdaQueryWrapper<ShipmentPlanItem> query = new LambdaQueryWrapper<>();
        query.eq(ShipmentPlanItem::getShipmentPlanId,updateReqVO.getId());
        List<ShipmentPlanItem> originPlanItemList = shipmentPlanItemMapper.selectList(query);
        if (CollUtil.isNotEmpty(originPlanItemList)) {
            originPlanItemList.forEach(s -> {
                shipmentQtyOldMap.put(s.getUniqueCode(), s.getShippingQuantity());
            });
        }
        // 更新
        ShipmentPlanDO updateObj = ShipmentPlanConvert.INSTANCE.convertShipmentPlanDO(updateReqVO);
        if (BooleanEnum.YES.getValue().equals(updateReqVO.getSubmitFlag())){
            updateObj.setStatus(ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue());
        }else {
            updateObj.setStatus(ShippingPlanStatusEnum.PAD_SUBMIT.getValue());
        }
        shipmentPlanMapper.updateById(updateObj);
        List<ShipmentPlanItem> shipmentPlanItemList = updateReqVO.getChildren();
        if (CollUtil.isNotEmpty(shipmentPlanItemList)) {
            shipmentPlanItemMapper.updateBatch(shipmentPlanItemList);
        }
        // 重写加减项
        List<AddSubItemDTO> addSubItemList = updateReqVO.getAddSubItemList();
        if (CollUtil.isNotEmpty(addSubItemList)) {
            saleContractApi.batchUpdateAddSubItem(addSubItemList);
        }
        // 回写销售明细出运数量
        if (CollUtil.isNotEmpty(shipmentPlanItemList)) {
            Map<Long, Integer> shipmentQtyMap = new HashMap<>();
            shipmentPlanItemList.forEach(s -> {
                Integer shippingQuantity = s.getShippingQuantity();
                Integer originShippingQty = shipmentQtyOldMap.get(s.getUniqueCode());
                Integer currentQty = NumberUtil.sub(shippingQuantity, originShippingQty).intValue();
                if (currentQty != 0) {
                    shipmentQtyMap.put(s.getSaleContractItemId(), currentQty);
                }
            });
            if (CollUtil.isNotEmpty(shipmentQtyMap)) {
                saleContractApi.updateShipmentQuantity(shipmentQtyMap, false);
            }
        }
    }

    @Override
    public void deleteShipmentPlan(Long id) {
        // 校验存在
        validateShipmentPlanExists(id);
        // 删除
        shipmentPlanMapper.deleteById(id);
    }

    private ShipmentPlanDO validateShipmentPlanExists(Long id) {
        ShipmentPlanDO shipmentPlanDO = shipmentPlanMapper.selectById(id);
        if ( shipmentPlanDO== null) {
            throw exception(SHIPMENT_PLAN_NOT_EXISTS);
        }
        return shipmentPlanDO;
    }

    @Override
    public ShipmentPlanRespVO getShipmentPlan(Long id,boolean isUpdate) {
        ShipmentPlanDO shipmentPlanDO = shipmentPlanMapper.selectById(id);
        if (shipmentPlanDO == null) {
            return null;
        }
        ShipmentPlanRespVO shipmentPlanRespVO = ShipmentPlanConvert.INSTANCE.convertShipmentPlanRespVO(shipmentPlanDO);
        LambdaQueryWrapper<ShipmentPlanItem> queryWrapper = new LambdaQueryWrapper<ShipmentPlanItem>().eq(ShipmentPlanItem::getShipmentPlanId, id);
        List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanItemMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(shipmentPlanItemList)) {
            List<Long> saleContractItemIdList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).toList();
            List<SaleContractItemSaveDTO> saleContractItemSaveDTOList = saleContractApi.listSaleContractItem(saleContractItemIdList);
            Map<Long, SaleContractItemSaveDTO> collect = saleContractItemSaveDTOList.stream().collect(Collectors.toMap(SaleContractItemSaveDTO::getId, Function.identity()));
            shipmentPlanItemList.forEach(x-> {
                SaleContractItemSaveDTO saleContractItemSaveDTO = collect.get(x.getSaleContractItemId());
                if(Objects.isNull(saleContractItemSaveDTO)){
                    throw exception(SHIPMENT_PLAN_NOT_SALE_ITEM);
                }
                if (isUpdate){
                    // 本次出运数量
                    Integer shippingQuantity = x.getShippingQuantity();
                    x.setUnshippedQuantity(saleContractItemSaveDTO.getUnshippedQuantity() == null ? saleContractItemSaveDTO.getQuantity() : NumberUtil.add(saleContractItemSaveDTO.getUnshippedQuantity(),shippingQuantity).intValue());
                    x.setShippedQuantity(saleContractItemSaveDTO.getShippedQuantity() == null ? 0 : NumberUtil.sub(saleContractItemSaveDTO.getShippedQuantity(),shippingQuantity).intValue());
                }else {
                    x.setUnshippedQuantity(saleContractItemSaveDTO.getUnshippedQuantity() == null ? saleContractItemSaveDTO.getQuantity() : saleContractItemSaveDTO.getUnshippedQuantity());
                    x.setShippedQuantity(saleContractItemSaveDTO.getShippedQuantity() == null ? 0 : saleContractItemSaveDTO.getShippedQuantity());
                }
            });
        }

        if (CollUtil.isNotEmpty(shipmentPlanItemList)) {
            List<String> saleContractCodeList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractCode).distinct().toList();
            if (CollUtil.isEmpty(saleContractCodeList)) {
                return shipmentPlanRespVO;
            }
            // 赋值验货状态
            Map<Long, Map<String, Integer>> checkStatusMap = purchaseContractApi.getCheckStatusMap(shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).collect(Collectors.toSet()));
            List<AddSubItemDTO> addSubItemListByContractCodeList = saleContractApi.getAddSubItemListByContractCodeList(saleContractCodeList);
            shipmentPlanRespVO.setAddSubItemList(addSubItemListByContractCodeList);
            List<Long> skuIdList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSkuId).distinct().toList();
            Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
            shipmentPlanItemList.forEach(s->{
                if (CollUtil.isNotEmpty(checkStatusMap)){
                    Map<String, Integer> statusMap = checkStatusMap.get(s.getSaleContractItemId());
                    if (CollUtil.isNotEmpty(statusMap)){
                        s.setCheckStatus(statusMap.get(s.getSkuCode()));
                    }
                }
                if(CollUtil.isNotEmpty(simpleSkuDTOMap)){
                    Boolean aBoolean = simpleSkuDTOMap.get(s.getSkuId());
                    if(Objects.isNull( aBoolean)){
                        aBoolean = false;
                    }
                    s.setSkuDeletedFlag(aBoolean ? 0 : 1 );
                }
            });
            // 客户PO
            String custPo = shipmentPlanItemList.stream().map(ShipmentPlanItem::getCustPo).filter(Objects::nonNull).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentPlanRespVO.setCustPo(custPo);
            // 客户名称
            String custName = shipmentPlanItemList.stream().map(ShipmentPlanItem::getCustName).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentPlanRespVO.setCustName(custName);
            // 销售合同号
            String saleContractCode = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractCode).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentPlanRespVO.setSaleContractCode(saleContractCode);
            // 业务员
            String managerName = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSales).filter(Objects::nonNull).map(UserDept::getNickname).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentPlanRespVO.setManagerName(managerName);
        }
        // 获取销售单价
        List<Long> saleItemIdList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).distinct().toList();
        Map<Long, JsonAmount> saleContractItemAmountMap = saleContractApi.getSaleContractItemAmountMap(saleItemIdList);
        shipmentPlanItemList.forEach(s->{
            JsonAmount amount = saleContractItemAmountMap.get(s.getSaleContractItemId());
            if (Objects.isNull(amount)||Objects.isNull(amount.getAmount())){
                return;
            }
            s.setSaleUnitPrice(amount);
            s.setSaleAmount(new JsonAmount().setAmount(NumUtil.mul(amount.getAmount(),s.getShippingQuantity())).setCurrency(amount.getCurrency()));
        });
        shipmentPlanRespVO.setChildren(shipmentPlanItemList);
        return shipmentPlanRespVO;
    }

    @Override
    public PageResult<ShipmentPlanRespVO> getShipmentPlanPage(ShipmentPlanPageReqVO pageReqVO) {
        if(pageReqVO.getStatus()==null){
            pageReqVO.setNeStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue());
        }
        if (StrUtil.isNotEmpty(pageReqVO.getCustName())||StrUtil.isNotEmpty(pageReqVO.getSaleContractCode())||StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode()) || StrUtil.isNotEmpty(pageReqVO.getCskuCode()) || StrUtil.isNotEmpty(pageReqVO.getSkuCode())|| StrUtil.isNotEmpty(pageReqVO.getCustPo())){
            LambdaQueryWrapperX<ShipmentPlanItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentPlanItem>()
                    .likeIfPresent(ShipmentPlanItem::getCustName, pageReqVO.getCustName())
                    .likeIfPresent(ShipmentPlanItem::getSaleContractCode, pageReqVO.getSaleContractCode())
                    .likeIfPresent(ShipmentPlanItem::getCskuCode, pageReqVO.getCskuCode())
                    .likeIfPresent(ShipmentPlanItem::getBasicSkuCode, pageReqVO.getBasicSkuCode())
                    .likeIfPresent(ShipmentPlanItem::getSkuCode, pageReqVO.getSkuCode())
                    .likeIfPresent(ShipmentPlanItem::getCustPo, pageReqVO.getCustPo());
            List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanItemMapper.selectList(queryWrapperX);
            if (CollUtil.isEmpty(shipmentPlanItemList)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(shipmentPlanItemList.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().toList());
        }
        PageResult<ShipmentPlanDO> planDOPageResult = shipmentPlanMapper.selectPage(pageReqVO);
        List<ShipmentPlanDO> doPageResultList = planDOPageResult.getList();
        if (CollUtil.isEmpty(doPageResultList)) {
            return PageResult.empty();
        }
        List<ShipmentPlanRespVO> shipmentPlanRespVOList = BeanUtils.toBean(doPageResultList, ShipmentPlanRespVO.class);
        List<Long> planIdList = doPageResultList.stream().map(ShipmentPlanDO::getId).toList();
        List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanItemMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanItem>().in(ShipmentPlanItem::getShipmentPlanId, planIdList));
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return new PageResult<ShipmentPlanRespVO>().setList(shipmentPlanRespVOList).setTotal(planDOPageResult.getTotal());
        }

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        if(CollUtil.isNotEmpty(packageTypeList)){
            shipmentPlanItemList.forEach(s->{
                if(CollUtil.isNotEmpty(s.getPackageType())){
                    List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt->distinctPackgeType.contains(pt.getId())).toList();
                    if(CollUtil.isNotEmpty(tempPackageTypeList)){
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        s.setPackageTypeName(String.join(",",packageTypeNameList));
                        List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                        s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                    }
                }
            });
        }
        // 获取销售单价
        List<Long> saleItemIdList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractItemId).distinct().toList();
        Map<Long, JsonAmount> saleContractItemAmountMap = saleContractApi.getSaleContractItemAmountMap(saleItemIdList);
        shipmentPlanItemList.forEach(s->{
            JsonAmount amount = saleContractItemAmountMap.get(s.getSaleContractItemId());
            if (Objects.isNull(amount)||Objects.isNull(amount.getAmount())){
                return;
            }
            s.setSaleUnitPrice(amount);
            s.setSaleAmount(new JsonAmount().setAmount(NumUtil.mul(amount.getAmount(),s.getShippingQuantity())).setCurrency(amount.getCurrency()));
        });
        Map<Long, List<ShipmentPlanItem>> shipmentItemMap = shipmentPlanItemList.stream().collect(Collectors.groupingBy(ShipmentPlanItem::getShipmentPlanId));
        shipmentPlanRespVOList.forEach(s -> {
            List<ShipmentPlanItem> shipmentPlanItems = shipmentItemMap.get(s.getId());
            if (CollUtil.isEmpty(shipmentPlanItems)){
                return;
            }
            s.setSaleContractCode(shipmentPlanItems.stream().map(ShipmentPlanItem::getSaleContractCode).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA)));
            s.setCustName(shipmentPlanItems.stream().map(ShipmentPlanItem::getCustName).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA)));
            s.setChildren(shipmentPlanItems);
        });
        return new PageResult<ShipmentPlanRespVO>().setList(shipmentPlanRespVOList).setTotal(planDOPageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeShipmentPlan(CloseShipmentPlanReq closeShipmentPlanReq) {
        Long parentId = null;
        if (Objects.nonNull(closeShipmentPlanReq.getItemId())) {
            ShipmentPlanItem baseItem = shipmentPlanItemMapper.selectById(closeShipmentPlanReq.getItemId());
            if (Objects.isNull(baseItem)){
                throw exception(SHIPMENT_PLAN_ITEM_NOT_EXISTS);
            }
            validateAntiAuditStatus(baseItem.getShipmentPlanId());
            ShipmentPlanItem shipmentPlanItem = new ShipmentPlanItem();
            shipmentPlanItem.setId(closeShipmentPlanReq.getItemId());
            shipmentPlanItem.setStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue());
            shipmentPlanItemMapper.updateById(shipmentPlanItem);
            List<Integer> shipmentItemStatusLink = shipmentPlanItemMapper.getShipmentPlanItemStatusLink(closeShipmentPlanReq.getItemId());
            if (CollUtil.isEmpty(shipmentItemStatusLink)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否出运计划明细全部结案
            boolean allClosed = shipmentItemStatusLink.stream()
                    .allMatch(status -> status.equals(ShippingPlanStatusEnum.CASE_CLOSED.getValue()));
            // 子项全部结案则出运计划状态变更为结案
            if (allClosed) {
                ShipmentPlanItem closedShipmentPlanItem = shipmentPlanItemMapper.selectById(closeShipmentPlanReq.getItemId());
                parentId = closedShipmentPlanItem.getShipmentPlanId();
                shipmentPlanMapper.updateById(new ShipmentPlanDO().setId(parentId).setStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue()));
                ShipmentPlanDO shipmentPlanDO = validateShipmentPlanExists(parentId);
                // 更新订单流状态
                orderLinkApi.updateOrderLinkStatus(shipmentPlanDO.getCode(),BusinessNameDict.SHIPMENT_PLAN_NAME,shipmentPlanDO.getLinkCodeList(),ShippingPlanStatusEnum.CASE_CLOSED.getValue());
            }
            // 恢复销售明细转出运标识
            HashMap<Long, Integer> shippingQuantityMap = new HashMap<>();
            shippingQuantityMap.put(baseItem.getSaleContractItemId(),baseItem.getShippingQuantity());
            saleContractApi.batchUpdateConvertShipmentFlag(shippingQuantityMap);
            return;
        }
        if (Objects.nonNull(closeShipmentPlanReq.getParentId())) {
            validateAntiAuditStatus(closeShipmentPlanReq.getParentId());
            ShipmentPlanDO shipmentPlanDO = validateShipmentPlanExists(closeShipmentPlanReq.getParentId());
            parentId = closeShipmentPlanReq.getParentId();
            shipmentPlanDO.setStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue());
            // 更新订单流状态
            orderLinkApi.updateOrderLinkStatus(shipmentPlanDO.getCode(),BusinessNameDict.SHIPMENT_PLAN_NAME,shipmentPlanDO.getLinkCodeList(),ShippingPlanStatusEnum.CASE_CLOSED.getValue());
            shipmentPlanMapper.updateById(shipmentPlanDO);
            List<ShipmentPlanItem> baseItems = shipmentPlanItemMapper.selectList(ShipmentPlanItem::getShipmentPlanId, closeShipmentPlanReq.getParentId());
            if (CollUtil.isEmpty(baseItems)){
                return;
            }
            Map<Long, Integer> shippingQuantityMap = baseItems.stream().collect(Collectors.toMap(ShipmentPlanItem::getSaleContractItemId, ShipmentPlanItem::getShippingQuantity));
            // 恢复销售明细转出运标识
            saleContractApi.batchUpdateConvertShipmentFlag(shippingQuantityMap);
            ShipmentPlanItem shipmentPlanItem = new ShipmentPlanItem();
            shipmentPlanItem.setStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue());
            shipmentPlanItemMapper.update(shipmentPlanItem, new LambdaQueryWrapperX<ShipmentPlanItem>().eq(ShipmentPlanItem::getShipmentPlanId, closeShipmentPlanReq.getParentId()));
        }
        if (Objects.nonNull(parentId)) {
            ShipmentPlanDO shipmentPlanDO = shipmentPlanMapper.selectById(parentId);
            if (Objects.isNull(shipmentPlanDO)) {
                return;
            }
            Map<String, Object> exts = new HashMap<>();
            exts.put(OPERATOR_EXTS_KEY, shipmentPlanDO.getCode());
            OperateLogCreateReqDTO operateLogCreateReqDTO = new OperateLogCreateReqDTO();
            operateLogCreateReqDTO.setExts(exts);
            operateLogCreateReqDTO.setContent(String.format(SIGN_BACK_OPERATOR_MESSAGE, shipmentPlanDO.getCode(), closeShipmentPlanReq.getRemark()));
            operateLogCreateReqDTO.setUserId(WebFrameworkUtils.getLoginUserId());
            operateLogCreateReqDTO.setModule("管理后台 - 出运计划");
            operateLogCreateReqDTO.setName("结案");
            operateLogCreateReqDTO.setStartTime(LocalDateTime.now());
            operateLogCreateReqDTO.setDuration(0);
            operateLogApi.batchCreateOperateLog(List.of(operateLogCreateReqDTO));
        }
    }

    @Override
    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        ShipmentPlanDO shipmentPlanDO = shipmentPlanMapper.selectById(id);
        if (shipmentPlanDO == null) {
            throw exception(SHIPMENT_PLAN_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (shipmentPlanDO.getForeignTradeCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, shipmentPlanDO.getForeignTradeCompanyId());
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
        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content = null;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, path);
        }

        List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanItemMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanItem>().eq(ShipmentPlanItem::getShipmentPlanId, id));
        List<ShipmentPlanItemExportVO> shipmentPlanItemExportVO = ShipmentPlanConvert.INSTANCE.convertShipmentPlanItemExportVO(shipmentPlanItemList,shipmentPlanDO);
        HashMap<String, Object> params = new HashMap();
        List<CellWriteHandler> handlers = null;
        //params.put("invoiceCode", shipmentPlanDO.getInvoiceCode());
        params.put("destinationPortName", shipmentPlanDO.getDestinationPortName());
        processShipmentPlanItemExportData(shipmentPlanItemExportVO, params);
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "明细.xlsx", shipmentPlanItemExportVO, null, 600, handlers);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    private void processShipmentPlanItemExportData(List<ShipmentPlanItemExportVO> shipmentPlanItemExportVO, Map<String, Object> params) {
        if (CollUtil.isEmpty(shipmentPlanItemExportVO)) {
            return;
        }
        // 4. 汇总数据
        Integer totalShippingQuantity = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getShippingQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        // 根据外箱单位分组求和箱数
        Integer totalBoxCount = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getBoxCount)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        BigDecimal totalNetweight = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getTotalNetweight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGrossWeight = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getTotalGrossweight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSingleNetweight = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getSingleNetweight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume = shipmentPlanItemExportVO.stream()
                .map(ShipmentPlanItemExportVO::getTotalVolume)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 将汇总数据放入params
        params.put("totalShippingQuantity", totalShippingQuantity);
        params.put("totalBoxCount", totalBoxCount);
        params.put("totalNetweight", totalNetweight);
        params.put("totalGrossweight", totalGrossWeight);
        params.put("totalVolume", totalVolume);
        params.put("totalSingleNetweight", totalSingleNetweight);
    }

    @Override
    public List<ShipmentPlanItem> getShipmentPlanItemList(List<Long> ids) {
        return shipmentPlanItemMapper.selectBatchIds(ids);
    }

    @Override
    public List<ShipmentPlanDO> getShipmentPlanList(Collection<Long> ids) {
        return shipmentPlanMapper.selectBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelatedShipmentPlanRespVO getRelatedNum(Long id) {
        ShipmentPlanDO shipmentPlanDO = shipmentPlanMapper.selectById(id);
        if (Objects.isNull(shipmentPlanDO)){
            throw exception(SHIPMENT_PLAN_NOT_EXISTS);
        }
        RelatedShipmentPlanRespVO relatedShipmentPlanRespVO = new RelatedShipmentPlanRespVO();
        List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanItem>().select(ShipmentPlanItem::getSaleContractCode, ShipmentPlanItem::getPurchaseContractCode).eq(ShipmentPlanItem::getShipmentPlanId, id));
        if (CollUtil.isNotEmpty(shipmentPlanItems)){
            long saleContractNum = shipmentPlanItems.stream().map(ShipmentPlanItem::getSaleContractCode).distinct().count();
            long purchaseContractNum = shipmentPlanItems.stream().map(ShipmentPlanItem::getPurchaseContractCode).distinct().count();
            relatedShipmentPlanRespVO.setSaleContractNum(saleContractNum);
            relatedShipmentPlanRespVO.setPurchaseContractNum(purchaseContractNum);
        }
        relatedShipmentPlanRespVO.setShipmentNum(shipmentMapper.selectCount(ShipmentDO::getShipmentPlanCode, shipmentPlanDO.getCode()));
        return relatedShipmentPlanRespVO;
    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        ShipmentPlanDO shipmentPlanDO = validateShipmentPlanExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        shipmentPlanDO.setStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = shipmentPlanMapper.updateById(shipmentPlanDO);
        return i > 0;
    }

    @Override
    public void updateLinkCodeList(Map<String, String> linkCodeMap) {
        if (CollUtil.isEmpty(linkCodeMap)){
            return;
        }
        List<ShipmentPlanDO> shipmentPlanDOS = shipmentPlanMapper.selectList(new LambdaQueryWrapper<ShipmentPlanDO>().select(ShipmentPlanDO::getId,ShipmentPlanDO::getCode, ShipmentPlanDO::getLinkCodeList).in(ShipmentPlanDO::getCode, linkCodeMap.keySet()));
        if (CollUtil.isEmpty(shipmentPlanDOS)){
            return;
        }
        shipmentPlanDOS.forEach(s->{
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isEmpty(linkCodeList)){
                return;
            }
            List<String> newLinkcodeList = new ArrayList<>(linkCodeList);
            String newLinkCode = linkCodeMap.get(s.getCode());
            if (StrUtil.isEmpty(newLinkCode)){
                return;
            }
            newLinkcodeList.add(newLinkCode);
            s.setLinkCodeList(newLinkcodeList);
        });
        shipmentPlanMapper.updateBatch(shipmentPlanDOS);
    }

    /**
     * 校验反审核状态
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        Long count = shipmentPlanMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }
}
