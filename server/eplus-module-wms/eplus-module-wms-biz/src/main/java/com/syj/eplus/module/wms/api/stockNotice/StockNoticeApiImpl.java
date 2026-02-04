package com.syj.eplus.module.wms.api.stockNotice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.*;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.convert.stockNotice.StockNoticeConvert;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Desc——入库通知单 API实现类
 * Create by Rangers at  2024-06-11 22:38
 */
@Service
public class StockNoticeApiImpl implements IStockNoticeApi {

    @Resource
    private StockNoticeService stockNoticeService;
    @Resource
    private SkuApi skuApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private CustApi custApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedResponse toStockNotice(StockNoticeReqVO stockNoticeReqVO) {
        StockNoticeSaveReqVO stockNoticeSaveReqVO = BeanUtils.toBean(stockNoticeReqVO, StockNoticeSaveReqVO.class);
        stockNoticeSaveReqVO.setNoticeType(StockTypeEnum.IN_STOCK.getValue());

        List<StockNoticeItemDO> stockNoticeItemDOList = BeanUtils.toBean(stockNoticeReqVO.getStockNoticeItemReqVOList(), StockNoticeItemDO.class);

        // 所有产品总体积
        BigDecimal noticeTotalVolume = BigDecimal.ZERO;
        // 所有产品总重量
        JsonWeight noticeJsonWeight = new JsonWeight();
        if (!CollectionUtils.isEmpty(stockNoticeItemDOList)) {
            BigDecimal noticeTotalWeight = BigDecimal.ZERO;

            List<Long> skuIdList = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getSkuId).distinct().collect(Collectors.toList());
            Map<Long, SkuDTO> subSkuMap = skuApi.getSkuDTOMap(skuIdList);

            List<Long> venderIdList = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getVenderId).distinct().collect(Collectors.toList());
            Map<Long, SimpleVenderRespDTO> simpleVenderRespDTOMap = venderApi.getSimpleVenderRespDTOMap(venderIdList);

