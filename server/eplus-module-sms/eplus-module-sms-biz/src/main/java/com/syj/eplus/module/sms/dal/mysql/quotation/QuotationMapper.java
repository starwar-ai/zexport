package com.syj.eplus.module.sms.dal.mysql.quotation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationPageReqVO;
import com.syj.eplus.module.sms.dal.dataobject.quotation.QuotationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报价单主 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QuotationMapper extends BaseMapperX<QuotationDO> {

    default PageResult<QuotationDO> selectPage(QuotationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuotationDO>()
                .eqIfPresent(QuotationDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(QuotationDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(QuotationDO::getCustId, reqVO.getCustId())
                .eqIfPresent(QuotationDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(QuotationDO::getCustName, reqVO.getCustName())
                .eqIfPresent(QuotationDO::getIsNewCust, reqVO.getIsNewCust())
                .eqIfPresent(QuotationDO::getSettlementTermType, reqVO.getSettlementTermType())
                .eqIfPresent(QuotationDO::getCountryId, reqVO.getCountryId())
                .eqIfPresent(QuotationDO::getDeparturePortId, reqVO.getDeparturePortId())
                .likeIfPresent(QuotationDO::getDeparturePortName, reqVO.getDeparturePortName())
                .eqIfPresent(QuotationDO::getValidPeriod, reqVO.getValidPeriod())
                .eqIfPresent(QuotationDO::getManager, reqVO.getManager())
                .eqIfPresent(QuotationDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(QuotationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(QuotationDO::getCode, reqVO.getCode())
                .betweenIfPresent(QuotationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(QuotationDO::getId));
    }

}