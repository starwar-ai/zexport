package com.syj.eplus.module.scm.service.purchasecontract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.framework.mybatis.core.mapper.ItemBaseMapperUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dict.DictTypeApi;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.*;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.crm.api.cust.dto.CustDTO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustResp;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.enums.cust.EffectMainInstanceFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.oa.api.PaymentAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.*;
import com.syj.eplus.module.qms.api.QualityInspectionApi;
import com.syj.eplus.module.qms.enums.InspectionItemStatusEnum;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.AuxiliaryPurchaseContractApi;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryPurchaseContractDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.*;
import com.syj.eplus.module.scm.api.quoteitem.QuoteitemApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationAllocationSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemAndPlanRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderInfoRespVO;
import com.syj.eplus.module.scm.convert.PurchaseContractConvert;
import com.syj.eplus.module.scm.convert.PurchaseContractItemConvert;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentItem;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractProductModeSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseItemAmountSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchaseItemSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.dal.dataobject.venderpayment.VenderPayment;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.dal.mysql.addsubterm.PurchaseAddSubTermMapper;
import com.syj.eplus.module.scm.dal.mysql.change.PurchasecontractchangeMapper;
import com.syj.eplus.module.scm.dal.mysql.paymentplan.PurchasePaymentPlanMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontract.PurchaseContractMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontractitem.PurchaseContractItemMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseplan.PurchasePlanMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseplanitem.PurchasePlanItemMapper;
import com.syj.eplus.module.scm.dal.mysql.venderpayment.VenderPaymentMapper;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.scm.service.invoicingnotices.InvoicingNoticesService;
import com.syj.eplus.module.scm.service.paymentapply.PaymentApplyService;
import com.syj.eplus.module.scm.service.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationService;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
import com.syj.eplus.module.scm.service.purchaseplan.PurchasePlanService;
import com.syj.eplus.module.scm.service.purchaseplanitem.PurchasePlanItemService;
import com.syj.eplus.module.scm.service.quoteitem.QuoteItemService;
import com.syj.eplus.module.scm.service.vender.VenderService;
import com.syj.eplus.module.scm.service.venderpoc.VenderPocService;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SaleContractDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.api.stockNotice.IStockNoticeApi;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeItemReqVO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeReqVO;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.enums.StockBillStatusEnum;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_CURRENCY_EXISTS;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_CONTRACT_NOT_EXISTS;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PURCHASE_PLAN_NOT_EXISTS;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SKU_AUXILIARY_NOT_EXISTS;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SKU_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.FIELD_CHANGE_CONFIG_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.NOT_UPDATE_PROCESS;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.COMPANY_NOT_EXITS;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.PURCHASE_ITEM_NOT_EXISTS;

