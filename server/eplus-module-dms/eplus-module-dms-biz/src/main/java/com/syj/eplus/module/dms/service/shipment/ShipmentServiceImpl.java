package com.syj.eplus.module.dms.service.shipment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.mapper.ItemBaseMapperUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.ChangeCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.DiffUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.framework.common.util.*;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dict.DictTypeApi;
import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeShareFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.*;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentPageReq;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentSaveReq;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.CloseShipmentPlanReq;
import com.syj.eplus.module.dms.convert.shipment.ShipmentConvert;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentchange.ShipmentChange;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import com.syj.eplus.module.dms.dal.dataobject.temporarysku.TemporarySku;
import com.syj.eplus.module.dms.dal.mysql.declarationitem.DeclarationItemMapper;
import com.syj.eplus.module.dms.dal.mysql.forwarderfee.ForwarderFeeMapper;
import com.syj.eplus.module.dms.dal.mysql.settlementform.SettlementFormMapper;
import com.syj.eplus.module.dms.dal.mysql.shipment.ShipmentMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentchange.ShipmentChangeMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentitem.ShipmentItemMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentplan.ShipmentPlanMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentplanitem.ShipmentPlanItemMapper;
import com.syj.eplus.module.dms.dal.mysql.temporarysku.TemporarySkuMapper;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.service.commodityinspection.CommodityInspectionService;
import com.syj.eplus.module.dms.service.declaration.DeclarationService;
import com.syj.eplus.module.dms.service.forwarderfee.ForwarderFeeService;
import com.syj.eplus.module.dms.service.settlementform.SettlementFormService;
import com.syj.eplus.module.dms.service.shipmentplan.ShipmentPlanService;
import com.syj.eplus.module.dms.util.CalcShipmentUtil;
import com.syj.eplus.module.dms.util.CheckShipmentUtil;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.mms.api.manufacture.IManufactureApi;
import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSaveDTO;
import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSkuItemSaveDTO;
import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSkuSaveDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuBomDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.scm.api.invoicingnotices.InvoicingNoticesApi;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesDTO;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.CompleteOrderReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.InvoiceQuantityDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.purchaseplan.PurchasePlanApi;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.sms.api.AddSubItemApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.*;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.api.stock.dto.*;
import com.syj.eplus.module.wms.api.stockNotice.IStockNoticeApi;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeItemDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticePageReqDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeRespDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeSaveReqDTO;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.INVALID_TO_DECLARATION;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.INVALID_TO_DOCUMENT;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_CONTRACT_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_PLAN_ITEM_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_PLAN_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.SHIPMENT_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.SALE_CONTRACT_STATUS_EMPTY;

/**
 * 出运单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class ShipmentServiceImpl implements ShipmentService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ShipmentMapper shipmentMapper;
    @Resource
    private PackageTypeApi packageTypeApi;

    @Resource
    private DictDataApi dictDataApi;

    @Resource
    private DictTypeApi dictTypeApi;

    @Resource
    private ShipmentItemMapper shipmentItemMapper;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private ShipmentPlanItemMapper shipmentPlanItemMapper;

    @Resource
    private ShipmentPlanMapper shipmentPlanMapper;

    @Resource
    private AddSubItemApi addSubItemApi;

    @Resource
    private TemporarySkuMapper temporarySkuMapper;

    @Resource
    private ForwarderFeeService forwarderFeeService;
    @Resource
    private ForwarderFeeMapper forwarderFeeMapper;

    @Resource
    private CommodityInspectionService commodityInspectionService;

    @Resource
    private DeclarationService declarationService;

    @Resource
    private SettlementFormService settlementFormService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private InvoicingNoticesApi invoicingNoticesApi;

    @Resource
    private RateApi rateApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private PurchasePlanApi purchasePlanApi;

    @Resource
    private ReportApi reportApi;
    @Resource
    private FileApi fileApi;
    @Resource
    @Lazy
    private CustApi custApi;

    @Resource
    @Lazy
    private SkuApi skuApi;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private DeclarationItemMapper declarationItemMapper;

    @Resource
    private ShipmentChangeMapper shipmentChangeMapper;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private FormChangeApi formChangeApi;

    @Resource
    private IStockNoticeApi iStockNoticeApi;

    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    private IStockApi iStockApi;

    @Resource
    private IWarehouseApi warehouseApi;

    @Resource
    private IManufactureApi manufactureApi;

    @Resource
    private CompanyApi companyApi;

    @Resource
    private SettlementFormMapper settlementFormMapper;
    @Resource
    private ThreadPoolTaskExecutor bizThreadPool;

    private static final String SN_TYPE = "shipment";
    private static final String CODE_PREFIX = "CY";
    private static final String CHANGE_SN_TYPE = "shipmentChange";
    private static final String CHANGE_CODE_PREFIX = "CYC";
    public static final String CHANGE_OPERATOR_EXTS_KEY = "changeShipmentCode";
    private static final String PROCESS_DEFINITION_CHANGE_KEY = "scm_shipment_change";
    private static final String VARIABLES_KEY = "deptName";
    private static final String MODEL_KEY = "dms_shipment_change";
    private static final String BUSINESS_MODEL_KEY = "dms_shipment_change_business";

    @Resource
    private ShipmentPlanService shipmentPlanService;

    @Resource
    private OrderLinkApi orderLinkApi;
    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createShipment(ShipmentSaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        //校验发票号，发票号不可重复
        if (StringUtils.isNotEmpty(createReqVO.getInvoiceCode())) {
            createReqVO.setInvoiceCode(createReqVO.getInvoiceCode().trim());
            LambdaQueryWrapper<ShipmentDO> queryWrapperX = new LambdaQueryWrapper<ShipmentDO>().eq(ShipmentDO::getInvoiceCode, createReqVO.getInvoiceCode()).eq(ShipmentDO::getAutoFlag, BooleanEnum.NO.getValue()).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue());
            Long count = shipmentMapper.selectCount(queryWrapperX);
            // 自动生成的判断是否有一个发票号即可
            if (BooleanEnum.YES.getValue().equals(createReqVO.getAutoFlag())) {
                if (count > CalculationDict.ONE) {
                    throw exception(INVOICE_CODE_EXITED);
                }
            } else {
                if (count > CalculationDict.ZERO) {
                    throw exception(INVOICE_CODE_EXITED);
                }
            }
        }
        ShipmentDO shipment = ShipmentConvert.INSTANCE.convertShipmentDO(createReqVO);
        // 生成 出运单 编号
        shipment.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        shipment.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
        if (!ShippingStatusEnum.AWAITING_PROCESSING.getValue().equals(shipment.getStatus())) {
            shipment.setStatus(ShippingStatusEnum.AWAITING_PROCESSING.getValue());
        }
        shipment.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
        // 插入
        shipmentMapper.insert(shipment);
        // 插入出运明细
        List<ShipmentItem> shipmentItemList = createReqVO.getChildren();
        // 赋值加工标识
        List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().collect(Collectors.toList());
        Map<Long, Integer> saleContractItemSplitPurchaseMap = saleContractApi.getSaleContractItemSplitPurchaseMap(saleItemIdList);
        shipmentItemList.forEach(s->{
            Long saleContractItemId = s.getSaleContractItemId();
            if (CollUtil.isNotEmpty(saleContractItemSplitPurchaseMap)){
                s.setProcessFlag(saleContractItemSplitPurchaseMap.get(saleContractItemId));
            }
        });
        List<String> saleCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        // 更新验货状态
        updateShipmentCheckStatus(shipmentItemList);
        // 计算库存成本
        calcStockCost(shipmentItemList);
        List<PurchasePlanItemDTO> planItemList = purchasePlanApi.getPurchasePlanItemListBySaleContractCodeList(saleCodeList);
        if (CollUtil.isNotEmpty(shipmentItemList)) {
            shipmentItemList.forEach(s -> {
                s.setId(null);
                JsonAmount declarationUnitPrice = s.getDeclarationUnitPrice();
                //报关金额=报关数量*报关单价（默认等于销售单价）
                if (!Objects.isNull(declarationUnitPrice)) {
                    BigDecimal amount = declarationUnitPrice.getAmount();
                    Integer quantity = s.getDeclarationQuantity();
                    BigDecimal result = NumUtil.mul(amount, new BigDecimal(quantity));
                    if (!Objects.isNull(s.getDeclarationAmount())) {
                        s.getDeclarationAmount().setAmount(result);
                    } else {
                        JsonAmount jsonAmount = new JsonAmount();
                        jsonAmount.setAmount(result);
                        jsonAmount.setCurrency(declarationUnitPrice.getCurrency());
                        s.setDeclarationAmount(jsonAmount);
                    }
                }
                if (Objects.isNull(s.getPurchaseContractCode())) {
                    s.setThisPurchaseQuantity(BigDecimal.ZERO);
                }
                s.setShipmentId(shipment.getId());
                s.setUniqueCode(IdUtil.fastSimpleUUID());
                if (CollUtil.isNotEmpty(planItemList)) {
                    Optional<PurchasePlanItemDTO> first = planItemList.stream().filter(sa -> Objects.equals(sa.getSaleContractItemId(), s.getSaleContractItemId())).findFirst();
                    first.ifPresent(purchasePlanItemDTO -> s.setPurchaseModel(purchasePlanItemDTO.getPurchaseModel()));
                }
            });
            shipmentItemMapper.insertBatch(shipmentItemList);
        }
        if (!BooleanEnum.YES.getValue().equals(createReqVO.getAutoFlag())) {
            List<String> linkCodeList = shipment.getLinkCodeList();
            // 插入单据流
            if (CollUtil.isNotEmpty(linkCodeList)) {
                List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                        .setBusinessId(shipment.getId())
                        .setCode(shipment.getCode())
                        .setType(OrderLinkTypeEnum.SHIPPING_DETAIL.getValue())
                        .setName(BusinessNameDict.SHIPMENT_NAME)
                        .setLinkCode(s)
                        .setItem(shipmentItemList)
                        .setParentCode(shipment.getShipmentPlanCode())
                        .setStatus(shipment.getStatus())
                        .setCompanyId(shipment.getForeignTradeCompanyId())
                        .setBusinessSubjectName(shipment.getCustName())
                        .setOrderMsg(shipment)).toList();
                orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
            }
        }
        //回写销售合同重新分配状态
        saleContractApi.setReLockFlag(saleItemIdList, false);

        //处理船运费用
        Long shipmentId = shipment.getId();
        updateShipmentForwarderFee(
                new ShipmentForwarderFeeSaveReqVO()
                        .setShipmentId(shipmentId)
                        .setForwarderFeeList(createReqVO.getForwarderFeeList()));

        // 返回
        responses.add(new CreatedResponse(shipmentId, shipment.getCode()));
        return responses;
    }

    /**
     * 计算库存成本
     *
     * @param shipmentItemList 出运单明细列表
     */
    private void calcStockCost(List<ShipmentItem> shipmentItemList) {
        List<String> saleCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        Map<String, Map<String, JsonAmount>> stockCostMap = iStockApi.calcStockCost(saleCodeList);
        if (CollUtil.isNotEmpty(stockCostMap)) {
            shipmentItemList.forEach(s -> {
                if(Objects.isNull(s.getRealLockQuantity())||s.getRealLockQuantity()==0){
                    return;
                }
                String saleContractCode = s.getSaleContractCode();
                String skuCode = s.getSkuCode();
                JsonAmount stockCost = new JsonAmount();
                Map<String, JsonAmount> skuCostMap = stockCostMap.get(saleContractCode);
                if (CollUtil.isEmpty(skuCostMap)) {
                    return;
                }
                stockCost = skuCostMap.get(skuCode);
                if (Objects.isNull(stockCost)) {
                    return;
                }
                s.setStockCost(stockCost);
            });
        }
    }

    /**
     * 更新验货状态
     *
     * @param shipmentItemList 出运单明细列表
     */
    private void updateShipmentCheckStatus(List<ShipmentItem> shipmentItemList) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        Set<Long> saleContractItemIds = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).collect(Collectors.toSet());
        Map<Long, Map<String, Integer>> checkStatusMap = purchaseContractApi.getCheckStatusMap(saleContractItemIds);
        shipmentItemList.forEach(s -> {
            Map<String, Integer> checkStatus = checkStatusMap.get(s.getSaleContractItemId());
            if (CollUtil.isEmpty(checkStatus)) {
                return;
            }
            s.setCheckStatus(checkStatus.get(s.getSkuCode()));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShipment(ShipmentSaveReqVO updateReqVO) {
        Long shipmentId = updateReqVO.getId();
        // 校验存在
        validateShipmentExists(updateReqVO.getId());

        //校验发票号，发票号不可重复
        if (StringUtils.isNotEmpty(updateReqVO.getInvoiceCode())) {
            updateReqVO.setInvoiceCode(updateReqVO.getInvoiceCode().trim());
            LambdaQueryWrapper<ShipmentDO> queryWrapperX = new LambdaQueryWrapper<ShipmentDO>().

                    eq(ShipmentDO::getInvoiceCode, updateReqVO.getInvoiceCode()).ne(ShipmentDO::getId, updateReqVO.getId()).eq(ShipmentDO::getAutoFlag, BooleanEnum.NO.getValue()).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue());
            List<ShipmentDO> shipmentList = shipmentMapper.selectList(queryWrapperX);
            if (!shipmentList.isEmpty()) {
                throw exception(INVOICE_CODE_EXITED);
            }
        }

        // 更新
        ShipmentDO updateObj = ShipmentConvert.INSTANCE.convertShipmentDO(updateReqVO);
        // 计算子项列表

        //CalcShipmentUtil.checkItemCost(updateReqVO, configApi.getConfigMap(), rateApi.getDailyRateMap());
        // 更新出运明细
        List<ShipmentItem> shipmentItemList = updateReqVO.getChildren();
        List<Long> shipmentItemIdList = shipmentItemList.stream().map(ShipmentItem::getId).distinct().toList();
        List<ShipmentItem> baseShipmentItems = shipmentItemMapper.selectList(ShipmentItem::getId, shipmentItemIdList);
        Map<Long, Integer> baseShippingQuantityMap = baseShipmentItems.stream().collect(Collectors.toMap(ShipmentItem::getId, ShipmentItem::getShippingQuantity));
        Map<Long,Integer> updateQuantityMap = new HashMap<>();
        shipmentItemList.forEach(s->{
            Long itemId = s.getId();
            Integer oldShipmentQuantity = baseShippingQuantityMap.get(itemId);
            if (!oldShipmentQuantity.equals(s.getShippingQuantity())){
                Integer updateQuantity = s.getShippingQuantity() - oldShipmentQuantity;
                updateQuantityMap.put(s.getSaleContractItemId(),updateQuantity);
            }
        });
        // 回写销售明细已转出运数量
        saleContractApi.updateShipmentTotalQuantity(updateQuantityMap,false);
//        shipmentItemList.forEach(s -> {
//            s.setDeclarationQuantity(s.getShippingQuantity());
//        });`
        shipmentItemMapper.delete(ShipmentItem::getShipmentId, shipmentId);
        if (CollUtil.isNotEmpty(shipmentItemList)) {
            shipmentItemList.forEach(s -> {
                s.setShipmentId(shipmentId);
                s.setId(null);
            });
            shipmentItemMapper.insertBatch(shipmentItemList);
        }

        List<String> saleContractCodeList = updateReqVO.getChildren().stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        List<AddSubItemDTO> addSubIdList = addSubItemApi.getAddSubItemListByContractCodeListAndStatus(saleContractCodeList, 1);
        List<AddSubItemDTO> addSubItemList = updateReqVO.getAddSubItemList();
        if (CollUtil.isNotEmpty(addSubIdList)) {
            if (CollUtil.isEmpty(addSubItemList)) {
                throw exception(ADD_SUB_CANNOT_DELETE);
            }
        }
        if (CollUtil.isNotEmpty(addSubItemList)) {
            List<Long> idList = addSubItemList.stream().map(AddSubItemDTO::getId).filter(Objects::nonNull).toList();
            //已经认领的数据改删除  status!=null
            if (!idList.isEmpty()) {
                addSubItemApi.deleteBatchByIds(idList);
            }
            addSubItemList.forEach(s -> {
                s.setId(null);
            });
            addSubItemApi.insertBatch(addSubItemList);
        }
        // 插入临时产品
        List<TemporarySku> temporarySkuList = updateReqVO.getTemporarySkuList();
        temporarySkuMapper.delete(TemporarySku::getShipmentId, shipmentId);
        if (CollUtil.isNotEmpty(temporarySkuList)) {
            temporarySkuList.forEach(s -> {
                s.setShipmentId(shipmentId);
                s.setId(null);
            });
            temporarySkuMapper.insertBatch(temporarySkuList);
        }
        shipmentMapper.updateById(updateObj);

        //处理船运费用
        updateShipmentForwarderFee(
                new ShipmentForwarderFeeSaveReqVO()
                        .setShipmentId(shipmentId)
                        .setForwarderFeeList(updateReqVO.getForwarderFeeList()));

        // 校验合计信息
        CheckShipmentUtil.checkItemCost(updateReqVO, rateApi.getDailyRateMap());

//        if (Objects.equals(updateReqVO.getStatus(), ShippingStatusEnum.READY_FOR_SHIPMENT.getValue())) {
//            // 处理公司路径
//            dealCompanyPath(updateReqVO);
//        }
    }


    /**
     * 自动生成出运明细
     *
     * @param updateReqVO 出运单
     */
    private void dealCompanyPath(ShipmentSaveReqVO updateReqVO) {
        //已经自动生成的不再处理
        if (BooleanEnum.YES.getValue().equals(updateReqVO.getAutoFlag())) {
            return;
        }
        List<ShipmentItem> children = updateReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            return;
        }
        List<String> itemSaleCContractCodeList = children.stream().map(ShipmentItem::getSaleContractCode).toList();
        //获取销售合同订单路径
        List<JsonCompanyPath> companyPathList = saleContractApi.getCompanyPathBySaleContractCodeList(itemSaleCContractCodeList);
        if (CollUtil.isEmpty(companyPathList)) {
            throw exception(SALE_CONTRACT_COMPANY_PATH_EMPTY);
        }
        JsonCompanyPath first = companyPathList.get(0);
        // 如果存在不相同路径则抛异常
        // chengbo todo
//        if (!companyPathList.stream().allMatch(element -> Objects.equals(first, element))) {
//            throw exception(SHIPMENT_DIFF_COMPANY_PATH);
//        }
        //从订单路径获取需要生成的下一个公司
        SimpleCompanyDTO simpleCompanyDTO = getSimpleCompanyDTO(first);
        if (Objects.isNull(simpleCompanyDTO)) {
            return;
        }
        //获取对应销售合同信息
        Map<String, SimpleData> contractMap = saleContractApi.getBatchSimpleSaleContractData(itemSaleCContractCodeList, simpleCompanyDTO.getId(), true);
        //获取明细采购偶合同信息
        Map<String, SimpleData> purchaseContractMap = saleContractApi.getBatchSimpleSaleContractData(itemSaleCContractCodeList, simpleCompanyDTO.getId(), false);
        children.forEach(s -> {
            String itemContractCode = s.getSaleContractCode();
            if (CollUtil.isEmpty(contractMap)) {
                return;
            }
            SimpleData simpleData = contractMap.get(itemContractCode);
            if (Objects.isNull(simpleData)) {
                return;
            }
            s.setSourceSaleContractCode(itemContractCode);
            s.setSaleContractCode(simpleData.getCode());
            SimpleData purchaseData = purchaseContractMap.get(s.getSourceUniqueCode());
            if (Objects.isNull(purchaseData)) {
                return;
            }
            s.setPurchaseContractCode(purchaseData.getCode());
        });
        updateReqVO.setForeignTradeCompanyId(simpleCompanyDTO.getId());
        updateReqVO.setForeignTradeCompanyName(simpleCompanyDTO.getCompanyName());
        //重置ID跟编号
        updateReqVO.setId(null);
        updateReqVO.setSourceCode(updateReqVO.getCode());
        updateReqVO.setCode(null);
        updateReqVO.setAutoFlag(BooleanEnum.YES.getValue());
        updateReqVO.setStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        createShipment(updateReqVO);
    }

    /**
     * 从订单路径获取需要生成的下一个公司
     *
     * @param companyPath 公司路径
     * @return 公司信息
     */
    private SimpleCompanyDTO getSimpleCompanyDTO(JsonCompanyPath companyPath) {
        //获取公司信息
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        Long companyId = companyPath.getId();
        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
        if (Objects.isNull(simpleCompanyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        return getForeignTradeCompanyId(companyPath, simpleCompanyDTOMap, null);
    }

    private SimpleCompanyDTO getForeignTradeCompanyId(JsonCompanyPath companyPath, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap, SimpleCompanyDTO companyDTO) {
        Long companyId = companyPath.getId();
        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
        // 内部企业则继续递归
        if (CompanyNatureEnum.INTERNAL_CUST.getValue().equals(simpleCompanyDTO.getCompanyNature())) {
            JsonCompanyPath next = companyPath.getNext();
            if (Objects.isNull(next)) {
                return companyDTO;
            }
            companyDTO = simpleCompanyDTOMap.get(next.getId());
            return getForeignTradeCompanyId(next, simpleCompanyDTOMap, companyDTO);
        }
        return companyDTO;
    }

    @Override
    public void deleteShipment(Long id) {
        // 校验存在
        validateShipmentExists(id);
        // 删除
        shipmentMapper.deleteById(id);
        // 删除销售明细
        shipmentItemMapper.delete(ShipmentItem::getShipmentId, id);
        // 删除临时产品
        temporarySkuMapper.delete(TemporarySku::getShipmentId, id);
    }

    private ShipmentDO validateShipmentExists(Long id) {
        ShipmentDO shipmentDO = shipmentMapper.selectById(id);
        if (Objects.isNull(shipmentDO)) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        return shipmentDO;
    }

    @Override
    public ShipmentRespVO getShipment(Long id) {
        ShipmentDO shipmentDO = shipmentMapper.selectById(id);
        if (shipmentDO == null) {
            return null;
        }
        ShipmentRespVO shipmentRespVO = ShipmentConvert.INSTANCE.convertShipmentRespVO(shipmentDO);
        Long shipmentId = shipmentRespVO.getId();
        //获取出运明细明细
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
        //处理出运单明细
        if (CollUtil.isNotEmpty(shipmentItemList)) {
            // 获取销售单价
            List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
//            Map<Long, JsonAmount> saleContractItemAmountMap = saleContractApi.getSaleContractItemAmountMap(saleItemIdList);
            List<String> purchaseContractCodeList = shipmentItemList.stream().filter(s->PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())).map(ShipmentItem::getPurchaseContractCode).filter(Objects::nonNull).distinct().toList();
            Map<Long, JsonAmount> purchasePricCache = purchaseContractApi.getiPurchasePricCache(purchaseContractCodeList);
            List<String> saleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
            Map<String, List<Long>> companyIdListBySaleContractCodeList = saleContractApi.getCompanyIdListBySaleContractCodeList(saleContractCodeList);
            List<Long> skuIdList = shipmentItemList.stream().map(ShipmentItem::getSkuId).distinct().toList();
            Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
            List<String> saleCodes = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
            Map<String, SmsContractAllDTO> saleMap = saleContractApi.getSmsByCodeList(saleCodes);
            Set<Long> saleItemIdSet = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).collect(Collectors.toSet());
            Map<Long, JsonAmount> realPurchasePriceMap = saleContractApi.getRealPurchasePriceMapByItemIds(saleItemIdSet);
            shipmentItemList.forEach(s -> {
                if (CollUtil.isNotEmpty(realPurchasePriceMap)&&PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())){
                    s.setPurchaseWithTaxPrice(realPurchasePriceMap.get(s.getSaleContractItemId()));
                }
                if (CollUtil.isNotEmpty(purchasePricCache)&&PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())){
                    s.setPurchaseWithTaxPrice(purchasePricCache.get(s.getSaleContractItemId()));
                }
                // JsonAmount amount = saleContractItemAmountMap.get(s.getSaleContractItemId());
                if (Objects.nonNull(s.getSaleUnitPrice().getAmount())){
                    //s.setSaleUnitPrice(amount);
                    s.setSaleAmount(new JsonAmount().setAmount(NumUtil.mul(s.getSaleUnitPrice().getAmount(),s.getShippingQuantity())).setCurrency(s.getSaleUnitPrice().getCurrency()));
                }
//                if (StrUtil.isNotEmpty(s.getPurchaseContractCode())&&invoicedPurchaseCodeSet.contains(s.getPurchaseContractCode())){
//                    s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
//                }
                if (CollUtil.isNotEmpty(saleMap)) {
                    SmsContractAllDTO smsContractAllDTO = saleMap.get(s.getSaleContractCode());
                    if (Objects.nonNull(smsContractAllDTO)) {
                        s.setCompanyPath(smsContractAllDTO.getCompanyPath());
                    }
                }
                String saleContractCode = s.getSaleContractCode();
                if (StrUtil.isEmpty(saleContractCode)) {
                    return;
                }
                List<Long> companyIdList = companyIdListBySaleContractCodeList.get(saleContractCode);
                if (CollUtil.isNotEmpty(companyIdList)) {
                    s.setCompanyIdList(companyIdList);
                }
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    Boolean aBoolean = simpleSkuDTOMap.get(s.getSkuId());
                    if (Objects.isNull(aBoolean)) {
                        aBoolean = false;
                    }
                    s.setSkuDeletedFlag(aBoolean ? 0 : 1);
                }
            });
        }
        shipmentRespVO.setChildren(shipmentItemList);
        // 获取加减项
        List<String> saleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).filter(Objects::nonNull).toList();
        shipmentRespVO.setAddSubItemList(addSubItemApi.getAddSubItemListByContractCodeList(saleContractCodeList));
        // 获取临时产品
        shipmentRespVO.setTemporarySkuList(temporarySkuMapper.selectList(TemporarySku::getShipmentId, shipmentDO.getId()));
        //获取船代费用
        shipmentRespVO.setForwarderFeeList(forwarderFeeService.getListByShipmentId(id));

        // 为每个出运明细项设置对应的合同详情信息
        fillShipmentItemContractDetails(shipmentItemList, saleContractCodeList);

        if (CollUtil.isNotEmpty(shipmentItemList)) {
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            // 客户PO
            String custPo = shipmentItemList.stream().map(ShipmentItem::getCustPo).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentRespVO.setCustPo(custPo);
            // 客户名称
            String custName = shipmentItemList.stream().map(ShipmentItem::getCustName).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentRespVO.setCustName(custName);
            // 销售合同号
            String saleContractCode = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentRespVO.setSaleContractCode(saleContractCode);
            // 业务员
            String managerName = shipmentItemList.stream().map(ShipmentItem::getSales).filter(Objects::nonNull).map(UserDept::getNickname).distinct().collect(Collectors.joining(CommonDict.COMMA));
            shipmentRespVO.setManagerName(managerName);
        }
        return shipmentRespVO;
    }

    @Override
    public PageResult<ShipmentRespVO> getShipmentPage(ShipmentPageReqVO pageReqVO) {
        PageResult<ShipmentRespVO> result = new PageResult<>();
        //查询上单据变更数量
        Map<String, Object> summary = new HashMap<>();
        Long count = shipmentMapper.selectCount(new LambdaQueryWrapperX<ShipmentDO>()
                .eq(ShipmentDO::getConfirmFlag, BooleanEnum.NO.getValue()));
        summary.put("changeCount", count);
        result.setSummary(summary);
        if (pageReqVO.getConfirmFlag() == null && pageReqVO.getIdList() == null) {
            pageReqVO.setConfirmFlag(1);
        }
        if ((pageReqVO.getStatusList() == null || pageReqVO.getStatusList().size() == 0) && pageReqVO.getStatus() == null) {
            pageReqVO.setNeStatus(ShippingStatusEnum.CASE_CLOSED.getValue());
        }
        if (StrUtil.isNotEmpty(pageReqVO.getCustPo())||StrUtil.isNotEmpty(pageReqVO.getCustName())||StrUtil.isNotEmpty(pageReqVO.getSaleContractCode()) || StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode()) || StrUtil.isNotEmpty(pageReqVO.getCskuCode()) || StrUtil.isNotEmpty(pageReqVO.getSkuCode()) || StrUtil.isNotEmpty(pageReqVO.getPurchaseContractCode()) || StrUtil.isNotEmpty(pageReqVO.getVenderCode())) {
            LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>()
                    .likeIfPresent(ShipmentItem::getCskuCode, pageReqVO.getCskuCode())
                    .likeIfPresent(ShipmentItem::getBasicSkuCode, pageReqVO.getBasicSkuCode())
                    .likeIfPresent(ShipmentItem::getSkuCode, pageReqVO.getSkuCode())
                    .likeIfPresent(ShipmentItem::getCustPo, pageReqVO.getCustPo())
                    .likeIfPresent(ShipmentItem::getSaleContractCode, pageReqVO.getSaleContractCode())
                    .likeIfPresent(ShipmentItem::getCustName, pageReqVO.getCustName())
                    .likeIfPresent(ShipmentItem::getPurchaseContractCode, pageReqVO.getPurchaseContractCode())
                    .eqIfPresent(ShipmentItem::getVenderCode, pageReqVO.getVenderCode());
            List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(queryWrapperX);
            if (CollUtil.isEmpty(shipmentItemList)) {
                return result;
            }
            pageReqVO.setIdList(shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList());
        }
        PageResult<ShipmentDO> shipmentDOPageResult = shipmentMapper.selectPage(pageReqVO);
        List<ShipmentDO> shipmentDOList = shipmentDOPageResult.getList();
        if (CollUtil.isEmpty(shipmentDOList)) {
            return result;
        }
        List<Long> shipmentIdList = shipmentDOList.stream().map(ShipmentDO::getId).distinct().toList();
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getShipmentId, shipmentIdList);
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return result;
        }
        List<String> purchaseContractCodeList = shipmentItemList.stream().map(ShipmentItem::getPurchaseContractCode).filter(Objects::nonNull).distinct().toList();
