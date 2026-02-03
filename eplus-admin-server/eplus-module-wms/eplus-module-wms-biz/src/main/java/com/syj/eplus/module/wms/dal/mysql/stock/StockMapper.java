package com.syj.eplus.module.wms.dal.mysql.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.syj.eplus.module.wms.controller.admin.stock.vo.StockPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.StockQueryVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.StockRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Objects;

/**
 * 仓储管理-库存明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StockMapper extends BaseMapperX<StockDO> {

    default PageResult<StockDO> selectPage(StockPageReqVO reqVO) {
        LambdaQueryWrapperX<StockDO> stockDOLambdaQueryWrapperX = new LambdaQueryWrapperX<StockDO>()
                .likeIfPresent(StockDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StockDO::getSkuCode, reqVO.getSkuCode())
                .inIfPresent(StockDO::getWarehouseId, reqVO.getWarehouseIdSet())
                .eqIfPresent(StockDO::getCskuCode, reqVO.getCskuCode())
                .likeIfPresent(StockDO::getBatchCode, reqVO.getBatchCode())
                .likeIfPresent(StockDO::getOskuCode, reqVO.getOskuCode())
                .eqIfPresent(StockDO::getBasicSkuCode, reqVO.getBasicSkuCode())
                .eqIfPresent(StockDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(StockDO::getCustProFlag, reqVO.getCustProFlag())
                .likeIfPresent(StockDO::getCustName, reqVO.getCustName())
                .eqIfPresent(StockDO::getCustPo, reqVO.getCustPo())
                .likeIfPresent(StockDO::getVenderName, reqVO.getVenderName())
                .likeIfPresent(StockDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .likeIfPresent(StockDO::getSaleContractCode, reqVO.getSaleContractCode())
                .betweenIfPresent(StockDO::getReceiptTime, reqVO.getReceiptTime())
                .orderByDesc(StockDO::getId);
        if(Objects.nonNull(reqVO.getPurchaseUserId())){
            stockDOLambdaQueryWrapperX.apply("JSON_EXTRACT(purchase_user, '$.userId') = {0}", reqVO.getPurchaseUserId());
        }
        if(Objects.nonNull(reqVO.getPurchaseUserDeptId())){
            stockDOLambdaQueryWrapperX.apply("JSON_EXTRACT(purchase_user, '$.deptId') = {0}", reqVO.getPurchaseUserDeptId());
        }

        return selectPage(reqVO,stockDOLambdaQueryWrapperX);
    }

    default PageResult<StockDO> listDetail(StockQueryVO reqVO) {
        LambdaQueryWrapperX<StockDO> queryWrapperX = new LambdaQueryWrapperX<StockDO>()
                .eqIfPresent(StockDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(StockDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(StockDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(StockDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(StockDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(StockDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(StockDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(StockDO::getSaleContractCode, reqVO.getSaleContractCode())
                .likeIfPresent(StockDO::getCustName, reqVO.getCustName())
                .eqIfPresent(StockDO::getCustPo, reqVO.getCustPo())
                .likeIfPresent(StockDO::getVenderName, reqVO.getVenderName())
                .likeIfPresent(StockDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StockDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(StockDO::getBasicSkuCode, reqVO.getBasicSkuCode())
                .eqIfPresent(StockDO::getCskuCode, reqVO.getCskuCode())
                .likeIfPresent(StockDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StockDO::getCustCode, reqVO.getCustCode())
                .eqIfPresent(StockDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(StockDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .betweenIfPresent(StockDO::getReceiptTime, reqVO.getReceiptTime())
                .orderByDesc(StockDO::getId);
        if (Objects.nonNull(reqVO.getRemainderQuantityMax())){
            queryWrapperX.apply("(init_quantity-used_quantity) <={0}",reqVO.getRemainderQuantityMax());
        }
        if (Objects.nonNull(reqVO.getRemainderQuantityMin())){
            queryWrapperX.apply("(init_quantity-used_quantity) >={0}",reqVO.getRemainderQuantityMin());
        }
        return selectPage(reqVO,queryWrapperX);
    }

    IPage<StockRespVO> listPage(IPage<StockRespVO> page,@Param("reqVO")StockPageReqVO pageReqVO);

    StockRespVO getStock(@Param("skuId") Long skuId);
}
