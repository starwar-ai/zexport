package com.syj.eplus.module.sms.service.salecontract;

import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.SaleTabEnum;
import com.syj.eplus.module.sms.api.dto.*;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 外销合同 Service 接口
 *
 * @author du
 */
public interface SaleContractService extends IService<SaleContractDO> {

    /**
     * 创建外销合同
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createSaleContract(@Valid SaleContractSaveReqVO createReqVO);

    /**
     * 更新外销合同
     *
     * @param updateReqVO 更新信息
     */
    void updateSaleContract(@Valid SaleContractSaveReqVO updateReqVO);

    /**
     * 删除外销合同
     *
     * @param id 编号
     */
    void deleteSaleContract(Long id);

    /**
     * 获得外销合同
     *
     * @param saleContractDetailReq 明细请求实体
     * @return 外销合同
     */
    SaleContractRespVO getSaleContract(SaleContractDetailReq saleContractDetailReq, SaleTabEnum type);

    /**
     * 获得外销合同分页
     *
     * @param pageReqVO 分页查询
     * @param isExport 是否为导出场景，true-导出场景简化处理，false-完整业务处理
     * @return 外销合同分页
     */
    PageResult<SaleContractRespVO> getSaleContractPage(SaleContractPageReqVO pageReqVO, boolean isExport);

    /**
     * 获得外销合同产品模式分页（按明细分页，扁平结构）
     * 用于产品视图的扁平化展示，只返回符合搜索条件的明细
     *
     * @param pageReqVO 分页查询
     * @param isExport  是否为导出场景
     * @return 外销合同产品模式分页
     * @author 波波
     */
    PageResult<SaleContractProductModeRespVO> getProductModePage(SaleContractPageReqVO pageReqVO, boolean isExport);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getExportProcessDefinitionKey();


    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getDomesticProcessDefinitionKey();

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getDomesticChangeProcessDefinitionKey();

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getFactoryProcessDefinitionKey();

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO, boolean changeFlag);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO, boolean changeFlag);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitExportTask(Long id, Long userId);


    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitDomesticTask(Long id, Long userId);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitfactoryTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId, Integer status);

    /**
     * 销售合同变更
     *
     * @param updateReqVO
     */
    List<CreatedResponse> changeSaleContract(ChangeSmsContractSaveReqVO updateReqVO);

    /**
     * 提交变更任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    String submitChangeTask(Long id, Long userId, String modelKey);

    /**
     * 更新变更单审核状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    /**
     * 回写变更数据
     *
     * @param changeId 变更记录id
     */
    void updateChangeResult(Long changeId);

    /**
     * 获得销售合同变更分页
     *
     * @param pageReqVO 分页查询
     * @return 销售合同分页
     */
    PageResult<SaleContractChange> getChangeSaleContractPage(SaleContractChangePageReq pageReqVO);


    /**
     * 获取采购合同变更详情
     *
     * @param req
     * @return
     */
    SaleContractChange getSaleContractChangeDetail(SaleContractDetailReq req);

