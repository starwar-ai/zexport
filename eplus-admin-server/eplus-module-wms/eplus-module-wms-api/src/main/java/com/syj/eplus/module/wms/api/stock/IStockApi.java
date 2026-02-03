package com.syj.eplus.module.wms.api.stock;

import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.wms.api.stock.dto.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 库存 API
 */
public interface IStockApi {

    /**
     * 采购合同下单-入库单&库存处理
     *
     * @param billSaveReqVO
     * @return
     */
    Boolean afterCreatePurchaseOrder(BillSaveReqVO billSaveReqVO);

    /**
     * 拒绝/结案采购合同-入库单&库存处理
     * @param purchaseContractId
     * @return
     */
    Boolean afterCanclePurchaseOrder(Long purchaseContractId);

    /**
     * 批量锁定库存
     *
     * @param stockLockSaveReqVOList
     * @return
     */
    Boolean batchLockStock(List<StockLockSaveReqVO> stockLockSaveReqVOList);

    /**
     * 释放锁定数量
     *
     * @param sourceOrderCode 原单据单号
     * @param sourceOrderType 原单据类型 1-销售合同 2-加工单 3-调拨单 4-采购计划
     * @return
     */
    Boolean cancelStockLock(String saleContractCode, Collection<Long> itemIdList, Integer sourceOrderType);

    /**
     * 采购合同-生产完成（供应商在制库存转入库库存）
     * @param purchaseContractId
     * @return
     */
    Boolean completePurchaseOrder(Long purchaseContractId,Map<String,Integer> usedQuantityMap,Boolean domesticSaleFlag,List<String> batchCodeList);

    Map<Long, List<StockDTO>> getStockBySkuIdList(List<Long> skuIdList);

    /**
     * 根据产品编号获取库存明细
     *
     * @return 库存明细
     */
    List<StockDTO> getStockListBySkuCode(List<String> skuCodeList);

    /**
     * 查询明细库存
     * @param queryStockReqVO
     * @return
     */
    List<StockDetailRespVO> listLockStock(QueryStockReqVO queryStockReqVO);

    /**
     * 根据外销合同编码查询锁定库存
     * @param queryStockReqVO
     * @return
     */
    List<StockLockRespVO> listStockLock(QueryStockReqVO queryStockReqVO);

    /**
     * 根据销售合同编号列表获取库存明细列表
     * @param saleContractCodes 销售合同编号列表
     * @return 库存明细列表
     */
    List<StockLockRespVO> getStockLockRespVOBySaleContractCodes(List<String> saleContractCodes);

    /**
     * 批量查询可用库存
     * @param queryStockReqVOList
     * @return
     */
    Map<String, Integer> queryTotalStock(List<QueryStockReqVO> queryStockReqVOList);

    /**
     * 根据销售合同编号列表获取库存明细列表
     * @param saleContractCodes 销售合同编号列表
     * @return 库存明细列表
     */
    List<StockDTO> getStockDTOBySaleContractCodes(List<String> saleContractCodes);

    /**
     * 根据销售合同编号列表获取库存明细列表
     * @param saleContractCodes 销售合同编号列表
     * @return 库存明细列表
     */
    List<StockDTO> getStockDTOBySaleContractCodesAndLastCompanyIds(List<String> saleContractCodes,List<Long> lastCompanyIds);

    /**
     * 批量获取库存明细
     * @param idList id列表
     * @return 库存明细
     */
    List<StockDTO> getStockDTOByIdList(List<Long> idList);

    /**
     * 加工单出入库及库存逻辑-子产品出库，主产品入库
     * @param childBillSaveReqVO
     * @param billSaveReqVO
     * @return
     */
    List<String> handleManufactureBillAndStock(BillSaveReqVO childBillSaveReqVO,BillSaveReqVO billSaveReqVO);

    /**
     * 根据销售合同编号列表校验锁定库存
     * @param saleContractCodeList 销售合同编号列表
     * @return 未使用库存销售编号
     */
    Map<String,Map<String,Integer>> validateStockByContractCodeList(List<String> saleContractCodeList);


