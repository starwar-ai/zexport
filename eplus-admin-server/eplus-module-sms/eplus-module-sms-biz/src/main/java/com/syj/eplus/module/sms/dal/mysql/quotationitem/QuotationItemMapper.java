package com.syj.eplus.module.sms.dal.mysql.quotationitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.QuotationItemPageReqVO;
import com.syj.eplus.module.sms.dal.dataobject.quotationitem.QuotationItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报价单子 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface QuotationItemMapper extends BaseMapperX<QuotationItemDO> {

    default Map<Long, List<QuotationItemDO>> getQuotationItemDOMapByQuotationIdList(List<Long> quotationIdList) {
        LambdaQueryWrapperX<QuotationItemDO> queryWrapperX = new LambdaQueryWrapperX<QuotationItemDO>().in(QuotationItemDO::getSmsQuotationId, quotationIdList);
        List<QuotationItemDO> quotationItemDOList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(quotationItemDOList)) {
            return null;
        }
        return quotationItemDOList.stream().collect(Collectors.groupingBy(QuotationItemDO::getSmsQuotationId));
    }

    default PageResult<QuotationItemDO> selectPage(QuotationItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuotationItemDO>()
                .likeIfPresent(QuotationItemDO::getSkuCode, reqVO.getSkuCode())
                .eq(QuotationItemDO::getCustProFlag, reqVO.getCustProFlag())
                .in(QuotationItemDO::getSmsQuotationId, reqVO.getSmsQuotationIdList())
                .orderByDesc(QuotationItemDO::getId));
    }
}