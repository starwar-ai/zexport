package com.syj.eplus.module.dms.dal.mysql.settlementform;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.settlementform.SettlementFormDO;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 结汇单 Mapper
 *
 * @author du
 */
@Mapper
public interface SettlementFormMapper extends BaseMapperX<SettlementFormDO> {

    default PageResult<SettlementFormDO> selectPage(SettlementFormPageReqVO reqVO) {
        MPJLambdaWrapperX<SettlementFormDO> queryWrapperX = new MPJLambdaWrapperX<SettlementFormDO>()
                .selectAll(SettlementFormDO.class)
                .likeIfPresent(SettlementFormDO::getInvoiceCode, reqVO.getInvoiceCode())
                .eqIfPresent(SettlementFormDO::getShipmentCode, reqVO.getShipmentCode())
                .likeIfPresent(SettlementFormDO::getCode, reqVO.getCode())
                .betweenIfPresent(SettlementFormDO::getCreateTime, reqVO.getCreateTime())
                .innerJoin(SettlementFormItem.class, SettlementFormItem::getSettlementFormId, SettlementFormDO::getId);

        if (StrUtil.isNotEmpty(reqVO.getSaleContractCode())) {
            queryWrapperX.like(SettlementFormItem::getSaleContractCode, reqVO.getSaleContractCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())) {
            queryWrapperX.like(SettlementFormItem::getCustName, reqVO.getCustName());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustPo())) {
            queryWrapperX.like(SettlementFormItem::getCustPo, reqVO.getCustPo());
        }
        queryWrapperX.groupBy(SettlementFormDO::getId)
                .orderByDesc(SettlementFormDO::getId);
        
        return selectJoinPage(reqVO, SettlementFormDO.class, queryWrapperX);
    }

}