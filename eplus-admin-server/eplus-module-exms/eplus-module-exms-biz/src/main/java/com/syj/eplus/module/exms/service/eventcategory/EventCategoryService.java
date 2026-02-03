package com.syj.eplus.module.exms.service.eventcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryPageReqVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryRespVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategorySaveReqVO;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;

import javax.validation.Valid;

/**
 * 展会系列 Service 接口
 *
 * @author ePlus
 */
public interface EventCategoryService {

    /**
     * 创建展会系列
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEventCategory(@Valid EventCategorySaveReqVO createReqVO);

    /**
     * 更新展会系列
     *
     * @param updateReqVO 更新信息
     */
    void updateEventCategory(@Valid EventCategorySaveReqVO updateReqVO);

    /**
     * 删除展会系列
     *
     * @param id 编号
     */
    void deleteEventCategory(Long id);

    /**
     * 获得展会系列
     *
     * @param  id 编号
     * @return 展会系列
     */
    EventCategoryRespVO getEventCategory(Long id);

    /**
     * 获得展会系列分页
     *
     * @param pageReqVO 分页查询
     * @return 展会系列分页
     */
    PageResult<EventCategoryDO> getEventCategoryPage(EventCategoryPageReqVO pageReqVO);

    PageResult<EventCategoryDO> getSimpleList(EventCategoryPageReqVO pageReqVO);
}