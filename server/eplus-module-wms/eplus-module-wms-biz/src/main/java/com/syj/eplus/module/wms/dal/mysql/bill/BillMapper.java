package com.syj.eplus.module.wms.dal.mysql.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-入(出)库单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface BillMapper extends BaseMapperX<BillDO> {

    default PageResult<BillDO> selectPage(BillPageReqVO reqVO) {
        LambdaQueryWrapperX<BillDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<BillDO>()
                .eqIfPresent(BillDO::getCode, reqVO.getCode())
                .eqIfPresent(BillDO::getBillType, reqVO.getBillType())
                .eqIfPresent(BillDO::getBillStatus, reqVO.getBillStatus())
                .eqIfPresent(BillDO::getNoticeCode, reqVO.getNoticeCode())
                .eqIfPresent(BillDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(BillDO::getSaleContractCode, reqVO.getSaleContractCode())
                .betweenIfPresent(BillDO::getBillTime, reqVO.getBillTime())
                .eqIfPresent(BillDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(BillDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(BillDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(BillDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(BillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(BillDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(BillDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(BillDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(BillDO::getId, reqVO.getIdList())
                .orderByDesc(BillDO::getId);
        if (StringUtils.isNotBlank(reqVO.getSkuCode())) {
            String condition = "FIND_IN_SET('" + reqVO.getSkuCode() + "', sku_codes)";
            lambdaQueryWrapperX.apply( condition);
        }
        return selectPage(reqVO, lambdaQueryWrapperX);
    }

}
