package com.syj.eplus.module.scm.service.purchaseplan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CurrencyUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.TransformUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.HsDataDTO;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractSaveInfoReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemAndContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.*;
import com.syj.eplus.module.scm.convert.PurchaseContractConvert;
import com.syj.eplus.module.scm.convert.PurchasePlanConvert;
import com.syj.eplus.module.scm.convert.PurchasePlanItemConvert;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchaseItemSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.dal.mysql.purchaseplan.PurchasePlanMapper;
import com.syj.eplus.module.scm.dal.mysql.purchaseplanitem.PurchasePlanItemMapper;
import com.syj.eplus.module.scm.entity.UpdateItemEntity;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
import com.syj.eplus.module.scm.service.purchaseplanitem.PurchasePlanItemService;
import com.syj.eplus.module.scm.service.quoteitem.QuoteItemService;
import com.syj.eplus.module.scm.service.vender.VenderService;
import com.syj.eplus.module.scm.util.CalcContactUtil;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractSaveDTO;
import com.syj.eplus.module.sms.api.dto.SimpleSaleItemDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.api.stock.dto.QueryStockReqVO;
import com.syj.eplus.module.wms.api.stock.dto.StockDetailRespVO;
import com.syj.eplus.module.wms.enums.StockLockSourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_CURRENCY_EXISTS;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.NOT_UPDATE_PROCESS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 采购计划 Service 实现类
 *
 * @author zhangcm
 */