//        Set<String> invoicedPurchaseCodeSet = purchaseContractApi.filterInvoicedPurchaseContractCode(purchaseContractCodeList);
        List<String> saleCodes = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        Map<String, SmsContractAllDTO> saleMap = saleContractApi.getSmsByCodeList(saleCodes);
        // 获取销售单价
        List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        Map<Long, JsonAmount> saleContractItemAmountMap = saleContractApi.getSaleContractItemAmountMap(saleItemIdList);
        Map<Long, JsonAmount> realPurchasePriceMap = saleContractApi.getRealPurchasePriceMapByItemIds(saleItemIdList);
        Set<String> purchaseCodeList = shipmentItemList.stream().filter(s -> PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())).map(ShipmentItem::getPurchaseContractCode).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, JsonAmount> purchasePricCache = purchaseContractApi.getiPurchasePricCache(purchaseCodeList);
        shipmentItemList.forEach(s -> {
            if (CollUtil.isNotEmpty(realPurchasePriceMap)&&PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())){
                s.setPurchaseWithTaxPrice(realPurchasePriceMap.get(s.getSaleContractItemId()));
            }
            if (CollUtil.isNotEmpty(purchasePricCache)&&PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())){
                s.setPurchaseWithTaxPrice(purchasePricCache.get(s.getSaleContractItemId()));
            }
            if (CollUtil.isNotEmpty(saleMap)) {
                SmsContractAllDTO smsContractAllDTO = saleMap.get(s.getSaleContractCode());
                if (Objects.nonNull(smsContractAllDTO)) {
                    s.setCompanyPath(smsContractAllDTO.getCompanyPath());
                }
            }
            JsonAmount amount = saleContractItemAmountMap.get(s.getSaleContractItemId());
            if (Objects.isNull(amount)||Objects.isNull(amount.getAmount())){
                return;
            }
            s.setSaleUnitPrice(amount);
            s.setSaleAmount(new JsonAmount().setAmount(NumUtil.mul(amount.getAmount(),s.getShippingQuantity())).setCurrency(amount.getCurrency()));
        });
        
        // 为每个出运明细项设置对应的合同详情信息（用于导出时按采购合同号拆分）
        List<String> saleContractCodeListForContract = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).filter(Objects::nonNull).distinct().toList();
        fillShipmentItemContractDetails(shipmentItemList, saleContractCodeListForContract);
        
        Map<Long, List<ShipmentItem>> shipmentItemMap = shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        List<String> saleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        Map<String, List<Long>> saleCompanyMap = saleContractApi.getCompanyIdListBySaleContractCodeList(saleContractCodeList);
        List<ShipmentRespVO> saleContractRespVOList = ShipmentConvert.INSTANCE.convertShipmentItemRespVO(shipmentDOList, shipmentItemMap, packageTypeList, saleCompanyMap);
        
        // 计算报关金额和总采购金额的合计（按币种分组）
        // 1. 报关金额合计
        Map<String, List<ShipmentItem>> declarationAmountGrouped = shipmentItemList.stream()
                .filter(item -> Objects.nonNull(item.getDeclarationAmount())
                        && Objects.nonNull(item.getDeclarationAmount().getAmount())
                        && StrUtil.isNotEmpty(item.getDeclarationAmount().getCurrency()))
                .collect(Collectors.groupingBy(item -> item.getDeclarationAmount().getCurrency()));
        if (CollUtil.isNotEmpty(declarationAmountGrouped)) {
            List<JsonAmount> declarationAmountList = declarationAmountGrouped.entrySet().stream().map(entry -> {
                BigDecimal sum = entry.getValue().stream()
                        .map(ShipmentItem::getDeclarationAmount)
                        .map(JsonAmount::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP);
                return new JsonAmount().setAmount(sum).setCurrency(entry.getKey());
            }).collect(Collectors.toList());
            summary.put("declarationAmount", declarationAmountList);
        }
        
        // 2. 总采购金额合计（需要动态计算：采购含税单价 * 本次采购数量）
        Map<String, List<ShipmentItem>> purchaseTotalAmountGrouped = shipmentItemList.stream()
                .filter(item -> Objects.nonNull(item.getPurchaseWithTaxPrice())
                        && Objects.nonNull(item.getPurchaseWithTaxPrice().getAmount())
                        && Objects.nonNull(item.getThisPurchaseQuantity())
                        && StrUtil.isNotEmpty(item.getPurchaseWithTaxPrice().getCurrency()))
                .collect(Collectors.groupingBy(item -> item.getPurchaseWithTaxPrice().getCurrency()));
        if (CollUtil.isNotEmpty(purchaseTotalAmountGrouped)) {
            List<JsonAmount> purchaseTotalAmountList = purchaseTotalAmountGrouped.entrySet().stream().map(entry -> {
                BigDecimal sum = entry.getValue().stream()
                        .map(item -> {
                            BigDecimal unitPrice = item.getPurchaseWithTaxPrice().getAmount();
                            return unitPrice.multiply( item.getThisPurchaseQuantity());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP);
                return new JsonAmount().setAmount(sum).setCurrency(entry.getKey());
            }).collect(Collectors.toList());
            summary.put("purchaseTotalAmount", purchaseTotalAmountList);
        }
        
        return result.setTotal(shipmentDOPageResult.getTotal()).setList(saleContractRespVOList);
    }

    @Override
    public List<Long> getShipmentIdsByCustCode(String custCode) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.getShipmentItemListByCustCode(custCode);
        if (shipmentItemList == null) {
            return null;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        return shipmentIds;
    }

    @Override
    public List<Long> getShipmentIdsByVenderCode(String venderCode) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.getShipmentItemListByVenderCode(venderCode);
        if (shipmentItemList == null) {
            return null;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        return shipmentIds;
    }

    @Override
    public List<Long> getShipmentIdsBySkuCode(String skuCode) {
        // 一级产品
        List<String> skuCodeList = new ArrayList<>(Collections.singletonList(skuCode));
        // 二级产品
        List<SkuBomDTO> secondSkuList = skuApi.getSonSkuListBySkuCode(skuCode);
        if (!CollUtil.isEmpty(secondSkuList)) {
            secondSkuList.stream().forEach(it -> {
                skuCodeList.add(it.getSkuCode());
            });
        }
        // 三级产品
        if (!CollUtil.isEmpty(secondSkuList)) {
            secondSkuList.stream().forEach(it -> {
                List<SkuBomDTO> thirdSkuList = skuApi.getSonSkuListBySkuCode(it.getSkuCode());
                if (CollUtil.isNotEmpty(thirdSkuList)) {
                    thirdSkuList.stream().forEach(x -> {
                        skuCodeList.add(x.getSkuCode());
                    });
                }
            });
        }
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.getShipmentItemListBySkuCodeList(skuCodeList);
        if (shipmentItemList == null) {
            return null;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        return shipmentIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> transformShipment(List<Long> ids) {
        List<CreatedResponse> responses = new ArrayList<>();
        // 根据ids将出运计划明细都查出来
        List<ShipmentPlanItem> oldShipmentPlanItemList = shipmentPlanService.getShipmentPlanItemList(ids);
        // 根据出运计划单分组
        if (CollUtil.isEmpty(oldShipmentPlanItemList)) {
            return responses;
        }

        //出运计划明细状态改为已完成
        oldShipmentPlanItemList.forEach(s -> {
            s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
        });
        //更新明细状态更新为已完成
        shipmentPlanItemMapper.updateBatch(oldShipmentPlanItemList);
        // 过滤出运数为0的数据
        List<ShipmentPlanItem> shipmentPlanItemList = oldShipmentPlanItemList.stream().filter(s->Objects.isNull(s.getShippingQuantity())||s.getShippingQuantity()>0).toList();
        if (CollUtil.isEmpty(shipmentPlanItemList)){
            return responses;
        }
        Map<Long, List<ShipmentPlanItem>> shipmentPlanMap = shipmentPlanItemList.stream().collect(Collectors.groupingBy(ShipmentPlanItem::getShipmentPlanId));
        // 批量查出出运计划单单据信息
        Set<Long> shipmentPlanidSet = shipmentPlanMap.keySet();
        List<ShipmentPlanDO> shipmentPlanList = shipmentPlanService.getShipmentPlanList(shipmentPlanidSet);
        if (CollUtil.isEmpty(shipmentPlanList)) {
            return responses;
        }

        shipmentPlanList.forEach(s -> {
            //所有出运计划明细状态为已完成将出运计划状态修改为已完成
            List<Integer> shipmentItemStatusList = shipmentPlanMapper.getShipmentPlanItemStatus(s.getId());
            if (CollUtil.isEmpty(shipmentItemStatusList)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否出运计划明细全部完成
            boolean allCompleted = shipmentItemStatusList.stream()
                    .allMatch(status -> status.equals(ShippingPlanStatusEnum.COMPLETED.getValue()));
            if (allCompleted) {
                s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
            }
        });
        shipmentPlanMapper.updateBatch(shipmentPlanList);

        Map<Long, ShipmentPlanDO> shipmentPlanDOMap =
                shipmentPlanList.stream().collect(Collectors.toMap(ShipmentPlanDO::getId, s -> s));
        List<ShipmentSaveReqVO> createList = new ArrayList<>();


        List<String> skuCodeList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSkuCode).distinct().toList();
        Map<String, String> skuMap = skuApi.getHsDataUnitBySkuCodes(skuCodeList);
        List<DictDataRespDTO> dictDataList = dictTypeApi.getHsDataUnitList();
        if (CollUtil.isEmpty(dictDataList)) {
            throw exception(HSDATA_UNIT_NOT_EXISTS);
        }
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        shipmentPlanMap.entrySet().forEach(s -> {
            Long shipmentPlanId = s.getKey();
            List<ShipmentPlanItem> shipmentPlanItems = s.getValue();
            ShipmentPlanDO shipmentPlanDO = shipmentPlanDOMap.get(shipmentPlanId);
            if (Objects.isNull(shipmentPlanDO)) {
                return;
            }
            ShipmentSaveReqVO shipmentSaveReqVO = ShipmentConvert.INSTANCE.convertShipmentSaveReqVO(shipmentPlanDO);
            //设置公司主体为外贸公司
            SaleContractSaveDTO saleContractInfoByCode = saleContractApi.getSaleContractInfoByCode(shipmentPlanDO.getSaleContractCode());
            if (Objects.nonNull(saleContractInfoByCode)) {
                JsonCompanyPath companyPath = saleContractInfoByCode.getCompanyPath();
                while (companyPath != null) {
                    SimpleCompanyDTO simpleCompanyDTO = simpleCompanyMap.get(companyPath.getId());
                    if (Objects.nonNull(simpleCompanyDTO) && Objects.equals(simpleCompanyDTO.getCompanyNature(), CompanyNatureEnum.FOREIGN_TRADE.getValue())) {
                        shipmentSaveReqVO.setForeignTradeCompanyId(simpleCompanyDTO.getId()).setForeignTradeCompanyName(simpleCompanyDTO.getName());
                        break;
                    }
                    companyPath = companyPath.getNext();
                }
            }

            List<ShipmentItem> shipmentItemList = ShipmentConvert.INSTANCE.convertShipmentItemList(shipmentPlanItems);
            List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
            List<SaleContractItemSaveDTO> SaleContractItemSaveDTOList = saleContractApi.listSaleContractItem(saleItemIdList);
            Map<Long, Integer> billStatusMap = SaleContractItemSaveDTOList.stream().collect(Collectors.toMap(SaleContractItemSaveDTO::getId, SaleContractItemSaveDTO::getBillStatus, (s1, s2) -> s2));
            Map<Long, Integer> quantityMap = purchaseContractApi.getPurchaseQuantityBySaleContractItemIds(saleItemIdList);
//            计算允许开票数量
            Map<Long, Integer> invoicedQuantityMap = CalculateQuantity(shipmentItemList);
            shipmentItemList.forEach(si -> {
                if (CollUtil.isNotEmpty(skuMap)) {
                    String s1 = skuMap.get(si.getSkuCode());
                    if (StrUtil.isNotEmpty(s1)) {
                        Optional<DictDataRespDTO> first = dictDataList.stream().filter(d -> Objects.equals(d.getLabel(), s1)).findFirst();
                        first.ifPresent(dictDataRespDTO -> si.setHsMeasureUnit(dictDataRespDTO.getValue()));
                    }
                }
                if (Objects.nonNull(quantityMap)) {
                    Integer quantity = quantityMap.get(si.getSaleContractItemId());
                    if (Objects.isNull(quantity)) {
                        quantity = 0;
                    }
                    si.setPurchaseTotalQuantity(quantity);
                }
                if (CollUtil.isNotEmpty(invoicedQuantityMap)) {
                    si.setThisPurchaseQuantity(BigDecimal.valueOf(invoicedQuantityMap.get(si.getSaleContractItemId())));
                }
                if (CollUtil.isNotEmpty(billStatusMap)) {
                    si.setBillStatus(billStatusMap.get(si.getSaleContractItemId()));
                }
                si.setSourceSaleContractCode(si.getSaleContractCode());
                si.setHsMeasureUnit(HsMeasureUnitEnum.GE.getValue());
                si.setTaxRefundRate(BigDecimal.valueOf(13));
            });
            UserDept inputUser = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
            shipmentSaveReqVO.setInputUser(inputUser);
            shipmentSaveReqVO.setInputDate(LocalDateTime.now());
            shipmentSaveReqVO.setChildren(shipmentItemList);
            createList.add(shipmentSaveReqVO);
        });
        if (CollUtil.isNotEmpty(createList)) {
            createList.forEach(s -> {
                List<CreatedResponse> res = createShipment(s);
                if (CollUtil.isNotEmpty(res) && res.size() > 0) {
                    responses.add(res.get(0));
                }
            });
        }
//        销售合同回写已转出运明细数量
        List<ShipmentItem> shipmentItemList = createList.stream().filter(s -> s.getChildren() != null).flatMap(s -> s.getChildren().stream()).toList();
        saleContractApi.batchUpdateShipmentTotalQuantity(BeanUtils.toBean(shipmentItemList, ShipmentQuantityDTO.class));

        return responses;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> toCommodityInspection(ToInspectionReqVO reqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<Long> ids = reqVO.getIds();
        if (CollUtil.isEmpty(ids)) {
            return responses;
        }
        // 根据出运单id查找对应的出运单明细map
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return responses;
        }
        //存在已转商检单记录，不可转单
        List<ShipmentItem> isToCommodityInspectionYesList = shipmentItemList.stream().filter(drl -> {
            return drl.getIsToCommodityInspection().equals(BooleanEnum.YES.getValue());
        }).toList();
        if (!isToCommodityInspectionYesList.isEmpty()) {
            throw exception(INVALID_TO_DOCUMENT);
        }
        List<ShipmentItem> commodityInspectionFlagYesList = shipmentItemList.stream().filter(drl -> {
            return drl.getCommodityInspectionFlag().equals(BooleanEnum.NO.getValue());
        }).toList();
        if (CollUtil.isNotEmpty(commodityInspectionFlagYesList)) {
            throw exception(FAIL_TO_COMMODITY_INSPECTION);
        }
        List<CommodityInspectionSaveReqVO> createList = new ArrayList<>();
        Map<Long, List<ShipmentItem>> shipmentMap = shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));

        shipmentMap.forEach((shipmentId, itemList) -> {
            ShipmentDO shipmentDO = shipmentMapper.selectById(shipmentId);
            List<CommodityInspectionItem> ownList = new ArrayList<>();
            List<CommodityInspectionItem> venderList = new ArrayList<>();
            itemList.forEach(si -> {
                //是否转商检单设置为是
                si.setIsToCommodityInspection(BooleanEnum.YES.getValue());
                //转商检单时间设置为当前时间
                si.setCommodityInspectionDate(LocalDateTime.now());
                //转商检单人设置为当前登录人
                Long loginUserId = WebFrameworkUtils.getLoginUserId();
                UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
                si.setCommodityInspectionUser(userDept);
                if (BooleanEnum.YES.getValue().equals(si.getCommodityInspectionFlag())) {
                    CommodityInspectionItem commodityInspectionItem = ShipmentConvert.INSTANCE.convertCommodityInspectionItem(si);
                    //--自主商检
                    if (CommodityInspectionTypeEnum.OWNER.getValue().equals(si.getCommodityInspectionType())) {
                        ownList.add(commodityInspectionItem);
                    }
                    //供应商商检
                    if (CommodityInspectionTypeEnum.VENDER.getValue().equals(si.getCommodityInspectionType())) {
                        if (!commodityInspectionItem.getVenderCode().isEmpty() && !commodityInspectionItem.getVenderCode().isBlank()) {
                            venderList.add(commodityInspectionItem);
                        }
                    }
                }

            });
            shipmentItemMapper.updateBatch(itemList);
            //自主商检的产品生成一条商检记录
            if (!CollUtil.isEmpty(ownList)) {
                CommodityInspectionSaveReqVO commodityInspectionSaveReqVO = ShipmentConvert.INSTANCE.convertCommodityInspectionSaveReqVO(shipmentDO);
                commodityInspectionSaveReqVO.setChildren(ownList);
                commodityInspectionSaveReqVO.setShipmentCode(shipmentDO.getCode());
                commodityInspectionSaveReqVO.setInputDate(LocalDateTime.now());
                createList.add(commodityInspectionSaveReqVO);
                // 创建订单链路
            }
            //供应商商检的产品根据供应商生成一个或者多个商检记录
            if (!CollUtil.isEmpty(venderList)) {
                Map<String, List<CommodityInspectionItem>> venderListMap = venderList.stream().collect(Collectors.groupingBy(CommodityInspectionItem::getVenderCode));
                venderListMap.entrySet().forEach(sv -> {
                    CommodityInspectionSaveReqVO commodityInspectionSaveReqVO = ShipmentConvert.INSTANCE.convertCommodityInspectionSaveReqVO(shipmentDO);
                    commodityInspectionSaveReqVO.setChildren(sv.getValue());
                    commodityInspectionSaveReqVO.setShipmentCode(shipmentDO.getCode());
                    commodityInspectionSaveReqVO.setInputDate(LocalDateTime.now());
                    createList.add(commodityInspectionSaveReqVO);
                });
            }
        });
        if (CollUtil.isNotEmpty(createList)) {
            createList.forEach(it -> {
                CreatedResponse commodityInspection = commodityInspectionService.createCommodityInspection(it);
                responses.add(commodityInspection);
            });
        }
        return responses;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> toDeclaration(ToDeclarationReqVO toDeclarationReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<DeclarationReq> declarationReqList = toDeclarationReqVO.getDeclarationReqList();
        if (CollUtil.isEmpty(declarationReqList)) {
            return responses;
        }
        List<Long> idList = declarationReqList.stream().map(DeclarationReq::getId).toList();
        Map<Long, Integer> declarationMap = declarationReqList.stream().collect(Collectors.groupingBy(DeclarationReq::getId, Collectors.summingInt(DeclarationReq::getDeclarationQuantityCurrent)));
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.getShipmentItemListByIdList(idList);
        if (CollUtil.isEmpty(shipmentItemList)){
            return responses;
        }
        
        // Validate invoice status and product name consistency
        validateInvoiceStatusAndProductName(shipmentItemList);
        
        Set<Long> shipmentIdSet = shipmentItemList.stream().map(ShipmentItem::getShipmentId).collect(Collectors.toSet());
        List<ShipmentDO> shipmentList = shipmentMapper.selectBatchIds(shipmentIdSet);
        if (CollUtil.isEmpty(shipmentList)){
            return responses;
        }
        // 只有选中的数据状态都在执行中情况下，才可以批量转
//        Optional<String> notReadyOpt = shipmentList.stream().filter(s -> !ShippingStatusEnum.READY_FOR_SHIPMENT.getValue().equals(s.getStatus())).map(ShipmentDO::getCode).findFirst();
//        notReadyOpt.ifPresent(s->{
//            throw exception(ONLY_SUPPORTS_BATCH_OPERATION_EXECUTION_DATA,s);
//        });
        Map<Long, ShipmentDO> shipmentDOMap = shipmentList.stream().collect(Collectors.toMap(ShipmentDO::getId, s -> s));
        long companyIdCount = shipmentList.stream().map(ShipmentDO::getForeignTradeCompanyId).distinct().count();
        if (companyIdCount > 1){
            throw exception(SHIPMENT_NOT_ALLOW_EXPORT_MULTI_COMPANY);
        }
        List<ShipmentItem> updateShipmentItemList = new ArrayList<>();
        // 当前登录人
        UserDept loginUser = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
        // 当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        shipmentItemList.forEach(si -> {
            //本次报关数量
            Integer declarationQuantityCurrent = declarationMap.get(si.getId());
            if (Objects.isNull(declarationQuantityCurrent)){
                return;
            }
            //报关数量
            Integer declarationQuantity = si.getDeclarationQuantity();
            Integer declarationQuantityOldAll = si.getDeclarationQuantityOld() + declarationQuantityCurrent;
            if (declarationQuantityOldAll > declarationQuantity) {
                throw exception(INVALID_TO_DECLARATION);
            }
            si.setDeclarationQuantityCurrent(declarationQuantityCurrent);
            si.setDeclarationQuantityOld(declarationQuantityOldAll);
            if (si.getShippingQuantity().equals(declarationQuantityOldAll)){
                si.setIsToDeclaration(ToDeclarationEnum.TRANSFERRED.getStatus());
            }else {
                si.setIsToDeclaration(ToDeclarationEnum.PARTIAL_CONVERSION.getStatus());
            }
            //转结报关单时间设置为当前时间
            si.setDeclarationDate(nowTime);
            //转报关单人设置为当前登录人
            si.setDeclarationUser(loginUser);
            updateShipmentItemList.add(si);
        });
        shipmentItemMapper.updateBatch(updateShipmentItemList);
        ShipmentDO shipmentDO = shipmentList.get(0);
        DeclarationSaveReqVO declarationSaveReqVO = ShipmentConvert.INSTANCE.convertDeclarationSaveReqVO(shipmentDO);
        String invoiceCode = shipmentList.stream().map(ShipmentDO::getInvoiceCode).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA));
        String saleContractCode = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA));
        String custPo = shipmentItemList.stream().map(ShipmentItem::getCustPo).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA));
        declarationSaveReqVO.setProtocolCode(invoiceCode);
        declarationSaveReqVO.setSaleContractCode(saleContractCode);
        declarationSaveReqVO.setCustPo(custPo);
        List<DeclarationItem> declarationItem = ShipmentConvert.INSTANCE.convertDeclarationItemList(shipmentItemList);
        declarationSaveReqVO.setChildren(declarationItem);
        validateDeclarationFlag(shipmentList.stream().map(ShipmentDO::getCode).distinct().collect(Collectors.toList()));
        CreatedResponse declaration = declarationService.createDeclaration(declarationSaveReqVO);
        responses.add(declaration);
        return responses;
    }

    @Override
    public void validateDeclarationFlag(List<String> shipmentCodeList){
        if (CollUtil.isEmpty(shipmentCodeList)){
            return;
        }
        List<ShipmentDO> shipmentDOList = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().select(ShipmentDO::getId).in(ShipmentDO::getCode, shipmentCodeList).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue()));
        if (CollUtil.isEmpty(shipmentDOList)){
            return;
        }
        List<Long> shipmentIdList = shipmentDOList.stream().map(ShipmentDO::getId).distinct().toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getShipmentId, ShipmentItem::getDeclarationQuantityOld, ShipmentItem::getShippingQuantity).in(ShipmentItem::getShipmentId, shipmentIdList));
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        Map<Long, List<ShipmentItem>> shipmentItemMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        List<ShipmentDO> updateList = new ArrayList<>();
        shipmentItemMap.forEach((shipmentId, itemList) -> {
            if (CollUtil.isEmpty(itemList)){
                return;
            }
            boolean transformFlag = itemList.stream().allMatch(s -> Objects.equals(s.getDeclarationQuantityOld(), s.getShippingQuantity()));
            if (transformFlag){
                updateList.add(new ShipmentDO().setId(shipmentId).setDeclarationFlag(BooleanEnum.YES.getValue()));
            }else {
                updateList.add(new ShipmentDO().setId(shipmentId).setDeclarationFlag(BooleanEnum.NO.getValue()));
            }
        });
        if (CollUtil.isNotEmpty(updateList)){
            shipmentMapper.updateBatch(updateList);
        }
    }

    /**
     * Validate invoice status and product name consistency for declaration
     * Check if shipment items have been invoiced and if the declaration name matches the invoice name
     * This validation checks both:
     * 1. Invoice notifications created from shipment items (sourceUniqueCode)
     * 2. Invoice notifications created from purchase contracts (purchaseContractItemId)
     *
     * @param shipmentItemList shipment item list
     */
    private void validateInvoiceStatusAndProductName(List<ShipmentItem> shipmentItemList) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }

        // Get unique codes from shipment items for shipment-based invoice notifications
        List<String> uniqueCodes = shipmentItemList.stream()
            .map(ShipmentItem::getUniqueCode)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .toList();

        // Query invoice notification items by source unique codes (shipment-based)
        Map<String, InvoicingNoticesItemDTO> invoiceItemMap = new HashMap<>();
        if (CollUtil.isNotEmpty(uniqueCodes)) {
            invoiceItemMap = invoicingNoticesApi.getInvoicingNoticesItemBySourceUniqueCodes(uniqueCodes);
        }

        // Get sale contract item IDs and query purchase contract item IDs
        List<Long> saleContractItemIds = shipmentItemList.stream()
            .map(ShipmentItem::getSaleContractItemId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        // Query invoice notifications created from purchase contracts
        Map<Long, List<InvoicingNoticesItemDTO>> purchaseInvoiceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(saleContractItemIds)) {
            // Get purchase contract item IDs from sale contract item IDs
            Map<Long, Long> saleItemToPurchaseItemMap = purchaseContractApi.getPurchaseItemIdBySaleContractItemIds(saleContractItemIds);
            if (CollUtil.isNotEmpty(saleItemToPurchaseItemMap)) {
                List<Long> purchaseItemIds = new ArrayList<>(saleItemToPurchaseItemMap.values());
                purchaseInvoiceMap = invoicingNoticesApi.getInvoicingNoticesItemByPurchaseItemIds(purchaseItemIds);
            }
        }

        // Build a map: saleContractItemId -> purchaseContractItemId for quick lookup
        Map<Long, Long> saleItemToPurchaseItemMap = new HashMap<>();
        if (CollUtil.isNotEmpty(saleContractItemIds)) {
            saleItemToPurchaseItemMap = purchaseContractApi.getPurchaseItemIdBySaleContractItemIds(saleContractItemIds);
        }

        // Validate each shipment item
        for (ShipmentItem shipmentItem : shipmentItemList) {
            Integer invoiceStatus = shipmentItem.getInvoiceStatus();
            String uniqueCode = shipmentItem.getUniqueCode();
            Long saleContractItemId = shipmentItem.getSaleContractItemId();

            // Check if invoice has been issued
            if (invoiceStatus == null || InvoiceStatusEnum.NOT_ISSUED.getValue().equals(invoiceStatus)) {
                // No invoice issued, skip validation
                continue;
            }

            // Try to find invoice notification from shipment-based (sourceUniqueCode)
            InvoicingNoticesItemDTO invoiceItem = null;
            if (StrUtil.isNotEmpty(uniqueCode)) {
                invoiceItem = invoiceItemMap.get(uniqueCode);
            }

            // If not found from shipment-based, try to find from purchase-contract-based
            if (invoiceItem == null && saleContractItemId != null) {
                Long purchaseItemId = saleItemToPurchaseItemMap.get(saleContractItemId);
                if (purchaseItemId != null) {
                    List<InvoicingNoticesItemDTO> purchaseInvoices = purchaseInvoiceMap.get(purchaseItemId);
                    if (CollUtil.isNotEmpty(purchaseInvoices)) {
                        // Use the first invoice notification found
                        invoiceItem = purchaseInvoices.get(0);
                    }
                }
            }

            // If still not found, invoice status indicates issued but no invoice notification exists
            if (invoiceItem == null) {
                throw exception(DECLARATION_INVOICE_NOT_ISSUED, shipmentItem.getSkuCode());
            }

            // Check product name consistency
            String declarationName = shipmentItem.getDeclarationName();
            String invoiceSkuName = invoiceItem.getInvoicSkuName();

            if (StrUtil.isNotEmpty(declarationName) && StrUtil.isNotEmpty(invoiceSkuName)) {
                if (!declarationName.equals(invoiceSkuName)) {
                    // Product declaration name does not match invoice name
                    throw exception(DECLARATION_NAME_MISMATCH_INVOICE,
                        shipmentItem.getSkuCode(),
                        declarationName,
                        invoiceSkuName);
                }
            }
        }
    }

    @Override
    public void validateInvoiceNoticeFlag(List<String> shipmentCodeList){
        if (CollUtil.isEmpty(shipmentCodeList)){
            return;
        }
        List<ShipmentDO> shipmentDOList = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().select(ShipmentDO::getId).in(ShipmentDO::getCode, shipmentCodeList).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue()));
        if (CollUtil.isEmpty(shipmentDOList)){
            return;
        }
        List<Long> shipmentIdList = shipmentDOList.stream().map(ShipmentDO::getId).distinct().toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getShipmentId, ShipmentItem::getInvoicedQuantity, ShipmentItem::getShippingQuantity).in(ShipmentItem::getShipmentId, shipmentIdList));
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        Map<Long, List<ShipmentItem>> shipmentItemMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        List<ShipmentDO> updateList = new ArrayList<>();
        shipmentItemMap.forEach((shipmentId, itemList) -> {
            if (CollUtil.isEmpty(itemList)){
                return;
            }
            boolean transformFlag = itemList.stream().allMatch(s -> Objects.equals(s.getInvoicedQuantity().intValue(), s.getShippingQuantity()));
            if (transformFlag){
                updateList.add(new ShipmentDO().setId(shipmentId).setInvoiceNoticeFlag(BooleanEnum.YES.getValue()));
            }else {
                updateList.add(new ShipmentDO().setId(shipmentId).setInvoiceNoticeFlag(BooleanEnum.NO.getValue()));
            }
        });
        if (CollUtil.isNotEmpty(updateList)){
            shipmentMapper.updateBatch(updateList);
        }
    }

    @Override
    public List<ShipmentDTO> getShipmentListByCode(List<String> shipmentCodeList) {
        if (CollUtil.isEmpty(shipmentCodeList)){
            return List.of();
        }
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectList(ShipmentDO::getCode, shipmentCodeList);
        if (CollUtil.isEmpty(shipmentDOS)){
            return List.of();
        }
        List<Long> shipmentIdList = shipmentDOS.stream().map(ShipmentDO::getId).toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getShipmentId, ShipmentItem::getPurchaseWithTaxPrice, ShipmentItem::getPurchaseTotalQuantity).in(ShipmentItem::getShipmentId, shipmentIdList));
        if (CollUtil.isEmpty(shipmentItems)){
            return List.of();
        }
        // 计算采购合计
        Map<Long, List<JsonAmount>> shipmentMap = shipmentItems.stream()
                .filter(s->Objects.nonNull(s.getPurchaseWithTaxPrice())&&StrUtil.isNotEmpty(s.getPurchaseWithTaxPrice().getCurrency())&&Objects.nonNull(s.getPurchaseWithTaxPrice().getAmount())&&Objects.nonNull(s.getPurchaseTotalQuantity()))
                .collect(Collectors.groupingBy(
                        ShipmentItem::getShipmentId,
                        Collectors.groupingBy(
                                item -> item.getPurchaseWithTaxPrice().getCurrency(),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        item -> NumUtil.mul(item.getPurchaseWithTaxPrice().getAmount(), item.getPurchaseTotalQuantity()),
                                        BigDecimal::add))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .map(e -> new JsonAmount().setCurrency(e.getKey()).setAmount(e.getValue()))
                                .collect(Collectors.toList())
                ));
        shipmentDOS.forEach(s->{
            if (CollUtil.isNotEmpty(shipmentMap)){
                s.setTotalPurchase(shipmentMap.get(s.getId()));
            }
        });
        return BeanUtils.toBean(shipmentDOS, ShipmentDTO.class);
    }

    @Override
    public CreatedResponse splitShipment(List<ShipmentItem> itemList) {
        if (CollUtil.isEmpty(itemList)){
            return null;
        }
        Set<Long> itemIdList = itemList.stream().map(ShipmentItem::getId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(itemIdList)){
            return null;
        }
        boolean shippingQuantityNotEnough = itemList.stream().anyMatch(s -> s.getSplitQuantity() <= 0);
        if (shippingQuantityNotEnough){
            throw exception(SHIPPING_QUANTITY_NOT_ENOUGH);
        }
        Map<Long, Integer> itemQuantityMap = itemList.stream().collect(Collectors.groupingBy(ShipmentItem::getId, Collectors.summingInt(ShipmentItem::getSplitQuantity)));
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectBatchIds(itemIdList);
        if (CollUtil.isEmpty(shipmentItems)){
            return null;
        }
        shipmentItems.forEach(s->{
            int splitQuantityCurrent = Objects.isNull(itemQuantityMap.get(s.getId())) ? 0 : itemQuantityMap.get(s.getId());
            s.setSplitQuantity(s.getSplitQuantity() + splitQuantityCurrent);
        });
        shipmentItemMapper.updateBatch(shipmentItems);
        Optional<Long> optional = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().findFirst();
        if (!optional.isPresent()){
            return null;
        }
        Long shipmentId = optional.get();
        ShipmentDO shipmentDO = validateShipmentExists(shipmentId);
        ShipmentDO updateDO = new ShipmentDO().setId(shipmentId).setBatchFlag(BooleanEnum.YES.getValue());
        shipmentMapper.updateById(updateDO);
        shipmentDO.setId(null);
        shipmentDO.setSourceId(shipmentId);
        shipmentDO.setSourceCode(shipmentDO.getCode());
        shipmentDO.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        shipmentDO.setStatus(ShippingStatusEnum.AWAITING_PROCESSING.getValue());
        shipmentDO.setShipmentFlag(BooleanEnum.NO.getValue());
        shipmentDO.setDeclarationFlag(BooleanEnum.NO.getValue());
        shipmentDO.setInoviceNotiFlag(BooleanEnum.NO.getValue());
        shipmentDO.setInvoiceNoticeFlag(BooleanEnum.NO.getValue());
        shipmentDO.setOutboundFlag(BooleanEnum.NO.getValue());
        shipmentDO.setSettleOrderFlag(BooleanEnum.NO.getValue());
        shipmentDO.setBatchFlag(BooleanEnum.NO.getValue());
        Map<String,String> linkCodeMap = new HashMap<>();
        List<String> linkCodeList = shipmentDO.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)){
            List<String> newLinkCodeList = new ArrayList<>();
            linkCodeList.forEach(s->{
                String newLinkCode = IdUtil.fastSimpleUUID();
                newLinkCodeList.add(newLinkCode);
                linkCodeMap.put(s, newLinkCode);
            });
            shipmentDO.setLinkCodeList(newLinkCodeList);
        }
        shipmentMapper.insert(shipmentDO);
        Long insertId = shipmentDO.getId();
        List<ShipmentItem> insertItems = shipmentItems.stream().map(s -> {
            ShipmentItem shipmentItem = BeanUtil.copyProperties(s, ShipmentItem.class);
            shipmentItem.setId(null);
            Integer shippingQuantity = itemQuantityMap.get(s.getId());
            if (Objects.isNull(shippingQuantity)){
                return null;
            }
            shipmentItem.setShippingQuantity(shippingQuantity);
            BigDecimal boxCount = NumUtil.div(shippingQuantity, shipmentItem.getQtyPerOuterbox()).setScale(0, RoundingMode.UP);
            shipmentItem.setBoxCount(boxCount.intValue());
            BigDecimal totalVolume = CalcSpecificationUtil.calcTotalVolumeByBoxCount(shipmentItem.getSpecificationList(), boxCount);
            JsonWeight totalGrossweight = CalcSpecificationUtil.calcTotalGrossweightByBoxCount(shipmentItem.getSpecificationList(), boxCount);
            JsonWeight toitalNetWight = CalcSpecificationUtil.calcTotalNetWeightByBoxCount(shipmentItem.getSpecificationList(), boxCount);
            shipmentItem.setTotalGrossweight(totalGrossweight);
            shipmentItem.setTotalNetweight(toitalNetWight);
            shipmentItem.setDeclarationQuantity(shippingQuantity);
            JsonAmount declarationUnitPrice = s.getDeclarationUnitPrice();
            //报关金额=报关数量*报关单价（默认等于销售单价）
            if (!Objects.isNull(declarationUnitPrice)) {
                BigDecimal amount = declarationUnitPrice.getAmount();
                Integer quantity = s.getDeclarationQuantity();
                BigDecimal result = NumUtil.mul(amount, new BigDecimal(quantity));
                if (!Objects.isNull(s.getDeclarationAmount())) {
                    s.getDeclarationAmount().setAmount(result);
                } else {
                    JsonAmount jsonAmount = new JsonAmount();
                    jsonAmount.setAmount(result);
                    jsonAmount.setCurrency(declarationUnitPrice.getCurrency());
                    s.setDeclarationAmount(jsonAmount);
                }
            }
            shipmentItem.setTotalVolume(totalVolume);
            shipmentItem.setShipmentId(insertId);
            shipmentItem.setSplitQuantity(0);
            shipmentItem.setSourceItemId(s.getId());
            shipmentItem.setSettleOrderFlag(BooleanEnum.NO.getValue());
            shipmentItem.setConverNoticeFlag(BooleanEnum.NO.getValue());
            shipmentItem.setCommodityInspectionFlag(BooleanEnum.NO.getValue());
            return shipmentItem;
        }).filter(Objects::nonNull).toList();
        CalcShipmentUtil.calcItemCost(shipmentDO,insertItems,rateApi.getDailyRateMap());
        shipmentMapper.updateById(shipmentDO);
        shipmentItemMapper.insertBatch(insertItems);
        // 复制订单流
        asyncUpdateOrderLinkCodeList(linkCodeMap,shipmentDO);

        return new CreatedResponse(shipmentDO.getId(),shipmentDO.getCode());
    }

    /**
     * 异步更新订单流
     * @param linkCodeMap 链路编号列表
     * @param shipmentDO 出运实体
     */
    private void asyncUpdateOrderLinkCodeList(Map<String,String> linkCodeMap,ShipmentDO shipmentDO){
        List<OrderLinkDTO> orderLinkDTOList = orderLinkApi.copyOrderLink(linkCodeMap, shipmentDO, OrderLinkTypeEnum.SHIPPING_DETAIL.getValue(), shipmentDO.getCode(),shipmentDO.getId());
        if (CollUtil.isEmpty(orderLinkDTOList)){
            return;
        }
        // 更新销售合同订单流
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> updateLinkCodeMap = orderLinkDTOList.stream()
                        .filter(orderLinkDTO -> Objects.equals(OrderLinkTypeEnum.SALE_CONTRACT.getValue(), orderLinkDTO.getType()))
                        .collect(Collectors.toMap(OrderLinkDTO::getCode, OrderLinkDTO::getLinkCode, (v1, v2) -> v1));
                saleContractApi.updateLinkCodeList(updateLinkCodeMap);
            } catch (Exception e) {
                logger.error("异步更新销售合同订单流编号失败", e);
                throw e;
            }
        }, bizThreadPool);
        // 更新采购计划订单流
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> updateLinkCodeMap = orderLinkDTOList.stream()
                        .filter(orderLinkDTO -> Objects.equals(OrderLinkTypeEnum.PURCHASE_PLAN.getValue(), orderLinkDTO.getType()))
                        .collect(Collectors.toMap(OrderLinkDTO::getCode, OrderLinkDTO::getLinkCode, (v1, v2) -> v1));
                purchasePlanApi.updateLinkCodeList(updateLinkCodeMap);
            } catch (Exception e) {
                logger.error("异步更新采购计划订单流编号失败", e);
                throw e;
            }
        }, bizThreadPool);
        // 更新采购合同订单流
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> updateLinkCodeMap = orderLinkDTOList.stream()
                        .filter(orderLinkDTO -> Objects.equals(OrderLinkTypeEnum.PURCHASE_CONTRACT.getValue(), orderLinkDTO.getType()))
                        .collect(Collectors.toMap(OrderLinkDTO::getCode, OrderLinkDTO::getLinkCode, (v1, v2) -> v1));
                purchaseContractApi.updateLinkCodeList(updateLinkCodeMap);
            } catch (Exception e) {
                logger.error("异步更新采购合同订单流编号失败", e);
                throw e;
            }
        }, bizThreadPool);
        // 更新出运计划订单流
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> updateLinkCodeMap = orderLinkDTOList.stream()
                        .filter(orderLinkDTO -> Objects.equals(OrderLinkTypeEnum.SHIPPING_PLAN.getValue(), orderLinkDTO.getType()))
                        .collect(Collectors.toMap(OrderLinkDTO::getCode, OrderLinkDTO::getLinkCode, (v1, v2) -> v1));
                shipmentPlanService.updateLinkCodeList(updateLinkCodeMap);
            } catch (Exception e) {
                logger.error("异步更新出运计划订单流编号失败", e);
                throw e;
            }
        }, bizThreadPool);
    }
    @Override
    public void batchFlag(Long shipmentId) {
        ShipmentDO shipmentDO = validateShipmentExists(shipmentId);
        Integer settleOrderFlag = shipmentDO.getSettleOrderFlag();
        Integer declarationFlag = shipmentDO.getDeclarationFlag();
        Integer outboundFlag = shipmentDO.getOutboundFlag();
        Integer invoiceNoticeFlag = shipmentDO.getInvoiceNoticeFlag();
        if (BooleanEnum.YES.getValue().equals(settleOrderFlag)||BooleanEnum.YES.getValue().equals(declarationFlag)||BooleanEnum.YES.getValue().equals(outboundFlag)||BooleanEnum.YES.getValue().equals(invoiceNoticeFlag)){
            throw exception(SHIPMENT_NOT_ALLOW_BATCH_FLAG);
        }
        shipmentDO.setBatchFlag(BooleanEnum.YES.getValue());
        shipmentMapper.updateById(shipmentDO);
    }

    @Override
    public void cancelBatchFlag(Long shipmentId) {
        ShipmentDO shipmentDO = validateShipmentExists(shipmentId);
        Long count = shipmentMapper.selectCount(new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getSourceId, shipmentId).ne(ShipmentDO::getStatus,ShippingStatusEnum.CASE_CLOSED.getValue()));
        if (count > 0){
            throw exception(SHIPMENT_BATCH_FLAG);
        }
        shipmentDO.setBatchFlag(BooleanEnum.NO.getValue());
        shipmentMapper.updateById(shipmentDO);
    }

    @Override
    public UserDept getDocumenterByInstanceIdInChange(String instanceId) {
        if (StrUtil.isEmpty(instanceId)){
            return null;
        }
        List<ShipmentChange> shipmentChanges = shipmentChangeMapper.selectList(ShipmentChange::getProcessInstanceId, instanceId);
        if (CollUtil.isEmpty(shipmentChanges)){
            return null;
        }
        Optional<ShipmentChange> changeOptional = shipmentChanges.stream().findFirst();
        if (!changeOptional.isPresent()){
            return null;
        }
        ShipmentChange shipmentChange = changeOptional.get();
        ShipmentRespVO oldShipment = shipmentChange.getOldData();
        if (Objects.isNull(oldShipment)){
            return null;
        }
        UserDept documenter = oldShipment.getDocumenter();
        if (Objects.isNull(documenter)){
            throw exception(DOCUMENTER_NOT_FUND,oldShipment.getCode());
        }
        return documenter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> toSettlementForm(ToSettlementFormReqVO toSettlementFormReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<SimpleSettlementFormReqVO> settlementFormList = toSettlementFormReqVO.getSettlementFormList();
        if (CollUtil.isEmpty(settlementFormList)) {
            return responses;
        }
        List<Long> idList = settlementFormList.stream().map(SimpleSettlementFormReqVO::getId).toList();
        Map<Long, Integer> settlementQuantityMap = settlementFormList.stream().collect(Collectors.toMap(SimpleSettlementFormReqVO::getId, SimpleSettlementFormReqVO::getQuantity));
        // 根据出运单明细id查找对应的出运单明细map
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return responses;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        List<ShipmentDO> shipmentDOList = shipmentMapper.selectBatchIds(shipmentIds);
        if (CollUtil.isEmpty(shipmentDOList)){
            return responses;
        }
        // 只有选中的数据状态都在执行中情况下，才可以批量转
//        Optional<String> notReadyOpt = shipmentDOList.stream().filter(s -> !ShippingStatusEnum.READY_FOR_SHIPMENT.getValue().equals(s.getStatus())).map(ShipmentDO::getCode).findFirst();
//        notReadyOpt.ifPresent(s->{
//            throw exception(ONLY_SUPPORTS_BATCH_OPERATION_EXECUTION_DATA,s);
//        });
        Map<Long, ShipmentDO> shipmentMap = shipmentDOList.stream().collect(Collectors.toMap(ShipmentDO::getId, Function.identity()));
        // 校验是否可转结汇单
        List<ShipmentItem> allShipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getShipmentId, shipmentMap.keySet()));
        boolean exists = allShipmentItems.stream().allMatch(s -> Objects.equals(s.getShippingQuantity(), s.getSettlementQuantity()));
        if (exists){
            throw exception(SHIPMENT_COMPANY_IS_TO_SETTLEFORM);
        }
        // 单个客户 运输方式，结汇方式不一样，不可以合并转（废）
        // 单个客户 运输方式不一样，不可以合并转
