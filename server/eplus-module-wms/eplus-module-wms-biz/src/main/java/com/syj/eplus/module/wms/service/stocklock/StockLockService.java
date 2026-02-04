package com.syj.eplus.module.wms.service.stocklock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.PriceQuantityEntity;
import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 仓储管理-库存明细-锁定库存 Service 接口
 *
 * @author ePlus
 */
public interface StockLockService extends IService<StockLockDO> {

    /**
     * 创建仓储管理-库存明细-锁定库存
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStockLock(@Valid StockLockSaveReqVO createReqVO);

    /**
     * 删除仓储管理-库存明细-锁定库存
     *
     * @param id 编号
     */
    void deleteStockLock(Long id);

    /**
     * 获得仓储管理-库存明细-锁定库存
     *
     * @param id 编号
     * @return 仓储管理-库存明细-锁定库存
     */
    StockLockRespVO getStockLock(Long id);

    /**
     * 获得仓储管理-库存明细-锁定库存分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-库存明细-锁定库存分页
     */
    PageResult<StockLockDO> getStockLockPage(StockLockPageReqVO pageReqVO);

    /**
     * 释放锁定数量
     *
     * @param id
     * @return
     */
    Boolean cancel(Long id);

    /**
     * 创建仓储管理-库存明细-批量锁定库存
     *
     * @param batchCreateReqVO 锁定库存列表
     * @return 更新数量
     */
    Long batchCreateStockLock(List<StockLockSaveReqVO> batchCreateReqVO);


    List<StockDO> getStockListBySkuIdList(List<Long> skuIdList);
    List<StockDTO> getStockDTOListBySkuIdList(List<Long> skuIdList);

    /**
     * 根据产品编号获取库存明细
     *
     * @return 库存明细
     */
    List<StockDTO> getStockListBySkuCode(List<String> skuCodeList);

    boolean deleteByContractId(Long purchaseContractId);

    /**
     * 根据销售明细主键列表获取库存价格
     * @param ids 产品id列表
     * @return 产品价格
     */
    Map<Long, PriceQuantityEntity> getStockPriceMap(List<Long> ids);


    /**
     * 根据销售合同编号列表校验锁定库存
     * @param saleContractCodeList 销售合同编号列表
     * @return 未使用库存销售编号
     */
    Map<String,Map<String,Integer>> validateStockByContractCodeList(List<String> saleContractCodeList);

    Map<Long, Integer> getLockCountBySaleContractCode(String code);

    List<StockLockDO> getStockLockListBySaleCode(String code);

    void deleteBatchByIds(List<Long> ids);

    /**
     * 根据销售合同编号获取锁定库存数量
     * @param saleContractCodes 销售合同编号列表
     * @return 锁定库存数量
     */
    Map<String, Map<String, Integer>> getStockLockQuantityBySaleContractCodes(List<String> saleContractCodes);

    /**
     * 释放手动创建的出库通知单锁定数量
     * @param sourceOrderType 订单类型
     * @param sourceOrderCode 订单编号
     */
    Boolean cancelOutNoticeLockQuantity(Integer sourceOrderType,String sourceOrderCode);
}
