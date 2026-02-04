package com.syj.eplus.module.wms.convert.warehouse;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseRespVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Mapper
public interface WarehouseConvert {

    WarehouseConvert INSTANCE = Mappers.getMapper(WarehouseConvert.class);

    WarehouseRespVO convert(WarehouseDO warehouseDO);

    default WarehouseRespVO convertWarehouseRespVO(WarehouseDO warehouseDO, Map<Long, UserDept> userDeptMap) {
        WarehouseRespVO warehouseRespVO = convert(warehouseDO);
        if (CollUtil.isNotEmpty(userDeptMap) && warehouseDO.getManagerIds() != null && !warehouseDO.getManagerIds().isEmpty()) {
            // 处理多个 managerId，用逗号拼接用户名称
            String managerNames = warehouseDO.getManagerIds().stream()
                    .map(userDeptMap::get)
                    .filter(Objects::nonNull)
                    .map(UserDept::getNickname)
                    .collect(Collectors.joining(","));
            warehouseRespVO.setManagerName(managerNames);
        } else {
            warehouseRespVO.setManagerName("");
        }
        return warehouseRespVO;
    }

    WarehouseDO convertWarehouseDO(WarehouseSaveReqVO saveReqVO);
}
