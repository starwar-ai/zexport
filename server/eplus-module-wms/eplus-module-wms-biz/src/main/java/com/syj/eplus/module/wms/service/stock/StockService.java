package com.syj.eplus.module.wms.service.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.wms.api.stock.dto.QueryStockReqVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.*;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 仓储管理-库存明细 Service 接口
 *
 * @author ePlus
 */
public interface StockService extends IService<StockDO> {

    /**
     * 创建仓储管理-库存明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStock(@Valid StockSaveReqVO createReqVO);

    /**
     * 更新仓储管理-库存明细
     *
     * @param updateReqVO 更新信息
     */
    void updateStock(@Valid StockSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-库存明细
     *
     * @param id 编号
     */
    void deleteStock(Long id);

    boolean deleteStockByContractId(Long purchaseContractId);

    /**
     * 获得仓储管理-库存明细
     *
     * @param skuId sku编号
     * @return 仓储管理-库存明细
     */
    StockRespVO getStock(Long skuId);

    /**
     * 获得仓储管理-库存明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-库存明细分页
     */
    PageResult<StockRespVO> getStockPage(StockPageReqVO pageReqVO);

    /**
     * 获得仓储管理-库存明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-库存明细分页
     */
    PageResult<StockRespVO> listPage(StockPageReqVO pageReqVO);

    /**
     * 条件查询批次库存信息(盘点)
     *
     * @param stockQueryVO
     * @return
     */
    PageResult<StockDetailRespVO> queryBatch(StockQueryVO stockQueryVO);

    /**
     * 根据产品编码查询库存信息
     *
     * @param stockForSaleQueryVO
     * @return
     */
    List<StockDetailRespVO> listBatch(QueryStockReqVO stockForSaleQueryVO);

    /**
     * 根据批次号批量获取入库单
     *
     * @param batchCodeList 批次号列表
     * @return 入库单缓存
     */
    Map<String, StockDO> getStockListByBatchCode(List<String> batchCodeList);

    /**
     * 汇总产品可用库存
     * @param queryStockReqVOList
     * @return
     */
    Map<String,Integer> queryTotalStock(List<QueryStockReqVO> queryStockReqVOList);

    PageResult<StockRespVO> listPageBySku(StockPageReqVO pageReqVO);

    /**
     * 删除采购合同编号对应的库存
     * @param purchaseContractCode 采购合同编号
     */
    void deleteByPurchaseContractCode(String purchaseContractCode);

    void deleteStockByPurchaseContractCode(List<String> purchaseContractCode);

    void rollBackLockQuantity(Long id);

    /**
     * 获取采购合同未出库库存
     * @param contractCode 采购合同编号
     * @param skuCodeList 产品编号
     * @return 未出库库存
     */
    Map<String,Integer> getNotOutStockByPurchaseContractCode(String contractCode,List<String> skuCodeList);

    /**
     * 计算剩余总金额
     * @param stockDOList 库存列表
     * @return 库存列表
     */
    List<StockDO> calculateRemainTotalAmount(List<StockDO> stockDOList);

    /**
     * 根据采购合同编号获取库存
     * @param purchaseContractCode 采购合同号
     * @param skuCodes 产品编号
     * @return 库存明细
     */
    List<StockDO> getStockDOByPurchaseContractCode(String purchaseContractCode, Set<String> skuCodes);

    void importStock(String importCode);

    PageResult<SimpleStockResp> getSimpleStock(StockPageReqVO pageReqVO);

    List<StockDO> updateTotalAmount(List<StockDO> stockList);
}
