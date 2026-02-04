package com.syj.eplus.module.dpms.service.design;

import com.syj.eplus.module.dpms.controller.admin.design.vo.DesignStatisticRespVO;
import com.syj.eplus.module.dtms.api.design.dto.DesignPageReqDTO;
import com.syj.eplus.module.dtms.api.design.dto.DesignRespDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/16:17
 * @Description:
 */

public interface DesignReportService {
    /**
     * 获得设计-任务单分页
     *
     * @param pageReqDTO 分页查询
     * @return 设计-任务单分页
     */
    List<DesignRespDTO> getDesignPage(DesignPageReqDTO pageReqDTO);


    /**
     * 获得设计任务统计信息
     *
     * @return 设计任务统计信息
     */
    DesignStatisticRespVO getDesignStatisticResp();
}