//    /**
//     * 更新采购合同变更单
//     *
//     * @param updateInfoReqVO 更新信息
//     */
//    void updateChangeSaleContract(@Valid SaleContractChange updateInfoReqVO);

    /**
     * 删除采购合同变更
     *
     * @param id 编号
     */
    void deleteChangeSaleContract(Long id);


    PageResult<SaleContractSimpleRespVO> getSaleContractSimplePage(SaleContractPageReqVO pageReqVO);

    /**
     * 销售合同下推采购计划
     *
     * @param saleContractId 销售合同id
     */
    List<CreatedResponse> toContract(Long saleContractId);


    /**
     * 根据产品编号获取近期成交价
     *
     * @param req 请求体
     * @return 近期成交价列表
     */
    List<HistoryTradePriceResp> getHistoryTradePriceBySkuCode(HistoryTradePriceReq req);

    /**
     * 获取下推出运计划中间页面数据
     *
     * @param req 请求体
     * @return 返回结果
     */
    PushOutShipmentPlanResp getPushOutShipmentPlanResp(PushOutShipmentPlanReq req);

    /**
     * 根据销售合同编号获取加减项列表
     *
     * @param saleContractCodeList 销售合同编号列表
     * @return 加减项列表
     */
    List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> saleContractCodeList);

    /**
     * 批量更新加减项
     *
     * @param addSubItemDTOList 加减项列表
     */
    void batchUpdateAddSubItem(List<AddSubItemDTO> addSubItemDTOList);


    /**
     * 外销合同回签
     *
     * @param signBackReq 请求体
     */
    void signBackSaleContract(SignBackReq signBackReq);

    /**
     * 外销合同结案
     *
     * @param closeSaleContractReq 请求
     */
    void closeSaleContract(CloseSaleContractReq closeSaleContractReq);

    /**
     * 回写出运数量
     *
     * @param shipmentQuantityMap 出运数量map
     */
    void updateShipmentQuantity(Map<Long, Integer> shipmentQuantityMap, boolean cancelFlag);

    /**
     * 更新确认状态
     *
     * @param confirmFlag 确认状态
     */
    void updateConfirmFlag(Integer confirmFlag, Long id);

    void updateConfirmFlag(Integer confirmFlag, String code);

    void updateCust(Long custId, String custName, String custCode);

    /**
     * 认领回写收款计划及加项
     *
     * @param writeBackDTOList 认领明细DTO
     */
    void writeBackContract(List<WriteBackDTO> writeBackDTOList,boolean rollbackFlag);

    List<SaleContractDO> getSmsByCodeList(List<String> codeList);

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
    String print(List<Long> ids, String reportCode, Long reportId, Long companyId, String printType);

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
    String domesticPrint(List<Long> ids, String reportCode, Long reportId, Long companyId);
    /**
     * 获取库存明细
     *
     * @param req 请求体
     * @return 库存明细
     */
    PageResult<StockDTO> getItemStock(ItemStockReq req);

    /**
     * 根据销售合同号获取客户信息
     *
     * @param contractCode 销售合同编号
     * @return 客户信息列表
     */
    List<SimpleContractRespVO> getSimpleContractRespVoList(String contractCode);

    /**
     * 生成订单路径中的购销合同
     *
     * @return
     */
    Map<Long,Map<String, JsonAmount>> generateContract(List<SaleContractItem> saleContractItems,Map<Long,SaleContractDO> saleContractDOMap,Map<Long, Tuple> realPurchasePriceCollect,String genContractUniqueCode);

    String getProcessDefinitionKeyByBusinessId(Long id);

    /**
     * 处理销售合同变更影响范围
     *
     * @param changeId 变更主键
     */
    void updateEffectRanageStatus(Long changeId);

    /**
     * 获得外销合同变更影响列表
     *
     * @param targetCode 影响范围主键
     * @return 外销合同变更影响列表
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    /**
     * 更新外销合同变更确认状态
     *
     * @param effectRangeType 影响范围类型
     * @param code            影响范围编号
     */
    void updateChangeConfirmFlag(Integer effectRangeType, String code);

    /**
     * 获取影响销售合同变更的来源信息
     *
     * @param id 销售合同主键
     * @return 变更来源信息
     */
    List<ConfirmSourceEntity> getConfirmSourceListBySaleContractId(Long id);

    /**
     * 编号
     * @param custCode
     * @return
     */
    List<Long> getSaleContractIdsByCustCode(String custCode);

    /**
     * 编号
     * @param skuCode
     * @return
     */
    List<Long> getSaleContractIdsBySkuCode(String skuCode);

    /**
     * 根据销售合同id获取关联单据数量
     * @param id 销售合同id
     * @return 关联单据数量
     */
    RelatedSaleContractRespVO getRelatedNum(Long id);

    /**
     * 设计稿上传编辑
     * @param updateReqVO
     */
    void updatePurchaseDesignContract(SaleContractDesignSaveReqVO updateReqVO);

    Map<Long, SaleContractItem> getItemListByItemIds(List<Long> itemIdList);


