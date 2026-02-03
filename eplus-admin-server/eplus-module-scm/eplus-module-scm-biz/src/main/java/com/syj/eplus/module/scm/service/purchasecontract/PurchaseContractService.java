package com.syj.eplus.module.scm.service.purchasecontract;

import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryPurchaseContractDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.*;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationAllocationSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemAndContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemBoardRespVO;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeReqVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采购合同 Service 接口
 *
 * @author zhangcm
 */
public interface PurchaseContractService extends IService<PurchaseContractDO> {

    /**
     * 创建采购合同
     *
     * @param createInfoReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createPurchaseContract(@Valid PurchaseContractSaveInfoReqVO createInfoReqVO);

    /**
     * 更新采购合同
     *
     * @param updateInfoReqVO 更新信息
     */
    Long updatePurchaseContract(@Valid PurchaseContractSaveInfoReqVO updateInfoReqVO);

    /**
     * 更新确认状态
     *
     * @param confirmFlag 确认状态
     */
    void updateConfirmFlag(Integer confirmFlag,  Long id);
    void updateAuxiliaryConfirmFlag(Integer confirmFlag,  Long id);

    void updateConfirmFlag(Integer confirmFlag,  String code);
    void updateVenderId(Long venderId, String venderCode);

    /**
     * 删除采购合同
     *
     * @param id 编号
     */
    void deletePurchaseContract(Long id,Integer isAuxiliary);

    /**
     * 删除采购合同变更
     *
     * @param id 编号
     */
    void deleteChangePurchaseContract(Long id);

    /**
     * 获得采购合同
     *
     * @param purchaseContractDetailReq 明细请求实体
     * @return 采购合同
     */
    PurchaseContractInfoRespVO getPurchaseContract(PurchaseContractDetailReq purchaseContractDetailReq);

    PurchaseContractInfoRespVO getPurchaseContractContainRelations(PurchaseContractDetailReq purchaseContractDetailReq);


    /**
     * 获得采购合同
     *
     * @param id 明细请求实体
     * @return 采购合同
     */
    List<PurchaseContractRespVO> getPurchaseContractListByPlanId(Long id);

    List<PurchaseContractItemAndContractInfoRespVO> getPurchaseContractItemInfoListByPlanId(List<Long> id);


    /**
     * 获得采购合同分页
     *
     * @param pageReqVO 分页查询
     * @return 采购合同分页
     */
    PageResult<PurchaseContractInfoRespVO> getPurchaseContractPage(PurchaseContractPageReqVO pageReqVO);

    /**
     * 获得采购合同产品模式分页
     * 用于产品视图的分页查询，将采购合同明细展开为扁平化列表，每条明细作为一行
     *
     * @param pageReqVO 分页查询
     * @return 采购合同产品模式分页
     */
    PageResult<PurchaseContractProductModeRespVO> getProductModePage(PurchaseContractPageReqVO pageReqVO);

    /**
     * 获得采购合同变更分页
     *
     * @param pageReqVO 分页查询
     * @return 采购合同分页
     */
    PageResult<PurchaseContractChange> getChangePurchaseContractPage(ChangePurchasePageReq pageReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

    /**
     * 获取辅料流程标识
     *
     * @return 流程标识
     */
    String getAuxiliaryProcessDefinitionKey();

    /**
     * 获取自营产品流程标识
     *
     * @return 自营流程标识
     */
    String getProcessDefinitionOwnBrandKey();

    /**
     * 获取变更流程标识
     *
     * @return 流程标识
     */
    String getChangeProcessDefinitionKey();


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
     * @param id                   业务id
     * @param userId               用户id
     * @param processDefinitionKey 流程标识
     */
    void submitTask(Long id, Long userId, String processDefinitionKey,Map<String, Object> variables);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId);

    /**
     * 更新变更单审核状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    void finishPurchaseContract(List<Long> contractIdList);

    void rollBackFinishPurchaseContract(List<Long> contractIdList);

    void placeOrderPurchaseContract(List<Long> contractIdList);

    boolean completeOrderTask(CompleteOrderReqDTO completeOrderReq);

//    void changePurchaseContract(List<Long> contractIdList);

//    void warehousingPurchaseContract(PurchaseContractToStockNoticeSaveReqVO reqVO);

//    void checkPurchaseContract(List<Long> contractIdList);
//
//    void exchangePurchaseContract(List<Long> contractIdList);
//
//    void payPurchaseContract(List<Long> contractIdList);

    void signBackPurchaseContract(PurchaseContractSignBackReqVO contractIdList);

    /**
     * 采购合同变更
     *
     * @param updateReqVO
     */
    List<CreatedResponse> changePurchaseContract(ChangePurchaseContract updateReqVO);