//        long settleTypeCount = shipmentItemList.stream().map(ShipmentItem::getSettlementId).distinct().count();
//        if (settleTypeCount > 1) {
//            throw exception(SETTLEMENT_DIFF_NOT_TRANSFORM);
//        }
        long transportTypeCount = shipmentDOList.stream().map(ShipmentDO::getTransportType).distinct().count();
        if (transportTypeCount > 1) {
            throw exception(TRANSPORT_DIFF_NOT_TRANSFORM);
        }
        List<String> saleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        Map<String, JsonCompanyPath> companyPathMap = saleContractApi.getCompanyPathMapBySaleContractCodeList(saleContractCodeList);
        Map<Long, BaseData> allInnerCustCache = custApi.getAllInnerCustCache();
        // 将首节点id置入明细中 方便分组
        shipmentItemList.forEach(s->{
            String saleContractCode = s.getSaleContractCode();
            if (StrUtil.isEmpty(saleContractCode)){
                return;
            }
            JsonCompanyPath companyPath = companyPathMap.get(saleContractCode);
            if (Objects.isNull(companyPath)){
                return;
            }
            s.setFirstNodeId(companyPath.getId());
            Integer settlementQuantity = s.getSettlementQuantity();
            Integer shippingQuantity = s.getShippingQuantity();
            if (Objects.equals(settlementQuantity, shippingQuantity)) {
                s.setIsToSettlementForm(ToSettlementEnum.TRANSFERRED.getStatus());
            } else {
                s.setIsToSettlementForm(ToSettlementEnum.PARTIAL_CONVERSION.getStatus());
            }
            //转结汇单时间设置为当前时间
            s.setSettlementFormDate(LocalDateTime.now());
            //转结汇单人设置为当前登录人
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            UserDept userDept = userDeptCache.get(loginUserId);
            s.setSettlementFormUser(userDept);
            s.setSettleOrderFlag(BooleanEnum.YES.getValue());
        });
        List<SettlementFormSaveReqVO> createList = new ArrayList<>();
        Map<String, Map<Long, List<ShipmentItem>>> shipmentItemMap = shipmentItemList.stream()
                .collect(Collectors.groupingBy(
                        ShipmentItem::getCustCode,  // 第一层分组：按客户分组
                        Collectors.groupingBy(
                                ShipmentItem::getFirstNodeId // 按首节点主体分组
                        )));
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        shipmentItemMap.forEach((custCode, custItemMap) -> {
            if (CollUtil.isEmpty(custItemMap)) {
                return;
            }
            // 处理首节点
            custItemMap.forEach((firstCompanyId, contractItems) -> {
                if (CollUtil.isEmpty(contractItems)){
                    return;
                }
                SettlementFormSaveReqVO forstSettlementFormSaveReqVO = transformShipmentDO(firstCompanyId, contractItems, shipmentMap, simpleCompanyDTOMap);
                if (Objects.isNull(forstSettlementFormSaveReqVO)){
                    return;
                }
                List<SettlementFormItem> settlementFormItems = ShipmentConvert.INSTANCE.convertSettlementFormItemList(contractItems, settlementQuantityMap);
                forstSettlementFormSaveReqVO.setChildren(settlementFormItems);
                createList.add(forstSettlementFormSaveReqVO);
            });
            // 非首节点按照出运明细分组再相同主题合并(外贸资质之前)
            List<ShipmentItem> custShipmentList = shipmentItemList.stream().filter(s -> Objects.equals(s.getCustCode(), custCode)).collect(Collectors.toList());
            Map<Long, List<ShipmentItem>> custShipmentMap = custShipmentList.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
            custShipmentMap.forEach((shipmentId, itemListByShipmentId) -> {
                ShipmentDO shipmentDO = shipmentMap.get(shipmentId);
                Map<Long, List<ShipmentItem>> companyItemMap = new HashMap<>();
                itemListByShipmentId.forEach(s -> {
                    JsonCompanyPath companyPath = companyPathMap.get(s.getSaleContractCode());
                    if (Objects.isNull(companyPath)){
                        return;
                    }
                    List<JsonCompanyPath> jsonCompanyPaths = TransformUtil.convertJsonCompanyPathToList(companyPath);
                    if (CollUtil.isEmpty(jsonCompanyPaths)){
                        return;
                    }
                    for (int i = 0; i < jsonCompanyPaths.size(); i++) {
                        JsonCompanyPath jsonCompanyPath = jsonCompanyPaths.get(i);
                        Long companyId = jsonCompanyPath.getId();
                        if (companyId.equals(s.getFirstNodeId())){
                            continue;
                        }
                        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(jsonCompanyPath.getId());
                        if (Objects.isNull(simpleCompanyDTO)){
                            return;
                        }
                        List<ShipmentItem> shipmentItems = CollUtil.isEmpty(companyItemMap.get(jsonCompanyPath.getId()))?new ArrayList<>():companyItemMap.get(jsonCompanyPath.getId());
                        ShipmentItem shipmentItem = BeanUtil.copyProperties(s, ShipmentItem.class);
                        if (i>0&&CollUtil.isNotEmpty(allInnerCustCache)){
                            JsonCompanyPath lastCompany = jsonCompanyPaths.get(i - 1);
                            if (Objects.nonNull(lastCompany)&&allInnerCustCache.containsKey(lastCompany.getId())){
                                BaseData baseData = allInnerCustCache.get(lastCompany.getId());
                                shipmentItem.setCustId(baseData.getId());
                                shipmentItem.setCustName(baseData.getName());
                                shipmentItem.setCustCode(baseData.getCode());
                            }
                        }
                        shipmentItems.add(shipmentItem);
                        companyItemMap.put(jsonCompanyPath.getId(),shipmentItems);
                        if (CompanyNatureEnum.FOREIGN_TRADE.getValue().equals(simpleCompanyDTO.getCompanyNature())){
                            break;
                        }
                    }
                });
                if (CollUtil.isEmpty(companyItemMap)){
                    return;
                }
                companyItemMap.forEach((companyId, itemList) -> {
                    SettlementFormSaveReqVO settlementFormSaveReqVO = ShipmentConvert.INSTANCE.convertSettlementFormSaveReqVO(shipmentDO);
                    SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
                    if (Objects.isNull(simpleCompanyDTO)){
                        return;
                    }
                    settlementFormSaveReqVO.setForeignTradeCompanyId(simpleCompanyDTO.getId());
                    settlementFormSaveReqVO.setForeignTradeCompanyName(simpleCompanyDTO.getName());
                    List<SettlementFormItem> settlementFormItems = ShipmentConvert.INSTANCE.convertSettlementFormItemList(itemList, settlementQuantityMap);
                    if (CollUtil.isNotEmpty(settlementFormItems)){
                        settlementFormSaveReqVO.setChildren(settlementFormItems);
                    }
                    createList.add(settlementFormSaveReqVO);
                });
            });
        });
        if (CollUtil.isNotEmpty(shipmentItemList)){
            shipmentItemMapper.updateBatch(shipmentItemList);
        }
        if (CollUtil.isNotEmpty(createList)) {
            List<String> shipmentCodeList = createList.stream().map(SettlementFormSaveReqVO::getShipmentCode).distinct().toList();
            validateSettlementFlag(shipmentCodeList,settlementQuantityMap);
            for(var it : createList){
                CreatedResponse settlementForm = settlementFormService.createSettlementForm(it);
                responses.add(settlementForm);
            }
        }
        return responses;
    }

    private SettlementFormSaveReqVO transformShipmentDO(Long companyId,List<ShipmentItem> shipmentItems,Map<Long, ShipmentDO> shipmentMap,Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap){
        if (Objects.isNull(companyId)||CollUtil.isEmpty(shipmentItems)||CollUtil.isEmpty(simpleCompanyDTOMap)||CollUtil.isEmpty(shipmentMap)){
            return null;
        }
        //SettlementFormSaveReqVO forstSettlementFormSaveReqVO = ShipmentConvert.INSTANCE.convertSettlementFormSaveReqVO(firstShipmentDO);
        SettlementFormSaveReqVO result = new SettlementFormSaveReqVO();
        Optional<ShipmentItem> optional = shipmentItems.stream().findFirst();
        if (optional.isPresent()){
            ShipmentDO shipmentDO = shipmentMap.get(optional.get().getShipmentId());
            result = ShipmentConvert.INSTANCE.convertSettlementFormSaveReqVO(shipmentDO);
            result.setForeignTradeCompanyId(companyId);
            SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
            if (Objects.nonNull(simpleCompanyDTO)){
                result.setForeignTradeCompanyName(simpleCompanyDTO.getName());
            }
        }
        String saleContractCode = shipmentItems.stream().map(ShipmentItem::getSaleContractCode).collect(Collectors.joining(CommonDict.COMMA));
        String invoiceCode = shipmentItems.stream().map(s->{
            Long shipmentId = s.getShipmentId();
            ShipmentDO shipmentDO = shipmentMap.get(shipmentId);
            if (Objects.isNull(shipmentDO)){
                return CommonDict.EMPTY_STR;
            }
            return shipmentDO.getInvoiceCode();
        }).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA));
        String shipmentCode = shipmentItems.stream().map(s->{
            Long shipmentId = s.getShipmentId();
            ShipmentDO shipmentDO = shipmentMap.get(shipmentId);
            if (Objects.isNull(shipmentDO)){
                return CommonDict.EMPTY_STR;
            }
            return shipmentDO.getCode();
        }).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA));
        Optional<ShipmentItem> shipmentItemOpt = shipmentItems.stream().findFirst();
        if (shipmentItemOpt.isPresent()){
            ShipmentItem shipmentItem = shipmentItemOpt.get();
            result.setSettlementId(shipmentItem.getSettlementId());
            result.setSettlementName(shipmentItem.getSettlementName());
        }
        result.setSaleContractCode(saleContractCode);
        result.setInvoiceCode(invoiceCode);
        result.setShipmentCode(shipmentCode);
        return result;
    }
    private void validateSettlementFlag(List<String> codeList,Map<Long, Integer> settlementQuantityMap){
        if (CollUtil.isEmpty(codeList)){
            return;
        }
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().select(ShipmentDO::getId).in(ShipmentDO::getCode, codeList));
        if (CollUtil.isEmpty(shipmentDOS)){
            return;
        }
        List<Long> ShipmentIdList = shipmentDOS.stream().map(ShipmentDO::getId).distinct().toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getId, ShipmentItem::getShipmentId, ShipmentItem::getSettleOrderFlag).in(ShipmentItem::getShipmentId, ShipmentIdList));
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        shipmentItems.forEach(s->{
            if (CollUtil.isNotEmpty(settlementQuantityMap)){
                Integer i = Objects.isNull(settlementQuantityMap.get(s.getId()))?0:settlementQuantityMap.get(s.getId());
                int settlementQuantity = Objects.isNull(s.getSettlementQuantity()) ? 0 : s.getSettlementQuantity();
                s.setSettlementQuantity(settlementQuantity+i);
            }
        });
        shipmentItemMapper.updateBatch(shipmentItems);
        Map<Long, List<ShipmentItem>> shipmentItemMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        List<ShipmentDO> updateList = new ArrayList<>();
        shipmentItemMap.forEach((shipmentId, shipmentItemList)->{
            if (CollUtil.isEmpty(shipmentItemList)){
                return;
            }
            boolean notTransformFlag = shipmentItemList.stream().noneMatch(shipmentItem -> BooleanEnum.YES.getValue().equals(shipmentItem.getSettleOrderFlag()));
            if (notTransformFlag){
                updateList.add(new ShipmentDO().setId(shipmentId).setSettleOrderFlag(BooleanEnum.NO.getValue()));
            }else {
                updateList.add(new ShipmentDO().setId(shipmentId).setSettleOrderFlag(BooleanEnum.YES.getValue()));
            }
        });
        shipmentMapper.updateBatch(updateList);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeShipment(CloseShipmentReq closeShipmentReq) {
        if (Objects.nonNull(closeShipmentReq.getItemId())) {
            ShipmentItem baseItem = shipmentItemMapper.selectById(closeShipmentReq.getItemId());
            if (Objects.isNull(baseItem)) {
                throw exception(SHIPMENT_ITEM_NOT_EXISTS);
            }
            validateAntiAuditStatus(baseItem.getShipmentId());
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setId(closeShipmentReq.getItemId());
            shipmentItem.setStatus(ShippingStatusEnum.CASE_CLOSED.getValue());
            shipmentItemMapper.updateById(shipmentItem);
            List<Integer> shipmentItemStatusLink = shipmentItemMapper.getShipmentItemStatusLink(closeShipmentReq.getItemId());
            if (CollUtil.isEmpty(shipmentItemStatusLink)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否全部结案
            boolean allClosed = shipmentItemStatusLink.stream()
                    .allMatch(status -> status.equals(ShippingStatusEnum.CASE_CLOSED.getValue()));
            // 子项全部结案则合同状态变更为结案
            if (allClosed) {
                ShipmentItem closedShipmentItem = shipmentItemMapper.selectById(closeShipmentReq.getItemId());
                Long contractId = closedShipmentItem.getShipmentId();
                // 更新单据流订单状态
                ShipmentDO shipmentDO = validateShipmentExists(contractId);
                orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.CASE_CLOSED.getValue());
                shipmentMapper.updateById(new ShipmentDO().setId(contractId).setStatus(ShippingStatusEnum.CASE_CLOSED.getValue()));
            }
            updateShipmentPlanStatus(List.of(baseItem.getSourceUniqueCode()));

        }
        if (Objects.nonNull(closeShipmentReq.getParentId())) {
            validateAntiAuditStatus(closeShipmentReq.getParentId());
            ShipmentDO shipmentDO = validateShipmentExists(closeShipmentReq.getParentId());
            shipmentDO.setId(closeShipmentReq.getParentId());
            shipmentDO.setStatus(ShippingStatusEnum.CASE_CLOSED.getValue());
            // 更新单据流订单状态
            orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.CASE_CLOSED.getValue());
            shipmentMapper.updateById(shipmentDO);
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setStatus(ShippingStatusEnum.CASE_CLOSED.getValue());
            shipmentItemMapper.update(shipmentItem, new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getShipmentId, closeShipmentReq.getParentId()));
            List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getShipmentId, closeShipmentReq.getParentId()));
            if (CollUtil.isEmpty(shipmentItemList)) {
                return;
            }
            // 分批出运作废回退数量
            Map<Long, Integer> splitQuantityMap = shipmentItemList.stream().filter(s -> Objects.nonNull(s.getSourceItemId())&&Objects.nonNull(s.getShippingQuantity())).collect(Collectors.groupingBy(ShipmentItem::getSourceItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
            rollBackSplitQuantiy(splitQuantityMap);
            List<String> uniqueCodeList = shipmentItemList.stream().map(ShipmentItem::getSourceUniqueCode).distinct().toList();
            if (CollUtil.isEmpty(uniqueCodeList)) {
                return;
            }
            updateShipmentPlanStatus(uniqueCodeList);

            //回写已转出运明细数量
            List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, closeShipmentReq.getParentId());
            List<CloseShipmentDTO> closeReq = new ArrayList<>();
            // 仅当明细不是分批出运时回写销售转出运数量
            shipmentItems.stream().filter(s->Objects.isNull(s.getSourceItemId())).forEach(s -> {
                closeReq.add(new CloseShipmentDTO().setSaleContractItemId(s.getSaleContractItemId()).setShipmentQuantity(s.getShippingQuantity()));
            });
            if (CollUtil.isNotEmpty(closeReq)){
                saleContractApi.updateShipmentedQuantity(closeReq);
            }
            // 作废自动生成的出运明细
            ShipmentDO autoShipment = shipmentMapper.selectOne(ShipmentDO::getSourceCode, shipmentDO.getCode());
            if (Objects.isNull(autoShipment)) {
                return;
            }
            closeShipment(new CloseShipmentReq().setParentId(autoShipment.getId()));

        }
    }

    /**
     * 作废回退拆分数量
     * @param splitQuantityMap 拆分缓存
     */
    private void rollBackSplitQuantiy(Map<Long, Integer> splitQuantityMap){
        if (CollUtil.isEmpty(splitQuantityMap)){
            return;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectBatchIds(splitQuantityMap.keySet());
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        List<ShipmentDO> updateShipmentList = new ArrayList<>();
        List<ShipmentItem> updateShipmentItemList = new ArrayList<>();
        Map<Long, List<ShipmentItem>> shipmentItemMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        shipmentItemMap.forEach((shipmentId,shipmentItemList)->{
            if (CollUtil.isEmpty(shipmentItemList)){
                return;
            }
            shipmentItemList.forEach(s->{
                Integer splitQuantity = splitQuantityMap.get(s.getId());
                if (Objects.nonNull(splitQuantity)){
                    s.setSplitQuantity(s.getSplitQuantity()-splitQuantity);
                    updateShipmentItemList.add(s);
                }
            });
        });
        if (CollUtil.isNotEmpty(updateShipmentItemList)){
            shipmentItemMapper.updateBatch(updateShipmentItemList);
        }
    }
    /**
     * 更新储运计划转出运状态
     *
     * @param uniqueCodeList 唯一标识
     */
    private void updateShipmentPlanStatus(List<String> uniqueCodeList) {
        List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemMapper.selectList(ShipmentPlanItem::getUniqueCode, uniqueCodeList);
        shipmentPlanItems.forEach(s -> {
            s.setTransformShipmentFlag(BooleanEnum.NO.getValue());
            s.setStatus(ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue());
        });
        // 更新出运计划明细转出运标识
        shipmentPlanItemMapper.updateBatch(shipmentPlanItems);
        List<Long> shipmentPlanIdList = shipmentPlanItems.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().toList();
        List<ShipmentPlanItem> allItems = shipmentPlanItemMapper.selectList(ShipmentPlanItem::getShipmentPlanId, shipmentPlanIdList);
        boolean transformShipmentFlag = allItems.stream().allMatch(s -> BooleanEnum.NO.getValue().equals(s.getTransformShipmentFlag()));
        // 如果全部作废则计划改为待转
        if (transformShipmentFlag) {
            shipmentPlanMapper.update(new LambdaUpdateWrapper<ShipmentPlanDO>().set(ShipmentPlanDO::getStatus, ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue()).in(ShipmentPlanDO::getId, shipmentPlanIdList).eq(ShipmentPlanDO::getStatus,ShippingPlanStatusEnum.COMPLETED.getValue()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackClose(CloseShipmentReq closeShipmentReq) {
        if (Objects.nonNull(closeShipmentReq.getItemId())) {
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setId(closeShipmentReq.getItemId());
            shipmentItem.setStatus(ShippingStatusEnum.READY_FOR_SHIPMENT.getValue());
            shipmentItemMapper.updateById(shipmentItem);
            List<Integer> shipmentItemStatusLink = shipmentItemMapper.getShipmentItemStatusLink(closeShipmentReq.getItemId());
            if (CollUtil.isEmpty(shipmentItemStatusLink)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否全部待处理
            boolean allClosed = shipmentItemStatusLink.stream()
                    .allMatch(status -> status.equals(ShippingStatusEnum.READY_FOR_SHIPMENT.getValue()));
            // 子项全部待处理则合同状态变更为待处理
            if (allClosed) {
                ShipmentItem closedShipmentItem = shipmentItemMapper.selectById(closeShipmentReq.getItemId());
                Long contractId = closedShipmentItem.getShipmentId();
                // 更新单据流订单状态
                ShipmentDO shipmentDO = validateShipmentExists(contractId);
                orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.READY_FOR_SHIPMENT.getValue());
                shipmentMapper.updateById(new ShipmentDO().setId(contractId).setStatus(ShippingStatusEnum.READY_FOR_SHIPMENT.getValue()));
            }
        }
        if (Objects.nonNull(closeShipmentReq.getParentId())) {
            ShipmentDO shipmentDO = new ShipmentDO();
            shipmentDO.setId(closeShipmentReq.getParentId());
            shipmentDO.setStatus(ShippingStatusEnum.READY_FOR_SHIPMENT.getValue());
            shipmentMapper.updateById(shipmentDO);
            // 更新单据流订单状态
            orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.READY_FOR_SHIPMENT.getValue());
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setStatus(ShippingStatusEnum.READY_FOR_SHIPMENT.getValue());
            shipmentItemMapper.update(shipmentItem, new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getShipmentId, closeShipmentReq.getParentId()));
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipment(ShipmentReq shipmentReq) {
        Long id = shipmentReq.getId();
        LocalDateTime estDepartureTime = shipmentReq.getEstDepartureTime();
        ShipmentDO shipmentDO = validateShipmentExists(id);
        String genContractUniqueCode = IdUtil.fastSimpleUUID();
        shipmentMapper.updateById(new ShipmentDO().setId(id).setGenContractUniqueCode(genContractUniqueCode).setEstDepartureTime(estDepartureTime).setShipmentFlag(BooleanEnum.YES.getValue()).setStatus(ShippingStatusEnum.SHIPPED.getValue()));
        // 更新单据流订单状态
        orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.SHIPPED.getValue());
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, id);
        shipmentItemList.forEach(s -> {
            // 未出库不可出运
            if (Objects.isNull(s.getOutQuantity()) || (s.getOutQuantity() < s.getShippingQuantity())) {
                throw exception(SKU_NOT_OUT_BILL);
            }
            if (s.getOutQuantity() > s.getShippingQuantity()) {
                throw exception(SKU_OUT_QUANTITY_OUT_OF_SHIPPING_QUANTITY);
            }
            s.setStatus(ShippingStatusEnum.SHIPPED.getValue());
        });
        shipmentItemMapper.updateBatch(shipmentItemList);
        List<Long> saleContractItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        // 更新采购合同出运日期
        List<Integer> dateTypes = new ArrayList<>();
        dateTypes.add(DateTypeEnum.SHIPMENT_DATE.getValue());
        purchaseContractApi.updateShipmentDate(saleContractItemIdList, dateTypes,estDepartureTime);
        List<String> smsContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        // 更新销售合同收款计划日期
        List<Integer> saleContractDateTypes = new ArrayList<>();
        dateTypes.add(ShettlementDateTypeEnum.SHIPMENT_DATE.getValue());
        saleContractApi.updateShipmentDate(smsContractCodeList, saleContractDateTypes);
        // 更新销售合同待出运数量及已出运数量
        Map<Long, Integer> shippedQuantityMap = shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        saleContractApi.batchUpdateShippedQuantity(shippedQuantityMap);
        // 生成内部购销合同
        Map<Long, Integer> shipmentMap = shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        Map<Long, Integer> shipmentPurchaseMap = shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(s->s.getThisPurchaseQuantity().intValue())));
        Map<Long,SimpleShipment> simpleShipmentMap = new HashMap<>();
        shipmentItemList.forEach(s->{
            if (!shipmentPurchaseMap.containsKey(s.getSaleContractItemId())){
                return;
            }
            simpleShipmentMap.put(s.getSaleContractItemId(),BeanUtils.toBean(s, SimpleShipment.class));

        });
        saleContractApi.genInternalContract(shipmentMap,shipmentPurchaseMap,genContractUniqueCode,simpleShipmentMap);
    }


    @Override
    public void finishShipment(Long id) {
        ShipmentDO shipmentDO = validateShipmentExists(id);
        shipmentMapper.updateById(new ShipmentDO().setId(id).setStatus(ShippingStatusEnum.COMPLETED.getValue()));
        // 更新单据流订单状态
        orderLinkApi.updateOrderLinkStatus(shipmentDO.getCode(), BusinessNameDict.SHIPMENT_NAME, shipmentDO.getLinkCodeList(), ShippingStatusEnum.COMPLETED.getValue());
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, id);
        shipmentItemList.forEach(s -> {
            s.setStatus(ShippingStatusEnum.COMPLETED.getValue());
        });
        shipmentItemMapper.updateBatch(shipmentItemList);
        //回写销售合同已经下推出运明细数
//        Map<Long, Integer> itemQuantityMap = shipmentItemList.stream().collect(Collectors.toMap(ShipmentItem::getSaleContractItemId, ShipmentItem::getShippingQuantity, (s1, s2) -> s1));
//        saleContractApi.setShipmentQuantity(itemQuantityMap);
    }

    @Override
    public List<CreatedResponse> transformInvoicingNotices(List<ShipmentToInvoiceSaveReq> reqList) {
        List<CreatedResponse> responses = new ArrayList<>();
        if (CollUtil.isEmpty(reqList)){
            return responses;
        }
        Set<Long> itemIdList = reqList.stream().map(ShipmentToInvoiceSaveReq::getItemId).distinct().collect(Collectors.toSet());
        Map<Long, ShipmentToInvoiceSaveReq> shipmentToInvoiceSaveReqMap = reqList.stream().collect(Collectors.toMap(ShipmentToInvoiceSaveReq::getItemId, s -> s, (o, n) -> n));
        if (CollUtil.isEmpty(itemIdList)){
            return responses;
        }
        // 获取出运单明细信息
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(itemIdList);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return responses;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        if (CollUtil.isEmpty(shipmentIds)) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        if (shipmentIds.size() > CalculationDict.ONE) {
            throw exception(MANY_SHIPMENT_NOT_ALLOWED);
        }
        Long shipmentId = shipmentIds.get(CalculationDict.ZERO);

        // 获取出运单信息
        ShipmentDO shipmentDO = shipmentMapper.selectById(shipmentId);
        if (Objects.isNull(shipmentDO)) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        boolean splitPurchaseFlag = shipmentItemList.stream().anyMatch(s -> PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel()));
        boolean shipmentFlag = shipmentDO.getStatus() >= ShippingStatusEnum.SHIPPED.getValue();
        if (splitPurchaseFlag && !shipmentFlag){
            throw exception(SPLIT_PURCHASE_NEED_SHIPMENT);
        }
        Set<String> saleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).collect(Collectors.toSet());
        if (CollUtil.isEmpty(saleContractCodeList)) {
            throw exception(SALECONTRACT_NULL);
        }
        // 校验相同路径下的销售合同才可转开票通知
