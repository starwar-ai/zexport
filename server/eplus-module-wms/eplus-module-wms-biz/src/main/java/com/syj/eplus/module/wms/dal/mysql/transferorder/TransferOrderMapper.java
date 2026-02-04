package com.syj.eplus.module.wms.dal.mysql.transferorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.transferorder.TransferOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调拨 Mapper
 *
 * @author du
 */
@Mapper
public interface TransferOrderMapper extends BaseMapperX<TransferOrderDO> {

    default PageResult<TransferOrderDO> selectPage(TransferOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TransferOrderDO>()
                .eqIfPresent(TransferOrderDO::getCode, reqVO.getCode())
                .eqIfPresent(TransferOrderDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(TransferOrderDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(TransferOrderDO::getTransferType, reqVO.getTransferType())
                .eqIfPresent(TransferOrderDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(TransferOrderDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(TransferOrderDO::getCustName, reqVO.getCustName())
                .eqIfPresent(TransferOrderDO::getInputUser, reqVO.getInputUser())
                .betweenIfPresent(TransferOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TransferOrderDO::getId));
    }

}