@Slf4j
@Service
@Validated
public class PurchasePlanServiceImpl extends ServiceImpl<PurchasePlanMapper, PurchasePlanDO> implements PurchasePlanService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private PurchasePlanMapper purchasePlanMapper;
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    private PurchasePlanItemMapper purchasePlanItemMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private PurchasePlanItemService purchasePlanItemService;
    @Resource
    private VenderService venderService;
    @Resource
    @Lazy
    private PurchaseContractService purchaseContractService;

    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    @Lazy
    private PurchaseContractItemService purchaseContractItemService;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CompanyApi companyApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private IStockApi stockApi;
    @Resource
    private VenderApi venderApi;
    // TODO: Replace with CompanyPathApi when available
    // @Lazy
    // @Resource
    // private CompanyPathService companyPathService;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private CustApi custApi;
    @Resource
    private QuoteItemService quoteItemService;
    @Resource
    private PaymentItemApi paymentItemApi;

    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private ConfigApi configApi;
    @Resource
    private RateApi rateApi;

    private static final String PROCESS_DEFINITION_KEY = "scm_purchase_plan";
    private static final String AUXILIARY_PROCESS_DEFINITION_KEY = "scm_purchase_plan_auxiliary";
    private static final String CODE_PREFIX = "PP";
    public static final String SN_TYPE = "SN_PURCHASEPLAN";
    private static final String OPERATOR_EXTS_KEY = "purchasePlanCode";
    // TODO 条件流转key，考虑是否做成可配
    private static final String CONDITION_KEY = "ownBrandFlag";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createPurchasePlan(PurchasePlanInfoSaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        String categoryCode = genPurchasePlanCode(createReqVO.getSaleContractCode(),createReqVO.getCode());
        createReqVO.setCode(categoryCode);
        createReqVO.setVer(1);
        createReqVO.setPlanDate(LocalDateTime.now());
        // 获取链路编号
        if (CollUtil.isEmpty(createReqVO.getLinkCodeList())) {
            createReqVO.setLinkCodeList(List.of(IdUtil.fastSimpleUUID()));
        }
        if (ObjectUtil.isNull(createReqVO.getAuditStatus())) {
            createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        }
        if (ObjectUtil.isNull(createReqVO.getPlanStatus())) {
            createReqVO.setPlanStatus(PurchasePlanStatusEnum.PENDING_SUBMIT.getCode());
        }

        PurchasePlanDO planDO = BeanUtils.toBean(createReqVO, PurchasePlanDO.class);
        // 赋值采购员
        List<PurchasePlanItemSaveReqVO> children = createReqVO.getChildren();
        List<Long> purchaseUserIdList = children.stream().map(PurchasePlanItemSaveReqVO::getPurchaseUserId).distinct().toList();
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(purchaseUserIdList);
        if (CollUtil.isNotEmpty(userDeptListCache)) {
            planDO.setPurchaseUserList(userDeptListCache.values().stream().toList());
        }
        var itemList = createReqVO.getChildren();
        Set<String> auxiliaryPurchaseContractCodeSet = itemList.stream().map(PurchasePlanItemSaveReqVO::getAuxiliaryPurchaseContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Set<String> auxiliarySaleContractCodeSet = itemList.stream().map(PurchasePlanItemSaveReqVO::getAuxiliarySaleContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Map<String, List<UserDept>> purchaseUserAndSalesMap = getPurchaseUserAndSales(auxiliaryPurchaseContractCodeSet, auxiliarySaleContractCodeSet);
        if (CollUtil.isNotEmpty(purchaseUserAndSalesMap)){
            planDO.setAuxiliaryPurchaseUser(purchaseUserAndSalesMap.get(CommonDict.PURCHASE_USER_FIELD_NAME));
            planDO.setAuxiliarySales(purchaseUserAndSalesMap.get(CommonDict.SALES_FIELD_NAME));
            planDO.setAuxiliaryManager(purchaseUserAndSalesMap.get(CommonDict.MANAGER_FIELD_NAME));
        }
        //库存采购把创建人计入业务员，用来解决业务员创建的库存采购在采购合同看不见的权限问题
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        if(Objects.isNull(planDO.getSales()) || Objects.isNull(planDO.getSales().getUserId())){
            UserDept userDeptByUserId = adminUserApi.getUserDeptByUserId(loginUserId);
            planDO.setSales(userDeptByUserId);
        }
        boolean stockFlag = itemList.stream().allMatch(item -> item.getNeedPurQuantity() == 0);
        if (stockFlag){
            planDO.setPlanStatus(PurchasePlanStatusEnum.COMPLETED.getCode());
        }
        purchasePlanMapper.insert(planDO);
        Long planId = planDO.getId();

        // 判断客户编号存在且有效
        if (StringUtils.isNotBlank(createReqVO.getCustCode())) {
            String custCode = createReqVO.getCustCode();
            Map<String, String> custNameCache = custApi.getCustNameCache(custCode);
            if (CollUtil.isNotEmpty(custNameCache)) {
                String s = custNameCache.get(custCode);
                if (Objects.nonNull(s)) {
                    preprocessingOwnbrandSku(itemList, createReqVO.getCustCode(), planId);
                }
            }
        }

        Long companyId = createReqVO.getCompanyId();
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (CollUtil.isNotEmpty(itemList)) {
            List<String> skuCodeList = itemList.stream().map(PurchasePlanItemSaveReqVO::getSkuCode).distinct().toList();
            Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
            itemList.forEach(s -> {
                s.setLevels(1);
                s.setPurchasePlanId(planId);
                s.setPurchasePlanCode(categoryCode);
                s.setVer(planDO.getVer());
                s.setConvertedFlag(BooleanEnum.NO.getValue());
                s.setCustId(createReqVO.getCustId());
                s.setCustCode(createReqVO.getCustCode());
                s.setLinkCodeList(planDO.getLinkCodeList());
                s.setId(null);
                String uniqueCode = IdUtil.fastSimpleUUID();
                s.setUniqueCode(uniqueCode);
                if (StrUtil.isEmpty(s.getSourceUniqueCode())) {
                    s.setSourceUniqueCode(uniqueCode);
                }
                if (StrUtil.isEmpty(s.getSaleItemUniqueCode())) {
                    s.setSaleItemUniqueCode(uniqueCode);
                }
                if (Objects.nonNull(s.getQtyPerOuterbox())) {
                    Integer outBox = s.getQtyPerOuterbox() == 0 ? 1 : s.getQtyPerOuterbox();
                    Integer boxCount = NumUtil.div(s.getNeedPurQuantity(), outBox).setScale(CalculationDict.ZERO, RoundingMode.UP).intValue();
                    s.setBoxCount(boxCount);
                }
                if (Objects.isNull(s.getSkuUnit())&&CollUtil.isNotEmpty(hsMap)) {
                    HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                    if (Objects.nonNull(hsDataDTO)){
                        s.setSkuUnit(hsDataDTO.getUnit());
                    }
                }
                if (ObjectUtil.isNotNull(companyDTO)) {
                    s.setPurchaseCompanyId(companyDTO.getId());
                    s.setPurchaseCompanyName(companyDTO.getCompanyName());
                }
            });
            purchasePlanItemService.batchSavePlanItem(itemList);
            // 回写销售明细转采购标识
            saleContractApi.updateSaleItemPurchaseFlag(itemList.stream().map(PurchasePlanItemSaveReqVO::getSaleContractItemId).toList(), BooleanEnum.YES.getValue());
        }
        List<String> linkCodeList = planDO.getLinkCodeList();
        // 插入订单链路
        if (CollUtil.isNotEmpty(linkCodeList)) {
            linkCodeList.forEach(s -> {
                orderLinkApi.createOrderLink(new OrderLinkDTO()
                        .setBusinessId(planId)
                        .setCode(planDO.getCode())
                        .setName(BusinessNameDict.PURCHASE_PLAN_NAME)
                        .setType(OrderLinkTypeEnum.PURCHASE_PLAN.getValue())
                        .setLinkCode(s)
                        .setItem(itemList)
                        .setParentCode(StrUtil.isEmpty(planDO.getSaleContractCode()) ? CommonDict.HYPHEN : planDO.getSaleContractCode())
                        .setStatus(planDO.getPlanStatus())
                        .setCompanyId(planDO.getCompanyId())
                        .setBusinessSubjectName(planDO.getCustName())
                        .setOrderMsg(planDO));
            });
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            Map<String, Object> variables = new HashMap<>(Map.of(CONDITION_KEY, 0));
            if (CollUtil.isNotEmpty(children)) {
                long count = children.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag())).count();
                if (count > CalculationDict.ZERO) {
                    variables.put(CONDITION_KEY, 1);
                }
            }
            if (BooleanEnum.YES.getValue().equals(createReqVO.getAuxiliaryFlag())) {
                submitAuxiliaryTask(planId, loginUserId, AUXILIARY_PROCESS_DEFINITION_KEY);
            } else {
                submitTask(planId, loginUserId, PROCESS_DEFINITION_KEY, variables);
            }
        }
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增采购计划】%s", categoryCode));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, categoryCode);

        responses.add(new CreatedResponse(planId, planDO.getCode()));
        return responses;
    }

    /**
     * 生成采购计划编号
     *
     * @param saleContractCode 销售合同编号
     * @return 采购计划编号
     */
    private String genPurchasePlanCode(String saleContractCode,String code) {
        String prefix;
        // 两位年号
        String shortYear = DateUtil.thisYear() % CalculationDict.ONE_HUNDRED + CommonDict.EMPTY_STR;
        if (StrUtil.isNotEmpty(code)){
            return code;
        }
        if (StrUtil.isEmpty(saleContractCode)) {
            prefix = "SG" + shortYear;
            return codeGeneratorApi.getCodeGenerator(SN_TYPE, prefix, false, 4);
        } else {
            return codeGeneratorApi.getCodeGenerator(SN_TYPE, code + "R", false, null);
        }

    }

    //校验币种是否可用
    private void checkCurrencyByCompany(Long companyId, List<PurchasePlanItemSaveReqVO> children) {
        //根据内部公司查询可用币种
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXISTS, companyId);
        }
        if (CollUtil.isEmpty(children)) {
            throw exception(PURCHASE_PLAN_PRODUCT_NULL);
        }
        if (Objects.isNull(companyDTO.getAvailableCurrencyList())) {
            throw exception(COMPANY_NOT_CURRENCY_EXISTS);
        }
        boolean judge = children.stream().map(PurchasePlanItemSaveReqVO::getCurrency).allMatch(companyDTO.getAvailableCurrencyList()::contains);
        if (!judge) {
            throw exception(PURCHASE_PLAN_CURRENCY_WRONG);
        }
    }

    private List<PurchasePlanItemSaveReqVO> preprocessingOwnbrandSku(List<PurchasePlanItemSaveReqVO> purchasePlanItemDOList, String custCode, Long planId) {
        if (CollUtil.isEmpty(purchasePlanItemDOList)) {
            return purchasePlanItemDOList;
        }
        // key -> 产品编号 value -> 客户货号
        Map<String, Tuple> skuCodeMap = purchasePlanItemDOList.stream().filter(s -> {
            // 自营产品且非客户产品
            return BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()) && BooleanEnum.NO.getValue().equals(s.getCustProFlag());
        }).collect(Collectors.toMap(PurchasePlanItemSaveReqVO::getSkuCode, s -> new Tuple(s.getCskuCode(), s.getWithTaxPrice(),s.getBarcode()), (o1, o2) -> o1));

        if (CollUtil.isEmpty(skuCodeMap)) {
            return purchasePlanItemDOList;
        }

        // 校验自营产品是否存在当前客户对应客户产品
        Map<String, SimpleData> stringSimpleDataMap = skuApi.validateOwnbrandSku(skuCodeMap, custCode);
        List<String> skuCodeList = purchasePlanItemDOList.stream().map(PurchasePlanItemSaveReqVO::getSkuCode).distinct().toList();
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
        purchasePlanItemDOList.forEach(s -> {
            s.setPurchasePlanId(planId);
            if (Objects.isNull(s.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                HsDataDTO hsDataDTO = hsMap.get(s.getSkuCode());
                if (Objects.nonNull(hsDataDTO)){
                    s.setSkuUnit(hsDataDTO.getUnit());
                }
            }
            if (CollUtil.isNotEmpty(stringSimpleDataMap)) {
                SimpleData simpleData = stringSimpleDataMap.get(s.getSkuCode());
                if (Objects.nonNull(simpleData)) {
                    s.setSkuId(simpleData.getId());
                    s.setSkuCode(simpleData.getCode());
                    s.setCustProFlag(BooleanEnum.YES.getValue());
                }
            }
        });
        return purchasePlanItemDOList;
    }

    @Override
    public Set<Long> updatePurchasePlan(PurchasePlanInfoSaveReqVO updateReqVO) {
        List<PurchasePlanItemSaveReqVO> children = updateReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
        }

        //币种校验
        logger.info("采购计划更新接口：updatePurchasePlan" + JsonUtils.toJsonString(updateReqVO));
        if (!BooleanEnum.YES.getValue().equals(updateReqVO.getAuxiliaryFlag())) {
            checkCurrencyByCompany(updateReqVO.getCompanyId(), children);
        }
        //校验采购锁定数量 不能大于销售数量  且不小于销售锁定数量
        children.forEach(s -> {
            if (Objects.isNull(s.getLockQuantity()) || Objects.isNull(s.getSaleQuantity()) || Objects.isNull(s.getSaleLockQuantity())) {
                return;
            }
            if (s.getSaleQuantity() < s.getLockQuantity()) {
                throw exception(PURCHASE_PLAN_LOCK_MORE);
            }
            if (s.getSaleLockQuantity() > s.getLockQuantity()) {
                throw exception(PURCHASE_PLAN_LOCK_LESS);
            }
        });

        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(updateReqVO.getAuditStatus())) {
            throw exception(NOT_UPDATE_PROCESS);
        }
        PurchasePlanDO purchasePlanDO = validatePurchasePlanExists(updateReqVO.getId());
        PurchasePlanDO saveDO = PurchasePlanConvert.INSTANCE.convert(updateReqVO);
        //库存采购把创建人计入业务员，用来解决业务员创建的库存采购在采购合同看不见的权限问题
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        if(Objects.isNull(saveDO.getSales()) || Objects.isNull(saveDO.getSales().getUserId())){
            UserDept userDeptByUserId = adminUserApi.getUserDeptByUserId(loginUserId);
            saveDO.setSales(userDeptByUserId);
        }
        Long planDOId = purchasePlanDO.getId();
        //日志记录
        {

            PurchasePlanDO updateObj = PurchasePlanConvert.INSTANCE.convertPurchasePlanDO(updateReqVO);

            List<ChangeRecord> changeRecords = new OperateCompareUtil<PurchasePlanDO>().compare(purchasePlanDO, updateObj);
            //采购主体变更
            TransformUtil.transformField(changeRecords,
                    Arrays.asList(String.valueOf(purchasePlanDO.getCompanyId()), String.valueOf(updateObj.getCompanyId())),
                    companyApi::getStringSimpleCompanyDTO,
                    CommonDict.COMPANY_NAME::equals,
                    SimpleCompanyDTO::getName);

            //采购明细变更修改
            dealPurchasePlanItem(planDOId, updateReqVO, changeRecords);
            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        }
        Set<String> auxiliaryPurchaseContractCodeSet = children.stream().map(PurchasePlanItemSaveReqVO::getAuxiliaryPurchaseContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Set<String> auxiliarySaleContractCodeSet = children.stream().map(PurchasePlanItemSaveReqVO::getAuxiliarySaleContractCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Map<String, List<UserDept>> purchaseUserAndSalesMap = getPurchaseUserAndSales(auxiliaryPurchaseContractCodeSet, auxiliarySaleContractCodeSet);
        if (CollUtil.isNotEmpty(purchaseUserAndSalesMap)){
            saveDO.setAuxiliaryPurchaseUser(purchaseUserAndSalesMap.get(CommonDict.PURCHASE_USER_FIELD_NAME));
            saveDO.setAuxiliarySales(purchaseUserAndSalesMap.get(CommonDict.SALES_FIELD_NAME));
            saveDO.setAuxiliaryManager(purchaseUserAndSalesMap.get(CommonDict.MANAGER_FIELD_NAME));
        }
        purchasePlanMapper.updateById(saveDO);
        Long id = saveDO.getId();
        List<String> skuCodeList = children.stream().map(PurchasePlanItemSaveReqVO::getSkuCode).distinct().toList();
        Map<String, HsDataDTO> hsMap = skuApi.getHsDataBySkuCodes(skuCodeList);
        List<PurchasePlanItemDO> itemListDO = children.stream().map(itemReq -> {
            PurchasePlanItemDO item = PurchasePlanItemConvert.INSTANCE.convertPurchasePlanItemDO(itemReq);
            // 更新明细转换状态
            if (BooleanEnum.YES.getValue().equals(updateReqVO.getSubmitFlag())){
                updatePurchasePlanItemConvertedFlag(item);
            }
            item.setPurchasePlanId(id);
            item.setPurchasePlanCode(saveDO.getCode());
            Integer currentLockQuantity = item.getCurrentLockQuantity();
            item.setVer(saveDO.getVer());
            item.setCustId(saveDO.getCustId());
            item.setCustCode(saveDO.getCustCode());
            item.setLinkCodeList(saveDO.getLinkCodeList());
            if (StrUtil.isEmpty(item.getUniqueCode())) {
                item.setUniqueCode(IdUtil.fastSimpleUUID());
            }
            if (Objects.nonNull(item.getQtyPerOuterbox())) {
                Integer outBox = item.getQtyPerOuterbox() == 0 ? 1 : item.getQtyPerOuterbox();
                Integer boxCount = NumUtil.div(item.getNeedPurQuantity(), outBox).setScale(CalculationDict.ZERO, RoundingMode.UP).intValue();
                item.setBoxCount(boxCount);
            }
            if (Objects.isNull(item.getSkuUnit()) && CollUtil.isNotEmpty(hsMap)) {
                HsDataDTO hsDataDTO = hsMap.get(itemReq.getSkuCode());
                if (Objects.nonNull(hsDataDTO)){
                    item.setSkuUnit(hsDataDTO.getUnit());
                }
            }
            if (ObjectUtil.isNotNull(currentLockQuantity) && currentLockQuantity > item.getSaleQuantity()) {
                throw exception(PURCHASE_PLAN_ITEM_LOCK_QUANTITY_ERROR);
            }
            if (Objects.nonNull(item.getWithTaxPrice()) && Objects.nonNull(item.getTaxRate())) {
                BigDecimal taxRate = item.getTaxRate();
                BigDecimal unitAmount = item.getWithTaxPrice().getAmount().multiply(BigDecimal.valueOf(100)).divide(taxRate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP);
                item.setUnitPrice(new JsonAmount().setCurrency(item.getWithTaxPrice().getCurrency()).setAmount(unitAmount));
            }
            return item;
        }).toList();
        // 采购计划明细入数据库处理
        Set<Long> itemIdSet = dealPlanItemToDataBase(itemListDO, id);
        // 赋值采购主表采购员
        List<PurchasePlanItemDO> purchasePlanItems = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().select(PurchasePlanItemDO::getId,PurchasePlanItemDO::getPurchaseUserId).eq(PurchasePlanItemDO::getPurchasePlanId, id));
        if (CollUtil.isNotEmpty(purchasePlanItems)){
            List<Long> purchaseUserIdList = purchasePlanItems.stream().map(PurchasePlanItemDO::getPurchaseUserId).distinct().toList();
            if (CollUtil.isNotEmpty(purchaseUserIdList)){
                Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(purchaseUserIdList);
                purchasePlanMapper.updateById(new PurchasePlanDO().setId(id).setPurchaseUserList(userDeptListCache.values().stream().toList()));
            }
        }
        // 采购计划明细-锁定库存  仅第一次下推合同处理
        List<PurchasePlanItemDO> dealStockList = itemListDO.stream().filter(s -> Objects.isNull(s.getConvertedQuantity()) || (Objects.nonNull(s.getConvertedQuantity()) && s.getConvertedQuantity() == 0)).toList();
        if (CollUtil.isNotEmpty(dealStockList)) {
            this.dealLockStock(dealStockList, purchasePlanDO.getSaleContractId(), purchasePlanDO.getSaleContractCode(), purchasePlanDO.getId(), purchasePlanDO.getCode());

        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag()) && !BooleanEnum.YES.getValue().equals(updateReqVO.getToContractFlag())) {
            Map<String, Object> variables = new HashMap<>(Map.of(CONDITION_KEY, 0));
            if (CollUtil.isNotEmpty(children)) {
                long count = children.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag())).count();
                if (count > CalculationDict.ZERO) {
                    variables.put(CONDITION_KEY, 1);
                }
            }
            if (BooleanEnum.YES.getValue().equals(updateReqVO.getAuxiliaryFlag())) {
                submitAuxiliaryTask(planDOId, WebFrameworkUtils.getLoginUserId(), AUXILIARY_PROCESS_DEFINITION_KEY);
            } else {
                submitTask(planDOId, WebFrameworkUtils.getLoginUserId(), PROCESS_DEFINITION_KEY, variables);
            }
        }
    return itemIdSet;
    }

    /**
     * 处理采购计划明细入库
     *
     * @param itemListDOs 采购计划明细列表
     * @param planId      采购计划id
     */
    private Set<Long> dealPlanItemToDataBase(List<PurchasePlanItemDO> itemListDOs, Long planId) {
        Set<Long> result= new HashSet<>();
        if (CollUtil.isEmpty(itemListDOs)) {
            return result;
        }
        List<PurchasePlanItemDO> purchasePlanItems = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, planId));
        // 库中没有则直接插入
        if (CollUtil.isEmpty(purchasePlanItems)) {
            itemListDOs.forEach(s -> {
                s.setPurchasePlanId(planId);
                s.setId(null);
                if (StrUtil.isEmpty(s.getUniqueCode())) {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                }
            });
            purchasePlanItemMapper.insertBatch(itemListDOs);
            return result;
        }
        Set<Long> thisIdSet = itemListDOs.stream().filter(s -> Objects.nonNull(s.getId())).map(PurchasePlanItemDO::getId).collect(Collectors.toSet());
        // 仅可删除拆分出来的明细
        Set<Long> deleteSet = purchasePlanItems.stream().filter(s -> !ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag())&&(thisIdSet.isEmpty() || !thisIdSet.contains(s.getId())&&(Objects.nonNull(s.getOneSplitNum())&&Objects.nonNull(s.getTwoSplitNum())))).map(PurchasePlanItemDO::getId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(deleteSet)) {
            purchasePlanItemMapper.delete(new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getId, deleteSet));
        }
        Set<Long> idSet = purchasePlanItems.stream().filter(s->!ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag())).map(PurchasePlanItemDO::getId).collect(Collectors.toSet());
        List<PurchasePlanItemDO> insertList = new ArrayList<>();
        List<PurchasePlanItemDO> updateList = new ArrayList<>();
        itemListDOs.forEach(s -> {
            if (Objects.isNull(s.getPurchasePlanId())) {
                s.setPurchasePlanId(planId);
            }
            if (Objects.isNull(s.getId())) {
                insertList.add(s);
                return;
            }
            if (idSet.contains(s.getId())) {
                updateList.add(s);
            }
        });
        if (CollUtil.isNotEmpty(insertList)) {
            insertList.forEach(s -> {
                s.setId(null);
                if (StrUtil.isEmpty(s.getUniqueCode())) {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                }
            });
            purchasePlanItemMapper.insertBatch(insertList);
            result.addAll(insertList.stream().map(PurchasePlanItemDO::getId).collect(Collectors.toSet()));
        }
        if (CollUtil.isNotEmpty(updateList)) {
            purchasePlanItemMapper.updateBatch(updateList);
            result.addAll(updateList.stream().map(PurchasePlanItemDO::getId).collect(Collectors.toSet()));
        }
        return result;
    }

    /**
     * 更新采购计划明细-转换状态
     *
     * @param item 采购计划明细
     */
    private void updatePurchasePlanItemConvertedFlag(PurchasePlanItemDO item) {
        Integer needPurQuantity = item.getNeedPurQuantity();
        Integer purchaseQuantity = Objects.isNull(item.getPurchaseQuantity()) ? 0 : item.getPurchaseQuantity();
        Integer convertedQuantity = Objects.isNull(item.getConvertedQuantity()) ? 0 : item.getConvertedQuantity();
        if (needPurQuantity == purchaseQuantity + convertedQuantity) {
            item.setConvertedFlag(ConvertedFlagEnum.CONVERTED.getStatus());
        } else {
            if (convertedQuantity == 0) {
                item.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
            } else {
                item.setConvertedFlag(ConvertedFlagEnum.PART_CONVERTED.getStatus());
            }
        }
        item.setConvertedQuantity(purchaseQuantity + convertedQuantity);
        item.setConvertTime(LocalDateTime.now());
    }

    /**
     * 采购计划-明细锁库逻辑
     *
     * @param purchasePlanItemDOList 销售明细列表
     * @param saleContractCode       销售合同编号
     */
    private void dealLockStock(List<PurchasePlanItemDO> purchasePlanItemDOList,
                               Long saleContractId, String saleContractCode,
                               Long purchasePlanId, String purchasePlanCode) {
        List<StockLockSaveReqVO> saveReqVOList = new ArrayList<>();
        // 循环产品明细,若存在锁定批次则进行锁定
        List<StockLockSaveReqVO> saleItemLockList = new ArrayList<>();
        purchasePlanItemDOList.forEach(purchasePlanItemDO -> {
            List<StockLockSaveReqVO> stockLockSaveReqVOList = purchasePlanItemDO.getStockLockSaveReqVOList();
            if (!CollectionUtils.isEmpty(stockLockSaveReqVOList)) {
                stockLockSaveReqVOList.removeIf(Objects::isNull);
                stockLockSaveReqVOList.forEach(lockSaveReqVO -> {
                    lockSaveReqVO.setSaleContractId(saleContractId);
                    lockSaveReqVO.setSaleContractCode(saleContractCode);
                    lockSaveReqVO.setSourceOrderType(StockLockSourceTypeEnum.PURCHASE_PLAN.getValue());
                    lockSaveReqVO.setSaleContractItemId(purchasePlanItemDO.getSaleContractItemId());
                    lockSaveReqVO.setSkuCode(purchasePlanItemDO.getSkuCode());
                    lockSaveReqVO.setCompanyId(lockSaveReqVO.getCompanyId());
                    lockSaveReqVO.setCompanyName(lockSaveReqVO.getCompanyName());
                    lockSaveReqVO.setLockQuantity(lockSaveReqVO.getSourceOrderLockedQuantity());
                });
                saveReqVOList.addAll(stockLockSaveReqVOList);
                // 仅回写普通产品及组套  子产品不需要回写锁定数量
                if (purchasePlanItemDO.getLevels() == 1) {
                    saleItemLockList.addAll(stockLockSaveReqVOList);
                }
            }
        });
        // 更新外销合同-明细锁定数量、待采购量
        if (CollUtil.isNotEmpty(saleItemLockList)) {
            Map<Long, List<StockLockSaveReqVO>> saleItemLockMap = saleItemLockList.stream().collect(Collectors.groupingBy(StockLockSaveReqVO::getSaleContractItemId));
            saleContractApi.syncSaleItemLockInfo(saleItemLockMap);
        }
        if (CollUtil.isNotEmpty(saveReqVOList)) {
            // 释放销售合同锁定库存
            List<Long> saleItemIdList = purchasePlanItemDOList.stream().map(PurchasePlanItemDO::getSaleContractItemId).distinct().toList();
            stockApi.cancelStockLock(saleContractCode, saleItemIdList, null);
            stockApi.batchLockStock(saveReqVOList);
        }
    }

    private void dealPurchasePlanItem(Long planDOId, PurchasePlanInfoSaveReqVO updateReqVO, List<ChangeRecord> changeRecords) {
        List<PurchasePlanItemDO> basePlanItemList = purchasePlanItemService.getPurchasePlanItemListByPurchasePlanId(planDOId);
        List<PurchasePlanItemDO> reqPlanItemList = BeanUtils.toBean(updateReqVO.getChildren(), PurchasePlanItemDO.class);

        if (CollUtil.isEmpty(reqPlanItemList) && CollUtil.isNotEmpty(basePlanItemList)) {     //判断删除
            changeRecords.add(new ChangeRecord().setFieldName("清空采购计划报价"));
        } else if (CollUtil.isNotEmpty(reqPlanItemList) && CollUtil.isEmpty(basePlanItemList)) {    //判断新增
            reqPlanItemList.forEach(s -> {
                changeRecords.add(new ChangeRecord().setFieldName("新增采购计划明细"));
            });
        } else if (CollUtil.isNotEmpty(reqPlanItemList) && CollUtil.isNotEmpty(basePlanItemList)) {
            Map<Long, PurchasePlanItemDO> dtoMap = basePlanItemList.stream().collect(Collectors.toMap(PurchasePlanItemDO::getId, s -> s));
            List<PurchasePlanItemDO> updateList = reqPlanItemList.stream().filter(s -> Objects.nonNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(updateList)) {
                updateList.forEach(s -> {
                    PurchasePlanItemDO purchasePlanItemDO = dtoMap.get(s.getId());
                    if (Objects.isNull(purchasePlanItemDO)) {
                        return;
                    }
                    List<ChangeRecord> quoteRecords = new OperateCompareUtil<PurchasePlanItemDO>().compare(purchasePlanItemDO, s);
                    if (CollUtil.isNotEmpty(quoteRecords)) {
                        quoteRecords.forEach(quoteRecord -> {
                            quoteRecord.setFieldName(String.format("修改供应商报价%s:%s %s -> %s", s.getSortNum(), quoteRecord.getFieldName(), quoteRecord.getOldValue(), quoteRecord.getValue()));
                        });
                        changeRecords.addAll(quoteRecords);
                    }
                });
            }

            List<PurchasePlanItemDO> insertList = reqPlanItemList.stream().filter(s -> Objects.isNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                insertList.forEach(insert -> {
                    changeRecords.add(new ChangeRecord().setFieldName("计划新增供应商报价"));
                });
            }

            Set<Long> list = reqPlanItemList.stream().map(PurchasePlanItemDO::getId).collect(Collectors.toSet());
            List<PurchasePlanItemDO> deleteList = basePlanItemList.stream().filter(s -> !list.contains(s.getId())).toList();
            if (CollUtil.isNotEmpty(deleteList)) {
                deleteList.forEach(delete -> {
                    changeRecords.add(new ChangeRecord().setFieldName("计划删除供应商报价"));
                });
            }
        }
    }

    @Override
    public void deletePurchasePlan(Long id) {
        PurchasePlanDO purchasePlanDo = validatePurchasePlanExists(id);
        purchasePlanMapper.deleteById(id);
        if (CollUtil.isNotEmpty(purchasePlanItemService.getPurchasePlanItemListByPurchasePlanId(id))) {
            purchasePlanItemService.deletePurchasePlanItemListByPurchasePlanId(id);
        }
    }

    private PurchasePlanDO validatePurchasePlanExists(Long id) {
        PurchasePlanDO plan = purchasePlanMapper.selectById(id);
        if (Objects.isNull(plan)) {
            throw exception(PURCHASE_PLAN_NOT_EXISTS);
        }
        return plan;
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
    public void submitTask(Long purchasePlanId, Long userId, String processDefinitionKey, Map<String, Object> variable) {
        bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, purchasePlanId, variable, null);
        updateAuditStatus(purchasePlanId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void submitAuxiliaryTask(Long id, Long userId, String processDefinitionKey) {
        // 获取辅料关联采购合同采购员跟销合同销售员
        PurchasePlanDO purchasePlanDO = validatePurchasePlanExists(id);
        Map<Long,UserDept> purchaseUsersAndSales = new HashMap<>();
        List<UserDept> auxiliaryManager = purchasePlanDO.getAuxiliaryManager();
        if (CollUtil.isNotEmpty(auxiliaryManager)){
            auxiliaryManager.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        List<UserDept> auxiliaryPurchaseUser = purchasePlanDO.getAuxiliaryPurchaseUser();
        if (CollUtil.isNotEmpty(auxiliaryPurchaseUser)){
            auxiliaryPurchaseUser.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        List<UserDept> auxiliarySales = purchasePlanDO.getAuxiliarySales();
        if (CollUtil.isNotEmpty(auxiliarySales)){
            auxiliarySales.forEach(s->{
                purchaseUsersAndSales.put(s.getUserId(),s);
            });
        }
        if (CollUtil.isNotEmpty(purchaseUsersAndSales)) {
            List<Long> approveUserIdList = purchaseUsersAndSales.values().stream().filter(Objects::nonNull).map(UserDept::getUserId).distinct().toList();
            Map<String, Object> variables = new HashMap<>();
            variables.put("loopCardinality", approveUserIdList.size());
            variables.put("approverIdList", approveUserIdList);
            bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, id, variables, Map.of());
        } else {
            bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, id);
        }
        updateAuditStatus(id, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    /**
     * 根据计划id获取包材对应采购合同跟销售合同的采购员跟业务员
     *
     * @param auxiliaryPurchaseContractCodeSet 计划id
     * @return auxiliarySaleContractCodeSet
     */
    @Override
    public Map<String,List<UserDept>> getPurchaseUserAndSales(Set<String> auxiliaryPurchaseContractCodeSet,Set<String> auxiliarySaleContractCodeSet) {
        if (CollUtil.isEmpty(auxiliaryPurchaseContractCodeSet)&&CollUtil.isEmpty(auxiliarySaleContractCodeSet)) {
            return Map.of();
        }
        List<UserDept> purchaseUsersAndSales = new ArrayList<>();
        Map<String,List<UserDept>> result = new HashMap<>();
        if (CollUtil.isNotEmpty(auxiliaryPurchaseContractCodeSet)) {
            Map<String, List<UserDept>> purchaseUserAndManagerMap = purchaseContractService.getPurchaseUserByContractCodeSet(auxiliaryPurchaseContractCodeSet);
            if (CollUtil.isNotEmpty(purchaseUserAndManagerMap)) {
              result.putAll(purchaseUserAndManagerMap);
            }
        }
        if (CollUtil.isNotEmpty(auxiliarySaleContractCodeSet)) {
            Map<String, List<UserDept>> salesMap = saleContractApi.getSalesByContractCodeSet(auxiliarySaleContractCodeSet);
            if (CollUtil.isNotEmpty(salesMap)) {
                result.putAll(salesMap);
            }
        }
        return result;
    }

    @Override
    public Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds) {
        Map<Long, List<Integer>> result = new HashMap<>();
        List<PurchasePlanItemDO> purchaseContractPlanItemDOS = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getSaleContractItemId, saleItemIds);
        if (CollUtil.isEmpty(purchaseContractPlanItemDOS)) {
            return result;
        }
        List<String> planCodeList = purchaseContractPlanItemDOS.stream().map(PurchasePlanItemDO::getPurchasePlanCode).toList();
        List<PurchasePlanDO> purchaseContractPlanDOS = purchasePlanMapper.selectList(new LambdaQueryWrapperX<PurchasePlanDO>().in(PurchasePlanDO::getCode, planCodeList));
        if (CollUtil.isEmpty(purchaseContractPlanDOS)){
            return result;
        }
        Map<String, Integer> purchaseContractPlanMap = purchaseContractPlanDOS.stream()
                .collect(Collectors.toMap(
                        PurchasePlanDO::getCode,
                        Function.identity(),
                        (o, n) ->
                                o.getCreateTime().isAfter(n.getCreateTime()) ? o : n
                )).values().stream()
                .collect(Collectors.toMap(PurchasePlanDO::getCode, PurchasePlanDO::getPlanStatus));
        purchaseContractPlanItemDOS.stream().filter(s-> purchaseContractPlanMap.containsKey(s.getPurchasePlanCode())).forEach(s -> {
            Integer status = purchaseContractPlanMap.get(s.getPurchasePlanCode());
            Integer cancelFlag = PurchasePlanStatusEnum.CASE_CLOSED.getCode().equals(status) ? BooleanEnum.YES.getValue() : BooleanEnum.NO.getValue();
            result.computeIfAbsent(s.getSaleContractItemId(), k -> new ArrayList<>()).add(cancelFlag);
        });
        return result;
    }

    @Override
    public void updateLinkCodeList(Map<String, String> linkCodeMap) {
        if (CollUtil.isEmpty(linkCodeMap)){
            return;
        }
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectList(new LambdaQueryWrapper<PurchasePlanDO>().select(PurchasePlanDO::getId,PurchasePlanDO::getCode, PurchasePlanDO::getLinkCodeList).in(PurchasePlanDO::getCode, linkCodeMap.keySet()));
        if (CollUtil.isEmpty(purchasePlanDOS)){
            return;
        }
        purchasePlanDOS.forEach(s->{
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
        purchasePlanMapper.updateBatch(purchasePlanDOS);
    }

    @Override
    public Map<Long, Integer> getPurchaseModelBySaleItemIds(Collection<Long> saleItems) {
        Map<Long, Integer> result = new HashMap<>();
        if (CollUtil.isEmpty(saleItems)){
            return result;
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().select(PurchasePlanItemDO::getSaleContractItemId, PurchasePlanItemDO::getPurchaseModel).in(PurchasePlanItemDO::getSaleContractItemId, saleItems));
        if (CollUtil.isEmpty(purchasePlanItemDOS)){
            return result;
        }
        purchasePlanItemDOS.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getSaleContractItemId))
                .forEach((k,v)->{
                    boolean combineFlag = v.stream().anyMatch(s -> PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel()));
                    if (combineFlag){
                        result.put(k, PurchaseModelEnum.COMBINE.getCode());
                    }else {
                        result.put(k, PurchaseModelEnum.STANDARD.getCode());
                    }
                });
        return result;
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        PurchasePlanDO planDO = validatePurchasePlanExists(auditableId);
        Integer status = 0;
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            status = PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode();
        } else if (BpmProcessInstanceResultEnum.REJECT.getResult().equals(auditStatus)) {
            status = PurchasePlanStatusEnum.REJECTED.getCode();
        } else if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(auditStatus)) {
            status = PurchasePlanStatusEnum.PENDING_SUBMIT.getCode();
        } else if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            status = PurchasePlanStatusEnum.PENDING_APPROVAL.getCode();
        } else if (BpmProcessInstanceResultEnum.CANCEL.getResult().equals(auditStatus)) {
            status = PurchasePlanStatusEnum.PENDING_SUBMIT.getCode();
        }

        purchasePlanMapper.updateById(PurchasePlanDO.builder().id(auditableId).auditStatus(auditStatus).planStatus(status).build());
        orderLinkApi.updateOrderLinkStatus(planDO.getCode(), BusinessNameDict.PURCHASE_PLAN_NAME, planDO.getLinkCodeList(), planDO.getPlanStatus());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishPurchasePlan(List<Long> planIdList,boolean validateFlag) {
        if (CollUtil.isEmpty(planIdList)) {
            return;
        }
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectBatchIds(planIdList);
        if (CollUtil.isEmpty(purchasePlanDOS)) {
            return;
        }
        boolean closeFlag = purchasePlanDOS.stream().anyMatch(s -> PurchasePlanStatusEnum.CASE_CLOSED.getCode().equals(s.getPlanStatus()));
        if (closeFlag && validateFlag){
            throw exception(PURCHASE_PLAN_CLOSED);
        }
        if (validateFlag){
            planIdList.forEach(this::validateAntiAuditStatus);
        }
        // 修改销售合同转采购标识
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().in(PurchasePlanItemDO::getPurchasePlanId, planIdList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> planItemDOS = purchasePlanItemMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(planItemDOS)) {
            planItemDOS.forEach(s -> s.setCancelFlag(BooleanEnum.YES.getValue()));
            purchasePlanItemMapper.updateBatch(planItemDOS);
        }
        List<Long> saleContractItemIdList = planItemDOS.stream().map(PurchasePlanItemDO::getSaleContractItemId).distinct().toList();
        List<OrderLinkDTO> orderLinkDTOList = purchasePlanDOS.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_PLAN_NAME, x, null, s.getSaleContractCode(), PurchasePlanStatusEnum.CASE_CLOSED.getCode(), null)).toList();
            }
            return null;
        }).filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchasePlanStatusEnum.CASE_CLOSED.getCode());
        purchasePlanMapper.update(
                new PurchasePlanDO().setPlanStatus(PurchasePlanStatusEnum.CASE_CLOSED.getCode())
                        .setFinishTime(LocalDateTime.now()),
                new LambdaQueryWrapperX<PurchasePlanDO>().in(PurchasePlanDO::getId, planIdList));
        // 释放销售合同锁定库存
        planItemDOS.forEach(s -> {
            // 释放销售合同锁定库存
            if (Objects.nonNull(s.getSaleContractCode()) && Objects.nonNull(s.getSaleContractItemId())) {
                stockApi.cancelStockLock(s.getSaleContractCode(), List.of(s.getSaleContractItemId()), null);
            }
        });
        // 处理作废拆分出来的采购计划
        Integer convertPurchaseFlag = finishSplitPurchasePlan(purchasePlanDOS, planItemDOS);
        // 处理源采购计划
        finishSourcePurchasePlan(purchasePlanDOS, planItemDOS,validateFlag);
        saleContractApi.updateSaleItemPurchaseFlag(saleContractItemIdList,convertPurchaseFlag);
    }

    private void finishSourcePurchasePlan(List<PurchasePlanDO> purchasePlanDOS, List<PurchasePlanItemDO> planItemDOS,boolean validateFlag) {
        List<PurchasePlanDO> sourcePlanDOList = purchasePlanDOS.stream().filter(s -> BooleanEnum.NO.getValue().equals(s.getSplitFlag())).toList();
        List<Long> saleContractItemIdList = planItemDOS.stream().map(PurchasePlanItemDO::getSaleContractItemId).distinct().toList();

        if (CollUtil.isEmpty(sourcePlanDOList) || CollUtil.isEmpty(planItemDOS)) {
            return;
        }
        List<Long> sourcePlanIdList = sourcePlanDOList.stream().map(PurchasePlanDO::getId).distinct().toList();
        // 如果有未作废的采购计划则不可作废源采购计划
        if (validateFlag && purchasePlanMapper.selectCount(new LambdaQueryWrapperX<PurchasePlanDO>().in(PurchasePlanDO::getSourcePlanId, sourcePlanIdList).eq(PurchasePlanDO::getSplitFlag, BooleanEnum.YES.getValue()).ne(PurchasePlanDO::getPlanStatus, PurchasePlanStatusEnum.CASE_CLOSED.getCode())) > 0) {
            throw exception(PURCHASE_PLAN_NOT_ALLOW_CANCEL);
        }
        // 回退销售明细数量及更改转采购标识
        List<Long> sourceSaleItemIdList = planItemDOS.stream().filter(s -> sourcePlanIdList.contains(s.getPurchasePlanId())).map(PurchasePlanItemDO::getSaleContractItemId).distinct().toList();
        if (CollUtil.isEmpty(sourceSaleItemIdList)) {
            return;
        }
        saleContractApi.rollbackSaleContractItemSourceList(sourceSaleItemIdList);
        // 重新锁定销售明细库存
        saleContractApi.reLockStock(saleContractItemIdList);
    }

    /**
     * 作废拆分采购计划
     *
     * @param purchasePlanDOS 采购计划
     * @param planItemDOS     采购计划明细
     */
    private Integer finishSplitPurchasePlan(List<PurchasePlanDO> purchasePlanDOS, List<PurchasePlanItemDO> planItemDOS) {
        Integer convertPurchaseFlag = BooleanEnum.NO.getValue();
        if (CollUtil.isEmpty(purchasePlanDOS) || CollUtil.isEmpty(planItemDOS)) {
            return convertPurchaseFlag;
        }
        List<PurchasePlanDO> splitPlanDOList = purchasePlanDOS.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getSplitFlag())).toList();
        if (CollUtil.isEmpty(splitPlanDOList)) {
            return convertPurchaseFlag;
        }
        List<Long> splitPlanIdList = splitPlanDOList.stream().map(PurchasePlanDO::getId).distinct().toList();
        List<PurchasePlanItemDO> itemDOList = planItemDOS.stream().filter(s -> splitPlanIdList.contains(s.getPurchasePlanId()) && Objects.nonNull(s.getSourcePlanItemId())).distinct().toList();
        if (CollUtil.isEmpty(itemDOList)) {
            return convertPurchaseFlag;
        }
        List<Long> saleContractItemIdList = itemDOList.stream().map(PurchasePlanItemDO::getSaleContractItemId).distinct().toList();
        // 回退源采购计划数量
        Map<Long, Integer> quantityMap = itemDOList.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getSourcePlanItemId, Collectors.summingInt(PurchasePlanItemDO::getPurchaseQuantity)));
        if (CollUtil.isNotEmpty(quantityMap)) {
            rollbackSourceItemQuantity(quantityMap);
        }
        // 删除对应销售明细
        if (CollUtil.isNotEmpty(saleContractItemIdList)) {
            return saleContractApi.deleteSplitSaleItem(saleContractItemIdList);
        }
        return convertPurchaseFlag;
    }

    /**
     * 作废回写源采购明细数量
     *
     * @param quantityMap 回退数量缓存
     */
    private void rollbackSourceItemQuantity(Map<Long, Integer> quantityMap) {
        if (CollUtil.isEmpty(quantityMap)) {
            return;
        }
        Set<Long> itemIdSet = quantityMap.keySet();
        List<PurchasePlanItemDO> itemDOList = purchasePlanItemMapper.selectBatchIds(itemIdSet);
        itemDOList.forEach(s -> {
            s.setNeedPurQuantity(s.getNeedPurQuantity() + quantityMap.get(s.getId()));
        });
        purchasePlanItemMapper.updateBatch(itemDOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purchasePlanItemToContract(List<Long> planItemIdList) {
        // 获取未转采购合同明细列表
        List<PurchasePlanItemRespVO> planItemList = purchasePlanItemService.getNoConvertPurchasePlanItemListByIdList(planItemIdList);
        if (Objects.isNull(planItemList)) {
            throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
        }

        //获取到的计划明细列表根据采购计划进行聚合，结果再根据供应商id聚合，目的是多个计划不进行合并
        Map<Long, Map<Long, List<PurchasePlanItemRespVO>>> itemMap = planItemList.stream().collect(
                Collectors.groupingBy(PurchasePlanItemRespVO::getPurchasePlanId,
                        Collectors.groupingBy(PurchasePlanItemRespVO::getVenderId)));

        List<Long> venderIdList = planItemList.stream().map(PurchasePlanItemRespVO::getVenderId).distinct().toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIdList);

        itemMap.forEach((key, value) -> {
            value.forEach((k, va) -> {
                PurchaseContractSaveInfoReqVO contractSaveInfoReqVO = new PurchaseContractSaveInfoReqVO();
                List<PurchaseContractItemSaveReqVO> itemSaveList = new ArrayList<>();
                List<Long> itemIdList = new ArrayList<>();

                va.forEach(v -> {
                    if (Objects.isNull(contractSaveInfoReqVO.getVenderCode())) {
                        Long venderId = v.getVenderId();
                        contractSaveInfoReqVO.setVenderCode(v.getVenderCode());
                        contractSaveInfoReqVO.setVenderId(venderId);
                        contractSaveInfoReqVO.setLinkCodeList(v.getLinkCodeList());
                        if (CollUtil.isNotEmpty(simpleVenderRespDTOList) && Objects.nonNull(venderId)) {
                            Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOList.stream().filter(i -> venderId.equals(i.getId())).findFirst();
                            if (first.isPresent()) {
                                SimpleVenderRespDTO simpleVenderRespDTO = first.get();
                                if (Objects.isNull(simpleVenderRespDTO)) {
                                    throw exception(VENDER_NOT_EXISTS);
                                }
                                List<VenderPaymentDTO> paymentList = simpleVenderRespDTO.getPaymentList();
                                if (CollUtil.isNotEmpty(paymentList)) {
                                    Optional<VenderPaymentDTO> optional = paymentList.stream().filter(s -> BooleanEnum.YES.getValue().intValue() == s.getDefaultFlag().intValue()).findFirst();
                                    if (optional.isPresent()) {
                                        VenderPaymentDTO venderPaymentDTO = optional.get();
                                        contractSaveInfoReqVO.setPaymentId(venderPaymentDTO.getPaymentId());
                                        contractSaveInfoReqVO.setPaymentName(venderPaymentDTO.getPaymentName());
                                        // 转换付款计划
                                        transformPurchasePlan(venderPaymentDTO.getPaymentId(), contractSaveInfoReqVO);
                                    }
                                }
                                List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                                if (CollUtil.isNotEmpty(taxMsg)){
                                    taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                        contractSaveInfoReqVO.setTaxType(s.getTaxType());
                                    });
                                }
                            }

                        }
                        PurchasePlanInfoRespVO purchasePlan = getPurchasePlan(new PurchasePlanDetailReq().setPurchasePlanId(v.getPurchasePlanId()));
                        if (Objects.isNull(purchasePlan))
                            throw exception(PURCHASE_PLAN_NOT_EXISTS);
                        contractSaveInfoReqVO.setPurchasePlanId(purchasePlan.getId());
                        contractSaveInfoReqVO.setPurchasePlanCode(purchasePlan.getCode());
                        contractSaveInfoReqVO.setCustId(purchasePlan.getCustId());
                        contractSaveInfoReqVO.setCustCode(purchasePlan.getCustCode());
                        contractSaveInfoReqVO.setRemark(purchasePlan.getRemark());
                        contractSaveInfoReqVO.setCompanyId(purchasePlan.getCompanyId());
                        contractSaveInfoReqVO.setDeliveryDate(purchasePlan.getPlanDate());
                        contractSaveInfoReqVO.setLinkCodeList(purchasePlan.getLinkCodeList());
                    }
                    itemIdList.add(v.getId());
                    PurchaseContractItemSaveReqVO itemSaveReqVO = BeanUtils.toBean(v, PurchaseContractItemSaveReqVO.class);
                    if (Objects.nonNull(itemSaveReqVO)) {
                        itemSaveReqVO.setSourceUniqueCode(itemSaveReqVO.getUniqueCode());
                        itemSaveReqVO.setSaleItemUniqueCode(itemSaveReqVO.getSaleItemUniqueCode());
                    }
                    itemSaveList.add(itemSaveReqVO);

                });
                itemSaveList.sort(Comparator.comparing(PurchaseContractItemSaveReqVO::getSortNum));
                itemSaveList.forEach(i -> {
                    i.setSortNum(itemSaveList.indexOf(i) + 1);
                });
                contractSaveInfoReqVO.setChildren(itemSaveList);
                contractSaveInfoReqVO.setCreateTime(null);

                purchaseContractService.createPurchaseContract(contractSaveInfoReqVO);
            });
        });
        //计划本身修改状态
        boolean updateResult = updatePurchasePlanStatusByItemMap(itemMap);
        if (!updateResult) {
            return;
        }
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectBatchIds(itemMap.keySet());
        if (CollUtil.isEmpty(purchasePlanDOS)) {
            return;
        }
        // 批量更新订单流状态
        List<OrderLinkDTO> orderLinkDTOList = purchasePlanDOS.stream().map(s -> {
            List<String> linkCodeList = s.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                return linkCodeList.stream().map(x -> new OrderLinkDTO(s.getCode(), BusinessNameDict.PURCHASE_PLAN_NAME, x, null, s.getSaleContractCode(), PurchasePlanStatusEnum.COMPLETED.getCode(), null)).toList();
            }
            return null;
        }).filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
        orderLinkApi.batchUpdateOrderLinkStatus(orderLinkDTOList, PurchasePlanStatusEnum.COMPLETED.getCode());
    }

    /**
     * 更新采购计划状态
     *
     * @param itemMap
     * @return
     */
    private boolean updatePurchasePlanStatusByItemMap(Map<Long, Map<Long, List<PurchasePlanItemRespVO>>> itemMap) {
        if (CollUtil.isEmpty(itemMap)) {
            return false;
        }
        Set<Long> purchasePlanIdSet = itemMap.keySet();
        if (CollUtil.isEmpty(purchasePlanIdSet)) {
            return false;
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getPurchasePlanId, purchasePlanIdSet);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return false;
        }
        Map<Long, List<PurchasePlanItemDO>> planItemMap = purchasePlanItemDOS.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getPurchasePlanId));
        if (CollUtil.isEmpty(planItemMap)) {
            return false;
        }
        List<PurchasePlanDO> updatePlanList = new ArrayList<>();
        planItemMap.forEach((k, v) -> {
            boolean isPurchaseFlag = v.stream().allMatch(s -> {
                Integer needPurQuantity = s.getNeedPurQuantity();
                Integer purchaseQuantity = Objects.isNull(s.getPurchaseQuantity()) ? 0 : s.getPurchaseQuantity();
                Integer convertedQuantity = Objects.isNull(s.getConvertedQuantity()) ? 0 : s.getConvertedQuantity();
                return needPurQuantity == purchaseQuantity + convertedQuantity;
            });
            PurchasePlanDO purchasePlanDO = new PurchasePlanDO();
            purchasePlanDO.setId(k);
            if (isPurchaseFlag) {
                purchasePlanDO.setPlanStatus(PurchasePlanStatusEnum.COMPLETED.getCode());
            } else {
                purchasePlanDO.setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode());
            }
            updatePlanList.add(purchasePlanDO);
        });
        return purchasePlanMapper.updateBatch(updatePlanList);
    }

    private void transformPurchasePlan(Long paymentId, PurchaseContractSaveInfoReqVO contractSaveInfoReqVO) {
        if (Objects.isNull(paymentId)) {
            return;
        }
        PaymentItemDTO paymentDTO = paymentItemApi.getPaymentDTO(paymentId);
        if (Objects.isNull(paymentDTO)) {
            throw exception(PAYMENT_NULL);
        }
        List<SystemPaymentPlanDTO> systemPaymentPlanDTOList = paymentDTO.getSystemPaymentPlanDTOList();
        List<PurchasePaymentPlan> purchasePaymentPlanList = PurchaseContractConvert.INSTANCE.convertPurchasePaymentPlan(systemPaymentPlanDTOList);
        if (CollUtil.isNotEmpty(purchasePaymentPlanList)) {
            purchasePaymentPlanList.forEach(s -> s.setPaymentName(paymentDTO.getName()));
        }
        contractSaveInfoReqVO.setPurchasePaymentPlanList(purchasePaymentPlanList);
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
    public PurchasePlanInfoRespVO getPurchasePlan(PurchasePlanDetailReq purchasePlanDetailReq) {

        Long purchasePlanId = Objects.nonNull(purchasePlanDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchasePlanDetailReq.getProcessInstanceId()) : purchasePlanDetailReq.getPurchasePlanId();
        if (Objects.isNull(purchasePlanId)) {
            logger.error("[采购计划]未获取到采购计划id");
            return null;
        }
        //根据采购计划ID获取采购计划
        PurchasePlanDO purchasePlanDO = purchasePlanMapper.selectById(purchasePlanId);
        if (Objects.isNull(purchasePlanDO)) {
            return null;
        }
        //采购计划DO->VO
        PurchasePlanInfoRespVO purchasePlanInfoRespVO = PurchasePlanConvert.INSTANCE.convertPurchasePlanRespVO(purchasePlanDO);
        //采购计划设置采购主体名称
        Map<Long, SimpleCompanyDTO> companyDTOMap = companyApi.getSimpleCompanyDTO();
        Long companyId = purchasePlanInfoRespVO.getCompanyId();
        if (Objects.nonNull(companyDTOMap)) {
            SimpleCompanyDTO simpleCompanyDTO = companyDTOMap.get(companyId);
            if (Objects.nonNull(simpleCompanyDTO)) {
                purchasePlanInfoRespVO.setCompanyName(simpleCompanyDTO.getCompanyName());
                purchasePlanInfoRespVO.setCompanyNature(simpleCompanyDTO.getCompanyNature());
            }
        }

        List<Long> parentIdList = new ArrayList<>(List.of(purchasePlanId));
        //获取采购计划明细列表
        List<PurchasePlanItemRespVO> itemRespVOList = purchasePlanItemService.getPurchasePlanItemListByPurchasePlanIdList(parentIdList);
        if (CollUtil.isNotEmpty(itemRespVOList)) {

            //如果供应商id为空  复制为默认报价的供应商
            itemRespVOList.forEach(s -> {

                if (s.getVenderId() == null) {
                    List<QuoteitemDTO> quoteitemDTOBySkuCode = quoteItemService.getQuoteitemDTOBySkuId(s.getAuxiliarySkuId());
                    if (CollUtil.isNotEmpty(quoteitemDTOBySkuCode)) {
                        Optional<QuoteitemDTO> first = quoteitemDTOBySkuCode.stream().filter(q -> q.getDefaultFlag() == 1).findFirst();
                        first.ifPresent(quoteitemDTO ->
                                s.setVenderId(quoteitemDTO.getVenderId())
                                        .setVenderCode(quoteitemDTO.getVenderCode())
                                        .setVenderName(quoteitemDTO.getVenderName()));
                    }
                }
            });

            //设置供应商信息
            List<Long> venderIdList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getVenderId).distinct().collect(Collectors.toList());


            List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIdList);
            List<Long> buyerIdList = simpleVenderRespDTOList.stream().flatMap(s -> s.getBuyerIds().stream()).distinct().toList();
            Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptListCache(buyerIdList);

            itemRespVOList.forEach(item -> {
                Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOList.stream().filter(s -> item.getVenderId() != null && item.getVenderId().equals(s.getId())).findFirst();
                if (first.isPresent()) {
                    item.setVenderName(first.get().getName());
                    List<UserDept> purchaseUserList = new ArrayList<>();
                    List<Long> buyerIds = first.get().getBuyerIds();
                    for (Long s : buyerIds) {
                        if (CollUtil.isNotEmpty(userDeptCache)) {
                            UserDept userDept = userDeptCache.get(s);
                            if (Objects.nonNull(userDept)) {
                                purchaseUserList.add(userDept);
                            }
                        }
                        item.setPurchaseUserList(purchaseUserList);
                    }
                }
            });
            //获取产品精简列表数据
            List<Long> idList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getSkuId).distinct().collect(Collectors.toList());
            List<Long> auxiliaryIdList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getAuxiliarySkuId).distinct().collect(Collectors.toList());
            Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(idList);
            Map<Long, SimpleSkuDTO> auxiliarySimpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(auxiliaryIdList);

            // 获取订单路径中收尾节点的公司主键
            Set<Long> companyIdSet = new HashSet<>();
            JsonCompanyPath companyPath = purchasePlanDO.getCompanyPath();
            if (Objects.nonNull(companyPath)) {
                Long lastCompanyId = TransformUtil.getLastCompanyId(companyPath);
                if (Objects.nonNull(lastCompanyId)) {
                    companyIdSet.add(lastCompanyId);
                }
                // 加工资质企业主键列表
                List<SimpleCompanyDTO> simpleCompanyDTOList = companyApi.listProducedCompany();
                if (CollUtil.isNotEmpty(simpleCompanyDTOList)) {
                    List<Long> producedCompanyIdList = simpleCompanyDTOList.stream().map(SimpleCompanyDTO::getId).toList();
                    companyIdSet.addAll(producedCompanyIdList);
                }
            }
            itemRespVOList.forEach(i -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        i.setMainPicture(simpleSkuDTO.getMainPicture());
                        i.setSkuName(simpleSkuDTO.getSkuName());
                        i.setSkuType(simpleSkuDTO.getSkuType());
                    }
                }
                if (CollUtil.isNotEmpty(auxiliarySimpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO1 = auxiliarySimpleSkuDTOMap.get(i.getAuxiliarySkuId());
                    if (Objects.nonNull(simpleSkuDTO1)) {
                        i.setAuxiliarySkuName(simpleSkuDTO1.getSkuName());
                    }
                }

                QueryStockReqVO queryStockReqVO = new QueryStockReqVO();
                queryStockReqVO.setCompanyIdList(new ArrayList<>(companyIdSet));
                queryStockReqVO.setSkuCode(i.getSkuCode());
//                queryStockReqVO.setSourceOrderCode(purchasePlanDO.getCode());
//                queryStockReqVO.setSourceOrderItemId(i.getId());
                queryStockReqVO.setSaleContractItemId(i.getSaleContractItemId());
                queryStockReqVO.setSourceOrderType(StockLockSourceTypeEnum.PURCHASE_PLAN.getValue());
                queryStockReqVO.setSaleContractCode(purchasePlanInfoRespVO.getSaleContractCode());
                List<StockDetailRespVO> stockDetailRespVOS = stockApi.listLockStock(queryStockReqVO);
                i.setStockDetailRespVOList(stockDetailRespVOS);

                List<StockLockSaveReqVO> stockLockSaveReqVOList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(stockDetailRespVOS)) {
                    stockDetailRespVOS.forEach(stockDetailRespVO -> {
                        if (ObjectUtil.isNotNull(stockDetailRespVO.getSourceOrderLockedQuantity())) {
                            StockLockSaveReqVO stockLockSaveReqVO = BeanUtils.toBean(stockDetailRespVO, StockLockSaveReqVO.class);
                            stockLockSaveReqVO.setStockId(stockDetailRespVO.getId());
//                            stockLockSaveReqVO.setSourceOrderId(purchasePlanDO.getId());
//                            stockLockSaveReqVO.setSourceOrderCode(purchasePlanDO.getCode());
                            stockLockSaveReqVO.setSourceOrderType(StockLockSourceTypeEnum.PURCHASE_PLAN.getValue());
                            stockLockSaveReqVO.setSaleContractItemId(i.getSaleContractItemId());
                            stockLockSaveReqVO.setSaleContractId(purchasePlanDO.getSaleContractId());
                            stockLockSaveReqVO.setSaleContractCode(purchasePlanDO.getSaleContractCode());

                            stockLockSaveReqVOList.add(stockLockSaveReqVO);
                        }
                    });
                }
                i.setStockLockSaveReqVOList(stockLockSaveReqVOList);
            });
            //绑定children
            purchasePlanInfoRespVO.setChildren(BeanUtils.toBean(itemRespVOList, PurchasePlanItemInfoRespVO.class));

            //设置汇总信息
            purchasePlanInfoRespVO.setSkuTypeCount(itemRespVOList.stream().map(PurchasePlanItemRespVO::getSkuCode).distinct().count());
            purchasePlanInfoRespVO.setSkuCount(itemRespVOList.stream().filter(s -> s.getNeedPurQuantity() != null).mapToInt(PurchasePlanItemRespVO::getNeedPurQuantity).sum());
            List<JsonAmount> result = new ArrayList<>();
            List<JsonAmount> amountList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getAmount).filter(Objects::nonNull).toList();
            if (CollUtil.isNotEmpty(amountList)) {
                Map<String, List<JsonAmount>> amountMap = amountList.stream().collect(Collectors.groupingBy(JsonAmount::getCurrency));
                if (CollUtil.isNotEmpty(amountMap)) {
                    amountMap.forEach((k, v) -> {
                        if (CollUtil.isNotEmpty(v)) {
                            Optional<BigDecimal> opt = v.stream().map(JsonAmount::getAmount).reduce(BigDecimal::add);
                            opt.ifPresent(bigDecimal -> result.add(new JsonAmount().setAmount(bigDecimal).setCurrency(k)));
                        }
                    });
                }
            }
            purchasePlanInfoRespVO.setSummary(result);
        }

