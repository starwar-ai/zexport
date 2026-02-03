package com.syj.eplus.module.exms.service.eventcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryPageReqVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryRespVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategorySaveReqVO;
import com.syj.eplus.module.exms.convert.eventcategory.EventCategoryConvert;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;
import com.syj.eplus.module.exms.dal.dataobject.exhibition.ExhibitionDO;
import com.syj.eplus.module.exms.dal.mysql.eventcategory.EventCategoryMapper;
import com.syj.eplus.module.exms.dal.mysql.exhibition.ExhibitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.exms.enums.ErrorCodeConstants.EVENT_CATEGORY_IN_USED;
import static com.syj.eplus.module.exms.enums.ErrorCodeConstants.EVENT_CATEGORY_NOT_EXISTS;

/**
 * 展会系列 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class EventCategoryServiceImpl implements EventCategoryService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private EventCategoryMapper eventCategoryMapper;
    @Resource
    private ExhibitionMapper exhibitionMapper;


    @Override
    public Long createEventCategory(EventCategorySaveReqVO createReqVO) {
       EventCategoryDO eventCategory = EventCategoryConvert.INSTANCE.convertEventCategoryDO(createReqVO);
        // 插入
        eventCategoryMapper.insert(eventCategory);
        // 返回
        return eventCategory.getId();
    }

    @Override
    public void updateEventCategory(EventCategorySaveReqVO updateReqVO) {
        // 校验存在
        validateEventCategoryExists(updateReqVO.getId());
        // 更新
        EventCategoryDO updateObj = EventCategoryConvert.INSTANCE.convertEventCategoryDO(updateReqVO);
        eventCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteEventCategory(Long id) {
        // 校验存在
        validateEventCategoryExists(id);
        List<ExhibitionDO> exhibitionDOList =  exhibitionMapper.selectList(new LambdaQueryWrapperX<ExhibitionDO>().eq(ExhibitionDO::getExmsEventCategoryId, id));
        if(!exhibitionDOList.isEmpty()){
            throw exception(EVENT_CATEGORY_IN_USED);
        }
        // 删除
        eventCategoryMapper.deleteById(id);
    }

    private void validateEventCategoryExists(Long id) {
        if (eventCategoryMapper.selectById(id) == null) {
            throw exception(EVENT_CATEGORY_NOT_EXISTS);
        }
    }
    @Override
    public EventCategoryRespVO getEventCategory(Long id) {
        EventCategoryDO eventCategoryDO = eventCategoryMapper.selectById(id);
        if (eventCategoryDO == null) {
            return null;
        }
        return EventCategoryConvert.INSTANCE.convertEventCategoryRespVO(eventCategoryDO);
    }

    @Override
    public PageResult<EventCategoryDO> getEventCategoryPage(EventCategoryPageReqVO pageReqVO) {
        return eventCategoryMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<EventCategoryDO> getSimpleList(EventCategoryPageReqVO pageReqVO) {
        return eventCategoryMapper.selectPage(pageReqVO);
    }
}