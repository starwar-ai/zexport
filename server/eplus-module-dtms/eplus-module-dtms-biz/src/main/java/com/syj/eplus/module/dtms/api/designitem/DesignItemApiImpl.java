package com.syj.eplus.module.dtms.api.designitem;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.dtms.api.designitem.dto.DesignItemRespDTO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import com.syj.eplus.module.dtms.service.designitem.DesignItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设计任务明细 API 实现类
 *
 * @author du
 */
@Service
public class DesignItemApiImpl implements DesignItemApi {

    @Resource
    private DesignItemService designItemService;

    @Override
    public List<DesignItemRespDTO> getAllDesignItemList() {
        List<DesignItemDO> list = designItemService.getAllDesignItemList();
        return BeanUtils.toBean(list, DesignItemRespDTO.class);
    }
}
