package com.syj.eplus.module.oa.service.apply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyDetailReq;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplySaveReqVO;

import javax.validation.Valid;
import java.util.List;

/**
 * OA申请单 Service 接口
 *
 * @author ePlus
 */
public interface ApplyService {

    /**
     * 创建OA申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApply(@Valid ApplySaveReqVO createReqVO,FeeShareSourceTypeEnum typeEnum);

    /**
     * 更新OA申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateApply(@Valid ApplySaveReqVO updateReqVO,FeeShareSourceTypeEnum typeEnum);

    /**
     * 删除OA申请单
     *
     * @param id 编号
     */
    void deleteApply(Long id);

    /**
     * 获得OA申请单
     *
     * @param  applyDetailReq 明细请求实体
     * @return OA申请单
     */
    ApplyRespVO getApply(ApplyDetailReq applyDetailReq, FeeShareSourceTypeEnum type);

    /**
     * 获得OA申请单分页
     *
     * @param pageReqVO 分页查询
     * @return OA申请单分页
     */
    PageResult<ApplyRespVO> getApplyPage(ApplyPageReqVO pageReqVO);
    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey(Integer typeNum);
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
    void submitTask(Long id, Long userId,Integer typeNum);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 通过id列表获取申请单列表
     * @param idList id列表
     * @return 申请单列表
     */
    List<ApplyRespVO> getApplyListByIdList(List<Long> idList);


    void batchUpdateIsApplyExpense(List<Long> applyIdList,Integer applyExpenseFlag);

    void closepApply(Long id);
}