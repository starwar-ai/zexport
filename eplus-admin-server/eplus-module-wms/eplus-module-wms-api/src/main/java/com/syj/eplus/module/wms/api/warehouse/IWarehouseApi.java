package com.syj.eplus.module.wms.api.warehouse;

import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;

import java.util.List;
import java.util.Map;

/**
 * 仓库资料 API 接口
 * Create by Rangers at  2024-06-04 15:43
 */
public interface IWarehouseApi {


    /**
     * 供应商审核通过后，自动创建供应商仓库
     * @return
     */
    Boolean createVenderWareHouse(WarehouseDTO warehouseDTO);

    /**
     * 根据供应商编号删除所有虚拟仓库
     * @param venderCode
     */
    void deleteWarehouseByVenderCode(String venderCode);

    /**
     * 根据仓库主键查询仓库信息（批量）
     * @param ids
     * @return
     */
    List<WarehouseDTO> selectBatchIds(List<Long> ids);

    /**
     * 根据供应商编码+供应仓类型查询仓库信息
     * @param venderCode
     * @param wareHouseName
     * @return
     */
    WarehouseDTO getWarehouse(String venderCode,String wareHouseName);

    /**
     * 根据供应仓标识（0-否 1-是）、供应商编码查询仓库
     * @param venderFlag
     * @param venderCode
     * @return
     */
    List<WarehouseDTO> listWarehouse(Integer venderFlag,String venderCode);

    /**
     * 获取仓库名称缓存
     * @return 仓库名称缓存
     */
    Map<Long,String> getWarehoseCache();

    WarehouseDTO getDefaultWareMouse();

}