/**
 * 采购合同 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class PurchaseContractServiceImpl extends ServiceImpl<PurchaseContractMapper, PurchaseContractDO> implements PurchaseContractService {
    @Resource
    private SkuApi skuApi;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private InvoicingNoticesService invoicingNoticesService;
    @Resource
    private DictDataApi dictDataApi;
    @Resource
    private DictTypeApi dictTypeApi;
    @Resource
    private PurchaseAuxiliaryAllocationService purchaseAuxiliaryAllocationService;
    @Resource
    private PurchaseContractMapper purchaseContractMapper;
    @Resource
    private PurchaseContractItemMapper purchaseContractItemMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private ConfigApi configApi;
    @Resource
    private PurchaseContractItemService purchaseContractItemService;
    @Resource
    private IStockNoticeApi iStockNoticeApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private CustApi custApi;
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    private VenderService venderService;
    // TODO: Replace with PortApi when getPort method is available
    // @Resource
    // private PortService portService;

    @Resource
    private VenderPocService venderPocService;
    @Resource
    private PurchasePlanService purchasePlanService;
    @Resource
    private PurchasePlanItemService purchasePlanItemService;
    @Resource
    private PurchaseAuxiliaryAllocationService allocationService;

    private static final String PROCESS_DEFINITION_CHANGE_KEY = "scm_purchase_contract_change";
    private static final String PROCESS_DEFINITION_AUXILIARY_CHANGE_KEY = "scm_purchase_auxiliary_contract_change";
    @Resource
    private CompanyApi companyApi;
    @Resource
    private AuxiliaryPurchaseContractApi auxiliaryPurchaseContractApi;

    @Resource
    private ReportApi reportApi;
    @Resource
    private FileApi fileApi;

    @Resource
    private PackageTypeApi packageTypeApi;

    @Resource
    private FeeShareApi feeShareApi;

    @Resource
    QuoteItemService quoteItemService;
    @Resource
    private IStockApi stockApi;
    @Resource
    private IWarehouseApi warehouseApi;
    @Resource
    private VenderApi venderApi;

    @Resource
    private PaymentApplyService paymentApplyService;
    @Resource
    private FormChangeApi formChangeApi;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private RateApi rateApi;

    @Resource
    private QuoteitemApi quoteitemApi;
    @Resource
    private PurchasecontractchangeMapper purchaseContractChangeMapper;
    @Resource
    private PaymentAppApi paymentAppApi;

    // 普通产品审批流程表示
    private static final String PROCESS_DEFINITION_KEY = "scm_purchase_contract";
    // 自营产品审批流程 ownBrand
    private static final String OWN_BRAND_PROCESS_DEFINITION_KEY = "scm_purchase_contract_own_brand";
    private static final String AUXILIARY_PROCESS_DEFINITION_KEY = "scm_purchase_contract_auxiliary";


    @Resource
    private PurchasecontractchangeMapper purchasecontractchangeMapper;
    private static final String CODE_PREFIX = "PC";
    public static final String CHANGE_SN_TYPE = "SN_PURCHASECONTRACT_CHANGE";
    public static final String SN_TYPE = "SN_PURCHASECONTRACT";
    private static final String OPERATOR_EXTS_KEY = "purchaseContractCode";
    public static final String CHANGE_OPERATOR_EXTS_KEY = "changePurchaseContractCode";
    private static final String CHANGE_CODE_PREFIX = "PCC";
    // TODO: Replace with proper MQ implementation
    // @Resource
    // private RedisMQTemplate redisMQTemplate;
    @Resource
    private IStockNoticeApi stockNoticeApi;

    @Resource
    private PurchaseAddSubTermMapper purchaseAddSubTermMapper;

    @Resource
    private PurchasePaymentPlanMapper purchasePaymentPlanMapper;

    @Resource
    private VenderPaymentMapper venderPaymentMapper;
    @Resource
    private PaymentItemApi paymentItemApi;

    @Resource
    private QualityInspectionApi qualityInspectionApi;
    @Resource
    private PaymentApi paymentApi;
    @Resource
    private PurchasePlanItemMapper purchasePlanItemMapper;
    @Resource
    private PurchasePlanMapper purchasePlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createPurchaseContract(PurchaseContractSaveInfoReqVO createReqVO) {
        List<CreatedResponse> response = new ArrayList<>();
        if (CollUtil.isEmpty(createReqVO.getChildren())) {
            return response;
        }

        createReqVO.setRepeatFlag(BooleanEnum.NO.getValue());
        //判断是否翻单
        LambdaQueryWrapperX<PurchaseContractDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(PurchaseContractDO::getDeleted, 0)
                .ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode())
                .eq(PurchaseContractDO::getVenderCode, createReqVO.getVenderCode());
        boolean exists = purchaseContractMapper.selectCount(queryWrapper) > 0;
        if (exists) {
            createReqVO.setRepeatFlag(BooleanEnum.YES.getValue());
        }
        //所有产品采购数量都是0  不生成采购合同
        int sum = createReqVO.getChildren().stream().mapToInt(PurchaseContractItemSaveReqVO::getQuantity).sum();
        if (sum == 0) {
            return response;
        }
        String currency = CurrencyCheck(createReqVO);
        if (currency == null) {
            throw exception(PURCHASE_CURRENCY_WRONG);
        }
        String categoryCode = genPurchaseContractCode(createReqVO.getAutoFlag(), createReqVO.getSaleContractCode(), createReqVO.getPurchasePlanCode());
        while (purchaseContractMapper.exists(new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getCode, categoryCode))) {
            categoryCode = genPurchaseContractCode(createReqVO.getAutoFlag(), createReqVO.getSaleContractCode(), createReqVO.getPurchasePlanCode());
        }
        createReqVO.setCode(categoryCode);
        createReqVO.setVer(1);
        createReqVO.setPrintFlag(BooleanEnum.NO.getValue());
        createReqVO.setPrepayStatus(BooleanEnum.NO.getValue());
        createReqVO.setPayStatus(BooleanEnum.NO.getValue());
        createReqVO.setInvoiceStatus(BooleanEnum.NO.getValue());
        createReqVO.setSignBackFlag(BooleanEnum.NO.getValue());
        if (CollUtil.isEmpty(createReqVO.getPurchasePaymentPlanList())){
            Map<Long,PaymentItemDTO> paymentItemDTOCache = venderApi.getPaymentItemDTOListByVenderCode(createReqVO.getVenderCode());
            if (CollUtil.isNotEmpty(paymentItemDTOCache)){
                PaymentItemDTO paymentItemDTO = paymentItemDTOCache.values().stream().findFirst().get();
                List<SystemPaymentPlanDTO> systemPaymentPlanDTOList = paymentItemDTO.getSystemPaymentPlanDTOList();
                createReqVO.setPurchasePaymentPlanList(BeanUtils.toBean(systemPaymentPlanDTOList,PurchasePaymentPlan.class));
                createReqVO.setPaymentId(paymentItemDTOCache.keySet().stream().findFirst().get());
            }
        }
        if (CollUtil.isEmpty(createReqVO.getLinkCodeList())) {
            createReqVO.setLinkCodeList(List.of(IdUtil.fastSimpleUUID()));
        }
        if (ObjectUtil.isNull(createReqVO.getContractStatus())) {
            createReqVO.setContractStatus(PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode());
        }
        if (ObjectUtil.isNull(createReqVO.getAuditStatus())) {
            createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        }
        createReqVO.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode());
        createReqVO.setPurchaseTime(LocalDateTime.now());
        //根据付款方式id设置名称
        if (Objects.nonNull(createReqVO.getPaymentId())) {
            PaymentItemDTO paymentDTO = paymentItemApi.getPaymentDTO(createReqVO.getPaymentId());
            if (Objects.nonNull(paymentDTO)) {
                createReqVO.setPaymentName(paymentDTO.getName());
            }
        }
        PurchaseContractDO contractDO = BeanUtils.toBean(createReqVO, PurchaseContractDO.class);
        // 计算采购总金额
        calcTotalAmount(createReqVO.getChildren(),contractDO);
        //设置供应商信息
        Long venderId = contractDO.getVenderId();
        SimpleVenderRespDTO simpleVender = venderApi.getSimpleVenderRespDTOById(venderId);
        if (Objects.isNull(simpleVender)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        if (Objects.isNull(contractDO.getVenderName())) {
            contractDO.setVenderName(simpleVender.getName());
        }
        if (Objects.isNull(contractDO.getCurrency())) {
            List<JsonVenderTax> taxMsg = simpleVender.getTaxMsg();
            if (CollUtil.isNotEmpty(taxMsg)){
                taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                    contractDO.setCurrency(s.getCurrency());
                });
            }
        }

        //供应商联系人
        List<VenderPocDO> venderPocDOS = venderPocService.getPocListByVenderId(venderId);
        if (CollUtil.isNotEmpty(venderPocDOS)) {
            Optional<VenderPocDO> first1 = venderPocDOS.stream().filter(s -> s.getDefaultFlag() == 1).findFirst();
            if (first1.isPresent()) {
                contractDO.setVenderPoc(first1.get());
            } else {
                contractDO.setVenderPoc(venderPocDOS.get(0));
            }
        }
        List<PurchaseContractItemSaveReqVO> itemList = createReqVO.getChildren().stream().filter(s -> s.getQuantity() != 0).toList();
        Set<String> auxiliaryPurchaseContractCodeSet = itemList.stream().map(PurchaseContractItemSaveReqVO::getAuxiliaryPurchaseContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Set<String> auxiliarySaleContractCodeSet = itemList.stream().map(PurchaseContractItemSaveReqVO::getAuxiliarySaleContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Map<String, List<UserDept>> purchaseUserAndSalesMap = purchasePlanService.getPurchaseUserAndSales(auxiliaryPurchaseContractCodeSet, auxiliarySaleContractCodeSet);
        if (CollUtil.isNotEmpty(purchaseUserAndSalesMap)){
            contractDO.setAuxiliaryPurchaseUser(purchaseUserAndSalesMap.get(CommonDict.PURCHASE_USER_FIELD_NAME));
            contractDO.setAuxiliarySales(purchaseUserAndSalesMap.get(CommonDict.SALES_FIELD_NAME));
            contractDO.setAuxiliaryManager(purchaseUserAndSalesMap.get(CommonDict.MANAGER_FIELD_NAME));
        }

        //初始化预计交期
        List<LocalDateTime> dates = itemList.stream().map(PurchaseContractItemSaveReqVO::getVenderDeliveryDate).filter(Objects::nonNull).toList();
        if(CollUtil.isNotEmpty(dates)){
            contractDO.setDeliveryDate( dates.stream()
                    .min(Comparator.naturalOrder())
                    .orElse(null));
        }
        //初始化字段取销售合同的预计交期
        SaleContractDTO saleContractDTO = saleContractApi.getSaleContractById(contractDO.getSaleContractId());
        if(Objects.nonNull(saleContractDTO)){
            contractDO.setInitDeliveryDate(saleContractDTO.getCustDeliveryDate());
        }

        purchaseContractMapper.insert(contractDO);
        Long contractId = contractDO.getId();
        // 回写出运明细采购合同编号
        if (BooleanEnum.YES.getValue().equals(createReqVO.getRollbackCodeToShipmentFlag())) {
            rollbackPurchaseCodeToShipment(categoryCode, createReqVO.getVenderId(), itemList);
        }
        // 判断客户编号存在且有效
        if (StringUtils.isNotBlank(createReqVO.getCustCode())&&!BooleanEnum.YES.getValue().equals(createReqVO.getAutoFlag())) {
            String custCode = createReqVO.getCustCode();
            Map<String, String> custNameCache = custApi.getCustNameCache(custCode);
            if (CollUtil.isNotEmpty(custNameCache)) {
                String s = custNameCache.get(custCode);
                if (Objects.nonNull(s)) {
                    preprocessingOwnbrandSku(itemList, createReqVO.getCustCode(), contractId);
                }
            }
        }
        List<Long> skuIdList = itemList.stream().map(PurchaseContractItemSaveReqVO::getSkuId).toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);

        List<String> custCodes = itemList.stream().map(PurchaseContractItemSaveReqVO::getCustCode).distinct().toList();
        Map<String, CustAllDTO> custAllDTOMap = custApi.getCustByCodeList(custCodes);

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();

        if (CollUtil.isNotEmpty(itemList)) {
            String finalCategoryCode = categoryCode;
            List<String> skuCodeList = itemList.stream().map(PurchaseContractItemSaveReqVO::getSkuCode).distinct().toList();
            Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
            itemList.forEach(s -> {
                if (Objects.isNull(s.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                    HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                    if (Objects.nonNull(hsDataDTO)){
                        s.setSkuUnit(hsDataDTO.getUnit());
                    }
                }
                if (contractDO.getSaleContractCode() == null) {
                    s.setSaleContractItemId(null);
                }
                s.setPurchaseContractId(contractId);
                s.setPurchaseContractCode(finalCategoryCode);
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(s.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        s.setMainPicture(simpleSkuDTO.getMainPicture());
                        s.setThumbnail(simpleSkuDTO.getThumbnail());
                        s.setSkuName(simpleSkuDTO.getSkuName());
                    }
                }
                s.setUniqueCode(IdUtil.fastSimpleUUID());
                s.setCurrency(s.getWithTaxPrice().getCurrency());
                if (CollUtil.isNotEmpty(custAllDTOMap)) {
                    CustAllDTO custAllDTO = custAllDTOMap.get(s.getCustCode());
                    if (Objects.nonNull(custAllDTO)) {
                        s.setCustName(custAllDTO.getName());
                    }
                }
                if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())) {
                    List<Long> distinctPackageType = s.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackageType.contains(pt.getId())).toList();
                    if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        s.setPackageTypeName(String.join(",", packageTypeNameList));
                        List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                        s.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                    }
                }

            });
            purchaseContractItemService.batchSavePlanItem(itemList);
        }
        //处理费用分摊
        dealAuxiliaryPurchaseAllocations(contractDO.getId());
//        Map<String, String> venderNameCache = venderApi.getVenderNameCache(contractDO.getVenderCode());
        List<String> linkCodeList = contractDO.getLinkCodeList();
        if (CollUtil.isNotEmpty(linkCodeList)) {
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> {
                // 插入订单流
                OrderLinkDTO orderLinkDTO = new OrderLinkDTO()
                        .setBusinessId(contractDO.getId())
                        .setCode(contractDO.getCode())
                        .setName(BusinessNameDict.PURCHASE_CONTRACT_NAME)
                        .setType(OrderLinkTypeEnum.PURCHASE_CONTRACT.getValue())
                        .setLinkCode(s)
                        .setItem(itemList)
                        .setParentCode(contractDO.getPurchasePlanCode())
                        .setStatus(contractDO.getContractStatus())
                        .setCompanyId(contractDO.getCompanyId())
                        .setOrderMsg(contractDO);
//                if (CollUtil.isNotEmpty(venderNameCache)) {
//                    orderLinkDTO.setBusinessSubjectName(venderNameCache.get(contractDO.getVenderCode()));
//                }
                orderLinkDTO.setBusinessSubjectName(simpleVender.getName());
                return orderLinkDTO;
            }).toList();
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        //更新总计数量和金额
        updateSum(contractDO, currency);
        // 处理付款计划应付金额
        dealPaymentPlan(createReqVO, contractDO, currency, categoryCode, false);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            List<PurchaseContractItemSaveReqVO> children = createReqVO.getChildren();
            boolean stockPurchaseFlag = children.stream().anyMatch(s -> StrUtil.isNotEmpty(s.getSaleContractCode())||Objects.nonNull(s.getSaleContractItemId())||StrUtil.isNotEmpty(s.getSaleItemUniqueCode()));
            Map<String, Object> variable = new HashMap<>();
            if (BooleanEnum.YES.getValue().equals(createReqVO.getAuxiliaryFlag())){
                submitAuxiliaryTask(contractId,WebFrameworkUtils.getLoginUserId(), AUXILIARY_PROCESS_DEFINITION_KEY);
            }else {
                if (stockPurchaseFlag) {
                    variable.put("stockPurchaseFlag", 0);
                    submitTask(contractId, WebFrameworkUtils.getLoginUserId(), PROCESS_DEFINITION_KEY, variable);
                } else {
                    variable.put("stockPurchaseFlag", 1);
                    submitTask(contractDO.getId(), WebFrameworkUtils.getLoginUserId(), PROCESS_DEFINITION_KEY, variable);
                }
            }
        }
        // 补充操作日志明细
        dealOperateLog(String.format("【新增采购合同】%s", categoryCode), categoryCode);

        response.add(new CreatedResponse(contractId, contractDO.getCode()));
        return response;
    }

    private void calcTotalAmount(List<PurchaseContractItemSaveReqVO> children,PurchaseContractDO contractDO){
        if (CollUtil.isEmpty(children)||Objects.isNull(contractDO)){
            return;
        }
        Optional<JsonAmount> totalAmountOpt = children.stream().map(s -> {
            BigDecimal withTaxPrice = Objects.isNull(s.getWithTaxPrice()) ? BigDecimal.ZERO : Objects.isNull(s.getWithTaxPrice().getAmount()) ? BigDecimal.ZERO : s.getWithTaxPrice().getAmount();
            int quantity = Objects.isNull(s.getQuantity()) ? 0 : s.getQuantity();
            return NumUtil.mul(withTaxPrice, quantity);
        }).reduce(BigDecimal::add).map(s -> new JsonAmount().setAmount(s).setCurrency(contractDO.getCurrency()));
        if (totalAmountOpt.isPresent()){
            contractDO.setTotalAmount(totalAmountOpt.get());
        }else {
            contractDO.setTotalAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(contractDO.getCurrency()));
        }
        // 设置税率
        Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
        BigDecimal taxRate = dailyRateMap.get(contractDO.getCurrency());
        contractDO.setTaxRate(taxRate);
        contractDO.setTotalAmountRmb(new JsonAmount().setAmount(NumUtil.mul(contractDO.getTotalAmount().getAmount(),taxRate)).setCurrency(CalculationDict.CURRENCY_RMB));
    }
    @Override
    public void submitAuxiliaryTask(Long purchaseContractId, Long userId, String processDefinitionKey) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(purchaseContractId);
        Map<Long,UserDept> purchaseUsersAndSales = new HashMap<>();
        List<UserDept> auxiliaryManager = purchaseContractDO.getAuxiliaryManager();
        if (CollUtil.isNotEmpty(auxiliaryManager)){
            auxiliaryManager.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        List<UserDept> auxiliaryPurchaseUser = purchaseContractDO.getAuxiliaryPurchaseUser();
        if (CollUtil.isNotEmpty(auxiliaryPurchaseUser)){
            auxiliaryPurchaseUser.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        List<UserDept> auxiliarySales = purchaseContractDO.getAuxiliarySales();
        if (CollUtil.isNotEmpty(auxiliarySales)){
            auxiliarySales.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        String processInstanceId;
        if (CollUtil.isNotEmpty(purchaseUsersAndSales)) {
            List<Long> approveUserIdList = purchaseUsersAndSales.values().stream().filter(Objects::nonNull).map(UserDept::getUserId).distinct().toList();
            Map<String, Object> variables = new HashMap<>();
            variables.put("loopCardinality", approveUserIdList.size());
            variables.put("approverIdList", approveUserIdList);
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, purchaseContractId, variables, Map.of());
        } else {
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, purchaseContractId);
        }

        updateAuditStatus(purchaseContractId, BpmProcessInstanceResultEnum.PROCESS.getResult(),processInstanceId);
    }

    @Override
    public void updateLinkCodeList(Map<String, String> linkCodeMap) {
        if (CollUtil.isEmpty(linkCodeMap)){
            return;
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapper<PurchaseContractDO>().select(PurchaseContractDO::getId, PurchaseContractDO::getCode,PurchaseContractDO::getLinkCodeList).in(PurchaseContractDO::getCode, linkCodeMap.keySet()));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return;
        }
        purchaseContractDOS.forEach(s->{
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
        purchaseContractMapper.updateBatch(purchaseContractDOS);
    }

    @Override
    public void updateThumbnail() {
        List<PurchaseContractItemDO> purchaseItemList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getThumbnail, PurchaseContractItemDO::getId, PurchaseContractItemDO::getMainPicture));
        if (CollUtil.isEmpty(purchaseItemList)){
            return;
        }
        purchaseItemList.forEach(s->{
            if (Objects.isNull(s.getMainPicture())){
                return;
            }
            SimpleFile picture = s.getMainPicture();
            String thumbnail = skuApi.processImageCompression(List.of(picture));
            s.setThumbnail(thumbnail);
        });
        purchaseContractItemMapper.updateBatch(purchaseItemList);
    }

    @Override
    public void updateCode(Long id, String code) {
        if (Objects.isNull(id)||StrUtil.isEmpty(code)){
            return;
        }
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        if (!Arrays.asList(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult(),BpmProcessInstanceResultEnum.REJECT.getResult()
                ,BpmProcessInstanceResultEnum.CANCEL.getResult(),BpmProcessInstanceResultEnum.CLOSE.getResult(),BpmProcessInstanceResultEnum.BACK.getResult()).contains(purchaseContractDO.getAuditStatus())){
            throw exception(PURCHASE_CONTRACT_AUDIT_STATUS_ERROR);
        }
        boolean exists = purchaseContractMapper.exists(new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getCode, code));
        if (exists){
            throw exception(PURCHASE_CONTRACT_CODE_EXISTS);
        }
        purchaseContractMapper.updateById(new PurchaseContractDO().setCode(code).setId(id));
        PurchaseContractItemDO purchaseContractItemDO = new PurchaseContractItemDO();
        purchaseContractItemDO.setPurchaseContractCode(code);
        purchaseContractItemMapper.update(purchaseContractItemDO,new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractDO.getCode()));
        PurchasePaymentPlan purchasePaymentPlan = new PurchasePaymentPlan();
        purchasePaymentPlan.setContractCode(code);
        purchasePaymentPlanMapper.update(purchasePaymentPlan,new LambdaQueryWrapperX<PurchasePaymentPlan>().eq(PurchasePaymentPlan::getContractCode, purchaseContractDO.getCode()));
        orderLinkApi.updateOrderLink(new OrderLinkDTO().setOrderMsg(purchaseContractDO).setNewCode(code).setCode(purchaseContractDO.getCode()).setStatus(purchaseContractDO.getContractStatus()).setName(BusinessNameDict.PURCHASE_CONTRACT_NAME).setType(OrderLinkTypeEnum.PURCHASE_CONTRACT.getValue()));
    }

    @Override
    public boolean validateVenderExists(String venderCode) {
        Long count = purchaseContractMapper.selectCount(new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getVenderCode, venderCode).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        return count > 0;
    }

    @Override
    public void updatePaymentPlan(UpdatePaymentPlanReq req) {
        List<PurchasePaymentPlan> paymentPlanList = req.getPaymentPlanList();
        if (CollUtil.isEmpty(paymentPlanList)){
            return;
        }
        purchasePaymentPlanMapper.updateBatch(paymentPlanList);
    }

    @Override
    public List<PurchaseContractItemDO> getItemMapByItemIds(List<Long> purchaseItemIds) {
        return purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId,purchaseItemIds);
    }

    @Override
    public Map<Long, JsonAmount> getiPurchasePricCache(Collection<String> purchaseCodeList) {
        if (CollUtil.isEmpty(purchaseCodeList)){
            return Map.of();
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getSaleContractItemId, PurchaseContractItemDO::getWithTaxPrice, PurchaseContractItemDO::getPurchaseContractCode).in(PurchaseContractItemDO::getPurchaseContractCode, purchaseCodeList));
        if (CollUtil.isEmpty(purchaseContractItemDOS)){
            return Map.of();
        }
        return purchaseContractItemDOS.stream().collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId,PurchaseContractItemDO::getWithTaxPrice, (a, b)->a));
    }

    @Override
    public void rollBackPurchaseNoticeQuantity(Map<String, Map<String, Integer>> updateMap) {
        if (CollUtil.isEmpty(updateMap)){
            return;
        }
        Set<String> purchaseCodes = updateMap.keySet();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractCode, PurchaseContractItemDO::getUniqueCode, PurchaseContractItemDO::getQuantity, PurchaseContractItemDO::getNoticedQuantity)
                .in(PurchaseContractItemDO::getPurchaseContractCode, purchaseCodes));
        if (CollUtil.isEmpty(purchaseContractItemDOS)){
            Map<String,Integer> updateStatusMap = new HashMap<>();
            purchaseCodes.forEach(s->{
                updateStatusMap.put(s,ConvertNoticeFlagEnum.NOT_ISSUED.getStatus());
            });
            updatePurchaseContractNoticeStatus(updateStatusMap);
        }else {
            purchaseContractItemDOS.forEach(s->{
                int noticedQuantity = Objects.isNull(s.getNoticedQuantity()) ? 0 : s.getNoticedQuantity();
                Map<String, Integer> stockMap = updateMap.get(s.getPurchaseContractCode());
                if (CollUtil.isEmpty(stockMap)){
                    return;
                }
                Integer stockQuantity = stockMap.get(s.getUniqueCode());
                if (Objects.isNull(stockQuantity)){
                    return;
                }
                int currNoticedQuantity = Math.max(noticedQuantity - stockQuantity, 0);
                s.setNoticedQuantity(currNoticedQuantity);
            });

            Map<String, List<PurchaseContractItemDO>> contractMap = purchaseContractItemDOS.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getPurchaseContractCode));
            Map<String, Integer> purchaseContractCodeMap = new HashMap<>();
            contractMap.forEach((k, v)->{
                boolean noticed = v.stream().allMatch(s -> s.getQuantity().equals(s.getNoticedQuantity()));
                boolean partNoticed = v.stream().allMatch(s -> Objects.isNull(s.getNoticedQuantity())||!Objects.equals(s.getQuantity(), s.getNoticedQuantity()));
                if (noticed){
                    purchaseContractCodeMap.put(k, ConvertNoticeFlagEnum.ISSUED.getStatus());
                }else if (partNoticed){
                    purchaseContractCodeMap.put(k, ConvertNoticeFlagEnum.PART_ISSUED.getStatus());
                }else {
                    purchaseContractCodeMap.put(k, ConvertNoticeFlagEnum.ISSUED.getStatus());
                }
            });
            purchaseContractItemMapper.updateBatch(purchaseContractItemDOS);
            // 更新采购合同转单状态
            updatePurchaseContractNoticeStatus(purchaseContractCodeMap);
        }
    }

    private void rollbackPurchaseCodeToShipment(String purchaseCode, Long venderId, List<PurchaseContractItemSaveReqVO> itemList) {
        if (CollUtil.isEmpty(itemList) || StrUtil.isEmpty(purchaseCode) || Objects.isNull(venderId)) {
            return;
        }
        Map<Long, Tuple> updateMap = new HashMap<>();
        SimpleVenderRespDTO simpleVenderRespDTOById = venderApi.getSimpleVenderRespDTOById(venderId);
        if (Objects.isNull(simpleVenderRespDTOById)) {
            return;
        }
        SimpleData simpleData = BeanUtils.toBean(simpleVenderRespDTOById, SimpleData.class);
        List<Long> saleContractItemIds = itemList.stream().map(PurchaseContractItemSaveReqVO::getSaleContractItemId).filter(Objects::nonNull).distinct().toList();
        shipmentApi.rollbackPurchaseCode(saleContractItemIds, purchaseCode, simpleData);
    }

    private String genPurchaseContractCode(Integer autoFlag, String saleContractCode, String purchasePlanCode) {
        String prefix;
        if (BooleanEnum.YES.getValue().equals(autoFlag)) {
            prefix = saleContractCode + CommonDict.HYPHEN;
        } else {
            prefix = purchasePlanCode + CommonDict.HYPHEN;
        }
        return codeGeneratorApi.getCodeGenerator(SN_TYPE, prefix, false, null);
    }

    // 补充操作日志明细
    private void dealOperateLog(String content, String code) {
        OperateLogUtils.setContent(content);
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
    }

    private void dealAuxiliaryPurchaseAllocations(Long contractId) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(contractId);
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        //只处理辅料合同
        if (Objects.equals(purchaseContractDO.getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            return;
        }
        //只查询产品相关
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(contractId)
                .stream().filter(s -> Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.PRODUCT.getCode())).toList();
        if (CollUtil.isEmpty(itemDOList)) {
            return;
        }
        List<PurchaseAuxiliaryAllocationDO> allocationDoList = new ArrayList<PurchaseAuxiliaryAllocationDO>();
        //获取辅料产品名称map
        List<String> skuCodeList = itemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuNameDTO> skuNameMap = skuApi.getSkuNameCacheByCodeList(skuCodeList);
        //获取关联产品名称，客户货号等信息
        List<String> auxiliarySkuCodeList = itemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(auxiliarySkuCodeList);
        //获取关联采购合同id,数据库只存了关联code
        List<String> purchaseCodeList = itemDOList.stream().map(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode).distinct().toList();
        List<PurchaseContractDO> contractDoList = purchaseContractMapper.selectList(PurchaseContractDO::getCode, purchaseCodeList);
        //辅料合同明细  转换生成分摊数据
        itemDOList.forEach(s -> {
            //如果辅料没有现在产品  不计入分摊数据库
            if (StrUtil.isEmpty(s.getAuxiliarySkuCode())) {
                return;
            }
            PurchaseAuxiliaryAllocationDO allocationDO = new PurchaseAuxiliaryAllocationDO();
            //当前合同为辅料合同  所以do数据为辅料相关字段
            allocationDO.setAuxiliaryPurchaseContractId(purchaseContractDO.getId()).setAuxiliaryPurchaseContractCode(purchaseContractDO.getCode());
            allocationDO.setAuxiliaryPurchaseContractItemId(s.getId());
            allocationDO.setAuxiliarySkuCode(s.getSkuCode());
            if (CollUtil.isNotEmpty(skuNameMap)) {
                SkuNameDTO skuNameDTO = skuNameMap.get(s.getAuxiliarySkuCode());
                if (Objects.nonNull(skuNameDTO)) {
                    allocationDO.setAuxiliarySkuName(skuNameDTO.getName());
                }
            }
            allocationDO.setQuantity(s.getQuantity());
            //采购合同子表字段包含auxiliary 表示辅料相关字段，这些字段相当于分摊表的合同相关字段
            allocationDO.setPurchaseContractCode(s.getAuxiliaryPurchaseContractCode());
            if (CollUtil.isNotEmpty(contractDoList)) {
                Optional<PurchaseContractDO> first = contractDoList.stream().filter(c -> Objects.equals(c.getCode(), s.getAuxiliaryPurchaseContractCode())).findFirst();
                first.ifPresent(contractDO -> s.setPurchaseContractId(contractDO.getId()));
            }
            allocationDO.setPurchaseContractItemId(s.getAuxiliaryPurchaseContractItemId());
            allocationDO.setSkuCode(s.getAuxiliarySkuCode());
            if (CollUtil.isNotEmpty(skuDTOMap)) {
                SkuDTO skuDTO = skuDTOMap.get(s.getAuxiliarySkuCode());
                if (Objects.nonNull(skuDTO)) {
                    allocationDO.setSkuName(skuDTO.getName());
                    allocationDO.setCskuCode(skuDTO.getCskuCode());
                }
            }
            if (Objects.nonNull(s.getTotalPrice()) && Objects.nonNull(s.getTotalPrice().getAmount())) {
                allocationDO.setAllocationAmount(s.getTotalPrice().setAmount(s.getTotalPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity()))));
            } else {
                throw exception(PURCHASE_TOTAL_PRICE_NULL);
            }
            allocationDoList.add(allocationDO);
        });
        allocationService.batchInsert(allocationDoList, contractId);
    }

    private void dealPaymentPlan(PurchaseContractSaveInfoReqVO createReqVO, PurchaseContractDO contractDO, String currency, String categoryCode, boolean isUpdate) {
        // 插入付款计划
        List<PurchasePaymentPlan> purchasePaymentPlanList = createReqVO.getPurchasePaymentPlanList();
        if (CollUtil.isNotEmpty(purchasePaymentPlanList)) {
            JsonAmount totalAmount = Objects.isNull(contractDO.getTotalAmount())?new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB):contractDO.getTotalAmount();
//            if (Objects.isNull(totalAmount)) {
//                throw exception(PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY);
//            }
            BigDecimal amount = totalAmount.getAmount();
            purchasePaymentPlanList.forEach(s -> {
                s.setId(null);
                BigDecimal paymentRatio = s.getPaymentRatio();
                if (Objects.isNull(paymentRatio)) {
                    throw exception(PURCHASE_CONTRACT_PAYMENT_RATIO_EMPTY);
                }
                // 比例取百分比
                BigDecimal ratio = NumUtil.div(paymentRatio, CalculationDict.ONE_HUNDRED);
                s.setContractCode(categoryCode);
                // 应付金额
                s.setReceivableAmount(new JsonAmount().setAmount(NumUtil.mul(amount, ratio)).setCurrency(currency));
            });
            if (isUpdate) {
                purchasePaymentPlanMapper.delete(PurchasePaymentPlan::getContractCode, contractDO.getCode());
            }
            purchasePaymentPlanMapper.insertBatch(purchasePaymentPlanList);
        }
    }

    private List<PurchaseContractItemSaveReqVO> preprocessingOwnbrandSku(List<PurchaseContractItemSaveReqVO> purchaseContractItemList, String custCode, Long contractId) {
        if (CollUtil.isEmpty(purchaseContractItemList)) {
            return purchaseContractItemList;
        }
        // key -> 产品编号 value -> 客户货号
        Map<String, Tuple> skuCodeMap = purchaseContractItemList.stream().filter(s -> {
            // 自营产品且非客户产品
            return BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()) && BooleanEnum.NO.getValue().equals(s.getCustProFlag());
        }).collect(Collectors.toMap(PurchaseContractItemSaveReqVO::getSkuCode, s -> new Tuple(s.getCskuCode(), s.getWithTaxPrice(),s.getBarcode()), (v1, v2) -> v1));
        if (CollUtil.isEmpty(skuCodeMap)) {
            return purchaseContractItemList;
        }
        // 校验自营产品是否存在当前客户对应客户产品
        Map<String, SimpleData> stringSimpleDataMap = skuApi.validateOwnbrandSku(skuCodeMap, custCode);
        List<String> skuCodeList = purchaseContractItemList.stream().map(PurchaseContractItemSaveReqVO::getSkuCode).distinct().toList();
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
        purchaseContractItemList.forEach(s -> {
            if (Objects.isNull(s.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                if (Objects.nonNull(hsDataDTO)){
                    s.setSkuUnit(hsDataDTO.getUnit());
                }
            }
            s.setPurchaseContractId(contractId);
            if (CollUtil.isNotEmpty(stringSimpleDataMap)) {
                SimpleData simpleData = stringSimpleDataMap.get(s.getSkuCode());
                if (Objects.nonNull(simpleData)) {
                    s.setSkuId(simpleData.getId());
                    s.setSkuCode(simpleData.getCode());
                    s.setCustProFlag(BooleanEnum.YES.getValue());
                }
            }
        });
        return purchaseContractItemList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updatePurchaseContract(PurchaseContractSaveInfoReqVO updateInfoReqVO) {
        String currency = CurrencyCheck(updateInfoReqVO);
        if (currency == null)
            throw exception(PURCHASE_CURRENCY_WRONG);

        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(updateInfoReqVO.getAuditStatus())) {
            throw exception(NOT_UPDATE_PROCESS);
        }
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(updateInfoReqVO.getId());
        Long contractDOId = purchaseContractDO.getId();
        if (StrUtil.isNotEmpty(purchaseContractDO.getSaleContractCode())) {
            saleContractApi.batchUpdatePurchaseUser(Set.of(purchaseContractDO.getSaleContractCode()));
        }
        if (Objects.nonNull(updateInfoReqVO.getPaymentId())) {
            PaymentItemDTO paymentDTO = paymentItemApi.getPaymentDTO(updateInfoReqVO.getPaymentId());
            if (Objects.nonNull(paymentDTO)) {
                updateInfoReqVO.setPaymentName(paymentDTO.getName());
            }
        }
        PurchaseContractDO contractDO = PurchaseContractConvert.INSTANCE.convert(updateInfoReqVO);
        // 计算采购总金额
        calcTotalAmount(updateInfoReqVO.getChildren(),contractDO);
        List<PurchaseContractItemSaveReqVO> children = updateInfoReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            throw exception(PURCHASE_ITEM_NOT_EXISTS);
        }
        List<String> skuCodeList = children.stream().map(PurchaseContractItemSaveReqVO::getSkuCode).distinct().toList();
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
        children.forEach(s -> {
            if (Objects.isNull(s.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                if (Objects.nonNull(hsDataDTO)){
                    s.setSkuUnit(hsDataDTO.getUnit());
                }
            }
        });
        List<PurchaseContractItemDO> itemDOList = PurchaseContractConvert.INSTANCE.convert(children);


        //操作日志
        {
            PurchaseContractDO updateObj = PurchaseContractConvert.INSTANCE.convertPurchaseContractDO(updateInfoReqVO);
            List<ChangeRecord> changeRecords = new OperateCompareUtil<PurchaseContractDO>().compare(purchaseContractDO, updateObj);
            //采购主体变更
            TransformUtil.transformField(changeRecords,
                    Arrays.asList(String.valueOf(purchaseContractDO.getCompanyId()), String.valueOf(updateObj.getCompanyId())),
                    companyApi::getStringSimpleCompanyDTO,
                    CommonDict.COMPANY_NAME::equals,
                    SimpleCompanyDTO::getName);
            //采购明细变更修改
            dealPurchaseContractItem(contractDOId, updateInfoReqVO, changeRecords);
            //付款计划修改
            dealPaymentPlanChange(contractDO.getCode(), updateInfoReqVO.getPurchasePaymentPlanList(), changeRecords);
            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateInfoReqVO.getCode());
        }

        List<PurchaseContractItemDO> oldItemDoList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractDOId);

        List<Long> skuIdList = itemDOList.stream().map(PurchaseContractItemDO::getSkuId).toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);

        List<String> custCodes = itemDOList.stream().map(PurchaseContractItemDO::getCustCode).distinct().toList();
        Map<String, CustAllDTO> custAllDTOMap = custApi.getCustByCodeList(custCodes);

        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();

        itemDOList.forEach(s -> {
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(s.getSkuId());
                if (Objects.nonNull(simpleSkuDTO)) {
                    s.setMainPicture(simpleSkuDTO.getMainPicture());
                    s.setSkuName(simpleSkuDTO.getSkuName());
                    s.setThumbnail(simpleSkuDTO.getThumbnail());
                }
            }
            if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())) {
                List<Long> distinctPackageType = s.getPackageType().stream().distinct().toList();
                List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackageType.contains(pt.getId())).toList();
                if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                    List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                    s.setPackageTypeName(String.join(",", packageTypeNameList));
                    List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                    s.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                }
            }
            if (CollUtil.isNotEmpty(custAllDTOMap)) {
                CustAllDTO custAllDTO = custAllDTOMap.get(s.getCskuCode());
                if (Objects.nonNull(custAllDTO)) {
                    s.setCustName(custAllDTO.getName());
                }
            }

            //存在更新
            Optional<PurchaseContractItemDO> first = oldItemDoList.stream().filter(i -> Objects.equals(i.getId(), s.getId())).findFirst();
            if (Objects.nonNull(s.getWithTaxPrice())) {
                if (Objects.nonNull(s.getTaxRate())) {
                    BigDecimal unitAmount = s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(100)).divide(s.getTaxRate().add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP);
                    s.setUnitPrice(new JsonAmount().setCurrency(s.getWithTaxPrice().getCurrency()).setAmount(unitAmount));
                }
                if (Objects.nonNull(s.getPackagingPrice())) {
                    BigDecimal totalAmount = s.getWithTaxPrice().getAmount().add(s.getPackagingPrice().getAmount());
                    s.setTotalPrice(new JsonAmount().setCurrency(s.getWithTaxPrice().getCurrency()).setAmount(totalAmount));
                } else {
                    s.setTotalPrice(s.getWithTaxPrice());
                }
            }
            s.setCurrency(s.getWithTaxPrice().getCurrency());
            if (first.isPresent()) {
                purchaseContractItemMapper.updateById(s);
            } else {  //不存在新增
                s.setId(null);
                s.setPurchaseContractId(contractDOId);
                s.setPurchaseContractCode(purchaseContractDO.getCode());

                purchaseContractItemMapper.insert(s);
            }
        });
        //删除多余的
        List<Long> itemIdList = itemDOList.stream().map(PurchaseContractItemDO::getId).distinct().toList();
        List<PurchaseContractItemDO> deleteList = oldItemDoList.stream().filter(s -> !itemIdList.contains(s.getId())).toList();
        if (CollUtil.isNotEmpty(deleteList)) {
            List<Long> deleteIdList = deleteList.stream().map(PurchaseContractItemDO::getId).distinct().toList();
            // 更新采购计划明细转换状态
            rollbackPurchasePlan(List.of(purchaseContractDO), deleteIdList);
            deleteIdList.forEach(s -> {
                purchaseContractItemMapper.deleteById(s);
            });
        }

        //更新总计数量和金额
        updateSum(contractDO, currency);
        // 处理付款计划应付金额
        dealPaymentPlan(updateInfoReqVO, contractDO, currency, contractDO.getCode(), true);


        //设置供应商信息
        Long venderId = contractDO.getVenderId();
        SimpleVenderRespDTO simpleVender = venderApi.getSimpleVenderRespDTOById(venderId);
        if (Objects.isNull(simpleVender)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        if (Objects.isNull(contractDO.getVenderName())) {
            contractDO.setVenderName(simpleVender.getName());
        }
        if (Objects.isNull(contractDO.getCurrency())) {
            List<JsonVenderTax> taxMsg = simpleVender.getTaxMsg();
            if (CollUtil.isNotEmpty(taxMsg)){
                taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                    contractDO.setCurrency(s.getCurrency());
                });
            }
        }

        //供应商联系人
        List<VenderPocDO> venderPocDOS = venderPocService.getPocListByVenderId(venderId);
        if (CollUtil.isNotEmpty(venderPocDOS)) {
            Optional<VenderPocDO> first1 = venderPocDOS.stream().filter(s -> s.getDefaultFlag() == 1).findFirst();
            if (first1.isPresent()) {
                contractDO.setVenderPoc(first1.get());
            } else {
                contractDO.setVenderPoc(venderPocDOS.get(0));
            }
        }
        Set<String> auxiliaryPurchaseContractCodeSet = children.stream().map(PurchaseContractItemSaveReqVO::getAuxiliaryPurchaseContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Set<String> auxiliarySaleContractCodeSet = children.stream().map(PurchaseContractItemSaveReqVO::getAuxiliarySaleContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Map<String, List<UserDept>> purchaseUserAndSalesMap = purchasePlanService.getPurchaseUserAndSales(auxiliaryPurchaseContractCodeSet, auxiliarySaleContractCodeSet);
        if (CollUtil.isNotEmpty(purchaseUserAndSalesMap)){
            contractDO.setAuxiliaryPurchaseUser(purchaseUserAndSalesMap.get(CommonDict.PURCHASE_USER_FIELD_NAME));
            contractDO.setAuxiliarySales(purchaseUserAndSalesMap.get(CommonDict.SALES_FIELD_NAME));
            contractDO.setAuxiliaryManager(purchaseUserAndSalesMap.get(CommonDict.MANAGER_FIELD_NAME));
        }
        purchaseContractMapper.updateById(contractDO);
        //处理费用分摊
        dealAuxiliaryPurchaseAllocations(contractDO.getId());

        // 更新付款计划
        List<PurchasePaymentPlan> purchasePaymentPlanList = updateInfoReqVO.getPurchasePaymentPlanList();
        if (CollUtil.isNotEmpty(purchasePaymentPlanList)) {
            purchasePaymentPlanMapper.delete(PurchasePaymentPlan::getContractCode, contractDO.getCode());
            purchasePaymentPlanList.forEach(s -> {
                s.setId(null);
                s.setContractCode(contractDO.getCode());
            });
            purchasePaymentPlanMapper.insertBatch(purchasePaymentPlanList);
        }
        // 更新加减项
        List<PurchaseAddSubTerm> purchaseAddSubItemList = updateInfoReqVO.getPurchaseAddSubTermList();
        if (CollUtil.isNotEmpty(purchaseAddSubItemList)) {
            purchaseAddSubTermMapper.delete(PurchaseAddSubTerm::getContractCode, contractDO.getCode());
            purchaseAddSubItemList.forEach(s -> s.setContractCode(contractDO.getCode()));
            purchaseAddSubTermMapper.insertBatch(purchaseAddSubItemList);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateInfoReqVO.getSubmitFlag())) {
            if (BooleanEnum.YES.getValue().equals(updateInfoReqVO.getAuxiliaryFlag())){
                submitAuxiliaryTask(updateInfoReqVO.getId(),WebFrameworkUtils.getLoginUserId(), AUXILIARY_PROCESS_DEFINITION_KEY);
            }else {
                boolean exists = children.stream().anyMatch(s -> BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()));
                Map<String, Object> variable = new HashMap<>();
                if (exists) {
                    variable.put("stockPurchaseFlag", 1);
                    submitTask(updateInfoReqVO.getId(), WebFrameworkUtils.getLoginUserId(), PROCESS_DEFINITION_KEY, variable);
                } else {
                    variable.put("stockPurchaseFlag", 0);
                    submitTask(contractDO.getId(), WebFrameworkUtils.getLoginUserId(), PROCESS_DEFINITION_KEY, variable);
                }
            }
        }
        return contractDOId;
    }

    private void updateSum(PurchaseContractDO contractDO, String currency) {
        //更新总价和总数量
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractDO.getId());

        if (CollUtil.isNotEmpty(itemDOList)) {
            Integer sumQuantity = itemDOList.stream().map(PurchaseContractItemDO::getQuantity).filter(Objects::nonNull).reduce(0, Integer::sum);
            BigDecimal sumAmount = BigDecimal.valueOf(0);
            for (PurchaseContractItemDO s : itemDOList) {
                if (Objects.nonNull(s.getWithTaxPrice())
                        && Objects.nonNull(s.getQuantity())
                        && Objects.nonNull(s.getWithTaxPrice().getAmount())) {
                    BigDecimal total = s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity()));
                    sumAmount = sumAmount.add(total);
                }
            }
            contractDO.setTotalAmount(new JsonAmount().setAmount(sumAmount).setCurrency(currency));
            contractDO.setTotalQuantity(sumQuantity);
            
            // 计算采购合同金额RMB：将totalAmount转换为RMB
            if (sumAmount.compareTo(BigDecimal.ZERO) > 0 && StrUtil.isNotEmpty(currency)) {
                Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
                JsonAmount totalAmountRmb = CurrencyUtil.changeCurrency(
                    new JsonAmount().setAmount(sumAmount).setCurrency(currency),
                    CalculationDict.CURRENCY_RMB,
                    dailyRateMap
                );
                contractDO.setTotalAmountRmb(totalAmountRmb);
            }
            
            purchaseContractMapper.updateById(contractDO);
        }
    }

    private String CurrencyCheck(PurchaseContractSaveInfoReqVO reqVO) {
        List<PurchaseContractItemSaveReqVO> children = reqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        String venderCode = reqVO.getVenderCode();
        VenderInfoRespVO vender = venderService.getVenderByCode(venderCode);
        if (Objects.isNull(vender)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        List<JsonVenderTax> taxMsg = vender.getTaxMsg();
        if (CollUtil.isEmpty(taxMsg)){
            throw exception(VENDER_TAX_MSG_IS_EMPTY);
        }
        Optional<JsonVenderTax> currencyOpt = taxMsg.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getDefaultFlag()) && StrUtil.isNotEmpty(s.getCurrency())).findFirst();
        if (!currencyOpt.isPresent()){
            throw exception(VENDER_TAX_MSG_IS_NOT_DEFAULT);
        }
        String currency = currencyOpt.get().getCurrency();
        if (Objects.equals(vender.getInternalFlag(), BooleanEnum.YES.getValue())) {
            JsonAmount withTaxPrice = children.get(0).getWithTaxPrice();
            if (Objects.isNull(withTaxPrice)) {
                throw exception(PURCHASE_WITH_TAX_PRICE_NULL);
            }
            currency = withTaxPrice.getCurrency();
        } else {
            reqVO.getChildren().forEach(s->{
                checkCurrencyByCompany(reqVO.getCompanyId(), s.getCurrency());
            });
        }

        Boolean totalAmountFlag = isCurrencyMatching(reqVO.getTotalAmount(), currency);
        if (!totalAmountFlag) {
            throw exception(PURCHASE_CONTRACT_CURRENCY_NOT_MATCHING, currency, reqVO.getTotalAmount().getCurrency());
        }
        Boolean freightFlag = isCurrencyMatching(reqVO.getFreight(), currency);
        if (!freightFlag) {
            throw exception(PURCHASE_CONTRACT_FREIGHT_CURRENCY_NOT_MATCHING, currency, currency, reqVO.getFreight().getCurrency());
        }
        Boolean costFlag = isCurrencyMatching(reqVO.getOtherCost(), currency);
        if (!costFlag) {
            throw exception(PURCHASE_CONTRACT_COST_CURRENCY_NOT_MATCHING, currency, currency, reqVO.getOtherCost().getCurrency());
        }
        Boolean payedFlag = isCurrencyMatching(reqVO.getPayedAmount(), currency);
        if (!payedFlag) {
            throw exception(PURCHASE_CONTRACT_PAYED_CURRENCY_NOT_MATCHING, currency, currency, reqVO.getPayedAmount().getCurrency());
        }
        Boolean prepayFlag = isCurrencyMatching(reqVO.getPrepayAmount(), currency);
        if (!prepayFlag) {
            throw exception(PURCHASE_CONTRACT_PREPAY_CURRENCY_NOT_MATCHING, currency, currency, reqVO.getPrepayAmount().getCurrency());
        }
        boolean invoiceCurrencyFlag = StrUtil.isEmpty(reqVO.getInvoicedCurrency()) || Objects.equals(reqVO.getInvoicedCurrency(), currency);
        if (!invoiceCurrencyFlag) {
            throw exception(PURCHASE_CONTRACT_INVOICE_CURRENCY_NOT_MATCHING, currency, currency, reqVO.getInvoicedCurrency());
        }
        children.forEach(s -> {
            s.setPackagingPrice(changeCurrency(s.getPackagingPrice(), currencyOpt.get().getCurrency()));
            s.setShippingPrice(changeCurrency(s.getShippingPrice(), currencyOpt.get().getCurrency()));
            s.setCurrency(currencyOpt.get().getCurrency());
        });
//        String curr = currency;
        // 暂时不用校验采购明细与供应商币种一致性
//        boolean childrenAmountFlag = children.stream().map(PurchaseContractItemSaveReqVO::getWithTaxPrice).map(JsonAmount::getCurrency).allMatch(curr::equals);
        if (totalAmountFlag && freightFlag && costFlag && payedFlag && prepayFlag && invoiceCurrencyFlag) {
            return currency;
        }
        return null;


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

    private Boolean isCurrencyMatching(JsonAmount amount, String currency) {
        return Objects.isNull(amount) ||
                (StrUtil.isNotEmpty(amount.getCurrency()) && currency.equals(amount.getCurrency()));
    }

    public void RollWriteQuote(Long contractId) {
        List<PurchaseContractItemDO> itemList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractId);

        List<PurchaseContractItemDO> syncList = itemList.stream().filter(Objects::nonNull).filter(s -> s.getSyncQuoteFlag() == 1).toList();
        if (CollUtil.isEmpty(syncList)) {
            return;
        }
        List<Long> venderIdList = syncList.stream().map(PurchaseContractItemDO::getVenderId).toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIdList);
        syncList.forEach(s -> {
            QuoteItemSaveReqVO quoteItemSaveReqVO = BeanUtils.toBean(s, QuoteItemSaveReqVO.class);
            if (Objects.nonNull(quoteItemSaveReqVO)) {
                quoteItemSaveReqVO.setId(null);
                quoteItemSaveReqVO.setDefaultFlag(0);
                quoteItemSaveReqVO.setQuoteDate(LocalDateTime.now());

                // 确保 withTaxPrice 不为空
                if (Objects.isNull(quoteItemSaveReqVO.getWithTaxPrice())) {
                    quoteItemSaveReqVO.setWithTaxPrice(new JsonAmount().setCurrency("CNY").setAmount(BigDecimal.ZERO));
                }

                // 设置包装价格
                if (Objects.isNull(quoteItemSaveReqVO.getPackagingPrice())) {
                    quoteItemSaveReqVO.setPackagingPrice(new JsonAmount().setCurrency(quoteItemSaveReqVO.getWithTaxPrice().getCurrency()).setAmount(BigDecimal.ZERO));
                }

                // 设置运输价格
                if (Objects.isNull(quoteItemSaveReqVO.getShippingPrice())) {
                    quoteItemSaveReqVO.setShippingPrice(new JsonAmount().setCurrency(quoteItemSaveReqVO.getWithTaxPrice().getCurrency()).setAmount(BigDecimal.ZERO));
                }

                // 计算总价
                BigDecimal total = quoteItemSaveReqVO.getWithTaxPrice().getAmount();
                if (quoteItemSaveReqVO.getPackageFlag() != 1 && Objects.nonNull(quoteItemSaveReqVO.getPackagingPrice()) && Objects.nonNull(quoteItemSaveReqVO.getPackagingPrice().getAmount())) {
                    total = total.add(quoteItemSaveReqVO.getPackagingPrice().getAmount());
                }
                if (quoteItemSaveReqVO.getFreightFlag() != 1 && Objects.nonNull(quoteItemSaveReqVO.getShippingPrice()) && Objects.nonNull(quoteItemSaveReqVO.getShippingPrice().getAmount())) {
                    total = total.add(quoteItemSaveReqVO.getShippingPrice().getAmount());
                }
                quoteItemSaveReqVO.setTotalPrice(new JsonAmount().setCurrency(quoteItemSaveReqVO.getWithTaxPrice().getCurrency()).setAmount(total));

                Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOList.stream().filter(v -> Objects.equals(v.getId(), s.getVenderId())).findFirst();
                first.ifPresent(simpleVenderRespDTO -> quoteItemSaveReqVO.setVenderName(simpleVenderRespDTO.getName()));
                quoteItemService.createQuoteItem(quoteItemSaveReqVO);
            }
        });
        List<Long> itemIdList = syncList.stream().map(PurchaseContractItemDO::getId).toList();

        purchaseContractItemMapper.update(
                PurchaseContractItemDO.builder().syncQuoteFlag(0).build(),
                new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getId, itemIdList));
    }

    private void dealPaymentPlanChange(String contractCode, List<PurchasePaymentPlan> purchasePaymentPlanList, List<ChangeRecord> changeRecords) {
        List<PurchasePaymentPlan> basePurchasePaymentPlans = purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, contractCode);
        List<PurchasePaymentPlan> reqPurchasePaymentPlans = BeanUtils.toBean(purchasePaymentPlanList, PurchasePaymentPlan.class);

        if (CollUtil.isEmpty(reqPurchasePaymentPlans) && CollUtil.isNotEmpty(basePurchasePaymentPlans)) {     //判断删除
            changeRecords.add(new ChangeRecord().setFieldName("清空采购付款计划"));
        } else if (CollUtil.isNotEmpty(reqPurchasePaymentPlans) && CollUtil.isEmpty(basePurchasePaymentPlans)) {    //判断新增
            reqPurchasePaymentPlans.forEach(s -> {
                Integer step = s.getStep();
                String paymentPlanStep = DictFrameworkUtils.getDictDataLabel("payment_plan_step", step);
                changeRecords.add(new ChangeRecord().setFieldName("新增采购付款计划-" + paymentPlanStep));
            });
        } else if (CollUtil.isNotEmpty(reqPurchasePaymentPlans) && CollUtil.isNotEmpty(basePurchasePaymentPlans)) {
            Map<Long, PurchasePaymentPlan> dtoMap = basePurchasePaymentPlans.stream().collect(Collectors.toMap(PurchasePaymentPlan::getId, s -> s));
            List<PurchasePaymentPlan> updateList = reqPurchasePaymentPlans.stream().filter(s -> Objects.nonNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(updateList)) {
                updateList.forEach(s -> {
                    PurchasePaymentPlan purchasePaymentPlan = dtoMap.get(s.getId());
                    if (Objects.isNull(purchasePaymentPlan)) {
                        return;
                    }
                    List<ChangeRecord> quoteRecords = new OperateCompareUtil<PurchasePaymentPlan>().compare(purchasePaymentPlan, s);
                    if (CollUtil.isNotEmpty(quoteRecords)) {
                        quoteRecords.forEach(quoteRecord -> {
                            quoteRecord.setFieldName(String.format("采购付款计划:%s", quoteRecord.getFieldName(), quoteRecord.getOldValue(), quoteRecord.getValue()));
                        });
                        changeRecords.addAll(quoteRecords);
                    }
                });
            }

            List<PurchasePaymentPlan> insertList = reqPurchasePaymentPlans.stream().filter(s -> Objects.isNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                insertList.forEach(insert -> {
                    Integer step = insert.getStep();
                    String paymentPlanStep = DictFrameworkUtils.getDictDataLabel("payment_plan_step", step);
                    changeRecords.add(new ChangeRecord().setFieldName("新增采购付款计划-" + paymentPlanStep));
                });
            }

            Set<Long> list = reqPurchasePaymentPlans.stream().map(PurchasePaymentPlan::getId).collect(Collectors.toSet());
            List<PurchasePaymentPlan> deleteList = basePurchasePaymentPlans.stream().filter(s -> !list.contains(s.getId())).toList();
            if (CollUtil.isNotEmpty(deleteList)) {
                deleteList.forEach(delete -> {
                    Integer step = delete.getStep();
                    String paymentPlanStep = DictFrameworkUtils.getDictDataLabel("payment_plan_step", step);
                    changeRecords.add(new ChangeRecord().setFieldName("删除采购付款计划-" + paymentPlanStep));
                });
            }
        }
    }

    private void dealPurchaseContractItem(Long contractDOId, PurchaseContractSaveInfoReqVO updateReqVO, List<ChangeRecord> changeRecords) {
        List<PurchaseContractItemDO> baseContractItemList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(contractDOId);
        List<PurchaseContractItemDO> reqContractItemList = BeanUtils.toBean(updateReqVO.getChildren(), PurchaseContractItemDO.class);

        if (CollUtil.isEmpty(reqContractItemList) && CollUtil.isNotEmpty(baseContractItemList)) {     //判断删除
            changeRecords.add(new ChangeRecord().setFieldName("清空采购合同明细"));
        } else if (CollUtil.isNotEmpty(reqContractItemList) && CollUtil.isEmpty(baseContractItemList)) {    //判断新增
            reqContractItemList.forEach(s -> {
                changeRecords.add(new ChangeRecord().setFieldName("新增采购合同明细"));
            });
        } else if (CollUtil.isNotEmpty(reqContractItemList) && CollUtil.isNotEmpty(baseContractItemList)) {
            Map<Long, PurchaseContractItemDO> dtoMap = baseContractItemList.stream().collect(Collectors.toMap(PurchaseContractItemDO::getId, s -> s));
            List<PurchaseContractItemDO> updateList = reqContractItemList.stream().filter(s -> Objects.nonNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(updateList)) {
                updateList.forEach(s -> {
                    PurchaseContractItemDO purchaseContractItemDO = dtoMap.get(s.getId());
                    if (Objects.isNull(purchaseContractItemDO)) {
                        return;
                    }
                    List<ChangeRecord> quoteRecords = new OperateCompareUtil<PurchaseContractItemDO>().compare(purchaseContractItemDO, s);
                    if (CollUtil.isNotEmpty(quoteRecords)) {
                        quoteRecords.forEach(quoteRecord -> {
                            quoteRecord.setFieldName(String.format("采购合同明细%s:%s", s.getSortNum(), quoteRecord.getFieldName(), quoteRecord.getOldValue(), quoteRecord.getValue()));
                        });
                        changeRecords.addAll(quoteRecords);
                    }
                });
            }

            List<PurchaseContractItemDO> insertList = reqContractItemList.stream().filter(s -> Objects.isNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                insertList.forEach(insert -> {
                    changeRecords.add(new ChangeRecord().setFieldName("新增采购合同明细"));
                });
            }

            Set<Long> list = reqContractItemList.stream().map(PurchaseContractItemDO::getId).collect(Collectors.toSet());
            List<PurchaseContractItemDO> deleteList = baseContractItemList.stream().filter(s -> !list.contains(s.getId())).toList();
            if (CollUtil.isNotEmpty(deleteList)) {
                deleteList.forEach(delete -> {
                    changeRecords.add(new ChangeRecord().setFieldName("删除采购合同明细"));
                });
            }
        }
    }

    @Override
    public void deletePurchaseContract(Long id, Integer isAuxiliary) {
        PurchaseContractDO purchaseContractDo = validatePurchaseContractExists(id);
        if (!Objects.equals(isAuxiliary, purchaseContractDo.getAuxiliaryFlag())) {
            if (Objects.equals(BooleanEnum.YES.getValue(), isAuxiliary)) {
                throw exception(PURCHASE_CONTRACT_IS_NOT_AUXILIARY);
            } else {
                throw exception(PURCHASE_CONTRACT_IS_AUXILIARY);
            }
        }
        purchaseContractMapper.deleteById(id);
        if (CollUtil.isNotEmpty(purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(id))) {
            purchaseContractItemService.deletePurchaseContractItemListByPurchaseContractId(id);
        }
    }

    @Override
    public void deleteChangePurchaseContract(Long id) {
        validateChangePurchaseContractExists(id);
        purchasecontractchangeMapper.deleteById(id);
    }


    private PurchaseContractDO validatePurchaseContractExists(Long id) {
        PurchaseContractDO contract = purchaseContractMapper.selectById(id);
        if (Objects.isNull(contract)) {
            throw exception(PURCHASE_PLAN_NOT_EXISTS);
        }
        return contract;
    }

    private PurchaseContractChange validateChangePurchaseContractExists(Long id) {
        PurchaseContractChange purchaseContractChange = purchasecontractchangeMapper.selectById(id);
        if (Objects.isNull(purchaseContractChange)) {
            throw exception(PURCHASE_CONTRACT_CHANGE_NOT_EXISTS);
        }
        return purchaseContractChange;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO, boolean changeFlag) {
        if (changeFlag) {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), getChangeProcessDefinitionKey());
        } else {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), getProcessDefinitionKey());
        }
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO, boolean changeFlag) {
        if (changeFlag) {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), getChangeProcessDefinitionKey());
        } else {
            bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), getProcessDefinitionKey());
        }
    }

    @Override
    public void submitTask(Long purchaseContractId, Long userId, String processDefinitionKey, Map<String, Object> variables) {
        Optional<PurchaseContractDO> first = purchaseContractMapper.selectList(PurchaseContractDO::getId, purchaseContractId).stream().findFirst();
        // 数据有效性校验
        first.ifPresent(this::createContractKeyParaCheck);
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, purchaseContractId, variables, Map.of());
        updateAuditStatus(purchaseContractId, BpmProcessInstanceResultEnum.PROCESS.getResult(),processInstanceId);
    }

    //采购计划转采购合同后会存在数据不全的情况，没有补充数据的判断
    private void createContractKeyParaCheck(PurchaseContractDO contractDO) {
        if (contractDO == null)
            return;
        if (Objects.nonNull(contractDO.getAuxiliaryFlag()) && contractDO.getAuxiliaryFlag() != CalculationDict.ONE) {
            if (Objects.isNull(contractDO.getManager().getUserId())) {
                throw exception(PLEASE_SELECT_MERCHANDISER);
            }
        }
        checkData(contractDO);
    }

    private void checkData(PurchaseContractDO contractDO) {
        if (Objects.isNull(contractDO.getPurchasePlanId())) {
            throw exception(PURCHASE_PLAN_EMPTY);
        }
        if (Objects.isNull(contractDO.getPurchaseUserId())) {
            throw exception(PURCHASE_USER_EMPTY);
        }
        if (Objects.isNull(contractDO.getVenderId())) {
            throw exception(PURCHASE_VENDER_EMPTY);
        }
        if (Objects.isNull(contractDO.getPaymentId())) {
            throw exception(PURCHASE_PAYMENT_EMPTY);
        }
        if (Objects.isNull(contractDO.getTaxType())) {
            throw exception(PURCHASE_TAX_EMPTY);
        }
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(auditableId);
        PurchaseContractDO updateDO = PurchaseContractDO.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)){
            updateDO.setProcessInstanceId(processInstanceId);
        }
        purchaseContractMapper.updateById(updateDO);
        Integer status = 0;
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, auditableId);
            // 审核通过回写销售合同采购含税价和跟单员及真实采购员
            saleContractApi.updateRealPurchasePrice(purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getSaleContractItemId).toList(), purchaseContractDO.getManager(), purchaseContractDO.getPurchaseUserId());
            RollWriteQuote(auditableId);
            status = PurchaseContractStatusEnum.AWAITING_ORDER.getCode();
            // 回写销售合同赠品数量
            Map<Long, Integer> freeQuantityMap = purchaseContractItemDOS.stream()
                    .filter(item -> BooleanEnum.NO.getValue().equals(item.getSplitFlag())
                            && BooleanEnum.YES.getValue().equals(item.getFreeFlag())
                            && Objects.nonNull(item.getSaleContractItemId()))
                    .collect(Collectors.groupingBy(
                            PurchaseContractItemDO::getSaleContractItemId,
                            Collectors.summingInt(PurchaseContractItemDO::getFreeQuantity)
                    ));
            saleContractApi.rewritePurchaseFreeQuantity(freeQuantityMap, false);
        } else if (BpmProcessInstanceResultEnum.REJECT.getResult().equals(auditStatus)) {
            status = PurchaseContractStatusEnum.REJECTED.getCode();
        } else if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(auditStatus)) {
            status = PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode();
        } else if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            status = PurchaseContractStatusEnum.AWAITING_APPROVAL.getCode();
        } else if (BpmProcessInstanceResultEnum.CANCEL.getResult().equals(auditStatus)) {
            status = PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode();
        }
        purchaseContractMapper.updateById(PurchaseContractDO.builder().id(auditableId).auditStatus(auditStatus).contractStatus(status).build());

        //包材审批通过后重新生成归集信息
        if (purchaseContractDO.getAuxiliaryFlag() == 1) {
            List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, purchaseContractDO.getId());
            List<FeeShareReqDTO> feeShares = new ArrayList<>();
            if (CollUtil.isEmpty(purchaseContractItemDOS)) {
                throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
            }
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(purchaseContractDO.getCompanyId());
            if (Objects.isNull(companyDTO)) {
                throw exception(COMPANY_NOT_EXISTS);
            }
            purchaseContractItemDOS.forEach(s -> {
                if (Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.OTHER.getCode())) {
                    return;
                }
                FeeShareReqDTO dto = new FeeShareReqDTO();
                dto.setCompanyId(purchaseContractDO.getCompanyId()).setCompanyName(companyDTO.getCompanyName());
                dto.setBusinessId(purchaseContractDO.getId()).setBusinessCode(purchaseContractDO.getCode()).setBusinessType(FeeShareSourceTypeEnum.AUXILIARY_PURCHASE.getValue());
                dto.setAmount(new JsonAmount().setCurrency("").setAmount(BigDecimal.ZERO)); //销售合同和采购合同类型的归集，金额在子表，主表为0
                dto.setFeeShareType(null);
                dto.setRelationType(null);
                dto.setInputUser(adminUserApi.getUserDeptByUserId(Long.parseLong(purchaseContractDO.getCreator())));
                dto.setAuditStatus(2).setStatus(1).setSourceStatus(2);

                if (Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.PRODUCT.getCode())) {
                    PurchaseContractAllDTO purchaseContractAllDTO = new PurchaseContractAllDTO();
                    purchaseContractAllDTO.setCode(s.getAuxiliaryPurchaseContractCode());
                    dto.setOrderType(2);
                    purchaseContractAllDTO.setAmount(
                            new JsonAmount().setAmount(s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity())))
                                    .setCurrency(s.getWithTaxPrice().getCurrency()));
                    dto.setPurchaseChildren(List.of(purchaseContractAllDTO));
                } else if (Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.ORDER.getCode())) {
                    SmsContractAllDTO smsContractAllDTO = new SmsContractAllDTO();
                    smsContractAllDTO.setCode(s.getAuxiliarySaleContractCode());
                    dto.setOrderType(1);
                    smsContractAllDTO.setAmount(
                            new JsonAmount().setAmount(s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity())))
                                    .setCurrency(s.getWithTaxPrice().getCurrency()));
                    dto.setSmsChildren(List.of(smsContractAllDTO));
                }
                feeShares.add(dto);
            });

            feeShareApi.updateFeeShareByDTOList(feeShares);
        }

        orderLinkApi.updateOrderLinkStatus(purchaseContractDO.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, purchaseContractDO.getLinkCodeList(), status);
    }

    /**
     * 生成内部购销合同
     *
     * @param contractId 采购合同主键
     */
    private boolean validateInternalContract(Long contractId) {
        // 查出采购合同对应计划下推所有采购合同
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(contractId);
        Long purchasePlanId = purchaseContractDO.getPurchasePlanId();
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getPurchasePlanId, purchasePlanId);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PLAN_PURCHASE_CONTRACT_NOT_EXISTS, purchasePlanId);
        }
        boolean approveFlag = purchaseContractDOList.stream().allMatch(s -> BpmProcessInstanceResultEnum.APPROVE.getResult().equals(s.getAuditStatus()));
        // 只要有一个合同没有审批通过则不生成内部购销合同
        return approveFlag;
