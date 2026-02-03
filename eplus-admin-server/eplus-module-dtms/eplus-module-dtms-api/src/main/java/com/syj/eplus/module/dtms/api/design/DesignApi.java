package com.syj.eplus.module.dtms.api.design;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.dtms.api.design.dto.DesignPageReqDTO;
import com.syj.eplus.module.dtms.api.design.dto.DesignRespDTO;

import java.util.List;

/**
 * 设计任务 API 接口
 *
 * @author du
 */
public interface DesignApi {

    /**
     * 获取设计任务分页
     *
     * @param pageReqDTO 分页请求
     * @return 设计任务分页结果
     */
    PageResult<DesignRespDTO> getDesignPage(DesignPageReqDTO pageReqDTO);

    /**
     * 获取所有设计任务列表
     *
     * @return 设计任务列表
     */
    List<DesignRespDTO> getAllDesignList();
}