            List<Long> custIdList = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getCustId).distinct().collect(Collectors.toList());
            Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = custApi.getSimpleCustRespDTOMap(custIdList);
            String weightUnit = CalculationDict.GRAM;
            for (int i = 0; i < stockNoticeItemDOList.size(); i++) {
                StockNoticeItemDO stockNoticeItemDO = stockNoticeItemDOList.get(i);
                stockNoticeItemDO.setSortNum(i + 1);
                stockNoticeItemDO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
                // sku信息
                SkuDTO skuDTO = subSkuMap.get(stockNoticeItemDO.getSkuId());
                if (ObjectUtil.isNotNull(skuDTO)) {
                    stockNoticeItemDO.setSkuName(skuDTO.getName());
                    stockNoticeItemDO.setCskuCode(skuDTO.getCskuCode());

                    stockNoticeItemDO.setOwnBrandFlag(skuDTO.getOwnBrandFlag());
                    stockNoticeItemDO.setCustProFlag(skuDTO.getCustProFlag());
                }
                // 供应商信息
                SimpleVenderRespDTO simpleVenderRespDTO = simpleVenderRespDTOMap.get(stockNoticeItemDO.getVenderId());
                if (ObjectUtil.isNotNull(simpleVenderRespDTO)) {
                    stockNoticeItemDO.setVenderName(simpleVenderRespDTO.getName());
                }
                // 客户信息
                SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(stockNoticeItemDO.getCustId());
                if (ObjectUtil.isNotNull(simpleCustRespDTO)) {
                    stockNoticeItemDO.setCustName(simpleCustRespDTO.getName());
                }
                // 单产品总体积
                List<JsonSpecificationEntity> specificationList = stockNoticeItemDO.getSpecificationList();
                BigDecimal volume = CalcSpecificationUtil.calcSpecificationTotalVolume(specificationList);
                BigDecimal totalVolume = NumUtil.mul(volume, stockNoticeItemDO.getOrderBoxQuantity());
                stockNoticeItemDO.setTotalVolume(totalVolume);
                noticeTotalVolume = noticeTotalVolume.add(totalVolume);
                // 单产品总重量
                JsonWeight outerboxGrossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(specificationList);
                JsonWeight totalWeight = new JsonWeight();
                String unit = ObjectUtil.isNotNull(outerboxGrossweight) ? outerboxGrossweight.getUnit() : "";
                totalWeight.setUnit(unit);
                BigDecimal singleBoxWeight = ObjectUtil.isNotNull(outerboxGrossweight) ? outerboxGrossweight.getWeight() : BigDecimal.ZERO;
                BigDecimal weight = NumberUtil.mul(singleBoxWeight, stockNoticeItemDO.getOrderBoxQuantity());
                totalWeight.setWeight(weight);
                noticeTotalWeight = noticeTotalWeight.add(weight);
                weightUnit = unit;
                stockNoticeItemDO.setTotalWeight(totalWeight);
            }
            noticeJsonWeight.setWeight(noticeTotalWeight);
            noticeJsonWeight.setUnit(weightUnit);
            stockNoticeSaveReqVO.setTotalVolume(noticeTotalVolume);
            stockNoticeSaveReqVO.setTotalWeight(noticeJsonWeight);
        }
        stockNoticeSaveReqVO.setNoticeItems(stockNoticeItemDOList);
        stockNoticeSaveReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        return stockNoticeService.createNotice(stockNoticeSaveReqVO);
    }

    @Override
    public List<StockNoticeRespDTO> GetStockNoticeListByPurchaseCode(String code) {
        List<StockNoticeRespDTO> result = new ArrayList<>();
        PageResult<StockNoticeRespVO> noticePage = stockNoticeService.getNoticePage(new StockNoticePageReqVO().setPurchaseContractCode(code));
        if (CollUtil.isNotEmpty(noticePage.getList())) {
            noticePage.getList().forEach(s -> {
                StockNoticeRespDTO re = BeanUtils.toBean(s, StockNoticeRespDTO.class);
                List<StockNoticeItemRespVO> list = s.getStockNoticeItemRespVOList();
                if (CollUtil.isNotEmpty(list)) {
                    List<StockNoticeItemRespDTO> children = BeanUtils.toBean(list, StockNoticeItemRespDTO.class);
                    re.setChildren(children);
                }
                result.add(re);
            });
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedResponse createNotice(StockNoticeSaveReqDTO stockNoticeSaveReqDTO) {
        StockNoticeSaveReqVO createReqVO = StockNoticeConvert.INSTANCE.convertNoticeSaveReqVO(stockNoticeSaveReqDTO);
        createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        return stockNoticeService.createNotice(createReqVO);
    }

    @Override
    public PageResult<StockNoticeRespDTO> getNoticePage(StockNoticePageReqDTO pageReqDTO) {
        StockNoticePageReqVO pageReqVO = StockNoticeConvert.INSTANCE.convertNoticePageReqVO(pageReqDTO);
        PageResult<StockNoticeRespVO> pageResult = stockNoticeService.getNoticePage(pageReqVO);
        List<StockNoticeRespVO> stockNoticeRespVOList = pageResult.getList();
        List<StockNoticeRespDTO> stockNoticeRespDTOList = StockNoticeConvert.INSTANCE.convertStockNoticeRespDTOList(stockNoticeRespVOList);
        return new PageResult<StockNoticeRespDTO>().setList(stockNoticeRespDTOList).setTotal(pageResult.getTotal());
    }

    @Override
    public Long getBillNumByPurchaseCode(Long id) {
        return stockNoticeService.getBillNumByPurchaseCode(id);
    }

    @Override
    public List<StockNoticeRespDTO> getStockNoticeListBySaleContractList(List<String> saleContractCodeList) {
        List<StockNoticeRespDTO> result = new ArrayList<>();
        PageResult<StockNoticeRespVO> noticePage = stockNoticeService.getNoticePage(new StockNoticePageReqVO().setSaleContractCodeList(saleContractCodeList));
        if (CollUtil.isNotEmpty(noticePage.getList())) {
            noticePage.getList().forEach(s -> {
                StockNoticeRespDTO re = BeanUtils.toBean(s, StockNoticeRespDTO.class);
                List<StockNoticeItemRespVO> list = s.getStockNoticeItemRespVOList();
                if (CollUtil.isNotEmpty(list)) {
                    List<StockNoticeItemRespDTO> children = BeanUtils.toBean(list, StockNoticeItemRespDTO.class);
                    re.setChildren(children);
                }
                result.add(re);
            });
        }
        return result;
    }
}