//        // 获取采购计划信息
//        PurchasePlanInfoRespVO purchasePlan = purchasePlanService.getPurchasePlanContainsContract(new PurchasePlanDetailReq().setPurchasePlanId(purchasePlanId));
//        if (Objects.isNull(purchasePlan)){
//            throw exception(PURCHASE_PLAN_NOT_EXISTS);
//        }
//        List<PurchasePlanItemInfoRespVO> planChildren = purchasePlan.getChildren();
//        if (CollUtil.isEmpty(planChildren)){
//            throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
//        }
//        // 添加组合产品
//        Boolean lastPathToProduceFlag = false;
//        List<PurchasePlanItemToContractSaveReqVO> combineItemList = purchasePlan.getCombineList();
//        if (!CollectionUtils.isEmpty(combineItemList)) {
//            lastPathToProduceFlag = true;
//            // 校验锁定数量
//            combineItemList.forEach(x -> {
//                if (x.getCurrentLockQuantity() != null && x.getCurrentLockQuantity() > x.getSaleQuantity()) {
//                    throw exception(PURCHASE_PLAN_ITEM_LOCK_EXCEED,x.getSkuName());
//                }
//            });
//        }
//        Long saleContractId = purchasePlan.getSaleContractId();
//        String purchasePlanCode = purchasePlan.getCode();
//        // 生成内部购销合同
//        List<String> itemCodeList = purchasePlanItemService.getItemCodeListByPurchasePlanId(purchasePlanId);
//        // 2. 生成首尾节点间的购销合同，根据规则分配利润，更新每条产品的分配利润信息到销售合同明细
//        // 采购计划-明细真实采购价  使用采购合同真实采购价
//        Map<Long, Tuple> realPurchasePriceCollect = saleContractApi.calcRealPurchasePrice(planChildren.stream().map(PurchasePlanItemInfoRespVO::getSaleContractItemId).toList());
//        // 生成公司间购销合同计算分配利润
//        Map<String, JsonAmount> lastNodeSkuPriceMap = new HashMap<>();
//        if (ObjectUtil.isNotNull(saleContractId)) {
//            lastNodeSkuPriceMap = saleContractApi.generateContract(saleContractId, purchasePlanId, purchasePlanCode, realPurchasePriceCollect,itemCodeList);
//            logger.info("尾部节点的销售合同采购单价:{}", JSONObject.toJSONString(lastNodeSkuPriceMap));
//        }
//        PurchasePlanItemToContractSaveInfoReqVO reqVO = BeanUtils.toBean(purchasePlan, PurchasePlanItemToContractSaveInfoReqVO.class);
//        // 将req中的产品都换为真实退税率
//        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
//        List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
//        if (CollUtil.isNotEmpty(planList) && CollUtil.isNotEmpty(realPurchasePriceCollect)){
//            planList.forEach(s->{
//                Tuple tuple = realPurchasePriceCollect.get(s.getSaleContractItemId());
//                if (Objects.nonNull(tuple)){
//                    s.setTaxRate(tuple.get(1));
//                }
//            });
//        }
//        if (CollUtil.isNotEmpty(combineList) && CollUtil.isNotEmpty(realPurchasePriceCollect)){
//            combineList.forEach(s->{
//                Tuple tuple = realPurchasePriceCollect.get(s.getSaleContractItemId());
//                if (Objects.nonNull(tuple)){
//                    s.setTaxRate(tuple.get(1));
//                }
//            });
//        }
//        // 3. 若存在组合产品分开采购的情况，生成尾部节点与加工型企业的销售合同
//        if (lastPathToProduceFlag) {
//            List<CreatedResponse> responses = purchasePlanService.handleLastToProducedContract(reqVO, lastNodeSkuPriceMap);
//        }
//        // 4. 若锁定库存中存在加工型企业，生成锁定库存的购销合同
//        purchasePlanService.handleProducedLockContract(reqVO, lastNodeSkuPriceMap);
//        // 5. 转换尾部节点向供应商采购合同，存在内部供应商时进行转换
//        purchasePlanService.lastCompanyToVenderContract(reqVO, lastNodeSkuPriceMap,false,true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishPurchaseContract(List<Long> contractIdList) {
        contractIdList.forEach(this::validateAntiAuditStatus);
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectBatchIds(contractIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return;
        }
        boolean closeFlag = purchaseContractDOS.stream().anyMatch(s -> PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(s.getContractStatus()));
        if (closeFlag){
            throw exception(PURCHASE_CONTRACT_CLOSED);
        }
        // 辅料采购判断是否已存在对公支付
        Set<String> auxiliaryCodeSet = purchaseContractDOS.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getAuxiliaryFlag())).map(PurchaseContractDO::getCode).collect(Collectors.toSet());
        boolean auxiliaryExists = paymentAppApi.validateRelationCodeExists(auxiliaryCodeSet);
        if (auxiliaryExists){
            throw exception(AUXILIARY_PURCHASE_CONTRACT_EXISTS);
        }
        List<String> purchaseCodeLsit = purchaseContractDOS.stream().map(PurchaseContractDO::getCode).distinct().toList();
        purchaseContractMapper.update(
                new PurchaseContractDO().setContractStatus(PurchaseContractStatusEnum.CASE_SETTLED.getCode())
                        .setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult()),
                new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getId, contractIdList));
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getSaleContractItemId, PurchaseContractItemDO::getFreeFlag, PurchaseContractItemDO::getFreeQuantity).in(PurchaseContractItemDO::getPurchaseContractId, contractIdList));
        LambdaUpdateWrapper<PurchaseContractItemDO> updateWapper = new LambdaUpdateWrapper<PurchaseContractItemDO>().set(PurchaseContractItemDO::getCancelFlag, BooleanEnum.YES.getValue())
                .in(PurchaseContractItemDO::getPurchaseContractId, contractIdList);
        purchaseContractItemMapper.update(updateWapper);
        // 回写采购计划状态
        rollbackPurchasePlan(purchaseContractDOS,null);
        // 删除下单生成的库存明细
        stockApi.deleteStockByPurchaseContractCode(purchaseCodeLsit);
        // 回写销售合同采购员
        saleContractApi.batchUpdatePurchaseUser(purchaseContractDOS.stream().map(PurchaseContractDO::getSaleContractCode).collect(Collectors.toSet()));
        List<OrderLinkDTO> orderLinkDTOList = purchaseContractDOS.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, x, null, s.getPurchasePlanCode(), PurchaseContractStatusEnum.CASE_SETTLED.getCode(), null)).toList();
            }
            return null;
        }).filter(Objects::nonNull).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchaseContractStatusEnum.CASE_SETTLED.getCode());
        // 回写销售合同赠品数量
        Map<Long, Integer> freeQuantityMap = purchaseContractItemDOS.stream()
                .filter(item -> BooleanEnum.NO.getValue().equals(item.getSplitFlag())
                        && BooleanEnum.YES.getValue().equals(item.getFreeFlag())
                        && Objects.nonNull(item.getSaleContractItemId()))
                .collect(Collectors.groupingBy(
                        PurchaseContractItemDO::getSaleContractItemId,
                        Collectors.summingInt(PurchaseContractItemDO::getFreeQuantity)
                ));
        saleContractApi.rewritePurchaseFreeQuantity(freeQuantityMap, true);
    }

    private void rollbackPurchasePlan(List<PurchaseContractDO> purchaseContractDOS,List<Long> itemIdList) {
        List<Long> purchasePlanIdList = purchaseContractDOS.stream().map(PurchaseContractDO::getPurchasePlanId).distinct().toList();
        List<PurchasePlanDO> planList = purchasePlanService.getPlanListByIds(purchasePlanIdList);
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        // 处理拆分采购计划下推的合同
        List<PurchasePlanDO> splitPlanList = planList.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getSplitFlag())).toList();
        dealSplitPlanList(splitPlanList, purchaseContractDOS.stream().map(PurchaseContractDO::getId).toList(),itemIdList);
        // 处理分批次下推的合同
        List<PurchasePlanDO> batchPlanList = planList.stream().filter(s -> BooleanEnum.NO.getValue().equals(s.getSplitFlag())).toList();
        dealBatchPlanList(batchPlanList, purchaseContractDOS.stream().map(PurchaseContractDO::getId).toList(),itemIdList);
    }

    /**
     * 处理分批次下推合同
     *
     * @param batchPlanList 分批次下推的合同列表
     */
    private void dealBatchPlanList(List<PurchasePlanDO> batchPlanList, List<Long> contractIdList,List<Long> itemIdList) {
        if (CollUtil.isEmpty(batchPlanList)) {
            return;
        }
        List<Long> planIdList = batchPlanList.stream().map(PurchasePlanDO::getId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(PurchaseContractDO::getPurchasePlanId, planIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return;
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList;
        if (CollUtil.isEmpty(itemIdList)){
            purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractIdList);
        }else {
            purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, itemIdList);
        }

        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return;
        }
        List<String> sourceUniqueCodeList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getSourceUniqueCode).distinct().toList();
        if (CollUtil.isEmpty(sourceUniqueCodeList)) {
            return;
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getPurchasePlanId, planIdList);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return;
        }
        List<PurchasePlanItemDO> updatePlanItemList = new ArrayList<>();
        purchasePlanItemDOS.forEach(s -> {
            if (sourceUniqueCodeList.contains(s.getUniqueCode())) {
                s.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                if (s.getNeedPurQuantity() == 0){
                    s.setNeedPurQuantity(s.getConvertedQuantity());
                }
                s.setConvertedQuantity(0);
                updatePlanItemList.add(s);
            }
        });
        if (CollUtil.isNotEmpty(updatePlanItemList)) {
            purchasePlanItemMapper.updateBatch(updatePlanItemList);
        }
        Map<Long, List<PurchasePlanItemDO>> planItemMap = purchasePlanItemDOS.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getPurchasePlanId));

        // 仅用作第一次下推时拆分的情况
        Map<Long, List<PurchaseContractDO>> planMap = purchaseContractDOS.stream().collect(Collectors.groupingBy(PurchaseContractDO::getPurchasePlanId));
        // 将销售明细id替换为源id 分批次下推仅有一个采购计划明细 此明细对应的销售明细为拆分前的
        List<Long> saleContractItemIdList = purchaseContractItemDOList.stream().filter(s -> contractIdList.contains(s.getPurchaseContractId())).map(PurchaseContractItemDO::getSaleContractItemId).distinct().toList();
        Map<Long, String> saleContractItemIdMap = saleContractApi.getSaleContractItemIdMap(saleContractItemIdList);
        Map<Long, List<PurchaseContractItemDO>> itemMap = purchaseContractItemDOList.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getPurchaseContractId));
        Map<Long, Map<String, Integer>> updateMap = new HashMap<>();
        planMap.forEach((k, v) -> {
            if (CollUtil.isEmpty(v)) {
                return;
            }
            Map<String, Integer> updateItemMap = new HashMap<>();
            v.forEach(s -> {
                if (!PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(s.getContractStatus())&&CollUtil.isEmpty(itemIdList)) {
                    return;
                }
                List<PurchaseContractItemDO> purchaseContractItemDOS = itemMap.get(s.getId());
                if (CollUtil.isEmpty(purchaseContractItemDOS)) {
                    return;
                }
                purchaseContractItemDOS.forEach(item -> {
                    if (CollUtil.isNotEmpty(saleContractItemIdMap)) {
                        String saleUniqueCode = saleContractItemIdMap.get(item.getSaleContractItemId());
                        if (StrUtil.isEmpty(saleUniqueCode)) {
                            return;
                        }
                        updateItemMap.merge(saleUniqueCode, item.getQuantity(), Integer::sum);
                    } else {
                        updateItemMap.merge(item.getSaleItemUniqueCode(), item.getQuantity(), Integer::sum);
                    }
                });
            });
            updateMap.put(k, updateItemMap);
        });
        purchasePlanService.updateBatchPlanList(updateMap, saleContractItemIdList);
    }

    /**
     * 处理拆分采购计划
     *
     * @param splitPlanList 拆分计划列表
     */
    private void dealSplitPlanList(List<PurchasePlanDO> splitPlanList, List<Long> contractIdList,List<Long> itemIdList) {
        if (CollUtil.isEmpty(splitPlanList)) {
            return;
        }
        dealNotFirstSplitPlan(splitPlanList, contractIdList,itemIdList);
    }

    /**
     * 处理拆分采购计划
     *
     * @param splitPlanList 拆分计划列表
     * @param contractIdList    采购合同id列表
     */
    private void dealNotFirstSplitPlan(List<PurchasePlanDO> splitPlanList, List<Long> contractIdList,List<Long> itemIdList) {
        dealFirstSplitPlan(splitPlanList, contractIdList,itemIdList);
        // 非第一次拆分
        List<PurchasePlanDO> notFirstSplitPlan = splitPlanList.stream().filter(s -> Objects.nonNull(s.getSourcePlanId())).toList();
        if (CollUtil.isEmpty(notFirstSplitPlan)) {
            return;
        }
        List<Long> planIdList = notFirstSplitPlan.stream().map(PurchasePlanDO::getId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(PurchaseContractDO::getPurchasePlanId, planIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return;
        }
        Map<Long, List<PurchaseContractDO>> planMap = purchaseContractDOS.stream().collect(Collectors.groupingBy(PurchaseContractDO::getPurchasePlanId));
        List<Long> updatePlanIds = new ArrayList<>();
        List<Long> updatePlanStatusList = new ArrayList<>();
        planMap.forEach((k, v) -> {
            if (CollUtil.isEmpty(v)) {
                return;
            }
            AtomicBoolean allSettled = new AtomicBoolean(true);
            v.forEach(s->{
                if (!PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(s.getContractStatus())){
                    allSettled.set(false);
                }else {
                    updatePlanStatusList.add(k);
                }
            });
            // 拆分计划对应的合同都作废的情况下回写采购计划
            if (allSettled.get()) {
                updatePlanIds.add(k);
            }
        });
        purchasePlanService.rollbackSplitPurchasePlan(updatePlanIds);
    }

    /**
     * 处理第一次下推采购合同拆分逻辑
     *
     * @param firstSplitPlan 第一次下推采购计划
     * @param contractIdList 采购合同id
     */
    private void dealFirstSplitPlan(List<PurchasePlanDO> firstSplitPlan, List<Long> contractIdList,List<Long> itemIdList) {
        if (CollUtil.isEmpty(firstSplitPlan) || CollUtil.isEmpty(contractIdList)) {
            return;
        }
        // 获取本次作废所有合同明细
        List<PurchaseContractItemDO> cancelContractItemList;
        if (CollUtil.isEmpty(itemIdList)){
            cancelContractItemList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractIdList);
        }else {
            cancelContractItemList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, itemIdList);
        }

        // 抽取采购合同明细跟计划明细对应关系
        if (CollUtil.isEmpty(cancelContractItemList)) {
            return;
        }
        List<String> sourceUniqueCodeList = cancelContractItemList.stream().map(PurchaseContractItemDO::getSourceUniqueCode).distinct().toList();
        if (CollUtil.isEmpty(sourceUniqueCodeList)) {
            return;
        }
        List<Long> firstSplitPlanIdList = firstSplitPlan.stream().map(PurchasePlanDO::getId).toList();
        dealPlanItem(firstSplitPlanIdList,sourceUniqueCodeList);
    }
    private void dealPlanItem(List<Long> planIdList,List<String> sourceUniqueCodeList){
        // 获取合同对应所有采购计划明细
        List<PurchasePlanItemDO> cancelPlanItemList = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getPurchasePlanId,planIdList);
        if (CollUtil.isEmpty(cancelPlanItemList)) {
            return;
        }
        List<PurchasePlanItemDO> updateItemList = new ArrayList<>();
        Set<Long> updatePlanIdList = new HashSet<>();
        cancelPlanItemList.stream().filter(s -> sourceUniqueCodeList.contains(s.getUniqueCode())).forEach(s -> {
            s.setConvertedFlag(BooleanEnum.NO.getValue());
            s.setConvertedQuantity(0);
            updateItemList.add(s);
            updatePlanIdList.add(s.getPurchasePlanId());
        });

        Map<Long, Map<Integer, List<PurchasePlanItemDO>>> planItemMap = cancelPlanItemList.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getPurchasePlanId, Collectors.groupingBy(PurchasePlanItemDO::getOneSplitNum)));
        planItemMap.forEach((planId, itemMap) -> {
            if (CollUtil.isEmpty(itemMap)) {
                return;
            }
            itemMap.forEach((k, v) -> {
                if (CollUtil.isEmpty(v)) {
                    return;
                }
                Map<Integer, List<PurchasePlanItemDO>> twoPlanItemMap = v.stream().filter(s -> Objects.nonNull(s.getTwoSplitNum())).collect(Collectors.groupingBy(PurchasePlanItemDO::getTwoSplitNum));
                if (CollUtil.isEmpty(twoPlanItemMap)) {
                    return;
                }
                twoPlanItemMap.forEach((twoNum, twoItemList) -> {
                    if (CollUtil.isEmpty(twoItemList)) {
                        return;
                    }
                    List<PurchasePlanItemDO> threeItemList = twoItemList.stream().filter(s -> Objects.nonNull(s.getThreeSplitNum())).toList();
                    if (CollUtil.isNotEmpty(threeItemList)) {
                        boolean threeNotConvertedFlag = threeItemList.stream().allMatch(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag()));
                        if (threeNotConvertedFlag) {
                            twoItemList.stream().filter(s -> Objects.isNull(s.getThreeSplitNum())).forEach(s -> {
                                s.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                                s.setConvertedQuantity(0);
                                updateItemList.add(s);
                                updatePlanIdList.add(planId);
                            });
                        }
                    }
                });
                List<PurchasePlanItemDO> twoItemList = v.stream().filter(s -> Objects.nonNull(s.getTwoSplitNum()) && Objects.isNull(s.getThreeSplitNum())).toList();
                if (CollUtil.isNotEmpty(twoItemList)) {
                    boolean twoNotConvertedFlag = twoItemList.stream().allMatch(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag()));
                    if (twoNotConvertedFlag) {
                        v.stream().filter(s -> Objects.isNull(s.getTwoSplitNum())).forEach(s -> {
                            s.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                            s.setConvertedQuantity(0);
                            updateItemList.add(s);
                            updatePlanIdList.add(planId);
                        });
                    }
                }
                List<PurchasePlanItemDO> oneItemList = v.stream().filter(s -> Objects.isNull(s.getTwoSplitNum()) && Objects.isNull(s.getThreeSplitNum())).toList();
                if (CollUtil.isNotEmpty(oneItemList)) {
                    boolean oneNotConvertedFlag = oneItemList.stream().anyMatch(s -> s.getConvertedFlag().equals(ConvertedFlagEnum.NOT_CONVERTED.getStatus()));
                    // 只要有任意一个一级产品未转则计划为待采购
                    if (oneNotConvertedFlag) {
                        updatePlanIdList.add(planId);
                    }
                }
            });
        });
        if (CollUtil.isNotEmpty(updateItemList)) {
            purchasePlanItemMapper.updateBatch(updateItemList);
        }
        if (CollUtil.isNotEmpty(updatePlanIdList)) {
            List<PurchasePlanDO> updatePlanList = updatePlanIdList.stream().distinct().map(s -> new PurchasePlanDO().setId(s).setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode())).toList();
            purchasePlanMapper.updateBatch(updatePlanList);
        }
    }
    @Override
    public void rollBackFinishPurchaseContract(List<Long> contractIdList) {
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectBatchIds(contractIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return;
        }
        List<OrderLinkDTO> orderLinkDTOList = purchaseContractDOS.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, x, null, s.getPurchasePlanCode(), PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode(), null)).toList();
            }
            return null;
        }).filter(Objects::nonNull).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode());
        purchaseContractMapper.update(
                new PurchaseContractDO().setContractStatus(PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode())
                        .setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult()),
                new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getId, contractIdList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void placeOrderPurchaseContract(List<Long> contractIdList) {
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getId, contractIdList);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return;
        }
        for (PurchaseContractDO purchaseContractDO : purchaseContractDOList) {
            if (Objects.equals(purchaseContractDO.getContractStatus(), PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode())) {
                throw exception(PURCHASE_ALL_READY_ORDER);
            }
        }
        List<OrderLinkDTO> orderLinkDTOList = purchaseContractDOList.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, x, null, s.getPurchasePlanCode(), PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode(), null)).toList();
            }
            return null;
        }).filter(Objects::nonNull).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode());
        purchaseContractMapper.update(
                new PurchaseContractDO().setPlaceOrderFlag(BooleanEnum.YES.getValue()).setOrderTime(LocalDateTime.now()),
                new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getId, contractIdList));

        // 采购合同下单，入在制库
        purchaseContractDOList.forEach(x -> {
            if (x.getAuxiliaryFlag() == 0) {
                this.handleWmsBillAndStock(x.getId());
                // 补充操作日志明细
                dealOperateLog("【采购合同下单】", x.getCode());
            }
        });
    }

    /**
     * 采购合同下单-入库单、入库单-明细及入库库存逻辑
     *
     * @param purchaseContractId 采购合同主键
     * @return
     */
    private boolean handleWmsBillAndStock(Long purchaseContractId) {
        PurchaseContractInfoRespVO contractInfoRespVO = this.getPurchaseContractContainRelations(new PurchaseContractDetailReq().setPurchaseContractId(purchaseContractId));
        BillSaveReqVO billSaveReqVO = BeanUtils.toBean(contractInfoRespVO, BillSaveReqVO.class);

        // 默认供应商仓库
        WarehouseDTO warehouse = warehouseApi.getWarehouse(contractInfoRespVO.getVenderCode(), "");
        if (Objects.isNull(warehouse)) {
            throw new ServiceException(PURCHASE_CONTRACT_WAREHOUSE_NULL);
        }
        billSaveReqVO.setWarehouseId(warehouse.getId());
        billSaveReqVO.setWarehouseName(warehouse.getName());

        // 补齐入库单-子表信息
        List<PurchaseContractItemInfoRespVO> contractItemRespList = contractInfoRespVO.getChildren();
        // 合并相同产品的库存明细的数量
        List<PurchaseContractItemInfoRespVO> contractItemList = contractItemRespList.stream()
                .collect(Collectors.toMap(PurchaseContractItemInfoRespVO::getSkuId, x -> x,
                        (x1, x2) -> {
                            x1.setQuantity(x1.getQuantity() + x2.getQuantity());
                            return x1;
                        })).values().stream().toList();
        List<BillItemSaveReqVO> billItemSaveReqVOList = PurchaseContractConvert.INSTANCE.convertBillItemSaveReqVOList(contractItemList);
        Map<Long, PurchaseContractItemInfoRespVO> contractItemMap = contractItemList.stream().collect(Collectors.toMap(PurchaseContractItemInfoRespVO::getSkuId, Function.identity()));
        List<Long> venderIdList = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getVenderId).toList();
        Map<Long, SimpleVenderRespDTO> simpleVenderRespDTOMap = venderApi.getSimpleVenderRespDTOMap(venderIdList);
        List<Long> skuIdList = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getSkuId).toList();
        Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(skuIdList);
        List<Long> custIdList = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getCustId).toList();
        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(custIdList);
        for (int i = 0; i < billItemSaveReqVOList.size(); i++) {
            BillItemSaveReqVO billItem = billItemSaveReqVOList.get(i);
            billItem.setSortNum(i + 1).setPosition("默认").setRemark("");
            Long skuId = billItem.getSkuId();
            SkuDTO skuDTO = skuDTOMap.get(skuId);
            if (Objects.isNull(skuDTO)) {
                throw exception(SKU_NOT_EXISTS);
            }
            billItem.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
            billItem.setSkuName(skuDTO.getName()).setOwnBrandFlag(skuDTO.getOwnBrandFlag()).setCustProFlag(skuDTO.getCustProFlag());
            billItem.setWarehouseId(billSaveReqVO.getWarehouseId()).setWarehouseName(billSaveReqVO.getWarehouseName());
            billItem.setCompanyId(billSaveReqVO.getCompanyId()).setCompanyName(billSaveReqVO.getCompanyName());
            SimpleVenderRespDTO simpleVenderRespDTO = simpleVenderRespDTOMap.get(billItem.getVenderId());
            billItem.setVenderCode(simpleVenderRespDTO.getCode()).setVenderName(simpleVenderRespDTO.getName());
            SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(billItem.getCustId());
            if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                billItem.setCustCode(simpleCustRespDTO.getCode()).setCustName(simpleCustRespDTO.getName());
            }
            PurchaseContractItemInfoRespVO contractItem = contractItemMap.get(skuId);
            billItem.setOrderQuantity(contractItem.getQuantity()).setOrderBoxQuantity(contractItem.getBoxCount());
            billItem.setActQuantity(contractItem.getQuantity()).setActBoxQuantity(contractItem.getBoxCount());
            billItem.setTotalVolume(contractItem.getOuterboxVolume()).setTotalWeight(contractItem.getOuterboxGrossweight());

            billItem.setPurchaseContractId(contractInfoRespVO.getId()).setPurchaseContractCode(contractInfoRespVO.getCode());
            billItem.setPurchaserId(contractInfoRespVO.getPurchaseUserId()).setPurchaserDeptId(contractInfoRespVO.getPurchaseUserDeptId());
            billItem.setSales(contractInfoRespVO.getSales());
        }
        billSaveReqVO.setBillItemSaveReqVOList(billItemSaveReqVOList);
        billSaveReqVO.setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
        billSaveReqVO.setBillType(StockTypeEnum.IN_STOCK.getValue());
        return stockApi.afterCreatePurchaseOrder(billSaveReqVO);
    }

    @Override
    public boolean completeOrderTask(CompleteOrderReqDTO completeOrderReq) {
        Long contractId = completeOrderReq.getContractId();
        Map<String, Integer> usedQuantityMap = completeOrderReq.getUsedQuantityMap();
        // 采购合同-生产完成
        boolean stockFLag = stockApi.completePurchaseOrder(contractId, usedQuantityMap,completeOrderReq.getDomesticSaleFlag(),completeOrderReq.getBatchCodeList());
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(contractId);
        if (Objects.isNull(purchaseContractDO)){
            return true;
        }
        if (Objects.equals(purchaseContractDO.getProduceCompleted(), BooleanEnum.YES.getValue())) {
            if (completeOrderReq.getCheckStatus()){
                throw exception(PURCHASE_CONTRACT_COMPLETED);
            }else {
                return true;
            }
        }

        // 补充操作日志明细
        dealOperateLog("【完成生产】", purchaseContractDO.getCode());
        // 更新外销合同明细中入库状态
        Long saleContractId = purchaseContractDO.getSaleContractId();
        List<PurchaseContractItemDO> purchaseContractItemList = purchaseContractItemMapper.getPurchaseContractItemListByContractId(contractId);

        if (ObjectUtil.isNotNull(saleContractId)) {
            if (!CollectionUtils.isEmpty(purchaseContractItemList)) {
                List<String> saleItemUniqueCodeList = purchaseContractItemList.stream().map(PurchaseContractItemDO::getSaleItemUniqueCode).toList();
                saleContractApi.updateSaleItemBillStatus(saleContractId, saleItemUniqueCodeList, ContractItemBillStatusEnum.VENDER_STORE.getStatus());
            }
        }
        // 更新采购合同明细中入库状态
        if (stockFLag && !CollectionUtils.isEmpty(purchaseContractItemList)) {
            List<String> uniqueCodeList = purchaseContractItemList.stream().map(PurchaseContractItemDO::getUniqueCode).toList();
            purchaseContractApi.updatePurchaseItem(saleContractId, null, uniqueCodeList, ContractItemBillStatusEnum.VENDER_STORE.getStatus(), new HashMap<>(), new HashMap<>());
        }
        boolean complatFlag = true;
        if (stockFLag && validateItemStockStatus(contractId)) {
            purchaseContractDO.setProduceCompleted(BooleanEnum.YES.getValue());
            purchaseContractDO.setDoneTime(completeOrderReq.getDoneTime());
            complatFlag = purchaseContractMapper.updateById(purchaseContractDO) > 0;
        }
        return stockFLag && complatFlag;
    }

    private boolean validateItemStockStatus(Long contractId) {
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractId);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return true;
        }
        return purchaseContractItemDOList.stream().allMatch(s -> Objects.equals(s.getBillStatus(), ContractItemBillStatusEnum.VENDER_STORE.getStatus()));
    }


    @Override
    public void signBackPurchaseContract(PurchaseContractSignBackReqVO contractList) {
        List<SignBackReq> signBackList = contractList.getSignBackList();
        if (CollUtil.isEmpty(signBackList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        signBackList.forEach(s -> {
            purchaseContractMapper.update(
                    new PurchaseContractDO().setSignBackFlag(BooleanEnum.YES.getValue())
                            .setSignBackTime(s.getSignBackDate())
                            .setSignBackAnnexList(s.getSignBackAnnexList())
                            .setSignBackDesc(s.getRemark()),
                    new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getId, s.getContractId()));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void donePurchaseContract(List<Long> contractIdList) {
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectBatchIds(contractIdList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return;
        }
        boolean finished = purchaseContractDOS.stream().anyMatch(s -> PurchaseContractStatusEnum.FINISHED.getCode().equals(s.getContractStatus()));
        if (finished){
            throw exception(PURCHASE_CONTRACT_FINISHED);
        }
        List<OrderLinkDTO> orderLinkDTOList = purchaseContractDOS.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, x, null, s.getPurchasePlanCode(), PurchaseContractStatusEnum.FINISHED.getCode(), null)).toList();
            }
            return null;
        }).filter(Objects::nonNull).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchaseContractStatusEnum.FINISHED.getCode());
        //整单完成
        purchaseContractMapper.update(
                new PurchaseContractDO().setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                        .setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode()),
                new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getId, contractIdList));
    }


    @Override
    public void SetArrivedDatePurchaseContract(PurchaseContractSetArrivedDateReqVO voReq) {
        if (Objects.isNull(voReq) || Objects.isNull(voReq.getId()))
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        if (Objects.isNull(voReq.getDate()))
            throw exception(PURCHASE_CONTRACT_DATE_NULL);
        purchaseContractItemMapper.update(
                PurchaseContractItemDO.builder().planArriveDate(voReq.getDate()).build(),
                new LambdaQueryWrapperX<PurchaseContractItemDO>().eq(PurchaseContractItemDO::getId, voReq.getId()));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        PurchaseContractChange purchaseContractChange = PurchaseContractChange.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            purchaseContractChange.setProcessInstanceId(processInstanceId);
        }
        purchasecontractchangeMapper.updateById(purchaseContractChange);
    }

    //根据内部公司查询可用币种
    private void checkCurrencyByCompany(Long companyId, String currency) {
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXISTS, companyId);
        }
        if (Objects.isNull(companyDTO.getAvailableCurrencyList())) {
            throw exception(COMPANY_NOT_CURRENCY_EXISTS);
        }
        if (!companyDTO.getAvailableCurrencyList().contains(currency)) {
            throw exception(PURCHASE_PLAN_CURRENCY_WRONG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> changePurchaseContract(ChangePurchaseContract updateReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        PurchaseContractInfoRespVO purchaseContract = updateReqVO.getPurchaseContractInfoRespVO();
        PurchaseContractInfoRespVO oldPurchaseContract = updateReqVO.getOld_purchaseContractInfoRespVO();
        // 提交流程标识 默认未提交
        AtomicReference<Integer> submitFlag = new AtomicReference<>(0);
        Set<String> changeFields = new ChangeCompareUtil<PurchaseContractInfoRespVO>().transformObject(oldPurchaseContract, purchaseContract);
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("scm_purchase_contract", "scm_payment_plan", "scm_add_sub_term", "scm_purchase_contract_item"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(SHIPMENT_FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        List<PurchaseContractItemInfoRespVO> oldChildren = oldPurchaseContract.getChildren();
        Map<Long, Integer> oldQuantityMap = oldChildren.stream().collect(Collectors.toMap(PurchaseContractItemInfoRespVO::getId, x -> x.getQuantity()));
        // 如果变更了供应商  需校验是否有付款申请单
        boolean paymentAppExists = paymentAppApi.validateRelationCodeExists(List.of(purchaseContract.getCode()));
        boolean paymentApplyExists = paymentApplyService.validateTransformPaymentApply(purchaseContract.getCode());
        if (!Objects.equals(purchaseContract.getVenderCode(), oldPurchaseContract.getVenderCode())) {
            if (BooleanEnum.YES.getValue().equals(purchaseContract.getAuxiliaryFlag())){
                if (paymentAppExists){
                    throw exception(NOT_CHANGE_VENDER_NAME);
                }
            }else {
                if (paymentApplyExists) {
                    throw exception(PURCHASE_CONTRACT_TRANSFORM_PAYMENT_APPLY);
                }
            }
        }
        // 如果有对公付款不能变更明细数量
        List<PurchaseContractItemInfoRespVO> children = purchaseContract.getChildren();
        children.forEach(s->{
            if (Objects.nonNull(s.getId())){
                Integer quantity = oldQuantityMap.get(s.getId());
                if ((Objects.isNull(quantity)||!Objects.equals(quantity,s.getQuantity()))&&paymentAppExists){
                    throw exception(NOT_CHANGE_PURCHASE_QUANTITY);
                }
            }
        });
        PurchaseContractChange purchaseContractChange = PurchaseContractConvert.INSTANCE.convertPurchaseContractChange(purchaseContract);
        purchaseContractChange.setOldData(oldPurchaseContract);
        // 处理影响范围
        ChangeEffectRespVO changeEffect = getChangeEffect(purchaseContract);
        if (Objects.nonNull(changeEffect)) {
            purchaseContractChange.setEffectRangeList(changeEffect.getEffectRangeList());
        }
        String code = codeGeneratorApi.getCodeGenerator(CHANGE_SN_TYPE, CHANGE_CODE_PREFIX);
        purchaseContractChange.setSourceCode(purchaseContract.getCode());
        purchaseContractChange.setContractStatus(purchaseContract.getContractStatus());
        purchaseContractChange.setCode(code);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        purchaseContractChange.setCreateUser(adminUserApi.getUserDeptByUserId(loginUserId));
        // 比对变更配置字段
        submitFlag = formChangeApi.dealShipmentTable(changeFields, formChangeDTOList, purchaseContractChange, "scm_purchase_contract", submitFlag, true);
        List<PurchaseContractItemInfoRespVO> purchaseContractItemChangeList = dealChangeField(oldChildren, children, PurchaseContractItemInfoRespVO.class, "scm_purchase_contract_item", purchaseContractChange, formChangeDTOList, submitFlag);
        purchaseContractChange.setChildren(purchaseContractItemChangeList);
        // 验证库存
        dealStock(purchaseContractItemChangeList, oldChildren, purchaseContract.getCode(),true);
        // 付款计划变更
        List<PurchasePaymentPlan> purchasePaymentPlanList = purchaseContract.getPurchasePaymentPlanList();
        List<PurchasePaymentPlan> oldPurchasePaymentPlanList = oldPurchaseContract.getPurchasePaymentPlanList();
        List<PurchasePaymentPlan> purchasePaymentPlanChangeList = dealChangeField(oldPurchasePaymentPlanList, purchasePaymentPlanList, PurchasePaymentPlan.class, "scm_payment_plan", purchaseContractChange, formChangeDTOList, submitFlag);
        purchaseContractChange.setPurchasePaymentPlanList(purchasePaymentPlanChangeList);
        // 加减项变更
        List<PurchaseAddSubTerm> purchaseAddSubTermList = purchaseContract.getPurchaseAddSubTermList();
        List<PurchaseAddSubTerm> oldPurchaseAddSubTermList = oldPurchaseContract.getPurchaseAddSubTermList();
        List<PurchaseAddSubTerm> purchaseAddSubTermChangeList = dealChangeField(oldPurchaseAddSubTermList, purchaseAddSubTermList, PurchaseAddSubTerm.class, "scm_add_sub_term", purchaseContractChange, formChangeDTOList, submitFlag);
        purchaseContractChange.setPurchaseAddSubTermList(purchaseAddSubTermChangeList);
        purchaseContractChange.setId(null);

        //更新总计数量和金额
        if (CollUtil.isNotEmpty(children)) {

            Integer sumQuantity = children.stream().map(PurchaseContractItemInfoRespVO::getQuantity).filter(Objects::nonNull).reduce(0, Integer::sum);
            BigDecimal sumAmount = BigDecimal.valueOf(0);
            String currency = "";
            for (PurchaseContractItemInfoRespVO s : children) {
                if (Objects.nonNull(s.getWithTaxPrice())
                        && Objects.nonNull(s.getQuantity())
                        && Objects.nonNull(s.getWithTaxPrice().getAmount())) {
                    BigDecimal total = s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity()));
                    sumAmount = sumAmount.add(total);
                    if ("".equals(currency)) {
                        currency = s.getWithTaxPrice().getCurrency();
                    }
                }
            }
            purchaseContractChange.setTotalAmount(new JsonAmount().setAmount(sumAmount).setCurrency(currency));
            purchaseContractChange.setTotalQuantity(sumQuantity);
        }
        // 入库
        purchaseContractChange.setAuxiliaryFlag(updateReqVO.getAuxiliaryFlag());
        purchasecontractchangeMapper.insert(purchaseContractChange);
        responses.add(new CreatedResponse(purchaseContractChange.getId(), purchaseContractChange.getCode()));
        // 记录操作日志
        OperateLogUtils.setContent(String.format("创建采购合同变更记录【%s】", code));
        OperateLogUtils.addExt(CHANGE_OPERATOR_EXTS_KEY, purchaseContractChange.getCode());
        OperateLogUtils.addExt(CHANGE_OPERATOR_EXTS_KEY, purchaseContractChange.getSourceCode());
        PurchaseContractChange updateObj = new PurchaseContractChange();
        updateObj.setId(purchaseContractChange.getId());
        // 如果都是普通级直接进入变更审批
        if (Objects.isNull(submitFlag) || SubmitFlagEnum.ONLY_SAVE.getStatus().equals(submitFlag.get())||BooleanEnum.YES.getValue().equals(purchaseContract.getAutoFlag())) {
            updateObj.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            updateEffectRanageStatus(purchaseContractChange.getId());
            return responses;
        }
        purchaseContractChange.setModelKey(getChangeProcessDefinitionKey());
        // 进入配置流程审批
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(submitFlag.get())&&!BooleanEnum.YES.getValue().equals(purchaseContract.getAutoFlag())) {

            List<PurchaseContractItemInfoRespVO> purchaseContractItems = purchaseContractChange.getChildren();
            // 是否库存采购
            boolean stockPurchaseFlag = purchaseContractItems.stream().anyMatch(s -> StrUtil.isNotEmpty(s.getSaleContractCode())||Objects.nonNull(s.getSaleContractItemId())||StrUtil.isNotEmpty(s.getSaleItemUniqueCode()));
            Map<String, Object> variable = new HashMap<>();
            String processInstanceId;
            if (stockPurchaseFlag) {
                variable.put("stockPurchaseFlag", 0);
                processInstanceId =submitChangeTask(purchaseContractChange.getId(), WebFrameworkUtils.getLoginUserId(), getChangeProcessDefinitionKey(), variable);
            } else {
                variable.put("stockPurchaseFlag", 1);
                processInstanceId = submitChangeTask(purchaseContractChange.getId(), WebFrameworkUtils.getLoginUserId(), getChangeProcessDefinitionKey(), variable);
            }
            purchaseContractChange.setProcessInstanceId(processInstanceId);
            purchaseContractChange.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            purchasecontractchangeMapper.updateById(purchaseContractChange);
            // 更改出运单变更状态
            purchaseContractMapper.updateById(new PurchaseContractDO().setId(purchaseContract.getId()).setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus()));
        }
        purchasecontractchangeMapper.updateById(updateObj);
        return responses;
    }

    private void dealStock(List<PurchaseContractItemInfoRespVO> purchaseContractItemChangeList, List<PurchaseContractItemInfoRespVO> oldChildren, String purchaseContractCode,boolean validateFlag) {
        Map<String, Integer> oldQuantityMap = oldChildren.stream().collect(Collectors.toMap(PurchaseContractItemInfoRespVO::getSkuCode, PurchaseContractItemInfoRespVO::getQuantity, Integer::sum));
        Map<String, Integer> updateQuantityMap = new HashMap<>();
        Map<String, BaseSkuEntity> updateMap = new HashMap<>();
        purchaseContractItemChangeList.stream().filter(s -> ChangeTypeEnum.UPDATE.getType().equals(s.getChangeFlag()))
                .collect(Collectors.toMap(PurchaseContractItemInfoRespVO::getSkuCode, PurchaseContractItemInfoRespVO::getQuantity, Integer::sum))
                .forEach((k, v) -> {
                    Integer oldQuantity = oldQuantityMap.get(k);
                    if (Objects.isNull(oldQuantity) || v == oldQuantity) {
                        return;
                    }
                    updateQuantityMap.put(k, v - oldQuantity);
                });
        purchaseContractItemChangeList.stream().filter(s -> ChangeTypeEnum.UPDATE.getType().equals(s.getChangeFlag())).forEach(s->{
            BaseSkuEntity baseSkuEntity = new BaseSkuEntity();
            baseSkuEntity.setQtyPerOuterbox(s.getQtyPerOuterbox());
            baseSkuEntity.setWithTaxPrice(s.getWithTaxPrice());
            baseSkuEntity.setSpecificationList(s.getSpecificationList());
            baseSkuEntity.setQuantity(updateQuantityMap.get(s.getSkuCode()));
            updateMap.put(s.getSkuCode(), baseSkuEntity);
        });
        if (validateFlag){
            stockApi.validateStock(purchaseContractCode, updateMap);
        }else {
            boolean stockFlag = stockApi.updateStockByPurchaseContractCode(purchaseContractCode, updateMap);
            if (stockFlag){
                purchaseContractMapper.update(new PurchaseContractDO().setProduceCompleted(BooleanEnum.NO.getValue()),new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getCode, purchaseContractCode));
            }
        }
    }

    @Override
    public String submitChangeTask(Long id, Long userId, String modelKey,Map<String, Object> variable) {
        return bpmProcessInstanceApi.createProcessInstance(userId, modelKey, id,variable,Map.of());
    }

    @Override
    public PurchaseContractChange getPurchaseContractContainChangeRelations(PurchaseContractDetailReq purchaseContractDetailReq) {
        Long purchaseContractId = purchaseContractDetailReq.getPurchaseContractId();
        PurchaseContractChange purchaseContractChange;
        if (Objects.nonNull(purchaseContractId)) {
            purchaseContractChange = purchasecontractchangeMapper.selectById(purchaseContractDetailReq.getPurchaseContractId());
        } else {
            purchaseContractChange = purchasecontractchangeMapper.selectOne(PurchaseContractChange::getProcessInstanceId, purchaseContractDetailReq.getProcessInstanceId());
        }
        return purchaseContractChange;
    }

    @Override
    public void updateChangeResult(Long changeId) {
        PurchaseContractChange purchaseContractChange = purchasecontractchangeMapper.selectById(changeId);
        if (Objects.isNull(purchaseContractChange)) {
            throw exception(PURCHASE_CONTRACT_CHANGE_NOT_EXISTS);
        }
        List<PurchaseContractItemInfoRespVO> children = purchaseContractChange.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            List<PurchaseContractItemDO> purchaseContractItemDOList = PurchaseContractConvert.INSTANCE.convertPurchaseContractItemDOList(children);
            // 更新采购合同明细
            ItemBaseMapperUtil.dealItemList(purchaseContractItemDOList, purchaseContractItemMapper);
        }
        List<PurchasePaymentPlan> purchasePaymentPlanList = purchaseContractChange.getPurchasePaymentPlanList();
        // 更新付款计划
        if (CollUtil.isNotEmpty(purchasePaymentPlanList)) {
            purchasePaymentPlanList.stream().filter(s->ChangeTypeEnum.ADDED.getType().equals(s.getChangeFlag())).forEach(s -> s.setContractCode(purchaseContractChange.getSourceCode()));
            ItemBaseMapperUtil.dealItemList(purchasePaymentPlanList, purchasePaymentPlanMapper);
        }
        List<PurchaseAddSubTerm> purchaseAddSubTermList = purchaseContractChange.getPurchaseAddSubTermList();
        // 更新加减项
        if (CollUtil.isNotEmpty(purchaseAddSubTermList)) {
            ItemBaseMapperUtil.dealItemList(purchaseAddSubTermList, purchaseAddSubTermMapper);
        }
        PurchaseContractDO purchaseContractDO = PurchaseContractConvert.INSTANCE.convertPurchaseContractDO(purchaseContractChange);
        PurchaseContractDO baseDO = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, purchaseContractDO.getCode());
        purchaseContractDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
        purchaseContractDO.setId(baseDO.getId());
        if (CollUtil.isNotEmpty(purchaseContractChange.getEffectRangeList())) {
            purchaseContractChange.getEffectRangeList().forEach(s -> {
                if (EffectRangeEnum.AUXILIARY_PURCHASE.getValue().equals(s.getEffectRangeType())) {
                    purchaseContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.DMS.getValue().equals(s.getEffectRangeType())) {
                    shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
            });
        }
        PurchaseContractInfoRespVO oldData = purchaseContractChange.getOldData();
        // 处理库存
        dealStock(purchaseContractChange.getChildren(), oldData.getChildren(), oldData.getCode(),false);
        // 计算采购总金额
        List<PurchaseContractItemSaveReqVO> calcChildren = children.stream().filter(s -> !ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(s -> BeanUtils.toBean(s, PurchaseContractItemSaveReqVO.class))
                .collect(Collectors.toList());
        calcTotalAmount(calcChildren,purchaseContractDO);
        // 更新采购合同
        purchaseContractMapper.updateById(purchaseContractDO);
        // 审核通过回写销售合同采购含税价和跟单员
        saleContractApi.updateRealPurchasePrice(children.stream().map(PurchaseContractItemInfoRespVO::getSaleContractItemId).toList(), purchaseContractDO.getManager(), purchaseContractDO.getPurchaseUserId());
        // 审核通过回写出运明细采购含税价和跟单员
        shipmentApi.updateRealPurchasePrice(purchaseContractDO.getSaleContractCode(), purchaseContractDO.getManager());
        // 回写销售合同赠品数量
        Map<Long, Integer> freeQuantityMap = children.stream()
                .filter(item -> BooleanEnum.NO.getValue().equals(item.getSplitFlag())
                        && BooleanEnum.YES.getValue().equals(item.getFreeFlag())
                        && Objects.nonNull(item.getSaleContractItemId()))
                .collect(Collectors.groupingBy(
                        PurchaseContractItemInfoRespVO::getSaleContractItemId,
                        Collectors.summingInt(PurchaseContractItemInfoRespVO::getFreeQuantity)
                ));
        saleContractApi.rewritePurchaseFreeQuantity(freeQuantityMap, false);
    }

    @Override
    public void updateChangePurchaseContract(PurchaseContractChange updateInfoReqVO) {
//        PurchaseContractChange baseItem = purchasecontractchangeMapper.selectById(updateInfoReqVO.getId());
//        if (Objects.isNull(baseItem)) {
//            throw exception(PURCHASE_CONTRACT_CHANGE_NOT_EXISTS);
//        }
//        List<ChangeRecord> changeRecords = new OperateCompareUtil<PurchaseContractChange>().compare(baseItem, updateInfoReqVO);
//        List<PurchaseContractItemChange> old_children = baseItem.getChildren();
//        List<PurchaseContractItemChange> children = updateInfoReqVO.getChildren();
//        List<ChangeRecord> child_changeRecords = DiffUtil.compareListsToChangeRecord(old_children, children, "采购产品", "skuCode");
//        if (CollUtil.isNotEmpty(child_changeRecords)) {
//            changeRecords.addAll(child_changeRecords);
//        }
//        // 插入操作日志
//        OperateLogUtils.addOperateLog(changeRecords, CHANGE_OPERATOR_EXTS_KEY, updateInfoReqVO.getCode());
//        purchasecontractchangeMapper.updateById(updateInfoReqVO);
    }


    @Override
    public PageResult<PurchaseContractItemBoardRespVO> getPurchaseContractBoardPage(PurchaseContractPageReqVO pageReqVO) {
        PageResult<PurchaseContractDO> purchaseContractDOPageResult = purchaseContractMapper.selectPage(pageReqVO);
        List<PurchaseContractDO> purchaseContractList = purchaseContractDOPageResult.getList();
        if (CollUtil.isEmpty(purchaseContractList)) {
            return PageResult.empty();
        }
        List<Long> parentIdList = purchaseContractList.stream().map(PurchaseContractDO::getId).distinct().toList();
        List<PurchaseContractItemRespVO> itemRespVOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractIdList(parentIdList);
        if (CollUtil.isEmpty(itemRespVOList)) {
            return new PageResult<>();
        }
        List<PurchaseContractItemBoardRespVO> boards = BeanUtils.toBean(itemRespVOList, PurchaseContractItemBoardRespVO.class);
        Map<Long, PurchaseContractDO> purchaseContractDOMap = purchaseContractList.stream().collect(Collectors.toMap(PurchaseContractDO::getId, s -> s));

        List<Long> venderIdList = boards.stream().map(PurchaseContractItemBoardRespVO::getVenderId).distinct().toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIdList);
        Map<Long, SimpleVenderRespDTO> venderRespDTOMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, s -> s));

        List<Long> custIdList = boards.stream().map(PurchaseContractItemBoardRespVO::getCustId).distinct().toList();
        List<SimpleCustRespDTO> simpleCustRespDTOList = custApi.getSimpleCustRespDTO(custIdList);
        Map<Long, SimpleCustRespDTO> custMap = simpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, s -> s));

        Map<Long, SimpleCompanyDTO> companyMap = companyApi.getSimpleCompanyDTO();

        List<Long> skuIdList = boards.stream().map(PurchaseContractItemBoardRespVO::getSkuId).distinct().toList();
        Map<Long, SimpleSkuDTO> skuMap = skuApi.getSimpleSkuDTOMap(skuIdList);

        boards.forEach(item -> {
            PurchaseContractDO purchaseContractDO = purchaseContractDOMap.get(item.getPurchaseContractId());
            if (Objects.nonNull(purchaseContractDO)) {
                item.setContractCode(purchaseContractDO.getCode());
                item.setContractVer(purchaseContractDO.getVer());
                item.setOtherCost(purchaseContractDO.getOtherCost());
                item.setDeliveryDate(purchaseContractDO.getDeliveryDate());
                item.setItemCountTotal(purchaseContractDO.getTotalQuantity());
                item.setSignBackFlag(purchaseContractDO.getSignBackFlag());
//                item.setTaxRate(purchaseContractDO.getTaxType());
                item.setPurchaseTime(purchaseContractDO.getPurchaseTime());
                item.setPaymentId(purchaseContractDO.getPaymentId());
                item.setPaymentName(purchaseContractDO.getPaymentName());
//                item.setSyncQuoteFlag(purchaseContractDO.getSyncQuoteFlag());
                item.setPortId(purchaseContractDO.getPortId());
                item.setFreightFlag(purchaseContractDO.getPrintFlag());
                item.setFreight(purchaseContractDO.getFreight());
                item.setRemark(purchaseContractDO.getRemark());
                item.setCreator(purchaseContractDO.getCreator());
                item.setCreatorName(purchaseContractDO.getStockName());
                item.setAnnex(purchaseContractDO.getAnnex());
                item.setCompanyId(purchaseContractDO.getCompanyId());
                item.setCompanyName(purchaseContractDO.getStockName());
                item.setVenderName(purchaseContractDO.getStockName());
                item.setStockName(purchaseContractDO.getStockName());
                item.setPurchasePlanId(purchaseContractDO.getPurchasePlanId());
                item.setPurchasePlanCode(purchaseContractDO.getPurchasePlanCode());
                item.setSaleContractId(purchaseContractDO.getSaleContractId());
                item.setSaleContractCode(purchaseContractDO.getSaleContractCode());
                item.setInvoicedAmount(purchaseContractDO.getInvoicedAmount());
                item.setInvoiceStatus(purchaseContractDO.getInvoiceStatus());
                item.setManager(purchaseContractDO.getManager());
                item.setStockId(purchaseContractDO.getStockId());
                item.setStockCode(purchaseContractDO.getStockCode());
                item.setPrintTimes(purchaseContractDO.getPrintTimes());
                item.setPrintFlag(purchaseContractDO.getPrintFlag());
                item.setPrepayAmount(purchaseContractDO.getPrepayAmount());
                item.setPrepayStatus(purchaseContractDO.getPrepayStatus());
                item.setPayStatus(purchaseContractDO.getPayStatus());
                item.setPayedAmount(purchaseContractDO.getPayedAmount());
                item.setTotalQuantity(purchaseContractDO.getTotalQuantity());
                item.setTotalAmount(purchaseContractDO.getTotalAmount());
                item.setContractStatus(purchaseContractDO.getContractStatus());
                item.setAuditStatus(purchaseContractDO.getAuditStatus());
                item.setPurchaseUserId(purchaseContractDO.getPurchaseUserId());
                item.setPurchaseUserName(purchaseContractDO.getPurchaseUserName());
                item.setPurchaseUserDeptId(purchaseContractDO.getPurchaseUserDeptId());
                item.setPurchaseUserDeptName(purchaseContractDO.getPurchaseUserDeptName());

            }
            if (CollUtil.isNotEmpty(venderRespDTOMap)) {
                SimpleVenderRespDTO simpleVenderRespDTO = venderRespDTOMap.get(item.getVenderId());
                if (Objects.nonNull(simpleVenderRespDTO)) {
                    item.setVenderName(simpleVenderRespDTO.getName());
                }
            }
            if (CollUtil.isNotEmpty(custMap)) {
                SimpleCustRespDTO simpleCustRespDTO = custMap.get(item.getCustId());
                if (Objects.nonNull(simpleCustRespDTO)) {
                    item.setCustName(simpleCustRespDTO.getName());
                }
            }
            if (CollUtil.isNotEmpty(companyMap)) {
                SimpleCompanyDTO simpleCompanyDTO = companyMap.get(item.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)) {
                    item.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
            if (CollUtil.isNotEmpty(skuMap)) {
                SimpleSkuDTO simpleSkuDTO = skuMap.get(item.getSkuId());
                if (Objects.nonNull(simpleSkuDTO)) {
                    item.setSkuName(simpleSkuDTO.getSkuName());
                    item.setMainPicture(simpleSkuDTO.getMainPicture());
                }
            }
        });
        return new PageResult<PurchaseContractItemBoardRespVO>().setList(boards).setTotal((long) boards.size());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getAuxiliaryProcessDefinitionKey() {
        return AUXILIARY_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getProcessDefinitionOwnBrandKey() {
        return OWN_BRAND_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getChangeProcessDefinitionKey() {
        return PROCESS_DEFINITION_CHANGE_KEY;
    }

    @Override
    public PurchaseContractInfoRespVO getPurchaseContract(PurchaseContractDetailReq purchaseContractDetailReq) {


        Long purchaseContractId = Objects.nonNull(purchaseContractDetailReq.getProcessInstanceId()) ?
                bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchaseContractDetailReq.getProcessInstanceId())
                : purchaseContractDetailReq.getPurchaseContractId();
        if (Objects.isNull(purchaseContractId)) {
            logger.error("[采购合同]未获取到采购合同id");
            return null;
        }
        //根据采购单ID 查订单数据
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(purchaseContractId);
        if (Objects.isNull(purchaseContractDO)) {
            return null;
        }
        //订单DO=>VO
        PurchaseContractInfoRespVO purchaseContractInfoRespVO = PurchaseContractConvert.INSTANCE.convertPurchaseContractRespVO(purchaseContractDO);
        if (Objects.nonNull(purchaseContractInfoRespVO.getCustCode())) {
            CustAllDTO custAllDTO = custApi.getCustByCode(purchaseContractInfoRespVO.getCustCode());
            if (Objects.nonNull(custAllDTO)) {
                purchaseContractInfoRespVO.setCustName(custAllDTO.getName());
            }
        }

        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        Long companyId = purchaseContractInfoRespVO.getCompanyId();
        Optional.ofNullable(companyId).
                filter(simpleCompanyDTO::containsKey)
                .map(simpleCompanyDTO::get)
                .map(SimpleCompanyDTO::getName)
                .ifPresent(purchaseContractInfoRespVO::setCompanyName);


        List<Long> parentIdList = new ArrayList<>(List.of(purchaseContractId));
        //根据订单ID 获取订单详情列表数据
        List<PurchaseContractItemRespVO> itemRespVOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractIdList(parentIdList);
        if (CollUtil.isNotEmpty(itemRespVOList)) {
            List<Long> skuIdList = itemRespVOList.stream().map(PurchaseContractItemRespVO::getSkuId).distinct().collect(Collectors.toList());
            List<Long> ASkuIdList = itemRespVOList.stream().map(PurchaseContractItemRespVO::getAuxiliarySkuId).distinct().toList();
            skuIdList.addAll(ASkuIdList);
            Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
//            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            List<Long> saleContractItemIds = itemRespVOList.stream().map(PurchaseContractItemRespVO::getSaleContractItemId).distinct().toList();
            Map<Long, SaleContractItemSaveDTO> saleContractItemMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(saleContractItemIds)) {
                List<SaleContractItemSaveDTO> saleContractItemSaveDTOList = saleContractApi.listSaleContractItem(saleContractItemIds);
                saleContractItemMap = saleContractItemSaveDTOList.stream().collect(Collectors.toMap(SaleContractItemSaveDTO::getId, s -> s, (o, n) -> o));
            }
            Map<String, SmsContractAllDTO> saleContractMap = new HashMap<>();
            List<String> saleContractCodes = itemRespVOList.stream().map(PurchaseContractItemRespVO::getSaleContractCode).distinct().toList();
            if (!CollectionUtils.isEmpty(saleContractCodes)) {
                saleContractMap = saleContractApi.getSmsByCodeList(saleContractCodes);
            }

            Map<Long, SaleContractItemSaveDTO> finalSaleContractItemMap = saleContractItemMap;
            Map<String, SmsContractAllDTO> finalSaleContractMap = saleContractMap;
            
            itemRespVOList.forEach(i -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        i.setMainPicture(simpleSkuDTO.getMainPicture());
//                        i.setSkuName(simpleSkuDTO.getSkuName());
                        Map<String, String> nameCache = custApi.getCustNameCache(i.getCustCode());
                        if (CollUtil.isNotEmpty(nameCache)) {
                            i.setCustName(nameCache.get(i.getCustCode()));
                        }
                    }
                    SimpleSkuDTO skuDTO = simpleSkuDTOMap.get(i.getAuxiliarySkuId());
                    if (Objects.nonNull(skuDTO)) {
                        i.setAuxiliarySkuName(skuDTO.getSkuName());
                    }
                    if (Objects.nonNull(finalSaleContractItemMap) && Objects.nonNull(i.getSaleContractItemId())) {
                        SaleContractItemSaveDTO saleContractItemSaveDTO = finalSaleContractItemMap.get(i.getSaleContractItemId());
                        if (Objects.nonNull(saleContractItemSaveDTO)) {
                            i.setSaleContractItemSaveDTO(saleContractItemSaveDTO);
                        }
                    }
                    if (Objects.nonNull(finalSaleContractMap) && Objects.nonNull(i.getSaleContractCode())) {
                        SmsContractAllDTO smsContractAllDTO = finalSaleContractMap.get(i.getSaleContractCode());
                        if (Objects.nonNull(smsContractAllDTO)) {
                            i.setCurrencySaleContract(smsContractAllDTO.getCurrency());
                        }
                    }
                }

            });

            //订单详情列表绑定VO
            Map<String, Integer> noticedQuantityMap = stockApi.getNoticedQuantityMap(purchaseContractInfoRespVO.getCode());
            if (CollUtil.isNotEmpty(itemRespVOList) && CollUtil.isNotEmpty(noticedQuantityMap)) {
                itemRespVOList.forEach(s -> {
                    s.setNoticedQuantity(Objects.isNull(noticedQuantityMap.get(s.getUniqueCode())) ? 0 : noticedQuantityMap.get(s.getUniqueCode()));
                });
            }
            long checkAcount = itemRespVOList.stream().filter(s -> Objects.nonNull(s.getCheckStatus()) && InspectionItemStatusEnum.getValues().contains(s.getCheckStatus())).count();
            if (checkAcount >= itemRespVOList.size()) {
                purchaseContractInfoRespVO.setCheckStatus(PurchaseCheckEnum.CHECKED.getStatus());
            } else if (checkAcount == 0) {
                purchaseContractInfoRespVO.setCheckStatus(PurchaseCheckEnum.NOT_CHECK.getStatus());
            } else {
                purchaseContractInfoRespVO.setCheckStatus(PurchaseCheckEnum.PART_CHECK.getStatus());
            }
            purchaseContractInfoRespVO.setChildren(BeanUtils.toBean(itemRespVOList, PurchaseContractItemInfoRespVO.class));


            //设置汇总信息
            purchaseContractInfoRespVO.setSkuTypeCount(itemRespVOList.stream().map(PurchaseContractItemRespVO::getSkuCode).distinct().count());
            purchaseContractInfoRespVO.setSkuCount(itemRespVOList.stream().mapToInt(PurchaseContractItemRespVO::getQuantity).sum());
            List<JsonAmount> resultAmountList = new ArrayList<>();
            if (CollUtil.isNotEmpty(itemRespVOList)) {
                for (PurchaseContractItemRespVO s : itemRespVOList) {
                    if (Objects.nonNull(s.getWithTaxPrice()) && Objects.nonNull(s.getWithTaxPrice().getAmount())
                            && Objects.nonNull(s.getWithTaxPrice().getCurrency())
                            && Objects.nonNull(s.getQuantity())) {
                        Optional<JsonAmount> first = resultAmountList.stream().filter(f -> Objects.equals(f.getCurrency(), s.getWithTaxPrice().getCurrency())).findFirst();
                        first.ifPresent(jsonAmount -> jsonAmount.getAmount().add(s.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(s.getQuantity()))));
                    }
                }
            }
            purchaseContractInfoRespVO.setSummary(resultAmountList);
        }

        //采购单如果是辅料采购的产品相关,传参包含的采购产品
        if (purchaseContractInfoRespVO.getAuxiliaryFlag() == 1) {
            List<PurchaseContractItemInfoRespVO> children = purchaseContractInfoRespVO.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                List<String> contractCodeList = children.stream().map(PurchaseContractItemInfoRespVO::getPurchaseContractCode).distinct().toList();
                List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, contractCodeList);
                List<Long> skuIdList = itemDOList.stream().map(PurchaseContractItemDO::getSkuId).distinct().toList();
                Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
                if (CollUtil.isNotEmpty(itemDOList)) {
                    Map<String, List<PurchaseContractItemDO>> itemMap = itemDOList.stream().filter(s -> s.getAuxiliaryPurchaseContractCode() != null).collect(Collectors.groupingBy(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode));
                    children.forEach(s -> {
                        if (Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.PRODUCT.getCode())) {
                            List<SimpleData> skuList = new ArrayList<>();
                            if (Objects.nonNull(s.getAuxiliaryPurchaseContractCode())) {
                                List<PurchaseContractItemDO> itemDOS = itemMap.get(s.getAuxiliaryPurchaseContractCode());
                                if (CollUtil.isNotEmpty(itemDOS)) {
                                    itemDOS.forEach(i -> {
                                        SimpleData simpleData = new SimpleData().setId(i.getSkuId()).setCode(i.getSkuCode());
                                        if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                                            SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                                            if (Objects.nonNull(simpleSkuDTO)) {
                                                simpleData.setName(simpleSkuDTO.getSkuName());
                                            }
                                        }
                                        skuList.add(simpleData);
                                    });
                                }
                            }
                            s.setAuxiliarySkuList(skuList);
                        }
                    });
                }
            }
        }

        List<Long> skuIdList = purchaseContractInfoRespVO.getChildren().stream().map(PurchaseContractItemInfoRespVO::getSkuId).toList();
        Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
        purchaseContractInfoRespVO.getChildren().forEach(s -> {
//            s.setMeasureUnit(s.getSkuUnit());
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                Boolean aBoolean = simpleSkuDTOMap.get(s.getSkuId());
                if (Objects.isNull(aBoolean)) {
                    aBoolean = false;
                }
                s.setSkuDeletedFlag(aBoolean ? 0 : 1);
            }
        });
        // 获得操作日志信息
        purchaseContractInfoRespVO.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, purchaseContractDO.getCode()));

        return purchaseContractInfoRespVO;
    }

    @Override
    public PurchaseContractInfoRespVO getPurchaseContractContainRelations(PurchaseContractDetailReq purchaseContractDetailReq) {
        PurchaseContractInfoRespVO purchaseContractInfoRespVO = getPurchaseContract(purchaseContractDetailReq);
        if (Objects.isNull(purchaseContractInfoRespVO)) {
            return null;
        }

        //设置采购计划信息
        Long purchasePlanId = purchaseContractInfoRespVO.getPurchasePlanId();
        if (Objects.nonNull(purchasePlanId)) {
            List<PurchasePlanItemAndPlanRespVO> purchasePlanItem = purchasePlanService.getPurchasePlanItem(purchasePlanId);
            purchaseContractInfoRespVO.setPurchasePlanList(purchasePlanItem);
        }

        String contractCode = purchaseContractInfoRespVO.getCode();

        //设置变更单
        ChangePurchasePageReq changePurchasePageReq = new ChangePurchasePageReq();
        changePurchasePageReq.setContractCode(contractCode);
        changePurchasePageReq.setPageSize(ChangePurchasePageReq.PAGE_SIZE_NONE);

        //设置验货单
        purchaseContractInfoRespVO.setCheckList(null);
        // 供应商付款方式列表
        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(VenderPayment::getVenderId, purchaseContractInfoRespVO.getVenderId());
        if (CollUtil.isNotEmpty(venderPaymentList)) {
            Map<Long, Integer> venderPaymentMap = venderPaymentList.stream().collect(Collectors.toMap(VenderPayment::getPaymentId, VenderPayment::getDefaultFlag, (o, n) -> o));
            List<PaymentItemDTO> paymentItemDTOList = paymentItemApi.getPaymentDTOList(new ArrayList<>(venderPaymentMap.keySet()));
            if (CollUtil.isNotEmpty(paymentItemDTOList)) {
                paymentItemDTOList.forEach(s -> {
                    s.setDefaultFlag(venderPaymentMap.get(s.getId()));
                });
            }
            purchaseContractInfoRespVO.setPaymentList(paymentItemDTOList);
        }

        // 付款计划
        List<PurchasePaymentPlan> purchasePaymentPlans = dealPurchasePaymentPlan(contractCode);
        if (CollUtil.isNotEmpty(purchasePaymentPlans)){
            purchaseContractInfoRespVO.setPurchasePaymentPlanList(purchasePaymentPlans);
            purchasePaymentPlans.stream().map(PurchasePaymentPlan::getReceivableAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull)
                    .reduce(BigDecimal::add).map(s->new JsonAmount().setAmount(s).setCurrency(purchaseContractInfoRespVO.getCurrency()))
                    .ifPresent(purchaseContractInfoRespVO::setPaymentAmount);
            BigDecimal payedAmount = Objects.isNull(purchaseContractInfoRespVO.getPayedAmount()) ? BigDecimal.ZERO : Objects.isNull(purchaseContractInfoRespVO.getPayedAmount().getAmount()) ? BigDecimal.ZERO : purchaseContractInfoRespVO.getPayedAmount().getAmount();
            BigDecimal unPaidAmount = purchaseContractInfoRespVO.getPaymentAmount().getAmount().subtract(payedAmount);
            purchaseContractInfoRespVO.setUnPaidAmount(new JsonAmount().setAmount(unPaidAmount).setCurrency(purchaseContractInfoRespVO.getCurrency()));
        }

        // 加减项
        List<PurchaseAddSubTerm> purchaseAddSubTerms = purchaseAddSubTermMapper.selectList(PurchaseAddSubTerm::getContractCode, contractCode);
        purchaseContractInfoRespVO.setPurchaseAddSubTermList(purchaseAddSubTerms);

        //辅料分摊列表
        purchaseContractInfoRespVO.setAllocationList(getAuxiliaryPurchaseAllocations(purchaseContractInfoRespVO.getCode()));

        //应付供应商
        if (purchaseContractInfoRespVO.getVenderId() != null) {
            SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderRespDTOById(purchaseContractInfoRespVO.getVenderId());
            purchaseContractInfoRespVO.setVenderlink(simpleVenderRespDTO.getVenderLink());
        }
        // 包材采购附件
        List<SimpleFile> auxiliaryAnnexByContractCode = purchasePlanItemService.getAuxiliaryAnnexByContractCode(purchaseContractInfoRespVO.getCode());
        if (CollUtil.isNotEmpty(auxiliaryAnnexByContractCode)) {
            purchaseContractInfoRespVO.setAuxiliaryAnnex(auxiliaryAnnexByContractCode);
        }
        //费用归集
        List<FeeShareRespDTO> feeShareDTO = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.AUXILIARY_PURCHASE, purchaseContractInfoRespVO.getCode());
        purchaseContractInfoRespVO.setFeeShare(feeShareDTO);

        if (purchaseContractInfoRespVO.getCreator() != null) {
            Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
            if (!userDeptCache.isEmpty()) {
                UserDept userDept = userDeptCache.get(Long.valueOf(purchaseContractInfoRespVO.getCreator()));
                if (userDept != null) {
                    purchaseContractInfoRespVO.setCreatorName(userDept.getNickname());
                    purchaseContractInfoRespVO.setCreatorDeptName(userDept.getDeptName());
                }
            }
        }

        return purchaseContractInfoRespVO;
    }

    private List<PurchasePaymentPlan> dealPurchasePaymentPlan(String contractCode) {
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, contractCode);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return purchasePaymentPlans;
        }
        List<Long> planIds = purchasePaymentPlans.stream().map(PurchasePaymentPlan::getId).distinct().toList();
        //查询当前合同的付款申请单
        List<PaymentApplyDO> paymentApplyDOS = paymentApplyService.getListByPurchaseCode(contractCode);
        //查询付款申请单对应的付款单
        List<String> applyCodes = paymentApplyDOS.stream().map(PaymentApplyDO::getCode).distinct().toList();
        Map<String, PaymentDTO> paymentDTOMap = paymentApi.getListByCodes(applyCodes);

        if (CollUtil.isEmpty(paymentApplyDOS) || CollUtil.isEmpty(paymentDTOMap)) {
            return purchasePaymentPlans;
        }

        List<PurchasePaymentItem> itemList = new ArrayList<>();
        paymentApplyDOS.forEach(s -> { //循环付款申请单
            s.getApplyPaymentPlanList().forEach(applyPaymentPlan -> {  //循环付款申请单的付款计划信息
                if (!planIds.contains(applyPaymentPlan.getId())) {
                    return;    //申请单中别的合同的付款不处理
                }
                PaymentDTO paymentDTO = paymentDTOMap.get(s.getCode());
                if (Objects.isNull(paymentDTO)) {
                    return; //没有付款单不处理
                }
                if (Objects.equals(paymentDTO.getStatus(), BooleanEnum.NO.getValue())) {
                    return;//没有完成付款的付款单不处理
                }
                PurchasePaymentItem item = new PurchasePaymentItem();
                item.setPaymentPlanId(applyPaymentPlan.getId());
                item.setPaymentDate(paymentDTO.getDate());
                item.setPaymentAmount(new JsonAmount().setAmount(applyPaymentPlan.getApplyAmount()).setCurrency(s.getCurrency()));
                item.setPaymentUser(paymentDTO.getCashier());
                itemList.add(item);
            });
        });
        if (CollUtil.isNotEmpty(itemList)) {
            Map<Long, List<PurchasePaymentItem>> itemMap = itemList.stream().collect(Collectors.groupingBy(PurchasePaymentItem::getPaymentPlanId));
            BigDecimal innerRate = BigDecimal.ZERO;
            String innerRateValue = configApi.getValueByConfigKey("company.inner.rate");
            try {
                innerRate = new BigDecimal(innerRateValue);
            } catch (Exception ignored) {
            }
            BigDecimal finalInnerRate = innerRate;
            purchasePaymentPlans.forEach(s -> {
                List<PurchasePaymentItem> purchasePaymentItems = itemMap.get(s.getId());
                if (CollUtil.isEmpty(purchasePaymentItems)) {
                    return;
                }
                purchasePaymentItems.forEach(item -> {
                    item.setStartDate(s.getStartDate());
                    if (Objects.nonNull(item.getStartDate()) && Objects.nonNull(item.getPaymentDate())) {
                        item.setOccupationDays(ChronoUnit.DAYS.between(item.getStartDate(), item.getPaymentDate()));
                    } else {
                        item.setOccupationDays(0L);
                    }
                    item.setOccupationInterest(item.getPaymentAmount().getAmount().multiply(finalInnerRate).multiply(BigDecimal.valueOf(item.getOccupationDays())));
                });
                s.setItemList(purchasePaymentItems);
            });
        }
        return purchasePaymentPlans;
    }


    private List<AuxiliaryPurchaseContractDTO> getAuxiliaryPurchaseAllocations(String code) {
        //获取辅料采购合同中关联该合同的合同明细数据
        List<PurchaseContractItemDO> auxiliaryItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .eq(PurchaseContractItemDO::getAuxiliarySkuType, PurchaseAuxiliaryTypeEnum.PRODUCT.getCode())
                .eq(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode, code)
        );
        if (CollUtil.isEmpty(auxiliaryItemDOList)) {
            return null;
        }

        //获取该合同明细
        List<PurchaseContractItemDO> itemList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, code).stream().toList();

        //查询采购合同分摊表中所有关联数据
        List<PurchaseAuxiliaryAllocationDO> allocationDoList = allocationService.getListByPurchaseContractCode(code);

        //获取产品信息
        List<Long> skuIdList = auxiliaryItemDOList.stream().map(PurchaseContractItemDO::getSkuId).distinct().toList();
        Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(skuIdList);
        List<AuxiliaryPurchaseContractDTO> auxiliaryPurchaseContractDTOList = new ArrayList<>();
        auxiliaryItemDOList.forEach(s -> {
            AuxiliaryPurchaseContractDTO dto = new AuxiliaryPurchaseContractDTO();
            //设置采购合同明细数据
            dto.setContractId(s.getPurchaseContractId()).setContractCode(s.getPurchaseContractCode());
            dto.setSkuId(s.getSkuId()).setSkuCode(s.getSkuCode());
            dto.setPurchaseContractItemId(s.getId());
            //设置产品相关信息
            if (CollUtil.isNotEmpty(skuDTOMap)) {
                SkuDTO skuDTO = skuDTOMap.get(s.getSkuId());
                if (Objects.nonNull(skuDTO)) {
                    dto.setSkuName(skuDTO.getName());
                    dto.setUnit(skuDTO.getMeasureUnit());
                    List<SimpleFile> picture = skuDTO.getPicture();
                    if (CollUtil.isNotEmpty(picture)) {
                        Optional<SimpleFile> first = picture.stream().filter(p -> Objects.equals(p.getMainFlag(), BooleanEnum.YES.getValue())).findFirst();
                        first.ifPresent(dto::setMainPicture);
                    }
                }
            }
            dto.setQuantity(s.getQuantity());
            dto.setUnitPrice(s.getUnitPrice()).setWithTaxPrice(s.getWithTaxPrice()).setTotalPrice(s.getTotalPrice());
            dto.setIsAllocation(BooleanEnum.NO.getValue());
            if (CollUtil.isNotEmpty(allocationDoList)) {
                Optional<PurchaseAuxiliaryAllocationDO> first = allocationDoList.stream().filter(a ->
                        Objects.equals(a.getAuxiliaryPurchaseContractItemId(), s.getId())).findFirst();
                if (first.isPresent()) {   //分摊中包含该明细，设置为已分摊，金额为分摊金额
                    dto.setIsAllocation(BooleanEnum.YES.getValue());
                    dto.setAllocationAmount(first.get().getAllocationAmount());
                } else { //分摊中不包含该明细，设置为未分摊
                    dto.setIsAllocation(BooleanEnum.NO.getValue());
                }
            }

            //分摊金额
            if (Objects.isNull(dto.getAllocationAmount()) && Objects.nonNull(dto.getWithTaxPrice())) {
                String currency = dto.getWithTaxPrice().getCurrency();
                BigDecimal amount = dto.getWithTaxPrice().getAmount();
                Integer quantity = dto.getQuantity();
                if (quantity != 0 && !Objects.equals(amount, null) && !Objects.equals(amount, BigDecimal.ZERO)) {
                    dto.setAllocationAmount(new JsonAmount().setCurrency(currency).setAmount(amount.multiply(BigDecimal.valueOf(quantity))));

                }
            }
            auxiliaryPurchaseContractDTOList.add(dto);
        });
        return auxiliaryPurchaseContractDTOList;
    }

    @Override
    public List<PurchaseContractRespVO> getPurchaseContractListByPlanId(Long id) {
        List<PurchaseContractRespVO> result = new ArrayList<>();
        List<Long> idListByPlanId = purchaseContractMapper.getIdListByPlanId(id);
        idListByPlanId.forEach(p -> {
            PurchaseContractInfoRespVO purchaseContract = getPurchaseContract(new PurchaseContractDetailReq().setPurchaseContractId(p));
            if (Objects.nonNull(purchaseContract))
                result.add(purchaseContract);
        });
        return result;
    }

    @Override
    public List<PurchaseContractItemAndContractInfoRespVO> getPurchaseContractItemInfoListByPlanId(List<Long> id) {
        List<PurchaseContractItemAndContractInfoRespVO> result = new ArrayList<>();
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getPurchasePlanId, id);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return null;
        }
        List<Long> parentIdList = purchaseContractDOList.stream().map(PurchaseContractDO::getId).distinct().toList();
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, parentIdList));
        Map<Long, PurchaseContractDO> purchaseContractDOMap = purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getId, s -> s));
        result = PurchaseContractItemConvert.INSTANCE.convert2(purchaseContractDOMap, itemDOList);

        return result;
    }

    @Override
    public PageResult<PurchaseContractInfoRespVO> getPurchaseContractPage(PurchaseContractPageReqVO pageReqVO) {
        PageResult<PurchaseContractInfoRespVO> result = new PageResult<>();
        if (pageReqVO.getConfirmFlag() == null && pageReqVO.getIdList() == null) {
            pageReqVO.setConfirmFlag(1);
        }
        //需求：审批状态包含变更数据
        if (Objects.equals(pageReqVO.getContractStatus(), SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue())) {
            pageReqVO.setConfirmFlag(null);
        }
        if ((pageReqVO.getContractStatusList() == null || pageReqVO.getContractStatusList().length == 0) && pageReqVO.getContractStatus() == null) {
            pageReqVO.setNeStatus(PurchaseContractStatusEnum.CASE_SETTLED.getCode());
        }
        Map<String, Object> summary = new HashMap<>();
        //查询上游单据变更数量
        Long count = purchaseContractMapper.selectCount(new LambdaQueryWrapperX<PurchaseContractDO>()
                .eq(PurchaseContractDO::getConfirmFlag, BooleanEnum.NO.getValue())
                .ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode())
                .eq(PurchaseContractDO::getAutoFlag, BooleanEnum.NO.getValue()));
        summary.put("changeCount", count);

        if (Objects.nonNull(pageReqVO.getVender()) && !pageReqVO.getVender().isEmpty() && Objects.isNull(pageReqVO.getVenderCodeList())) {
            List<VenderDO> venderDOList = venderService.getSimpleListByCodeAndName(pageReqVO.getVender());
            List<String> venderCodeList = venderDOList.stream().map(VenderDO::getCode).distinct().toList();
            pageReqVO.setVenderCodeList(venderCodeList);
        }

        if (Objects.nonNull(pageReqVO.getCustName())) {
            List<SimpleCustResp> simpleCustRespList = custApi.getSimpleCustRespByName(pageReqVO.getCustName());
            if (!simpleCustRespList.isEmpty()) {
                List<Long> custIds = simpleCustRespList.stream().map(SimpleCustResp::getId).distinct().toList();
                pageReqVO.setCustIdList(custIds);
            } else {
                return PageResult.empty();
            }
        }
        
        // 处理优势产品筛选
        if (Objects.nonNull(pageReqVO.getAdvantageFlag())) {
            List<Long> advantageSkuIds = skuApi.getSkuIdsByAdvantageFlag(pageReqVO.getAdvantageFlag());
            if (CollUtil.isEmpty(advantageSkuIds)) {
                return PageResult.empty();
            }
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(advantageSkuIds);
            List<String> advantageSkuCodes = CollUtil.isEmpty(skuDTOMap) ? List.of() : skuDTOMap.values().stream()
                    .map(SkuDTO::getCode)
                    .filter(StrUtil::isNotEmpty)
                    .distinct()
                    .toList();
            if (CollUtil.isEmpty(advantageSkuCodes)) {
                return PageResult.empty();
            }
            // 查询包含优势产品的采购合同ID
            List<Long> contractIds = purchaseContractItemMapper.selectList(
                new LambdaQueryWrapperX<PurchaseContractItemDO>()
                    .select(PurchaseContractItemDO::getPurchaseContractId)
                    .in(PurchaseContractItemDO::getSkuCode, advantageSkuCodes)
            ).stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
            
            if (CollUtil.isEmpty(contractIds)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(contractIds.toArray(new Long[0]));
        }
        
        PageResult<PurchaseContractDO> purchaseContractDOPageResult = purchaseContractMapper.selectPage(pageReqVO);
        List<PurchaseContractDO> purchaseContractList = purchaseContractDOPageResult.getList();
        if (CollUtil.isEmpty(purchaseContractList)) {
            result.setSummary(summary);
            return result;
        }
        List<Long> parentIdList = purchaseContractList.stream().map(PurchaseContractDO::getId).distinct().toList();
        List<String> parentCodeList = purchaseContractList.stream().map(PurchaseContractDO::getCode).distinct().toList();
        List<PurchaseContractItemRespVO> itemRespVOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractIdList(parentIdList);
        if (CollUtil.isEmpty(itemRespVOList)) {
            result.setSummary(summary);
            return result.setList(PurchaseContractConvert.INSTANCE.convertPurchaseContractInfoVOList(purchaseContractList, null, null, null, null, null,null))
                    .setTotal(purchaseContractDOPageResult.getTotal());
        }
        Map<Long, List<PurchaseContractItemRespVO>> purchaseItemMap = itemRespVOList.stream().collect(Collectors.groupingBy(PurchaseContractItemRespVO::getPurchaseContractId));
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, parentCodeList);
        Map<String, List<PurchasePaymentPlan>> purchasePaymentPlanMap = new HashMap<>();
        if (CollUtil.isNotEmpty(purchasePaymentPlans)){
            purchasePaymentPlanMap = purchasePaymentPlans.stream().collect(Collectors.groupingBy(PurchasePaymentPlan::getContractCode));
        }
        Collection<Long> ids = purchaseContractList.stream().map(p ->
        {
            try {
                return Long.parseLong(p.getCreator());
            } catch (Exception exception) {
                logger.error(p.getCreator() + " 不是Integer");
                return null;
            }
        }).collect(Collectors.toList());


        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();

        List<PurchaseContractInfoRespVO> purchaseContractRespVOList = PurchaseContractConvert.INSTANCE.convertPurchaseContractInfoVOList(
                purchaseContractList, adminUserApi.getUserMap(ids), purchaseItemMap,
                simpleCompanyMap, null, null,purchasePaymentPlanMap);
        if (CollUtil.isNotEmpty(purchaseContractRespVOList)) {
            Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);

            List<Long> custIds = purchaseContractRespVOList.stream().map(PurchaseContractInfoRespVO::getCustId).distinct().toList();
            Map<Long, CustDTO> custMap = custApi.getCustByIds(custIds);

            //销售合同主表信息查询，如果后续需要主表的字段，直接使用
            //放开之后删除  custMap的查询和使用  改为custMap获取
