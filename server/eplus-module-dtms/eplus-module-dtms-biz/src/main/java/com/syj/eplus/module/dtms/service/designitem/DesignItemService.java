package com.syj.eplus.module.dtms.service.designitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemRespVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemSaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 设计-认领明细 Service 接口
 *
 * @author ePlus
 */
public interface DesignItemService extends IService<DesignItemDO> {

    /**
     * 创建设计-认领明细
     *
     * @param createReqVOList 创建信息
     * @return 编号
     */
    Boolean createDesignItem(@Valid List<DesignItemSaveReqVO> createReqVOList);

    /**
     * 更新设计-认领明细
     *
     * @param updateReqVOList 更新信息
     */
    Boolean evaluate(@Valid List<DesignItemSaveReqVO> updateReqVOList);

    /**
     * 更新设计-认领人
     *
     * @param updateReqVO 更新信息
     */
    void updateDesigner(@Valid DesignItemSaveReqVO updateReqVO);

    /**
     * 删除设计-认领明细
     *
     * @param id 编号
     */
    void deleteDesignItem(Long id);

    /**
     * 获得设计-认领明细
     *
     * @param id 编号
     * @return 设计-认领明细
     */
    DesignItemRespVO getDesignItem(Long id);

    /**
     * 获得设计-认领明细分页
     *
     * @param pageReqVO 分页查询
     * @return 设计-认领明细分页
     */
    PageResult<DesignItemDO> getDesignItemPage(DesignItemPageReqVO pageReqVO);

    /**
     * 完成任务
     *
     * @param updateReqVO
     * @return
     */
    Boolean complate(DesignItemSaveReqVO updateReqVO);

    /**
     * 获取所有设计任务明细列表
     *
     * @return 设计任务明细列表
     */
    List<DesignItemDO> getAllDesignItemList();
}
