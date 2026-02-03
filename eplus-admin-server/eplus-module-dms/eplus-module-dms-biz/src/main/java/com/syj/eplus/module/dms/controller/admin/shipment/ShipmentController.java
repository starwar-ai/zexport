package com.syj.eplus.module.dms.controller.admin.shipment;

import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ConfirmFlagEnum;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.module.crm.api.category.CrmCategoryApi;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.crm.enums.cust.SourceTypeEnum;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeShareFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.*;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentPageReq;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentSaveReq;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.service.shipment.ShipmentService;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoDTO;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 出运单")
@RestController
@RequestMapping("/dms/shipment")
@Validated
public class ShipmentController {

    private static final Logger log = LoggerFactory.getLogger(ShipmentController.class);
    @Resource
    private ShipmentService shipmentService;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private CustApi custApi;

    @Resource
    private CrmCategoryApi crmCategoryApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @PostMapping("/create")
    @Operation(summary = "创建出运单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:create')")
    public CommonResult<List<CreatedResponse>> createShipment(@Valid @RequestBody ShipmentSaveReqVO createReqVO) {
        return success(shipmentService.createShipment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出运单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:update')")
    public CommonResult<Boolean> updateShipment(@Valid @RequestBody ShipmentSaveReqVO updateReqVO) {
        shipmentService.updateShipment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出运单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:shipment:delete')")
    public CommonResult<Boolean> deleteShipment(@RequestParam("id") Long id) {
        shipmentService.deleteShipment(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得出运单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:shipment:query')")
    public CommonResult<ShipmentRespVO> getShipment(@RequestParam("id") Long id) {
        ShipmentRespVO shipment = shipmentService.getShipment(id);
        return success(shipment);
    }

    @GetMapping("/page")
    @Operation(summary = "获得出运单分页")
    @PreAuthorize("@ss.hasPermission('dms:shipment:query')")
    public CommonResult<PageResult<ShipmentRespVO>> getShipmentPage(@Valid ShipmentPageReqVO pageReqVO) {
        return success(shipmentService.getShipmentPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出出运单列表 Excel")
    @PreAuthorize("@ss.hasPermission('dms:shipment:export')")
    @OperateLog(type = EXPORT)
    public void exportShipmentExcel(@Valid ShipmentPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<ShipmentRespVO> shipmentPage = shipmentService.getShipmentPage(pageReqVO);
        if(!pageReqVO.getIsTree()){
            // 导出 Excel
            ExcelUtils.write(response, "出运单-单据.xls", "数据", ShipmentRespVO.class, shipmentPage.getList());
        }else{
            List<ShipmentItemExportRespVO> PurchaseRegistrationItemRespList = new ArrayList<>();
            AtomicInteger sortIndex = new AtomicInteger(1);
            List<String> saleContractCodes = new ArrayList();
            shipmentPage.getList().forEach(item->{
                item.getChildren().forEach(cl->{
                     if(!saleContractCodes.contains(cl.getSaleContractCode())){
                         saleContractCodes.add(cl.getSaleContractCode());
                     }
                });
            });
            Map<String, SmsContractAllDTO> saleMap = saleContractApi.getSmsByCodeList(saleContractCodes);
            
            // 收集所有客户编号，用于批量查询客户信息
            List<String> custCodes = shipmentPage.getList().stream()
                    .flatMap(item -> item.getChildren().stream())
                    .map(cl -> cl.getCustCode())
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(custCodes);
            
            // 收集所有客户类型ID，用于批量查询分类信息
            List<Long> customerTypeIds = custMap.values().stream()
                    .filter(cust -> cust.getCustomerTypes() != null)
                    .flatMap(cust -> cust.getCustomerTypes().stream())
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, String> categoryNameMap = crmCategoryApi.getCategoryNameMap(customerTypeIds);
            
            // 收集所有采购合同号，用于批量查询采购合同信息（获取采购员和部门）
            List<String> purchaseContractCodes = shipmentPage.getList().stream()
                    .flatMap(item -> item.getChildren().stream())
                    .filter(cl -> cl.getContractDetails() != null && !cl.getContractDetails().isEmpty())
                    .flatMap(cl -> cl.getContractDetails().stream())
                    .map(SimpleContractDetailDTO::getContractCode)
                    .filter(code -> code != null && !code.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, PurchaseContractAllDTO> purchaseContractMap = purchaseContractCodes.isEmpty() ? 
                new HashMap<>() : purchaseContractApi.getPurchaseByCodeList(purchaseContractCodes);
            
            shipmentPage.getList().forEach(item->{
                item.getChildren().forEach(cl->{
                    // 按采购合同号拆分出运明细
                    if(cl.getContractDetails() != null && !cl.getContractDetails().isEmpty()){
                        // 按照采购合同号分组并汇总数量
                        Map<String, SimpleContractDetailDTO> contractQuantityMap = new LinkedHashMap<>();
                        for(SimpleContractDetailDTO contractDetail : cl.getContractDetails()){
                            String contractCode = contractDetail.getContractCode();
                            Integer usedQuantity = contractDetail.getUsedQuantity();
                            if(contractCode != null && usedQuantity != null){
                                SimpleContractDetailDTO existing = contractQuantityMap.get(contractCode);
                                if(existing == null){
                                    contractQuantityMap.put(contractCode, contractDetail);
                                } else {
                                    // 相同采购合同号，累加数量
                                    existing.setUsedQuantity(existing.getUsedQuantity() + usedQuantity);
                                }
                            }
                        }
                        
                        // 为每个采购合同号生成一条导出记录
                        for(SimpleContractDetailDTO contractDetail : contractQuantityMap.values()){
                            ShipmentItemExportRespVO exportVO = createShipmentItemExportVO(
                                cl, item, saleMap, custMap, categoryNameMap, purchaseContractMap, sortIndex, contractDetail);
                            PurchaseRegistrationItemRespList.add(exportVO);
                        }
                    } else {
                        // 没有合同详情，按原逻辑处理
                        SimpleContractDetailDTO defaultDetail = new SimpleContractDetailDTO();
                        defaultDetail.setContractCode(cl.getPurchaseContractCode());
                        defaultDetail.setUsedQuantity(cl.getOutQuantity() != null ? cl.getOutQuantity() : 0);
                        defaultDetail.setSupplierName(cl.getVenderName());
                        ShipmentItemExportRespVO exportVO = createShipmentItemExportVO(
                            cl, item, saleMap, custMap, categoryNameMap, purchaseContractMap, sortIndex, defaultDetail);
                        PurchaseRegistrationItemRespList.add(exportVO);
                    }
                });
            });
            ExcelUtils.write(response, "出运单-产品.xls", "数据", ShipmentItemExportRespVO.class, PurchaseRegistrationItemRespList);
        }
    }

    /**
     * 创建出运明细导出VO对象
     * 按图片要求处理各字段：
     * 1. 采购事业部、采购员、采购合同、厂商简称：要带对应采购合同中的信息（如果库存中没有，空着就行了）
     * 2. 出货数量和报关数量：根据对应合同的使用库存进行拆分
     * 3. 报关金额 = 报关数量 * 报关价
     * 4. 采购单价：取对应合同中的采购单价或者使用库存的库存价格（从contractDetail.price获取）
     * 5. 采购总价 = 出货数量 * 采购单价
     * 6. 退税金额 = 采购总价 / (1 + 退税率) * 退税率
     */
    private ShipmentItemExportRespVO createShipmentItemExportVO(
            ShipmentItem cl,
            ShipmentRespVO item,
            Map<String, SmsContractAllDTO> saleMap,
            Map<String, CustAllDTO> custMap,
            Map<Long, String> categoryNameMap,
            Map<String, PurchaseContractAllDTO> purchaseContractMap,
            AtomicInteger sortIndex,
            SimpleContractDetailDTO contractDetail) {
        
        ShipmentItemExportRespVO shipmentItemExportRespVO = BeanUtils.toBean(cl, ShipmentItemExportRespVO.class);
        SmsContractAllDTO smsContractAllDTO = saleMap.get(cl.getSaleContractCode());
        CustAllDTO custAllDTO = custMap.get(cl.getCustCode());
        
        // 设置基本字段
        shipmentItemExportRespVO.setSalesCreateDate(smsContractAllDTO != null ? smsContractAllDTO.getCreateTime() : null);
        shipmentItemExportRespVO.setInvoiceCode(item.getInvoiceCode());
        shipmentItemExportRespVO.setShipDate(item.getShipDate());
        shipmentItemExportRespVO.setCode(item.getCode());
        shipmentItemExportRespVO.setSortNum(sortIndex.getAndIncrement());
        shipmentItemExportRespVO.setOrderNum(shipmentItemExportRespVO.getSortNum());
        shipmentItemExportRespVO.setCustPO(cl.getCustPo());

        // 【关键】设置采购合同号和供应商信息
        shipmentItemExportRespVO.setPurchaseContractCode(contractDetail.getContractCode());
        shipmentItemExportRespVO.setVenderName(contractDetail.getSupplierName());

        // 根据采购合同号获取采购员和采购事业部信息
        String purchaseContractCode = contractDetail.getContractCode();
        if(purchaseContractCode != null && purchaseContractMap.containsKey(purchaseContractCode)){
            PurchaseContractAllDTO purchaseContract = purchaseContractMap.get(purchaseContractCode);
            shipmentItemExportRespVO.setPurchaseUserName(purchaseContract.getPurchaseUserName());
            shipmentItemExportRespVO.setPurchaseUserDeptName(purchaseContract.getPurchaseUserDeptName());
        } 
        
        // 设置日期字段
        if(smsContractAllDTO != null && smsContractAllDTO.getCreateTime() != null){
            shipmentItemExportRespVO.setContractDate(smsContractAllDTO.getSignBackDate());
        }
        if(item.getCustDeliveryDate() != null){
            shipmentItemExportRespVO.setContractDeliveryDate(item.getCustDeliveryDate());
        }
        if(item.getEstDepartureTime() != null){
            shipmentItemExportRespVO.setActualShipDate(item.getEstDepartureTime());
        }
        
        // 设置客户归属
        if(custAllDTO != null && custAllDTO.getCustomerTypes() != null && !custAllDTO.getCustomerTypes().isEmpty()){
            StringBuilder customerTypeStr = new StringBuilder();
            for(Long typeId : custAllDTO.getCustomerTypes()){
                String categoryName = categoryNameMap.get(typeId);
                if(categoryName != null){
                    if(customerTypeStr.length() > 0){
                        customerTypeStr.append(",");
                    }
                    customerTypeStr.append(categoryName);
                }
            }
            shipmentItemExportRespVO.setCustomerType(customerTypeStr.toString());
        }
        
        // 设置客户分类
        if(custAllDTO != null && custAllDTO.getSourceType() != null){
            SourceTypeEnum sourceEnum = SourceTypeEnum.getByValue(custAllDTO.getSourceType());
            if(sourceEnum != null){
                shipmentItemExportRespVO.setCustomerSource(sourceEnum.getName());
            }
        }
        // 设置销售信息
        if(cl.getSales()!=null){
            shipmentItemExportRespVO.setSalesName(cl.getSales().getNickname());
            shipmentItemExportRespVO.setSalesDeptName(cl.getSales().getDeptName());
        }
        // 价格取对应合同中的采购单价或者使用库存的库存价格
        JsonAmount purchasePrice = contractDetail.getPrice();
        if(purchasePrice != null && purchasePrice.getAmount() != null){
            BigDecimal purchaseUnitPrice = purchasePrice.getAmount();
            String purchaseCurrency = purchasePrice.getCurrency();
            shipmentItemExportRespVO.setPurchaseWithTaxPriceDecimal(NumberFormatUtil.formatAmount(purchaseUnitPrice));
            shipmentItemExportRespVO.setPurchaseWithTaxPriceCurrency(purchaseCurrency);
            // 采购总价 = 出运数量 * 采购单价
            BigDecimal purchaseTotal = NumberUtil.mul(cl.getShippingQuantity(), purchaseUnitPrice);
            shipmentItemExportRespVO.setPurchaseTotalDecimal(NumberFormatUtil.formatAmount(purchaseTotal));
            shipmentItemExportRespVO.setPurchaseTotalCurrency(purchaseCurrency);
            
            // 退税金额 = 采购总价 / (1 + 退税率) * 退税率
            if(cl.getTaxRefundRate() != null && cl.getTaxRefundRate().compareTo(BigDecimal.ZERO) > 0){
                BigDecimal taxRefundRate = cl.getTaxRefundRate();
                BigDecimal taxRefundAmount = purchaseTotal
                    .divide(BigDecimal.ONE.add(taxRefundRate), 6, RoundingMode.HALF_UP)
                    .multiply(taxRefundRate);
                shipmentItemExportRespVO.setTaxRefundPriceBigDecimal(NumberFormatUtil.formatAmount(taxRefundAmount));
                shipmentItemExportRespVO.setTaxRefundPriceCurrency(purchaseCurrency);
            }
        }
        if(cl.getTotalNetweight()!=null){
            shipmentItemExportRespVO.setTotalNetweightDecimal(NumberFormatUtil.formatWeight(cl.getTotalNetweight().getWeight()));
        }
        if(cl.getSaleUnitPrice()!=null){
            shipmentItemExportRespVO.setSaleUnitPriceBigDecimal(NumberFormatUtil.formatAmount(cl.getSaleUnitPrice().getAmount()));
            shipmentItemExportRespVO.setSaleUnitPriceCurrency(cl.getSaleUnitPrice().getCurrency());
        }
        if(cl.getDeclarationUnitPrice()!=null){
            shipmentItemExportRespVO.setDeclarationUnitPriceDecimal(NumberFormatUtil.formatAmount(cl.getDeclarationUnitPrice().getAmount()));
            shipmentItemExportRespVO.setDeclarationUnitPriceCurrency(cl.getDeclarationUnitPrice().getCurrency());
            // 报关金额 = 报关数量 * 报关价
            if(shipmentItemExportRespVO.getDeclarationQuantity() != null){
                BigDecimal declarationAmount = NumberUtil.mul(
                    shipmentItemExportRespVO.getDeclarationQuantity(),
                    cl.getDeclarationUnitPrice().getAmount()
                );
                shipmentItemExportRespVO.setDeclarationAmountDecimal(NumberFormatUtil.formatAmount(declarationAmount));
                shipmentItemExportRespVO.setDeclarationAmountCurrency(cl.getDeclarationUnitPrice().getCurrency());
            }
        }
        if(cl.getTaxRefundRate()!=null){
            shipmentItemExportRespVO.setTaxRefundRate(cl.getTaxRefundRate().setScale(CalculationDict.FOUR, RoundingMode.HALF_UP).stripTrailingZeros());
        }
        if(cl.getOutboundFlag()!=null){
            if(BooleanEnum.YES.getValue().equals(cl.getOutboundFlag())){
                shipmentItemExportRespVO.setOutboundFlagString("是");
            }else{
                shipmentItemExportRespVO.setOutboundFlagString("否");
            }
        }
        return shipmentItemExportRespVO;
    }


    @GetMapping("/export-detail-excel")
    @Operation(summary = "导出出运单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "exportType", required = false, description = "导出类型", example = "1024"),
    })
    @OperateLog(type = EXPORT)
    @PreAuthorize("@ss.hasPermission('dms:shipment:detail-export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("custCode") String custCode,
            @RequestParam("exportType") String exportType,
            HttpServletResponse response) {
        shipmentService.exportExcel(id,exportType,reportCode,custCode,response);
    }

    @PostMapping("/transform-shipment")
    @Operation(summary = "转出运明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:transform')")
    public CommonResult<List<CreatedResponse>> transformShipment(@RequestParam("ids") List<Long> ids) {
        return success(shipmentService.transformShipment(ids));
    }

    @PostMapping("/batch-transform-shipment")
    @Operation(summary = "批量转出运明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:transform')")
    public CommonResult<List<CreatedResponse>> batchTransformShipment(@RequestParam("ids") List<Long> ids) {
        return success(shipmentService.batrchTansformShipment(ids));
    }

    @PostMapping("/append-shipment")
    @Operation(summary = "追加转出运明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:transform')")
    public CommonResult<Boolean> appendShipment(@RequestBody AppendShipmentReq req) {
        shipmentService.appendShipment(req);
        return success( true);
    }

    @PostMapping("/to-commodity-inspection")
    @Operation(summary = "转商检单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:to-commodity-inspection')")
    public CommonResult<List<CreatedResponse>> toCommodityInspection(@RequestBody ToInspectionReqVO reqVO) {
        return success( shipmentService.toCommodityInspection(reqVO));
    }

    @PostMapping("/to-declaration")
    @Operation(summary = "转报关单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:to-declaration')")
    public CommonResult<List<CreatedResponse>> toDeclaration(@RequestBody ToDeclarationReqVO toDeclarationReqVO) {
        return success(shipmentService.toDeclaration(toDeclarationReqVO));
    }

    @PostMapping("/to-settlement-form")
    @Operation(summary = "转结汇单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:to-settlement-form')")
    public CommonResult<List<CreatedResponse>> toSettlementForm(@RequestBody ToSettlementFormReqVO toSettlementFormReqVO) {
        return success(shipmentService.toSettlementForm(toSettlementFormReqVO));
    }

    @PutMapping("/close")
    @Operation(summary = "出运明细作废")
    @PreAuthorize("@ss.hasPermission('dms:shipment:close')")
    public CommonResult<Boolean> closeShipment(@RequestBody CloseShipmentReq closeShipmentReq) {
        shipmentService.closeShipment(closeShipmentReq);
        return success(true);
    }

    @PutMapping("/rollback-close")
    @Operation(summary = "出运明细反作废")
    @PreAuthorize("@ss.hasPermission('dms:shipment:rollback-close')")
    public CommonResult<Boolean> rollbackCloseShipment(@RequestBody CloseShipmentReq closeShipmentReq) {
        shipmentService.rollbackClose(closeShipmentReq);
        return success(true);
    }

    @PutMapping("/shipment")
    @Operation(summary = "出运明细出运")
    @PreAuthorize("@ss.hasPermission('dms:shipment:shipment')")
    public CommonResult<Boolean> shipment(@RequestBody ShipmentReq shipmentReq) {
        shipmentService.shipment(shipmentReq);
        return success(true);
    }

    @GetMapping("/finish")
    @Operation(summary = "出运明细交单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:finish')")
    public CommonResult<Boolean> finishShipment(@RequestParam Long id) {
        shipmentService.finishShipment(id);
        return success(true);
    }

    @PostMapping("/transform-invoicing-notices")
    @Operation(summary = "转开票通知")
    @PreAuthorize("@ss.hasPermission('dms:shipment:transform-invoicing-notices')")
    public CommonResult<List<CreatedResponse>> transformInvoicingNotices(@RequestBody List<ShipmentToInvoiceSaveReq> reqList) {
        return success(shipmentService.transformInvoicingNotices(reqList));
    }

    @PostMapping("/transform-container-transportation")
    @Operation(summary = "创建拉柜通知单")
    @PreAuthorize("@ss.hasPermission('dms:shipment:transform-container-transportation','dms:shipment:factory-outbound')")
    public CommonResult<List<CreatedResponse>> transformContainerTransportation(@Valid @RequestBody ContainerMidVO containerMidVO) {
        log.info("创建拉柜通知单请求参数：{}",JsonUtils.toJsonString(containerMidVO));
        return success(shipmentService.createNotice(containerMidVO));
    }

//    @GetMapping("/container-transportation-page")
//    @Operation(summary = "获得拉柜通知单分页")
//    @PreAuthorize("@ss.hasPermission('dms:container-transportation:query')")
//    public CommonResult<PageResult<StockNoticeRespDTO>> getNoticePage(@Valid StockNoticePageReqDTO pageReqDTO) {
//        //入库单列表查询是否拉柜为是
//        pageReqDTO.setIsContainerTransportation(BooleanEnum.YES.getValue());
//        return success(shipmentService.getNoticePage(pageReqDTO));
//    }

    @GetMapping("/container-transportation-mid-page")
    @Operation(summary = "获得拉柜通知单中间页")
    @PreAuthorize("@ss.hasPermission('dms:container-transportation:query')")
    public CommonResult<ContainerMidVO> getNoticeMidPage(@RequestParam("ids") List<Long> ids,@RequestParam("shippedAddress")Integer shippedAddress) {
        return success(shipmentService.getContainerMid(ids,shippedAddress));
    }

    @GetMapping("/listManufactureSku")
    @Operation(summary = "查询采购计划需要加工组套件产品")
    @PreAuthorize("@ss.hasPermission('dms:container-transportation:manufacture')")
    public CommonResult<PurchasePlanInfoDTO> listManufactureSku(String purchaseContractCode) {
        PurchasePlanInfoDTO purchasePlanInfoDTO = shipmentService.listManufactureSku(purchaseContractCode,null);
        return success(purchasePlanInfoDTO);
    }

    @GetMapping("/manufactureSku")
    @Operation(summary = "加工组套产品")
    @PreAuthorize("@ss.hasPermission('dms:container-transportation:manufacture')")
    public CommonResult<Boolean> manufactureSku(String purchaseContractCode,String skuCode,Integer processingNum) {
        shipmentService.manufactureSku( skuCode, processingNum,null);
        return success(true);
    }


    @PostMapping("/batchManufactureSku")
    @Operation(summary = "批量加工组套产品")
    @PreAuthorize("@ss.hasPermission('dms:container-transportation:manufacture')")
    public CommonResult<Boolean> batchManufactureSku(@RequestBody List<ManufactureSkuReqVO> manufactureSkuReqVOList) {
        shipmentService.batchManufactureSku(manufactureSkuReqVOList);
        return success(true);
    }

    @PutMapping("/change")
    @Operation(summary = "变更出运单")
    @PreAuthorize("@ss.hasPermission('dms:export-sale-contract-change:update')")
    public CommonResult<List<CreatedResponse>> changeSaleContract(@RequestBody ChangeShipmentSaveReq updateReqVO) {
        return success( shipmentService.changeShipment(updateReqVO, false));
    }

    @PutMapping("/change-business")
    @Operation(summary = "变更出运单(业务员)")
    @PreAuthorize("@ss.hasPermission('dms:export-sale-contract-change-business:update')")
    public CommonResult<Boolean> changeSaleContractByBusiness(@RequestBody ChangeShipmentSaveReq updateReqVO) {
        shipmentService.changeShipment(updateReqVO, true);
        return success(true);
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得出运单变更列表")
    @PreAuthorize("@ss.hasPermission('dms:container-change:query')")
    public CommonResult<PageResult<ChangeShipmentRespVO>> getShipmentChangePage(@Valid ChangeShipmentPageReq pageReq) {
        return success(shipmentService.getChangePage(pageReq));
    }


    @GetMapping("/change-detail")
    @Operation(summary = "获得出运单变更单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:query')")
    public CommonResult<ChangeShipmentRespVO> getShipmentChange(@RequestParam("id") Long id) {
        ChangeShipmentRespVO shipmentChange = shipmentService.getShipmentChange(id);
        return success(shipmentChange);
    }

    @GetMapping("/change-audit-detail")
    @Operation(summary = "获得出运单变更单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:query')")
    public CommonResult<ChangeShipmentRespVO> getAuditShipmentChange(@RequestParam("id") String id) {
        ChangeShipmentRespVO shipmentChange = shipmentService.getAuditShipmentChange(id);
        return success(shipmentChange);
    }

//    @PutMapping("/change-submit")
//    @Operation(summary = "提交变更任务")
//    @PreAuthorize("@ss.hasPermission('dms:shipment-change:submit')")
//    public CommonResult<Boolean> submitChangeTask(@RequestParam Long id) {
//        shipmentService.submitChangeTask(id, WebFrameworkUtils.getLoginUserId());
//        return success(true);
//    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        shipmentService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        shipmentService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除出运变更")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:delete')")
    public CommonResult<Boolean> deleteChangeShipmentChange(@RequestParam("id") Long id) {
        shipmentService.deleteShipmentChange(id);
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认")
    @PreAuthorize("@ss.hasPermission('dms:shipment-change:confirm')")
    public CommonResult<Boolean> confirm(@RequestParam Long id) {
        shipmentService.updateConfirmFlag(ConfirmFlagEnum.YES.getValue(), id);
        return success(true);
    }

    @GetMapping("/confirm-source")
    @Operation(summary = "获取确认来源列表")
//    @PreAuthorize("@ss.hasPermission('dms:shipment-change:confirm-source')")
    public CommonResult<List<ConfirmSourceEntity>> getConfirmSource(@RequestParam Long id) {
        List<ConfirmSourceEntity> confirmSourceEntities = shipmentService.getConfirmSourceListByShipmentId(id);
        return success(confirmSourceEntities);
    }

    @GetMapping("/related-num")
    @Operation(summary = "关联单据数量")
    public CommonResult<RelatedShipmentRespVO> getRelatedNum(@RequestParam Long id) {
        return success(shipmentService.getRelatedNum(id));
    }

    // 出库


    @PutMapping("/forwarder-fee-update")
    @Operation(summary = "更新出运单船代费用")
    @PreAuthorize("@ss.hasPermission('dms:shipment:forwarder-fee-update')")
    public CommonResult<Boolean> updateShipmentForwarderFee(@Valid @RequestBody ShipmentForwarderFeeSaveReqVO updateReqVO) {
        return success(shipmentService.updateShipmentForwarderFee(updateReqVO));
    }

    @GetMapping("/get-list-by-code")
    @Operation(summary = "根据编号字符串获得船代费用列表")
    public CommonResult<List<ForwarderFeeShareFeeRespVO>> getListByCodeList(@Valid String codeList) {
        List<ForwarderFeeShareFeeRespVO> pageResult = shipmentService.getListByCodeList(codeList);
        return success(pageResult);
    }


    @PutMapping("/split-item")
    @Operation(summary = "拆分出运单明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:split-item')")
    public CommonResult<List<ShipmentItem>> splitShipmentItem(@RequestParam("ids") List<Long> ids ) {
        return success(shipmentService.splitShipmentItem(ids));
    }

    @PostMapping("/update-shipment-item")
    @Operation(summary = "更新出运单明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:split-item')")
    public CommonResult<Boolean> updateShipmentItem(@RequestBody List<ShipmentItem> shipmentItemList) {
        shipmentService.batchUpdateShipmentItem(shipmentItemList);
        return success(true);
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('dms:shipment:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long shipmentId) {
        return success(shipmentService.antiAudit(shipmentId));
    }

    @PutMapping("/gen-contract")
    @Operation(summary = "生成内部合同",description = "生成内部合同")
    @PreAuthorize("@ss.hasPermission('dms:shipment:gen-contract')")
    public CommonResult<Boolean> genContract(@RequestParam Long shipmentId) {
        shipmentService.genInternalContract(shipmentId);
        return success(true);
    }

    @PutMapping("/delete-gen-contract")
    @Operation(summary = "删除内部合同",description = "删除内部合同")
    @PreAuthorize("@ss.hasPermission('dms:shipment:delete-gen-contract')")
    public CommonResult<Boolean> deleteGenContract(@RequestParam Long shipmentId) {
        shipmentService.deleteGenContract(shipmentId);
        return success(true);
    }

    @PutMapping("/split-shipment")
    @Operation(summary = "下推出运明细",description = "下推出运明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:split-shipment')")
    public CommonResult<CreatedResponse> splitShipment(@RequestBody BatchShipmentReq req) {
        CreatedResponse createdResponse = shipmentService.splitShipment(req.getItemList());
        return success(createdResponse);
    }

    @PutMapping("/batch-flag")
    @Operation(summary = "分批出运",description = "分批出运")
    @PreAuthorize("@ss.hasPermission('dms:shipment:batch-flag')")
    public CommonResult<Boolean> batchFlag(@RequestParam Long shipmentId) {
        shipmentService.batchFlag(shipmentId);
        return success(true);
    }

    @PutMapping("/cancel-batch-flag")
    @Operation(summary = "取消分批出运",description = "取消分批出运")
    @PreAuthorize("@ss.hasPermission('dms:shipment:cancel-batch-flag')")
    public CommonResult<Boolean> cancelBatchFlag(@RequestParam Long shipmentId) {
        shipmentService.cancelBatchFlag(shipmentId);
        return success(true);
    }

    @PutMapping("/update-thumbnail")
    @Operation(summary = "更新缩略图")
    public CommonResult<Boolean> updateThumbnail() {
        shipmentService.updateThumbnail();
        return success(true);
    }

    @PutMapping("/delete-item")
    @Operation(summary = "移除出运单明细",description = "移除出运单明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:delete-item')")
    public CommonResult<Boolean> deleteItem(@RequestParam List<Long> ids) {
        shipmentService.deleteItem(ids,true);
        return success(true);
    }


    @PutMapping("/merge-item")
    @Operation(summary = "移除出运单明细",description = "移除出运单明细")
    @PreAuthorize("@ss.hasPermission('dms:shipment:merge-item')")
    public CommonResult<Boolean> mergeItem(@RequestParam List<Long> ids, @RequestParam Long targetId) {
        shipmentService.mergeItem(ids,targetId);
        return success(true);
    }

}
