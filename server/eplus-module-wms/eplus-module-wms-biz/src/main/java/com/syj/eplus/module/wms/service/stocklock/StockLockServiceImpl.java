package com.syj.eplus.module.wms.service.stocklock;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PriceQuantityEntity;
import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockRespVO;
import com.syj.eplus.module.wms.convert.stock.StockConvert;
import com.syj.eplus.module.wms.convert.stocklock.StockLockConvert;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.syj.eplus.module.wms.dal.mysql.stock.StockMapper;
import com.syj.eplus.module.wms.dal.mysql.stocklock.StockLockMapper;
import com.syj.eplus.module.wms.enums.StockLockSourceTypeEnum;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.service.warehouse.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.OPERATION_FORBIDDEN;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓储管理-库存明细-锁定库存 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StockLockServiceImpl extends ServiceImpl<StockLockMapper, StockLockDO> implements StockLockService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StockLockMapper stockLockMapper;
    @Resource
    private StockService stockService;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private WarehouseService warehouseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockLock(StockLockSaveReqVO createReqVO) {
        StockLockDO stockLock = StockLockConvert.INSTANCE.convertStockLockDO(createReqVO);
        stockLock.setLockTime(LocalDateTime.now());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserRespDTO adminUserApiUser = adminUserApi.getUser(loginUserId);
        stockLock.setLockByUserId(loginUserId);
        stockLock.setLockByUserName(adminUserApiUser.getNickname());

        StockDO stockDO = stockService.getById(stockLock.getStockId());
        // 锁定数量
        Integer lockedQuantity = NumberUtil.add(stockDO.getLockQuantity(), stockLock.getLockQuantity()).intValue();
        // 可用数量=在制数量+入库数量-出库数量-锁定数量(弃)   修改：可用数量=在制数量+入库数量-出库数量(弃)
        // 修改：可用 = 入库-在制-出库
        Integer totalQuantity = NumberUtil.sub(stockDO.getInitQuantity(), stockDO.getProducingQuantity()).intValue();
        int availableQuantity = NumberUtil.sub(totalQuantity, stockDO.getUsedQuantity()).intValue();

        //锁定数量不能大于入库加可用
        if(lockedQuantity > stockDO.getProducingQuantity() + availableQuantity){
            throw exception(STOCK_LOCK_EXCEED);
        }
        //  1. 更新库存数量信息
        stockDO.setLockQuantity(lockedQuantity);
        stockDO.setAvailableQuantity(availableQuantity);
        stockService.updateById(stockDO);

        // 2. 插入占用数据
        stockLockMapper.insert(stockLock);
        // 返回
        return stockLock.getId();
    }

    @Override
    public void deleteStockLock(Long id) {
        // 校验存在
        validateStockLockExists(id);
        // 删除
        stockLockMapper.deleteById(id);
    }

    private void validateStockLockExists(Long id) {
        if (stockLockMapper.selectById(id) == null) {
            throw exception(STOCK_LOCK_NOT_EXISTS);
        }
    }

    @Override
    public StockLockRespVO getStockLock(Long id) {
        StockLockDO stockLockDO = stockLockMapper.selectById(id);
        if (stockLockDO == null) {
            return null;
        }
        return StockLockConvert.INSTANCE.convertStockLockRespVO(stockLockDO);
    }

    @Override
    public PageResult<StockLockDO> getStockLockPage(StockLockPageReqVO pageReqVO) {
        return stockLockMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(Long id) {
        StockLockDO stockLock = super.getById(id);

        StockDO stockDO = stockService.getById(stockLock.getStockId());

        Integer lockedQuantity = NumberUtil.sub(stockDO.getLockQuantity(), stockLock.getLockQuantity()).intValue();
        //  1. 更新库存数量信息
        stockDO.setLockQuantity(lockedQuantity);
        StockDO updateDO = new StockDO();
        updateDO.setId(stockDO.getId());
        updateDO.setLockQuantity(lockedQuantity);
        boolean stockFlag =  stockMapper.updateById(updateDO) > 0;
        // 2. 移除占用数据
        boolean stockLockFlag = super.removeById(id);

        return stockFlag && stockLockFlag;
    }

    @Override
    public Long batchCreateStockLock(List<StockLockSaveReqVO> batchCreateReqVO) {
        List<StockLockDO> stockLockDOList = StockLockConvert.INSTANCE.convertStockLockDOList(batchCreateReqVO);
        if (CollUtil.isEmpty(stockLockDOList)) {
            return null;
        }
        // 获取操作人信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserRespDTO adminUserApiUser = adminUserApi.getUser(loginUserId);
        if (Objects.isNull(adminUserApiUser)) {
            throw exception(OPERATION_FORBIDDEN);
        }
        stockLockDOList.forEach(s -> {
            s.setLockTime(LocalDateTime.now());
            s.setLockByUserId(adminUserApiUser.getId());
            s.setLockByUserName(adminUserApiUser.getNickname());
        });
        List<Long> stockIdList = stockLockDOList.stream().map(StockLockDO::getStockId).distinct().toList();
        if (CollUtil.isEmpty(stockIdList)) {
            throw exception(STOCK_NOT_EXISTS);
        }
        List<StockDO> stockDOList = stockMapper.selectBatchIds(stockIdList);
        if (CollUtil.isEmpty(stockDOList)) {
            throw exception(STOCK_NOT_EXISTS);
        }
        Map<Long, StockDO> stockDOMap = stockDOList.stream().collect(Collectors.toMap(StockDO::getId, s -> s));
        List<StockDO> updateStockDOList = new ArrayList<>();
        stockLockDOList.forEach(s -> {
            StockDO stockDO = stockDOMap.get(s.getStockId());
            if (Objects.isNull(stockDO)) {
                return;
            }
            Integer lockedQuantity = NumberUtil.add(stockDO.getLockQuantity(), s.getLockQuantity()).intValue();
            int availableQuantity = NumberUtil.sub(stockDO.getInitQuantity(), stockDO.getUsedQuantity()).intValue();
            if (availableQuantity < BigDecimal.ZERO.intValue()) {
                throw exception(STOCK_LOCK_EXCEED);
            }
            //  1. 更新库存数量信息
            stockDO.setLockQuantity(lockedQuantity);
            stockDO.setAvailableQuantity(availableQuantity);
            updateStockDOList.add(stockDO);
        });
        // 批量更新库存数量信息
        stockMapper.updateBatch(updateStockDOList);
        // 插入占用数据
        stockLockMapper.insertBatch(stockLockDOList);
        // 超额校验
        return null;
    }

    @Override
    public List<StockDO> getStockListBySkuIdList(List<Long> skuIdList) {
        return stockMapper.selectList(StockDO::getId,skuIdList);
    }

    @Override
      public List<StockDTO> getStockDTOListBySkuIdList(List<Long> skuIdList) {
        List<StockDO> doList = stockMapper.selectList(StockDO::getSkuId, skuIdList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<StockDTO> dtoList = BeanUtils.toBean(doList, StockDTO.class);
        List<Long> wareHouseIdList = dtoList.stream().map(StockDTO::getWarehouseId).distinct().toList();
        if(CollUtil.isNotEmpty(wareHouseIdList)){
            Map<Long,WarehouseDO> wareHouseMap = warehouseService.getWarehouseList(wareHouseIdList);
            if(CollUtil.isNotEmpty(wareHouseMap)){
                dtoList.forEach(s->{
                    WarehouseDO warehouseDO = wareHouseMap.get(s.getWarehouseId());
                    if(warehouseDO != null){
                        s.setVenderFlag(warehouseDO.getVenderFlag());
                    }
                });
            }
        }
        return dtoList;
    }

    @Override
    public List<StockDTO> getStockListBySkuCode(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)) {
            return List.of();
        }
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getSkuCode, skuCodeList);
        if (CollUtil.isEmpty(stockDOList)) {
            return List.of();
        }
        return StockConvert.INSTANCE.convertStockDTOList(stockDOList);
    }

    @Override
    public boolean deleteByContractId(Long purchaseContractId) {
        LambdaUpdateWrapper<StockLockDO> wrapper = new LambdaUpdateWrapper<StockLockDO>();
        wrapper.eq(StockLockDO::getSaleContractId, purchaseContractId);
        return stockLockMapper.delete(wrapper) > 0;
    }

    @Override
    public Map<Long, PriceQuantityEntity> getStockPriceMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        List<StockLockDO> stockLockDOS = stockLockMapper.selectList(new LambdaQueryWrapperX<StockLockDO>().eq(StockLockDO::getSourceOrderType, StockLockSourceTypeEnum.SALE_CONTRACT.getValue())
                .in(StockLockDO::getSaleContractItemId, ids));
        if (CollUtil.isEmpty(stockLockDOS)) {
            return Map.of();
        }
        List<Long> stockIds = stockLockDOS.stream().map(StockLockDO::getStockId).toList();
        List<StockDO> stockDOS = stockMapper.selectList(StockDO::getId, stockIds);
        if (CollUtil.isEmpty(stockDOS)){
            return Map.of();
        }
        Map<Long, StockDO> stockMap = stockDOS.stream().collect(Collectors.toMap(StockDO::getId, s -> s));
        Map<Long, List<StockLockDO>> stockLockMap = stockLockDOS.stream().collect(Collectors.groupingBy(StockLockDO::getSaleContractItemId));
        Map<Long, PriceQuantityEntity> result = new HashMap<>();
        stockLockMap.forEach((k, v) -> {
          if (CollUtil.isEmpty(v)){
              return;
          }
            List<PriceQuantityEntity> priceQuantityEntities = v.stream().map(s -> {
                Long stockId = s.getStockId();
                StockDO stockDO = stockMap.get(stockId);
                if (Objects.isNull(stockDO)) {
                    return null;
                }
                return new PriceQuantityEntity().setQuantity(s.getLockQuantity()).setWithTaxPrice(stockDO.getPrice());
            }).filter(Objects::nonNull).toList();
          if (CollUtil.isEmpty(priceQuantityEntities)){
              return;
          }
          // 总价
            Optional<JsonAmount> totalPriceOpt = priceQuantityEntities.stream().map(PriceQuantityEntity::getWithTaxPrice).filter(Objects::nonNull).reduce((a, b) -> new JsonAmount().setAmount(a.getAmount().add(b.getAmount())).setCurrency(a.getCurrency()));
          // 总数量
            Integer totalQuantity = priceQuantityEntities.stream().map(PriceQuantityEntity::getQuantity).reduce(Integer::sum).orElse(0);
           if (totalPriceOpt.isPresent()&&totalQuantity>0){
                result.put(k, new PriceQuantityEntity().setQuantity(totalQuantity).setWithTaxPrice(new JsonAmount().setAmount(NumberUtil.div(totalPriceOpt.get().getAmount(),BigDecimal.valueOf(totalQuantity))).setCurrency("RMB")));
           }
        });
        return result;
    }

    @Override
    public Map<String,Map<String,Integer>> validateStockByContractCodeList(List<String> saleContractCodeList) {
        List<StockLockDO> stockLockDOList = stockLockMapper.selectList(new LambdaQueryWrapperX<StockLockDO>().in(StockLockDO::getSaleContractCode,saleContractCodeList));
        Map<String,Map<String,Integer>> stockLockResultMap = new HashMap<>();
        Map<String,Map<String,Integer>> stockResultMap = new HashMap<>();
        Map<String, Map<String, Integer>> result = new HashMap<>();
        if (CollUtil.isNotEmpty(stockLockDOList)){
            Map<String, List<StockLockDO>> stockLockContractMap = stockLockDOList.stream().collect(Collectors.groupingBy(StockLockDO::getSaleContractCode));
            stockLockContractMap.forEach((k, v) -> {
                if (CollUtil.isNotEmpty(v)){
                    Map<String, Integer> stockLockMap = v.stream().collect(Collectors.toMap(StockLockDO::getSkuCode, StockLockDO::getLockQuantity,Integer::sum));
                    stockLockResultMap.put(k, stockLockMap);
                }
            });
        }
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getSaleContractCode, saleContractCodeList);
        if (CollUtil.isNotEmpty(stockDOList)){
            Map<String, List<StockDO>> stockContractMap = stockDOList.stream().collect(Collectors.groupingBy(StockDO::getSaleContractCode));
            stockContractMap.forEach((k, v) -> {
                if (CollUtil.isNotEmpty(v)){
                    // 在制数量 + 可用数量
                    Map<String, Integer> stockMap = v.stream().collect(Collectors.toMap(StockDO::getSkuCode, s->s.getProducingQuantity()+s.getAvailableQuantity(),Integer::sum));
                    stockResultMap.put(k, stockMap);
                }
            });
        }
        // 合并锁库数量
        combineMaps(stockLockResultMap, result);
        // 合并库存明细数量
        combineMaps(stockResultMap, result);
        return result;
    }

    @Override
    public Map<Long, Integer> getLockCountBySaleContractCode(String code) {
        List<StockLockDO> doList = stockLockMapper.selectList(StockLockDO::getSaleContractCode,code);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        return doList.stream().filter(s->Objects.nonNull(s.getSaleContractItemId())).collect(Collectors.groupingBy(StockLockDO::getSaleContractItemId,
                Collectors.summingInt(StockLockDO::getLockQuantity)));
    }

    @Override
    public List<StockLockDO> getStockLockListBySaleCode(String code) {
        return stockLockMapper.selectList(StockLockDO::getSaleContractCode,code);
    }

    @Override
    public void deleteBatchByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)){
            return;
        }
        stockLockMapper.deleteBatchIds(ids);
    }

    @Override
    public Map<String, Map<String, Integer>> getStockLockQuantityBySaleContractCodes(List<String> saleContractCodes) {
        if (CollUtil.isEmpty(saleContractCodes)){
            return Map.of();
        }
        List<StockLockDO> stockLockDOS = stockLockMapper.selectList(StockLockDO::getSaleContractCode, saleContractCodes);
        if (CollUtil.isEmpty(stockLockDOS)){
            return Map.of();
        }
        return stockLockDOS.stream().collect(Collectors.groupingBy(StockLockDO::getSaleContractCode, Collectors.toMap(StockLockDO::getBatchCode, StockLockDO::getLockQuantity, Integer::sum)));
    }

    @Override
    public Boolean cancelOutNoticeLockQuantity(Integer sourceOrderType, String sourceOrderCode) {
        List<StockLockDO> stockLockDOList = stockLockMapper.selectList(new LambdaQueryWrapperX<StockLockDO>().eq(StockLockDO::getSourceOrderType, sourceOrderType).eq(StockLockDO::getSourceOrderCode, sourceOrderCode));
        if (CollUtil.isEmpty(stockLockDOList)){
            return false;
        }
        AtomicInteger flagAto = new AtomicInteger(0);
        stockLockDOList.forEach(stockLockDO -> {
            Boolean cancelFlag = cancel(stockLockDO.getId());
            if (!cancelFlag){
                flagAto.getAndAdd(1);
            }
        });
        return flagAto.get() == stockLockDOList.size();
    }

    private void combineMaps(Map<String, Map<String, Integer>> sourceMap, Map<String, Map<String, Integer>> targetMap) {
        sourceMap.forEach((outerKey, innerMap) -> {
//            targetMap.computeIfAbsent(outerKey, k -> new HashMap<>())
//                    .forEach((innerKey, value) -> {
//                        Integer existingValue = innerMap.get(innerKey);
//                        if (existingValue != null) {
//                            targetMap.get(outerKey).merge(innerKey, existingValue, Integer::sum);
//                        }
//                    });
            innerMap.forEach((innerKey, value) ->
                    targetMap.computeIfAbsent(outerKey, k -> new HashMap<>())
                            .merge(innerKey, value,Integer::sum)
            );
        });
    }

}