//            List<String> saleCodes = purchaseContractRespVOList.stream().map(PurchaseContractInfoRespVO::getSaleContractCode).distinct().toList();
//            Map<String, SmsContractAllDTO>  = saleContractApi.getSmsByCodeList(saleCodes);

            purchaseContractRespVOList.forEach(s -> {
                List<PurchasePaymentPlan> purchasePaymentPlanList = s.getPurchasePaymentPlanList();
                if (CollUtil.isNotEmpty(purchasePaymentPlanList)){
                    purchasePaymentPlanList.stream().map(PurchasePaymentPlan::getReceivableAmount).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal::add).map(amount->new JsonAmount().setAmount(amount).setCurrency(s.getCurrency()))
                            .ifPresent(s::setPaymentAmount);
                    BigDecimal payedAmount = Objects.isNull(s.getPayedAmount()) ? BigDecimal.ZERO : Objects.isNull(s.getPayedAmount().getAmount()) ? BigDecimal.ZERO : s.getPayedAmount().getAmount();
                    BigDecimal paymentAmount = Objects.isNull(s.getPaymentAmount()) ? BigDecimal.ZERO : Objects.isNull(s.getPaymentAmount().getAmount()) ? BigDecimal.ZERO : s.getPaymentAmount().getAmount();
                    BigDecimal unPaidAmount = paymentAmount.equals(0) ? BigDecimal.ZERO : paymentAmount.subtract(payedAmount);
                    s.setUnPaidAmount(new JsonAmount().setAmount(unPaidAmount).setCurrency(s.getCurrency()));
                }
                if(CollUtil.isNotEmpty(custMap)){
                    CustDTO custDTO = custMap.get(s.getCustId());
                    if(Objects.nonNull(custDTO)){
                        s.setCustName(custDTO.getName() );
                    }
                }
                
                // 导出时填充totalAmountRmb：将totalAmount转换为RMB
                if (BooleanEnum.YES.getValue().equals(pageReqVO.getExportFlag()) && Objects.nonNull(s.getTotalAmount())) {
                    JsonAmount totalAmount = s.getTotalAmount();
                    if (Objects.nonNull(totalAmount.getAmount()) && totalAmount.getAmount().compareTo(BigDecimal.ZERO) > 0 
                        && StrUtil.isNotEmpty(totalAmount.getCurrency())) {
                        try {
                            Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
                            JsonAmount totalAmountRmb = CurrencyUtil.changeCurrency(
                                totalAmount,
                                CalculationDict.CURRENCY_RMB,
                                dailyRateMap
                            );
                            s.setTotalAmountRmb(totalAmountRmb);
                        } catch (Exception e) {
                            logger.error("计算采购合同金额RMB失败, 合同编号: {}, 错误: {}", s.getCode(), e.getMessage());
                        }
                    }
                }
                
                List<PurchaseContractItemInfoRespVO> children = s.getChildren();
                if (CollUtil.isEmpty(children)) {
                    return;
                }
                
                // 设置验货状态
                long checkAcount = children.stream().filter(item -> Objects.nonNull(item.getCheckStatus()) && InspectionItemStatusEnum.getValues().contains(item.getCheckStatus())).count();
                if (checkAcount >= children.size()) {
                    s.setCheckStatus(PurchaseCheckEnum.CHECKED.getStatus());
                } else if (checkAcount == 0) {
                    s.setCheckStatus(PurchaseCheckEnum.NOT_CHECK.getStatus());
                } else {
                    s.setCheckStatus(PurchaseCheckEnum.PART_CHECK.getStatus());
                }
                if (!BooleanEnum.YES.getValue().equals(pageReqVO.getExportFlag())){
                    Map<String, Integer> noticedQuantityMap = stockApi.getNoticedQuantityMap(s.getCode());
                    if (CollUtil.isNotEmpty(noticedQuantityMap)) {
                        children.forEach(item -> {
                            item.setNoticedQuantity(Objects.isNull(noticedQuantityMap.get(item.getUniqueCode())) ? 0 : noticedQuantityMap.get(item.getUniqueCode()));
                        });
                        s.setChildren(children);
                    }
                }
                //创建人名称赋值
                if (s.getCreator() != null) {
                    if (!userDeptCache.isEmpty()) {
                        UserDept userDept = userDeptCache.get(Long.valueOf(s.getCreator()));
                        if (userDept != null) {
                            s.setCreatorName(userDept.getNickname());
                            s.setCreatorDeptName(userDept.getDeptName());
                        }
                    }
                }
            });
        }

        LocalDateTime[] createTimeArr = pageReqVO.getCreateTime();
        LocalDateTime createTimeStart = null;
        LocalDateTime createTimeEnd = null;
        if (ObjectUtil.isNotNull(createTimeArr) && createTimeArr.length > 0) {
            createTimeStart = createTimeArr[0];
            createTimeEnd = createTimeArr[1];
        }
        if (!BooleanEnum.YES.getValue().equals(pageReqVO.getExportFlag())){
            List<PurchaseItemSummaryDO> purchaseItemSummary = purchaseContractItemMapper.getPurchaseItemSummary(pageReqVO.getCompanyId(), pageReqVO.getVender(), pageReqVO.getManagerId(), pageReqVO.getCode(), pageReqVO.getCustIdList(), pageReqVO.getCurrency(),
                    pageReqVO.getPaymentId(), pageReqVO.getPurchaseUserId(), pageReqVO.getPurchaseUserDeptId(), pageReqVO.getAutoFlag(), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeStart), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeEnd),
                    pageReqVO.getSkuCode(), pageReqVO.getCskuCode(), pageReqVO.getBasicSkuCode(), pageReqVO.getSalesUserId());
            if (CollUtil.isNotEmpty(purchaseItemSummary)) {
                summary.put("qtySum", purchaseItemSummary.stream().mapToInt(PurchaseItemSummaryDO::getSumQty).sum());
                summary.put("totalSum", purchaseItemSummary.stream().map(item ->
                        new JsonAmount().setCurrency(item.getCurrency()).setAmount(item.getSumTotal().setScale(2, RoundingMode.HALF_UP))));
            }
            List<PurchaseItemAmountSummaryDO> paymentAmountList = purchaseContractItemMapper.getPaymentAmountSummary(pageReqVO.getCompanyId(), pageReqVO.getVender(), pageReqVO.getManagerId(), pageReqVO.getCode(), pageReqVO.getCustIdList(), pageReqVO.getCurrency(),
                    pageReqVO.getPaymentId(), pageReqVO.getPurchaseUserId(), pageReqVO.getPurchaseUserDeptId(), pageReqVO.getAutoFlag(), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeStart), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeEnd),
                    pageReqVO.getSkuCode(), pageReqVO.getCskuCode(), pageReqVO.getBasicSkuCode(), pageReqVO.getSalesUserId());
            List<PurchaseItemAmountSummaryDO> payedAmountList = purchaseContractItemMapper.getPayedAmountSummary(pageReqVO.getCompanyId(), pageReqVO.getVender(), pageReqVO.getManagerId(), pageReqVO.getCode(), pageReqVO.getCustIdList(), pageReqVO.getCurrency(),
                    pageReqVO.getPaymentId(), pageReqVO.getPurchaseUserId(), pageReqVO.getPurchaseUserDeptId(), pageReqVO.getAutoFlag(), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeStart), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeEnd),
                    pageReqVO.getSkuCode(), pageReqVO.getCskuCode(), pageReqVO.getBasicSkuCode(), pageReqVO.getSalesUserId());
            List<PurchaseItemAmountSummaryDO> totalAmountRmbList = purchaseContractItemMapper.getTotalAmountRmbSummary(pageReqVO.getCompanyId(), pageReqVO.getVender(), pageReqVO.getManagerId(), pageReqVO.getCode(), pageReqVO.getCustIdList(), pageReqVO.getCurrency(),
                    pageReqVO.getPaymentId(), pageReqVO.getPurchaseUserId(), pageReqVO.getPurchaseUserDeptId(), pageReqVO.getAutoFlag(), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeStart), ObjectUtil.isNull(createTimeEnd) ? null : new DateTime(createTimeEnd),
                    pageReqVO.getSkuCode(), pageReqVO.getCskuCode(), pageReqVO.getBasicSkuCode(), pageReqVO.getSalesUserId());
            summary.put("paymentAmount",paymentAmountList);
            summary.put("payedAmount",payedAmountList);
            summary.put("totalAmountRmb",totalAmountRmbList);
        }
        return result.setList(purchaseContractRespVOList)
                .setTotal(purchaseContractDOPageResult.getTotal())
                .setSummary(summary);

    }

    @Override
    public PageResult<PurchaseContractProductModeRespVO> getProductModePage(PurchaseContractPageReqVO pageReqVO) {
        // ===================== 第一阶段：初始化与查询条件处理 =====================
        PageResult<PurchaseContractProductModeRespVO> result = new PageResult<>();
        
        // 设置默认筛选条件
        if (pageReqVO.getConfirmFlag() == null && pageReqVO.getIdList() == null) {
            pageReqVO.setConfirmFlag(1);
        }
        
        // 处理供应商条件
        if (!handleVenderCondition(pageReqVO)) {
            return result;
        }
        
        // 处理客户条件
        if (!handleCustCondition(pageReqVO)) {
            return result;
        }
        
        // ===================== 第二阶段：分页查询明细数据 =====================
        Page<PurchaseContractProductModeRespVO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<PurchaseContractProductModeRespVO> pageResult = purchaseContractItemMapper.selectProductModePage(page, pageReqVO);
        
        if (pageResult.getRecords() == null || pageResult.getRecords().isEmpty()) {
            return result;
        }
        
        // ===================== 第三阶段：构建汇总数据 =====================
        Map<String, Object> summary = getProductModeSummary(pageReqVO);
        
        // ===================== 第四阶段：填充额外信息 =====================
        List<PurchaseContractProductModeRespVO> records = pageResult.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            // 填充公司名称
            fillCompanyName(records);
            // 填充创建人信息
            fillCreatorInfo(records);
        }
        
        // ===================== 第五阶段：返回结果 =====================
        result.setList(records);
        result.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        
        return result;
    }

    // ==================== 产品模式共通方法 ====================

    /**
     * 处理优势产品查询条件
     *
     * @param pageReqVO 分页查询请求
     * @return true-继续查询，false-直接返回空结果
     */
    private boolean handleAdvantageFlag(PurchaseContractPageReqVO pageReqVO) {
        if (pageReqVO.getAdvantageFlag() != null) {
            List<Long> advantageSkuIds = skuApi.getSkuIdsByAdvantageFlag(pageReqVO.getAdvantageFlag());
            if (CollUtil.isEmpty(advantageSkuIds)) {
                return false;
            }
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(advantageSkuIds);
            List<String> advantageSkuCodes = CollUtil.isEmpty(skuDTOMap) ? List.of() : skuDTOMap.values().stream()
                    .map(SkuDTO::getCode)
                    .filter(StrUtil::isNotEmpty)
                    .distinct()
                    .toList();
            if (CollUtil.isEmpty(advantageSkuCodes)) {
                return false;
            }
            pageReqVO.setAdvantageSkuCodes(advantageSkuCodes);
        }
        return true;
    }

    /**
     * 处理供应商查询条件
     *
     * @param pageReqVO 分页查询请求
     * @return true-继续查询，false-直接返回空结果
     */
    private boolean handleVenderCondition(PurchaseContractPageReqVO pageReqVO) {
        if (Objects.nonNull(pageReqVO.getVender()) && !pageReqVO.getVender().isEmpty() && Objects.isNull(pageReqVO.getVenderCodeList())) {
            List<VenderDO> venderDOList = venderService.getSimpleListByCodeAndName(pageReqVO.getVender());
            List<String> venderCodeList = venderDOList.stream().map(VenderDO::getCode).distinct().toList();
            pageReqVO.setVenderCodeList(venderCodeList);
        }
        return true;
    }

    /**
     * 处理客户查询条件
     *
     * @param pageReqVO 分页查询请求
     * @return true-继续查询，false-直接返回空结果
     */
    private boolean handleCustCondition(PurchaseContractPageReqVO pageReqVO) {
        if (Objects.nonNull(pageReqVO.getCustName())) {
            List<SimpleCustResp> simpleCustRespList = custApi.getSimpleCustRespByName(pageReqVO.getCustName());
            if (!simpleCustRespList.isEmpty()) {
                List<Long> custIds = simpleCustRespList.stream().map(SimpleCustResp::getId).distinct().toList();
                pageReqVO.setCustIdList(custIds);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取产品模式汇总数据
     *
     * @param pageReqVO 分页查询请求
     * @return 汇总数据Map
     */
    private Map<String, Object> getProductModeSummary(PurchaseContractPageReqVO pageReqVO) {
        Map<String, Object> summary = new HashMap<>();
        
        // 查询上单据变更数量
        Long count = purchaseContractMapper.selectCount(new LambdaQueryWrapperX<PurchaseContractDO>()
                .eq(PurchaseContractDO::getConfirmFlag, BooleanEnum.NO.getValue())
                .ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode())
                .eq(PurchaseContractDO::getAutoFlag, BooleanEnum.NO.getValue()));
        summary.put("changeCount", count);
        
        // 使用与selectProductModePage相同的筛选条件查询汇总
        List<PurchaseContractProductModeSummaryDO> summaryList = purchaseContractItemMapper.selectProductModeSummary(pageReqVO);
        
        if (CollUtil.isNotEmpty(summaryList)) {
            // 数量合计（所有币种的数量加总）
            long totalQuantity = summaryList.stream()
                    .filter(item -> item.getSumQuantity() != null)
                    .mapToLong(PurchaseContractProductModeSummaryDO::getSumQuantity)
                    .sum();
            summary.put("qtySum", totalQuantity);
            
            // 金额合计（按币种分组）
            List<JsonAmount> totalSumList = summaryList.stream()
                    .filter(item -> item.getSumWithTaxTotal() != null && item.getCurrency() != null)
                    .map(item -> new JsonAmount()
                            .setCurrency(item.getCurrency())
                            .setAmount(item.getSumWithTaxTotal().setScale(2, RoundingMode.HALF_UP)))
                    .toList();
            summary.put("totalSum", totalSumList);
            
            // 含税总价人民币合计（所有明细的人民币金额加总）
            BigDecimal totalPriceRmbSum = summaryList.stream()
                    .filter(item -> item.getSumTotalPriceRmb() != null)
                    .map(PurchaseContractProductModeSummaryDO::getSumTotalPriceRmb)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<JsonAmount> totalPriceRmbList = new ArrayList<>();
            totalPriceRmbList.add(new JsonAmount()
                    .setCurrency("RMB")
                    .setAmount(totalPriceRmbSum.setScale(2, RoundingMode.HALF_UP)));
            summary.put("totalPriceRmbSum", totalPriceRmbList);
        }
        
        return summary;
    }

    /**
     * 填充公司名称
     *
     * @param records 记录列表
     */
    private void fillCompanyName(List<PurchaseContractProductModeRespVO> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isEmpty(simpleCompanyMap)) {
            return;
        }
        records.forEach(item -> {
            if (item.getCompanyId() != null) {
                SimpleCompanyDTO companyDTO = simpleCompanyMap.get(item.getCompanyId());
                if (companyDTO != null) {
                    item.setCompanyName(companyDTO.getName());
                }
            }
        });
    }

    /**
     * 填充创建人信息
     *
     * @param records 记录列表
     */
    private void fillCreatorInfo(List<PurchaseContractProductModeRespVO> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        if (userDeptCache.isEmpty()) {
            return;
        }
        records.forEach(item -> {
            if (item.getCreator() != null) {
                try {
                    UserDept userDept = userDeptCache.get(Long.valueOf(item.getCreator()));
                    if (userDept != null) {
                        item.setCreatorName(userDept.getNickname());
                        item.setCreatorDeptName(userDept.getDeptName());
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无法解析创建人ID: {}", item.getCreator());
                }
            }
        });
    }

    @Override
    public PageResult<PurchaseContractChange> getChangePurchaseContractPage(ChangePurchasePageReq pageReqVO) {
        return purchasecontractchangeMapper.selectPage(pageReqVO);
    }


    @Override
    public CreatedResponse toStockNoticeById(Long id) {
        // 0. 校验状态，待到货
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (purchaseContractDO.getContractStatus().intValue() != PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode().intValue()) {
            throw exception(PURCHASE_CONTRACT_NOT_DELIVERY);
        }
        if (purchaseContractDO.getProduceCompleted().intValue() != BooleanEnum.YES.getValue().intValue()) {
            throw exception(PURCHASE_CONTRACT_NOT_COMPLETED);
        }

        StockNoticeReqVO stockNotice = new StockNoticeReqVO();
        // 1. 主表信息
        Long contractDOId = purchaseContractDO.getId();
        stockNotice.setSaleContractId(purchaseContractDO.getSaleContractId());
        stockNotice.setSaleContractCode(purchaseContractDO.getSaleContractCode());
        Long companyId = purchaseContractDO.getCompanyId();
        stockNotice.setCompanyId(companyId);
        String companyName = "";
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (ObjectUtil.isNotNull(companyDTO)) {
            companyName = companyDTO.getCompanyName();
        }
        stockNotice.setCompanyName(companyName);
        // 2. 子表信息
        List<StockNoticeItemReqVO> stockNoticeItemReqVOList = new ArrayList<>();
        LambdaQueryWrapper<PurchaseContractItemDO> queryWrapper = new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, contractDOId);
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(itemDOList)) {
            itemDOList.forEach(x -> {
                StockNoticeItemReqVO stockNoticeItemReqVO = new StockNoticeItemReqVO();
                stockNoticeItemReqVO.setSourceSortNum(x.getSortNum());
                stockNoticeItemReqVO.setSkuId(x.getSkuId());
                stockNoticeItemReqVO.setSkuCode(x.getSkuCode());
                stockNoticeItemReqVO.setVenderId(x.getVenderId());
                stockNoticeItemReqVO.setQtyPerOuterbox(x.getQtyPerOuterbox());
                stockNoticeItemReqVO.setCustId(x.getCustId());
                stockNoticeItemReqVO.setOrderQuantity(x.getQuantity());
                stockNoticeItemReqVO.setOrderBoxQuantity(x.getBoxCount());
                stockNoticeItemReqVO.setSpecificationList(x.getSpecificationList());
                stockNoticeItemReqVO.setPurchaseContractId(contractDOId);
                stockNoticeItemReqVO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
                stockNoticeItemReqVO.setPurchaseContractCode(purchaseContractDO.getCode());
                stockNoticeItemReqVO.setPurchaserId(purchaseContractDO.getPurchaseUserId());
                stockNoticeItemReqVO.setPurchaserDeptId(purchaseContractDO.getPurchaseUserDeptId());
                stockNoticeItemReqVO.setLinkCodeList(purchaseContractDO.getLinkCodeList());
                stockNoticeItemReqVOList.add(stockNoticeItemReqVO);
            });
        }
        stockNotice.setStockNoticeItemReqVOList(stockNoticeItemReqVOList);

        // 3. 转换接口
        return stockNoticeApi.toStockNotice(stockNotice);
    }

    @Override
    public CreatedResponse toStockNotice(StockNoticeReqVO stockNoticeReqVO) {
        // 更新采购合同转入库单标识
//        PurchaseContractDO purchaseContractDO = super.getById(stockNoticeReqVO.getPurchaseContractId());
//        purchaseContractDO.setConvertNoticeFlag(BooleanEnum.YES.getValue());
        // 更新采购合同明细入库标识
        List<StockNoticeItemReqVO> stockNoticeItemReqVOList = stockNoticeReqVO.getStockNoticeItemReqVOList();
        if (CollUtil.isEmpty(stockNoticeItemReqVOList)) {
            return null;
        }
        // key - 采购合同明细唯一编号  value - 本次入库数量
        Map<String, Integer> stockMap = stockNoticeItemReqVOList.stream().collect(Collectors.toMap(StockNoticeItemReqVO::getSourceUniqueCode, StockNoticeItemReqVO::getOrderQuantity));
        if (CollUtil.isEmpty(stockMap)) {
            return null;
        }
        Set<String> purchaseContractItemUniqueCodeList = stockMap.keySet();
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getUniqueCode, purchaseContractItemUniqueCodeList);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        List<Long> purchaseContractIdList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).toList();
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectBatchIds(purchaseContractIdList);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        purchaseContractDOList.stream().forEach(x -> {
            if (x.getProduceCompleted().intValue() != BooleanEnum.YES.getValue().intValue()) {
                throw exception(PURCHASE_CONTRACT_NOT_COMPLETED);
            }
        });
        // 本次入库影响的采购合同id列表 后续批量更新状态
        Map<String, Integer> purchaseContractIdMap = new HashMap<>();
        // 重新计算采购合同明细中入库数量
        purchaseContractItemDOList.forEach(s -> {
            // 采购合同主键
            String purchaseContractCode = s.getPurchaseContractCode();
            // 采购数量
            Integer quantity = s.getQuantity();
            // 已转入库通知单数量
            Integer noticedQuantity = s.getNoticedQuantity();
            // 本次入库数量
            Integer stockNoticeQuantity = stockMap.get(s.getUniqueCode());
            // 已转入库通知单数量 = 已转入库通知单数量 + 本次入库数量
            noticedQuantity = noticedQuantity + stockNoticeQuantity;
            s.setNoticedQuantity(noticedQuantity);
            // 已全部转完
            if (quantity.intValue() <= noticedQuantity.intValue()) {
                s.setWarehousingType(ConvertNoticeFlagEnum.ISSUED.getStatus());
                purchaseContractIdMap.put(purchaseContractCode, ConvertNoticeFlagEnum.ISSUED.getStatus());
            } else {
                s.setWarehousingType(ConvertNoticeFlagEnum.PART_ISSUED.getStatus());
                purchaseContractIdMap.put(purchaseContractCode, ConvertNoticeFlagEnum.PART_ISSUED.getStatus());
            }

            // 补充操作日志明细
            dealOperateLog("【采购合同转入库通知单】,序号：" + s.getSortNum(), s.getPurchaseContractCode());

        });
        // 批量更新采购合同明细
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOList);
        // 更新采购合同转单状态
        updatePurchaseContractNoticeStatus(purchaseContractIdMap);
