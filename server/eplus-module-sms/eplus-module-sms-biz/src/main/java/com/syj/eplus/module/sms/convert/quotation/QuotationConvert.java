package com.syj.eplus.module.sms.convert.quotation;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationItemExportVO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationItemRespVO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationRespVO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.quotation.QuotationDO;
import com.syj.eplus.module.sms.dal.dataobject.quotationitem.QuotationItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuotationConvert {

        QuotationConvert INSTANCE = Mappers.getMapper(QuotationConvert.class);

        QuotationRespVO convert(QuotationDO quotationDO);

        default QuotationRespVO convertQuotationRespVO(QuotationDO quotationDO){
                QuotationRespVO declarationRespVO = convert(quotationDO);
                return declarationRespVO;
        }

    default List<QuotationRespVO> convertQuotationRespVO (List<QuotationDO> quotationDOList, Map<Long, List<QuotationItemDO>> quotationItemDOMap,Map<Long, UserDept> userDeptCache) {
                return CollectionUtils.convertList(quotationDOList, quotationDO -> {
                        Long  declarationId = quotationDO.getId();
                        QuotationRespVO quotationRespVO = convert(quotationDO);
                        if (CollUtil.isNotEmpty(userDeptCache)){
                                quotationRespVO.setCreateUser(userDeptCache.get(Long.parseLong(quotationDO.getCreator())));
                        }
                        if (CollUtil.isNotEmpty(quotationItemDOMap)) {
                                List<QuotationItemDO> quotationItemDOList = quotationItemDOMap.get(declarationId);
                                if (CollUtil.isNotEmpty(quotationItemDOList)) {
                                        quotationRespVO.setChildren(quotationItemDOList);
                                }
                        }
                        return quotationRespVO;
                });
        }

        QuotationDO convertQuotationDO(QuotationSaveReqVO saveReqVO);

        QuotationItemExportVO convertQuotationItemExportVO(QuotationItemDO quotationItemDO);
        default List<QuotationItemExportVO> convertQuotationItemExportVO(List<QuotationItemDO> quotationItemDOList) {
                return CollectionUtils.convertList(quotationItemDOList, s -> {
                        QuotationItemExportVO quotationItemExportVO = convertQuotationItemExportVO(s);
                        if (CollUtil.isNotEmpty(s.getSpecificationList())) {
                                quotationItemExportVO.setOuterboxNetweight(NumberFormatUtil.format(s.getSpecificationList().get(0).getOuterboxNetweight().getWeight(),CalculationDict.TWO).toPlainString());
                                quotationItemExportVO.setOuterboxGrossweight(NumberFormatUtil.format(s.getSpecificationList().get(0).getOuterboxGrossweight().getWeight(),CalculationDict.TWO).toPlainString());
                                quotationItemExportVO.setOuterboxLength(NumberFormatUtil.format(s.getSpecificationList().get(0).getOuterboxLength(),CalculationDict.TWO).toPlainString());
                                quotationItemExportVO.setOuterboxWidth(NumberFormatUtil.format(s.getSpecificationList().get(0).getOuterboxWidth(), CalculationDict.TWO).toPlainString());
                                quotationItemExportVO.setOuterboxHeight(NumberFormatUtil.format(s.getSpecificationList().get(0).getOuterboxHeight(),CalculationDict.TWO).toPlainString());
                                quotationItemExportVO.setOuterboxVolume(VolumeUtil.processVolume(s.getSpecificationList().get(0).getOuterboxVolume()).toPlainString());
                                quotationItemExportVO.setTotalPurchase(NumUtil.mul(s.getWithTaxPrice().getAmount(), s.getMoq()).toPlainString());
                        }
                        return quotationItemExportVO;
                });
        }

        QuotationItemRespVO convertQuotationItemRespVO(QuotationItemDO quotationItemDO);
        default List<QuotationItemRespVO> convertQuotationItemRespVOList(List<QuotationItemDO> quotationItemDOList,Map<Long,QuotationDO> quotationDOMap,Map<String, String> custNameCache){
                return CollectionUtils.convertList(quotationItemDOList, s -> {
                        QuotationItemRespVO quotationItemRespVO = convertQuotationItemRespVO(s);
                        QuotationDO quotationDO = quotationDOMap.get(s.getSmsQuotationId());
                        if (quotationDO != null) {
                                quotationItemRespVO.setCustName(custNameCache.get(quotationDO.getCustCode()));
                                quotationItemRespVO.setCurrency(quotationDO.getCurrency());
                                quotationItemRespVO.setSettlementTermType(quotationDO.getSettlementTermType());
                        }
                        return quotationItemRespVO;
                });
        }
}