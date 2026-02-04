package com.syj.eplus.module.scm.dal.mysql.purchaseauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购合同辅料分摊 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PurchaseAuxiliaryAllocationMapper extends BaseMapperX<PurchaseAuxiliaryAllocationDO> {

    default PageResult<PurchaseAuxiliaryAllocationDO> selectPage(PurchaseAuxiliaryAllocationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchaseAuxiliaryAllocationDO>()
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getPurchaseContractId, reqVO.getPurchaseContractId())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getPurchaseContractItemId, reqVO.getPurchaseContractItemId())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(PurchaseAuxiliaryAllocationDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractId, reqVO.getAuxiliaryPurchaseContractId())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractCode, reqVO.getAuxiliaryPurchaseContractCode())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId, reqVO.getAuxiliaryPurchaseContractItemId())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getAuxiliarySkuCode, reqVO.getAuxiliarySkuCode())
                .likeIfPresent(PurchaseAuxiliaryAllocationDO::getAuxiliarySkuName, reqVO.getAuxiliarySkuName())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(PurchaseAuxiliaryAllocationDO::getAllocationAmount, reqVO.getAllocationAmount())
                .betweenIfPresent(PurchaseAuxiliaryAllocationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PurchaseAuxiliaryAllocationDO::getId));
    }

}