//        super.updateById(purchaseContractDO);
        // 采购合同转入库通知单
        return stockNoticeApi.toStockNotice(stockNoticeReqVO);
    }

    /**
     * 更新采购合同转入库通知单状态
     *
     * @param purchaseContractIdMap 采购合同主键列表
     */
    private void updatePurchaseContractNoticeStatus(Map<String, Integer> purchaseContractIdMap) {
        if (CollUtil.isEmpty(purchaseContractIdMap)) {
            return;
        }
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getCode, purchaseContractIdMap.keySet()));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return;
        }
        purchaseContractDOList.forEach(s -> {
            Integer convertNoticeFlag = Objects.isNull(purchaseContractIdMap.get(s.getCode())) ? ConvertNoticeFlagEnum.NOT_ISSUED.getStatus() : purchaseContractIdMap.get(s.getCode());
            s.setConvertNoticeFlag(convertNoticeFlag);
        });
        purchaseContractMapper.updateBatch(purchaseContractDOList);
    }

    @Override
    public String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId, Boolean isAuxiliary) {
        if (id == null) {
            throw exception(PURCHASE_CONTRACT_ID_NULL);
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
        ReportDTO reportDTO;
        if (sourceCode != null && sourceType != null && companyId != null) {
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
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (purchaseContractDO == null) {
            throw exception(PURCHASECONTRACT_NULL);
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(id);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASECONTRACTITEM_NULL);
        }
        HashMap<String, Object> params;
        if (isAuxiliary) {
            params = getWordParamsAuxiliary(purchaseContractDO, purchaseContractItemDOList);
        } else {
            params = getWordParams(purchaseContractDO, purchaseContractItemDOList);
        }
        String pdfFile = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,purchaseContractDO.getCode());
        if (StrUtil.isNotEmpty(pdfFile)) {
            // 更新采购合同打印状态
            PurchaseContractDO updateDO = new PurchaseContractDO();
            updateDO.setId(id);
            updateDO.setPrintFlag(BooleanEnum.YES.getValue());
            int printTimes = Objects.isNull(purchaseContractDO.getPrintTimes()) ? CalculationDict.ZERO : purchaseContractDO.getPrintTimes();
            updateDO.setPrintTimes(printTimes + 1);
            purchaseContractMapper.updateById(updateDO);
        }
        // 增加操作日志
        OperateLogUtils.addOperateLog(OPERATOR_EXTS_KEY, purchaseContractDO.getCode(), "打印");
        return pdfFile;
    }
    public HashMap<String, Object> getParamsCommon(PurchaseContractDO purchaseContractDO, List<PurchaseContractItemDO> splitListByFreeQuantity) {
        HashMap<String, Object> params = new HashMap();
        params.put("code", purchaseContractDO.getCode());
        //订单日
        if (purchaseContractDO.getPurchaseTime() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String purchaseDate = dtf.format(purchaseContractDO.getPurchaseTime());
            params.put("purchaseDate", purchaseDate);
        }

        String empty = "□";
        String checkedString = "✓";
        Integer boxColor = purchaseContractDO.getBoxWithColor();
        if (Objects.nonNull(boxColor)) {
            params.put("boxWithColorYellow",
                    boxColor.equals(BoxWithColorEnum.YELLOW.getValue()) ? checkedString : empty);
            params.put("boxWithColorTransparent",
                    boxColor.equals(BoxWithColorEnum.TRANSPARENT.getValue()) ? checkedString : empty);
            params.put("boxWithColorWhite",
                    boxColor.equals(BoxWithColorEnum.WHITE.getValue()) ? checkedString : empty);
        } else {
            params.put("boxWithColorYellow", empty);
            params.put("boxWithColorTransparent", empty);
            params.put("boxWithColorWhite", empty);
        }

        params.put("sampleCount", purchaseContractDO.getSampleCount() == null ? 0 : purchaseContractDO.getSampleCount());
        // 结算方式
        params.put("paymentName", purchaseContractDO.getPaymentName());
        //采购员
        params.put("purchaseUserName", purchaseContractDO.getPurchaseUserName());
        //跟单员
        params.put("trackUserName", purchaseContractDO.getManager().getNickname());
        //采购员电话
        AdminUserRespDTO purchaseUser = adminUserApi.getUser(purchaseContractDO.getPurchaseUserId());
        params.put("purchaseUserMobile", purchaseUser.getMobile());
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(purchaseContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        params.put("companyNameEng", companyDTO.getCompanyNameEng());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("companyAddress", companyDTO.getCompanyAddress());
        params.put("companyTel", companyDTO.getCompanyTel());
        params.put("companyFax", companyDTO.getCompanyFax());
        // 供应商信息
        VenderInfoRespVO vender = venderService.getVenderByCode(purchaseContractDO.getVenderCode());
        if (vender != null) {
            params.put("venderName", vender.getName());
            params.put("venderCompanyAddress",AreaUtils.format(vender.getFactoryAreaId(), CommonDict.SLASH_CHAR)+vender.getFactoryAddress());
            params.put("venderPhone", vender.getPhone());
            params.put("venderFax", vender.getFax());
        }
        params.put("remark", purchaseContractDO.getRemark());
        //交货日期
        String deliveryTime = "";
        if (purchaseContractDO.getDeliveryDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            deliveryTime = dtf.format(purchaseContractDO.getDeliveryDate());
        }
        params.put("deliveryTime", deliveryTime);
        //含税金额
        List<JsonAmount> withTaxPriceList = splitListByFreeQuantity.stream().map(it -> new JsonAmount(NumUtil.mul(it.getWithTaxPrice().getAmount(), it.getQuantity()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros(), it.getCurrency())).toList();
        Map<String, BigDecimal> groupedAndSummedWithTaxPriceMap = withTaxPriceList.stream()
                .collect(Collectors.groupingBy(
                        JsonAmount::getCurrency,
                        CollectorsUtil.summingBigDecimal(JsonAmount::getAmount)
                ));
        StringBuffer withTaxPrice = new StringBuffer();
        if (groupedAndSummedWithTaxPriceMap.size() == 1) {
            var withTaxPriceEntry = groupedAndSummedWithTaxPriceMap.entrySet().stream().toList().get(0);
            withTaxPrice.append(withTaxPriceEntry.getKey() + ":" +  withTaxPriceEntry.getValue());
        } else {
            groupedAndSummedWithTaxPriceMap.entrySet().forEach(i -> {
                if (withTaxPrice.isEmpty()) {
                    withTaxPrice.append(i.getKey() + ":" + i.getValue());
                } else {
                    withTaxPrice.append("|" + i.getKey() + ":" + i.getValue());
                }
            });
        }
        params.put("withTaxPrice", withTaxPrice);
        //不含税金额
        List<JsonAmount> unitPriceList = splitListByFreeQuantity.stream().map(it -> new JsonAmount(NumUtil.div(NumUtil.mul(it.getWithTaxPrice().getAmount(), it.getQuantity()), NumUtil.add(NumUtil.div(it.getTaxRate(), 100), 1)).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros(), it.getCurrency())).toList();
        Map<String, BigDecimal> groupedAndSummedUnitPriceMap = unitPriceList.stream()
                .collect(Collectors.groupingBy(
                        JsonAmount::getCurrency,
                        CollectorsUtil.summingBigDecimal(JsonAmount::getAmount)
                ));
        StringBuffer unitPrice = new StringBuffer();
        if (groupedAndSummedUnitPriceMap.size() == 1) {
            unitPrice.append(groupedAndSummedUnitPriceMap.entrySet().stream().toList().get(0).getValue());
        } else {
            groupedAndSummedUnitPriceMap.entrySet().forEach(i -> {
                if (unitPrice.isEmpty()) {
                    unitPrice.append(i.getKey() + ":" + i.getValue());
                } else {
                    unitPrice.append("|" + i.getKey() + ":" + i.getValue());
                }
            });
        }
        params.put("unitPrice", unitPrice);
        //税金
        List<JsonAmount> taxPriceList = splitListByFreeQuantity.stream().map(it -> new JsonAmount(NumUtil.sub(NumUtil.mul(it.getWithTaxPrice().getAmount(), it.getQuantity()), NumUtil.div(NumUtil.mul(it.getWithTaxPrice().getAmount(), it.getQuantity()), NumUtil.add(NumUtil.div(it.getTaxRate(), 100), 1))).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros(), it.getCurrency())).toList();
        Map<String, BigDecimal> groupedAndSummedTaxPriceMap = taxPriceList.stream()
                .collect(Collectors.groupingBy(
                        JsonAmount::getCurrency,
                        CollectorsUtil.summingBigDecimal(JsonAmount::getAmount)
                ));
        StringBuffer taxPrice = new StringBuffer();
        if (groupedAndSummedTaxPriceMap.size() == 1) {
            taxPrice.append(groupedAndSummedTaxPriceMap.entrySet().stream().toList().get(0).getValue());
        } else {
            groupedAndSummedTaxPriceMap.entrySet().forEach(i -> {
                if (taxPrice.isEmpty()) {
                    taxPrice.append(i.getKey() + ":" + i.getValue());
                } else {
                    taxPrice.append("|" + i.getKey() + ":" + i.getValue());
                }
            });
        }
        params.put("taxPrice", taxPrice);
        return params;
    }

    public HashMap<String, Object> getExcelParams(PurchaseContractDO purchaseContractDO, List<PurchaseContractItemDO> purchaseContractItemDOList) {
        List<PurchaseContractItemDO> splitListByFreeQuantity = splitPurchaseContractItemListByFreeQuantity(purchaseContractItemDOList);
        HashMap<String, Object> params = getParamsCommon(purchaseContractDO,splitListByFreeQuantity);
        params.replaceAll((k, v) -> v instanceof StringBuffer ? v.toString() : v);
        return params;
    }
    public HashMap<String, Object> getWordParams(PurchaseContractDO purchaseContractDO, List<PurchaseContractItemDO> purchaseContractItemDOList) {
        List<PurchaseContractItemDO> splitListByFreeQuantity = splitPurchaseContractItemListByFreeQuantity(purchaseContractItemDOList);
        HashMap<String, Object> params = getParamsCommon(purchaseContractDO,splitListByFreeQuantity);
        //货物明细
        RowRenderData row0 = Rows.of("货号", "品名及规格", null, null, null, "数量", "单价", "金额", "开票税率").rowExactHeight(1).center().create();
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{10, 15, 12, 13, 6, 10, 10, 14, 10}).create();
        table.addRow(row0);
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        final int[] currentRow = {Integer.parseInt(Long.toString(table.getRows().stream().count()))};
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 1), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        List<Long> skuIds = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getSkuId).distinct().toList();
//        Map<String, BigDecimal> taxRateMap = quoteitemApi.getTaxRateBySkuIdList(skuIds);
        splitListByFreeQuantity.forEach(it -> {
            SkuDTO skuDTO = skuApi.getSkuDTO(it.getSkuId());
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

            BigDecimal taxRate = Objects.isNull(it.getTaxRate())?BigDecimal.ZERO:it.getTaxRate().setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
//            if (taxRateMap.containsKey(it.getSkuCode())) {
//                taxRate = taxRateMap.get(it.getSkuCode()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP) + " %";
//            }

            String cskuCode = it.getCskuCode();
            if(StrUtil.isEmpty(cskuCode)){
                cskuCode = it.getOskuCode();
            }
            RowRenderData dataRow = Rows.of(cskuCode, it.getDescription(), null, null, null, it.getQuantity().toString(), it.getWithTaxPrice().getAmount().setScale(CalculationDict.TWO, RoundingMode.HALF_UP).toString(), NumUtil.mul(it.getWithTaxPrice().getAmount(), new BigDecimal(it.getQuantity())).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).toString(), taxRate.toString()).rowExactHeight(1).center().create();
            //为描述文本设置样式，确保完整显示
            List<ParagraphRenderData> desc = dataRow.getCells().get(1).getParagraphs();
            ParagraphStyle descStyle = ParagraphStyle
                    .builder()
                    .withAlign(ParagraphAlignment.LEFT)
                    .withSpacingRule(LineSpacingRule.EXACT)
                    .withSpacing(13)
                    .build();
            desc.get(0).setParagraphStyle(descStyle);
            table.addRow(dataRow);

            //添加第二行 - 图片行
            RowRenderData pictureRow = Rows.of("", "", null, null, null, "", "", "", "").center().create();
            //第二行第六列图片
            List<ParagraphRenderData> paragraphs = new ArrayList<>();
            ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
            try {
                byte[] content = fileApi.getFileContent(thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
                PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(180, 180).create();
                paragraphRenderData.addPicture(pictureRenderData);
                paragraphs.add(paragraphRenderData);
                pictureRow.getCells().get(5).setParagraphs(paragraphs);
                logger.info("缩略图{}加载成功", thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            } catch (Exception e) {
                logger.warn("缩略图{}加载失败，详情：" + e.getMessage(), thumbnail.substring(thumbnail.lastIndexOf("get/") + 4));
            }
            table.addRow(pictureRow);
            //添加合并规则
            currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
            ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 0))
                    .map(MergeCellRule.Grid.of(currentRow[0] - 2, 1), MergeCellRule.Grid.of(currentRow[0] - 1, 4))
                    .map(MergeCellRule.Grid.of(currentRow[0] - 1, 5), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        });
        //添加备注行
        table.addRow(Rows.of("备注：", purchaseContractDO.getRemark(), null, null, null, null, null, null, null).rowAtleastHeight(2).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 1), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        table.addRow(Rows.of("交货日期", params.get("deliveryTime").toString(), "不含税金额", params.get("unitPrice").toString(), "税金", params.get("taxPrice").toString(), "含税金额", params.get("withTaxPrice").toString(), null).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 7), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        table.setMergeRule(ruleBuilder.build());
        params.put("skuTable", table);

        //公章处理
        //1.回签之后的合同才处理
