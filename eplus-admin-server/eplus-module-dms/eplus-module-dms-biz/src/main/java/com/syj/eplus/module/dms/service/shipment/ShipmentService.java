package com.syj.eplus.module.dms.service.shipment;

import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.ContainerMidVO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.ManufactureSkuReqVO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeShareFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.*;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentPageReq;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentSaveReq;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticePageReqDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeRespDTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 出运单 Service 接口
 *
 * @author du
 */
public interface ShipmentService {

    /**
     * 创建出运单
     *
     * @param createReqVO 创建信息
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createShipment(@Valid ShipmentSaveReqVO createReqVO);

    /**
     *
     * 更新出运单
     *
     * @param updateReqVO 更新信息
     */
    void updateShipment(@Valid ShipmentSaveReqVO updateReqVO);

    /**
     * 删除出运单
     *
     * @param id 编号
     */
    void deleteShipment(Long id);

    /**
     * 获得出运单
     *
     * @param id 编号
     * @return 出运单
     */
    ShipmentRespVO getShipment(Long id);

    /**
     * 获得出运单分页
     *
     * @param pageReqVO 分页查询
     * @return 出运单分页
     */
    PageResult<ShipmentRespVO> getShipmentPage(ShipmentPageReqVO pageReqVO);

    /**
     * 客户编号
     * @param custCode
     * @return
     */
    List<Long> getShipmentIdsByCustCode(String custCode);

    /**
     * 编号
     * @param venderCode
     * @return
     */
    List<Long> getShipmentIdsByVenderCode(String venderCode);

    /**
     * 编号
     * @param skuCode
     * @return
     */
    List<Long> getShipmentIdsBySkuCode(String skuCode);


    /**
     * 批量转出运明细
     *
     * @param ids 出运计划明细id列表
     */
    List<CreatedResponse> transformShipment(List<Long> ids);

    /**
     * 批量转商检单
     *
     * @param reqVO 出运单明细id List
     */
    List<CreatedResponse> toCommodityInspection(ToInspectionReqVO reqVO);


    /**
     * 批量报关单
     *
     * @param toDeclarationReqVO 出运单明细 List
     */
    List<CreatedResponse> toDeclaration(ToDeclarationReqVO toDeclarationReqVO);

    /**
     * 批量转结汇单
     *
     * @param toSettlementFormReqVO 转结汇单类型，ids 明细id List
     */
    List<CreatedResponse> toSettlementForm(ToSettlementFormReqVO toSettlementFormReqVO);

    /**
     * 出运明细结案
     *
     * @param closeShipmentReq 请求
     */
    void closeShipment(CloseShipmentReq closeShipmentReq);

    /**
     * 出运明细结案
     *
     * @param closeShipmentReq 请求
     */
    void rollbackClose(CloseShipmentReq closeShipmentReq);


    /**
     * 出运明细出运
     *
     * @param shipmentReq 出运明细Id
     */
    void shipment(ShipmentReq shipmentReq);

    /**
     * 交单
     *
     * @param id 出运明细Id
     */
    void finishShipment(Long id);

    /**
     * 转开票通知
     *
     * @param reqList 出运单明细主键列表
     */
    List<CreatedResponse> transformInvoicingNotices(List<ShipmentToInvoiceSaveReq> reqList);

    /**
     * 变更出运明细
     *
     * @param changeReq 请求
     */
    List<CreatedResponse> changeShipment(ChangeShipmentSaveReq changeReq, boolean businessFlag);

    /**
     * 提交变更任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    String submitChangeTask(Long id, Long userId, String processDefinitionKey, Map<String, Object> variables);

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

    String getProcessDefinitionKeyByBusinessId(Long id);


    PageResult<ChangeShipmentRespVO> getChangePage(ChangeShipmentPageReq pageReq);

    ChangeShipmentRespVO getShipmentChange(Long id);

    ChangeShipmentRespVO getAuditShipmentChange(String id);

    /**
     * 更新确认状态
     *
     * @param confirmFlag 确认状态
     */
    void updateConfirmFlag(Integer confirmFlag, Long id);
    void updateConfirmFlag(Integer confirmFlag, String code);
    void updateVender(Long venderId, String venderName, String venderCode);
    void updateCust(Long custId, String custName, String custCode);

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
     * 删除变更任务
     *
     * @param id 变更主键
     */
    void deleteShipmentChange(Long id);

    /**
     * 获取影响出运单变更来源信息
     *
     * @param id 出运单主键
     * @return 变更来源信息
     */
    List<ConfirmSourceEntity> getConfirmSourceListByShipmentId(Long id);

    /**
     * 根据销售合同id获取出运明细数量
     * @param code 销售合同编号
     * @return 出运明细数量
     */
    Long getOrderNumBySaleContractCode(String code);

    /**
     * 根据销售合同id获取出运计划数量
     * @param code 销售合同编号
     * @return 出运计划数量
     */
    Long getPlanNumBySaleContractCode(String code);

    /**
     * 根据id获取出运明细关联单据数量
     * @param id 出运明细id
     * @return 关联单据数量
     */
    RelatedShipmentRespVO getRelatedNum(Long id);


    /**
     * 获取拉柜通知单中间页
     * @param ids 出运明细主键列表
     * @return 拉柜通知单中间页
     */
    ContainerMidVO getContainerMid(List<Long> ids,Integer shippedAddress);

    /**
     * 获得拉柜通知单分页
     * @param pageReqDTO 请求体
     * @return 拉柜通知单分页
     */
    PageResult<StockNoticeRespDTO> getNoticePage(StockNoticePageReqDTO pageReqDTO);

