package com.syj.eplus.module.scm.service.purchaseplanitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 采购计划明细 Service 接口
 *
 * @author zhangcm
 */
public interface PurchasePlanItemService extends IService<PurchasePlanItemDO> {

    /**
     * 创建采购计划明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchasePlanItem(@Valid PurchasePlanItemSaveReqVO createReqVO);

    /**
     * 更新采购计划明细
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchasePlanItem(@Valid PurchasePlanItemSaveReqVO updateReqVO);

    /**
     * 删除采购计划明细
     *
     * @param id 编号
     */
    void deletePurchasePlanItem(Long id);

    /**
     * 获得采购计划明细
     *
     * @param purchasePlanItemDetailReq 明细请求实体
     * @return 采购计划明细
     */
    PurchasePlanItemRespVO getPurchasePlanItem(PurchasePlanItemDetailReq purchasePlanItemDetailReq);

    /**
     * 获得采购计划明细分页
     *
     * @param pageReqVO 分页查询
     * @return 采购计划明细分页
     */
    PageResult<PurchasePlanItemDO> getPurchasePlanItemPage(PurchasePlanItemPageReqVO pageReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

    /**
     * 获取自营产品流程标识
     *
     * @return 流程标识
     */
    String getOwnBrandProcessDefinitionKey();

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
    void submitTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    void batchSavePlanItem(List<PurchasePlanItemSaveReqVO> itemList);

    List<PurchasePlanItemDO> getPurchasePlanItemListByPurchasePlanId(Long id);

    void deletePurchasePlanItemListByPurchasePlanId(Long id);

    List<PurchasePlanItemRespVO> getPurchasePlanItemListByPurchasePlanIdList(List<Long> idList);

    List<PurchasePlanItemRespVO> getPurchasePlanItemListByIdList(List<Long> idList);

    List<PurchasePlanItemRespVO> getNoConvertPurchasePlanItemListByIdList(List<Long> idList);

    List<String> getItemCodeListByPurchasePlanId(Long id);

    /**
     * 更新采购计划明细转换状态
     * @param itemIdSet 明细id列表
     */
    Integer updatePurchasePlanItemConvertedFlag(Set<Long> itemIdSet,Long purchasePlanId);

    /**
     * 根据采购合同编号获取对应包材所有附件
     * @param purchaseContractCode 采购合同号
     * @return 附件列表
     */
    List<SimpleFile> getAuxiliaryAnnexByContractCode(String purchaseContractCode);
}
