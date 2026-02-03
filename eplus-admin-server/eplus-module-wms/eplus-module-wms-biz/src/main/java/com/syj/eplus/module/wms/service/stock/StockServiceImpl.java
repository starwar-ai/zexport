package com.syj.eplus.module.wms.service.stock;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonLock;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SaleContractDTO;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.QueryStockReqVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.*;
import com.syj.eplus.module.wms.convert.stock.StockConvert;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.syj.eplus.module.wms.dal.mysql.stock.StockMapper;
import com.syj.eplus.module.wms.dal.mysql.stocklock.StockLockMapper;
import com.syj.eplus.module.wms.enums.StockLockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.service.bill.BillService;
import com.syj.eplus.module.wms.service.stockimport.StockImportService;
import com.syj.eplus.module.wms.service.warehouse.WarehouseService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓储管理-库存明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StockServiceImpl extends ServiceImpl<StockMapper, StockDO> implements StockService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StockMapper stockMapper;
    @Resource
    private SkuApi skuApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private CustApi custApi;
    @Resource
    private StockLockMapper stockLockMapper;
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private StockImportService stockImportService;
    @Resource
    @Lazy
    private BillService billService;
    @Resource
    private SaleContractApi saleContractApi;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStock(StockSaveReqVO createReqVO) {
        StockDO stock = StockConvert.INSTANCE.convertStockDO(createReqVO);
        // 插入
        stockMapper.insert(updateTotalAmount(stock));
        // 返回
        return stock.getId();
    }

    //入库计算 totalAmount = price * 入库数量
    private StockDO updateTotalAmount(StockDO stock){
        JsonAmount price = stock.getPrice();
        if(Objects.isNull(price)){
            return stock;
        }
        JsonAmount total = new JsonAmount().setCurrency(price.getCurrency());
        total.setAmount(price.getAmount().multiply(BigDecimal.valueOf(stock.getInitQuantity()))) ;
        stock.setTotalAmount(total);
        return stock;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(StockSaveReqVO updateReqVO) {
        // 校验存在
        validateStockExists(updateReqVO.getId());
        // 更新
        StockDO updateObj = StockConvert.INSTANCE.convertStockDO(updateReqVO);
        stockMapper.updateById( updateTotalAmount(updateObj) );
    }

    @Override
    public void deleteStock(Long id) {
        // 校验存在
        validateStockExists(id);
        // 删除
        stockMapper.deleteById(id);
    }

    @Override
    public boolean deleteStockByContractId(Long purchaseContractId) {
        LambdaUpdateWrapper<StockDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StockDO::getPurchaseContractId, purchaseContractId);
        return stockMapper.delete(wrapper) > 0;
    }

    private void validateStockExists(Long id) {
        if (stockMapper.selectById(id) == null) {
            throw exception(STOCK_NOT_EXISTS);
        }
    }

    @Override
    public StockRespVO getStock(Long skuId) {
        StockRespVO stockRespVO = stockMapper.getStock(skuId);
        if (stockRespVO == null) {
            return null;
        }
        // sku信息
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(Collections.singletonList(stockRespVO.getSkuCode()));
        // 客户信息
        Long custId = stockRespVO.getCustId();
        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(Collections.singletonList(custId));

        // 对批次信息进行分组并补全信息
        LambdaQueryWrapper<StockDO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StockDO::getSkuId, skuId);
        List<StockDO> stockDOList = super.list(queryWrapper);
        List<StockDetailRespVO> stockDetailRespVOList = BeanUtils.toBean(stockDOList, StockDetailRespVO.class);
        stockDetailRespVOList.stream().forEach(x -> {
            SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
            String skuCode = "";
            SimpleFile simpleFile = new SimpleFile().setFileUrl("");
            if (ObjectUtil.isNotNull(skuDTO)) {
                skuCode = skuDTO.getCode();
                x.setThumbnail(skuDTO.getThumbnail());
                List<SimpleFile> pictureList = skuDTO.getPicture();
                simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
            }
            x.setSkuCode(skuCode);
            x.setMainPicture(simpleFile); 
            String custCode = "";
            SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(x.getCustId());
            if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                custCode = simpleCustRespDTO.getCode();
            }
            x.setCustCode(custCode);

            int remainderQuantity = NumberUtil.sub(x.getInitQuantity(), x.getUsedQuantity()).intValue();
            x.setRemainderQuantity(remainderQuantity);
        });

        SkuDTO skuDTO = skuDTOMap.get(stockRespVO.getSkuCode());
        String skuCode = "";
        SimpleFile simpleFile = new SimpleFile().setFileUrl("");
        if (ObjectUtil.isNotNull(skuDTO)) {
            skuCode = skuDTO.getCode();
            stockRespVO.setThumbnail(skuDTO.getThumbnail());
            List<SimpleFile> pictureList = skuDTO.getPicture();
            simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
        }
        stockRespVO.setSkuCode(skuCode);
        stockRespVO.setMainPicture(simpleFile);

        String custCode = "";
        SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(custId);
        if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
            custCode = simpleCustRespDTO.getCode();
        }
        stockRespVO.setCustCode(custCode);