//        saleContractApi.validateCompanyPath(saleContractCodeList);
        // 根据供应商及采购合同分组
        Map<String, List<ShipmentItem>> shipmentItemMap = shipmentItemList.stream().filter(s->{
            String purchaseContractCode = s.getPurchaseContractCode();
            if (StrUtil.isEmpty(purchaseContractCode)){
                throw exception(SHIPMENT_PURCHASE_CODE_IS_EMPTY);
            }
            return true;
        }).collect(Collectors.groupingBy(ShipmentItem::getPurchaseContractCode));
        if (CollUtil.isEmpty(shipmentItemMap)) {
            return responses;
        }
        List<InvoicingNoticesDTO> result = new ArrayList<>();

        //计算出运数量
//        Map<Long,Integer> quantityMap = CalculateQuantity(shipmentItemList) ;
        Map<String, String> purchaseVenderMap = purchaseContractApi.getVenderCodeMapByPurchaseContractCodeList(shipmentItemMap.keySet());
        // 获取采购合同主体
        Map<String, Long> purchaseCompanyMap = purchaseContractApi.getPurchaseCompanyMap(shipmentItemMap.keySet());
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        // 供应商简要信息缓存
        Map<String, SimpleVenderResp> simpleVenderMapByCodes = venderApi.getSimpleVenderMapByCodes(purchaseVenderMap.values());
        shipmentItemMap.forEach((purchaseContractCode, value) -> {
            String venderCode = purchaseVenderMap.get(purchaseContractCode);
            InvoicingNoticesDTO invoicingNoticesDTO = transfromInvoicingNoticesDTO(shipmentDO, venderCode, purchaseContractCode, simpleVenderMapByCodes, value);
            invoicingNoticesDTO.setChildren(transformInvoicingNoticesItemDTOList(value, shipmentDO,shipmentToInvoiceSaveReqMap));
            invoicingNoticesDTO.setSourceType(InvoiceNoticeSourceTypeEnum.SHIPMENT.getValue());
            invoicingNoticesDTO.setLinkCodeList(shipmentDO.getLinkCodeList());
            //设置为采购合同主体
            if (CollUtil.isNotEmpty(purchaseCompanyMap)){
                Long companyId = purchaseCompanyMap.get(purchaseContractCode);
                invoicingNoticesDTO.setCompanyId(companyId);
                Optional.ofNullable(simpleCompanyDTOMap.get(companyId)).ifPresent(s->{
                    invoicingNoticesDTO.setCompanyId(s.getId());
                    invoicingNoticesDTO.setCompanyName(s.getCompanyName());
                });
            }
            result.add(invoicingNoticesDTO);
        });
        responses = invoicingNoticesApi.createInvoicingNotices(result);
        shipmentMapper.updateById(new ShipmentDO().setInvoiceNoticeFlag(BooleanEnum.YES.getValue()).setId(shipmentId));
        shipmentItemList.forEach(s->{
            ShipmentToInvoiceSaveReq shipmentToInvoiceSaveReq = shipmentToInvoiceSaveReqMap.get(s.getId());
            BigDecimal invoicedQuantity = Objects.isNull(s.getInvoicedQuantity()) ? BigDecimal.ZERO : s.getInvoicedQuantity();
            if (Objects.nonNull(shipmentToInvoiceSaveReq)){
                s.setInvoicedQuantity(NumUtil.add(invoicedQuantity, shipmentToInvoiceSaveReq.getInvoiceQuantity()));
                if (s.getInvoicedQuantity().intValue()>=s.getShippingQuantity()){
                    s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
                }else {
                    s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
                }
            }
        });
        shipmentItemMapper.updateBatch(shipmentItemList);
        return responses;
    }

    private Map<Long, Integer> CalculateQuantity(List<ShipmentItem> shipmentItemList) {
        Map<Long, Integer> result = new HashMap<>();
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        List<Long> saleIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        Map<Long, SaleContractItemDTO> itemDTOMap = saleContractApi.getItemListByIds(saleIdList);
        if (CollUtil.isEmpty(itemDTOMap)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        shipmentItemList.forEach(s -> {
            SaleContractItemDTO saleContractItemDTO = itemDTOMap.get(s.getSaleContractItemId());
            if (Objects.nonNull(saleContractItemDTO)) {
                Integer noticeQuantity = 0; //开票数
                //计算销售使用库存 = 销售数量-待采购数量
                Integer stockQuantity = saleContractItemDTO.getRealLockQuantity();
                //计算不包含这次已转出运数 = 已转出运总数 - 本次出运数量
//                Integer lastShipmentTotalQuantity = saleContractItemDTO.getShipmentTotalQuantity() - s.getShippingQuantity();
                //计算当前可用库存数量 = 库存数量 - 不包含这次已转出运数

                //转出运计划时还未写入本次数量
                Integer useStockQuantity = stockQuantity - saleContractItemDTO.getShipmentTotalQuantity();
                if (useStockQuantity > 0) { //库存还未全部出运，开票数 = 出运数 - 可使用数量
                    noticeQuantity = s.getShippingQuantity() - useStockQuantity;
                    if (noticeQuantity < 0) {  //出运数不足可使用库存数，全部使用库存，不开票
                        noticeQuantity = 0;
                    }
                } else {  //库存全部使用，开票数=出运数
                    noticeQuantity = s.getShippingQuantity();
                }
                result.put(s.getSaleContractItemId(), noticeQuantity);
            }
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> changeShipment(ChangeShipmentSaveReq changeReq, boolean businessFlag) {
        List<CreatedResponse> responses = new ArrayList<>();
        ShipmentRespVO oldShipment = changeReq.getOld_shipment();
        ShipmentRespVO shipment = changeReq.getShipment();
        // 提交流程标识 默认使用前端传入
        AtomicReference<Integer> submitFlag = new AtomicReference<>(0);
        Set<String> changeFields = new ChangeCompareUtil<ShipmentRespVO>().transformObject(oldShipment, shipment);
        // 变更字段名称
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("dms_shipment", "dms_shipment_item", "sms_add_sub_term", "dms_temporary_sku", "dms_forwarder_fee"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(SHIPMENT_FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        String code = codeGeneratorApi.getCodeGenerator(CHANGE_SN_TYPE, CHANGE_CODE_PREFIX);
        ShipmentChange shipmentChange = ShipmentConvert.INSTANCE.convertShipmentChange(shipment);
        shipmentChange.setOldData(oldShipment);
        shipmentChange.setCode(code);
        // 比对变更配置字段
        submitFlag = formChangeApi.dealShipmentTable(changeFields, formChangeDTOList, shipmentChange, "dms_shipment", submitFlag, true);
        // 处理出运单明细
        List<ShipmentItem> oldChildren = oldShipment.getChildren();
        List<ShipmentItem> children = shipment.getChildren();
        // 校验明细数量是否可变更
        validateItemQuantity(oldChildren,children);
        // 变更数量逻辑处理
        calcShipmentItemPurchaseAndStockQuantity(oldChildren,children);
        List<DiffRecord<ShipmentItem>> diffRecords = DiffUtil.compareLists(oldChildren, children);
        Tuple itemTuple = new ChangeCompareUtil<ShipmentItem>().transformChildList(diffRecords, ShipmentItem.class);
        List<ShipmentItem> itemChangeLsit = itemTuple.get(0);
        Set<String> itemFieldList = itemTuple.get(1);
        // 比对出运单明细字段
        submitFlag = formChangeApi.dealShipmentTable(itemFieldList, formChangeDTOList, shipmentChange, "dms_shipment_item", submitFlag, false);
        shipmentChange.setChildren(itemChangeLsit);
        // 处理加减项
        List<AddSubItemDTO> oldAddSubItemList = oldShipment.getAddSubItemList();
        List<AddSubItemDTO> addSubItemList = shipment.getAddSubItemList();
        List<DiffRecord<AddSubItemDTO>> addSubTermDiffRecords = DiffUtil.compareLists(oldAddSubItemList, addSubItemList);
        Tuple subItem = new ChangeCompareUtil<AddSubItemDTO>().transformChildList(addSubTermDiffRecords, AddSubItemDTO.class);
        List<AddSubItemDTO> changeAddSubItemList = subItem.get(0);
        Set<String> addSubItemFieldList = subItem.get(1);
        submitFlag = formChangeApi.dealShipmentTable(addSubItemFieldList, formChangeDTOList, shipmentChange, "sms_add_sub_term", submitFlag, false);
        shipmentChange.setAddSubItemList(changeAddSubItemList);
        // 处理船代费用
        List<ForwarderFeeDO> oldForwarderFeeList = oldShipment.getForwarderFeeList();
        List<ForwarderFeeDO> forwarderFeeList = shipment.getForwarderFeeList();
        List<DiffRecord<ForwarderFeeDO>> forwarderFeeDiffRecords = DiffUtil.compareLists(oldForwarderFeeList, forwarderFeeList);
        Tuple forwarderFeeItem = new ChangeCompareUtil<ForwarderFeeDO>().transformChildList(forwarderFeeDiffRecords, ForwarderFeeDO.class);
        List<ForwarderFeeDO> changeForwarderFeeList = forwarderFeeItem.get(0);
        Set<String> addForwarderFeeFieldList = forwarderFeeItem.get(1);
        submitFlag = formChangeApi.dealShipmentTable(addForwarderFeeFieldList, formChangeDTOList, shipmentChange, "dms_forwarder_fee", submitFlag, false);

        //船代费用补齐信息
        changeForwarderFeeList.forEach(s -> {
            if (Objects.isNull(s.getId())) {
                s.setCode(forwarderFeeService.getNewCode());
                s.setShipmentId(shipment.getId());
                s.setShipmentCode(shipment.getCode());
                s.setInvoiceCode(shipment.getInvoiceCode());
                s.setCompanyId(shipment.getForeignTradeCompanyId());
                s.setCompanyName(shipment.getForeignTradeCompanyName());
                s.setPayStatus(PayStatusEnum.NOT_APPLY.getValue());
                s.setApplyer(shipmentChange.getInputUser());
            }
        });
        shipmentChange.setForwarderFeeList(changeForwarderFeeList);

        // 处理临时产品
        List<TemporarySku> oldTemporarySkuList = oldShipment.getTemporarySkuList();
        List<TemporarySku> temporarySkuList = shipment.getTemporarySkuList();
        List<DiffRecord<TemporarySku>> temporarySkuDiffRecords = DiffUtil.compareLists(oldTemporarySkuList, temporarySkuList);
        Tuple temporaryTuple = new ChangeCompareUtil<TemporarySku>().transformChildList(temporarySkuDiffRecords, TemporarySku.class);
        List<TemporarySku> changeTemporarySkuList = temporaryTuple.get(0);
        Set<String> temporaryFieldList = temporaryTuple.get(1);
        submitFlag = formChangeApi.dealShipmentTable(temporaryFieldList, formChangeDTOList, shipmentChange, "dms_temporary_sku", submitFlag, false);
        shipmentChange.setTemporarySkuList(changeTemporarySkuList);
        shipmentChange.setId(null);
        shipmentChange.setSales(new UserDept());
        // 记录操作日志
        OperateLogUtils.setContent(String.format("创建出运明细变更记录【%s】", code));
        OperateLogUtils.addExt(CHANGE_OPERATOR_EXTS_KEY, shipmentChange.getCode());
        shipmentChangeMapper.insert(shipmentChange);
        responses.add(new CreatedResponse(shipmentChange.getId(), shipmentChange.getCode()));
        Map<String, Object> variables = new HashMap<>();
        // 如果都是普通级直接进入变更审批
        if (Objects.isNull(submitFlag) || SubmitFlagEnum.ONLY_SAVE.getStatus().equals(submitFlag.get())) {
            // 具体规则待定 暂写死单证部
            String processInstanceId;
            if (businessFlag) {
                variables.put("businessFlag", 1);
                Long documenterId = Objects.isNull(oldShipment.getDocumenter()) ? null : oldShipment.getDocumenter().getUserId();
                variables.put("documenterId",documenterId);
                processInstanceId = submitChangeTask(shipmentChange.getId(), WebFrameworkUtils.getLoginUserId(), BUSINESS_MODEL_KEY, variables);
                shipmentChange.setModelKey(BUSINESS_MODEL_KEY);
                shipmentChange.setProcessInstanceId(processInstanceId);
                shipmentChange.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
                // 更改出运单变更状态
                shipmentMapper.updateById(new ShipmentDO().setId(oldShipment.getId()).setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus()));
            } else {
                updateChangeResult(shipmentChange.getId());
                shipmentChange.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            }
            shipmentChangeMapper.updateById(shipmentChange);
            return responses;
        }
        // 进入配置流程审批
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(submitFlag.get())) {
            variables.put("businessFlag", 0);
            String processInstanceId = submitChangeTask(shipmentChange.getId(), WebFrameworkUtils.getLoginUserId(), shipmentChange.getModelKey(), variables);
            shipmentChange.setProcessInstanceId(processInstanceId);
            shipmentChangeMapper.updateById(shipmentChange);
            // 更改出运单变更状态
            shipmentMapper.updateById(new ShipmentDO().setId(oldShipment.getId()).setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus()));
        }
        return responses;
    }

    public List<ShipmentItem> calcShipmentItemPurchaseAndStockQuantity(List<ShipmentItem> oldshipmentItems,List<ShipmentItem> newshipmentItems){
        if (CollUtil.isEmpty(oldshipmentItems)||CollUtil.isEmpty(newshipmentItems)){
            return List.of();
        }
        // 1.计算数量的差值
        Map<Long,Integer> changeQuantityMap = new HashMap<>();
        Map<Long,Integer> oldShippingQuantityMap = oldshipmentItems.stream().collect(Collectors.toMap(ShipmentItem::getId, s->{return Objects.isNull(s.getShippingQuantity())?0:s.getShippingQuantity();}));
        newshipmentItems.forEach(newshipmentItem->{
            Integer oldShippingQuantity = Objects.isNull(oldShippingQuantityMap.get(newshipmentItem.getId()))?0:oldShippingQuantityMap.get(newshipmentItem.getId());
            Integer shippingQuantity = newshipmentItem.getShippingQuantity();
            changeQuantityMap.put(newshipmentItem.getId(),shippingQuantity-oldShippingQuantity);
        });
        // 2.获取销售对应总数量
        Set<Long> saleItemIdSet = newshipmentItems.stream().map(ShipmentItem::getSaleContractItemId).collect(Collectors.toSet());
        Map<Long, Map<String, Integer>> salePurchaseAndStockQuantityMap = saleContractApi.getSalePurchaseAndStockQuantityMap(saleItemIdSet);
        // 3.查询已占用采购数及库存数（因为当前变更的明细还未实际入库，所以需单独处理）
        List<ShipmentItem> allShipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getSaleContractItemId, saleItemIdSet));
        Set<Long> shipmentIdSet = allShipmentItems.stream().map(ShipmentItem::getShipmentId).collect(Collectors.toSet());
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().in(ShipmentDO::getId, shipmentIdSet).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue()));
        Set<Long> availableShipmentIds = shipmentDOS.stream().map(ShipmentDO::getId).collect(Collectors.toSet());
        Set<ShipmentItem> availableShipmentItems = allShipmentItems.stream().filter(s -> availableShipmentIds.contains(s.getShipmentId())).collect(Collectors.toSet());
        // 已使用的库存数量缓存
        Map<Long, Integer> usedStockQuantityMap = availableShipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(s -> {return Objects.isNull(s.getRealLockQuantity()) ? 0 : s.getRealLockQuantity();})));
        // 执行计算
        newshipmentItems.forEach(s->{
            Integer changeQuantity = changeQuantityMap.get(s.getId());
            // 数量未发生变更不需要重新计算
            if (Objects.isNull(changeQuantity)||changeQuantity==0){
                return;
            }
            Map<String, Integer> startSaleQuantity = salePurchaseAndStockQuantityMap.get(s.getSaleContractItemId());
            Map<String,Integer> updateMap = new HashMap<>();
            if (CollUtil.isEmpty(startSaleQuantity)){
                throw exception(SALE_ITEM_DATA_NOT_FOUND);
            }
            Integer allPurchaseQuantity = startSaleQuantity.get(CommonDict.PURCHASE_QUANTITY);
            Integer allStockQuantity = startSaleQuantity.get(CommonDict.STOCK_QUANTITY);
            // 数量增加
            if (changeQuantity>0){
                // 剩余可用库存
                Integer availableStockQuantity = allStockQuantity-usedStockQuantityMap.getOrDefault(s.getSaleContractItemId(),0);
                if (availableStockQuantity>=changeQuantity){
                    // 销售可用库存扣减
                    updateMap.put(CommonDict.STOCK_QUANTITY,allStockQuantity-changeQuantity);
                    s.setRealLockQuantity(s.getRealLockQuantity()+changeQuantity);
                }else if (availableStockQuantity>0){
                    // 优先使用库存
                    s.setRealLockQuantity(availableStockQuantity);
                    BigDecimal realPurchaseQuantiy = NumUtil.sub(changeQuantity, availableStockQuantity);
                    if (realPurchaseQuantiy.intValue()>allPurchaseQuantity){
                        throw exception(SALE_PURCHASE_QUANTITY_NOT_ENOUGH);
                    }
                    s.setThisPurchaseQuantity(NumUtil.add(s.getThisPurchaseQuantity(),realPurchaseQuantiy));
                    updateMap.put(CommonDict.STOCK_QUANTITY,0);
                    updateMap.put(CommonDict.PURCHASE_QUANTITY,allPurchaseQuantity-realPurchaseQuantiy.intValue());
                }else if (availableStockQuantity==0){
                    // 全部使用采购
                    s.setThisPurchaseQuantity(NumUtil.add(s.getThisPurchaseQuantity(),changeQuantity));
                    updateMap.put(CommonDict.PURCHASE_QUANTITY,allPurchaseQuantity-changeQuantity);
                }
            }else { //数量减少
                int absChangeQuantity = Math.abs(changeQuantity);
                if (s.getThisPurchaseQuantity().intValue()>=absChangeQuantity){
                    // 采购数量足够扣减
                    s.setThisPurchaseQuantity(NumUtil.sub(s.getThisPurchaseQuantity(),absChangeQuantity));
                    updateMap.put(CommonDict.PURCHASE_QUANTITY,allPurchaseQuantity+changeQuantity);
                }else if (s.getThisPurchaseQuantity().intValue()>0){
                    // 采购数量不够扣减 则剩余部分扣减库存
                    s.setThisPurchaseQuantity(BigDecimal.ZERO);
                    BigDecimal usedLockQuantity = NumUtil.sub(absChangeQuantity, s.getThisPurchaseQuantity());
                    s.setRealLockQuantity(NumUtil.sub(s.getRealLockQuantity(),usedLockQuantity).intValue());
                    updateMap.put(CommonDict.STOCK_QUANTITY,allStockQuantity-usedLockQuantity.intValue());
                    updateMap.put(CommonDict.PURCHASE_QUANTITY,0);
                }else if (s.getThisPurchaseQuantity().intValue()==0){
                    // 全部扣减库存
                    s.setRealLockQuantity(NumUtil.sub(s.getRealLockQuantity(),changeQuantity).intValue());
                    updateMap.put(CommonDict.STOCK_QUANTITY,allStockQuantity-changeQuantity);
                    updateMap.put(CommonDict.PURCHASE_QUANTITY,0);
                }
            }
            salePurchaseAndStockQuantityMap.put(s.getSaleContractItemId(),startSaleQuantity);
        });
        return newshipmentItems;
    }

    private void validateItemQuantity(List<ShipmentItem> oldChildren,List<ShipmentItem> children){
        if (CollUtil.isEmpty(oldChildren)||CollUtil.isEmpty(children)){
            return;
        }
        List<Long> saleItemIds = children.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        Map<Long, Integer> unTransformShipmentedQuantityMap = saleContractApi.getUnTransformShipmentedQuantityMap(saleItemIds);
        Map<Long, Integer> oldShipmentQuantityMap = oldChildren.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        Map<Long, Integer> shipmentQuantityMap = children.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        Map<Long,Integer> addQuantityMap = new HashMap<>();
        Map<Long,Integer> subQuantityMap = new HashMap<>();
        shipmentQuantityMap.forEach((k,v)->{
            Integer oldQuantity = oldShipmentQuantityMap.get(k);
            if (Objects.isNull(oldQuantity)|| Objects.equals(v, oldQuantity)){
                return;
            }
            // 差值
            int differenceQuantity = oldQuantity - v;
            // 减少时回写已出运数量 相加
            if (differenceQuantity > 0) {
                addQuantityMap.merge(k,differenceQuantity,Integer::sum);
            }else {
                //增加时判断差值是否超过待出运数量
                int unTransformShipmentedQuantity = Objects.isNull(unTransformShipmentedQuantityMap.get(k)) ? 0 : unTransformShipmentedQuantityMap.get(k);
                if (Math.abs(differenceQuantity)>unTransformShipmentedQuantity){
                    // 可出运数量大于待转出运数量，不可进行变更
                    throw exception(SHIPMENT_QUANTITY_EXCEED_NOT_CHANGE);
                }else {
                    // 回写已出运数量 相减
                    subQuantityMap.merge(k,Math.abs(differenceQuantity),Integer::sum);
                }
            }
        });
        if (CollUtil.isNotEmpty(addQuantityMap)){
            saleContractApi.rollBackTransformShipmentedQuantityByChange(addQuantityMap,true);
        }
        if (CollUtil.isNotEmpty(subQuantityMap)){
            saleContractApi.rollBackTransformShipmentedQuantityByChange(subQuantityMap,false);
        }
    }
    @Override
    public String submitChangeTask(Long id, Long userId, String processDefinitionKey, Map<String, Object> variables) {
        String processInstanceId;
        if (CollUtil.isNotEmpty(variables)) {
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, id, variables, null);
        } else {
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, id);
        }
        updateChangeAuditStatus(id, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
        return processInstanceId;
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectById(auditableId);
        shipmentChange.setAuditStatus(auditStatus);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            shipmentChange.setProcessInstanceId(processInstanceId);
        }
        shipmentChangeMapper.updateById(shipmentChange);
        String shipmentCode = shipmentChange.getShipmentCode();
        // 仅审核通过回写变更
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            updateChangeResult(shipmentChange.getId());
        } else {
            shipmentMapper.update(new ShipmentDO().setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus()), new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getCode, shipmentCode));
        }
    }

    @Override
    public void updateChangeResult(Long changeId) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectById(changeId);
        if (Objects.isNull(shipmentChange)) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        List<ShipmentItem> children = shipmentChange.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            // 更新出运单明细
            ItemBaseMapperUtil.dealItemList(children, shipmentItemMapper);
        }
        List<AddSubItemDTO> addSubItemList = shipmentChange.getAddSubItemList();
        if (CollUtil.isNotEmpty(addSubItemList)) {
            // 更新加减项
            List<AddSubItemDTO> insertList = addSubItemList.stream().filter(s -> s.getChangeFlag() != null && s.getChangeFlag() == ChangeTypeEnum.ADDED.getType()).toList();
            List<AddSubItemDTO> updateList = addSubItemList.stream().filter(s -> s.getChangeFlag() != null && s.getChangeFlag() == ChangeTypeEnum.UPDATE.getType()).toList();
            List<Long> deleteList = addSubItemList.stream().filter(s -> s.getChangeFlag() != null && s.getChangeFlag() == ChangeTypeEnum.DELETED.getType()).map(AddSubItemDTO::getId).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                addSubItemApi.insertBatch(insertList);
            }
            if (CollUtil.isNotEmpty(deleteList)) {
                addSubItemApi.deleteBatchByIds(deleteList);
            }
        }
        List<TemporarySku> temporarySkuList = shipmentChange.getTemporarySkuList();
        if (CollUtil.isNotEmpty(temporarySkuList)) {
            // 更新临时产品
            ItemBaseMapperUtil.dealItemList(temporarySkuList, temporarySkuMapper);
        }
        List<ForwarderFeeDO> forwarderFeeList = shipmentChange.getForwarderFeeList();
        if (CollUtil.isNotEmpty(forwarderFeeList)) {
            // 更新船代费用
            ItemBaseMapperUtil.dealItemList(forwarderFeeList, forwarderFeeMapper);
        }
        ShipmentDO shipmentDO = ShipmentConvert.INSTANCE.convertShipmentDO(shipmentChange);
        shipmentDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
        // 更新出运单
        shipmentMapper.update(shipmentDO, new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getCode, shipmentChange.getShipmentCode()));
        // 回写销售合同转出运数量和待转出运明细
        List<ShipmentItem> shipmentItems = shipmentItemMapper.getShippingQuantity(shipmentDO.getCode());
        if (CollUtil.isNotEmpty(shipmentItems)) {
            Map<Long, Integer> shippingMap = shipmentItems.stream().collect(Collectors.toMap(ShipmentItem::getSaleContractItemId, ShipmentItem::getShippingQuantity, Integer::sum));
            Map<Long,Integer> updateMap = new HashMap<>(shippingMap);
            List<Long> saleItemIds = shipmentItems.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
            List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanItem>().in(ShipmentPlanItem::getSaleContractItemId, saleItemIds).ne(ShipmentPlanItem::getTransformShipmentFlag, BooleanEnum.YES.getValue()));
            if (CollUtil.isNotEmpty(shipmentPlanItems)) {
                List<Long> shipmentPlanIdList = shipmentPlanItems.stream().map(ShipmentPlanItem::getShipmentPlanId).toList();
                List<ShipmentPlanDO> shipmentPlanDOS = shipmentPlanMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanDO>().in(ShipmentPlanDO::getId, shipmentPlanIdList).eq(ShipmentPlanDO::getStatus,ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue()));
                if (CollUtil.isNotEmpty(shipmentPlanDOS)){
                    Set<Long> shipmentPlanIdSet = shipmentPlanDOS.stream().map(ShipmentPlanDO::getId).collect(Collectors.toSet());
                    shipmentPlanItems.stream().filter(s -> shipmentPlanIdSet.contains(s.getShipmentPlanId())).forEach(s -> {
                        Long saleContractItemId = s.getSaleContractItemId();
                        Integer shippingQuantity = Objects.isNull(s.getShippingQuantity()) ? 0 : s.getShippingQuantity();
                        Integer quantity = Objects.isNull(shippingMap.get(saleContractItemId))?0:shippingMap.get(saleContractItemId);
                        int unTransShippingQuantity = quantity + shippingQuantity;
                        updateMap.put(saleContractItemId, unTransShippingQuantity);
                    });
                }
            }
            // 更新已转出运数量
            saleContractApi.batchUpdateTransformShippedQuantity(updateMap);
            // 更新已转出运明细总数
            saleContractApi.updateShipmentTotalQuantity(shippingMap, false);
        }
    }

    @Override
    public String getProcessDefinitionKeyByBusinessId(Long id) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectById(id);
        if (Objects.isNull(shipmentChange)) {
            return null;
        }
        return shipmentChange.getModelKey();
    }

    @Override
    public PageResult<ChangeShipmentRespVO> getChangePage(ChangeShipmentPageReq pageReq) {
        PageResult<ShipmentChange> shipmentChangePageResult = shipmentChangeMapper.selectPage(pageReq);
        if (CollUtil.isEmpty(shipmentChangePageResult.getList())) {
            return PageResult.empty();
        }
        List<ChangeShipmentRespVO> changeShipmentRespVOList = ShipmentConvert.INSTANCE.convertChangeShipmentRespVOList(shipmentChangePageResult.getList());
        return new PageResult<ChangeShipmentRespVO>().setTotal(shipmentChangePageResult.getTotal()).setList(changeShipmentRespVOList);
    }

    @Override
    public ChangeShipmentRespVO getShipmentChange(Long id) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectById(id);
        if (Objects.isNull(shipmentChange)) {
            return null;
        }
        return ShipmentConvert.INSTANCE.convertChangeShipmentRespVO(shipmentChange);
    }

    @Override
    public ChangeShipmentRespVO getAuditShipmentChange(String id) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectOne(ShipmentChange::getProcessInstanceId, id);
        if (Objects.isNull(shipmentChange)) {
            return null;
        }
        return getShipmentChange(shipmentChange.getId());
    }


    /**
     * 转换出票通知
     *
     * @param shipmentDO           出运单
     * @param venderCode           供应商编号
     * @param purchaseContractCode 采购合同编号
     * @param simpleVenderRespMap  供应商精简缓存
     * @param shipmentItemList     采购合同跟单员缓存
     * @return 出票通知DTO
     */
    private InvoicingNoticesDTO transfromInvoicingNoticesDTO(ShipmentDO shipmentDO, String venderCode, String purchaseContractCode, Map<String, SimpleVenderResp> simpleVenderRespMap, List<ShipmentItem> shipmentItemList) {
        InvoicingNoticesDTO invoicingNoticesDTO = new InvoicingNoticesDTO();
        invoicingNoticesDTO.setCompanyId(shipmentDO.getForeignTradeCompanyId());
        invoicingNoticesDTO.setCompanyName(shipmentDO.getForeignTradeCompanyName());
        // 跟单员默认当前操作员
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept inputUser = adminUserApi.getUserDeptByUserId(loginUserId);
        invoicingNoticesDTO.setInputUser(inputUser);
        invoicingNoticesDTO.setInputDate(LocalDateTime.now());
        // 跟单员默认采购合同中跟单员
        if (CollUtil.isNotEmpty(shipmentItemList)) {
            invoicingNoticesDTO.setManager(shipmentItemList.get(0).getManager());
        }
        invoicingNoticesDTO.setVenderCode(venderCode);
        if (CollUtil.isNotEmpty(simpleVenderRespMap)) {
            SimpleVenderResp simpleVenderResp = simpleVenderRespMap.get(venderCode);
            if (Objects.nonNull(simpleVenderResp)) {
                invoicingNoticesDTO.setVenderName(simpleVenderResp.getName());
            }
        }
        invoicingNoticesDTO.setShipmentCode(shipmentDO.getCode());
        invoicingNoticesDTO.setShipInvoiceCode(shipmentDO.getInvoiceCode());
        // 出运日期使用出运明细的实际开船日期
        invoicingNoticesDTO.setShipDate(shipmentDO.getShipDate());
        invoicingNoticesDTO.setPrintFlag(PrintStatusEnum.NOT_PRINTED.getStatus());
        invoicingNoticesDTO.setPurOrderCode(purchaseContractCode);
        invoicingNoticesDTO.setManuallyFlag(BooleanEnum.NO.getValue());
        invoicingNoticesDTO.setShipInvoiceCode(shipmentDO.getInvoiceCode());
        return invoicingNoticesDTO;
    }

    /**
     * 批量转换开票通知明细
     *
     * @param shipmentItemList 出运单明细列表
     * @param shipmentDO       出运单信息
     * @return 开票通知明细
     */
    private List<InvoicingNoticesItemDTO> transformInvoicingNoticesItemDTOList(List<ShipmentItem> shipmentItemList, ShipmentDO shipmentDO,Map<Long, ShipmentToInvoiceSaveReq> shipmentToInvoiceSaveReqMap) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return Collections.emptyList();
        }
        List<Long> idList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        Map<Long, Long> map = purchaseContractApi.getPurchaseItemIdBySaleContractItemIds(idList);
        Map<Long, JsonAmount> realPurchasePriceMap = saleContractApi.getRealPurchasePriceMapByItemIds(idList);
        Set<String> purchaseCodeList = shipmentItemList.stream().filter(s -> PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())).map(ShipmentItem::getPurchaseContractCode).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, JsonAmount> purchasePricCache = purchaseContractApi.getiPurchasePricCache(purchaseCodeList);
        return CollectionUtils.convertList(shipmentItemList, s -> {
            InvoicingNoticesItemDTO invoicingNoticesItemDTO = new InvoicingNoticesItemDTO();
            invoicingNoticesItemDTO.setShipmentItemId(s.getId());
            invoicingNoticesItemDTO.setPurchaseContractCode(s.getPurchaseContractCode());
            invoicingNoticesItemDTO.setSaleContractItemId(s.getSaleContractItemId());
            invoicingNoticesItemDTO.setSkuCode(s.getSkuCode());
            invoicingNoticesItemDTO.setCskuCode(s.getCskuCode());
            invoicingNoticesItemDTO.setDeclarationQuantity(s.getShippingQuantity());
            invoicingNoticesItemDTO.setHsCode(s.getHsCode());
            invoicingNoticesItemDTO.setHsMeasureUnit(s.getHsMeasureUnit());
            invoicingNoticesItemDTO.setInvoicSkuName(s.getDeclarationName());
            invoicingNoticesItemDTO.setSpecificationList(s.getSpecificationList());
            invoicingNoticesItemDTO.setQtyPerOuterbox(s.getQtyPerOuterbox());
            invoicingNoticesItemDTO.setSplitBoxFlag(s.getSplitBoxFlag());
            JsonAmount jsonPurchaseWithTaxPrice = new JsonAmount();
            if (CollUtil.isNotEmpty(realPurchasePriceMap)&&PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())){
                jsonPurchaseWithTaxPrice = realPurchasePriceMap.get(s.getSaleContractItemId());
            }
            if (CollUtil.isNotEmpty(purchasePricCache)&&PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel())){
                jsonPurchaseWithTaxPrice = purchasePricCache.get(s.getSaleContractItemId());
            }
            BigDecimal purchaseWithTaxPrice = BigDecimal.ZERO;
            if (Objects.nonNull(jsonPurchaseWithTaxPrice)) {
                purchaseWithTaxPrice = jsonPurchaseWithTaxPrice.getAmount();
            }
            // 转换开票数量
