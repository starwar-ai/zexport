package com.syj.eplus.module.crm.dal.mysql.custsettlement;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custsettlement.CustSettlementDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户结汇方式 Mapper
 *
 * @author eplus
 */
@Mapper
public interface CustSettlementMapper extends BaseMapperX<CustSettlementDO> {

    default PageResult<CustSettlementDO> selectPage(CustSettlementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CustSettlementDO>()
                .eqIfPresent(CustSettlementDO::getSettlementId, reqVO.getSettlementId())
                .eqIfPresent(CustSettlementDO::getCustId, reqVO.getCustId())
                .eqIfPresent(CustSettlementDO::getDefaultFlag, reqVO.getDefaultFlag())
                .betweenIfPresent(CustSettlementDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CustSettlementDO::getId));
    }

}