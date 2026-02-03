package com.syj.eplus.module.exms.service.exhibition;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.exms.controller.admin.exhibition.vo.*;

import javax.validation.Valid;

/**
 * 展会 Service 接口
 *
 * @author zhangcm
 */
public interface ExhibitionService {

    /**
     * 创建展会
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExhibition(@Valid ExhibitionSaveReqVO createReqVO);

    /**
     * 更新展会
     *
     * @param updateReqVO 更新信息
     */
    void updateExhibition(@Valid ExhibitionSaveReqVO updateReqVO);

    /**
     * 删除展会
     *
     * @param id 编号
     */
    void deleteExhibition(Long id);

    /**
     * 获得展会
     *
* @param  exhibitionDetailReq 明细请求实体
     * @return 展会
     */
ExhibitionRespVO getExhibition(ExhibitionDetailReq exhibitionDetailReq);

    /**
     * 获得展会分页
     *
     * @param pageReqVO 分页查询
     * @return 展会分页
     */
    PageResult<ExhibitionRespVO> getExhibitionPage(ExhibitionPageReqVO pageReqVO);
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
    * @param id 业务id
    * @param userId    用户id
    */
    void submitTask(Long id, Long userId);

    /**
    * 更新审核状态状态
    *
    * @param auditableId 审核业务id
    * @param auditStatus 审核状态
    */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    void finishManufacture(Long id);

    void rollbackFinishManufacture(Long id);

    void doneManufacture(ExhibitionDoneReqVO reqVo );

    PageResult<ExhibitionSimpleRespVO> getSimpleExhibitionPage(ExhibitionPageReqVO pageReqVO);
}