package com.syj.eplus.module.oa.service.other;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.oa.controller.admin.otherreimb.vo.OtherReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.otherreimb.vo.OtherReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.otherreimb.vo.OtherReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;

import javax.validation.Valid;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-30
 * @Description: 其他费用报销接口类
 */
public interface OtherReimbService {

    /**
     * 创建一般费用报销
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOtherReimb(@Valid OtherReimbSaveReqVO createReqVO);

    /**
     * 更新一般费用报销
     *
     * @param updateReqVO 更新信息
     */
    int updateOtherReimb(@Valid OtherReimbSaveReqVO updateReqVO);

    /**
     * 删除一般费用报销
     *
     * @param id 编号
     */
    int deleteOtherReimb(Long id);

    /**
     * 获得一般费用报销
     *
     * @param req 编号
     * @return 一般费用报销
     */
    OtherReimbRespVO getOtherReimb(OtherReimbDetailReq req);

    /**
     * 获得一般费用报销分页
     *
     * @param pageReqVO 分页查询
     * @return 一般费用报销分页
     */
    PageResult<OtherReimbRespVO> getOtherReimbPage(ReimbPageReqVO pageReqVO);
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
     * @param otherReimbId 一般费用报销id
     */
    void submitTask(Long otherReimbId);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    JsonAmount getFeeShareAmountByCode(String businessCode);

    /**
     * 更新其他报销节点审批人
     * @param reimbId 报销id
     * @param userId 审批人
     */
    void updateApproveUserByInstanceId(Long reimbId,Long userId);

}
