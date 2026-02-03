package com.syj.eplus.module.mms.service.manufactureskuitem;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemRespVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 加工单子产品 Service 接口
 *
 * @author zhangcm
 */
public interface ManufactureSkuItemService {

    /**
     * 创建加工单子产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createManufactureSkuItem(@Valid ManufactureSkuItemSaveReqVO createReqVO);

    /**
     * 更新加工单子产品
     *
     * @param updateReqVO 更新信息
     */
    void updateManufactureSkuItem(@Valid ManufactureSkuItemSaveReqVO updateReqVO);

    /**
     * 删除加工单子产品
     *
     * @param id 编号
     */
    void deleteManufactureSkuItem(Long id);

    /**
     * 获得加工单子产品
     *
* @param  id 编号 
     * @return 加工单子产品
     */
ManufactureSkuItemRespVO getManufactureSkuItem(Long id);

    /**
     * 获得加工单子产品分页
     *
     * @param pageReqVO 分页查询
     * @return 加工单子产品分页
     */
    PageResult<ManufactureSkuItemDO> getManufactureSkuItemPage(ManufactureSkuItemPageReqVO pageReqVO);

    void createBatch(List<ManufactureSkuDO> children, Long manufactureId);



    void deleteByManufactureId(Long manufactureId);

    List<ManufactureSkuItemDO> selectListByManufactureId(Long manufactureId);

    List<ManufactureSkuItemDO> selectListByManufactureIds(List<Long> manufactureIdList);

}