    PageResult<PurchaseContractItemBoardRespVO> getPurchaseContractBoardPage(PurchaseContractPageReqVO pageReqVO);

    /**
     * 提交变更任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    String submitChangeTask(Long id, Long userId,String modelKey,Map<String, Object> variable);

    /**
     * 获取采购合同变更详情
     *
     * @param purchaseContractDetailReq
     * @return
     */
    PurchaseContractChange getPurchaseContractContainChangeRelations(PurchaseContractDetailReq purchaseContractDetailReq);

    /**
     * 回写变更数据
     *
     * @param changeId 变更记录id
     */
    void updateChangeResult(Long changeId);

    /**
     * 更新采购合同变更单
     *
     * @param updateInfoReqVO 更新信息
     */
    void updateChangePurchaseContract(@Valid PurchaseContractChange updateInfoReqVO);

    void donePurchaseContract(List<Long> contractIdList);

    void SetArrivedDatePurchaseContract(PurchaseContractSetArrivedDateReqVO voReq);

    /**
     * 采购合同转入库通知单
     *
     * @param id
     * @return
     */
    CreatedResponse toStockNoticeById(Long id);

    /**
     * 采购合同转入库通知单
     *
     * @param stockNoticeReqVO
     * @return
     */
    CreatedResponse toStockNotice(StockNoticeReqVO stockNoticeReqVO);

    /**
     * 打印
     *
     * @param id
     * @param reportCode
     * @param sourceCode
     * @param sourceType
     * @param companyId
     * @return
     */
    String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId,Boolean isAuxiliary);

    PageResult<PurchaseContractSimpleDTO> getPurchaseContractSimplePage(PurchaseContractGetSimplePageReqDTO pageReqVO);

    void rePurchaseById(RePurchaseSaveReqVO reqVO);

    List<PurchaseContractDO> selectByIdList(List<Long> idList);

    /**
     * 根据采购合同编号批量获取跟单员缓存
     *
     * @param codeList 采购合同编号列表
     * @return 采购合同跟单员缓存
     */
    Map<String, UserDept> getPurManagerMapByCodeList(List<String> codeList);

    /**
     * 校验采购合同是否存在
     *
     * @param codeList 采购合同编号列表
     * @return 不存在的采购合同编号列表
     */
    Collection<String> validatePurContractExists(Collection<String> codeList);

    /**
     * 根据销售合同编号列表批量获取采购合同精简缓存
     *
     * @param codeList 销售合同编号列表
     * @return 采购合同精简缓存
     */