//            transformInvoiceQuantity(invoicingNoticesItemDTO, s, purchaseWithTaxPrice);
            invoicingNoticesItemDTO.setInvoicPrice(purchaseWithTaxPrice);
            ShipmentToInvoiceSaveReq shipmentToInvoiceSaveReq = shipmentToInvoiceSaveReqMap.get(s.getId());
            invoicingNoticesItemDTO.setInvoicNoticesQuantity(shipmentToInvoiceSaveReq.getInvoiceQuantity());
            invoicingNoticesItemDTO.setHsMeasureUnit(shipmentToInvoiceSaveReq.getHsMeasureUnit());
            invoicingNoticesItemDTO.setDeclarationQuantity(s.getDeclarationQuantity());
            invoicingNoticesItemDTO.setPurchaseWithTaxPrice(jsonPurchaseWithTaxPrice);
            invoicingNoticesItemDTO.setInveicRegisteredStatus(RegisteredStatusEnum.NOT_REGISTERED.getValue());
            invoicingNoticesItemDTO.setPurchaseTotalQuantity(s.getPurchaseTotalQuantity());
            invoicingNoticesItemDTO.setPurchaseCurrency(s.getPurchaseCurrency());
            invoicingNoticesItemDTO.setCustCode(s.getCustCode());
            invoicingNoticesItemDTO.setCustName(s.getCustName());
            invoicingNoticesItemDTO.setSaleContractCode(s.getSaleContractCode());
            invoicingNoticesItemDTO.setManuallyFlag(BooleanEnum.NO.getValue());
            invoicingNoticesItemDTO.setInvoicSkuName(s.getDeclarationName());
            //采购合同明细主键  出运明细item->销售item->采购item
            if (CollUtil.isNotEmpty(map)) {
                invoicingNoticesItemDTO.setPurchaseContractItemId(map.get(s.getSaleContractItemId()));
            }

            invoicingNoticesItemDTO.setShipInvoiceCode(shipmentDO.getInvoiceCode());

            // 关联来源编号
            invoicingNoticesItemDTO.setSourceUniqueCode(s.getUniqueCode());
            return invoicingNoticesItemDTO;
        });
    }

    /**
     * 将kg跟g根据海关计量单位转换
     *
     * @param invoicingNoticesItemDTO 开票通知DTO
     * @param shipmentItem            出运单明细
     * @param purchaseWithTaxPrice    采购价
     */
    private void transformInvoiceQuantity(InvoicingNoticesItemDTO invoicingNoticesItemDTO, ShipmentItem shipmentItem, BigDecimal purchaseWithTaxPrice) {
        //海关计量单位为"千克" 开票单价和开票数量计算
        String hsMeasureUnit = shipmentItem.getHsMeasureUnit();
        Integer declarationQuantity = shipmentItem.getThisPurchaseQuantity().intValue();
        //  本次开票金额 = 报关数量 * 采购单价
        BigDecimal invoiceThisPrice = NumUtil.mul(declarationQuantity, purchaseWithTaxPrice);
        if (StrUtil.isNotEmpty(hsMeasureUnit) && (hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue()) || hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()))) {
            // 外箱总净重
            JsonWeight totalNetWeight = CalcSpecificationUtil.calcSpecificationTotalNetweight(shipmentItem.getSpecificationList());
            if (Objects.isNull(totalNetWeight)) {
                return;
            }
            String unit = totalNetWeight.getUnit();
            BigDecimal weight = totalNetWeight.getWeight();
            if (hsMeasureUnit.equals(HsMeasureUnitEnum.KG.getValue()) && CalculationDict.GRAM.equals(unit)) {
                //海关计量单位为kg 外箱总净重单位为g，外箱总净重/1000
                weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
            } else if (hsMeasureUnit.equals(HsMeasureUnitEnum.KE.getValue()) && CalculationDict.KILOGRAM.equals(unit)) {
                //海关计量单位为g 外箱总净重单位为kg，外箱总净重*1000
                weight = NumUtil.mul(weight, CalculationDict.ONE_THOUSAND);
            }
            // 开票单价 =（采购单价*出运数量）/ 外箱总净重
            invoicingNoticesItemDTO.setInvoicPrice(NumUtil.div(invoiceThisPrice, weight));
            // 开票数量=（采购单价*出运数量）/开票单价
            invoicingNoticesItemDTO.setInvoicNoticesQuantity(weight);
        } else {
            invoicingNoticesItemDTO.setInvoicNoticesQuantity(BigDecimal.valueOf(declarationQuantity));
            invoicingNoticesItemDTO.setInvoicPrice(purchaseWithTaxPrice);
        }
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, Long id) {
        ShipmentDO shipmentDO = validateShipmentExists(id);
        shipmentDO.setConfirmFlag(confirmFlag);
        shipmentMapper.update(shipmentDO, new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getId, id));
        // 来源变更单确认状态改为已确认
        // 来源单据为销售合同
        saleContractApi.updateChangeConfirmFlag(EffectRangeEnum.DMS.getValue(), shipmentDO.getCode());
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, String code) {
        ShipmentDO shipmentDO = shipmentMapper.selectOne(ShipmentDO::getCode, code);
        shipmentDO.setConfirmFlag(confirmFlag);
        shipmentMapper.update(shipmentDO, new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getCode, code));
    }

    @Override
    public void updateVender(Long venderId, String venderName, String venderCode) {
        Integer[] statusList = {
                ShippingStatusEnum.READY_FOR_SHIPMENT.getValue(),
                ShippingStatusEnum.SHIPPED_IN_BATCH.getValue(),
                ShippingStatusEnum.SHIPPED.getValue()
        };
        List<ShipmentDO> shipmentList = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().in(ShipmentDO::getStatus, (Object[]) statusList));
        List<Long> shipmentIdlist = shipmentList.stream().map(ShipmentDO::getId).distinct().toList();
        if (CollUtil.isNotEmpty(shipmentIdlist)) {
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setVenderId(venderId);
            shipmentItem.setVenderName(venderName);
            shipmentItemMapper.update(shipmentItem, new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getVenderCode, venderCode).in(ShipmentItem::getShipmentId, shipmentIdlist));
        }
    }

    @Override
    public void updateCust(Long custId, String custName, String custCode) {
        Integer[] statusList = {
                ShippingStatusEnum.READY_FOR_SHIPMENT.getValue(),
                ShippingStatusEnum.SHIPPED_IN_BATCH.getValue(),
                ShippingStatusEnum.SHIPPED.getValue()
        };
        List<ShipmentDO> shipmentList = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().in(ShipmentDO::getStatus, (Object[]) statusList));
        List<Long> shipmentIdlist = shipmentList.stream().map(ShipmentDO::getId).distinct().toList();
        if (CollUtil.isNotEmpty(shipmentIdlist)) {
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setCustId(custId);
            shipmentItem.setCustName(custName);
            shipmentItemMapper.update(shipmentItem, new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getCustCode, custCode).in(ShipmentItem::getShipmentId, shipmentIdlist));
        }
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceListByShipmentId(Long id) {
        ShipmentDO shipmentDO = validateShipmentExists(id);
        String code = shipmentDO.getCode();
        List<ConfirmSourceEntity> confirmSourceList = new ArrayList<>();
        // 获取销售变更来源信息
        List<ConfirmSourceEntity> custList = custApi.getConfirmSourceList(code, EffectRangeEnum.DMS.getValue());
        if (CollUtil.isNotEmpty(custList)) {
            confirmSourceList.addAll(custList);
        }
        List<ConfirmSourceEntity> venderList = venderApi.getConfirmSourceList(code, EffectRangeEnum.DMS.getValue());
        if (CollUtil.isNotEmpty(venderList)) {
            confirmSourceList.addAll(venderList);
        }
        List<ConfirmSourceEntity> skuList = skuApi.getConfirmSourceList(code, EffectRangeEnum.DMS.getValue());
        if (CollUtil.isNotEmpty(skuList)) {
            confirmSourceList.addAll(skuList);
        }
        List<ConfirmSourceEntity> saleConfirmList = saleContractApi.getConfirmSourceList(code, EffectRangeEnum.DMS.getValue());
        if (CollUtil.isNotEmpty(saleConfirmList)) {
            confirmSourceList.addAll(saleConfirmList);
        }
        List<ConfirmSourceEntity> purchaseContractConfirmList = purchaseContractApi.getConfirmSourceList(code, EffectRangeEnum.DMS.getValue());
        if (CollUtil.isNotEmpty(purchaseContractConfirmList)) {
            confirmSourceList.addAll(purchaseContractConfirmList);
        }
        return confirmSourceList;
    }

    @Override
    public Long getOrderNumBySaleContractCode(String code) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getShipmentId).eq(ShipmentItem::getSaleContractCode, code));
        if (CollUtil.isEmpty(shipmentItemList)) {
            return CalculationDict.LONG_ZERO;
        }
        return shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().count();
    }

    @Override
    public Long getPlanNumBySaleContractCode(String code) {
        List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanItemMapper.selectList(new LambdaQueryWrapperX<ShipmentPlanItem>().select(ShipmentPlanItem::getShipmentPlanId).eq(ShipmentPlanItem::getSaleContractCode, code));
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return CalculationDict.LONG_ZERO;
        }
        return shipmentPlanItemList.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().count();
    }

    @Override
    public RelatedShipmentRespVO getRelatedNum(Long id) {
        RelatedShipmentRespVO relatedShipmentRespVO = new RelatedShipmentRespVO();
        ShipmentDO shipmentDO = shipmentMapper.selectById(id);
        if (Objects.isNull(shipmentDO)) {
            throw new ServiceException(SHIPMENT_NOT_EXISTS);
        }
        // 销售合同 采购合同
        CompletableFuture<Tuple> contractFuture = CompletableFuture.supplyAsync(() -> {
            List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getSaleContractCode, ShipmentItem::getPurchaseContractCode).eq(ShipmentItem::getShipmentId, id));
            if (CollUtil.isEmpty(shipmentItemList)) {
                return new Tuple(CalculationDict.LONG_ZERO, CalculationDict.LONG_ZERO);
            }
            long saleContractNum = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().count();
            long purchaseContractNum = shipmentItemList.stream().map(ShipmentItem::getPurchaseContractCode).distinct().count();
            return new Tuple(saleContractNum, purchaseContractNum);
        });
        // 出运计划
        Long shipmentPlanNum = Objects.nonNull(shipmentDO.getShipmentPlanCode()) ? CalculationDict.LONG_ONE : CalculationDict.LONG_ZERO;
        // 拉柜通知单
        // TODO 等孙斌出库通知单做完再添加逻辑
        // 报关单
        // 结汇单

        // 商检单


        return relatedShipmentRespVO;
    }

    @Override
    public ContainerMidVO getContainerMid(List<Long> ids,Integer shippedAddress) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(shipmentItemList)) {
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        // 过滤已转出运明细
        shipmentItemList = shipmentItemList.stream().filter(x -> x.getConverNoticeFlag().intValue() == BooleanEnum.NO.getValue()).toList();
        if (ShippedAddressEnum.FACTORY.getValue().equals(shippedAddress)){
            shipmentItemList = shipmentItemList.stream().filter(x -> ShippedAddressEnum.FACTORY.getValue().equals(x.getShippedAddress())).toList();
        }
        if (CollUtil.isEmpty(shipmentItemList)) {
            throw exception(SHIPMENT_ITEM_ALL_CONVERTED);
        }
        // 获取出运单提单号
        String billLadingNum = CommonDict.EMPTY_STR;
        Optional<Long> firstOpt = shipmentItemList.stream().map(ShipmentItem::getShipmentId).findFirst();
        if (firstOpt.isEmpty()) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        Long shipmentId = firstOpt.get();
        ShipmentDO shipmentDO = shipmentMapper.selectById(shipmentId);
        if (Objects.nonNull(shipmentDO)) {
            billLadingNum = shipmentDO.getBillLadingNum();
        }
        ContainerMidVO containerMidVO = new ContainerMidVO();
        List<String> checkSaleContractCodeList = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        List<String> skuCodeList = shipmentItemList.stream().map(ShipmentItem::getSkuCode).distinct().toList();
        if (CollUtil.isEmpty(checkSaleContractCodeList)) {
            throw exception(SALE_CONTRACT_CODE_EMPTY);
        }
        // 若存在原始外销合同，则使用原始外销合同关联
        List<String> saleContractCodeList = new ArrayList<>();
        shipmentItemList.forEach(x -> {
            String sourceSaleContractCode = x.getSourceSaleContractCode();
            if (StringUtils.isNotBlank(sourceSaleContractCode)) {
                if (!saleContractCodeList.contains(sourceSaleContractCode)) {
                    saleContractCodeList.add(sourceSaleContractCode);
                }
            } else {
                throw exception(SOURCE_SALE_CONTRACT_CODE_EMPTY);
            }
        });
        containerMidVO.setShipmentCode(shipmentDO.getCode());
        containerMidVO.setInvoiceCode(shipmentDO.getInvoiceCode());
        containerMidVO.setLinkCodeList(shipmentDO.getLinkCodeList());
        containerMidVO.setCompanyId(shipmentDO.getForeignTradeCompanyId());
        // 查询锁定库存
        List<StockLockRespVO> stockLockRespVOList = saleContractCodeList.stream().flatMap(s -> iStockApi.listStockLock(new QueryStockReqVO().setSaleContractCode(s)).stream()).toList();
        List<JsonCompanyPath> companyPathList = saleContractApi.getCompanyPathBySaleContractCodeList(saleContractCodeList);
        List<Long> lastCompanyIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(companyPathList)) {
            companyPathList.forEach(item -> {
                JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(item);
                if (Objects.nonNull(lastCompanyPath)) {
                    lastCompanyIds.add(lastCompanyPath.getId());
                }
            });
        }
        // 获取加工主体
        List<Long> producedCompanyIds = companyApi.listProducedCompany().stream().map(SimpleCompanyDTO::getId).distinct().toList();
        lastCompanyIds.addAll(producedCompanyIds);
        // 查询库存明细
        List<StockDTO> stockDTOList = iStockApi.getStockDTOBySaleContractCodesAndLastCompanyIds(saleContractCodeList, lastCompanyIds);
        List<String> shipmentItemCodeList = shipmentItemList.stream().map(ShipmentItem::getSkuCode).toList();
        Map<String, SkuNameDTO> skuNameCache = skuApi.getSkuNameCacheByCodeList(shipmentItemCodeList);
        // 如果没有库存则直接返回明细信息
        if (CollUtil.isEmpty(stockLockRespVOList) && CollUtil.isEmpty(stockDTOList)) {
            containerMidVO.setChildren(ShipmentConvert.INSTANCE.convertStockChildVOByItems(shipmentItemList, skuNameCache));
        } else {
            //锁定库存
            Map<String, List<StockLockRespVO>> stockLockRespVOMap = new HashMap<>();
            // 库存明细
            Map<String, List<StockDTO>> stockDTOMap = new HashMap<>();
            Map<Long, StockDTO> stockMap = new HashMap<>();
            Map<String, Integer> skuTypeMap = skuApi.getSkuTypeMap(skuCodeList);
            if (CollUtil.isNotEmpty(stockLockRespVOList)) {
                // 自主产品锁定库存替换为客户产品库存
                Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(shipmentItemCodeList);
                stockLockRespVOList.stream().forEach(x -> {
                    skuDTOMap.forEach((k, v) -> {
                        String skuCode = x.getSkuCode();
                        String sourceCode = v.getSourceCode();
                        if (skuCode.equals(sourceCode)) {
                            x.setSkuCode(v.getCode());
                        }
                    });
                });
                stockLockRespVOMap = stockLockRespVOList.stream().collect(Collectors.groupingBy(s -> s.getSaleContractCode() + s.getSaleContractItemId()));
                List<Long> stockIdList = stockLockRespVOList.stream().map(StockLockRespVO::getStockId).distinct().toList();
                List<StockDTO> stockDTOS = iStockApi.getStockDTOByIdList(stockIdList);
                if (CollUtil.isNotEmpty(stockDTOS)) {
                    stockMap = stockDTOS.stream().collect(Collectors.toMap(StockDTO::getId, s -> s));
                }
            }
            if (CollUtil.isNotEmpty(stockDTOList)) {
                stockDTOMap = stockDTOList.stream().collect(Collectors.groupingBy(s -> s.getSaleContractCode() + s.getSkuCode() + s.getPurchaseContractCode()));
            }
            containerMidVO.setReferenceNumber(billLadingNum);
            containerMidVO.setChildren(ShipmentConvert.INSTANCE.convertStockChildVOListByShipmentItem(stockLockRespVOMap, stockDTOMap, shipmentItemList, skuNameCache, billLadingNum, stockMap, skuTypeMap));
        }
        //是否可自动加工标识赋值，组合产品拆分采购的可自动加工
        List<PurchasePlanItemDTO> purchasePlanItemList = purchasePlanApi.getPurchasePlanItemListBySaleContractCodeList(saleContractCodeList);
        Map<String, List<PurchasePlanItemDTO>> purchasePlanItemMap = new HashMap<>();
        if (CollUtil.isNotEmpty(purchasePlanItemList)){
            purchasePlanItemMap = purchasePlanItemList.stream().collect(Collectors.groupingBy(s -> s.getSaleContractCode() + s.getSkuCode()));
        }
        List<StockChildVO> children = containerMidVO.getChildren();
        Map<String, List<PurchasePlanItemDTO>> finalPurchasePlanItemMap = purchasePlanItemMap;
        children.forEach(cm -> {
            if (CollUtil.isEmpty(finalPurchasePlanItemMap)){
                cm.setManufactureFlag(BooleanEnum.NO.getValue());
                return;
            }
            List<PurchasePlanItemDTO> purchasePlanItemDTOList = finalPurchasePlanItemMap.get(cm.getSaleContractCode() + cm.getSkuCode());
            if (CollUtil.isEmpty(purchasePlanItemDTOList)){
                cm.setManufactureFlag(BooleanEnum.NO.getValue());
                return;
            }
            List<PurchasePlanItemDTO> combinePlanItemDTOList = purchasePlanItemDTOList.stream()
                    .filter(x -> x.getPurchaseModel().intValue() == PurchaseModelEnum.COMBINE.getCode()
                            && x.getSkuType().intValue() == SkuTypeEnum.PRODUCT_MIX.getValue()).toList();
            if (CollUtil.isEmpty(combinePlanItemDTOList)) {
                cm.setManufactureFlag(BooleanEnum.NO.getValue());
            } else {
                cm.setManufactureFlag(BooleanEnum.YES.getValue());
            }
        });
        List<Long> shipmentItemIds = children.stream().map(StockChildVO::getShipmentItemId).distinct().toList();
        containerMidVO.setShipmentItemIdList(shipmentItemIds);
        return containerMidVO;
    }

    @Override
    public PageResult<StockNoticeRespDTO> getNoticePage(StockNoticePageReqDTO pageReqDTO) {
        return iStockNoticeApi.getNoticePage(pageReqDTO);
    }

    public boolean validateStock(List<SkuBomDTO> skuBomDTOList, Integer needQuantity, List<StockDTO> stockDTOList) {
        if (skuBomDTOList != null && !skuBomDTOList.isEmpty()) {
            if (stockDTOList == null || stockDTOList.isEmpty()) {
                return true;
            }
            //子产品可加工主产品数量
            AtomicReference<Integer> bomToSkuNum = new AtomicReference<>(0);
            skuBomDTOList.forEach(sbd -> {
                List<StockDTO> stockSkuBomList = stockDTOList.stream().filter(i -> {
                    List<String> ownBrandSkuCodeList = sbd.getOwnBrandSkuCodeList();
                    if (CollUtil.isEmpty(ownBrandSkuCodeList)){
                        return i.getSkuCode().equals(sbd.getSkuCode());
                    }else {
                        return ownBrandSkuCodeList.contains(i.getSkuCode());
                    }
                }).toList();
                AtomicReference<Integer> bomToSkuNumTmp = new AtomicReference<>(0);
                stockSkuBomList.forEach(skb -> {
                    bomToSkuNumTmp.set(bomToSkuNumTmp.get() + NumUtil.div(skb.getAvailableQuantity(), sbd.getQty()).intValue());
                });
                if (bomToSkuNumTmp.get().equals(0)) {
                    bomToSkuNum.set(0);
                } else {
                    if (bomToSkuNum.get() == 0 || bomToSkuNum.get() < bomToSkuNumTmp.get()) {
                        bomToSkuNum.set(bomToSkuNumTmp.get());
                    }
                }
            });
            //需要加工数量小于子产品可加工主产品数量报错
            return needQuantity > bomToSkuNum.get();
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createNotice(ContainerMidVO containerMidVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        StockNoticeSaveReqDTO stockNoticeSaveReqDTO = new StockNoticeSaveReqDTO();
        stockNoticeSaveReqDTO.setShipmentType(containerMidVO.getShipmentType());
        stockNoticeSaveReqDTO.setFactoryOutboundFlag(containerMidVO.getFactoryOutboundFlag());
        List<StockChildVO> children = containerMidVO.getChildren();
        // 合并相同产品明细出运数量
        Set<String> sourceStockBatchCodeSet = new HashSet<>();
        children.forEach(s -> {
            List<BatchChildVO> childVOList = s.getChildren();
            if (CollUtil.isEmpty(childVOList)) {
                return;
            }
            childVOList.forEach(c -> {
                String batchCode = c.getBatchCode();
                if (StrUtil.isNotEmpty(batchCode)) {
                    sourceStockBatchCodeSet.add(batchCode);
                }
            });
        });
        Map<String, Integer> batchUserQuantityMap = children.stream().map(StockChildVO::getChildren).flatMap(List::stream).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(BatchChildVO::getBatchCode, Collectors.summingInt(BatchChildVO::getUsedQuantity)));
        //销售合同包含未转出库单产品记录
        List<String> skuCodeList = children.stream().map(StockChildVO::getSkuCode).distinct().toList();
        List<String> saleContractCodeList = children.stream().map(StockChildVO::getSaleContractCode).distinct().toList();
//        if (!saleContractCodeList.isEmpty()) {
//            List<StockNoticeRespDTO> StockNoticeRespDTOList = iStockNoticeApi.getStockNoticeListBySaleContractList(saleContractCodeList);
//            if (!StockNoticeRespDTOList.isEmpty()) {
//                //查询转单中，未转，部分转出库通知单信息
//                List<StockNoticeRespDTO> StockNoticeRespDTOUnTurn = StockNoticeRespDTOList.stream().filter(i -> i.getNoticeStatus().equals(NoticeStatusEnum.PART_CONVERT.getValue()) || i.getNoticeStatus().equals(NoticeStatusEnum.UN_CONVERT.getValue()) || i.getNoticeStatus().equals(NoticeStatusEnum.IN_CONVERT.getValue())).toList();
//                if (!StockNoticeRespDTOUnTurn.isEmpty()) {
//                    StockNoticeRespDTOUnTurn.forEach(srd -> {
//                        if (!srd.getChildren().isEmpty()) {
//                            List<StockNoticeItemRespDTO> tempList = srd.getChildren().stream().filter(st ->
//                                    skuCodeList.contains(st.getSkuCode())
//                            ).toList();
//                            if (!tempList.isEmpty()) {
//                                throw exception(UNTURN_SALE_SKU);
//                            }
//                        }
//                    });
//                }
//            }
//        }
        List<String> purchaseContractCodeList = children.stream().map(StockChildVO::getPurchaseContractCode).distinct().toList();
        // TODO 待优化
        Set<String> batchCodeSet = new HashSet<>();
        //自动生产完成
        List<StockDTO> stockDTOList = iStockApi.getStockDTOBySaleContractCodes(saleContractCodeList);
        //自动生产完成lock表
        List<StockLockRespVO> stockLockRespVOList = iStockApi.getStockLockRespVOBySaleContractCodes(saleContractCodeList);
        if (CollUtil.isNotEmpty(stockLockRespVOList)) {
            stockLockRespVOList.forEach(s -> batchCodeSet.add(s.getBatchCode()));
            List<Long> lockStockIds = stockLockRespVOList.stream().map(StockLockRespVO::getStockId).distinct().toList();
            List<StockDTO> lockStockDTOList = iStockApi.getStockDTOByIdList(lockStockIds);
            if (CollUtil.isNotEmpty(lockStockDTOList)){
                stockDTOList.addAll(lockStockDTOList);
            }
        }
        if (CollUtil.isNotEmpty(stockDTOList)) {
            Map<Long, List<StockDTO>> stockDTOMap = new HashMap<>();
            stockDTOList.forEach(s -> {
                batchCodeSet.add(s.getBatchCode());
                // 将s追加到stockDTOMap的value中
                stockDTOMap.computeIfAbsent(s.getPurchaseContractId(), k -> new ArrayList<>()).add(s);
            });
            if (CollUtil.isNotEmpty(stockDTOMap)) {
                Map<String, Integer> skuProductingQuantityMap = children.stream().collect(Collectors.groupingBy(StockChildVO::getSkuCode, Collectors.summingInt(s -> {
                    Integer shippingQuantity = s.getShippingQuantity();
                    List<BatchChildVO> shippChild = s.getChildren();
                    if (CollUtil.isEmpty(shippChild)) {
                        return shippingQuantity;
                    }
                    return shippingQuantity - shippChild.stream().map(BatchChildVO::getUsedQuantity).reduce(Integer::sum).orElse(0);
                })));
                Map<String, List<SkuBomDTO>> sonSkuMap = skuApi.getSonSkuMapBySkuCode(new ArrayList<>(skuProductingQuantityMap.keySet()));
                Map<String, Integer> sonSkuUsedQuantityMap = new HashMap<>();
                if (CollUtil.isNotEmpty(sonSkuMap)) {
                    sonSkuMap.forEach((skuCode, skuBomDTOList) -> {
                        Integer parentQuantity = skuProductingQuantityMap.get(skuCode);
                        if (Objects.isNull(parentQuantity)) {
                            return;
                        }
                        skuBomDTOList.forEach(skuBomDTO -> {
                            Integer qty = skuBomDTO.getQty();
                            if (Objects.isNull(qty) || qty == 0) {
                                return;
                            }
                            int sonUsedQuantity = parentQuantity * qty;
                            sonSkuUsedQuantityMap.put(skuBomDTO.getSkuCode(), sonUsedQuantity);
                        });
                    });
                }
                stockDTOMap.forEach((k, v) -> {
                    List<StockDTO> producingStockDTOList = stockDTOMap.get(k).stream().filter(i -> i.getProducingQuantity() > 0).toList();
                    if (CollUtil.isNotEmpty(producingStockDTOList)) {
                        List<String> batchCodeList = producingStockDTOList.stream().map(StockDTO::getBatchCode).distinct().toList();
                        CompleteOrderReqDTO completeOrderReqDTO = new CompleteOrderReqDTO().setContractId(k).setBatchCodeList(batchCodeList).setDoneTime(LocalDateTime.now());
                        if (CollUtil.isNotEmpty(batchUserQuantityMap)) {
                            Map<String, Integer> usedQuantityMap = producingStockDTOList.stream().collect(Collectors.toMap(StockDTO::getBatchCode, s -> {
                                Integer usedQuantitiy = Objects.isNull(batchUserQuantityMap.get(s.getBatchCode())) ? 0 : batchUserQuantityMap.get(s.getBatchCode());
                                Integer availableQuantity = Objects.isNull(s.getAvailableQuantity()) ? 0 : s.getAvailableQuantity();
                                Integer cabinetQuantity = Objects.isNull(s.getCabinetQuantity()) ? 0 : s.getCabinetQuantity();
                                // 实际使用的在制数量
                                Integer productingQuantity = Math.max(usedQuantitiy-(availableQuantity - cabinetQuantity), 0);
                                if (productingQuantity == 0) {
                                    // 需要使用子产品总数
                                    Integer needSonQuantity = Objects.isNull(sonSkuUsedQuantityMap.get(s.getSkuCode())) ? 0 : sonSkuUsedQuantityMap.get(s.getSkuCode());
                                    if (needSonQuantity - s.getProducingQuantity() >0){
                                        productingQuantity = s.getProducingQuantity();
                                        sonSkuUsedQuantityMap.put(s.getSkuCode(), needSonQuantity - s.getProducingQuantity());
                                    }else {
                                        productingQuantity = needSonQuantity;
                                    }
                                }
                                return productingQuantity;
                            }, (x, y) -> x));
                            if (CollUtil.isNotEmpty(usedQuantityMap)) {
                                completeOrderReqDTO.setUsedQuantityMap(usedQuantityMap);
                            }
                        }
                        purchaseContractApi.completeOrderTask(completeOrderReqDTO);
                    }
                });
            }
        }
        // 重新查可用库存
        List<StockDTO> newStockDTOList = iStockApi.getStockDTOByBatchCodes(batchCodeSet);
        List<PurchasePlanItemDTO> purchasePlanItemList = purchasePlanApi.getPurchasePlanItemListBySaleContractCodeList(saleContractCodeList);
        // 获取出运明细
        List<Long> shipmentItemIdList = children.stream().map(StockChildVO::getShipmentItemId).distinct().toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectBatchIds(shipmentItemIdList);
        Map<Long, ShipmentItem> shipmentItemMap = shipmentItems.stream().collect(Collectors.toMap(ShipmentItem::getId, s -> s, (x, y) -> x));
        children.forEach(cmv -> {
            // 采购合同信息
            //Integer manufactureQuantity = cmv.getShippingQuantity() - cmv.getAvailableCabinetQuantity();
            Integer usedQuantitySum = cmv.getChildren().stream().map(BatchChildVO::getUsedQuantity).filter(Objects::nonNull).reduce(0, Integer::sum);
            Integer manufactureQuantity = cmv.getShippingQuantity() - usedQuantitySum;
            if (manufactureQuantity > 0) {
                if (CollUtil.isEmpty(purchasePlanItemList)) {
                    throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
                }
                List<PurchasePlanItemDTO> purchasePlanItemDTOList = purchasePlanItemList.stream().filter(i -> i.getSkuCode().equals(cmv.getSkuCode())).toList();
                if (CollUtil.isEmpty(purchasePlanItemDTOList)) {
                    throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
                }
                // 过滤出组合产品并拆分采购的一、二级产品列表
                List<PurchasePlanItemDTO> combinePlanItemDTOList = purchasePlanItemDTOList.stream()
                        .filter(x -> x.getPurchaseModel().intValue() == PurchaseModelEnum.COMBINE.getCode()
                                && x.getSkuType().intValue() == SkuTypeEnum.PRODUCT_MIX.getValue()).toList();
                if (CollUtil.isEmpty(combinePlanItemDTOList)) {
                    throw exception(GEN_SKU_STOCK_NOT_ENOUGH,cmv.getSkuCode());
                }
                // 校验子产品库存
                validateChildSkuStock(cmv.getSkuCode(), manufactureQuantity, newStockDTOList);
                List<ManufactureSkuReqVO> manufactureSkuReqVOList = new ArrayList<>();
                ManufactureSkuReqVO manufactureSkuReqVO = new ManufactureSkuReqVO();
                manufactureSkuReqVO.setSkuCode(cmv.getSkuCode());
                manufactureSkuReqVO.setProcessingNum(manufactureQuantity);
                manufactureSkuReqVO.setSaleContractItemId(cmv.getSaleContractItemId());
                manufactureSkuReqVOList.add(manufactureSkuReqVO);
                //自动加工
                List<String> addBatchCodeList = batchManufactureSku(manufactureSkuReqVOList);
                List<StockDTO> stockDTOBySaleContractCodes = iStockApi.getStockDTOBySaleContractCodes(saleContractCodeList);
                //TODO  组装好的加工产品库存信息
                if (CollUtil.isNotEmpty(stockDTOBySaleContractCodes)) {
                    Map<String, List<StockDTO>> stockMap = stockDTOBySaleContractCodes.stream().collect(Collectors.groupingBy(StockDTO::getSkuCode));
                    String skuCode = cmv.getSkuCode();
                    List<StockDTO> stockDTOS = stockMap.get(skuCode);
                    List<StockDTO> transformStockDTOList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(stockDTOS)){
                        stockDTOS.forEach(s->{
                            Integer availableQuantity = Objects.isNull(s.getAvailableQuantity())?0:s.getAvailableQuantity();
                            Integer producingQuantity = Objects.isNull(s.getProducingQuantity())?0:s.getProducingQuantity();
                            Integer usedQuantity = batchUserQuantityMap.get(s.getBatchCode());
                            if (Objects.nonNull(usedQuantity)){
                                int calcQuantity = availableQuantity + producingQuantity - usedQuantity;
                                if (calcQuantity <= 0){
                                    return;
                                }
                                s.setAvailableQuantity(calcQuantity);
                                s.setProducingQuantity(0);
                                transformStockDTOList.add(s);
                            }else {
                                batchUserQuantityMap.put(s.getBatchCode(),availableQuantity + producingQuantity);
                                transformStockDTOList.add(s);
                            }
                        });
                    }
                    List<BatchChildVO> batchChildVOS = ShipmentConvert.INSTANCE.convertBatchildByStockList(shipmentItemMap.get(cmv.getShipmentItemId()),transformStockDTOList);
                    List<BatchChildVO> list = batchChildVOS.stream().filter(x -> addBatchCodeList.contains(x.getBatchCode())&&x.getAvailableCabinetQuantity() > 0).collect(Collectors.toList());
                    if (CollUtil.isEmpty(list)) {
                        return;
                    }
                    list.stream().filter(s -> !sourceStockBatchCodeSet.contains(s.getBatchCode())).forEach(s -> {
                        s.setUsedQuantity(s.getAvailableQuantity());
                    });
                    List<BatchChildVO> cmvChildren = new ArrayList<>(Optional.ofNullable(cmv.getChildren())
                            .orElse(Collections.emptyList()));
                    if (CollUtil.isEmpty(cmvChildren)) {
                        cmv.setChildren(list);
                    } else {
                        cmvChildren.addAll(list);
                        cmv.setChildren(cmvChildren);
                    }
                }
            }
        });
        stockNoticeSaveReqDTO.setIsContainerTransportation(BooleanEnum.YES.getValue());
        stockNoticeSaveReqDTO.setReferenceNumber(containerMidVO.getReferenceNumber());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        stockNoticeSaveReqDTO.setApplyer(adminUserApi.getUserDeptByUserId(loginUserId));
        stockNoticeSaveReqDTO.setInboundDate(containerMidVO.getInboundDate());
        stockNoticeSaveReqDTO.setNoticeType(StockTypeEnum.OUT_STOCK.getValue());
        stockNoticeSaveReqDTO.setSaleContractCodeList(saleContractCodeList);
        stockNoticeSaveReqDTO.setShipmentCode(containerMidVO.getShipmentCode());
        stockNoticeSaveReqDTO.setInvoiceCode(containerMidVO.getInvoiceCode());
        stockNoticeSaveReqDTO.setLinkCodeList(containerMidVO.getLinkCodeList());
        Long companyId = containerMidVO.getCompanyId();
        if (Objects.nonNull(companyId)){
            stockNoticeSaveReqDTO.setCompanyId(companyId);
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            String companyName = "";
            if (ObjectUtil.isNotNull(companyDTO)) {
                companyName = companyDTO.getCompanyName();
            }
            stockNoticeSaveReqDTO.setCompanyName(companyName);
        }
        List<StockNoticeItemDTO> itemDTOList = new ArrayList<>();
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(containerMidVO.getShipmentItemIdList());

        Map<Long, SaleContractItemDTO> saleItemMap;
        if (CollUtil.isNotEmpty(shipmentItemList)) {
            List<Long> saleItemIds = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
            saleItemMap = saleContractApi.getItemListByIds(saleItemIds);
        } else {
            saleItemMap = null;
        }

        List<Long> itemIds = children.stream().map(StockChildVO::getSaleContractItemId).distinct().toList();
        Map<Long, String> itemIdMap = saleContractApi.getItemCodesByIds(itemIds);

        children.forEach(s -> {
            List<BatchChildVO> childList = s.getChildren();
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            childList.forEach(child -> {
                String batchCode = child.getBatchCode();
                StockNoticeItemDTO  stockNoticeItemDTO = new StockNoticeItemDTO();
                Integer baseOrderQuantity = Objects.isNull(stockNoticeItemDTO.getOrderQuantity()) ? CalculationDict.ZERO : stockNoticeItemDTO.getOrderQuantity();
                if (CollUtil.isNotEmpty(itemIdMap)) {
                    stockNoticeItemDTO.setSaleItemUniqueCode(itemIdMap.get(s.getSaleContractItemId()));
                }
                stockNoticeItemDTO.setSkuCode(s.getSkuCode());
                stockNoticeItemDTO.setShipmentItemId(child.getShipmentItemId());
                stockNoticeItemDTO.setSkuId(s.getSkuId());
                stockNoticeItemDTO.setSkuName(s.getSkuName());
                stockNoticeItemDTO.setBatchCode(child.getBatchCode());
                stockNoticeItemDTO.setSaleContractCode(s.getSaleContractCode());
                stockNoticeItemDTO.setVenderCode(child.getVenderCode());
                stockNoticeItemDTO.setVenderId(child.getVenderId());
                stockNoticeItemDTO.setVenderName(child.getVenderName());
                stockNoticeItemDTO.setCustPo(s.getCustPo());
                stockNoticeItemDTO.setSourceUniqueCode(IdUtil.fastSimpleUUID());
                stockNoticeItemDTO.setQtyPerInnerbox(child.getQtyPerInnerbox());
                stockNoticeItemDTO.setQtyPerOuterbox(child.getQtyPerOuterbox());
                stockNoticeItemDTO.setSpecificationList(child.getSpecificationList());
                stockNoticeItemDTO.setSplitBoxFlag(child.getSplitBoxFlag());
                stockNoticeItemDTO.setPalletWeight(s.getTotalGrossweight());
                stockNoticeItemDTO.setSourceUniqueCode(s.getSourceUniqueCode());
                Integer orderQuantity = child.getUsedQuantity() + baseOrderQuantity;
                stockNoticeItemDTO.setOrderQuantity(orderQuantity);
                Integer orderBoxQuantity = NumberUtil.div(orderQuantity, child.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
                stockNoticeItemDTO.setOrderBoxQuantity(orderBoxQuantity);
                stockNoticeItemDTO.setTotalVolume(CalcSpecificationUtil.calcTotalVolumeByBoxCount(child.getSpecificationList(),BigDecimal.valueOf(orderBoxQuantity)));
                stockNoticeItemDTO.setTotalWeight(CalcSpecificationUtil.calcTotalGrossweightByBoxCount(child.getSpecificationList(),BigDecimal.valueOf(orderBoxQuantity)));
                stockNoticeItemDTO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
                stockNoticeItemDTO.setPurchaserId(s.getPurchaseUserId());
                stockNoticeItemDTO.setPurchaserDeptId(s.getPurchaseUserDeptId());
                stockNoticeItemDTO.setPurchaseUserName(s.getPurchaseUserName());
                stockNoticeItemDTO.setPurchaseUserDeptName(s.getPurchaseUserDeptName());
                stockNoticeItemDTO.setCskuCode(s.getCskuCode());
                stockNoticeItemDTO.setBasicSkuCode(s.getBasicSkuCode());
                stockNoticeItemDTO.setCustId(s.getCustId());
                stockNoticeItemDTO.setCustCode(s.getCustCode());
                stockNoticeItemDTO.setCustName(s.getCustName());
                stockNoticeItemDTO.setWarehouseId(child.getWarehouseId());
                stockNoticeItemDTO.setWarehouseName(child.getWarehouseName());
                stockNoticeItemDTO.setPurchaseContractCode(child.getPurchaseContractCode());
                stockNoticeItemDTO.setSales(s.getSales());
                stockNoticeItemDTO.setManager(s.getManager());
                stockNoticeItemDTO.setShippedAddress(child.getShippedAddress());
                if (Objects.nonNull(stockNoticeItemDTO.getOrderQuantity())&&stockNoticeItemDTO.getOrderQuantity() > 0){
                    itemDTOList.add(stockNoticeItemDTO);
                }
            });
        });
        if (CollUtil.isNotEmpty(itemDTOList)) {
            stockNoticeSaveReqDTO.setNoticeItems(itemDTOList);
        }
        stockNoticeSaveReqDTO.setPurContractCodeList(purchaseContractCodeList);
        // 更新出运明细转拉柜通知单标识
        if (!org.springframework.util.CollectionUtils.isEmpty(shipmentItemList)) {
            shipmentItemList.forEach(x -> x.setConverNoticeFlag(BooleanEnum.YES.getValue()));
            shipmentItemMapper.updateBatch(shipmentItemList);
        }

        //  回写进仓日期
        List<ShipmentDO> updateList = shipmentItemList.stream().map(s -> {
            ShipmentDO shipmentDO = new ShipmentDO();
            shipmentDO.setId(s.getShipmentId());
            shipmentDO.setInboundDate(containerMidVO.getInboundDate());
            return shipmentDO;
        }).distinct().toList();
        shipmentMapper.updateBatch(updateList);
        CreatedResponse notice = iStockNoticeApi.createNotice(stockNoticeSaveReqDTO);
        // 更新库存拉柜数量
        List<StockNoticeItemDTO> noticeItems = stockNoticeSaveReqDTO.getNoticeItems();
        Map<String,Map<String,Integer>> updateQunatityMap = new HashMap<>();
        Map<String, List<StockNoticeItemDTO>> noticeItemMap = noticeItems.stream().collect(Collectors.groupingBy(StockNoticeItemDTO::getSaleContractCode));
        noticeItemMap.forEach((k,v)->{
            Map<String,Integer> updateMap = new HashMap<>();
            v.forEach(s->{
                updateMap.merge(s.getBatchCode(),s.getOrderQuantity(),Integer::sum);
            });
            updateQunatityMap.put(k,updateMap);
        });
        iStockApi.updateStockCabinetQuantity(updateQunatityMap);
        responses.add(notice);
        return responses;
    }

    private void validateChildSkuStock(String skuCode,Integer manufactureQuantity,List<StockDTO> newStockDTOList){
        //二级产品库存校验
        List<SkuBomDTO> skuBomTwoLevelList = skuApi.getSonSkuListBySkuCode(skuCode);
        if (!validateStock(skuBomTwoLevelList, manufactureQuantity, newStockDTOList)) {
            return;
        }
        //三级产品库存校验
        skuBomTwoLevelList.forEach(skuBomDTO -> {
            List<SkuBomDTO> skuBomDTOThreeLevelList = skuApi.getSonSkuListBySkuCode(skuBomDTO.getSkuCode());
            List<StockDTO> stockSkuBomThreeLevelList = newStockDTOList.stream().filter(i -> i.getSkuCode().equals(skuBomDTO.getSkuCode())).toList();
            Integer sumTwoLevelQuantity = stockSkuBomThreeLevelList.stream().map(StockDTO::getAvailableQuantity).reduce(0, Integer::sum);
            if (validateStock(skuBomDTOThreeLevelList, sumTwoLevelQuantity, newStockDTOList)) {
                throw exception(STOCK_QUANTITY_NOT_ENOUGH);
            }
        });
    }
    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), CHANGE_OPERATOR_EXTS_KEY);
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class), CHANGE_OPERATOR_EXTS_KEY);
    }

    @Override
    public void deleteShipmentChange(Long id) {
        ShipmentChange shipmentChange = shipmentChangeMapper.selectById(id);
        if (Objects.isNull(shipmentChange)) {
            throw exception(SHIPMENT_CHANGE_NOT_EXISTS);
        }
        Integer auditStatus = shipmentChange.getAuditStatus();
        if (!BpmProcessInstanceResultEnum.BACK.getResult().equals(auditStatus) || !BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(auditStatus)) {
            throw exception(SHIPMENT_CHANGE_STATUS_EXCEPTION);
        }
        shipmentChangeMapper.deleteById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateShipmentForwarderFee(ShipmentForwarderFeeSaveReqVO updateReqVO) {
        String currency = "RMB";
        Long shipmentId = updateReqVO.getShipmentId();
        List<ForwarderFeeDO> feeList = updateReqVO.getForwarderFeeList();
        if (Objects.isNull(shipmentId)) {
            return false;
        }
        List<ForwarderFeeDO> doList = forwarderFeeService.getListByShipmentId(shipmentId);
//        if (CollUtil.isNotEmpty(doList)) {
//            forwarderFeeService.deleteByShipmentId(shipmentId);
//        }
        ShipmentDO shipmentDO = shipmentMapper.selectOne(ShipmentDO::getId, shipmentId);
        if (Objects.isNull(shipmentDO)){
            return false;
        }
        if(CollUtil.isEmpty(feeList)){
            List<ShipmentItem> itemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
            for (ShipmentItem shipmentItem : itemList) {
                shipmentItem.setForwarderShareAmount(new JsonAmount().setCurrency("").setAmount(BigDecimal.ZERO));
            }
            shipmentItemMapper.updateBatch(itemList);
            //删除单证费用和费用归集
            updateShipmentForwarderFeeItems(doList,feeList);
            List<String> fCodes = doList.stream().map(ForwarderFeeDO::getCode).distinct().toList();
            feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.SHIPMENT_FORWARDER_FEE,fCodes);
            return false;
        }

        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();

        //子项赋值出运信息、主体信息和申请人信息
        feeList.forEach(s -> {
            s.setShipmentId(shipmentId);
            if (s.getPayStatus() == null) {
                s.setPayStatus(PayStatusEnum.NOT_APPLY.getValue());
            }
            s.setPurchaseUserList(updateReqVO.getPurchaseUserList());
            s.setManagerList(updateReqVO.getManagerList());
            s.setInvoiceCode(shipmentDO.getInvoiceCode());
            s.setShipmentCode(shipmentDO.getCode());
            s.setCompanyId(shipmentDO.getForeignTradeCompanyId());
            s.setCompanyName(shipmentDO.getForeignTradeCompanyName());
            if (Objects.isNull(s.getApplyer())) {
                s.setApplyer(shipmentDO.getInputUser());
            }
        });
        //对amount金额求和
        BigDecimal totalAmount = feeList.stream().map(ForwarderFeeDO::getAmount).filter(Objects::nonNull)
                .map(amount -> dailyRateMap.get(amount.getCurrency()).multiply(amount.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (Objects.equals(totalAmount, BigDecimal.ZERO)) {
            throw exception(FORWARDER_FEE_AMOUNT_NULL);
        }
//        forwarderFeeService.insertBatch(feeList);
        updateShipmentForwarderFeeItems(doList,feeList);

        //根据体积均摊金额回写费用归集明细的明细
        //均摊金额不为0 总体积不为0 再处理均摊
        BigDecimal totalVolume = shipmentDO.getTotalVolume();
        if (totalAmount.compareTo(BigDecimal.ZERO) != 0 && totalVolume.compareTo(BigDecimal.ZERO) != 0) {
            List<ShipmentItem> itemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = 0; i < itemList.size(); i++) {
                if (i < itemList.size() - 1) {
                    BigDecimal value = totalAmount.multiply(itemList.get(i).getTotalVolume()).divide(totalVolume, CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                    BigDecimal value2 = value.setScale(2, RoundingMode.HALF_UP);
                    itemList.get(i).setForwarderShareAmount(new JsonAmount().setCurrency(currency).setAmount(value2));
                    sum = sum.add(value2);
                } else {
                    //处理最后一条数据，避免总和不一致
                    itemList.get(i).setForwarderShareAmount(new JsonAmount().setCurrency(currency).setAmount(totalAmount.subtract(sum)));
                }
            }
            //查询结果做 出运费用赋值操作后回写，不允许在此之前增加其他操作
            shipmentItemMapper.updateBatch(itemList);
            //自动生成费用归集数据
            List<FeeShareReqDTO> feeShareReqDTOList = new ArrayList<>();
            //费用归集按照单条的船代费用编号进行，方便下推费用显示    之后的销售合同利润统计，需要根据出运明细查询所有的船代费用，再查费用归集
            Map<String, List<ShipmentItem>> itemMap = itemList.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractCode)); //子项销售合同可能多条，进行聚合
            if (CollUtil.isEmpty(itemMap)) {
                throw exception(SHIPMENT_NOT_EXISTS_SALE_CONTRACT);
            }
            List<ForwarderFeeDO> feeDOList = forwarderFeeService.getListByShipmentId(shipmentId);   // 重新查询是因为编号在入库才自动生成
            feeDOList.forEach(s -> {
                itemMap.forEach((k, v) -> {
                    FeeShareReqDTO feeShare = new FeeShareReqDTO();
                    feeShare.setBusinessType(FeeShareSourceTypeEnum.SHIPMENT_FORWARDER_FEE.getValue()).setBusinessId(s.getId())
                            .setBusinessCode(s.getCode());
                    feeShare.setOrderType(OaOrderTypeEnum.SMS.getCode());
                    feeShare.setCompanyId(shipmentDO.getForeignTradeCompanyId()).setCompanyName(shipmentDO.getForeignTradeCompanyName());
                    feeShare.setSourceStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                            .setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                            .setStatus(BooleanEnum.YES.getValue());
                    BigDecimal volume = v.stream().map(ShipmentItem::getTotalVolume).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    SmsContractAllDTO sms = new SmsContractAllDTO();
                    JsonAmount jsonAmount = Objects.isNull(s.getAmount())?new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(currency):s.getAmount();
                    BigDecimal value = NumUtil.div(NumUtil.mul(jsonAmount.getAmount(),volume),  totalVolume).setScale(6, RoundingMode.HALF_UP);
                    sms.setAmount(new JsonAmount().setAmount(value).setCurrency(s.getAmount().getCurrency()));
                    Map<String, SmsContractAllDTO> smsByCodeList = saleContractApi.getSmsByCodeList(Collections.singletonList(k));
                    if (Objects.isNull(smsByCodeList)) {
                        throw exception(SALE_CONTRACT_NOT_EXISTS);
                    }
                    sms.setId(smsByCodeList.get(k).getId()).setCode(k);
                    feeShare.setSmsChildren(List.of(sms));
                    feeShare.setInputUser(shipmentDO.getInputUser());
                    feeShareReqDTOList.add(feeShare);
                });
            });
            if (CollUtil.isNotEmpty(feeShareReqDTOList)) {
                feeShareApi.updateFeeShareByDTOList(feeShareReqDTOList);
            }
        }
        return true;
    }

    private void updateShipmentForwarderFeeItems(List<ForwarderFeeDO> sourceList, List<ForwarderFeeDO> newList) {
        Map<Long, ForwarderFeeDO> sourceMap = sourceList.stream().collect(Collectors.toMap(ForwarderFeeDO::getId, item -> item));
//        Map<Long, ForwarderFeeDO> newMap = newList.stream().collect(Collectors.toMap(ForwarderFeeDO::getId, item -> item));
        if (CollUtil.isEmpty(sourceList)&&CollUtil.isEmpty(newList)){
            return;
        }
        List<ForwarderFeeDO> updateArr =  new ArrayList<>();
        if (CollUtil.isEmpty(newList)){
            forwarderFeeMapper.deleteBatchIds(sourceList);
            return;
        }
        for (ForwarderFeeDO newItem : newList) {
            if (sourceMap.containsKey(newItem.getId())) {
                ForwarderFeeDO originalItem = sourceMap.get(newItem.getId());
                //1.原有的已经申请的数据，不可编辑，使用原先的数据  payStatus = 2
                if (Objects.equals(originalItem.getPayStatus(), PayStatusEnum.APPLY.getValue())) {
                    continue;
                } else {   // 2.原有没有申请的更新
                    updateArr.add(newItem);
                }
            }
        }
        if(CollUtil.isNotEmpty(updateArr)){
            forwarderFeeMapper.updateBatch(updateArr);
        }

        //原先有的，现在没有的删除
        List<ForwarderFeeDO> deleteArr = sourceList.stream().filter(item -> {
            return newList.stream().noneMatch(s -> Objects.equals(s.getId(), item.getId()));
        }).collect(Collectors.toList());
        if (!deleteArr.isEmpty()) {
            List<String> deleteCodes = deleteArr.stream().map(ForwarderFeeDO::getCode).toList();
            feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.SHIPMENT_FORWARDER_FEE,deleteCodes);
            forwarderFeeMapper.deleteBatchIds(deleteArr);
        }
        //原先没有的，现在有的新增
        List<ForwarderFeeDO> addArr = newList.stream().filter(item -> item.getId() == null || !sourceMap.containsKey(item.getId())).collect(Collectors.toList());
        if (!addArr.isEmpty()) {
            forwarderFeeService.insertBatch(addArr);
        }

    }

    @Override
    public List<ForwarderFeeShareFeeRespVO> getListByCodeList(String codeList) {
        if (StrUtil.isEmpty(codeList)) {
            throw exception(FORWARDER_FEE_CODE_WRONG);
        }
        List<String> list = Arrays.stream(codeList.split(",")).toList();
        List<ForwarderFeeDO> feeDoList = forwarderFeeService.getListByCodeList(list);
        Map<String, List<FeeShareRespDTO>> listMap = feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.SHIPMENT_FORWARDER_FEE.getValue(), list);
        List<ForwarderFeeShareFeeRespVO> result = new ArrayList<>();
        if (CollUtil.isEmpty(listMap)) {
            return null;
        }
        listMap.forEach((k, v) -> {
            Optional<ForwarderFeeDO> first = feeDoList.stream().filter(s -> Objects.equals(s.getCode(), k)).findFirst();
            if (first.isPresent()) {
                ForwarderFeeDO forwarderFeeDO = first.get();
                String desc = "";
                List<String> stringList = v.stream().map(FeeShareRespDTO::getFeeShareDetail).filter(Objects::nonNull).toList();
                if (CollUtil.isNotEmpty(stringList)) {
                    desc = String.join(",", stringList);
                }
                BigDecimal amount = v.stream().map(FeeShareRespDTO::getTotalAmount).filter(Objects::nonNull)
                        .map(JsonAmount::getAmount).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                Optional<String> first1 = v.stream().map(FeeShareRespDTO::getTotalAmount).filter(Objects::nonNull)
                        .map(JsonAmount::getCurrency).filter(Objects::nonNull).findFirst();
                String currrency = "";
                if (first1.isPresent()) {
                    currrency = first1.get();
                }
                result.add(new ForwarderFeeShareFeeRespVO()
                        .setShipmentId(forwarderFeeDO.getShipmentId())
                        .setShipmentCode(forwarderFeeDO.getShipmentCode())
                        .setCode(forwarderFeeDO.getInvoiceCode())
                        .setAmount(new JsonAmount().setAmount(amount).setCurrency(currrency))
                        .setDesc(desc)
                        .setBusinessType(v.get(0).getBusinessType())
                );
            }

        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ShipmentItem> splitShipmentItem(List<Long> ids) {
        // 校验是否存在
        List<ShipmentItem> shipmentItemList = validateShipmentItemExists(ids);
        // 响应结果
        List<ShipmentItem> result = new ArrayList<>();
        // 拆分 ->  出运数量 > 结汇单、报关单、开票数 三者最大值才可拆分
        shipmentItemList.forEach(s -> {
            // 出运数量
            Integer shippingQuantity = Objects.isNull(s.getShippingQuantity()) ? CalculationDict.ZERO : s.getShippingQuantity();
            // 结汇数量
            Integer settlementQuantity = Objects.isNull(s.getSettlementQuantity()) ? CalculationDict.ZERO : s.getSettlementQuantity();
            // 开票数量
            BigDecimal invoicedQuantity = Objects.isNull(s.getInvoicedQuantity()) ? BigDecimal.ZERO : s.getInvoicedQuantity();
            // 报关数量
            Integer declaredQuantity = Objects.isNull(s.getDeclaredQuantity()) ? CalculationDict.ZERO : s.getDeclaredQuantity();
            Integer maxQuantity = NumUtil.max(declaredQuantity, settlementQuantity, invoicedQuantity.intValue());
            // 出运数量 = 三者最大值则不可拆分
            if (NumUtil.compare(maxQuantity, shippingQuantity) >= 0) {
                throw exception(SHIPMENT_QUANTITY_NOT_ENOUGHT);
            }
            // 更新明细出运数量
            s.setShippingQuantity(maxQuantity);
            // 创建拆分记录
            ShipmentItem shipmentItem = BeanUtil.copyProperties(s, s.getClass());
            shipmentItem.setId(null);
            // 出运数量 = 出运数量 - 单据数量最大值
            shipmentItem.setShippingQuantity(NumUtil.sub(shippingQuantity, maxQuantity).intValue());
            // 置空单据数量
            shipmentItem.setSettlementQuantity(CalculationDict.ZERO);
            shipmentItem.setInvoicedQuantity(BigDecimal.ZERO);
            shipmentItem.setDeclaredQuantity(CalculationDict.ZERO);
            shipmentItem.setIsToSettlementForm(ToSettlementEnum.NOT_YET_TRANSFERRED.getStatus());
            shipmentItem.setIsToDeclaration(BooleanEnum.NO.getValue());
            // 重新校验明细状态
            updateShipmentItemStatus(s);
            result.add(s);
            result.add(shipmentItem);
        });
        return result;
    }

    @Override
    public void batchUpdateShipmentItem(List<ShipmentItem> shipmentItemList) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        List<ShipmentItem> createList = shipmentItemList.stream().filter(s -> Objects.isNull(s.getId())).toList();
        List<ShipmentItem> updateList = shipmentItemList.stream().filter(s -> Objects.nonNull(s.getId())).toList();
        shipmentItemMapper.insertBatch(createList);
        shipmentItemMapper.updateBatch(updateList);
    }

    @Override
    public void updateShipmentItemInvoiceStatus(List<String> uniqueCodeList, Integer status) {
        if (CollUtil.isEmpty(uniqueCodeList)) {
            return;
        }
        ShipmentItem shipmentItem = new ShipmentItem();
        shipmentItem.setInvoiceStatus(status);
        shipmentItemMapper.update(shipmentItem, new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getUniqueCode, uniqueCodeList));
    }

    @Override
    public void updateShipmentIItemInvoiceQuantity(Map<Long, BigDecimal> quantityMap) {
        if (CollUtil.isEmpty(quantityMap)) {
            return;
        }
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(quantityMap.keySet());
        if (CollUtil.isEmpty(shipmentItemList)) {
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        List<InvoiceQuantityDTO> updateList = new ArrayList<>();
        List<String> purchaseCodeList = shipmentItemList.stream().map(ShipmentItem::getPurchaseContractCode).distinct().toList();
        Map<String, String> purchaseVenderMap = purchaseContractApi.getVenderCodeMapByPurchaseContractCodeList(purchaseCodeList);
        shipmentItemList.forEach(s -> {
            Long id = s.getId();
            BigDecimal invoiceQuantity = quantityMap.get(id);
            if (Objects.isNull(invoiceQuantity)) {
                return;
            }
            BigDecimal baseInvoiceQuantity = Objects.isNull(s.getInvoicedQuantity()) ? BigDecimal.ZERO : s.getInvoicedQuantity();
            s.setInvoicedQuantity(NumUtil.add(baseInvoiceQuantity, invoiceQuantity));
            // 出运数量 = 开票数量则更新状态 -> 已转
            if (s.getShippingQuantity() == s.getInvoicedQuantity().intValue()) {
                s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
            } else if (s.getShippingQuantity() > s.getInvoicedQuantity().intValue()) {
                s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
            }
            InvoiceQuantityDTO invoiceQuantityDTO = new InvoiceQuantityDTO();
            invoiceQuantityDTO.setVenderCode(purchaseVenderMap.get(s.getPurchaseContractCode()));
            invoiceQuantityDTO.setPurchaseContractCode(s.getPurchaseContractCode());
            // 转换报关数量
            String hsMeasureUnit = s.getHsMeasureUnit();
            Long saleContractItemId = s.getSaleContractItemId();
            invoiceQuantityDTO.setSaleContractItemId(saleContractItemId);
            invoiceQuantityDTO.setSkuCode(s.getSkuCode());
            // 回写采购开票数量为本次采购数量
            invoiceQuantityDTO.setInvoiceQuantity(s.getThisPurchaseQuantity());
            updateList.add(invoiceQuantityDTO);
        });
        shipmentItemMapper.updateBatch(shipmentItemList);
        purchaseContractApi.updateInvoiceData(updateList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> batchManufactureSku(List<ManufactureSkuReqVO> manufactureSkuReqVOList) {
        List<String> result = new ArrayList<>();
        manufactureSkuReqVOList.forEach(x -> {
            List<String> batchCodeList = this.manufactureSku(x.getSkuCode(), x.getProcessingNum(), x.getSaleContractItemId());
            if (CollUtil.isNotEmpty(batchCodeList)){
                result.addAll(batchCodeList);
            }
        });
        return result;
    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        ShipmentDO shipmentDO = validateShipmentExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        shipmentDO.setStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = shipmentMapper.updateById(shipmentDO);
        return i > 0;
    }

    @Override
    public Map<String, ShipmentDTO> getByShipmentCodeList(List<String> shipmentCodeList) {
        if (CollUtil.isEmpty(shipmentCodeList)) {
            return null;
        }
        List<ShipmentDO> doList = shipmentMapper.selectList(ShipmentDO::getCode, shipmentCodeList);
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        List<ShipmentDTO> dtoList = BeanUtils.toBean(doList, ShipmentDTO.class);
        return dtoList.stream().collect(Collectors.toMap(ShipmentDTO::getCode, s -> s, (s1, s2) -> s1));
    }

    @Override
    public ShipmentDTO getByShipmentCode(String shipmentCode) {
        if (StrUtil.isEmpty(shipmentCode)) {
            return null;
        }
        Optional<ShipmentDO> first = shipmentMapper.selectList(ShipmentDO::getCode, shipmentCode).stream().findFirst();
        return first.map(shipmentDO -> BeanUtils.toBean(shipmentDO, ShipmentDTO.class)).orElse(null);
    }

    @Override
    public List<Long> getShipmentIdsBySaleContractCode(String saleContractCode) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.getShipmentItemListBySaleContractCode(saleContractCode);
        if (shipmentItemList == null) {
            return null;
        }
        List<Long> shipmentIds = shipmentItemList.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        return shipmentIds;
    }

    @Override
    @DataPermission(enable = false)
    public void updateOutQuantity(String shipmentCode, Map<Long, Integer> skuQuantityMap, LocalDateTime outDate) {
        if (StrUtil.isEmpty(shipmentCode) || CollUtil.isEmpty(skuQuantityMap)) {
            return;
        }
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().eq(ShipmentDO::getCode, shipmentCode));
        if (CollUtil.isEmpty(shipmentDOS)){
            return;
        }
        ShipmentDO shipmentDO = shipmentDOS.get(0);
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getShipmentId, shipmentDO.getId()));
        if (CollUtil.isEmpty(shipmentItemList)) {
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        shipmentItemList.forEach(s -> {
            Integer quantity = skuQuantityMap.get(s.getId());
            if (Objects.isNull(quantity)) {
                return;
            }
            if (quantity > s.getShippingQuantity()){
                s.setOutQuantity(s.getShippingQuantity());
            }else{
                s.setOutQuantity(quantity);
            }
            s.setOutDate(outDate);
            s.setOutboundFlag(BooleanEnum.YES.getValue());
            if (s.getShippingQuantity() == s.getOutQuantity().intValue()){
                shipmentDO.setOutboundFlag(BooleanEnum.YES.getValue());
                shipmentDO.setOutboundDate(outDate);
            }
        });
        shipmentMapper.updateById(shipmentDO);
        shipmentItemMapper.updateBatch(shipmentItemList);
        //更新销售合同收款计划为出库日的起始日期
        List<String> saleContractCodes = shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).distinct().toList();
        if (CollUtil.isNotEmpty(saleContractCodes)) {
            List<Integer> dateTypes = new ArrayList<>();
            dateTypes.add(ShettlementDateTypeEnum.OUTSTOCK_DATE.getValue());
            saleContractApi.updateShipmentDate(saleContractCodes, dateTypes);
        }
    }

    @Override
    public void cancelConvertContainerFlag(List<String> uniqueCodeList) {
        if (CollUtil.isEmpty(uniqueCodeList)) {
            return;
        }
        LambdaUpdateWrapper<ShipmentItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ShipmentItem::getUniqueCode, uniqueCodeList);
        updateWrapper.set(ShipmentItem::getConverNoticeFlag, BooleanEnum.NO.getValue());
        updateWrapper.set(ShipmentItem::getProcessFlag, BooleanEnum.NO.getValue());
        shipmentItemMapper.update(updateWrapper);
    }

    @Override
    public void updateRealPurchasePrice(String saleContractCode, UserDept manager) {
        if (StrUtil.isEmpty(saleContractCode)) {
            return;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getSaleContractCode, saleContractCode);
        if (CollUtil.isEmpty(shipmentItems)) {
            return;
        }
        List<Long> saleItems = shipmentItems.stream().map(ShipmentItem::getSaleContractItemId).filter(Objects::nonNull).distinct().toList();
        Map<Long, Tuple> realPurchasePriceMap = saleContractApi.calcRealPurchasePrice(saleItems);
        shipmentItems.forEach(s -> {
            Long saleContractItemId = s.getSaleContractItemId();
            Tuple tuple = realPurchasePriceMap.get(saleContractItemId);
            if (Objects.isNull(tuple)) {
                return;
            }
            s.setPurchaseWithTaxPrice(tuple.get(0));
            s.setPurchaseTotalQuantity(tuple.get(2));
            s.setManager(manager);
        });

        shipmentItemMapper.updateBatch(shipmentItems);
    }

    @Override
    public Map<Long, List<Integer>> getShipmentContractItemCancelFlag(List<Long> itemIdList) {
        Map<Long, List<Integer>> result = new HashMap<>();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getSaleContractItemId, itemIdList);
        if (CollUtil.isEmpty(shipmentItems)) {
            return result;
        }
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).toList();
        List<ShipmentDO> shipmentDOList = shipmentMapper.selectBatchIds(shipmentIdList);
        Map<Long, Integer> shipmentMap = shipmentDOList.stream().collect(Collectors.toMap(ShipmentDO::getId, ShipmentDO::getStatus));
        shipmentItems.forEach(s -> {
            Integer status = shipmentMap.get(s.getShipmentId());
            Integer cancelFlag = ShippingStatusEnum.CASE_CLOSED.getValue().equals(status) ? BooleanEnum.YES.getValue() : BooleanEnum.NO.getValue();
            result.computeIfAbsent(s.getSaleContractItemId(), k -> new ArrayList<>()).add(cancelFlag);
        });
        return result;
    }

    @Override
    public void cancelShipmentPlanItem(List<Long> itemIdList) {
        if (CollUtil.isEmpty(itemIdList)) {
            return;
        }
        List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemMapper.selectList(ShipmentPlanItem::getSaleContractItemId, itemIdList);
        if (CollUtil.isEmpty(shipmentPlanItems)) {
            return;
        }
        shipmentPlanItems.forEach(s -> s.setStatus(ShippingPlanStatusEnum.CASE_CLOSED.getValue()));
        shipmentPlanItemMapper.updateBatch(shipmentPlanItems);
        List<Long> planIdList = shipmentPlanItems.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().toList();
        List<ShipmentPlanItem> allShipmentPlanItem = shipmentPlanItemMapper.selectList(ShipmentPlanItem::getShipmentPlanId, planIdList);
        Map<Long, List<ShipmentPlanItem>> planItemMap = allShipmentPlanItem.stream().collect(Collectors.groupingBy(ShipmentPlanItem::getShipmentPlanId));
        List<Long> cancelPlanIdList = new ArrayList<>();
        // 当计划明细全部作废时作废对应计划
        planItemMap.forEach((k, v) -> {
            boolean cancelFlag = v.stream().allMatch(s -> ShippingPlanStatusEnum.CASE_CLOSED.getValue().equals(s.getStatus()));
            if (cancelFlag) {
                // 作废计划
                cancelPlanIdList.add(k);
            }
        });
        if (CollUtil.isNotEmpty(cancelPlanIdList)) {
            cancelPlanIdList.forEach(s -> shipmentPlanService.closeShipmentPlan(new CloseShipmentPlanReq().setParentId(s)));
        }

    }

    @Override
    @DataPermission(enable = false)
    public List<String> getEffectShipmentCode(String saleContractCode, List<Long> itemIdList) {
        if (StrUtil.isEmpty(saleContractCode)) {
            return List.of();
        }
        LambdaQueryWrapper<ShipmentItem> queryWrapper = new LambdaQueryWrapper<ShipmentItem>().eq(ShipmentItem::getSaleContractCode, saleContractCode).or().eq(ShipmentItem::getSourceSaleContractCode, saleContractCode);
        if (CollUtil.isNotEmpty(itemIdList)) {
            queryWrapper.in(ShipmentItem::getSaleContractItemId, itemIdList);
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(shipmentItems)) {
            return List.of();
        }
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        if (CollUtil.isNotEmpty(shipmentIdList)) {
            List<ShipmentDO> shipmentDOList = shipmentMapper.selectList(ShipmentDO::getId, shipmentIdList);
            if (CollUtil.isNotEmpty(shipmentDOList)) {
                return shipmentDOList.stream().map(ShipmentDO::getCode).distinct().toList();
            }
        }
        return List.of();
    }

    @Override
    public void exportExcel(Long id, String exportType, String reportCode, String custCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        ShipmentDO shipmentDO = shipmentMapper.selectById(id);
        if (shipmentDO == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (shipmentDO.getForeignTradeCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, shipmentDO.getForeignTradeCompanyId());
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

        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().eq(ShipmentItem::getShipmentId, id).eq(ShipmentItem::getCustCode, custCode));
        List<ShipmentItemExportVO> shipmentItemExportVOList = ShipmentConvert.INSTANCE.convertShipmentItemExportVO(shipmentItemList,shipmentDO,exportType);
        HashMap<String, Object> params = new HashMap();
        List<CellWriteHandler> handlers = null;
        switch (exportType){
            case "bookingNote":{
                params = getBookingNoteParams(shipmentDO,shipmentItemList);
                // 处理frontShippingMark和汇总数据
                processShipmentItemExportData(shipmentItemList, params,shipmentDO);
                // 自动识别明细区起始行号（{.nameEng}所在行）
                int detailStartRow = -1;
                try (ByteArrayInputStream scanStream = new ByteArrayInputStream(content)) {
                    org.apache.poi.ss.usermodel.Workbook templateWb = org.apache.poi.ss.usermodel.WorkbookFactory.create(scanStream);
                    org.apache.poi.ss.usermodel.Sheet sheet = templateWb.getSheetAt(0);
                    outer:
                    for (Row row : sheet) {
                        for (org.apache.poi.ss.usermodel.Cell cell : row) {
                            if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                                String cellValue = cell.getStringCellValue();
                                if (cellValue != null && cellValue.contains("{.nameEng}")) {
                                    detailStartRow = row.getRowNum();
                                    break outer;
                                }
                            }
                        }
                    }
                    templateWb.close();
                } catch (Exception e) {
                    throw new RuntimeException("模板明细区起始行号识别失败", e);
                }
                // 创建自定义Handler列表
                handlers = new ArrayList<>();
                if (detailStartRow != -1 && !shipmentItemExportVOList.isEmpty()) {
                    handlers.add(new DetailMergeHandler(detailStartRow, shipmentItemExportVOList.size()));
                }
                break;
            }
            case "hsCode":{
                for (ShipmentItemExportVO item : shipmentItemExportVOList) {
                    if(item.getMainPicture()!=null){
                        String mainPictureInputPath = item.getMainPicture().getFileUrl();
                        try {
                            if(mainPictureInputPath!=null){
                                byte[] mainPictureContent = fileApi.getFileContent(mainPictureInputPath.substring(mainPictureInputPath.lastIndexOf("get/") + 4));
                                File inputFile = FileUtils.createTempFile(mainPictureContent);
                                BufferedImage image = ImageIO.read(inputFile);
                                Double width = Double.valueOf(image.getWidth());
                                Double height = Double.valueOf(image.getHeight());
                                WriteCellData<Void> voidWriteCellData = ExcelUtils.imageCells(mainPictureContent,width,height,2.0,2.0,0,0);
                                item.setCptp(voidWriteCellData);
                                inputFile.delete();
                            }
                        } catch (Exception e) {
                            logger.info("导出图片获取失败"+e.getMessage());
                        }
                    }
                }
                processShipmentItemExportData(shipmentItemList, params,shipmentDO);
                break;
            }
            case "detail":{
                params.put("invoiceCode", shipmentDO.getInvoiceCode());
                params.put("destinationPortName", shipmentDO.getDestinationPortName());
                processShipmentItemExportData(shipmentItemList, params,shipmentDO);
                break;
            }
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "托单.xlsx", shipmentItemExportVOList, null, 0, handlers);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    public HashMap<String, Object> getBookingNoteParams(ShipmentDO shipmentDO , List<ShipmentItem> shipmentItemList ){
        HashMap<String, Object> params = new HashMap();
        Long companyId = shipmentDO.getForeignTradeCompanyId();
        if (Objects.nonNull(companyId)){
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            if (Objects.nonNull(companyDTO)){
                params.put("name", companyDTO.getName());
                params.put("companyNameEng", companyDTO.getCompanyNameEng());
                params.put("companyName", companyDTO.getCompanyName());
                params.put("companyAddressEng", companyDTO.getCompanyAddressEng());
            }
        }
        params.put("frontShippingMark", shipmentDO.getFrontShippingMark());
        String contractCode = shipmentItemList.stream()
                .map(ShipmentItem::getSaleContractCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(CommonDict.COMMA));
        params.put("contractCode", contractCode);
        params.put("custPo", shipmentItemList.stream()
                .map(ShipmentItem::getCustPo)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(CommonDict.COMMA)));
        params.put("invoiceCode", shipmentDO.getInvoiceCode());
        params.put("custContractCode", shipmentDO.getCustContractCode());
        params.put("departurePortName", shipmentDO.getDeparturePortName());
        params.put("destinationPortName", shipmentDO.getDestinationPortName());
        params.put("billLadingNum", shipmentDO.getBillLadingNum());
        UserDept documenter = shipmentDO.getDocumenter();
        Long userId = documenter.getUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        if (Objects.isNull(user)) {
            throw exception(DOCUMENTER_IS_EMPTY);
        }
        String tel = user.getTel();
        String email = user.getEmail();
        params.put("documenterTel", tel);
        params.put("documenterEmail", email);
        params.put("documenter", documenter.getNickname());
        return params;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendShipment(AppendShipmentReq req) {
        ShipmentDO shipmentDO = shipmentMapper.selectById(req.getShipmentId());
        if (Objects.isNull(shipmentDO)) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        // 根据ids将出运计划明细都查出来
        List<ShipmentPlanItem> shipmentPlanItemList = shipmentPlanService.getShipmentPlanItemList(req.getShipmentPlanIds());
        // 根据出运计划单分组
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            throw exception(SHIPMENT_PLAN_ITEM_NOT_EXISTS);
        }
        List<ShipmentPlanItem>  filterShipmentPlanItemList= shipmentPlanItemList.stream().filter(s->!ShippingPlanStatusEnum.COMPLETED.getValue().equals(s.getStatus())&&s.getShippingQuantity() > 0).toList();
        if (CollUtil.isEmpty(filterShipmentPlanItemList)){
            throw exception(SHIPMENT_PLAN_ERROR);
        }
        shipmentPlanItemList.forEach(s -> {
            s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
        });
        //更新明细状态更新为已完成
        shipmentPlanItemMapper.updateBatch(shipmentPlanItemList);
        List<Long> planItemIds = filterShipmentPlanItemList.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().toList();
        List<ShipmentPlanDO> shipmentPlanList = shipmentPlanService.getShipmentPlanList(planItemIds);
        if (CollUtil.isEmpty(shipmentPlanList)) {
            return;
        }

        shipmentPlanList.forEach(s -> {
            //所有出运计划明细状态为已完成将出运计划状态修改为已完成
            List<Integer> shipmentItemStatusList = shipmentPlanMapper.getShipmentPlanItemStatus(s.getId());
            if (CollUtil.isEmpty(shipmentItemStatusList)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否出运计划明细全部完成
            boolean allCompleted = shipmentItemStatusList.stream()
                    .allMatch(status -> status.equals(ShippingPlanStatusEnum.COMPLETED.getValue()));
            if (allCompleted) {
                s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
            }
        });
        shipmentPlanMapper.updateBatch(shipmentPlanList);

        List<ShipmentItem> shipmentItemList = ShipmentConvert.INSTANCE.convertShipmentItemList(filterShipmentPlanItemList);
        List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        List<SaleContractItemSaveDTO> SaleContractItemSaveDTOList = saleContractApi.listSaleContractItem(saleItemIdList);
        Map<Long, Integer> billStatusMap = SaleContractItemSaveDTOList.stream().collect(Collectors.toMap(SaleContractItemSaveDTO::getId, SaleContractItemSaveDTO::getBillStatus, (s1, s2) -> s2));
        Map<Long, Integer> quantityMap = purchaseContractApi.getPurchaseQuantityBySaleContractItemIds(saleItemIdList);
        List<String> skuCodeList = filterShipmentPlanItemList.stream().map(ShipmentPlanItem::getSkuCode).distinct().toList();
        Map<String, String> skuMap = skuApi.getHsDataUnitBySkuCodes(skuCodeList);
        List<DictDataRespDTO> dictDataList = dictTypeApi.getHsDataUnitList();
        if (CollUtil.isEmpty(dictDataList)) {
            throw exception(HSDATA_UNIT_NOT_EXISTS);
        }
        Map<Long, Integer> invoicedQuantityMap = CalculateQuantity(shipmentItemList);
        shipmentItemList.forEach(s -> {
            s.setId(null);
            s.setShipmentId(shipmentDO.getId());
            if (CollUtil.isNotEmpty(skuMap)) {
                String s1 = skuMap.get(s.getSkuCode());
                if (StrUtil.isNotEmpty(s1)) {
                    Optional<DictDataRespDTO> first = dictDataList.stream().filter(d -> Objects.equals(d.getLabel(), s1)).findFirst();
                    first.ifPresent(dictDataRespDTO -> s.setHsMeasureUnit(dictDataRespDTO.getValue()));
                }
            }
            if (Objects.nonNull(quantityMap)) {
                Integer quantity = quantityMap.get(s.getSaleContractItemId());
                if (Objects.isNull(quantity)) {
                    quantity = 0;
                }
                s.setPurchaseTotalQuantity(quantity);
            }
            if (CollUtil.isNotEmpty(invoicedQuantityMap)) {
                s.setThisPurchaseQuantity(BigDecimal.valueOf(invoicedQuantityMap.get(s.getSaleContractItemId())));
            }
            if (CollUtil.isNotEmpty(billStatusMap)) {
                s.setBillStatus(billStatusMap.get(s.getSaleContractItemId()));
            }
            s.setSourceSaleContractCode(s.getSaleContractCode());
        });
        calcStockCost(shipmentItemList);
        shipmentItemMapper.insertBatch(shipmentItemList);
        saleContractApi.batchUpdateShipmentTotalQuantity(BeanUtils.toBean(shipmentItemList, ShipmentQuantityDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> batrchTansformShipment(List<Long> ids) {
        List<CreatedResponse> responses = new ArrayList<>();

        List<ShipmentPlanItem> oldShipmentPlanItemList = shipmentPlanService.getShipmentPlanItemList(ids);
        if (CollUtil.isEmpty(oldShipmentPlanItemList)) {
            return responses;
        }
        //出运计划明细状态改为已完成
        oldShipmentPlanItemList.forEach(s -> {
            s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
        });

        //更新明细状态更新为已完成
        shipmentPlanItemMapper.updateBatch(oldShipmentPlanItemList);
        List<ShipmentPlanItem> shipmentPlanItemList = oldShipmentPlanItemList.stream().filter(s -> Objects.nonNull(s.getShippingQuantity()) && s.getShippingQuantity() > 0).toList();
        List<String> saleCodes = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSaleContractCode).distinct().toList();
        Map<String, SmsContractAllDTO> contractAllDTOMap = saleContractApi.getSmsByCodeList(saleCodes);
        if (CollUtil.isEmpty(contractAllDTOMap)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }

        Map<String, JsonCompanyPath> companyPathMap = contractAllDTOMap.values().stream().collect(Collectors.toMap(SmsContractAllDTO::getCode, SmsContractAllDTO::getCompanyPath));
        Map<String, SimpleCompanyDTO> companyDTOMap = getForeignTradeCompany(companyPathMap);
        if (CollUtil.isEmpty(companyDTOMap)) {
            return responses;
        }
        List<Long> companyIds = companyDTOMap.values().stream().map(SimpleCompanyDTO::getId).distinct().toList();
        if (CollUtil.isNotEmpty(companyIds) && companyIds.size() > 1) {
            throw exception(FOREIGN_TRADE_COMPANY_NOT_SAME);
        }

//        Map<Long, List<ShipmentPlanItem>> shipmentPlanMap = shipmentPlanItemList.stream().collect(Collectors.groupingBy(ShipmentPlanItem::getShipmentPlanId));
        // 批量查出出运计划单单据信息
        List<Long> shipmentPlanIds = shipmentPlanItemList.stream().map(ShipmentPlanItem::getShipmentPlanId).distinct().toList();
        List<ShipmentPlanDO> shipmentPlanList = shipmentPlanService.getShipmentPlanList(shipmentPlanIds);
        if (CollUtil.isEmpty(shipmentPlanList)) {
            return responses;
        }

        shipmentPlanList.forEach(s -> {
            //所有出运计划明细状态为已完成将出运计划状态修改为已完成
            List<Integer> shipmentItemStatusList = shipmentPlanMapper.getShipmentPlanItemStatus(s.getId());
            if (CollUtil.isEmpty(shipmentItemStatusList)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否出运计划明细全部完成
            boolean allCompleted = shipmentItemStatusList.stream()
                    .allMatch(status -> status.equals(ShippingPlanStatusEnum.COMPLETED.getValue()));
            if (allCompleted) {
                s.setStatus(ShippingPlanStatusEnum.COMPLETED.getValue());
            }
        });
        shipmentPlanMapper.updateBatch(shipmentPlanList);

        Map<Long, ShipmentPlanDO> shipmentPlanDOMap =
                shipmentPlanList.stream().collect(Collectors.toMap(ShipmentPlanDO::getId, s -> s));
//        List<cshipmentPlanIds> createList = new ArrayList<>();


        List<String> skuCodeList = shipmentPlanItemList.stream().map(ShipmentPlanItem::getSkuCode).distinct().toList();
        Map<String, String> skuMap = skuApi.getHsDataUnitBySkuCodes(skuCodeList);
        List<DictDataRespDTO> dictDataList = dictTypeApi.getHsDataUnitList();
        if (CollUtil.isEmpty(dictDataList)) {
            throw exception(HSDATA_UNIT_NOT_EXISTS);
        }

//        shipmentPlanMap.entrySet().forEach(s -> {

        Long shipmentPlanId = shipmentPlanIds.get(0);
        List<ShipmentPlanItem> shipmentPlanItems = shipmentPlanItemList;
        ShipmentPlanDO shipmentPlanDO = shipmentPlanDOMap.get(shipmentPlanId);
        if (Objects.isNull(shipmentPlanDO)) {
            return responses;
        }
        ShipmentSaveReqVO shipmentSaveReqVO = ShipmentConvert.INSTANCE.convertShipmentSaveReqVO(shipmentPlanDO);
        //设置公司主体为外贸公司
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        SaleContractSaveDTO saleContractInfoByCode = saleContractApi.getSaleContractInfoByCode(shipmentPlanDO.getSaleContractCode());
        if (Objects.nonNull(saleContractInfoByCode)) {
            JsonCompanyPath companyPath = saleContractInfoByCode.getCompanyPath();
            while (companyPath != null) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyMap.get(companyPath.getId());
                if (Objects.nonNull(simpleCompanyDTO) && Objects.equals(simpleCompanyDTO.getCompanyNature(), CompanyNatureEnum.FOREIGN_TRADE.getValue())) {
                    shipmentSaveReqVO.setForeignTradeCompanyId(simpleCompanyDTO.getId()).setForeignTradeCompanyName(simpleCompanyDTO.getName());
                    break;
                }
                companyPath = companyPath.getNext();
            }
        }

        List<ShipmentItem> shipmentItemList = ShipmentConvert.INSTANCE.convertShipmentItemList(shipmentPlanItems);
        List<Long> saleItemIdList = shipmentItemList.stream().map(ShipmentItem::getSaleContractItemId).distinct().toList();
        List<SaleContractItemSaveDTO> SaleContractItemSaveDTOList = saleContractApi.listSaleContractItem(saleItemIdList);
        Map<Long, Integer> billStatusMap = SaleContractItemSaveDTOList.stream().collect(Collectors.toMap(SaleContractItemSaveDTO::getId, SaleContractItemSaveDTO::getBillStatus, (s1, s2) -> s2));
        Map<Long, Integer> quantityMap = purchaseContractApi.getPurchaseQuantityBySaleContractItemIds(saleItemIdList);
//            计算允许开票数量
        Map<Long, Integer> invoicedQuantityMap = CalculateQuantity(shipmentItemList);
        shipmentItemList.forEach(si -> {
            if (CollUtil.isNotEmpty(skuMap)) {
                String s1 = skuMap.get(si.getSkuCode());
                if (StrUtil.isNotEmpty(s1)) {
                    Optional<DictDataRespDTO> first = dictDataList.stream().filter(d -> Objects.equals(d.getLabel(), s1)).findFirst();
                    first.ifPresent(dictDataRespDTO -> si.setHsMeasureUnit(dictDataRespDTO.getValue()));
                }
            }
            if (Objects.nonNull(quantityMap)) {
                Integer quantity = quantityMap.get(si.getSaleContractItemId());
                if (Objects.isNull(quantity)) {
                    quantity = 0;
                }
                si.setPurchaseTotalQuantity(quantity);
            }
            if (CollUtil.isNotEmpty(invoicedQuantityMap)) {
                si.setThisPurchaseQuantity(BigDecimal.valueOf(invoicedQuantityMap.get(si.getSaleContractItemId())));
            }
            if (CollUtil.isNotEmpty(billStatusMap)) {
                si.setBillStatus(billStatusMap.get(si.getSaleContractItemId()));
            }
            si.setSourceSaleContractCode(si.getSaleContractCode());
        });
        UserDept inputUser = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
        shipmentSaveReqVO.setInputUser(inputUser);
        shipmentSaveReqVO.setInputDate(LocalDateTime.now());
        shipmentSaveReqVO.setChildren(shipmentItemList);

        responses.addAll(createShipment(shipmentSaveReqVO));
//            createList.add(shipmentSaveReqVO);
//        });
//        if (CollUtil.isNotEmpty(createList)) {
//            createList.forEach(s->{
//                List<CreatedResponse> res = createShipment(s);
//                if(CollUtil.isNotEmpty(res)&&res.size()>0){
//                    responses.add(res.get(0));
//                }
//            });
//        }
//        销售合同回写已转出运明细数量
//        List<ShipmentItem> shipmentItemList = createList.stream().filter(s -> s.getChildren() != null).flatMap(s -> s.getChildren().stream()).toList();
        saleContractApi.batchUpdateShipmentTotalQuantity(BeanUtils.toBean(shipmentSaveReqVO.getChildren(), ShipmentQuantityDTO.class));

        return responses;


//        return   transformShipment(ids);
    }

    @Override
    public void genInternalContract(Long shipmentId) {
        ShipmentDO shipmentDO = validateShipmentExists(shipmentId);
        String oldUniqueCode = shipmentDO.getGenContractUniqueCode();
        if (StrUtil.isNotEmpty(oldUniqueCode)){
            saleContractApi.deleteGenContract(oldUniqueCode);
        }
        String genContractUniqueCode = IdUtil.fastSimpleUUID();
        shipmentMapper.updateById(new ShipmentDO().setId(shipmentId).setGenContractUniqueCode(genContractUniqueCode));
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
        if (CollUtil.isEmpty(shipmentItems)){
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        // 生成内部购销合同
        Map<Long, Integer> shipmentMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        Map<Long, Integer> shipmentPurchaseMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(s->s.getThisPurchaseQuantity().intValue())));
        Map<Long,SimpleShipment> simpleShipmentMap = new HashMap<>();
        shipmentItems.forEach(s->{
            if (shipmentPurchaseMap.containsKey(s.getSaleContractItemId())){
                return;
            }
            simpleShipmentMap.put(s.getSaleContractItemId(),BeanUtils.toBean(s, SimpleShipment.class));

        });
        saleContractApi.genInternalContract(shipmentMap,shipmentPurchaseMap,genContractUniqueCode,simpleShipmentMap);
    }

    @Override
    public void deleteGenContract(Long shipmentId) {
        ShipmentDO shipmentDO = validateShipmentExists(shipmentId);
        String genContractUniqueCode = shipmentDO.getGenContractUniqueCode();
        if (StrUtil.isEmpty(genContractUniqueCode)){
            return;
        }
        // 删除内部销售合同
        saleContractApi.deleteGenContract(genContractUniqueCode);
        // 删除内部采购合同
        purchaseContractApi.deleteGenContract(genContractUniqueCode);
    }

    private Map<String, SimpleCompanyDTO> getForeignTradeCompany(Map<String, JsonCompanyPath> companyPathMap) {
        Map<String, SimpleCompanyDTO> map = new HashMap<>();
        if (companyPathMap == null) {
            return map;
        }
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        for (Map.Entry<String, JsonCompanyPath> entry : companyPathMap.entrySet()) {
            String saleCode = entry.getKey();
            JsonCompanyPath companyPath = entry.getValue();
            SimpleCompanyDTO companyDTO = getForeignTradeCompany(companyPath, simpleCompanyMap);
            if (companyDTO != null) {
                map.put(saleCode, companyDTO);
            }
        }
        return map;
    }

    private SimpleCompanyDTO getForeignTradeCompany(JsonCompanyPath companyPath, Map<Long, SimpleCompanyDTO> companyDTOMap) {
        if (CollUtil.isEmpty(companyDTOMap) || companyPath == null) {
            return null;
        }
        List<SimpleCompanyDTO> simpleCompanyDTOS = companyDTOMap.values().stream().filter(s -> s.getCompanyNature() == CompanyNatureEnum.FOREIGN_TRADE.getValue()).toList();
        if (CollUtil.isEmpty(simpleCompanyDTOS)) {
            throw exception(FOREIGN_TRADE_COMPANY_NOT_EXITS);
        }
        List<Long> tradeIds = simpleCompanyDTOS.stream().map(SimpleCompanyDTO::getId).distinct().toList();
        JsonCompanyPath current = companyPath;
        Long targetId = null;
        String targetName = null;
        while (current != null) {
            if (current.getId() != null && tradeIds.contains(current.getId())) {
                targetId = current.getId();
                targetName = current.getName();
                break;
            }
            current = current.getNext();
        }
        if (targetId == null) {
            return null;
        }

        // 从map中获取对应的DTO或创建新的
        SimpleCompanyDTO dto = companyDTOMap.get(targetId);
        if (dto != null) {
            // 如果map中已有DTO，可以返回它或创建副本
            SimpleCompanyDTO result = new SimpleCompanyDTO();
            result.setId(dto.getId());
            result.setName(dto.getName());
            // 复制其他必要字段...
            return result;
        } else {
            // 如果map中没有，但id存在，创建新的DTO
            SimpleCompanyDTO result = new SimpleCompanyDTO();
            result.setId(targetId);
            result.setName(targetName != null ? targetName : "");
            // 设置其他默认字段...
            return result;
        }
    }

    /**
     * 校验反审核状态
     *
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        Long count = shipmentMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    /**
     * 根据单据数量更新出运单明细状态
     *
     * @param shipmentItem 出运单明细
     */
    private void updateShipmentItemStatus(ShipmentItem shipmentItem) {
        // 出运数量
        Integer shippingQuantity = Objects.isNull(shipmentItem.getShippingQuantity()) ? CalculationDict.ZERO : shipmentItem.getShippingQuantity();
        // 结汇数量
        Integer settlementQuantity = Objects.isNull(shipmentItem.getSettlementQuantity()) ? CalculationDict.ZERO : shipmentItem.getSettlementQuantity();
        // 报关数量
        Integer declaredQuantity = Objects.isNull(shipmentItem.getDeclaredQuantity()) ? CalculationDict.ZERO : shipmentItem.getDeclaredQuantity();

        // 计算结汇状态
        if (settlementQuantity == 0) {
            shipmentItem.setIsToSettlementForm(ToSettlementEnum.NOT_YET_TRANSFERRED.getStatus());
        } else {
            if (NumUtil.compare(shippingQuantity, settlementQuantity) == 0) {
                shipmentItem.setIsToSettlementForm(ToSettlementEnum.TRANSFERRED.getStatus());
            } else {
                shipmentItem.setIsToSettlementForm(ToSettlementEnum.PARTIAL_CONVERSION.getStatus());
            }
        }
        // 计算报关状态
        if (declaredQuantity == 0) {
            shipmentItem.setIsToDeclaration(ToDeclarationEnum.NOT_YET_TRANSFERRED.getStatus());
        } else {
            if (NumUtil.compare(shippingQuantity, declaredQuantity) == 0) {
                shipmentItem.setIsToDeclaration(ToDeclarationEnum.TRANSFERRED.getStatus());
            } else {
                shipmentItem.setIsToDeclaration(ToDeclarationEnum.PARTIAL_CONVERSION.getStatus());
            }
        }
    }

    private List<ShipmentItem> validateShipmentItemExists(List<Long> ids) {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(shipmentItemList)) {
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        long count = shipmentItemList.stream().filter(s -> !ids.contains(s.getId())).count();
        if (count > 0) {
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        return shipmentItemList;
    }

    @Override
    public PurchasePlanInfoDTO listManufactureSku(String purchaseContractCode, Long saleContractItemId) {
        Long purchasePlanId;
        if (StrUtil.isNotEmpty(purchaseContractCode)) {
            PurchaseContractAllDTO purchaseContractAllDTO = purchaseContractApi.getPurchaseContractByCode(purchaseContractCode);
            if (ObjectUtil.isNull(purchaseContractAllDTO)) {
                throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
            }
            purchasePlanId = purchaseContractAllDTO.getPurchasePlanId();
        } else {
            purchasePlanId = purchaseContractApi.getPlanIdBySaleItemId(saleContractItemId);
        }
        PurchasePlanInfoDTO purchasePlanInfoDTO = purchasePlanApi.getPurchasePlan(purchasePlanId);
        if (ObjectUtil.isNull(purchasePlanInfoDTO)) {
            throw exception(PURCHASE_PLAN_NOT_EXISTS);
        }
        List<PurchasePlanItemDTO> purchasePlanItemDTOList = purchasePlanInfoDTO.getPurchasePlanItemDTOList();
        if (CollUtil.isEmpty(purchasePlanItemDTOList)) {
            throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
        }
        // 过滤出组合产品并拆分采购的一、二级产品列表
        List<PurchasePlanItemDTO> combinePlanItemDTOList = purchasePlanItemDTOList.stream()
                .filter(x -> x.getPurchaseModel().intValue() == PurchaseModelEnum.COMBINE.getCode()
                        && x.getSkuType().intValue() == SkuTypeEnum.PRODUCT_MIX.getValue()
                        && (x.getLevels() == 1 || x.getLevels() == 2)).toList();
        if (CollUtil.isEmpty(combinePlanItemDTOList)) {
            throw exception(PURCHASE_PLAN_COMBINE_ITEM_NOT_EXISTS);
        }
        purchasePlanInfoDTO.setPurchasePlanItemDTOList(combinePlanItemDTOList);
        return purchasePlanInfoDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> manufactureSku(String skuCode, Integer processingNum, Long saleContractItemId) {
        List<String> batchCodeList = new ArrayList<>();
        if (processingNum <= 0) {
            return batchCodeList;
        }
        // 销售明细信息
        SaleContractItemDTO saleContractItemDTO = saleContractApi.getSaleContractItemById(saleContractItemId);
        if (Objects.isNull(saleContractItemDTO)) {
            return batchCodeList;
        }
        String contractCode = saleContractItemDTO.getContractCode();
        Long contractId = saleContractItemDTO.getContractId();
        // 查询库存明细
        List<StockDTO> stockDTOList = iStockApi.getStockDTOBySaleContractCodes(Collections.singletonList(contractCode));
        // 查询锁定库存
        List<StockLockRespVO> stockLockList = iStockApi.listStockLock(new QueryStockReqVO().setSaleContractCode(contractCode));
        if (CollUtil.isNotEmpty(stockLockList)) {
            Map<String,String> lockSkuCodeMap = new HashMap<>();
            stockLockList.forEach(s->{
                lockSkuCodeMap.put(s.getBatchCode(),s.getSkuCode());
            });
            Map<String, Integer> stockLockMap = stockLockList.stream().collect(Collectors.toMap(StockLockRespVO::getBatchCode, StockLockRespVO::getLockQuantity, Integer::sum));
            List<StockDTO> stockDTOByBatchCodes = iStockApi.getStockDTOByBatchCodes(stockLockMap.keySet());
            if (CollUtil.isNotEmpty(stockDTOByBatchCodes)) {
                if (CollUtil.isEmpty(stockDTOList)) {
                    stockDTOList = stockDTOByBatchCodes;
                } else {
                    // 避免相同批次号重复
                    List<StockDTO> stockDTOS = new ArrayList<>();
                    List<String> checkBatchCodeList = stockDTOList.stream().map(StockDTO::getBatchCode).toList();
                    stockDTOByBatchCodes.forEach(x -> {
                        if (!checkBatchCodeList.contains(x.getBatchCode())) {
                            Integer lockQuantity = stockLockMap.get(x.getBatchCode());
                            if (Objects.isNull(lockQuantity) || lockQuantity == CalculationDict.ZERO) {
                                return;
                            }
                            x.setAvailableQuantity(lockQuantity);
                            x.setStockMethod(StockMethodEnum.LOCK.getType());
                            x.setSkuCode(lockSkuCodeMap.get(x.getBatchCode()));
                            stockDTOS.add(x);
                        }
                    });
                    if (CollUtil.isNotEmpty(stockDTOS)) {
                        stockDTOList.addAll(stockDTOS);
                    }
                }
            }
        }
        if (CollectionUtil.isEmpty(stockDTOList)) {
            throw exception(CHILDREN_STOCK_NOT_EXISTS);
        }
        Map<String, List<StockLockRespVO>> stockLockMap = stockLockList.stream().collect(Collectors.groupingBy(StockLockRespVO::getSkuCode));
        Map<String, List<StockDTO>> skuStockMap = stockDTOList.stream().collect(Collectors.groupingBy(StockDTO::getSkuCode));
        // 一级产品
        List<SplitPurchase> splitPurchaseList = saleContractItemDTO.getSplitPurchaseList();
        // 加工主体
        Long manufactureCompanyId = saleContractApi.getManufactureCompanyIdByItemId(saleContractItemId);
        // 采购主体缓存
        Map<String, List<SplitPurchase>> combineCollect = splitPurchaseList.stream().collect(Collectors.groupingBy(SplitPurchase::getSkuCode));
        // 二级产品
        List<SkuBomDTO> secondSkuList = skuApi.getSonSkuListBySkuCode(skuCode);
        // 二级产品子产品明细Map，即二三级产品关系
        Map<String, List<SkuBomDTO>> secondSonSkuMap = new HashMap<>();
        if (!CollUtil.isEmpty(secondSkuList)) {
            secondSkuList.stream().forEach(x -> {
                x.setQty(processingNum * x.getQty());
                String secondSkuCode = x.getSkuCode();
                List<SkuBomDTO> thirdSkuList = skuApi.getSonSkuListBySkuCode(secondSkuCode);
                if (CollUtil.isNotEmpty(thirdSkuList)) {
                    thirdSkuList.forEach(s -> {
                        s.setQty(x.getQty() * s.getQty());
                    });
                    secondSonSkuMap.put(secondSkuCode, thirdSkuList);
                }
            });
        }

        // 加工单-产品列表
        List<ManufactureSkuSaveDTO> manufactureSkuSaveDTOList = new ArrayList<>();
        // 查询中转仓库
//        WarehouseDTO warehouse = warehouseApi.getWarehouse(CommonDict.EMPTY_STR, CommonDict.TRANSFER);
//        if (warehouse == null) {
//            throw exception(ZZHOUSE_NOT_EXISTS);
//        }

        WarehouseDTO warehouse = warehouseApi.getDefaultWareMouse();
        // 1 三级产品出库、二级产品入库
        if (!secondSonSkuMap.isEmpty()) {
            secondSonSkuMap.forEach((secondSkuCode, thirdSkuDTOList) -> {
                // 子产品出库单（三级产品出库）
                BillSaveReqVO childrenBillSaveReqVO = new BillSaveReqVO();
                childrenBillSaveReqVO.setSaleContractCode(contractCode);
                childrenBillSaveReqVO.setSaleContractId(contractId);
                List<BillItemSaveReqVO> childrenBillItemSaveReqVOList = new ArrayList<>();
                // 加工单-子产品列表
                List<ManufactureSkuItemSaveDTO> manufactureSkuItemSaveDTOList = new ArrayList<>();
                // sku信息Map
                List<Long> stockSkuIdList = thirdSkuDTOList.stream().map(SkuBomDTO::getSkuId).distinct().collect(Collectors.toList());
                Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(stockSkuIdList);
                thirdSkuDTOList.forEach(x -> {
                    List<StockDTO> stockDTOs = skuStockMap.get(x.getSkuCode());
                    if (CollUtil.isEmpty(stockDTOs)){
                        return;
                    }
                    StockDTO stockDTO = stockDTOs.get(0);
                    if (CollUtil.isNotEmpty(stockDTOs)) {
                        AtomicReference<Integer> quantity = new AtomicReference<>(x.getQty());
                        stockDTOs.forEach(s -> {
                            // 仅加工拆分主体的库存
                            if (!manufactureCompanyId.equals(s.getCompanyId()) || quantity.get() == 0) {
                                return;
                            }
                            int stockQuantity = s.getAvailableQuantity() + s.getProducingQuantity();
                            if (stockQuantity == 0) {
                                return;
                            }
                            Integer actQuantity = 0;
                            if (stockQuantity >= quantity.get()) {
                                actQuantity = quantity.get();
                                quantity.set(0);
                            } else {
                                actQuantity = stockQuantity;
                                quantity.set(quantity.get() - stockQuantity);
                            }
                            BillItemSaveReqVO sonBillItemSaveReqVO = BeanUtils.toBean(s, BillItemSaveReqVO.class);
                            sonBillItemSaveReqVO.setOrderQuantity(actQuantity).setActQuantity(actQuantity);
                            sonBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.MANUSACTURE.getValue());
                            BigDecimal boxQuantity = NumUtil.div(actQuantity, stockDTO.getQtyPerOuterbox()).setScale(2, RoundingMode.HALF_UP);
                            sonBillItemSaveReqVO.setOrderBoxQuantity(boxQuantity.intValue());
                            sonBillItemSaveReqVO.setActBoxQuantity(boxQuantity.intValue());
                            sonBillItemSaveReqVO.setSales(saleContractItemDTO.getSales());
                            childrenBillItemSaveReqVOList.add(sonBillItemSaveReqVO);
                        });

                    }
                    if (ObjectUtil.isNotNull(stockDTO)) {
                        List<StockLockRespVO> stockDetailRespVOS = stockLockMap.get(x.getSkuCode());
                        Integer quantity;
                        if (CollUtil.isEmpty(stockDetailRespVOS)) {
                            quantity = stockDTOs.stream().map(StockDTO::getAvailableQuantity).reduce(CalculationDict.ZERO, Integer::sum);
                        } else {
                            quantity = stockDetailRespVOS.stream().map(StockLockRespVO::getLockQuantity).reduce(CalculationDict.ZERO, Integer::sum);
                        }
                        // 二级产品-子产品加工单明细（三级产品）
                        ManufactureSkuItemSaveDTO manufactureSkuItemSaveDTO = new ManufactureSkuItemSaveDTO();
                        manufactureSkuItemSaveDTO.setSkuId(stockDTO.getSkuId());
                        manufactureSkuItemSaveDTO.setSkuCode(stockDTO.getSkuCode());
                        manufactureSkuItemSaveDTO.setSkuName(stockDTO.getSkuName());
                        manufactureSkuItemSaveDTO.setQuantity(quantity);
                        List<StockDTO> stockList = stockDTOs.stream().filter(s -> s.getAvailableQuantity() > CalculationDict.ZERO && manufactureCompanyId.equals(s.getCompanyId())).toList();
                        if (CollUtil.isNotEmpty(stockList)) {
                            manufactureSkuItemSaveDTO.setStockList(BeanUtils.toBean(stockList, JsonStockDTO.class));
                        }
                        manufactureSkuItemSaveDTO.setRatio(x.getQty());
                        // sku图片
                        SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                        SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                        if (ObjectUtil.isNotNull(skuDTO)) {
                            List<SimpleFile> pictureList = skuDTO.getPicture();
                            simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                        }
                        manufactureSkuItemSaveDTO.setMainPicture(simpleFile);
                        manufactureSkuItemSaveDTOList.add(manufactureSkuItemSaveDTO);
                    }
                });
                // 父级产品库存足够则不需要处理三级产品出入库
                if (CollUtil.isEmpty(childrenBillItemSaveReqVOList)){
                    return;
                }
                childrenBillSaveReqVO.setBillItemSaveReqVOList(childrenBillItemSaveReqVOList);

                // 父产品入库单（二级产品入库）
                BillSaveReqVO parentBillSaveReqVO = new BillSaveReqVO();
                List<SplitPurchase> purchasePlanItemDTOList = combineCollect.get(secondSkuCode);
                SplitPurchase purchasePlanItemDTO = purchasePlanItemDTOList.get(0);
                Long purchaseCompanyId = purchasePlanItemDTO.getPurchaseCompanyId();
                String purchaseCompanyName = purchasePlanItemDTO.getPurchaseCompanyName();
                parentBillSaveReqVO.setSaleContractId(contractId).setSaleContractCode(contractCode);
                parentBillSaveReqVO.setCompanyId(purchaseCompanyId).setCompanyName(purchaseCompanyName);
                parentBillSaveReqVO.setWarehouseId(warehouse.getId()).setWarehouseName(warehouse.getName());

                BillItemSaveReqVO parentBillItemSaveReqVO = BeanUtils.toBean(purchasePlanItemDTO, BillItemSaveReqVO.class);
                parentBillItemSaveReqVO.setSortNum(CalculationDict.ONE).setSourceSortNum(purchasePlanItemDTO.getSortNum());

                Integer saleQuantity = purchasePlanItemDTOList.stream().map(SplitPurchase::getSaleQuantity).reduce(CalculationDict.ZERO, Integer::sum);
                parentBillItemSaveReqVO.setPosition(CommonDict.DEFAULT_POSITION).setOrderQuantity(saleQuantity).setActQuantity(saleQuantity);
                parentBillItemSaveReqVO.setWarehouseId(warehouse.getId()).setWarehouseName(warehouse.getName());
                parentBillItemSaveReqVO.setCompanyId(purchaseCompanyId).setCompanyName(purchaseCompanyName);
                parentBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.MANUSACTURE.getValue());
                BillItemSaveReqVO sonBillItem = childrenBillItemSaveReqVOList.get(0);
                Long sonManufactureCompanyId = sonBillItem.getCompanyId();
                SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(sonManufactureCompanyId);
                if (Objects.nonNull(simpleVenderRespDTO)) {
                    parentBillItemSaveReqVO.setVenderName(simpleVenderRespDTO.getName());
                    parentBillItemSaveReqVO.setVenderCode(simpleVenderRespDTO.getCode());
                    parentBillItemSaveReqVO.setVenderId(simpleVenderRespDTO.getId());
                } else {
                    parentBillItemSaveReqVO.setVenderName(sonBillItem.getVenderName());
                }
                parentBillItemSaveReqVO.setCustName(saleContractItemDTO.getCustName());
                Integer boxQuantity = NumberUtil.div(processingNum, purchasePlanItemDTO.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
                parentBillItemSaveReqVO.setOrderQuantity(processingNum).setOrderBoxQuantity(boxQuantity);
                parentBillItemSaveReqVO.setActQuantity(processingNum).setActBoxQuantity(boxQuantity);
                JsonAmount price = skuApi.getCombSkuPrice(parentBillItemSaveReqVO.getSkuId());
                parentBillItemSaveReqVO.setPrice(price);
                parentBillSaveReqVO.setBillItemSaveReqVOList(Collections.singletonList(parentBillItemSaveReqVO));
                parentBillItemSaveReqVO.setSales(saleContractItemDTO.getSales());

                // 二级产品-组装信息
                ManufactureSkuSaveDTO manufactureSkuSaveDTO = new ManufactureSkuSaveDTO();
                manufactureSkuSaveDTO.setSkuId(parentBillItemSaveReqVO.getSkuId());
                manufactureSkuSaveDTO.setSkuCode(parentBillItemSaveReqVO.getSkuCode());
                manufactureSkuSaveDTO.setSkuName(parentBillItemSaveReqVO.getSkuName());
                manufactureSkuSaveDTO.setCskuCode(parentBillItemSaveReqVO.getCskuCode());
                manufactureSkuSaveDTO.setQuantity(purchasePlanItemDTO.getSaleQuantity());
                manufactureSkuSaveDTO.setMainPicture(purchasePlanItemDTO.getMainPicture());
                manufactureSkuSaveDTO.setSmsContractId(contractId).setSmsContractCode(contractCode);
                manufactureSkuSaveDTO.setSkuItemList(manufactureSkuItemSaveDTOList);
                manufactureSkuSaveDTOList.add(manufactureSkuSaveDTO);
                if (CollUtil.isNotEmpty(childrenBillSaveReqVO.getBillItemSaveReqVOList())&&CollUtil.isNotEmpty(parentBillSaveReqVO.getBillItemSaveReqVOList())){
                    List<String> addBatchCodeList = iStockApi.handleManufactureBillAndStock(childrenBillSaveReqVO, parentBillSaveReqVO);
                    if (CollUtil.isNotEmpty(addBatchCodeList)){
                        batchCodeList.addAll(addBatchCodeList);
                    }
                }
            });
        }
        // 重新查询二级组合产品入库库存
        if (CollUtil.isNotEmpty(batchCodeList)){
            List<StockDTO> newStockDTOList = iStockApi.getStockDTOByBatchCodes(batchCodeList.stream().collect(Collectors.toSet()));
            if (CollUtil.isNotEmpty(newStockDTOList)){
                newStockDTOList.forEach(s->{
                    skuStockMap.merge(s.getSkuCode(), new ArrayList<>(), (old, now) -> {
                        if (CollUtil.isEmpty(old)){
                            return now;
                        }
                        old.addAll(now);
                        return old;
                    });
                });
            }
        }
        // 2 二级产品出库、一级产品入库
        // 子产品出库单（二级产品出库）
        BillSaveReqVO childrenBillSaveReqVO = new BillSaveReqVO();
        List<BillItemSaveReqVO> childrenBillItemSaveReqVOList = new ArrayList<>();
        // 加工单-子产品列表
        List<ManufactureSkuItemSaveDTO> manufactureSkuItemSaveDTOList = new ArrayList<>();
        // sku信息Map
        List<Long> stockSkuIdList = secondSkuList.stream().map(SkuBomDTO::getSkuId).distinct().collect(Collectors.toList());
        Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(stockSkuIdList);
        secondSkuList.forEach(x -> {
            List<StockDTO> stockDTOs = skuStockMap.get(x.getSkuCode());
            if (CollUtil.isNotEmpty(stockDTOs)) {
                StockDTO stockDTO = stockDTOs.get(0);
                AtomicReference<Integer> quantityAto = new AtomicReference<>(x.getQty());
                stockDTOs.forEach(s -> {
                    // 仅加工拆分主体的库存
                    if (!manufactureCompanyId.equals(s.getCompanyId()) || quantityAto.get() == 0) {
                        return;
                    }
                    int stockQuantity = s.getAvailableQuantity() + s.getProducingQuantity();
                    if (stockQuantity == 0) {
                        return;
                    }
                    Integer actQuantity = 0;
                    if (stockQuantity >= quantityAto.get()) {
                        actQuantity = quantityAto.get();
                        quantityAto.set(0);
                    } else {
                        actQuantity = stockQuantity;
                        quantityAto.set(quantityAto.get() - stockQuantity);
                    }
                    BillItemSaveReqVO sonBillItemSaveReqVO = BeanUtils.toBean(s, BillItemSaveReqVO.class);
                    sonBillItemSaveReqVO.setOrderQuantity(actQuantity).setActQuantity(actQuantity);
                    sonBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.MANUSACTURE.getValue());
                    BigDecimal boxQuantity = NumUtil.div(actQuantity, stockDTO.getQtyPerOuterbox()).setScale(2, RoundingMode.HALF_UP);
                    sonBillItemSaveReqVO.setOrderBoxQuantity(boxQuantity.intValue());
                    sonBillItemSaveReqVO.setActBoxQuantity(boxQuantity.intValue());
                    sonBillItemSaveReqVO.setSales(saleContractItemDTO.getSales());
                    childrenBillItemSaveReqVOList.add(sonBillItemSaveReqVO);
                });
                if (ObjectUtil.isNotNull(stockDTO)) {
                    Integer quantity = stockDTOs.stream().map(StockDTO::getAvailableQuantity).reduce(CalculationDict.ZERO, Integer::sum);
                    // 一级产品-加工单明细（二级产品）
                    ManufactureSkuItemSaveDTO manufactureSkuItemSaveDTO = new ManufactureSkuItemSaveDTO();
                    manufactureSkuItemSaveDTO.setSkuId(stockDTO.getSkuId());
                    manufactureSkuItemSaveDTO.setSkuCode(stockDTO.getSkuCode());
                    manufactureSkuItemSaveDTO.setSkuName(stockDTO.getSkuName());
                    manufactureSkuItemSaveDTO.setQuantity(quantity);
                    List<StockDTO> stockList = stockDTOs.stream().filter(s -> s.getAvailableQuantity() > CalculationDict.ZERO && manufactureCompanyId.equals(s.getCompanyId())).toList();
                    if (CollUtil.isNotEmpty(stockList)) {
                        manufactureSkuItemSaveDTO.setStockList(BeanUtils.toBean(stockList, JsonStockDTO.class));
                    }
                    manufactureSkuItemSaveDTO.setRatio(x.getQty());
                    // sku图片
                    SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                    SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                    if (ObjectUtil.isNotNull(skuDTO)) {
                        List<SimpleFile> pictureList = skuDTO.getPicture();
                        simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                    }
                    manufactureSkuItemSaveDTO.setMainPicture(simpleFile);
                    manufactureSkuItemSaveDTOList.add(manufactureSkuItemSaveDTO);
                }
            }
      
        });
        String venderName = "";
        if (CollUtil.isNotEmpty(childrenBillItemSaveReqVOList)){
            BillItemSaveReqVO billItem = childrenBillItemSaveReqVOList.get(0);
            if (Objects.nonNull(billItem)){
                venderName = billItem.getVenderName();
            }
            childrenBillSaveReqVO.setBillItemSaveReqVOList(childrenBillItemSaveReqVOList);
        }

        // 父产品入库单（一级产品入库）
        BillSaveReqVO parentBillSaveReqVO = BeanUtils.toBean(saleContractItemDTO,BillSaveReqVO.class);
        parentBillSaveReqVO.setSaleContractId(contractId).setSaleContractCode(contractCode);
        SimpleCompanyDTO manufactureCompany = companyApi.getCompanyDTO(manufactureCompanyId);
        parentBillSaveReqVO.setCompanyId(manufactureCompanyId).setCompanyName(manufactureCompany.getCompanyName());
        parentBillSaveReqVO.setWarehouseId(warehouse.getId()).setWarehouseName(warehouse.getName());
        BillItemSaveReqVO parentBillItemSaveReqVO = BeanUtils.toBean(saleContractItemDTO,BillItemSaveReqVO.class);
        parentBillItemSaveReqVO.setSkuName(saleContractItemDTO.getName());
        parentBillItemSaveReqVO.setSortNum(CalculationDict.ONE);
        parentBillItemSaveReqVO.setPosition(CommonDict.DEFAULT_POSITION).setActQuantity(processingNum);
        parentBillItemSaveReqVO.setWarehouseId(warehouse.getId()).setWarehouseName(warehouse.getName());
        parentBillItemSaveReqVO.setCompanyId(manufactureCompanyId).setCompanyName(manufactureCompany.getCompanyName());
        parentBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.MANUSACTURE.getValue());
        parentBillItemSaveReqVO.setSales(saleContractItemDTO.getSales());
        SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(manufactureCompanyId);
        if (Objects.nonNull(simpleVenderRespDTO)) {
            parentBillItemSaveReqVO.setVenderName(simpleVenderRespDTO.getName());
            parentBillItemSaveReqVO.setVenderCode(simpleVenderRespDTO.getCode());
            parentBillItemSaveReqVO.setVenderId(simpleVenderRespDTO.getId());
        } else {
            parentBillItemSaveReqVO.setVenderName(venderName);
        }
        parentBillItemSaveReqVO.setCustName(saleContractItemDTO.getCustName());
        Integer boxQuantity = NumberUtil.div(processingNum, saleContractItemDTO.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
        parentBillItemSaveReqVO.setOrderQuantity(processingNum).setActQuantity(processingNum).setOrderBoxQuantity(boxQuantity);
        parentBillItemSaveReqVO.setActBoxQuantity(boxQuantity);
        // 一级产品价格为子产品价格总和
        JsonAmount price = skuApi.getCombSkuPrice(parentBillItemSaveReqVO.getSkuId());
        parentBillItemSaveReqVO.setPrice(price);
        parentBillSaveReqVO.setBillItemSaveReqVOList(Collections.singletonList(parentBillItemSaveReqVO));
        // 一级产品-组装信息
        ManufactureSkuSaveDTO manufactureSkuSaveDTO = new ManufactureSkuSaveDTO();
        manufactureSkuSaveDTO.setSkuId(parentBillItemSaveReqVO.getSkuId());
        manufactureSkuSaveDTO.setSkuCode(parentBillItemSaveReqVO.getSkuCode());
        manufactureSkuSaveDTO.setSkuName(parentBillItemSaveReqVO.getSkuName());
        manufactureSkuSaveDTO.setCskuCode(parentBillItemSaveReqVO.getCskuCode());
        manufactureSkuSaveDTO.setQuantity(processingNum);
        manufactureSkuSaveDTO.setMainPicture(saleContractItemDTO.getMainPicture());
        manufactureSkuSaveDTO.setSmsContractId(contractId).setSmsContractCode(contractCode);
        manufactureSkuSaveDTO.setSkuItemList(manufactureSkuItemSaveDTOList);
        manufactureSkuSaveDTOList.add(manufactureSkuSaveDTO);

        List<String> addBatchCodeList = iStockApi.handleManufactureBillAndStock(childrenBillSaveReqVO, parentBillSaveReqVO);
        if (CollUtil.isNotEmpty(addBatchCodeList)){
            batchCodeList.addAll(addBatchCodeList);
        }

        // 3 创建加工单
        ManufactureSaveDTO manufactureSaveDTO = new ManufactureSaveDTO();
        manufactureSaveDTO.setInputUserId(-1L);
        manufactureSaveDTO.setInputUserName(CommonDict.SYSTEM_NAME);
        manufactureSaveDTO.setInputTime(LocalDateTime.now());
        manufactureSaveDTO.setStockId(warehouse.getId());
        manufactureSaveDTO.setStockName(warehouse.getName());
        if (Objects.nonNull(manufactureCompanyId)) {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(manufactureCompanyId);
            manufactureSaveDTO.setCompanyId(companyDTO.getId());
            manufactureSaveDTO.setCompanyName(companyDTO.getCompanyName());
        }
        manufactureSaveDTO.setCustId(saleContractItemDTO.getCustId());
        manufactureSaveDTO.setCustCode(saleContractItemDTO.getCustCode());
        manufactureSaveDTO.setCustName(saleContractItemDTO.getCustName());
        manufactureSaveDTO.setManufactureStatus(MmsManufactureStatusEnum.DONE.getCode());
        manufactureSaveDTO.setDoneTime(LocalDateTime.now());
        manufactureSaveDTO.setAutoFlag(BooleanEnum.YES.getValue());
        manufactureSaveDTO.setChildren(manufactureSkuSaveDTOList);
        manufactureSaveDTO.setSaleContractCode(contractCode);
        manufactureApi.createManufacture(manufactureSaveDTO);

        return batchCodeList;
    }

    private void processShipmentItemExportData(List<ShipmentItem> shipmentItemList, Map<String, Object> params,ShipmentDO shipmentDO) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        // 唛头取第一个不为空的
        params.put("frontShippingMark", shipmentDO.getFrontShippingMark());
        params.put("receivePerson", shipmentDO.getReceivePerson());
        params.put("notifyPerson", shipmentDO.getNotifyPerson());
        // 4. 汇总数据
        Integer totalShippingQuantity = shipmentItemList.stream()
                .map(ShipmentItem::getShippingQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        // 根据外箱单位分组求和箱数
        Integer totalBoxCount = shipmentItemList.stream()
                .map(ShipmentItem::getBoxCount)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        String totalBoxCountLabel = shipmentItemList.stream()
                .filter(item -> Objects.nonNull(item.getBoxCount()) && Objects.nonNull(item.getUnitPerOuterbox()) && Objects.nonNull(UnitPreOuterBoxTypeEnum.getNameByValue(item.getUnitPerOuterbox())))
                .collect(Collectors.groupingBy(
                        item -> UnitPreOuterBoxTypeEnum.getNameByValue(item.getUnitPerOuterbox()),
                        Collectors.summingInt(ShipmentItem::getBoxCount)
                ))
                .entrySet().stream()
                .map(entry -> entry.getValue() + entry.getKey())
                .collect(Collectors.joining(","));

        BigDecimal totalNetweight = shipmentItemList.stream()
                .map(s->Objects.isNull(s.getTotalNetweight())?BigDecimal.ZERO:Objects.isNull(s.getTotalNetweight().getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(s.getTotalNetweight()).getWeight())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGrossWeight = shipmentItemList.stream()
                .map(s->Objects.isNull(s.getTotalGrossweight())?BigDecimal.ZERO:Objects.isNull(s.getTotalGrossweight().getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(s.getTotalGrossweight()).getWeight())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume = shipmentItemList.stream()
                .map(s-> VolumeUtil.processVolume(s.getTotalVolume()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 将汇总数据放入params
        params.put("totalShippingQuantity", totalShippingQuantity);
        params.put("totalBoxCount", totalBoxCount);
        params.put("totalBoxCountLabel", totalBoxCountLabel);
        params.put("totalNetweight", totalNetweight);
        params.put("totalGrossweight", totalGrossWeight);
        params.put("totalVolume", totalVolume);
    }

    /**
     * 静态内部类：明细区B、C列合并Handler
     */
    private static class DetailMergeHandler implements CellWriteHandler {
        private final int detailStartRow;
        private final int detailRowCount;
        private final boolean processed = false;

        public DetailMergeHandler(int detailStartRow, int detailRowCount) {
            this.detailStartRow = detailStartRow;
            this.detailRowCount = detailRowCount;
        }

        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                     List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            if (isHead != null && isHead) {
                return; // 跳过表头
            }

            int rowIndex = cell.getRowIndex();
            int colIndex = cell.getColumnIndex();

            // 只在明细区范围内的B列（索引1）触发合并，避免重复执行
            if (rowIndex >= detailStartRow && rowIndex < detailStartRow + detailRowCount && colIndex == 1) {
                Sheet sheet = writeSheetHolder.getSheet();
                // 避免重复合并
                if (!isMerged(sheet, rowIndex, 1, 2)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 2));
                }
            }
        }

        private boolean isMerged(Sheet sheet, int row, int colStart, int colEnd) {
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() == row && region.getLastRow() == row
                        && region.getFirstColumn() == colStart && region.getLastColumn() == colEnd) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void updateThumbnail() {
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getThumbnail, ShipmentItem::getId, ShipmentItem::getMainPicture));
        if (CollUtil.isEmpty(shipmentItemList)){
            return;
        }
        shipmentItemList.forEach(s->{
            if (Objects.isNull(s.getMainPicture())){
                return;
            }
            SimpleFile picture = s.getMainPicture();
            String thumbnail = skuApi.processImageCompression(List.of(picture));
            s.setThumbnail(thumbnail);
        });
        shipmentItemMapper.updateBatch(shipmentItemList);
    }

    @Override
    public void deleteItem(List<Long> ids,boolean isRollBackSaleFlag) {
        if (CollUtil.isEmpty(ids)){
            return;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(shipmentItems)){
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        Set<Long> shipmentIdSet = shipmentItems.stream().map(ShipmentItem::getShipmentId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(shipmentIdSet)){
            throw exception(SHIPMENT_ID_IS_EMPTY);
        }
        if (shipmentIdSet.size() > 1){
            throw exception(SHIPMENT_ID_IS_NOT_SINGLE);
        }
        Long shipmentId = shipmentIdSet.stream().findFirst().get();
        ShipmentDO shipmentDO = shipmentMapper.selectById(shipmentId);
        if (Objects.isNull(shipmentDO)){
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
        if (CollUtil.isEmpty(shipmentItemList)){
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        shipmentItemList = shipmentItemList.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        if (shipmentItemList.isEmpty()){
            throw exception(SHIPMENT_ITEM_NOT_ALL_DELETED);
        }
        // TODO 是否需要校验出运单状态再进行移除明细
        // 移除
        shipmentItemMapper.deleteBatchIds(ids);
        CalcShipmentUtil.calcItemCost(shipmentDO,shipmentItemList,rateApi.getDailyRateMap());
        shipmentMapper.updateById(shipmentDO);
        Map<Long, Integer> shipmentQuantityMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractItemId, Collectors.summingInt(ShipmentItem::getShippingQuantity)));
        if (isRollBackSaleFlag){
            saleContractApi.updateShipmentTotalQuantity(shipmentQuantityMap,true);
        }
    }

    @Override
    public void mergeItem(List<Long> ids, Long targetId) {
        if (CollUtil.isEmpty(ids)||Objects.isNull(targetId)){
            return;
        }
        ShipmentDO shipmentDO = shipmentMapper.selectById(targetId);
        if (Objects.isNull(shipmentDO)){
            throw exception(TARGET_SHIPMENT_NOT_EXISTS);
        }
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(shipmentItemList)){
            throw exception(SHIPMENT_ITEM_NOT_EXISTS);
        }
        deleteItem(ids,false);
        shipmentItemList.forEach(s->{
            s.setId(null);
            s.setShipmentId(targetId);
        });
        shipmentItemMapper.insertBatch(shipmentItemList);
        List<ShipmentItem> targetItems = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, targetId);
        CalcShipmentUtil.calcItemCost(shipmentDO,targetItems,rateApi.getDailyRateMap());
        shipmentMapper.updateById(shipmentDO);
    }

    /**
     * 填充出运明细的合同详情信息
     * 用于导出时按采购合同号拆分
     *
     * @param shipmentItemList 出运明细列表
     * @param saleContractCodeList 销售合同号列表
     */
    private void fillShipmentItemContractDetails(List<ShipmentItem> shipmentItemList, List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        } 
        // 获取库存信息
        Map<Long, List<StockDTO>> stockDTOMap = iStockApi.getStockDTOMapBySaleContractCodes(saleContractCodeList);

        // 获取采购合同信息
        Set<Long> saleItemIds = shipmentItemList.stream()
            .map(ShipmentItem::getSaleContractItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, Map<String, List<SimpleContractDetailDTO>>> contractCodeMap = purchaseContractApi.getContractCodeMapByItemId(saleItemIds);
        
        // 为每个出运明细设置合同详情
        shipmentItemList.forEach(item -> {
            List<SimpleContractDetailDTO> itemContractDetails = new ArrayList<>();        
            // 1. 从库存信息中获取采购合同号，类型为库存采购
            if(CollUtil.isNotEmpty(stockDTOMap) && item.getSaleContractItemId() != null){
                List<StockDTO> stockDTOList = stockDTOMap.get(item.getSaleContractItemId());
                if(CollUtil.isNotEmpty(stockDTOList)){
                    // 根据stock信息构建库存采购类型的ContractDetailVO
                    for (StockDTO stock : stockDTOList) {
                        if(StrUtil.isNotEmpty(stock.getPurchaseContractCode())){
                            SimpleContractDetailDTO contractDetail = new SimpleContractDetailDTO();
                            contractDetail.setContractCode(stock.getPurchaseContractCode());
                            contractDetail.setPurchaseContractType(ShipmentContractTypeEnum.STOCK_PURCHASE.getName());
                            contractDetail.setUsedQuantity(stock.getLockQuantity());
                            contractDetail.setSupplierName(item.getVenderName());
                            contractDetail.setInvoicingStatus(item.getInvoiceStatus() != null ?
                                InvoiceStatusEnum.getNameByValue(item.getInvoiceStatus()) : "未开票");
                            contractDetail.setInvoicedQuantity(item.getInvoicedQuantity() != null ?
                                item.getInvoicedQuantity().intValue() : 0);
                            contractDetail.setInvoicedItemName(item.getName());
                            // 库存采购：从库存表中获取价格
                            if(stock.getPrice() != null){
                                contractDetail.setPrice(stock.getPrice());
                            }
                            itemContractDetails.add(contractDetail);
                        }
                    }
                }
            }       
            // 2. 根据销售明细ID查询采购明细对应的采购合同号，类型为本次采购
            if(item.getSaleContractItemId() != null){
                if(CollUtil.isNotEmpty(contractCodeMap)){
                    Map<String, List<SimpleContractDetailDTO>> skuContractMap = contractCodeMap.get(item.getSaleContractItemId());
                    if(CollUtil.isNotEmpty(skuContractMap)){
                        List<SimpleContractDetailDTO> collect = skuContractMap.values().stream().flatMap(List::stream)
                                .filter(s -> {
                                    Integer purchaseModel = item.getPurchaseModel();
                                    if (!PurchaseModelEnum.COMBINE.getCode().equals(purchaseModel)) {
                                        return !BooleanEnum.YES.getValue().equals(s.getAutoFlag());
                                    } else {
                                        return true;
                                    }
                                }).collect(Collectors.toList());
                        itemContractDetails.addAll(collect);
                    }
                }
            }         
            // 将合同详情信息设置到明细项中
            item.setContractDetails(itemContractDetails);
        });
    }

}
