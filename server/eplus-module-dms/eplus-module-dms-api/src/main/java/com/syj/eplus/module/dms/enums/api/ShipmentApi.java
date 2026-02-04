package com.syj.eplus.module.dms.enums.api;

import com.syj.eplus.framework.common.entity.ManufactureSkuReqVO;
import com.syj.eplus.framework.common.entity.ShipmentPriceEntity;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.enums.api.dto.DeclarationItemDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.dms.enums.api.dto.SimpleShipmentItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/8/30 17:37
 */
public interface ShipmentApi {

    /**
     * 根据客户编号获取未完成的出运明细列表
     *
     * @param custCode 客户编号
     * @return 出运明细列表
     */
    List<ShipmentDTO> getUnShippedDTOByCustCode(String custCode);

    /**
     * 根据供应商编号获取未完成的出运明细列表
     *
     * @param venderCode 供应商编号
     * @return 出运明细列表
     */
    List<ShipmentDTO> getUnShippedDTOByVenderCode(String venderCode);

    /**
     * 根据sku编号获取未完成的出运明细列表
     *
     * @param skuCode sku编号
     * @return 出运明细列表
     */
    List<ShipmentDTO> getUnShippedDTOBySkuCode(String skuCode);

    /**
     * 更新出运明细的确认状态
     *
     * @param confirmFlag 确认状态
     * @param id 出运明细id
     * @return
     */
    void updateConfirmFlag(Integer confirmFlag, Long id);
    void updateConfirmFlag(Integer confirmFlag, String code);

    /**
     * 供应商变更后更新后续未完成的出运明细的供应商id,name
     * @param venderId
     * @param venderName
     * @param venderCode
     */
    void updateVender(Long venderId, String venderName, String venderCode);

    /**
     * 客户变更后更新后续未完成的出运明细的客户id,name
     * @param custId
     * @param custName
     * @param custCode
     */
    void updateCust(Long custId, String custName, String custCode);

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

    Map<String,ShipmentDTO> getByShipmentCodeList(List<String> shipmentCodeList);

    ShipmentDTO getByShipmentCode(String shipmentCode);

    Map<Long, DeclarationItemDTO> getDeclarationListByShipmentId(List<Long> list);

    /**
     * 根据销售合同号获取未完成的出运明细列表
     *
     * @param saleContractCode 销售合同号
     * @return 出运明细列表
     */
    List<ShipmentDTO> getUnShippedDTOBySaleContractCode(String saleContractCode);

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
     * 更新出运明细入库状态
     * @param saleItemIdList
     * @param saleItemBillStatus
     * @return
     */
    Boolean updateSaleItemBillStatus(List<Long> saleItemIdList,Integer saleItemBillStatus);


    /**
     * 更新出运明细验货状态
     * @param updateMap 验货状态缓存
     */
    void updateCheckStatus(Map<Long, Map<String, Integer>> updateMap);

    /**
     * 通过销售合同号获取出运对应的应收金额
     * @param saleContractCodeList 销售合同编号
     * @return 出运对应的应收金额
     */
    Map<String,List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList);

    List<ShipmentItemDTO> getShipmentItemDTOListByShipmentId (Long shipmentId);

    List<ShipmentItemDTO> getShipmentItemDTOListByIdList (List<Long> shipmentItemIds);


    void updateShipmentItem(List<ShipmentItemDTO> shipmentItemDTOList,boolean changeInvoiceConvertFlag);

    /**
     * 回写出运明细采购合同编号
     * @param saleContractItemIds 销售明细id列表
     * @param purchaseCode 采购合同编号
     * @param simpleData 供应商精简信息
     */
    void rollbackPurchaseCode(List<Long> saleContractItemIds, String purchaseCode, SimpleData simpleData);

    /**
     * 批量加工产品
     * @param manufactureSkuReqVOList 加工产品请求参数
     */
    void batchManufactureSku(List<ManufactureSkuReqVO> manufactureSkuReqVOList);

    Map<String, ShipmentDTO> getShipmentByForwarderFeeCodes(List<String> relationCodes);

    Map<Long, SimpleShipmentItemDTO> getSimpleShipmentItemDTOMap(List<String> saleContractCodeList);

    /**
     * 删除结汇单出运明细
     * @param shipmentItemIdList 出运明细子表id
     */
    void rollbackShipmentByDeletedSettlement(List<Long> shipmentItemIdList);

    /**
     * 根据采购合同号批量获取预计开船时间
     * @param shipmentItemIdList 销售明细id列表
     * @return 预计开船时间
     */
    Map<Long,LocalDateTime> getEstDepartureTimeByPurchaseCodeList(List<Long> shipmentItemIdList);

    /**
     * 验证是否转报关单
     * @param shipmentCodeList 出运单号列表
     */
    void validateDeclarationFlag(List<String> shipmentCodeList);

    /**
     * 根据出运单号批量获取出运单信息
     * @param shipmentCodeList 出运单号列表
     * @return 出运单信息
     */
    List<ShipmentDTO> getShipmentListByCode(List<String> shipmentCodeList);
}
