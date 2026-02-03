package com.syj.eplus.module.dtms.service.designsummary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryRespVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummarySaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;

import javax.validation.Valid;

/**
 * 设计-工作总结 Service 接口
 *
 * @author ePlus
 */
public interface DesignSummaryService extends IService<DesignSummaryDO> {

    /**
     * 创建设计-工作总结
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDesignSummary(@Valid DesignSummarySaveReqVO createReqVO);

    /**
     * 更新设计-工作总结
     *
     * @param updateReqVO 更新信息
     */
    void updateDesignSummary(@Valid DesignSummarySaveReqVO updateReqVO);

    /**
     * 删除设计-工作总结
     *
     * @param id 编号
     */
    void deleteDesignSummary(Long id);

    /**
     * 获得设计-工作总结
     *
     */
    DesignSummaryRespVO getDesignSummary(Long designId,Long designerId);

    /**
     * 获得设计-工作总结分页
     *
     * @param pageReqVO 分页查询
     * @return 设计-工作总结分页
     */
    PageResult<DesignSummaryDO> getDesignSummaryPage(DesignSummaryPageReqVO pageReqVO);

}
