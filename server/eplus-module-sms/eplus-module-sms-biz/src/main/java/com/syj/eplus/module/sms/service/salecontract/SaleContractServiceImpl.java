package com.syj.eplus.module.sms.service.salecontract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.*;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.ChangeCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.DiffUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.companypath.CompanyPathApi;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import com.syj.eplus.module.infra.api.settlement.SettlementApi;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.syj.eplus.module.infra.enums.company.OfficialSealTypeEnum;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.*;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CollectionAccountDTO;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.api.cust.dto.SystemCollectionPlanDTO;
import com.syj.eplus.module.crm.enums.cust.EffectMainInstanceFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.ShipmentPlanApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.SimpleShipmentItemDTO;
import com.syj.eplus.module.fms.api.payment.api.custclaim.CustClaimApi;
import com.syj.eplus.module.fms.api.payment.api.receipt.ReceiptApi;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.*;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.*;
import com.syj.eplus.module.scm.api.purchaseplan.PurchasePlanApi;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoSaveReqDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemSaveReqDTO;
import com.syj.eplus.module.scm.api.quoteitem.QuoteitemApi;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;
import com.syj.eplus.module.sms.api.dto.*;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.convert.salecontract.SaleContractConvert;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractProductModeSummaryDO;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractSummaryDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import com.syj.eplus.module.sms.dal.mysql.addsubitem.AddSubItemMapper;
import com.syj.eplus.module.sms.dal.mysql.collectionplan.CollectionPlanMapper;
import com.syj.eplus.module.sms.dal.mysql.salecontract.SaleContractMapper;
import com.syj.eplus.module.sms.dal.mysql.salecontractchange.SaleContractChangeMapper;
import com.syj.eplus.module.sms.dal.mysql.salecontractitem.SaleContractItemMapper;
import com.syj.eplus.module.sms.entity.GroupSaleItemEntity;
import com.syj.eplus.module.sms.enums.AllocateConditionTypeEnum;
import com.syj.eplus.module.sms.enums.AllocateTypeEnum;
import com.syj.eplus.module.sms.util.*;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.api.stock.dto.*;
import com.syj.eplus.module.wms.api.stockNotice.IStockNoticeApi;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeItemDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeSaveReqDTO;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.enums.StockLockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.FIELD_CHANGE_CONFIG_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_PLAN_ITEM_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.SALE_CONTRACT_STATUS_EMPTY;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.SALE_CONTRACT_NOT_EXITS;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.SALE_CONTRACT_SHIPMENT_TOTAL_QUANTITY_LESS;
import static java.lang.Integer.parseInt;

/**
 * 外销合同 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class SaleContractServiceImpl extends ServiceImpl<SaleContractMapper, SaleContractDO> implements SaleContractService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private SaleContractMapper saleContractMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private SaleContractItemMapper saleContractItemMapper;
    @Resource
    private AddSubItemMapper addSubItemMapper;
    @Resource
    private CollectionPlanMapper collectionPlanMapper;
    @Resource
    private ConfigApi configApi;
    @Resource
    private RateApi rateApi;
    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    @Lazy
    private CustApi custApi;
    @Resource
    @Lazy
    private VenderApi venderApi;

    @Resource
    private CustClaimApi custClaimApi;
    @Resource
    @Lazy
    private PurchasePlanApi purchasePlanApi;
    @Resource
    private QuoteitemApi quoteitemApi;

    @Resource
    private SaleContractChangeMapper saleContractChangeMapper;

    @Resource
    private SkuApi skuApi;

    @Resource
    private ShipmentPlanApi shipmentPlanApi;

    @Resource
    private IStockApi stockApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private ReportApi reportApi;

    @Resource
    private FileApi fileApi;

    @Lazy
    @Resource
    private CompanyPathApi companyPathApi;

    @Resource
    private FormChangeApi formChangeApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private ReceiptApi receiptApi;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private CompanyApi companyApi;
    @Resource
    private PaymentItemApi paymentItemApi;
    @Resource
    private IStockApi iStockApi;
    @Resource
    private IStockNoticeApi iStockNoticeApi;
    @Resource
    private IWarehouseApi warehouseApi;
    @Resource
    private SettlementApi settlementApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createSaleContract(SaleContractSaveReqVO createReqVO) {
        Integer autoFlag = createReqVO.getAutoFlag();

        if(Objects.isNull(createReqVO.getSaleContractDate())){
            createReqVO.setSaleContractDate(LocalDateTime.now());
        }
        // 校验内部客户是否全部使用库存
        if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(createReqVO.getSaleType())) {
            String custCode = createReqVO.getCustCode();
            boolean internalFlag = custApi.checkIsInternalCust(custCode);
            if (internalFlag && !BooleanEnum.YES.getValue().equals(autoFlag)) {
                boolean notAllStock = createReqVO.getChildren().stream().anyMatch(s -> s.getNeedPurQuantity() > 0);
                if (notAllStock) {
                    throw exception(INTERNAL_CUST_NOT_ALL_STOCK);
                }
            }
        } else if (SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(createReqVO.getSaleType())){
            if (!BooleanEnum.YES.getValue().equals(autoFlag)) {
                JsonCompanyPath companyPath = createReqVO.getCompanyPath();
                List<JsonCompanyPath> companyPathList = TransformUtil.convertJsonCompanyPathToList(companyPath);
                Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
                boolean foreignTradeFlag = companyPathList.stream().anyMatch(s -> {
                    SimpleCompanyDTO companyDTO = simpleCompanyDTO.get(s.getId());
                    return CompanyNatureEnum.FOREIGN_TRADE.getValue().equals(companyDTO.getCompanyNature());
                });
                //2025.9.15 不限制必须包含外贸资质，控股直接采购的情况
//                if (!foreignTradeFlag) {
//                    throw exception(SALE_CONTRACT_NOT_FOREIGN);
//                }
            }
        }
        List<CreatedResponse> responses = new ArrayList<>();
        SaleContractDO saleContract = SaleContractConvert.INSTANCE.convertSaleContractDO(createReqVO);
        saleContract.setId(null);
        // 生成 外销合同 编号
        boolean ownBrandFlag = createReqVO.getChildren().stream().anyMatch(s -> BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()));
        String code = createReqVO.getCode();
        if (StrUtil.isEmpty(code)|| BooleanEnum.YES.getValue().equals(autoFlag)){
            code = genSaleContractCode(createReqVO.getAutoFlag(), saleContract.getCompanyId(), ownBrandFlag, createReqVO.getCode(),createReqVO.getSaleType());
        while (saleContractMapper.exists(new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCode, code))) {
            code = genSaleContractCode(createReqVO.getAutoFlag(), saleContract.getCompanyId(), ownBrandFlag, createReqVO.getCode(),createReqVO.getSaleType());
            }
        }
        String finalCode = code;
        // 订单链路编号
        String linkCode = IdUtil.fastSimpleUUID();
        saleContract.setLinkCodeList(List.of(linkCode));
        saleContract.setCode(finalCode);
        List<SaleContractItem> saleContractItemList = createReqVO.getChildren();
        Map<Long,UserDept> purchaseUserMap = new HashMap<>();
        if (CollUtil.isNotEmpty(saleContractItemList)){
            saleContractItemList.forEach(s->{
                BigDecimal realTaxRefundRate = s.getRealTaxRefundRate();
                if (realTaxRefundRate == null || BigDecimal.ZERO.compareTo(realTaxRefundRate) == 0) {
                    s.setRealTaxRefundRate(s.getTaxRefundRate());
                } else {
                    s.setRealTaxRefundRate(realTaxRefundRate);
                }
                if (Objects.nonNull(s.getPurchaseUser())){
                    purchaseUserMap.put(s.getPurchaseUser().getUserId(),s.getPurchaseUser());
                }
            });
        }
        if (CollUtil.isNotEmpty(purchaseUserMap)){
            saleContract.setPurchaseUserList(purchaseUserMap.values().stream().toList());
        }
        // 录入日期为当前日期
        saleContract.setInputDate(LocalDateTime.now());
        if (ObjectUtil.isNull(saleContract.getStatus())) {
            saleContract.setStatus(SaleContractStatusEnum.WAITING_FOR_SUBMISSION.getValue());
        }
        if (ObjectUtil.isNull(saleContract.getStatus())) {
            saleContract.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        }
        if (createReqVO.getCheckFlag()) {
            // 计算子项列表
            CalcSaleContactUtil.checkItemCost(createReqVO, configApi.getConfigMap(), rateApi.getDailyRateMapByDate(saleContract.getSaleContractDate()));
        }
        // 插入
        saleContractMapper.insert(saleContract);
        Long saleContractId = saleContract.getId();
        // 插入销售明细
        if (CollUtil.isNotEmpty(saleContractItemList)) {
            // 校验锁定数量
            saleContractItemList.stream().forEach(x -> {
                if (x.getCurrentLockQuantity() != null && x.getCurrentLockQuantity() > x.getQuantity()) {
                    throw exception(SALE_CONTRACT_ITEM_LOCK_EXCEED, x.getName());
                }
            });
            // 将非客户产品的自营产品转为当前客户客户产品
            if (!BooleanEnum.YES.getValue().equals(createReqVO.getAutoFlag())) {
                preprocessingOwnbrandSku(saleContractItemList, createReqVO.getCustCode(), saleContractId);
            }

            // 如果已经存在产品，且产品客户货号或者条形码为空，需要写入产品（2365）
            dealSkuUpdate(saleContractItemList);

            saleContractItemList.forEach(s -> {
                s.setContractId(saleContractId);
                s.setContractCode(finalCode);
                s.setId(null);
                if (StrUtil.isEmpty(s.getUniqueCode())) {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                }
                if (StrUtil.isEmpty(s.getSourceUniqueCode())) {
                    s.setSourceUniqueCode(s.getUniqueCode());
                }
                s.setRealLockQuantity(s.getCurrentLockQuantity());
                s.setRealPurchaseWithTaxPrice(s.getPurchaseWithTaxPrice());
                s.setWithTaxPriceRemoveFree(s.getPurchaseWithTaxPrice());
                Integer currentLockQuantity = s.getCurrentLockQuantity();
                if (ObjectUtil.isNotNull(currentLockQuantity) && currentLockQuantity > s.getQuantity()) {
                    throw exception(SALE_CONTRACT_ITEM_LOCK_QUANTITY_ERROR);
                }
                if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
                    s.setTaxRefundRate(BigDecimal.ZERO);
                    s.setTaxRefundPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
                    s.setRealTaxRefundRate(BigDecimal.ZERO);
                    s.setRealTaxRefundPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
                }
            });
            saleContractItemMapper.insertBatch(saleContractItemList);
            //更新库存信息
            if (!Objects.equals(createReqVO.getAutoFlag(), BooleanEnum.YES.getValue())) {
                dealLockStock(saleContractItemList, saleContractId, finalCode, true);
            }


        }
        List<String> linkCodeList = saleContract.getLinkCodeList();
        // 插入订单流转数据
        if (CollUtil.isNotEmpty(linkCodeList)) {
            linkCodeList.forEach(x -> {
                orderLinkApi.createOrderLink(new OrderLinkDTO()
                        .setBusinessId(saleContractId)
                        .setCode(finalCode)
                        .setName(BusinessNameDict.EXPORT_SALE_CONTRACT_NAME)
                        .setType(OrderLinkTypeEnum.SALE_CONTRACT.getValue())
                        .setLinkCode(x)
                        .setItem(saleContractItemList)
                        .setParentCode(CommonDict.HYPHEN)
                        .setStatus(saleContract.getStatus())
                        .setCompanyId(saleContract.getCompanyId())
                        .setBusinessSubjectName(saleContract.getCustName())
                        .setOrderMsg(saleContract));
            });
        }
        // 插入加减项
        List<AddSubItem> subItemList = createReqVO.getAddSubItemList();
        if (CollUtil.isNotEmpty(subItemList)) {
            subItemList.forEach(s -> {
                s.setContractCode(finalCode);
                s.setId(null);
            });
            addSubItemMapper.insertBatch(subItemList);
        }
        JsonAmount totalGoodsValue = saleContract.getTotalGoodsValue();
        // 插入收款方式
        if (CollUtil.isEmpty(createReqVO.getCollectionPlanList())){
            String custCode = createReqVO.getCustCode();
            List<SystemCollectionPlanDTO> collectionPlanDTOList = custApi.getCollectionPlanDTOByCustCode(custCode);
            createReqVO.setCollectionPlanList(BeanUtils.toBean(collectionPlanDTOList, CollectionPlan.class));
        }
        if (CollUtil.isNotEmpty(createReqVO.getCollectionPlanList())) {
            List<CollectionPlan> collectionPlanList = createReqVO.getCollectionPlanList();
            Map<String, BigDecimal> dailyRateMapByDate = rateApi.getDailyRateMapByDate(saleContract.getSaleContractDate());
            collectionPlanList.forEach(s -> {
                s.setId(null);
                s.setContractId(saleContractId);
                calcCollectionPlanAmount(s, saleContract.getCollectionTotal(), subItemList, dailyRateMapByDate);
            });
            collectionPlanMapper.insertBatch(collectionPlanList);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(createReqVO.getSaleType())) {
                submitDomesticTask(saleContractId, WebFrameworkUtils.getLoginUserId());
            } else if (SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(createReqVO.getSaleType())) {
                List<Long> saleItemIds = saleContractItemList.stream().map(SaleContractItem::getId).toList();
                updateRealPurchasePrice(saleItemIds, null, null);
                submitExportTask(saleContractId, WebFrameworkUtils.getLoginUserId());
            }else if (SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(createReqVO.getSaleType())) {
                submitfactoryTask(saleContractId, WebFrameworkUtils.getLoginUserId());
            }
            orderLinkApi.updateOrderLinkStatus(saleContract.getCode(), BusinessNameDict.EXPORT_SALE_CONTRACT_NAME, saleContract.getLinkCodeList(), BpmProcessInstanceResultEnum.PROCESS.getResult());
        }
        // 记录操作日志
        OperateLogUtils.setContent(String.format(SaleDict.SALE_LOGGER_CREATE_FORMAT, saleContract.getCode()));
        OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContract.getCode());
        // 返回
        responses.add(new CreatedResponse(saleContractId, saleContract.getCode()));
        return responses;
    }

    private void calcCollectionPlanAmount(CollectionPlan collectionPlan, JsonAmount collectionTotal, List<AddSubItem> subItemList, Map<String, BigDecimal> dailyRateMapByDate) {
        JsonAmount totalAmount = new JsonAmount();
        // 加项金额
        BigDecimal addAmount = BigDecimal.ZERO;
        // 减项金额
        BigDecimal subAmount = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(subItemList)){
            // 将加减项转为人民币计算
            addAmount = subItemList.stream().filter(x -> CalclationTypeEnum.ADD.getType().equals(x.getCalculationType())).map(AddSubItem::getAmount).filter(Objects::nonNull).map(s->{
                if (CollUtil.isEmpty(dailyRateMapByDate)){
                    return BigDecimal.ZERO;
                }else {
                    BigDecimal rate = Objects.isNull(dailyRateMapByDate.get(s.getCurrency()))?BigDecimal.ZERO:dailyRateMapByDate.get(s.getCurrency());
                    return NumUtil.mul(rate,s.getAmount());
                }
            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            subAmount = subItemList.stream().filter(x -> CalclationTypeEnum.DEDUCTION.getType().equals(x.getCalculationType())).map(AddSubItem::getAmount).filter(Objects::nonNull).filter(Objects::nonNull).map(s->{
                if (CollUtil.isEmpty(dailyRateMapByDate)){
                    return BigDecimal.ZERO;
                }else {
                    BigDecimal rate = Objects.isNull(dailyRateMapByDate.get(s.getCurrency()))?BigDecimal.ZERO:dailyRateMapByDate.get(s.getCurrency());
                    return NumUtil.mul(rate,s.getAmount());
                }
            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        }
        String currency = collectionTotal.getCurrency();
        BigDecimal contractRate = dailyRateMapByDate.get(currency);
        if (Objects.isNull(contractRate)){
            throw exception(NOT_FOUND_CONTRACT_RATE);
        }
        totalAmount.setAmount(NumUtil.add(NumUtil.sub(collectionTotal.getAmount(), NumUtil.div(subAmount,contractRate)), NumUtil.div(addAmount,contractRate)));
        totalAmount.setCurrency(currency);
        BigDecimal collectionRatio = collectionPlan.getCollectionRatio();
        if (Objects.isNull(collectionRatio)) {
            return;
        }
        Optional.ofNullable(totalAmount).ifPresent(s -> Optional.ofNullable(s.getAmount()).ifPresent(amount -> {
            collectionPlan.setReceivableAmount(new JsonAmount().setAmount(NumUtil.mul(NumUtil.div(collectionRatio, CalculationDict.ONE_HUNDRED), amount)).setCurrency(s.getCurrency()));
        }));
    }

    /**
     * 生成销售合同编号
     *
     * @param autoFlag     是否内部合同
     * @param companyId    下单主体
     * @param ownBrandFlag 自营标识
     * @return
     */
    private String genSaleContractCode(Integer autoFlag, Long companyId, boolean ownBrandFlag, String sourceCode,Integer saleType) {
        // 两位年号
        String shortYear = DateUtil.thisYear() % CalculationDict.ONE_HUNDRED + CommonDict.EMPTY_STR;
        // 前缀
        String prefix;
        String code;
        // 自营产品编号
        if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleType)) {
            prefix = "DT" + shortYear;
        } else {
            // 普通产品编号
            prefix = "VK" + shortYear;
        }
        code = codeGeneratorApi.getCodeGenerator(SaleDict.SN_TYPE, prefix, false, 4);
        // 非内部合同编号
        if (!BooleanEnum.YES.getValue().equals(autoFlag)) {
            return code + "C";
        }
        // 自动生成编号需拼接内部公司简称
        String shortname = companyApi.getCompanyDTO(companyId).getShortname();
        if (StrUtil.isEmpty(shortname)) {
            throw exception(COMPANY_SHORTNAME_NOT_EXISTS);
        }
        return codeGeneratorApi.getCodeGenerator(SaleDict.SN_TYPE, sourceCode + shortname, false, null);
    }

    /**
     * 校验自营产品是否有对应当前客户产品  有的话把对应客户信息取过来 没有的话新增对应客户产品并将客户信息返回
     *
     * @param saleContractItemList
     * @return
     */
    private List<SaleContractItem> preprocessingOwnbrandSku(List<SaleContractItem> saleContractItemList, String custCode, Long contractId) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return saleContractItemList;
        }
        // key -> 产品编号 value -> 客户货号
        Map<String, Tuple> skuCodeMap = saleContractItemList.stream().filter(s -> {
            // 自营产品且非客户产品
            return BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()) && BooleanEnum.NO.getValue().equals(s.getCustProFlag());
        }).collect(Collectors.toMap(SaleContractItem::getSkuCode, s -> new Tuple(s.getCskuCode(), s.getUnitPrice(),s.getBarcode()), (v1, v2) -> v1));
        if (CollUtil.isEmpty(skuCodeMap)) {
            return saleContractItemList;
        }
        // 校验自营产品是否存在当前客户对应客户产品
        Map<String, SimpleData> stringSimpleDataMap = skuApi.validateOwnbrandSku(skuCodeMap, custCode);
        saleContractItemList.forEach(s -> {
            s.setContractId(contractId);
            if (CollUtil.isNotEmpty(stringSimpleDataMap)) {
                SimpleData simpleData = stringSimpleDataMap.get(s.getSkuCode());
                if (Objects.nonNull(simpleData)) {
                    s.setSkuId(simpleData.getId());
                    s.setSkuCode(simpleData.getCode());
                    s.setCustProFlag(BooleanEnum.YES.getValue());
                }
            }
        });
        return saleContractItemList;
    }

    private void dealSkuUpdate(List<SaleContractItem> saleContractItemList){
        List<String> skuCodes = saleContractItemList.stream()
                .map(SaleContractItem::getSkuCode)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(skuCodes)) {
            return;
        }
        List<SkuDTO> skuDTOList = skuApi.getSkuDTOListByCodeList(skuCodes);
        if (CollUtil.isEmpty(skuDTOList)) {
            return;
        }
        // 过滤出客户产品且客户货号为空的SKU
        List<SkuDTO> custProSkuList = skuDTOList.stream()
                .filter(sku -> sku.getCustProFlag() == 1)
                .toList();
        if (CollUtil.isEmpty(custProSkuList)) {
            return;
        }
        Map<String, List<SaleContractItem>> skuCodeItemMap = saleContractItemList.stream()
                .collect(Collectors.groupingBy(SaleContractItem::getSkuCode));
        custProSkuList.forEach(sku -> {
            List<SaleContractItem> matchedItems = skuCodeItemMap.get(sku.getCode());
            if (CollUtil.isEmpty(matchedItems)) {
                return;
            }
            // 获取第一个非空的客户货号
            Optional<String> cskuCodeOpt = matchedItems.stream()
                    .map(SaleContractItem::getCskuCode)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (cskuCodeOpt.isPresent()) {
                 skuApi.updateCSkuCodeByCode(sku.getCode(), cskuCodeOpt.get());
            }
            // 获取第一个非空的条形码
            Optional<String> barcodeOpt = matchedItems.stream()
                    .map(SaleContractItem::getBarcode)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (barcodeOpt.isPresent()) {
                 skuApi.updateBarcodeByCode(sku.getCode(), barcodeOpt.get());
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleContract(SaleContractSaveReqVO updateReqVO) {
        Long contractId = updateReqVO.getId();
        // 校验存在
        SaleContractDO saleContractDO = validateSaleContractExists(contractId);
        // 更新
        SaleContractDO updateObj = SaleContractConvert.INSTANCE.convertSaleContractDO(updateReqVO);
        // 计算子项列表
        CalcSaleContactUtil.checkItemCost(updateReqVO, configApi.getConfigMap(), rateApi.getDailyRateMapByDate(updateReqVO.getSaleContractDate()));
        // 更新销售明细
        List<SaleContractItem> saleContractItemList = updateReqVO.getChildren();
        // 处理销售明细
        dealSaleContractItem(saleContractItemList, contractId, saleContractDO.getCode(), saleContractDO.getCustCode(),saleContractDO.getCode());
        // 锁库处理
        if (!BooleanEnum.YES.getValue().equals(saleContractDO.getAutoFlag())){
            dealLockStock(saleContractItemList, contractId, saleContractDO.getCode(), true);
        }
        // 更新加减项
        List<AddSubItem> addSubItemList = updateReqVO.getAddSubItemList();
        addSubItemMapper.delete(AddSubItem::getContractCode, saleContractDO.getCode());
        if (CollUtil.isNotEmpty(addSubItemList)) {
            List<AddSubItem> insertList = addSubItemList.stream().filter(s -> Objects.isNull(s.getChangeFlag()) || !ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(s -> {
                s.setId(null);
                s.setContractCode(saleContractDO.getCode());
                return s;
            }).toList();
            addSubItemMapper.insertBatch(insertList);
        }
        // 更新收款计划
        List<CollectionPlan> collectionPlanList = updateReqVO.getCollectionPlanList();
        collectionPlanMapper.delete(CollectionPlan::getContractId, contractId);
        if (CollUtil.isNotEmpty(collectionPlanList)) {
            List<CollectionPlan> insertList = collectionPlanList.stream().filter(s -> Objects.isNull(s.getChangeFlag()) || !ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(s -> {
                s.setContractId(contractId);
                s.setId(null);
                return s;
            }).toList();
            collectionPlanMapper.insertBatch(insertList);
        }
        // 操作记录
        List<ChangeRecord> changeRecords = new OperateCompareUtil<SaleContractDO>().compare(saleContractDO, updateObj);
        List<AddSubItem> baseAddSubItemList = addSubItemMapper.selectList(AddSubItem::getContractCode, saleContractDO.getCode());
        List<ChangeRecord> child_addSubItem_changeRecords = DiffUtil.compareListsToChangeRecord(baseAddSubItemList, addSubItemList, SaleDict.ADD_SUB_ITEM_NAME, SaleDict.SALE_LOGGER_COMMON_FLAG);
        if (CollUtil.isNotEmpty(child_addSubItem_changeRecords)) {
            changeRecords.addAll(child_addSubItem_changeRecords);
        }
        List<CollectionPlan> baseCollectionPlanList = collectionPlanMapper.selectList(CollectionPlan::getContractId, contractId);
        List<ChangeRecord> child_collectionPlan_changeRecords = DiffUtil.compareListsToChangeRecord(baseCollectionPlanList, collectionPlanList, SaleDict.COLLECTION_PLAN_NAME, SaleDict.SALE_LOGGER_COMMON_FLAG);
        if (CollUtil.isNotEmpty(child_collectionPlan_changeRecords)) {
            changeRecords.addAll(child_collectionPlan_changeRecords);
        }
        OperateLogUtils.addOperateLog(changeRecords, SaleDict.OPERATOR_EXTS_KEY, updateReqVO.getCode());
        saleContractMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(updateReqVO.getSaleType())) {
                submitDomesticTask(contractId, WebFrameworkUtils.getLoginUserId());
            } else if (SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(updateReqVO.getSaleType())) {
                submitExportTask(contractId, WebFrameworkUtils.getLoginUserId());
            }else if (SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(updateReqVO.getSaleType())){
                submitfactoryTask(contractId, WebFrameworkUtils.getLoginUserId());
            }
            orderLinkApi.updateOrderLinkStatus(saleContractDO.getCode(), BusinessNameDict.EXPORT_SALE_CONTRACT_NAME, saleContractDO.getLinkCodeList(), BpmProcessInstanceResultEnum.PROCESS.getResult());
        }
    }


    /**
     * 处理销售明细
     *
     * @param saleContractItemList 销售明细列表
     */
    private void dealSaleContractItem(List<SaleContractItem> saleContractItemList, Long contractId, String code, String custCode,String saleContractCode) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }

        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().select(SaleContractItem::getId).eq(SaleContractItem::getContractId, contractId));
        // 库中没有则直接插入
        if (CollUtil.isEmpty(saleContractItems)) {
            saleContractItemList.forEach(s -> {
                String uniqueCode = IdUtil.fastSimpleUUID();
                s.setContractId(contractId);
                s.setContractCode(code);
                s.setUniqueCode(uniqueCode);
                s.setSourceUniqueCode(uniqueCode);
                s.setRealLockQuantity(s.getCurrentLockQuantity());
                s.setRealPurchaseQuantity(s.getRealPurchaseQuantity());
                s.setRealPurchaseWithTaxPrice(s.getPurchaseWithTaxPrice());
                s.setWithTaxPriceRemoveFree(s.getPurchaseWithTaxPrice());
            });
            saleContractItemMapper.insertBatch(saleContractItemList);
            return;
        }
        Set<Long> thisIdSet = saleContractItemList.stream().filter(s -> Objects.nonNull(s.getId()) && !ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(SaleContractItem::getId).collect(Collectors.toSet());
        Set<Long> deleteSet = saleContractItems.stream().filter(s -> thisIdSet.isEmpty() || !thisIdSet.contains(s.getId())).map(SaleContractItem::getId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(deleteSet)) {
            saleContractItemMapper.delete(new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getId, deleteSet));
        }
        if (CollUtil.isNotEmpty(deleteSet)){
            stockApi.cancelStockLock(saleContractCode,deleteSet,null);
        }
        Set<Long> idSet = saleContractItems.stream().map(SaleContractItem::getId).collect(Collectors.toSet());
        saleContractItemList.forEach(s -> {
            if (Objects.isNull(s.getContractId())) {
                s.setContractId(contractId);
            }
            if (Objects.isNull(s.getId())) {
                s.setChangeFlag(ChangeTypeEnum.ADDED.getType());
                return;
            }
            if (idSet.contains(s.getId())) {
                s.setChangeFlag(ChangeTypeEnum.UPDATE.getType());
            }
        });
        // 新增项
        List<SaleContractItem> insertList = saleContractItemList.stream().filter(s -> ChangeTypeEnum.ADDED.getType().equals(s.getChangeFlag())).toList();
        // 修改项
        List<SaleContractItem> updateList = saleContractItemList.stream().filter(s -> ChangeTypeEnum.UPDATE.getType().equals(s.getChangeFlag())).toList();
        if (CollUtil.isNotEmpty(insertList)) {
            insertList.forEach(s -> {
                String uniqueCode = IdUtil.fastSimpleUUID();
                s.setId(null);
                s.setUniqueCode(uniqueCode);
                s.setSourceUniqueCode(uniqueCode);
            });
            saleContractItemMapper.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {

            saleContractItemMapper.updateBatch(updateList);
        }
    }

    /**
     * 销售明细锁库逻辑
     *
     * @param saleContractItemList 销售明细列表
     * @param contractId           销售合同主键
     * @param saleContractCode     销售合同编号
     */
    private void dealLockStock(List<SaleContractItem> saleContractItemList, Long contractId, String saleContractCode, boolean isUpdateLockMsg) {
        List<StockLockSaveReqVO> saveReqVOList = new ArrayList<>();
        // 释放销售合同锁定库存
        stockApi.cancelStockLock(saleContractCode, saleContractItemList.stream().map(SaleContractItem::getId).distinct().toList(), null);
        // 循环产品明细,若存在锁定批次则进行锁定
        saleContractItemList.forEach(saleContractItem -> {
            List<StockLockSaveReqVO> stockLockSaveReqVOList = saleContractItem.getStockLockSaveReqVOList();
            List<JsonLock> lockList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(stockLockSaveReqVOList)) {
                List<StockLockSaveReqVO> stockLockSaveReqVOS = stockLockSaveReqVOList.stream().filter(Objects::nonNull).toList();
                stockLockSaveReqVOS.forEach(lockSaveReqVO -> {
                    lockSaveReqVO.setSaleContractId(contractId);
                    lockSaveReqVO.setSaleContractCode(saleContractCode);
                    lockSaveReqVO.setSaleContractItemId(saleContractItem.getId());
                    lockSaveReqVO.setSourceOrderType(StockLockSourceTypeEnum.SALE_CONTRACT.getValue());
                    lockSaveReqVO.setLockQuantity(lockSaveReqVO.getSourceOrderLockedQuantity());
                    lockSaveReqVO.setSkuCode(saleContractItem.getSkuCode());
                    JsonLock lock = new JsonLock();
                    lock.setLockQuantity(lockSaveReqVO.getSourceOrderLockedQuantity());
                    lock.setStockId(lockSaveReqVO.getStockId()).setBatchCode(lockSaveReqVO.getBatchCode());
                    lockList.add(lock);
                });
                saveReqVOList.addAll(stockLockSaveReqVOList);
                if (isUpdateLockMsg) {
                    saleContractItem.setLockMsg(lockList);
                }
            }
        });
        stockApi.batchLockStock(saveReqVOList);
        saleContractItemMapper.updateBatch(saleContractItemList);
    }

    /**
     * 重新锁定销售合同库存
     *
     * @param saleContractItemIdList 销售明细主键列表
     */
    @Override
    public void reLockStock(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)) {
            return;
        }
        List<StockLockSaveReqVO> saveReqVOList = new ArrayList<>();
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        // 循环产品明细,若存在锁定批次则进行锁定
        saleContractItemList.forEach(saleContractItem -> {
            List<JsonLock> lockMsg = saleContractItem.getLockMsg();
            if (CollUtil.isEmpty(lockMsg)) {
                return;
            }
            lockMsg = lockMsg.stream().filter(Objects::nonNull).toList();
            if (CollUtil.isEmpty(lockMsg)) {
                return;
            }
            Integer lockQuantity = lockMsg.stream().map(JsonLock::getLockQuantity).reduce(Integer::sum).orElse(0);
            saleContractItem.setRealLockQuantity(lockQuantity);
            saleContractItem.setNeedPurQuantity(saleContractItem.getQuantity() - lockQuantity);
            List<StockLockSaveReqVO> stockLockSaveReqVOList = lockMsg.stream().map(jsonLock -> {
                StockLockSaveReqVO lockSaveReqVO = new StockLockSaveReqVO();
                lockSaveReqVO.setStockId(jsonLock.getStockId());
                lockSaveReqVO.setBatchCode(jsonLock.getBatchCode());
                lockSaveReqVO.setSaleContractId(saleContractItem.getContractId());
                lockSaveReqVO.setSaleContractCode(saleContractItem.getContractCode());
                lockSaveReqVO.setSaleContractItemId(saleContractItem.getId());
                lockSaveReqVO.setSourceOrderType(StockLockSourceTypeEnum.SALE_CONTRACT.getValue());
                lockSaveReqVO.setLockQuantity(jsonLock.getLockQuantity());
                lockSaveReqVO.setSkuCode(saleContractItem.getSkuCode());
                return lockSaveReqVO;
            }).toList();
            saveReqVOList.addAll(stockLockSaveReqVOList);
        });
        stockApi.batchLockStock(saveReqVOList);
        saleContractItemMapper.updateBatch(saleContractItemList);
    }

    @Override
    @DataPermission(enable = false)
    public SaleContractItemDTO getSaleContractItemById(Long saleContractItemId) {
        if (Objects.isNull(saleContractItemId)) {
            return null;
        }
        SaleContractItem saleContractItem = saleContractItemMapper.selectById(saleContractItemId);
        Long contractId = saleContractItem.getContractId();
        SaleContractDO saleContractDO = saleContractMapper.selectById(contractId);
        if (Objects.isNull(saleContractDO)){
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        SaleContractItemDTO itemDTO = SaleContractConvert.INSTANCE.toItemDTO(saleContractItem);
        itemDTO.setCustId(saleContractDO.getCustId());
        itemDTO.setCustName(saleContractDO.getCustName());
        itemDTO.setCustCode(saleContractDO.getCustCode());
        itemDTO.setSaleType(saleContractDO.getSaleType());
        itemDTO.setTotalVolume(CalcSpecificationUtil.calcTotalVolumeByBoxCount(saleContractItem.getSpecificationList(), BigDecimal.valueOf(saleContractItem.getBoxCount())));
        itemDTO.setTotalWeight(CalcSpecificationUtil.calcTotalGrossweightByBoxCount(saleContractItem.getSpecificationList(), BigDecimal.valueOf(saleContractItem.getBoxCount())));
        return itemDTO;
    }

    @Override
    public Integer deleteSplitSaleItem(List<Long> saleContractItemIdList) {
        Integer convertPurchaseFlag = BooleanEnum.NO.getValue();
        if (CollUtil.isEmpty(saleContractItemIdList)) {
            return convertPurchaseFlag;
        }
        List<SaleContractItem> splitSaleItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(splitSaleItems)) {
            return convertPurchaseFlag;
        }
        List<Long> deleteBatchIds = splitSaleItems.stream().filter(s -> !s.getUniqueCode().equals(s.getSourceUniqueCode())).map(SaleContractItem::getId).distinct().toList();
        List<String> parentSaleItemUniqueCodes = splitSaleItems.stream().map(SaleContractItem::getSourceUniqueCode).distinct().toList();
        Map<String, Integer> splitSaleItemQuantityMap = splitSaleItems.stream().filter(s -> !s.getUniqueCode().equals(s.getSourceUniqueCode())).collect(Collectors.groupingBy(SaleContractItem::getSourceUniqueCode, Collectors.summingInt(SaleContractItem::getQuantity)));
        Optional<SaleContractItem> parentItemOpt = splitSaleItems.stream().filter(s -> s.getUniqueCode().equals(s.getSourceUniqueCode())).findFirst();
        List<SaleContractItem> parentSaleItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, parentSaleItemUniqueCodes);
        // 删除拆分后的明细
        if (CollUtil.isEmpty(deleteBatchIds)) {
           return BooleanEnum.YES.getValue();
        }
        saleContractItemMapper.deleteBatchIds(deleteBatchIds);
        // 回退拆分明细数量
        Map<String, String> configMap = configApi.getConfigMap();
        List<Long> contractIdList = parentSaleItems.stream().map(SaleContractItem::getContractId).distinct().toList();
        if (CollUtil.isEmpty(contractIdList)) {
            return convertPurchaseFlag;
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectBatchIds(contractIdList);
        if (CollUtil.isEmpty(saleContractDOS)) {
            return convertPurchaseFlag;
        }
        Map<Long, SaleContractDO> saleContractDOMap = new HashMap<>();
        saleContractDOS.forEach(s -> {
            saleContractDOMap.put(s.getId(), s);
        });
        List<SaleContractItem> updateList = new ArrayList<>();
        parentSaleItems.forEach(parentSaleItem -> {
            if (CollUtil.isEmpty(splitSaleItemQuantityMap)) {
                dealSourceSaleContractItem(parentSaleItem, updateList, parentItemOpt);
                return;
            }
            Integer splitSaleItemQuantity = splitSaleItemQuantityMap.get(parentSaleItem.getSourceUniqueCode());
            if (Objects.isNull(splitSaleItemQuantity)) {
                dealSourceSaleContractItem(parentSaleItem, updateList, parentItemOpt);
                return;
            }
            parentSaleItem.setQuantity(parentSaleItem.getQuantity() + splitSaleItemQuantity);
            parentSaleItem.setNeedPurQuantity(parentSaleItem.getNeedPurQuantity() + splitSaleItemQuantity);
            updateList.add(parentSaleItem);
        });
        // 更新回退数量后的源明细
        if (CollUtil.isNotEmpty(updateList)) {
            updateList.forEach(s -> {
                SaleContractDO saleContractDO = saleContractDOMap.get(s.getContractId());
                if (Objects.isNull(saleContractDO)) {
                    return;
                }
                Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate());
                CalcSaleContactUtil.calcSaleItemCost(s, saleContractDO, dailyRateMap, configMap);
            });
            saleContractItemMapper.updateBatch(updateList);
        }
       return convertPurchaseFlag;
    }

    private void dealSourceSaleContractItem(SaleContractItem parentSaleItem, List<SaleContractItem> updateList, Optional<SaleContractItem> parentItemOpt) {
        if (parentItemOpt.isPresent() && parentSaleItem.getUniqueCode().equals(parentItemOpt.get().getUniqueCode())) {
            parentSaleItem.setNeedPurQuantity(parentSaleItem.getQuantity() - parentSaleItem.getRealLockQuantity());
            parentSaleItem.setRealPurchaseQuantity(0);
            parentSaleItem.setConvertPurchaseFlag(BooleanEnum.NO.getValue());
            parentSaleItem.setSplitPurchaseList(new ArrayList<>());
            parentSaleItem.setSplitPurchaseFlag(BooleanEnum.NO.getValue());
            updateList.add(parentSaleItem);
        }
    }

    @Override
    public void rollbackSaleContractItemSourceList(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)) {
            return;
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        saleContractItemList.forEach(s -> {
            s.setNeedPurQuantity(s.getQuantity());
            s.setConvertPurchaseFlag(BooleanEnum.NO.getValue());
            s.setRealPurchaseQuantity(0);
            s.setSplitPurchaseFlag(BooleanEnum.NO.getValue());
            s.setSplitPurchaseList(new ArrayList<>());
            s.setSplitPurchaseQuantity(0);
        });
        saleContractItemMapper.updateBatch(saleContractItemList);
        List<String> sourceUniqueCodeList = saleContractItemList.stream().map(SaleContractItem::getUniqueCode).distinct().toList();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getSourceUniqueCode, sourceUniqueCodeList);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        List<Long> deleteIdList = saleContractItems.stream().filter(s -> {
            return !s.getUniqueCode().equals(s.getSourceUniqueCode());
        }).map(SaleContractItem::getId).toList();
        if (CollUtil.isEmpty(deleteIdList)) {
            return;
        }
        saleContractItemMapper.deleteBatchIds(deleteIdList);
    }

    @Override
    public Map<Long, String> getSaleContractItemIdMap(List<Long> saleContractItemIdList) {
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItems)) {
            return Map.of();
        }
        Map<Long, String> saleContractItemIdMap = new HashMap<>();
        List<String> sourceUniqueCodeList = saleContractItems.stream().map(SaleContractItem::getSourceUniqueCode).distinct().toList();
        List<SaleContractItem> parentSaleContractItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, sourceUniqueCodeList);
        saleContractItems.forEach(s -> {
            saleContractItemIdMap.put(s.getId(), s.getSourceUniqueCode());
        });
        return saleContractItemIdMap;
    }

    @Override
    public ContainerMidVO getOutNoticeMid(List<Long> ids) {
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(saleContractItems)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        boolean convertedNoticeFlag = saleContractItems.stream().anyMatch(s -> BooleanEnum.YES.getValue() == s.getConverNoticeFlag());
        if (convertedNoticeFlag){
            throw exception(SALE_CONTRACT_ITEM_CONVERTED_NOTICE);
        }
        // 过滤已转出运明细
        saleContractItems = saleContractItems.stream().filter(x -> x.getConverNoticeFlag().intValue() == BooleanEnum.NO.getValue()).toList();
        if (CollUtil.isEmpty(saleContractItems)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        // 赋值采购合同号 后续查询库存明细
        Map<Long, Map<String, List<SimpleContractDetailDTO>>> contractCodeMap = purchaseContractApi.getContractCodeMapByItemId(ids);
        if (CollUtil.isNotEmpty(contractCodeMap)) {
            saleContractItems.forEach(s -> {
                Map<String, List<SimpleContractDetailDTO>> skuContractCodeMap = contractCodeMap.get(s.getId());
                if (CollUtil.isEmpty(skuContractCodeMap)) {
                    return;
                }
                List<SimpleContractDetailDTO> purchaseContract = skuContractCodeMap.get(s.getSkuCode());
                if (CollUtil.isNotEmpty(purchaseContract)) {
                    s.setPurchaseContractCode(purchaseContract.get(0).getContractCode());
                }
            });
        }
        Optional<Long> first = saleContractItems.stream().map(SaleContractItem::getContractId).findFirst();
        SaleContractDO saleContractDO = saleContractMapper.selectById(first.get());
        saleContractItems.forEach(s -> s.setSales(saleContractDO.getSales()));
        ContainerMidVO containerMidVO = new ContainerMidVO();
        // 若存在原始外销合同，则使用原始外销合同关联
        containerMidVO.setLinkCodeList(saleContractDO.getLinkCodeList());
        containerMidVO.setCompanyId(saleContractDO.getCompanyId());
        // 查询锁定库存
        List<StockLockRespVO> stockLockRespVOList = iStockApi.listStockLock(new QueryStockReqVO().setSaleContractCode(saleContractDO.getCode())).stream().toList();
        JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
        Long lastCompanyId = saleContractDO.getCompanyId();
        if (Objects.nonNull(companyPath)) {
            JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(companyPath);
            if (Objects.nonNull(lastCompanyPath)) {
                lastCompanyId = lastCompanyPath.getId();
            }
        }
        // 查询库存明细
        List<StockDTO> stockDTOList = iStockApi.getStockDTOBySaleContractCodesAndLastCompanyIds(List.of(saleContractDO.getCode()), List.of(lastCompanyId));
        List<String> saleContractItemCodeList = saleContractItems.stream().map(SaleContractItem::getSkuCode).toList();
        Map<String, SkuNameDTO> skuNameCache = skuApi.getSkuNameCacheByCodeList(saleContractItemCodeList);
        // 如果没有库存则直接返回明细信息
        if (CollUtil.isEmpty(stockLockRespVOList) && CollUtil.isEmpty(stockDTOList)) {
            containerMidVO.setChildren(SaleContractConvert.INSTANCE.convertStockChildVOByItems(saleContractItems));
        } else {
            //锁定库存
            Map<String, List<StockLockRespVO>> stockLockRespVOMap = new HashMap<>();
            // 库存明细
            Map<String, List<StockDTO>> stockDTOMap = new HashMap<>();
            Map<Long, StockDTO> stockMap = new HashMap<>();
            Map<String, Integer> skuTypeMap = skuApi.getSkuTypeMap(saleContractItemCodeList);
            if (CollUtil.isNotEmpty(stockLockRespVOList)) {
                // 自主产品锁定库存替换为客户产品库存
                Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(saleContractItemCodeList);
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
            containerMidVO.setChildren(SaleContractConvert.INSTANCE.convertStockChildVOListBySaleContractItem(stockLockRespVOMap, stockDTOMap, saleContractItems, stockMap, skuTypeMap, saleContractDO));
        }
        //是否可自动加工标识赋值，组合产品拆分采购的可自动加工
        List<PurchasePlanItemDTO> purchasePlanItemList = purchasePlanApi.getPurchasePlanItemListBySaleContractCodeList(List.of(saleContractDO.getCode()));
        Map<String, List<PurchasePlanItemDTO>> purchasePlanItemMap = purchasePlanItemList.stream().collect(Collectors.groupingBy(s -> s.getSaleContractCode() + s.getSkuCode()));
        List<StockChildVO> children = containerMidVO.getChildren();
        children.forEach(cm -> {
            List<PurchasePlanItemDTO> purchasePlanItemDTOList = purchasePlanItemMap.get(cm.getSaleContractCode() + cm.getSkuCode());
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
    public List<CreatedResponse> createNotice(ContainerMidVO containerMidVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        StockNoticeSaveReqDTO stockNoticeSaveReqDTO = new StockNoticeSaveReqDTO();
        List<StockChildVO> children = containerMidVO.getChildren();
        // 获取销售明细
        List<Long> saleContractItemIdList = children.stream().map(StockChildVO::getSaleContractItemId).distinct().toList();
        if (CollUtil.isEmpty(saleContractItemIdList)){
            return responses;
        }
        boolean exists = saleContractItemMapper.exists(new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getId, saleContractItemIdList).eq(SaleContractItem::getConverNoticeFlag, BooleanEnum.YES.getValue()));
        if (exists){
            throw exception(SALE_CONTRACT_ITEM_CONVERTED_NOTICE);
        }
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
        List<String> purchaseContractCodeList = children.stream().map(StockChildVO::getPurchaseContractCode).distinct().toList();
        Set<String> batchCodeSet = new HashSet<>();
        //自动生产完成
        List<StockDTO> stockDTOList = iStockApi.getStockDTOBySaleContractCodes(saleContractCodeList);
        //自动生产完成lock表
        List<StockLockRespVO> stockLockRespVOList = iStockApi.getStockLockRespVOBySaleContractCodes(saleContractCodeList);
        if (CollUtil.isNotEmpty(stockLockRespVOList)) {
            stockLockRespVOList.forEach(s -> batchCodeSet.add(s.getBatchCode()));
            List<Long> lockStockIds = stockLockRespVOList.stream().map(StockLockRespVO::getStockId).distinct().toList();
            List<StockDTO> lockStockDTOList = iStockApi.getStockDTOByIdList(lockStockIds);
            if (CollUtil.isNotEmpty(lockStockDTOList)) {
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
                    Integer quantity = s.getQuantity();
                    List<BatchChildVO> shippChild = s.getChildren();
                    if (CollUtil.isEmpty(shippChild)) {
                        return quantity;
                    }
                    return quantity - shippChild.stream().map(BatchChildVO::getUsedQuantity).reduce(Integer::sum).orElse(0);
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
                        CompleteOrderReqDTO completeOrderReqDTO = new CompleteOrderReqDTO().setContractId(k).setDoneTime(LocalDateTime.now());
                        if (CollUtil.isNotEmpty(batchUserQuantityMap)) {
                            Map<String, Integer> usedQuantityMap = producingStockDTOList.stream().collect(Collectors.toMap(StockDTO::getBatchCode, s -> {
                                Integer usedQuantitiy = Objects.isNull(batchUserQuantityMap.get(s.getBatchCode())) ? 0 : batchUserQuantityMap.get(s.getBatchCode());
                                Integer productingQuantity = 0;
                                Integer availableQuantity = Objects.isNull(s.getAvailableQuantity()) ? 0 : s.getAvailableQuantity();
                                // 实际使用的在制数量
                                productingQuantity = Math.max(usedQuantitiy - availableQuantity, 0);
                                if (productingQuantity == 0) {
                                    // 需要使用子产品总数
                                    Integer needSonQuantity = Objects.isNull(sonSkuUsedQuantityMap.get(s.getSkuCode())) ? 0 : sonSkuUsedQuantityMap.get(s.getSkuCode());
                                    if (needSonQuantity - s.getProducingQuantity() > 0) {
                                        productingQuantity = s.getProducingQuantity();
                                        sonSkuUsedQuantityMap.put(s.getSkuCode(), needSonQuantity - s.getProducingQuantity());
                                    } else {
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
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        Map<Long, SaleContractItem> saleContractItemMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, s -> s, (x, y) -> x));
        children.forEach(cmv -> {
            // 采购合同信息
            Integer usedQuantitySum = cmv.getChildren().stream().map(BatchChildVO::getUsedQuantity).filter(Objects::nonNull).reduce(0, Integer::sum);
            Integer manufactureQuantity = cmv.getQuantity() - usedQuantitySum;
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
                    throw exception(PURCHASE_PLAN_COMBINE_ITEM_NOT_EXISTS);
                }
                //二级产品库存校验
                List<SkuBomDTO> skuBomTwoLevelList = skuApi.getSonSkuListBySkuCode(cmv.getSkuCode());
                if (validateStock(skuBomTwoLevelList, manufactureQuantity, newStockDTOList)) {
                    throw exception(STOCK_QUANTITY_NOT_ENOUGH);
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
                List<ManufactureSkuReqVO> manufactureSkuReqVOList = new ArrayList<>();
                ManufactureSkuReqVO manufactureSkuReqVO = new ManufactureSkuReqVO();
                manufactureSkuReqVO.setSkuCode(cmv.getSkuCode());
                manufactureSkuReqVO.setProcessingNum(manufactureQuantity);
                manufactureSkuReqVO.setSaleContractItemId(cmv.getSaleContractItemId());
                manufactureSkuReqVOList.add(manufactureSkuReqVO);
                //自动加工
                shipmentApi.batchManufactureSku(manufactureSkuReqVOList);
                List<StockDTO> stockDTOBySaleContractCodes = iStockApi.getStockDTOBySaleContractCodes(saleContractCodeList);
                //TODO  组装好的加工产品库存信息
                if (CollUtil.isNotEmpty(stockDTOBySaleContractCodes)) {
                    Map<String, List<StockDTO>> stockMap = stockDTOBySaleContractCodes.stream().collect(Collectors.groupingBy(StockDTO::getSkuCode));
                    String skuCode = cmv.getSkuCode();
                    List<StockDTO> stockDTOS = stockMap.get(skuCode);
                    List<StockDTO> transformStockDTOList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(stockDTOS)) {
                        stockDTOS.forEach(s -> {
                            Integer availableQuantity = Objects.isNull(s.getAvailableQuantity()) ? 0 : s.getAvailableQuantity();
                            Integer producingQuantity = Objects.isNull(s.getProducingQuantity()) ? 0 : s.getProducingQuantity();
                            Integer usedQuantity = batchUserQuantityMap.get(s.getBatchCode());
                            if (Objects.nonNull(usedQuantity)) {
                                int calcQuantity = availableQuantity + producingQuantity - usedQuantity;
                                if (calcQuantity <= 0) {
                                    return;
                                }
                                s.setAvailableQuantity(calcQuantity);
                                s.setProducingQuantity(0);
                                transformStockDTOList.add(s);
                            } else {
                                batchUserQuantityMap.put(s.getBatchCode(), availableQuantity + producingQuantity);
                                transformStockDTOList.add(s);
                            }
                        });
                    }
                    List<BatchChildVO> batchChildVOS = SaleContractConvert.INSTANCE.convertBatchildByStockList(saleContractItemMap.get(cmv.getSaleContractItemId()), transformStockDTOList, 0);
                    List<BatchChildVO> list = batchChildVOS.stream().filter(x -> x.getAvailableCabinetQuantity() > 0).collect(Collectors.toList());
                    if (CollUtil.isEmpty(list)) {
                        return;
                    }
                    list.stream().filter(s -> !sourceStockBatchCodeSet.contains(s.getBatchCode())).forEach(s -> {
                        s.setUsedQuantity(s.getAvailableQuantity());
                    });
                    List<BatchChildVO> cmvChildren = new ArrayList<>(Optional.ofNullable(cmv.getChildren())
                            .orElse(Collections.emptyList()));
                    
                    // 获取已存在的批次号，避免重复添加
                    Set<String> existingBatchCodes = cmvChildren.stream()
                            .map(BatchChildVO::getBatchCode)
                            .filter(StrUtil::isNotEmpty)
                            .collect(Collectors.toSet());
                    
                    // 只添加不存在的批次
                    List<BatchChildVO> newBatchList = list.stream()
                            .filter(s -> !existingBatchCodes.contains(s.getBatchCode()))
                            .collect(Collectors.toList());
                    
                    if (CollUtil.isEmpty(cmvChildren)) {
                        cmv.setChildren(newBatchList);
                    } else if (CollUtil.isNotEmpty(newBatchList)) {
                        cmvChildren.addAll(newBatchList);
                        cmv.setChildren(cmvChildren);
                    }
                }
            }
        });
        stockNoticeSaveReqDTO.setIsContainerTransportation(BooleanEnum.NO.getValue());
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
        stockNoticeSaveReqDTO.setCompanyId(companyId);
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        String companyName = "";
        if (ObjectUtil.isNotNull(companyDTO)) {
            companyName = companyDTO.getCompanyName();
        }
        stockNoticeSaveReqDTO.setCompanyName(companyName);
        List<StockNoticeItemDTO> itemDTOList = new ArrayList<>();

        List<Long> itemIds = children.stream().map(StockChildVO::getSaleContractItemId).distinct().toList();
        Map<Long, String> itemIdMap = getItemCodesByIds(itemIds);

        children.forEach(s -> {
            List<BatchChildVO> childList = s.getChildren();
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            childList.forEach(child -> {
                String batchCode = child.getBatchCode();
                StockNoticeItemDTO stockNoticeItemDTO = new StockNoticeItemDTO();
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
                stockNoticeItemDTO.setShippedAddress(child.getShippedAddress());
                stockNoticeItemDTO.setSourceUniqueCode(IdUtil.fastSimpleUUID());
                stockNoticeItemDTO.setQtyPerInnerbox(child.getQtyPerInnerbox());
                stockNoticeItemDTO.setQtyPerOuterbox(child.getQtyPerOuterbox());
                stockNoticeItemDTO.setSpecificationList(child.getSpecificationList());
                stockNoticeItemDTO.setSplitBoxFlag(child.getSplitBoxFlag());

                stockNoticeItemDTO.setTotalWeight(s.getTotalGrossweight());
                stockNoticeItemDTO.setSourceUniqueCode(s.getSourceUniqueCode());
                Integer orderQuantity = child.getUsedQuantity() + baseOrderQuantity;
                stockNoticeItemDTO.setOrderQuantity(orderQuantity);
                if(child.getQtyPerOuterbox()==null || child.getQtyPerOuterbox()==0){
                    throw exception(QTY_PEROUTERBOX_ZERO);
                }
                Integer orderBoxQuantity = NumberUtil.div(orderQuantity, child.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
                stockNoticeItemDTO.setOrderBoxQuantity(orderBoxQuantity);
                stockNoticeItemDTO.setTotalVolume(CalcSpecificationUtil.calcTotalVolumeByBoxCount(child.getSpecificationList(), BigDecimal.valueOf(orderBoxQuantity)));
                stockNoticeItemDTO.setTotalWeight(CalcSpecificationUtil.calcTotalGrossweightByBoxCount(child.getSpecificationList(), BigDecimal.valueOf(orderBoxQuantity)));
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
                if (Objects.nonNull(stockNoticeItemDTO.getOrderQuantity()) && stockNoticeItemDTO.getOrderQuantity() > 0) {
                    itemDTOList.add(stockNoticeItemDTO);
                }
            });
        });
        if (CollUtil.isNotEmpty(itemDTOList)) {
            stockNoticeSaveReqDTO.setNoticeItems(itemDTOList);
        }
        stockNoticeSaveReqDTO.setPurContractCodeList(purchaseContractCodeList);
        // 更新销售明细转拉柜通知单标识
        if (!CollectionUtils.isEmpty(saleContractItems)) {
            saleContractItems.forEach(x -> x.setConverNoticeFlag(BooleanEnum.YES.getValue()));
            saleContractItemMapper.updateBatch(saleContractItems);
        }

        //  回写进仓日期
//        List<ShipmentDO> updateList = shipmentItemList.stream().map(s -> {
//            ShipmentDO shipmentDO = new ShipmentDO();
//            shipmentDO.setId(s.getShipmentId());
//            shipmentDO.setInboundDate(containerMidVO.getInboundDate());
//            return shipmentDO;
//        }).distinct().toList();
//        shipmentMapper.updateBatch(updateList);
        CreatedResponse notice = iStockNoticeApi.createNotice(stockNoticeSaveReqDTO);
        // 更新库存拉柜数量
        List<StockNoticeItemDTO> noticeItems = stockNoticeSaveReqDTO.getNoticeItems();
        Map<String, Map<String, Integer>> updateQunatityMap = new HashMap<>();
        Map<String, List<StockNoticeItemDTO>> noticeItemMap = noticeItems.stream().collect(Collectors.groupingBy(StockNoticeItemDTO::getSaleContractCode));
        noticeItemMap.forEach((k, v) -> {
            Map<String, Integer> updateMap = new HashMap<>();
            v.forEach(s -> {
                updateMap.merge(s.getBatchCode(), s.getOrderQuantity(), Integer::sum);
            });
            updateQunatityMap.put(k, updateMap);
        });
        iStockApi.updateStockCabinetQuantity(updateQunatityMap);
        responses.add(notice);
        return responses;
    }

    @Override
    public void genInternalContractByOutBill(Map<String, Integer> saleItemMap, String genContractUniqueCode) {
        if (CollUtil.isEmpty(saleItemMap)) {
            return;
        }
        Set<String> saleItemUniqueCodeSet = saleItemMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, saleItemUniqueCodeSet);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        Map<Long, Integer> updateQuantityMap = new HashMap<>();
        saleContractItems.forEach(s -> {
            updateQuantityMap.merge(s.getId(), saleItemMap.get(s.getUniqueCode()), Integer::sum);
        });
        List<Long> contractIdList = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().toList();
        List<SaleContractDO> saleContracts = saleContractMapper.selectList(new LambdaQueryWrapper<SaleContractDO>().in(SaleContractDO::getId, contractIdList).eq(SaleContractDO::getSaleType, SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue()));
        if (CollUtil.isEmpty(saleContracts)) {
            return;
        }
        List<SplitPurchase> combineList = saleContractItems.stream().map(SaleContractItem::getSplitPurchaseList).flatMap(List::stream).toList();
        Map<Long, SaleContractDO> saleContractMap = saleContracts.stream().collect(Collectors.toMap(SaleContractDO::getId, s -> s));
        Map<Long, List<SaleContractItem>> genSaleItemMap = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
        genSaleItemMap.forEach((k, v) -> {
            SaleContractDO saleContractDO = saleContractMap.get(k);
            // 3. 若存在组合产品分开采购的情况，生成尾部节点与加工型企业的销售合同
            List<CreatedResponse> createdResponses = handleLastToProducedContract(saleContractDO, v, combineList, null, updateQuantityMap, genContractUniqueCode);
            // 4. 若锁定库存中存在加工型企业，生成锁定库存的购销合同
            handleProducedLockContract(saleContractDO, v, null, genContractUniqueCode);
            // 5. 转换尾部节点向供应商采购合同，存在内部供应商时进行转换
            AtomicReference<String> codePrefix = new AtomicReference<>();
            if (CollUtil.isNotEmpty(createdResponses)){
                createdResponses.stream().findFirst().ifPresent(s->{
                    codePrefix.set(s.getCode());
                });
            }
            lastCompanyToVenderContract(saleContractDO, v, null, updateQuantityMap, genContractUniqueCode, codePrefix.get());
        });
    }

    @Override
    public Map<String, JsonCompanyPath> getCompanyPathMapBySaleContractCodeList(List<String> saleContractCodes) {
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapper<SaleContractDO>().select(SaleContractDO::getCode, SaleContractDO::getCompanyPath).in(SaleContractDO::getCode, saleContractCodes).ne(SaleContractDO::getStatus, SaleContractStatusEnum.CASE_CLOSED.getValue()));
        if (CollUtil.isEmpty(saleContractDOS)){
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        return saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getCompanyPath));
    }

    @Override
    public void batchUpdatePurchaseUser(Set<String> saleContractCodeSet) {
        if (CollUtil.isEmpty(saleContractCodeSet)){
            return;
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapper<SaleContractDO>().select(SaleContractDO::getCode, SaleContractDO::getId).in(SaleContractDO::getCode,saleContractCodeSet));
        if (CollUtil.isEmpty(saleContractDOS)){
            return;
        }
        // 获取采购员
        Map<String, List<UserDept>> purchaseUserMap = purchaseContractApi.getPurchaseUserListBySaleContractCodeSet(saleContractCodeSet);
        saleContractDOS.forEach(s->{
            if (CollUtil.isEmpty(purchaseUserMap)){
                return;
            }
            s.setPurchaseUserList(purchaseUserMap.get(s.getCode()));
        });
        saleContractMapper.updateBatch(saleContractDOS);
    }

    @Override
    public void rewritePurchaseFreeQuantity(Map<Long, Integer> freeQUantityMap,boolean isConcel) {
        if (CollUtil.isEmpty(freeQUantityMap)){
            return;
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(freeQUantityMap.keySet());
        if (CollUtil.isEmpty(saleContractItems)){
            return;
        }
        saleContractItems.forEach(s->{
            Integer purchaseFreeQuantity = Objects.isNull(s.getPurchaseFreeQuantity())?0:s.getPurchaseFreeQuantity();
            Integer currentFreeQuantity = freeQUantityMap.get(s.getId());
            if (Objects.isNull(currentFreeQuantity)||currentFreeQuantity == 0){
                return;
            }
            if (isConcel){
                purchaseFreeQuantity -=currentFreeQuantity;
            }else {
                purchaseFreeQuantity +=currentFreeQuantity;
            }
            s.setPurchaseFreeQuantity(purchaseFreeQuantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String,List<UserDept>> getSalesByContractCodeSet(Set<String> contractCodeList) {
        if (CollUtil.isEmpty(contractCodeList)){
            return Map.of();
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapper<SaleContractDO>().select(SaleContractDO::getSales).in(SaleContractDO::getCode, contractCodeList));
        if (CollUtil.isEmpty(saleContractDOS)){
            return Map.of();
        }
        Map<String,List<UserDept>> result = new HashMap<>();
        Map<Long,UserDept> salesMap = new HashMap<>();
         saleContractDOS.forEach(s->{
             salesMap.put(s.getSales().getUserId(),s.getSales());
         });
         if (CollUtil.isNotEmpty(salesMap)){
             result.put(CommonDict.SALES_FIELD_NAME,salesMap.values().stream().collect(Collectors.toList()));
         }
         return result;
    }

    @Override
    public void updateLinkCodeList(Map<String,String> linkCodeMap) {
        if (CollUtil.isEmpty(linkCodeMap)){
            return;
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapper<SaleContractDO>().select(SaleContractDO::getId, SaleContractDO::getCode,SaleContractDO::getLinkCodeList).in(SaleContractDO::getCode, linkCodeMap.keySet()));
        if (CollUtil.isEmpty(saleContractDOS)){
            return;
        }
        saleContractDOS.forEach(s->{
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
        saleContractMapper.updateBatch(saleContractDOS);
    }

    public boolean validateStock(List<SkuBomDTO> skuBomDTOList, Integer needQuantity, List<StockDTO> stockDTOList) {
        if (skuBomDTOList != null && !skuBomDTOList.isEmpty()) {
            if (stockDTOList == null || stockDTOList.isEmpty()) {
                return true;
            }
            //子产品可加工主产品数量
            AtomicReference<Integer> bomToSkuNum = new AtomicReference<>(0);
            skuBomDTOList.forEach(sbd -> {
                List<StockDTO> stockSkuBomList = stockDTOList.stream().filter(i -> i.getSkuCode().equals(sbd.getSkuCode())).toList();
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
    public void deleteSaleContract(Long id) {
        // 校验存在
        SaleContractDO saleContractDO = validateSaleContractExists(id);
        // 删除
        saleContractMapper.deleteById(id);
        // 释放销售合同锁定库存
        stockApi.cancelStockLock(saleContractDO.getCode(), null, null);

        // 删除销售明细
        saleContractItemMapper.delete(SaleContractItem::getContractId, id);
        // 删除加减项
        addSubItemMapper.delete(AddSubItem::getContractCode, saleContractDO.getCode());
        // 删除收款计划
        collectionPlanMapper.delete(CollectionPlan::getContractId, id);

        // 记录操作日志
        OperateLogUtils.setContent(String.format(SaleDict.SALE_LOGGER_DELETE_FORMAT, saleContractDO.getCode()));
        OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContractDO.getCode());
    }

    @DataPermission(enable = false)
    private SaleContractDO validateSaleContractExists(Long id) {
        SaleContractDO saleContractDO = saleContractMapper.selectById(id);
        if (Objects.isNull(saleContractDO)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        return saleContractDO;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO, boolean changeFlag) {
        if (changeFlag) {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), SaleDict.PROCESS_DEFINITION_CHANGE_KEY);
        } else {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), SaleDict.PROCESS_DEFINITION_KEY_EXPORT);
        }

    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO, boolean changeFlag) {
        if (changeFlag) {
            bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class), SaleDict.PROCESS_DEFINITION_CHANGE_KEY);
        } else {
            bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class), SaleDict.PROCESS_DEFINITION_KEY_EXPORT);
        }
    }

    @Override
    public void submitExportTask(Long saleContractId, Long userId) {
        UserDept user = adminUserApi.getUserDeptByUserId(userId);
        String deptCode = user.getDeptCode();
        Map<String, Object> variable = new HashMap<>();
        variable.put("deptCode", deptCode);
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, SaleDict.PROCESS_DEFINITION_KEY_EXPORT, saleContractId, variable, null);
        updateAuditStatus(saleContractId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId, SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue());
    }

    @Override
    public void submitDomesticTask(Long saleContractId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, SaleDict.PROCESS_DEFINITION_KEY_DOMESTIC, saleContractId);
        updateAuditStatus(saleContractId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId, SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue());
    }

    @Override
    public void submitfactoryTask(Long saleContractId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, SaleDict.PROCESS_DEFINITION_KEY_FACTORY, saleContractId);
        updateAuditStatus(saleContractId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId, SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId, Integer status) {
        SaleContractDO saleContractDO = validateSaleContractExists(auditableId);
        saleContractDO.setAuditStatus(auditStatus);
        saleContractDO.setStatus(status);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            saleContractDO.setProcessInstanceId(processInstanceId);
        }
        saleContractMapper.updateById(saleContractDO);
        List<SaleContractItem> itemList = saleContractItemMapper.selectList(SaleContractItem::getContractId, auditableId);
        if (CollUtil.isEmpty(itemList)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        if (Objects.equals(auditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            itemList.forEach(s -> {
                if (s.getRealLockQuantity() > 0) {
                    s.setReLockFlag(BooleanEnum.YES.getValue());
                }
            });
            saleContractItemMapper.updateBatch(itemList);
            // 内销审核通过逻辑
            if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleContractDO.getSaleType())) {
                String custCode = saleContractDO.getCustCode();
                boolean internalFlag = custApi.checkIsInternalCust(custCode);
                if (internalFlag) {
                    Long internalCompanyId = custApi.getInternalCompany(custCode);
                    if (Objects.isNull(internalCompanyId)) {
                        throw exception(INTERNAL_COMPANY_NOT_EXISTS);
                    }
                    // 内部客户的内销合同审核通过创建内部客户和下单主体间的采购合同
                    List<CreatedResponse> purchaseContractResponse = genPurchaseContract(saleContractDO, itemList, internalCompanyId);
                    // 处理库存
                    dealStockMsg(saleContractDO, itemList, internalCompanyId,purchaseContractResponse);
                    saleContractDO.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
                    saleContractMapper.updateById(saleContractDO);
                }
            }
        }
    }

    private void updateBySaleContractDate(SaleContractDO saleContractDO,LocalDateTime dateTime){
        //设置合同时间为当天时间
        saleContractDO.setSaleContractDate(dateTime);
        //修改当天汇率
        Map<String, BigDecimal> rateMap = rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate());
        if(CollUtil.isEmpty(rateMap)){
            throw exception(RATE_NOT_EXISTS);
        }
        saleContractDO.setExchangeRate(rateMap.get(saleContractDO.getCurrency()));
        saleContractDO.setUsdRate(rateMap.get("USD"));
        //重新计算利润
        SaleContractSaveReqVO saleContractSaveReqVO = BeanUtils.toBean(saleContractDO, SaleContractSaveReqVO.class);
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getContractCode, saleContractDO.getCode());

        saleContractSaveReqVO.setChildren(saleContractItems);
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMapByDate(dateTime);
        CalcContactUtil.checkItemCost(saleContractSaveReqVO,configApi.getConfigMap(),dailyRateMap,saleContractDO.getCurrency());
        SaleContractDO contractDO = BeanUtils.toBean(saleContractSaveReqVO, SaleContractDO.class);
        List<SaleContractItem> itemList = BeanUtils.toBean(saleContractSaveReqVO.getChildren(), SaleContractItem.class);
        List<AddSubItem> addSubItems = addSubItemMapper.selectList(AddSubItem::getContractCode, contractDO.getCode() );
        CalcSaleContactUtil.calcSaleContractCost(contractDO, itemList, dailyRateMap, addSubItems);
        contractDO.setGrossProfitMargin(contractDO.getRealGrossProfitMargin());
        saleContractMapper.updateById(contractDO);
        saleContractItemMapper.updateBatch(itemList);
    }

    private void dealStockMsg(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, Long internalCompanyId,List<CreatedResponse> purchaseContractResponse) {
         // 查询锁定库存
        List<StockLockRespVO> stockLockList = iStockApi.getStockLockListBySaleCode(saleContractDO.getCode());
        List<StockDTO> stockDTOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(stockLockList)) {
            Map<String, String> lockSkuCodeMap = new HashMap<>();
            stockLockList.forEach(s -> {
                lockSkuCodeMap.put(s.getBatchCode(), s.getSkuCode());
            });
            Map<String, Integer> stockLockMap = stockLockList.stream().collect(Collectors.toMap(StockLockRespVO::getBatchCode, StockLockRespVO::getLockQuantity, Integer::sum));
            List<StockDTO> stockDTOByBatchCodes = iStockApi.getStockDTOByBatchCodes(stockLockMap.keySet());
            if (CollUtil.isNotEmpty(stockDTOByBatchCodes)) {
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
                        if(!CollectionUtils.isEmpty(purchaseContractResponse)){
                            x.setPurchaseContractCode(purchaseContractResponse.get(0).getCode());
                            x.setPurchaseContractId(purchaseContractResponse.get(0).getId());
                        }  
                        stockDTOS.add(x);
                    }
                });
                if (CollUtil.isNotEmpty(stockDTOS)) {
                    stockDTOList.addAll(stockDTOS);
                }
            }
        }
        if (CollUtil.isEmpty(stockDTOList)) {
            return;
        }
        // 自动生产完成
        stockDTOList.forEach(s->{
            CompleteOrderReqDTO completeOrderReqDTO = new CompleteOrderReqDTO().setContractId(s.getPurchaseContractId()).setDoneTime(LocalDateTime.now());
            completeOrderReqDTO.setUsedQuantityMap(Map.of(s.getBatchCode(),s.getAvailableQuantity()));
            completeOrderReqDTO.setDomesticSaleFlag(true);
            purchaseContractApi.completeOrderTask(completeOrderReqDTO);
        });
        BillSaveReqVO outBIllSaveReqVO = new BillSaveReqVO();
        outBIllSaveReqVO.setSaleContractId(saleContractDO.getId());
        outBIllSaveReqVO.setSaleContractCode(saleContractDO.getCode());
        List<BillItemSaveReqVO> outBillItemSaveReqVOList = new ArrayList<>();
        Map<String, List<StockDTO>> skuStockMap = stockDTOList.stream().collect(Collectors.groupingBy(StockDTO::getSkuCode));
        saleContractItems.forEach(s -> {
            List<StockDTO> stockDTOS = skuStockMap.get(s.getSkuCode());
            if (CollUtil.isEmpty(stockDTOS)) {
                return;
            }
            AtomicReference<Integer> quantity = new AtomicReference<>(s.getQuantity());
            AtomicInteger sortNum = new AtomicInteger(1);
            stockDTOS.forEach(stockDTO -> {
                int stockQuantity = stockDTO.getAvailableQuantity() + stockDTO.getProducingQuantity();
                if (stockQuantity <= 0) {
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
                BillItemSaveReqVO sonBillItemSaveReqVO = BeanUtils.toBean(stockDTO, BillItemSaveReqVO.class);
                sonBillItemSaveReqVO.setSortNum(sortNum.get()).setSourceSortNum(s.getSortNum());
                sonBillItemSaveReqVO.setOrderQuantity(actQuantity).setActQuantity(actQuantity);
                sonBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
                sonBillItemSaveReqVO.setBasicSkuCode(s.getBasicSkuCode());
                BigDecimal boxQuantity = NumUtil.div(actQuantity, stockDTO.getQtyPerOuterbox()).setScale(2, RoundingMode.HALF_UP);
                sonBillItemSaveReqVO.setOrderBoxQuantity(boxQuantity.intValue());
                sonBillItemSaveReqVO.setActBoxQuantity(boxQuantity.intValue());
                sonBillItemSaveReqVO.setCustId(saleContractDO.getCustId());
                sonBillItemSaveReqVO.setCustName(saleContractDO.getCustName());
                sonBillItemSaveReqVO.setCustCode(saleContractDO.getCustCode());
                sonBillItemSaveReqVO.setAbnormalStatus(s.getAbnormalStatus());
                if(s.getPurchaseUser()!=null){
                    sonBillItemSaveReqVO.setPurchaserId(s.getPurchaseUser().getUserId());
                    sonBillItemSaveReqVO.setPurchaserDeptId(s.getPurchaseUser().getDeptId());
                }
                if(Objects.nonNull(saleContractDO.getSales())){
                    sonBillItemSaveReqVO.setSales(saleContractDO.getSales());
                }
                if(!CollectionUtils.isEmpty(purchaseContractResponse)){
                    sonBillItemSaveReqVO.setPurchaseContractCode(purchaseContractResponse.get(0).getCode());
                    sonBillItemSaveReqVO.setPurchaseContractId(purchaseContractResponse.get(0).getId());
                }
                outBillItemSaveReqVOList.add(sonBillItemSaveReqVO);
            });
        });
        outBIllSaveReqVO.setBillItemSaveReqVOList(outBillItemSaveReqVOList);
        BillSaveReqVO inBIllSaveReqVO = new BillSaveReqVO();
        String custCode = saleContractDO.getCustCode();
        boolean internalCustFlag = custApi.checkIsInternalCust(custCode);
        if (!internalCustFlag){
            inBIllSaveReqVO.setSaleContractCode(saleContractDO.getCode());
            inBIllSaveReqVO.setSaleContractId(saleContractDO.getId());
        }
        inBIllSaveReqVO.setCompanyId(internalCompanyId);
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(internalCompanyId);
        if (Objects.nonNull(companyDTO)) {
            inBIllSaveReqVO.setCompanyName(companyDTO.getName());
        }
        WarehouseDTO defaultWareMouse = warehouseApi.getDefaultWareMouse();
        inBIllSaveReqVO.setWarehouseId(defaultWareMouse.getId());
        inBIllSaveReqVO.setWarehouseName(defaultWareMouse.getName());
        List<SaleContractItem> calcSaleContractItems = calcStockLockPrice(saleContractItems);
        List<BillItemSaveReqVO> inBillItemList = calcSaleContractItems.stream().map(s -> {
            BillItemSaveReqVO inBillItemSaveReqVO = BeanUtils.toBean(s, BillItemSaveReqVO.class);
            inBillItemSaveReqVO.setSortNum(CalculationDict.ONE).setSourceSortNum(s.getSortNum());
            inBillItemSaveReqVO.setPosition(CommonDict.DEFAULT_POSITION).setOrderQuantity(s.getQuantity()).setActQuantity(s.getQuantity());
            inBillItemSaveReqVO.setWarehouseId(defaultWareMouse.getId()).setWarehouseName(defaultWareMouse.getName());
            inBillItemSaveReqVO.setCompanyId(internalCompanyId);
            if (Objects.nonNull(companyDTO)) {
                inBillItemSaveReqVO.setCompanyName(companyDTO.getName());
            }
            inBillItemSaveReqVO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
            inBillItemSaveReqVO.setSkuName(s.getName());
            SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(internalCompanyId);
            if (Objects.nonNull(simpleVenderRespDTO)) {
                inBillItemSaveReqVO.setVenderName(simpleVenderRespDTO.getName());
                inBillItemSaveReqVO.setVenderCode(simpleVenderRespDTO.getCode());
                inBillItemSaveReqVO.setVenderId(simpleVenderRespDTO.getId());
            } else {
                inBillItemSaveReqVO.setVenderName(saleContractDO.getCustName());
            }
            inBillItemSaveReqVO.setCustId(saleContractDO.getCustId());
            inBillItemSaveReqVO.setCustName(saleContractDO.getCustName());
            inBillItemSaveReqVO.setCustCode(saleContractDO.getCustCode());
            if(s.getPurchaseUser()!=null){
                inBillItemSaveReqVO.setPurchaserId(s.getPurchaseUser().getUserId());
                inBillItemSaveReqVO.setPurchaserDeptId(s.getPurchaseUser().getDeptId());
            }
            if(Objects.nonNull(saleContractDO.getSales())){
                inBillItemSaveReqVO.setSales(saleContractDO.getSales());
            }
            Integer boxQuantity = NumberUtil.div(s.getQuantity(), s.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
            inBillItemSaveReqVO.setOrderQuantity(s.getQuantity()).setOrderBoxQuantity(boxQuantity);
            inBillItemSaveReqVO.setActQuantity(s.getQuantity()).setActBoxQuantity(boxQuantity);
            JsonAmount price = skuApi.getCombSkuPrice(inBillItemSaveReqVO.getSkuId());
            if(!s.getSpecificationList().isEmpty()){
                inBillItemSaveReqVO.setTotalWeight(CalcSpecificationUtil.calcTotalGrossweightByBoxCount(s.getSpecificationList(),new BigDecimal(s.getBoxCount())));
            }
            if (Objects.isNull(price)||Objects.isNull(price.getAmount())||BigDecimal.ZERO.compareTo(price.getAmount())==0){
                price =s.getStockLockPrice();
            }
            inBillItemSaveReqVO.setPrice(price);
            if(!CollectionUtils.isEmpty(purchaseContractResponse)){
                inBillItemSaveReqVO.setPurchaseContractCode(purchaseContractResponse.get(0).getCode());
                inBillItemSaveReqVO.setPurchaseContractId(purchaseContractResponse.get(0).getId());
            }
            return inBillItemSaveReqVO;
        }).toList();
        if (CollUtil.isNotEmpty(inBillItemList)) {
            inBIllSaveReqVO.setBillItemSaveReqVOList(inBillItemList);
        }
        iStockApi.handleManufactureBillAndStock(outBIllSaveReqVO, inBIllSaveReqVO);
    }

    private List<CreatedResponse> genPurchaseContract(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, Long internalCompany) {
        if (Objects.isNull(saleContractDO)) {
            return List.of();
        }
        UserDept sales = saleContractDO.getSales();
        SavePurchaseContractReqVO savePurchaseContractReqVO = BeanUtils.toBean(saleContractDO, SavePurchaseContractReqVO.class);
        savePurchaseContractReqVO.setId(null);
        savePurchaseContractReqVO.setAutoFlag(BooleanEnum.YES.getValue());
        savePurchaseContractReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        savePurchaseContractReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
        savePurchaseContractReqVO.setSaleContractId(saleContractDO.getId());
        savePurchaseContractReqVO.setSaleContractCode(saleContractDO.getCode());
        savePurchaseContractReqVO.setCompanyId(internalCompany);
        SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(saleContractDO.getCompanyId());
        if (Objects.isNull(simpleVenderRespDTO)) {
            throw exception(INTERNAL_COMPANY_VENDER_NOT_EXISTS);
        }
        savePurchaseContractReqVO.setVenderId(simpleVenderRespDTO.getId());
        savePurchaseContractReqVO.setVenderCode(simpleVenderRespDTO.getCode());
        List<SavePurchaseContractItemReqVO> purchaseContractItemReqVOS = saleContractItems.stream().map(s -> {
            SavePurchaseContractItemReqVO savePurchaseContractItemReqVO = BeanUtils.toBean(s, SavePurchaseContractItemReqVO.class);
            savePurchaseContractItemReqVO.setPurchaseUserId(sales.getUserId());
            savePurchaseContractItemReqVO.setPurchaseUserName(sales.getNickname());
            savePurchaseContractItemReqVO.setPurchaseUserDeptId(sales.getDeptId());
            savePurchaseContractItemReqVO.setPurchaseUserDeptName(sales.getDeptName());
            savePurchaseContractItemReqVO.setSaleContractId(saleContractDO.getId());
            savePurchaseContractItemReqVO.setSaleContractCode(saleContractDO.getCode());
            savePurchaseContractItemReqVO.setSaleContractItemId(s.getId());
            savePurchaseContractItemReqVO.setWithTaxPrice(s.getUnitPrice());
            savePurchaseContractItemReqVO.setSaleItemUniqueCode(s.getUniqueCode());
            return savePurchaseContractItemReqVO;
        }).toList();
        if (CollUtil.isNotEmpty(purchaseContractItemReqVOS)) {
            savePurchaseContractReqVO.setChildren(purchaseContractItemReqVOS);
        }
        return purchaseContractApi.genPurchaseContract(savePurchaseContractReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> changeSaleContract(ChangeSmsContractSaveReqVO updateReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        SaleContractRespVO saleContract = updateReqVO.getSaleContractRespVO();
        SaleContractRespVO oldSaleContract = updateReqVO.getOld_saleContractRespVO();
        // 订单路径校验
        boolean sameTree = isSameTree(saleContract.getCompanyPath(), oldSaleContract.getCompanyPath());
        if (SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue().equals(oldSaleContract.getStatus())&&!sameTree){
            throw exception(EXISTS_NEST_ORDER);
        }
        // 收款计划校验
        List<CollectionPlan> oldCollectionPlanList = oldSaleContract.getCollectionPlanList();
//        if (CollUtil.isNotEmpty(oldCollectionPlanList)){
//            boolean collectFlag = oldCollectionPlanList.stream().anyMatch(s -> CollUtil.isNotEmpty(s.getChildren()));
//            if (collectFlag){
//                throw exception(COLLECTION_FLAG);
//            }
//        }
        // 提交流程标识 默认未提交
        AtomicReference<Integer> submitFlag = new AtomicReference<>(0);
        // 单据信息变更
        Set<String> changeFields = new ChangeCompareUtil<SaleContractRespVO>().transformObject(oldSaleContract, saleContract);
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("sms_sale_contract", "sms_sale_contract_item", "sms_add_sub_term", "sms_collection_plan"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(SHIPMENT_FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        String code = saleContract.getCode();
        SaleContractChange saleContractChange = SaleContractConvert.INSTANCE.convertSaleContractChange(saleContract);
        saleContractChange.setOldData(oldSaleContract);
        saleContractChange.setCode(code);
        saleContractChange.setSaleType(saleContract.getSaleType());
        // 处理影响范围
        ChangeEffectRespVO changeEffect = getChangeEffect(saleContract);
        List<JsonEffectRange> effectRangeList = Objects.isNull(changeEffect) ? new ArrayList<>() : Objects.isNull(changeEffect.getEffectRangeList()) ? new ArrayList<>() : changeEffect.getEffectRangeList();
        // 比对变更配置字段
        submitFlag = formChangeApi.dealShipmentTable(changeFields, formChangeDTOList, saleContractChange, "sms_sale_contract", submitFlag, true);
        // 加减项变更
        List<AddSubItem> addSubItemList = saleContract.getAddSubItemList();
        if (CollUtil.isNotEmpty(addSubItemList)) {
            addSubItemList.forEach(s -> s.setContractCode(code));
        }
        List<AddSubItem> oldAddSubItemList = oldSaleContract.getAddSubItemList();
        saleContractChange.setAddSubItemList(dealChangeField(oldAddSubItemList, addSubItemList, AddSubItem.class, "sms_add_sub_term", saleContractChange, formChangeDTOList, submitFlag).get(0));
        // 收款计划变更
        List<CollectionPlan> collectionPlanList = saleContract.getCollectionPlanList();
//        if (CollUtil.isNotEmpty(collectionPlanList)) {
//            Map<String, BigDecimal> dailyRateMapByDate = rateApi.getDailyRateMapByDate(saleContractChange.getSaleContractDate());
//            collectionPlanList.forEach(s -> {
//                s.setContractId(saleContract.getId());
//                calcCollectionPlanAmount(s,saleContract.getCollectionTotal(), addSubItemList, dailyRateMapByDate);
//            });
//        }
        saleContractChange.setCollectionPlanList(dealChangeField(oldCollectionPlanList, collectionPlanList, CollectionPlan.class, "sms_collection_plan", saleContractChange, formChangeDTOList, submitFlag).get(0));
        // 处理销售明细变更逻辑
        List<SaleContractItem> saleContractItems = dealSaleItemChange(saleContract, oldSaleContract, saleContractChange, formChangeDTOList, submitFlag, effectRangeList);
        saleContractChange.setChildren(saleContractItems);
        saleContractChange.setSourceCode(saleContract.getCode());
        saleContractChange.setCreateUser(adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId()));
        saleContractChange.setId(null);
        if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
            saleContractChange.setModelKey(SaleDict.DOMESTIC_DEFINITION_CHANGE_KEY);
        }else if (SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
            saleContractChange.setModelKey(SaleDict.PROCESS_DEFINITION_CHANGE_KEY);
        }else if (SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
            saleContractChange.setModelKey(SaleDict.FACTORY_DEFINITION_CHANGE_KEY);
        }
        saleContractChange.setEffectRangeList(effectRangeList);
        // 入库
        saleContractChangeMapper.insert(saleContractChange);
        responses.add(new CreatedResponse(saleContractChange.getId(), saleContractChange.getCode()));
        // 记录操作日志
        OperateLogUtils.setContent(String.format(SaleDict.SALE_CHANGE_LOGGER_CREATE_FORMAT, code));
        OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContractChange.getCode());
        SaleContractChange updateObj = new SaleContractChange();
        updateObj.setId(saleContractChange.getId());
        // 如果都是普通级直接变更数据
        if (Objects.isNull(submitFlag.get()) || SubmitFlagEnum.ONLY_SAVE.getStatus().equals(submitFlag.get())||BooleanEnum.YES.getValue().equals(saleContract.getAutoFlag())) {
            // 更新变更表为已通过
            updateObj.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            // 更新影响范围状态
            updateEffectRanageStatus(saleContractChange.getId());
            return responses;
        }
        // 进入流程审批
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(submitFlag.get())&&!BooleanEnum.YES.getValue().equals(saleContract.getAutoFlag())) {
            if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
                submitChangeTask(saleContractChange.getId(), WebFrameworkUtils.getLoginUserId(), SaleDict.DOMESTIC_DEFINITION_CHANGE_KEY);
            }else if (SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
                submitChangeTask(saleContractChange.getId(), WebFrameworkUtils.getLoginUserId(), SaleDict.PROCESS_DEFINITION_CHANGE_KEY);
            }else if (SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(saleContract.getSaleType())){
                submitChangeTask(saleContractChange.getId(), WebFrameworkUtils.getLoginUserId(), SaleDict.FACTORY_DEFINITION_CHANGE_KEY);
            }
            // 更改出运单变更状态
            saleContractMapper.updateById(new SaleContractDO().setId(oldSaleContract.getId()).setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus()));
        }
        saleContractChangeMapper.updateById(updateObj);
        return responses;
    }

    private List<SaleContractItem> dealSaleItemChange(SaleContractRespVO saleContract, SaleContractRespVO oldSaleContract, SaleContractChange saleContractChange, Map<String, FormChangeDTO> formChangeDTOList, AtomicReference<Integer> submitFlag, List<JsonEffectRange> effectRangeList) {
        List<SaleContractItem> children = saleContract.getChildren();
        // 销售明细变更
        if (CollUtil.isNotEmpty(children)) {
            children.forEach(s -> s.setContractId(saleContract.getId()));
        }
        List<SaleContractItem> oldChildren = oldSaleContract.getChildren();
        Tuple saleItemTuple = dealChangeField(oldChildren, children, SaleContractItem.class, "sms_sale_contract_item", saleContractChange, formChangeDTOList, submitFlag);
        List<SaleContractItem> saleContractItems = saleItemTuple.get(0);
        Set<String> itemChangeFields = saleItemTuple.get(1);
        List<Long> updateIdList = saleContractItems.stream().filter(s -> ChangeTypeEnum.UPDATE.getType().equals(s.getChangeFlag())).map(SaleContractItem::getId).toList();
//        List<Long> saleItemIdList = saleContractItems.stream().map(SaleContractItem::getId).toList();
        // 如果新增明细则修改合同状态为待采购
        boolean addSubFlag = saleContractItems.stream().anyMatch(s -> ChangeTypeEnum.ADDED.getType().equals(s.getChangeFlag()));
        // 如果是待回签状态下则新增明细不改状态
        boolean waitingForProcurementFlag = SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue().equals(oldSaleContract.getStatus());
        if (addSubFlag && !waitingForProcurementFlag) {
            saleContractChange.setStatus(SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue());
        }
        List<Long> deleteSaleItemIdList = saleContractItems.stream().filter(s -> ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(SaleContractItem::getId).distinct().toList();
        // 校验销售明细是否存在后续单据(其实只需判断是否存在采购明细即可)
        Collection<Long> checkIdList = CollUtil.union(deleteSaleItemIdList,updateIdList);
        Collection<Long> existsIdList = purchaseContractApi.validatePurContractItemExists(checkIdList);
        if (CollUtil.isNotEmpty(deleteSaleItemIdList)) {
            Set<Long> deletedExistsIdList = CollUtil.intersectionDistinct(deleteSaleItemIdList, existsIdList);
            if (CollUtil.isNotEmpty(deletedExistsIdList)){
                throw exception(SALE_CONTRACT_ITEM_EXISTS_PURCHASE);
            }
        }
        List<SaleContractItem> changeItems = saleContractItems.stream().filter(s -> ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).toList();
        if (CollUtil.isNotEmpty(changeItems)) {
            List<Long> rollUpdateIdList = CollUtil.subtractToList(updateIdList, existsIdList);
            // 处理采购计划明细变更
            dealPurchasePlanItem(saleContractChange, changeItems, rollUpdateIdList, effectRangeList, formChangeDTOList, saleContract.getCode(), itemChangeFields);
            // 处理储运计划明细变更
            dealShipmentPlanItem(saleContractChange, changeItems, updateIdList, effectRangeList, formChangeDTOList, saleContract.getCode(), itemChangeFields);
        }
        return saleContractItems;
    }

    private void dealShipmentPlanItem(SaleContractChange saleContractChange, List<SaleContractItem> saleContractItems, List<Long> saleItemIdList, List<JsonEffectRange> effectRangeList, Map<String, FormChangeDTO> formChangeDTOList, String code, Set<String> itemChangeFields) {
        // 获取销售明细对应采购明细状态
        List<Long> effectItemIdList = new ArrayList<>();
        List<Long> cancelItemIdList = new ArrayList<>();
        Map<Long, List<Integer>> shipmentCancelMap = shipmentApi.getShipmentContractItemCancelFlag(saleItemIdList);
        if (CollUtil.isEmpty(shipmentCancelMap)) {
            saleContractChange.setCancelShipmentPlanItemIdList(saleContractItems.stream().map(SaleContractItem::getId).toList());
            return;
        }
        saleContractItems.forEach(s -> {
            Long itemId = s.getId();
            List<Integer> cancelList = shipmentCancelMap.get(itemId);
            if (CollUtil.isEmpty(cancelList)) {
                return;
            }
            // 全部没有作废
            if (!cancelList.contains(BooleanEnum.YES.getValue())) {
                effectItemIdList.add(itemId);
            }
            // 部分作废
            if (cancelList.contains(BooleanEnum.YES.getValue()) && !cancelList.contains(BooleanEnum.NO.getValue())) {
                throw exception(NOT_ALLOW_CHANGE);
            }
            // 全部作废
            if (!cancelList.contains(BooleanEnum.NO.getValue())) {
                cancelItemIdList.add(itemId);
            }
        });
        effectRangeList.addAll(dealEffectRange(formChangeDTOList, code, itemChangeFields, "sms_sale_contract_item", effectItemIdList, List.of(EffectRangeEnum.DMS.getValue())));
        saleContractChange.setCancelShipmentPlanItemIdList(cancelItemIdList);
    }

    private void dealPurchasePlanItem(SaleContractChange saleContractChange, List<SaleContractItem> saleContractItems, List<Long> saleItemIdList, List<JsonEffectRange> effectRangeList, Map<String, FormChangeDTO> formChangeDTOList, String code, Set<String> itemChangeFields) {
        if (CollUtil.isEmpty(saleItemIdList)){
            return;
        }
        List<Long> effectItemIdList = new ArrayList<>();
        List<Long> cancelItemIdList = new ArrayList<>();
        // 获取销售明细对应采购明细状态
        Map<Long, List<Integer>> contractItemCancelMap = purchasePlanApi.getPurchaseContractItemCancelFlag(saleItemIdList);
        Map<Long, List<Integer>> purchaseContractCancelMap = purchaseContractApi.getPurchaseContractItemCancelFlag(saleItemIdList);
        if (CollUtil.isNotEmpty(purchaseContractCancelMap)){
            saleContractItems.stream().filter(s->{
                List<Integer> cancelList = purchaseContractCancelMap.get(s.getId());
                return CollUtil.isNotEmpty(cancelList) && !cancelList.contains(BooleanEnum.YES.getValue());
            }).forEach(s->effectItemIdList.add(s.getId()));
        }
        if (CollUtil.isEmpty(contractItemCancelMap)) {
            saleContractChange.setCancelPurchasePlanItemIdList(saleItemIdList);
            return;
        }
        saleContractItems.forEach(s -> {
            Long itemId = s.getId();
            List<Integer> cancelList = contractItemCancelMap.get(itemId);
            // 未变更则不处理
            if (CollUtil.isEmpty(cancelList)) {
                return;
            }
            // 全部没有作废
            if (!cancelList.contains(BooleanEnum.YES.getValue())) {
                effectItemIdList.add(itemId);
            }
            // 部分作废
            if (cancelList.contains(BooleanEnum.YES.getValue()) && cancelList.contains(BooleanEnum.NO.getValue())) {
                throw exception(NOT_ALLOW_CHANGE);
            }
            // 全部作废
            if (!cancelList.contains(BooleanEnum.NO.getValue())) {
                cancelItemIdList.add(itemId);
            }
        });
        effectRangeList.addAll(dealEffectRange(formChangeDTOList, code, itemChangeFields, "sms_sale_contract_item", effectItemIdList, List.of(EffectRangeEnum.SCM.getValue())));
        saleContractChange.setCancelPurchasePlanItemIdList(cancelItemIdList);
    }

    private List<JsonEffectRange> dealEffectRange(Map<String, FormChangeDTO> formChangeDTOList, String saleContractCode, Set<String> changeFieldName, String tableName, List<Long> itemIdList, List<Integer> effectRangeList) {
        // 获取影响范围
        List<Integer> effectRange = formChangeDTOList.values().stream()
                .filter(s -> tableName.equals(s.getName()))
                .flatMap(s -> s.getChildren().stream())
                .filter(s -> BooleanEnum.YES.getValue().equals(s.getEffectMainInstanceFlag()) && changeFieldName.contains(StrUtils.snakeToCamel(s.getNameEng())))
                .flatMap(s -> {
                    if (CollUtil.isEmpty(s.getEffectRange())) {
                        return Stream.of();
                    }
                    return s.getEffectRange().stream();
                })
                .distinct().toList();
        if (CollUtil.isEmpty(effectRange)) {
            return List.of();
        }
        return effectRange.stream().map(s -> {
                    if (!effectRangeList.contains(s)) {
                        return null;
                    }
                    // 影响采购处理
                    if (EffectRangeEnum.SCM.getValue().equals(s)) {
                        List<String> effectPurchaseCode = purchaseContractApi.getEffectPurchaseCode(saleContractCode, itemIdList);
                        if (CollUtil.isEmpty(effectPurchaseCode)) {
                            return null;
                        }
                        return effectPurchaseCode.stream().map(code -> new JsonEffectRange().setEffectRangeCode(code).setEffectRangeType(s).setConfirmFlag(ConfirmFlagEnum.NO.getValue())).toList();
                        //影响出运处理
                    } else if (EffectRangeEnum.DMS.getValue().equals(s)) {
                        List<String> effectShipmentCode = shipmentApi.getEffectShipmentCode(saleContractCode, itemIdList);
                        if (CollUtil.isEmpty(effectShipmentCode)) {
                            return null;
                        }
                        return effectShipmentCode.stream().map(code -> new JsonEffectRange().setEffectRangeCode(code).setEffectRangeType(s).setConfirmFlag(ConfirmFlagEnum.NO.getValue())).toList();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public String submitChangeTask(Long id, Long userId, String modelKey) {
        UserDept user = adminUserApi.getUserDeptByUserId(userId);
        String deptCode = user.getDeptCode();
        Map<String, Object> variable = new HashMap<>();
        variable.put("deptCode", deptCode);
        String processInstance = bpmProcessInstanceApi.createProcessInstance(userId, modelKey, id, variable, null);
        updateChangeAuditStatus(id, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstance);
        return processInstance;
    }

    @Override
    public String getExportProcessDefinitionKey() {
        return SaleDict.PROCESS_DEFINITION_KEY_EXPORT;
    }

    @Override
    public String getDomesticProcessDefinitionKey() {
        return SaleDict.PROCESS_DEFINITION_KEY_DOMESTIC;
    }

    @Override
    public String getDomesticChangeProcessDefinitionKey() {
        return SaleDict.DOMESTIC_DEFINITION_CHANGE_KEY;
    }

    @Override
    public String getFactoryProcessDefinitionKey() {
        return SaleDict.PROCESS_DEFINITION_KEY_FACTORY;
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        SaleContractChange saleContractChange = SaleContractChange.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            saleContractChange.setProcessInstanceId(processInstanceId);
        }
        saleContractChangeMapper.updateById(saleContractChange);
    }

    @Override
    public SaleContractRespVO getSaleContract(SaleContractDetailReq saleContractDetailReq, SaleTabEnum saleTabEnum) {
        if (saleTabEnum == null) {//默认查详情
            saleTabEnum = SaleTabEnum.SALE_DETAIL;
        }
        SaleContractDO saleContractDO;
        if (Objects.nonNull(saleContractDetailReq.getSaleContractId())) {
            saleContractDO = saleContractMapper.selectById(saleContractDetailReq.getSaleContractId());
        } else if (Objects.nonNull(saleContractDetailReq.getProcessInstanceId())) {
            saleContractDO = saleContractMapper.selectOne(SaleContractDO::getProcessInstanceId, saleContractDetailReq.getProcessInstanceId());
        } else if (Objects.nonNull(saleContractDetailReq.getSaleContractCode())) {
            saleContractDO = saleContractMapper.selectOne(SaleContractDO::getCode, saleContractDetailReq.getSaleContractCode());
        } else {
            logger.warn("[合同详情]获取参数为空req-{}", saleContractDetailReq);
            return null;
        }
        if (saleContractDO == null) {
            return null;
        }
        Long contractId = saleContractDO.getId();
        //获取销售明细
        List<SaleContractItem> saleContractItems = transformChildren(contractId, saleContractDO);
        // 获取加减项
        List<AddSubItem> addSubItems = addSubItemMapper.selectList(AddSubItem::getContractCode, saleContractDO.getCode());
        Map<String, String> configMap = configApi.getConfigMap();
        Map<String,LocalDateTime> dateTimeMap = new HashMap<>();
        dateTimeMap.put(saleContractDO.getCode(),saleContractDO.getSaleContractDate());
        Map<String, Map<String, BigDecimal>> rateMapByDate = rateApi.getDailyRateMapByDate(dateTimeMap);
        Map<Long, List<SimplePurchaseContractItemDTO>> simplePurchaseContractItemMap = purchaseContractApi.getSimplePurchaseContractItemMap(List.of(saleContractDO.getCode()));
        Map<Long, SimpleShipmentItemDTO> simpleShipmentItemDTOMap = shipmentApi.getSimpleShipmentItemDTOMap(List.of(saleContractDO.getCode()));
        SaleContractConvert.INSTANCE.transformPurchaseMsg(saleContractItems,simplePurchaseContractItemMap,rateMapByDate,simpleShipmentItemDTOMap);
        CalcContractPurchaseUtil.CalcContractPurchase(saleContractDO,configMap, rateMapByDate.get(saleContractDO.getCode()),saleContractItems,addSubItems);
        SaleContractRespVO saleContractRespVO = SaleContractConvert.INSTANCE.convertSaleContractRespVO(saleContractDO);
        saleContractRespVO.setChildren(calcStockLockPrice(saleContractItems));
        List<SaleContractItem> groupChildren = transformGroupChildren(saleContractItems, rateMapByDate.get(saleContractDO.getCode()), configMap, saleContractDO);
        if (CollUtil.isEmpty(groupChildren)) {
            saleContractRespVO.setGroupChildren(calcStockLockPrice(saleContractItems));
        } else {
            saleContractRespVO.setGroupChildren(calcStockLockPrice(groupChildren));
        }

        // 获取公司路径数据
        Map<Long, JsonCompanyPath> companyPathMap = new HashMap<>(Map.of(saleContractRespVO.getId(), saleContractRespVO.getCompanyPath()));
        Map<Long, SimpleCompanyDTO> foreignTradeCompanyMap = getForeignTradeCompany(companyPathMap);

        if (CollUtil.isNotEmpty(foreignTradeCompanyMap)) {
            SimpleCompanyDTO simpleCompanyDTO = foreignTradeCompanyMap.get(saleContractRespVO.getId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                saleContractRespVO.setForeignTradeCompanyId(simpleCompanyDTO.getId());
                saleContractRespVO.setForeignTradeCompanyName(simpleCompanyDTO.getName());
            }
        }

        if (SaleTabEnum.SALE_FEE == saleTabEnum) {
            //获取辅料分摊
            saleContractRespVO.setAuxiliaryPurchaseItemList(purchaseContractApi.getAuxiliaryContractItemListBySaleCode(saleContractDO.getCode()));
        } else if (SaleTabEnum.SALE_DETAIL == saleTabEnum) {
            // 获取加减项
            saleContractRespVO.setAddSubItemList(addSubItems);
            // 获取收款计划
            List<CollectionPlan> collectionPlans = collectionPlanMapper.selectList(CollectionPlan::getContractId, contractId);
//            List<Long> planIds = collectionPlans.stream().map(CollectionPlan::getId).distinct().toList();
//            Map<Long,List<CustClaimDTO>> custClaimMap = custClaimApi.getListByPlanIds(planIds);
//            if(CollUtil.isNotEmpty(custClaimMap)){
//                collectionPlans.forEach(s->{
//                    List<CustClaimDTO> custClaimDTOS = custClaimMap.get(s.getId());
//                    List<CollectionPlanItem> items = new ArrayList<>();
//                    if(CollUtil.isNotEmpty(custClaimDTOS)){
//                        custClaimDTOS.forEach(c->{
//                            items.add(new CollectionPlanItem()
//                                    .setSettlementCode(c.getReceiptCode())
//                                    .setDate(c.getReceiveDate())
//                                    .setUser(c.getRegisteredBy())
//                                    .setAmount(new JsonAmount().setAmount(c.getClaimedAmount()).setCurrency(c.getCurrency())));
//                        });     }
//                    s.setChildren(items);
//                });
//            }
            if (CollUtil.isNotEmpty(collectionPlans)) {
//                Map<String, BigDecimal> dailyRateMapByDate = rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate());
//                collectionPlans.forEach(s -> {
//                    calcCollectionPlanAmount(s, saleContractDO.getTotalGoodsValue(),addSubItems,saleContractRespVO.getCommission(),dailyRateMapByDate);
//                });
                collectionPlans.stream().map(CollectionPlan::getReceivedAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(saleContractRespVO.getCurrency()))
                        .ifPresent(saleContractRespVO::setReceivedAmount);
                BigDecimal unReceivedAmount = saleContractDO.getCollectionTotal().getAmount().subtract(saleContractRespVO.getReceivedAmount().getAmount());
                saleContractRespVO.setUnReceivedAmount(new JsonAmount().setAmount(unReceivedAmount).setCurrency(saleContractRespVO.getCurrency()));
            }
            saleContractRespVO.setCollectionPlanList(collectionPlans);
            // 获取操作记录
            List<OperateLogRespDTO> operateLogRespDTOList = operateLogApi.getOperateLogRespDTOList(SaleDict.OPERATOR_EXTS_KEY, saleContractDO.getCode());
            saleContractRespVO.setOperateLogRespDTOList(operateLogRespDTOList);
            List<OrderLinkDTO> orderLingDTO = orderLinkApi.getOrderLinkDTO(saleContractDO.getLinkCodeList());
            saleContractRespVO.setOrderLingDTO(orderLingDTO);
            //创建人名称赋值
            if (saleContractRespVO.getCreator()!=null) {
                Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
                if(!userDeptCache.isEmpty()){
                    UserDept userDept = userDeptCache.get(Long.valueOf(saleContractRespVO.getCreator()));
                    if(userDept!=null){
                        saleContractRespVO.setCreatorName(userDept.getNickname());
                        saleContractRespVO.setCreatorDeptName(userDept.getDeptName());
                    }
                }
            }
        }
        return saleContractRespVO;
    }

    /**
     * 计算销售明细锁库价格
     *
     * @param saleContractItems 销售明细列表
     * @return 计算后的销售明细
     */
    private List<SaleContractItem> calcStockLockPrice(List<SaleContractItem> saleContractItems) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return saleContractItems;
        }
        List<String> batchCodeList = saleContractItems.stream().filter(s->CollUtil.isNotEmpty(s.getStockLockSaveReqVOList())).map(SaleContractItem::getStockLockSaveReqVOList).flatMap(List::stream).map(StockLockSaveReqVO::getBatchCode).distinct().toList();
        if (CollUtil.isEmpty(batchCodeList)) {
            return saleContractItems;
        }
        Map<String, JsonAmount> stockPriceMap = stockApi.getStockPrice(batchCodeList);
        if (CollUtil.isEmpty(stockPriceMap)) {
            return saleContractItems;
        }
        saleContractItems.forEach(s -> {
            List<StockLockSaveReqVO> stockLockSaveReqVOList = s.getStockLockSaveReqVOList();
            if (CollUtil.isEmpty(stockLockSaveReqVOList)) {
                return;
            }
            Optional<Integer> lockQuantityOpt = stockLockSaveReqVOList.stream().map(StockLockSaveReqVO::getLockQuantity).reduce(Integer::sum);
            if (!lockQuantityOpt.isPresent()) {
                return;
            }
            Integer lockQuantity = lockQuantityOpt.get();
            AtomicReference<String> currency = new AtomicReference<>("RMB");
            BigDecimal lockPrice = stockLockSaveReqVOList.stream().map(stockLockSaveReqVO -> {
                String batchCode = stockLockSaveReqVO.getBatchCode();
                JsonAmount stockPrice = stockPriceMap.get(batchCode);
                if (Objects.isNull(stockPrice) || Objects.isNull(stockPrice.getAmount())) {
                    return BigDecimal.ZERO;
                }
                currency.set(stockPrice.getCurrency());
                return NumUtil.mul(stockPrice.getAmount(), stockLockSaveReqVO.getLockQuantity());
            }).reduce(BigDecimal::add).get();
            s.setStockLockTotalPrice(new JsonAmount(lockPrice, currency.get()));
            s.setStockLockPrice(new JsonAmount(NumUtil.div(lockPrice, lockQuantity), currency.get()));
        });
        return saleContractItems;
    }

    private List<SaleContractItem> transformGroupChildren(List<SaleContractItem> saleContractItems, Map<String, BigDecimal> dailyRateMap, Map<String, String> configMap, SaleContractDO saleContractDO) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return List.of();
        }
        Map<String, GroupSaleItemEntity> groupedMap = saleContractItems.stream()
                .collect(Collectors.groupingBy(
                        SaleContractItem::getSourceUniqueCode,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    GroupSaleItemEntity entity = new GroupSaleItemEntity();
                                    entity.setQuantity(list.stream()
                                            .mapToInt(SaleContractItem::getQuantity)
                                            .sum());
                                    entity.setNeedPurQuantity(list.stream()
                                            .filter(s -> Objects.nonNull(s.getNeedPurQuantity()))
                                            .mapToInt(SaleContractItem::getNeedPurQuantity)
                                            .sum());
                                    entity.setShippedQuantity(list.stream()
                                            .filter(s -> Objects.nonNull(s.getShippedQuantity()))
                                            .mapToInt(SaleContractItem::getShippedQuantity)
                                            .sum());
                                    entity.setRealPurchaseQuantity(list.stream()
                                            .filter(s -> Objects.nonNull(s.getRealPurchaseQuantity()))
                                            .mapToInt(SaleContractItem::getRealPurchaseQuantity)
                                            .sum());
                                    entity.setTransferShippedQuantity(list.stream()
                                            .filter(s -> Objects.nonNull(s.getTransferShippedQuantity()))
                                            .mapToInt(SaleContractItem::getTransferShippedQuantity)
                                            .sum());
                                    entity.setShipmentTotalQuantity(list.stream()
                                            .filter(s -> Objects.nonNull(s.getShipmentTotalQuantity()))
                                            .mapToInt(SaleContractItem::getShipmentTotalQuantity)
                                            .sum());
                                    entity.setRealPurchaseWithTaxPrice(calcRealPurchaseWithTaxPrice(list));
                                    return entity;
                                }
                        )
                ));
        List<SaleContractItem> groupChildren = new ArrayList<>();
        saleContractItems.stream().filter(s ->
                groupedMap.containsKey(s.getUniqueCode())
        ).forEach(s -> {
            SaleContractItem saleContractItem = BeanUtil.copyProperties(s, SaleContractItem.class);
            GroupSaleItemEntity groupSaleItemEntity = groupedMap.get(saleContractItem.getUniqueCode());
            if (Objects.isNull(groupSaleItemEntity)) {
                return;
            }
            saleContractItem.setQuantity(groupSaleItemEntity.getQuantity());
            saleContractItem.setNeedPurQuantity(groupSaleItemEntity.getNeedPurQuantity());
            saleContractItem.setShippedQuantity(groupSaleItemEntity.getShippedQuantity());
            if (Objects.nonNull(groupSaleItemEntity.getRealPurchaseQuantity())&&groupSaleItemEntity.getRealPurchaseQuantity()!=0){
                saleContractItem.setRealPurchaseQuantity(groupSaleItemEntity.getRealPurchaseQuantity());
            }

            if (Objects.nonNull(groupSaleItemEntity.getRealPurchaseWithTaxPrice())&&BigDecimal.ZERO.compareTo(groupSaleItemEntity.getRealPurchaseWithTaxPrice().getAmount())!=0){
                saleContractItem.setRealPurchaseWithTaxPrice(groupSaleItemEntity.getRealPurchaseWithTaxPrice());
            }
            saleContractItem.setTransferShippedQuantity(groupSaleItemEntity.getTransferShippedQuantity());
            saleContractItem.setShipmentTotalQuantity(groupSaleItemEntity.getShipmentTotalQuantity());
            if (Objects.nonNull(saleContractDO)) {
                CalcSaleContactUtil.calcSaleItemCost(saleContractItem, saleContractDO, dailyRateMap, configMap);
            }
            groupChildren.add(saleContractItem);
        });
        return groupChildren;
    }

    /**
     * 计算汇总后的真实采购价(算术平均值)
     *
     * @param saleContractItems 销售明细列表
     * @return 平均真实采购价
     */
    private JsonAmount calcRealPurchaseWithTaxPrice(List<SaleContractItem> saleContractItems) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency("RMB");
        }
        String currency = saleContractItems.stream().map(SaleContractItem::getRealPurchaseWithTaxPrice).filter(Objects::nonNull).map(JsonAmount::getCurrency).distinct().findFirst().orElse("RMB");
        BigDecimal totalPrice = saleContractItems.stream().filter(s -> Objects.nonNull(s.getRealPurchaseQuantity()) && Objects.nonNull(s.getRealPurchaseWithTaxPrice()) && Objects.nonNull(s.getRealPurchaseWithTaxPrice().getAmount()))
                .map(s -> NumUtil.mul(s.getRealPurchaseWithTaxPrice().getAmount(), s.getRealPurchaseQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
         Optional<Integer> totalQuantityOpt = saleContractItems.stream().map(SaleContractItem::getRealPurchaseQuantity).filter(Objects::nonNull).reduce(Integer::sum);
        if (!totalQuantityOpt.isPresent() || totalQuantityOpt.get() == 0) {
            return new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(currency);
        }
        return new JsonAmount().setAmount(NumUtil.div(totalPrice, totalQuantityOpt.get())).setCurrency(currency);
    }

    private List<SaleContractItem> transformChildren(Long contractId, SaleContractDO saleContractDO) {
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(SaleContractItem::getContractId, contractId);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        List<Long> skuIdList = saleContractItemList.stream().map(SaleContractItem::getSkuId).distinct().toList();
        Map<String, List<UserDept>> purchaseUserListBySkuIdList = skuApi.getPurchaseUserListBySkuIdList(skuIdList);
        if (CollUtil.isEmpty(purchaseUserListBySkuIdList)) {
            logger.error("[获取销售明细]未获取到明细对应默认报价中供应商采购员skuIdList-{}", skuIdList);
        }
        Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
        // 获取订单路径中收尾节点的公司主键
        List<Long> companyIdList = new ArrayList<>();
        JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
        if (Objects.nonNull(companyPath)) {
            companyIdList.add(TransformUtil.getLastCompanyId(companyPath));
        }

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        Map<Long, Integer> lockMap = stockApi.getLockCountBySaleContractCode(saleContractDO.getCode());
        saleContractItemList.forEach(s -> {
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                Boolean aBoolean = simpleSkuDTOMap.get(s.getSkuId());
                if (Objects.isNull(aBoolean)) {
                    aBoolean = false;
                }
                s.setSkuDeletedFlag(aBoolean ? 0 : 1);
            }
            if (CollUtil.isNotEmpty(lockMap)) {
                s.setRealLockQuantity(lockMap.get(s.getId()));
            }
            String skuCode = s.getSkuCode();
            if (CollUtil.isNotEmpty(purchaseUserListBySkuIdList)) {
                s.setPurchaseUserList(purchaseUserListBySkuIdList.get(skuCode));
            }
            QueryStockReqVO queryStockReqVO = new QueryStockReqVO();
            queryStockReqVO.setCompanyIdList(companyIdList);
            queryStockReqVO.setSkuCode(s.getSkuCode());
            queryStockReqVO.setSaleContractCode(saleContractDO.getCode());
            queryStockReqVO.setSaleContractItemId(s.getId());
            List<StockDetailRespVO> stockDetailRespVOS = stockApi.listLockStock(queryStockReqVO);
            s.setStockDetailRespVOList(stockDetailRespVOS);
            s.setCompanyIdList(companyIdList);
            if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())) {
                List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item -> distinctPackgeType.contains(item.getId())).toList();
                if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                    List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                    s.setPackageTypeName(String.join(",", packageTypeNameList));
                    List<String> packageTypeEngNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                    s.setPackageTypeEngName(String.join(",", packageTypeEngNameList));
                }
            }
            Optional.ofNullable(s.getPurchaseUser()).ifPresent(purchaseUser -> {
                s.setPurchaseUserId(purchaseUser.getUserId());
            });
        });
        return saleContractItemList;
    }

    @Override
    public void updateChangeResult(Long changeId) {//TODO 此处变更回写时是否需考虑子项增删的情况
        SaleContractChange saleContractChange = saleContractChangeMapper.selectById(changeId);
        if (Objects.isNull(saleContractChange)) {
            throw exception(SALE_CONTRACT_CHANGE_NOT_EXISTS);
        }
        List<Long> cancelShipmentPlanItemIdList = saleContractChange.getCancelShipmentPlanItemIdList();
        List<Long> cancelPurchasePlanItemIdList = saleContractChange.getCancelPurchasePlanItemIdList();
        SaleContractSaveReqVO saleContractUpdateReqVO = SaleContractConvert.INSTANCE.convertSaleContractSaveReqVOByChange(saleContractChange);
        saleContractUpdateReqVO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
        SaleContractDO baseDO = saleContractMapper.selectOne(new  LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCode, saleContractUpdateReqVO.getCode()).ne(SaleContractDO::getStatus, SaleContractStatusEnum.CASE_CLOSED.getValue()));
        if (Objects.isNull(baseDO)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        List<SaleContractItem> children = saleContractChange.getChildren();
        children.forEach(s -> {
            Integer quantity = s.getQuantity();
            Integer realLockQuantity = Objects.isNull(s.getRealLockQuantity()) ? CalculationDict.ZERO : s.getRealLockQuantity();
            int purchaseQuantity = quantity - realLockQuantity;
            s.setNeedPurQuantity(purchaseQuantity);
            if (Objects.equals(s.getChangeFlag(), ChangeTypeEnum.ADDED.getType()) && realLockQuantity > 0) {
                s.setReLockFlag(1);
            }
            if (CollUtil.isNotEmpty(cancelPurchasePlanItemIdList)&&cancelPurchasePlanItemIdList.contains(s.getId())){
                s.setConvertPurchaseFlag(BooleanEnum.NO.getValue());
            }
        });
        saleContractUpdateReqVO.setId(baseDO.getId());
        saleContractUpdateReqVO.setChildren(saleContractChange.getChildren());
        saleContractUpdateReqVO.setAddSubItemList(saleContractChange.getAddSubItemList().stream().filter(s-> !Objects.equals(s.getChangeFlag(), ChangeTypeEnum.DELETED.getType())).toList());
        saleContractUpdateReqVO.setCollectionPlanList(saleContractChange.getCollectionPlanList());
        if (CollUtil.isNotEmpty(children)){
            boolean allMatch = children.stream().allMatch(s -> BooleanEnum.NO.getValue().equals(s.getConvertPurchaseFlag()));
            if (allMatch&&saleContractUpdateReqVO.getStatus()>SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue()){
                saleContractUpdateReqVO.setStatus(SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue());
            }
        }
        updateSaleContract(saleContractUpdateReqVO);
        // 作废采购计划明细
        Set<Long> saleItemIds = children.stream().filter(s -> ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(SaleContractItem::getId).collect(Collectors.toSet());
        purchaseContractApi.cancelPurchasePlanItem(cancelPurchasePlanItemIdList,saleItemIds);
        // 作废出运计划明细
        shipmentApi.cancelShipmentPlanItem(cancelShipmentPlanItemIdList);
        // 更新影响范围状态
        if (CollUtil.isNotEmpty(saleContractChange.getEffectRangeList())) {
            saleContractChange.getEffectRangeList().forEach(s -> {
                if (EffectRangeEnum.SCM.getValue().equals(s.getEffectRangeType()) || EffectRangeEnum.AUXILIARY_PURCHASE.getValue().equals(s.getEffectRangeType())) {
                    purchaseContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.DMS.getValue().equals(s.getEffectRangeType())) {
                    shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
            });
        }
    }


    /**
     * 获得销售合同分页（单据模式）
     * 按合同分页，只返回主表必要数据，不查询子明细的采购/出运信息
     * 
     * 优化说明：
     * - 单据视图不展开子明细，无需查询采购信息、出运信息
     * - 减少不必要的数据库查询和 RPC 调用，提升响应速度
     *
     * @param pageReqVO 分页查询条件
     * @param isExport  是否为导出场景
     * @return 单据模式分页结果
     * @author 波波
     */
    @Override
    public PageResult<SaleContractRespVO> getSaleContractPage(SaleContractPageReqVO pageReqVO, boolean isExport) {
        // ===================== 第一阶段：初始化与查询条件处理 =====================
        PageResult<SaleContractRespVO> result = new PageResult<>();

        // 根据明细条件（SKU编码、品名、优势产品等）筛选合同ID
        Set<Long> contractIdsByItem = getContractIdsByItemConditions(pageReqVO);
        if (contractIdsByItem != null) {
            if (contractIdsByItem.isEmpty()) {
                return result;
            }
            pageReqVO.setIdList(contractIdsByItem.toArray(new Long[0]));
        }

        // ===================== 第二阶段：分页查询主表数据 =====================
        PageResult<SaleContractDO> saleContractDOPageResult = saleContractMapper.selectPage(pageReqVO);
        List<SaleContractDO> saleContractDOList = saleContractDOPageResult.getList();
        if (CollUtil.isEmpty(saleContractDOList)) {
            return result;
        }

        // 提取合同ID列表，用于后续批量查询
        List<Long> contractIdList = saleContractDOList.stream()
                .map(SaleContractDO::getId)
                .distinct()
                .collect(Collectors.toList());

        // ===================== 第三阶段：处理导出场景 =====================
        if (isExport && !ExportContext.needChildren(pageReqVO)) {
            // 单据维度导出：不需要明细数据，直接转换主表
            List<SaleContractRespVO> saleContractRespVOList = SaleContractConvert.INSTANCE
                    .convertSaleContractRespVODocumentExport(saleContractDOList);
            return result.setTotal(saleContractDOPageResult.getTotal())
                    .setList(saleContractRespVOList);
        }

        // 产品维度导出场景：需要查询明细数据
        if (isExport) {
            Map<Long, List<SaleContractItem>> contractItemMap = saleContractItemMapper.getSaleContractItemMapByContractIdListOptimized(contractIdList);
            List<SaleContractRespVO> saleContractRespVOList = SaleContractConvert.INSTANCE
                    .convertSaleContractRespVOExport(saleContractDOList, contractItemMap);
            return result.setTotal(saleContractDOPageResult.getTotal())
                    .setList(saleContractRespVOList);
        }

        // ===================== 第四阶段：单据模式列表查询（优化版） =====================
        // 【优化】单据模式只查询必要的关联数据，不查询采购/出运信息
        
        // 查询收款计划（用于计算已收/未收金额）
        Map<Long, List<CollectionPlan>> collectionPlanMap = collectionPlanMapper.getCollectionPlanMapByContractIdList(contractIdList);

        // ===================== 第五阶段：数据转换 =====================
        // 转换为VO对象（简化版，只处理主表数据）
        List<SaleContractRespVO> saleContractRespVOList = SaleContractConvert.INSTANCE
                .convertSaleContractRespVOSimple(saleContractDOList, collectionPlanMap);

        // ===================== 第六阶段：填充额外信息 =====================
        if (CollUtil.isNotEmpty(saleContractRespVOList)) {
            // 获取外贸公司信息（单据视图显示"报关主体"）
            Map<Long, JsonCompanyPath> companyPathMap = saleContractRespVOList.stream()
                    .filter(s -> s.getCompanyPath() != null)
                    .collect(Collectors.toMap(SaleContractRespVO::getId, SaleContractRespVO::getCompanyPath, (v1, v2) -> v1));
            Map<Long, SimpleCompanyDTO> foreignTradeCompanyMap = getForeignTradeCompany(companyPathMap);

            // 获取创建人信息缓存
            Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);

            // 遍历处理每个合同的补充信息
            saleContractRespVOList.forEach(s -> {
                // 填充外贸公司信息
                fillForeignTradeCompanyInfo(s, foreignTradeCompanyMap);
                // 填充创建人信息
                fillCreatorInfoForRespVO(s, userDeptCache);
                // 计算收款金额：已收金额、未收金额
                calcReceivedAndUnReceivedAmount(s);
            });
        }

        // ===================== 第七阶段：构建汇总数据 =====================
        Map<String, Object> summary = getSummary(pageReqVO);

        // ===================== 第八阶段：返回结果 =====================
        result.setSummary(summary);
        return result.setTotal(saleContractDOPageResult.getTotal())
                .setList(saleContractRespVOList);
    }

    /**
     * 获得销售合同产品模式分页（按明细分页，扁平结构）
     * 用于产品视图的扁平化展示，直接查询明细表并关联主表条件
     *
     * @param pageReqVO 分页查询条件
     * @param isExport  是否为导出场景
     * @return 产品模式分页结果
     */
    @Override
    public PageResult<SaleContractProductModeRespVO> getProductModePage(SaleContractPageReqVO pageReqVO, boolean isExport) {
        // ===================== 第一阶段：初始化与查询条件处理 =====================
        PageResult<SaleContractProductModeRespVO> result = new PageResult<>();

        // 处理优势产品条件，若无匹配数据则直接返回
        if (!handleAdvantageFlag(pageReqVO)) {
            return result;
        }

        // ===================== 第二阶段：分页查询明细数据 =====================
        Page<SaleContractProductModeRespVO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<SaleContractProductModeRespVO> pageResult = saleContractItemMapper.selectProductModePage(page, pageReqVO);

        if (pageResult.getRecords() == null || pageResult.getRecords().isEmpty()) {
            return result;
        }

        // ===================== 第三阶段：构建汇总数据 =====================
        // 统一汇总入口（内部根据 queryMode 区分产品模式/单据模式）
        Map<String, Object> summary = getSummary(pageReqVO);

        // ===================== 第四阶段：填充额外信息 =====================
        List<SaleContractProductModeRespVO> records = pageResult.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            // 填充创建人信息
            fillCreatorInfo(records, SaleContractProductModeRespVO::getCreator,
                    SaleContractProductModeRespVO::setCreatorName,
                    SaleContractProductModeRespVO::setCreatorDeptName);
            // 填充外贸公司名称
            fillProductModeForeignTradeCompanyName(records);
            // 计算锁库单价和锁库总价
            calcProductModeStockLockPrice(records);
        }

        // ===================== 第五阶段：返回结果 =====================
        result.setList(records);
        result.setTotal(pageResult.getTotal());
        result.setSummary(summary);

        return result;
    }

    // ==================== 共通方法 ====================

    /**
     * 处理优势产品查询条件
     *
     * @param pageReqVO 分页查询请求
     * @return true-继续查询，false-直接返回空结果
     */
    private boolean handleAdvantageFlag(SaleContractPageReqVO pageReqVO) {
        if (pageReqVO.getAdvantageFlag() != null) {
            List<Long> advantageSkuIds = skuApi.getSkuIdsByAdvantageFlag(pageReqVO.getAdvantageFlag());
            if (CollUtil.isEmpty(advantageSkuIds)) {
                return false;
            }
            pageReqVO.setAdvantageSkuIds(advantageSkuIds);
        }
        return true;
    }

    /**
     * 获取变更数量并添加到汇总
     *
     * @param summary  汇总数据Map
     * @param saleType 销售类型
     */
    private void addChangeCountToSummary(Map<String, Object> summary, Integer saleType) {
        Long changeCount = saleContractMapper.selectCount(
                new LambdaQueryWrapperX<SaleContractDO>()
                        .eq(SaleContractDO::getConfirmFlag, BooleanEnum.NO.getValue())
                        .eq(SaleContractDO::getSaleType, saleType)
                        .eq(SaleContractDO::getAutoFlag, BooleanEnum.NO.getValue())
        );
        summary.put("changeCount", changeCount);
    }

    /**
     * 填充创建人信息
     *
     * @param records            记录列表
     * @param getCreator         获取创建人ID的方法
     * @param setCreatorName     设置创建人名称的方法
     * @param setCreatorDeptName 设置创建人部门名称的方法
     * @param <T>                记录类型
     */
    private <T> void fillCreatorInfo(List<T> records, java.util.function.Function<T, String> getCreator,
                                     java.util.function.BiConsumer<T, String> setCreatorName,
                                     java.util.function.BiConsumer<T, String> setCreatorDeptName) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        if (userDeptCache.isEmpty()) {
            return;
        }
        records.forEach(item -> {
            String creator = getCreator.apply(item);
            if (creator != null) {
                try {
                    UserDept userDept = userDeptCache.get(Long.valueOf(creator));
                    if (userDept != null) {
                        setCreatorName.accept(item, userDept.getNickname());
                        setCreatorDeptName.accept(item, userDept.getDeptName());
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无法解析创建人ID: {}", creator);
                }
            }
        });
    }

    /**
     * 获取汇总数据（统一入口）
     * 根据 queryMode 分支处理：产品模式基于明细表汇总，单据模式基于主表汇总
     *
     * @param pageReqVO 分页查询请求
     * @return 汇总数据Map
     * @author 波波
     */
    private Map<String, Object> getSummary(SaleContractPageReqVO pageReqVO) {
        Map<String, Object> summary = new HashMap<>();
        
        Integer queryMode = pageReqVO.getQueryMode();
        if (queryMode != null && queryMode == 2) {
            // 产品模式：基于明细表汇总
            buildProductModeSummary(pageReqVO, summary);
        } else {
            // 单据模式：基于主表汇总
            buildDocumentModeSummary(pageReqVO, summary);
        }
        
        // 公共汇总：变更数量
        addChangeCountToSummary(summary, pageReqVO.getSaleType());
        
        return summary;
    }

    /**
     * 构建产品模式汇总数据（基于明细表）
     *
     * @param pageReqVO 分页查询请求
     * @param summary   汇总数据Map
     * @author 波波
     */
    private void buildProductModeSummary(SaleContractPageReqVO pageReqVO, Map<String, Object> summary) {
        SaleContractProductModeSummaryDO summaryDO = saleContractItemMapper.selectProductModeSummary(pageReqVO);
        if (summaryDO != null) {
            summary.put("sumQuantity", summaryDO.getSumQuantity());
            summary.put("sumBoxCount", summaryDO.getSumBoxCount());
            summary.put("sumVolume", summaryDO.getSumVolume());
            // 前端 currencyJsonAnalysis 期望数组格式：[{ currency: "USD", amount: xxx }]
            List<Map<String, Object>> totalAmountUsdList = new ArrayList<>();
            Map<String, Object> usdAmount = new HashMap<>();
            usdAmount.put("currency", "USD");
            usdAmount.put("amount", summaryDO.getSumTotalSaleAmountUsd());
            totalAmountUsdList.add(usdAmount);
            summary.put("sumTotalAmountUsd", totalAmountUsdList);
            summary.put("sumRealPurchaseQuantity", summaryDO.getSumRealPurchaseQuantity());
            summary.put("sumRealLockQuantity", summaryDO.getSumRealLockQuantity());
            summary.put("sumShippedQuantity", summaryDO.getSumShippedQuantity());
            summary.put("sumTransferShippedQuantity", summaryDO.getSumTransferShippedQuantity());
        }

        // 原币种汇总（产品模式下可能存在多币种）：按合同币种分组汇总，保持与单据模式一致
        List<SaleContractSummaryDO> totalAmountThisCurrencySummary =
                saleContractItemMapper.selectProductModeTotalAmountThisCurrencySummary(pageReqVO);
        convertSummaryToJsonAmountList(totalAmountThisCurrencySummary, "sumTotalAmountThisCurrency", summary);
    }

    /**
     * 构建单据模式汇总数据（基于主表）
     *
     * @param pageReqVO 分页查询请求
     * @param summary   汇总数据Map
     * @author 波波
     */
    private void buildDocumentModeSummary(SaleContractPageReqVO pageReqVO, Map<String, Object> summary) {
        // 查询金额汇总（按USD计算）
        List<SaleContractSummaryDO> saleContractSummaryDO = saleContractMapper.getSummary(pageReqVO);
        if (CollUtil.isNotEmpty(saleContractSummaryDO)) {
            summary.put("sumTotalAmountUsd", saleContractSummaryDO.stream()
                    .filter(Objects::nonNull)
                    .map(item -> new JsonAmount()
                            .setCurrency(item.getCurrency())
                            .setAmount(item.getSumTotalAmount().setScale(2, RoundingMode.HALF_UP)))
                    .collect(Collectors.toList()));
        }
        
        // 查询原币种金额汇总
        List<SaleContractSummaryDO> saleContractSummaryDOs = saleContractMapper.getTotalAmountThisCurrencySummary(pageReqVO);
        convertSummaryToJsonAmountList(saleContractSummaryDOs, "sumTotalAmountThisCurrency", summary);
        
        // 查询人民币金额汇总
        List<SaleContractSummaryDO> saleContractTotalAmountSummaryDOs = saleContractMapper.getTotalAmountSummary(pageReqVO);
        convertSummaryToJsonAmountList(saleContractTotalAmountSummaryDOs, "sumTotalAmount", summary);
    }

    /**
     * 填充产品模式外贸公司名称
     *
     * @param records 产品模式记录列表
     */
    private void fillProductModeForeignTradeCompanyName(List<SaleContractProductModeRespVO> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        // 直接从 records 中获取 companyPath，无需再查询数据库（分页查询时已经查出）
        Map<Long, JsonCompanyPath> companyPathMap = records.stream()
                .filter(r -> r.getContractId() != null && r.getCompanyPath() != null)
                .collect(Collectors.toMap(
                        SaleContractProductModeRespVO::getContractId,
                        SaleContractProductModeRespVO::getCompanyPath,
                        (v1, v2) -> v1
                ));
        if (CollUtil.isEmpty(companyPathMap)) {
            return;
        }
        Map<Long, SimpleCompanyDTO> foreignTradeCompanyMap = getForeignTradeCompany(companyPathMap);
        if (CollUtil.isNotEmpty(foreignTradeCompanyMap)) {
            records.forEach(item -> {
                SimpleCompanyDTO simpleCompanyDTO = foreignTradeCompanyMap.get(item.getContractId());
                if (simpleCompanyDTO != null) {
                    item.setForeignTradeCompanyId(simpleCompanyDTO.getId());
                    item.setForeignTradeCompanyName(simpleCompanyDTO.getName());
                }
            });
        }
    }

    /**
     * 计算产品模式锁库单价和锁库总价
     * 参考单据模式 calcStockLockPrice 方法实现
     *
     * @param records 产品模式记录列表
     * @author 波波
     */
    private void calcProductModeStockLockPrice(List<SaleContractProductModeRespVO> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        // 收集所有批次号
        List<String> batchCodeList = records.stream()
                .filter(s -> CollUtil.isNotEmpty(s.getLockMsg()))
                .flatMap(s -> s.getLockMsg().stream())
                .map(JsonLock::getBatchCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(batchCodeList)) {
            return;
        }
        // 批量查询库存价格
        Map<String, JsonAmount> stockPriceMap = stockApi.getStockPrice(batchCodeList);
        if (CollUtil.isEmpty(stockPriceMap)) {
            return;
        }
        // 计算每条明细的锁库单价和总价
        records.forEach(s -> {
            List<JsonLock> lockMsgList = s.getLockMsg();
            if (CollUtil.isEmpty(lockMsgList)) {
                return;
            }
            // 计算锁定总数量
            Optional<Integer> lockQuantityOpt = lockMsgList.stream()
                    .map(JsonLock::getLockQuantity)
                    .filter(Objects::nonNull)
                    .reduce(Integer::sum);
            if (lockQuantityOpt.isEmpty() || lockQuantityOpt.get() == 0) {
                return;
            }
            Integer lockQuantity = lockQuantityOpt.get();
            AtomicReference<String> currency = new AtomicReference<>("RMB");
            // 计算锁库总价
            BigDecimal lockPrice = lockMsgList.stream()
                    .map(jsonLock -> {
                        String batchCode = jsonLock.getBatchCode();
                        JsonAmount stockPrice = stockPriceMap.get(batchCode);
                        if (Objects.isNull(stockPrice) || Objects.isNull(stockPrice.getAmount())) {
                            return BigDecimal.ZERO;
                        }
                        currency.set(stockPrice.getCurrency());
                        Integer qty = jsonLock.getLockQuantity();
                        if (qty == null) {
                            qty = 0;
                        }
                        return NumUtil.mul(stockPrice.getAmount(), qty);
                    })
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);
            // 设置锁库总价和单价
            s.setStockLockTotalPrice(new JsonAmount(lockPrice, currency.get()));
            s.setStockLockPrice(new JsonAmount(NumUtil.div(lockPrice, lockQuantity), currency.get()));
        });
    }

    /**
     * 按币种分组计算金额汇总
     *
     * @param list       数据列表
     * @param getAmount  获取金额的方法
     * @param summaryKey 汇总数据的key
     * @param summary    汇总数据Map
     * @param <T>        数据类型
     */
    private <T> void sumAmountByCurrency(List<T> list, java.util.function.Function<T, JsonAmount> getAmount,
                                         String summaryKey, Map<String, Object> summary) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<String, List<T>> grouped = list.stream()
                .filter(item -> {
                    JsonAmount amount = getAmount.apply(item);
                    return Objects.nonNull(amount) 
                            && Objects.nonNull(amount.getAmount()) 
                            && StrUtil.isNotEmpty(amount.getCurrency());
                })
                .collect(Collectors.groupingBy(item -> getAmount.apply(item).getCurrency()));
        if (CollUtil.isNotEmpty(grouped)) {
            List<JsonAmount> amountList = grouped.entrySet().stream().map(entry -> {
                BigDecimal sum = entry.getValue().stream()
                        .map(getAmount)
                        .map(JsonAmount::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP);
                return new JsonAmount().setAmount(sum).setCurrency(entry.getKey());
            }).collect(Collectors.toList());
            summary.put(summaryKey, amountList);
        }
    }

    /**
     * 根据明细条件获取合同ID集合
     * 用于处理 basicSkuCode、cskuCode、skuCode、name、nameEng、advantageFlag 等查询条件
     *
     * @param pageReqVO 分页查询请求
     * @return 合同ID集合，null表示无明细条件，空Set表示无匹配数据
     */
    private Set<Long> getContractIdsByItemConditions(SaleContractPageReqVO pageReqVO) {
        boolean basicSkuCodeFlag = StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode());
        boolean cskuCodeFlag = StrUtil.isNotEmpty(pageReqVO.getCskuCode());
        boolean skuCodeFlag = StrUtil.isNotEmpty(pageReqVO.getSkuCode());
        boolean nameFlag = StrUtil.isNotEmpty(pageReqVO.getName());
        boolean nameEngFlag = StrUtil.isNotEmpty(pageReqVO.getNameEng());
        boolean advantageFlag = pageReqVO.getAdvantageFlag() != null;

        // 如果没有明细查询条件，返回null
        if (!basicSkuCodeFlag && !cskuCodeFlag && !skuCodeFlag && !nameFlag && !nameEngFlag && !advantageFlag) {
            return null;
        }

        LambdaQueryWrapper<SaleContractItem> queryWrapper = new LambdaQueryWrapperX<SaleContractItem>();
        if (basicSkuCodeFlag) {
            queryWrapper.like(SaleContractItem::getBasicSkuCode, pageReqVO.getBasicSkuCode());
        }
        if (cskuCodeFlag) {
            queryWrapper.like(SaleContractItem::getCskuCode, pageReqVO.getCskuCode());
        }
        if (skuCodeFlag) {
            queryWrapper.like(SaleContractItem::getSkuCode, pageReqVO.getSkuCode());
        }
        if (nameFlag) {
            queryWrapper.like(SaleContractItem::getName, pageReqVO.getName());
        }
        if (nameEngFlag) {
            queryWrapper.like(SaleContractItem::getNameEng, pageReqVO.getNameEng());
        }
        // 处理优势产品条件
        if (advantageFlag) {
            List<Long> advantageSkuIds = skuApi.getSkuIdsByAdvantageFlag(pageReqVO.getAdvantageFlag());
            if (CollUtil.isEmpty(advantageSkuIds)) {
                return Collections.emptySet();
            }
            queryWrapper.in(SaleContractItem::getSkuId, advantageSkuIds);
        }

        // 优化：只查询 contract_id 字段
        queryWrapper.select(SaleContractItem::getContractId);
        List<Object> contractIdObjects = saleContractItemMapper.selectObjs(queryWrapper);
        if (CollUtil.isEmpty(contractIdObjects)) {
            return Collections.emptySet();
        }
        return contractIdObjects.stream()
                .map(obj -> ((Number) obj).longValue())
                .collect(Collectors.toSet());
    }

    /**
     * 将 SaleContractSummaryDO 列表转换为 JsonAmount 列表并添加到汇总
     *
     * @param summaryDOList 汇总数据列表
     * @param summaryKey    汇总数据的key
     * @param summary       汇总数据Map
     */
    private void convertSummaryToJsonAmountList(List<SaleContractSummaryDO> summaryDOList, 
                                                 String summaryKey, Map<String, Object> summary) {
        if (CollUtil.isNotEmpty(summaryDOList)) {
            summary.put(summaryKey, summaryDOList.stream()
                    .filter(Objects::nonNull)
                    .map(item -> new JsonAmount()
                            .setCurrency(item.getCurrency())
                            .setAmount(item.getSumTotalAmount().setScale(2, RoundingMode.HALF_UP)))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * 处理销售明细分组和锁库价格
     *
     * @param respVO          销售合同响应VO
     * @param saleContractDOMap 合同ID->DO映射
     * @param contractDateMap   合同编号->汇率映射
     * @param configMap         系统配置映射
     */
    private void processChildrenGroupAndStockLockPrice(SaleContractRespVO respVO,
                                                        Map<Long, SaleContractDO> saleContractDOMap,
                                                        Map<String, Map<String, BigDecimal>> contractDateMap,
                                                        Map<String, String> configMap) {
        List<SaleContractItem> children = respVO.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            SaleContractDO saleContractDO = saleContractDOMap.get(respVO.getId());
            List<SaleContractItem> saleContractItems = transformGroupChildren(
                    children, contractDateMap.get(saleContractDO.getCode()), configMap, saleContractDO);
            respVO.setChildren(calcStockLockPrice(saleContractItems));
        }
    }

    /**
     * 填充外贸公司信息
     *
     * @param respVO               销售合同响应VO
     * @param foreignTradeCompanyMap 外贸公司映射
     */
    private void fillForeignTradeCompanyInfo(SaleContractRespVO respVO,
                                              Map<Long, SimpleCompanyDTO> foreignTradeCompanyMap) {
        if (CollUtil.isNotEmpty(foreignTradeCompanyMap)) {
            SimpleCompanyDTO simpleCompanyDTO = foreignTradeCompanyMap.get(respVO.getId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                respVO.setForeignTradeCompanyId(simpleCompanyDTO.getId());
                respVO.setForeignTradeCompanyName(simpleCompanyDTO.getName());
            }
        }
    }

    /**
     * 填充创建人信息（针对SaleContractRespVO）
     *
     * @param respVO        销售合同响应VO
     * @param userDeptCache 用户部门缓存
     */
    private void fillCreatorInfoForRespVO(SaleContractRespVO respVO, Map<Long, UserDept> userDeptCache) {
        if (respVO.getCreator() != null && !userDeptCache.isEmpty()) {
            UserDept userDept = userDeptCache.get(Long.valueOf(respVO.getCreator()));
            if (userDept != null) {
                respVO.setCreatorName(userDept.getNickname());
                respVO.setCreatorDeptName(userDept.getDeptName());
            }
        }
    }

    /**
     * 计算收款金额（已收金额、未收金额）
     *
     * @param respVO 销售合同响应VO
     */
    private void calcReceivedAndUnReceivedAmount(SaleContractRespVO respVO) {
        List<CollectionPlan> collectionPlanList = respVO.getCollectionPlanList();
        if (CollUtil.isNotEmpty(collectionPlanList)) {
            // 计算已收金额
            collectionPlanList.stream()
                    .map(CollectionPlan::getReceivedAmount)
                    .filter(Objects::nonNull)
                    .map(JsonAmount::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .map(amount -> new JsonAmount().setAmount(amount).setCurrency(respVO.getCurrency()))
                    .ifPresent(respVO::setReceivedAmount);
            // 计算未收金额
            BigDecimal receivedAmount = Objects.isNull(respVO.getReceivedAmount()) ? BigDecimal.ZERO 
                    : Objects.isNull(respVO.getReceivedAmount().getAmount()) ? BigDecimal.ZERO 
                    : respVO.getReceivedAmount().getAmount();
            BigDecimal unReceivedAmount = respVO.getCollectionTotal().getAmount().subtract(receivedAmount);
            respVO.setUnReceivedAmount(new JsonAmount().setAmount(unReceivedAmount).setCurrency(respVO.getCurrency()));
        }
    }

    private Map<Long, SimpleCompanyDTO> getForeignTradeCompany(Map<Long, JsonCompanyPath> companyPathMap) {
        Map<Long, SimpleCompanyDTO> map = new HashMap<>();
        if (companyPathMap == null) {
            return map;
        }
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        for (Map.Entry<Long, JsonCompanyPath> entry : companyPathMap.entrySet()) {
            Long companyId = entry.getKey();
            JsonCompanyPath companyPath = entry.getValue();
            SimpleCompanyDTO companyDTO = getForeignTradeCompany(companyPath, simpleCompanyMap);
            if (companyDTO != null) {
                map.put(companyId, companyDTO);
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

    @Override
    public PageResult<SaleContractChange> getChangeSaleContractPage(SaleContractChangePageReq pageReqVO) {
        PageResult<SaleContractChange> saleContractChangePageResult = saleContractChangeMapper.selectPage(pageReqVO);
        List<SaleContractChange> list = saleContractChangePageResult.getList();
        if (CollUtil.isEmpty(list)){
            return saleContractChangePageResult;
        }
        list.forEach(saleContractChange->{
            List<CollectionPlan> collectionPlans = saleContractChange.getCollectionPlanList();
            List<AddSubItem> addSubItems = saleContractChange.getAddSubItemList();
            if (CollUtil.isNotEmpty(collectionPlans)) {
//                Map<String, BigDecimal> dailyRateMapByDate = rateApi.getDailyRateMapByDate(saleContractChange.getSaleContractDate());
//                collectionPlans.forEach(s -> {
//                    calcCollectionPlanAmount(s, saleContractChange.getTotalGoodsValue(),addSubItems,saleContractChange.getCommission(),dailyRateMapByDate);
//                });
                // 计算应收金额
                collectionPlans.stream().map(CollectionPlan::getReceivableAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(saleContractChange.getCurrency()))
                        .ifPresent(saleContractChange::setReceivableAmount);
                // 计算实收金额
                collectionPlans.stream().map(CollectionPlan::getReceivedAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(saleContractChange.getCurrency()))
                        .ifPresent(saleContractChange::setReceivedAmount);
                BigDecimal unReceivedAmount = saleContractChange.getCollectionTotal().getAmount().subtract(saleContractChange.getReceivedAmount().getAmount());
                saleContractChange.setUnReceivedAmount(new JsonAmount().setAmount(unReceivedAmount).setCurrency(saleContractChange.getCurrency()));
            }
        });
        saleContractChangePageResult.setList(list);
        return saleContractChangePageResult;
    }

    @Override
    public SaleContractChange getSaleContractChangeDetail(SaleContractDetailReq req) {
        Long saleContractId = req.getSaleContractId();
        SaleContractChange saleContractChange;
        if (Objects.nonNull(saleContractId)) {
            saleContractChange = saleContractChangeMapper.selectById(req.getSaleContractId());
        } else {
            saleContractChange = saleContractChangeMapper.selectOne(SaleContractChange::getProcessInstanceId, req.getProcessInstanceId());
        }

        if (Objects.isNull(saleContractChange)) {
            return null;
        }
        saleContractChange.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(SaleDict.OPERATOR_EXTS_KEY, saleContractChange.getCode()));
        List<CollectionPlan> collectionPlans = saleContractChange.getCollectionPlanList();
        List<AddSubItem> addSubItems = saleContractChange.getAddSubItemList();
        if (CollUtil.isNotEmpty(collectionPlans)) {
//            Map<String, BigDecimal> dailyRateMapByDate = rateApi.getDailyRateMapByDate(saleContractChange.getSaleContractDate());
//            collectionPlans.forEach(s -> {
//                calcCollectionPlanAmount(s, saleContractChange.getTotalGoodsValue(),addSubItems,saleContractChange.getCommission(),dailyRateMapByDate);
//            });
            // 计算应收金额
            collectionPlans.stream().map(CollectionPlan::getReceivableAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(saleContractChange.getCurrency()))
                    .ifPresent(saleContractChange::setReceivableAmount);
            // 计算实收金额
            collectionPlans.stream().map(CollectionPlan::getReceivedAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(saleContractChange.getCurrency()))
                    .ifPresent(saleContractChange::setReceivedAmount);
            BigDecimal unReceivedAmount = saleContractChange.getCollectionTotal().getAmount().subtract(saleContractChange.getReceivedAmount().getAmount());
            saleContractChange.setUnReceivedAmount(new JsonAmount().setAmount(unReceivedAmount).setCurrency(saleContractChange.getCurrency()));
        }
        return saleContractChange;
    }

//    @Override
//    public void updateChangeSaleContract(SaleContractChange updateInfoReqVO) {
//        SaleContractChange baseItem = saleContractChangeMapper.selectById(updateInfoReqVO.getId());
//        if (Objects.isNull(baseItem)) {
//            throw exception(SALE_CONTRACT_CHANGE_NOT_EXISTS);
//        }
//        List<ChangeRecord> changeRecords = new OperateCompareUtil<SaleContractChange>().compare(baseItem, updateInfoReqVO);
//        List<SaleContractItemChange> old_children = baseItem.getChildren();
//        List<SaleContractItemChange> children = updateInfoReqVO.getChildren();
//        List<ChangeRecord> child_changeRecords = DiffUtil.compareListsToChangeRecord(old_children, children, SaleD
//        ict.SALE_ITEM_NAME, SaleDict.SALE_ITEM_LOGGER_FLAG);
//        if (CollUtil.isNotEmpty(child_changeRecords)) {
//            changeRecords.addAll(child_changeRecords);
//        }
//        // 插入操作日志
//        OperateLogUtils.addOperateLog(changeRecords, SaleDict.CHANGE_OPERATOR_EXTS_KEY, updateInfoReqVO.getCode());
//        saleContractChangeMapper.updateById(updateInfoReqVO);
//    }

    @Override
    public void deleteChangeSaleContract(Long id) {
        validateChangeSaleContractExists(id);
        saleContractChangeMapper.deleteById(id);
    }


    @Override
    public PageResult<SaleContractSimpleRespVO> getSaleContractSimplePage(SaleContractPageReqVO pageReqVO) {
        if (pageReqVO.getConfirmFlag() == null && pageReqVO.getIdList() == null) {
            pageReqVO.setConfirmFlag(1);
        }
        PageResult<SaleContractDO> saleContractDOPageResult = saleContractMapper.selectPage(
                pageReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())   //审核通过
        );
        List<SaleContractDO> saleContractDOList = saleContractDOPageResult.getList();
        if (CollUtil.isEmpty(saleContractDOList)) {
            return new PageResult<SaleContractSimpleRespVO>().setList(null).setTotal((long) 0);
        }
        List<Long> contractIdList = saleContractDOList.stream().map(SaleContractDO::getId).distinct().toList();
        List<String> contractCodeList = saleContractDOList.stream().map(SaleContractDO::getCode).distinct().toList();
        Map<Long, List<SaleContractItem>> contractItemMap = saleContractItemMapper.getSaleContractItemMapByContractIdList(contractIdList);
        Map<String, List<AddSubItem>> addsubItemMap = addSubItemMapper.getAddSubItemMapByContractCodeList(contractCodeList);
        Map<Long, List<CollectionPlan>> collectionPlanMap = collectionPlanMapper.getCollectionPlanMapByContractIdList(contractIdList);
        List<SaleContractSimpleRespVO> saleContractRespVOList = SaleContractConvert.INSTANCE.convertSaleContractSimpleRespVO(saleContractDOList, contractItemMap, addsubItemMap, collectionPlanMap);
        return new PageResult<SaleContractSimpleRespVO>().setTotal(saleContractDOPageResult.getTotal()).setList(saleContractRespVOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> toContract(Long saleContractId) {
        List<CreatedResponse> responses = new ArrayList<>();
        //查询销售合同数据
        SaleContractDO saleContractDO = saleContractMapper.selectById(saleContractId);
        Integer status = saleContractDO.getStatus();
        if (!SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue().equals(status)){
            throw exception(CONTRACT_CONVERTED_PURCHASE_PLAN);
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().eq(SaleContractItem::getContractId, saleContractId).eq(SaleContractItem::getConvertPurchaseFlag, BooleanEnum.NO.getValue()));
        //外销合同下推采购计划不控制，计划转合同时根据数量具体判断金额是否满足默认差异，控制是否允许下推合同
        Optional<BigDecimal> sum = saleContractItems.stream().map(s -> NumUtil.mul(s.getQuantity(), s.getUnitPrice().getAmount())).reduce(BigDecimal::add);
        if (sum.isPresent()){
            checkCollectionPlan(saleContractDO.getCode(),true,sum.get());
        }
        //合同转采购计划
        PurchasePlanInfoSaveReqDTO saveReqDTO = new PurchasePlanInfoSaveReqDTO();
        {
            saveReqDTO.setCustId(saleContractDO.getCustId()).setCustCode(saleContractDO.getCustCode()).setCustName(saleContractDO.getCustName());
            saveReqDTO.setSourceType(PurchasePlanSourceTypeEnum.SALE_CONTRACT.getCode());
            saveReqDTO.setCode(saleContractDO.getCode());
            saveReqDTO.setSaleType(saleContractDO.getSaleType());
            saveReqDTO.setSales(saleContractDO.getSales());
            saveReqDTO.setPurchaseUserList(getPurchaseUserIds(saleContractItems));
            saveReqDTO.setLinkCodeList(saleContractDO.getLinkCodeList());
            saveReqDTO.setSales(saleContractDO.getSales());
            //采购主体默认订单路径最后一个主体
            Long companyId = -1L;
            String companyName = "";
            JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
            if (Objects.nonNull(companyPath)) {
                JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(companyPath);
                companyId = lastCompanyPath.getId();
                companyName = lastCompanyPath.getName();
            }
            saveReqDTO.setCompanyId(companyId);
            saveReqDTO.setCompanyName(companyName);
            saveReqDTO.setCompanyPath(saleContractDO.getCompanyPath());
            saveReqDTO.setSaleContractId(saleContractDO.getId());
            saveReqDTO.setSaleContractCode(saleContractDO.getCode());
            // 审核状态为已审核
            saveReqDTO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            //状态设置为待采购
            saveReqDTO.setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode());

            //2024.12.4 预计交期,销售合同交货日期减去七天赋值
            //2025.3.17 增加配置，如果配置读取不到，默认7天
            //2025.11.17 采购计划主表交期取值为销售合同明细子表最大值
            Optional<LocalDateTime> maxDeliveryDate = saleContractItems.stream().map(SaleContractItem::getVenderDeliveryDate).filter(Objects::nonNull).max(LocalDateTime::compareTo);
            saveReqDTO.setEstDeliveryDate(maxDeliveryDate.orElse(null));
//            String planDay = configApi.getValueByConfigKey("purchase.plan.day");
//            int planDayInt = 7;
//            try {
//                planDayInt = parseInt(planDay);
//            } catch (NumberFormatException ignored) {
//            }
//            saveReqDTO.setEstDeliveryDate(saleContractDO.getCustDeliveryDate().minusDays(planDayInt));
        }
        List<PurchasePlanItemSaveReqDTO> children = new ArrayList<>();

        List<Long> skuIdList = saleContractItems.stream().map(SaleContractItem::getSkuId).distinct().toList();
        Map<String, List<QuoteitemDTO>> quoteItemDTOMapBySkuIdList = quoteitemApi.getQuoteItemDTOMapBySkuIdList(skuIdList);
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
        List<String> skuCodeList = saleContractItems.stream().map(SaleContractItem::getSkuCode).distinct().toList();
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
        saleContractItems.forEach(s -> {
            Integer needPurQuantity = s.getNeedPurQuantity();
            if ((!Objects.equals(s.getQuantity(), s.getCurrentLockQuantity()))&&(Objects.isNull(needPurQuantity) || needPurQuantity <= 0)) {
                return;
            }
            PurchasePlanItemSaveReqDTO item = new PurchasePlanItemSaveReqDTO();
            //报价信息
            if (CollUtil.isNotEmpty(quoteItemDTOMapBySkuIdList)) {

                List<QuoteitemDTO> quoteItemDTOList = quoteItemDTOMapBySkuIdList.get(s.getSkuCode());
                if (Objects.nonNull(quoteItemDTOList)) {
                    Optional<QuoteitemDTO> first = quoteItemDTOList.stream().filter(q -> q.getDefaultFlag() == 1).findFirst();
                    if (first.isPresent()) {
                        QuoteitemDTO quoteitemDTO = first.get();
                        item = BeanUtils.toBean(quoteitemDTO, PurchasePlanItemSaveReqDTO.class);
                        item.setVenderCode(quoteitemDTO.getVenderCode()).setVenderId(quoteitemDTO.getVenderId());
                        // 采购单价取默认供应商报价
                        item.setUnitPrice(quoteitemDTO.getUnitPrice());
                    }
                }
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)&&CollUtil.isNotEmpty(hsMap)) {
                    if (Objects.isNull(item.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                        HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                        if (Objects.nonNull(hsDataDTO)){
                            item.setSkuUnit(hsDataDTO.getUnit());
                        }
                    }
                }
            }
            item.setVenderId(s.getVenderId());
            item.setVenderCode(s.getVenderCode());
            item.setVenderDeliveryDate(s.getVenderDeliveryDate());
            item.setOskuCode(s.getOskuCode());
            item.setCustPo(saleContractDO.getCustPo());
            item.setSpecificationList(s.getSpecificationList());
            item.setThumbnail(s.getThumbnail());
            item.setSaleContractItemId(s.getId());
            item.setSortNum(s.getSortNum());
            item.setSkuId(s.getSkuId()).setSkuCode(s.getSkuCode()).setCskuCode(s.getCskuCode()).setBasicSkuCode(s.getBasicSkuCode()).setBarcode(s.getBarcode());
            item.setCustId(saleContractDO.getCustId()).setCustCode(saleContractDO.getCustCode());
            item.setPurchaseType(PurchaseTypeEnum.STANDARD.getCode());
            item.setLevels(1);
            // 销售合同信息
            item.setSaleContractId(saleContractDO.getId()).setSaleContractCode(saleContractDO.getCode());
            item.setFreeFlag(0);
            item.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode());
            item.setSkuType(s.getSkuType());
            item.setSkuId(s.getSkuId()).setSkuName(s.getName()).setMainPicture(s.getMainPicture());

            UserDept userDept = s.getPurchaseUser();
            if (ObjectUtil.isNotNull(userDept)) {
                item.setPurchaseUserId(userDept.getUserId()).setPurchaseUserName(userDept.getNickname())
                        .setPurchaseUserDeptId(userDept.getDeptId()).setPurchaseUserDeptName(userDept.getDeptName());
            }
            item.setSaleQuantity(s.getQuantity());
            item.setCurrentLockQuantity(s.getCurrentLockQuantity());
            item.setNeedPurQuantity(s.getNeedPurQuantity());
            item.setBoxCount(s.getBoxCount());
            item.setQtyPerInnerbox(s.getQtyPerInnerbox());
            item.setQtyPerOuterbox(s.getQtyPerOuterbox());
            item.setSaleLockQuantity(s.getCurrentLockQuantity());
            item.setLockQuantity(s.getCurrentLockQuantity());

            //辅料相关
            item.setAuxiliaryPurchaseContractCode(null);
            item.setSpecRemark(null).setAnnex(null);
            item.setAuxiliaryCskuCode(null).setAuxiliarySkuCode(null).setAuxiliarySkuId(null);
            item.setAuxiliarySkuFlag(null).setAuxiliarySkuType(null);
            item.setPurchaseCompanyId(saveReqDTO.getCompanyId());
            item.setPurchaseCompanyName(saveReqDTO.getCompanyName());
            item.setSourceUniqueCode(s.getUniqueCode());
            item.setSaleItemUniqueCode(s.getUniqueCode());
            item.setCustProFlag(s.getCustProFlag());
            item.setOwnBrandFlag(s.getOwnBrandFlag());
            children.add(item);
        });
        saveReqDTO.setChildren(children);

        responses = purchasePlanApi.createPurchasePlan(saveReqDTO);
        // 记录操作日志
        OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContractDO.getCode());
        OperateLogUtils.setContent("【下推采购计划】");
        //改写合同标记和时间
        saleContractDO.setConvertPurchaseFlag(1);
        saleContractDO.setConvertPurchaseTime(LocalDateTime.now());
        saleContractDO.setStatus(SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue());
        saleContractMapper.updateById(saleContractDO);
        orderLinkApi.updateOrderLinkStatus(saleContractDO.getCode(), BusinessNameDict.EXPORT_SALE_CONTRACT_NAME, saleContractDO.getLinkCodeList(), saleContractDO.getStatus());
        return responses;
    }

    private List<UserDept> getPurchaseUserIds(List<SaleContractItem> saleContractItems) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return List.of();
        }
        return saleContractItems.stream().map(SaleContractItem::getPurchaseUser).filter(Objects::nonNull).distinct().toList();
    }

    @Override
    public List<HistoryTradePriceResp> getHistoryTradePriceBySkuCode(HistoryTradePriceReq req) {
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select());
        if (CollUtil.isEmpty(saleContractDOS)) {
            return List.of();
        }
        Map<Long, SaleContractDO> saleContractDOMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getId, s -> s));
        List<Long> contractIdList = saleContractDOS.stream().map(SaleContractDO::getId).toList();
        MPJLambdaWrapperX<SaleContractItem> queryWrapper = new MPJLambdaWrapperX<SaleContractItem>();
        queryWrapper.selectAll(SaleContractItem.class);
        queryWrapper.leftJoin(SaleContractDO.class, SaleContractDO::getId, SaleContractItem::getContractId);
        queryWrapper.eq(SaleContractItem::getSkuCode, req.getSkuCode());
        queryWrapper.eq(SaleContractItem::getContractId, contractIdList);
        queryWrapper.eq(SaleContractDO::getAutoFlag, BooleanEnum.NO.getValue());
        if (Objects.nonNull(req.getSaleTime()) && req.getSaleTime().length > 1) {
            queryWrapper.betweenIfPresent(SaleContractItem::getCreateTime, req.getSaleTime());
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        List<HistoryTradePriceResp> result = new ArrayList<>();
        saleContractItemList.forEach(s -> {
            HistoryTradePriceResp historyTradePriceResp = new HistoryTradePriceResp();
            historyTradePriceResp.setTradePrice(s.getUnitPrice());
            historyTradePriceResp.setCskuCode(s.getCskuCode());
            historyTradePriceResp.setCustName(s.getCskuCode());
            SaleContractDO saleContractDO = saleContractDOMap.get(s.getContractId());
            if (Objects.nonNull(saleContractDO)) {
                historyTradePriceResp.setCustName(saleContractDO.getCustName());
                historyTradePriceResp.setCustCountryName(saleContractDO.getCustCountryName());
            }
            historyTradePriceResp.setQuantity(s.getQuantity());
            historyTradePriceResp.setSplitBoxFlag(s.getSplitBoxFlag());
            historyTradePriceResp.setSpecificationList(s.getSpecificationList());
            historyTradePriceResp.setSkuName(s.getName());
            historyTradePriceResp.setPurchasePrice(s.getRealPurchaseWithTaxPrice());
            historyTradePriceResp.setSaleTime(s.getCreateTime());
            historyTradePriceResp.setPurchaseQuantity(s.getNeedPurQuantity());
            historyTradePriceResp.setSaleContractCode(s.getContractCode());
            result.add(historyTradePriceResp);
        });
        return result;
    }

    @Override
    public PushOutShipmentPlanResp getPushOutShipmentPlanResp(PushOutShipmentPlanReq req) {
        List<Long> childrenIdList = req.getChildrenIdList();
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getId, childrenIdList));
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        List<SaleContractItem> uniqueEmptyList = saleContractItemList.stream().filter(it -> StrUtil.isEmpty(it.getSourceUniqueCode())).toList();
        if (!CollectionUtils.isEmpty(uniqueEmptyList)) {
            throw exception(ERROR_UNIQE_EXITS);
        }
        List<String> uniqueList = saleContractItemList.stream().map(SaleContractItem::getSourceUniqueCode).distinct().toList();
        saleContractItemList = saleContractItemMapper.selectList(SaleContractItem::getSourceUniqueCode, uniqueList);
        saleContractItemList = saleContractItemList.stream().filter(x -> {
            int transferShippedQuantity = Objects.isNull(x.getTransferShippedQuantity()) ? 0 : x.getTransferShippedQuantity();
            return transferShippedQuantity < x.getQuantity();
        }).toList();
        Set<Long> contractIdList = saleContractItemList.stream().map(SaleContractItem::getContractId).collect(Collectors.toSet());
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getId, contractIdList));
        boolean convertedShipmentFlag = saleContractDOList.stream().anyMatch(s -> !SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue().equals(s.getStatus()));
        if (convertedShipmentFlag){
            throw exception(CONTRACT_CONVERTED_SHIPMENT_PLAN);
        }
        List<String> saleContractCodeList = saleContractItemList.stream().map(SaleContractItem::getContractCode).distinct().toList();
        List<Long> saleItemIdList = saleContractItemList.stream().map(SaleContractItem::getId).distinct().toList();
        // 获取所有出货客户信息
        List<SimpleCustSaveResp> custList = saleContractDOList.stream().map(s -> new SimpleCustSaveResp().setId(s.getCustId()).setCode(s.getCustCode()).setName(s.getCustName())).distinct().toList();
        //唛头赋值
        if (CollUtil.isEmpty(custList)) {
            throw exception(CUST_LIST_NOT_EXITS);
        }
        List<String> custCodeList = custList.stream().map(SimpleCustSaveResp::getCode).distinct().toList();
        Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(custCodeList);
        if (CollUtil.isNotEmpty(custMap)) {
            custList.forEach(s -> {
                CustAllDTO custAllDTO = custMap.get(s.getCode());
                if (Objects.nonNull(custAllDTO)) {
                    s.setFrontShippingMark(custAllDTO.getMainMark()).setSideShippingMark(custAllDTO.getSideMark());
                }
            });
        }
        // 如果存在不同账套的销售合同 不可下推出运计划
        Long companyId = req.getCompanyId();
        //20250427 by zhangcm 校验外贸公司一致
        Map<Long, JsonCompanyPath> companyPathMap = saleContractDOList.stream().collect(Collectors.toMap(SaleContractDO::getId, SaleContractDO::getCompanyPath));
        Map<Long, SimpleCompanyDTO> foreignTradeCompany = getForeignTradeCompany(companyPathMap);
        if (CollUtil.isNotEmpty(foreignTradeCompany)) {
            Optional<SimpleCompanyDTO> any = foreignTradeCompany.values().stream().filter(s -> s.getId() != companyId).findAny();
            if (any.isPresent()) {
                throw exception(ONLY_ALLOW_PUSH_SAME_FOREIGN_COMPANY);
            }
        }

//        long invalidCount = saleContractDOList.stream().map(SaleContractDO::getCompanyId).filter(s -> !s.equals(companyId)).count();
//        if (invalidCount > 0) {
//            throw exception(ONLY_ALLOW_PUSH_SAME_COMPANY);
//        }
        // 校验下推出运计划的外销合同出运口岸、目的口岸、出运方式
//        boolean departurePortFlag = saleContractDOList.stream().map(SaleContractDO::getDeparturePortId).distinct().limit(2).toList().size() > 1;
//        if (departurePortFlag) {
//            throw exception(DEPARTURE_PORT_NOT_SAME);
//        }
//        boolean destinationPortFlag = saleContractDOList.stream().map(SaleContractDO::getDestinationPortId).distinct().limit(2).toList().size() > 1;
//        if (destinationPortFlag) {
//            throw exception(DESTINATION_PORT_NOT_SAME);
//        }
//        boolean transportTypeFlag = saleContractDOList.stream().map(SaleContractDO::getTransportType).distinct().limit(2).toList().size() > 1;
//        if (transportTypeFlag) {
//            throw exception(TRANSPORT_TYPE_NOT_SAME);
//        }
//        boolean settlementTermTypeFlag = saleContractDOList.stream().map(SaleContractDO::getSettlementTermType).distinct().count() > 1;
//        if (settlementTermTypeFlag) {
//            throw exception(SETTLEMENT_TERM_TYPE_NOT_SAME);
//        }


        Map<String, SaleContractDO> saleContractDOMap = saleContractDOList.stream().collect(Collectors.toMap(SaleContractDO::getCode, s -> s));
        PushOutShipmentPlanResp result = new PushOutShipmentPlanResp().setForeignTradeCompanyId(companyId).setForeignTradeCompanyName(req.getCompanyName());
        result.setCustDeliveryDate(saleContractDOList.get(0).getCustDeliveryDate());
        List<PushOytShipmentPlanItem> children = new ArrayList<>();
        if (CollUtil.isNotEmpty(custMap)){
            String notifyPerson = custMap.values().stream().map(CustAllDTO::getNotifyPerson).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
            String receivePerson = custMap.values().stream().map(CustAllDTO::getReceivePerson).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
            result.setNotifyPerson(notifyPerson);
            result.setReceivePerson(receivePerson);
        }
        Map<Long, Long> lastCompanyPathMap = getLastCompanyPathByItemList(saleContractItemList);
        Map<Long, PurchaseContractItemDTO> purchaseContractMap = purchaseContractApi.getPurchaseContractMap(saleItemIdList, lastCompanyPathMap);
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        //查询库存信息
        Map<Long, List<StockDTO>> stockDTOMap = stockApi.getStockDTOMapBySaleContractCodes(saleContractCodeList);
        Map<Long, List<StockDTO>> finalStockDTOMap = stockDTOMap;
        saleContractItemList.forEach(s -> {
            String contractCode = s.getContractCode();
            SaleContractDO saleContractDO = saleContractDOMap.get(contractCode);
            if (Objects.isNull(saleContractDO)) {
                throw exception(SALE_CONTRACT_NOT_EXISTS);
            }
            children.add(dealPushOytShipmentPlanItem(s, saleContractDO, purchaseContractMap, packageTypeList, finalStockDTOMap));
        });
        List<AddSubItem> addSubItemList = addSubItemMapper.selectList(new LambdaQueryWrapperX<AddSubItem>().in(AddSubItem::getContractCode, saleContractCodeList));
        result.setAddSubItemList(addSubItemList);
        List<LocalDateTime> deliveryDateList = children.stream().map(PushOytShipmentPlanItem::getDeliveryDate).filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isNotEmpty(deliveryDateList)) {
            // 预计交货日期  取明细中最晚时间
            result.setEstDeliveryDate(Collections.max(deliveryDateList));
        }
        result.setChildren(children);
        result.setStatus(ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue());
        result.setShipmentCustList(custList);
        if (CollUtil.isNotEmpty(custList)) {
            String sideShippingMark = custList.stream().map(SimpleCustSaveResp::getSideShippingMark).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
            String frontShippingMark = custList.stream().map(SimpleCustSaveResp::getFrontShippingMark).filter(StrUtil::isNotEmpty).collect(Collectors.joining(CommonDict.COMMA));
            result.setSideShippingMark(sideShippingMark).setFrontShippingMark(frontShippingMark);
        }
        // 赋值采购员跟业务员
        setPurchaseAndManager(saleContractDOList, result, saleContractItemList);
        SaleContractDO saleContractDO = saleContractDOList.get(0);
        // 出运国信息
        result.setDepartureCountryId(saleContractDO.getDepartureCountryId());
        result.setDepartureCountryName(saleContractDO.getDepartureCountryName());
        result.setDepartureCountryArea(saleContractDO.getDepartureCountryArea());
        result.setDeparturePortId(saleContractDO.getDeparturePortId());
        result.setDeparturePortName(saleContractDO.getDeparturePortName());
        // 贸易国信息
        result.setTradeCountryId(saleContractDO.getTradeCountryId());
        result.setTradeCountryName(saleContractDO.getTradeCountryName());
        result.setTradeCountryArea(saleContractDO.getTradeCountryArea());
        result.setDestinationPortId(saleContractDO.getDestinationPortId());
        result.setDestinationPortName(saleContractDO.getDestinationPortName());
        result.setLinkCodeList(saleContractDO.getLinkCodeList());
        result.setTransportType(saleContractDO.getTransportType());
        result.setSettlementTermType(saleContractDO.getSettlementTermType());
        String custPo = saleContractDOList.stream().map(SaleContractDO::getCustPo).filter(Objects::nonNull).distinct().collect(Collectors.joining(CommonDict.COMMA));
        String custName = saleContractDOList.stream().map(SaleContractDO::getCustName).filter(Objects::nonNull).distinct().collect(Collectors.joining(CommonDict.COMMA));
        String managerName = saleContractDOList.stream().map(SaleContractDO::getSales).filter(Objects::nonNull).map(UserDept::getNickname).filter(Objects::nonNull).distinct().collect(Collectors.joining(CommonDict.COMMA));
        result.setCustPo(custPo);
        result.setCustName(custName);
        result.setSaleContractCode(String.join(CommonDict.COMMA, saleContractCodeList));
        result.setManagerName(managerName);
        return result;
    }

    private void setPurchaseAndManager(List<SaleContractDO> saleContractDOList, PushOutShipmentPlanResp result, List<SaleContractItem> saleContractItemList) {
        // 设置采购员
        List<UserDept> purchaseUsers = saleContractItemList.stream().map(SaleContractItem::getPurchaseUser).distinct().toList();
        // 设置销售员
        List<UserDept> salesList = saleContractDOList.stream().map(SaleContractDO::getSales).distinct().toList();
        result.setPurchaseUserList(purchaseUsers);
        result.setManagerList(salesList);
    }

    private Map<Long, Long> getLastCompanyPathByItemList(List<SaleContractItem> saleItemList) {
        if (CollUtil.isEmpty(saleItemList)) {
            return null;
        }
        List<SaleContractItem> allSaleContractItems = null;
        List<String> saleCodeList = new ArrayList<>(saleItemList.stream().map(SaleContractItem::getContractCode).distinct().toList());
        List<SaleContractDO> saleRelationDOS = saleContractMapper.selectList(SaleContractDO::getSourceContractCode, saleCodeList);
        if (CollUtil.isNotEmpty(saleRelationDOS)) {
            saleCodeList.addAll(saleRelationDOS.stream().map(SaleContractDO::getCode).distinct().toList());
        }
        allSaleContractItems = saleContractItemMapper.selectList(SaleContractItem::getContractCode, saleCodeList);
        if (CollUtil.isEmpty(allSaleContractItems)) {
            return null;
        }
        Map<Long, String> idMap = allSaleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getContractCode, (s1, s2) -> s1));
        List<String> SalecodeList = idMap.values().stream().distinct().toList();
        if (CollUtil.isEmpty(SalecodeList)) {
            return null;
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(
                new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getCode, SalecodeList));
        if (CollUtil.isEmpty(saleContractDOS)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        Map<Long, Long> result = new HashMap<>();
        idMap.forEach((k, v) -> {
            Optional<SaleContractDO> first = saleContractDOS.stream().filter(s -> Objects.equals(s.getCode(), v)).findFirst();
            if (first.isPresent()) {
                JsonCompanyPath companyPath = first.get().getCompanyPath();
                JsonCompanyPath lastNode = getLastNode(companyPath);
                if (Objects.nonNull(lastNode)) {
                    result.put(k, lastNode.getId());
                }
            }
        });
        return result;
    }

    private JsonCompanyPath getLastNode(JsonCompanyPath companyPath) {
        return Objects.isNull(companyPath.getNext()) ? companyPath : getLastNode(companyPath.getNext());
    }

    @Override
    public List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(saleContractCodeList)) {
            return null;
        }
        List<AddSubItem> addSubItemList = addSubItemMapper.selectList(new LambdaQueryWrapperX<AddSubItem>().in(AddSubItem::getContractCode, saleContractCodeList));
        if (CollUtil.isEmpty(addSubItemList)) {
            return null;
        }
        return BeanUtils.toBean(addSubItemList, AddSubItemDTO.class);
    }

    @Override
    public void batchUpdateAddSubItem(List<AddSubItemDTO> addSubItemDTOList) {
        if (CollUtil.isEmpty(addSubItemDTOList)) {
            return;
        }
        List<String> saleContractCodeList = addSubItemDTOList.stream().map(AddSubItemDTO::getContractCode).distinct().toList();
        if (CollUtil.isEmpty(saleContractCodeList)) {
            return;
        }
        List<SaleContractDO> saleContractDOList = getSmsByCodeList(saleContractCodeList);
        List<AddSubItemDTO> addItemList = addSubItemDTOList.stream().filter(s -> CalclationTypeEnum.ADD.getType().equals(s.getCalculationType())).toList();
        List<AddSubItemDTO> subItemList = addSubItemDTOList.stream().filter(s -> CalclationTypeEnum.DEDUCTION.getType().equals(s.getCalculationType())).toList();
        Map<String, LocalDateTime> saleDateMap = saleContractDOList.stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getSaleContractDate,(s1,s2)->s1));
        Map<String, Map<String, BigDecimal>> rateMapByDateMap = rateApi.getDailyRateMapByDate(saleDateMap);

        JsonAmount additionAmount = new JsonAmount();
        additionAmount.setCurrency("RMB");
        JsonAmount deductionAmount = new JsonAmount();
        deductionAmount.setCurrency("RMB");
        if (CollUtil.isNotEmpty(addItemList)) {
            BigDecimal additionAmountCalc = addItemList.stream()
                    .map(s -> {
                        JsonAmount amount = s.getAmount();
                        if (Objects.isNull(amount)) {
                            return null;
                        }
                        BigDecimal currencyRate = rateMapByDateMap.get(s.getContractCode()).get(amount.getCurrency());
                        return NumUtil.mul(amount.getAmount(), currencyRate);
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            additionAmount.setAmount(additionAmountCalc);
        }
        if (CollUtil.isNotEmpty(subItemList)) {
            BigDecimal deductionAmountCalc = subItemList.stream()
                    .map(s -> {
                        JsonAmount amount = s.getAmount();
                        if (Objects.isNull(amount)) {
                            return null;
                        }
                        BigDecimal currencyRate = rateMapByDateMap.get(s.getContractCode()).get(amount.getCurrency());
                        return NumUtil.mul(amount.getAmount(), currencyRate);
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            deductionAmount.setAmount(deductionAmountCalc);
        }
        List<SaleContractItem> itemList = saleContractItemMapper.selectList(SaleContractItem::getContractCode, saleContractCodeList);
        Map<String, List<SaleContractItem>> itemMap = itemList.stream().collect(Collectors.groupingBy(SaleContractItem::getContractCode));
        saleContractDOList.forEach(item -> {
            if (Objects.nonNull(additionAmount.getAmount())) {
                item.setAdditionAmount(additionAmount);
            }
            if (Objects.nonNull(deductionAmount.getAmount())) {
                item.setDeductionAmount(deductionAmount);
            }
            Map<String, BigDecimal> currencyRateMap = rateMapByDateMap.get(item.getCode());
            //销售总金额重新计算
            List<SaleContractItem> saleContractItemList = itemMap.get(item.getCode());
            BigDecimal subCommissionAmount = saleContractItemList.stream()
                    .filter(s -> BooleanEnum.YES.getValue().equals(s.getCommissionSubTotal()))
                    .map(s -> {
                        JsonAmount commissionAmount = s.getCommissionAmount();
                        if (Objects.isNull(commissionAmount)) {
                            return null;
                        }
                        BigDecimal amount = commissionAmount.getAmount();
                        return NumUtil.mul(amount,  rateMapByDateMap.get(s.getContractCode()).get(s.getCommissionAmount().getCurrency()));
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);

            BigDecimal totalAmountCalc = NumberUtil.mul(item.getTotalGoodsValue().getAmount(), currencyRateMap.get(item.getCurrency()))
                    .add(NumberUtil.mul(item.getAdditionAmount().getAmount(),  currencyRateMap.get(item.getAdditionAmount().getCurrency())))
                    .subtract(NumberUtil.mul(item.getDeductionAmount().getCheckAmount(), currencyRateMap.get(item.getDeductionAmount().getCurrency())))
                    .subtract(subCommissionAmount)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            JsonAmount totalAmount = new JsonAmount();
            totalAmount.setCurrency("RMB");
            totalAmount.setAmount(totalAmountCalc);
            item.setTotalAmount(totalAmount);
            BigDecimal totalAmountUsdCalc = NumberUtil.div(totalAmountCalc, currencyRateMap.get("USD")).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
            JsonAmount totalAmountUsd = new JsonAmount();
            totalAmountUsd.setCurrency("USD");
            totalAmountUsd.setAmount(totalAmountUsdCalc);
            item.setTotalAmountUsd(totalAmountUsd);
            //毛利毛利率重新计算
            //毛利润=收入-成本
            //其中收入=货值合计*汇率+加项金额+退税金额
            //其中成本=采购合计+包材+配件+总运费+验货费+平台费+减项金额+保险费+中信保费
            //采购合计=采购总金额*对人民币汇率，需要看采购币种
            BigDecimal addAmount = NumberUtil.mul(item.getTotalGoodsValue().getAmount(), currencyRateMap.get(item.getTotalGoodsValue().getCurrency()))
                    .add(NumberUtil.mul(item.getTotalVatRefund().getAmount(), currencyRateMap.get(item.getTotalVatRefund().getCurrency())))
                    .add(NumberUtil.mul(item.getAdditionAmount().getAmount(), currencyRateMap.get(item.getAdditionAmount().getCurrency())))
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);

            BigDecimal totalPurchaseCalc = item.getTotalPurchase().stream()
                    .map(s -> {
                        BigDecimal amount = s.getAmount();
                        return NumUtil.mul(amount, currencyRateMap.get(s.getCurrency()));
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            BigDecimal subAmount = totalPurchaseCalc
                    .add(NumberUtil.mul(item.getEstimatedPackingMaterials().getAmount(), currencyRateMap.get(item.getEstimatedPackingMaterials().getCurrency())))
                    .add(NumberUtil.mul(item.getAccessoriesPurchaseTotal().getAmount(), currencyRateMap.get(item.getAccessoriesPurchaseTotal().getCurrency())))
                    .add(NumberUtil.mul(item.getEstimatedTotalFreight().getAmount(), currencyRateMap.get(item.getEstimatedTotalFreight().getCurrency())))
                    .add(NumberUtil.mul(item.getInspectionFee().getAmount(), currencyRateMap.get(item.getInspectionFee().getCurrency())))
                    .add(NumberUtil.mul(item.getPlatformFee().getAmount(), currencyRateMap.get(item.getPlatformFee().getCurrency())))
                    .add(NumberUtil.mul(item.getDeductionAmount().getAmount(), currencyRateMap.get(item.getDeductionAmount().getCurrency())))
                    .add(NumberUtil.mul(item.getInsuranceFee().getAmount(), currencyRateMap.get(item.getInsuranceFee().getCurrency())))
                    .add(NumberUtil.mul(item.getSinosureFee().getAmount(), currencyRateMap.get(item.getSinosureFee().getCurrency())))
                    .add(subCommissionAmount)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            //毛利润 = 收入 - 成本
            BigDecimal orderGrossProfitCalc = addAmount.subtract(subAmount);
            JsonAmount orderGrossProfit = new JsonAmount();
            orderGrossProfit.setAmount(orderGrossProfitCalc);
            orderGrossProfit.setCurrency("RMB");
            item.setOrderGrossProfit(orderGrossProfit);
            //毛利率=毛利润/收入
            BigDecimal grossProfitMargin = BigDecimal.ZERO.compareTo(addAmount) == 0 ?BigDecimal.ZERO:NumberUtil.div(orderGrossProfitCalc, addAmount);
            item.setRealGrossProfitMargin(grossProfitMargin);
        });

        saleContractMapper.updateBatch(saleContractDOList);
        // 删除之前的加减项
        addSubItemMapper.delete(new LambdaQueryWrapperX<AddSubItem>().in(AddSubItem::getContractCode, saleContractCodeList));
        // 重新插入
        addSubItemMapper.insertBatch(BeanUtils.toBean(addSubItemDTOList, AddSubItem.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signBackSaleContract(SignBackReq signBackReq) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        LocalDateTime signBackDate = signBackReq.getSignBackDate();
        if (Objects.isNull(signBackDate)) {
            throw exception(SIGN_BACK_DATE_NOT_NULL);
        }
        if (signBackDate.isAfter(LocalDateTime.now())) {
            throw exception(SIGN_BACK_DATE_NOT_AFTER_NOW);
        }
        SaleContractDO saleContractDO = saleContractMapper.selectById(signBackReq.getContractId());
        if (!SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue().equals(saleContractDO.getStatus())){
            throw exception(CONTRACT_ALL_SIGN_BACK);
        }
        if (Objects.isNull(saleContractDO)) {
            return;
        }
        saleContractDO.setSignBackAnnex(signBackReq.getSignBackAnnex());
        saleContractDO.setSignBackFlag(BooleanEnum.YES.getValue());
        saleContractDO.setSignBackDate(signBackReq.getSignBackDate());
        saleContractDO.setSignBackUser(userDept);
        saleContractDO.setSignBackDesc(signBackReq.getSignBackDesc());
        saleContractDO.setStatus(SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue());
        // 批量更新合同回签信息
        saleContractMapper.updateById(saleContractDO);
        //重新计算销售金额
        updateBySaleContractDate(saleContractDO,signBackReq.getSignBackDate());
        // 插入操作日志
        OperateLogUtils.setContent(String.format(SaleDict.SIGN_BACK_OPERATOR_MESSAGE, saleContractDO.getCode(), signBackReq.getSignBackDesc()));
        OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContractDO.getCode());
        // 更新收款计划预计收款日  回签日+3天
//        LocalDateTime expectedReceiptDate = signBackDate.plusDays(3);
//        LambdaQueryWrapperX<CollectionPlan> queryWrapperX = new LambdaQueryWrapperX<CollectionPlan>().eq(CollectionPlan::getContractId, saleContractDO.getId()).eq(CollectionPlan::getDateType, DateTypeEnum.SIGN_BACK_DATE.getValue());
//        CollectionPlan collectionPlan = new CollectionPlan();
//        collectionPlan.setStartDate(signBackDate);
//        collectionPlan.setExpectedReceiptDate(expectedReceiptDate);
//        collectionPlanMapper.update(collectionPlan, queryWrapperX);
        List<Integer> dateTypes = new ArrayList<>();
        dateTypes.add(ShettlementDateTypeEnum.SIGNBACK_DATE.getValue());
        updatePaymentPlanSignBackTime(saleContractDO.getId(), dateTypes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeSaleContract(CloseSaleContractReq closeSaleContractReq) {

        if (Objects.nonNull(closeSaleContractReq.getItemId())) {
            SaleContractItem baseItem = saleContractItemMapper.selectById(closeSaleContractReq.getItemId());
            if (Objects.isNull(baseItem)) {
                throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
            }
            boolean exists = saleContractMapper.exists(new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getId, baseItem.getContractId()).eq(SaleContractDO::getStatus, SaleContractStatusEnum.CASE_CLOSED.getValue()));
            if (exists){
                throw exception(CONTRACT_ALL_CLOSE);
            }
            validateAntiAuditStatus(baseItem.getContractCode());
            SaleContractItem saleContractItem = new SaleContractItem();
            saleContractItem.setId(closeSaleContractReq.getItemId());
            saleContractItem.setStatus(SaleContractStatusEnum.CASE_CLOSED.getValue());
            saleContractItemMapper.updateById(saleContractItem);
            List<Integer> contractItemStatusLink = saleContractItemMapper.getSaleContractItemStatusLink(closeSaleContractReq.getItemId());
            if (CollUtil.isEmpty(contractItemStatusLink)) {
                throw exception(SALE_CONTRACT_STATUS_EMPTY);
            }
            // 判断是否销售明细全部结案
            boolean allClosed = contractItemStatusLink.stream()
                    .allMatch(status -> status.equals(SaleContractStatusEnum.CASE_CLOSED.getValue()));
            // 子项全部结案则销售合同状态变更为结案
            if (allClosed) {
                SaleContractItem closedSaleContractItem = saleContractItemMapper.selectById(closeSaleContractReq.getItemId());
                Long contractId = closedSaleContractItem.getContractId();
                saleContractMapper.updateById(new SaleContractDO().setId(contractId).setStatus(SaleContractStatusEnum.CASE_CLOSED.getValue()));
            }
        }
        if (Objects.nonNull(closeSaleContractReq.getParentId())) {
            SaleContractDO contractDO = validateSaleContractExists(closeSaleContractReq.getParentId());
            if (SaleContractStatusEnum.CASE_CLOSED.getValue().equals(contractDO.getStatus())){
                throw exception(CONTRACT_ALL_CLOSE);
            }
            // 校验是否有后续单据
            validateAntiAuditStatus(contractDO.getCode());
            // 释放销售合同锁定库存
            stockApi.cancelStockLock(contractDO.getCode(), null, null);
//            SaleContractDO saleContractDO = new SaleContractDO();
//            saleContractDO.setId(closeSaleContractReq.getParentId());
            contractDO.setStatus(SaleContractStatusEnum.CASE_CLOSED.getValue());

            orderLinkApi.updateOrderLinkStatus(contractDO.getCode(), BusinessNameDict.EXPORT_SALE_CONTRACT_NAME, contractDO.getLinkCodeList(), contractDO.getStatus());
            saleContractMapper.updateById(contractDO);
            SaleContractItem saleContractItem = new SaleContractItem();
            saleContractItem.setStatus(SaleContractStatusEnum.CASE_CLOSED.getValue());
            saleContractItemMapper.update(saleContractItem, new LambdaQueryWrapperX<SaleContractItem>().eq(SaleContractItem::getContractId, closeSaleContractReq.getParentId()));
        }

    }

    @Override
    public void updateShipmentQuantity(Map<Long, Integer> shipmentQuantityMap, boolean cancelFlag) {
        if (CollUtil.isEmpty(shipmentQuantityMap)) {
            return;
        }
        Set<Long> saleContractItemIds = shipmentQuantityMap.keySet();
        if (CollUtil.isEmpty(saleContractItemIds)) {
            return;
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectBatchIds(saleContractItemIds);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        List<SaleContractItem> resultList = new ArrayList<>();
        saleContractItemList.forEach(s -> {
            // 销售数量
            Integer quantity = s.getQuantity();
            // 已出运数量
            Integer transferShippedQuantity = Objects.isNull(s.getTransferShippedQuantity()) ? CalculationDict.ZERO : s.getTransferShippedQuantity();
            Long id = s.getId();
            // 当次出运数量
            Integer shippingQuantity = shipmentQuantityMap.get(id);
            if (Objects.isNull(shippingQuantity)) {
                return;
            }
            if (NumUtil.compare(transferShippedQuantity, quantity) > CalculationDict.ZERO) {
                throw exception(SHIPMENT_QUANTITY_EXCEED);
            }
            // 如正常回写 则 已出运数量 = 已出运数量 + 本次出运数量
            // 撤销出运  则 已出运数量 = 已出运数量 - 本次出运数量
            int transferShippedQuantityResult;
            if (cancelFlag) {
                transferShippedQuantityResult = NumUtil.sub(transferShippedQuantity, shippingQuantity).intValue();
            } else {
                transferShippedQuantityResult = NumUtil.add(transferShippedQuantity, shippingQuantity).intValue();
            }
            if (NumUtil.compare(transferShippedQuantityResult, quantity) > CalculationDict.ZERO) {
                throw exception(SHIPMENT_QUANTITY_EXCEED);
            }
            s.setTransferShippedQuantity(transferShippedQuantityResult);
            // 待出运数量 = 销售数量 - 已出运数量
            int unTransferShippedQuantityResult = NumUtil.sub(quantity, Integer.valueOf(transferShippedQuantityResult)).intValue();
            if (unTransferShippedQuantityResult == 0) {
                s.setConvertShipmentFlag(BooleanEnum.YES.getValue());
            } else {
                s.setConvertShipmentFlag(BooleanEnum.NO.getValue());
            }
            resultList.add(s);
        });
        if (CollUtil.isNotEmpty(resultList)) {
            saleContractItemMapper.updateBatch(resultList);
        }
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, Long id) {
        SaleContractDO saleContractDO = validateSaleContractExists(id);
        saleContractDO.setConfirmFlag(confirmFlag);
        saleContractMapper.update(saleContractDO, new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getId, id));
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, String code) {
        SaleContractDO saleContractDO = saleContractMapper.selectOne(SaleContractDO::getCode, code);
        saleContractDO.setConfirmFlag(confirmFlag);
        saleContractMapper.update(saleContractDO, new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCode, code));
    }

    @Override
    public void updateCust(Long custId, String custName, String custCode) {
        Integer[] statusList = {
                SaleContractStatusEnum.WAITING_FOR_SUBMISSION.getValue(),
                SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue(),
                SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue(),
                SaleContractStatusEnum.REJECTED.getValue(),
                SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue(),
                SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue()
        };
        saleContractMapper.update(new SaleContractDO().setCustId(custId).setCustName(custName), new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCustCode, custCode).in(SaleContractDO::getStatus, (Object[]) statusList));
    }

    @Override
    public String getProcessDefinitionKeyByBusinessId(Long id) {
        SaleContractChange saleContractChange = saleContractChangeMapper.selectById(id);
        if (Objects.isNull(saleContractChange)) {
            return null;
        }
        return saleContractChange.getModelKey();
    }

    @Override
    public void updateEffectRanageStatus(Long changeId) {
        updateChangeResult(changeId);

    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        LambdaQueryWrapper<SaleContractChange> queryWrapper = new LambdaQueryWrapper<SaleContractChange>().select(SaleContractChange::getId, SaleContractChange::getCode)
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
        List<SaleContractChange> TList = saleContractChangeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return List.of();
        }
        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getCode()).setConfirmSourceType(EffectRangeEnum.SMS.getValue())).toList();
    }

    @Override
    public void updateChangeConfirmFlag(Integer effectRangeType, String code) {
        LambdaQueryWrapper<SaleContractChange> queryWrapper = new LambdaQueryWrapper<SaleContractChange>()
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(code).setConfirmFlag(0)));
        List<SaleContractChange> TList = saleContractChangeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return;
        }
        TList.forEach(s -> {
            List<JsonEffectRange> effectRangeList = s.getEffectRangeList();
            if (CollUtil.isEmpty(effectRangeList)) {
                return;
            }
            effectRangeList.forEach(e -> {
                if (e.getEffectRangeCode().equals(code) && e.getEffectRangeType().equals(effectRangeType)) {
                    e.setConfirmFlag(1);
                }
            });
        });
        saleContractChangeMapper.updateBatch(TList);
    }

    @Override
    public void writeBackContract(List<WriteBackDTO> writeBackDTOList, boolean rollbackFlag) {
        if (CollUtil.isEmpty(writeBackDTOList)) {
            return;
        }

        List<WriteBackDTO> collectionPlanList = writeBackDTOList.stream().filter(s -> CollectionPlanStepEnum.getStepList().contains(s.getSourceType())).toList();
        if (CollUtil.isNotEmpty(collectionPlanList)) {
            List<Long> idList = collectionPlanList.stream().map(WriteBackDTO::getItemId).toList();
            // 查找数据库中收款计划
            List<CollectionPlan> baseCollectionPlanList = collectionPlanMapper.selectBatchIds(idList);
            if (CollUtil.isNotEmpty(baseCollectionPlanList)) {
                Map<Long, CollectionPlan> collectionPlanMap = baseCollectionPlanList.stream().collect(Collectors.toMap(CollectionPlan::getId, s -> s));
                if (CollUtil.isNotEmpty(collectionPlanMap)) {
                    // 赋值处理
                    List<CollectionPlan> updateCollectionPlan = collectionPlanList.stream().map(s -> {
                        CollectionPlan collectionPlan = collectionPlanMap.get(s.getItemId());
                        if (Objects.isNull(collectionPlan)) {
                            return null;
                        }
                        List<CollectionPlanItem> children = CollUtil.isEmpty(collectionPlan.getChildren())
                                ? new ArrayList<>()
                                : new ArrayList<>(collectionPlan.getChildren());
                        if (rollbackFlag) {
                            children.removeIf(x -> Objects.nonNull(x.getCustClaimItemId()) && x.getCustClaimItemId().equals(s.getId()));
                        } else {
                            children.add(s.getCollectionPlanItem());
                        }
                        if (CollUtil.isNotEmpty(children)) {
                            collectionPlan.setChildren(children.stream().filter(Objects::nonNull).toList());
                        } else {
                            collectionPlan.setChildren(new ArrayList<>());
                        }
                        if (!rollbackFlag) {
                            collectionPlan.setDifferenceReason(s.getDifferenceReason());
                        }
                        JsonAmount receivedAmount = collectionPlan.getReceivedAmount();
                        if (Objects.isNull(receivedAmount)) {
                            if (!rollbackFlag) {
                                collectionPlan.setReceivedAmount(new JsonAmount(s.getContractAmount(), s.getCurrency()));
                            }
                        } else {
                            BigDecimal baseAmount = receivedAmount.getAmount();
                            // 如果数据库中已处理金额为空或等于0 则已处理金额 = 本次认领金额
                            if (Objects.isNull(baseAmount) || baseAmount.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
                                if (!rollbackFlag) {
                                    collectionPlan.setReceivedAmount(new JsonAmount(s.getContractAmount(), s.getCurrency()));
                                }
                            } else {
                                BigDecimal calcAmount;
                                if (rollbackFlag) {
                                    calcAmount = NumUtil.sub(baseAmount, s.getContractAmount());
                                } else {
                                    calcAmount = NumUtil.add(baseAmount, s.getContractAmount());
                                }
                                collectionPlan.setReceivedAmount(new JsonAmount().setAmount(calcAmount).setCurrency(s.getCurrency()));
                            }
                        }
                        updateExeStatus(collectionPlan);
                        if(BooleanEnum.YES.getValue().equals(s.getCompletedFlag())){
                            collectionPlan.setExeStatus(ExecuteStatusEnum.EXECUTED.getStatus());
                        }
                        return collectionPlan;
                    }).filter(Objects::nonNull).toList();
                    if (CollUtil.isNotEmpty(updateCollectionPlan)) {
                        collectionPlanMapper.updateBatch(updateCollectionPlan);
                    }
                }
            }
        }
        List<WriteBackDTO> addSubItemList = writeBackDTOList.stream().filter(s -> CalculationDict.ZERO == s.getSourceType()).toList();
        if (CollUtil.isNotEmpty(addSubItemList)) {
            List<Long> idList = addSubItemList.stream().map(WriteBackDTO::getItemId).toList();
            if (CollUtil.isNotEmpty(idList)) {
                List<AddSubItem> baseAddSubItemList = addSubItemMapper.selectBatchIds(idList);
                if (CollUtil.isNotEmpty(baseAddSubItemList)) {
                    Map<Long, AddSubItem> addSubItemMap = baseAddSubItemList.stream().collect(Collectors.toMap(AddSubItem::getId, s -> s));
                    if (CollUtil.isNotEmpty(addSubItemMap)) {
                        List<AddSubItem> updateAddSubItem = addSubItemList.stream().map(s -> {
                            AddSubItem addSubItem = addSubItemMap.get(s.getItemId());
                            if (Objects.isNull(addSubItem)) {
                                return null;
                            }
                            addSubItem.setStatus(s.getCompletedFlag());
                            JsonAmount completedAmount = addSubItem.getCompletedAmount();
                            if (Objects.isNull(completedAmount)) {
                                if (!rollbackFlag) {
                                    addSubItem.setCompletedAmount(new JsonAmount(s.getContractAmount(), s.getCurrency()));
                                }
                            } else {
                                BigDecimal baseAmount = completedAmount.getAmount();
                                // 如果数据库中已处理金额为空或等于0 则已处理金额 = 本次认领金额
                                if (Objects.isNull(baseAmount) || baseAmount.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
                                    if (!rollbackFlag) {
                                        addSubItem.setCompletedAmount(new JsonAmount(s.getContractAmount(), s.getCurrency()));
                                    }
                                } else {
                                    BigDecimal calcAmount;
                                    if (rollbackFlag) {
                                        calcAmount = NumUtil.sub(baseAmount, s.getContractAmount());
                                    } else {
                                        calcAmount = NumUtil.add(baseAmount, s.getContractAmount());
                                    }
                                    addSubItem.setCompletedAmount(new JsonAmount().setAmount(calcAmount).setCurrency(s.getCurrency()));
                                }
                            }
                            if (!rollbackFlag) {
                                addSubItem.setDifferenceReason(s.getDifferenceReason());
                            }

                            return addSubItem;
                        }).toList();
                        addSubItemMapper.updateBatch(updateAddSubItem);
                    }
                }
            }
        }
    }

    @Override
    @DataPermission(enable = false)
    public List<SaleContractDO> getSmsByCodeList(List<String> codeList) {
        return saleContractMapper.selectList(SaleContractDO::getCode, codeList);
    }

    private void updateExeStatus(CollectionPlan collectionPlan) {
        if (Objects.isNull(collectionPlan)) {
            return;
        }
        List<CollectionPlanItem> children = collectionPlan.getChildren();
        if (CollUtil.isEmpty(children)) {
            collectionPlan.setExeStatus(ExecuteStatusEnum.NOT_EXECUTED.getStatus());
        } else {
            Optional<BigDecimal> claimAmountOpt = children.stream().map(CollectionPlanItem::getAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal::add);
            claimAmountOpt.ifPresent(claimAmount -> collectionPlan.setExeStatus(claimAmount.compareTo(collectionPlan.getReceivableAmount().getAmount()) == CalculationDict.ZERO ? ExecuteStatusEnum.EXECUTED.getStatus() : BigDecimal.ZERO.compareTo(claimAmount) == 0 ? ExecuteStatusEnum.NOT_EXECUTED.getStatus() : ExecuteStatusEnum.PART_EXECUTED.getStatus()));
        }
    }

    /**
     * 处理出运计划中间页面数据
     *
     * @param saleContractItem 销售明细
     * @param saleContractDO   销售合同信息
     * @return
     */
    private PushOytShipmentPlanItem dealPushOytShipmentPlanItem(SaleContractItem saleContractItem, SaleContractDO saleContractDO, Map<Long, PurchaseContractItemDTO> purchaseContractItemMap, List<PackageTypeDTO> packageTypeList,Map<Long, List<StockDTO>> stockDTOMap) {
        // 赋值销售订单信息
        PushOytShipmentPlanItem pushOytShipmentPlanItem = SaleContractConvert.INSTANCE.convertShipmentPlanItem(saleContractItem);
        //库存采购合同号赋值
        List<String> allPurchaseContractCodes = new ArrayList<>();
        
        // 1. 从库存信息中获取采购合同号
        if(CollUtil.isNotEmpty(stockDTOMap)){
            List<StockDTO> stockDTOList =  stockDTOMap.get(saleContractItem.getId());
            if(CollUtil.isNotEmpty(stockDTOList)){
                List<String> stockPurchaseCodes = stockDTOList.stream()
                        .map(StockDTO::getPurchaseContractCode)
                        .filter(Objects::nonNull)
                        .filter(StrUtil::isNotEmpty)
                        .collect(Collectors.toList());
                allPurchaseContractCodes.addAll(stockPurchaseCodes);
            }
        }
        
        // 2. 根据销售明细ID查询采购明细对应的采购合同号
        if(saleContractItem.getId() != null){
            Map<Long, Map<String, List<SimpleContractDetailDTO>>> contractCodeMap = purchaseContractApi.getContractCodeMapByItemId(List.of(saleContractItem.getId()));
            if(CollUtil.isNotEmpty(contractCodeMap)){
                Map<String, List<SimpleContractDetailDTO>> skuContractMap = contractCodeMap.get(saleContractItem.getId());
                if(CollUtil.isNotEmpty(skuContractMap)){
                    List<String> purchaseCodes = skuContractMap.values().stream()
                            .flatMap(List::stream)
                            .filter(Objects::nonNull)
                            .filter(s->BooleanEnum.NO.getValue().equals(s.getAutoFlag()))
                            .map(SimpleContractDetailDTO::getContractCode)
                            .distinct()
                            .collect(Collectors.toList());
                    allPurchaseContractCodes.addAll(purchaseCodes);
                }
            }
        }
        
        // 3. 合并并去重
        List<String> stockPurchaseContractCodes = allPurchaseContractCodes.stream()
                .distinct()
                .collect(Collectors.toList());
        
        if(CollUtil.isNotEmpty(stockPurchaseContractCodes)){
            pushOytShipmentPlanItem.setStockPurchaseContractCodes(stockPurchaseContractCodes);
        }
        pushOytShipmentPlanItem.setCompanyId(saleContractDO.getCompanyId());
        pushOytShipmentPlanItem.setCompanyName(saleContractDO.getCompanyName());
        pushOytShipmentPlanItem.setCustId(saleContractDO.getCustId());
        pushOytShipmentPlanItem.setSaleContractCode(saleContractItem.getContractCode());
        pushOytShipmentPlanItem.setVenderCode(saleContractItem.getVenderCode());
        pushOytShipmentPlanItem.setVenderId(saleContractItem.getVenderId());
        pushOytShipmentPlanItem.setVenderName(saleContractItem.getVenderName());
        pushOytShipmentPlanItem.setCustName(saleContractDO.getCustName());
        pushOytShipmentPlanItem.setStatus(ShippingPlanStatusEnum.AWAITING_PROCESSING.getValue());
        pushOytShipmentPlanItem.setCustCode(saleContractDO.getCustCode());
        pushOytShipmentPlanItem.setSaleUnitPrice(saleContractItem.getUnitPrice());
        pushOytShipmentPlanItem.setSettlementId(saleContractDO.getSettlementId());
        pushOytShipmentPlanItem.setSettlementName(saleContractDO.getSettlementName());
        pushOytShipmentPlanItem.setSettlementTermType(saleContractDO.getSettlementTermType());
        pushOytShipmentPlanItem.setDeclarationQuantity(saleContractItem.getQuantity());
        pushOytShipmentPlanItem.setSaleQuantity(saleContractItem.getQuantity());
        pushOytShipmentPlanItem.setShippingQuantity(saleContractItem.getQuantity());
        pushOytShipmentPlanItem.setCurrency(saleContractDO.getCurrency());
        pushOytShipmentPlanItem.setCustPo(saleContractDO.getCustPo());
        pushOytShipmentPlanItem.setFreeFlag(saleContractItem.getFreeFlag());
        Integer splitPurchaseFlag = saleContractItem.getSplitPurchaseFlag();
        Integer splitPurchaseQuantity = Objects.isNull(saleContractItem.getSplitPurchaseQuantity())?0:saleContractItem.getSplitPurchaseQuantity();
        List<SplitPurchase> splitPurchaseList = CollUtil.isEmpty(saleContractItem.getSplitPurchaseList())?List.of():saleContractItem.getSplitPurchaseList();
        Integer billStatus = Objects.isNull(saleContractItem.getBillStatus()) ? ContractItemBillStatusEnum.VENDER_STORE.getStatus() : saleContractItem.getBillStatus();
        if (ContractItemBillStatusEnum.VERTAK_STORE.getStatus().equals(billStatus)) {
            pushOytShipmentPlanItem.setShippedAddress(ShippedAddressEnum.BILL.getValue());
        } else {
            if (BooleanEnum.YES.getValue().equals(splitPurchaseFlag)||splitPurchaseQuantity>0||splitPurchaseList.size()>0){
                pushOytShipmentPlanItem.setShippedAddress(ShippedAddressEnum.BILL.getValue());
            }else {
                pushOytShipmentPlanItem.setShippedAddress(ShippedAddressEnum.FACTORY.getValue());
            }
        }
        if (Objects.isNull(pushOytShipmentPlanItem.getTotalNetweight())) {
            pushOytShipmentPlanItem.setTotalNetweight(new JsonWeight());
        }
        if (Objects.isNull(pushOytShipmentPlanItem.getTotalGrossweight())) {
            pushOytShipmentPlanItem.setTotalGrossweight(new JsonWeight());
        }
        if (Objects.isNull(pushOytShipmentPlanItem.getDeclarationAmount())) {
            pushOytShipmentPlanItem.setDeclarationAmount(new JsonAmount());
        }
        if (Objects.isNull(pushOytShipmentPlanItem.getSaleAmount())) {
            pushOytShipmentPlanItem.setSaleAmount(new JsonAmount());
        }

        pushOytShipmentPlanItem.setSaleContractItemId(saleContractItem.getId());
        pushOytShipmentPlanItem.setSourceUniqueCode(saleContractItem.getUniqueCode());
        pushOytShipmentPlanItem.setCommodityInspectionFlag(saleContractItem.getCommodityInspectionFlag());
        pushOytShipmentPlanItem.setPurchaseWithTaxPrice(saleContractItem.getRealPurchaseWithTaxPrice());
        pushOytShipmentPlanItem.setPurchaseQuantity(saleContractItem.getRealPurchaseQuantity());
        pushOytShipmentPlanItem.setTaxRefundRate(saleContractItem.getRealTaxRefundRate());
        Integer quantity = saleContractItem.getQuantity();
        Integer transferShippedQuantity = Objects.isNull(saleContractItem.getTransferShippedQuantity()) ? CalculationDict.ZERO : saleContractItem.getTransferShippedQuantity();
        Integer unTransferShippedQuantity = quantity - transferShippedQuantity;
        if (ObjectUtil.isNotNull(unTransferShippedQuantity)) {
            pushOytShipmentPlanItem.setUnshippedQuantity(unTransferShippedQuantity);
        } else {
            pushOytShipmentPlanItem.setUnshippedQuantity(saleContractItem.getQuantity());
        }
        if (ObjectUtil.isNotNull(transferShippedQuantity)) {
            pushOytShipmentPlanItem.setShippedQuantity(transferShippedQuantity);
        } else {
            pushOytShipmentPlanItem.setShippedQuantity(BigDecimal.ZERO.intValue());
        }
        // 赋值包装方式名称
        if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(saleContractItem.getPackageType())) {
            List<Long> distinctPackgeType = saleContractItem.getPackageType().stream().distinct().toList();
            List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item -> distinctPackgeType.contains(item.getId())).toList();
            if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                pushOytShipmentPlanItem.setPackageTypeName(String.join(",", packageTypeNameList));
            }
        }
        pushOytShipmentPlanItem.setSales(saleContractDO.getSales());
        // 采购信息
        if (CollUtil.isEmpty(purchaseContractItemMap)) {
            return pushOytShipmentPlanItem;
        }
        PurchaseContractItemDTO purchaseContractItemDTO = purchaseContractItemMap.get(saleContractItem.getId());
        if (Objects.isNull(purchaseContractItemDTO)) {
            return pushOytShipmentPlanItem;
        }
        pushOytShipmentPlanItem.setDeliveryDate(purchaseContractItemDTO.getDeliveryDate());
        pushOytShipmentPlanItem.setPurchaseContractCode(purchaseContractItemDTO.getPurchaseContractCode());
        pushOytShipmentPlanItem.setPurchaseUserId(purchaseContractItemDTO.getPurchaseUserId());
        pushOytShipmentPlanItem.setPurchaseUserName(purchaseContractItemDTO.getPurchaseUserName());
        pushOytShipmentPlanItem.setPurchaseUserDeptId(purchaseContractItemDTO.getPurchaseUserDeptId());
        pushOytShipmentPlanItem.setPurchaseUserDeptName(purchaseContractItemDTO.getPurchaseUserDeptName());
        pushOytShipmentPlanItem.setPurchaseQuantity(purchaseContractItemDTO.getQuantity());
        pushOytShipmentPlanItem.setBillStatus(purchaseContractItemDTO.getBillStatus());
        pushOytShipmentPlanItem.setPurchaseCurrency(purchaseContractItemDTO.getPurchaseCurrency());
        JsonAmount purchaseWithTaxPrice = pushOytShipmentPlanItem.getPurchaseWithTaxPrice();
        JsonAmount totalAmount = new JsonAmount();
        if (Objects.nonNull(purchaseWithTaxPrice)) {
            totalAmount.setCurrency(purchaseWithTaxPrice.getCurrency());
            if (Objects.isNull(pushOytShipmentPlanItem.getPurchaseQuantity())) {
                pushOytShipmentPlanItem.setPurchaseQuantity(0);
            }
            totalAmount.setAmount(purchaseWithTaxPrice.getAmount().multiply(BigDecimal.valueOf(pushOytShipmentPlanItem.getPurchaseQuantity())));
        }
//        pushOytShipmentPlanItem.setPurchaseTotalPrice(totalAmount);
        //规格，重量，供应商使用采购数据
        pushOytShipmentPlanItem.setSpecificationList(purchaseContractItemDTO.getSpecificationList());
        pushOytShipmentPlanItem.setTotalVolume(CalcSpecificationUtil.calcTotalVolumeByBoxCount(purchaseContractItemDTO.getSpecificationList(), BigDecimal.valueOf(purchaseContractItemDTO.getBoxCount())));
        pushOytShipmentPlanItem.setTotalNetweight(CalcSpecificationUtil.calcTotalNetWeightByBoxCount(purchaseContractItemDTO.getSpecificationList(), BigDecimal.valueOf(purchaseContractItemDTO.getBoxCount())));
        pushOytShipmentPlanItem.setTotalGrossweight(CalcSpecificationUtil.calcTotalGrossweightByBoxCount(purchaseContractItemDTO.getSpecificationList(), BigDecimal.valueOf(purchaseContractItemDTO.getBoxCount())));
        pushOytShipmentPlanItem.setVenderId(purchaseContractItemDTO.getVenderId());
        pushOytShipmentPlanItem.setVenderCode(purchaseContractItemDTO.getVenderCode());
        pushOytShipmentPlanItem.setVenderName(purchaseContractItemDTO.getVenderName());
        pushOytShipmentPlanItem.setPayVenderId(purchaseContractItemDTO.getPayVenderId());
        pushOytShipmentPlanItem.setPayVenderCode(purchaseContractItemDTO.getPayVenderCode());
        pushOytShipmentPlanItem.setPayVenderName(purchaseContractItemDTO.getPayVenderName());
        pushOytShipmentPlanItem.setHsMeasureUnit(HsMeasureUnitEnum.GE.getValue());
        return pushOytShipmentPlanItem;
    }


    private SaleContractChange validateChangeSaleContractExists(Long id) {
        SaleContractChange saleContractChange = saleContractChangeMapper.selectById(id);
        if (Objects.isNull(saleContractChange)) {
            throw exception(SALE_CONTRACT_CHANGE_NOT_EXISTS);
        }
        return saleContractChange;
    }


    /**
     * 打印外销/工厂合同（支持单个或批量）
     *
     * @param ids 外销合同ID列表（单个打印时传入只有一个元素的列表）
     * @param reportCode 模板code
     * @param reportId   打印模板id
     * @param companyId  归属公司主键
     * @return PDF文件路径
     * @author 波波
     */
    @Override
    public String print(List<Long> ids, String reportCode, Long reportId, Long companyId, String printType) {
        if (CollUtil.isEmpty(ids)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }

        // 查询所有合同数据
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(saleContractDOList)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }

        // 使用第一个合同的数据作为基础
        SaleContractDO firstContract = saleContractDOList.get(0);

        // 收集所有合同的客户名称、客户合同号、收款方式
        Set<String> custNameSet = new LinkedHashSet<>();
        Set<String> custPoSet = new LinkedHashSet<>();
        Set<String> settlementNameSet = new LinkedHashSet<>();

        // 收集所有合同的明细
        List<SaleContractItem> allItems = new ArrayList<>();
        for (SaleContractDO contract : saleContractDOList) {
            // 收集客户名称
            if (StrUtil.isNotBlank(contract.getCustName())) {
                custNameSet.add(contract.getCustName());
            }
            // 收集客户合同号
            if (StrUtil.isNotBlank(contract.getCustPo())) {
                custPoSet.add(contract.getCustPo());
            }
            // 收集收款方式
            if (StrUtil.isNotBlank(contract.getSettlementName())) {
                settlementNameSet.add(contract.getSettlementName());
            }

            // 获取合同明细
            List<SaleContractItem> items = saleContractItemMapper.selectList(
                SaleContractItem::getContractId, contract.getId()
            );
            if (CollUtil.isNotEmpty(items)) {
                allItems.addAll(items);
            }
        }

        if (CollUtil.isEmpty(allItems)) {
            throw exception(SALECONTRACTITEM_NULL);
        }

        ReportDTO reportDTO;
        if (reportId != null) {
            reportDTO = reportApi.getReportById(reportId);
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
        HashMap<String, Object> params;
        
        // 根据printType判断使用哪种打印参数
        if ("hsCode".equals(printType)) {
            params = getWordParamsHsCode(firstContract, allItems);
        } else {
            params = getWordParams(firstContract, allItems);
        }

        // 使用逗号拼接的客户名称、客户合同号、收款方式
        String custNameMerged = custNameSet.stream()
                    .collect(Collectors.joining(", "));

        String custPoMerged = custPoSet.stream()
            .collect(Collectors.joining(", "));

        String settlementNameMerged = settlementNameSet.stream()
            .collect(Collectors.joining(", "));

        params.put("custName", custNameMerged);
        params.put("custPO", custPoMerged);
        params.put("settlementName", settlementNameMerged);

        // 生成文件名（单个或批量）
        String fileName = saleContractDOList.size() > 1 ? "批量合同_" + firstContract.getCode() : firstContract.getCode();
        if ("hsCode".equals(printType)) {
            fileName = fileName + "_海关编码";
        }

        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params, fileName);

        if (StrUtil.isNotEmpty(result)) {
            // 更新所有合同的打印状态
            for (SaleContractDO contract : saleContractDOList) {
                SaleContractDO updateDO = new SaleContractDO();
                updateDO.setId(contract.getId());
                updateDO.setPrintFlag(BooleanEnum.YES.getValue());
                int printTimes = Objects.isNull(contract.getPrintTimes()) ? CalculationDict.ZERO : contract.getPrintTimes();
                updateDO.setPrintTimes(printTimes + 1);
                saleContractMapper.updateById(updateDO);

                // 记录操作日志
                OperateLogUtils.addOperateLog(SaleDict.OPERATOR_EXTS_KEY, contract.getCode(), saleContractDOList.size() > 1 ? "批量打印" : "打印");
            }
        }
        return result;
    }

    /**
     * 打印内销合同（支持单个或批量）
     *
     * @param ids 内销合同ID列表（单个打印时传入只有一个元素的列表）
     * @param reportCode 模板code
     * @param reportId   打印模板id
     * @param companyId  归属公司主键
     * @return PDF文件路径
     * @author 波波
     */
    @Override
    public String domesticPrint(List<Long> ids, String reportCode, Long reportId, Long companyId) {
        if (CollUtil.isEmpty(ids)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }

        // 查询所有合同数据
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(saleContractDOList)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }

        // 使用第一个合同的数据作为基础
        SaleContractDO firstContract = saleContractDOList.get(0);

        // 收集所有合同的客户名称、客户合同号、收款方式
        Set<String> custNameSet = new LinkedHashSet<>();
        Set<String> custPoSet = new LinkedHashSet<>();
        Set<String> settlementNameSet = new LinkedHashSet<>();

        // 收集所有合同的明细
        List<SaleContractItem> allItems = new ArrayList<>();
        for (SaleContractDO contract : saleContractDOList) {
            // 收集客户名称
            if (StrUtil.isNotBlank(contract.getCustName())) {
                custNameSet.add(contract.getCustName());
            }
            // 收集客户合同号
            if (StrUtil.isNotBlank(contract.getCustPo())) {
                custPoSet.add(contract.getCustPo());
            }
            // 收集收款方式
            if (StrUtil.isNotBlank(contract.getSettlementName())) {
                settlementNameSet.add(contract.getSettlementName());
            }

            // 获取合同明细
            List<SaleContractItem> items = saleContractItemMapper.selectList(
                SaleContractItem::getContractId, contract.getId()
            );
            if (CollUtil.isNotEmpty(items)) {
                allItems.addAll(items);
            }
        }

        if (CollUtil.isEmpty(allItems)) {
            throw exception(SALECONTRACTITEM_NULL);
        }

        ReportDTO reportDTO;
        if (reportId != null) {
            reportDTO = reportApi.getReportById(reportId);
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
        HashMap<String, Object> params = getDomesticWordParams(firstContract, allItems);

        // 使用逗号拼接的客户名称、客户合同号、收款方式，并进行安全处理
        String custNameMerged = custNameSet.stream()
            .map(name -> name.replaceAll("[\\r\\n\\t]", " ").trim())
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.joining(", "));

        String custPoMerged = custPoSet.stream()
            .map(po -> po.replaceAll("[\\r\\n\\t]", " ").trim())
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.joining(", "));

        String settlementNameMerged = settlementNameSet.stream()
            .map(name -> name.replaceAll("[\\r\\n\\t]", " ").trim())
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.joining(", "));

        // 限制合并字符串长度，避免模板处理问题
        if (custNameMerged.length() > 500) {
            custNameMerged = custNameMerged.substring(0, 500) + "...";
        }
        if (custPoMerged.length() > 500) {
            custPoMerged = custPoMerged.substring(0, 500) + "...";
        }
        if (settlementNameMerged.length() > 500) {
            settlementNameMerged = settlementNameMerged.substring(0, 500) + "...";
        }

        params.put("custName", custNameMerged);
        params.put("custPO", custPoMerged);
        params.put("settlementName", settlementNameMerged);

        // 生成文件名（单个或批量）
        String fileName = saleContractDOList.size() > 1 ? "批量合同_" + firstContract.getCode() : firstContract.getCode();

        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params, fileName);

        if (StrUtil.isNotEmpty(result)) {
            // 更新所有合同的打印状态
            for (SaleContractDO contract : saleContractDOList) {
                SaleContractDO updateDO = new SaleContractDO();
                updateDO.setId(contract.getId());
                updateDO.setPrintFlag(BooleanEnum.YES.getValue());
                int printTimes = Objects.isNull(contract.getPrintTimes()) ? CalculationDict.ZERO : contract.getPrintTimes();
                updateDO.setPrintTimes(printTimes + 1);
                saleContractMapper.updateById(updateDO);

                // 记录操作日志
                OperateLogUtils.addOperateLog(SaleDict.OPERATOR_EXTS_KEY, contract.getCode(), saleContractDOList.size() > 1 ? "批量打印" : "打印");
            }
        }
        return result;
    }

    @Override
    public PageResult<StockDTO> getItemStock(ItemStockReq req) {
        String contractCode = req.getSaleContractCode();
        String cskuCode = req.getCskuCode();
        String skuCode = req.getSkuCode();
        Integer pageNo = req.getPageNo();
        Integer pageSize = req.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        if (StrUtil.isEmpty(contractCode)) {
            return PageResult.empty();
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().select(SaleContractItem::getSkuCode).eq(SaleContractItem::getContractCode, contractCode));
        if (CollUtil.isEmpty(saleContractItemList)) {
            return PageResult.empty();
        }
        List<String> skuCodeList = saleContractItemList.stream().map(SaleContractItem::getSkuCode).distinct().toList();
        List<StockDTO> stockList = stockApi.getStockListBySkuCode(skuCodeList);
        if (CollUtil.isEmpty(stockList)) {
            return PageResult.empty();
        }
        List<StockDTO> totalList = stockList.stream().filter(s -> {
            boolean skuCodeFlag = true;
            boolean cskuCodeFlag = true;

            if (StrUtil.isNotEmpty(skuCode)) {
                skuCodeFlag = skuCode.equals(s.getSkuCode());
            }
            if (StrUtil.isNotEmpty(cskuCode)) {
                cskuCodeFlag = cskuCode.equals(s.getCskuCode());
            }
            return skuCodeFlag && cskuCodeFlag;
        }).toList();
        if (CollUtil.isEmpty(totalList)) {
            return PageResult.empty();
        }
        List<StockDTO> resultList = totalList.stream().skip(skip).limit(pageSize).toList();
        return new PageResult<StockDTO>().setList(resultList).setTotal((long) totalList.size());
    }

    @Override
    public List<SimpleContractRespVO> getSimpleContractRespVoList(String contractCode) {
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getCode, SaleContractDO::getCustCode, SaleContractDO::getCustName).like(SaleContractDO::getCode, contractCode));
        if (CollUtil.isEmpty(saleContractDOList)) {
            return List.of();
        }
        return BeanUtils.toBean(saleContractDOList, SimpleContractRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataPermission(enable = false)
    public Map<Long, Map<String, JsonAmount>> generateContract(List<SaleContractItem> saleContractItems, Map<Long, SaleContractDO> saleContractDOMap, Map<Long, Tuple> realPurchasePriceCollect, String genContractUniqueCode) {
        Map<Long, Map<String, JsonAmount>> lastNodeSkuPurchasePriceMap = new HashMap<>();
        List<SaleContractItemAllocation> saleContractItemAllocationList = new ArrayList<>();
        // 过滤销售合同明细中采购单价为空、采购数量非 0 的数据
        List<SaleContractItem> filteredSaleContractItemList = saleContractItems.stream().filter(x -> {
            return (Objects.nonNull(x.getUnitPrice().getAmount()) && !NumUtil.bigDecimalsEqual(x.getUnitPrice().getAmount(), BigDecimal.ZERO)) || x.getQuantity() > 0;
        }).toList();
        Map<Long, List<JsonCompanyPath>> companyPathMap = new HashMap<>();
        saleContractDOMap.forEach((k, v) -> {
            companyPathMap.put(k, TransformUtil.convertJsonCompanyPathToList(v.getCompanyPath()));
        });
        if (CollUtil.isEmpty(companyPathMap)) {
            throw exception(SALE_COMPANY_PATH_EMPTY);
        }
        List<String> contractCodeList = saleContractItems.stream().map(SaleContractItem::getContractCode).distinct().toList();
        // 当前外销合同中所有的锁定库存
        Map<Long, Map<String, Integer>> stockLockCollect = new HashMap<>();
        List<StockLockRespVO> stockLockRespVOS = stockApi.getStockLockRespVOBySaleContractCodes(contractCodeList);
        if (!CollectionUtils.isEmpty(stockLockRespVOS)) {
            Map<Long, List<StockLockRespVO>> stockMapByContract = stockLockRespVOS.stream().collect(Collectors.groupingBy(StockLockRespVO::getSaleContractId));
            stockMapByContract.forEach((k, v) -> {
                stockLockCollect.put(k, v.stream().collect(Collectors.toMap(x -> x.getSkuCode() + "&" + x.getCompanyId() + "&" + x.getSaleContractItemId(), StockLockRespVO::getLockQuantity, Integer::sum)));
            });
        }
        Map<String, LocalDateTime> saleDateMap = saleContractDOMap.values().stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getSaleContractDate,(s1,s2)->s1));
        Map<String, Map<String, BigDecimal>> rateMapByDateMap = rateApi.getDailyRateMapByDate(saleDateMap);


        if (CollUtil.isEmpty(rateMapByDateMap)) {
            throw exception(RATE_NOT_EXISTS);
        }
        companyPathMap.forEach((k, v) -> {
            // 根据配置规则计算明细单价
            List<JsonCompanyPath> sortedList = v.stream().sorted(Comparator.comparing(JsonCompanyPath::getLevel)).toList();

            SaleContractDO saleContractDO = saleContractDOMap.get(k);
            if (Objects.isNull(saleContractDO)) {
                return;
            }
            Map<String, JsonAmount> priceMap = new HashMap<>();
            for (int i = 0; i < sortedList.size(); i++) {
                // 当前节点
                JsonCompanyPath currentCompanyPath = sortedList.get(i);
                Long currentPathId = currentCompanyPath.getId();
                // 下个节点
                JsonCompanyPath nextCompanyPath = new JsonCompanyPath();
                Long nextPathCompanyId;
                AtomicBoolean isReturn = new AtomicBoolean(false);
                String nextPathCompanyName = CommonDict.HYPHEN;
                if (i != sortedList.size() - 1) {
                    nextCompanyPath = sortedList.get(i + 1);
                    nextPathCompanyId = nextCompanyPath.getId();
                    SimpleCompanyDTO simpleCompanyDTO = companyApi.getCompanyDTO(nextPathCompanyId);
                    if (!Objects.isNull(simpleCompanyDTO)) {
                        nextPathCompanyName = simpleCompanyDTO.getCompanyName();
                    }
                } else {
                    nextPathCompanyId = null;
                    isReturn.set(true);
                }

                // 采购合同
                SavePurchaseContractReqVO savePurchaseContractReqVO = BeanUtils.toBean(saleContractDO, SavePurchaseContractReqVO.class);
                savePurchaseContractReqVO.setId(null);
                savePurchaseContractReqVO.setGenContractUniqueCode(genContractUniqueCode);
                savePurchaseContractReqVO.setCompanyId(currentPathId);
                savePurchaseContractReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                savePurchaseContractReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
                savePurchaseContractReqVO.setSaleContractId(saleContractDO.getId());
                savePurchaseContractReqVO.setSaleContractCode(saleContractDO.getCode());
                // 根据采购公司主键查询内部供应商，补全供应商相关信息
                if (!isReturn.get()) {
                    SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(nextPathCompanyId);
                    if (ObjectUtil.isNotNull(simpleVenderRespDTO)) {
                        Long venderId = simpleVenderRespDTO.getId();
                        String venderCode = simpleVenderRespDTO.getCode();

                        savePurchaseContractReqVO.setVenderId(venderId);
                        savePurchaseContractReqVO.setVenderCode(venderCode);
                        List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                        if (CollUtil.isNotEmpty(taxMsg)){
                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s-> {
                                savePurchaseContractReqVO.setCurrency(s.getCurrency());
                            });
                        }
                    }
                }
                Long loginUserId = WebFrameworkUtils.getLoginUserId();
                UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
                if (ObjectUtil.isNotNull(userDept)) {
                    savePurchaseContractReqVO.setPurchaseUserId(userDept.getUserId());
                    savePurchaseContractReqVO.setPurchaseUserName(userDept.getNickname());
                    savePurchaseContractReqVO.setPurchaseUserDeptId(userDept.getDeptId());
                    savePurchaseContractReqVO.setPurchaseUserDeptName(userDept.getDeptName());
                }

                savePurchaseContractReqVO.setDeliveryDate(LocalDateTime.now());
                savePurchaseContractReqVO.setPurchaseTime(LocalDateTime.now());

                // 销售合同
                SaleContractSaveReqVO saleContractSaveReqVO = BeanUtils.toBean(saleContractDO, SaleContractSaveReqVO.class);
                saleContractSaveReqVO.setSourceContractId(saleContractSaveReqVO.getId());
                saleContractSaveReqVO.setSourceContractCode(saleContractSaveReqVO.getCode());
                saleContractSaveReqVO.setGenContractUniqueCode(genContractUniqueCode);
                saleContractSaveReqVO.setId(null);
                saleContractSaveReqVO.setCompanyId(nextPathCompanyId);
                saleContractSaveReqVO.setCompanyName(nextPathCompanyName);

                SimpleCompanyDTO apiCompanyDTO = companyApi.getCompanyDTO(currentPathId);
                if (ObjectUtil.isNotNull(apiCompanyDTO)) {
                    if (apiCompanyDTO.getCompanyNature().intValue() == CompanyNatureEnum.INTERNAL_CUST.getValue()) {
                        saleContractSaveReqVO.setSaleType(SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue());
                    } else {
                        saleContractSaveReqVO.setSaleType(SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue());
                    }
                }
                saleContractSaveReqVO.setStatus(SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue());
                saleContractSaveReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                // 根据当前路径主键查询客户，补全客户相关信息
                SimpleCustRespDTO simpleCustRespDTO = custApi.getSimpleCustByInternalCompanyId(currentPathId);
                if (ObjectUtil.isNull(simpleCustRespDTO)) {
                    throw exception(INTERNAL_CUST_NOT_EXISTS,currentCompanyPath.getName());
                }
                saleContractSaveReqVO.setInternalCustId(simpleCustRespDTO.getId());
                saleContractSaveReqVO.setInternalCustCode(simpleCustRespDTO.getCode());
                saleContractSaveReqVO.setInternalCustName(simpleCustRespDTO.getName());

                saleContractSaveReqVO.setCustId(simpleCustRespDTO.getId());
                saleContractSaveReqVO.setCustCode(simpleCustRespDTO.getCode());
                saleContractSaveReqVO.setCustName(simpleCustRespDTO.getName());
                int finalI = i;
                filteredSaleContractItemList.forEach(saleContractItem -> {
                    String skuCode = saleContractItem.getSkuCode();
                    // 获取合同明细真实采购单价
                    Long saleContractItemId = saleContractItem.getId();
                    Tuple tuple = realPurchasePriceCollect.get(saleContractItemId);
//                    if (Objects.nonNull(tuple)) {
//                        saleContractItem.setRealPurchaseWithTaxPrice(tuple.get(0));
//                    }
                    // 分配后的采购单价
                    JsonAmount contractItemPrice = allocateByCondition(saleContractItem, currentCompanyPath, rateMapByDateMap.get(saleContractItem.getContractCode()));
                    JsonAmount contractPrice = changeCurrency(contractItemPrice, simpleCustRespDTO.getCurrency());
                    saleContractItem.setUnitPrice(contractPrice);
                    if (finalI == sortedList.size() - 1) {
                        // 末尾节点公司的各产品的销售单价
                        priceMap.put(skuCode, contractPrice);
                        return;
                    }
//                SkuDTO skuDTO = skuDTOMap.get(skuCode);
//                Integer lockQuantity = finalStockLockCollect.get(skuCode + "&" + currentPathId + "&" + saleContractItem.getId());
                    saleContractItem.setCurrentLockQuantity(0);
//                Integer sourceSkuLockQuantity = finalStockLockCollect.get(skuDTO.getSourceCode() + "&" + currentPathId+ "&" + saleContractItem.getId());
//                Integer tmpQuantity = skuQutity.get(skuCode);
//                if (ObjectUtil.isNull(tmpQuantity)) {
//                    tmpQuantity = saleContractItem.getQuantity();
//                }
                    //过程中计算库存数量
//                Integer quantity = NumberUtil.sub(tmpQuantity, lockQuantity, sourceSkuLockQuantity).intValue();
//                Integer quantity = tmpQuantity;
//                saleContractItem.setQuantity(1);
//                skuQutity.put(skuCode, quantity);

                    // 产品分配利润信息
                    SaleContractItemAllocation saleContractItemAllocation = new SaleContractItemAllocation();
                    saleContractItemAllocation.setPurchaseCompanyId(currentPathId);
                    SimpleCompanyDTO simpleCompanyDTO = companyApi.getCompanyDTO(currentPathId);
                    if (!Objects.isNull(simpleCompanyDTO)) {
                        saleContractItemAllocation.setPurchaseCompanyName(simpleCompanyDTO.getCompanyName());
                    }
                    saleContractItemAllocation.setSaleCompanyId(nextPathCompanyId);
                    SimpleCompanyDTO simpleCompanyDTONext = companyApi.getCompanyDTO(currentPathId);
                    if (!Objects.isNull(simpleCompanyDTONext)) {
                        saleContractItemAllocation.setSaleCompanyName(simpleCompanyDTONext.getCompanyName());
                    }
                    saleContractItemAllocation.setSaleContractItemId(saleContractItemId);
                    saleContractItemAllocationList.add(saleContractItemAllocation);
                });
                if (isReturn.get()) {
                    return;
                }
                // 生成采购合同
                List<SavePurchaseContractItemReqVO> savePurchaseContractItemReqVOList = new ArrayList<>();
                filteredSaleContractItemList.forEach(x -> {
                    if (!Objects.equals(x.getContractId(), k)) {
                        return;
                    }
                    SavePurchaseContractItemReqVO purchasecontractItemReqVO = BeanUtils.toBean(x, SavePurchaseContractItemReqVO.class);
                    purchasecontractItemReqVO.setId(null);
                    purchasecontractItemReqVO.setPurchaseType(PurchaseTypeEnum.STANDARD.getCode());
                    // 当前销售单价为上个路径中的采购单价
                    JsonAmount withTaxPrice = changeCurrency(x.getUnitPrice(), savePurchaseContractReqVO.getCurrency());
                    if (Objects.nonNull(withTaxPrice)) {
                        BigDecimal unitPiceAmount = withTaxPrice.getAmount().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(purchasecontractItemReqVO.getTaxRate()), 2, RoundingMode.HALF_UP);
                        purchasecontractItemReqVO.setUnitPrice(new JsonAmount().setAmount(unitPiceAmount).setCurrency(withTaxPrice.getCurrency()));
                        purchasecontractItemReqVO.setWithTaxPrice(withTaxPrice);
                    }
                    purchasecontractItemReqVO.setTotalPrice(x.getTotalSaleAmount());
                    purchasecontractItemReqVO.setSaleContractItemId(x.getId());
                    purchasecontractItemReqVO.setSaleContractId(x.getContractId());
                    purchasecontractItemReqVO.setSaleContractCode(x.getContractCode());
                    purchasecontractItemReqVO.setCustId(saleContractDO.getCustId());
                    purchasecontractItemReqVO.setSourceUniqueCode(x.getUniqueCode());
                    purchasecontractItemReqVO.setUniqueCode(IdUtil.fastSimpleUUID());
                    purchasecontractItemReqVO.setSaleItemUniqueCode(x.getUniqueCode());
                    UserDept purchaseUser = x.getPurchaseUser();
                    if (ObjectUtil.isNotNull(purchaseUser)) {
                        purchasecontractItemReqVO.setPurchaseUserId(purchaseUser.getUserId());
                        purchasecontractItemReqVO.setPurchaseUserName(purchaseUser.getNickname());
                        purchasecontractItemReqVO.setPurchaseUserDeptId(purchaseUser.getDeptId());
                        purchasecontractItemReqVO.setPurchaseUserDeptName(purchaseUser.getDeptName());
                    }
                    savePurchaseContractItemReqVOList.add(purchasecontractItemReqVO);
                });
                filteredSaleContractItemList.stream().map(SaleContractItem::getPurchaseUser).filter(Objects::nonNull).findFirst().ifPresent(s->{
                    savePurchaseContractReqVO.setPurchaseUserId(s.getUserId());
                    savePurchaseContractReqVO.setPurchaseUserName(s.getNickname());
                    savePurchaseContractReqVO.setPurchaseUserDeptId(s.getDeptId());
                    savePurchaseContractReqVO.setPurchaseUserDeptName(s.getDeptName());
                });
                if (CollUtil.isNotEmpty(savePurchaseContractItemReqVOList)) {
                    savePurchaseContractItemReqVOList.stream().map(s -> {
                        JsonAmount withTaxPrice = s.getWithTaxPrice();
                        Integer quantity = s.getQuantity();
                        if (Objects.isNull(withTaxPrice) || Objects.isNull(withTaxPrice.getAmount())) {
                            return BigDecimal.ZERO;
                        }
                        return NumUtil.mul(withTaxPrice.getAmount(), BigDecimal.valueOf(quantity));
                    }).reduce(BigDecimal::add).ifPresent(s -> savePurchaseContractReqVO.setTotalAmount(new JsonAmount().setAmount(s).setCurrency(savePurchaseContractReqVO.getCurrency())));
                    savePurchaseContractReqVO.setChildren(savePurchaseContractItemReqVOList);
                }
                savePurchaseContractReqVO.setAutoFlag(BooleanEnum.YES.getValue());
                // 生成销售合同
                List<SaleContractItem> saleContractSaveReqVOList = BeanUtils.toBean(filteredSaleContractItemList, SaleContractItem.class);
                Map<String, String> uniqueMap = new HashMap<>();
                saleContractSaveReqVOList.forEach(x -> {
                    if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                        uniqueMap.put(x.getUniqueCode(), IdUtil.fastSimpleUUID());
                    }
                });
                saleContractSaveReqVOList.forEach(x -> {
                    x.setId(null);
                    x.setRealPurchaseQuantity(x.getQuantity());
                    if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                        String uniqueCode = uniqueMap.get(x.getUniqueCode());
                        x.setUniqueCode(uniqueCode);
                        x.setSourceUniqueCode(uniqueCode);
                    } else {
                        String uniqueCode = uniqueMap.get(x.getSourceUniqueCode());
                        x.setUniqueCode(IdUtil.fastSimpleUUID());
                        x.setSourceUniqueCode(uniqueMap.get(x.getSourceUniqueCode()));
                    }
                    JsonAmount unitPrice = x.getUnitPrice();
                    x.setUnitPrice(changeCurrency(unitPrice, simpleCustRespDTO.getCurrency()));
                });
                List<SaleContractItem> saveChildren = saleContractSaveReqVOList.stream().filter(s -> Objects.equals(s.getContractId(), k) && s.getQuantity() > 0).toList();
                saleContractSaveReqVO.setChildren(saveChildren);
                saleContractSaveReqVO.setAutoFlag(BooleanEnum.YES.getValue());
                saleContractSaveReqVO.setCheckFlag(Boolean.FALSE);
                saleContractSaveReqVO.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
                saleContractSaveReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                logger.info("节点路径-生成销售合同：{}", JsonUtils.toJsonString(saleContractSaveReqVO));
                List<CreatedResponse> createdResponses = this.createSaleContract(saleContractSaveReqVO);
                if (CollUtil.isNotEmpty(createdResponses)){
                    createdResponses.stream().findFirst().ifPresent(s->{
                        savePurchaseContractReqVO.setSaleContractCode(s.getCode());
                    });
                }
                logger.info("节点路径-生成采购合同：{}", JsonUtils.toJsonString(savePurchaseContractReqVO));
                if (StrUtil.isEmpty(savePurchaseContractReqVO.getVenderCode())) {
                    throw exception(PURCHASE_PLAN_COMPANY_NO_VENDER_ERROR);
                }
                purchaseContractApi.genPurchaseContract(savePurchaseContractReqVO);
            }
            lastNodeSkuPurchasePriceMap.put(k, priceMap);
        });

        return lastNodeSkuPurchasePriceMap;
    }

    /**
     * 根据币种税率转换币种金额
     *
     * @param amount   原金额
     * @param currency 目标币种
     * @return 转换后金额
     */
    private JsonAmount changeCurrency(JsonAmount amount, String currency) {
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        return CurrencyUtil.changeCurrency(amount, currency, dailyRateMap);
    }

    /**
     * 根据条件进行分配利润，根据配置条件与值获取合同明细-单价
     *
     * @param saleContractItem
     * @param currentCompanyPath
     */
    private JsonAmount allocateByCondition(SaleContractItem saleContractItem, JsonCompanyPath currentCompanyPath, Map<String, BigDecimal> dailyRateMap) {
        BigDecimal contractItemPrice;
        JsonAmount unitPrice = saleContractItem.getUnitPrice();
        if (Objects.isNull(unitPrice)) {
            throw exception(SALE_CONTRACT_ITEM_UNIT_PRICE_NULL);
        }
        // 真实采购单价
        JsonAmount realPurchaseWithTaxPrice = saleContractItem.getRealPurchaseWithTaxPrice();
        // 采购币种
        String purchaseCurrency = realPurchaseWithTaxPrice.getCurrency();
        unitPrice = changeCurrency(unitPrice, purchaseCurrency);
        // 明细-销售单价
        BigDecimal salePrice = unitPrice.getAmount();
        // 明细-采购单价
        BigDecimal realPurchasePrice = realPurchaseWithTaxPrice.getAmount();
        // 明细-利润
        BigDecimal profit = NumberUtil.sub(salePrice, realPurchasePrice);
        // 明细-利润率
        BigDecimal profitRatio = NumberUtil.mul(NumberUtil.div(profit, salePrice.compareTo(BigDecimal.ZERO) == 0 ? 1 : salePrice), 100);
        // 分配条件-值
        BigDecimal conditionValue = BigDecimal.valueOf(currentCompanyPath.getAllocateConditionValue());
        // 分配条件
        AllocateConditionTypeEnum allocateConditionTypeEnum = EnumUtil.likeValueOf(AllocateConditionTypeEnum.class, currentCompanyPath.getAllocateConditionType());
        Boolean flag = false;
        switch (currentCompanyPath.getAllocateConditionType()) {
            case 1 -> flag = true;
            case 2 -> flag = profitRatio.compareTo(conditionValue) > CalculationDict.ZERO;
            case 3 -> flag = profitRatio.compareTo(conditionValue) >= CalculationDict.ZERO;
            case 4 -> flag = profitRatio.compareTo(conditionValue) < CalculationDict.ZERO;
            case 5 -> flag = profitRatio.compareTo(conditionValue) <= CalculationDict.ZERO;
        }
        if (flag) {
            contractItemPrice = allocateByProfitRatio(currentCompanyPath, profitRatio, realPurchasePrice, salePrice);
        } else {
            contractItemPrice = salePrice;
        }
        // 将金额转为人民币
        return new JsonAmount().setAmount(contractItemPrice).setCurrency(purchaseCurrency);
    }

    /**
     * 根据利润率分配利润，计算合同明细-单价
     *
     * @param currentCompanyPath
     * @param profitRatio
     * @param purchasePrice
     * @param salePrice
     * @return
     */
    private BigDecimal allocateByProfitRatio(JsonCompanyPath currentCompanyPath, BigDecimal profitRatio, BigDecimal purchasePrice, BigDecimal salePrice) {
        BigDecimal contractItemPrice = BigDecimal.ZERO;
        AllocateTypeEnum allocateTypeEnum = EnumUtil.likeValueOf(AllocateTypeEnum.class, currentCompanyPath.getAllocateType());
        switch (allocateTypeEnum) {
            case NONE -> contractItemPrice = salePrice;
            case FIX_RATIO -> {
                BigDecimal fixRatio = BigDecimal.valueOf(currentCompanyPath.getFixRatio());
                // 节点利润率
                BigDecimal surplusProfitRatio = NumberUtil.sub(profitRatio, fixRatio);
                // 节点利润
                BigDecimal surplusProfit = NumberUtil.mul(salePrice, NumberUtil.div(surplusProfitRatio, BigDecimal.valueOf(100)));
                // 明细-单价（采购&销售）
                contractItemPrice = NumberUtil.add(surplusProfit, purchasePrice).setScale(6, RoundingMode.HALF_UP);
            }
            case RANDOM_RATIO -> {
                BigDecimal randomRatio = RandomUtil.randomBigDecimal(BigDecimal.valueOf(currentCompanyPath.getRangeMinRatio()), BigDecimal.valueOf(currentCompanyPath.getRangeMaxRatio()));
                // 剩余利润率
                BigDecimal surplusProfitRatio = NumberUtil.div(NumberUtil.sub(profitRatio, randomRatio), 100);
                // 明细-单价（采购&销售）
                contractItemPrice = NumberUtil.add(NumberUtil.mul(salePrice, surplusProfitRatio), purchasePrice).setScale(6, RoundingMode.HALF_UP);
            }
        }
        return contractItemPrice;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceListBySaleContractId(Long id) {
        SaleContractDO saleContractDO = validateSaleContractExists(id);
        String code = saleContractDO.getCode();
        List<ConfirmSourceEntity> confirmSourceList = new ArrayList<>();
        // 获取销售合同变更来源信息
        List<ConfirmSourceEntity> custList = custApi.getConfirmSourceList(code, EffectRangeEnum.SMS.getValue());
        if (CollUtil.isNotEmpty(custList)) {
            confirmSourceList.addAll(custList);
        }
        List<ConfirmSourceEntity> venderList = venderApi.getConfirmSourceList(code, EffectRangeEnum.SMS.getValue());
        if (CollUtil.isNotEmpty(venderList)) {
            confirmSourceList.addAll(venderList);
        }
        List<ConfirmSourceEntity> skuList = skuApi.getConfirmSourceList(code, EffectRangeEnum.SMS.getValue());
        if (CollUtil.isNotEmpty(skuList)) {
            confirmSourceList.addAll(skuList);
        }
        return confirmSourceList;
    }

    /**
     * 处理子项变更
     *
     * @param oldList            变更前列表
     * @param list               变更后列表
     * @param tarClazz           目标类型
     * @param saleContractChange 销售合同变更实体
     * @param formChangeDTOList  变更字段配置缓存
     * @param submitFlag         提交标识
     * @param <T>                变更对象类型
     * @return 变更后列表
     */
    private <T extends ChangeExInterface> Tuple dealChangeField(List<T> oldList, List<T> list, Class<T> tarClazz, String tableName, SaleContractChange saleContractChange, Map<String, FormChangeDTO> formChangeDTOList, AtomicReference<Integer> submitFlag) {
        List<DiffRecord<T>> diffRecords = DiffUtil.compareLists(oldList, list);
        Tuple tuple = new ChangeCompareUtil<T>().transformChildList(diffRecords, tarClazz);
        // 明细中变更字段
        Set<String> fieldList = tuple.get(1);
        if (Objects.isNull(submitFlag.get()) || SubmitFlagEnum.ONLY_SAVE.getStatus().equals(submitFlag.get())) {
            // 比对销售明细字段
            submitFlag.set(formChangeApi.dealShipmentTable(fieldList, formChangeDTOList, saleContractChange, tableName, submitFlag, false).get());
        }
        return tuple;
    }

    @Override
    public List<Long> getSaleContractIdsByCustCode(String custCode) {
//        List<SaleContractItem> saleContractItemList = saleContractItemMapper.getSaleContractItemListByCustCode(custCode);
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(SaleContractDO::getCustCode, custCode);
        if (CollUtil.isEmpty(saleContractDOS)) {
            return null;
        }
        List<Long> contractIds = saleContractDOS.stream().map(SaleContractDO::getId).distinct().toList();
        return contractIds;
    }

    @Override
    public List<Long> getSaleContractIdsBySkuCode(String skuCode) {
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
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.getSaleContractItemListBySkuCodeList(skuCodeList);
        if (saleContractItemList == null) {
            return null;
        }
        List<Long> contractIds = saleContractItemList.stream().map(SaleContractItem::getContractId).distinct().toList();
        return contractIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelatedSaleContractRespVO getRelatedNum(Long id) {
        SaleContractDO saleContractDO = saleContractMapper.selectById(id);
        if (Objects.isNull(saleContractDO)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        String contractCode = saleContractDO.getCode();
        // 销售变更
        CompletableFuture<Long> changeFuture = CompletableFuture.supplyAsync(() -> saleContractChangeMapper.selectCount(SaleContractChange::getSourceCode, contractCode));
        // 采购计划
        CompletableFuture<Long> purchasePlanFuture = CompletableFuture.supplyAsync(() -> purchasePlanApi.getOrderNumBySaleContractId(id));
        // 采购合同
        CompletableFuture<Long> purchaseContractFuture = CompletableFuture.supplyAsync(() -> purchaseContractApi.getOrderNumBySaleContractId(false, id));
        // 出运计划
        CompletableFuture<Long> shipmentPlanFuture = CompletableFuture.supplyAsync(() -> shipmentApi.getPlanNumBySaleContractCode(contractCode));
        // 出运明细
        CompletableFuture<Long> shipmentFuture = CompletableFuture.supplyAsync(() -> shipmentApi.getOrderNumBySaleContractCode(contractCode));
        // 收款账单
//        CompletableFuture<Long> receiveFuture = CompletableFuture.supplyAsync(() -> receiptApi.getOrderNumByBusinessCode(BusinessTypeEnum.GENERAL_APPcontractCode));
        // 辅料采购合同
        CompletableFuture<Long> accessoryPurchaseContractFuture = CompletableFuture.supplyAsync(() -> purchaseContractApi.getOrderNumBySaleContractId(true, id));
        RelatedSaleContractRespVO relatedSaleContractRespVO = new RelatedSaleContractRespVO();
        try {
            relatedSaleContractRespVO.setSaleContractChangeNum(changeFuture.get())
                    //                .setCollectionNum()
                    .setShipmentNum(shipmentFuture.get())
                    .setPurchaseContractNum(purchaseContractFuture.get())
                    .setPurchasePlanNum(purchasePlanFuture.get())
                    .setAuxiliaryPurchaseContractNum(accessoryPurchaseContractFuture.get())
                    .setShipmentPlanNum(shipmentPlanFuture.get());
        } catch (Exception e) {
            throw exception(RELATED_NUM_ERROR);
        }
        return relatedSaleContractRespVO;
    }

    @Override
    public void updatePurchaseDesignContract(SaleContractDesignSaveReqVO updateReqVO) {
        SaleContractDO saleContractDO = saleContractMapper.selectById(updateReqVO.getId());
        if (Objects.isNull(saleContractDO)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        saleContractDO.setDesignDraftList(updateReqVO.getDesignDraftList());
        saleContractMapper.updateById(saleContractDO);
    }

    @Override
    public Map<Long, SaleContractItem> getItemListByItemIds(List<Long> itemIdList) {
        List<SaleContractItem> itemList = saleContractItemMapper.selectList(SaleContractItem::getId, itemIdList);
        return itemList.stream().collect(Collectors.toMap(SaleContractItem::getId, s -> s, (s1, s2) -> s1));
    }

    @Override
    public void batchUpdateBillStatus(List<SaleContractBillSimpleDTO> billSimpleDTOList) {
        if (CollUtil.isEmpty(billSimpleDTOList)) {
            return;
        }
        List<String> list = billSimpleDTOList.stream().map(SaleContractBillSimpleDTO::getUniqueCode).toList();
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, list);
        if (CollUtil.isEmpty(saleContractItemList)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        Map<String, SaleContractItem> itemMap = saleContractItemList.stream().collect(Collectors.toMap(SaleContractItem::getUniqueCode, s -> s));
        List<SaleContractItem> updateItems = billSimpleDTOList.stream().map(s -> {
            SaleContractItem saleContractItem = itemMap.get(s.getUniqueCode());
            if (Objects.isNull(saleContractItem)) {
                throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
            }
            // 已入库数量
            Integer billQuantity = saleContractItem.getBillQuantity();
            // 销售数量
            Integer quantity = saleContractItem.getQuantity();
            // 本次入库数量
            Integer thisBillQuantity = s.getQuantity();
            if (quantity.equals(billQuantity)) {
                saleContractItem.setBillStatus(BillStatusEnum.BILLED.getStatus());
            }
            billQuantity += thisBillQuantity;
            if (quantity.equals(billQuantity)) {
                saleContractItem.setBillStatus(BillStatusEnum.BILLED.getStatus());
            } else {
                saleContractItem.setBillStatus(BillStatusEnum.PART_BILL.getStatus());
            }
            saleContractItem.setAbnormalStatus(s.getAbnormalStatus());
            saleContractItem.setAbnormalRemark(s.getAbnormalRemark());
            return saleContractItem;
        }).toList();
        saleContractItemMapper.updateBatch(updateItems);
    }

    @Override
    public List<CreatedResponse> batchToContractTask(List<Long> saleContractIdList) {
        List<CreatedResponse> responses = new ArrayList<>();
        saleContractIdList.stream().distinct().forEach(s -> {
            responses.addAll(toContract(s));
        });  //复用代码，后期如果效率过慢，重构该方法，减少数据查询
        return responses;
    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        SaleContractDO saleContractDO = validateSaleContractExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(saleContractDO.getCode());
        // 更改单据状态
        saleContractDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = saleContractMapper.updateById(saleContractDO);
        return i > 0;
    }

    @Override
    @DataPermission(enable = false)
    public List<JsonCompanyPath> getCompanyPathBySaleContractCodeList(List<String> saleContractCodes) {
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getCompanyPath).in(SaleContractDO::getCode, saleContractCodes));
        if (CollUtil.isEmpty(saleContractDOS)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        return saleContractDOS.stream().map(SaleContractDO::getCompanyPath).toList();
    }

    @Override
    public SimpleData getSimpleSaleContractData(String sourceSaleContractCode, Long conpanyId) {
        SaleContractDO saleContractDO = saleContractMapper.selectOne(new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getSourceContractCode, sourceSaleContractCode).eq(SaleContractDO::getCompanyId, conpanyId));
        if (Objects.isNull(saleContractDO)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        return BeanUtils.toBean(saleContractDO, SimpleData.class);
    }

    @Override
    public Map<String, SimpleData> getBatchSimpleSaleContractData(List<String> contractCodeList, Long conpanyId, Boolean isSale) {
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getSourceContractCode, contractCodeList).eq(SaleContractDO::getCompanyId, conpanyId));
        if (CollUtil.isEmpty(saleContractDOS)) {
            return Map.of();
        }
        if (isSale) {
            return saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getSourceContractCode, s -> BeanUtils.toBean(s, SimpleData.class)));
        }
        Map<String, String> codeMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getSourceContractCode));
        // 采购信息需从明细拿
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getContractCode, saleContractDOS.stream().map(SaleContractDO::getCode).toList());
        if (CollUtil.isEmpty(saleContractItems)) {
            return Map.of();
        }
        List<Long> saleItemIdList = saleContractItems.stream().map(SaleContractItem::getId).toList();
        Map<Long, String> purchaseContractMap = purchaseContractApi.getPurchaseContractCodeBySaleItemIdList(saleItemIdList, conpanyId);
        Map<String, SimpleData> result = new HashMap<>();
        saleContractItems.forEach(s -> {
            String key = codeMap.get(s.getContractCode());
            if (StrUtil.isEmpty(key) || CollUtil.isEmpty(purchaseContractMap)) {
                return;
            }
            String purchaseCode = purchaseContractMap.get(s.getId());
            result.put(key, new SimpleData().setCode(purchaseCode));
        });
        return result;
    }

    @Override
    public void updateItemWithTaxPriceAndManager(Map<String, JsonAmount> withTaxPriceMap, UserDept manager) {
        if (CollUtil.isEmpty(withTaxPriceMap)) {
            return;
        }
        Set<String> uniqueCodeSet = withTaxPriceMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, uniqueCodeSet);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            JsonAmount jsonAmount = withTaxPriceMap.get(s.getUniqueCode());
            if (Objects.nonNull(jsonAmount)) {
                s.setRealPurchaseWithTaxPrice(jsonAmount);
            }
            s.setManager(manager);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void checkContractStatus(PushOutShipmentPlanReq req) {
        List<Long> childrenIdList = req.getChildrenIdList();
        if (CollUtil.isEmpty(childrenIdList)) {
            throw exception(PARA_CHILDREN_ID_NOT_EXISTS);
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getId, childrenIdList));
        if (CollUtil.isEmpty(saleContractItemList)) {
            throw exception(CONTRACT_ITEM_NOT_EXISTS);
        }
        List<String> uniqueList = saleContractItemList.stream().map(SaleContractItem::getSourceUniqueCode).distinct().toList();
        saleContractItemList = saleContractItemMapper.selectList(SaleContractItem::getSourceUniqueCode, uniqueList);
        saleContractItemList = saleContractItemList.stream().filter(x -> {
            int transferShippedQuantity = Objects.isNull(x.getTransferShippedQuantity()) ? 0 : x.getTransferShippedQuantity();
            return transferShippedQuantity < x.getQuantity();
        }).toList();
        if (CollUtil.isEmpty(saleContractItemList)) {
            throw exception(SALE_CONTRACT_ITEM_ALL_CONVERTED_SHIPMENT);
        }
        // 批量获取销售合同订单信息
        Set<String> contractCodeList = saleContractItemList.stream().map(SaleContractItem::getContractCode).collect(Collectors.toSet());
        if (CollUtil.isEmpty(contractCodeList)) {
            throw exception(SALE_CONTRACT_ITEM_PARENT_NOT_EXISTS);
        }
//        validateCompanyPath(contractCodeList);
    }


    @Override
    @DataPermission(enable = false)
    public void validateCompanyPath(Set<String> contractCodeList) {
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getCode, contractCodeList));
        if (CollUtil.isEmpty(saleContractDOList)) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        // 不同路径的销售合同不可下推出运计划
        AtomicReference<JsonCompanyPath> firstCompanyPath = new AtomicReference<>();
        saleContractDOList.forEach(s -> {
            if (Objects.isNull(firstCompanyPath.get())) {
                firstCompanyPath.set(s.getCompanyPath());
            }
            if (!firstCompanyPath.get().equals(s.getCompanyPath())) {
                throw exception(DIFFERENT_PATH_EXCEPTION);
            }
        });
    }

    @Override
    public Map<String, List<String>> getLinkCodeMap(List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(saleContractCodeList)) {
            return Map.of();
        }
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getLinkCodeList, SaleContractDO::getCode).in(SaleContractDO::getCode, saleContractCodeList));
        if (CollUtil.isEmpty(saleContractDOList)) {
            return Map.of();
        }
        return saleContractDOList.stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getLinkCodeList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateShipmentTotalQuantity(List<ShipmentQuantityDTO> dtoList) {
        List<Long> list = dtoList.stream().map(ShipmentQuantityDTO::getSaleContractItemId).distinct().toList();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, list);
        if (CollUtil.isEmpty(saleContractItems)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        dtoList.forEach(s -> {
            Optional<SaleContractItem> first = saleContractItems.stream().filter(sa -> Objects.equals(sa.getId(), s.getSaleContractItemId())).findFirst();
            if (first.isPresent()) {
                SaleContractItem saleContractItem = first.get();
                if (saleContractItem.getShipmentTotalQuantity() + s.getShippingQuantity() > saleContractItem.getQuantity()) {
                    throw exception(SALE_CONTRACT_SHIPMENT_QUANTITY_OVER);
                }
                saleContractItem.setShipmentTotalQuantity(saleContractItem.getShipmentTotalQuantity() + s.getShippingQuantity());
                saleContractItemMapper.updateById(saleContractItem);
            }
        });
    }

    @Override
    public List<SaleContractItem> getItemListByIds(List<Long> saleIdList) {
        return saleContractItemMapper.selectList(SaleContractItem::getId, saleIdList);
    }

    @Override
    public void updateSaleItemPurchaseFlag(List<Long> itemIds, Integer purchaseFlag) {
        if (CollUtil.isEmpty(itemIds)) {
            return;
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            s.setConvertPurchaseFlag(purchaseFlag);
            if (BooleanEnum.NO.getValue().equals(purchaseFlag)){
                s.setRealPurchaseQuantity(0);
            }
            List<JsonLock> lockMsgs = s.getLockMsg();
            if (CollUtil.isEmpty(lockMsgs)) {
            }
        });
        saleContractItemMapper.updateBatch(saleContractItems);
        if (BooleanEnum.NO.getValue().equals(purchaseFlag)) {
            List<Long> contractIdList = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().toList();
            List<SaleContractItem> allSaleItems = saleContractItemMapper.selectList(SaleContractItem::getContractId, contractIdList);
            Map<Long, List<SaleContractItem>> contractItemMap = allSaleItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
            List<Long> updateList = new ArrayList<>();
            contractItemMap.forEach((k, v) -> {
                boolean allConvertPurchaseFlag = v.stream().allMatch(s -> BooleanEnum.NO.getValue().equals(s.getConvertPurchaseFlag()));
                if (allConvertPurchaseFlag) {
                    updateList.add(k);
                }
            });
            if (CollUtil.isNotEmpty(updateList)) {
                saleContractMapper.update(new LambdaUpdateWrapper<SaleContractDO>().set(SaleContractDO::getStatus, SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue()).in(SaleContractDO::getId, updateList));
            }
        }
        // 重新锁定销售合同库存
        Map<Long, String> contractMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getContractId, SaleContractItem::getContractCode, (o, n) -> o));
        saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId)).forEach((k, v) -> {
            String contractCode = contractMap.get(k);
            dealLockStock(v, k, contractCode, false);
        });
    }

    @Override
    public List<Long> getSaleContractIdsBySaleContractCode(String saleContractCode) {
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(SaleContractDO::getCode, saleContractCode);
        if (CollUtil.isEmpty(saleContractDOS)) {
            return null;
        }
        List<Long> contractIds = saleContractDOS.stream().map(SaleContractDO::getId).distinct().toList();
        return contractIds;
    }

    @Override
    public ChangeEffectRespVO getChangeEffect(SaleContractRespVO changeReqVO) {
        //查询新采购合同与原采购合同
        SaleContractDO saleContractDO = saleContractMapper.selectOne(new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCode, changeReqVO.getCode()).ne(SaleContractDO::getStatus, SaleContractStatusEnum.CASE_CLOSED.getValue()));
        SaleContractRespVO saleContract = getSaleContract(new SaleContractDetailReq().setSaleContractId(saleContractDO.getId()), SaleTabEnum.SALE_DETAIL);
        //初始化变更标记
        final boolean[] changeFlag = {false, false, false};
        Integer submitFlag = BooleanEnum.NO.getValue();
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("sms_sale_contract", "sms_sale_contract_item", "sms_add_sub_term", "sms_collection_plan"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        ChangeEffectRespVO changeEffectRespVO = new ChangeEffectRespVO();
        //采购主表
        Set<String> changeFields = new ChangeCompareUtil<SaleContractRespVO>().transformObject(saleContract, changeReqVO);
        compareTableField(changeFields, formChangeDTOList.get("sms_sale_contract"), changeFlag, submitFlag);

        //采购子表
        List<SaleContractItem> oldItem = saleContract.getChildren();
        List<SaleContractItem> item = changeReqVO.getChildren();
        List<DiffRecord<SaleContractItem>> itemDiffRecords = DiffUtil.compareLists(oldItem, item);
        Tuple itemTuple = new ChangeCompareUtil<SaleContractItem>().transformChildList(itemDiffRecords, SaleContractItem.class);
        compareTableField(itemTuple.get(1), formChangeDTOList.get("sms_sale_contract_item"), changeFlag, submitFlag);

        //客户联系人
        List<AddSubItem> oldAddSubItemList = saleContract.getAddSubItemList();
        List<AddSubItem> addSubItemList = changeReqVO.getAddSubItemList();
        List<DiffRecord<AddSubItem>> addSubItemDiffRecords = DiffUtil.compareLists(oldAddSubItemList, addSubItemList);
        Tuple addSubItemTuple = new ChangeCompareUtil<AddSubItem>().transformChildList(addSubItemDiffRecords, AddSubItem.class);
        compareTableField(addSubItemTuple.get(1), formChangeDTOList.get("sms_add_sub_term"), changeFlag, submitFlag);

        //客户结汇方式
        List<CollectionPlan> oldCollectionPlanList = saleContract.getCollectionPlanList();
        List<CollectionPlan> collectionPlanList = changeReqVO.getCollectionPlanList();
        List<DiffRecord<CollectionPlan>> collectionPlanDiffRecords = DiffUtil.compareLists(oldCollectionPlanList, collectionPlanList);
        Tuple collectionPlanTuple = new ChangeCompareUtil<CollectionPlan>().transformChildList(collectionPlanDiffRecords, CollectionPlan.class);
        compareTableField(collectionPlanTuple.get(1), formChangeDTOList.get("sms_collection_plan"), changeFlag, submitFlag);

        // 处理影响范围
        List<JsonEffectRange> effectRangeList = new ArrayList<>();
        //影响采购字段
        if (changeFlag[1]) {
            List<PurchaseContractDTO> purchaseContractDTOs = purchaseContractApi.getUnCompletedDTOBySaleContractCode(changeReqVO.getCode());
            if (CollUtil.isNotEmpty(purchaseContractDTOs)) {
                purchaseContractDTOs.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.SCM.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //影响出运字段
        if (changeFlag[2]) {
            List<ShipmentDTO> shipments = new ArrayList<>();
            shipments = shipmentApi.getUnShippedDTOBySaleContractCode(changeReqVO.getCode());
            if (CollUtil.isNotEmpty(shipments)) {
                shipments.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.DMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        changeEffectRespVO.setEffectRangeList(effectRangeList);
        changeEffectRespVO.setSubmitFlag(submitFlag);
        return changeEffectRespVO;
    }

    @Override
    public void deleteAutoSaleContract(List<String> sourceSaleContractCode) {
        if (CollUtil.isEmpty(sourceSaleContractCode)) {
            return;
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getSourceContractCode, sourceSaleContractCode));
        if (CollUtil.isEmpty(saleContractDOS)) {
            return;
        }
        List<Long> saleContractIds = saleContractDOS.stream().map(SaleContractDO::getId).toList();
        saleContractMapper.deleteBatchIds(saleContractIds);
        saleContractItemMapper.delete(new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getContractId, saleContractIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reLockSaleContract(ReLockReq reLockReq) {
        String saleContractCode = reLockReq.getSaleContractCode();
        Optional<SaleContractDO> firstDO = saleContractMapper.selectList(SaleContractDO::getCode, saleContractCode).stream().findFirst();
        if (firstDO.isEmpty()) {
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        SaleContractDO saleContractDO = firstDO.get();
        Long saleContractId = saleContractDO.getId();
        List<SaleContractItem> itemList = saleContractItemMapper.selectList(SaleContractItem::getContractId, saleContractId);
        if (CollUtil.isEmpty(itemList)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        List<ReLockItem> reqItemList = reLockReq.getItemList();
        if (CollUtil.isEmpty(reqItemList)) {
            return;
        }
        itemList.forEach(s -> {
            Optional<ReLockItem> first = reqItemList.stream().filter(item -> Objects.equals(item.getSaleContractItemId(), s.getId())).findFirst();
            first.ifPresent(reLockItem -> s.setStockLockSaveReqVOList(reLockItem.getLockSaveReqVOList()));
        });
        //校验锁定数量
//        List<StockLockRespVO> stockDTOList = stockApi.getStockLockListBySaleCode(saleContractDO.getCode());
//        if (CollUtil.isEmpty(stockDTOList)) {
//            return;
//        }
//        Map<String, List<SaleContractItem>> listMap = itemList.stream().collect(Collectors.groupingBy(SaleContractItem::getSkuCode));
//        listMap.forEach((k, v) -> {
//            int oldSum = 0;
//            List<StockLockRespVO> vos = stockDTOList.stream().filter(s -> Objects.equals(s.getSkuCode(), k)).toList();
//            if (CollUtil.isNotEmpty(vos)) {
//                oldSum = vos.stream().mapToInt(StockLockRespVO::getLockQuantity).sum();
//            }
//            Optional<Integer> newSumOpt = v.stream().map(s ->
//                            CollUtil.isEmpty(s.getStockLockSaveReqVOList()) ?
//                                    List.of(new StockLockSaveReqVO()) :
//                                    s.getStockLockSaveReqVOList()).flatMap(List::stream).filter(Objects::nonNull)
//                    .map(StockLockSaveReqVO::getSourceOrderLockedQuantity).filter(Objects::nonNull).reduce(Integer::sum);
//
//            if (newSumOpt.isEmpty() || oldSum != newSumOpt.get()) {
//                throw exception(SALE_CONTRACT_ITEM_LOCK_WRONG);
//            }
//        });
        dealLockStock(itemList, saleContractId, saleContractDO.getCode(), true);
    }

    @Override
    public void batchUpdateConvertShipmentFlag(Map<Long, Integer> shippingQuantityMap) {
        if (CollUtil.isEmpty(shippingQuantityMap)) {
            return;
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, shippingQuantityMap.keySet());
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            Integer shippingQuantity = shippingQuantityMap.get(s.getId());
            if (Objects.isNull(shippingQuantity)) {
                return;
            }
            s.setConvertShipmentFlag(BooleanEnum.NO.getValue());
//            int quantity = Objects.isNull(s.getQuantity()) ? CalculationDict.ZERO : s.getQuantity();
            int transferShippedQuantity = Objects.isNull(s.getTransferShippedQuantity()) ? CalculationDict.ZERO : s.getTransferShippedQuantity();
//            s.setQuantity(quantity+shippedQuantity);
            s.setTransferShippedQuantity(transferShippedQuantity - shippingQuantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void updateShipmentDate(List<String> smsContractCodeList, List<Integer> dateTypes) {
        if (CollUtil.isEmpty(smsContractCodeList)) {
            return;
        }
        List<SaleContractDO> smsContractDOs = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().in(SaleContractDO::getCode, smsContractCodeList));
        if (CollUtil.isEmpty(smsContractDOs)) {
            return;
        }
        // 如遇性能问题此处后续使用批量更新
        smsContractDOs.forEach(s -> {
            updatePaymentPlanSignBackTime(s.getId(), dateTypes);
        });
    }

    @Override
    public void setReLockFlag(List<Long> saleItemIdList, Boolean reLockFlag) {
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, saleItemIdList);
        if (CollUtil.isNotEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            if (reLockFlag) {  //回写允许重新分配时需要判断数量
                if (s.getShipmentTotalQuantity() == 0) {
                    s.setReLockFlag(0);
                }
            } else {  //不允许重新分配直接修改标记
                s.setReLockFlag(1);
            }
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void setShipmentQuantity(Map<Long, Integer> itemQuantityMap) {
        if (CollUtil.isEmpty(itemQuantityMap)) {
            return;
        }
        List<Long> keyList = new ArrayList<>(itemQuantityMap.keySet());
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, keyList);
        if (CollUtil.isNotEmpty(saleContractItems)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        saleContractItems.forEach(s -> {
            Integer quantity = itemQuantityMap.get(s.getId());
            Integer shipmentTotalQuantity = s.getShipmentTotalQuantity() - quantity;
            if (shipmentTotalQuantity < 0) {
                shipmentTotalQuantity = 0;
            }
            s.setShipmentTotalQuantity(shipmentTotalQuantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
        //判断重新分配库存标记
        setReLockFlag(keyList, true);
    }

    @Override
    public Map<String, List<Long>> getCompanyIdListBySaleContractCodeList(List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(saleContractCodeList)) {
            return Map.of();
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getCode, SaleContractDO::getCompanyPath).in(SaleContractDO::getCode, saleContractCodeList));
        if (CollUtil.isEmpty(saleContractDOS)) {
            return Map.of();
        }
        return saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getCode, s -> {
            JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(s.getCompanyPath());
            return Objects.isNull(lastCompanyPath) ? List.of() : List.of(lastCompanyPath.getId());
        }));
    }

    @Override
    public void batchUpdateShippedQuantity(Map<Long, Integer> shippedQuantityMap) {
        if (CollUtil.isEmpty(shippedQuantityMap)) {
            return;
        }
        Set<Long> saleItemIdList = shippedQuantityMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, saleItemIdList);
        saleContractItems.forEach(s -> {
            Long id = s.getId();
            Integer thisShippedQuantity = shippedQuantityMap.get(id);
            Integer shippedQuantity = Objects.isNull(s.getShippedQuantity()) ? CalculationDict.ZERO : s.getShippedQuantity();
            s.setShippedQuantity(shippedQuantity + thisShippedQuantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void batchUpdateTransformShippedQuantity(Map<Long, Integer> transformShippedQuantityMap) {
        if (CollUtil.isEmpty(transformShippedQuantityMap)) {
            return;
        }
        Set<Long> saleItemIdList = transformShippedQuantityMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, saleItemIdList);
        saleContractItems.forEach(s -> {
            Long id = s.getId();
            Integer transformShippedQuantity = transformShippedQuantityMap.get(id);
            s.setTransferShippedQuantity(transformShippedQuantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void updateContractChangeStatus(Long changeId) {
        SaleContractChange saleContractChange = saleContractChangeMapper.selectById(changeId);
        if (Objects.isNull(saleContractChange)) {
            return;
        }
        String sourceCode = saleContractChange.getSourceCode();
        if (StrUtil.isEmpty(sourceCode)) {
            return;
        }
        saleContractMapper.update(new LambdaUpdateWrapper<SaleContractDO>().set(SaleContractDO::getChangeStatus, ChangeStatusEnum.NOT_CHANGED.getStatus()).eq(SaleContractDO::getCode, sourceCode));
    }

    /**
     * 修改外销合同回签日期
     *
     * @param contractId 外销合同编号
     * @param dateTypes  日期类型
     */
    private void updatePaymentPlanSignBackTime(Long contractId, List<Integer> dateTypes) {
        if (Objects.isNull(contractId)) {
            return;
        }
        List<CollectionPlan> purchasePaymentPlans = collectionPlanMapper.selectList(CollectionPlan::getContractId, contractId);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return;
        }
        purchasePaymentPlans.stream().filter(s -> dateTypes.contains(s.getDateType())).forEach(s -> {
            LocalDateTime signDate = LocalDateTime.now();
            s.setStartDate(signDate);
            s.setExpectedReceiptDate(signDate.plusDays(s.getDays()));
        });
        collectionPlanMapper.updateBatch(purchasePaymentPlans);
    }

    @Override
    public Long getIdByCode(String code) {
        SaleContractDO saleContractDO = saleContractMapper.selectOne(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getId).eq(SaleContractDO::getCode, code));
        if (Objects.isNull(saleContractDO)) {
            return null;
        }
        return saleContractDO.getId();
    }

    /**
     * 校验反审核状态
     *
     * @param code 编号
     */
    private void validateAntiAuditStatus(String code) {
        Long count = saleContractMapper.validateAntiAuditStatus(code);
        if (Objects.nonNull(count) && count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }


    /**
     * 计算真实采购价
     *
     * @param saleItems 销售明细id
     * @return Map<Long, Tuple>   key - 销售明细id，Tuple[0] - 真实采购价 Tuple[1] - 真实退税率
     */
    @Override
    public Map<Long, Tuple> calcRealPurchasePrice(List<Long> saleItems) {
        if (CollUtil.isEmpty(saleItems)) {
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItems);
        if (CollUtil.isEmpty(saleContractItems)) {
            return Map.of();
        }
        Map<Long, String> saleItemSkuMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getSkuCode));
        Map<String, List<SaleContractItem>> saleItemMap = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractCode));
        if (CollUtil.isEmpty(saleItemMap)) {
            return Map.of();
        }
        Map<Long, Tuple> result = new HashMap<>();
        saleItemMap.forEach((k, v) -> {
            SaleContractDO saleContractDO = saleContractMapper.selectOne(
                    new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCode, k));
            if (saleContractDO == null || saleContractDO.getCompanyPath() == null) {
                return;
            }
            //获取订单路径尾节点
            Long companyId = TransformUtil.getLastCompanyPath(saleContractDO.getCompanyPath()).getId();
            // 查询采购明细采购价
            Map<Long, PriceQuantityEntity> purchasePriceMap = purchaseContractApi.getPurchaseContractItemPriceMap(saleItemSkuMap, companyId, k);
            // 查询库存明细
//            Map<Long, PriceQuantityEntity> stockPriceMap = stockApi.getStockPriceMap(itemIds);
            // 计算真实采购价
            v.forEach(s -> {
                // 组合产品无需处理
                Long id = s.getId();
                Integer realPurchaseQuantity = s.getRealPurchaseQuantity();
                //真实退税率回写，销售合同退税率和采购合同税率相比 谁小就是谁
                if (CollUtil.isNotEmpty(purchasePriceMap) && Objects.nonNull(purchasePriceMap.get(id))) {
                    PriceQuantityEntity priceQuantityEntity = purchasePriceMap.get(id);
                    JsonAmount realPurchasePrice = priceQuantityEntity.getWithTaxPrice();
                    BigDecimal taxRefundRate = BigDecimal.ZERO;
                    BigDecimal purchaseTaxRate = purchasePriceMap.get(id).getTaxRate();
                    if (s.getTaxRefundRate() == null) {
                        result.put(id, new Tuple(purchasePriceMap.get(id).getWithTaxPriceRemoveFree(), taxRefundRate, realPurchaseQuantity, realPurchasePrice));
                        return;
                    }
                    if (purchaseTaxRate.compareTo(s.getTaxRefundRate()) < 0) {
                        result.put(id, new Tuple(purchasePriceMap.get(id).getWithTaxPriceRemoveFree(), purchaseTaxRate, realPurchaseQuantity,realPurchasePrice));
                    } else {
                        result.put(id, new Tuple(purchasePriceMap.get(id).getWithTaxPriceRemoveFree(), s.getTaxRefundRate(), realPurchaseQuantity,realPurchasePrice));
                    }
                }

            });
        });
        return result;
    }

    private Map<Long, Tuple> calcGenRealPurchasePrice(List<SaleContractItem> saleContractItems, Map<Long, SaleContractDO> saleContractMap) {
        if (CollUtil.isEmpty(saleContractItems) || CollUtil.isEmpty(saleContractMap)) {
            return Map.of();
        }
        List<Long> itemIds = saleContractItems.stream().map(SaleContractItem::getId).toList();
        Map<Long, String> saleSkuCodeMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getSkuCode));
        Map<Long, List<SaleContractItem>> saleItemMap = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
        if (CollUtil.isEmpty(saleItemMap)) {
            return Map.of();
        }
        Map<Long, Tuple> result = new HashMap<>();
        saleItemMap.forEach((k, v) -> {
            SaleContractDO saleContractDO = saleContractMap.get(k);
            if (saleContractDO == null || saleContractDO.getCompanyPath() == null) {
                return;
            }
            //获取订单路径尾节点
            Long companyId = TransformUtil.getLastCompanyPath(saleContractDO.getCompanyPath()).getId();
            // 查询采购明细采购价
            Map<Long, PriceQuantityEntity> purchasePriceMap = purchaseContractApi.getPurchaseContractItemPriceMap(saleSkuCodeMap, companyId, saleContractDO.getCode());
            // 查询库存明细
            Map<Long, PriceQuantityEntity> stockPriceMap = stockApi.getStockPriceMap(itemIds);
            // 计算真实采购价
            v.forEach(s -> {
                Long id = s.getId();
                Integer realPurchaseQuantity = s.getRealPurchaseQuantity();
                //真实退税率回写，销售合同退税率和采购合同税率相比 谁小就是谁
                if (CollUtil.isNotEmpty(purchasePriceMap) && Objects.nonNull(purchasePriceMap.get(id))) {
                    JsonAmount realPurchasePrice = calc(purchasePriceMap.get(id), stockPriceMap.get(id));
                    BigDecimal taxRefundRate = BigDecimal.ZERO;
                    BigDecimal purchaseTaxRate = purchasePriceMap.get(id).getTaxRate();
                    if (s.getTaxRefundRate() == null) {
                        result.put(id, new Tuple(realPurchasePrice, taxRefundRate, realPurchaseQuantity));
                        return;
                    }
                    if (purchaseTaxRate.compareTo(s.getTaxRefundRate()) < 0) {
                        result.put(id, new Tuple(realPurchasePrice, purchaseTaxRate, realPurchaseQuantity));
                    } else {
                        result.put(id, new Tuple(realPurchasePrice, s.getTaxRefundRate(), realPurchaseQuantity));
                    }
                }

            });
        });
        return result;
    }

    @Override
    public void updateRealPurchasePrice(List<Long> saleItems, UserDept manager, Long purchaseUserId) {
        if (CollUtil.isEmpty(saleItems)) {
            return;
        }
        UserDept purchaseUser;
        if (Objects.nonNull(purchaseUserId)) {
            purchaseUser = adminUserApi.getUserDeptByUserId(purchaseUserId);
        } else {
            purchaseUser = null;
        }

        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItems);
        Map<Long, Tuple> realPurchasePriceMap = calcRealPurchasePrice(saleItems);
        if (CollUtil.isEmpty(realPurchasePriceMap)) {
            updateManager(saleContractItems, manager);
            return;
        }
        // 计算真实采购价
        saleContractItems.forEach(s -> {
            if (Objects.nonNull(purchaseUser)) {
                s.setPurchaseUser(purchaseUser);
            }
            Long id = s.getId();
            //真实退税率回写，销售合同退税率和采购合同税率相比 谁小就是谁
            Tuple tuple = realPurchasePriceMap.get(id);
            if (Objects.isNull(tuple)) {
                if (Objects.nonNull(manager)) {
                    s.setManager(manager);
                }
                return;
            }
            JsonAmount realPurchasePrice = tuple.get(3);
            JsonAmount withTaxPriceRemoveFree = tuple.get(0);
            BigDecimal purchaseTaxRate = tuple.get(1);
            tuple.get(CalculationDict.ZERO);
            s.setRealPurchaseWithTaxPrice(withTaxPriceRemoveFree);
            s.setWithTaxPriceRemoveFree(realPurchasePrice);
            if (s.getTaxRefundRate() == null) {
                s.setTaxRefundRate(BigDecimal.ZERO);
            }
            if (purchaseTaxRate.compareTo(s.getTaxRefundRate()) < 0) {
                s.setRealTaxRefundRate(purchaseTaxRate);
            } else {
                s.setRealTaxRefundRate(s.getTaxRefundRate());
            }
            if (Objects.nonNull(manager)) {
                s.setManager(manager);
            }
        });
        // 计算真实采购数量
        List<Long> saleItemIds = saleContractItems.stream().map(SaleContractItem::getId).distinct().toList();
        Map<Long, Map<String, Integer>> purchaseQuantityMap = purchaseContractApi.getItemQuantityMapBySaleItemIds(saleItemIds);
        if (CollUtil.isEmpty(purchaseQuantityMap)) {
            saleContractItemMapper.updateBatch(saleContractItems);
            return;
        }
        // 计算销售明细真实采购数量
        calcSaleItemRealPurchaseQuantity(saleContractItems, purchaseQuantityMap);
        // 重新计算采购明细信息
        List<Long> contractIdList = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().toList();
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectBatchIds(contractIdList);
        Map<Long, SaleContractDO> saleContractDOMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getId, s -> s));
        Map<String, LocalDateTime> saleDateMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getCode, SaleContractDO::getSaleContractDate,(s1,s2)->s1));
        Map<String, Map<String, BigDecimal>> dailyRateMap = rateApi.getDailyRateMapByDate(saleDateMap);


        Map<String, String> configMap = configApi.getConfigMap();
        // 计算明细金额
        saleContractItems.forEach(s -> {
            CalcSaleContactUtil.calcSaleItemCost(s, saleContractDOMap.get(s.getContractId()), dailyRateMap.get(s.getContractCode()), configMap);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
        // 计算表单金额
        Map<Long, List<SaleContractItem>> contractMap = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
        List<AddSubItem> addSubItems = addSubItemMapper.selectList(AddSubItem::getContractCode, saleContractDOS.stream().map(SaleContractDO::getCode).distinct().toList());
        saleContractDOS.forEach(s -> {
            List<SaleContractItem> saleContractItemList = contractMap.get(s.getId());
            if (CollUtil.isEmpty(saleContractItemList)) {
                return;
            }
            CalcSaleContactUtil.calcSaleContractCost(s, saleContractItemList, dailyRateMap.get(s.getCode()), addSubItems);
        });
        saleContractMapper.updateBatch(saleContractDOS);
    }

    /**
     * 计算销售明细真实采购数量
     *
     * @param saleContractItems   销售明细列表
     * @param purchaseQuantityMap 采购数量缓存
     */
    private void calcSaleItemRealPurchaseQuantity(List<SaleContractItem> saleContractItems, Map<Long, Map<String, Integer>> purchaseQuantityMap) {
        if (CollUtil.isEmpty(saleContractItems) || CollUtil.isEmpty(purchaseQuantityMap)) {
            return;
        }
        Map<Long, List<SkuBomDTO>> allBomDTOMap = skuApi.getAllBomDTOMap();
        if (CollUtil.isEmpty(allBomDTOMap)) {
            logger.warn("[calcSaleItemRealPurchaseQuantity] 未查询到bom信息");
        }
        saleContractItems.forEach(s -> {
            Long id = s.getId();
            String skuCode = s.getSkuCode();
            // 根据销售明细id获取对应sku采购数量
            Map<String, Integer> skuQuantityMap = purchaseQuantityMap.get(id);
            if (CollUtil.isEmpty(skuQuantityMap)) {
                return;
            }
            // 根据产品编号获取真实采购数量
            Integer realPurchaseQuantity = skuQuantityMap.get(skuCode);
            // 明细中的产品有采购数量则直接回写
            if (Objects.nonNull(realPurchaseQuantity) && realPurchaseQuantity > 0) {
                s.setRealPurchaseQuantity(realPurchaseQuantity);
            } else {
                realPurchaseQuantity = CalculationDict.ZERO;
            }
            // 非组合产品无需处理
            if (!SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())) {
                return;
            }
            List<SkuBomDTO> skuBomDTOs = allBomDTOMap.get(s.getSkuId());
            if (CollUtil.isEmpty(skuBomDTOs)) {
                logger.warn("[calcSaleItemRealPurchaseQuantity] 产品-{}未找到bom信息", s.getSkuCode());
                return;
            }
            OptionalInt realPurchaseOpt = skuBomDTOs.stream().map(skuBomDTO -> {
                String subSkuCode = skuBomDTO.getSkuCode();
                Integer qty = skuBomDTO.getQty();
                Integer subRealPurchaseQuantity = skuQuantityMap.get(subSkuCode);
                if (Objects.isNull(subRealPurchaseQuantity)) {
                    return 0;
                }
                return NumUtil.div(subRealPurchaseQuantity, qty).intValue();
            }).mapToInt(Integer::intValue).min();
            if (realPurchaseOpt.isPresent()) {
                s.setRealPurchaseQuantity(realPurchaseQuantity + realPurchaseOpt.getAsInt());
            } else {
                s.setRealPurchaseQuantity(realPurchaseQuantity);
            }
        });
    }

    private void updateManager(List<SaleContractItem> saleContractItems, UserDept manager) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> s.setManager(manager));
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public Long getSaleContractIdByCode(String saleContractCode) {

        SaleContractDO saleContractDO = saleContractMapper.selectOne(SaleContractDO::getCode, saleContractCode);
        if (Objects.isNull(saleContractDO)) {
            return null;
        }
        return saleContractDO.getId();
    }

    /**
     * 计算真实采购价
     *
     * @param purchasePriceObj 采购价对象
     * @param stockPriceObj    库存价对象
     * @return 真实采购价
     */
    private JsonAmount calc(PriceQuantityEntity purchasePriceObj, PriceQuantityEntity stockPriceObj) {
        if ((Objects.isNull(purchasePriceObj) || purchasePriceObj.getQuantity() == 0) && (Objects.isNull(stockPriceObj) || stockPriceObj.getQuantity() == 0)) {
            return null;
        }
        if (Objects.isNull(purchasePriceObj) || purchasePriceObj.getQuantity() == 0) {
            return stockPriceObj.getWithTaxPrice();
        }
        if (Objects.isNull(stockPriceObj) || stockPriceObj.getQuantity() == 0) {
            return purchasePriceObj.getWithTaxPrice();
        }
        // 采购价
        BigDecimal jsonPurchasePrice = Objects.isNull(purchasePriceObj.getWithTaxPrice()) ? BigDecimal.ZERO : purchasePriceObj.getWithTaxPrice().getAmount();
        // 采购数量
        Integer purchaseQuantity = purchasePriceObj.getQuantity();
        // 库存价
        BigDecimal jsonStockPrice = Objects.isNull(stockPriceObj.getWithTaxPrice()) ? BigDecimal.ZERO : stockPriceObj.getWithTaxPrice().getAmount();
        // 库存数量
        Integer stockQuantity = stockPriceObj.getQuantity();
        // 采购平均价 = ((采购价 * 采购数量) + (库存价 * 库存数量)) / (采购数量 + 库存数量)
        BigDecimal realPurchasePrice = NumberUtil.div(NumberUtil.add(NumberUtil.mul(jsonPurchasePrice, BigDecimal.valueOf(purchaseQuantity)), NumberUtil.mul(jsonStockPrice, BigDecimal.valueOf(stockQuantity))), BigDecimal.valueOf(purchaseQuantity + stockQuantity));
        return new JsonAmount().setAmount(realPurchasePrice).setCurrency("RMB");
    }

    private void compareTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag, Integer submitFlag) {
        if (formChange != null) {
            //影响采购的字段
            List<FormChangeItemDTO> scmItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.SCM.getValue())).toList();
            scmItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[1] = true;
                }
            });
            //影响出运的字段
            List<FormChangeItemDTO> dmsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.DMS.getValue())).toList();
            dmsItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[2] = true;
                }
            });
            // 影响包材采购字段
            List<FormChangeItemDTO> auxiliaryPurchaseItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.AUXILIARY_PURCHASE.getValue())).toList();
            auxiliaryPurchaseItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[1] = true;
                }
            });
            boolean isSubmitFlag = formChange.getChildren().stream().anyMatch(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel()));
            if (isSubmitFlag) {
                submitFlag = BooleanEnum.YES.getValue();
            }
        }
    }

//    @Override
//    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
//        if (id == null) {
//            throw exception(DECLARATION_NOT_EXISTS);
//        }
//        if (reportCode == null) {
//            throw exception(REPORTCODE_NULL);
//        }
//        SaleContractDO saleContractDO = saleContractMapper.selectById(id);
//        if (saleContractDO == null) {
//            throw exception(SALE_CONTRACT_NOT_EXITS);
//        }
//        ReportDTO reportDTO = null;
//        if (saleContractDO.getCompanyId() != null) {
//            reportDTO = reportApi.getCompanyReport(reportCode, saleContractDO.getCompanyId());
//        }
//        if (reportDTO == null) {
//            reportDTO = reportApi.getReport(reportCode);
//        }
//        if (reportDTO == null) {
//            throw exception(REPORT_NULL, "reportCode-" + reportCode);
//        }
//        // 已在审核中的数据不允许修改
//        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
//            throw exception(REPORT_NOT_APPROVE);
//        }
//        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().eq(SaleContractItem::getContractId, id));
//        List<SaleContractItemExportVO> saleContractItemExportVOList = SaleContractConvert.INSTANCE.convertSaleContractItemExportVOList(saleContractItemList);
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        Map<String, BigDecimal> dailyRateMapByDateMap = rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate());
//        saleContractItemExportVOList.forEach(item -> {
//            if (item.getTaxRefundPrice() != null && item.getTaxRefundPrice().getAmount() != null) {
//                item.setTaxRefundPriceAmount(NumberFormatUtil.formatAmount(item.getTaxRefundPrice().getAmount()));
//            }
//            if (item.getUnitPrice() != null && item.getUnitPrice().getAmount() != null) {
//                item.setUnitPriceAmount(NumberFormatUtil.formatUnitAmount(item.getUnitPrice().getAmount()));
//            }
//            if (item.getTotalSaleAmount() != null && item.getTotalSaleAmount().getAmount() != null) {
//                item.setTotalSaleAmountAmount(NumberFormatUtil.formatAmount(item.getTotalSaleAmount().getAmount()));
//            }
//            if (item.getPurchaseWithTaxPrice() != null && item.getPurchaseWithTaxPrice().getAmount() != null) {
//                item.setPurchaseWithTaxPriceAmount(NumberFormatUtil.formatAmount(item.getPurchaseWithTaxPrice().getAmount()));
//            }
//
//            if (item.getPurchaseTotalPrice() != null && item.getPurchaseTotalPrice().getAmount() != null) {
//                item.setPurchaseTotalPriceAmount(NumberFormatUtil.formatAmount(item.getPurchaseTotalPrice().getAmount()));
//            }
//
//            if (item.getOrderGrossProfit() != null && item.getOrderGrossProfit().getAmount() != null) {
//                item.setOrderGrossProfitAmount(NumberFormatUtil.formatAmount(item.getOrderGrossProfit().getAmount()));
//            }
//
//            if (item.getOrderGrossProfitRate() != null && item.getOrderGrossProfitRate() != null) {
//                item.setOrderGrossProfitRate(NumberFormatUtil.formatRate(item.getOrderGrossProfitRate()));
//            }
//
//            if (item.getVenderDeliveryDate() != null) {
//                item.setVenderDeliveryDateFormat(dtf.format(item.getVenderDeliveryDate()));
//            }
//            if (item.getOuterboxGrossweight() != null && item.getOuterboxGrossweight().getWeight() != null) {
//                item.setOuterboxGrossweightWeight(NumberFormatUtil.formatWeight(item.getOuterboxGrossweight().getWeight()));
//            }
//            if (item.getOuterboxNetweight() != null && item.getOuterboxNetweight().getWeight() != null) {
//                item.setOuterboxNetweightWeight(NumberFormatUtil.formatWeight(item.getOuterboxNetweight().getWeight()));
//            }
//            if (item.getTaxRefundRate() != null) {
//                item.setTaxRefundRate(NumberFormatUtil.formatRate(item.getTaxRefundRate()));
//            }
//            if (item.getOuterboxHeight() != null) {
//                item.setOuterboxHeight(NumberFormatUtil.format(item.getOuterboxHeight(), CalculationDict.TWO));
//            }
//            if (item.getOuterboxLength() != null) {
//                item.setOuterboxLength(NumberFormatUtil.format(item.getOuterboxLength(), CalculationDict.TWO));
//            }
//            if (item.getOuterboxWidth() != null) {
//                item.setOuterboxWidth(NumberFormatUtil.format(item.getOuterboxWidth(), CalculationDict.TWO));
//            }
//            if (item.getOuterboxVolume() != null) {
//                item.setOuterboxVolume(VolumeUtil.processVolume(item.getOuterboxVolume()));
//            }
//
//            if (item.getPurchaseUser() != null) {
//                item.setPurchaseUserName(item.getPurchaseUser().getNickname());
//                item.setPurchaseUserCode(item.getPurchaseUser().getUserCode());
//            }
//
//            if (item.getOrderGrossProfitRate() != null) {
//                if (item.getOrderGrossProfitRate().intValue() < 0) {
//                    item.setNegativeProfitValue("是");
//                } else {
//                    item.setNegativeProfitValue("否");
//                }
//            }
//            if (item.getInspectionFee() != null && item.getInspectionFee().getAmount() != null) {
//                item.setInspectionFeeAmount(NumberFormatUtil.formatAmount(item.getInspectionFee().getAmount()));
//            }
//            if (item.getFundOccupancyFee() != null && item.getFundOccupancyFee().getAmount() != null) {
//                item.setFundOccupancyFeeAmount(NumberFormatUtil.formatAmount(item.getFundOccupancyFee().getAmount()));
//            }
//            if (item.getCommissionAmount() != null && item.getCommissionAmount().getAmount() != null) {
//                item.setCommissionAmountAmount(NumberFormatUtil.formatAmount(item.getCommissionAmount().getAmount()));
//            }
//            if (item.getTrailerFee() != null && item.getTrailerFee().getAmount() != null) {
//                item.setTrailerFeeAmount(NumberFormatUtil.formatAmount(item.getTrailerFee().getAmount()));
//            }
//            if (item.getInsuranceFee() != null && item.getInsuranceFee().getAmount() != null) {
//                item.setInsuranceFeeAmount(NumberFormatUtil.formatAmount(item.getInsuranceFee().getAmount()));
//            }
//            if (item.getPurchasePackagingPrice() != null && item.getPurchasePackagingPrice().getAmount() != null) {
//                item.setPurchasePackagingPriceAmount(NumberFormatUtil.formatAmount(item.getPurchasePackagingPrice().getAmount()));
//            }
//            if (item.getPlatformFee() != null && item.getPlatformFee().getAmount() != null) {
//                item.setPlatformFeeAmount(NumberFormatUtil.formatAmount(item.getPlatformFee().getAmount()));
//            }
//            if (item.getInnerCalcPrice() != null && item.getInnerCalcPrice().getAmount() != null) {
//                item.setInnerCalcPriceAmount(NumberFormatUtil.formatAmount(item.getInnerCalcPrice().getAmount()));
//            }
//            if (item.getSinosureFee() != null && item.getSinosureFee().getAmount() != null) {
//                item.setSinosureFeeAmount(NumberFormatUtil.formatAmount(item.getSinosureFee().getAmount()));
//            }
//            if (item.getStatus() != null) {
//                SaleContractStatusEnum saleContractStatusEnum = SaleContractStatusEnum.getByValue(item.getStatus());
//                if (saleContractStatusEnum != null) {
//                    item.setStatusName(saleContractStatusEnum.getName());
//                }
//            }
//        });
//        HashMap<String, Object> params = new HashMap();
//        if (saleContractDO.getTransportType() != null) {
//            String transportType = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.TRANSPORT_TYPE, String.valueOf(saleContractDO.getTransportType()));
//            params.put("transportType", transportType);
//        }
//        params.put("code", saleContractDO.getCode());
//        params.put("custPo", saleContractDO.getCustPo());
//        if (saleContractDO.getInputDate() != null) {
//            String inputDate = dtf.format(saleContractDO.getInputDate());
//            params.put("inputDate", inputDate);
//        }
//        if (saleContractDO.getStatus() != null) {
//            SaleContractStatusEnum saleContractStatusEnum = SaleContractStatusEnum.getByValue(saleContractDO.getStatus());
//            if (saleContractStatusEnum != null) {
//                params.put("status", saleContractStatusEnum.getName());
//            }
//        }
//
//        if (saleContractDO.getSaleType() != null) {
//            SaleTypeEnum saleTypeEnum = SaleTypeEnum.getByValue(saleContractDO.getStatus());
//            if (saleTypeEnum != null) {
//                params.put("saleType", saleTypeEnum.getName());
//            }
//        }
//        params.put("custCode", saleContractDO.getCustCode());
//        params.put("custName", saleContractDO.getCustName());
//        params.put("currency", saleContractDO.getCurrency());
//        if (saleContractDO.getExchangeRate() != null) {
//            params.put("exchangeRate", NumberFormatUtil.formatRate(saleContractDO.getExchangeRate()));
//        }
//        params.put("settlementTermType", saleContractDO.getSettlementTermType());
//        params.put("settlementName", saleContractDO.getSettlementName());
//        if (saleContractDO.getSales() != null) {
//            params.put("salesCode", saleContractDO.getSales().getUserCode());
//            params.put("salesName", saleContractDO.getSales().getNickname());
//            params.put("salesDeptName", saleContractDO.getSales().getDeptName());
//        }
//        params.put("tradeCountryName", saleContractDO.getTradeCountryName());
//        params.put("departurePortName", saleContractDO.getDeparturePortName());
//        params.put("destinationPortName", saleContractDO.getDestinationPortName());
//        params.put("tradeCountryArea", saleContractDO.getTradeCountryArea());
//        params.put("custCountryName", saleContractDO.getCustCountryName());
//        params.put("transportType", saleContractDO.getTransportType());
//        if (saleContractDO.getCustDeliveryDate() != null) {
//            String custDeliveryDate = dtf.format(saleContractDO.getCustDeliveryDate());
//            params.put("custDeliveryDate", custDeliveryDate);
//        }
//
//        if (Objects.nonNull(saleContractDO.getCustCode())) {
//            CustAllDTO custAllDTO = custApi.getCustByCode(saleContractDO.getCustCode());
//            if (Objects.nonNull(custAllDTO.getSourceType())) {
//                SourceTypeEnum sourceTypeEnum = SourceTypeEnum.getByValue(custAllDTO.getSourceType());
//                if (sourceTypeEnum != null) {
//                    params.put("sourceType", sourceTypeEnum.getName());
//                }
//            }
//        }
//
//        if (saleContractDO.getTrailerFee() != null) {
//            if (saleContractDO.getTrailerFee().getAmount().intValue() > 0) {
//                params.put("hasTrailerFee", "是");
//            } else {
//                params.put("hasTrailerFee", "否");
//            }
//        }
//
//        if (saleContractDO.getInspectionFee() != null) {
//            if (saleContractDO.getInspectionFee().getAmount().intValue() > 0) {
//                params.put("inspectionFee", "是");
//            } else {
//                params.put("inspectionFee", "否");
//            }
//        }
//
//        if (Objects.nonNull(saleContractDO.getSignBackUser())) {
//            params.put("signBackUserName", saleContractDO.getSignBackUser().getNickname());
//            if (Objects.nonNull(saleContractDO.getSignBackUser().getNickname())) {
//                params.put("signBackStatus", "已回签");
//            } else {
//                params.put("signBackStatus", "未回签");
//            }
//        }
//        params.put("signBackDesc", saleContractDO.getSignBackDesc());
//
//        if (saleContractDO.getSignBackDate() != null) {
//            String signBackDate = dtf.format(saleContractDO.getSignBackDate());
//            params.put("signBackDate", signBackDate);
//        }
//        params.put("collectedCustCode", saleContractDO.getCollectedCustCode());
//        params.put("collectedCustName", saleContractDO.getCollectedCustName());
//        params.put("twentyFootCabinetNum", saleContractDO.getTwentyFootCabinetNum());
//        params.put("fortyFootCabinetNum", saleContractDO.getFortyFootCabinetNum());
//        params.put("fortyFootContainerNum", saleContractDO.getFortyFootContainerNum());
//        params.put("bulkHandlingVolume", saleContractDO.getBulkHandlingVolume());
//
//        if (saleContractDO.getTrailerFee() != null && saleContractDO.getTrailerFee().getAmount() != null) {
//            params.put("trailerFee", NumberFormatUtil.formatAmount(saleContractDO.getTrailerFee().getAmount()));
//        }
//
//        if (saleContractDO.getEstimatedTotalFreight() != null && saleContractDO.getEstimatedTotalFreight().getAmount() != null) {
//            params.put("estimatedTotalFreight", NumberFormatUtil.formatAmount(saleContractDO.getEstimatedTotalFreight().getAmount()));
//        }
//        if (saleContractDO.getCommission() != null && saleContractDO.getCommission().getAmount() != null) {
//            BigDecimal commissionRMB = NumberUtil.mul(saleContractDO.getCommission().getAmount(), dailyRateMapByDateMap.get(saleContractDO.getCommission().getCurrency()));
//            BigDecimal commissionUSD = NumberUtil.div(commissionRMB, dailyRateMapByDateMap.get(CommonCurrencyDict.USD).setScale(CalculationDict.TWO, RoundingMode.UP));
//            params.put("commission", commissionUSD);
//        }
//
//        if (saleContractDO.getInsuranceFee() != null && saleContractDO.getInsuranceFee().getAmount() != null) {
//            params.put("insuranceFee", NumberFormatUtil.formatAmount(saleContractDO.getInsuranceFee().getAmount()));
//        }
//
//        if (saleContractDO.getAdditionAmount() != null && saleContractDO.getAdditionAmount().getAmount() != null) {
//            params.put("additionAmount", NumberFormatUtil.formatAmount(saleContractDO.getAdditionAmount().getAmount()));
//        }
//
//        if (saleContractDO.getDeductionAmount() != null && saleContractDO.getDeductionAmount().getAmount() != null) {
//            params.put("deductionAmount", NumberFormatUtil.formatAmount(saleContractDO.getDeductionAmount().getAmount()));
//        }
//
//        if (saleContractDO.getPlatformFee() != null && saleContractDO.getPlatformFee().getAmount() != null) {
//            params.put("platformFee", NumberFormatUtil.formatAmount(saleContractDO.getPlatformFee().getAmount()));
//        }
//        params.put("totalBoxes", saleContractDO.getTotalBoxes());
//        if (saleContractDO.getTotalGrossweight() != null && saleContractDO.getTotalGrossweight().getWeight() != null) {
//            params.put("totalGrossweight", NumberFormatUtil.formatWeight(saleContractDO.getTotalGrossweight().getWeight()));
//        }
//
//        if (saleContractDO.getTotalWeight() != null && saleContractDO.getTotalWeight().getWeight() != null) {
//            params.put("totalWeight", NumberFormatUtil.formatWeight(saleContractDO.getTotalWeight().getWeight()));
//        }
//
//        if (saleContractDO.getTotalVolume() != null) {
//            params.put("totalVolume", VolumeUtil.processVolume(saleContractDO.getTotalVolume()));
//        }
//
//        if (saleContractDO.getTotalGoodsValue() != null && saleContractDO.getTotalGoodsValue().getAmount() != null) {
//            params.put("totalGoodsValue", NumberFormatUtil.formatAmount(saleContractDO.getTotalGoodsValue().getAmount()));
//        }
//
//        //合计
//        List<JsonAmount> totalPurchaseList = saleContractDO.getTotalPurchase();
//        if (CollUtil.isNotEmpty(totalPurchaseList)) {
//            BigDecimal totalPurchaseAmount = totalPurchaseList.stream()
//                    .map(JsonAmount::getAmount)
//                    .filter(Objects::nonNull)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add)
//                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
//            params.put("totalPurchase", totalPurchaseAmount);
//        }
//
//        if (saleContractDO.getInspectionFee() != null && saleContractDO.getInspectionFee().getAmount() != null) {
//            params.put("inspectionFee", NumberFormatUtil.formatAmount(saleContractDO.getInspectionFee().getAmount()));
//        }
//
//        if (saleContractDO.getTotalVatRefund() != null && saleContractDO.getTotalVatRefund().getAmount() != null) {
//            params.put("totalVatRefund", NumberFormatUtil.formatAmount(saleContractDO.getTotalVatRefund().getAmount()));
//        }
//
//        if (saleContractDO.getOrderGrossProfit() != null && saleContractDO.getOrderGrossProfit().getAmount() != null) {
//            params.put("orderGrossProfit", NumberFormatUtil.formatAmount(saleContractDO.getOrderGrossProfit().getAmount()));
//        }
//        if (saleContractDO.getGrossProfitMargin() != null) {
//            params.put("grossProfitMargin", NumUtil.mul(saleContractDO.getGrossProfitMargin(), 100).setScale(CalculationDict.TWO, RoundingMode.UP));
//        }
//
//        params.put("totalQuantity", saleContractDO.getTotalQuantity());
//
//        if (saleContractDO.getEstimatedTotalFreight() != null && saleContractDO.getEstimatedTotalFreight().getAmount() != null) {
//            params.put("estimatedTotalFreight", NumberFormatUtil.formatAmount(saleContractDO.getEstimatedTotalFreight().getAmount()));
//        }
//
//        if (saleContractDO.getAccessoriesPurchaseTotal() != null && saleContractDO.getAccessoriesPurchaseTotal().getAmount() != null) {
//            params.put("accessoriesPurchaseTotal", NumberFormatUtil.formatAmount(saleContractDO.getAccessoriesPurchaseTotal().getAmount()));
//        }
//
//        if (saleContractDO.getEstimatedPackingMaterials() != null && saleContractDO.getEstimatedPackingMaterials().getAmount() != null) {
//            params.put("estimatedPackingMaterials", NumberFormatUtil.formatAmount(saleContractDO.getEstimatedPackingMaterials().getAmount()));
//        }
//
//        if (saleContractDO.getSinosureFee() != null && saleContractDO.getSinosureFee().getAmount() != null) {
//            params.put("sinosureFee", NumberFormatUtil.formatAmount(saleContractDO.getSinosureFee().getAmount()));
//        }
//
//        // 写入加减项
//        List<AddSubItem> addSubItemList = new ArrayList<>();
//        addSubItemList = addSubItemMapper.selectList(new LambdaQueryWrapperX<AddSubItem>().eq(AddSubItem::getContractCode, saleContractDO.getCode()));
//        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
//        byte[] content;
//        String path = "";
//        try {
//            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
//            content = fileApi.getFileContent(path);
//        } catch (Exception e) {
//            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path-" + path);
//        }
//        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
//        List<SaleContractAddSubItemExportVO> saleContractAddSubItemExportVO = SaleContractConvert.INSTANCE.convertSaleContractAddSubItemExportVOList(addSubItemList);
//        saleContractAddSubItemExportVO.forEach(item -> {
//            if (item.getAmount() != null) {
//                item.setAmountValue(NumberFormatUtil.formatAmount(item.getAmount().getAmount()));
//            }
//            if (item.getCalculationType() != null) {
//                String calculationTypeLabel = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.CALCULATION_TYPE, String.valueOf(item.getCalculationType()));
//                item.setCalculationTypeLabel(calculationTypeLabel);
//            }
//        });
//        HashMap<String, Object> tableParams = new HashMap();
//        tableParams.put("childItem", saleContractItemExportVOList);
//        tableParams.put("addSubItem", saleContractAddSubItemExportVO);
//        try {
//            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "销售合同.xlsx", null, tableParams, null);
//        } catch (IOException e) {
//            throw exception(EXCEL_EXPORT_FAIL);
//        }
//    }

    /**
     * 导出外销合同 Excel（支持单个或批量）
     *
     * @param ids 外销合同ID列表（单个导出时传入只有一个元素的列表）
     * @param exportType 导出类型
     * @param reportCode 模板编码
     * @param response HTTP响应
     * @throws IOException IO异常
     * @author 波波
     */
    @Override
    public void exportExcel(List<Long> ids, String exportType, String reportCode, HttpServletResponse response) throws IOException {
        if (CollUtil.isEmpty(ids)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }

        // 查询所有合同数据
        List<SaleContractDO> saleContractDOList = saleContractMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(saleContractDOList)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }

        // 使用第一个合同的数据作为基础
        SaleContractDO saleContractDO = saleContractDOList.get(0);
        ReportDTO reportDTO = null;
        if (saleContractDO.getCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, saleContractDO.getCompanyId());
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

        // 收集所有合同的明细和客户名称
        List<SaleContractItemExportVO> saleContractItemExportVOList = new ArrayList<>();
        Set<String> custNameSet = new LinkedHashSet<>();
        for (SaleContractDO contract : saleContractDOList) {
            // 收集客户名称
            if (StrUtil.isNotBlank(contract.getCustName())) {
                custNameSet.add(contract.getCustName());
            }
            // 获取合同明细
            List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(
                new LambdaQueryWrapperX<SaleContractItem>().eq(SaleContractItem::getContractId, contract.getId())
            );
            // 转换明细为导出VO
            for (SaleContractItem item : saleContractItemList) {
                SaleContractItemExportVO vo = new SaleContractItemExportVO();
                vo.setBasicSkuCode(item.getBasicSkuCode());
                vo.setCskuCode(item.getCskuCode());
                vo.setHsCode(item.getHsCode());
                vo.setSkuCode(item.getSkuCode());
                vo.setQuantity(item.getQuantity());
                vo.setDescriptionEng(item.getDescriptionEng());
                vo.setUnitPriceAmount(item.getUnitPrice().getCheckAmount());
                vo.setTotalSaleAmountAmount(item.getTotalSaleAmount().getCheckAmount());
                // 处理图片
                if (item.getMainPicture() != null) {
                    String inputPath = item.getMainPicture().getFileUrl();
                    try {
                        byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                        File inputFile = FileUtils.createTempFile(content);
                        BufferedImage image = ImageIO.read(inputFile);
                        Double width = Double.valueOf(image.getWidth());
                        Double height = Double.valueOf(image.getHeight());
                        WriteCellData<Void> voidWriteCellData = ExcelUtils.imageCells(content, width, height, 2.0, 2.0, 0, 0);
                        vo.setCptp(voidWriteCellData);
                        inputFile.delete();
                    } catch (Exception e) {
                        logger.info("销售合同导出图片获取失败: " + e.getMessage());
                    }
                }
                saleContractItemExportVOList.add(vo);
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
        try {
            ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
            // 2. 用POI打开临时文件并处理明细块
            Workbook workbook = WorkbookFactory.create(templateFileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            HashMap<String, Object> params = new HashMap<>();
            switch (exportType) {
                case "detail","factory": {
                    //--外销合同导出
                    params = getExportParams(saleContractDO);
                    // 使用拼接后的客户名称（单个合同拼接后也是一个客户名称）
                    params.put("custName", String.join(", ", custNameSet));
                    int blockStartRow = 7; // 第7行开始
                    int blockRowCount = 2;
                    for (int i = 0; i < saleContractItemExportVOList.size(); i++) {
                        int targetStartRow = blockStartRow + i * blockRowCount; // 从第7行开始计算
                        // 插入新行（每个商品都需要插入2行）
                        sheet.shiftRows(targetStartRow, sheet.getLastRowNum(), blockRowCount, true, false);
                        // 创建新行
                        for (int j = 0; j < blockRowCount; j++) {
                            sheet.createRow(targetStartRow + j);
                        }
                        // 1. 先移除可能存在的合并区域，避免冲突
                        removeExistingMergedRegions(sheet, targetStartRow, targetStartRow + 1, 1, 8);
                        // 2. 合并单元格
                        CellRangeAddress[] regions = {
                                //数量 垂直合并（G列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 6, 6),
                                // 单价. 跨行跨列合并（H列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 7, 7),
                                // 总价. 跨行跨列合并（I列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 8, 8),
                                // 货物名称及规格  跨行跨列合并（D-F列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 3, 5),
                                // 图片区域 跨行跨列合并第二行（B-C列）
                                new CellRangeAddress(targetStartRow+1, targetStartRow+1 , 1, 2),
                        };
                        for (CellRangeAddress region : regions) {
                            sheet.addMergedRegion(region);
                            setRegionBorder(sheet, workbook, region);
                        }
                        // 3. 填充数据 - 根据图片中红色框圈住的结构
                        SaleContractItemExportVO saleContractItemExportVO = saleContractItemExportVOList.get(i);

                        // Vertak No. 填充到B列（垂直合并区域）
                        setCellValueSafe(sheet, targetStartRow, 1, saleContractItemExportVO.getBasicSkuCode());

                        // RefNo. 填充到C列（跨行跨列合并区域）
                        setCellValueSafe(sheet, targetStartRow, 2, saleContractItemExportVO.getCskuCode());

                        // 货物描述填充到D-F列（跨行跨列合并区域）
                        setCellValueSafe(sheet, targetStartRow, 3, saleContractItemExportVO.getDescriptionEng());

                        // 数量、单价、总价填充到G-I列（第二行，图片下方）
                        setCellValueSafe(sheet, targetStartRow, 6, String.valueOf(saleContractItemExportVO.getQuantity())); // G列：数量
                        setCellValueSafe(sheet, targetStartRow, 7, saleContractItemExportVO.getUnitPriceAmount().toString()); // H列：单价
                        setCellValueSafe(sheet, targetStartRow, 8, saleContractItemExportVO.getTotalSaleAmountAmount().toString()); // I列：总价
                        // 设置单元格样式
                        for (int rowOffset = 0; rowOffset < blockRowCount; rowOffset++) {
                            Row currentRow = sheet.getRow(targetStartRow + rowOffset);
                            if (currentRow != null) {
                                // 设置默认行高（图片行的行高会在插入图片时动态调整）
                                if (rowOffset == 0 && (saleContractItemExportVO.getCptp() == null || saleContractItemExportVO.getCptp().getImageDataList() == null || saleContractItemExportVO.getCptp().getImageDataList().isEmpty())) {
                                    // 第一行没有图片时设置默认行高
                                    currentRow.setHeightInPoints(40);
                                } else if (rowOffset == 1) {
                                    // 第二行（数量、单价、总价行）设置默认行高
                                    currentRow.setHeightInPoints(40);
                                }

                                // 设置单元格样式
                                CellStyle centerStyle = sheet.getWorkbook().createCellStyle();
                                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                                centerStyle.setWrapText(true);

                                // 为所有单元格设置样式（从B列开始到I列）
                                for (int col = 1; col <= 8; col++) {
                                    Cell cell = currentRow.getCell(col);
                                    if (cell != null) {
                                        cell.setCellStyle(centerStyle);
                                    }
                                }
                            }
                        }
                        // 直接插入图片
                        if (saleContractItemExportVO.getCptp() != null && saleContractItemExportVO.getCptp().getImageDataList() != null && !saleContractItemExportVO.getCptp().getImageDataList().isEmpty()) {
                            try {
                                ImageData imageData = saleContractItemExportVO.getCptp().getImageDataList().get(0);
                                byte[] imageBytes = imageData.getImage();
                                if (imageBytes != null) {
                                    int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
                                    CreationHelper helper = workbook.getCreationHelper();
                                    Drawing<?> drawing = sheet.createDrawingPatriarch();
                                    ClientAnchor anchor = helper.createClientAnchor();
                                    // 设置锚点位置 - 图片插入到b-c列的上半部分，只占用2行
                                    anchor.setCol1(1); // B列（起始列）
                                    anchor.setCol2(2); // C列（结束列）
                                    anchor.setRow1(targetStartRow+1); // 第2行
                                    anchor.setRow2(targetStartRow+1); // 只占第2行
                                    
                                    // 增大图片尺寸，根据模板图片中的实际显示效果调整
                                    int displayWidth = 200; // 增大到200像素宽度
                                    int displayHeight = 150; // 增大到150像素高度

                                    // 居中偏移量计算
                                    int centerOffsetX = 10; // 水平居中偏移
                                    int centerOffsetY = 5; // 垂直居中偏移
                                    
                                    // 调整第一行的行高以适应图片
                                    Row imageRow = sheet.getRow(targetStartRow+1);
                                    if (imageRow != null) {
                                        int requiredHeightInPoints = Math.max(80, (int)(displayHeight * 0.75));
                                        int currentRowHeightInPoints = (int) imageRow.getHeightInPoints();
                                        if (currentRowHeightInPoints < requiredHeightInPoints) {
                                            imageRow.setHeightInPoints(requiredHeightInPoints);
                                        }
                                    }
                                    // 将像素转换为EMU（Excel度量单位）
                                    int emuPerPixel = 9525;
                                    anchor.setDx1(centerOffsetX * emuPerPixel); // 水平居中偏移
                                    anchor.setDx2((centerOffsetX + displayWidth) * emuPerPixel); // 设置宽度
                                    anchor.setDy1(centerOffsetY * emuPerPixel); // 垂直居中偏移
                                    anchor.setDy2((centerOffsetY + displayHeight) * emuPerPixel); // 设置高度
                                    drawing.createPicture(anchor, pictureIdx);
                                }
                            } catch (Exception e) {
                                logger.warn("插入图片失败: " + e.getMessage());
                            }
                        }
                        //1.审核通过合同才处理
                        if (Objects.equals(saleContractDO.getAuditStatus(),BpmProcessInstanceResultEnum.APPROVE.getResult())) {
                            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
                            if (Objects.isNull(companyDTO)) {
                                throw exception(COMPANY_NOT_EXITS);
                            }
                            String pictureJson = companyDTO.getPicture();
                            List<DictSimpleFileList> simpleFileLists = StrUtil.isNotEmpty(pictureJson) ? 
                                JsonUtils.parseArray(pictureJson, DictSimpleFileList.class) : Collections.emptyList();
                            //2.有公章才处理
                            if (CollUtil.isNotEmpty(simpleFileLists)) {
                                Optional<DictSimpleFileList> first = simpleFileLists.stream().filter(s -> Objects.equals(s.getValue(), OfficialSealTypeEnum.BUSINESS_STAMP.getValue().toString())).findFirst();
                                if (first.isPresent()) {
                                    Optional<SimpleFile> first1 = first.get().getFileList().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                                    if (first1.isPresent()) {
                                        SimpleFile simpleFile = first1.get();
                                         try {
                                             byte[] content2 = fileApi.getFileContent(simpleFile.getFileUrl().substring(simpleFile.getFileUrl().lastIndexOf("get/") + 4));
                                             File inputFile = FileUtils.createTempFile(content2);
                                             BufferedImage image = ImageIO.read(inputFile);
                                             // 插入公章图片到Excel
                                             insertCompanySealImage(workbook, sheet, content2, image, saleContractItemExportVOList.size());
                                         } catch (Exception e) {
                                             logger.info("销售合同导出图片获取失败"+e.getMessage());
                                         }
                                    }
                                }
                            }
                        }
                        // 4. 给整个块加边框
                        for (int r = targetStartRow; r < targetStartRow + blockRowCount; r++) {
                            Row row = sheet.getRow(r);
                            for (int c = 1; c < 18; c++) {
                                Cell cell = row.getCell(c);
                                if (cell != null) {
                                    CellStyle style = workbook.createCellStyle();
                                    style.cloneStyleFrom(cell.getCellStyle());
                                    style.setBorderTop(BorderStyle.THIN);
                                    style.setBorderBottom(BorderStyle.THIN);
                                    style.setBorderLeft(BorderStyle.THIN);
                                    style.setBorderRight(BorderStyle.THIN);
                                    cell.setCellStyle(style);
                                }
                            }
                        }
                    }
                    break;
                }
                case "detailHsCode": {
                    //--外销合同导出（带hsCode列）
                    params = getExportParams(saleContractDO);
                    // 使用拼接后的客户名称（单个合同拼接后也是一个客户名称）
                    params.put("custName", String.join(", ", custNameSet));
                    int blockStartRow = 7; // 第7行开始
                    int blockRowCount = 2;
                    for (int i = 0; i < saleContractItemExportVOList.size(); i++) {
                        int targetStartRow = blockStartRow + i * blockRowCount; // 从第7行开始计算
                        // 插入新行（每个商品都需要插入2行）
                        sheet.shiftRows(targetStartRow, sheet.getLastRowNum(), blockRowCount, true, false);
                        // 创建新行
                        for (int j = 0; j < blockRowCount; j++) {
                            sheet.createRow(targetStartRow + j);
                        }
                        // 1. 先移除可能存在的合并区域，避免冲突
                        removeExistingMergedRegions(sheet, targetStartRow, targetStartRow + 1, 1, 9);
                        // 2. 合并单元格
                        CellRangeAddress[] regions = {
                                //数量 垂直合并（H列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 7, 7),
                                // 单价. 跨行跨列合并（I列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 8, 8),
                                // 总价. 跨行跨列合并（J列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 9, 9),
                                // HSCode 垂直合并（D列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 3, 3),
                                // 货物名称及规格  跨行跨列合并（E-G列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 4, 6),
                                // 图片区域 跨行跨列合并第二行（B-C列）
                                new CellRangeAddress(targetStartRow+1, targetStartRow+1 , 1, 2),
                        };
                        for (CellRangeAddress region : regions) {
                            sheet.addMergedRegion(region);
                            setRegionBorder(sheet, workbook, region);
                        }
                        // 3. 填充数据 - 根据图片中红色框圈住的结构
                        SaleContractItemExportVO saleContractItemExportVO = saleContractItemExportVOList.get(i);

                        // Vertak No. 填充到B列（垂直合并区域）
                        setCellValueSafe(sheet, targetStartRow, 1, saleContractItemExportVO.getBasicSkuCode());

                        // RefNo. 填充到C列（跨行跨列合并区域）
                        setCellValueSafe(sheet, targetStartRow, 2, saleContractItemExportVO.getCskuCode());

                        // HSCode 填充到D列（垂直合并区域）
                        setCellValueSafe(sheet, targetStartRow, 3, saleContractItemExportVO.getHsCode());

                        // 货物描述填充到E-G列（跨行跨列合并区域）
                        setCellValueSafe(sheet, targetStartRow, 4, saleContractItemExportVO.getDescriptionEng());

                        // 数量、单价、总价填充到H-J列（第二行，图片下方）
                        setCellValueSafe(sheet, targetStartRow, 7, String.valueOf(saleContractItemExportVO.getQuantity())); // H列：数量
                        setCellValueSafe(sheet, targetStartRow, 8, saleContractItemExportVO.getUnitPriceAmount().toString()); // I列：单价
                        setCellValueSafe(sheet, targetStartRow, 9, saleContractItemExportVO.getTotalSaleAmountAmount().toString()); // J列：总价
                        // 设置单元格样式
                        for (int rowOffset = 0; rowOffset < blockRowCount; rowOffset++) {
                            Row currentRow = sheet.getRow(targetStartRow + rowOffset);
                            if (currentRow != null) {
                                // 设置默认行高（图片行的行高会在插入图片时动态调整）
                                if (rowOffset == 0 && (saleContractItemExportVO.getCptp() == null || saleContractItemExportVO.getCptp().getImageDataList() == null || saleContractItemExportVO.getCptp().getImageDataList().isEmpty())) {
                                    // 第一行没有图片时设置默认行高
                                    currentRow.setHeightInPoints(40);
                                } else if (rowOffset == 1) {
                                    // 第二行（数量、单价、总价行）设置默认行高
                                    currentRow.setHeightInPoints(40);
                                }

                                // 设置单元格样式
                                CellStyle centerStyle = sheet.getWorkbook().createCellStyle();
                                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                                centerStyle.setWrapText(true);

                                // 为所有单元格设置样式（从B列开始到J列）
                                for (int col = 1; col <= 9; col++) {
                                    Cell cell = currentRow.getCell(col);
                                    if (cell != null) {
                                        cell.setCellStyle(centerStyle);
                                    }
                                }
                            }
                        }
                        // 直接插入图片（与detail逻辑相同）
                        if (saleContractItemExportVO.getCptp() != null && saleContractItemExportVO.getCptp().getImageDataList() != null && !saleContractItemExportVO.getCptp().getImageDataList().isEmpty()) {
                            try {
                                ImageData imageData = saleContractItemExportVO.getCptp().getImageDataList().get(0);
                                byte[] imageBytes = imageData.getImage();
                                if (imageBytes != null) {
                                    int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
                                    CreationHelper helper = workbook.getCreationHelper();
                                    Drawing<?> drawing = sheet.createDrawingPatriarch();
                                    ClientAnchor anchor = helper.createClientAnchor();
                                    // 设置锚点位置 - 图片插入到b-c列的上半部分，只占用2行
                                    anchor.setCol1(1); // B列（起始列）
                                    anchor.setCol2(2); // C列（结束列）
                                    anchor.setRow1(targetStartRow+1); // 第2行
                                    anchor.setRow2(targetStartRow+1); // 只占第2行
                                    
                                    // 增大图片尺寸，根据模板图片中的实际显示效果调整
                                    int displayWidth = 200; // 增大到200像素宽度
                                    int displayHeight = 150; // 增大到150像素高度

                                    // 居中偏移量计算
                                    int centerOffsetX = 10; // 水平居中偏移
                                    int centerOffsetY = 5; // 垂直居中偏移

                                    anchor.setDx1(centerOffsetX * 9525);
                                    anchor.setDy1(centerOffsetY * 9525);
                                    anchor.setDx2(displayWidth * 9525);
                                    anchor.setDy2(displayHeight * 9525);
                                    
                                    drawing.createPicture(anchor, pictureIdx);
                                }
                            } catch (Exception e) {
                                logger.info("图片插入失败: " + e.getMessage());
                            }
                        }
                        // 4. 给整个块加边框
                        for (int r = targetStartRow; r < targetStartRow + blockRowCount; r++) {
                            Row row = sheet.getRow(r);
                            for (int c = 1; c < 18; c++) {
                                Cell cell = row.getCell(c);
                                if (cell != null) {
                                    CellStyle style = workbook.createCellStyle();
                                    style.cloneStyleFrom(cell.getCellStyle());
                                    style.setBorderTop(BorderStyle.THIN);
                                    style.setBorderBottom(BorderStyle.THIN);
                                    style.setBorderLeft(BorderStyle.THIN);
                                    style.setBorderRight(BorderStyle.THIN);
                                    cell.setCellStyle(style);
                                }
                            }
                        }
                    }
                    break;
                }
                case "domestic": {
                    //--内销合同导出
                    // 获取所有明细的SKU列表
                    List<String> skuCodeList = saleContractItemExportVOList.stream()
                        .map(SaleContractItemExportVO::getSkuCode)
                        .distinct()
                        .collect(Collectors.toList());
                    Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
                    List<SaleContractItem> firstContractItems = saleContractItemMapper.selectList(
                        new LambdaQueryWrapperX<SaleContractItem>().eq(SaleContractItem::getContractId, saleContractDO.getId())
                    );
                    params = getExportParamsDomestic(saleContractDO, firstContractItems);
                    // 使用拼接后的客户名称
                    params.put("custName", String.join(", ", custNameSet));

                    int blockStartRow = 8; // 第8行开始
                    int blockRowCount = 2; // 每个产品占2行
                    for (int i = 0; i < saleContractItemExportVOList.size(); i++) {
                        int targetStartRow = blockStartRow + i * blockRowCount; // 从第8行开始计算
                        // 插入新行（每个商品都需要插入2行）
                        sheet.shiftRows(targetStartRow, sheet.getLastRowNum(), blockRowCount, true, false);
                        // 创建新行
                        for (int j = 0; j < blockRowCount; j++) {
                            sheet.createRow(targetStartRow + j);
                        }
                        // 1. 先移除可能存在的合并区域，避免冲突
                        removeExistingMergedRegions(sheet, targetStartRow, targetStartRow + 1, 1, 8);
                        // 2. 合并单元格 - 根据内销合同模版格式调整
                        CellRangeAddress[] regions = {
                                // 序号 垂直合并（B列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 1, 1),
                                // 产品描述 跨行跨列合并（E列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 4, 4),
                                // 单位 跨行跨列合并（F列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 5, 5),
                                // 单价 跨行跨列合并（G列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 6, 6),
                                // 数量 跨行跨列合并（H列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 7, 7),
                                // 金额 跨行跨列合并（I列，跨2行）
                                new CellRangeAddress(targetStartRow, targetStartRow + 1, 8, 8),
                                // 图片区域 - 在第二行显示
                                new CellRangeAddress(targetStartRow + 1, targetStartRow + 1, 2, 3),
                        };
                        for (CellRangeAddress region : regions) {
                            sheet.addMergedRegion(region);
                            setRegionBorder(sheet, workbook, region);
                        }
                        // 3. 填充数据 - 根据内销合同模版格式
                        SaleContractItemExportVO saleContractItemExportVO = saleContractItemExportVOList.get(i);

                        // 序号 填充到B列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 1, String.valueOf(i + 1));

                        // 泛太货号 填充到C列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 2, saleContractItemExportVO.getSkuCode());

                        // 客户货号 填充到D列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 3, saleContractItemExportVO.getCskuCode());

                        // 产品描述 填充到E列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 4, saleContractItemExportVO.getDescriptionEng());

                        // 单位 填充到F列（只在第一行填充）
                        SkuDTO skuDTO = skuDTOMap.get(saleContractItemExportVO.getSkuCode());
                        String measureUnit = DictFrameworkUtils.getDictDataLabel("measure_unit", skuDTO.getMeasureUnit());
                        setCellValueSafe(sheet, targetStartRow, 5, measureUnit);

                        // 单价 填充到G列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 6, saleContractItemExportVO.getUnitPriceAmount().toString());

                        // 数量 填充到H列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 7, String.valueOf(saleContractItemExportVO.getQuantity()));

                        // 金额 填充到I列（只在第一行填充）
                        setCellValueSafe(sheet, targetStartRow, 8, saleContractItemExportVO.getTotalSaleAmountAmount().toString());
                        
                        // 设置单元格样式
                        for (int rowOffset = 0; rowOffset < blockRowCount; rowOffset++) {
                            Row currentRow = sheet.getRow(targetStartRow + rowOffset);
                            if (currentRow != null) {
                                // 设置默认行高
                                currentRow.setHeightInPoints(40);

                                // 设置单元格样式
                                CellStyle centerStyle = sheet.getWorkbook().createCellStyle();
                                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                                centerStyle.setWrapText(true);

                                // 为所有单元格设置样式（从B列开始到I列）
                                for (int col = 1; col <= 8; col++) {
                                    Cell cell = currentRow.getCell(col);
                                    if (cell != null) {
                                        cell.setCellStyle(centerStyle);
                                    }
                                }
                            }
                        }
                        
                        // 插入图片（内销合同产品图片插入逻辑）
                        if (saleContractItemExportVO.getCptp() != null && saleContractItemExportVO.getCptp().getImageDataList() != null && !saleContractItemExportVO.getCptp().getImageDataList().isEmpty()) {
                            try {
                                ImageData imageData = saleContractItemExportVO.getCptp().getImageDataList().get(0);
                                byte[] imageBytes = imageData.getImage();
                                if (imageBytes != null) {
                                    int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
                                    CreationHelper helper = workbook.getCreationHelper();
                                    Drawing<?> drawing = sheet.createDrawingPatriarch();
                                    ClientAnchor anchor = helper.createClientAnchor();
                                    
                                    // 设置锚点位置 - 图片插入到第二行，C-D列
                                    anchor.setCol1(2); // C列（泛太货号列）
                                    anchor.setCol2(3); // D列（客户货号列）
                                    anchor.setRow1(targetStartRow + 1); // 第二行
                                    anchor.setRow2(targetStartRow + 1); // 只占第二行
                                    
                                    // 内销合同图片尺寸
                                    int displayWidth = 200; // 增大到200像素宽度
                                    int displayHeight = 150; // 增大到150像素高度

                                    // 居中偏移量计算
                                    int centerOffsetX = 10; // 水平居中偏移
                                    int centerOffsetY = 5; // 垂直居中偏移
                                    
                                    // 调整第二行的行高以适应图片
                                    Row imageRow = sheet.getRow(targetStartRow + 1);
                                    if (imageRow != null) {
                                        int requiredHeightInPoints = Math.max(80, (int)(displayHeight * 0.75));
                                        int currentRowHeightInPoints = (int) imageRow.getHeightInPoints();
                                        if (currentRowHeightInPoints < requiredHeightInPoints) {
                                            imageRow.setHeightInPoints(requiredHeightInPoints);
                                        }
                                    }
                                    
                                    // 将像素转换为EMU（Excel度量单位）
                                    int emuPerPixel = 9525;
                                    anchor.setDx1(centerOffsetX * emuPerPixel);
                                    anchor.setDx2((centerOffsetX + displayWidth) * emuPerPixel);
                                    anchor.setDy1(centerOffsetY * emuPerPixel);
                                    anchor.setDy2((centerOffsetY + displayHeight) * emuPerPixel);
                                    drawing.createPicture(anchor, pictureIdx);
                                }
                            } catch (Exception e) {
                                logger.warn("内销合同插入图片失败: " + e.getMessage());
                            }
                        }

                        // 4. 给整个块加边框
                        for (int r = targetStartRow; r < targetStartRow + blockRowCount; r++) {
                            Row row = sheet.getRow(r);
                            for (int c = 1; c < 9; c++) {
                                Cell cell = row.getCell(c);
                                if (cell != null) {
                                    CellStyle style = workbook.createCellStyle();
                                    style.cloneStyleFrom(cell.getCellStyle());
                                    style.setBorderTop(BorderStyle.THIN);
                                    style.setBorderBottom(BorderStyle.THIN);
                                    style.setBorderLeft(BorderStyle.THIN);
                                    style.setBorderRight(BorderStyle.THIN);
                                    cell.setCellStyle(style);
                                }
                            }
                        }
                    }
                    break;
                }
            }
    
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            // 根据是单个还是批量导出设置不同的文件名
            String fileName = saleContractDOList.size() > 1 ? "批量销售合同.xlsx" : "销售合同.xlsx";
            ExcelUtils.writeByTemplate(response, inputStream, params, fileName, null, null, 600);
        } catch (Exception e) {
            logger.error("销售合同导出失败", e);
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    private void setCellValueSafe(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }
    
    /**
     * 移除指定区域内的现有合并区域，避免合并冲突
     * 
     * @param sheet Excel工作表
     * @param startRow 起始行
     * @param endRow 结束行
     * @param startCol 起始列
     * @param endCol 结束列
     * @author 波波
     */
    private void removeExistingMergedRegions(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        List<Integer> regionsToRemove = new ArrayList<>();
        
        // 遍历所有合并区域，找到与指定区域重叠的区域
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            
            // 检查是否有重叠
            if (region.getFirstRow() <= endRow && region.getLastRow() >= startRow &&
                region.getFirstColumn() <= endCol && region.getLastColumn() >= startCol) {
                regionsToRemove.add(i);
            }
        }
        
        // 从后往前移除，避免索引变化
        for (int i = regionsToRemove.size() - 1; i >= 0; i--) {
            int regionIndex = regionsToRemove.get(i);
            sheet.removeMergedRegion(regionIndex);
        }
    }
    private void setRegionBorder(Sheet sheet, Workbook workbook, CellRangeAddress region) {
        for (int r = region.getFirstRow(); r <= region.getLastRow(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            for (int c = region.getFirstColumn(); c <= region.getLastColumn(); c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);
                CellStyle style = workbook.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(style);
            }
        }
    }

    @Override
    public void setOrderDone(Long id) {
        SaleContractDO saleContractDO = saleContractMapper.selectById(id);
        if (Objects.isNull(saleContractDO)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        saleContractDO.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
        saleContractMapper.updateById(saleContractDO);
    }

    @Override
    @DataPermission(enable = false)
    public Boolean checkCollectionPlan(String saleContractCode, Boolean isPurchase, BigDecimal needSum) {
        if (Objects.equals(needSum, BigDecimal.ZERO))
            return true;
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(SaleContractDO::getCode, saleContractCode).stream().toList();
        if (CollUtil.isEmpty(saleContractDOS) || saleContractDOS.size() < 1) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        SaleContractDO saleContractDO = saleContractDOS.get(0);
        //获取收款计划
        List<CollectionPlan> collectionPlans = collectionPlanMapper.selectList(CollectionPlan::getContractId, saleContractDO.getId());
        if (CollUtil.isEmpty(collectionPlans)) {
            return true;
        }

        List<CollectionPlan> planList = new ArrayList<>();
        if (isPurchase) {
            planList = collectionPlans.stream().filter(s -> Objects.equals(s.getControlPurchaseFlag(), BooleanEnum.YES.getValue())).toList();
        } else {
            planList = collectionPlans.stream().filter(s -> Objects.equals(s.getControlShipmentFlag(), BooleanEnum.YES.getValue())).toList();
        }
        if (CollUtil.isEmpty(planList)) {
            return true;
        }

        //获取实际收款金额和应收比例
        String currency = planList.get(0).getReceivableAmount().getCurrency();
        BigDecimal rateSum = BigDecimal.ZERO;
        BigDecimal receiveSum = BigDecimal.ZERO;
        for (CollectionPlan collectionPlan : planList) {
            if (!Objects.equals(collectionPlan.getReceivableAmount().getCurrency(), currency)) {
                throw exception(SALE_CONTRACT_COLLECTION_PLAN_CURRENCY_WRONG);
            }
            rateSum = rateSum.add(collectionPlan.getCollectionRatio());
            if (Objects.equals(collectionPlan.getExeStatus(), ExecuteStatusEnum.EXECUTED.getStatus())) {
                receiveSum = receiveSum.add(collectionPlan.getReceivableAmount().getAmount());  //已完成的收款相当于应收金额已付款
            } else {
                if (StrUtil.isEmpty(collectionPlan.getReceivedAmount().getCurrency())) {
                    continue;
                }
                if (!Objects.equals(collectionPlan.getReceivedAmount().getCurrency(), currency)) {
                    throw exception(SALE_CONTRACT_COLLECTION_PLAN_CURRENCY_WRONG);
                }
                receiveSum = receiveSum.add(collectionPlan.getReceivedAmount().getAmount());
            }
        }

        //读取默认差异比例
        String defaultRate = configApi.getValueByConfigKey("settlement.default.rate");
        int defaultRateValue = 3;
        try {
            defaultRateValue = parseInt(defaultRate);
        } catch (NumberFormatException ignored) {
        }

        //计算比较
        //该数量的总金额 * 控制的总比例 = 符合比例的总金额
        BigDecimal rateNeedSum = needSum.multiply(rateSum).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        //收到的金额 / 符合比例的总金额 = 收到的金额比例
        BigDecimal receiveRate = receiveSum.divide(rateNeedSum, 6, RoundingMode.HALF_UP);
        // 1 - 收到的金额比例 = 实际的差异金额比例
        BigDecimal otherRate = BigDecimal.ONE.subtract(receiveRate);
        //判断实际差额金额比例 大于 默认比例 返回异常
        if (otherRate.multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(defaultRateValue)) > 0) {
            throw exception(isPurchase ? SALE_CONTRACT_COLLECTION_PLAN_OVER_DEFAULT_PURCHASE : SALE_CONTRACT_COLLECTION_PLAN_OVER_DEFAULT_SHIPMENT);
        }
        return true;
    }

    @Override
    public Map<String, SaleContractItemDTO> getItemListByUniqueCodes(List<String> saleUniqueCodeList) {
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, saleUniqueCodeList);
        if (CollUtil.isEmpty(saleContractItems)) {
            return null;
        }
        List<SaleContractItemDTO> saleContractItemDTOS = BeanUtils.toBean(saleContractItems, SaleContractItemDTO.class);
        return saleContractItemDTOS.stream().collect(Collectors.toMap(SaleContractItemDTO::getUniqueCode, s -> s));
    }

    @Override
    public Map<Long, String> getItemCodesByIds(List<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)) {
            return null;
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, itemIds);
        if (CollUtil.isEmpty(saleContractItems)) {
            return null;
        }
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getUniqueCode));
    }


    @Override
    public Map<Long, SimpleSaleItemDTO> splitSaleContractItem(Map<Long, Tuple> saleItemQuantity) {
        if (CollUtil.isEmpty(saleItemQuantity)) {
            return Map.of();
        }
        Set<Long> saleItemIds = saleItemQuantity.keySet();
        if (CollUtil.isEmpty(saleItemIds)) {
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItemIds);
        List<String> sourceUniqueCodeList = saleContractItems.stream().map(SaleContractItem::getSourceUniqueCode).distinct().toList();
        List<SaleContractItem> sourceSaleItems = saleContractItemMapper.selectList(SaleContractItem::getUniqueCode, sourceUniqueCodeList);
        Map<String, SaleContractItem> sourceSaleItemMap = sourceSaleItems.stream().collect(Collectors.toMap(SaleContractItem::getUniqueCode, s -> s));
        Optional<Long> first = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().findFirst();
        if (first.isEmpty()) {
            return Map.of();
        }
        SaleContractDO saleContractDO = saleContractMapper.selectById(first.get());
        Map<String, Long> saleItemIdMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getUniqueCode, SaleContractItem::getId));
        List<SaleContractItem> splitSaleContractItems = new ArrayList<>();
        List<SaleContractItem> updateSaleContractItems = new ArrayList<>();
        saleContractItems.forEach(s -> {
            SaleContractItem sourceItem = sourceSaleItemMap.get(s.getSourceUniqueCode());
            // 更新明细中数据
            Tuple tuple = saleItemQuantity.get(s.getId());
            Integer quantity = tuple.get(0);
            if (Objects.isNull(quantity) || quantity == 0) {
                return;
            }
            Integer lockQuantity = Objects.isNull(tuple.get(1)) ? 0 : tuple.get(1);
            List<SplitPurchase> splitList = Objects.isNull(tuple.get(2)) ? List.of() : tuple.get(2);
            sourceItem.setNeedPurQuantity(sourceItem.getNeedPurQuantity() - quantity);
            sourceItem.setQuantity(sourceItem.getQuantity() - quantity);
            updateSaleContractItems.add(sourceItem);
            // 拆分明细
            SaleContractItem saleContractItem = BeanUtil.copyProperties(sourceItem, SaleContractItem.class);
            String uniqueCode = IdUtil.fastSimpleUUID();
            saleContractItem.setQuantity(quantity);
            saleContractItem.setNeedPurQuantity(quantity);
            saleContractItem.setRealPurchaseQuantity(quantity);
            saleContractItem.setId(null);
            if (CollUtil.isNotEmpty(splitList)) {
                saleContractItem.setSplitPurchaseFlag(BooleanEnum.YES.getValue());
                saleContractItem.setSplitPurchaseQuantity(quantity - lockQuantity);
            } else {
                saleContractItem.setSplitPurchaseFlag(BooleanEnum.NO.getValue());
                saleContractItem.setSplitPurchaseQuantity(0);
            }
            saleContractItem.setSplitPurchaseList(splitList);
            saleContractItem.setCurrentLockQuantity(0);
            saleContractItem.setRealLockQuantity(0);
            saleContractItem.setStockLockSaveReqVOList(new ArrayList<>());
            saleContractItem.setStockLockPrice(new JsonAmount());
            saleContractItem.setStockLockTotalPrice(new JsonAmount());
            saleContractItem.setSourceUniqueCode(sourceItem.getUniqueCode());
            saleContractItem.setUniqueCode(uniqueCode);
            saleContractItem.setSplitFlag(BooleanEnum.YES.getValue());

            splitSaleContractItems.add(saleContractItem);
        });
        // 重新计算明细费用
        if (CollUtil.isNotEmpty(updateSaleContractItems)) {
            updateSaleContractItems.forEach(s -> {
                CalcSaleContactUtil.calcSaleItemCost(s, saleContractDO, rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate()), configApi.getConfigMap());
            });
            saleContractItemMapper.updateBatch(updateSaleContractItems);
        }
        if (CollUtil.isNotEmpty(splitSaleContractItems)) {
            splitSaleContractItems.forEach(s -> {
                CalcSaleContactUtil.calcSaleItemCost(s, saleContractDO, rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate()), configApi.getConfigMap());
            });
            saleContractItemMapper.insertBatch(splitSaleContractItems);
        }
        return splitSaleContractItems.stream().collect(Collectors.toMap(s -> saleItemIdMap.get(s.getSourceUniqueCode()), s -> new SimpleSaleItemDTO().setId(s.getId()).setUniqueCode(s.getUniqueCode())));
    }

    @Override
    public void updateShipmentedQuantity(List<CloseShipmentDTO> closeReq) {
        if (Objects.isNull(closeReq)) {
            return;
        }
        List<Long> itemIds = closeReq.stream().map(CloseShipmentDTO::getSaleContractItemId).distinct().toList();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, itemIds);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            List<CloseShipmentDTO> closeShipmentDTOS = closeReq.stream().filter(c -> Objects.equals(c.getSaleContractItemId(), s.getId())).toList();
            if (CollUtil.isEmpty(closeShipmentDTOS)) {
                return;
            }
            int sum = closeShipmentDTOS.stream().mapToInt(CloseShipmentDTO::getShipmentQuantity).sum();
            int quantity = s.getShipmentTotalQuantity() - sum;
            if (quantity < 0) {
                throw exception(SALE_CONTRACT_SHIPMENT_TOTAL_QUANTITY_LESS);
            }
            s.setShipmentTotalQuantity(quantity);
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void rollbackSaleContractItemSplitList(List<SplitPurchase> splitList) {
        if (CollUtil.isEmpty(splitList)) {
            return;
        }
        Map<Long, List<SplitPurchase>> groupMap = splitList.stream().collect(Collectors.groupingBy(SplitPurchase::getSaleContractItemId));
        List<SaleContractItem> updateItems = new ArrayList<>();
        groupMap.forEach((k, v) -> {
            SaleContractItem saleContractItem = new SaleContractItem();
            saleContractItem.setId(k);
            Integer splitPurchaseFlag = CollUtil.isEmpty(v) ? BooleanEnum.NO.getValue() : BooleanEnum.YES.getValue();
            saleContractItem.setSplitPurchaseFlag(splitPurchaseFlag);
            saleContractItem.setSplitPurchaseList(v);
            updateItems.add(saleContractItem);
        });
        saleContractItemMapper.updateBatch(updateItems);
    }

    @Transactional(rollbackFor = Exception.class)
    @DataPermission(enable = false)
    public void genInternalContract(Map<Long, Integer> saleItemMap, Map<Long, Integer> shipmentPurchaseMap, String genContractUniqueCode,Map<Long,SimpleShipment> simpleShipmentMap) {
        if (CollUtil.isEmpty(saleItemMap)) {
            return;
        }
        Set<Long> saleItemIdSet = saleItemMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItemIdSet);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            Integer quantity = saleItemMap.get(s.getId());
            s.setQuantity(quantity);
            s.setShippedQuantity(quantity);
            s.setRealPurchaseQuantity(quantity);
            if (CollUtil.isEmpty(simpleShipmentMap)){
                return;
            }
            SimpleShipment simpleShipment = simpleShipmentMap.get(s.getId());
            if (Objects.isNull(simpleShipment)){
                return;
            }
            s.setName(simpleShipment.getDeclarationName());
            s.setNameEng(simpleShipment.getCustomsDeclarationNameEng());
            s.setHsMeasureUnit(simpleShipment.getHsMeasureUnit());
            s.setTaxRefundRate(simpleShipment.getTaxRefundRate());
            s.setHsCode(simpleShipment.getHsCode());
        });
        List<Long> contractIdList = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().toList();
        List<SaleContractDO> saleContracts = saleContractMapper.selectBatchIds(contractIdList);
        if (CollUtil.isEmpty(saleContracts)) {
            return;
        }
        Map<Long, SaleContractDO> saleContractMap = saleContracts.stream().collect(Collectors.toMap(SaleContractDO::getId, s -> s));
//        boolean allPurFlag = saleContractItems.stream().allMatch(s -> s.getRealPurchaseQuantity() + s.getRealLockQuantity() == s.getQuantity());
//        if (!allPurFlag){
//            return;
//        }
        // 真实采购价
        Map<Long, Tuple> realPurchasePriceCollect = calcGenRealPurchasePrice(saleContractItems, saleContractMap);
        // 2.生成内部购销合同
        Map<Long, Map<String, JsonAmount>> lastNodeSkuPriceMap = generateContract(saleContractItems, saleContractMap, realPurchasePriceCollect, genContractUniqueCode);
        // 替换真实退税率
        saleContractItems.forEach(s -> {
            Tuple tuple = realPurchasePriceCollect.get(s.getId());
            if (Objects.nonNull(tuple)) {
                s.setTaxRate(tuple.get(1));
            }
        });
        List<SplitPurchase> combineList = saleContractItems.stream().map(SaleContractItem::getSplitPurchaseList).flatMap(List::stream).toList();
        if (CollUtil.isNotEmpty(combineList) && CollUtil.isNotEmpty(realPurchasePriceCollect)) {
            combineList.forEach(s -> {
                Tuple tuple = realPurchasePriceCollect.get(s.getSaleContractItemId());
                if (Objects.nonNull(tuple)) {
                    s.setTaxRate(tuple.get(1));
                }
            });
        }
        Map<Long, List<SaleContractItem>> genSaleItemMap = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
        genSaleItemMap.forEach((k, v) -> {
            SaleContractDO saleContractDO = saleContractMap.get(k);
            Map<String, JsonAmount> prickMap = lastNodeSkuPriceMap.get(k);
            if (Objects.isNull(prickMap)) {
                prickMap = new HashMap<>();
            }
            // 3. 若存在组合产品分开采购的情况，生成尾部节点与加工型企业的销售合同
            List<CreatedResponse> createdResponses = handleLastToProducedContract(saleContractDO, v, combineList, prickMap, shipmentPurchaseMap, genContractUniqueCode);
            // 4. 若锁定库存中存在加工型企业，生成锁定库存的购销合同
            handleProducedLockContract(saleContractDO, v, prickMap, genContractUniqueCode);
            // 5. 转换尾部节点向2供应商采购合同，存在内部供应商时进行转换
            AtomicReference<String> codePrefix = new AtomicReference<>();
            if (CollUtil.isNotEmpty(createdResponses)){
                createdResponses.stream().findFirst().ifPresent(s->{
                    codePrefix.set(s.getCode());
                });
            }
            lastCompanyToVenderContract(saleContractDO, v, prickMap, shipmentPurchaseMap, genContractUniqueCode, codePrefix.get());
        });
    }

    @Override
    public void deleteGenContract(String genContractUniqueCode) {
        // 删除内部销售合同
        saleContractMapper.delete(new LambdaQueryWrapper<SaleContractDO>().eq(SaleContractDO::getGenContractUniqueCode, genContractUniqueCode).eq(SaleContractDO::getAutoFlag, BooleanEnum.YES.getValue()));
    }

    @Override
    public List<CreatedResponse> batchMergeToContractTask(List<Long> saleContractIdList) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(SaleContractDO::getId, saleContractIdList);
        if (CollUtil.isEmpty(saleContractDOS)) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }
        List<SaleContractItem> saleContractAllItems = saleContractItemMapper.selectList(
                new LambdaQueryWrapperX<SaleContractItem>().in(SaleContractItem::getContractId, saleContractIdList).eq(SaleContractItem::getConvertPurchaseFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(saleContractAllItems)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        Map<Long, JsonCompanyPath> companyPathMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getId, SaleContractDO::getCompanyPath));
        Map<Long, SimpleCompanyDTO> foreignTradeCompany = getForeignTradeCompany(companyPathMap);
        if (CollUtil.isEmpty(foreignTradeCompany)) {
            return responses;
        }
        saleContractDOS.forEach(s -> {
            SimpleCompanyDTO simpleCompanyDTO = foreignTradeCompany.get(s.getId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                s.setForeignTradeCompanyId(simpleCompanyDTO.getId());
            }
        });

        Map<Long, List<SaleContractDO>> listMap = saleContractDOS.stream().collect(Collectors.groupingBy(SaleContractDO::getForeignTradeCompanyId));

        listMap.forEach((foreignTradeId, list) -> {
            if (CollUtil.isEmpty(list) && list.size() == 0) {
                return;
            }
            list.sort(Comparator.comparing(SaleContractDO::getId));
            SaleContractDO saleContractDO = list.get(0);
            List<Long> contractIds = list.stream().map(SaleContractDO::getId).distinct().toList();


            List<SaleContractItem> saleContractItems = saleContractAllItems.stream().filter(s -> contractIds.contains(s.getContractId())).toList();
            //合同转采购计划
            PurchasePlanInfoSaveReqDTO saveReqDTO = new PurchasePlanInfoSaveReqDTO();
            {
                saveReqDTO.setCustId(saleContractDO.getCustId()).setCustCode(saleContractDO.getCustCode()).setCustName(saleContractDO.getCustName());
                saveReqDTO.setSourceType(PurchasePlanSourceTypeEnum.SALE_CONTRACT.getCode());
                saveReqDTO.setSales(saleContractDO.getSales());
                saveReqDTO.setPurchaseUserList(getPurchaseUserIds(saleContractItems));
                saveReqDTO.setLinkCodeList(saleContractDO.getLinkCodeList());
                saveReqDTO.setSales(saleContractDO.getSales());
                //采购主体默认订单路径最后一个主体
                Long companyId = -1L;
                String companyName = "";
                String companyPathStr = JsonUtils.toJsonString(saleContractDO.getCompanyPath());
                JsonCompanyPath companyPath = JsonUtils.parseObject(companyPathStr, JsonCompanyPath.class);
                JsonCompanyPath delinkCompanyPath = BeanUtil.toBean(companyPath, JsonCompanyPath.class);
                List<JsonCompanyPath> jsonCompanyPaths = TransformUtil.convertJsonCompanyPathToList(delinkCompanyPath);
                if (!CollectionUtils.isEmpty(jsonCompanyPaths)) {
                    JsonCompanyPath lastCompanyPath = jsonCompanyPaths.get(jsonCompanyPaths.size() - 1);
                    companyId = lastCompanyPath.getId();
                    SimpleCompanyDTO simpleCompanyDTO = companyApi.getCompanyDTO(lastCompanyPath.getId());
                    if (Objects.nonNull(simpleCompanyDTO)) {
                        companyName = simpleCompanyDTO.getCompanyName();
                    }
                }
                saveReqDTO.setCompanyId(companyId);
                saveReqDTO.setCompanyName(companyName);
                saveReqDTO.setCompanyPath(saleContractDO.getCompanyPath());
                saveReqDTO.setSaleContractId(saleContractDO.getId());
                saveReqDTO.setSaleContractCode(saleContractDO.getCode());
                // 审核状态为已审核
                saveReqDTO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                //状态设置为待采购
                saveReqDTO.setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode());

                //2024.12.4 预计交期,销售合同交货日期减去七天赋值
                //2025.3.17 增加配置，如果配置读取不到，默认7天
                String planDay = configApi.getValueByConfigKey("purchase.plan.day");
                int planDayInt = 7;
                try {
                    planDayInt = parseInt(planDay);
                } catch (NumberFormatException ignored) {
                }
                saveReqDTO.setEstDeliveryDate(saleContractDO.getCustDeliveryDate().minusDays(planDayInt));
            }
            List<PurchasePlanItemSaveReqDTO> children = new ArrayList<>();

            List<Long> skuIdList = saleContractItems.stream().map(SaleContractItem::getSkuId).distinct().toList();
            Map<String, List<QuoteitemDTO>> quoteItemDTOMapBySkuIdList = quoteitemApi.getQuoteItemDTOMapBySkuIdList(skuIdList);
            List<String> skuCodeList = saleContractItems.stream().map(SaleContractItem::getSkuCode).distinct().toList();
            Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
            saleContractItems.forEach(s -> {
                PurchasePlanItemSaveReqDTO item = new PurchasePlanItemSaveReqDTO();
                //报价信息
                if (CollUtil.isNotEmpty(quoteItemDTOMapBySkuIdList)) {

                    List<QuoteitemDTO> quoteItemDTOList = quoteItemDTOMapBySkuIdList.get(s.getSkuCode());
                    if (Objects.nonNull(quoteItemDTOList)) {
                        Optional<QuoteitemDTO> first = quoteItemDTOList.stream().filter(q -> q.getDefaultFlag() == 1).findFirst();
                        if (first.isPresent()) {
                            QuoteitemDTO quoteitemDTO = first.get();
                            item = BeanUtils.toBean(quoteitemDTO, PurchasePlanItemSaveReqDTO.class);
                            item.setVenderCode(quoteitemDTO.getVenderCode()).setVenderId(quoteitemDTO.getVenderId());
                            // 采购单价取默认供应商报价
                            item.setUnitPrice(quoteitemDTO.getUnitPrice());
                        }
                    }
                    if (CollUtil.isNotEmpty(hsMap)) {
                        HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                        if (Objects.nonNull(hsDataDTO)) {
                            item.setSkuUnit(hsDataDTO.getUnit());
                        }
                    }
                }

                item.setSaleContractItemId(s.getId());
                item.setSortNum(s.getSortNum());
                item.setSkuId(s.getSkuId()).setSkuCode(s.getSkuCode()).setCskuCode(s.getCskuCode()).setBarcode(s.getBarcode());
                item.setCustId(saleContractDO.getCustId()).setCustCode(saleContractDO.getCustCode());
                item.setPurchaseType(PurchaseTypeEnum.STANDARD.getCode());
                item.setLevels(1);
                // 销售合同信息
                item.setSaleContractId(saleContractDO.getId()).setSaleContractCode(saleContractDO.getCode());
                item.setFreeFlag(0);
                item.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode());
                item.setSkuType(s.getSkuType());
                item.setSkuId(s.getSkuId()).setSkuName(s.getName()).setMainPicture(s.getMainPicture());

                UserDept userDept = s.getPurchaseUser();
                if (ObjectUtil.isNotNull(userDept)) {
                    item.setPurchaseUserId(userDept.getUserId()).setPurchaseUserName(userDept.getNickname())
                            .setPurchaseUserDeptId(userDept.getDeptId()).setPurchaseUserDeptName(userDept.getDeptName());
                }
                item.setSaleQuantity(s.getQuantity());
                item.setCurrentLockQuantity(s.getCurrentLockQuantity());
                item.setNeedPurQuantity(s.getNeedPurQuantity());
                item.setBoxCount(s.getBoxCount());
                item.setQtyPerInnerbox(s.getQtyPerInnerbox());
                item.setQtyPerOuterbox(s.getQtyPerOuterbox());
                item.setSaleLockQuantity(s.getCurrentLockQuantity());
                item.setLockQuantity(s.getCurrentLockQuantity());

                //辅料相关
                item.setAuxiliaryPurchaseContractCode(null);
                item.setSpecRemark(null).setAnnex(null);
                item.setAuxiliaryCskuCode(null).setAuxiliarySkuCode(null).setAuxiliarySkuId(null);
                item.setAuxiliarySkuFlag(null).setAuxiliarySkuType(null);
                item.setPurchaseCompanyId(saveReqDTO.getCompanyId());
                item.setPurchaseCompanyName(saveReqDTO.getCompanyName());
                item.setSourceUniqueCode(s.getUniqueCode());
                item.setSaleItemUniqueCode(s.getUniqueCode());
                item.setCustProFlag(s.getCustProFlag());
                item.setOwnBrandFlag(s.getOwnBrandFlag());
                children.add(item);
            });
            saveReqDTO.setChildren(children);

            responses.addAll(purchasePlanApi.createPurchasePlan(saveReqDTO));
            // 记录操作日志
            OperateLogUtils.addExt(SaleDict.OPERATOR_EXTS_KEY, saleContractDO.getCode());
            OperateLogUtils.setContent("【下推采购计划】");
            //改写合同标记和时间
            saleContractDO.setConvertPurchaseFlag(1);
            saleContractDO.setConvertPurchaseTime(LocalDateTime.now());
            saleContractDO.setStatus(SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue());
            saleContractMapper.updateById(saleContractDO);
            orderLinkApi.updateOrderLinkStatus(saleContractDO.getCode(), BusinessNameDict.EXPORT_SALE_CONTRACT_NAME, saleContractDO.getLinkCodeList(), saleContractDO.getStatus());

        });
        return responses;
    }

    @Override
    public Long getManufactureCompanyIdByItemId(Long saleContractItemId) {
        if (Objects.isNull(saleContractItemId)) {
            return null;
        }
        SaleContractItem saleContractItem = saleContractItemMapper.selectById(saleContractItemId);
        if (Objects.isNull(saleContractItem)) {
            return null;
        }
        List<SplitPurchase> splitPurchaseList = saleContractItem.getSplitPurchaseList();
        if (CollUtil.isEmpty(splitPurchaseList)) {
            return null;
        }
        Optional<Long> first = splitPurchaseList.stream().map(SplitPurchase::getPurchaseCompanyId).findFirst();
        return first.orElse(null);
    }

    @Override
    public Map<String, List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList) {
        if(CollUtil.isEmpty(saleContractCodeList)){
            return Map.of();
        }
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getCode, SaleContractDO::getId,SaleContractDO::getSaleType).in(SaleContractDO::getCode, saleContractCodeList));
        if (CollUtil.isEmpty(saleContractDOS)) {
            return Map.of();
        }
        List<String> exportCodeList = saleContractDOS.stream().filter(s -> SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(s.getSaleType())).map(SaleContractDO::getCode).collect(Collectors.toList());
        List<String> domesticCodeList = saleContractDOS.stream().filter(s -> SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(s.getSaleType())).map(SaleContractDO::getCode).collect(Collectors.toList());
        Map<String, List<ShipmentPriceEntity>> result = new HashMap<>();
        Map<String, List<ShipmentPriceEntity>> shipmentPriceMap = shipmentApi.getShipmentPriceBySaleContractCode(exportCodeList);
        if (CollUtil.isNotEmpty(shipmentPriceMap)) {
            result.putAll(shipmentPriceMap);
        }
        Map<String, List<ShipmentPriceEntity>> stockPriceMap = stockApi.getStockPriceBySaleContractCode(domesticCodeList);
        if (CollUtil.isNotEmpty(stockPriceMap)){
            result.putAll(stockPriceMap);
        }
        List<CollectionPlan> collectionPlans = collectionPlanMapper.selectList(CollectionPlan::getContractId, saleContractDOS.stream().map(SaleContractDO::getId).toList());
        if (CollUtil.isEmpty(collectionPlans)) {
            return result;
        }
        Map<Long, String> saleIdCodeMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getId, SaleContractDO::getCode));
        collectionPlans.forEach(collectionPlan -> {
            if (CollUtil.isEmpty(collectionPlan.getChildren())) {
                return;
            }
            collectionPlan.getChildren().forEach(item -> {
                if (StrUtil.isEmpty(item.getInvoiceCode()) || !BooleanEnum.YES.getValue().equals(item.getCompletedFlag())) {
                    return;
                }
                String contractCode = saleIdCodeMap.get(collectionPlan.getContractId());
                if (StrUtil.isEmpty(contractCode)) {
                    return;
                }
                List<ShipmentPriceEntity> shipmentPriceEntities = result.get(contractCode);
                if (CollUtil.isEmpty(shipmentPriceEntities)) {
                    return;
                }
                shipmentPriceEntities.forEach(shipmentPriceEntity -> {
                    if (shipmentPriceEntity.getInvoiceCode().equals(item.getInvoiceCode())) {
                        shipmentPriceEntity.setCompletedFlag(item.getCompletedFlag());
                    }
                });
                result.put(contractCode, shipmentPriceEntities);
            });
        });
        return result;
    }

    @Override
    public void rollbackSaleContractItemPurchaseQuantity(Map<Long, Integer> purchaseQuantityMap) {
        if (CollUtil.isEmpty(purchaseQuantityMap)) {
            return;
        }
        Set<Long> itemIds = purchaseQuantityMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.stream().filter(s -> s.getUniqueCode().equals(s.getSourceUniqueCode())).forEach(s -> {
            s.setRealPurchaseQuantity(s.getRealPurchaseQuantity() + purchaseQuantityMap.get(s.getId()));
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    public List<CreatedResponse> handleLastToProducedContract(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, List<SplitPurchase> combineList, Map<String, JsonAmount> lastNodeSkuPriceMap, Map<Long, Integer> shipmentPurchaseMap, String genContractUniqueCode) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<SaleContractItem> genSaleItems = new ArrayList<>();
        saleContractItems.forEach(s -> {
            genSaleItems.add(BeanUtil.copyProperties(s, SaleContractItem.class));
        });
        List<String> skuCodeList = genSaleItems.stream().map(SaleContractItem::getSkuCode).toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
        // 筛选组合产品分开采购的明细
        List<SaleContractItem> filteredItemlist = genSaleItems.stream().filter(x -> {
            // 产品类型-组合产品
            SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
            Integer skuType = skuDTO.getSkuType();
            // 采购模式-组套件采购
            return CollUtil.isNotEmpty(x.getSplitPurchaseList());
        }).toList();
        if (CollectionUtils.isEmpty(filteredItemlist)) {
            return responses;
        }

        // 获取采购计划尾部节点
        JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
        if (Objects.isNull(companyPath)) {
            return responses;
        }
        JsonCompanyPath lastCompanyPath = getLastNode(companyPath);
        Long lastCompanyId = lastCompanyPath.getId() != null ? lastCompanyPath.getId() : saleContractDO.getCompanyId();

        // 加工资质企业主键
        Long producedCompanyId = combineList.stream().findFirst().get().getPurchaseCompanyId();

        // 生成销售合同
        SaleContractSaveReqVO genSaleContractReq = BeanUtil.copyProperties(saleContractDO, SaleContractSaveReqVO.class);
        genSaleContractReq.setId(null);
        genSaleContractReq.setGenContractUniqueCode(genContractUniqueCode);
        genSaleContractReq.setCheckFlag(Boolean.FALSE);
        genSaleContractReq.setSubmitFlag(BooleanEnum.NO.getValue());
        genSaleContractReq.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        genSaleContractReq.setAutoFlag(BooleanEnum.YES.getValue());
        genSaleContractReq.setCompanyId(lastCompanyId);
        genSaleContractReq.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
        genSaleContractReq.setSourceContractId(saleContractDO.getId());
        genSaleContractReq.setSourceContractCode(saleContractDO.getCode());
        SimpleCompanyDTO lastCompanyDTO = companyApi.getCompanyDTO(lastCompanyId);
        if (ObjectUtil.isNotNull(lastCompanyDTO)) {
            Integer companyNature = lastCompanyDTO.getCompanyNature();
            if (companyNature.intValue() == CompanyNatureEnum.INTERNAL_CUST.getValue()) {
                genSaleContractReq.setSaleType(SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue());
            } else {
                genSaleContractReq.setSaleType(SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue());
            }
            // 若尾部节点为工厂具有加工资质，则不生成销售合同
            if (companyNature.intValue() == CompanyNatureEnum.FACTORY.getValue()) {
                return responses;
            }
        }
        // 根据当前路径主键查询客户，补全客户相关信息
        SimpleCustRespDTO simpleCustRespDTO = custApi.getSimpleCustByInternalCompanyId(lastCompanyId);
        if (ObjectUtil.isNull(simpleCustRespDTO)) {
            throw exception(CUST_NOT_EXISTS);
        }
        AtomicInteger sortNum = new AtomicInteger(1);
        Map<String, String> uniqueMap = new HashMap<>();
        filteredItemlist.forEach(x -> {
            if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                uniqueMap.put(x.getUniqueCode(), IdUtil.fastSimpleUUID());
            }
        });
        List<SaleContractItem> children = filteredItemlist.stream().map(x -> {
            SaleContractItem contractItemSaveDTO = BeanUtil.copyProperties(x, SaleContractItem.class);
            if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                String uniqueCode = uniqueMap.get(x.getUniqueCode());
                contractItemSaveDTO.setUniqueCode(uniqueCode);
                contractItemSaveDTO.setSourceUniqueCode(uniqueCode);
            } else {
                String uniqueCode = uniqueMap.get(x.getSourceUniqueCode());
                contractItemSaveDTO.setUniqueCode(IdUtil.fastSimpleUUID());
                contractItemSaveDTO.setSourceUniqueCode(uniqueMap.get(x.getSourceUniqueCode()));
            }
            contractItemSaveDTO.setId(null);
            Integer quantity = shipmentPurchaseMap.get(x.getId());
            contractItemSaveDTO.setQuantity(quantity);
            contractItemSaveDTO.setRealPurchaseQuantity(quantity);
            contractItemSaveDTO.setNeedPurQuantity(0);
            contractItemSaveDTO.setCurrentLockQuantity(BigDecimal.ZERO.intValue());
            contractItemSaveDTO.setTotalSaleAmount(changeCurrency(x.getTotalSaleAmount(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setPurchaseCurrency(x.getPurchaseCurrency());
            contractItemSaveDTO.setPurchasePackagingPrice(changeCurrency(x.getPurchasePackagingPrice(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setPurchaseWithTaxPrice(changeCurrency(x.getPurchaseWithTaxPrice(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setName(x.getName());
            if (CollUtil.isNotEmpty(lastNodeSkuPriceMap)) {
                JsonAmount unitPrice = lastNodeSkuPriceMap.get(x.getSkuCode());
                if (ObjectUtil.isNotNull(unitPrice)) {
                    contractItemSaveDTO.setUnitPrice(changeCurrency(unitPrice, simpleCustRespDTO.getCurrency()));
                }
            } else {
                contractItemSaveDTO.setUnitPrice(changeCurrency(x.getUnitPrice(), simpleCustRespDTO.getCurrency()));
            }

            contractItemSaveDTO.setSortNum(sortNum.get());
            sortNum.getAndIncrement();
            return contractItemSaveDTO;
        }).toList();
        genSaleContractReq.setChildren(children);

        genSaleContractReq.setCompanyId(producedCompanyId);
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(producedCompanyId);
        if (Objects.nonNull(companyDTO)) {
            genSaleContractReq.setCompanyName(companyDTO.getCompanyName());
        }
        genSaleContractReq.setInternalCustId(simpleCustRespDTO.getId());
        genSaleContractReq.setInternalCustCode(simpleCustRespDTO.getCode());
        genSaleContractReq.setInternalCustName(simpleCustRespDTO.getName());
        genSaleContractReq.setAutoFlag(BooleanEnum.YES.getValue());
        genSaleContractReq.setCustId(simpleCustRespDTO.getId());
        genSaleContractReq.setCustCode(simpleCustRespDTO.getCode());
        genSaleContractReq.setCustName(simpleCustRespDTO.getName());
        genSaleContractReq.setCurrency(simpleCustRespDTO.getCurrency());
        logger.info("尾部节点与加工型企业-生成销售合同：{}", JsonUtils.toJsonString(genSaleContractReq));
        CalcContactUtil.checkItemCost(genSaleContractReq, configApi.getConfigMap(), rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate()), genSaleContractReq.getCurrency());
        return createSaleContract(genSaleContractReq);
    }

    public void handleProducedLockContract(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, Map<String, JsonAmount> lastNodeSkuPriceMap, String genContractUniqueCode) {
        // 1. 查询加工资质的公司及采购计划产品明细
        List<SaleContractItem> genSaleItems = new ArrayList<>();
        saleContractItems.forEach(s -> {
            genSaleItems.add(BeanUtil.copyProperties(s, SaleContractItem.class));
        });
        // 获取可加工的公司列表
        List<SimpleCompanyDTO> producedCompanyDTOList = companyApi.listProducedCompany();
        if (CollectionUtils.isEmpty(producedCompanyDTOList)) {
            return;
        }
        List<Long> producedCompanyIdList = producedCompanyDTOList.stream().map(SimpleCompanyDTO::getId).toList();

        // 采购计划产品列表，即一级明细
        if (CollectionUtils.isEmpty(genSaleItems)) {
            return;
        }

        // 获取采购计划尾部节点
        JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
        JsonCompanyPath lastCompanyPath = getLastNode(companyPath);
        Long purchaseCompanyId = lastCompanyPath.getId() != null ? lastCompanyPath.getId() : saleContractDO.getCompanyId();

        // 2. 过滤当前计划的锁定库存中，存在加工资质企业的锁定明细
        List<StockLockSaveReqVO> stockLockSaveReqVOS = genSaleItems.stream().map(SaleContractItem::getStockLockSaveReqVOList).flatMap(List::stream).toList();
        if (CollectionUtils.isEmpty(stockLockSaveReqVOS)) {
            return;
        }
        // 加工资质企业及产品信息，key->公司主键，value->产品明细编码列表
        Map<Long, List<Long>> producedCompanySkuMap = new HashMap<>();
        List<StockLockSaveReqVO> producedCompanyStockLockList = stockLockSaveReqVOS.stream().filter(x -> {
            Long companyId = x.getCompanyId();
            if (producedCompanyIdList.contains(companyId) && companyId != purchaseCompanyId) {
                List<Long> producedCompanySkuCodeList = producedCompanySkuMap.getOrDefault(companyId, new ArrayList<>());
                producedCompanySkuCodeList.add(x.getSaleContractItemId());
                producedCompanySkuMap.put(companyId, producedCompanySkuCodeList);
                return true;
            }
            return false;
        }).toList();
        if (CollectionUtils.isEmpty(producedCompanyStockLockList)) {
            return;
        }
        if (CollectionUtils.isEmpty(producedCompanySkuMap)) {
            return;
        }
        // 3. 生成尾部节点至具有加工资质企业的购销合同
        // 汇总产品锁定库存数量
        Map<Long, Integer> producedStockLockCollect = producedCompanyStockLockList.stream().filter(s -> s.getSourceOrderLockedQuantity() != null).collect(Collectors.toMap(StockLockSaveReqVO::getSaleContractItemId, StockLockSaveReqVO::getSourceOrderLockedQuantity, Integer::sum));
        // 生成合同
        this.genProducedContract(saleContractDO, genSaleItems, producedCompanySkuMap, producedStockLockCollect, lastNodeSkuPriceMap, purchaseCompanyId, genContractUniqueCode);
    }

    private void genProducedContract(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, Map<Long, List<Long>> producedCompanySkuMap,
                                     Map<Long, Integer> producedStockLockCollect, Map<String, JsonAmount> lastNodeSkuPriceMap, Long purchaseCompanyId, String genContractUniqueCode) {

        // 生成合同
        producedCompanySkuMap.forEach((companyId, saleContractItemIdList) -> {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(companyId);
            if (Objects.isNull(simpleVenderRespDTO)) {
                throw exception(PURCHASE_PLAN_COMPANY_NO_VENDER_ERROR);
            }
            // 生成采购合同
            SavePurchaseContractReqVO purchaseContractSaveInfoReqVO = BeanUtils.toBean(saleContractDO, SavePurchaseContractReqVO.class);
            purchaseContractSaveInfoReqVO.setGenContractUniqueCode(genContractUniqueCode);
            purchaseContractSaveInfoReqVO.setSaleContractCode(saleContractDO.getCode());
            purchaseContractSaveInfoReqVO.setSaleContractId(saleContractDO.getId());
            List<SaleContractItem> productedSkuList = saleContractItems.stream().filter(x -> {
                return saleContractItemIdList.contains(x.getId());
            }).toList();
            if (CollectionUtils.isEmpty(productedSkuList)) {
                return;
            }
            List<SavePurchaseContractItemReqVO> purchaseContractItemSaveReqVOList = SaleContractConvert.INSTANCE.convertSavePurchaseContractItemReqVOList(productedSkuList);
            int sortNum = 1;
            int totalQuantity = 0;
            for (SavePurchaseContractItemReqVO vo : purchaseContractItemSaveReqVOList) {
                vo.setUniqueCode(IdUtil.fastSimpleUUID());
                // 采购数量 = 客户产品锁定数量 + 自营产品锁定数量
                String skuCode = vo.getSkuCode();
                Integer lockQuantity = producedStockLockCollect.get(vo.getSaleContractItemId());
                if (lockQuantity == null) {
                    lockQuantity = 0;
                }
                vo.setQuantity(lockQuantity);
                totalQuantity += lockQuantity;
                // 设置采购单价
                if (CollUtil.isNotEmpty(lastNodeSkuPriceMap)) {
                    JsonAmount withTaxPrice = lastNodeSkuPriceMap.get(skuCode);
                    if (ObjectUtil.isNotNull(withTaxPrice)) {
                        List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                        if (CollUtil.isNotEmpty(taxMsg)){
                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                vo.setWithTaxPrice(changeCurrency(withTaxPrice, s.getCurrency()));
                            });
                        }
                    }
                }
                vo.setSortNum(sortNum);
                sortNum++;
            }
            purchaseContractSaveInfoReqVO.setTotalQuantity(totalQuantity);
            purchaseContractSaveInfoReqVO.setChildren(purchaseContractItemSaveReqVOList);

            // 采购员及部门信息
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
            if (ObjectUtil.isNotNull(userDept)) {
                purchaseContractSaveInfoReqVO.setPurchaseUserId(userDept.getUserId());
                purchaseContractSaveInfoReqVO.setPurchaseUserName(userDept.getNickname());
                purchaseContractSaveInfoReqVO.setPurchaseUserDeptId(userDept.getDeptId());
                purchaseContractSaveInfoReqVO.setPurchaseUserDeptName(userDept.getDeptName());
            }


            // 供应商信息
            purchaseContractSaveInfoReqVO.setVenderId(simpleVenderRespDTO.getId());
            purchaseContractSaveInfoReqVO.setVenderCode(simpleVenderRespDTO.getCode());
            List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
            if (CollUtil.isNotEmpty(taxMsg)){
                taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                    purchaseContractSaveInfoReqVO.setCurrency(s.getCurrency());
                    purchaseContractSaveInfoReqVO.setTaxType(s.getTaxType());
                });
            }
            List<VenderPaymentDTO> paymentList = venderApi.getVenderPaymentList(simpleVenderRespDTO.getId());
            if (CollUtil.isNotEmpty(paymentList)) {
                paymentList.forEach(s -> {
                    if (Objects.nonNull(s.getDefaultFlag()) && s.getDefaultFlag() == 1) {
                        purchaseContractSaveInfoReqVO.setPaymentId(s.getPaymentId());
                        purchaseContractSaveInfoReqVO.setPaymentName(s.getPaymentName());
                        // 转换付款计划
                        transformPurchasePlan(s.getPaymentId(), purchaseContractSaveInfoReqVO);
                    }
                });
            }
            String venderCode = purchaseContractSaveInfoReqVO.getVenderCode();
            SimpleCompanyDTO companyByVenderCode = venderApi.getCompanyByVenderCode(venderCode);
//            if (Objects.nonNull(companyByVenderCode)) {
//                if (CompanyNatureEnum.FACTORY.getValue().equals(companyByVenderCode.getCompanyNature())) {
//                    purchaseContractSaveInfoReqVO.setSaleContractCode(null);
//                    purchaseContractSaveInfoReqVO.setSaleContractId(null);
//                }
//                purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
//            }
            purchaseContractSaveInfoReqVO.setCompanyId(purchaseCompanyId);
            purchaseContractSaveInfoReqVO.setPurchasePlanId(null);
            purchaseContractSaveInfoReqVO.setPurchasePlanCode(null);
            purchaseContractSaveInfoReqVO.setDeliveryDate(saleContractDO.getCustDeliveryDate());
            purchaseContractSaveInfoReqVO.setPurchaseTime(LocalDateTime.now());

            purchaseContractSaveInfoReqVO.setId(null);
            purchaseContractSaveInfoReqVO.setCreateTime(null);
            purchaseContractSaveInfoReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
            purchaseContractSaveInfoReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
//            if (CollUtil.isEmpty(lastNodeSkuPriceMap)) {
//                return;
//            }
            // 生成销售合同
            SaleContractSaveReqVO genSaleContractReq = BeanUtil.copyProperties(saleContractDO, SaleContractSaveReqVO.class);
            genSaleContractReq.setSourceContractId(saleContractDO.getId());
            genSaleContractReq.setGenContractUniqueCode(genContractUniqueCode);
            genSaleContractReq.setSourceContractCode(saleContractDO.getCode());
            genSaleContractReq.setId(null);
            // 根据当前路径主键查询客户，补全客户相关信息
            SimpleCustRespDTO simpleCustRespDTO = custApi.getSimpleCustByInternalCompanyId(purchaseCompanyId);
            if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                genSaleContractReq.setInternalCustId(simpleCustRespDTO.getId());
                genSaleContractReq.setInternalCustCode(simpleCustRespDTO.getCode());
                genSaleContractReq.setInternalCustName(simpleCustRespDTO.getName());

                genSaleContractReq.setCustId(simpleCustRespDTO.getId());
                genSaleContractReq.setCustCode(simpleCustRespDTO.getCode());
                genSaleContractReq.setCustName(simpleCustRespDTO.getName());
                genSaleContractReq.setCurrency(simpleCustRespDTO.getCurrency());
            }
            SimpleCompanyDTO apiCompanyDTO = companyApi.getCompanyDTO(purchaseCompanyId);
            if (ObjectUtil.isNotNull(apiCompanyDTO)) {
                if (apiCompanyDTO.getCompanyNature().intValue() == CompanyNatureEnum.INTERNAL_CUST.getValue()) {
                    genSaleContractReq.setSaleType(SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue());
                } else {
                    genSaleContractReq.setSaleType(SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue());
                }
            }

            List<SaleContractItem> saleChildren = new ArrayList<>();
            Map<String, String> uniqueMap = new HashMap<>();
            saleContractItems.forEach(x -> {
                if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                    uniqueMap.put(x.getUniqueCode(), IdUtil.fastSimpleUUID());
                }
            });
            saleContractItems.forEach(x -> {
                if (x.getUniqueCode().equals(x.getSourceUniqueCode())) {
                    String uniqueCOde = uniqueMap.get(x.getUniqueCode());
                    x.setUniqueCode(uniqueCOde);
                    x.setSourceUniqueCode(uniqueCOde);
                } else {
                    x.setUniqueCode(IdUtil.fastSimpleUUID());
                    x.setSourceUniqueCode(uniqueMap.get(x.getSourceUniqueCode()));
                }
                // 采购数量 = 客户产品锁定数量 + 自营产品锁定数量
                String skuCode = x.getSkuCode();
                // 设置采购单价
                if (CollUtil.isNotEmpty(lastNodeSkuPriceMap)) {
                    JsonAmount withTaxPrice = lastNodeSkuPriceMap.get(skuCode);
                    if (ObjectUtil.isNotNull(withTaxPrice)) {
                        if (CollUtil.isNotEmpty(taxMsg)){
                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                x.setUnitPrice(changeCurrency(withTaxPrice, s.getCurrency()));
                            });
                        }
                    }
                }
                x.setId(null);
                x.setCurrentLockQuantity(BigDecimal.ZERO.intValue());
                x.setNeedPurQuantity(0);
                x.setQuantity(x.getRealLockQuantity());
                x.setRealPurchaseQuantity(x.getRealLockQuantity());
                if (x.getQuantity() > 0) {
                    saleChildren.add(x);
                }
            });
            genSaleContractReq.setChildren(saleChildren);

            genSaleContractReq.setCompanyId(companyId);
            if (Objects.nonNull(companyDTO)) {
                genSaleContractReq.setCompanyName(companyDTO.getCompanyName());
            }
            genSaleContractReq.setCheckFlag(Boolean.FALSE);
            genSaleContractReq.setAutoFlag(BooleanEnum.YES.getValue());
            genSaleContractReq.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
            genSaleContractReq.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            logger.info("尾部节点与加工型企业锁定库存-生成销售合同:{}", JsonUtils.toJsonString(genSaleContractReq));
            CalcContactUtil.checkItemCost(genSaleContractReq, configApi.getConfigMap(), rateApi.getDailyRateMapByDate(saleContractDO.getSaleContractDate()), genSaleContractReq.getCurrency());
            List<CreatedResponse> createdResponses = createSaleContract(genSaleContractReq);
            if (CollUtil.isNotEmpty(createdResponses)) {
                createdResponses.stream().findFirst().ifPresent(s->{
                    purchaseContractSaveInfoReqVO.setSaleContractCode(s.getCode());
                });
            }
            logger.info("尾部节点与加工型企业锁定库存-生成采购合同:{}", JsonUtils.toJsonString(purchaseContractSaveInfoReqVO));
            purchaseContractApi.genPurchaseContract(purchaseContractSaveInfoReqVO);
        });
    }

    private void transformPurchasePlan(Long paymentId, SavePurchaseContractReqVO purchaseContractSaveInfoReqVO) {
        if (Objects.isNull(paymentId)) {
            return;
        }
        PaymentItemDTO paymentDTO = paymentItemApi.getPaymentDTO(paymentId);
        List<SystemPaymentPlanDTO> systemPaymentPlanDTOList = paymentDTO.getSystemPaymentPlanDTOList();
        List<PurchasePaymentPlanDTO> purchasePaymentPlanList = BeanUtils.toBean(systemPaymentPlanDTOList, PurchasePaymentPlanDTO.class);
        purchaseContractSaveInfoReqVO.setPaymentPlanDTOList(purchasePaymentPlanList);
    }

    public void lastCompanyToVenderContract(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItems, Map<String, JsonAmount> lastNodeSkuPriceMap, Map<Long, Integer> shipmentPurchaseMap, String genContractUniqueCode,String codePrefix) {
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        saleContractItems.forEach(s -> {
            List<SplitPurchase> splitPurchaseList = s.getSplitPurchaseList();
            if (CollUtil.isEmpty(splitPurchaseList)) {
                return;
            }
            Long purchaseCompanyId = splitPurchaseList.get(0).getPurchaseCompanyId();
            s.setPurchaseCompanyId(purchaseCompanyId);
            UserDept purchaseUser = s.getPurchaseUser();
            if (Objects.nonNull(purchaseUser)) {
                s.setPurchaseUserId(purchaseUser.getUserId());
            }
        });
        saleContractItems = saleContractItems.stream().filter(s -> {
            // 单据一级产品明细数据
            Long companyId = s.getPurchaseCompanyId();
            if (ObjectUtil.isNull(companyId)) {
                return false;
            }
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            // 当前主题为加工型企业时，仅添加标准采购模式产品，否则一级产品全部添加
            return CollUtil.isNotEmpty(s.getSplitPurchaseList()) && ObjectUtil.isNotNull(companyDTO) && companyDTO.getCompanyNature().intValue() == CompanyNatureEnum.FACTORY.getValue();
        }).toList();
        if (CollUtil.isEmpty(saleContractItems)) {
            return;
        }
        // 获取应商信息
        List<Long> allVenderIdList = saleContractItems.stream().map(SaleContractItem::getVenderId).toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderApi.getSimpleVenderRespDTOList(allVenderIdList);
        Map<Long, SimpleVenderRespDTO> venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, s -> s));

        // 产品类型为组合产品、采购模式为组合采购、供应商类型为外部公司时（修改当前明细:采购主体-当前采购主体，供应商主体-拆分后明细产品采购主体）
        if (!CollectionUtils.isEmpty(saleContractItems)) {
            // 更新供应商主体
            saleContractItems.forEach(x -> {
                List<SplitPurchase> splitPurchaseList = x.getSplitPurchaseList();
                if (CollUtil.isEmpty(splitPurchaseList)) {
                    return;
                }
                List<SplitPurchase> twoLevelsCombineList = splitPurchaseList.stream().filter(y -> y.getLevels() == 2).toList();
                if (!CollectionUtils.isEmpty(twoLevelsCombineList)) {
                    SplitPurchase purchasePlanItemToContractSaveReqVO = twoLevelsCombineList.get(0);
                    Long purchaseCompanyId = purchasePlanItemToContractSaveReqVO.getPurchaseCompanyId();
                    SimpleVenderRespDTO simpleVender = venderApi.getSimpleVenderByInternalCompanyId(purchaseCompanyId);
                    if (Objects.nonNull(simpleVender)) {
                        x.setVenderId(simpleVender.getId());
                        x.setVenderCode(simpleVender.getCode());
                        x.setVenderName(simpleVender.getName());
                    }
                }
            });
            Map<Long, SaleContractItem> collect = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, Function.identity()));
            saleContractItems.forEach(x -> {
                SaleContractItem saleContractItem = collect.get(x.getId());
                x.setVenderId(saleContractItem.getVenderId());
                x.setVenderCode(saleContractItem.getVenderCode());
                x.setVenderName(saleContractItem.getVenderName());
                SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderRespDTOById(saleContractItem.getVenderId());
                if (CollUtil.isNotEmpty(lastNodeSkuPriceMap)) {
                    JsonAmount jsonAmount = lastNodeSkuPriceMap.get(x.getSkuCode());
                    if (ObjectUtil.isNotNull(jsonAmount)) {
                        List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                        if (CollUtil.isNotEmpty(taxMsg)){
                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                x.setRealPurchaseWithTaxPrice(changeCurrency(jsonAmount, s.getCurrency()));
                            });
                        }
                    }
                }
            });
        }
        // 获取尾节点主体
        JsonCompanyPath companyPath = saleContractDO.getCompanyPath();
        JsonCompanyPath lastNode = getLastNode(companyPath);
        // 过滤二级产品分开采购的数据
        //根据供应商进行聚合，满足业务不同的供应商不同的订单
        Map<Long, List<SaleContractItem>> planItemToContractCollectByVender = saleContractItems.stream().collect(Collectors.groupingBy(SaleContractItem::getVenderId));
        if (CollUtil.isNotEmpty(planItemToContractCollectByVender)) {
            planItemToContractCollectByVender.forEach((venderId, planItemByVenderList) -> {
                if (CollUtil.isNotEmpty(planItemByVenderList)) {
                    planItemByVenderList.forEach(x -> {
                        if (ObjectUtil.isNull(x.getPurchaseCompanyId())) throw exception(PURCHASE_COMPANY_INFO_EMPTY);
                    });
                    // 根据主体进行聚合，不同的主体不能合并合同
                    Map<Long, List<SaleContractItem>> planItemToContractCollectByPurchaseCompany = planItemByVenderList.stream().collect(Collectors.groupingBy(SaleContractItem::getPurchaseCompanyId));
                    if (CollUtil.isNotEmpty(planItemToContractCollectByPurchaseCompany)) {
                        planItemToContractCollectByPurchaseCompany.forEach((purchaseCompanyId, planItemByCompanyList) -> {
                            planItemByCompanyList.forEach(x -> {
                                if (ObjectUtil.isNull(x.getPurchaseUser())) throw exception(PURCHASE_USER_EMPTY);
                            });
                            // 根据采购员聚合
                            Map<Long, List<SaleContractItem>> planItemToContractCollectByPurchaseUser = planItemByCompanyList.stream().collect(Collectors.groupingBy(SaleContractItem::getPurchaseUserId));
                            if (CollUtil.isNotEmpty(planItemToContractCollectByPurchaseUser)) {
                                planItemToContractCollectByPurchaseUser.forEach((purchaseUserId, planItemByPurchaseUserList) -> {
                                    // 生成采购合同
                                    SavePurchaseContractReqVO purchaseContractSaveInfoReqVO = BeanUtils.toBean(saleContractDO, SavePurchaseContractReqVO.class);
                                    purchaseContractSaveInfoReqVO.setGenContractUniqueCode(genContractUniqueCode);
                                    purchaseContractSaveInfoReqVO.setRollbackCodeToShipmentFlag(BooleanEnum.YES.getValue());
                                    purchaseContractSaveInfoReqVO.setSaleContractCode(codePrefix);
                                    purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                                    purchaseContractSaveInfoReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
                                    purchaseContractSaveInfoReqVO.setCompanyId(lastNode.getId());
                                    List<SavePurchaseContractItemReqVO> purchaseContractItemSaveReqVOList = new ArrayList<>();
                                    int sortNum = 1;
                                    for (SaleContractItem saleContractItem : planItemByCompanyList) {
                                        SavePurchaseContractItemReqVO vo = BeanUtils.toBean(saleContractItem, SavePurchaseContractItemReqVO.class);
                                        vo.setSourceUniqueCode(vo.getUniqueCode());
                                        vo.setSortNum(sortNum);
                                        Integer quantity = shipmentPurchaseMap.get(saleContractItem.getId());
                                        vo.setQuantity(quantity);
                                        vo.setWithTaxPrice(saleContractItem.getRealPurchaseWithTaxPrice());
                                        vo.setSaleContractItemId(saleContractItem.getId());
                                        purchaseContractItemSaveReqVOList.add(vo);
                                        sortNum++;
                                    }
                                    purchaseContractSaveInfoReqVO.setChildren(purchaseContractItemSaveReqVOList);
                                    Optional<SaleContractItem> first = planItemByPurchaseUserList.stream().findFirst();
                                    if (first.isPresent()) {
                                        SaleContractItem saleContractItem = first.get();
                                        Long vendorId = saleContractItem.getVenderId();
                                        purchaseContractSaveInfoReqVO.setVenderId(vendorId);
                                        purchaseContractSaveInfoReqVO.setVenderCode(saleContractItem.getVenderCode());
                                        UserDept purchaseUser = saleContractItem.getPurchaseUser();
                                        if (Objects.nonNull(purchaseUser)) {
                                            purchaseContractSaveInfoReqVO.setPurchaseUserId(purchaseUser.getUserId()).setPurchaseUserName(purchaseUser.getNickname())
                                                    .setPurchaseUserDeptId(purchaseUser.getDeptId())
                                                    .setPurchaseUserDeptName(purchaseUser.getDeptName());
                                        }

                                        if (Objects.nonNull(vendorId)) {
                                            SimpleVenderRespDTO simpleVenderRespDTO = venderMap.get(vendorId);
                                            if (Objects.nonNull(simpleVenderRespDTO)) {
                                                List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                                                if (CollUtil.isNotEmpty(taxMsg)){
                                                    taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                                        purchaseContractSaveInfoReqVO.setTaxType(s.getTaxType());
                                                        purchaseContractSaveInfoReqVO.setCurrency(s.getCurrency());
                                                    });
                                                }
                                                List<VenderPaymentDTO> paymentList = venderApi.getVenderPaymentList(vendorId);
                                                if (CollUtil.isNotEmpty(paymentList)) {
                                                    paymentList.forEach(s -> {
                                                        if (Objects.nonNull(s.getDefaultFlag()) && s.getDefaultFlag() == 1) {
                                                            purchaseContractSaveInfoReqVO.setPaymentId(s.getPaymentId());
                                                            purchaseContractSaveInfoReqVO.setPaymentName(s.getPaymentName());
                                                            // 转换付款计划
                                                            transformPurchasePlan(s.getPaymentId(), purchaseContractSaveInfoReqVO);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    String venderCode = purchaseContractSaveInfoReqVO.getVenderCode();
                                    SimpleCompanyDTO companyByVenderCode = venderApi.getCompanyByVenderCode(venderCode);
                                    if (Objects.nonNull(companyByVenderCode)) {
//                                        if(CompanyNatureEnum.FACTORY.getValue().equals(companyByVenderCode.getCompanyNature())){
//                                            purchaseContractSaveInfoReqVO.setSaleContractCode(null);
//                                            purchaseContractSaveInfoReqVO.setSaleContractId(null);
//                                        }
                                        purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
                                    } else {
                                        return;
                                    }
                                    purchaseContractSaveInfoReqVO.setPurchasePlanId(null);
                                    purchaseContractSaveInfoReqVO.setPurchasePlanCode(null);
                                    purchaseContractSaveInfoReqVO.setDeliveryDate(saleContractDO.getCustDeliveryDate());
                                    purchaseContractSaveInfoReqVO.setPurchaseTime(LocalDateTime.now());
                                    purchaseContractSaveInfoReqVO.setId(null);
                                    purchaseContractSaveInfoReqVO.setCreateTime(null);
                                    purchaseContractSaveInfoReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
                                    logger.info("尾部节点-供应商/加工型企业采购合同：{}", JsonUtils.toJsonString(purchaseContractSaveInfoReqVO));
                                    purchaseContractApi.genPurchaseContract(purchaseContractSaveInfoReqVO);
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void exportWord(Long id, String reportCode, Long reportId, Long companyId, HttpServletResponse response) {
        if (id == null) {
            throw exception(SALE_CONTRACT_ID_NULL);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        ReportDTO reportDTO;
        if (reportId != null) {
            reportDTO = reportApi.getReportById(reportId);
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
        SaleContractDO saleContractDO = saleContractMapper.selectById(id);
        if (saleContractDO == null) {
            throw exception(SALECONTRACT_NULL);
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(SaleContractItem::getContractId, id);
        if (CollUtil.isEmpty(saleContractItemList)) {
            throw exception(SALECONTRACTITEM_NULL);
        }
        HashMap<String, Object> params = getDomesticWordParams(saleContractDO, saleContractItemList);
        String projectPath = System.getProperty("user.dir");
        reportApi.exportWord(response, reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params);
    }
    public HashMap<String, Object> getDomesticWordParams(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        HashMap<String, Object> params = getParamsCommonDomestic(saleContractDO,companyDTO);
        // 创建表格
        TableRenderData table = Tables.ofA4ExtendWidth()
                .percentWidth("100%", new int[]{5,15,15,27,8,12,6,12}) // 调整列宽以匹配图片
                .create();
        // 表头
        RowRenderData headerRow = Rows.of("序号", "泛太货号", "客户货号","产品描述","单位", "单价", "数量", "金额").rowExactHeight(1).center().create();
        ParagraphStyle centerStyle = ParagraphStyle.builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        ParagraphStyle leftStyle = ParagraphStyle.builder()
                .withAlign(ParagraphAlignment.LEFT)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        ParagraphStyle rightStyle = ParagraphStyle.builder()
                .withAlign(ParagraphAlignment.RIGHT)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        final int[] currentRow = {Integer.parseInt(Long.toString(table.getRows().stream().count()))};
        // 设置表头样式
        headerRow.getCells().get(0).getParagraphs().get(0).setParagraphStyle(centerStyle); // 序
        headerRow.getCells().get(1).getParagraphs().get(0).setParagraphStyle(centerStyle); // 编号
        headerRow.getCells().get(2).getParagraphs().get(0).setParagraphStyle(centerStyle); // 客户货号
        headerRow.getCells().get(3).getParagraphs().get(0).setParagraphStyle(centerStyle);   // 描述
        headerRow.getCells().get(4).getParagraphs().get(0).setParagraphStyle(centerStyle); // 单位
        headerRow.getCells().get(5).getParagraphs().get(0).setParagraphStyle(centerStyle);  // 单价
        headerRow.getCells().get(6).getParagraphs().get(0).setParagraphStyle(centerStyle);  // 数量
        headerRow.getCells().get(7).getParagraphs().get(0).setParagraphStyle(centerStyle);  // 金额
        table.addRow(headerRow);

        // 数据行
        AtomicInteger rowIndex = new AtomicInteger(1);
        List<Long> skuIdList = saleContractItemList.stream().map(SaleContractItem::getSkuId).distinct().collect(Collectors.toList());
        Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(skuIdList);
        saleContractItemList.forEach(sci -> {
            SkuDTO skuDTO = skuDTOMap.get(sci.getSkuId());
//            List<SimpleFile> pictures = skuDTO.getPicture();
//            if (CollUtil.isEmpty(pictures)) {
//                throw exception(SKU_PICTURE_NULL);
//            }
//            SimpleFile simpleFile = pictures.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
//            if (simpleFile == null) {
//                throw exception(SKU_MAIN_PICTURE_NULL);
//            }
            String thumbnail = skuDTO.getThumbnail();
            if (StrUtil.isEmpty(thumbnail)) {
                throw exception(new ErrorCode(SKU_THUMBNAIL_NULL.getCode(), SKU_THUMBNAIL_NULL.getMsg() + "，产品编号：" + skuDTO.getCode() + "，基础产品编号：" + skuDTO.getBasicSkuCode() + "，客户货号：" + skuDTO.getCskuCode()));
            }

            String measureUnit = DictFrameworkUtils.getDictDataLabel("measure_unit", skuDTO.getMeasureUnit());
            AtomicReference<BigDecimal> unitPrice = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> totalSaleAmount = new AtomicReference<>(BigDecimal.ZERO);
            Optional.ofNullable(sci.getUnitPrice()).ifPresent(s->{
                 Optional.ofNullable(s.getAmount())
                        .ifPresent(a->{
                            unitPrice.set(a.setScale(2,RoundingMode.HALF_UP));
                        });
            });
            Optional.ofNullable(sci.getTotalSaleAmount()).ifPresent(s->{
                 Optional.ofNullable(s.getAmount())
                        .ifPresent(a->{
                            totalSaleAmount.set(a.setScale(2,RoundingMode.HALF_UP));
                        });
            });
            RowRenderData dataRow = Rows.of(String.valueOf(rowIndex.getAndIncrement()),
                            sci.getBasicSkuCode(),
                            skuDTO.getCskuCode(),
                            skuDTO.getDescriptionEng(),
                            measureUnit,
                            unitPrice.get().toString(),
                            sci.getQuantity().toString(),
                            totalSaleAmount.get().toString()
                            )
                    .center().create();
            RowRenderData specRow = Rows.of(CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,
                            CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,
                            CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,CommonDict.EMPTY_STR)
                    .center().create();
            dataRow.getCells().get(0).getParagraphs().get(0).setParagraphStyle(centerStyle);
            dataRow.getCells().get(1).getParagraphs().get(0).setParagraphStyle(leftStyle);
            dataRow.getCells().get(2).getParagraphs().get(0).setParagraphStyle(leftStyle);
            dataRow.getCells().get(3).getParagraphs().get(0).setParagraphStyle(centerStyle);
            dataRow.getCells().get(4).getParagraphs().get(0).setParagraphStyle(centerStyle);
            dataRow.getCells().get(5).getParagraphs().get(0).setParagraphStyle(rightStyle);
            dataRow.getCells().get(6).getParagraphs().get(0).setParagraphStyle(rightStyle);
            dataRow.getCells().get(7).getParagraphs().get(0).setParagraphStyle(rightStyle);
            List<ParagraphRenderData> paragraphs = new ArrayList<>();
            ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
            try {
                byte[] content = fileApi.getFileContent(thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
                PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(80, 80).create();
                paragraphRenderData.addPicture(pictureRenderData);
                paragraphs.add(paragraphRenderData);
                logger.info("缩略图{}加载成功", thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            } catch (Exception e) {
                logger.warn("缩略图{}加载失败，详情：" + e.getMessage(), thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            }
            specRow.getCells().get(1).setParagraphs(paragraphs);
            table.addRow(dataRow);
            table.addRow(specRow);
            currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
            // 行合并：当前行的第二列和第三列
            ruleBuilder.map(
                    MergeCellRule.Grid.of(currentRow[0]-1, 1), // 当前行第二列
                    MergeCellRule.Grid.of(currentRow[0]-1, 2)  // 当前行第三列
            );
            // 列合并：当前行的第四列与上一行第四列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 3), // 上一行第四列
                        MergeCellRule.Grid.of(currentRow[0] -1, 3)      // 当前行第四列
                );
            }
            // 列合并：当前行的第五列与上一行第五列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 4), // 上一行第五列
                        MergeCellRule.Grid.of(currentRow[0] -1, 4)      // 当前行第五列
                );
            }
            // 列合并：当前行的第六列与上一行第六列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 5), // 上一行第六列
                        MergeCellRule.Grid.of(currentRow[0] -1, 5)      // 当前行第六列
                );
            }
            // 列合并：当前行的第七列与上一行第七列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 6), // 上一行第七列
                        MergeCellRule.Grid.of(currentRow[0] -1, 6)      // 当前行第七列
                );
            }
            // 列合并：当前行的第八列与上一行第八列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 7), // 上一行第八列
                        MergeCellRule.Grid.of(currentRow[0] -1, 7)      // 当前行第八列
                );
            }
        });
        // 合计行
        // 添加备注、交货日期、合计行
        RowRenderData footerRow = Rows.of(
                "备注：",
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR
        ).center().create();

        // 设置备注列居左对齐
        footerRow.getCells().get(0).getParagraphs().get(0).setParagraphStyle(leftStyle);
        table.addRow(footerRow);
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        // 添加交货日期行
        String inputDate=CommonDict.EMPTY_STR;
        if (saleContractDO.getCustDeliveryDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            inputDate= dtf.format(saleContractDO.getCustDeliveryDate());
        }
        RowRenderData deliveryRow = Rows.of(
                "交货日期：" + inputDate,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR
        ).center().create();
        deliveryRow.getCells().get(0).getParagraphs().get(0).setParagraphStyle(leftStyle);
        table.addRow(deliveryRow);
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        // 添加合计行
        Optional<BigDecimal> totalAmountOpt = saleContractItemList.stream().map(s -> s.getTotalSaleAmount()).filter(Objects::nonNull)
                .filter(s -> Objects.nonNull(s.getAmount()))
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal::add);
        RowRenderData totalRow = Rows.of(
                "合计："+AmountToChineseUtil.toChineseUpper(totalAmountOpt.get()),
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                totalAmountOpt.get().setScale(2,RoundingMode.HALF_UP).toString(),
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR
        ).center().create();
        totalRow.getCells().get(0).getParagraphs().get(0).setParagraphStyle(centerStyle);
        totalRow.getCells().get(5).getParagraphs().get(0).setParagraphStyle(rightStyle); // 合计金额右对齐
        table.addRow(totalRow);
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 4))
                .map(MergeCellRule.Grid.of(currentRow[0] - 1, 5), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        // 将表格放入参数
        table.setMergeRule(ruleBuilder.build());
        params.put("skuTable", table);
        return params;
    }

    /**
     * 内销合同excel导出参数获取
     * @param saleContractDO
     * @return
     */
    public HashMap<String, Object> getExportParamsDomestic(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        HashMap<String, Object> params = getParamsCommonDomestic(saleContractDO,companyDTO);
        if (saleContractDO.getCustDeliveryDate() != null) {
            //交货日期
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            params.put("custDeliveryDate", dtf.format(saleContractDO.getCustDeliveryDate()));
        }
        Optional<BigDecimal> totalAmountOpt = saleContractItemList.stream().map(s -> s.getTotalSaleAmount()).filter(Objects::nonNull)
                .filter(s -> Objects.nonNull(s.getAmount()))
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal::add);
        params.put("totalAmountChinese", AmountToChineseUtil.toChineseUpper(totalAmountOpt.get()));
        params.put("totalAmount", totalAmountOpt.get().setScale(2,RoundingMode.HALF_UP).toString());
        return params;
    }
    public HashMap<String, Object> getParamsCommonDomestic(SaleContractDO saleContractDO,SimpleCompanyDTO companyDTO) {
        HashMap<String, Object> params = new HashMap<>();
        // 基本信息
        params.put("companyNameEng", companyDTO.getCompanyNameEng());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("code", saleContractDO.getCode());
        params.put("custName", saleContractDO.getCustName());
        params.put("saleType", "内销合同");
        params.put("settlementType", saleContractDO.getSettlementName());
        params.put("bank", companyDTO.getCompanyBankRespDTO().getBankName());
        params.put("bankPoc", companyDTO.getName());
        params.put("bankAccount", companyDTO.getCompanyBankRespDTO().getBankCode());
        String custCode = saleContractDO.getCustCode();
        String custPoc = custApi.getCustPocByCustCode(custCode);
        if (StrUtil.isNotEmpty(custPoc)) {
            params.put("custPoc", custPoc);
        }
        // 输入日期
        if (saleContractDO.getInputDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String inputDate = dtf.format(saleContractDO.getInputDate());
            params.put("date", inputDate);
        }
        return params;
    }

    /**
     * 外销合同excel导出参数获取
     * @param saleContractDO
     * @return
     */
    public HashMap<String, Object> getExportParams(SaleContractDO saleContractDO) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        HashMap<String, Object> params = getParamsCommon(saleContractDO,companyDTO);
        return params;
    }
    public HashMap<String, Object> getWordParams(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        HashMap<String, Object> params = getParamsCommon(saleContractDO,companyDTO);
        //合同明细
        RowRenderData row0 = Rows.of("Vertak No.", "RefNo.", "(1) 货物名称规格Name of commodity & Specifitions", "(2) 数量 Quantity", "(3) 单价 Unit price(" + saleContractDO.getCurrency() + ")", "(4) 总价 Total value (" + saleContractDO.getCurrency() + ")").rowExactHeight(1).center().create();
        ParagraphStyle centerStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        row0.getCells().get(1).getParagraphs().get(0).setParagraphStyle(centerStyle);
        row0.getCells().get(2).getParagraphs().get(0).setParagraphStyle(centerStyle);
        row0.getCells().get(3).getParagraphs().get(0).setParagraphStyle(centerStyle);
        row0.getCells().get(4).getParagraphs().get(0).setParagraphStyle(centerStyle);
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{13, 13, 41, 10, 10, 13}).create();
//        //添加表格头部信息
        table.addRow(row0);
        ParagraphStyle leftStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        final int[] currentRow = {parseInt(Long.toString(table.getRows().stream().count()))};
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        // 根据赠品数量拆分销售明细
        saleContractItemList.forEach(sci -> {
            SkuDTO skuDTO = skuApi.getSkuDTO(sci.getSkuId());
            // List<SimpleFile> pictures = skuDTO.getPicture();
            // if (CollUtil.isEmpty(pictures)) {
            //     throw exception(SKU_PICTURE_NULL);
            // }
            // SimpleFile simpleFile = pictures.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
            String thumbnail = skuDTO.getThumbnail();
            if (StrUtil.isEmpty(thumbnail)) {
                throw exception(new ErrorCode(SKU_THUMBNAIL_NULL.getCode(), SKU_THUMBNAIL_NULL.getMsg() + "，产品编号：" + skuDTO.getCode() + "，基础产品编号：" + skuDTO.getBasicSkuCode() + "，客户货号：" + skuDTO.getCskuCode()));
            }
            RowRenderData dataRow = Rows.of(sci.getBasicSkuCode(), sci.getCskuCode(), skuDTO.getDescriptionEng(),
                            sci.getQuantity().toString(), sci.getUnitPrice().getCheckAmount().toString(),
                            sci.getTotalSaleAmount().getCheckAmount().toString())
                    .center().create();
            RowRenderData specRow = Rows.of(CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,
                            CommonDict.EMPTY_STR,CommonDict.EMPTY_STR,
                            CommonDict.EMPTY_STR)
                    .center().create();
            List<ParagraphRenderData> desc = dataRow.getCells().get(2).getParagraphs();
            desc.get(0).setParagraphStyle(leftStyle);
            List<ParagraphRenderData> paragraphs = new ArrayList<>();
            ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
            try {
                byte[] content = fileApi.getFileContent(thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
                PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(80, 80).create();

                paragraphRenderData.addPicture(pictureRenderData);
                paragraphs.add(paragraphRenderData);
                specRow.getCells().get(0).setParagraphs(paragraphs);
                logger.info("缩略图{}加载成功", thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            } catch (Exception e) {
                logger.warn("缩略图{}加载失败，详情：" + e.getMessage(), thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            }
            table.addRow(dataRow);
            table.addRow(specRow);
            currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
            // 行合并：当前行的第一列和第二列
            ruleBuilder.map(
                    MergeCellRule.Grid.of(currentRow[0]-1, 0), // 当前行第一列
                    MergeCellRule.Grid.of(currentRow[0]-1, 1)  // 当前行第二列
            );
            // 列合并：当前行的第三列与上一行第三列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 2), // 上一行第三列
                        MergeCellRule.Grid.of(currentRow[0] -1, 2)      // 当前行第三列
                );
            }
            // 列合并：当前行的第四列与上一行第四列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 3), // 上一行第四列
                        MergeCellRule.Grid.of(currentRow[0] -1, 3)      // 当前行第四列
                );
            }
            // 列合并：当前行的第五列与上一行第五列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 4), // 上一行第五列
                        MergeCellRule.Grid.of(currentRow[0] -1, 4)      // 当前行第五列
                );
            }
            // 列合并：当前行的第六列与上一行第六列
            if (currentRow[0] > 0) { // 确保有上一行
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 5), // 上一行第六列
                        MergeCellRule.Grid.of(currentRow[0] -1, 5)      // 当前行第六列
                );
            }
        });
        //添加合计行
        ParagraphStyle rightStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.RIGHT)
                .withSpacingRule(LineSpacingRule.EXACT)
                .build();
        RowRenderData rowSum = Rows.of("Total:" + saleContractDO.getTotalQuantity().toString(), null, null, null, saleContractDO.getTotalGoodsValue().getCurrency() + "：" + saleContractDO.getTotalGoodsValue().getAmount(), null).rowExactHeight(1).center().create();
        rowSum.getCells().get(0).getParagraphs().get(0).setParagraphStyle(rightStyle);
        table.addRow(rowSum);
        currentRow[0] = parseInt(Long.toString(table.getRows().size()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 3))
                .map(MergeCellRule.Grid.of(currentRow[0] - 1, 4), MergeCellRule.Grid.of(currentRow[0] - 1, 5));
        table.setMergeRule(ruleBuilder.build());
        params.put("saleContractTable", table);
        //公章处理(审核通过显示)
        CustomPictureRenderData pictureRenderData = new CustomPictureRenderData();
        if (Objects.equals(saleContractDO.getAuditStatus(),BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            String pictureJson = companyDTO.getPicture();
            List<DictSimpleFileList> simpleFileLists = StrUtil.isNotEmpty(pictureJson) ? 
                JsonUtils.parseArray(pictureJson, DictSimpleFileList.class) : Collections.emptyList();
            //2.有公章才处理
            if (CollUtil.isNotEmpty(simpleFileLists)) {
                Optional<DictSimpleFileList> first = simpleFileLists.stream().filter(s -> Objects.equals(s.getValue(), OfficialSealTypeEnum.BUSINESS_STAMP.getValue().toString())).findFirst();
                if (first.isPresent()) {
                    Optional<SimpleFile> first1 = first.get().getFileList().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                    if (first1.isPresent()) {
                        SimpleFile simpleFile = first1.get();
                        //3.如果有公章，且合同已经回签 “加盖公章”
                        try {
                            byte[] content = fileApi.getFileContent(simpleFile.getFileUrl().substring(simpleFile.getFileUrl().lastIndexOf("get/") + 4));

                            // 获取图片原始尺寸
                            File inputFile = FileUtils.createTempFile(content);
                            BufferedImage image = ImageIO.read(inputFile);
                            int originalWidth = image.getWidth();
                            int originalHeight = image.getHeight();
                            inputFile.delete();

                            // 创建CustomPictureRenderData对象而不是使用Pictures.ofBytes
                            pictureRenderData.setData(content);
                            pictureRenderData.setPath(simpleFile.getFileUrl());
                            pictureRenderData.setWidth((int) (originalWidth*0.35));
                            pictureRenderData.setHeight((int) (originalHeight*0.35));
                        } catch (Exception e) {
                            logger.warn("公章图片加载失败，详情：" + e.getMessage());
                        }
                    }
                }
            }
        }
        params.put("companyImg", pictureRenderData);
        Configure configure = Configure.createDefault().plugin('%', new PoiPicPolicy());
        params.put("configure", configure);
        return params;
    }

    /**
     * 外销合同打印参数获取 - 包含HSCode列
     * @param saleContractDO 合同信息
     * @param saleContractItemList 合同明细列表
     * @return 打印参数
     */
    public HashMap<String, Object> getWordParamsHsCode(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(saleContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        HashMap<String, Object> params = getParamsCommon(saleContractDO, companyDTO);
        
        // 合同明细表头 - 在RefNo后面增加HSCode列
        RowRenderData row0 = Rows.of("Vertak No.", "RefNo.", "HSCode", "(1) 货物名称规格Name of commodity & Specifitions", "(2) 数量 Quantity", "(3) 单价 Unit price(" + saleContractDO.getCurrency() + ")", "(4) 总价 Total value (" + saleContractDO.getCurrency() + ")").rowExactHeight(1).center().create();
        
        ParagraphStyle centerStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        
        // 设置表头样式
        for (int i = 0; i < row0.getCells().size(); i++) {
            row0.getCells().get(i).getParagraphs().get(0).setParagraphStyle(centerStyle);
        }
        
        // 调整列宽比例以容纳新增的HSCode列：原来的6列变成7列
        // 原列宽：13, 13, 41, 10, 10, 13 (总计100)
        // 新列宽：12, 12, 12, 35, 9, 9, 11 (总计100，保持比例协调)
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{12, 12, 12, 35, 9, 9, 11}).create();
        table.addRow(row0);
        
        ParagraphStyle leftStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacingRule(LineSpacingRule.EXACT)
                .withSpacing(13)
                .build();
        
        final int[] currentRow = {parseInt(Long.toString(table.getRows().stream().count()))};
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        
        // 处理销售明细
        saleContractItemList.forEach(sci -> {
            SkuDTO skuDTO = skuApi.getSkuDTO(sci.getSkuId());
            String thumbnail = skuDTO.getThumbnail();
            if (StrUtil.isEmpty(thumbnail)) {
                throw exception(new ErrorCode(SKU_THUMBNAIL_NULL.getCode(), SKU_THUMBNAIL_NULL.getMsg() + "，产品编号：" + skuDTO.getCode() + "，基础产品编号：" + skuDTO.getBasicSkuCode() + "，客户货号：" + skuDTO.getCskuCode()));
            }
            
            // 获取HSCode，如果为空则显示空字符串
            String hsCode = StrUtil.isNotBlank(sci.getHsCode()) ? sci.getHsCode() : "";
            
            // 数据行：增加HSCode列
            RowRenderData dataRow = Rows.of(
                sci.getBasicSkuCode(), 
                sci.getCskuCode(), 
                hsCode,
                skuDTO.getDescriptionEng(),
                sci.getQuantity().toString(), 
                sci.getUnitPrice().getCheckAmount().toString(),
                sci.getTotalSaleAmount().getCheckAmount().toString()
            ).center().create();
            
            // 规格行：也要对应增加一列
            RowRenderData specRow = Rows.of(
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR,
                CommonDict.EMPTY_STR
            ).center().create();
            
            // 设置描述列样式
            List<ParagraphRenderData> desc = dataRow.getCells().get(3).getParagraphs(); // 描述列现在在第4列（索引3）
            desc.get(0).setParagraphStyle(leftStyle);
            
            // 处理图片
            List<ParagraphRenderData> paragraphs = new ArrayList<>();
            ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
            try {
                byte[] content = fileApi.getFileContent(thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
                PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(80, 80).create();

                paragraphRenderData.addPicture(pictureRenderData);
                paragraphs.add(paragraphRenderData);
                specRow.getCells().get(0).setParagraphs(paragraphs);
                logger.info("缩略图{}加载成功", thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            } catch (Exception e) {
                logger.warn("缩略图{}加载失败，详情：" + e.getMessage(), thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            }
            
            table.addRow(dataRow);
            table.addRow(specRow);
            currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
            
            // 行合并：第一列和第二列
            ruleBuilder.map(
                    MergeCellRule.Grid.of(currentRow[0]-1, 0), // 当前行第一列
                    MergeCellRule.Grid.of(currentRow[0]-1, 1)  // 当前行第二列
            );
            
            // 第三列（HSCode列）行合并 - 让它与specRow的第2列合并
            ruleBuilder.map(
                    MergeCellRule.Grid.of(currentRow[0]-2, 2), // 数据行第三列（HSCode）
                    MergeCellRule.Grid.of(currentRow[0]-1, 2)  // 规格行第三列（HSCode）
            );
            
            // 列合并：第四列（描述列）与上一行合并
            if (currentRow[0] > 0) {
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 3), // 上一行第四列
                        MergeCellRule.Grid.of(currentRow[0] - 1, 3)  // 当前行第四列
                );
            }
            
            // 列合并：第五列（数量列）与上一行合并
            if (currentRow[0] > 0) {
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 4), // 上一行第五列
                        MergeCellRule.Grid.of(currentRow[0] - 1, 4)  // 当前行第五列
                );
            }
            
            // 列合并：第六列（单价列）与上一行合并
            if (currentRow[0] > 0) {
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 5), // 上一行第六列
                        MergeCellRule.Grid.of(currentRow[0] - 1, 5)  // 当前行第六列
                );
            }
            
            // 列合并：第七列（总价列）与上一行合并
            if (currentRow[0] > 0) {
                ruleBuilder.map(
                        MergeCellRule.Grid.of(currentRow[0] - 2, 6), // 上一行第七列
                        MergeCellRule.Grid.of(currentRow[0] - 1, 6)  // 当前行第七列
                );
            }
        });
        
        // 添加合计行 - 需要对应7列
        ParagraphStyle rightStyle = ParagraphStyle
                .builder()
                .withAlign(ParagraphAlignment.RIGHT)
                .withSpacingRule(LineSpacingRule.EXACT)
                .build();
        
        RowRenderData rowSum = Rows.of(
            "Total:" + saleContractDO.getTotalQuantity().toString(), 
            null, 
            null, 
            null, 
            null, 
            saleContractDO.getTotalGoodsValue().getCurrency() + "：" + saleContractDO.getTotalGoodsValue().getAmount(), 
            null
        ).rowExactHeight(1).center().create();
        
        rowSum.getCells().get(0).getParagraphs().get(0).setParagraphStyle(rightStyle);
        table.addRow(rowSum);
        currentRow[0] = parseInt(Long.toString(table.getRows().size()));
        
        // 合计行的合并规则：前5列合并，第6列和第7列合并
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 4))
                .map(MergeCellRule.Grid.of(currentRow[0] - 1, 5), MergeCellRule.Grid.of(currentRow[0] - 1, 6));
        
        table.setMergeRule(ruleBuilder.build());
        params.put("saleContractTable", table);
        
        // 公章处理(审核通过显示)
        CustomPictureRenderData pictureRenderData = new CustomPictureRenderData();
        if (Objects.equals(saleContractDO.getAuditStatus(), BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            String pictureJson = companyDTO.getPicture();
            List<DictSimpleFileList> simpleFileLists = StrUtil.isNotEmpty(pictureJson) ? 
                JsonUtils.parseArray(pictureJson, DictSimpleFileList.class) : Collections.emptyList();
            if (CollUtil.isNotEmpty(simpleFileLists)) {
                Optional<DictSimpleFileList> first = simpleFileLists.stream().filter(s -> Objects.equals(s.getValue(), OfficialSealTypeEnum.BUSINESS_STAMP.getValue().toString())).findFirst();
                if (first.isPresent()) {
                    Optional<SimpleFile> first1 = first.get().getFileList().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                    if (first1.isPresent()) {
                        SimpleFile simpleFile = first1.get();
                        try {
                            byte[] content = fileApi.getFileContent(simpleFile.getFileUrl().substring(simpleFile.getFileUrl().lastIndexOf("get/") + 4));

                            File inputFile = FileUtils.createTempFile(content);
                            BufferedImage image = ImageIO.read(inputFile);
                            int originalWidth = image.getWidth();
                            int originalHeight = image.getHeight();
                            inputFile.delete();

                            pictureRenderData.setData(content);
                            pictureRenderData.setPath(simpleFile.getFileUrl());
                            pictureRenderData.setWidth((int) (originalWidth*0.35));
                            pictureRenderData.setHeight((int) (originalHeight*0.35));
                        } catch (Exception e) {
                            logger.warn("公章图片加载失败，详情：" + e.getMessage());
                        }
                    }
                }
            }
        }
        params.put("companyImg", pictureRenderData);
        Configure configure = Configure.createDefault().plugin('%', new PoiPicPolicy());
        params.put("configure", configure);
        return params;
    }
    public HashMap<String, Object> getParamsCommon(SaleContractDO saleContractDO,SimpleCompanyDTO companyDTO ) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("code", saleContractDO.getCode());
        params.put("custPo", saleContractDO.getCustPo());
        //日期
        if (saleContractDO.getInputDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String inputDate = dtf.format(saleContractDO.getInputDate());
            params.put("inputDate", inputDate);
        }
        //业务员
        if (saleContractDO.getSales() != null) {
            String salesName = saleContractDO.getSales().getNickname();
            params.put("salesName", salesName);
        }

        //客户账号信息
        CollectionAccountDTO collectionAccountRespDTO = custApi.getCollectionAccountByCust(saleContractDO.getCustCode(), saleContractDO.getCompanyId());
        if (Objects.nonNull(collectionAccountRespDTO)) {
            params.put("bank", collectionAccountRespDTO.getBankNameEng());
            params.put("bankAdd", collectionAccountRespDTO.getBankAddressEng());
            params.put("swith", collectionAccountRespDTO.getSwiftCode());
            params.put("account", collectionAccountRespDTO.getBankCode());
        }

        params.put("departurePortName", saleContractDO.getDeparturePortName());
        params.put("destinationPortName", saleContractDO.getDestinationPortName());
        params.put("custName", saleContractDO.getCustName());
        params.put("totalGoodsValueAmount", saleContractDO.getTotalGoodsValue().getAmount());
        
        // 处理价格条款
        String settlementTermType = saleContractDO.getSettlementTermType();
        if (StrUtil.isNotEmpty(settlementTermType)) {
            // 如果是FOB价格条款，拼接出运口岸
            if ("FOB".equalsIgnoreCase(settlementTermType)) {
                String departurePortName = saleContractDO.getDeparturePortName();
                if (StrUtil.isNotEmpty(departurePortName)) {
                    params.put("settlementTermType", "FOB " + departurePortName);
                } else {
                    params.put("settlementTermType", "FOB");
                }
            } else {
                // 其他价格条款直接使用
                params.put("settlementTermType", settlementTermType);
            }
        }
        
        // 根据SettlementId查询结汇方式英文名称
        if (Objects.nonNull(saleContractDO.getSettlementId())) {
            SettlementDTO settlementDTO = settlementApi.getSettlementDTOById(saleContractDO.getSettlementId());
            if (Objects.nonNull(settlementDTO)) {
                params.put("paymentNameEng", settlementDTO.getNameEng());
            }
        }
        params.put("custDeliveryDate", saleContractDO.getCustDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        //合计
        List<JsonAmount> totalPurchaseList = saleContractDO.getTotalPurchase();
        if (CollUtil.isNotEmpty(totalPurchaseList)) {
            BigDecimal totalPurchaseAmount = totalPurchaseList.stream()
                    .map(JsonAmount::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            params.put("totalPurchaseAmount", totalPurchaseAmount);
        }
        params.put("sinosureFeeAmount", saleContractDO.getSinosureFee().getAmount());
        params.put("departurePortName", saleContractDO.getDeparturePortName());
        params.put("companyNameEng", companyDTO.getCompanyNameEng());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("companyAddress", companyDTO.getCompanyAddress());
        params.put("companyAddressEng", companyDTO.getCompanyAddressEng());
        params.put("companyTel", companyDTO.getCompanyTel());
        params.put("companyFax", companyDTO.getCompanyFax());
        params.put("total", saleContractDO.getTotalQuantity());
        params.put("totalCurrency", saleContractDO.getTotalGoodsValue().getCurrency());
        params.put("totalAmount", saleContractDO.getTotalGoodsValue().getAmount());
        params.put("currency", saleContractDO.getCurrency());
        return params;
    }

    @Override
    public void updateThumbnail() {
        List<SaleContractItem> saleItemList = saleContractItemMapper.selectList(new LambdaQueryWrapperX<SaleContractItem>().select(SaleContractItem::getThumbnail, SaleContractItem::getId, SaleContractItem::getMainPicture));
        if (CollUtil.isEmpty(saleItemList)){
            return;
        }
        saleItemList.forEach(s->{
            if (Objects.isNull(s.getMainPicture())){
                return;
            }
            SimpleFile picture = s.getMainPicture();
            String thumbnail = skuApi.processImageCompression(List.of(picture));
            s.setThumbnail(thumbnail);
        });
        saleContractItemMapper.updateBatch(saleItemList);
    }

    @Override
    public void updateShipmentTotalQuantity(Map<Long, Integer> shipmentTotalQuantityMap,boolean isDeletedFlag) {
        if (CollUtil.isEmpty(shipmentTotalQuantityMap)){
            return;
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectList(SaleContractItem::getId, shipmentTotalQuantityMap.keySet());
        if (CollUtil.isEmpty(saleContractItems)){
            return;
        }
        saleContractItems.forEach(s->{
            Integer shipmentQuantity = shipmentTotalQuantityMap.get(s.getId());
            if (Objects.isNull(shipmentQuantity)){
                return;
            }
            if (isDeletedFlag){
                int shipmentTotalQuantity = Math.max(s.getShipmentTotalQuantity() - shipmentQuantity, 0);
                int transferShippedQuantity = Math.max(s.getTransferShippedQuantity() - shipmentQuantity, 0);
                s.setShipmentTotalQuantity(shipmentTotalQuantity);
                s.setTransferShippedQuantity(transferShippedQuantity);
                s.setConvertShipmentFlag(BooleanEnum.NO.getValue());
            }else {
                s.setShipmentTotalQuantity(s.getShipmentTotalQuantity()+shipmentQuantity);
                s.setConvertShipmentFlag(BooleanEnum.NO.getValue());
            }
        });
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public Map<Long, JsonAmount> getSaleContractItemAmountMap(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        return saleContractItems.stream()
                .collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getUnitPrice));
    }

    @Override
    public void exportSaleContractExcel(SaleContractPageReqVO pageReqVO, String fileName, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<SaleContractRespVO> saleContractPage = getSaleContractPage(pageReqVO, true);

        if (!pageReqVO.getIsTree()) {
            // 导出单据维度 Excel
            ExcelUtils.write(response, fileName + "-单据.xls", "数据", SaleContractRespVO.class, saleContractPage.getList());
        } else {
            // 导出产品维度 Excel
            List<SaleContractProductExportVO> saleContractProductList = new ArrayList<>();
            // 处理产品维度数据
            saleContractPage.getList().stream().filter(it->Objects.nonNull(it.getChildren())).forEach(item -> {
                    item.getChildren().forEach(cl -> {
                    SaleContractProductExportVO saleContractProductExportVO = new SaleContractProductExportVO();
                    // 设置合同相关信息
                    saleContractProductExportVO.setSaleContractCode(item.getCode());
                    saleContractProductExportVO.setCustName(item.getCustName());
                    saleContractProductExportVO.setCustPO(item.getCustPo());
                    saleContractProductExportVO.setCustCountryName(item.getCustCountryName());
                    saleContractProductExportVO.setTradeCountryName(item.getTradeCountryName());
                    saleContractProductExportVO.setSettlementName(item.getSettlementName());
                    saleContractProductExportVO.setExchangeRate(item.getExchangeRate());
                    saleContractProductExportVO.setInputDate(item.getInputDate());
                    saleContractProductExportVO.setCreatorName(item.getCreatorName());
                    saleContractProductExportVO.setCreateTime(item.getCreateTime());
                    saleContractProductExportVO.setSignBackDate(item.getSignBackDate());

                    // 设置业务员信息
                    if (item.getSales() != null) {
                        saleContractProductExportVO.setSalesNickname(item.getSales().getNickname());
                        saleContractProductExportVO.setSalesDeptName(item.getSales().getDeptName());
                    }

                    // 设置产品信息
                    saleContractProductExportVO.setCskuCode(cl.getCskuCode());
                    saleContractProductExportVO.setSkuCode(cl.getSkuCode());
                    saleContractProductExportVO.setName(cl.getName());
                    saleContractProductExportVO.setNameEng(cl.getNameEng());
                    saleContractProductExportVO.setQuantity(cl.getQuantity());

                    // 设置采购员信息
                    if (cl.getPurchaseUser() != null) {
                        saleContractProductExportVO.setPurchaseUserNickname(cl.getPurchaseUser().getNickname());
                    }

                    // 设置供应商信息
                    saleContractProductExportVO.setVenderName(cl.getVenderName());

                    // 设置价格信息
                    if (cl.getUnitPrice() != null) {
                        saleContractProductExportVO.setUnitPriceAmount(cl.getUnitPrice().getCheckAmount());
                    }
                    if (cl.getTotalSaleAmount() != null) {
                        saleContractProductExportVO.setTotalAmount(cl.getTotalSaleAmount().getCheckAmount());
                    }

                    // 设置状态信息
                    if (item.getStatus() != null) {
                        SaleContractStatusEnum statusEnum = SaleContractStatusEnum.getByValue(item.getStatus());
                        if(statusEnum!=null){
                            saleContractProductExportVO.setStatusName(statusEnum.getName());
                        }  
                    }

                    // 设置毛利信息
                    if (item.getOrderGrossProfit() != null) {
                        saleContractProductExportVO.setEstimatedGrossProfit(item.getOrderGrossProfit().getCheckAmount());
                    }
                    if (item.getGrossProfitMargin() != null) {
                        saleContractProductExportVO.setGrossProfitMargin(item.getGrossProfitMargin());
                    }

                    // 处理产品图片
                    if (cl.getMainPicture() != null) {
                        String inputPath = cl.getMainPicture().getFileUrl();
                        try {
                            byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                            File inputFile = FileUtils.createTempFile(content);
                            BufferedImage image = ImageIO.read(inputFile);
                            Double width = Double.valueOf(image.getWidth());
                            Double height = Double.valueOf(image.getHeight());
                            WriteCellData<Void> voidWriteCellData = ExcelUtils.imageCells(content, width, height, 2.0, 2.0, 0, 0);
                            saleContractProductExportVO.setProductImage(voidWriteCellData);
                            inputFile.delete();
                        } catch (Exception e) {
                            logger.info("产品维度导出图片获取失败: " + e.getMessage());
                        }
                    }

                    saleContractProductList.add(saleContractProductExportVO);
                });
            });

            // 导出产品维度 Excel
            ExcelUtils.write(response, fileName + "-产品.xls", "数据", SaleContractProductExportVO.class, saleContractProductList);
        }
    }

    @Override
    public void updateCollectionPlan(UpdateCollectionPlanReq req) {
        if (CollUtil.isEmpty(req.getCollectionPlanList())||CollUtil.isEmpty(req.getCollectionPlanList())){
            return;
        }
        collectionPlanMapper.updateBatch(req.getCollectionPlanList());
    }

    @Override
    public Map<Long, Map<String, Integer>> getSalePurchaseAndStockQuantityMap(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, item -> {
            Integer quantity = item.getQuantity();
            Integer realLockQuantity = Objects.isNull(item.getRealLockQuantity())?0:item.getRealLockQuantity();
            return Map.of(CommonDict.PURCHASE_QUANTITY, quantity, CommonDict.STOCK_QUANTITY, realLockQuantity);
        }));
    }

    @Override
    public boolean validateCustExists(String custCode) {
        Long count = saleContractMapper.selectCount(new LambdaQueryWrapperX<SaleContractDO>().eq(SaleContractDO::getCustCode, custCode).ne(SaleContractDO::getStatus, SaleContractStatusEnum.CASE_CLOSED.getValue()));
        return count > 0;
    }

    @Override
    public Map<Long, JsonAmount> getRealPurchasePriceMapByItemIds(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, item ->item.getRealPurchaseWithTaxPrice()));
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, Boolean> getSaleItemSaleType(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        Set<Long> contractIdSet = saleContractItems.stream().map(SaleContractItem::getContractId).distinct().collect(Collectors.toSet());
        List<SaleContractDO> saleContractDOS = saleContractMapper.selectList(new LambdaQueryWrapperX<SaleContractDO>().select(SaleContractDO::getId, SaleContractDO::getSaleType).in(SaleContractDO::getId, contractIdSet));
        if (CollUtil.isEmpty(saleContractDOS)){
            return Map.of();
        }
        Map<Long, Integer> saleTypeMap = saleContractDOS.stream().collect(Collectors.toMap(SaleContractDO::getId, SaleContractDO::getSaleType));
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, item -> SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue().equals(saleTypeMap.get(item.getContractId()))));
    }

    /**
     * 比较两个 JsonCompanyPath 树是否相同（基于 id 是否相等）
     *
     * @param node1 第一个树的根节点
     * @param node2 第二个树的根节点
     * @return 如果两个树结构中所有节点的 id 都相同，则返回 true，否则返回 false
     */
    public static boolean isSameTree(JsonCompanyPath node1, JsonCompanyPath node2) {
        // 两者都为空，视为相同
        if (node1 == null && node2 == null) {
            return true;
        }
        // 一方为空，另一方不为空，视为不同
        if (node1 == null || node2 == null) {
            return false;
        }
        // 比较当前节点的 id 是否相同
        if (!Objects.equals(node1.getId(), node2.getId())) {
            return false;
        }
        // 递归比较 next 节点（链式结构）
        return isSameTree(node1.getNext(), node2.getNext());
    }

    /**
     * 插入公章图片到Excel中
     * @param workbook Excel工作簿
     * @param sheet Excel工作表
     * @param imageBytes 图片字节数组
     * @param image 图片对象
     * @param productCount 产品数量
     */
    private void insertCompanySealImage(Workbook workbook, Sheet sheet, byte[] imageBytes, BufferedImage image, int productCount) {
        try {
            // 计算公章图片的动态位置
            // 基础位置：28行5列，每个产品占用2行
            int baseRow = 28;
            int baseCol = 5;
            int rowOffsetPerProduct = 2;
            
            // 计算最终位置
            int finalRow = baseRow + (productCount * rowOffsetPerProduct);
            int finalCol = baseCol;
            
            // 插入公司印章图片到Excel
            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            // 设置图片位置和大小
            anchor.setCol1(finalCol);
            anchor.setCol2(finalCol + 2); // 跨3列
            anchor.setRow1(finalRow);
            anchor.setRow2(finalRow + 4); // 跨5行

            int displayWidth = image.getWidth();
            int displayHeight = image.getHeight();

            // 将像素转换为EMU（Excel度量单位）
            int emuPerPixel = 9525;
            anchor.setDx1(emuPerPixel); // 水平居中偏移
            anchor.setDx2(displayWidth * emuPerPixel); // 设置宽度
            anchor.setDy1(emuPerPixel); // 垂直居中偏移
            anchor.setDy2(displayHeight * emuPerPixel); // 设置高度
            drawing.createPicture(anchor, pictureIdx);
            
        } catch (Exception e) {
            logger.warn("插入公章图片失败: " + e.getMessage());
        }
    }
}