//        if (CollUtil.isNotEmpty(purchasePlanInfoRespVO.getChildren())) {
//            purchasePlanInfoRespVO.getChildren().forEach(s -> {
//                s.setMeasureUnit(s.getSkuUnit());
//            });
//        }

        //采购单如果是辅料采购的产品相关,传参包含的采购产品
        if (Objects.equals(purchasePlanInfoRespVO.getAuxiliaryFlag(), BooleanEnum.YES.getValue())) {
            List<PurchasePlanItemInfoRespVO> children = purchasePlanInfoRespVO.getChildren();
            if (CollUtil.isEmpty(children)) {
                throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
            }
            List<String> contractCodeList = children.stream()
                    .map(PurchasePlanItemInfoRespVO::getAuxiliaryPurchaseContractCode)
                    .filter(StringUtils::isNotBlank).distinct().toList();
            if (CollUtil.isNotEmpty(contractCodeList)) {
                List<PurchaseContractItemRespVO> itemDOList = purchaseContractItemService.getContractItemListByContractCodeList(contractCodeList);
                if (CollUtil.isEmpty(itemDOList)) {
                    throw exception(PURCHASE_CONTRACT_ITEM_NOT_EXISTS);
                }
                List<Long> skuIdList = itemDOList.stream().map(PurchaseContractItemRespVO::getSkuId).distinct().toList();
                Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
                Map<String, List<PurchaseContractItemRespVO>> itemMap = itemDOList.stream().collect(Collectors.groupingBy(PurchaseContractItemRespVO::getPurchaseContractCode));
                children.forEach(s -> {
                    if (!Objects.equals(s.getAuxiliarySkuType(), PurchaseAuxiliaryTypeEnum.PRODUCT.getCode())) {
                        return;
                    }
                    List<SkuSimpleVO> skuList = new ArrayList<>();
                    if (Objects.isNull(s.getAuxiliaryPurchaseContractCode())) {
                        return;
                    }
                    List<PurchaseContractItemRespVO> itemDOS = itemMap.get(s.getAuxiliaryPurchaseContractCode());
                    if (CollUtil.isEmpty(itemDOS)) {
                        return;
                    }
                    itemDOS.forEach(i -> {
                        SkuSimpleVO simpleData = new SkuSimpleVO().setSkuId(i.getSkuId()).setSkuCode(i.getSkuCode());
                        if (CollUtil.isEmpty(simpleSkuDTOMap)) {
                            return;
                        }
                        SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                        if (Objects.isNull(simpleSkuDTO)) {
                            return;
                        }
                        simpleData.setSkuName(simpleSkuDTO.getSkuName());
                        skuList.add(simpleData);
                    });
                    s.setAuxiliarySkuList(skuList);
                });
            }
        }

        boolean auxiliaryFlag = Objects.equals(purchasePlanDO.getAuxiliaryFlag(), BooleanEnum.YES.getValue());
        String bpmProcessInstanceId;
        if (auxiliaryFlag) {
            bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(purchasePlanId, AUXILIARY_PROCESS_DEFINITION_KEY);
        } else {
            bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(purchasePlanId, PROCESS_DEFINITION_KEY);
        }
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            purchasePlanInfoRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }

        //库存相关
        List<QueryStockReqVO> queryStockReqVOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(purchasePlanInfoRespVO.getChildren())) {
            List<String> skuCodeList = purchasePlanInfoRespVO.getChildren().stream().map(PurchasePlanItemInfoRespVO::getSkuCode).toList();
            if (CollUtil.isNotEmpty(skuCodeList)) {
                skuCodeList.forEach(skuCode -> {
                    QueryStockReqVO queryStockReqVO = new QueryStockReqVO();
//                    queryStockReqVO.setCompanyIdList(companyIdList);
                    queryStockReqVO.setSkuCode(skuCode);
                    queryStockReqVOList.add(queryStockReqVO);
                });
            }
//            Map<String, Integer> skuTotalStockMap = stockApi.queryTotalStock(queryStockReqVOList);
            // 过滤出第一级明细
//            List<PurchasePlanItemInfoRespVO> filterPlanItemList = purchasePlanInfoRespVO.getChildren().stream().filter(child -> ObjectUtil.isNull(child.getLevels()) || (ObjectUtil.isNotNull(child.getLevels()) && child.getLevels() == 1)).toList();
            purchasePlanInfoRespVO.getChildren().forEach(c -> {
                c.setPlanStatus(purchasePlanInfoRespVO.getPlanStatus());
                c.setAuditStatus(purchasePlanInfoRespVO.getAuditStatus());
                List<StockDetailRespVO> stockDetailRespVOList = c.getStockDetailRespVOList();
                if (CollUtil.isNotEmpty(stockDetailRespVOList)) {
                    int sum = stockDetailRespVOList.stream().filter(s -> s.getAvailableQuantity() != null).mapToInt(StockDetailRespVO::getAvailableQuantity).sum();
                    c.setAvailableQuantity(sum);
                }
            });
//            purchasePlanInfoRespVO.setChildren(filterPlanItemList);
        }


        // 获得操作日志信息
        purchasePlanInfoRespVO.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, purchasePlanDO.getCode()));

        return purchasePlanInfoRespVO;
    }

    @Override
    public List<PurchasePlanItemToContractItemRespVO> getPurchaseMixPlanItem(Long planId, Long purchaseCompanyId, String planItemSkuCode, Integer demergeQuantity) {
        List<PurchasePlanItemToContractItemRespVO> purchasePlanItemToContractRespVOS = new ArrayList<>();

        if (demergeQuantity <= 0) {
            throw exception(PURCHASE_PLAN_ITEM_NEEDQUANTITY_ERROR);
        }
        Map<String, List<SimpleSkuDTO>> subSkuMap = skuApi.getSubSkuMap(Collections.singletonList(planItemSkuCode));
        if (ObjectUtil.isNull(subSkuMap) || CollUtil.isEmpty(subSkuMap.get(planItemSkuCode))) {
            throw exception(PURCHASE_PLAN_ITEM_SUB_EMPTY);
        }
        List<SimpleSkuDTO> simpleSkuDTOList = subSkuMap.get(planItemSkuCode);
        purchasePlanItemToContractRespVOS = BeanUtils.toBean(simpleSkuDTOList, PurchasePlanItemToContractItemRespVO.class);
        PurchasePlanInfoRespVO purchasePlan = this.getPurchasePlan(new PurchasePlanDetailReq().setPurchasePlanId(planId));

        // 批量查询产品可用库存
        List<QueryStockReqVO> queryStockReqVOList = new ArrayList<>();
        List<String> skuCodeList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getCode).toList();
        if (!CollUtil.isEmpty(skuCodeList)) {
            skuCodeList.forEach(skuCode -> {
                QueryStockReqVO queryStockReqVO = new QueryStockReqVO();
                queryStockReqVO.setCompanyIdList(Collections.singletonList(purchaseCompanyId));
                queryStockReqVO.setSkuCode(skuCode);
                queryStockReqVOList.add(queryStockReqVO);
            });
        }
        Map<String, Integer> skuTotalStockMap = stockApi.queryTotalStock(queryStockReqVOList);

        PurchasePlanItemInfoRespVO purchasePlanItemDO = purchasePlan.getChildren().get(0);
        purchasePlanItemToContractRespVOS.forEach(itemRespVO -> {
            itemRespVO.setPurchaseType(purchasePlanItemDO.getPurchaseType());
            itemRespVO.setFreeFlag(purchasePlanItemDO.getFreeFlag());
            if (!SkuTypeEnum.ACCESSORIES.getValue().equals(itemRespVO.getSkuType())) {
                Integer quantity = itemRespVO.getSonSkuCount() == null ? 0 : demergeQuantity * itemRespVO.getSonSkuCount();
                itemRespVO.setSaleQuantity(quantity);
                Integer qtyPerInnerBox = purchasePlanItemDO.getQtyPerInnerbox();
                if (quantity > 0 && qtyPerInnerBox != null && qtyPerInnerBox != 0) {
                    itemRespVO.setBoxCount(quantity / qtyPerInnerBox);
                }
            }
            String skuCode = itemRespVO.getCode();
            Integer availableQuantity = skuTotalStockMap.get(skuCode);
            itemRespVO.setAvailableQuantity(availableQuantity);
            itemRespVO.setNeedPurQuantity(itemRespVO.getSaleQuantity());
            itemRespVO.setCurrentLockQuantity(BigDecimal.ZERO.intValue());
            itemRespVO.setCustId(purchasePlanItemDO.getCustId());
            itemRespVO.setCustName(purchasePlanItemDO.getCustName());
            itemRespVO.setPurchaseUserId(purchasePlanItemDO.getPurchaseUserId());
            itemRespVO.setPurchaseUserName(purchasePlanItemDO.getPurchaseUserName());
            List<QuoteitemDTO> quoteitemList = itemRespVO.getQuoteitemList();

            if (CollUtil.isNotEmpty(quoteitemList)) {
                Optional<QuoteitemDTO> first1 = quoteitemList.stream().filter(q -> q.getDefaultFlag() == 1).findFirst();
                if (first1.isPresent()) {
                    String venderCode = first1.get().getVenderCode();
                    List<QuoteitemDTO> quoteItemListByVender = quoteitemList.stream().filter(q -> Objects.equals(q.getVenderCode(), venderCode)).toList();
                    itemRespVO.setQuoteitemList(quoteItemListByVender);
                }
            }

        });
        List<Long> skuIdList = purchasePlanItemToContractRespVOS.stream().map(PurchasePlanItemToContractItemRespVO::getId).distinct().toList();
        Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
        purchasePlanItemToContractRespVOS.forEach(s -> {
            s.setPurchaseQuantity(s.getNeedPurQuantity());
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                Boolean aBoolean = simpleSkuDTOMap.get(s.getId());
                if (Objects.isNull(aBoolean)) {
                    aBoolean = false;
                }
                s.setSkuDeletedFlag(aBoolean ? 0 : 1);
            }
            List<QuoteitemDTO> quoteitemList = s.getQuoteitemList();
            if (CollUtil.isNotEmpty(quoteitemList)) {
                List<Long> purchaseUserIdList = quoteitemList.stream().map(QuoteitemDTO::getPurchaseUserId).filter(Objects::nonNull).distinct().toList();
                Map<Long, UserDept> userDeptMap = adminUserApi.getUserDeptListCache(purchaseUserIdList);
                Optional<UserDept> quoteItemPurchaseUserOpt = quoteitemList.stream().filter(quoteitemDTO -> BooleanEnum.YES.getValue().equals(quoteitemDTO.getDefaultFlag())).map(quoteitemDTO -> {
                    if (CollUtil.isNotEmpty(userDeptMap)){
                        UserDept userDept = userDeptMap.get(quoteitemDTO.getPurchaseUserId());
                        if (Objects.nonNull(userDept)){
                            return new UserDept().setUserId(quoteitemDTO.getPurchaseUserId()).setNickname(userDept.getNickname()).setDeptId(userDept.getDeptId()).setDeptName(userDept.getDeptName());
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).findFirst();
                if (quoteItemPurchaseUserOpt.isPresent()) {
                    UserDept purchaseUser = quoteItemPurchaseUserOpt.get();
                    s.setPurchaseUserList(List.of(purchaseUser));
                    s.setPurchaseUserId(purchaseUser.getUserId());
                    s.setPurchaseUserName(purchaseUser.getNickname());
                }
            }


        });
        return purchasePlanItemToContractRespVOS;
    }

    @Override
    public PurchasePlanInfoRespVO getPurchasePlanContainsContract(PurchasePlanDetailReq purchasePlanDetailReq) {
        Long purchasePlanId = Objects.nonNull(purchasePlanDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchasePlanDetailReq.getProcessInstanceId()) : purchasePlanDetailReq.getPurchasePlanId();
        if (Objects.isNull(purchasePlanId)) {
            logger.error("[采购计划]未获取到采购计划id");
            return null;
        }
        PurchasePlanInfoRespVO purchasePlanInfoRespVO = getPurchasePlan(purchasePlanDetailReq);
        //设置采购合同信息
        List<PurchaseContractItemAndContractInfoRespVO> purchaseContractList = purchaseContractService.getPurchaseContractItemInfoListByPlanId(new ArrayList<>(List.of(purchasePlanId)));
        if (CollUtil.isNotEmpty(purchaseContractList)) {
            List<Long> venderIdList = purchaseContractList.stream().map(PurchaseContractItemAndContractInfoRespVO::getVenderId).distinct().collect(Collectors.toList());
            Map<Long, String> venderNameCache = venderService.getVenderNameCache(venderIdList);
            List<Long> idList = purchaseContractList.stream().map(PurchaseContractItemAndContractInfoRespVO::getSkuId).distinct().collect(Collectors.toList());
            Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(idList);
            //获取包装方式
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            if (CollUtil.isNotEmpty(purchaseContractList)) {
                purchaseContractList.forEach(contract -> {
                    //设置供应商信息
                    String venderName = venderNameCache.getOrDefault(contract.getVenderId(), null);
                    if (venderName != null) {
                        contract.setVenderName(venderName);
                    }
                    if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                        SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(contract.getSkuId());
                        if (Objects.nonNull(simpleSkuDTO)) {
                            contract.setMainPicture(simpleSkuDTO.getMainPicture());
                            contract.setSkuName(simpleSkuDTO.getSkuName());
                            contract.setSkuType(simpleSkuDTO.getSkuType());
                        }
                    }
                    contract.setCustName(purchasePlanInfoRespVO.getCustName());
                    if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(contract.getPackageType())) {
                        List<Long> distinctPackgeType = contract.getPackageType().stream().distinct().toList();
                        List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item -> distinctPackgeType.contains(item.getId())).toList();
                        if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                            List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                            contract.setPackageTypeName(String.join(",", packageTypeNameList));
                            List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                            contract.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                        }
                    }
                });
                purchasePlanInfoRespVO.setContractList(purchaseContractList);
            }
        }
        List<PurchasePlanItemInfoRespVO> children = purchasePlanInfoRespVO.getChildren();
        if (CollUtil.isNotEmpty(children)) {

            List<Long> skuIdList = children.stream().map(PurchasePlanItemInfoRespVO::getSkuId).distinct().toList();
            Map<Long, Boolean> simpleSkuDTOMap = skuApi.getSkuExitsByIds(skuIdList);
            children.forEach(s -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    Boolean aBoolean = simpleSkuDTOMap.get(s.getSkuId());
                    if (Objects.isNull(aBoolean)) {
                        aBoolean = false;
                    }
                    s.setSkuDeletedFlag(aBoolean ? 0 : 1);
                }
            });

            List<PurchasePlanItemInfoRespVO> planlist = children.stream().filter(x -> ObjectUtil.isNull(x.getLevels()) || (ObjectUtil.isNotNull(x.getLevels()) && x.getLevels() == 1)).toList();
            purchasePlanInfoRespVO.setPlanList(BeanUtils.toBean(planlist, PurchasePlanItemToContractSaveReqVO.class));
            List<PurchasePlanItemInfoRespVO> combineList = children.stream().filter(x -> ObjectUtil.isNull(x.getLevels()) || (ObjectUtil.isNotNull(x.getLevels()) && x.getLevels() != 1)).toList();
            purchasePlanInfoRespVO.setCombineList(BeanUtils.toBean(combineList, PurchasePlanItemToContractSaveReqVO.class));
        }

        // 加工资质企业主键列表
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyApi.listProducedCompany();
        if (CollUtil.isNotEmpty(simpleCompanyDTOList)) {
            List<Long> companyIdList = simpleCompanyDTOList.stream().map(SimpleCompanyDTO::getId).toList();
            purchasePlanInfoRespVO.setProducedCompanyIdList(companyIdList);

        }

        //设置当前可用币种
        Map<Long, SimpleCompanyDTO> companyDTOMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isNotEmpty(companyDTOMap)) {
            SimpleCompanyDTO simpleCompanyDTO = companyDTOMap.get(purchasePlanInfoRespVO.getCompanyId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                purchasePlanInfoRespVO.setCompanyCurrencyList(simpleCompanyDTO.getAvailableCurrencyList());
            }
        }
        if (purchasePlanInfoRespVO.getCreator() != null) {
            Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
            if (!userDeptCache.isEmpty()) {
                UserDept userDept = userDeptCache.get(Long.valueOf(purchasePlanInfoRespVO.getCreator()));
                if (userDept != null) {
                    purchasePlanInfoRespVO.setCreatorName(userDept.getNickname());
                    purchasePlanInfoRespVO.setCreatorDeptName(userDept.getDeptName());
                }
            }
        }
        return purchasePlanInfoRespVO;
    }

    @Override
    public PageResult<PurchasePlanInfoRespVO> getPurchasePlanPage(PurchasePlanPageReqVO pageReqVO) {

        Integer planStatus = pageReqVO.getPlanStatus();
        if (planStatus == null) {
            pageReqVO.setNeStatus(PurchasePlanStatusEnum.CASE_CLOSED.getCode());
        }
        PageResult<PurchasePlanDO> purchasePlanDOPageResult = purchasePlanMapper.selectPage(pageReqVO);
        List<PurchasePlanDO> purchasePlanDOList = purchasePlanDOPageResult.getList();
        if (CollUtil.isEmpty(purchasePlanDOList)) {
            return PageResult.empty();
        }
        Long total = purchasePlanDOPageResult.getTotal();
        List<Long> idList = purchasePlanDOList.stream().map(PurchasePlanDO::getId).distinct().toList();
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>();

        if (CollUtil.isNotEmpty(idList)) {
            queryWrapper = queryWrapper.in(PurchasePlanItemDO::getPurchasePlanId, idList);
        }
        List<PurchasePlanItemDO> planItemDOList = purchasePlanItemMapper.selectList(queryWrapper);

        if (CollUtil.isEmpty(planItemDOList)) {
            return PageResult.empty();
        }

        List<PurchasePlanItemRespVO> itemRespVOList = BeanUtils.toBean(planItemDOList, PurchasePlanItemRespVO.class);


        //增加业务逻辑判断  待采购的数据获取明细为待转换的数据,已完成的数据仅获取明细为已转换订单的数据
        if (Objects.nonNull(planStatus)) {
            if (planStatus.equals(PurchasePlanStatusEnum.COMPLETED.getCode())) {
                itemRespVOList = itemRespVOList.stream().filter(item -> item.getConvertedFlag() == 1).collect(Collectors.toList());  //过滤已转
            } else if (planStatus.equals(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode())) {
                itemRespVOList = itemRespVOList.stream().filter(item -> item.getConvertedFlag() == 0).collect(Collectors.toList());   //过滤未转
            }
        }

        if (CollUtil.isEmpty(itemRespVOList)) {
            return PageResult.empty();
        }
        List<Long> venderIdList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getVenderId).distinct().collect(Collectors.toList());
        Map<Long, String> venderNameCache = venderService.getVenderNameCache(venderIdList);
        List<Long> skuIdList = itemRespVOList.stream().map(PurchasePlanItemRespVO::getSkuId).distinct().collect(Collectors.toList());
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
        //获取包装方式
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        itemRespVOList.forEach(item -> {
            if (CollUtil.isNotEmpty(venderNameCache)) {
                String venderName = venderNameCache.getOrDefault(item.getVenderId(), null);
                if (venderName != null) {
                    item.setVenderName(venderName);
                }
            }
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuId());
                if (Objects.nonNull(simpleSkuDTO)) {
                    item.setMainPicture(simpleSkuDTO.getMainPicture());
                    item.setSkuName(simpleSkuDTO.getSkuName());
                    item.setSkuType(simpleSkuDTO.getSkuType());
                }
            }
            if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(item.getPackageType())) {
                List<Long> distinctPackgeType = item.getPackageType().stream().distinct().toList();
                List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackgeType.contains(pt.getId())).toList();
                if (Objects.nonNull(tempPackageTypeList)) {
                    List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                    item.setPackageTypeName(String.join(",", packageTypeNameList));
                    List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                    item.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                }
            }
        });
        Map<Long, List<PurchasePlanItemRespVO>> purchaseItemMap = itemRespVOList.stream().collect(
                Collectors.groupingBy(PurchasePlanItemRespVO::getPurchasePlanId));   //采购计划列表根据采购计划ID进行聚合分类，生成map

        List<String> userIdList = purchasePlanDOList.stream().map(PurchasePlanDO::getCreator).distinct().toList();
        Map<String, AdminUserRespDTO> userMap = adminUserApi.getUserMapByStringIdList(userIdList);

        List<PurchasePlanInfoRespVO> purchasePlanRespVOList = BeanUtils.toBean(purchasePlanDOList, PurchasePlanInfoRespVO.class);
        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        purchasePlanRespVOList.forEach(item -> {
            //item赋值
            if (CollUtil.isNotEmpty(purchaseItemMap)) {
                List<PurchasePlanItemRespVO> purchasePlanItemRespVOS = purchaseItemMap.get(item.getId());
                if (CollUtil.isNotEmpty(purchasePlanItemRespVOS)) {
                    item.setChildren(BeanUtils.toBean(purchasePlanItemRespVOS, PurchasePlanItemInfoRespVO.class));
                    //计算采购总数
                    Integer sumTotal = purchasePlanItemRespVOS.stream().filter(
                            s -> s.getNeedPurQuantity() != null).mapToInt(PurchasePlanItemRespVO::getNeedPurQuantity).sum();
                    item.setItemCountTotal(sumTotal);
                }
            }

            /*//公司路径转换 无数据库访问
            List<Long> companyIdList = new ArrayList<>();
            String companyPathStr = JsonUtils.toJsonString(item.getCompanyPath());
            JsonCompanyPath companyPath = JsonUtils.parseObject(companyPathStr, JsonCompanyPath.class);
            List<JsonCompanyPath> jsonCompanyPaths = companyPathService.delinkNodeByLevel(companyPath);
            if (!CollUtil.isEmpty(jsonCompanyPaths)) {
                companyIdList.add(jsonCompanyPaths.get(0).getId());
                if (jsonCompanyPaths.size() != 1) {
                    companyIdList.add(jsonCompanyPaths.get(jsonCompanyPaths.size() - 1).getId());
                }
            }
            //库存相关
            List<QueryStockReqVO> queryStockReqVOList = new ArrayList<>();
            if (CollUtil.isNotEmpty(item.getChildren())) {
                List<String> skuCodeList = item.getChildren().stream().map(PurchasePlanItemInfoRespVO::getSkuCode).toList();
                if (CollUtil.isNotEmpty(skuCodeList)) {
                    skuCodeList.forEach(skuCode -> {
                        QueryStockReqVO queryStockReqVO = new QueryStockReqVO();
                        queryStockReqVO.setCompanyIdList(companyIdList);
                        queryStockReqVO.setSkuCode(skuCode);
                        queryStockReqVOList.add(queryStockReqVO);
                    });
                }
                Map<String, Integer> skuTotalStockMap = stockApi.queryTotalStock(queryStockReqVOList);
                // 过滤出第一级明细
                List<PurchasePlanItemInfoRespVO> filterPlanItemList = item.getChildren().stream().filter(child -> ObjectUtil.isNull(child.getLevels()) || (ObjectUtil.isNotNull(child.getLevels()) && child.getLevels() == 1)).toList();
                filterPlanItemList.forEach(c -> {
                    c.setPlanStatus(item.getPlanStatus());
                    c.setAuditStatus(item.getAuditStatus());
                    if (CollUtil.isNotEmpty(skuTotalStockMap)) {
                        Integer availableQuantity = skuTotalStockMap.get(c.getSkuCode());
                        c.setAvailableQuantity(availableQuantity);
                    }
                });
                item.setChildren(filterPlanItemList);
            }*/


            //公司名称赋值
            Long companyId = item.getCompanyId();
            if (Objects.nonNull(simpleCompanyDTO)) {
                Optional.ofNullable(companyId).
                        filter(simpleCompanyDTO::containsKey)
                        .map(simpleCompanyDTO::get)
                        .map(SimpleCompanyDTO::getName)
                        .ifPresent(item::setCompanyName);
            }

            //创建人名称赋值
            if (item.getCreator() != null) {
                Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
                if (!userDeptCache.isEmpty()) {
                    UserDept userDept = userDeptCache.get(Long.valueOf(item.getCreator()));
                    if (userDept != null) {
                        item.setCreatorName(userDept.getNickname());
                        item.setCreatorDeptName(userDept.getDeptName());
                    }
                }
            }
        });

        Collections.sort(purchasePlanRespVOList, Comparator.comparing(PurchasePlanInfoRespVO::getId).reversed());
        //汇总数据
        Map<String, Object> summary = new HashMap<>();

        List<PurchaseItemSummaryDO> purchaseItemSummary = purchasePlanItemMapper.getPurchaseItemSummary(pageReqVO.getPlanStatus(), pageReqVO.getAuxiliaryFlag())
                .stream().filter(Objects::nonNull).toList();
        if (CollUtil.isNotEmpty(purchaseItemSummary)) {
            summary.put("qtySum", purchaseItemSummary.stream().filter(s -> s.getSumQty() != null).mapToInt(PurchaseItemSummaryDO::getSumQty).sum());
            summary.put("totalSum", purchaseItemSummary.stream().map(item ->
                    new JsonAmount().setCurrency(item.getCurrency()).setAmount(item.getSumTotal())));
        }
        return new PageResult<PurchasePlanInfoRespVO>().setList(purchasePlanRespVOList).setTotal(total).setSummary(summary);
    }


    @Override
    public PageResult<PurchasePlanItemInfoRespVO> getPurchasePlanBoardPage(PurchasePlanPageReqVO pageReqVO) {
        PageResult<PurchasePlanDO> purchasePlanDOPageResult = purchasePlanMapper.selectPage(pageReqVO);
        List<PurchasePlanDO> purchasePlanList;
        purchasePlanList = purchasePlanDOPageResult.getList();
        if (CollUtil.isEmpty(purchasePlanList)) {
            return PageResult.empty();
        }
        //根据用户接口的map填写用户名字
        Collection<Long> userIdList = purchasePlanList.stream().map(p ->
        {
            try {
                return Long.parseLong(p.getCreator());
            } catch (Exception exception) {
                logger.error(p.getCreator() + " 不是Integer");
                throw exception(PURCHASE_PLAN_USER_WRONG);
            }
        }).collect(Collectors.toList());
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIdList);

        List<Long> parentIdList = purchasePlanList.stream().map(PurchasePlanDO::getId).distinct().toList();  //获取采购计划列表ID
        List<PurchasePlanItemRespVO> itemRespVOList = purchasePlanItemService.getPurchasePlanItemListByPurchasePlanIdList(parentIdList);   //获取采购计划明细列表
        List<PurchasePlanItemInfoRespVO> itemInfoRespVOList = BeanUtils.toBean(itemRespVOList, PurchasePlanItemInfoRespVO.class);


        if (CollUtil.isEmpty(itemInfoRespVOList)) {
            return null;
        }
        List<Long> venderIdList = itemInfoRespVOList.stream().map(PurchasePlanItemRespVO::getVenderId).distinct().collect(Collectors.toList());
        List<Long> skuIdList = itemInfoRespVOList.stream().map(PurchasePlanItemRespVO::getSkuId).distinct().collect(Collectors.toList());
        Map<Long, String> venderNameCache = venderService.getVenderNameCache(venderIdList);
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
        Map<Long, SimpleCompanyDTO> simpleCompanyMap = companyApi.getSimpleCompanyDTO();
        List<Long> planIdList = itemInfoRespVOList.stream().map(PurchasePlanItemRespVO::getPurchasePlanId).distinct().collect(Collectors.toList());

        List<PurchaseContractItemAndContractInfoRespVO> purchaseContractItemInfoList = purchaseContractService.getPurchaseContractItemInfoListByPlanId(planIdList);
        Map<Long, List<PurchaseContractItemAndContractInfoRespVO>> contractMap;
        if (CollUtil.isNotEmpty(purchaseContractItemInfoList)) {
            contractMap = purchaseContractItemInfoList.stream().collect(Collectors.groupingBy(PurchaseContractItemAndContractInfoRespVO::getPurchasePlanId));
        } else {
            contractMap = null;
        }

        itemInfoRespVOList.forEach(item -> {
            Optional<PurchasePlanDO> first = purchasePlanList.stream().filter(p -> Objects.equals(p.getId(), item.getPurchasePlanId())).findFirst();
            if (first.isPresent()) {
                PurchasePlanDO purchasePlanDO = first.get();
                item.setCode(purchasePlanDO.getCode());
                item.setAuditStatus(purchasePlanDO.getVer());
                item.setPlanStatus(purchasePlanDO.getVer());
                item.setCustId(purchasePlanDO.getCustId());
                item.setCustCode(purchasePlanDO.getCustCode());
                item.setCustName(purchasePlanDO.getCustName());
                item.setSourceType(purchasePlanDO.getSourceType());
//                item.setSaleContractId(purchasePlanDO.getSaleContractId());
//                item.setSaleContractCode(purchasePlanDO.getSaleContractCode());
                item.setRemark(purchasePlanDO.getRemark());
                item.setCreateTime(purchasePlanDO.getCreateTime());
                item.setCreator(purchasePlanDO.getCreator());
                item.setUpdateTime(purchasePlanDO.getUpdateTime());
                item.setCreatorName(purchasePlanDO.getCustName());
                item.setCompanyId(purchasePlanDO.getCompanyId());
                item.setPlanDate(purchasePlanDO.getPlanDate());
            }
            String venderName = venderNameCache.getOrDefault(item.getVenderId(), null);
            if (venderName != null) {
                item.setVenderName(venderName);
            }
            if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuId());
                if (Objects.nonNull(simpleSkuDTO)) {
                    item.setMainPicture(simpleSkuDTO.getMainPicture());
                    item.setSkuName(simpleSkuDTO.getSkuName());
                    item.setSkuType(simpleSkuDTO.getSkuType());
                }
            }

            if (CollUtil.isNotEmpty(userMap)) {
                AdminUserRespDTO adminUserRespDTO = userMap.get(item.getPurchaseUserId());
                if (Objects.nonNull(adminUserRespDTO)) {
                    item.setCreatorName(adminUserRespDTO.getNickname());
                }
            }
            if (CollUtil.isNotEmpty(simpleCompanyMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyMap.get(item.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)) {
                    item.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
            //采购合同数据获取
            if (CollUtil.isNotEmpty(purchaseContractItemInfoList)) {
                if (CollUtil.isNotEmpty(contractMap) && Objects.nonNull(item.getPurchasePlanId())) {
                    List<PurchaseContractItemAndContractInfoRespVO> list = contractMap.get(item.getPurchasePlanId());
                    if (CollUtil.isEmpty(list)) {
                        return;
                    }
                    Optional<PurchaseContractItemAndContractInfoRespVO> first1 = list.stream().filter(Objects::nonNull).findFirst();
                    if (first1.isPresent()) {
                        PurchaseContractItemAndContractInfoRespVO purchaseContractItemAndContractInfoRespVO = first1.get();
                        item.setContractCode(purchaseContractItemAndContractInfoRespVO.getCode());
                        item.setContractQuantity(purchaseContractItemAndContractInfoRespVO.getQuantity());
                        item.setContractExchangedQuantity(purchaseContractItemAndContractInfoRespVO.getExchangeQuantity());
                        item.setContractReturnedQuantity(purchaseContractItemAndContractInfoRespVO.getReturnQuantity());
                        item.setContractReceivedQuantity(purchaseContractItemAndContractInfoRespVO.getReceivedQuantity());
                        item.setContractCheckedQuantity(purchaseContractItemAndContractInfoRespVO.getCheckedQuantity());
                        item.setContractCreateTime(purchaseContractItemAndContractInfoRespVO.getCreateTime());
                        item.setContractPlanTime(purchaseContractItemAndContractInfoRespVO.getDeliveryDate());
                        item.setContractFreight(purchaseContractItemAndContractInfoRespVO.getFreight());
                        item.setContractOtherCost(purchaseContractItemAndContractInfoRespVO.getOtherCost());
                    }
                }
            }

        });


        return new PageResult<PurchasePlanItemInfoRespVO>()
                .setList(itemInfoRespVOList);

    }

    @Override
    public List<PurchasePlanItemAndPlanRespVO> getPurchasePlanItem(Long purchasePlanId) {
        List<PurchasePlanItemAndPlanRespVO> result = new ArrayList<>();
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, purchasePlanId).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(queryWrapper);
        Optional<PurchasePlanDO> purchasePlanDO = purchasePlanMapper.selectList(PurchasePlanDO::getId, purchasePlanId).stream().findFirst();
        List<Long> list = purchasePlanItemDOS.stream().map(PurchasePlanItemDO::getSkuId).toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(list);
        if (CollUtil.isNotEmpty(purchasePlanItemDOS) && purchasePlanDO.isPresent()) {
            List<PurchasePlanItemRespVO> purchasePlanItemRespVOS = PurchasePlanItemConvert.INSTANCE.convertRespVOList(purchasePlanItemDOS);
            if (CollUtil.isNotEmpty(purchasePlanItemRespVOS)) {
                result = BeanUtils.toBean(purchasePlanItemRespVOS, PurchasePlanItemAndPlanRespVO.class);
                result.forEach(item -> {
                    PurchasePlanInfoRespVO purchasePlanRespVO = BeanUtils.toBean(purchasePlanDO.get(), PurchasePlanInfoRespVO.class);
                    item.setPurchasePlan(purchasePlanRespVO);
                    if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                        if (Objects.nonNull(item.getSkuId())) {
                            SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuId());
                            if (Objects.nonNull(simpleSkuDTO)) {
                                item.setSkuName(simpleSkuDTO.getSkuName());
                            }
                        }
                    }
                });
            }
        }

        return result;
    }

    @Override
    public List<PurchasePlanItemToContractRespVO> getPurchasePlanToContractList(List<Long> planItemIdList) {
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().in(PurchasePlanItemDO::getId, planItemIdList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(queryWrapper);

        if (CollUtil.isNotEmpty(purchasePlanItemDOS)) {
            //过滤已转数据
//            List<PurchasePlanItemDO> list = purchasePlanItemDOS.stream().filter(s -> s.getConvertedFlag() == 0).toList();
            if (CollUtil.isNotEmpty(purchasePlanItemDOS)) {
                List<PurchasePlanItemToContractRespVO> result = BeanUtils.toBean(purchasePlanItemDOS, PurchasePlanItemToContractRespVO.class);
                List<Long> venderIdList = result.stream().map(PurchasePlanItemToContractRespVO::getVenderId).toList();
                List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIdList);
                List<Long> buyerIdList = simpleVenderRespDTOList.stream().flatMap(s -> s.getBuyerIds().stream()).distinct().toList();
                Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(buyerIdList);

                if (CollUtil.isNotEmpty(result)) {
                    List<String> skuCodeList = result.stream().map(PurchasePlanItemRespVO::getSkuCode).toList();
                    List<String> auxiliarySkuIdList = result.stream().map(PurchasePlanItemRespVO::getAuxiliarySkuCode).toList();
                    //供应商名称赋值
                    Map<String, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMapByCode(skuCodeList);
                    Map<String, SimpleSkuDTO> auxiliarySimpleSkuDTOMap = skuApi.getSimpleSkuDTOMapByCode(auxiliarySkuIdList);

                    List<Long> planIdList = result.stream().map(PurchasePlanItemRespVO::getPurchasePlanId).distinct().toList();
                    List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectList(PurchasePlanDO::getId, planIdList);

                    result.forEach(s -> {
                        if (CollUtil.isNotEmpty(purchasePlanDOS)) {
                            Optional<PurchasePlanDO> first = purchasePlanDOS.stream().filter(p -> Objects.equals(p.getId(), s.getPurchasePlanId())).findFirst();
                            first.ifPresent(planDO -> s.setSourceType(planDO.getSourceType()));
                        }

                        //设置供应商名称
                        if (CollUtil.isNotEmpty(simpleVenderRespDTOList)) {
                            Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOList.stream().filter(v -> Objects.equals(v.getId(), s.getVenderId())).findFirst();
                            first.ifPresent(simpleVenderRespDTO -> {
                                s.setVenderName(simpleVenderRespDTO.getName());
                                List<Long> buyerIds = simpleVenderRespDTO.getBuyerIds();
                                List<UserDept> userDepts = new ArrayList<>();
                                buyerIds.forEach(b -> {
                                    if (CollUtil.isNotEmpty(userDeptListCache)) {
                                        userDepts.add(userDeptListCache.get(b));
                                    }
                                });
                                s.setPurchaseUserList(userDepts);
                            });
                        }
                        //设置物料名称
                        if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                            SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(s.getSkuId());
                            if (Objects.nonNull(simpleSkuDTO)) {
                                s.setSkuName(simpleSkuDTO.getSkuName());
                                s.setMainPicture(simpleSkuDTO.getMainPicture());
                            }
                        }
                        if (CollUtil.isNotEmpty(auxiliarySimpleSkuDTOMap)) {
                            SimpleSkuDTO simpleSkuDTO = auxiliarySimpleSkuDTOMap.get(s.getAuxiliarySkuId());
                            if (Objects.nonNull(simpleSkuDTO)) {
                                s.setAuxiliarySkuName(simpleSkuDTO.getSkuName());

                            }
                        }
                    });

                    if (CollUtil.isNotEmpty(skuCodeList)) {
                        //调用skuAPI获取组合产品详情
                        Map<String, List<SimpleSkuDTO>> subSkuMap = skuApi.getSubSkuMap(skuCodeList);

                        if (CollUtil.isNotEmpty(subSkuMap)) {
                            result.forEach(s -> {
                                List<SimpleSkuDTO> simpleSkuDTOS = subSkuMap.get(s.getSkuId());
                                if (CollUtil.isNotEmpty(simpleSkuDTOS)) {
                                    List<PurchasePlanItemToContractItemRespVO> purchasePlanItemToContractRespVOS = BeanUtils.toBean(simpleSkuDTOS, PurchasePlanItemToContractItemRespVO.class);
                                    purchasePlanItemToContractRespVOS.forEach(i -> {
                                        i.setPurchaseType(s.getPurchaseType());
                                        i.setFreeFlag(s.getFreeFlag());
                                        if (!SkuTypeEnum.ACCESSORIES.getValue().equals(i.getSkuType())) {
                                            Integer quantity = i.getSonSkuCount() == null ? 0 : s.getNeedPurQuantity() * i.getSonSkuCount();
                                            i.setNeedPurQuantity(quantity);
                                            Integer qtyPerInnerBox = s.getQtyPerInnerbox();
                                            if (quantity > 0 && qtyPerInnerBox != null && qtyPerInnerBox != 0) {
                                                i.setBoxCount(quantity / qtyPerInnerBox);
                                            }
                                        }

                                        i.setPurchaseUserId(s.getPurchaseUserId());
                                        i.setPurchaseUserName(s.getPurchaseUserName());
                                        i.setSourceType(s.getSourceType());
                                        //根据采购员过滤报价数据
//                                        Map<Long, List<QuoteitemDTO>> quoteDTOMap = i.getQuoteitemList().stream().collect(Collectors.groupingBy(QuoteitemDTO::getPurchaseUserId));
                                        List<QuoteitemDTO> quoteitemList = i.getQuoteitemList();

                                        if (CollUtil.isNotEmpty(quoteitemList)) {
                                            Optional<QuoteitemDTO> first1 = quoteitemList.stream().filter(q -> q.getDefaultFlag() == 1).findFirst();
                                            if (first1.isPresent()) {
                                                String venderCode = first1.get().getVenderCode();
                                                List<QuoteitemDTO> quoteitemListByVender = quoteitemList.stream().filter(q -> Objects.equals(q.getVenderCode(), venderCode)).toList();
                                                i.setQuoteitemList(quoteitemListByVender);
                                            }

//
//                                             List<Long> quoteVenderIdList = quoteitemList.stream().map(QuoteitemDTO::getVenderId).toList();
//                                             List<SimpleVenderRespDTO> simpleVenderRespDTOList1 = venderService.getSimpleVenderRespDTOList(quoteVenderIdList);
//
//                                            List<QuoteitemDTO> combineQuoteItemList = new ArrayList<>();
//
//                                            quoteitemList.forEach(quoteitemDTO->{
////                                                Long purchaseUserId = quoteitemDTO.getPurchaseUserId();
////                                                if(Objects.nonNull(purchaseUserId) && CollUtil.isNotEmpty(simpleVenderRespDTOList1)){
////                                                    Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOList1.stream().filter(k ->k.getBuyerIds().contains(purchaseUserId)).findFirst();
////                                                    if(first.isPresent()){
////                                                        List<Long> idList = first.get().getBuyerIds();
////                                                        if(Objects.nonNull(idList)&& idList.contains(purchaseUserId)){
////                                                            combineQuoteItemList.add(quoteitemDTO);
////                                                        }
////                                                    }
////                                                }
//                                            });
//                                            i.setQuoteitemList(combineQuoteItemList);
                                        }

                                    });
                                    s.setCombineList(purchasePlanItemToContractRespVOS);
                                }
                            });
                        }
                    }
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean checkOwnBrandFlag(Long id) {
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, id).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> planItemDOList = purchasePlanItemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(planItemDOList)) {
            return false;
        }
        return planItemDOList.stream().anyMatch(s -> BooleanEnum.YES.getValue().equals(s.getOwnBrandFlag()));
    }

    @Override
    public String getConditionKey() {
        return CONDITION_KEY;
    }

    @Override
    public RelatedOrderRespVO getRelatedOrderNum(Long id) {
        // 获取销售合同数量
        PurchasePlanDO purchasePlanDO = purchasePlanMapper.selectById(id);
        if (Objects.isNull(purchasePlanDO)) {
            return null;
        }
        String saleContractCode = purchasePlanDO.getSaleContractCode();
        Long saleContractNum = Objects.nonNull(saleContractCode) ? 1L : 0L;
        // 获取采购合同数量
        Long purchaseContractNum = purchaseContractService.getPurchaseContractNumByPlanId(id);
        purchaseContractNum = Objects.nonNull(purchaseContractNum) ? purchaseContractNum : 0L;
        return new RelatedOrderRespVO().setSaleContractNum(saleContractNum).setPurchaseContractNum(purchaseContractNum);
    }

    @Override
    @DataPermission(enable = false)
    public Long getOrderNumBySaleContractId(Long contractId) {
        return purchasePlanMapper.selectCount(PurchasePlanDO::getSaleContractId, contractId);
    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        PurchasePlanDO purchasePlanDO = validatePurchasePlanExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        purchasePlanDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        purchasePlanDO.setPlanStatus(PurchasePlanStatusEnum.PENDING_SUBMIT.getCode());
        int i = purchasePlanMapper.updateById(purchasePlanDO);
        return i > 0;
    }

    @Override
    public List<PurchasePlanItemDO> getItemListBySaleContractId(Long id) {

        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectList(PurchasePlanDO::getSaleContractId, id);
        if (CollUtil.isEmpty(purchasePlanDOS)) {
            return null;
        }
        List<Long> doList = purchasePlanDOS.stream().map(PurchasePlanDO::getId).distinct().toList();
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().in(PurchasePlanItemDO::getPurchasePlanId, doList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        return purchasePlanItemMapper.selectList(queryWrapper);

    }

    @Override
    public void batchUpdatePlanStatus(List<Long> planIdList, Integer planStatus) {
        if (CollUtil.isEmpty(planIdList)) {
            return;
        }
        List<PurchasePlanDO> list = planIdList.stream().map(id -> {
            PurchasePlanDO purchasePlanDO = new PurchasePlanDO();
            purchasePlanDO.setId(id);
            purchasePlanDO.setPlanStatus(planStatus);
            return purchasePlanDO;
        }).toList();
        purchasePlanMapper.updateBatch(list);
    }

    @Override
    public boolean validatePurContractItemExists(List<Long> saleContractItemIds) {
        if (CollUtil.isEmpty(saleContractItemIds)) {
            return false;
        }
        return purchasePlanItemMapper.selectCount(new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getSaleContractItemId, saleContractItemIds).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue())) > CalculationDict.ZERO;
    }

    /**
     * 校验反审核状态
     *
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        validatePurchasePlanExists(id);
//        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, id);
//        List<PurchasePlanItemDO> planItemDOS = purchasePlanItemMapper.selectList(queryWrapper);
//        if (CollUtil.isEmpty(planItemDOS)) {
//            throw exception(PURCHASE_PLAN_ITEM_NULL);
//        }
//        List<String> saleContractCodeList = planItemDOS.stream().map(PurchasePlanItemDO::getSaleContractCode).filter(Objects::nonNull).distinct().toList();
//        // 库存采购不需要校验后续单据
//        if (CollUtil.isEmpty(saleContractCodeList)) {
//            return;
//        }
        Long count = purchasePlanMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    private String getNewCode(String code, String saleContractCode) {
        return codeGeneratorApi.getCodeGenerator(SN_TYPE, code+"R", false, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataPermission(enable = false)
    public List<CreatedResponse> purchasePlanToContract(PurchasePlanItemToContractSaveInfoReqVO reqVO) {
        boolean convertedFlag = reqVO.getPlanList().stream().anyMatch(s -> ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag()));
        boolean childConvertedFlag = reqVO.getCombineList().stream().anyMatch(s -> ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag()));
        if ((convertedFlag||childConvertedFlag)&&BooleanEnum.YES.getValue().equals(reqVO.getSubmitFlag())){
            throw exception(PURCHASE_PLAN_CONVERTED_NOT_ALLOW_TO_CONTRACT);
        }
        List<CreatedResponse> responses = new ArrayList<>();
        boolean exists = purchasePlanMapper.exists(new LambdaQueryWrapperX<PurchasePlanDO>().eq(PurchasePlanDO::getId, reqVO.getId()).ne(PurchasePlanDO::getPlanStatus, PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode()));
        if (exists){
            throw exception(PURCHASE_PLAN_NOT_ALLOW_TO_CONTRACT);
        }
        //校验收款金额
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
        if (BooleanEnum.YES.getValue().equals(reqVO.getSubmitFlag())){
            // 外币采购只能组合采购
            if (SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(reqVO.getSaleType())){
                List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
                if (CollUtil.isNotEmpty(combineList)) {
                        throw exception(PURCHASE_PLAN_COMBINE_NOT_ALLOW_TO_CONTRACT);
                }
                valicateInnerVender(planList,reqVO.getCombineList(),true);
            }else {
                // 下推采购合同的时候，如果是普通产品 或者 组合产品不拆分采购，则不允许报价是内部供应商的产品下推
                boolean combineAndProductMix = planList.stream().anyMatch(s -> SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())
                        && PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())
                        && venderService.checkInnerVender(s.getVenderCode())
                        && s.getPurchaseQuantity() > 0
                );
                valicateInnerVender(planList,null,false);
                if (combineAndProductMix) {
                    throw exception(INNER_VENDER_PRODUCT_EXCEPTION);
                }
                List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
                if (CollUtil.isNotEmpty(combineList)) {
                    List<PurchasePlanItemToContractSaveReqVO> twoSplitList = combineList.stream().filter(s -> Objects.nonNull(s.getOneSplitNum()) && Objects.nonNull(s.getTwoSplitNum()) && Objects.isNull(s.getThreeSplitNum())).collect(Collectors.toList());
                    valicateInnerVender(null,combineList,false);
                    boolean twoCombineAndProductMix = twoSplitList.stream().anyMatch(s -> SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())
                            && PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())
                            && venderService.checkInnerVender(s.getVenderCode())
                            && s.getPurchaseQuantity() > 0);
                    if (twoCombineAndProductMix){
                        throw exception(INNER_VENDER_PRODUCT_EXCEPTION);
                    }
                }
            }


        }
        // 拆分销售明细
        Map<Long, SimpleSaleItemDTO> saleItemQuantityMap = splitSaleItem(reqVO);
        //拆分采购计划-判断是否需要拆分
        if (CollUtil.isNotEmpty(reqVO.getCombineList())) {
            List<PurchasePlanItemToContractSaveReqVO> resourcePlanList = BeanUtil.copyToList(reqVO.getPlanList(), PurchasePlanItemToContractSaveReqVO.class);
            if (CollUtil.isNotEmpty(resourcePlanList)) {
                resourcePlanList.forEach(s -> {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                });
            }
            List<PurchasePlanItemToContractSaveReqVO> resourceCombineList = reqVO.getCombineList();
            if (CollUtil.isNotEmpty(resourceCombineList)) {
                resourceCombineList.forEach(s -> {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                });
            }
            List<PurchasePlanItemToContractSaveReqVO> newPlanList = reqVO.getPlanList();
            if (CollUtil.isNotEmpty(newPlanList)) {
                newPlanList.forEach(s -> {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                });
            }
            List<PurchasePlanItemToContractSaveReqVO> newCombineList = reqVO.getCombineList();
            if (CollUtil.isNotEmpty(newCombineList)) {
                newCombineList.forEach(s -> {
                    s.setUniqueCode(IdUtil.fastSimpleUUID());
                });
            }
            PurchasePlanItemToContractSaveInfoReqVO resourceReq = new PurchasePlanItemToContractSaveInfoReqVO();
            BeanUtil.copyProperties(reqVO, resourceReq);
            PurchasePlanItemToContractSaveInfoReqVO newReq = new PurchasePlanItemToContractSaveInfoReqVO();
            BeanUtil.copyProperties(reqVO, newReq);
            // 替换拆分销售信息
            replaceSaleItemMsg(newReq, saleItemQuantityMap);
            //处理特殊情况：如果已转为0，并且本次全部拆分，不拆分
            List<PurchasePlanItemToContractSaveReqVO> notSplitPlans = new ArrayList<>();
            resourcePlanList.forEach(s -> {
                if ((s.getConvertedFlag() == 1 && Objects.equals(s.getPurchaseQuantity(), 0)) || (s.getConvertedFlag() == 0 && Objects.equals(s.getPurchaseQuantity(), s.getNeedPurQuantity()))) {
                    notSplitPlans.add(s);
                }
                if (Objects.equals(s.getPurchaseModel(), PurchaseModelEnum.COMBINE.getCode()) && !notSplitPlans.contains(s)) {
                    s.setNeedPurQuantity(s.getNeedPurQuantity() - s.getPurchaseQuantity() - s.getConvertedQuantity());
                    s.setPurchaseQuantity(0);
                    s.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode());
                    s.setBoxCount((int) Math.ceil(s.getQtyPerOuterbox() != 0 ? (double) s.getNeedPurQuantity() / s.getQtyPerOuterbox() : 0));
                }
            });
            List<PurchasePlanItemToContractSaveReqVO> parentPlanList = resourcePlanList.stream().filter(s -> (PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel()) && s.getPurchaseQuantity() == 0) || (!PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel()) || Objects.equals(s.getPurchaseQuantity(), s.getNeedPurQuantity()))).toList();
            List<Integer> notSplitNumbs = notSplitPlans.stream().map(PurchasePlanItemRespVO::getOneSplitNum).toList();
            resourceReq.setCombineList(null);
            Set<Integer> parentCombineCodeSet = new HashSet<>();
            if (Objects.nonNull(resourceCombineList)) {
                List<PurchasePlanItemToContractSaveReqVO> purchasePlanItemToContractSaveReqVOS = resourceCombineList.stream().filter(s -> notSplitNumbs.contains(s.getOneSplitNum())).toList();
                if (CollUtil.isNotEmpty(purchasePlanItemToContractSaveReqVOS)) {
                    purchasePlanItemToContractSaveReqVOS.forEach(s -> {
                        if (BooleanEnum.NO.getValue().equals(s.getConvertedFlag())) {
                            s.setId(null);
                        }
                    });
                    resourceReq.setCombineList(purchasePlanItemToContractSaveReqVOS);
                }
                purchasePlanItemToContractSaveReqVOS.forEach(s->parentCombineCodeSet.add(s.getOneSplitNum()));
            }
            resourceReq.setPlanList(parentPlanList.stream().filter(s ->parentCombineCodeSet.contains(s.getOneSplitNum())||s.getPurchaseModel().equals(PurchaseModelEnum.STANDARD.getCode())||(Objects.isNull(s.getPurchaseQuantity())||s.getPurchaseQuantity() == 0)).collect(Collectors.toList()));
            PurchasePlanDO purchasePlanDO = purchasePlanMapper.selectById(reqVO.getId());
            newPlanList = newPlanList.stream().filter(s ->
                    Objects.equals(s.getPurchaseModel(), PurchaseModelEnum.COMBINE.getCode())
                            || (CollUtil.isNotEmpty(notSplitNumbs)&&!notSplitNumbs.contains(s.getOneSplitNum()))
            ).toList();

            List<PurchasePlanItemDO> sourceItems = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getPurchasePlanId, purchasePlanDO.getId()).stream().toList();

            List<PurchasePlanItemToContractSaveReqVO> finalNewPlanList = newPlanList;
            sourceItems.forEach(s -> {
                if (notSplitNumbs.contains(s.getOneSplitNum())) {
                    return;
                }
                Optional<PurchasePlanItemToContractSaveReqVO> first = finalNewPlanList.stream().filter(c -> Objects.equals(c.getId(), s.getId())).findFirst();
                if (first.isPresent()) {
                    s.setNeedPurQuantity(s.getNeedPurQuantity() - s.getPurchaseQuantity());
                    s.setPurchaseQuantity(0);
                    s.setPurchaseModel(PurchaseModelEnum.STANDARD.getCode());
                    s.setBoxCount((int) Math.ceil(s.getQtyPerOuterbox() != 0 ? (double) s.getNeedPurQuantity() / s.getQtyPerOuterbox() : 0));
                }
            });
            newCombineList = newCombineList.stream().filter(s -> !notSplitNumbs.contains(s.getOneSplitNum())).toList();
            newReq.setCombineList(newCombineList);
            List<Integer> newCombineCodeList = newCombineList.stream().map(PurchasePlanItemToContractSaveReqVO::getOneSplitNum).distinct().toList();
            if (CollUtil.isNotEmpty(newCombineList)) {
                purchasePlanDO.setCode(getNewCode(purchasePlanDO.getCode(), purchasePlanDO.getSaleContractCode()));
                purchasePlanDO.setSourcePlanId(purchasePlanDO.getId());
                purchasePlanDO.setId(null);
                purchasePlanMapper.insert(purchasePlanDO);
                newPlanList.forEach(s -> {
                    s.setConvertedQuantity(0);
                    s.setSourcePlanItemId(s.getId());
                    s.setId(null);
                    s.setNeedPurQuantity(s.getPurchaseQuantity());
                    s.setLockQuantity(0);
                    s.setStockLockSaveReqVOList(new ArrayList<>());
                    s.setCurrentLockQuantity(0);
                    s.setSaleLockQuantity(0);
                    s.setPurchasePlanId(purchasePlanDO.getId());
                    s.setPurchasePlanCode(purchasePlanDO.getCode());
                    s.setBoxCount((int) Math.ceil(s.getQtyPerOuterbox() != 0 ? (double) s.getNeedPurQuantity() / s.getQtyPerOuterbox() : 0));
                });
                newReq.setPlanList(newPlanList.stream().filter(s->newCombineCodeList.contains(s.getOneSplitNum())||s.getPurchaseModel().equals(PurchaseModelEnum.STANDARD.getCode())).collect(Collectors.toList()));
                newCombineList.forEach(s -> {
                    s.setId(null);
                    s.setPurchasePlanId(purchasePlanDO.getId());
                    s.setPurchasePlanCode(purchasePlanDO.getCode());
                    s.setBoxCount((int) Math.ceil(s.getQtyPerOuterbox() != 0 ? (double) s.getNeedPurQuantity() / s.getQtyPerOuterbox() : 0));
                });
                newReq.setId(purchasePlanDO.getId()).setCode(purchasePlanDO.getCode());
                newReq.setSplitFlag(BooleanEnum.YES.getValue());
                responses.addAll(pushToContract(newReq, saleItemQuantityMap, false));
            }
            responses.addAll(pushToContract(resourceReq, saleItemQuantityMap, false));
            if(BooleanEnum.YES.getValue().equals(reqVO.getSubmitFlag())){
                return responses;
            }
        } else {
            List<CreatedResponse> createdResponses = pushToContract(reqVO, saleItemQuantityMap, true);
            if(BooleanEnum.YES.getValue().equals(reqVO.getSubmitFlag())){
                return createdResponses;
            }
        }
        return List.of();
    }
    private void valicateInnerVender(List<PurchasePlanItemToContractSaveReqVO> planList,List<PurchasePlanItemToContractSaveReqVO> combineList,boolean forigFlag){
        if (CollUtil.isNotEmpty(planList)){
            boolean simFlag = planList.stream().anyMatch(s -> {
                if (forigFlag){
                    return  venderService.checkInnerVender(s.getVenderCode())
                            && s.getPurchaseQuantity() > 0;
                }else {
                    return !SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())
                            && venderService.checkInnerVender(s.getVenderCode())
                            && s.getPurchaseQuantity() > 0;
                }
            }
            );
            if (simFlag){
                throw exception(NOT_ALLOW_SIM_VENDER_CONVERTED);
            }
        }
      if (CollUtil.isNotEmpty(combineList)){
          List<PurchasePlanItemToContractSaveReqVO> twoSplitList = combineList.stream().filter(s -> Objects.nonNull(s.getOneSplitNum()) && Objects.nonNull(s.getTwoSplitNum()) && Objects.isNull(s.getThreeSplitNum())).collect(Collectors.toList());
          boolean innerFlag = twoSplitList.stream().anyMatch(s -> !SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())
                  && PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())
                  && venderService.checkInnerVender(s.getVenderCode())
                  && s.getPurchaseQuantity() > 0);
          if (innerFlag){
              throw exception(NOT_ALLOW_SIM_VENDER_CONVERTED);
          }
      }
    }
    private void rollbackCombinePlan(List<PurchasePlanItemToContractSaveReqVO> combineList) {
        // 回写拆分信息
        if (CollUtil.isNotEmpty(combineList)) {
            List<SplitPurchase> transformSplitList = new ArrayList<>();
            combineList.forEach(s -> {
                SplitPurchase splitPurchase = BeanUtils.toBean(s, SplitPurchase.class);
                splitPurchase.setSplitCompanyId(s.getPurchaseCompanyId());
                splitPurchase.setSplitCompanyName(s.getPurchaseCompanyName());
                transformSplitList.add(splitPurchase);
            });
            List<SplitPurchase> splitList = transformSplitList.stream().filter(s -> Objects.nonNull(s.getSaleContractItemId())).toList();
            if (CollUtil.isNotEmpty(splitList)) {
                // 回写销售明细拆分信息
                saleContractApi.rollbackSaleContractItemSplitList(splitList);
            }
        }
    }
    /**
     * 替换拆分后销售明细信息 仅处理拆分采购情况
     *
     * @param reqVO
     */
    private void replaceSaleItemMsg(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<Long, SimpleSaleItemDTO> saleItemQuantityMap) {
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
        List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
        List<PurchasePlanItemSaveReqVO> children = reqVO.getChildren();
        if (CollUtil.isEmpty(planList) || CollUtil.isEmpty(combineList) || CollUtil.isEmpty(saleItemQuantityMap) || CollUtil.isEmpty(children)) {
            return;
        }
        planList.forEach(s -> {
            if (saleItemQuantityMap.containsKey(s.getSaleContractItemId())) {
                SimpleSaleItemDTO simpleSaleItemDTO = saleItemQuantityMap.get(s.getSaleContractItemId());
                if (Objects.nonNull(simpleSaleItemDTO)) {
                    s.setSaleContractItemId(simpleSaleItemDTO.getId());
                    s.setSourceUniqueCode(simpleSaleItemDTO.getUniqueCode());
                }
            }
        });
        reqVO.setPlanList(planList);
        combineList.forEach(s -> {
            if (saleItemQuantityMap.containsKey(s.getSaleContractItemId())) {
                SimpleSaleItemDTO simpleSaleItemDTO = saleItemQuantityMap.get(s.getSaleContractItemId());
                if (Objects.nonNull(simpleSaleItemDTO)) {
                    s.setSaleContractItemId(simpleSaleItemDTO.getId());
                    s.setSourceUniqueCode(simpleSaleItemDTO.getUniqueCode());
                }
            }
        });
        reqVO.setCombineList(combineList);
        children.forEach(s -> {
            if (saleItemQuantityMap.containsKey(s.getSaleContractItemId())) {
                SimpleSaleItemDTO simpleSaleItemDTO = saleItemQuantityMap.get(s.getSaleContractItemId());
                if (Objects.nonNull(simpleSaleItemDTO)) {
                    s.setSaleContractItemId(simpleSaleItemDTO.getId());
                    s.setSourceUniqueCode(simpleSaleItemDTO.getUniqueCode());
                }
            }
        });
        reqVO.setChildren(children);
    }

    private Map<Long, SimpleSaleItemDTO> splitSaleItem(PurchasePlanItemToContractSaveInfoReqVO purchasePlanInfoSaveReqVO) {
        List<PurchasePlanItemToContractSaveReqVO> purchasePlanItemSaveReqVOList = purchasePlanInfoSaveReqVO.getPlanList();
        List<Long> saleItemIdList = purchasePlanItemSaveReqVOList.stream().map(PurchasePlanItemToContractSaveReqVO::getSaleContractItemId).filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isNotEmpty(saleItemIdList)) {
            List<Long> splitIds = purchaseContractItemService.validateSplitSales(saleItemIdList);
            // 第一次下推时仅回写真实采购数量
            Map<Long, Integer> updateRealPurchaseQuantityMap = purchasePlanItemSaveReqVOList.stream().filter(s -> (s.getPurchaseQuantity() > 0 && Objects.isNull(s.getSaleContractItemId())) || !splitIds.contains(s.getSaleContractItemId())).collect(Collectors.toMap(PurchasePlanItemToContractSaveReqVO::getSaleContractItemId, PurchasePlanItemRespVO::getPurchaseQuantity, (o, n) -> o));
            if (CollUtil.isNotEmpty(updateRealPurchaseQuantityMap)) {
                saleContractApi.rollbackSaleContractItemPurchaseQuantity(updateRealPurchaseQuantityMap);
            }
            // 校验拆分销售明细  仅判断一级产品
            Map<Long, Tuple> saleItemQuantity = purchasePlanItemSaveReqVOList.stream().filter(s -> Objects.nonNull(s.getSaleContractItemId()) && splitIds.contains(s.getSaleContractItemId())).collect(Collectors.toMap(PurchasePlanItemToContractSaveReqVO::getSaleContractItemId, s -> {
                int LockQuantity = Objects.isNull(s.getLockQuantity()) ? 0 : s.getLockQuantity();
                return new Tuple(s.getPurchaseQuantity(), LockQuantity, List.of());
            }, (o, n) -> o));
            List<PurchasePlanItemToContractSaveReqVO> combineList = purchasePlanInfoSaveReqVO.getCombineList();
            Map<Long, Tuple> updateQuantityMap = new HashMap<>();
            if (CollUtil.isNotEmpty(saleItemQuantity)) {
                updateQuantityMap.putAll(saleItemQuantity);
            }
            if (CollUtil.isNotEmpty(combineList)) {
                Map<Long, List<PurchasePlanItemToContractSaveReqVO>> combineMap = combineList.stream().filter(s -> Objects.nonNull(s.getSaleContractItemId())).collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getSaleContractItemId));
                if (CollUtil.isNotEmpty(saleItemQuantity)) {
                    saleItemQuantity.forEach((k, v) -> {
                        List<PurchasePlanItemToContractSaveReqVO> purchasePlanItemToContractSaveReqVOS = combineMap.get(k);
                        if (CollUtil.isNotEmpty(purchasePlanItemToContractSaveReqVOS)) {
                            List<SplitPurchase> transformSplitList = BeanUtils.toBean(purchasePlanItemToContractSaveReqVOS, SplitPurchase.class);
                            updateQuantityMap.put(k, new Tuple(v.get(0), v.get(1), transformSplitList));
                        }
                    });
                }
            }
            return saleContractApi.splitSaleContractItem(updateQuantityMap);
        }
        return Map.of();
    }

    private List<CreatedResponse> pushToContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<Long, SimpleSaleItemDTO> saleItemQuantityMap, boolean combineFlag) {
        rollbackCombinePlan(reqVO.getCombineList());
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
        if (CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }
        List<Long> venderIds = planList.stream().map(PurchasePlanItemToContractSaveReqVO::getVenderId).distinct().toList();
        List<SimpleVenderRespDTO> venderRespDTOList = venderService.getSimpleVenderRespDTOList(venderIds);
        if (venderIds.size() == 0 || venderIds.size() < venderRespDTOList.size()) {
            throw exception(VENDER_ERROR);
        }
        return generateLastNodePurchaseContract(reqVO, saleItemQuantityMap, combineFlag);

    }


    @Override
    public List<CreatedResponse> generateLastNodePurchaseContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<Long, SimpleSaleItemDTO> saleItemQuantityMap, boolean combineFlag) {
        List<CreatedResponse> responses = new ArrayList<>();
        Long purchasePlanId = reqVO.getId();
        Integer submitFlag = reqVO.getSubmitFlag();
        // 0. 校验采购计划明细参数
        List<PurchasePlanItemToContractSaveReqVO> planItemList = BeanUtils.toBean(reqVO.getPlanList(), PurchasePlanItemToContractSaveReqVO.class);
//        planItemList = planItemList.stream().filter(s -> s.getPurchaseQuantity() > 0).collect(Collectors.toList());
        // 1. 更新采购计划信息
        // 添加组合产品
        List<PurchasePlanItemToContractSaveReqVO> combineItemList = reqVO.getCombineList();
        if (!CollectionUtils.isEmpty(combineItemList)) {
            // 校验锁定数量
            combineItemList.forEach(x -> {
                if (x.getCurrentLockQuantity() != null && x.getCurrentLockQuantity() > x.getSaleQuantity()) {
                    throw exception(PURCHASE_PLAN_ITEM_LOCK_EXCEED, x.getSkuName());
                }
            });
            planItemList.addAll(combineItemList);
        }
        if (CollectionUtils.isEmpty(planItemList)) {
            throw exception(PURCHASE_PLAN_ITEM_NULL);
        }
        // 校验锁定数量
        planItemList.forEach(x -> {
            if (x.getCurrentLockQuantity() != null && x.getCurrentLockQuantity() > x.getSaleQuantity()) {
                throw exception(PURCHASE_PLAN_ITEM_LOCK_EXCEED, x.getSkuName());
            }
        });
        PurchasePlanInfoSaveReqVO purchasePlanInfoSaveReqVO = BeanUtils.toBean(reqVO, PurchasePlanInfoSaveReqVO.class);
        List<PurchasePlanItemSaveReqVO> purchasePlanItemSaveReqVOList = BeanUtils.toBean(planItemList, PurchasePlanItemSaveReqVO.class);
//        purchasePlanItemSaveReqVOList = purchasePlanItemSaveReqVOList.stream().filter(s->s.getPurchaseQuantity() > 0).toList();
        // 按层级排序，设置序号
        purchasePlanItemSaveReqVOList.stream().sorted(Comparator.comparing(PurchasePlanItemSaveReqVO::getLevels));
        AtomicInteger sortAto = new AtomicInteger(0);
        purchasePlanItemSaveReqVOList.forEach(x -> {
            x.setSortNum(sortAto.addAndGet(1));
        });
        // 取消前端随机生成的二、三级组合产品主键 非多次作废重新下推子产品情况
        if (combineFlag) {
            purchasePlanItemSaveReqVOList.forEach(x -> {
                if (x.getLevels() != 1) {
                    x.setId(null);
                }
            });
        }
        purchasePlanInfoSaveReqVO.setChildren(purchasePlanItemSaveReqVOList);
        purchasePlanInfoSaveReqVO.setToContractFlag(BooleanEnum.YES.getValue());
        Set<Long> itemIdSet = this.updatePurchasePlan(purchasePlanInfoSaveReqVO);
        // 如果存在组套件产品，更新采购计划明细中二、三级关联主键
//        if (!CollectionUtils.isEmpty(combineItemList)) {
//            List<PurchasePlanItemToContractSaveReqVO> twoLevelsPlanItemList = combineItemList.stream().filter(x -> x.getLevels().intValue() == 2).toList();
//            if (!CollectionUtils.isEmpty(twoLevelsPlanItemList)) {
//                this.handleCombinePurchasePlanItem(purchasePlanId, 1);
//            }
//            List<PurchasePlanItemToContractSaveReqVO> threeLevelsPlanItemList = combineItemList.stream().filter(x -> x.getLevels().intValue() == 3).toList();
//            if (!CollectionUtils.isEmpty(threeLevelsPlanItemList)) {
//                this.handleCombinePurchasePlanItem(purchasePlanId, 2);
//            }
//        }
        if (submitFlag.intValue() == BooleanEnum.YES.getValue()) {
            // 4. 若锁定库存中存在加工型企业，生成锁定库存的购销合同
//            this.handleProducedLockContract(reqVO, new HashMap<>());
            // 5. 转换尾部节点向供应商采购合同，存在内部供应商时进行转换
            responses.addAll(this.lastCompanyToVenderContract(reqVO, new HashMap<>(), false, saleItemQuantityMap));
            // 更新采购计划明细转采购标识及数量
            if (CollUtil.isNotEmpty(itemIdSet)){
                Integer planStatus = purchasePlanItemService.updatePurchasePlanItemConvertedFlag(itemIdSet,purchasePlanId);
                purchasePlanMapper.updateById(new PurchasePlanDO().setId(purchasePlanId).setPlanStatus(planStatus));
            }
            // 补充操作日志明细
            String code = reqVO.getCode();
            OperateLogUtils.setContent(String.format("【采购计划转采购合同】%s", code));
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
        } else {
            // 补充操作日志明细
            String code = reqVO.getCode();
            OperateLogUtils.setContent(String.format("【采购计划拆分编辑修改】%s", code));
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
        }
        return responses;
    }

    /**
     * 生成尾部节点与加工型企业的销售合同
     *
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    @Override
    public List<CreatedResponse> handleLastToProducedContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
        List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
        List<String> skuCodeList = planList.stream().map(PurchasePlanItemToContractSaveReqVO::getSkuCode).toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
        // 筛选组合产品分开采购的明细
        List<PurchasePlanItemToContractSaveReqVO> filteredPlanlist = planList.stream().filter(x -> {
            // 产品类型-组合产品
            SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
            Integer skuType = skuDTO.getSkuType();
            // 采购模式-组套件采购
            Integer purchaseModel = x.getPurchaseModel();
            return skuType.intValue() == SkuTypeEnum.PRODUCT_MIX.getValue()
                    && purchaseModel.intValue() == PurchaseModelEnum.COMBINE.getCode();
        }).toList();
        if (CollectionUtils.isEmpty(filteredPlanlist)) {
            return responses;
        }

        // 获取采购计划尾部节点
        PurchasePlanDO purchasePlanDO = super.getById(reqVO.getId());
        JsonCompanyPath companyPath = purchasePlanDO.getCompanyPath();
        JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(companyPath);
        Long lastCompanyId = lastCompanyPath.getId() != null ? lastCompanyPath.getId() : reqVO.getCompanyId();

        Long saleContractId = reqVO.getSaleContractId();
        // 加工资质企业主键
        Long producedCompanyId = combineList.stream().findFirst().get().getPurchaseCompanyId();
        // 生成采购合同
        /*PurchaseContractSaveInfoReqVO purchaseContractSaveInfoReqVO = BeanUtils.toBean(reqVO, PurchaseContractSaveInfoReqVO.class);

        List<PurchaseContractItemSaveReqVO> purchaseContractItemSaveReqVOList = PurchasePlanConvert.INSTANCE.convertToContractSaveReqVOList(filteredPlanlist);
        int sortNum = 1;
        int totalQuantity = 0;
        for (PurchaseContractItemSaveReqVO vo : purchaseContractItemSaveReqVOList) {
            String skuCode = vo.getSkuCode();
            Integer quantity = vo.getQuantity();
            vo.setQuantity(quantity);
            totalQuantity += quantity;
            // 设置采购单价
            JsonAmount unitPrice = lastNodeSkuPriceMap.get(skuCode);
            if (ObjectUtil.isNotNull(unitPrice)) {
                vo.setUnitPrice(unitPrice);
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
            purchaseContractSaveInfoReqVO.setPurchaseUserId(userDept.getUserId()).setPurchaseUserName(userDept.getNickname())
                    .setPurchaseUserDeptId(userDept.getDeptId()).setPurchaseUserDeptName(userDept.getDeptName());
        }
        purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
        purchaseContractSaveInfoReqVO.setSaleContractId(saleContractId);
        purchaseContractSaveInfoReqVO.setSaleContractCode(reqVO.getSaleContractCode());

        purchaseContractSaveInfoReqVO.setCompanyId(lastCompanyId);
        purchaseContractSaveInfoReqVO.setPurchasePlanId(reqVO.getId());
        purchaseContractSaveInfoReqVO.setPurchasePlanCode(reqVO.getCode());
        purchaseContractSaveInfoReqVO.setDeliveryDate(reqVO.getPlanDate());
        purchaseContractSaveInfoReqVO.setPurchaseTime(LocalDateTime.now());
        SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(producedCompanyId);
        if (Objects.nonNull(simpleVenderRespDTO)) {
            // 供应商信息
            purchaseContractSaveInfoReqVO.setVenderId(simpleVenderRespDTO.getId());
            purchaseContractSaveInfoReqVO.setVenderCode(simpleVenderRespDTO.getCode());

            purchaseContractSaveInfoReqVO.setTaxType(simpleVenderRespDTO.getTaxType());
        } else {
            throw exception(PURCHASE_PLAN_COMPANY_NO_VENDER_ERROR);
        }

        purchaseContractSaveInfoReqVO.setId(null);
        purchaseContractSaveInfoReqVO.setCreateTime(null);
        purchaseContractSaveInfoReqVO.setRemark(reqVO.getRemark());
        purchaseContractSaveInfoReqVO.setSubmitFlag(BooleanEnum.YES.getValue());
        purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
        purchaseContractSaveInfoReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
        purchaseContractService.createPurchaseContract(purchaseContractSaveInfoReqVO);*/

        // 生成销售合同
        SaleContractSaveDTO saleContractInfo = BeanUtils.toBean(reqVO, SaleContractSaveDTO.class);
        saleContractInfo.setId(null);
        saleContractInfo.setCode(null);
        saleContractInfo.setCheckFlag(Boolean.FALSE);
        saleContractInfo.setSubmitFlag(BooleanEnum.YES.getValue());
        saleContractInfo.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        saleContractInfo.setAutoFlag(BooleanEnum.YES.getValue());
        saleContractInfo.setCompanyId(lastCompanyId);
        saleContractInfo.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
        if (ObjectUtil.isNotNull(saleContractId)) {
            saleContractInfo = saleContractApi.getSaleContractInfo(saleContractId);
            saleContractInfo.setSourceContractId(saleContractId);
            saleContractInfo.setSourceContractCode(saleContractInfo.getCode());
        }
        SimpleCompanyDTO lastCompanyDTO = companyApi.getCompanyDTO(lastCompanyId);
        if (ObjectUtil.isNotNull(lastCompanyDTO)) {
            Integer companyNature = lastCompanyDTO.getCompanyNature();
            if (companyNature.intValue() == CompanyNatureEnum.INTERNAL_CUST.getValue()) {
                saleContractInfo.setSaleType(SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue());
            } else {
                saleContractInfo.setSaleType(SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue());
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
        List<SaleContractItemSaveDTO> saleContractItemSaveDTOList = new ArrayList<>();
        AtomicInteger sortNum = new AtomicInteger(1);
        filteredPlanlist.forEach(x -> {
            SaleContractItemSaveDTO contractItemSaveDTO = PurchasePlanConvert.INSTANCE.purchasePlanItemToContractSaveReqVOToSaleContractItemSaveDTO(x);
//            contractItemSaveDTO.setForecastTotalCost(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CommonDict.EMPTY_STR));
//            contractItemSaveDTO.setTrailerFee(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CommonDict.EMPTY_STR));
            contractItemSaveDTO.setId(null).setQuantity(x.getPurchaseQuantity()).setCurrentLockQuantity(BigDecimal.ZERO.intValue());
            contractItemSaveDTO.setPurchaseUser(new UserDept().setUserId(x.getPurchaseUserId()).setNickname(x.getPurchaseUserName()).setDeptId(x.getPurchaseUserDeptId()).setDeptName(x.getPurchaseUserDeptName()));
            contractItemSaveDTO.setTotalSaleAmount(changeCurrency(x.getTotalPrice(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setPurchaseCurrency(x.getCurrency());
            contractItemSaveDTO.setPurchasePackagingPrice(changeCurrency(x.getPackagingPrice(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setPurchaseWithTaxPrice(changeCurrency(x.getWithTaxPrice(), simpleCustRespDTO.getCurrency()));
            contractItemSaveDTO.setName(x.getSkuName());
            JsonAmount unitPrice = lastNodeSkuPriceMap.get(x.getSkuCode());
            if (ObjectUtil.isNotNull(unitPrice)) {
                contractItemSaveDTO.setUnitPrice(changeCurrency(unitPrice, simpleCustRespDTO.getCurrency()));
            }
            contractItemSaveDTO.setSortNum(sortNum.get());
            sortNum.getAndIncrement();
            saleContractItemSaveDTOList.add(contractItemSaveDTO);
        });
        saleContractInfo.setChildren(saleContractItemSaveDTOList);

        saleContractInfo.setCompanyId(producedCompanyId);
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(producedCompanyId);
        if (Objects.nonNull(companyDTO)) {
            saleContractInfo.setCompanyName(companyDTO.getCompanyName());
        }

        saleContractInfo.setInternalCustId(simpleCustRespDTO.getId());
        saleContractInfo.setInternalCustCode(simpleCustRespDTO.getCode());
        saleContractInfo.setInternalCustName(simpleCustRespDTO.getName());
        saleContractInfo.setAutoFlag(BooleanEnum.YES.getValue());
        saleContractInfo.setCustId(simpleCustRespDTO.getId());
        saleContractInfo.setCustCode(simpleCustRespDTO.getCode());
        saleContractInfo.setCustName(simpleCustRespDTO.getName());
        saleContractInfo.setCurrency(simpleCustRespDTO.getCurrency());
//        saleContractInfo.setPlatformFee(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CommonDict.EMPTY_STR));

        log.info("尾部节点与加工型企业-生成销售合同：{}", JsonUtils.toJsonString(saleContractInfo));
        CalcContactUtil.checkItemCost(saleContractInfo, configApi.getConfigMap(), rateApi.getDailyRateMap(), saleContractInfo.getCurrency());
        return saleContractApi.generateSaleContract(saleContractInfo);
    }

    /**
     * 更新二、三级产品明细关联主键
     *
     * @param purchasePlanId
     */
    private void handleCombinePurchasePlanItem(Long purchasePlanId, Integer levels) {
        PurchasePlanInfoRespVO purchasePlanRespVO = this.getPurchasePlan(new PurchasePlanDetailReq().setPurchasePlanId(purchasePlanId));
        List<PurchasePlanItemInfoRespVO> purchasePlanItemInfoRespVOList = purchasePlanRespVO.getChildren();
        if (CollectionUtils.isEmpty(purchasePlanItemInfoRespVOList)) {
            return;
        }
        List<PurchasePlanItemSaveReqVO> purchasePlanItemSaveReqVOList = BeanUtils.toBean(purchasePlanItemInfoRespVOList, PurchasePlanItemSaveReqVO.class);
        // 获取一、二级产品明细的主键
        Map<String, Long> planItemSkuMap = new HashMap<>();
        purchasePlanItemSaveReqVOList.forEach(x -> {
            if (x.getLevels() == levels.intValue()) {
                planItemSkuMap.put(x.getSkuCode(), x.getId());
            }
        });
        // 更新二、三级组合产品的关联主键
        List<PurchasePlanItemSaveReqVO> combinePlanItemList = purchasePlanItemSaveReqVOList.stream().filter(x -> x.getLevels() == (levels.intValue() + 1)).toList();
        if (CollUtil.isNotEmpty(combinePlanItemList)) {
            combinePlanItemList.forEach(x -> {
                String skuCode = x.getSkuCode();
                Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(Collections.singletonList(skuCode));
                SkuDTO skuDTO = skuDTOMap.get(skuCode);
                if (ObjectUtil.isNotNull(skuDTO)) {
                    Long parentSkuId = skuDTO.getParentSkuId();
                    if (Objects.nonNull(parentSkuId) && CollUtil.isNotEmpty(planItemSkuMap)) {
                        SkuDTO parentSkuDTO = skuApi.getSkuDTO(parentSkuId);
                        if (Objects.nonNull(parentSkuDTO)) {
                            Long nextLevelPlanItemId = planItemSkuMap.get(parentSkuDTO.getCode());
                            x.setSourceId(nextLevelPlanItemId);
                        }
                    }
                }
            });
            List<Long> combinePlanItemIdlist = combinePlanItemList.stream().map(PurchasePlanItemSaveReqVO::getId).toList();
            purchasePlanItemService.removeBatchByIds(combinePlanItemIdlist);
            List<PurchasePlanItemDO> purchasePlanItemDOList = BeanUtils.toBean(combinePlanItemList, PurchasePlanItemDO.class);
            purchasePlanItemDOList.forEach(x -> x.setId(null));
            purchasePlanItemService.saveBatch(purchasePlanItemDOList);
        }
    }


    /**
     * 若锁定库存中存在加工型企业，生成锁定库存的购销合同
     *
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    @Override
    @Deprecated
    public List<CreatedResponse> handleProducedLockContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap) {
        List<CreatedResponse> responses = new ArrayList<>();
        // 1. 查询加工资质的公司及采购计划产品明细
        // 获取可加工的公司列表
        List<SimpleCompanyDTO> producedCompanyDTOList = companyApi.listProducedCompany();
        if (CollectionUtils.isEmpty(producedCompanyDTOList)) {
            return responses;
        }
        List<Long> producedCompanyIdList = producedCompanyDTOList.stream().map(SimpleCompanyDTO::getId).toList();

        // 采购计划产品列表，即一级明细
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();
        if (CollectionUtils.isEmpty(planList)) {
            return responses;
        }

        // 获取采购计划尾部节点
        PurchasePlanDO purchasePlanDO = super.getById(reqVO.getId());
        JsonCompanyPath companyPath = purchasePlanDO.getCompanyPath();
        JsonCompanyPath lastCompanyPath = TransformUtil.getLastCompanyPath(companyPath);
        Long purchaseCompanyId = lastCompanyPath.getId() != null ? lastCompanyPath.getId() : reqVO.getCompanyId();

        // 2. 过滤当前计划的锁定库存中，存在加工资质企业的锁定明细
        List<StockLockSaveReqVO> stockLockSaveReqVOS = new ArrayList<>();
        planList.stream().forEach(x -> {
            if (!CollectionUtils.isEmpty(x.getStockLockSaveReqVOList())) {
                stockLockSaveReqVOS.addAll(x.getStockLockSaveReqVOList());
            }
        });
        if (CollectionUtils.isEmpty(stockLockSaveReqVOS)) {
            return responses;
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
            return responses;
        }
        if (CollectionUtils.isEmpty(producedCompanySkuMap)) {
            return responses;
        }
        // 3. 生成尾部节点至具有加工资质企业的购销合同
        // 汇总产品锁定库存数量
        Map<Long, Integer> producedStockLockCollect = producedCompanyStockLockList.stream().filter(s -> s.getSourceOrderLockedQuantity() != null).collect(Collectors.toMap(StockLockSaveReqVO::getSaleContractItemId, StockLockSaveReqVO::getSourceOrderLockedQuantity, Integer::sum));
        // 生成合同
        return this.genProducedContract(reqVO, producedCompanySkuMap, producedStockLockCollect, lastNodeSkuPriceMap, purchaseCompanyId);

    }

    /**
     * 生成有加工资质企业的合同
     *
     * @param reqVO
     * @param producedCompanySkuMap 加工资质企业及产品信息，key->公司主键，value->产品明细编码列表
     */
    @Deprecated
    private List<CreatedResponse> genProducedContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<Long, List<Long>> producedCompanySkuMap,
                                                      Map<Long, Integer> producedStockLockCollect, Map<String, JsonAmount> lastNodeSkuPriceMap, Long purchaseCompanyId) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<PurchasePlanItemToContractSaveReqVO> planList = reqVO.getPlanList();

        // 获取产品信息
        List<String> skuCodeList = planList.stream().map(PurchasePlanItemToContractSaveReqVO::getSkuCode).toList();
        // 生成合同
        producedCompanySkuMap.forEach((companyId, saleContractItemIdList) -> {
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderByInternalCompanyId(companyId);
            if (Objects.isNull(simpleVenderRespDTO)) {
                throw exception(PURCHASE_PLAN_COMPANY_NO_VENDER_ERROR);
            }
            // 生成采购合同
            PurchaseContractSaveInfoReqVO purchaseContractSaveInfoReqVO = BeanUtils.toBean(reqVO, PurchaseContractSaveInfoReqVO.class);
            List<PurchasePlanItemToContractSaveReqVO> productedSkuList = planList.stream().filter(x -> {
                return saleContractItemIdList.contains(x.getSaleContractItemId());
            }).toList();
            if (CollectionUtils.isEmpty(productedSkuList)) {
                return;
            }
            List<PurchaseContractItemSaveReqVO> purchaseContractItemSaveReqVOList = BeanUtils.toBean(productedSkuList, PurchaseContractItemSaveReqVO.class);
            int sortNum = 1;
            int totalQuantity = 0;
            for (PurchaseContractItemSaveReqVO vo : purchaseContractItemSaveReqVOList) {
                vo.setUniqueCode(IdUtil.fastSimpleUUID());
                // 采购数量 = 客户产品锁定数量 + 自营产品锁定数量
                String skuCode = vo.getSkuCode();
                Integer lockQuantity = producedStockLockCollect.get(vo.getSaleContractItemId());
//                Integer sourceSkuLockQuantity = producedStockLockCollect.get(skuDTO.getSourceCode() + "&" + companyId);
//                Integer purchaseQuantity = NumberUtil.add(lockQuantity, sourceSkuLockQuantity).intValue();
                vo.setQuantity(lockQuantity);
                totalQuantity += lockQuantity;
                // 设置采购单价
                JsonAmount withTaxPrice = lastNodeSkuPriceMap.get(skuCode);
                if (ObjectUtil.isNotNull(withTaxPrice)) {
                    List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                    if (CollUtil.isNotEmpty(taxMsg)){
                        taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                            vo.setWithTaxPrice(changeCurrency(withTaxPrice, s.getCurrency()));
                        });
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
            List<VenderPaymentDTO> paymentList = venderService.getVenderPaymentList(simpleVenderRespDTO.getId());
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
            if (Objects.nonNull(companyByVenderCode)) {
                if (CompanyNatureEnum.FACTORY.getValue().equals(companyByVenderCode.getCompanyNature())) {
                    purchaseContractSaveInfoReqVO.setSaleContractCode(null);
                    purchaseContractSaveInfoReqVO.setSaleContractId(null);
                }
                purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
            }
            Long saleContractId = reqVO.getSaleContractId();
            purchaseContractSaveInfoReqVO.setCompanyId(purchaseCompanyId);
            purchaseContractSaveInfoReqVO.setPurchasePlanId(reqVO.getId());
            purchaseContractSaveInfoReqVO.setPurchasePlanCode(reqVO.getCode());
            purchaseContractSaveInfoReqVO.setDeliveryDate(reqVO.getPlanDate());
            purchaseContractSaveInfoReqVO.setPurchaseTime(LocalDateTime.now());

            purchaseContractSaveInfoReqVO.setId(null);
            purchaseContractSaveInfoReqVO.setCreateTime(null);
            purchaseContractSaveInfoReqVO.setRemark(reqVO.getRemark());

            purchaseContractSaveInfoReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
            purchaseContractSaveInfoReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
            log.info("尾部节点与加工型企业锁定库存-生成采购合同:{}", JsonUtils.toJsonString(purchaseContractSaveInfoReqVO));
            responses.addAll(purchaseContractService.createPurchaseContract(purchaseContractSaveInfoReqVO));
//            if (CollUtil.isEmpty(lastNodeSkuPriceMap)) {
//                return;
//            }
            // 生成销售合同
            SaleContractSaveDTO saleContractInfo = PurchasePlanConvert.INSTANCE.convertSaleContractSaveDTO(purchaseContractSaveInfoReqVO);
            if (ObjectUtil.isNotNull(saleContractId)) {
                saleContractInfo = saleContractApi.getSaleContractInfo(saleContractId);
                saleContractInfo.setSourceContractId(saleContractId);
                saleContractInfo.setSourceContractCode(saleContractInfo.getCode());
            }

            saleContractInfo.setId(null);
            saleContractInfo.setCode(null);

            // 根据当前路径主键查询客户，补全客户相关信息
            SimpleCustRespDTO simpleCustRespDTO = custApi.getSimpleCustByInternalCompanyId(purchaseCompanyId);
            if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                saleContractInfo.setInternalCustId(simpleCustRespDTO.getId());
                saleContractInfo.setInternalCustCode(simpleCustRespDTO.getCode());
                saleContractInfo.setInternalCustName(simpleCustRespDTO.getName());

                saleContractInfo.setCustId(simpleCustRespDTO.getId());
                saleContractInfo.setCustCode(simpleCustRespDTO.getCode());
                saleContractInfo.setCustName(simpleCustRespDTO.getName());
                saleContractInfo.setCurrency(simpleCustRespDTO.getCurrency());
            }
            SimpleCompanyDTO apiCompanyDTO = companyApi.getCompanyDTO(purchaseCompanyId);
            if (ObjectUtil.isNotNull(apiCompanyDTO)) {
                if (apiCompanyDTO.getCompanyNature().intValue() == CompanyNatureEnum.INTERNAL_CUST.getValue()) {
                    saleContractInfo.setSaleType(SaleTypeEnum.EXPORT_SALE_CONTRACT.getValue());
                } else {
                    saleContractInfo.setSaleType(SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue());
                }
            }

            List<SaleContractItemSaveDTO> saleContractItemSaveDTOList = new ArrayList<>();
            saleContractInfo.getChildren().forEach(x -> {
                SaleContractItemSaveDTO contractItemSaveDTO = BeanUtils.toBean(x, SaleContractItemSaveDTO.class);
                contractItemSaveDTO.setUniqueCode(IdUtil.fastSimpleUUID());
                // 采购数量 = 客户产品锁定数量 + 自营产品锁定数量
                String skuCode = contractItemSaveDTO.getSkuCode();
                Integer saleQuantity = producedStockLockCollect.get(x.getId());
                if (Objects.isNull(saleQuantity)) {
                    return;
                }
                contractItemSaveDTO.setQuantity(saleQuantity);
                if (Objects.nonNull(x.getQtyPerOuterbox()) && x.getQtyPerOuterbox() > 0) {
                    contractItemSaveDTO.setBoxCount((int) Math.ceil((double) saleQuantity / x.getQtyPerOuterbox()));
                }
                // 设置采购单价
                JsonAmount withTaxPrice = lastNodeSkuPriceMap.get(skuCode);
                if (ObjectUtil.isNotNull(withTaxPrice)) {
                    if (CollUtil.isNotEmpty(taxMsg)){
                        taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                            contractItemSaveDTO.setUnitPrice(changeCurrency(withTaxPrice, s.getCurrency()));
                        });
                    }
                }
                contractItemSaveDTO.setId(null).setCurrentLockQuantity(BigDecimal.ZERO.intValue());
                saleContractItemSaveDTOList.add(contractItemSaveDTO);
            });
            saleContractInfo.setChildren(saleContractItemSaveDTOList);

            saleContractInfo.setCompanyId(companyId);
            if (Objects.nonNull(companyDTO)) {
                saleContractInfo.setCompanyName(companyDTO.getCompanyName());
            }
            saleContractInfo.setCheckFlag(Boolean.FALSE);
            saleContractInfo.setAutoFlag(BooleanEnum.YES.getValue());
            saleContractInfo.setStatus(SaleContractStatusEnum.COMPLETED.getValue());
            saleContractInfo.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            log.info("尾部节点与加工型企业锁定库存-生成销售合同:{}", JsonUtils.toJsonString(saleContractInfo));
            CalcContactUtil.checkItemCost(saleContractInfo, configApi.getConfigMap(), rateApi.getDailyRateMap(), saleContractInfo.getCurrency());
            saleContractApi.generateSaleContract(saleContractInfo);
        });
        return responses;
    }

    /**
     * 转换尾部节点向供应商采购合同，存在内部供应商时进行转换
     * 根据供应商、采购主体、采购员进行分组生成合同
     *
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    @Override
    public List<CreatedResponse> lastCompanyToVenderContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap, Boolean internalFlag, Map<Long, SimpleSaleItemDTO> saleItemQuantityMap) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<PurchasePlanItemToContractSaveReqVO> allItemList = new ArrayList<>();
        Set<String> saleContractCodeSet = new HashSet<>();
        // 单据一级产品明细数据
        List<PurchasePlanItemToContractSaveReqVO> planItemList = reqVO.getPlanList();
        if (!CollectionUtils.isEmpty(planItemList)) {
            Long companyId = reqVO.getCompanyId();
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
            // 当前主题为加工型企业时，仅添加标准采购模式产品，否则一级产品全部添加
            if (ObjectUtil.isNotNull(companyDTO) && companyDTO.getCompanyNature().intValue() == CompanyNatureEnum.FACTORY.getValue()) {
                List<PurchasePlanItemToContractSaveReqVO> standardItemlist = planItemList.stream().filter(x -> x.getPurchaseModel().intValue() == PurchaseModelEnum.STANDARD.getCode() && x.getNeedPurQuantity() > 0).toList();
                if (!CollectionUtils.isEmpty(standardItemlist)) {
                    allItemList.addAll(standardItemlist);
                    planItemList = standardItemlist;
                } else {
                    planItemList = new ArrayList<>();
                }
            } else {
                allItemList.addAll(planItemList);
            }
        }

        // 组套件产品明细数据
        List<PurchasePlanItemToContractSaveReqVO> combineItemList = reqVO.getCombineList();
        if (CollUtil.isNotEmpty(combineItemList)) {
            allItemList.addAll(combineItemList);
            // 更新采购员及部门信息  此处改为取采购计划中选中的采购员
//            Long loginUserId = WebFrameworkUtils.getLoginUserId();
//            UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
//            if (ObjectUtil.isNotNull(userDept)) {
//                allItemList.forEach(x -> {
//                    x.setPurchaseUserId(userDept.getUserId()).setPurchaseUserName(userDept.getNickname());
//                    x.setPurchaseUserDeptId(userDept.getDeptId()).setPurchaseUserDeptName(userDept.getDeptName());
//                });
//            }
        }
        // 过滤未转采购计划明细
        List<PurchasePlanItemToContractSaveReqVO> filteredAllItemList = allItemList.stream().filter(s -> {
            int needPurQuantity = Objects.isNull(s.getNeedPurQuantity()) ? 0 : s.getNeedPurQuantity();
            int convertedQuantity = Objects.isNull(s.getConvertedQuantity()) ? 0 : s.getConvertedQuantity();
            return needPurQuantity > 0 && needPurQuantity > convertedQuantity;
        }).toList();
        if (CollUtil.isEmpty(filteredAllItemList)) {
            return responses;
        }
        // 获取应商信息
        List<Long> allVenderIdList = allItemList.stream().map(PurchasePlanItemToContractSaveReqVO::getVenderId).toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(allVenderIdList);
        Map<Long, SimpleVenderRespDTO> venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, s -> s));

        // 产品类型为组合产品、采购模式为组合采购、供应商类型为外部公司时（修改当前明细:采购主体-当前采购主体，供应商主体-拆分后明细产品采购主体）
        List<String> skuCodeList = allItemList.stream().map(PurchasePlanItemToContractSaveReqVO::getSkuCode).toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
        List<PurchasePlanItemToContractSaveReqVO> filteredPlanItemList = planItemList.stream().toList();
        if (!CollectionUtils.isEmpty(filteredPlanItemList)) {
            List<PurchasePlanItemToContractSaveReqVO> filteredComlist = filteredPlanItemList.stream().filter(x -> {
                if ((BooleanEnum.YES.getValue().equals(x.getConvertedFlag()) && !internalFlag) || x.getPurchaseQuantity() == 0) {
                    return false;
                }
                // 产品类型-组合产品
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
                Integer skuType = skuDTO.getSkuType();
                // 采购模式-组套件采购
                Integer purchaseModel = x.getPurchaseModel();

                return skuType.intValue() == SkuTypeEnum.PRODUCT_MIX.getValue() && purchaseModel.intValue() == PurchaseModelEnum.COMBINE.getCode();
            }).toList();
            if (!CollectionUtils.isEmpty(filteredComlist)) {
                // 更新供应商主体
                filteredComlist.forEach(x -> {
                    List<PurchasePlanItemToContractSaveReqVO> twoLevelsCombineList = combineItemList.stream().filter(y -> y.getLevels() == 2 && y.getOneSplitNum().intValue() == x.getOneSplitNum()).toList();
                    if (!CollectionUtils.isEmpty(twoLevelsCombineList)) {
                        PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO = twoLevelsCombineList.get(0);
                        Long purchaseCompanyId = purchasePlanItemToContractSaveReqVO.getPurchaseCompanyId();
                        SimpleVenderRespDTO simpleVender = venderApi.getSimpleVenderByInternalCompanyId(purchaseCompanyId);
                        if (Objects.nonNull(simpleVender)) {
                            x.setVenderId(simpleVender.getId());
                            x.setVenderCode(simpleVender.getCode());
                            x.setVenderName(simpleVender.getName());
                        }
                    }
                });
                Map<Integer, PurchasePlanItemToContractSaveReqVO> collect = filteredComlist.stream().collect(Collectors.toMap(PurchasePlanItemToContractSaveReqVO::getOneSplitNum, Function.identity()));
                filteredAllItemList.forEach(x -> {
                    PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO = collect.get(x.getOneSplitNum());
                    if (x.getLevels() == 1 && ObjectUtil.isNotNull(purchasePlanItemToContractSaveReqVO)) {
                        x.setVenderId(purchasePlanItemToContractSaveReqVO.getVenderId());
                        x.setVenderCode(purchasePlanItemToContractSaveReqVO.getVenderCode());
                        x.setVenderName(purchasePlanItemToContractSaveReqVO.getVenderName());
                        SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderRespDTOById(purchasePlanItemToContractSaveReqVO.getVenderId());
                        JsonAmount jsonAmount = lastNodeSkuPriceMap.get(x.getSkuCode());
                        if (ObjectUtil.isNotNull(jsonAmount)) {
                            List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                            if (CollUtil.isNotEmpty(taxMsg)){
                                taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                    x.setWithTaxPrice(changeCurrency(jsonAmount, s.getCurrency()));
                                });
                            }
                        }
                    }
                });
            }
        }

        // 过滤二级产品分开采购的数据
        filteredAllItemList = filteredAllItemList.stream()
                .filter(x -> x.getLevels() != 2 || x.getPurchaseModel().intValue() != PurchaseModelEnum.COMBINE.getCode()).toList();
        // 校验订单方
        filteredAllItemList.forEach(x -> {
            if (ObjectUtil.isNull(x.getVenderId())) throw exception(PURCHASE_VENDER_EMPTY);
        });

        //根据供应商进行聚合，满足业务不同的供应商不同的订单
        Map<String, List<PurchasePlanItemToContractSaveReqVO>> planItemToContractCollectByVender = filteredAllItemList.stream().filter(s -> s.getPurchaseQuantity() > 0).collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getVenderCode));
        if (CollUtil.isNotEmpty(planItemToContractCollectByVender)) {
            planItemToContractCollectByVender.forEach((venderCode, planItemByVenderList) -> {
                if (CollUtil.isNotEmpty(planItemByVenderList)) {
                    planItemByVenderList.forEach(x -> {
                        if (ObjectUtil.isNull(x.getPurchaseCompanyId())) throw exception(PURCHASE_COMPANY_INFO_EMPTY);
                    });
                    // 根据主体进行聚合，不同的主体不能合并合同
                    Map<Long, List<PurchasePlanItemToContractSaveReqVO>> planItemToContractCollectByPurchaseCompany = planItemByVenderList.stream().collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getPurchaseCompanyId));
                    if (CollUtil.isNotEmpty(planItemToContractCollectByPurchaseCompany)) {
                        planItemToContractCollectByPurchaseCompany.forEach((purchaseCompanyId, planItemByCompanyList) -> {
                            planItemByCompanyList.forEach(x -> {
                                if (ObjectUtil.isNull(x.getPurchaseUserId())) throw exception(PURCHASE_USER_EMPTY);
                            });
                            // 根据采购员聚合
                            Map<Long, List<PurchasePlanItemToContractSaveReqVO>> planItemToContractCollectByPurchaseUser = planItemByCompanyList.stream().collect(Collectors.groupingBy(PurchasePlanItemRespVO::getPurchaseUserId));
                            if (CollUtil.isNotEmpty(planItemToContractCollectByPurchaseUser)) {
                                planItemToContractCollectByPurchaseUser.forEach((purchaseUserId, planItemByPurchaseUserList) -> {
                                    // 生成采购合同
                                    PurchaseContractSaveInfoReqVO purchaseContractSaveInfoReqVO = BeanUtils.toBean(reqVO, PurchaseContractSaveInfoReqVO.class);
                                    purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
                                    purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.NO.getValue());
                                    purchaseContractSaveInfoReqVO.setCompanyId(purchaseCompanyId);
                                    List<PurchaseContractItemSaveReqVO> purchaseContractItemSaveReqVOList = new ArrayList<>();
                                    int sortNum = 1;
                                    Boolean combineFlag = false;
                                    for (PurchasePlanItemToContractSaveReqVO planItemToContractSaveReqVO : planItemByPurchaseUserList) {
                                        if (planItemToContractSaveReqVO.getPurchaseModel().intValue() == PurchaseModelEnum.COMBINE.getCode()) {
                                            combineFlag = true;
                                        }
                                        PurchaseContractItemSaveReqVO vo = BeanUtils.toBean(planItemToContractSaveReqVO, PurchaseContractItemSaveReqVO.class);
                                        if (Objects.nonNull(planItemToContractSaveReqVO.getTwoSplitNum()) || Objects.nonNull(planItemToContractSaveReqVO.getThreeSplitNum())) {
                                            vo.setSplitFlag(BooleanEnum.YES.getValue());
                                        }
                                        vo.setSourceUniqueCode(vo.getUniqueCode());
                                        vo.setSortNum(sortNum);
                                        vo.setQuantity(planItemToContractSaveReqVO.getPurchaseQuantity());
                                        if (CollUtil.isNotEmpty(saleItemQuantityMap)) {
                                            SimpleSaleItemDTO simpleSaleItemDTO = saleItemQuantityMap.get(vo.getSaleContractItemId());
                                            if (Objects.nonNull(simpleSaleItemDTO)) {
                                                vo.setSaleContractItemId(simpleSaleItemDTO.getId());
                                                vo.setSaleItemUniqueCode(simpleSaleItemDTO.getUniqueCode());
                                            }
                                        }
                                        purchaseContractItemSaveReqVOList.add(vo);
                                        sortNum++;
                                    }
                                    // 锁库走原来逻辑
                                    if (Objects.nonNull(internalFlag)) {
                                        // 如果是尾节点则不需要生成内部合同
                                        if (!internalFlag && combineFlag) {
                                            return;
                                        }
                                        // 如果不是尾节点则不生成与供应商的合同
                                        if (internalFlag && !combineFlag) {
                                            return;
                                        }
                                    }

                                    purchaseContractSaveInfoReqVO.setChildren(purchaseContractItemSaveReqVOList);

                                    Optional<PurchasePlanItemToContractSaveReqVO> first = planItemByPurchaseUserList.stream().findFirst();
                                    if (first.isPresent()) {
                                        PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO = first.get();

                                        Long vendorId = purchasePlanItemToContractSaveReqVO.getVenderId();
                                        purchaseContractSaveInfoReqVO.setVenderId(vendorId);
                                        purchaseContractSaveInfoReqVO.setVenderCode(purchasePlanItemToContractSaveReqVO.getVenderCode());
                                        purchaseContractSaveInfoReqVO.setPurchaseUserId(purchaseUserId).setPurchaseUserName(purchasePlanItemToContractSaveReqVO.getPurchaseUserName())
                                                .setPurchaseUserDeptId(purchasePlanItemToContractSaveReqVO.getPurchaseUserDeptId())
                                                .setPurchaseUserDeptName(purchasePlanItemToContractSaveReqVO.getPurchaseUserDeptName());
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
                                                List<VenderPaymentDTO> paymentList = venderService.getVenderPaymentList(vendorId);
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

                                    SimpleCompanyDTO companyByVenderCode = venderApi.getCompanyByVenderCode(venderCode);
                                    if (Objects.nonNull(companyByVenderCode)) {
                                        purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
                                    }
                                    purchaseContractSaveInfoReqVO.setPurchasePlanId(reqVO.getId());
                                    purchaseContractSaveInfoReqVO.setPurchasePlanCode(reqVO.getCode());
                                    purchaseContractSaveInfoReqVO.setDeliveryDate(reqVO.getEstDeliveryDate());
                                    purchaseContractSaveInfoReqVO.setPurchaseTime(LocalDateTime.now());
                                    purchaseContractSaveInfoReqVO.setPlanSourceType(reqVO.getSourceType());
                                    purchaseContractSaveInfoReqVO.setId(null);
                                    purchaseContractSaveInfoReqVO.setCreateTime(null);
                                    purchaseContractSaveInfoReqVO.setRemark(reqVO.getRemark());
                                    purchaseContractSaveInfoReqVO.setSubmitFlag(BooleanEnum.NO.getValue());

                                    if (combineFlag) {
                                        purchaseContractSaveInfoReqVO.setAutoFlag(BooleanEnum.YES.getValue());
                                        purchaseContractSaveInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                                        purchaseContractSaveInfoReqVO.setContractStatus(PurchaseContractStatusEnum.FINISHED.getCode());
                                    }
                                    logger.info("尾部节点-供应商/加工型企业采购合同：{}", JsonUtils.toJsonString(purchaseContractSaveInfoReqVO));
                                    List<CreatedResponse> purchaseContracts = purchaseContractService.createPurchaseContract(purchaseContractSaveInfoReqVO);
                                    UserDept purchaseUser = new UserDept();
                                    purchaseUser.setUserId(purchaseUserId);
                                    purchaseUser.setNickname(purchaseContractSaveInfoReqVO.getPurchaseUserName());
                                    purchaseUser.setDeptId(purchaseContractSaveInfoReqVO.getPurchaseUserDeptId());
                                    purchaseUser.setDeptName(purchaseContractSaveInfoReqVO.getPurchaseUserDeptName());
                                    saleContractCodeSet.add(purchaseContractSaveInfoReqVO.getSaleContractCode());
                                    responses.addAll(purchaseContracts);
                                });
                            }
                        });
                    }
                }
            });
        }
        if (CollUtil.isNotEmpty(saleContractCodeSet)) {
            saleContractApi.batchUpdatePurchaseUser(saleContractCodeSet);
        }
        return responses;
    }

    @Override
    public void updatePlanItemConvertFlag(List<UpdateItemEntity> updateItemEntities) {
        if (CollUtil.isEmpty(updateItemEntities)) {
            return;
        }
        // 一级序号
        Set<Integer> oneSplitNumList = new HashSet<>();
        // 二级序号
        Set<Integer> twoSplitNumList = new HashSet<>();
        // 三级序号
        Set<Integer> threeSplitNumList = new HashSet<>();
        updateItemEntities.forEach(s -> {
            if (Objects.isNull(s.getConvertFlag())) {
                return;
            }
            LambdaQueryWrapperX<PurchasePlanItemDO> queryWrapperX = new LambdaQueryWrapperX<>();
            if (StrUtil.isNotEmpty(s.getSaleContractCode())) {
                queryWrapperX.eq(PurchasePlanItemDO::getSaleContractCode, s.getSaleContractCode());
            }
            queryWrapperX.eq(PurchasePlanItemDO::getSkuCode, s.getSkuCode());
            queryWrapperX.eq(PurchasePlanItemDO::getVenderCode, s.getVenderCOde());
            queryWrapperX.eq(PurchasePlanItemDO::getPurchaseUserId, s.getPurchaseUserId());
            List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(queryWrapperX);
            if (CollUtil.isEmpty(purchasePlanItemDOS)) {
                return;
            }
            purchasePlanItemDOS.forEach(item -> {
                item.setConvertedFlag(s.getConvertFlag());
                oneSplitNumList.add(item.getOneSplitNum());
                twoSplitNumList.add(item.getTwoSplitNum());
                threeSplitNumList.add(item.getThreeSplitNum());
                Optional<UpdateItemEntity> first1 = updateItemEntities.stream().filter(entity ->
                        Objects.equals(entity.getSaleContractCode(), item.getSaleContractCode())
                                && Objects.equals(entity.getSkuCode(), item.getSkuCode())
                                && Objects.equals(entity.getVenderCOde(), item.getVenderCode())
                                && Objects.equals(entity.getPurchaseUserId(), item.getPurchaseUserId())
                ).findFirst();
                first1.ifPresent(updateItemEntity -> {
                            item.setPurchaseQuantity(item.getPurchaseQuantity() + updateItemEntity.getPurchaseQuantity());
                            item.setConvertedQuantity(item.getConvertedQuantity() - updateItemEntity.getPurchaseQuantity());
                        }
                );

            });
        });
        List<PurchasePlanItemDO> updateList = new ArrayList<>();
        if (CollUtil.isEmpty(oneSplitNumList)) {
            return;
        }
        LambdaQueryWrapperX<PurchasePlanItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getOneSplitNum, oneSplitNumList);
        if (CollUtil.isNotEmpty(twoSplitNumList)) {
            queryWrapperX.or().in(PurchasePlanItemDO::getTwoSplitNum, twoSplitNumList);
        }
        if (CollUtil.isNotEmpty(threeSplitNumList)) {
            queryWrapperX.or().in(PurchasePlanItemDO::getThreeSplitNum, threeSplitNumList);
        }
        // 处理拆分明细
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return;
        }
        Map<Integer, List<PurchasePlanItemDO>> oneSplitMap = purchasePlanItemDOS.stream().filter(s -> Objects.nonNull(s.getOneSplitNum())).collect(Collectors.groupingBy(PurchasePlanItemDO::getOneSplitNum));
        Map<Integer, List<PurchasePlanItemDO>> twoSplitMap = purchasePlanItemDOS.stream().filter(s -> Objects.nonNull(s.getTwoSplitNum())).collect(Collectors.groupingBy(PurchasePlanItemDO::getTwoSplitNum));
        // 处理三级拆分
        if (CollUtil.isNotEmpty(twoSplitMap)) {
            twoSplitMap.forEach((k, v) -> {
                if (CollUtil.isEmpty(v)) {
                    return;
                }
                boolean convertedFlag = v.stream().filter(s -> Objects.nonNull(s.getThreeSplitNum())).allMatch(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag()));
                if (convertedFlag) {
                    Optional<PurchasePlanItemDO> first = v.stream().filter(s -> Objects.isNull(s.getThreeSplitNum())).findFirst();
                    first.ifPresent(planItemDO -> {
                        planItemDO.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                        Optional<UpdateItemEntity> first1 = updateItemEntities.stream().filter(s ->
                                Objects.equals(s.getSaleContractCode(), planItemDO.getSaleContractCode())
                                        && Objects.equals(s.getSkuCode(), planItemDO.getSkuCode())
                                        && Objects.equals(s.getVenderCOde(), planItemDO.getVenderCode())
                                        && Objects.equals(s.getPurchaseUserId(), planItemDO.getPurchaseUserId())
                        ).findFirst();
                        first1.ifPresent(updateItemEntity -> {
                                    planItemDO.setPurchaseQuantity(planItemDO.getPurchaseQuantity() + updateItemEntity.getPurchaseQuantity());
                                    planItemDO.setConvertedQuantity(planItemDO.getConvertedQuantity() - updateItemEntity.getPurchaseQuantity());
                                }

                        );

                        updateList.add(planItemDO);
                    });
                }

            });
        }
        // 处理二级拆分
        if (CollUtil.isNotEmpty(oneSplitMap)) {
            oneSplitMap.forEach((k, v) -> {
                if (CollUtil.isEmpty(v)) {
                    return;
                }
                boolean convertedFlag = v.stream().filter(s -> Objects.nonNull(s.getTwoSplitNum())).allMatch(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag()));
                if (convertedFlag) {
                    Optional<PurchasePlanItemDO> first = v.stream().filter(s -> Objects.isNull(s.getTwoSplitNum())).findFirst();
                    first.ifPresent(planItemDO -> {
                        planItemDO.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                        Optional<UpdateItemEntity> first1 = updateItemEntities.stream().filter(s ->
                                Objects.equals(s.getSaleContractCode(), planItemDO.getSaleContractCode())
                                        && Objects.equals(s.getSkuCode(), planItemDO.getSkuCode())
                                        && Objects.equals(s.getVenderCOde(), planItemDO.getVenderCode())
                                        && Objects.equals(s.getPurchaseUserId(), planItemDO.getPurchaseUserId())
                        ).findFirst();
                        first1.ifPresent(updateItemEntity -> {
                            planItemDO.setPurchaseQuantity(planItemDO.getPurchaseQuantity() + updateItemEntity.getPurchaseQuantity());
                            planItemDO.setConvertedQuantity(planItemDO.getConvertedQuantity() - updateItemEntity.getPurchaseQuantity());
                        });
                        updateList.add(planItemDO);
                    });
                }
            });
        }
        if (CollUtil.isNotEmpty(updateList)) {
            purchasePlanItemMapper.updateBatch(updateList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPurchasePlanItem(List<Long> saleItemIdList,Collection<Long> saleItemIds) {
        if (CollUtil.isNotEmpty(saleItemIds)){
            List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getSaleContractItemId, saleItemIds));
            if (CollUtil.isNotEmpty(purchasePlanItemDOS)){
                Set<PurchasePlanItemDO> purchItemPlans = purchasePlanItemDOS.stream().filter(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag())).collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(purchItemPlans)){
                    purchasePlanItemMapper.deleteBatchIds(purchItemPlans);
                    Set<Long> planIds = purchItemPlans.stream().map(PurchasePlanItemDO::getPurchasePlanId).collect(Collectors.toSet());
                    List<PurchasePlanItemDO> allItems = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().select(PurchasePlanItemDO::getPurchasePlanId).eq(PurchasePlanItemDO::getPurchasePlanId, planIds));
                    List<Long> updateList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(planIds)&&CollUtil.isEmpty(allItems)){
                        updateList.addAll(planIds);
                    }else if (CollUtil.isNotEmpty(planIds)&&CollUtil.isNotEmpty(allItems)){
                        Set<Long> availePlanIdSet = allItems.stream().map(PurchasePlanItemDO::getPurchasePlanId).collect(Collectors.toSet());
                        Collection<Long> subtract = CollUtil.subtract(planIds, availePlanIdSet);
                        if (CollUtil.isNotEmpty(subtract)){
                            List<PurchasePlanDO> updatePlanList = subtract.stream().map(s -> new PurchasePlanDO().setId(s).setPlanStatus(PurchasePlanStatusEnum.CASE_CLOSED.getCode())).collect(Collectors.toList());
                            if (CollUtil.isNotEmpty(updatePlanList)){
                                purchasePlanMapper.updateBatch(updatePlanList);
                            }
                        }
                    }else {
                        // 计划不存在则不作处理
                    }
                }
            }
        }
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().in(PurchasePlanItemDO::getSaleContractItemId, saleItemIdList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return;
        }
        purchasePlanItemDOS.forEach(s -> s.setCancelFlag(BooleanEnum.YES.getValue()));
        purchasePlanItemMapper.updateBatch(purchasePlanItemDOS);
        List<Long> planIdList = purchasePlanItemDOS.stream().map(PurchasePlanItemDO::getPurchasePlanId).distinct().toList();
        LambdaQueryWrapper<PurchasePlanItemDO> itemQueryWrapper = new LambdaQueryWrapper<PurchasePlanItemDO>().in(PurchasePlanItemDO::getPurchasePlanId, planIdList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue());
        List<PurchasePlanItemDO> allPurchasePlanItem = purchasePlanItemMapper.selectList(itemQueryWrapper);
        Map<Long, List<PurchasePlanItemDO>> planItemMap = allPurchasePlanItem.stream().collect(Collectors.groupingBy(PurchasePlanItemDO::getPurchasePlanId));
        List<Long> cancelPlanIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(planItemMap)) {
            // 当计划明细全部作废时作废对应计划
            planItemMap.forEach((k, v) -> {
                boolean cancelFlag = v.stream().allMatch(s -> BooleanEnum.YES.getValue().equals(s.getCancelFlag()));
                if (cancelFlag) {
                    // 作废计划
                    cancelPlanIdList.add(k);
                }
            });
            finishPurchasePlan(cancelPlanIdList,false);
        }
    }

    @Override
    public boolean validatePurContractItemIsSplit(Set<Long> saleContractItemIds) {
        if (CollUtil.isEmpty(saleContractItemIds)) {
            return false;
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapper<PurchasePlanItemDO>().select(PurchasePlanItemDO::getPurchaseModel).in(PurchasePlanItemDO::getSaleContractItemId, saleContractItemIds));
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return false;
        }
        return purchasePlanItemDOS.stream().map(PurchasePlanItemDO::getPurchaseModel).distinct().anyMatch(s -> PurchaseModelEnum.COMBINE.getCode().equals(s));
    }

    //采购计划-下推采购合同-张聪敏
    @Override
    @Transactional(rollbackFor = Exception.class )
    public List<CreatedResponse> toSaveContract(PurchasePlanItemToContractSaveInfoReqVO reqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        //传参为空，直接报错
        if (Objects.isNull(reqVO) || CollUtil.isEmpty(reqVO.getPlanList()))
            throw exception(PURCHASE_PLAN_ITEM_NULL);
        List<PurchasePlanItemToContractSaveReqVO> allList;
        if (CollUtil.isNotEmpty(reqVO.getCombineList())) {
            allList = Stream.concat(reqVO.getPlanList().stream(), reqVO.getCombineList().stream()).toList();
        } else {
            allList = reqVO.getPlanList();
        }

        List<Long> allVenderIdList = allList.stream().map(PurchasePlanItemToContractSaveReqVO::getVenderId).toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(allVenderIdList);
        Map<Long, SimpleVenderRespDTO> venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, s -> s));

        //过滤已转数据
        List<PurchasePlanItemToContractSaveReqVO> planItemList = reqVO.getPlanList().stream().filter(s -> ConvertedFlagEnum.NOT_CONVERTED.getStatus().equals(s.getConvertedFlag())).toList();
        List<Long> planIdList = planItemList.stream().map(PurchasePlanItemToContractSaveReqVO::getPurchasePlanId).toList();
        Map<Long, PurchasePlanDO> planMap = purchasePlanMapper.selectList(PurchasePlanDO::getId, planIdList).stream().collect(Collectors.toMap(PurchasePlanDO::getId, s -> s));
        //将采购计划信息中的主体信息赋值，用于进行聚合分类
        if (CollUtil.isNotEmpty(planMap)) {
            planItemList.forEach(s -> {
                PurchasePlanDO purchasePlanDO = planMap.get(s.getPurchasePlanId());
                if (Objects.nonNull(purchasePlanDO)) {
                    s.setCompanyId(purchasePlanDO.getCompanyId());
                }
            });
        }
        //根据采购计划id进行聚合分类，满足业务不同采购计划不能合并生成采购订单
        Map<Long, List<PurchasePlanItemToContractSaveReqVO>> planItemMap = planItemList.stream().collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getPurchasePlanId));
        if (CollUtil.isEmpty(planItemMap))
            throw exception(PURCHASE_PLAN_ITEM_NULL);

        planItemMap.forEach((key, value) -> {
            if (CollUtil.isNotEmpty(value)) {
                //获取标准采购采购计划详情
                List<PurchasePlanItemToContractSaveReqVO> itemList = new ArrayList<>(value.stream().filter(s -> PurchaseModelEnum.STANDARD.getCode().equals(s.getPurchaseModel())).toList());

                //获取组套件模式数据，组装数据
                List<Long> itemCombineList = value.stream()
                        .filter(s -> PurchaseModelEnum.COMBINE.getCode().equals(s.getPurchaseModel()))
                        .map(PurchasePlanItemToContractSaveReqVO::getId).toList();

                if (CollUtil.isNotEmpty(reqVO.getCombineList())) {
                    List<PurchasePlanItemToContractSaveReqVO> combineList = reqVO.getCombineList();
                    if (CollUtil.isNotEmpty(combineList)) {
                        List<PurchasePlanItemToContractSaveReqVO> comList = combineList.stream().filter(s -> itemCombineList.contains(s.getSourceId())).toList();
                        if (CollUtil.isNotEmpty(comList)) {
                            itemList.addAll(comList);
                        }
                    }
                }

                //将配件的排序设置成组合产产品的排序
                Map<Long, PurchasePlanItemToContractSaveReqVO> itemCombineMap = value.stream().collect(Collectors.toMap(PurchasePlanItemToContractSaveReqVO::getId, s -> s));
                if (CollUtil.isNotEmpty(itemCombineMap)) {
                    itemList.forEach(s -> {
                        Long id = s.getSourceId();
                        PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO = itemCombineMap.get(id);
                        if (Objects.nonNull(purchasePlanItemToContractSaveReqVO)) {
                            s.setSortNum(purchasePlanItemToContractSaveReqVO.getSortNum());
                        }
                    });
                }

                //数据业务排序，根据采购计划的顺序，配件明细站位原先组合产品位置
                itemList.sort(Comparator.comparing(PurchasePlanItemToContractSaveReqVO::getSortNum));
                AtomicInteger cuter = new AtomicInteger(1);
                itemList.forEach(s -> s.setSortNum(cuter.getAndIncrement()));
                UserDept loginUser = adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId());
                //根据供应商进行聚合，满足业务不同的供应商不同的订单
                Map<Long, List<PurchasePlanItemToContractSaveReqVO>> planItemByVenderMap = itemList.stream().collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getVenderId));
                if (CollUtil.isNotEmpty(planItemByVenderMap)) {
                    planItemByVenderMap.forEach((k, v) -> {
                        SimpleVenderRespDTO venderRespDTO = venderApi.getSimpleVenderRespDTOById(k);
                        if (CollUtil.isNotEmpty(v)) {
                            //根据主体进行聚合，不同的主体不能合并合同
                            Map<Long, List<PurchasePlanItemToContractSaveReqVO>> planItemByCompanyMap = v.stream().collect(Collectors.groupingBy(PurchasePlanItemToContractSaveReqVO::getCompanyId));
                            if (CollUtil.isNotEmpty(planItemByCompanyMap)) {
                                planItemByCompanyMap.forEach((companyId, item) -> {
                                    //根据采购员聚合
                                    Map<Long, List<PurchasePlanItemToContractSaveReqVO>> purchaseUserMap = item.stream().collect(Collectors.groupingBy(PurchasePlanItemRespVO::getPurchaseUserId));
                                    if (CollUtil.isNotEmpty(purchaseUserMap)) {
                                        purchaseUserMap.forEach((userId, it) -> {
                                            //生成采购合同
                                            if (CollUtil.isEmpty(planMap))
                                                throw exception(PURCHASE_PLAN_TO_CONTRACT_WRONG);
                                            PurchasePlanDO purchasePlanDO = planMap.get(key);
                                            if (Objects.isNull(purchasePlanDO))
                                                throw exception(PURCHASE_PLAN_TO_CONTRACT_WRONG);

                                            PurchaseContractSaveInfoReqVO contract = BeanUtils.toBean(purchasePlanDO, PurchaseContractSaveInfoReqVO.class);
                                            if (Objects.nonNull(loginUser)){
                                                contract.setPurchaseUserId(loginUser.getUserId());
                                                contract.setPurchaseUserName(loginUser.getNickname());
                                                contract.setPurchaseUserDeptId(loginUser.getDeptId());
                                                contract.setPurchaseUserDeptName(loginUser.getDeptName());
                                            }
                                            contract.setCompanyId(companyId);
                                            contract.setSaleContractId(purchasePlanDO.getSaleContractId());
                                            contract.setSaleContractCode(purchasePlanDO.getSaleContractCode());
                                            List<PurchaseContractItemSaveReqVO> pItemList = new Stack<>();
                                            int sortNum = 1;
                                            for (PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO : it) {
                                                PurchaseContractItemSaveReqVO vo = BeanUtils.toBean(purchasePlanItemToContractSaveReqVO, PurchaseContractItemSaveReqVO.class);
                                                vo.setQuantity(purchasePlanItemToContractSaveReqVO.getNeedPurQuantity());
                                                vo.setSortNum(sortNum);
                                                if (Objects.nonNull(loginUser)){
                                                    vo.setPurchaseUserId(loginUser.getUserId());
                                                    vo.setPurchaseUserName(loginUser.getNickname());
                                                    vo.setPurchaseUserDeptId(loginUser.getDeptId());
                                                    vo.setPurchaseUserDeptName(loginUser.getDeptName());
                                                }
                                                sortNum++;
                                                pItemList.add(vo);
                                            }
                                            contract.setChildren(pItemList);
                                            contract.setId(null);

                                            Optional<PurchasePlanItemToContractSaveReqVO> first = it.stream().findFirst();
                                            if (first.isPresent()) {
                                                PurchasePlanItemToContractSaveReqVO purchasePlanItemToContractSaveReqVO = first.get();
                                                Long vendorId = purchasePlanItemToContractSaveReqVO.getVenderId();
                                                contract.setVenderId(vendorId);
                                                contract.setVenderCode(purchasePlanItemToContractSaveReqVO.getVenderCode());
                                                if (Objects.nonNull(vendorId)) {
                                                    SimpleVenderRespDTO simpleVenderRespDTO = venderMap.get(vendorId);
                                                    if (Objects.nonNull(simpleVenderRespDTO)) {
                                                        List<JsonVenderTax> taxMsg = simpleVenderRespDTO.getTaxMsg();
                                                        if (CollUtil.isNotEmpty(taxMsg)){
                                                            taxMsg.stream().filter(s->BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().ifPresent(s->{
                                                                contract.setTaxType(s.getTaxType());
                                                            });
                                                        }
                                                        List<VenderPaymentDTO> paymentList = venderService.getVenderPaymentList(vendorId);
                                                        if (CollUtil.isNotEmpty(paymentList)) {
                                                            paymentList.forEach(s -> {
                                                                if (Objects.nonNull(s.getDefaultFlag()) && s.getDefaultFlag() == 1) {
                                                                    contract.setPaymentId(s.getPaymentId());
                                                                    contract.setPaymentName(s.getPaymentName());
                                                                    // 转换付款计划
                                                                    transformPurchasePlan(s.getPaymentId(), contract);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            if (Objects.nonNull(venderRespDTO)) {
                                                List<VenderPaymentDTO> paymentList = venderRespDTO.getPaymentList();
                                                if (Objects.nonNull(paymentList)) {
                                                    Optional<VenderPaymentDTO> first1 = paymentList.stream().filter(s -> s.getDefaultFlag() == 1).findFirst();
                                                    first1.ifPresent(venderPaymentDTO -> {
                                                        contract.setPaymentId(venderPaymentDTO.getPaymentId());
                                                        contract.setPaymentName(venderPaymentDTO.getPaymentName());
                                                    });
                                                }

                                            }
                                            contract.setPurchasePlanId(purchasePlanDO.getId());
                                            contract.setPurchasePlanCode(purchasePlanDO.getCode());
                                            contract.setDeliveryDate(purchasePlanDO.getPlanDate());
//                                            contract.setDeliveryDate(purchasePlanDO.getPlanDate());
                                            contract.setPurchaseTime(LocalDateTime.now());
                                            contract.setCreateTime(null);
                                            contract.setRemark(purchasePlanDO.getRemark());
                                            contract.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
                                            List<CreatedResponse> res = purchaseContractService.createPurchaseContract(contract);
                                            if (CollUtil.isNotEmpty(res) && res.size() > 0) {
                                                responses.add(res.get(0));
                                            }
                                        });

                                    }


                                });


                            }
                        }
                    });
                }
            }
        });

        {
            //判断采购计划状态，是否需要修改成已完成
            if (CollUtil.isNotEmpty(planIdList)) {
                if (CollUtil.isNotEmpty(allList)) {
                    List<PurchasePlanItemDO> allItems = PurchasePlanItemConvert.INSTANCE.convertPurchasePlanItemListBySaveList(allList);
                    List<PurchasePlanDO> updateList = new ArrayList<>();
                    // 更新明细转采购标识
                    if (BooleanEnum.YES.getValue().equals(reqVO.getSubmitFlag())){
                        allItems.forEach(this::updatePurchasePlanItemConvertedFlag);
                    }
                    purchasePlanItemMapper.updateBatch(allItems);
                    Map<Long, List<PurchasePlanItemDO>> itemsGroupedByPlanId = allItems.stream()
                            .collect(Collectors.groupingBy(PurchasePlanItemDO::getPurchasePlanId));
                    itemsGroupedByPlanId.forEach((planId, itemDOList) -> {
                        boolean isPurchaseFlag = itemDOList.stream().allMatch(s -> ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag()));
                        PurchasePlanDO purchasePlanDO = PurchasePlanDO.builder()
                                .id(planId)
                                .build();
                        List<Long> purchaseUserIdList = itemDOList.stream().map(PurchasePlanItemDO::getPurchaseUserId).distinct().toList();
                        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(purchaseUserIdList);
                        if (CollUtil.isNotEmpty(userDeptListCache)) {
                            purchasePlanDO.setPurchaseUserList(userDeptListCache.values().stream().toList());
                        }
                        if (isPurchaseFlag) {
                            purchasePlanDO.setPlanStatus(PurchasePlanStatusEnum.COMPLETED.getCode());
                        } else {
                            purchasePlanDO.setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode());
                        }
                        updateList.add(purchasePlanDO);
                    });
                    purchasePlanMapper.updateBatch(updateList);
                }
            }

        }
        return responses;
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

    @Override
    public String getPlanCodeBySaleContractCode(String saleContractCode) {
        if (StrUtil.isEmpty(saleContractCode)) {
            return null;
        }
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectList(PurchasePlanDO::getSaleContractCode, saleContractCode);
        if (purchasePlanDOS == null || purchasePlanDOS.size() == 0) {
            throw exception(PLAN_PURCHASE_CONTRACT_NOT_EXISTS);
        }
        return purchasePlanDOS.get(0).getCode();
    }

    @Override
    public Long getPlanIdBySaleItemId(Long saleContractItemId) {
        if (Objects.isNull(saleContractItemId)) {
            return null;
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getSaleContractItemId, saleContractItemId);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return null;
        }
        Optional<Long> optional = purchasePlanItemDOS.stream().filter(s -> {
            return Objects.nonNull(s.getTwoSplitNum()) || Objects.nonNull(s.getThreeSplitNum());
        }).map(PurchasePlanItemDO::getPurchasePlanId).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public List<PurchasePlanDO> getPlanListByIds(List<Long> planIdList) {
        if (CollUtil.isEmpty(planIdList)) {
            return List.of();
        }
        return purchasePlanMapper.selectBatchIds(planIdList);
    }

    @Override
    public void rollbackSplitPurchasePlan(List<Long> planIds) {
        if (CollUtil.isEmpty(planIds)) {
            return;
        }
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectBatchIds(planIds);
        if (CollUtil.isEmpty(purchasePlanDOS)) {
            return;
        }
        purchasePlanDOS.forEach(s -> s.setPlanStatus(PurchasePlanStatusEnum.CASE_CLOSED.getCode()));
        purchasePlanMapper.updateBatch(purchasePlanDOS);
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getPurchasePlanId, planIds));
        finishSplitPurchasePlan(purchasePlanDOS, purchasePlanItemDOS);
    }

    @Override
    public void updateBatchPlanList(Map<Long, Map<String, Integer>> updateMap, List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(updateMap)) {
            return;
        }
        Set<Long> planIds = updateMap.keySet();
        List<PurchasePlanDO> purchasePlanDOS = purchasePlanMapper.selectBatchIds(planIds);
        if (CollUtil.isEmpty(purchasePlanDOS)) {
            return;
        }
        List<PurchasePlanDO> updateList = new ArrayList<>();
        purchasePlanDOS.forEach(s -> {
            Map<String, Integer> itemMap = updateMap.get(s.getId());
            if (CollUtil.isEmpty(itemMap)) {
                return;
            }
            s.setPlanStatus(PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode());
            updateList.add(s);
        });
        if (CollUtil.isNotEmpty(updateList)) {
            purchasePlanMapper.updateBatch(updateList);
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(PurchasePlanItemDO::getPurchasePlanId, planIds);
        if (CollUtil.isEmpty(purchasePlanItemDOS)) {
            return;
        }
        List<PurchasePlanItemDO> updateItemList = new ArrayList<>();
        purchasePlanItemDOS.forEach(s -> {
            Map<String, Integer> itemQuantityMap = updateMap.get(s.getPurchasePlanId());
            if (CollUtil.isEmpty(itemQuantityMap)) {
                return;
            }
            Integer quantity = itemQuantityMap.get(s.getSourceUniqueCode());
            if (Objects.isNull(quantity)) {
                return;
            }
            Integer convertedQuantity = Math.max(s.getConvertedQuantity() - quantity, 0);
            if (BooleanEnum.NO.getValue().equals(s.getAuxiliarySkuFlag())){
                s.setNeedPurQuantity(s.getNeedPurQuantity()+quantity);
            }
            s.setConvertedQuantity(convertedQuantity);
            if (s.getConvertedQuantity() == 0) {
                s.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
            } else if (s.getConvertedQuantity() < s.getNeedPurQuantity()) {
                s.setConvertedFlag(ConvertedFlagEnum.PART_CONVERTED.getStatus());
            }
            updateItemList.add(s);
        });
        if (CollUtil.isNotEmpty(updateItemList)) {
            purchasePlanItemMapper.updateBatch(updateItemList);
        }
        saleContractApi.deleteSplitSaleItem(saleContractItemIdList);
    }
}
