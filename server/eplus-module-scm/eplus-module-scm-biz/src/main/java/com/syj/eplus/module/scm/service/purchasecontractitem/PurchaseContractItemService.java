package com.syj.eplus.module.scm.service.purchasecontractitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryContractItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseBillSimpleDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采购合同明细 Service 接口
 *
 * @author zhangcm
 */
public interface PurchaseContractItemService extends IService<PurchaseContractItemDO> {

    /**
     * 创建采购合同明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseContractItem(@Valid PurchaseContractItemSaveReqVO createReqVO);

    /**
     * 更新采购合同明细
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseContractItem(@Valid PurchaseContractItemSaveReqVO updateReqVO);

    /**
     * 删除采购合同明细
     *
     * @param id 编号
     */
    void deletePurchaseContractItem(Long id);

    /**
     * 获得采购合同明细
     *
     * @param  purchaseContractItemDetailReq 明细请求实体
     * @return 采购合同明细
     */
    PurchaseContractItemRespVO getPurchaseContractItem(PurchaseContractItemDetailReq purchaseContractItemDetailReq);

    /**
     * 获得采购合同明细分页
     *
     * @param pageReqVO 分页查询
     * @return 采购合同明细分页
     */
    PageResult<PurchaseContractItemDO> getPurchaseContractItemPage(PurchaseContractGetItemPageReqDTO pageReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

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
     * @param id 业务id
     * @param userId    用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    void batchSavePlanItem(List<PurchaseContractItemSaveReqVO> itemList);

    List<PurchaseContractItemDO> getPurchaseContractItemListByPurchaseContractId(Long id);

    List<PurchaseContractItemDO> getPurchaseContractItemListByPurchaseContractId(List<Long> id);

    void deletePurchaseContractItemListByPurchaseContractId(Long id);

    List<PurchaseContractItemRespVO> getPurchaseContractItemListByPurchaseContractIdList(List<Long> parentIdList);

    List<PurchaseContractItemRespVO> getContractItemListByContractCodeList(List<String> parentCodeList);

    PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO reqDTO);

    Map<Long, PurchaseContractItemDO> getByIdList(List<Long> list);

    List<AuxiliaryContractItemDTO> getAuxiliaryContractItemListBySaleCode(String code);

    /**
     * 批量更新采购合同入库状态
     * @param billSimpleDTOList 入库列表
     */
    void batchUpdateBillStatus(List<PurchaseBillSimpleDTO> billSimpleDTOList);


    /**
     * 根据id集合获取采购合同明细
     * @param saleItemIds 销售明细id集合
     * @return 采购合同明细
     */
    List<PurchaseContractItemDO> getItemDOListByIds(Set<Long> saleItemIds);

    /**
     * 校验是否需要拆分销售明细
     * @param saleItemIds 销售明细id
     * @return 已存在采购合同的明细
     */
    List<Long> validateSplitSales(List<Long> saleItemIds);
}