//        stockRespVO.setOskuCode(skuDTO.getOskuCode());
        int totalRemainderQuantity = NumberUtil.sub(stockRespVO.getTotalInitQuantity(), stockRespVO.getTotalUsedQuantity()).intValue();
        stockRespVO.setTotalRemainderQuantity(totalRemainderQuantity);

        stockDetailRespVOList.sort(Comparator.comparing(StockDetailRespVO::getBatchCode).reversed());
        stockRespVO.setChildren(stockDetailRespVOList);
        return stockRespVO;
    }

    @Override
    public PageResult<StockRespVO> getStockPage(StockPageReqVO pageReqVO) {
        PageResult<StockDO> stockDOPageResult = stockMapper.selectPage(pageReqVO);
        List<StockDO> list = stockDOPageResult.getList();

        if (CollectionUtils.isEmpty(list)) {
            return new PageResult<StockRespVO>().setList(new ArrayList<>()).setTotal(stockDOPageResult.getTotal());
        }
        List<StockRespVO> stockRespVOList = BeanUtils.toBean(list, StockRespVO.class);
        List<Long> stockSkuIdList = stockRespVOList.stream().map(StockRespVO::getSkuId).collect(Collectors.toList());
        Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(stockSkuIdList);
        stockRespVOList.stream().forEach(x -> {
            SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
            String skuCode = "";
            SimpleFile simpleFile = new SimpleFile().setFileUrl("");
            if (ObjectUtil.isNotNull(skuDTO)) {
                skuCode = skuDTO.getCode();
                x.setThumbnail(skuDTO.getThumbnail());
                List<SimpleFile> pictureList = skuDTO.getPicture();
                simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
            }
            x.setSkuCode(skuCode);
            x.setMainPicture(simpleFile);
        });

        return new PageResult<StockRespVO>().setList(stockRespVOList).setTotal(stockDOPageResult.getTotal());
    }

    @Override
    public PageResult<StockRespVO> listPage(StockPageReqVO reqVO) {
        // 必须使用 MyBatis Plus 的分页对象
        IPage<StockRespVO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        IPage<StockRespVO> pageResult = stockMapper.listPage(page, reqVO);

        List<StockRespVO> records = pageResult.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            // sku信息
            List<String> stockSkuCodeList = records.stream().map(StockRespVO::getSkuCode).distinct().collect(Collectors.toList());
            Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(stockSkuCodeList);
            // 客户信息
            List<Long> stockCustIdList = records.stream().map(StockRespVO::getCustId).distinct().collect(Collectors.toList());
            Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(stockCustIdList);

            // 对批次信息进行分组并补全信息
            LambdaQueryWrapper<StockDO> stockQueryWrapper = new LambdaQueryWrapper<>();
            stockQueryWrapper.in(StockDO::getSkuCode, stockSkuCodeList);
            List<StockDO> stockDOList = super.list(stockQueryWrapper);
            List<StockDetailRespVO> stockDetailRespVOList = BeanUtils.toBean(stockDOList, StockDetailRespVO.class);
            stockDetailRespVOList.sort(Comparator.comparing(StockDetailRespVO::getBatchCode).reversed());
            // 查询仓库信息
            List<Long> warehouseIdList = stockDetailRespVOList.stream().map(StockDetailRespVO::getWarehouseId).toList();
            Map<Long, Integer> warehouseCollect = warehouseService.listByIds(warehouseIdList).stream().collect(Collectors.toMap(WarehouseDO::getId, WarehouseDO::getVenderFlag));
            stockDetailRespVOList.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    x.setThumbnail(skuDTO.getThumbnail());
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                String custCode = "";
                SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(x.getCustId());
                if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                    custCode = simpleCustRespDTO.getCode();
                }
                x.setCustCode(custCode);
                x.setVenderFlag(warehouseCollect.get(x.getWarehouseId()));

                int remainderQuantity = NumberUtil.sub(x.getInitQuantity(), x.getUsedQuantity()).intValue();
                x.setRemainderQuantity(remainderQuantity);
            });

            Map<String, List<StockDetailRespVO>> collectedMap = stockDetailRespVOList.stream().collect(Collectors.groupingBy(x -> x.getSkuCode() + "$" + x.getCompanyId()));
            records.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    x.setThumbnail(skuDTO.getThumbnail());
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                String custCode = "";
                SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(x.getCustId());
                if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                    custCode = simpleCustRespDTO.getCode();
                }
                x.setCustCode(custCode);

                int totalRemainderQuantity = NumberUtil.sub(x.getTotalInitQuantity(), x.getTotalUsedQuantity()).intValue();
                x.setTotalRemainderQuantity(totalRemainderQuantity);

                String grouoKey = x.getSkuCode() + "$" + x.getCompanyId();
                List<StockDetailRespVO> children = collectedMap.get(grouoKey);
                x.setChildren(children);
            });
        }
        return new PageResult<>(records, pageResult.getTotal());
    }

    @Override
    public PageResult<StockDetailRespVO> queryBatch(StockQueryVO stockQueryVO) {
        PageResult<StockDO> stockDOPageResult = stockMapper.listDetail(stockQueryVO);
        List<StockDO> stockList = stockDOPageResult.getList();

        List<StockDetailRespVO> records = BeanUtils.toBean(stockList, StockDetailRespVO.class);
        if (!CollectionUtils.isEmpty(records)) {
            // sku信息
            List<String> stockSkuCodeList = records.stream().map(StockDetailRespVO::getSkuCode).distinct().collect(Collectors.toList());
            Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(stockSkuCodeList);
            // 客户信息
            List<Long> stockCustIdList = records.stream().map(StockDetailRespVO::getCustId).distinct().collect(Collectors.toList());
            Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(stockCustIdList);

            records.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuCode());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    x.setThumbnail(skuDTO.getThumbnail());
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                String custCode = "";
                SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(x.getCustId());
                if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                    custCode = simpleCustRespDTO.getCode();
                }
                x.setCustCode(custCode);

                int remainderQuantity = NumberUtil.sub(x.getInitQuantity(), x.getUsedQuantity()).intValue();
                x.setRemainderQuantity(remainderQuantity);
            });

        }
        return new PageResult<>(records, stockDOPageResult.getTotal());
    }

    @Override
    public List<StockDetailRespVO> listBatch(QueryStockReqVO listStockQueryVO) {

        List<StockDetailRespVO> records = new ArrayList<>();
        //库存查询条件
        LambdaQueryWrapper<StockDO> stockDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        stockDOLambdaQueryWrapper.isNull(StockDO::getSaleContractId).isNull(StockDO::getSaleContractCode);

        // 传参主体ID列表
        List<Long> companyIdList = new ArrayList<>(Optional.ofNullable(listStockQueryVO.getCompanyIdList()).orElse(Collections.emptyList()));
        // 业务增加-加工主体ID列表
        List<Long> producedCompanyIds = companyApi.listProducedCompany().stream().map(SimpleCompanyDTO::getId).distinct().toList();
        //主体ID列表合并  默认查询加工主体库存
        Integer producedFlag = Objects.isNull(listStockQueryVO.getProducedFlag()) ? BooleanEnum.YES.getValue() : listStockQueryVO.getProducedFlag();
        if (BooleanEnum.YES.getValue().equals(producedFlag)){
            companyIdList.addAll(producedCompanyIds.stream().filter(id -> !companyIdList.contains(id)).toList());
        }
        if (!CollectionUtils.isEmpty(companyIdList)) {
            stockDOLambdaQueryWrapper.in(StockDO::getCompanyId, companyIdList);
        }

        // 如果SKU编码不为空，构建SKU条件
        String skuCode = listStockQueryVO.getSkuCode();
        if (StrUtil.isNotEmpty(skuCode)) {
            Map<String, List<String>> ownSkuCodeMap = skuApi.getOwnSkuCodeListBySkuCode(List.of(skuCode));
            if (CollUtil.isEmpty(ownSkuCodeMap)||CollUtil.isEmpty(ownSkuCodeMap.get(skuCode))){
                return records;
            }
            stockDOLambdaQueryWrapper.in(StockDO::getSkuCode, ownSkuCodeMap.get(skuCode));
        }

        //库存查询  条件：传参+工厂主体  sku+自营sku
        List<StockDO> stockDOList = stockMapper.selectList(stockDOLambdaQueryWrapper);

        //产品锁库数据查询
        List<StockLockDO> baseStockLockList = stockLockMapper.selectList(new LambdaQueryWrapper<StockLockDO>().eq(StockLockDO::getSkuCode, skuCode));

        //过滤该销售合同的锁库数据
        String saleContractCode = listStockQueryVO.getSaleContractCode();
        Long saleContractItemId = listStockQueryVO.getSaleContractItemId();
        Map<String, Integer> thisSaleStockLockMap = new HashMap<>();
        if (CollUtil.isNotEmpty(baseStockLockList)){
            //查询使用本合同之外的数量，不过滤外销明细id
            thisSaleStockLockMap = baseStockLockList.stream().filter(s-> Objects.equals(s.getSaleContractCode(), saleContractCode))
                    .collect(Collectors.groupingBy(StockLockDO::getBatchCode,
                            Collectors.mapping(StockLockDO::getLockQuantity,
                                    Collectors.reducing(CalculationDict.ZERO, Integer::sum))));

            //使用外销明细过滤数据
//            if(Objects.isNull(saleContractItemId)){
//                thisSaleStockLockMap = baseStockLockList.stream().filter(s-> Objects.equals(s.getSaleContractCode(), saleContractCode))
//                        .collect(Collectors.groupingBy(StockLockDO::getBatchCode,
//                                Collectors.mapping(StockLockDO::getLockQuantity,
//                                        Collectors.reducing(CalculationDict.ZERO, Integer::sum))));
//            }else {
//                thisSaleStockLockMap = baseStockLockList.stream().filter(s->
//                                Objects.equals(s.getSaleContractCode(), saleContractCode)
//                                        && Objects.equals(s.getSaleContractItemId(), saleContractItemId) )
//                        .collect(Collectors.groupingBy(StockLockDO::getBatchCode,
//                                Collectors.mapping(StockLockDO::getLockQuantity,
//                                        Collectors.reducing(CalculationDict.ZERO, Integer::sum))));
//            }
        }

        if (!CollectionUtils.isEmpty(stockDOList)) {
            // 过滤无销售合同&可用数量大于 0 的库存批次信息
            List<StockDO> stockDOListFilter = stockDOList.stream().filter(x->
                    ObjectUtil.isNull(x.getSaleContractId())
                            && (x.getAvailableQuantity() > 0 || x.getLockQuantity() >0) || x.getProducingQuantity() >0
            ).toList();
            List<StockDetailRespVO> stockDetailRespVOList = BeanUtils.toBean(stockDOListFilter, StockDetailRespVO.class);
            if (!CollectionUtils.isEmpty(stockDetailRespVOList)) {
                // 计算实际可用库存 = 可用库存 - 锁定库存
                calcAvailableQuantitySubLockQuantity(stockDetailRespVOList,thisSaleStockLockMap);
                records.addAll(stockDetailRespVOList);
            }
        }

        // 设置当前单据-明细可编辑库存（可用库存+锁定库存）
        if (!CollectionUtils.isEmpty(records) ) {
            LambdaQueryWrapper<StockLockDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StockLockDO::getSaleContractCode, saleContractCode);
            lambdaQueryWrapper.eq(StockLockDO::getSaleContractItemId, listStockQueryVO.getSaleContractItemId());
            List<StockLockDO> stockLockDOS = stockLockMapper.selectList(lambdaQueryWrapper);

            // 添加销售合同锁定库存
//            if (ObjectUtil.isNotNull(sourceOrderType) && sourceOrderType.intValue() == StockLockSourceTypeEnum.PURCHASE_PLAN.getValue()){
//                LambdaQueryWrapper<StockLockDO> saleContractQuery = new LambdaQueryWrapper<>();
//                saleContractQuery.eq(StockLockDO::getSaleContractCode, saleContractCode);
//                saleContractQuery.eq(StockLockDO::getSourceOrderType, StockLockSourceTypeEnum.SALE_CONTRACT.getValue());
//                List<StockLockDO> saleContractDOS = stockLockMapper.selectList(listStockQueryVO.getSaleContractCode());
//                if (!CollectionUtils.isEmpty(saleContractDOS)) {
//                    stockLockDOS.addAll(saleContractDOS);
//                }
//            }
            if (!CollectionUtils.isEmpty(stockLockDOS)) {
                Map<String, Integer> stockLockCollect = stockLockDOS.stream().collect(Collectors.toMap(StockLockDO::getBatchCode, StockLockDO::getLockQuantity, Integer::sum));
                records.forEach(x->{
                    Integer lockQuantity = stockLockCollect.get(x.getBatchCode());
                    x.setSourceOrderLockedQuantity(lockQuantity);
                    x.setAvailableQuantity(x.getAvailableQuantity());

                });
            }
        }
        // 库存数量 = 可用库存 + 在制库存
        if (CollUtil.isNotEmpty(records)){
            records.forEach(s->{
                int availableQuantity = Objects.isNull(s.getAvailableQuantity()) ? CalculationDict.ZERO : s.getAvailableQuantity();
                int producingQuantity = Objects.isNull(s.getProducingQuantity()) ? CalculationDict.ZERO : s.getProducingQuantity();
                if (!BooleanEnum.YES.getValue().equals(listStockQueryVO.getOnlyAvailableQuantityFlag())){
                    s.setAvailableQuantity(availableQuantity + producingQuantity);
                }
            });
        }

        if (BooleanEnum.YES.getValue().equals(listStockQueryVO.getOnlyLockFlag())){
            return records.stream().filter(s->s.getAvailableQuantity() > 0&&(Objects.nonNull(s.getSourceOrderLockedQuantity())&&s.getSourceOrderLockedQuantity()>0)).toList();
        }else {
            return records.stream().filter(s->s.getAvailableQuantity() > 0).toList();
        }
    }

    /**
     * 计算实际可用库存 = 可用库存 - 锁定库存
     * @param stockDetailRespVOList 库存明细列表
     * @param thisSaleStockLockMap      锁定库存缓存
     */

    private void calcAvailableQuantitySubLockQuantity(List<StockDetailRespVO> stockDetailRespVOList, Map<String, Integer> thisSaleStockLockMap){
        if (CollUtil.isEmpty(stockDetailRespVOList)){
            return;
        }
        stockDetailRespVOList.forEach(stockDetailRespVO->{
            String batchCode = stockDetailRespVO.getBatchCode();
            Integer lockQuantity =stockDetailRespVO.getLockQuantity();
            if (Objects.isNull(lockQuantity)||lockQuantity == CalculationDict.ZERO){
                return;
            }
            stockDetailRespVO.setAvailableQuantity(NumberUtil.sub(stockDetailRespVO.getAvailableQuantity(),lockQuantity).intValue());
            Integer thisSaleLockQuantity = thisSaleStockLockMap.get(batchCode);
            if (Objects.isNull(thisSaleLockQuantity)||thisSaleLockQuantity == CalculationDict.ZERO){
                return;
            }
            stockDetailRespVO.setAvailableQuantity(NumberUtil.add(stockDetailRespVO.getAvailableQuantity(),thisSaleLockQuantity).intValue());
        });
    }

    @Override
    public Map<String, StockDO> getStockListByBatchCode(List<String> batchCodeList) {
        if (CollUtil.isEmpty(batchCodeList)) {
            return Map.of();
        }
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getBatchCode, batchCodeList);
        if (CollUtil.isEmpty(stockDOList)) {
            return Map.of();
        }
        return stockDOList.stream().collect(Collectors.toMap(StockDO::getBatchCode, s -> s));
    }

    @Override
    public Map<String, Integer> queryTotalStock(List<QueryStockReqVO> queryStockReqVOList) {
        Map<String, Integer> result = new HashMap<>();
        queryStockReqVOList.forEach(queryStockReqVO -> {
            int availableQuantity = 0;
            List<StockDetailRespVO> stockDetailRespVOList = this.listBatch(queryStockReqVO);
            if (!CollectionUtils.isEmpty(stockDetailRespVOList)) {
                availableQuantity = stockDetailRespVOList.stream().mapToInt(StockDetailRespVO::getAvailableQuantity).sum();
            }
            result.put(queryStockReqVO.getSkuCode(), availableQuantity);
        });

        return result;
    }

    @Override
    public PageResult<StockRespVO> listPageBySku(StockPageReqVO pageReqVO) {
        Integer pageNo = pageReqVO.getPageNo();
        Integer pageSize = pageReqVO.getPageSize();
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<StockDO> stockDOPageResult = stockMapper.selectPage(pageReqVO);

        List<StockDO> doList = stockDOPageResult.getList();
        if(CollUtil.isEmpty(doList)){
            return PageResult.empty();
        }

        Map<String, List<StockDO>> listMap = doList.stream().collect(Collectors.groupingBy(StockDO::getSkuCode));
        List<StockRespVO> result= new Stack<>();

        // sku信息
        List<String> stockSkuCodeList = doList.stream().map(StockDO::getSkuCode).distinct().collect(Collectors.toList());
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(stockSkuCodeList);
        // 客户信息
//        List<Long> stockCustIdList = doList.stream().map(StockDO::getCustId).distinct().collect(Collectors.toList());
//        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(stockCustIdList);
        //仓库信息
        List<Long> warehouseIdList = doList.stream().map(StockDO::getWarehouseId).distinct().toList();
        Map<Long, Integer> warehouseMap = warehouseService.listByIds(warehouseIdList).stream().collect(Collectors.toMap(WarehouseDO::getId, WarehouseDO::getVenderFlag));


        Integer no = 0;
        for (var entry : listMap.entrySet()){
            if(pageSize * (pageNo -1) > no || pageNo * pageSize <= no ){
                no++;
                continue;
            }
            no++;
            StockRespVO stockVO = null;

            for (var stock : entry.getValue()){
                if(stockVO == null){
                    stockVO = BeanUtils.toBean(stock,StockRespVO.class);
                    stockVO.setTotalProducingQuantity(0).setTotalLockQuantity(0)
                            .setTotalAvailableQuantity(0).setTotalInitQuantity(0).setTotalUsedQuantity(0);
                    if(CollUtil.isNotEmpty(skuDTOMap)){
                        SkuDTO skuDTO = skuDTOMap.get(stock.getSkuCode());
                        if(Objects.nonNull(skuDTO)){
                            Optional<SimpleFile> first = skuDTO.getPicture().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                            if(first.isPresent()){
                                stockVO.setMainPicture(first.get());
                            }
                            stockVO.setThumbnail(skuDTO.getThumbnail());
                        }
                    }
                }
                stockVO.setTotalUsedQuantity(stockVO.getTotalUsedQuantity() + stock.getUsedQuantity());
                stockVO.setTotalInitQuantity(stockVO.getTotalInitQuantity() + stock.getInitQuantity());
                stockVO.setTotalLockQuantity(stockVO.getTotalLockQuantity() + stock.getLockQuantity());
                stockVO.setTotalAvailableQuantity(stockVO.getTotalAvailableQuantity() + stock.getAvailableQuantity());
                stockVO.setTotalProducingQuantity(stockVO.getTotalProducingQuantity() + stock.getProducingQuantity());
            }
            if(stockVO == null){
                stockVO = new StockRespVO();
            }
            List<StockDetailRespVO> children = BeanUtils.toBean(entry.getValue(), StockDetailRespVO.class);
            if(CollUtil.isNotEmpty(children)){
                children.forEach(ch->{
                    if(CollUtil.isNotEmpty(skuDTOMap)){
                        SkuDTO skuDTO = skuDTOMap.get(ch.getSkuCode());
                        if(Objects.nonNull(skuDTO)){
                            Optional<SimpleFile> first = skuDTO.getPicture().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                            first.ifPresent(ch::setMainPicture);
                            ch.setThumbnail(skuDTO.getThumbnail());
                        }
                    }
                    if(CollUtil.isNotEmpty(warehouseMap)){
                        ch.setVenderFlag(warehouseMap.get(ch.getWarehouseId()));
                    }
                });
            }
            stockVO.setChildren(children);
            result.add(stockVO);
        }

        return new PageResult<StockRespVO>().setList(result).setTotal((long) listMap.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByPurchaseContractCode(String purchaseContractCode) {
        List<StockDO> stockDOS = stockMapper.selectList(StockDO::getPurchaseContractCode, purchaseContractCode);
        if(CollUtil.isEmpty(stockDOS)){
            return;
        }
        List<Long> idList = stockDOS.stream().map(StockDO::getId).distinct().toList();
        List<StockLockDO> stockLockDOS = stockLockMapper.selectList(StockLockDO::getStockId, idList);
        if(CollUtil.isNotEmpty(stockLockDOS)){
            throw exception(STOCK_LOCK_EXISTS);
        }
        stockMapper.delete(StockDO::getPurchaseContractCode,purchaseContractCode);
    }

    @Override
    public void deleteStockByPurchaseContractCode(List<String> purchaseContractCode) {
        LambdaUpdateWrapper<StockDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(StockDO::getPurchaseContractCode, purchaseContractCode);
        stockMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollBackLockQuantity(Long id) {
        SaleContractDTO contractDTO = saleContractApi.getSaleContractById(id);
        if(Objects.isNull(contractDTO) || CollUtil.isNotEmpty(contractDTO.getChildren())) {
            throw exception(SALE_CONTRACT_NOT_EXITS);
        }

        int delete = stockLockMapper.delete(StockLockDO::getSaleContractCode, contractDTO.getId());
        List<StockLockDO> list = new ArrayList<>();
        contractDTO.getChildren().forEach(s->{
            List<JsonLock> lockMsg = s.getLockMsg();
            if(CollUtil.isEmpty(lockMsg)){
                return;
            }
            lockMsg.forEach(l->{
                StockLockDO lockDO = new StockLockDO();
                lockDO.setLockQuantity(l.getLockQuantity()).setStockId(l.getStockId()).setBatchCode(l.getBatchCode());
                lockDO.setSkuCode(s.getSkuCode()).setSaleContractId(contractDTO.getId()).setSaleContractCode(contractDTO.getCode());
                lockDO.setSaleContractItemId(s.getId()).setSourceOrderType(StockLockSourceTypeEnum.SALE_CONTRACT.getValue());
                lockDO.setLockTime(LocalDateTime.now());
                lockDO.setCompanyId(contractDTO.getCompanyId()).setCompanyName(contractDTO.getCompanyName());
                list.add(lockDO);
            });
        });
        stockLockMapper.insertBatch(list);
    }

    @Override
    public Map<String, Integer> getNotOutStockByPurchaseContractCode(String contractCode, List<String> skuCodeList) {
        List<StockDO> stockDOList = stockMapper.selectList(new LambdaQueryWrapperX<StockDO>().eq(StockDO::getPurchaseContractCode, contractCode).in(StockDO::getSkuCode, skuCodeList));
        if (CollUtil.isEmpty(stockDOList)){
            return Map.of();
        }
        return stockDOList.stream().collect(Collectors.toMap(StockDO::getSkuCode, StockDO::getAvailableQuantity,Integer::sum));
    }

    @Override
    public List<StockDO> calculateRemainTotalAmount(List<StockDO> stockDOList) {
        if (CollUtil.isEmpty(stockDOList)){
            return stockDOList;
        }
        stockDOList.forEach(s->{
            Integer initQuantity = s.getInitQuantity();
            Integer usedQuantity = s.getUsedQuantity();
            s.setRemainTotalAmount(new JsonAmount().setAmount(s.getPrice().getAmount().multiply(BigDecimal.valueOf(initQuantity - usedQuantity))).setCurrency(s.getPrice().getCurrency()));
        });
        return stockDOList;
    }

    @Override
    public List<StockDO> getStockDOByPurchaseContractCode(String purchaseContractCode, Set<String> skuCodes) {
        if (StrUtil.isEmpty(purchaseContractCode)||CollUtil.isEmpty(skuCodes)){
            return List.of();
        }
        return stockMapper.selectList(new LambdaQueryWrapperX<StockDO>().eq(StockDO::getPurchaseContractCode, purchaseContractCode).in(StockDO::getSkuCode, skuCodes));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importStock(String importCode) {
        List<StockImportDO> importDos = stockImportService.getListByImportCode(importCode);
        if(CollUtil.isEmpty(importDos)){
            throw exception(STOCK_NOT_DONE);
        }
        List<StockImportDO> stockImportDOS = importDos.stream().filter(s -> s.getStockFlag() == 0 && s.getErrorFlag() == 0).toList();
        if(CollUtil.isEmpty(stockImportDOS)){
            throw exception(STOCK_NOT_DONE);
        }
        stockImportDOS.forEach(s->{
            if(s.getQtyPerOuterbox() == null || s.getQtyPerOuterbox() == 0){
                s.setQtyPerOuterbox(1);
            }
//            s.setId(null);
            s.setBatchCode(batchCodeUtil.genBatchCode(CommonDict.EMPTY_STR));
            s.setAvailableQuantity(s.getInitQuantity());
        });
        List<StockDO> stockDOS = CreateStockBill(BeanUtils.toBean(stockImportDOS, StockDO.class));
        stockDOS.forEach(s->{
            s.setId(null);
        });
        stockMapper.insertBatch(stockDOS);
        //会写数据
        stockImportDOS.forEach(s->{
            s.setStockFlag(1);
        });
        stockImportService.BatchUpdate(stockImportDOS);
    }

    @Override
    public PageResult<SimpleStockResp> getSimpleStock(StockPageReqVO pageReqVO) {
        List<WarehouseDO> warehouseDOList = warehouseService.list(new LambdaQueryWrapperX<WarehouseDO>().select(WarehouseDO::getId).ne(WarehouseDO::getVenderFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(warehouseDOList)){
            return PageResult.empty();
        }
        Set<Long> warehouseIdSet = warehouseDOList.stream().map(WarehouseDO::getId).collect(Collectors.toSet());
        pageReqVO.setWarehouseIdSet(warehouseIdSet);
        PageResult<StockDO> stockDOPageResult = stockMapper.selectPage(pageReqVO);
        List<StockDO> doList = stockDOPageResult.getList();
        if(CollUtil.isEmpty(doList)){
            return PageResult.empty();
        }
        List<SimpleStockResp> result= new Stack<>();
        // sku信息
        List<String> stockSkuCodeList = doList.stream().map(StockDO::getSkuCode).distinct().collect(Collectors.toList());
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(stockSkuCodeList);
        // 客户信息
//        List<Long> stockCustIdList = doList.stream().map(StockDO::getCustId).distinct().collect(Collectors.toList());
//        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(stockCustIdList);
        //仓库信息
        List<Long> warehouseIdList = doList.stream().map(StockDO::getWarehouseId).distinct().toList();
        doList.forEach(stockDO->{
            if(CollUtil.isNotEmpty(skuDTOMap)){
                SkuDTO skuDTO = skuDTOMap.get(stockDO.getSkuCode());
                if(Objects.nonNull(skuDTO)){
                    Optional<SimpleFile> first = skuDTO.getPicture().stream().filter(s -> s.getMainFlag() == 1).findFirst();
                    first.ifPresent(stockDO::setMainPicture);
                    stockDO.setThumbnail(skuDTO.getThumbnail());
                }
            }
            stockDO.setVenderFlag(BooleanEnum.NO.getValue());
            SimpleStockResp stockRespVO = BeanUtils.toBean(stockDO, SimpleStockResp.class);
            int availableQuantity = Objects.isNull(stockDO.getAvailableQuantity()) ? 0 : stockDO.getAvailableQuantity();
            int lockQuantity = Objects.isNull(stockDO.getLockQuantity()) ? 0 : stockDO.getLockQuantity();
            stockRespVO.setTotalAvailableQuantity(availableQuantity);
            stockRespVO.setOutQuantity(availableQuantity-lockQuantity);
            result.add(stockRespVO);
        });
        return new PageResult<SimpleStockResp>().setList(result).setTotal(stockDOPageResult.getTotal());
    }

    private List<StockDO> CreateStockBill(List<StockDO> stockDOS){
        if(CollUtil.isEmpty(stockDOS))
            return stockDOS;
        stockDOS.forEach(s->{
            BillSaveReqVO vo = new BillSaveReqVO();
            vo.setBillType(1).setBillStatus(2).setSourceType(StockSourceTypeEnum.IMPORT.getValue()).setBillTime(LocalDateTime.now());
            vo.setWarehouseId(s.getWarehouseId()).setWarehouseName(s.getWarehouseName());
            vo.setCompanyId(s.getCompanyId()).setCompanyName(s.getCompanyName());
            BillItemSaveReqVO item =  BeanUtils.toBean(s,BillItemSaveReqVO.class);
            item.setBatchCode(s.getBatchCode());
            item.setActQuantity(s.getInitQuantity());
            item.setOrderQuantity(s.getInitQuantity());
            item.setSourceType(StockSourceTypeEnum.IMPORT.getValue());

            if(Objects.isNull(s.getQtyPerOuterbox()) || s.getQtyPerOuterbox() > 0){
                item.setActBoxQuantity(0);
                item.setOrderBoxQuantity(0);
            }else {
                item.setActBoxQuantity(s.getInitQuantity() / s.getQtyPerOuterbox());
                item.setOrderBoxQuantity(item.getActBoxQuantity());
            }
            vo.setBillItemSaveReqVOList(List.of(item));
            billService.createBill(vo);
        });
        return stockDOS;
    }


    //入库计算 totalAmount = price * 入库数量
    @Override
    public List<StockDO> updateTotalAmount(List<StockDO> stockList) {
        if (CollUtil.isEmpty(stockList)) {
            return null;
        }
        stockList.forEach(stock -> {
            JsonAmount price = stock.getPrice();
            if (Objects.isNull(price)) {
                return;
            }
            JsonAmount total = new JsonAmount().setCurrency(price.getCurrency());
            total.setAmount(price.getAmount().multiply(BigDecimal.valueOf(stock.getInitQuantity())));
            stock.setTotalAmount(total);
            // 入库数量
            Integer initQuantity = stock.getInitQuantity();
            // 外箱装量
            Integer qtyPerOuterbox = stock.getQtyPerOuterbox();
            // 单箱体积
            BigDecimal outerboxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(stock.getSpecificationList());
            if (Objects.nonNull(qtyPerOuterbox) && qtyPerOuterbox != 0) {
                // 箱数 (向上取整)
                BigDecimal boxQuantity = NumUtil.div(initQuantity, qtyPerOuterbox).setScale(0, RoundingMode.UP);
                // 计算总体积 (入库数量/外箱装量*单箱体积)
                BigDecimal totalVolume = NumUtil.mul(boxQuantity, outerboxVolume);
                stock.setTotalVolume(totalVolume);
                // 计算总毛重 (入库数量/外箱装量*单箱毛重)
                JsonWeight outerBoxGrossWeight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(stock.getSpecificationList());
                Optional.ofNullable(outerBoxGrossWeight).flatMap(s -> Optional.ofNullable(s.getWeight())).ifPresent(weight -> {
                    stock.setTotalWeight(new JsonWeight(NumUtil.mul(boxQuantity, weight).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP), outerBoxGrossWeight.getUnit()));
                });
            }
        });
        return stockList;
    }
}