    /**
     * 创建拉柜通知单
     * @param containerMidVO 请求体
     * @return  结果
     */
    List<CreatedResponse> createNotice(ContainerMidVO containerMidVO);

    boolean updateShipmentForwarderFee(ShipmentForwarderFeeSaveReqVO updateReqVO);

    List<ForwarderFeeShareFeeRespVO> getListByCodeList(String codeList);

    /**
     * 拆分出运单明细
     * @param ids 出运单明细id列表
     */
    List<ShipmentItem> splitShipmentItem(List<Long> ids);

    /**
     * 批量更新出运单明细
     * @param shipmentItemList 出运单明细列表
     */
    void batchUpdateShipmentItem(List<ShipmentItem> shipmentItemList);

    /**
     * 查询采购计划需要加工组套件产品
     * @param purchaseContractCode
     * @return
     */
    PurchasePlanInfoDTO listManufactureSku(String purchaseContractCode,Long saleContractItemId);

    /**
     * 加工组套件产品
     * @param skuCode
     * @param processingNum
     * @return
     */
    List<String> manufactureSku(String skuCode, Integer processingNum,Long saleContractItemId);

    /**
     * 更新出运单明细开票状态
     * @param uniqueCodeList 唯一编号标识
     * @param status 状态
     */
    void updateShipmentItemInvoiceStatus(List<String> uniqueCodeList,Integer status);

    /**
     * 更新出运单明细已开票数量
     * @param quantityMap 开票数量map
     */
    void updateShipmentIItemInvoiceQuantity(Map<Long, BigDecimal> quantityMap);

    List<String> batchManufactureSku(List<ManufactureSkuReqVO> manufactureSkuReqVOList);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    Map<String,ShipmentDTO> getByShipmentCodeList(List<String> shipmentCodeList);

    ShipmentDTO getByShipmentCode(String shipmentCode);

    /**
     * 客户编号
     * @param saleContractCode
     * @return
     */
    List<Long> getShipmentIdsBySaleContractCode(String saleContractCode);

    /**
     * 更新出运明细的出库数量
     * @param shipmentCode 出运单号
     * @param skuQuantityMap sku数量map
     * @param outDate 出库日期
     */
    void updateOutQuantity(String shipmentCode, Map<Long,Integer> skuQuantityMap, LocalDateTime outDate);

    /**
     * 更新出运明细的转拉柜标识
     * @param uniqueCodeList 唯一编号列表
     */
    void cancelConvertContainerFlag(List<String> uniqueCodeList);

    /**
     * 更新销售合同真实采购价
     * @param saleContractCode 销售合同编号
     * @param manager 跟单员
     */
    void updateRealPurchasePrice(String saleContractCode, UserDept manager);

    /**
     * 根据销售合同明细id获取出运明细的作废标识
     * @param itemIdList 销售合同明细id
     * @return 作废标识缓存
     */
    Map<Long, List<Integer>> getShipmentContractItemCancelFlag(List<Long> itemIdList);


    /**
     * 作废储运计划明细
     * @param itemIdList 销售明细id列表
     */
    void cancelShipmentPlanItem(List<Long> itemIdList);

    /**
     * 获取影响出运单号
     * @param saleContractCode 销售合同编号
     * @param itemIdList 销售明细id列表
     * @return 出运单号
     */
    List<String> getEffectShipmentCode(String saleContractCode, List<Long> itemIdList);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @param custCode 客户编号
     * @return
     */
    void exportExcel(Long id, String exportType, String reportCode,String custCode,HttpServletResponse response);

    void appendShipment(AppendShipmentReq req);

    List<CreatedResponse> batrchTansformShipment(List<Long> ids);

    /**
     * 自动生成内部单据
     * @param shipmentId 出运明细主键
     */
    void genInternalContract(Long shipmentId);

    /**
     * 删除内部合同
     * @param shipmentId 出运明细主键
     */
    void deleteGenContract(Long shipmentId);

    /**
     * 验证是否转报关单
     * @param shipmentCodeList 出运单号列表
     */
    void validateDeclarationFlag(List<String> shipmentCodeList);

    /**
     * 验证是否转开票通知
     * @param shipmentCodeList 出运单号列表
     */
    void validateInvoiceNoticeFlag(List<String> shipmentCodeList);
    /**
     * 根据出运单号批量获取出运单信息
     * @param shipmentCodeList 出运单号列表
     * @return 出运单信息
     */
    List<ShipmentDTO> getShipmentListByCode(List<String> shipmentCodeList);

    /**
     * 拆分出运明细
     * @param itemList 出运子项
     * @return 创建结果
     */
    CreatedResponse splitShipment(List<ShipmentItem> itemList);

    /**
     * 分批出运
     * @param shipmentId 出运明细id
     */
    void batchFlag(Long shipmentId);

    /**
     * 取消分批出运
     * @param shipmentId 出运明细id
     */
    void cancelBatchFlag(Long shipmentId);

    /**
     * 通过流程实例id获取变更表中单证员
     * @param instanceId 流程实例id
     * @return 单证员
     */
    UserDept getDocumenterByInstanceIdInChange(String instanceId);

    void updateThumbnail();

    /**
     * 删除出运明细
     * @param ids 出运明细id列表
     */
    void deleteItem(List<Long> ids,boolean isRollBackSaleFlag);

    /**
     * 合并出运明细
     */
    void mergeItem(List<Long> ids, Long targetId);
}
