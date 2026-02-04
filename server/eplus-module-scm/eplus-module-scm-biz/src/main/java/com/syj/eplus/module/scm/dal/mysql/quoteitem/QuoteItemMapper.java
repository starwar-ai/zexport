package com.syj.eplus.module.scm.dal.mysql.quoteitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.quoteitem.vo.QuoteItemPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.quoteitem.QuoteItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商报价明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QuoteItemMapper extends BaseMapperX<QuoteItemDO> {

    default PageResult<QuoteItemDO> selectPage(QuoteItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuoteItemDO>()
                .eqIfPresent(QuoteItemDO::getQuoteId, reqVO.getQuoteId())
                .eqIfPresent(QuoteItemDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(QuoteItemDO::getVenderCode, reqVO.getVenderCode())
                .eqIfPresent(QuoteItemDO::getPurchaseUserId, reqVO.getPurchaseUserId())
                .likeIfPresent(QuoteItemDO::getPurchaseUserName, reqVO.getPurchaseUserName())
                .eqIfPresent(QuoteItemDO::getPurchaseUserDeptId, reqVO.getPurchaseUserDeptId())
                .likeIfPresent(QuoteItemDO::getPurchaseUserDeptName, reqVO.getPurchaseUserDeptName())
                .eqIfPresent(QuoteItemDO::getVenderProdCode, reqVO.getVenderProdCode())
                .betweenIfPresent(QuoteItemDO::getQuoteDate, reqVO.getQuoteDate())
                .eqIfPresent(QuoteItemDO::getFreightFlag, reqVO.getFreightFlag())
                .eqIfPresent(QuoteItemDO::getPackageFlag, reqVO.getPackageFlag())
                .eqIfPresent(QuoteItemDO::getPackageType, reqVO.getPackageType())
                .eqIfPresent(QuoteItemDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(QuoteItemDO::getFaxFlag, reqVO.getFaxFlag())
                .eqIfPresent(QuoteItemDO::getTaxRate, reqVO.getTaxRate())
                .eqIfPresent(QuoteItemDO::getMoq, reqVO.getMoq())
                .eqIfPresent(QuoteItemDO::getPackagingPrice, reqVO.getPackagingPrice())
                .eqIfPresent(QuoteItemDO::getShippingPrice, reqVO.getShippingPrice())
                .eqIfPresent(QuoteItemDO::getUnitPrice, reqVO.getUnitPrice())
                .eqIfPresent(QuoteItemDO::getTotalPrice, reqVO.getTotalPrice())
                .eqIfPresent(QuoteItemDO::getWithTaxPrice, reqVO.getWithTaxPrice())
                .eqIfPresent(QuoteItemDO::getDelivery, reqVO.getDelivery())
                .eqIfPresent(QuoteItemDO::getPurchaseUrl, reqVO.getPurchaseUrl())
                .eqIfPresent(QuoteItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(QuoteItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(QuoteItemDO::getPackageLength, reqVO.getPackageLength())
                .eqIfPresent(QuoteItemDO::getPackageWidth, reqVO.getPackageWidth())
                .eqIfPresent(QuoteItemDO::getPackageHeight, reqVO.getPackageHeight())
                .eqIfPresent(QuoteItemDO::getDefaultFlag, reqVO.getDefaultFlag())
                .eqIfPresent(QuoteItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(QuoteItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(QuoteItemDO::getId));
    }

}