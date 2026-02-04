package com.syj.eplus.module.scm.service.quoteitem;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ContainerTypeEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemRespVO;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemSaveReqVO;
import com.syj.eplus.module.scm.convert.quoteitem.QuoteItemConvert;
import com.syj.eplus.module.scm.dal.dataobject.quoteitem.QuoteItemDO;
import com.syj.eplus.module.scm.dal.dataobject.venderpayment.VenderPayment;
import com.syj.eplus.module.scm.dal.mysql.quoteitem.QuoteItemMapper;
import com.syj.eplus.module.scm.dal.mysql.venderpayment.VenderPaymentMapper;
import com.syj.eplus.module.scm.service.vender.VenderService;
import com.syj.eplus.module.scm.util.CalcContactUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 供应商报价明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class QuoteItemServiceImpl implements QuoteItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QuoteItemMapper quoteItemMapper;

    @Resource
    @Lazy
    VenderService venderService;

    @Resource
    private PackageTypeApi packageTypeApi;
    @Resource
    private VenderPaymentMapper venderPaymentMapper;

    @Resource
    private PaymentItemApi paymentItemApi;

    @Resource
    @Lazy
    private SkuApi skuApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuoteItem(QuoteItemSaveReqVO createReqVO) {
        QuoteItemDO quoteItem = QuoteItemConvert.INSTANCE.convertQuoteItemDO(createReqVO);
        // 插入
        quoteItemMapper.insert(autoDealDO(quoteItem));

        checkQuoteItem(quoteItem.getSkuId());
        // 返回
        return quoteItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuoteItem(QuoteItemSaveReqVO updateReqVO) {
        // 校验存在
        validateQuoteItemExists(updateReqVO.getId());
        // 更新
        QuoteItemDO updateObj = QuoteItemConvert.INSTANCE.convertQuoteItemDO(updateReqVO);
        checkQuoteItem(updateReqVO.getSkuId());
        quoteItemMapper.updateById(autoDealDO(updateObj));
    }

    @Override
    public void deleteQuoteItem(Long id) {
        // 校验存在
        validateQuoteItemExists(id);
        // 删除
        quoteItemMapper.deleteById(id);
    }

    private void validateQuoteItemExists(Long id) {
        if (quoteItemMapper.selectById(id) == null) {
            throw exception(QUOTE_ITEM_NOT_EXISTS);
        }
    }

    @Override
    public QuoteItemRespVO getQuoteItem(Long id) {
        QuoteItemDO quoteItemDO = quoteItemMapper.selectById(id);
        if (quoteItemDO == null) {
            return null;
        }
        QuoteItemRespVO quoteItemRespVO = QuoteItemConvert.INSTANCE.convertQuoteItemRespVO(quoteItemDO);
        PackageTypeDTO dto = packageTypeApi.getById(quoteItemDO.getId());
        if(Objects.nonNull(dto)){
            quoteItemRespVO.setPackageTypeName(dto.getName()).setPackageTypeEngName(dto.getNameEng());
        }
        return quoteItemRespVO;
    }

    @Override
    public PageResult<QuoteItemRespVO> getQuoteItemPage(QuoteItemPageReqVO pageReqVO) {
        PageResult<QuoteItemDO> quoteItemDOPageResult = quoteItemMapper.selectPage(pageReqVO);
        List<QuoteItemRespVO> list = BeanUtils.toBean(quoteItemDOPageResult.getList(),QuoteItemRespVO.class);
        if(CollUtil.isNotEmpty(list)){
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            if(CollUtil.isNotEmpty(packageTypeList)){
                list.forEach(s->{
                    if(CollUtil.isNotEmpty(s.getPackageType())){
                        List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                        List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item->distinctPackgeType.contains(item.getId())).toList();
                        if(CollUtil.isNotEmpty(tempPackageTypeList)){
                            List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                            s.setPackageTypeName(String.join(",",packageTypeNameList));
                            List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                            s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                        }
                    }
                });
            }
        }
        return new PageResult<QuoteItemRespVO>().setList(list).setTotal(quoteItemDOPageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertQuoteItem(List<QuoteItemSaveReqVO> quoteItemSaveReqVOList) {
        List<QuoteItemDO> quoteItemDOList = QuoteItemConvert.INSTANCE.convertQuoteItenDOList(quoteItemSaveReqVOList);
        quoteItemMapper.insertBatch(autoDealDO(quoteItemDOList));
        List<Long> list = quoteItemDOList.stream().map(QuoteItemDO::getSkuId).distinct().toList();
        list.forEach(this::checkQuoteItem);
    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOBySkuId(Long skuId) {
        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().eq(QuoteItemDO::getSkuId, skuId));
        if (CollUtil.isEmpty(quoteItemDOList)) {
            return null;
        }
        List<String> venderCodeList = quoteItemDOList.stream().map(QuoteItemDO::getVenderCode).distinct().toList();
        Map<String, SimpleVenderRespDTO> venderMap = venderService.getSimpleVenderDTOMapByCodes(venderCodeList,BooleanEnum.YES.getValue());

        List<QuoteitemDTO> quoteitemDTOList = QuoteItemConvert.INSTANCE.convertQuoteItemDTOListById(quoteItemDOList, venderMap);

        if(CollUtil.isNotEmpty(quoteitemDTOList)){
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            if(CollUtil.isNotEmpty(packageTypeList)){
                quoteitemDTOList.forEach(s->{
                    if(CollUtil.isNotEmpty(s.getPackageType())){
                        List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                        List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt->distinctPackgeType.contains(pt.getId())).toList();
                        if(CollUtil.isNotEmpty(tempPackageTypeList)){
                            List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                            s.setPackageTypeName(String.join(",",packageTypeNameList));
                            List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                            s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                        }
                    }
                });
            }
        }

        return quoteitemDTOList;
    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOByPackageTypeId(Long pckageTypeId) {
        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().eq(QuoteItemDO::getPackageType, pckageTypeId));
        if (CollUtil.isEmpty(quoteItemDOList)) {
            return null;
        }
        return  QuoteItemConvert.INSTANCE.convertQuoteItemDTOByDOList(quoteItemDOList);
    }

    @Override
    public void batchDeleteQuoteitem(String skuCode,Long skuId) {
        LambdaQueryWrapperX<QuoteItemDO> queryWrapperX = new LambdaQueryWrapperX<QuoteItemDO>();
        if (StrUtil.isNotEmpty(skuCode)){
            queryWrapperX.eq(QuoteItemDO::getSkuCode, skuCode);
        }
        if (Objects.nonNull(skuId)){
            queryWrapperX.eq(QuoteItemDO::getSkuId, skuId);
        }
        quoteItemMapper.delete(queryWrapperX);
    }

//    @Override
//    public List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList) {
//        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().in(QuoteItemDO::getSkuCode, skuCodeList));
//        if (CollUtil.isEmpty(quoteItemDOList)) {
//            return null;
//        }
//        List<String> venderCodeList = quoteItemDOList.stream().map(QuoteItemDO::getVenderCode).distinct().toList();
//        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOListByCodeList(venderCodeList);
//        Map<String, SimpleVenderRespDTO> venderMap;
//        if(CollUtil.isNotEmpty(simpleVenderRespDTOList)){
//            venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getCode, s -> s));
//        }else {
//            venderMap = null;
//        }
//        return QuoteItemConvert.INSTANCE.convertQuoteItemDTOListByCode(quoteItemDOList,venderMap);
//    }

    @Override
    public List<QuoteitemDTO> getQuoteItemDTOListBySkuIdList(List<Long> skuIdList) {
        if (CollUtil.isEmpty(skuIdList)){
            return List.of();
        }
        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().in(QuoteItemDO::getSkuId, skuIdList));
        if (CollUtil.isEmpty(quoteItemDOList)) {
            return null;
        }
        List<String> venderCodeList = quoteItemDOList.stream().map(QuoteItemDO::getVenderCode).distinct().toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOListByCodeList(venderCodeList);
        Map<String, SimpleVenderRespDTO> venderMap;
        if(CollUtil.isNotEmpty(simpleVenderRespDTOList)){
            venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getCode, s -> s));
        }else {
            venderMap = null;
        }
        List<QuoteitemDTO> quoteitemDTOList = QuoteItemConvert.INSTANCE.convertQuoteItemDTOListByCode(quoteItemDOList, venderMap);
        if(CollUtil.isNotEmpty(quoteitemDTOList)){
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            if(CollUtil.isNotEmpty(packageTypeList)){
                quoteitemDTOList.forEach(s->{
                    if(CollUtil.isNotEmpty(s.getPackageType())){
                        List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                        List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt->distinctPackgeType.contains(pt.getId())).toList();
                        if(CollUtil.isNotEmpty(tempPackageTypeList)){
                            List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                            s.setPackageTypeName(String.join(",",packageTypeNameList));
                            List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                            s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                        }
                    }
                });
            }
        }

        return quoteitemDTOList;
    }

    private void checkQuoteItem(Long skuId){
        List<QuoteItemDO> doList = quoteItemMapper.selectList(QuoteItemDO::getSkuId, skuId);
        if(CollUtil.isEmpty(doList)){
            throw exception(QUOTE_ITEM_NOT_EXISTS);
        }
        List<QuoteItemDO> list = doList.stream().filter(s -> Objects.equals(s.getDefaultFlag(), BooleanEnum.YES.getValue())).toList();
        if(list.isEmpty()){
            throw exception(QUOTE_ITEM_NOT_EXISTS_DEFAULT);
        }
        if(list.size()>1){
            throw exception(QUOTE_ITEM_EXISTS_MORE_DEFAULT);
        }
    }

    private List<QuoteItemDO> autoDealDO(List<QuoteItemDO> doList){
        doList.forEach(this::autoDealDO);
        return doList;
    }
    private QuoteItemDO autoDealDO(QuoteItemDO quoteItemDO){
        //计算尺柜装量
        QuoteItemDO result = SetContainerCapacity(quoteItemDO);
        //计算基础单价
        result = SetUnitPrice(result);
        return result;
    }

    //计算基础单价
    private QuoteItemDO SetUnitPrice(QuoteItemDO quoteItemDO){
        JsonAmount withTaxPrice = quoteItemDO.getWithTaxPrice();
        BigDecimal taxRate = quoteItemDO.getTaxRate();
        if(Objects.isNull(withTaxPrice) || Objects.isNull(taxRate)){
            return quoteItemDO;
        }
        BigDecimal amount = withTaxPrice.getAmount();
        String currency = withTaxPrice.getCurrency();
        if(Objects.isNull(amount)  ||  Objects.equals(amount,BigDecimal.ZERO.stripTrailingZeros()) || StrUtil.isBlank(currency)){
            return quoteItemDO;
        }
        BigDecimal unitAmount = amount.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(taxRate),2, RoundingMode.HALF_UP);
        quoteItemDO.setUnitPrice(new JsonAmount().setCurrency(currency).setAmount(unitAmount)); return quoteItemDO;
    }


    //计算尺柜装量
    private QuoteItemDO SetContainerCapacity(QuoteItemDO quoteItemDO){
        BigDecimal volume = CalcSpecificationUtil.calcSpecificationTotalVolume(quoteItemDO.getSpecificationList());
        if (Objects.isNull(volume)||BigDecimal.ZERO.compareTo(volume) == 0){
            throw exception(QUOTE_ITEM_VOLUME_ZERO);
        }
        quoteItemDO.setTwentyFootContainerCapacity(CalcContactUtil.getContainerCapacity(ContainerTypeEnum.TWENTY,volume));
        quoteItemDO.setTwentyFootContainerContainNum(CalcContactUtil.getContainerContainNum(ContainerTypeEnum.TWENTY,volume,quoteItemDO.getQtyPerOuterbox()));
        quoteItemDO.setFortyFootContainerCapacity(CalcContactUtil.getContainerCapacity(ContainerTypeEnum.FORTY,volume));
        quoteItemDO.setFortyFootContainerContainNum(CalcContactUtil.getContainerContainNum(ContainerTypeEnum.FORTY,volume,quoteItemDO.getQtyPerOuterbox()));
        quoteItemDO.setFortyFootHighContainerCapacity(CalcContactUtil.getContainerCapacity(ContainerTypeEnum.FORTY_HIGH,volume));
        quoteItemDO.setFortyFootHighContainerContainNum(CalcContactUtil.getContainerContainNum(ContainerTypeEnum.FORTY_HIGH,volume,quoteItemDO.getQtyPerOuterbox()));
        return quoteItemDO;
    }

    //批量计算尺柜装量
    private List<QuoteItemDO> SetContainerCapacity(List<QuoteItemDO> doList){
        doList.forEach(this::SetContainerCapacity);
        return doList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateQuoteItem(List<QuoteitemDTO> quoteitemList) {
        List<QuoteItemDO> quoteItemDOList = QuoteItemConvert.INSTANCE.convertQuoteItemDOByDTOList(quoteitemList);
        quoteItemMapper.updateBatch(autoDealDO(quoteItemDOList));
        List<Long> list = quoteItemDOList.stream().map(QuoteItemDO::getSkuId).distinct().toList();
        list.forEach(this::checkQuoteItem);
    }

    @Override
    public List<String> getSkuCodeListByVenderCode(String venderCode) {
        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().select(QuoteItemDO::getSkuCode).eq(QuoteItemDO::getVenderCode, venderCode));
        if (CollUtil.isEmpty(quoteItemDOList)) {
            return null;
        }
        return quoteItemDOList.stream().map(QuoteItemDO::getSkuCode).distinct().toList();
    }