//    /**
//     * 根据编号获取关联销售合同列表
//     * @param codeList
//     * @return
//     */
//    List<SimpleLinkSaleContractRespVO> getSimpleLinkSaleContract(List<String> codeList);

    /**
     * 批量更新销售合同入库状态
     * @param billSimpleDTOList 入库列表
     */
    void batchUpdateBillStatus(List<SaleContractBillSimpleDTO> billSimpleDTOList);

    List<CreatedResponse> batchToContractTask(List<Long> saleContractIdList);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    /**
     * 根据销售合同编号获取订单路径
     * @param saleContractCodes 销售合同编号列表
     * @return 订单路径
     */
    List<JsonCompanyPath> getCompanyPathBySaleContractCodeList(List<String> saleContractCodes);

    /**
     * 根据来源编号获取自动生成销售合同信息
     * @param sourceSaleContractCode 来源编号
     * @param conpanyId 公司主键
     * @return 销售合同信息
     */
    SimpleData getSimpleSaleContractData(String sourceSaleContractCode, Long conpanyId);

    /**
     * 根据来源编号批量获取自动生成销售合同信息
     * @param contractCodeList 来源编号
     * @param conpanyId 公司主键
     * @return 销售合同信息
     */
    Map<String,SimpleData> getBatchSimpleSaleContractData(List<String> contractCodeList,Long conpanyId,Boolean isSale);

    /**
     * 根据唯一标识批量更新销售合同明细含税价格
     * @param withTaxPriceMap 采购含税价格缓存
     */
    void updateItemWithTaxPriceAndManager(Map<String, JsonAmount> withTaxPriceMap, UserDept manager);

    /**
     * 校验合同状态
     * @param req 请求体
     */
    void checkContractStatus(PushOutShipmentPlanReq req);

    /**
     * 计算真实采购价
     * @param saleItems 销售明细id
     */
    Map<Long,Tuple> calcRealPurchasePrice(List<Long> saleItems);

    /**
     * 更新销售合同真实采购价
     * @param saleItems 销售明细id
     * @param manager 跟单员
     * @param purchaseUserId 真实采购员
     */
    void updateRealPurchasePrice(List<Long> saleItems, UserDept manager,Long purchaseUserId);

    Long getSaleContractIdByCode(String saleContractCode);

    /**
     * 校验公司路径
     * @param contractCodeList 销售合同编号列表
     */
    void validateCompanyPath(Set<String> contractCodeList);

    /**
     * 根据销售合同编号获取销售合同链路编号缓存
     * @param saleContractCodeList 销售合同编号列表
     * @return 销售合同链路编号缓存
     */
    Map<String,List<String>> getLinkCodeMap(List<String> saleContractCodeList);

    /**
     * 根据销售合同编号获取销售合同主键
     * @param code 销售合同编号
     * @return 销售合同主键
     */
    Long getIdByCode(String code);

    void batchUpdateShipmentTotalQuantity(List<ShipmentQuantityDTO> dtoList);

    List<SaleContractItem> getItemListByIds(List<Long> saleIdList);

    /**
     * 回写销售合同明细的转采购标识
     * @param itemIds 销售合同明细id列表
     * @param purchaseFlag 转采购标识
     */
    void updateSaleItemPurchaseFlag(List<Long> itemIds, Integer purchaseFlag);

    /**
     * 编号
     * @param saleContractCode
     * @return
     */
    List<Long> getSaleContractIdsBySaleContractCode(String saleContractCode);

    /**
     * 获得变更外销合同影响的范围
     *
     * @param changeReqVO
     * @return
     */
    ChangeEffectRespVO getChangeEffect(SaleContractRespVO changeReqVO);

    /**
     * 删除自动生成的销售合同
     * @param sourceSaleContractCode 来源销售合同编号
     */
    void deleteAutoSaleContract(List<String> sourceSaleContractCode);

    void reLockSaleContract(ReLockReq reLockReq);

    /**
     * 批量更新销售合同转出运标识
     * @param shippingQuantityMap 出运数量缓存
     */
    void batchUpdateConvertShipmentFlag(Map<Long,Integer> shippingQuantityMap);

    /**
     * 更新销售合同出运日期
     * @param smsContractCodeList 销售合同编号列表
     */
    void updateShipmentDate(List<String> smsContractCodeList,List<Integer> dateTypes);

    void setReLockFlag(List<Long> saleItemIdList, Boolean reLockFlag);

    void setShipmentQuantity(Map<Long, Integer> itemQuantityMap);

    /**
     * 根据销售合同编号获取销售合同信息
     * @param saleContractCodeList 销售合同编号列表
     * @return 销售合同信息
     */
    Map<String,List<Long>> getCompanyIdListBySaleContractCodeList(List<String> saleContractCodeList);

    /**
     * 批量更新销售合同明细已转运数量
     * @param shippedQuantityMap 销售明细出运数量缓存
     */
    void batchUpdateShippedQuantity(Map<Long,Integer> shippedQuantityMap);

    /**
     * 批量更新销售合同明细转出运数量
     */
    void batchUpdateTransformShippedQuantity(Map<Long,Integer> transformShippedQuantityMap);

    /**
     * 更新销售合同变更状态
     * @param changeId 变更主键
     */
    void updateContractChangeStatus(Long changeId);

    /**
     * 导出/打印外销合同（支持单个或批量，支持Excel导出和PDF打印）
     *
     * @param ids 外销合同ID列表（单个导出时传入只有一个元素的列表）
     * @param exportType 导出类型（区分内销/外销/工厂）
     * @param reportCode 模板编码
     * @param response HTTP响应
     * @throws IOException IO异常
     * @author 波波
     */
    void exportExcel(List<Long> ids, String exportType, String reportCode, HttpServletResponse response) throws IOException;

    void setOrderDone(Long id);

    Boolean checkCollectionPlan(String saleContractCode,Boolean isPurchase, BigDecimal needSum);

    Map<String, SaleContractItemDTO> getItemListByUniqueCodes(List<String> saleUniqueCodeList);

    Map<Long, String> getItemCodesByIds(List<Long> itemIds);

    /**
     *
     * @param saleItemQuantity
     * @return
     */
    Map<Long,SimpleSaleItemDTO> splitSaleContractItem(Map<Long, Tuple> saleItemQuantity);

    void updateShipmentedQuantity(List<CloseShipmentDTO> closeReq);


    /**
     * 回写销售合同明细拆单信息
     * @param splitList 拆单信息
     */
    void rollbackSaleContractItemSplitList(List<SplitPurchase> splitList);


    /**
     * 生成内部合同
     * @param saleItemMap 销售明细数量缓存
     */
    void genInternalContract(Map<Long,Integer> saleItemMap,Map<Long, Integer> shipmentPurchaseMap,String genContractUniqueCode,Map<Long,SimpleShipment> simpleShipmentMap);

    /**
     * 删除内部合同
     * @param genContractUniqueCode 内部生成唯一编码
     */
    void deleteGenContract(String genContractUniqueCode);

    List<CreatedResponse> batchMergeToContractTask(List<Long> saleContractIdList);

    /**
     * 通过销售明细id获取加工主体
     * @param saleContractItemId 销售明细id
     * @return 加工主体
     */
    Long getManufactureCompanyIdByItemId(Long saleContractItemId);

    /**
     * 通过销售合同号获取出运对应的应收金额
     * @param saleContractCodeList 销售合同编号
     * @return 出运对应的应收金额
     */
    Map<String,List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList);


    /**
     * 回写真实采购数量
     * @param purchaseQuantityMap 真实采购数量缓存
     */
    void rollbackSaleContractItemPurchaseQuantity(Map<Long,Integer> purchaseQuantityMap);

    /**
     * 导出Word
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportWord(Long id, String reportCode, Long reportId, Long companyId, HttpServletResponse response);

    /**
     * 重新锁定销售合同库存
     *
     * @param saleContractItemIdList 销售明细主键列表
     */
    void reLockStock(List<Long> saleContractItemIdList);

    /**
     * 获取销售明细信息
     * @param saleContractItemId 销售明细主键
     * @return 销售明细信息
     */
    SaleContractItemDTO getSaleContractItemById(Long saleContractItemId);

    /**
     * 删除拆分销售合同明细
     * @param saleContractItemIdList 销售明细主键列表
     */
    Integer deleteSplitSaleItem(List<Long> saleContractItemIdList);

    /**
     * 回写来源销售合同明细数量及转采购标识
     * @param saleContractItemIdList 销售明细主键列表
     */
    void rollbackSaleContractItemSourceList(List<Long> saleContractItemIdList);

    /**
     * 根据拆分后销售明细id获取拆分前销售明细id缓存
     * @param saleContractItemIdList 销售明细id列表
     * @return 销售明细id缓存
     */
    Map<Long,String> getSaleContractItemIdMap(List<Long> saleContractItemIdList);

    ContainerMidVO getOutNoticeMid(List<Long> ids);

    List<CreatedResponse> createNotice(ContainerMidVO containerMidVO);

    void genInternalContractByOutBill(Map<String,Integer> saleItemMap,String genContractUniqueCode);

    /**
     * 根据销售合同编号获取订单路径
     * @param saleContractCodes 销售合同编号列表
     * @return 订单路径
     */
    Map<String,JsonCompanyPath> getCompanyPathMapBySaleContractCodeList(List<String> saleContractCodes);

    /**
     * 批量更新销售合同
     * @param saleContractCodeSet 销售合同编号列表
     */
    void batchUpdatePurchaseUser(Set<String> saleContractCodeSet);

    /**
     * 回写采购赠品数量
     * @param freeQUantityMap 采购赠品数量缓存
     */
    void rewritePurchaseFreeQuantity(Map<Long,Integer> freeQUantityMap,boolean isConcel);

    /**
     * 根据销售合同编号获取销售员列表
     * @param contractCodeList 销售合同编号列表
     * @return 销售员列表
     */
    Map<String,List<UserDept>> getSalesByContractCodeSet(Set<String> contractCodeList);


    /**
     * 更新销售合同订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    void updateThumbnail();

    /**
     * 更新销售合同转出运数量
     * @param shipmentTotalQuantityMap 出运数量缓存
     */
    void updateShipmentTotalQuantity(Map<Long,Integer> shipmentTotalQuantityMap,boolean isDeletedFlag);

    /**
     * 获取销售合同明细金额缓存
     * @param saleContractItemIdList 销售明细id列表
     * @return 销售合同明细金额缓存
     */
    Map<Long,JsonAmount> getSaleContractItemAmountMap(List<Long> saleContractItemIdList);

    /**
     * 导出销售合同Excel
     * @param pageReqVO 分页查询参数
     * @param fileName 导出文件名前缀
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportSaleContractExcel(SaleContractPageReqVO pageReqVO, String fileName, HttpServletResponse response) throws IOException;

    /**
     * 更新收款计划
     * @param req 更新收款计划参数
     */
    void updateCollectionPlan(UpdateCollectionPlanReq  req);

    /**
     * 获取销售合同明细采购数量缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细采购数量缓存
     */
    Map<Long,Map<String,Integer>> getSalePurchaseAndStockQuantityMap(Collection<Long> itemIds);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);

    /**
     * 获取销售合同明细采购价格缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细采购价格缓存
     */
    Map<Long,JsonAmount> getRealPurchasePriceMapByItemIds(Collection<Long> itemIds);

    /**
     * 获取销售合同明细销售类型缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细销售类型缓存
     */
    Map<Long,Boolean> getSaleItemSaleType(Collection<Long> itemIds);
}
