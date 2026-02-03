package com.syj.eplus.module.wms.dal.mysql.stockimport;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stockimport.vo.StockImportPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-库存明细导入 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface StockImportMapper extends BaseMapperX<StockImportDO> {

    default PageResult<StockImportDO> selectPage(StockImportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockImportDO>()
                .eqIfPresent(StockImportDO::getSplitBoxFlag, reqVO.getSplitBoxFlag())
                .eqIfPresent(StockImportDO::getSpecificationList, reqVO.getSpecificationList())
                .eqIfPresent(StockImportDO::getCabinetQuantity, reqVO.getCabinetQuantity())
                .eqIfPresent(StockImportDO::getPurchaseUser, reqVO.getPurchaseUser())
                .eqIfPresent(StockImportDO::getBasicSkuCode, reqVO.getBasicSkuCode())
                .eqIfPresent(StockImportDO::getDiffQuantity, reqVO.getDiffQuantity())
                .eqIfPresent(StockImportDO::getRemainTotalAmount, reqVO.getRemainTotalAmount())
                .eqIfPresent(StockImportDO::getProducingQuantity, reqVO.getProducingQuantity())
                .eqIfPresent(StockImportDO::getBillId, reqVO.getBillId())
                .eqIfPresent(StockImportDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(StockImportDO::getBillItemId, reqVO.getBillItemId())
                .eqIfPresent(StockImportDO::getSkuId, reqVO.getSkuId())
                .eqIfPresent(StockImportDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(StockImportDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StockImportDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(StockImportDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(StockImportDO::getCustProFlag, reqVO.getCustProFlag())
                .betweenIfPresent(StockImportDO::getReceiptTime, reqVO.getReceiptTime())
                .eqIfPresent(StockImportDO::getInitQuantity, reqVO.getInitQuantity())
                .eqIfPresent(StockImportDO::getUsedQuantity, reqVO.getUsedQuantity())
                .eqIfPresent(StockImportDO::getLockQuantity, reqVO.getLockQuantity())
                .eqIfPresent(StockImportDO::getAvailableQuantity, reqVO.getAvailableQuantity())
                .eqIfPresent(StockImportDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(StockImportDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(StockImportDO::getPrice, reqVO.getPrice())
                .eqIfPresent(StockImportDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(StockImportDO::getPurchaseContractId, reqVO.getPurchaseContractId())
                .eqIfPresent(StockImportDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(StockImportDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(StockImportDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(StockImportDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(StockImportDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(StockImportDO::getPosition, reqVO.getPosition())
                .eqIfPresent(StockImportDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(StockImportDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(StockImportDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(StockImportDO::getCustId, reqVO.getCustId())
                .eqIfPresent(StockImportDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(StockImportDO::getCustName, reqVO.getCustName())
                .eqIfPresent(StockImportDO::getCustPo, reqVO.getCustPo())
                .eqIfPresent(StockImportDO::getOuterboxLength, reqVO.getOuterboxLength())
                .eqIfPresent(StockImportDO::getOuterboxWidth, reqVO.getOuterboxWidth())
                .eqIfPresent(StockImportDO::getOuterboxHeight, reqVO.getOuterboxHeight())
                .eqIfPresent(StockImportDO::getTotalVolume, reqVO.getTotalVolume())
                .eqIfPresent(StockImportDO::getTotalWeight, reqVO.getTotalWeight())
                .eqIfPresent(StockImportDO::getPalletVolume, reqVO.getPalletVolume())
                .eqIfPresent(StockImportDO::getPalletWeight, reqVO.getPalletWeight())
                .eqIfPresent(StockImportDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(StockImportDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(StockImportDO::getRemark, reqVO.getRemark())
                .eqIfPresent(StockImportDO::getErrorRemark, reqVO.getErrorRemark())
                .eqIfPresent(StockImportDO::getErrorFlag, reqVO.getErrorFlag())
                .eqIfPresent(StockImportDO::getImportCode, reqVO.getImportCode())
                .betweenIfPresent(StockImportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockImportDO::getId));
    }

}