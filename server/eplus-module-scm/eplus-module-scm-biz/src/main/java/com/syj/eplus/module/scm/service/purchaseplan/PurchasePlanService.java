package com.syj.eplus.module.scm.service.purchaseplan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.*;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.entity.UpdateItemEntity;
import com.syj.eplus.module.sms.api.dto.SimpleSaleItemDTO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采购计划 Service 接口
 *
 * @author zhangcm
 */
public interface PurchasePlanService extends IService<PurchasePlanDO> {

    /**
     * 创建采购计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createPurchasePlan(@Valid PurchasePlanInfoSaveReqVO createReqVO);

    /**
     * 更新采购计划
     *
     * @param updateReqVO 更新信息
     */
    Set<Long> updatePurchasePlan(@Valid PurchasePlanInfoSaveReqVO updateReqVO);

    /**
     * 删除采购计划
     *
     * @param id 编号
     */
    void deletePurchasePlan(Long id);

    /**
     * 获得采购计划
     *
     * @param purchasePlanDetailReq 明细请求实体
     * @return 采购计划
     */
    PurchasePlanInfoRespVO getPurchasePlan(PurchasePlanDetailReq purchasePlanDetailReq);

    /**
     * 获取组套件产品拆分明细
     * @param planId
     * @param planItemSkuCode
     * @param demergeQuantity
     * @return
     */
    List<PurchasePlanItemToContractItemRespVO> getPurchaseMixPlanItem(Long planId, Long purchaseCompanyId, String planItemSkuCode, Integer demergeQuantity);

    PurchasePlanInfoRespVO getPurchasePlanContainsContract(PurchasePlanDetailReq purchasePlanDetailReq);

    /**
     * 获得采购计划分页
     *
     * @param pageReqVO 分页查询
     * @return 采购计划分页
     */
    PageResult<PurchasePlanInfoRespVO> getPurchasePlanPage(PurchasePlanPageReqVO pageReqVO);

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
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId, String processDefinitionKey, Map<String,Object> variable);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitAuxiliaryTask(Long id, Long userId, String processDefinitionKey);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    void finishPurchasePlan(List<Long> planIdList,boolean validateFlag);

    /**
     * 采购计划明细-批量转采购合同
     * @param planItemIdList
     */
    void purchasePlanItemToContract(List<Long> planItemIdList);

    /**
     * 采购计划-转采购合同
     * @param reqVOList
     */
    List<CreatedResponse> purchasePlanToContract(PurchasePlanItemToContractSaveInfoReqVO reqVOList);

    /**
     * 生成尾部节点采购合同
     * @param reqVOList
     */
    List<CreatedResponse> generateLastNodePurchaseContract(PurchasePlanItemToContractSaveInfoReqVO reqVOList,Map<Long, SimpleSaleItemDTO> saleItemQuantityMap,boolean combineFlag);
    List<CreatedResponse> toSaveContract(PurchasePlanItemToContractSaveInfoReqVO reqVOList);

    PageResult<PurchasePlanItemInfoRespVO> getPurchasePlanBoardPage(PurchasePlanPageReqVO pageReqVO);


    List<PurchasePlanItemAndPlanRespVO> getPurchasePlanItem(Long purchasePlanId);

    List<PurchasePlanItemToContractRespVO> getPurchasePlanToContractList(List<Long> planItemIdList);

    /**
     * 判断采购计划是否包含自营产品
     * @param id 采购计划主键
     * @return 是否包含自营产品
     */
    boolean checkOwnBrandFlag(Long id);

    /**
     * 获取条件key
     * @return key
     */
    String getConditionKey();

    /**
     * 根据采购计划id获取关联订单数量
     * @param id 采购计划id
     * @return 关联订单数量
     */
    RelatedOrderRespVO getRelatedOrderNum(Long id);

    /**
     * 根据销售合同id获取采购计划数量
     * @param contractId 销售合同id
     * @return 采购计划数量
     */
    Long getOrderNumBySaleContractId(Long contractId);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    List<PurchasePlanItemDO> getItemListBySaleContractId(Long id);

    /**
     * 批量更新采购计划状态
     * @param planIdList 采购计划id列表
     * @param planStatus 采购计划状态
     */
    void batchUpdatePlanStatus(List<Long> planIdList, Integer planStatus);

    /**
     * 校验销售明细是否存在对应采购明细
     *
     * @param saleContractItemIds 销售合同明细id列表
     * @return 是否存在对应采购明细
     */
    boolean validatePurContractItemExists(List<Long> saleContractItemIds);

    /**
     * 生成尾部节点与加工型企业的销售合同
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    List<CreatedResponse> handleLastToProducedContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap);

    /**
     * 若锁定库存中存在加工型企业，生成锁定库存的购销合同
     *
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    List<CreatedResponse>  handleProducedLockContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap);

    /**
     * 转换尾部节点向供应商采购合同，存在内部供应商时进行转换
     * 根据供应商、采购主体、采购员进行分组生成合同
     *
     * @param reqVO
     * @param lastNodeSkuPriceMap
     */
    List<CreatedResponse> lastCompanyToVenderContract(PurchasePlanItemToContractSaveInfoReqVO reqVO, Map<String, JsonAmount> lastNodeSkuPriceMap,Boolean internalFlag,Map<Long, SimpleSaleItemDTO> saleItemQuantityMap);

    /**
     * 更新采购合同明细转换标识
     * @param updateItemEntities 更新条件
     */
    void updatePlanItemConvertFlag(List<UpdateItemEntity> updateItemEntities);

    /**
     * 作废采购计划明细
     * @param saleItemIdList 销售明细id
     */

    void cancelPurchasePlanItem(List<Long> saleItemIdList, Collection<Long> saleItemIds);

    /**
     * 校验销售明细是否存在拆分采购
     *
     * @param saleContractItemIds 销售合同明细id列表
     * @return 是否存在对应采购明细
     */
    boolean validatePurContractItemIsSplit(Set<Long> saleContractItemIds);

    //根据销售合同编号查询计划编号
    String getPlanCodeBySaleContractCode(String saleContractCode);

    Long getPlanIdBySaleItemId(Long saleContractItemId);

    List<PurchasePlanDO> getPlanListByIds(List<Long> planIdList);

    /**
     * 批量更新拆分采购计划状态(仅采购合同作废)
     * @param planIds 采购计划id列表
     */
    void rollbackSplitPurchasePlan(List<Long> planIds);

    /**
     * 批量更新组合采购计划状态(仅采购合同作废)
     * @param updateMap
     */
    void updateBatchPlanList(Map<Long,Map<String,Integer>> updateMap,List<Long> saleContractItemIdList);

    /**
     * 获取采购计划关联的用户和部门
     * @param auxiliaryPurchaseContractCodeSet 采购合同编号集合
     * @param auxiliarySaleContractCodeSet 销售合同编号集合
     * @return
     */
    Map<String,List<UserDept>> getPurchaseUserAndSales(Set<String> auxiliaryPurchaseContractCodeSet, Set<String> auxiliarySaleContractCodeSet);

    /**
     * 根据销售明细id列表获取采购明细作废标识
     * @param saleItemIds 销售明细id列表
     */
    Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds);

    /**
     * 更新销售合同订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    /**
     * 根据销售明细id列表获取采购计划模型
     * @param saleItems 销售明细id列表
     */
    Map<Long,Integer> getPurchaseModelBySaleItemIds(Collection<Long> saleItems);
}
