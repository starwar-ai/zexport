package com.syj.eplus.module.wms.api.warehouse;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.syj.eplus.module.wms.service.warehouse.WarehouseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Desc——仓库资料 API 实现类
 * Create by Rangers at  2024-06-04 15:46
 */
@Service
public class WarehouseApiImpl implements IWarehouseApi{


    @Resource
    private WarehouseService warehouseService;

    @Override
    public Boolean createVenderWareHouse(WarehouseDTO warehouseDTO) {
        WarehouseSaveReqVO saveReqVO = BeanUtils.toBean(warehouseDTO, WarehouseSaveReqVO.class);
        // 已存在供应商编码则更新
        WarehouseDTO warehouse = this.getWarehouse(warehouseDTO.getVenderCode(), "");
        if (ObjectUtil.isNotNull(warehouse)){
            WarehouseSaveReqVO updateReqVO = BeanUtils.toBean(warehouseDTO, WarehouseSaveReqVO.class);
            updateReqVO.setId(warehouse.getId());
            return warehouseService.updateWarehouse(updateReqVO) > 0;
        }
        return warehouseService.createWarehouse(saveReqVO) != -1;
    }

    @Override
    public void deleteWarehouseByVenderCode(String venderCode) {
        warehouseService.deleteWarehouseByVenderCode(venderCode);
    }

    @Override
    public List<WarehouseDTO> selectBatchIds(List<Long> ids) {
        List<WarehouseDO> warehouseDOList = warehouseService.listByIds(ids);
        return BeanUtils.toBean(warehouseDOList, WarehouseDTO.class);
    }

    @Override
    public WarehouseDTO getWarehouse(String venderCode,String wareHouseName) {
        LambdaQueryWrapper<WarehouseDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(venderCode)) {
            queryWrapper.eq(WarehouseDO::getVenderCode,venderCode);
        }
        if (StringUtils.isNotBlank(wareHouseName)) {
            queryWrapper.like(WarehouseDO::getName,wareHouseName);
        }
        WarehouseDO warehouseDO = warehouseService.getOne(queryWrapper);
        return BeanUtils.toBean(warehouseDO, WarehouseDTO.class);
    }

    @Override
    public List<WarehouseDTO> listWarehouse(Integer venderFlag,String venderCode) {
        LambdaQueryWrapper<WarehouseDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseDO::getVenderFlag,venderFlag);
        if (StringUtils.isNotBlank(venderCode)) {
            queryWrapper.eq(WarehouseDO::getVenderCode,venderCode);
        }
        List<WarehouseDO> list = warehouseService.list(queryWrapper);
        return BeanUtils.toBean(list, WarehouseDTO.class);
    }

    @Override
    public Map<Long, String> getWarehoseCache() {
        return warehouseService.getWarehoseCache();
    }

    @Override
    public WarehouseDTO getDefaultWareMouse() {
        return warehouseService.getDefaultWareMouse();
    }
}