//        CustomPictureRenderData pictureRenderData = new CustomPictureRenderData();
//        if (Objects.equals(purchaseContractDO.getSignBackFlag(), BooleanEnum.YES.getValue())) {
//            List<DictSimpleFileList> simpleFileLists = companyDTO.getPicture();
//            //2.有公章才处理
//            if (CollUtil.isNotEmpty(simpleFileLists)) {
//                Optional<DictSimpleFileList> first = simpleFileLists.stream().filter(s -> Objects.equals(s.getValue(), OfficialSealTypeEnum.CONTRACT_STAMP.getValue().toString())).findFirst();
//                if (first.isPresent()) {
//                    Optional<SimpleFile> first1 = first.get().getFileList().stream().filter(s -> s.getMainFlag() == 1).findFirst();
//                    if (first1.isPresent()) {
//                        SimpleFile simpleFile = first1.get();
//                        //3.如果有公章，且合同已经回签 “加盖公章”
//                        try {
//                            byte[] content = fileApi.getFileContent(simpleFile.getFileUrl().substring(simpleFile.getFileUrl().lastIndexOf("get/") + 4));
//                            // 创建CustomPictureRenderData对象而不是使用Pictures.ofBytes
//                            pictureRenderData.setData(content);
//                            pictureRenderData.setPath(simpleFile.getFileUrl());
//                            pictureRenderData.setWidth(100);
//                            pictureRenderData.setHeight(100);
//                        } catch (Exception e) {
//                            logger.warn("公章图片加载失败，详情：" + e.getMessage());
//                        }
//                    }
//                }
//            }
//        }
//        params.put("companyImg", pictureRenderData);
//        Configure configure = Configure.createDefault().plugin('%', new PoiPicPolicy());
//        params.put("configure", configure);

        //行循环/动态表格
//        List<Skus> skus = new ArrayList<>();
//        purchaseContractItemDOList.forEach(it -> {
//            SkuDTO skuDTO = skuApi.getSkuDTO(it.getSkuId());
//            List<SimpleFile> pictures = skuDTO.getPicture();
//            if (CollUtil.isEmpty(pictures)) {
//                throw exception(SKU_PICTURE_NULL);
//            }
//            SimpleFile simpleFile = pictures.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
//            if (simpleFile == null) {
//                throw exception(SKU_MAIN_PICTURE_NULL);
//            }
//            Skus sku = new Skus();
//            sku.cskuCode = it.getCskuCode();
//            sku.description = skuDTO.getDescription();
//            byte[] content = new byte[0];
//            try {
//                content = fileApi.getFileContent(simpleFile.getFileUrl().substring(simpleFile.getFileUrl().lastIndexOf("get/") + 4));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            sku.picture = content;
//            sku.quantity = it.getQuantity().toString();
//            sku.withTaxPrice = it.getWithTaxPrice().getCurrency() + "：" + it.getWithTaxPrice().getAmount();
//            sku.amount = NumUtil.mul(it.getWithTaxPrice().getAmount(), new BigDecimal(it.getQuantity())).toString();
//            skus.add(sku);
//        });
//        params.put("detail_table", skus);
//        Configure configure = Configure.builder().bind("detail_table", new DetailTablePolicy()).build();
//        params.put("configure", configure);
        return params;
    }

    public HashMap<String, Object> getWordParamsAuxiliary(PurchaseContractDO purchaseContractDO, List<PurchaseContractItemDO> purchaseContractItemDOList) {
        HashMap<String, Object> params = new HashMap();
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(purchaseContractDO.getCompanyId());
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        params.put("companyNameEng", companyDTO.getCompanyNameEng());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("companyAddress", companyDTO.getCompanyAddress());
        params.put("companyTel", companyDTO.getCompanyTel());
        params.put("companyFax", companyDTO.getCompanyFax());

        VenderInfoRespVO vender = venderService.getVenderByCode(purchaseContractDO.getVenderCode());
        if (vender != null) {
            params.put("venderName", vender.getName());
            params.put("venderCompanyAddress",AreaUtils.format(vender.getFactoryAreaId(), CommonDict.SLASH_CHAR)+vender.getFactoryAddress());
            params.put("venderTel", vender.getPhone());
        }
        params.put("deliveryAddress", purchaseContractDO.getDeliveryAddress());
        if (purchaseContractDO.getDeliveryDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String deliveryDate = dtf.format(purchaseContractDO.getDeliveryDate());
            params.put("deliveryDate", deliveryDate);
        }
        params.put("code", purchaseContractDO.getCode());
        if (purchaseContractDO.getCreateTime() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String createTime = dtf.format(purchaseContractDO.getCreateTime());
            params.put("createTime", createTime);
        }
        //采购员
        params.put("purchaseUserName", purchaseContractDO.getPurchaseUserName());
        //跟单员
        params.put("trackUserName", purchaseContractDO.getManager().getNickname());

        params.put("paymentName", purchaseContractDO.getPaymentName());
        params.put("minimumBaseQuantity", purchaseContractDO.getMinimumBaseQuantity().setScale(CalculationDict.TWO, RoundingMode.HALF_UP));
        params.put("restockingDeadline", purchaseContractDO.getRestockingDeadline());
        //货物明细
        RowRenderData row0 = Rows.of("合同号", "包材货号", "品名", "规格","数量", "单价", "金额", "备注").rowExactHeight(1).center().create();
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{15, 15, 10, 15,10, 10, 15, 10}).create();
        table.addRow(row0);
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        final int[] currentRow = {Integer.parseInt(Long.toString(table.getRows().stream().count()))};
        purchaseContractItemDOList.forEach(it -> {
            //添加第一行
            String code = null;
            if(StrUtil.isNotEmpty(it.getAuxiliaryPurchaseContractCode())){
                code = it.getAuxiliaryPurchaseContractCode();
            }else{
                code = it.getSaleContractCode();
            }
            RowRenderData dataRow = Rows.of(code,
                    it.getSkuCode(),
                    it.getSkuName(),
                    it.getSpecRemark(),
                    it.getQuantity().toString(),
                    it.getWithTaxPrice().getAmount().setScale(CalculationDict.TWO, RoundingMode.HALF_UP).toString(),
                    NumUtil.mul(it.getWithTaxPrice().getAmount(),
                            new BigDecimal(it.getQuantity())).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).toString(),
                    it.getRemark()).center().create();
            table.addRow(dataRow);
        });
        BigDecimal totalAmount = purchaseContractItemDOList.stream()
                .map(s -> NumUtil.mul(s.getWithTaxPrice().getAmount(), new BigDecimal(s.getQuantity())))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.TWO, RoundingMode.HALF_UP);
        table.addRow(Rows.of("合计：", null, null, null, null,null, totalAmount.toString(), null).rowAtleastHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        table.setMergeRule(ruleBuilder.build());
        params.put("skuTable", table);
        return params;
    }

    @Override
    public PageResult<PurchaseContractSimpleDTO> getPurchaseContractSimplePage(PurchaseContractGetSimplePageReqDTO pageReqVO) {
        List<String> venderCodeList = venderService.getCodeListByName(pageReqVO.getVenderName());
        if (CollUtil.isEmpty(venderCodeList)) {
            venderCodeList = pageReqVO.getVenderCodeList();
        } else {
            if (CollUtil.isNotEmpty(pageReqVO.getVenderCodeList())) {
                venderCodeList.addAll(pageReqVO.getVenderCodeList());
            }
        }
        List<String> custCodeList = custApi.getCodeListByName(pageReqVO.getCustName());
        if (pageReqVO.getConfirmFlag() == null) {
            pageReqVO.setConfirmFlag(1);
        }
        PurchaseContractPageReqVO req = new PurchaseContractPageReqVO()
                .setCode(pageReqVO.getCode())
                .setPurchaseContractCode(pageReqVO.getPurchaseContractCode())
                .setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                .setExcludePurchaseContract(pageReqVO.getExcludePurchaseContract())
                .setCompanyId(pageReqVO.getCompanyId())
                .setAuxiliaryFlag(pageReqVO.getAuxiliaryFlag())
                .setVenderCode(pageReqVO.getVenderCode())
                .setVenderCodeList(venderCodeList)
                .setPurchaseUserId(pageReqVO.getPurchaseUserId())
                .setPurchaseUserDeptId(pageReqVO.getPurchaseUserDeptId())
                .setConfirmFlag(pageReqVO.getConfirmFlag())
                .setCustCodeList(custCodeList)
                .setAutoFlag(BooleanEnum.NO.getValue());
        req.setPageNo(pageReqVO.getPageNo()).setPageSize(pageReqVO.getPageSize());
        PageResult<PurchaseContractDO> purchaseContractDOPageResult = purchaseContractMapper.selectPage(req);
        PageResult<PurchaseContractSimpleDTO> result = BeanUtils.toBean(purchaseContractDOPageResult, PurchaseContractSimpleDTO.class);
        List<PurchaseContractSimpleDTO> resultList = result.getList();
        if (CollUtil.isEmpty(resultList)) {
            return result;
        }
        List<Long> custIdList = resultList.stream().map(PurchaseContractSimpleDTO::getCustId).distinct().toList();
        Map<Long, SimpleCustRespDTO> custMap = custApi.getSimpleCustRespDTOMap(custIdList);
        List<Long> venderIdList = resultList.stream().map(PurchaseContractSimpleDTO::getVenderId).distinct().toList();
        Map<Long, String> venderMap = venderService.getVenderNameCache(venderIdList);
        Map<Long, SimpleCompanyDTO> companyMap = companyApi.getSimpleCompanyDTO();
        resultList.forEach(s -> {
            if (CollUtil.isNotEmpty(custMap)) {
                SimpleCustRespDTO simpleCustRespDTO = custMap.get(s.getCustId());
                if (Objects.nonNull(simpleCustRespDTO)) {
                    s.setCustName(simpleCustRespDTO.getName());
                }
            }
            if (CollUtil.isNotEmpty(venderMap)) {
                s.setVenderName(venderMap.get(s.getVenderId()));
            }
            if (CollUtil.isNotEmpty(companyMap)) {
                SimpleCompanyDTO simpleCompanyDTO = companyMap.get(s.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)) {
                    s.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
        });
        List<Long> contractIdList = resultList.stream().map(PurchaseContractSimpleDTO::getId).distinct().toList();
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, contractIdList);
        List<Long> skuyIdList = itemDOList.stream().map(PurchaseContractItemDO::getSkuId).distinct().toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuyIdList);
        if (CollUtil.isEmpty(itemDOList)) {
            return result;
        }
        Map<Long, List<PurchaseContractItemDO>> itemDOMap = itemDOList.stream().collect(Collectors.groupingBy(PurchaseContractItemDO::getPurchaseContractId));
        if (CollUtil.isEmpty(itemDOMap)) {
            return result;
        }
        resultList.forEach(s -> {
            List<PurchaseContractItemDO> itemDOS = itemDOMap.get(s.getId());
            if (CollUtil.isNotEmpty(itemDOS)) {
                List<PurchaseContractItemSimpleDTO> items = BeanUtils.toBean(itemDOS, PurchaseContractItemSimpleDTO.class);
                items.forEach(i -> {
                    if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                        SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                        if (Objects.nonNull(simpleSkuDTO)) {
                            i.setSkuName(simpleSkuDTO.getSkuName());
                            i.setCustProFlag(simpleSkuDTO.getCustProFlag());
                        }
                    }
                });
                //子项根据sku去重，目前业务不需要
//                List<PurchaseContractItemSimpleDTO> children = new ArrayList<>();
//                items.forEach(i -> {
//                    i.setId(null);
//                    Optional<PurchaseContractItemSimpleDTO> first = children.stream().filter(c -> Objects.equals(c.getSkuCode(), i.getSkuCode())).findFirst();
//                    if (first.isPresent()) {
//                        first.get().setQuantity(first.get().getQuantity() + i.getQuantity());
//                    } else {
//                        children.add(i);
//                    }
//                });
                s.setChildren(items);
            }
        });
        return result;
    }


    @Override
    public void rePurchaseById(RePurchaseSaveReqVO reqVO) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(reqVO.getId());
        purchaseContractDO.setRePurchaseDesc(reqVO.getRePurchaseDesc())
                .setPurchaseTime(LocalDateTime.now())
                .setRePurchaseOldFlag(1);
        purchaseContractMapper.updateById(purchaseContractDO);
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractDO.getCode());

        purchaseContractDO.setId(null).setCode(null);
        purchaseContractDO.setCreateTime(LocalDateTime.now()).setUpdateTime(null);
        purchaseContractDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult())
                .setContractStatus(PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode());
        purchaseContractDO.setRePurchaseFlag(1);
        String categoryCode = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        purchaseContractDO.setCode(categoryCode);
        purchaseContractMapper.insert(purchaseContractDO);
        orderLinkApi.updateOrderLinkStatus(purchaseContractDO.getCode(), BusinessNameDict.PURCHASE_CONTRACT_NAME, purchaseContractDO.getLinkCodeList(), PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode());
        itemDOList.forEach(s -> {
            s.setId(null);
            s.setPurchaseContractCode(categoryCode);
            s.setPurchaseContractId(purchaseContractDO.getId());
        });
        purchaseContractItemMapper.insertBatch(itemDOList);

    }

    @Override
    public List<PurchaseContractDO> selectByIdList(List<Long> idList) {
        return purchaseContractMapper.selectBatchIds(idList);
    }

    @Override
    public Map<String, UserDept> getPurManagerMapByCodeList(List<String> codeList) {
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getManager, PurchaseContractDO::getCode).in(PurchaseContractDO::getCode, codeList));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return Map.of();
        }
        List<UserDept> tractUserList = purchaseContractDOList.stream().map(PurchaseContractDO::getManager).distinct().toList();
//        if (CollUtil.isEmpty(tractUserIdList)) {
//            return Map.of();
//        }
//        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(tractUserIdList);
//        if (CollUtil.isEmpty(userDeptListCache)) {
//            return Map.of();
//        }
        return purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, s -> s.getManager()));
    }

    @Override
    @DataPermission(enable = false)
    public Collection<String> validatePurContractExists(Collection<String> codeList) {
        List<PurchaseContractDO> contractDOList = purchaseContractMapper.selectList(PurchaseContractDO::getCode, codeList);
        if (CollUtil.isEmpty(contractDOList)) {
            return codeList;
        }
        List<PurchaseContractDO> list = contractDOList.stream().filter(s -> BpmProcessInstanceResultEnum.APPROVE.getResult().equals(s.getAuditStatus()) && !BooleanEnum.YES.getValue().equals(s.getAutoFlag())).toList();
        if (CollUtil.isEmpty(list)) {
            return codeList;
        }
        List<String> contractCodeList = list.stream().map(PurchaseContractDO::getCode).toList();
        return CollUtil.subtract(codeList,contractCodeList);
    }

