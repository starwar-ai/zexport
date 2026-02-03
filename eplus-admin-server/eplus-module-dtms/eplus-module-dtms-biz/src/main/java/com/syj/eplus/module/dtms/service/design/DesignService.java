package com.syj.eplus.module.dtms.service.design;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignDetailReq;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignRespVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignSaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 设计-任务单 Service 接口
 *
 * @author ePlus
 */
public interface DesignService extends IService<DesignDO> {

    /**
     * 创建设计-任务单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDesign(@Valid DesignSaveReqVO createReqVO);

    /**
     * 更新设计-任务单
     *
     * @param updateReqVO 更新信息
     */
    void updateDesign(@Valid DesignSaveReqVO updateReqVO);

    /**
     * 删除设计-任务单
     *
     * @param id 编号
     */
    void deleteDesign(Long id);

    /**
     * 获得设计-任务单
     *
     * @param designDetailReq 明细请求实体
     * @return 设计-任务单
     */
    DesignRespVO getDesign(DesignDetailReq designDetailReq);

    /**
     * 获得设计-任务单分页
     *
     * @param pageReqVO 分页查询
     * @return 设计-任务单分页
     */
    PageResult<DesignRespVO> getDesignPage(DesignPageReqVO pageReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 获取所有设计任务
     *
     * @return 设计任务列表
     */
    List<DesignDO> getAllList();

    /**
     * 取消认领
     *
     * @param id     业务id
     */
    void cancleClaim(Long id);

    /**
     * 获得设计-任务单分页
     *
     * @param pageReqVO 分页查询
     * @return 设计-任务单分页
     */
    PageResult<DesignRespVO> getClaimDesignPage(DesignPageReqVO pageReqVO);

    DesignDO getDOById(Long designId);

    void updateDOById(DesignDO designDO);
}
