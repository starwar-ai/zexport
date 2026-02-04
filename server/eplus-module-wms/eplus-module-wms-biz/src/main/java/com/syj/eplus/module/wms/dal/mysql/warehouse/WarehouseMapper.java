package com.syj.eplus.module.wms.dal.mysql.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehousePageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库管理-仓库 Mapper
 *
 * @author Rangers
 */
@Mapper
public interface WarehouseMapper extends BaseMapperX<WarehouseDO> {

    default PageResult<WarehouseDO> selectPage(WarehousePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WarehouseDO>()
                .eqIfPresent(WarehouseDO::getCode, reqVO.getCode())
                .likeIfPresent(WarehouseDO::getName, reqVO.getName())
                .eqIfPresent(WarehouseDO::getAddress, reqVO.getAddress())
                .eqIfPresent(WarehouseDO::getEnableFlag, reqVO.getEnableFlag())
                .inIfPresent(WarehouseDO::getManagerIds, reqVO.getManagerId())
                .eqIfPresent(WarehouseDO::getVenderFlag, reqVO.getVenderFlag())
                .eqIfPresent(WarehouseDO::getVenderCode, reqVO.getVenderId())
                .likeIfPresent(WarehouseDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(WarehouseDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(WarehouseDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WarehouseDO::getId));
    }

}