//    Map<String, PurchaseContractDTO> getPurchaseContractMap(List<String> codeList,Long companyId);

    List<PurchaseContractDO> getPurchaseByCodeList(List<String> codeList);

    /**
     * 根据采购合同id获取付款计划列表
     *
     * @param idList 采购合同id列表
     * @return 付款计划列表
     */
    List<PurchasePaymentPlan> getPurchasePaymentPlanList(List<Long> idList);

    void signBackContract(SignBackReq signBackReq);

    String getProcessDefinitionKeyByBusinessId(Long id);

    /**
     * 处理采购合同变更影响范围
     *
     * @param changeId 变更主键
     */
    void updateEffectRanageStatus(Long changeId);

    /**
     * 编号
     * @param venderCode
     * @return
     */
    List<Long> getPurchaseContractIdsByVenderCode(String venderCode);

    /**
     * 编号
     * @param skuCode
     * @return
     */
    List<Long> getPurchaseContractIdsBySkuCode(String skuCode);

    /**
     * 获取影响采购合同变更的来源信息
     *
     * @param id 采购合同主键
     * @return 变更来源信息
     */
    List<ConfirmSourceEntity> getConfirmSourceListByPurchaseContractId(Long id, EffectRangeEnum effectRangeEnum);

    /**
     * 获取影响采购合同变更的来源信息
     *
     * @param id 采购合同主键
     * @return 变更来源信息
     */
    List<ConfirmSourceEntity> getConfirmSourceListByAuxiliaryPurchaseContractId(Long id);
    /**
     * 获得采购合同变更影响列表
     *
     * @param targetCode 影响范围主键
     * @return 采购合同
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    /**
     * 根据采购计划查询关联采购合同数量
     * @param planId 采购计划主键
     * @return 采购合同数量
     */
    Long getPurchaseContractNumByPlanId(Long planId);

    /**
     * 获取采购合同关联单据
     * @param id 采购合同主键
     * @return 采购合同关联单据
     */
    RelatedPurchaseContractRespVO getRelatedNum(Long id);

    /**
     * 根据销售合同id获取采购合同数量
     * @param auxiliaryFlag 辅料标识
     * @param id 销售合同id
     * @return 采购合同数量
     */
    Long getOrderNumBySaleContractId(boolean auxiliaryFlag,Long id);

    /**
     * 编辑设计稿
     * @param updateReqVO
     */
    void updatePurchaseDesignContract(PurchaseContractDesignSaveReqVO updateReqVO);

    /**
     * 下单
     * @param id
     */
    void placeOrderContract(Long id);

    /**
     * 批量转入库通知单中间页
     * @param ids 明细id列表
     * @return 入库通知单中间页
     */
    List<TransformNoticeMidVO> getToNoticeMid(List<Long> ids);

    /**
     * 批量转入库通知单
     *
     * @param req 请求体
     * @return 响应结果
     */
    List<CreatedResponse> batchToStockNotice(List<TransformNoticeMidVO> req);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    /**
     * 转辅料采购中间页
     * @param ids
     * @return
     */
    List<TransformAuxiliaryMidVO> getToAuxiliaryMid(List<Long> ids);

    List<CreatedResponse> batchToSAuxiliary(List<TransformAuxiliaryMidVO> req);


    /**
     * 更新采购合同出运日期
     * @param saleContractItemIdList 销售明细id
     */
    void updateShipmentDate(List<Long> saleContractItemIdList, List<Integer> dateTypes, LocalDateTime estDepartureTime);

    List<PurchaseAuxiliaryAllocationDO> getAuxiliaryAllocationList(AuxiliaryAllocationReq req);

    void updateAuxiliaryAllocationList(PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO);

    void deleteAuxiliaryAllocationList(Long itemId);

    /**
     * 回写真实采购含税价和跟单员
     * @param id 采购合同主键
     */
    void updateRealTaxPriceAndManager(Long id);

    List<AuxiliaryPurchaseContractDTO> getAuxiliaryAllocationListByContractCode(String code);

    Map<String, String> getSaleCustPOByPurchaseCodes(List<String> purchaseContractCodeList);

    void updatePurchaseAmount(String businessCode, BigDecimal amount);

    InvoiceNoticeVO toInvoiceNoticedMid(Long id);

    List<CreatedResponse> toInvoiceNoticed(InvoiceNoticeVO req);

    Map<Long, PurchaseContractItemDTO> getPurchaseContractMap(List<Long> saleItemIdList, Map<Long,Long> lastCompanyPathMap );

    /**
     * 通过销售明细id跟主体获取对应采购信息
     * @param saleItemIdList 销售明细id
     * @param companyId 公司id
     * @return  采购合同信息
     */
    Map<Long,String> getPurchaseContractCodeBySaleItemIdList(List<Long> saleItemIdList,Long companyId);

    /**
     * 通过采购合同号获取链路编号
     * @param codeList 采购合同号
     * @return 链路编号
     */
    Map<String,List<String>> getLinkCodeMapByCodeList(List<String> codeList);

    void updateCheckStatus(String purchaseContractCode, Integer value);

    /**
     * 获得变更采购合同影响的范围
     *
     * @param changeReqVO
     * @return
     */
    ChangeEffectRespVO getChangeEffect(PurchaseContractInfoRespVO changeReqVO);

    /**
     * 校验销售明细是否存在对应采购明细
     *
     * @param saleContractItemIds 销售合同明细id列表
     * @return 是否存在对应采购明细
     */
    Collection<Long> validatePurContractItemExists(Collection<Long> saleContractItemIds);

    /**
     * 客户编号
     * @param saleContractCode
     * @return
     */
    List<Long> getPurchaseContractIdsBySaleContractCode(String saleContractCode);

    /**
     * 根据销售明细id列表获取采购明细采购数量缓存
     * @param saleItemIds 销售明细id列表
     * @return 采购明细采购数量缓存
     */
    Map<Long,Map<String,Integer>> getItemQuantityMapBySaleItemIds(List<Long> saleItemIds);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportExcel(Long id, String reportCode, HttpServletResponse response);

    /**
     * 导出Word
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportWord(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId, HttpServletResponse response);

    /**
     * 更新采购合同明细开票数量
     * @param invoiceQuantityDTO 开票数量
     */
    void updateInvoiceData(List<InvoiceQuantityDTO> invoiceQuantityDTO,boolean closeFlag);

    void setOrderDone(Long id);

    /**
     * 更改采购合同变更状态
     * @param id 主键
     * @param status 状态
     */
    void updateChangeStatus(Long id,Integer status);

    /**
     * 更新出运明细验货状态
     * @param contractItemMap 验货状态缓存
     */
    void updateShipmentCheckStatus(Map<Long, Integer> contractItemMap);


    /**
     * 获取销售合同明细验货状态
     * @return 验货状态缓存
     */
    Map<Long, Map<String, Integer>> getCheckStatusMap(Set<Long> saleContractItemIds);

    /**
     * 根据采购合同编号获取付款计划信息
     * @param code 采购合同编号
     * @return 付款计划信息
     */
    List<PurchasePaymentPlan> getPurchasePaymentPlanListByCode(String code);

    /**
     * 修改验货信息
     * @param updateInspectionDTOMap 验货信息
     */
    void updatePurchaseInspectionData(Map<Long,UpdateInspectionDTO> updateInspectionDTOMap);

    /**
     * 获取采购合同明细缓存
     * @param saleColeList 销售编号列表
     * @return 采购合同明细缓存
     */
    Map<Long,List<SimplePurchaseContractItemDTO>> getSimplePurchaseContractItemMap(List<String> saleColeList);

    void updateAuxiliaryPaymentStatusByCodes(List<String> relationCode, Integer value);

    List<FeeShareResp> getListByCodeList(String codeList);

    void updateAuxiliaryPurchaseAmount(String code, JsonAmount amount);

    /**
     * 通过销售合同编号获取采购员列表
     * @param saleContractCodeSet 销售合同编号列表
     * @return 采购员列表
     */
    Map<String,List<UserDept>> getPurchaseUserListBySaleContractCodeSet(Set<String> saleContractCodeSet);

    /**
     * 过滤已转开票合同号
     * @param purchaseCodeList 采购合同列表
     * @return 已转开票的合同列表
     */
    Set<String> filterInvoicedPurchaseContractCode(Collection<String> purchaseCodeList);

    /**
     * 根据采购合同编号获取采购员列表
     * @param contractCodeList 采购合同编号列表
     * @return 采购员列表
     */
    Map<String, List<UserDept>> getPurchaseUserByContractCodeSet(Set<String> contractCodeList);

    /**
     * 提交辅料采购合同
     * @param purchaseContractId 采购合同id
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     */
    void submitAuxiliaryTask(Long purchaseContractId, Long userId, String processDefinitionKey);

    /**
     * 更新采购合同订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    void updateThumbnail();

    /**
     * 变更采购合同编号
     */
    void updateCode(Long id,String code);

    /**
     * 验证供应商是否存在
     * @param venderCode 供应商编号
     * @return 是否存在
     */
    boolean validateVenderExists(String venderCode);

    /**
     * 获取采购合同付款计划
     * @param req 请求体
     * @return 采购合同付款计划
     */
    void updatePaymentPlan(UpdatePaymentPlanReq req);

    List<PurchaseContractItemDO> getItemMapByItemIds(List<Long> purchaseItemIds);

    /**
     * 获取采购合同单价缓存
     * @param purchaseCodeList 采购合同列表
     * @return 采购合同单价缓存
     */
    Map<Long,JsonAmount> getiPurchasePricCache(Collection<String> purchaseCodeList);

    /**
     * 采购合同回滚
     * @param updateMap 回滚数据
     */
    void rollBackPurchaseNoticeQuantity(Map<String, Map<String, Integer>> updateMap);
}