//    @Override
//    public Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuCodeList(List<String> skuCodeList) {
//        List<QuoteitemDTO> quoteitemDTOList = getQuoteItemDTOListBySkuCodeList(skuCodeList);
//        if (CollUtil.isEmpty(quoteitemDTOList)) {
//            return null;
//        }
//        return quoteitemDTOList.stream().collect(Collectors.groupingBy(QuoteitemDTO::getSkuCode));
//    }




    @Override
    public Map<String, List<QuoteitemDTO>> getQuoteItemDTOMapBySkuIdList(List<Long> skuIdList) {
        List<QuoteitemDTO> quoteitemDTOList = getQuoteItemDTOListBySkuIdList(skuIdList);
        if (CollUtil.isEmpty(quoteitemDTOList)) {
            return null;
        }
        return quoteitemDTOList.stream().collect(Collectors.groupingBy(QuoteitemDTO::getSkuCode));
    }

    @Override
    public Map<String, JsonAmount> getPriceMapBySkuIdList(List<Long> skuIdList) {
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().select(QuoteItemDO::getWithTaxPrice, QuoteItemDO::getSkuCode).in(QuoteItemDO::getSkuId, skuIdList).eq(QuoteItemDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(quoteItemDOS)){
            return Map.of();
        }
        return quoteItemDOS.stream().collect(Collectors.toMap(QuoteItemDO::getSkuCode, QuoteItemDO::getWithTaxPrice));
    }

    @Override
    public List<QuoteitemDTO> getQuoteItemDTOListBySkuCodeList(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)){
            return List.of();
        }
        List<QuoteItemDO> quoteItemDOList = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().in(QuoteItemDO::getSkuCode, skuCodeList));
        if (CollUtil.isEmpty(quoteItemDOList)) {
            return null;
        }
        List<String> venderCodeList = quoteItemDOList.stream().map(QuoteItemDO::getVenderCode).distinct().toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOListByCodeList(venderCodeList);
        Map<String, SimpleVenderRespDTO> venderMap;
        if(CollUtil.isNotEmpty(simpleVenderRespDTOList)){
            venderMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getCode, s -> s));
        }else {
            venderMap = null;
        }
        List<QuoteitemDTO> quoteitemDTOList = QuoteItemConvert.INSTANCE.convertQuoteItemDTOListByCode(quoteItemDOList, venderMap);
        if(CollUtil.isNotEmpty(quoteitemDTOList)){
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            if(CollUtil.isNotEmpty(packageTypeList)){
                quoteitemDTOList.forEach(s->{
                    if(CollUtil.isNotEmpty(s.getPackageType())){
                        List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                        List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt->distinctPackgeType.contains(pt.getId())).toList();
                        if(CollUtil.isNotEmpty(tempPackageTypeList)){
                            List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                            s.setPackageTypeName(String.join(",",packageTypeNameList));
                            List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                            s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                        }
                    }
                });
            }
        }

        return quoteitemDTOList;

    }

    @Override
    public List<QuoteitemDTO> getQuoteitemDTOByVenderCodesOrPurchaseUserId(List<String> venderCodeList,Long purchaseUserId) {
        LambdaQueryWrapperX<QuoteItemDO> queryWrapperX = new LambdaQueryWrapperX<QuoteItemDO>();
        if (CollUtil.isNotEmpty(venderCodeList)){
            queryWrapperX.in(QuoteItemDO::getVenderCode,venderCodeList);
        }
        if (Objects.nonNull(purchaseUserId)){
            queryWrapperX.eq(QuoteItemDO::getPurchaseUserId, purchaseUserId);
        }
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(queryWrapperX);
        if(CollUtil.isEmpty(quoteItemDOS)){
            return null;
        }
        return BeanUtils.toBean(quoteItemDOS,QuoteitemDTO.class);

    }

    @Override
    public Map<String, BigDecimal> getTaxRateBySkuIdList(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)){
            return Map.of();
        }
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().select(QuoteItemDO::getSkuCode, QuoteItemDO::getTaxRate).in(QuoteItemDO::getSkuId, skuIds).eq(QuoteItemDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(quoteItemDOS)){
            return Map.of();
        }
        return quoteItemDOS.stream().collect(Collectors.toMap(QuoteItemDO::getSkuCode, QuoteItemDO::getTaxRate,(o,n)->o));
    }

    @Override
    public Set<String> getAvailableSkuIdSetBySkuCodeList(Collection<String> skuCodeList) {
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().select(QuoteItemDO::getSkuId, QuoteItemDO::getVenderCode,QuoteItemDO::getSkuCode).in(QuoteItemDO::getSkuCode, skuCodeList).eq(QuoteItemDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(quoteItemDOS)){
            throw exception(QUOTE_ITEM_NOT_EXISTS);
        }
        List<String> venderCodeList = quoteItemDOS.stream().map(QuoteItemDO::getVenderCode).distinct().toList();
        Set<String> availableVenderIdSet = venderService.getAvailableVenderIdSetByVenderIdList(venderCodeList);
        return quoteItemDOS.stream().filter(s-> availableVenderIdSet.contains(s.getVenderCode())).map(QuoteItemDO::getSkuCode).collect(Collectors.toSet());
    }

    @Override
    public List<QuoteitemDTO> getSameVenderQuoteList(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)){
            return List.of();
        }
        // 转换产品编号为id
        Set<SimpleSkuDTO> simpleSkuDTOS = skuApi.transformIdByCode(skuCodeList);
        if (CollUtil.isEmpty(simpleSkuDTOS)){
            return List.of();
        }
        Set<Long> skuIdSet = simpleSkuDTOS.stream().map(SimpleSkuDTO::getId).collect(Collectors.toSet());
        // 追加基础产品id
        Set<SimpleSkuDTO> baseSimpleSkuDTOSet = skuApi.transformIdByCode(simpleSkuDTOS.stream().filter(s->s.getBasicSkuCode()!=s.getCode()).map(SimpleSkuDTO::getBasicSkuCode).toList());
        if (CollUtil.isNotEmpty(baseSimpleSkuDTOSet)){
            baseSimpleSkuDTOSet.forEach(s->{
                if (!skuIdSet.contains(s.getId())){
                    simpleSkuDTOS.add(s);
                }
            });
        }
        Map<Long, String> baskuTransformMap = simpleSkuDTOS.stream().collect(Collectors.toMap(SimpleSkuDTO::getId, SimpleSkuDTO::getBasicSkuCode, (o, n) -> o));
        Set<Long> allSkuIdSet = simpleSkuDTOS.stream().map(SimpleSkuDTO::getId).collect(Collectors.toSet());
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(new LambdaQueryWrapperX<QuoteItemDO>().in(QuoteItemDO::getSkuId, allSkuIdSet));
        if (CollUtil.isEmpty(quoteItemDOS)){
            return List.of();
        }
        quoteItemDOS.forEach(s->{
            if (CollUtil.isNotEmpty(baskuTransformMap)){
                s.setBaseSkuCode(baskuTransformMap.get(s.getSkuId()));
            }
        });
        Map<String, List<QuoteItemDO>> quoteItemMap = quoteItemDOS.stream().collect(Collectors.groupingBy(QuoteItemDO::getBaseSkuCode));
        Set<String> venderCodeSet = quoteItemDOS.stream().map(QuoteItemDO::getVenderCode).collect(Collectors.toSet());
        quoteItemMap.forEach((skuCode,quoteItemDOList)->{
            // 如果供应商编号为空则不需要筛选，直接返回
            if (CollUtil.isEmpty(venderCodeSet)){
                return;
            }
            // 只要存在一个产品没有报价则清空供应商
            if (CollUtil.isEmpty(quoteItemDOList)){
                venderCodeSet.clear();
                return;
            }
            Set<String> skuVenderSet = quoteItemDOList.stream().map(QuoteItemDO::getVenderCode).collect(Collectors.toSet());
            venderCodeSet.removeIf(s->!skuVenderSet.contains(s));
        });
        if (CollUtil.isEmpty(venderCodeSet)){
            return List.of();
        }
        Map<String, List<QuoteItemDO>> filterQuoteItemMap = quoteItemDOS.stream().filter(s -> venderCodeSet.contains(s.getVenderCode())).collect(Collectors.groupingBy(QuoteItemDO::getVenderCode));
        List<QuoteItemDO> resultDO =new ArrayList<>();
        filterQuoteItemMap.forEach((venderCode,quoteItemDOList)->{
            if (CollUtil.isEmpty(quoteItemDOList)){
                return;
            }
            quoteItemDOList.stream()
                .max(Comparator.comparing((QuoteItemDO q) -> 
                    q.getQuoteDate() != null ? q.getQuoteDate() : q.getCreateTime()))
                .ifPresent(resultDO::add);
        });
        // 过滤内部供应商
        Map<String, SimpleVenderRespDTO> venderMap = venderService.getSimpleVenderDTOMapByCodes(venderCodeSet,BooleanEnum.NO.getValue());
        List<QuoteitemDTO> quoteitemDTOS = QuoteItemConvert.INSTANCE.convertQuoteItemDTOListById(resultDO, venderMap);
        if (CollUtil.isEmpty(quoteitemDTOS)){
            return List.of();
        }
        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(VenderPayment::getVenderId,quoteitemDTOS.stream().map(QuoteitemDTO::getVenderId).distinct().toList());
        if (CollUtil.isEmpty(venderPaymentList)){
            logger.error("getSameVenderQuoteList-供应商付款方式为空");
            return List.of();
        }
        Map<Long, List<VenderPayment>> venderPaymentMap = venderPaymentList.stream().collect(Collectors.groupingBy(VenderPayment::getVenderId));
        Map<Long,List<PaymentItemDTO>> paymentItemMap = new HashMap<>();
        venderPaymentMap.forEach((venderId,venderPaymentDOList)->{
            if (CollUtil.isEmpty(venderPaymentDOList)){
                return;
            }
            Map<Long, Integer> defaultFlagMap = venderPaymentDOList.stream().collect(Collectors.toMap(VenderPayment::getPaymentId, VenderPayment::getDefaultFlag, (o, n) -> o));
            List<PaymentItemDTO> paymentItemDTOList = paymentItemApi.getPaymentDTOList(new ArrayList<>(defaultFlagMap.keySet()));
            if (CollUtil.isNotEmpty(paymentItemDTOList)) {
                paymentItemDTOList.forEach(s -> {
                    s.setDefaultFlag(defaultFlagMap.get(s.getId()));
                });
            }
            paymentItemMap.put(venderId,paymentItemDTOList);
        });
        quoteitemDTOS.forEach(s->{
            if (CollUtil.isEmpty(paymentItemMap)){
                return;
            }
            s.setPaymentList(paymentItemMap.get(s.getVenderId()));
        });
        return quoteitemDTOS;
    }

    @Override
    public Set<Long> getSkuIdListByCurrencyAndAmount(String currency, BigDecimal amountMax, BigDecimal amountMin) {
        if (StrUtil.isEmpty(currency)&& Objects.isNull(amountMax)&&Objects.isNull(amountMin)){
            return Set.of();
        }

        LambdaQueryWrapperX<QuoteItemDO> queryWrapperX = new LambdaQueryWrapperX<QuoteItemDO>().eqIfPresent(QuoteItemDO::getCurrency, currency);
        if (Objects.nonNull(amountMax)){
            queryWrapperX.apply("JSON_EXTRACT(unit_price, '$.amount') <= {0}",amountMax);
        }
        if (Objects.nonNull(amountMin)){
            queryWrapperX.apply("JSON_EXTRACT(unit_price, '$.amount') >= {0}",amountMin);
        }
        List<QuoteItemDO> quoteItemDOS = quoteItemMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(quoteItemDOS)){
            return Set.of();
        }
        return quoteItemDOS.stream().map(QuoteItemDO::getSkuId).collect(Collectors.toSet());
    }

}