//    @Override
//    public Map<String, PurchaseContractDTO> getPurchaseContractMap(List<String> codeList,Long companyId) {
//        LambdaQueryWrapperX<PurchaseContractDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<>();
//        lambdaQueryWrapperX.in(PurchaseContractDO::getSaleContractCode, codeList);
//        lambdaQueryWrapperX.eqIfPresent(PurchaseContractDO::getCompanyId,companyId);
//        lambdaQueryWrapperX.eq(PurchaseContractDO::getAutoFlag,BooleanEnum.NO.getValue());
//        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(lambdaQueryWrapperX);
//        if (CollUtil.isEmpty(purchaseContractDOList)) {
//            return Map.of();
//        }
//        return purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getSaleContractCode, s -> new PurchaseContractDTO().setSaleContractCode(s.getSaleContractCode())
//                .setPurchaseContractCode(s.getCode())
//                .setPurchaseUserId(s.getPurchaseUserId())
//                .setPurchaseUserName(s.getPurchaseUserName())
//                .setPurchaseUserDeptId(s.getPurchaseUserDeptId())
//                .setPurchaseUserDeptName(s.getPurchaseUserDeptName()), (o, n) -> o));
//    }

    @Override
    @DataPermission(enable = false)
    public List<PurchaseContractDO> getPurchaseByCodeList(List<String> codeList) {

        return purchaseContractMapper.selectList(PurchaseContractDO::getCode, codeList);
    }

    @Override
    public List<PurchasePaymentPlan> getPurchasePaymentPlanList(List<Long> idList) {
        // 仅查询采购合同编号及供应商编号即可
        LambdaQueryWrapper<PurchaseContractDO> queryWrapper = new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getVenderCode, PurchaseContractDO::getCompanyId)
                .in(PurchaseContractDO::getId, idList);
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        Set<Long> companyIdList = purchaseContractDOList.stream().map(PurchaseContractDO::getCompanyId).collect(Collectors.toSet());
        if (companyIdList.size() > CalculationDict.ONE) {
            throw exception(NOT_SUPPORTED_PAYMENTS_DIFFERENT_COMPANY);
        }
        // 有且仅有一个供应商时可发起付款申请
        Set<String> venderCodeSet = purchaseContractDOList.stream().map(PurchaseContractDO::getVenderCode).collect(Collectors.toSet());
        if (venderCodeSet.size() > CalculationDict.ONE) {
            throw exception(NOT_SUPPORTED_PAYMENTS_DIFFERENT_VENDER);
        }
        List<String> contractCodeList = purchaseContractDOList.stream().map(PurchaseContractDO::getCode).toList();
        return purchasePaymentPlanMapper.selectList(new LambdaQueryWrapperX<PurchasePaymentPlan>().in(PurchasePaymentPlan::getContractCode, contractCodeList));
    }

    @Override
    public String getProcessDefinitionKeyByBusinessId(Long id) {
        PurchaseContractChange purchaseContractChange = purchasecontractchangeMapper.selectById(id);
        if (Objects.isNull(purchaseContractChange)) {
            return null;
        }
        return purchaseContractChange.getModelKey();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEffectRanageStatus(Long changeId) {
        updateChangeResult(changeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signBackContract(SignBackReq signBackReq) {
        LocalDateTime signBackDate = signBackReq.getSignBackDate();
        if (Objects.isNull(signBackDate)) {
            throw exception(SIGN_BACK_DATE_NOT_NULL);
        }
        if (signBackDate.isAfter(LocalDateTime.now())) {
            throw exception(SIGN_BACK_DATE_NOT_AFTER_NOW);
        }
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectOne(PurchaseContractDO::getId, signBackReq.getContractId());
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_ID_NULL);
        }
//        if(!Objects.equals(purchaseContractDO.getPlaceOrderFlag(),BooleanEnum.YES.getValue())){
//            throw exception(PURCHASE_CONTRACT_NOT_PLACE_ORDER);
//        }
        List<SimpleFile> signBackAnnexList = CollUtil.isEmpty(purchaseContractDO.getSignBackAnnexList()) ? new ArrayList<>() : purchaseContractDO.getSignBackAnnexList();
        if (CollUtil.isNotEmpty(signBackReq.getSignBackAnnexList())) {
            signBackAnnexList.addAll(signBackReq.getSignBackAnnexList());
        }
        Integer contractStatus = purchaseContractDO.getContractStatus();
        purchaseContractDO.setSignBackTime(DateTime.now().toLocalDateTime()).setSignBackDesc(signBackReq.getRemark()).setSignBackAnnexList(signBackAnnexList);
        purchaseContractDO.setSignBackFlag(BooleanEnum.YES.getValue());
        purchaseContractDO.setContractStatus(PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode());
        purchaseContractMapper.updateById(purchaseContractDO);
        // 更新采购合同付款计划回签日
        List<Integer> dateTypes = new ArrayList<>();
        dateTypes.add(DateTypeEnum.SIGN_BACK_DATE.getValue());
        updatePaymentPlanSignBackTime(purchaseContractDO.getCode(), dateTypes,LocalDateTime.now());
        // 补充操作日志明细
        dealOperateLog("【采购合同回签】", purchaseContractDO.getCode());
        // 回签完成自动下单
        // 仅待下单状态自动下单
        if (Objects.equals(contractStatus, PurchaseContractStatusEnum.AWAITING_ORDER.getCode()) && Objects.equals(purchaseContractDO.getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            placeOrderContract(signBackReq.getContractId());
        }
    }

    /**
     * 修改采购合同回签日期
     *
     * @param contractCode 采购合同编号
     * @param dateTypes    日期类型
     */
    private void updatePaymentPlanSignBackTime(String contractCode, List<Integer> dateTypes,LocalDateTime estDepartureTime) {
        if (StrUtil.isEmpty(contractCode)) {
            return;
        }
        List<PurchasePaymentPlan> purchasePaymentPlans = purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, contractCode);
        if (CollUtil.isEmpty(purchasePaymentPlans)) {
            return;
        }
        purchasePaymentPlans.stream().filter(s -> dateTypes.contains(s.getDateType())).forEach(s -> {
            s.setStartDate(estDepartureTime);  //TODO：计算起始日
            s.setExpectedReceiptDate(DateTimeUtil.getNearestPaymentDay(estDepartureTime.plusDays(s.getDays())));
        });
        purchasePaymentPlanMapper.updateBatch(purchasePaymentPlans);
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, Long id) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        purchaseContractDO.setConfirmFlag(confirmFlag);
        purchaseContractMapper.update(purchaseContractDO, new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getId, id));
        saleContractApi.updateChangeConfirmFlag(EffectRangeEnum.SCM.getValue(), purchaseContractDO.getCode());
    }

    @Override
    @DataPermission(enable = false)
    public void updateAuxiliaryConfirmFlag(Integer confirmFlag, Long id) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        purchaseContractDO.setConfirmFlag(confirmFlag);
        purchaseContractMapper.update(purchaseContractDO, new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getId, id));
        saleContractApi.updateChangeConfirmFlag(EffectRangeEnum.AUXILIARY_PURCHASE.getValue(), purchaseContractDO.getCode());
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, String code) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, code);
        purchaseContractDO.setConfirmFlag(confirmFlag);
        purchaseContractMapper.update(purchaseContractDO, new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getCode, code));
    }

    @Override
    public void updateVenderId(Long venderId, String venderCode) {
        Integer[] statusList = {
                PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode(),
                PurchaseContractStatusEnum.AWAITING_APPROVAL.getCode(),
                PurchaseContractStatusEnum.REJECTED.getCode(),
                PurchaseContractStatusEnum.AWAITING_ORDER.getCode(),
                PurchaseContractStatusEnum.EXPECTING_DELIVERY.getCode()
        };
        purchaseContractMapper.update(new PurchaseContractDO().setVenderId(venderId), new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getVenderCode, venderCode).in(PurchaseContractDO::getContractStatus, statusList));
    }

    /**
     * 处理子项变更
     *
     * @param oldList                变更前列表
     * @param list                   变更后列表
     * @param tarClazz               目标类型
     * @param purchaseContractChange 采购合同变更实体
     * @param formChangeDTOList      变更字段配置缓存
     * @param submitFlag             提交标识
     * @param <T>                    变更对象类型
     * @return 变更后列表
     */
    private <T extends ChangeExInterface> List<T> dealChangeField(List<T> oldList, List<T> list, Class<T> tarClazz, String tableName, PurchaseContractChange purchaseContractChange, Map<String, FormChangeDTO> formChangeDTOList, AtomicReference<Integer> submitFlag) {
        if (Objects.isNull(submitFlag)) {
            submitFlag = new AtomicReference<>(0);
        }
        List<DiffRecord<T>> diffRecords = DiffUtil.compareLists(oldList, list);
        Tuple tuple = new ChangeCompareUtil<T>().transformChildList(diffRecords, tarClazz);
        List<T> changeList = tuple.get(0);
        if (Objects.isNull(submitFlag.get()) || SubmitFlagEnum.ONLY_SAVE.getStatus().equals(submitFlag.get())) {
            // 比对销售明细字段
            Set<String> fieldList = tuple.get(1);
            AtomicReference<Integer> atomicReference = formChangeApi.dealShipmentTable(fieldList, formChangeDTOList, purchaseContractChange, tableName, submitFlag, false);
            if (Objects.isNull(atomicReference)) {
                submitFlag.set(0);
            } else {
                submitFlag.set(atomicReference.get());
            }
        }
        return changeList;
    }

    private List<JsonEffectRange> dealEffectRange(Map<String, FormChangeDTO> formChangeDTOList, String saleContractCode) {
        // 获取影响范围
        List<Integer> effectRange = formChangeDTOList.values().stream()
                .filter(s -> BooleanEnum.YES.getValue().equals(s.getMainInstanceFlag()))
                .flatMap(s -> s.getChildren().stream())
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
        return List.of();
//        return effectRange.stream().map(s -> {
//                    // 影响采购处理
//                    if (EffectRangeEnum.SCM.getValue().equals(s)) {
//                        List<String> effectPurchaseCode = saleContractMapper.getEffectPurchaseCode(saleContractCode);
//                        if (CollUtil.isEmpty(effectPurchaseCode)) {
//                            return null;
//                        }
//                        return effectPurchaseCode.stream().map(code->new JsonEffectRange().setEffectRangeCode(code).setEffectRangeType(s).setConfirmFlag(ConfirmFlagEnum.NO.getValue())).toList();
//                        //影响出运处理
//                    } else if (EffectRangeEnum.DMS.getValue().equals(s)) {
//                        List<String> effectShipmentCode = saleContractMapper.getEffectShipmentCode(saleContractCode);
//                        if (CollUtil.isEmpty(effectShipmentCode)) {
//                            return null;
//                        }
//                        return effectShipmentCode.stream().map(code->new JsonEffectRange().setEffectRangeCode(code).setEffectRangeType(s).setConfirmFlag(ConfirmFlagEnum.NO.getValue())).toList();
//                    }
//                    return null;
//                }).filter(Objects::nonNull)
//                .flatMap(Collection::stream)
//                .toList();
    }

    @Override
    public List<Long> getPurchaseContractIdsByVenderCode(String venderCode) {
        List<PurchaseContractItemDO> purchaseContractItemList = purchaseContractItemMapper.getPurchaseContractItemListByVenderCode(venderCode);
        if (purchaseContractItemList == null) {
            return null;
        }
        List<Long> purchaseContractIds = purchaseContractItemList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        return purchaseContractIds;
    }

    @Override
    public List<Long> getPurchaseContractIdsBySkuCode(String skuCode) {
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
        List<PurchaseContractItemDO> purchaseContractItemList = purchaseContractItemMapper.getPurchaseContractItemListBySkuCodeList(skuCodeList);
        if (purchaseContractItemList == null) {
            return null;
        }
        List<Long> purchaseContractIds = purchaseContractItemList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        return purchaseContractIds;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceListByPurchaseContractId(Long id, EffectRangeEnum effectRangeEnum) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        String code = purchaseContractDO.getCode();
        List<ConfirmSourceEntity> confirmSourceList = new ArrayList<>();
        // 获取采购合同变更来源信息
        List<ConfirmSourceEntity> custList = custApi.getConfirmSourceList(code, effectRangeEnum.getValue());
        if (CollUtil.isNotEmpty(custList)) {
            confirmSourceList.addAll(custList);
        }
        List<ConfirmSourceEntity> venderList = venderApi.getConfirmSourceList(code, effectRangeEnum.getValue());
        if (CollUtil.isNotEmpty(venderList)) {
            confirmSourceList.addAll(venderList);
        }
        List<ConfirmSourceEntity> skuList = skuApi.getConfirmSourceList(code, effectRangeEnum.getValue());
        if (CollUtil.isNotEmpty(skuList)) {
            confirmSourceList.addAll(skuList);
        }
        List<ConfirmSourceEntity> saleConfirmList = saleContractApi.getConfirmSourceList(code, effectRangeEnum.getValue());
        if (CollUtil.isNotEmpty(saleConfirmList)) {
            confirmSourceList.addAll(saleConfirmList);
        }
        List<ConfirmSourceEntity> purhcaseConfirmList = getConfirmSourceList(code, effectRangeEnum.getValue());
        if (CollUtil.isNotEmpty(purhcaseConfirmList)) {
            confirmSourceList.addAll(purhcaseConfirmList);
        }
        return confirmSourceList;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceListByAuxiliaryPurchaseContractId(Long id) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        String code = purchaseContractDO.getCode();
        List<ConfirmSourceEntity> confirmSourceList = new ArrayList<>();
        // 获取采购合同变更来源信息
        List<ConfirmSourceEntity> saleConfirmList = saleContractApi.getConfirmSourceList(code, EffectRangeEnum.AUXILIARY_PURCHASE.getValue());
        if (CollUtil.isNotEmpty(saleConfirmList)) {
            confirmSourceList.addAll(saleConfirmList);
        }
        List<ConfirmSourceEntity> purchaseConfirmList = getConfirmSourceList(code, EffectRangeEnum.AUXILIARY_PURCHASE.getValue());
        if (CollUtil.isNotEmpty(purchaseConfirmList)) {
            confirmSourceList.addAll(purchaseConfirmList);
        }
        return confirmSourceList;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        LambdaQueryWrapper<PurchaseContractChange> queryWrapper = new LambdaQueryWrapper<PurchaseContractChange>().select(PurchaseContractChange::getId, PurchaseContractChange::getSourceCode)
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
        List<PurchaseContractChange> TList = purchaseContractChangeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return List.of();
        }
        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getCode()).setConfirmSourceType(EffectRangeEnum.SCM.getValue())).toList();
    }

    @Override
    public Long getPurchaseContractNumByPlanId(Long planId) {
        return purchaseContractMapper.selectCount(PurchaseContractDO::getPurchasePlanId, planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelatedPurchaseContractRespVO getRelatedNum(Long id) {
        // 销售合同数量
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        String purchaseContractCode = Optional.ofNullable(purchaseContractDO).map(PurchaseContractDO::getCode).orElse(CommonDict.EMPTY_STR);
        Long saleContractNum = Optional.ofNullable(purchaseContractDO)
                .map(s -> {
                    if (Objects.isNull(s.getSaleContractId())) {
                        return CalculationDict.LONG_ZERO;
                    } else {
                        return CalculationDict.LONG_ZERO;
                    }
                })
                .orElse(CalculationDict.LONG_ZERO);
        // 采购计划数量
        Long purchasePlanNum = Optional.ofNullable(purchaseContractDO)
                .map(s -> {
                    if (Objects.isNull(s.getPurchasePlanId())) {
                        return CalculationDict.LONG_ZERO;
                    } else {
                        return CalculationDict.LONG_ZERO;
                    }
                })
                .orElse(CalculationDict.LONG_ZERO);
        // 采购变更单
        CompletableFuture<Long> purchaseChangeFuture = CompletableFuture.supplyAsync(() -> purchasecontractchangeMapper.selectCount(PurchaseContractChange::getSourceCode, purchaseContractCode));
        // 入库单
        CompletableFuture<Long> billFuture = CompletableFuture.supplyAsync(() -> stockNoticeApi.getBillNumByPurchaseCode(id));
        // 验货单
        CompletableFuture<Long> qualityInspectionFuture = CompletableFuture.supplyAsync(() -> qualityInspectionApi.getQualityInspectionNumByPurchaseContractCode(purchaseContractCode));
        // 付款单
        CompletableFuture<Long> paymentFuture = CompletableFuture.supplyAsync(() -> paymentApi.getPaymentNumByPurchaseContractCode(BusinessTypeEnum.PURCHASE_PAYMENT.getCode(), purchaseContractCode));
        // 退货单

        // 辅料合同
        CompletableFuture<Long> auxFuture = CompletableFuture.supplyAsync(() -> {
            List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractId).eq(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode, purchaseContractCode));
            if (CollUtil.isEmpty(purchaseContractItemDOList)) {
                return CalculationDict.LONG_ZERO;
            }
            return purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().count();
        });
        RelatedPurchaseContractRespVO relatedPurchaseContractRespVO;
        try {
            relatedPurchaseContractRespVO = new RelatedPurchaseContractRespVO()
                    .setReceiptOrderNum(billFuture.get())
                    .setPaymentOrderNum(paymentFuture.get())
                    .setAuxiliaryContractNum(auxFuture.get())
                    .setPurchaseChangeNum(purchaseChangeFuture.get())
                    .setPurchasePlanNum(purchasePlanNum)
                    //                .setExchangeOrderNum()
                    .setInspectionOrderNum(qualityInspectionFuture.get())
                    .setSaleContractNum(saleContractNum);
        } catch (Exception e) {
            throw exception(RELATION_ORDER_EXCEPTION);
        }
        return relatedPurchaseContractRespVO;
    }

    @Override
    public Long getOrderNumBySaleContractId(boolean auxiliaryFlag, Long id) {
        LambdaQueryWrapperX<PurchaseContractDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getSaleContractId, id);
        if (auxiliaryFlag) {
            queryWrapperX.eq(PurchaseContractDO::getAuxiliaryFlag, BooleanEnum.YES.getValue());
        } else {
            queryWrapperX.eq(PurchaseContractDO::getAuxiliaryFlag, BooleanEnum.NO.getValue());
        }
        return purchaseContractMapper.selectCount(queryWrapperX);
    }

    @Override
    public void updatePurchaseDesignContract(PurchaseContractDesignSaveReqVO updateReqVO) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(updateReqVO.getId());
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        purchaseContractDO.setDesignDraftList(updateReqVO.getDesignDraftList());
        purchaseContractMapper.updateById(purchaseContractDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void placeOrderContract(Long id) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_ID_NULL);
        }
        if (purchaseContractDO.getPlaceOrderFlag().equals(BooleanEnum.YES.getValue())) {
            return;
        }
        this.handleWmsBillAndStock(purchaseContractDO.getId());
        purchaseContractDO.setPlaceOrderFlag(BooleanEnum.YES.getValue());
        purchaseContractDO.setPlaceOrderTime(LocalDateTime.now());
        purchaseContractMapper.updateById(purchaseContractDO);
        // 补充操作日志明细
        dealOperateLog("【采购合同下单】", purchaseContractDO.getCode());
    }

    @Override
    public List<TransformNoticeMidVO> getToNoticeMid(List<Long> ids) {
        // 查找明细信息
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        boolean convertedNoticeFlag = purchaseContractItemDOList.stream().anyMatch(s -> {
            int noticedQuantity = Objects.isNull(s.getNoticedQuantity()) ? 0 : s.getNoticedQuantity();
            return noticedQuantity == s.getQuantity();
        });
        if (convertedNoticeFlag){
            throw exception(PURCHASE_CONTRACT_CONVERTED_STOCK_NOTICE);
        }
        // 获取对应采购合同
        List<Long> purchaseContractIdList = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectBatchIds(purchaseContractIdList);
        if (CollUtil.isEmpty(purchaseContractDOList) || purchaseContractDOList.size() != purchaseContractIdList.size()) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        List<PurchaseContractItemDO> checkShipmentList = purchaseContractItemDOList.stream().filter(x -> x.getOutFlag().intValue() == BooleanEnum.YES.getValue()).toList();
        if (!CollectionUtils.isEmpty(checkShipmentList)) {
            String skuCodeCollect = checkShipmentList.stream().map(PurchaseContractItemDO::getSkuCode).collect(Collectors.joining(","));
            throw exception(PURCHASE_CONTRACT_ITEM_SHIPMENTED, skuCodeCollect);
        }
        // 采购合同按照主键归集 方便写入后续主单信息
        Map<Long, PurchaseContractDO> purchaseContractMap = purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getId, s -> s, (o, n) -> o));
        List<TransformNoticeMidVO> transformNoticeMidRespVOList = PurchaseContractConvert.INSTANCE.convertTransformNoticeMidList(purchaseContractItemDOList);
        if (CollUtil.isNotEmpty(transformNoticeMidRespVOList)) {
            // 获取主体信息 赋值主体名称
            Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
            // 获取供应商缓存  赋值供应商名称
            Map<String, String> venderNameCache = venderApi.getVenderNameCache(null);
            //获取产品
            List<String> skuCodeList = transformNoticeMidRespVOList.stream().map(TransformNoticeMidVO::getSkuCode).distinct().toList();
            Map<String, SkuDTO> skuMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
            //获取客户
            List<Long> custIds = transformNoticeMidRespVOList.stream().map(TransformNoticeMidVO::getCustId).distinct().toList();
            Map<Long, SimpleCustRespDTO> custMap = custApi.getSimpleCustRespDTOMap(custIds);
            transformNoticeMidRespVOList.forEach(s -> {
                Long purchaseContractId = s.getPurchaseContractId();
                PurchaseContractDO purchaseContractDO = purchaseContractMap.get(purchaseContractId);
                s.setCompanyId(purchaseContractDO.getCompanyId());
                s.setManager(purchaseContractDO.getManager());
                s.setSales(purchaseContractDO.getSales());
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(s.getCompanyId());
                s.setCompanyName(simpleCompanyDTO.getName());
                s.setVenderId(purchaseContractDO.getVenderId());
                s.setVenderCode(purchaseContractDO.getVenderCode());
                s.setVenderName(venderNameCache.get(s.getVenderCode()));
                s.setSaleContractId(purchaseContractDO.getSaleContractId());
                s.setOrderQuantity(s.getQuantity());
                s.setSaleContractCode(purchaseContractDO.getSaleContractCode());
                s.setLinkCodeList(purchaseContractDO.getLinkCodeList());
                if (CollUtil.isNotEmpty(skuMap)) {
                    SkuDTO skuDTO = skuMap.get(s.getSkuCode());
                    if (Objects.nonNull(skuDTO)) {
                        s.setSkuName(skuDTO.getName());
                    }
                }
                if (CollUtil.isNotEmpty(custMap) && s.getCustId() != null) {
                    SimpleCustRespDTO simpleCustRespDTO = custMap.get(s.getCustId());
                    if (Objects.nonNull(simpleCustRespDTO)) {
                        s.setCustName(simpleCustRespDTO.getName());
                    }
                }
                // 查找库存写入未出库数量
                Map<String, Integer> stockMap = stockApi.getNotOutStockByPurchaseContractCode(s.getPurchaseContractCode(), List.of(s.getSkuCode()));
                s.setUnOutStockQuantity(stockMap.get(s.getSkuCode()));
                Map<String, Integer> noticedQuantityMap = stockApi.getNoticedQuantityMap(s.getPurchaseContractCode());
                s.setNoticedQuantity(Objects.isNull(noticedQuantityMap.get(s.getUniqueCode())) ? 0 : noticedQuantityMap.get(s.getUniqueCode()));
            });
        }
        return transformNoticeMidRespVOList;
    }

    @Override
    public List<CreatedResponse> batchToStockNotice(List<TransformNoticeMidVO> req) {
        List<CreatedResponse> responses = new ArrayList<>();
        // 过滤明细中本次入库数量大于 0
        req = req.stream().filter(x -> x.getOrderQuantity() > 0).toList();
        if (CollUtil.isEmpty(req)) {
            return responses;
        }
        // 按照 0-供应商，1-公司主体，2-到货时间，3-采购仓库 分组
        Map<String, List<TransformNoticeMidVO>> midMap = req.stream().collect(Collectors.groupingBy(s -> s.getVenderCode() + CommonDict.BASE_SNAKE + s.getCompanyId() + CommonDict.BASE_SNAKE + s.getExpectDate() + CommonDict.BASE_SNAKE + s.getWarehouseId()));
        if (CollUtil.isEmpty(midMap)) {
            return responses;
        }
        log.debug("分组后合同数据：" + JsonUtils.toJsonString(midMap));
        Map<Long, String> warehoseCache = warehouseApi.getWarehoseCache();
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        List<StockNoticeReqVO> batchStockNoticeReqVOList = new ArrayList<>();
        midMap.forEach((k, v) -> {
            List<String> keyList = Arrays.asList(k.split(CommonDict.BASE_SNAKE));
            long companyId = Long.parseLong(keyList.get(1));
            long warehouseId = Long.parseLong(keyList.get(3));
            StockNoticeReqVO stockNoticeReqVO = new StockNoticeReqVO();
            List<StockNoticeItemReqVO> stockNoticeItemReqVOList = PurchaseContractItemConvert.INSTANCE.convertStockNoticeItemList(v);
            stockNoticeReqVO.setExpectDate(LocalDateTime.parse(keyList.get(2)));
            stockNoticeReqVO.setCompanyId(companyId);
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
                if (Objects.nonNull(simpleCompanyDTO)) {
                    stockNoticeReqVO.setCompanyName(simpleCompanyDTO.getName());
                }
            }
            stockNoticeReqVO.setWarehouseId(warehouseId);
            if (CollUtil.isNotEmpty(warehoseCache)) {
                stockNoticeReqVO.setWarehouseName(warehoseCache.get(warehouseId));
            }
            TransformNoticeMidVO transformNoticeMidVO = v.get(0);
            stockNoticeReqVO.setSaleContractId(transformNoticeMidVO.getSaleContractId());
            stockNoticeReqVO.setSaleContractCode(transformNoticeMidVO.getSaleContractCode());
            if (CollUtil.isNotEmpty(stockNoticeItemReqVOList)) {
                stockNoticeItemReqVOList.forEach(s -> {
                    s.setRealBillQuantity(CalculationDict.ZERO);
                    s.setPendingStockQuantity(s.getOrderQuantity());
                    s.setSales(transformNoticeMidVO.getSales());
                });
                // 重新计算箱数,总体积,总毛重
                stockNoticeItemReqVOList.forEach(s->calculateStockNoticeItem(s));
            }
            stockNoticeReqVO.setStockNoticeItemReqVOList(stockNoticeItemReqVOList);
            batchStockNoticeReqVOList.add(stockNoticeReqVO);
        });
        batchStockNoticeReqVOList.forEach(s -> {
            responses.add(toStockNotice(s));
        });
        // 会导致创建重复的入库通知单
