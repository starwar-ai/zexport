package com.syj.eplus.module.wms.service.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehousePageReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseRespVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSimpleRespVO;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 仓库管理-仓库 Service 接口
 *
 * @author Rangers
 */
public interface WarehouseService extends IService<WarehouseDO> {

    /**
     * 创建仓库管理-仓库
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWarehouse(@Valid WarehouseSaveReqVO createReqVO);

    /**
     * 更新仓库管理-仓库
     *
     * @param updateReqVO 更新信息
     */
    Integer updateWarehouse(@Valid WarehouseSaveReqVO updateReqVO);

    /**
     * 启用
     * @param id
     */
    void enable(Long id);

    /**
     * 停用
     * @param id
     */
    void disable(Long id);

    /**
     * 根据供应商编号删除所有的虚拟仓库
     * @param venderCode
     */
    void deleteWarehouseByVenderCode(String venderCode);

    /**
     * 删除仓库管理-仓库
     *
     * @param id 编号
     */
    void deleteWarehouse(Long id);

    /**
     * 获得仓库管理-仓库
     *
     * @param id 编号
     * @return 仓库管理-仓库
     */
    WarehouseRespVO getWarehouse(Long id);

    /**
     * 获得仓库管理-仓库分页
     *
     * @param pageReqVO 分页查询
     * @return 仓库管理-仓库分页
     */
    PageResult<WarehouseRespVO> getWarehousePage(WarehousePageReqVO pageReqVO);
    /**
     * 获得仓库管理-精简列表
     * @return 仓库管理-仓库分页
     */
    List<WarehouseSimpleRespVO> getSimpleListWarehouse();

    Map<Long,WarehouseDO> getWarehouseList(List<Long> wareHouseIdList);

    /**
     * 获取仓库名称缓存
     * @return 仓库名称缓存
     */
    Map<Long,String> getWarehoseCache();

    WarehouseDTO getDefaultWareMouse();

    void setDefault(Long id);
}
