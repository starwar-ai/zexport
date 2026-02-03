package com.syj.eplus.module.oa.service.generalreimb;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;

import javax.validation.Valid;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 15:52
 */
public interface GeneralReimbService {

    /**
     * 创建一般费用报销
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGeneralReimb(@Valid GeneralReimbSaveReqVO createReqVO);

    /**
     * 更新一般费用报销
     *
     * @param updateReqVO 更新信息
     */
    int updateGeneralReimb(@Valid GeneralReimbSaveReqVO updateReqVO);

    /**
     * 删除一般费用报销
     *
     * @param id 编号
     */
    int deleteGeneralReimb(Long id);

    /**
     * 获得一般费用报销
     *
     * @param req 编号
     * @return 一般费用报销
     */
    GeneralReimbRespVO getGeneralReimb(GeneralReimbDetailReq req);

    /**
     * 获得一般费用报销分页
     *
     * @param pageReqVO 分页查询
     * @return 一般费用报销分页
     */
    PageResult<GeneralReimbRespVO> getGeneralReimbPage(ReimbPageReqVO pageReqVO);
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
     * @param generalReimbId 一般费用报销id
     */
    void submitTask(Long generalReimbId);

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
}
