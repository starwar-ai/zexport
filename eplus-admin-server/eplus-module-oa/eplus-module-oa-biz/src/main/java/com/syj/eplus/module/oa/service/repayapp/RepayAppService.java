package com.syj.eplus.module.oa.service.repayapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppDetailReq;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 还款单 Service 接口
 *
 * @author ePlus
 */
public interface RepayAppService {

    /**
     * 创建还款单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRepayApp(@Valid RepayAppSaveReqVO createReqVO);

    /**
     * 更新还款单
     *
     * @param updateReqVO 更新信息
     */
    void updateRepayApp(@Valid RepayAppSaveReqVO updateReqVO);

    /**
     * 删除还款单
     *
     * @param id 编号
     */
    void deleteRepayApp(Long id);

    /**
     * 获得还款单
     *
     * @param req 详情请求
     * @return 还款单
     */
    RepayAppRespVO getRepayApp(RepayAppDetailReq req);

    /**
     * 获得还款单分页
     *
     * @param pageReqVO 分页查询
     * @return 还款单分页
     */
    PageResult<RepayAppRespVO> getRepayAppPage(RepayAppPageReqVO pageReqVO);

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
     * @param repayId 还款单id
     * @param userId  用户id
     */
    void submitTask(Long repayId, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 根据借款单id批量获取还款详情
     *
     * @param ids 借款单id列表
     * @return 还款详情map
     */
    Map<Long, List<RepayAppRespVO>> getRepayAppMap(List<Long> ids, Long repayAppId);

    /**
     * 根据借款单id获取还款详情(不包含已取消状态)
     *
     * @param id 借款单id
     * @return 还款详情list
     */
    List<RepayAppDO> getRepayAppListWithOutCancel(Long id);

    /**
     * 更新还款状态
     *
     * @param code 还款单编号
     */
    void updateRepayStatus(String code);

    /**
     * 根据申请单号获取申请人信息
     * @param code 申请单号
     * @return 申请人信息
     */
    UserDept getApplyerByCode(String code);
}