package com.syj.eplus.module.dtms.api.designitem;

import com.syj.eplus.module.dtms.api.designitem.dto.DesignItemRespDTO;

import java.util.List;

/**
 * 设计任务明细 API 接口
 *
 * @author du
 */
public interface DesignItemApi {

    /**
     * 获取所有设计任务明细列表
     *
     * @return 设计任务明细列表
     */
    List<DesignItemRespDTO> getAllDesignItemList();
}