//        batchStockNoticeReqVOList.forEach(
//                this::toStockNotice
//        );
        return responses;

    }
    private void calculateStockNoticeItem(StockNoticeItemReqVO stockNoticeItemReqVO) {
        if (Objects.isNull(stockNoticeItemReqVO)){
            return;
        }
        List<JsonSpecificationEntity> specificationList = stockNoticeItemReqVO.getSpecificationList();
        if (CollUtil.isEmpty(specificationList)){
            return;
        }
        Integer orderQuantity = stockNoticeItemReqVO.getOrderQuantity();
        Integer qtyPerOuterbox = stockNoticeItemReqVO.getQtyPerOuterbox();
        // 箱数 数量/外箱装量
        BigDecimal boxQuantity = NumUtil.div(orderQuantity,qtyPerOuterbox).setScale(0,RoundingMode.UP);
        // 总体积
        BigDecimal totalVolume = CalcSpecificationUtil.calcTotalVolumeByBoxCount(specificationList, boxQuantity);
        // 总毛重
        JsonWeight totalGrossweight = CalcSpecificationUtil.calcTotalGrossweightByBoxCount(specificationList, boxQuantity);
        stockNoticeItemReqVO.setOrderBoxQuantity(boxQuantity.intValue());
        stockNoticeItemReqVO.setTotalVolume(totalVolume);
        stockNoticeItemReqVO.setTotalWeight(totalGrossweight);


    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        purchaseContractDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        purchaseContractDO.setContractStatus(PurchaseContractStatusEnum.READY_TO_SUBMIT.getCode());
        int i = purchaseContractMapper.updateById(purchaseContractDO);
        return i > 0;
    }

    @Override
    public List<TransformAuxiliaryMidVO> getToAuxiliaryMid(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return null;
        }
        List<TransformAuxiliaryMidVO> result = new ArrayList<>();
        //采购合同明细
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, ids);
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(SKU_NOT_EXISTS);
        }
        //获取采购合同信息
        List<Long> contractIdList = itemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> doList = purchaseContractMapper.selectList(PurchaseContractDO::getId, contractIdList);
        if (CollUtil.isEmpty(doList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        //获取主体名称
        List<Long> companyIdList = doList.stream().map(PurchaseContractDO::getCompanyId).distinct().toList();
        Map<Long, SimpleCompanyDTO> companyMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isEmpty(companyMap)) {
            throw exception(PURCHASE_COMPANY_EMPTY);
        }

        //辅料配比信息
        List<String> skuCodeList = itemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, List<SkuAuxiliaryDTO>> dtoMap = skuApi.getAuxiliaryListByCodeList(skuCodeList);
        if (CollUtil.isEmpty(dtoMap)) {
            throw exception(SKU_AUXILIARY_NOT_EXISTS);
        }
        //组装数据
        itemDOList.forEach(s -> {
            List<SkuAuxiliaryDTO> dtoList = dtoMap.get(s.getSkuCode());
            if (CollUtil.isEmpty(dtoList)) {
                return;  //客户产品没有配置辅料配比，跳过   如业务修改，该处报错提醒
            }
            dtoList.forEach(dto -> {
                TransformAuxiliaryMidVO vo = BeanUtils.toBean(dto, TransformAuxiliaryMidVO.class);
                vo.setPurchaseContractItemId(s.getId())
                        .setPurchaseContractCode(s.getPurchaseContractCode()).setPurchaseContractId(s.getPurchaseContractId());
                vo.setAuxiliarySkuType(PurchaseAuxiliaryTypeEnum.PRODUCT.getCode());  //产品模式
                vo.setSkuQuantity(s.getQuantity());
                //根据配比计算辅料数量
                if (Objects.nonNull(dto.getSkuRate()) && Objects.nonNull(dto.getAuxiliarySkuRate())
                        && dto.getSkuRate() != 0 && dto.getAuxiliarySkuRate() != 0) {
                    Integer quantity = (int) Math.ceil(s.getQuantity() * dto.getAuxiliarySkuRate() / dto.getSkuRate()); //向上取整
                    vo.setAuxiliaryQuantity(quantity);
                }
                //名称不一致,单独复制
                vo.setAuxiliaryDescription(dto.getDescription()).setAuxiliaryRemark(dto.getRemark());
                vo.setAuxiliarySkuPicture(dto.getAuxiliarySkuPicture()).setAuxiliarySkuUnit(dto.getAuxiliarySkuUnit());
                //主体
                Optional<PurchaseContractDO> first = doList.stream().filter(d -> Objects.equals(d.getId(), s.getPurchaseContractId())).findFirst();
                if (first.isPresent()) {
                    Long companyId = first.get().getCompanyId();
                    vo.setCompanyId(companyId);
                    if (Objects.nonNull(companyId)) {
                        SimpleCompanyDTO simpleCompanyDTO = companyMap.get(companyId);
                        if (Objects.nonNull(simpleCompanyDTO)) {
                            vo.setCompanyName(simpleCompanyDTO.getCompanyName());
                        }
                    }
                }
                result.add(vo);
            });
        });
        return result;

    }

    @Override
    public List<CreatedResponse> batchToSAuxiliary(List<TransformAuxiliaryMidVO> req) {
        List<CreatedResponse> responses = new ArrayList<>();
        if (CollUtil.isEmpty(req)) {
            return responses;
        }
        //采购合同明细详情
        List<Long> itemIdList = req.stream().map(TransformAuxiliaryMidVO::getPurchaseContractItemId).distinct().toList();
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getId, itemIdList);
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        //采购合同明细转采购计划明细
        List<PurchasePlanItemSaveReqVO> planItemVOList = BeanUtils.toBean(itemDOList, PurchasePlanItemSaveReqVO.class);

        List<String> skuCodeList = req.stream().map(TransformAuxiliaryMidVO::getAuxiliarySkuCode).distinct().toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);

        //根据主体聚合
        PurchasePlanInfoSaveReqVO saveInfo = new PurchasePlanInfoSaveReqVO();
        Map<Long, List<TransformAuxiliaryMidVO>> companyMap = req.stream().collect(Collectors.groupingBy(TransformAuxiliaryMidVO::getCompanyId));
        if (CollUtil.isEmpty(companyMap)) {
            throw exception(PURCHASE_COMPANY_NOT_EXISTS);
        }
        saveInfo.setSubmitFlag(BooleanEnum.NO.getValue());  //保存，非提交
        saveInfo.setSourceType(PurchasePlanSourceTypeEnum.PURCHASE_CONTRACT.getCode());  //类型为采购下推
        saveInfo.setAuxiliaryFlag(BooleanEnum.YES.getValue());   //辅料采购标记
        //非必填字段为空，根据业务赋值
        saveInfo.setCustId(null).setCustCode(null).setCustName(null);
        saveInfo.setRemark(null).setAnnex(null).setEstDeliveryDate(null).setPlanDate(null);
        saveInfo.setSaleContractId(null).setSaleContractCode(null);

        //根据采购主体聚合
        for (Map.Entry<Long, List<TransformAuxiliaryMidVO>> entry : companyMap.entrySet()) {
            Long k = entry.getKey();
            List<TransformAuxiliaryMidVO> v = entry.getValue();
            saveInfo.setCompanyId(k);
            if (CollUtil.isNotEmpty(v)) {
                List<PurchasePlanItemSaveReqVO> children = new ArrayList<>();
                int numb = 1;

                //设置默认采购员
                String valueByConfigKey = configApi.getValueByConfigKey("auxiliary.purchase.user.default");
                UserDept userDeptByUserId = adminUserApi.getUserDeptByUserCode(valueByConfigKey);

                //明细组装
                for (TransformAuxiliaryMidVO transformAuxiliaryMidVO : v) {
                    Optional<PurchasePlanItemSaveReqVO> first = planItemVOList.stream().filter(vo -> Objects.equals(vo.getId(), transformAuxiliaryMidVO.getPurchaseContractItemId())).findFirst();
                    if (first.isEmpty()) {
                        continue;
                    }
                    PurchasePlanItemSaveReqVO item = BeanUtils.toBean(first.get(), PurchasePlanItemSaveReqVO.class);

                    item.setSortNum(numb);
                    numb++;
                    item.setId(null).setPurchasePlanId(null).setPurchasePlanCode(null);
                    item.setSaleContractItemId(null).setStockLockSaveReqVOList(null).setLinkCodeList(null);
                    item.setUniqueCode(null).setSourceUniqueCode(null);

                    item.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode()); //标准采购
                    item.setSkuType(SkuTypeEnum.AUXILIARY_MATERIALS.getValue());  //辅料
                    if (CollUtil.isNotEmpty(skuDTOMap)) {
                        SkuDTO skuDTO = skuDTOMap.get(transformAuxiliaryMidVO.getAuxiliarySkuCode());
                        if (Objects.nonNull(skuDTO)) {
                            item.setAuxiliarySkuFlag(skuDTO.getAuxiliarySkuType());
                        }
                    }
                    item.setAuxiliarySkuType(PurchaseAuxiliaryTypeEnum.PRODUCT.getCode()); //产品相关
                    item.setSpecRemark(transformAuxiliaryMidVO.getAuxiliaryDescription()).setRemark(transformAuxiliaryMidVO.getAuxiliaryRemark());  //规格备注
                    item.setAuxiliaryPurchaseContractCode(transformAuxiliaryMidVO.getPurchaseContractCode());  //合同
                    item.setAuxiliarySkuId(transformAuxiliaryMidVO.getSkuId()).setAuxiliarySkuCode(transformAuxiliaryMidVO.getSkuCode());   //产品变关联产品
                    item.setAuxiliaryCskuCode(transformAuxiliaryMidVO.getCskuCode());
                    item.setPurchaseCompanyId(transformAuxiliaryMidVO.getCompanyId()).setPurchaseCompanyName(transformAuxiliaryMidVO.getCompanyName());  //主体
                    item.setAmount(null);
                    item.setSkuId(transformAuxiliaryMidVO.getAuxiliarySkuId()).setSkuCode(transformAuxiliaryMidVO.getAuxiliarySkuCode()).setSkuName(transformAuxiliaryMidVO.getSkuName());  //辅料为产品信息
                    item.setCskuCode(null);
                    item.setMainPicture(transformAuxiliaryMidVO.getAuxiliarySkuPicture());
                    item.setPurchaseType(1);

                    item.setNeedPurQuantity(transformAuxiliaryMidVO.getAuxiliaryQuantity());
                    item.setSaleQuantity(transformAuxiliaryMidVO.getAuxiliaryQuantity());

                    if (Objects.nonNull(userDeptByUserId)) {
                        item.setPurchaseUserId(userDeptByUserId.getUserId()).setPurchaseUserName(userDeptByUserId.getNickname()).setPurchaseUserDeptId(userDeptByUserId.getDeptId());
                        item.setPurchaseUserDeptName(userDeptByUserId.getDeptName());
                    }
                    children.add(item);
                }

                saveInfo.setChildren(children);
                List<CreatedResponse> purchasePlan = purchasePlanService.createPurchasePlan(saveInfo);
                if (CollUtil.isNotEmpty(purchasePlan) && purchasePlan.size() > 0) {
                    responses.add(purchasePlan.get(0));
                }
            }
        }

        itemDOList.forEach(s -> {
            // 补充操作日志明细
            dealOperateLog("【采购合同转入库通知单】,序号：" + s.getSortNum(), s.getPurchaseContractCode());
        });
        return responses;
    }

    /**
     * 校验反审核状态
     *
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        Long count = purchaseContractMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    @Override
    public void updateShipmentDate(List<Long> saleContractItemIdList, List<Integer> dateTypes,LocalDateTime estDepartureTime) {
        if (CollUtil.isEmpty(saleContractItemIdList)) {
            return;
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(new LambdaQueryWrapper<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractCode).in(PurchaseContractItemDO::getSaleContractItemId, saleContractItemIdList));
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return;
        }
        // 如遇性能问题此处后续使用批量更新
        purchaseContractItemDOS.forEach(s -> {
            updatePaymentPlanSignBackTime(s.getPurchaseContractCode(), dateTypes,estDepartureTime);
        });
    }

    @Override
    public List<PurchaseAuxiliaryAllocationDO> getAuxiliaryAllocationList(AuxiliaryAllocationReq req) {
        //已分摊数据数据库查询
        if (req.getAllocationFlag() == 1) {
            return purchaseAuxiliaryAllocationService.getListByPurchaseItemId(req.getAuxiliaryContractItemId());
        }
        //未分摊的数据组装
        else {
            List<PurchaseAuxiliaryAllocationDO> doList = new ArrayList<>();
            List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, req.getContractId());
            if (CollUtil.isEmpty(purchaseContractItemDOS)) {
                return null;
            }
            Optional<PurchaseContractItemDO> first = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, req.getAuxiliaryContractItemId()).stream().findFirst();
            List<String> skuCodeList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
            Map<String, SkuDTO> skuMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
            List<String> auxiliarySkuCodeList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getAuxiliarySkuCode).distinct().toList();
            Map<String, SkuDTO> auxiliarySkuMap = skuApi.getSkuDTOMapByCodeList(auxiliarySkuCodeList);

            purchaseContractItemDOS.forEach(s -> {
                PurchaseAuxiliaryAllocationDO allocation = new PurchaseAuxiliaryAllocationDO();
                allocation.setPurchaseContractId(s.getPurchaseContractId()).setPurchaseContractCode(s.getPurchaseContractCode());
                allocation.setPurchaseContractItemId(s.getId());
                allocation.setSkuCode(s.getSkuCode());
                SkuDTO skuDTO = skuMap.get(s.getSkuCode());
                if (Objects.nonNull(skuDTO)) {
                    allocation.setSkuName(skuDTO.getName());
                }
                allocation.setCskuCode(s.getCskuCode());
                first.ifPresent(purchaseContractItemDO -> {
                    allocation.setAuxiliaryPurchaseContractId(purchaseContractItemDO.getPurchaseContractId());
                    allocation.setAuxiliaryPurchaseContractCode(purchaseContractItemDO.getPurchaseContractCode());
                    allocation.setAuxiliaryPurchaseContractItemId(purchaseContractItemDO.getId());
                    allocation.setAuxiliarySkuCode(purchaseContractItemDO.getSkuCode());
                    SkuDTO auxiliarySkuDTO = auxiliarySkuMap.get(purchaseContractItemDO.getSkuCode());
                    if (Objects.nonNull(auxiliarySkuDTO)) {
                        allocation.setAuxiliarySkuName(auxiliarySkuDTO.getName());
                    }
                });

                allocation.setQuantity(s.getQuantity());
                doList.add(allocation);
            });
            return doList;
        }
    }

    @Override
    public void updateAuxiliaryAllocationList(PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO) {
        purchaseAuxiliaryAllocationService.updatePurchaseAuxiliaryAllocationAllocation(updateReqVO);
    }

    @Override
    public void deleteAuxiliaryAllocationList(Long itemId) {
        purchaseAuxiliaryAllocationService.deletePurchaseAuxiliaryAllocationByItemId(itemId);

    }

    @Override
    @Deprecated
    public void updateRealTaxPriceAndManager(Long id) {
        PurchaseContractDO purchaseContractDo = purchaseContractMapper.selectById(id);
        UserDept manager = purchaseContractDo.getManager();
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, id);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return;
        }
        Map<String, JsonAmount> withTaxPriceMap = purchaseContractItemDOList.stream().filter(s -> StrUtil.isNotEmpty(s.getSaleItemUniqueCode())).collect(Collectors.toMap(PurchaseContractItemDO::getSaleItemUniqueCode, PurchaseContractItemDO::getWithTaxPrice));
        if (CollUtil.isEmpty(withTaxPriceMap)) {
            return;
        }
        // 根据来源编号批量更新销售明细真实含税总价
        saleContractApi.updateItemWithTaxPriceAndManager(withTaxPriceMap, manager);
    }

    @Override
    public List<AuxiliaryPurchaseContractDTO> getAuxiliaryAllocationListByContractCode(String code) {
        return getAuxiliaryPurchaseAllocations(code);
    }

    @Override
    public Map<String, String> getSaleCustPOByPurchaseCodes(List<String> purchaseContractCodeList) {
        if (CollUtil.isEmpty(purchaseContractCodeList)) {
            return null;
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(PurchaseContractDO::getCode, purchaseContractCodeList);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return null;
        }
        List<String> saleCodeList = purchaseContractDOS.stream().map(PurchaseContractDO::getSaleContractCode).distinct().toList();
        Map<String, SmsContractAllDTO> smsMap = saleContractApi.getSmsByCodeList(saleCodeList);
        if (CollUtil.isEmpty(smsMap)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        purchaseContractDOS.forEach(s -> {
            SmsContractAllDTO smsContractAllDTO = smsMap.get(s.getSaleContractCode());
            if (Objects.nonNull(smsContractAllDTO)) {
                map.put(s.getCode(), smsContractAllDTO.getCustPo());
            }
        });
        return map;
    }

    @Override
    public void updatePurchaseAmount(String businessCode, BigDecimal amount) {
        Optional<PurchaseContractDO> first = purchaseContractMapper.selectList(PurchaseContractDO::getCode, businessCode).stream().findFirst();
        if (first.isEmpty()) {
            return;
        }
        PurchaseContractDO purchaseContractDO = first.get();
        if (Objects.isNull(purchaseContractDO.getTotalAmount())) {
            throw exception(PURCHASE_AMOUNT_ERROR);
        }
        if (Objects.isNull(amount)) {
            throw exception(PAYMENT_AMOUNT_ERROR);
        }
        if (Objects.isNull(purchaseContractDO.getPayedAmount())) {
            purchaseContractDO.setPayedAmount(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(purchaseContractDO.getTotalAmount().getCurrency()));
        }
        BigDecimal total = amount.add(purchaseContractDO.getPayedAmount().getAmount());
        if (purchaseContractDO.getTotalAmount().getAmount().setScale(2, RoundingMode.HALF_UP).compareTo(total.setScale(2, RoundingMode.HALF_UP)) < 0) {
            throw exception(PAYMENT_AMOUNT_PAYMENT_ERROR);
        }
        purchaseContractDO.setPayedAmount(purchaseContractDO.getPayedAmount().setAmount(total));
        purchaseContractMapper.updateById(purchaseContractDO);
    }

    public JsonCompanyPath getLastCompanyPath(JsonCompanyPath companyPath) {
        return Objects.isNull(companyPath.getNext()) ? companyPath : getLastCompanyPath(companyPath.getNext());
    }

    @Override
    public InvoiceNoticeVO toInvoiceNoticedMid(Long id) {
        PurchaseContractDO purchaseContractDO = validatePurchaseContractExists(id);
        InvoiceNoticeVO vo = new InvoiceNoticeVO();
        vo.setLinkCodeList(purchaseContractDO.getLinkCodeList());
        Long companyId = purchaseContractDO.getCompanyId();
        SimpleCompanyDTO simpleCompanyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.nonNull(simpleCompanyDTO)) {
            vo.setCompanyId(simpleCompanyDTO.getId()).setCompanyName(simpleCompanyDTO.getCompanyName());
        }
        String venderCode = purchaseContractDO.getVenderCode();
        vo.setVenderId(purchaseContractDO.getVenderId()).setVenderCode(venderCode);
        vo.setPurOrderCode(purchaseContractDO.getCode());
        Map<String, String> venderNameCache = venderApi.getVenderNameCache(venderCode);
        if (Objects.nonNull(venderNameCache)) {
            vo.setVenderName(venderNameCache.get(venderCode));
        }
        vo.setManagerId(purchaseContractDO.getManager().getUserId()).setManagerName(purchaseContractDO.getManager().getNickname());
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, id));
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(PURCHASE_ITEM_NOT_EXISTS);
        }
        boolean issuedFlag = itemDOList.stream().allMatch(s -> Objects.equals(s.getInvoiceStatus(), InvoiceStatusEnum.ISSUED.getValue()));
        if (issuedFlag){
            throw exception(PURCHASE_ITEM_ALL_INVOICE);
        }
        itemDOList = itemDOList.stream().filter(s -> !Objects.equals(s.getInvoiceStatus(), InvoiceStatusEnum.ISSUED.getValue())).toList();
        List<InvoiceNoticeItemVO> children = new ArrayList<>();
        List<String> skuCodeList = itemDOList.stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);

        List<Long> custIdList = itemDOList.stream().map(PurchaseContractItemDO::getCustId).distinct().toList();
        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(custIdList);
        List<Long> itemIdList = itemDOList.stream().map(PurchaseContractItemDO::getId).distinct().toList();
        Map<Long, Integer> invoicedQuantity = invoicingNoticesService.getInvoiceQuantityByPurchaseItemIds(itemIdList);
        itemDOList.forEach(s -> {
            Integer invoiceStatus = s.getInvoiceStatus();
            InvoiceNoticeItemVO item = new InvoiceNoticeItemVO();
            item.setPurchaseContractCode(purchaseContractDO.getCode());
            item.setSkuId(s.getSkuId()).setSkuCode(s.getSkuCode()).setCskuCode(s.getCskuCode()).setBasicSkuCode(s.getBasicSkuCode());
            if (CollUtil.isNotEmpty(skuDTOMap)) {
                SkuDTO skuDTO = skuDTOMap.get(s.getSkuCode());
                if (Objects.nonNull(skuDTO)) {
                    item.setSkuName(skuDTO.getName()).setSkuNoticeName(skuDTO.getName());
                }
            }
            if (CollUtil.isNotEmpty(hsMap)) {
                HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                if (Objects.nonNull(hsDataDTO)) {
                    item.setHsCode(hsDataDTO.getCode()).setHsMeasureUnit(hsDataDTO.getUnit());
                }
            }
            item.setPurchaseTotalQuantity(s.getQuantity());
            JsonAmount withTaxPrice = s.getWithTaxPrice();
            item.setPurchaseWithTaxPrice(withTaxPrice).setPurchasePrice(s.getWithTaxPrice()).setNoticePrice(s.getWithTaxPrice()).setNoticeAmount(new JsonAmount().setAmount(NumUtil.mul(withTaxPrice.getAmount(), s.getQuantity())).setCurrency(withTaxPrice.getCurrency()));
            item.setPurchaseCurrency(s.getWithTaxPrice().getCurrency());
            item.setCustId(s.getCustId()).setCustCode(s.getCustCode());
            if (CollUtil.isNotEmpty(simpleCustRespDTOMap)) {
                SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(s.getCustId());
                if (Objects.nonNull(simpleCustRespDTO)) {
                    item.setCustName(simpleCustRespDTO.getName());
                }
            }
            item.setSaleContractCode(s.getSaleContractCode()).setSaleContractItemId(s.getSaleContractItemId());
            item.setPurchaseContractItemId(s.getId()).setPurchaseSortNum(s.getSortNum());
            item.setSourceUniqueCode(s.getUniqueCode());
            item.setInvoiceStatus(s.getInvoiceStatus());
            item.setInvoicedQuantity(invoicedQuantity.get(s.getId()));
            item.setSkuUnit(s.getSkuUnit());
            item.setQtyPerOuterbox(s.getQtyPerOuterbox());
            item.setSpecificationList(s.getSpecificationList());
            // 转换开票通知数量单位
//            transformInvoiceQuantity(item,s);
            children.add(item);
        });
        vo.setChildren(children);
        return vo;
    }

    @Override
    @Deprecated
    public List<CreatedResponse> toInvoiceNoticed(InvoiceNoticeVO req) {
        InvoicingNoticesSaveReqVO reqVO = new InvoicingNoticesSaveReqVO();
        reqVO = BeanUtils.toBean(req, InvoicingNoticesSaveReqVO.class);
        reqVO.setManuallyFlag(1);
        reqVO.setSubmitFlag(BooleanEnum.NO.getValue()); // 采购合同转开票通知设置为不提交，保持待提交状态
        reqVO.setSourceType(InvoiceNoticeSourceTypeEnum.PURCHASE.getValue());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept inputUser = adminUserApi.getUserDeptByUserId(loginUserId);
        reqVO.setInputUser(inputUser);
        if (CollUtil.isNotEmpty(req.getChildren())) {
            List<InvoicingNoticesItem> items = req.getChildren().stream().map(s -> {
                InvoicingNoticesItem item = BeanUtils.toBean(s, InvoicingNoticesItem.class);
                item.setManuallyFlag(1);
                item.setTotalAmount(NumUtil.mul(s.getNoticePrice().getAmount(), s.getInvoicNoticesQuantity()));
                item.setInvoicSkuName(s.getSkuNoticeName());
                item.setInvoicPrice(s.getNoticePrice().getAmount());
                item.setSourceUniqueCode(s.getSourceUniqueCode());
                item.setSkuName(s.getSkuName());
                item.setHsMeasureUnit(s.getHsMeasureUnit());
                // 采购转开票通知 出运数量默认采购数量
                item.setDeclarationQuantity(s.getInvoicedQuantity());
                return item;
            }).toList();
            reqVO.setChildren(items);
        }
        return invoicingNoticesService.createInvoicingNotices(reqVO);
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, PurchaseContractItemDTO> getPurchaseContractMap(List<Long> saleItemIdList, Map<Long, Long> lastCompanyPathMap) {
        if (CollUtil.isEmpty(lastCompanyPathMap)) {
            return Map.of();
        }
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getSaleContractItemId, saleItemIdList);
        if (CollUtil.isEmpty(itemDOList)) {
            return Map.of();
        }
        List<PurchaseContractItemDTO> dtoList = itemDOList.stream().map(s -> {
            PurchaseContractItemDTO itemDTO = BeanUtils.toBean(s, PurchaseContractItemDTO.class);
            itemDTO.setPurchaseCurrency(s.getCurrency());
            return itemDTO;
        }).toList();
        List<String> codeList = dtoList.stream().map(PurchaseContractItemDTO::getPurchaseContractCode).distinct().toList();
        List<PurchaseContractDO> doList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getCode, codeList).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        List<PurchaseContractItemDTO> result = new Stack<>();
        List<String> venderCodeList = doList.stream().map(PurchaseContractDO::getVenderCode).distinct().toList();
        List<String> contractCodeList = doList.stream().map(PurchaseContractDO::getCode).distinct().toList();
        Map<String, SimpleVenderResp> venderMap = venderApi.getSimpleVenderMapByCodes(venderCodeList);
        if (CollUtil.isNotEmpty(doList)) {
            dtoList.stream().filter(s -> contractCodeList.contains(s.getPurchaseContractCode())).forEach(s -> {
                Optional<PurchaseContractDO> first1 = doList.stream().filter(d -> Objects.equals(d.getCode(), s.getPurchaseContractCode())).findFirst();
                if (first1.isEmpty()) {
                    throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
                }
                PurchaseContractDO purchaseContractDO = first1.get();
                if (Objects.equals(purchaseContractDO.getCompanyId(), lastCompanyPathMap.get(s.getSaleContractItemId()))) {
                    s.setPurchaseUserId(purchaseContractDO.getPurchaseUserId());
                    s.setPurchaseUserName(purchaseContractDO.getPurchaseUserName());
                    s.setPurchaseUserDeptId(purchaseContractDO.getPurchaseUserDeptId());
                    s.setPurchaseUserDeptName(purchaseContractDO.getPurchaseUserDeptName());
                    s.setPayVenderId(purchaseContractDO.getPaymentVenderId());
                    s.setPayVenderCode(purchaseContractDO.getPaymentVenderCode());
                    s.setPayVenderName(purchaseContractDO.getPaymentVenderName());
                    s.setDeliveryDate(purchaseContractDO.getDeliveryDate());
                    if (CollUtil.isNotEmpty(venderMap)) {
                        SimpleVenderResp simpleVenderResp = venderMap.get(s.getVenderCode());
                        if (Objects.nonNull(simpleVenderResp)) {
                            s.setVenderName(simpleVenderResp.getName());
                        }
                    }
                    result.add(s);
                }
            });
        }
        return result.stream().collect(Collectors.toMap(PurchaseContractItemDTO::getSaleContractItemId, s -> s, (s1, s2) -> s1));
    }

    @Override
    public Map<Long, String> getPurchaseContractCodeBySaleItemIdList(List<Long> saleItemIdList, Long companyId) {
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractCode, PurchaseContractItemDO::getSaleContractItemId).in(PurchaseContractItemDO::getSaleContractItemId, saleItemIdList));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        List<String> contractCodeList = itemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractCode).distinct().toList();
        if (CollUtil.isEmpty(contractCodeList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode).in(PurchaseContractDO::getCode, contractCodeList).eq(PurchaseContractDO::getCompanyId, companyId));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        return itemDOList.stream().filter(s -> StrUtil.isNotEmpty(s.getPurchaseContractCode()) && purchaseContractDOList.stream().anyMatch(d -> StrUtil.equals(d.getCode(), s.getPurchaseContractCode()))).collect(Collectors.toMap(PurchaseContractItemDO::getSaleContractItemId, PurchaseContractItemDO::getPurchaseContractCode, (o, n) -> o));
    }

    @Override
    public Map<String, List<String>> getLinkCodeMapByCodeList(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getCode, PurchaseContractDO::getLinkCodeList).in(PurchaseContractDO::getCode, codeList));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return Map.of();
        }
        return purchaseContractDOList.stream().collect(Collectors.toMap(PurchaseContractDO::getCode, PurchaseContractDO::getLinkCodeList, (o, n) -> n));
    }

    @Override
    public void updateCheckStatus(String purchaseContractCode, Integer value) {
        Optional<PurchaseContractDO> first = purchaseContractMapper.selectList(PurchaseContractDO::getCode, purchaseContractCode).stream().findFirst();
        if (first.isEmpty()) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        PurchaseContractDO purchaseContractDO = first.get();
        purchaseContractDO.setCheckStatus(value);
        purchaseContractMapper.updateById(purchaseContractDO);
    }

    @Override
    public ChangeEffectRespVO getChangeEffect(PurchaseContractInfoRespVO changeReqVO) {
        //查询新采购合同与原采购合同
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectOne(PurchaseContractDO::getCode, changeReqVO.getCode());
        PurchaseContractInfoRespVO purchaseContract = getPurchaseContract(new PurchaseContractDetailReq().setPurchaseContractId(purchaseContractDO.getId()));
        //初始化变更标记
        final boolean[] changeFlag = {false, false, false};
        Integer submitFlag = BooleanEnum.NO.getValue();
        ChangeEffectRespVO changeEffectRespVO = new ChangeEffectRespVO();
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("scm_purchase_contract", "scm_purchase_contract_item", "system_payment_item", "scm_add_sub_term"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }

        //采购主表
        Set<String> changeFields = new ChangeCompareUtil<PurchaseContractInfoRespVO>().transformObject(purchaseContract, changeReqVO);
        compareTableField(changeFields, formChangeDTOList.get("scm_purchase_contract"), changeFlag, submitFlag);

        //采购子表
        List<PurchaseContractItemInfoRespVO> oldCustBankaccountList = purchaseContract.getChildren();
        List<PurchaseContractItemInfoRespVO> custBankaccountList = changeReqVO.getChildren();
        List<DiffRecord<PurchaseContractItemInfoRespVO>> itemDiffRecords = DiffUtil.compareLists(oldCustBankaccountList, custBankaccountList);
        Tuple itemTuple = new ChangeCompareUtil<PurchaseContractItemInfoRespVO>().transformChildList(itemDiffRecords, PurchaseContractItemInfoRespVO.class);
        compareTableField(itemTuple.get(1), formChangeDTOList.get("scm_purchase_contract_item"), changeFlag, submitFlag);

        //付款方式
        List<PaymentItemDTO> oldCustPocList = purchaseContract.getPaymentList();
        List<PaymentItemDTO> paymentItemList = changeReqVO.getPaymentList();
        List<DiffRecord<PaymentItemDTO>> paymentItemDiffRecords = DiffUtil.compareLists(oldCustPocList, paymentItemList);
        Tuple paymentItemTuple = new ChangeCompareUtil<PaymentItemDTO>().transformChildList(paymentItemDiffRecords, PaymentItemDTO.class);
        compareTableField(paymentItemTuple.get(1), formChangeDTOList.get("system_payment_item"), changeFlag, submitFlag);

        //加减项
        List<PurchaseAddSubTerm> oldCustSettlementList = purchaseContract.getPurchaseAddSubTermList();
        List<PurchaseAddSubTerm> custSettlementList = changeReqVO.getPurchaseAddSubTermList();
        List<DiffRecord<PurchaseAddSubTerm>> addSubTermDiffRecords = DiffUtil.compareLists(oldCustSettlementList, custSettlementList);
        Tuple addSubTermTuple = new ChangeCompareUtil<PurchaseAddSubTerm>().transformChildList(addSubTermDiffRecords, PurchaseAddSubTerm.class);
        compareTableField(addSubTermTuple.get(1), formChangeDTOList.get("scm_add_sub_term"), changeFlag, submitFlag);

        // 处理影响范围
        List<JsonEffectRange> effectRangeList = new ArrayList<>();
        //更新该客户未完成的所有包材采购为未确认
        if (changeFlag[0]) {
            List<String> auxiliaryContracts = this.getAuxiliaryPurchaseCodeByPurchaseContractCode(purchaseContractDO.getCode());
            if (CollUtil.isNotEmpty(auxiliaryContracts)) {
                auxiliaryContracts.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s).setEffectRangeType(EffectRangeEnum.AUXILIARY_PURCHASE.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //更新该客户未完成的所有出运明细为未确认
        List<ShipmentDTO> shipments = new ArrayList<>();
        if (changeFlag[2]) {
            shipments = shipmentApi.getUnShippedDTOBySaleContractCode(changeReqVO.getSaleContractCode());
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

    private List<String> getAuxiliaryPurchaseCodeByPurchaseContractCode(String purchaseCode) {
        if (StrUtil.isEmpty(purchaseCode)) {
            return List.of();
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode, purchaseCode);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return List.of();
        }
        return purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractCode).toList();
    }

    @Override
    public Collection<Long> validatePurContractItemExists(Collection<Long> saleContractItemIds) {
        if (CollUtil.isEmpty(saleContractItemIds)) {
            return List.of();
        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getSaleContractItemId, PurchaseContractItemDO::getPurchaseContractId).in(PurchaseContractItemDO::getSaleContractItemId, saleContractItemIds));
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return List.of();
        }
        List<Long> contractIdList = purchaseContractItemDOS.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getId).in(PurchaseContractDO::getId, contractIdList).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return List.of();
        }
        // 未作废的合同
        List<Long> unFinishIdList = purchaseContractDOS.stream().map(PurchaseContractDO::getId).distinct().toList();
        return purchaseContractItemDOS.stream().filter(s -> unFinishIdList.contains(s.getPurchaseContractId())).map(PurchaseContractItemDO::getSaleContractItemId).distinct().toList();
    }

    private void compareTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag, Integer submitFlag) {
        if (formChange != null) {
            //影响包材采购的字段
            List<FormChangeItemDTO> auxilaryPurchaseItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.AUXILIARY_PURCHASE.getValue())).toList();
            //影响出运的字段
            List<FormChangeItemDTO> dmsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.DMS.getValue())).toList();
            auxilaryPurchaseItems.forEach(s -> {
                if (changeFieldNames.contains(s.getNameEng())) {
                    changeFlag[0] = true;
                }
            });
            dmsItems.forEach(s -> {
                if (changeFieldNames.contains(s.getNameEng())) {
                    changeFlag[2] = true;
                }
            });
            boolean isSubmitFlag = formChange.getChildren().stream().anyMatch(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel()));
            if (isSubmitFlag) {
                submitFlag = BooleanEnum.YES.getValue();
            }
        }
    }

    private void deleteAutoPlchaseContract(List<String> saleContractCodeList) {
        List<PurchaseContractDO> purchaseContractDOList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getSaleContractCode, saleContractCodeList).eq(PurchaseContractDO::getAutoFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(purchaseContractDOList)) {
            return;
        }
        List<Long> purchaseContractIdList = purchaseContractDOList.stream().map(PurchaseContractDO::getId).distinct().toList();
        purchaseContractItemMapper.delete(new LambdaQueryWrapperX<PurchaseContractItemDO>().in(PurchaseContractItemDO::getPurchaseContractId, purchaseContractIdList));
        purchaseContractMapper.delete(new LambdaQueryWrapperX<PurchaseContractDO>().in(PurchaseContractDO::getSaleContractCode, saleContractCodeList).eq(PurchaseContractDO::getAutoFlag, BooleanEnum.YES.getValue()));

    }

    @Override
    public List<Long> getPurchaseContractIdsBySaleContractCode(String saleContractCode) {
        List<PurchaseContractDO> purchaseContractList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().eq(PurchaseContractDO::getSaleContractCode, saleContractCode).notIn(PurchaseContractDO::getContractStatus, List.of(PurchaseContractStatusEnum.CASE_SETTLED.getCode(), PurchaseContractStatusEnum.FINISHED.getCode())));
        if (purchaseContractList == null) {
            return null;
        }
        List<Long> purchaseContractIds = purchaseContractList.stream().map(PurchaseContractDO::getId).distinct().toList();
        return purchaseContractIds;
    }

    @Override
    public Map<Long, Map<String, Integer>> getItemQuantityMapBySaleItemIds(List<Long> saleItemIds) {
        if (CollUtil.isEmpty(saleItemIds)) {
            return Map.of();
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemMapper.selectList(new LambdaQueryWrapperX<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractId, PurchaseContractItemDO::getSaleContractItemId, PurchaseContractItemDO::getSkuCode, PurchaseContractItemDO::getQuantity)
                .in(PurchaseContractItemDO::getSaleContractItemId, saleItemIds));
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            return Map.of();
        }
        List<Long> purchaseContractIds = purchaseContractItemDOList.stream().map(PurchaseContractItemDO::getPurchaseContractId).distinct().toList();
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectBatchIds(purchaseContractIds);
        List<Long> autoPurchaseContractIds = purchaseContractDOS.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getAutoFlag()) || PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(s.getContractStatus())).map(PurchaseContractDO::getId).distinct().toList();
        return purchaseContractItemDOList.stream().filter(s -> !autoPurchaseContractIds.contains(s.getPurchaseContractId())).collect(Collectors.groupingBy(
                PurchaseContractItemDO::getSaleContractItemId,
                Collectors.toMap(
                        PurchaseContractItemDO::getSkuCode,
                        PurchaseContractItemDO::getQuantity,
                        Integer::sum)));
    }

    @Override
    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        ReportDTO reportDTO = null;
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
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (purchaseContractDO == null) {
            throw exception(PURCHASECONTRACT_NULL);
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(id);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASECONTRACTITEM_NULL);
        }
        HashMap<String, Object> params = getExcelParams(purchaseContractDO,purchaseContractItemDOList);
        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content = null;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path" + path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "采购合同.xlsx", null, null, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    @Override
    public void exportWord(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId, HttpServletResponse response) {
        if (id == null) {
            throw exception(PURCHASE_CONTRACT_ID_NULL);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        ReportDTO reportDTO;
        if (sourceCode != null && sourceType != null && companyId != null) {
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
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (purchaseContractDO == null) {
            throw exception(PURCHASECONTRACT_NULL);
        }
        List<PurchaseContractItemDO> purchaseContractItemDOList = purchaseContractItemService.getPurchaseContractItemListByPurchaseContractId(id);
        if (CollUtil.isEmpty(purchaseContractItemDOList)) {
            throw exception(PURCHASECONTRACTITEM_NULL);
        }
        HashMap<String, Object> params = getWordParams(purchaseContractDO, purchaseContractItemDOList);
        reportApi.exportWord(response, reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params);
    }

    @Override
    public void updateInvoiceData(List<InvoiceQuantityDTO> invoiceQuantityDTOList,boolean closeFlag) {
        if (CollUtil.isEmpty(invoiceQuantityDTOList)) {
            return;
        }
        Map<Long, InvoiceQuantityDTO> invoiceQuantityMap = invoiceQuantityDTOList.stream().collect(Collectors.toMap(InvoiceQuantityDTO::getSaleContractItemId, s -> s, (o, n) -> o));
        Set<String> purchaseContractCodes = invoiceQuantityDTOList.stream().map(InvoiceQuantityDTO::getPurchaseContractCode).collect(Collectors.toSet());
        // 校验是否拆分采购
//        boolean isSplit = purchasePlanService.validatePurContractItemIsSplit(saleItemIds);
//        if (isSplit){
//            throw exception(PURCHASE_CONTRACT_ITEM_IS_SPLIT);
//        }
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractCode, purchaseContractCodes);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
        }
        purchaseContractItemDOS.forEach(s -> {
            Long saleContractItemId = s.getSaleContractItemId();
            InvoiceQuantityDTO invoiceQuantityDTO = invoiceQuantityMap.get(saleContractItemId);
            if (Objects.isNull(invoiceQuantityDTO)) {
                return;
            }
            if (!invoiceQuantityDTO.getVenderCode().equals(s.getVenderCode())) {
                return;
            }
            // skuCode 校验：如果 DTO 中 skuCode 为空（来自出运作废场景），则跳过此校验
            if (StrUtil.isNotEmpty(invoiceQuantityDTO.getSkuCode()) && !s.getSkuCode().equals(invoiceQuantityDTO.getSkuCode())){
                return;
            }
            String purchaseContractCode = invoiceQuantityDTO.getPurchaseContractCode();
            if (StrUtil.isEmpty(purchaseContractCode) || !purchaseContractCode.equals(s.getPurchaseContractCode())) {
                return;
            }
            // 已开票数量
            Integer invoicedQuantity = Objects.isNull(s.getInvoicedQuantity()) ? CalculationDict.ZERO : s.getInvoicedQuantity();
            // 采购数量
            Integer quantity = Objects.isNull(s.getQuantity()) ? CalculationDict.ZERO : s.getQuantity();
            // 未开票数量
            Integer unInvoicedQuantity = quantity - invoicedQuantity;
//            Integer thisInvoiceQuantity = invoiceQuantityDTO.getInvoiceQuantity().intValue();
//            if (closeFlag){、
//                invoicedQuantity =invoicedQuantity - thisInvoiceQuantity;
//            }else {
//                invoicedQuantity =invoicedQuantity + thisInvoiceQuantity;
//            }
            s.setInvoicedQuantity(invoicedQuantity);
            if (quantity > invoicedQuantity&&invoicedQuantity > 0) {
                s.setInvoiceStatus(InvoiceStatusEnum.PART_ISSUED.getValue());
            } else if (quantity.equals(invoicedQuantity)) {
                s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
            } else if (invoicedQuantity == 0){
                s.setInvoiceStatus(InvoiceStatusEnum.NOT_ISSUED.getValue());
            }else {
                throw exception(INVOICE_QUANTITY_NOT_ENOUGH, unInvoicedQuantity);
            }
        });
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOS);
    }

    @Override
    public void setOrderDone(Long id) {
        PurchaseContractDO purchaseContractDO = purchaseContractMapper.selectById(id);
        if (Objects.isNull(purchaseContractDO)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        purchaseContractDO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
        purchaseContractMapper.updateById(purchaseContractDO);
    }

    @Override
    public void updateChangeStatus(Long id, Integer status) {
        PurchaseContractChange purchaseContractChange = validateChangePurchaseContractExists(id);
        String sourceCode = purchaseContractChange.getSourceCode();
        PurchaseContractDO purchaseContractDO = new PurchaseContractDO().setChangeStatus(status);
        purchaseContractMapper.update(purchaseContractDO, new LambdaUpdateWrapper<PurchaseContractDO>().eq(PurchaseContractDO::getCode, sourceCode));
    }

    @Override
    public void updateShipmentCheckStatus(Map<Long, Integer> contractItemMap) {
        // 更新出运明细验货状态
        Map<Long, Map<String, Integer>> updateMap = getCheckStatusMap(contractItemMap.keySet());
        if (CollUtil.isNotEmpty(updateMap)) {
            shipmentApi.updateCheckStatus(updateMap);
        }
    }

    @Override
    public Map<Long, Map<String, Integer>> getCheckStatusMap(Set<Long> saleContractItemIds) {
        if (CollUtil.isEmpty(saleContractItemIds)) {
            return Map.of();
        }
        // 更新出运明细验货状态
        List<PurchaseContractItemDO> itemDOList = purchaseContractItemService.getItemDOListByIds(saleContractItemIds);
        if (CollUtil.isEmpty(itemDOList)) {
            return Map.of();
        }
        return itemDOList.stream().collect(Collectors.groupingBy(
                PurchaseContractItemDO::getSaleContractItemId,
                Collectors.toMap(
                        PurchaseContractItemDO::getSkuCode,
                        PurchaseContractItemDO::getCheckStatus, (o, n) -> o)));
    }

    @Override
    public List<PurchasePaymentPlan> getPurchasePaymentPlanListByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return List.of();
        }
        return purchasePaymentPlanMapper.selectList(PurchasePaymentPlan::getContractCode, code);
    }

    @Override
    public void updatePurchaseInspectionData(Map<Long, UpdateInspectionDTO> updateInspectionDTOMap) {
        if (CollUtil.isEmpty(updateInspectionDTOMap)) {
            return;
        }
        Set<Long> purchaseItemIdSet = updateInspectionDTOMap.keySet();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectBatchIds(purchaseItemIdSet);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return;
        }
        List<Long> updatePaymentDate = new ArrayList<>();
        purchaseContractItemDOS.forEach(s -> {
            UpdateInspectionDTO updateInspectionDTO = updateInspectionDTOMap.get(s.getId());
            if (Objects.isNull(updateInspectionDTO)) {
                return;
            }
            s.setHandleFlag(updateInspectionDTO.getHandleFlag());
            s.setCheckStatus(updateInspectionDTO.getCheckStatus());
            s.setCheckedQuantity(updateInspectionDTO.getQuantity());
            s.setInspectionTime(updateInspectionDTO.getInspectionTime());
            if (InspectionItemStatusEnum.SUCESS.getValue().equals(s.getCheckStatus())) {
                updatePaymentDate.add(s.getSaleContractItemId());
            }
        });
        purchaseContractItemMapper.updateBatch(purchaseContractItemDOS);
        if (CollUtil.isNotEmpty(updatePaymentDate)) {
            updatePaymentDate.forEach(s -> {
                // 更新采购合同付款方式为验货日日期
                List<Integer> dateTypes = new ArrayList<>();
                dateTypes.add(DateTypeEnum.INSPECTION_DATE.getValue());
                dateTypes.add(DateTypeEnum.INSPECTION_TICKET_DATE.getValue());
                List<Long> saleContractItemIds = new ArrayList<>();
                saleContractItemIds.add(s);
                updateShipmentDate(saleContractItemIds, dateTypes,LocalDateTime.now());
            });
        }
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, List<SimplePurchaseContractItemDTO>> getSimplePurchaseContractItemMap(List<String> saleColeList) {
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapper<PurchaseContractDO>().select(PurchaseContractDO::getId, PurchaseContractDO::getVenderName, PurchaseContractDO::getCurrency).in(PurchaseContractDO::getSaleContractCode, saleColeList).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return Map.of();
        }
        Map<Long, PurchaseContractDO> contractDOMap = purchaseContractDOS.stream().collect(Collectors.toMap(PurchaseContractDO::getId, s -> s, (o, n) -> o));
        List<Long> purchaseContractIdList = purchaseContractDOS.stream().map(PurchaseContractDO::getId).distinct().toList();
        List<PurchaseContractItemDO> purchaseContractItemDOS = purchaseContractItemMapper.selectList(PurchaseContractItemDO::getPurchaseContractId, purchaseContractIdList);
        if (CollUtil.isEmpty(purchaseContractItemDOS)) {
            return Map.of();
        }
        List<SimplePurchaseContractItemDTO> simplePurchaseContractItemDTOS = PurchaseContractConvert.INSTANCE.convertSimplePurchaseContractItemDTOList(purchaseContractItemDOS, contractDOMap);
        return simplePurchaseContractItemDTOS.stream().collect(Collectors.groupingBy(SimplePurchaseContractItemDTO::getSaleContractItemId));
    }

    @Override
    public void updateAuxiliaryPaymentStatusByCodes(List<String> relationCode, Integer value) {
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(PurchaseContractDO::getCode, relationCode);
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        purchaseContractDOS.forEach(s -> {
            if (s.getAuxiliaryFlag() == 0) {
                throw exception(PURCHASE_CONTRACT_IS_NOT_AUXILIARY);
            }
            s.setAuxiliaryPaymentFlag(value);
        });
        purchaseContractMapper.updateBatch(purchaseContractDOS);
    }

    @Override
    public List<FeeShareResp> getListByCodeList(String codeList) {
        List<String> list = Arrays.stream(codeList.split(",")).toList();
        List<PurchaseContractDO> doList = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().inIfPresent(PurchaseContractDO::getCode, list)).stream().toList();

        if (CollUtil.isNotEmpty(doList)) {
            List<FeeShareResp> feeShareResps = new ArrayList<>();

            List<String> codes = doList.stream().map(PurchaseContractDO::getCode).distinct().toList();
            Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.AUXILIARY_PURCHASE.getValue(), codes);
            if (CollUtil.isNotEmpty(feeShareMap)) {
                doList.forEach(s -> {
                    List<FeeShareRespDTO> feeShareRespDTOS = feeShareMap.get(s.getCode());
                    FeeShareResp feeShareResp = new FeeShareResp();
                    feeShareResp.setCode(s.getCode());
                    List<String> details = feeShareRespDTOS.stream().map(FeeShareRespDTO::getFeeShareDetail).toList();
                    feeShareResp.setDesc(String.join(",", details));
                    feeShareResp.setAmount(s.getTotalAmount());
                    feeShareResps.add(feeShareResp);
                });
            }

            return feeShareResps;
        }
        return null;
    }

    @Override
    public void updateAuxiliaryPurchaseAmount(String code, JsonAmount amount) {
        List<PurchaseContractDO> doList = purchaseContractMapper.selectList(PurchaseContractDO::getCode, code);
        if (CollUtil.isEmpty(doList)) {
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        PurchaseContractDO purchaseContractDO = doList.get(0);
        if (Objects.equals(purchaseContractDO.getAuxiliaryFlag(), BooleanEnum.NO.getValue())) {
            throw exception(PURCHASE_CONTRACT_IS_NOT_AUXILIARY);
        }
        BigDecimal addAmount = BigDecimal.ZERO;
        if (Objects.nonNull(purchaseContractDO.getPayedAmount()) && Objects.nonNull(purchaseContractDO.getPayedAmount().getAmount())) {
            addAmount = purchaseContractDO.getPayedAmount().getAmount().add(amount.getAmount());
        } else {
            addAmount = amount.getAmount();
        }
        purchaseContractDO.setPayedAmount(new JsonAmount().setAmount(addAmount).setCurrency(amount.getCurrency()));
        //包材的采购合同下推对公付款之后，包材采购合同就不能转付款申请了   再转付款单判断付款金额时候已经一定是不能转付款申请了
        if (addAmount.compareTo(purchaseContractDO.getTotalAmount().getAmount()) >= 0) {
            purchaseContractDO.setAuxiliaryPaymentFlag(BooleanEnum.YES.getValue());
        }
        purchaseContractMapper.updateById(purchaseContractDO);
    }

    @Override
    public Map<String, List<UserDept>> getPurchaseUserListBySaleContractCodeSet(Set<String> saleContractCodeSet) {
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapper<PurchaseContractDO>().select(PurchaseContractDO::getSaleContractCode, PurchaseContractDO::getPurchaseUserId, PurchaseContractDO::getPurchaseUserDeptId, PurchaseContractDO::getPurchaseUserName, PurchaseContractDO::getPurchaseUserDeptName).in(PurchaseContractDO::getSaleContractCode, saleContractCodeSet).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        if (CollUtil.isEmpty(purchaseContractDOS)) {
            return Map.of();
        }
        return purchaseContractDOS.stream().collect(Collectors.groupingBy(PurchaseContractDO::getSaleContractCode, Collectors.mapping(s -> new UserDept().setUserId(s.getPurchaseUserId()).setNickname(s.getPurchaseUserName()).setDeptId(s.getPurchaseUserDeptId()).setDeptName(s.getPurchaseUserDeptName()), Collectors.toList())));
    }

    @Override
    public Set<String> filterInvoicedPurchaseContractCode(Collection<String> purchaseCodeList) {
        if (CollUtil.isEmpty(purchaseCodeList)){
            return Set.of();
        }
        List<PurchaseContractItemDO> invoicedPurchaseList = purchaseContractItemMapper.selectList(new LambdaQueryWrapper<PurchaseContractItemDO>().select(PurchaseContractItemDO::getPurchaseContractCode).in(PurchaseContractItemDO::getPurchaseContractCode, purchaseCodeList).ne(PurchaseContractItemDO::getInvoiceStatus, InvoiceStatusEnum.NOT_ISSUED.getValue()));
        if (CollUtil.isEmpty(invoicedPurchaseList)){
            return Set.of();
        }
        return invoicedPurchaseList.stream().map(PurchaseContractItemDO::getPurchaseContractCode).collect(Collectors.toSet());
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, List<UserDept>> getPurchaseUserByContractCodeSet(Set<String> contractCodeList) {
        if (CollUtil.isEmpty(contractCodeList)){
            return Map.of();
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapper<PurchaseContractDO>().select(PurchaseContractDO::getPurchaseUserId, PurchaseContractDO::getPurchaseUserName, PurchaseContractDO::getPurchaseUserDeptId, PurchaseContractDO::getPurchaseUserDeptName).in(PurchaseContractDO::getCode, contractCodeList).ne(PurchaseContractDO::getContractStatus, PurchaseContractStatusEnum.CASE_SETTLED.getCode()));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return Map.of();
        }
        Map<String, List<UserDept>> result = new HashMap<>();
        Map<Long, UserDept> purchaseUserMap = new HashMap<>();
        Map<Long, UserDept> managerMap = new HashMap<>();
        purchaseContractDOS.stream().filter(s -> Objects.nonNull(s.getPurchaseUserId())||Objects.nonNull(s.getManager())).forEach(s -> {
            UserDept purchaseUser = new UserDept();
            purchaseUser.setUserId(s.getPurchaseUserId());
            purchaseUser.setNickname(s.getPurchaseUserName());
            purchaseUser.setDeptId(s.getPurchaseUserDeptId());
            purchaseUser.setDeptName(s.getPurchaseUserDeptName());
            purchaseUserMap.put(s.getPurchaseUserId(), purchaseUser);
            if (Objects.nonNull(s.getManager())){
                managerMap.put(s.getManager().getUserId(), s.getManager());
            }
        });
        if (CollUtil.isNotEmpty(purchaseUserMap)){
            List<UserDept> value = purchaseUserMap.values().stream().collect(Collectors.toList());
            result.put(CommonDict.PURCHASE_USER_FIELD_NAME, value);
        }
        if (CollUtil.isNotEmpty(managerMap)){
            List<UserDept> value = managerMap.values().stream().collect(Collectors.toList());
            result.put(CommonDict.MANAGER_FIELD_NAME, value);
        }
        return result;
    }

    /**
     * 根据赠品数量拆分采购明细明细
     * @param purchaseContractItemDOList 采购明细列表
     * @return 拆分后列表
     */
    private List<PurchaseContractItemDO> splitPurchaseContractItemListByFreeQuantity(List<PurchaseContractItemDO> purchaseContractItemDOList) {
        if (CollUtil.isEmpty(purchaseContractItemDOList)){
            return List.of();
        }
        List<PurchaseContractItemDO> splitListByFreeQuantity = new ArrayList<>();
        purchaseContractItemDOList.forEach(s -> {
            Integer freeQuantity = s.getFreeQuantity();
            if (Objects.isNull(freeQuantity) || freeQuantity == 0){
                splitListByFreeQuantity.add(s);
                return;
            }
            // 拆分销售明细
            PurchaseContractItemDO freeItem = BeanUtil.copyProperties(s, PurchaseContractItemDO.class);
            // 真实销售数量 如果为0则仅打印赠品数量
            int realPurchaseQuantity = s.getQuantity() - freeQuantity;
            if (realPurchaseQuantity > 0) {
                s.setQuantity(realPurchaseQuantity);
                splitListByFreeQuantity.add(s);
            }
            freeItem.setQuantity(freeQuantity);
            // 赠品将销售单价及销售总额置0
            Optional.ofNullable(freeItem.getUnitPrice()).ifPresent(unitPrice->{
                freeItem.setUnitPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(unitPrice.getCurrency()));
                freeItem.setWithTaxPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(unitPrice.getCurrency()));
            });
            splitListByFreeQuantity.add(freeItem);
        });
        return splitListByFreeQuantity;
    }
}

