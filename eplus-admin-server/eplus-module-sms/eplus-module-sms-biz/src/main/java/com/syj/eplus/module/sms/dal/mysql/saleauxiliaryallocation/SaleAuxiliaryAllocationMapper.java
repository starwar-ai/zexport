package com.syj.eplus.module.sms.dal.mysql.saleauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.SaleAuxiliaryAllocationPageReqVO;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售合同辅料分摊 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface SaleAuxiliaryAllocationMapper extends BaseMapperX<SaleAuxiliaryAllocationDO> {

    default PageResult<SaleAuxiliaryAllocationDO> selectPage(SaleAuxiliaryAllocationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SaleAuxiliaryAllocationDO>()
                .eqIfPresent(SaleAuxiliaryAllocationDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getSaleContractItemId, reqVO.getSaleContractItemId())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(SaleAuxiliaryAllocationDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAuxiliaryPurchaseContractId, reqVO.getAuxiliaryPurchaseContractId())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAuxiliaryPurchaseContractCode, reqVO.getAuxiliaryPurchaseContractCode())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId, reqVO.getAuxiliaryPurchaseContractItemId())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAuxiliarySkuCode, reqVO.getAuxiliarySkuCode())
                .likeIfPresent(SaleAuxiliaryAllocationDO::getAuxiliarySkuName, reqVO.getAuxiliarySkuName())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAllocationAmount, reqVO.getAllocationAmount())
                .betweenIfPresent(SaleAuxiliaryAllocationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SaleAuxiliaryAllocationDO::getId));
    }

}