    /**
     * 根据销售明细主键列表获取库存价格
     * @param ids 产品id列表
     * @return 产品价格
     */
    Map<Long, PriceQuantityEntity> getStockPriceMap(List<Long> ids);

    /**
     * 删除采购合同编号对应的库存
     * @param purchaseContractCode 采购合同编号
     */
    void deleteStockByPurchaseContractCode(List<String> purchaseContractCode);
    /**
     * 根据销售合同编号查询锁库数量的map
     * Long为销售合同明细id
     * @param code
     * @return
     */
    Map<Long, Integer> getLockCountBySaleContractCode(String code);

    Map<Long,List<StockLockRespVO>> getStockLockMapBySaleCode(String code);

    List<StockLockRespVO> getStockLockListBySaleCode(String code);

    /**
     * 删除采购合同的库存
     * @param code
     */
    void deleteStockByPurchaseContractCode(String code);

    /**
     * 采购计划回滚销售合同的锁库信息
     * @param id  销售合同id
     */
    void rollBackLockQuantity(Long id);


    /**
     * 获取采购合同未出库库存
     * @param contractCode 采购合同编号
     * @param skuCodeList 产品编号
     * @return 未出库库存
     */
    Map<String,Integer> getNotOutStockByPurchaseContractCode(String contractCode,List<String> skuCodeList);

    /**
     * 根据批次号列表获取库存明细列表
     * @param batchCodeList 批次号编号列表
     * @return 库存明细列表
     */
    List<StockDTO> getStockDTOByBatchCodes(Set<String> batchCodeList);

    /**
     * 更新采购合同明细库存
     * @param purchaseContractCode 采购合同编号
     * @param skuQuantityMap 明细数量缓存
     */
    boolean updateStockByPurchaseContractCode(String purchaseContractCode, Map<String,BaseSkuEntity> skuQuantityMap);

    /**
     * 验证库存
     * @param purchaseContractCode 采购合同编号
     * @param skuQuantityMap 明细数量缓存
     */
    void validateStock(String purchaseContractCode, Map<String, BaseSkuEntity> skuQuantityMap);
    /**
     * 获得销售合同库存成本
     * @param saleContractCodes 销售合同编号
     * @return 库存成本
     */
    Map<String,Map<String, JsonAmount>> calcStockCost(List<String> saleContractCodes);

    /**
     * 根据批次号获取库存价格
     * @param batchCodeList 批次号
     * @return 库存价格
     */
    Map<String,JsonAmount> getStockPrice(List<String> batchCodeList);

    /**
     * 更新库存中拉柜数量
     * @param updateQunatityMap 拉柜数量缓存
     */
    void updateStockCabinetQuantity(Map<String,Map<String,Integer>> updateQunatityMap);

    Map<String,Integer> getNoticedQuantityMap(String purchaseContractCode);

    /**
     * 通过销售合同号获取出库对应的应收金额
     * @param saleContractCodeList 销售合同编号
     * @return 出库对应的应收金额
     */
    Map<String,List<ShipmentPriceEntity>> getStockPriceBySaleContractCode(List<String> saleContractCodeList);

    /**
     * 通过销售明细获取出库日期
     * @param saleItemUniqueCodeList 销售明细编号
     * @return 出库日期
     */
    Map<String, LocalDateTime> getOutStockTimeBySaleItems(List<String> saleItemUniqueCodeList);

    /**
     * 根据销售合同编号列表获取库存明细Map
     * 先查询库存锁定信息，再根据批次号查询库存，最后按销售合同明细ID分组
     * @param saleContractCodeList 销售合同编号列表
     * @return 以销售合同明细ID为key，库存明细列表为value的Map
     */
    Map<Long, List<StockDTO>> getStockDTOMapBySaleContractCodes(List<String> saleContractCodeList);

    /**
     * 根据采购合同号列表查询库存明细
     * @param purchaseContractCodes 采购合同编号列表
     * @return 库存明细列表
     */
    List<StockDTO> getStockDTOByPurchaseContractCodes(List<String> purchaseContractCodes);
}
