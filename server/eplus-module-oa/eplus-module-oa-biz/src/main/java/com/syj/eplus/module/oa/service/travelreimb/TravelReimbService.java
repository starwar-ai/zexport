package com.syj.eplus.module.oa.service.travelreimb;


import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbStandardRespVO;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 出差报销 Service 接口
 *
 * @author ePlus
 */
public interface TravelReimbService {

    /**
     * 创建出差报销
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTravelReimb(@Valid TravelReimbSaveReqVO createReqVO);

    /**
     * 更新出差报销
     *
     * @param updateReqVO 更新信息
     */
    int updateTravelReimb(@Valid TravelReimbSaveReqVO updateReqVO);

    /**
     * 删除出差报销
     *
     * @param id 编号
     */
    int deleteTravelReimb(Long id);

    /**
     * 获得出差报销
     *
     * @param req 编号
     * @return 出差报销
     */
    TravelReimbRespVO getTravelReimb(TravelReimbDetailReq req);

    /**
     * 获得出差报销分页
     *
     * @param pageReqVO 分页查询
     * @return 出差报销分页
     */
    PageResult<TravelReimbRespVO> getTravelReimbPage(ReimbPageReqVO pageReqVO);

    /**
     * 获取住宿参考
     *
     * @return 住宿参考文本
     */
    String getLodgingSubsidy();

    /**
     * 获取出差补贴标准
     *
     * @return 出差补贴标准
     */
    List<ReimbStandardRespVO> getTravelStandard();

    /**
     * 获取里程补贴标准
     *
     * @return 里程补贴标准
     */
    BigDecimal getMileageStandard();

    /**
     * 计算实际出差报销金额
     *
     * @param travelReimb 出差报销单结果
     * @return Tuple(实际报销金额, 还款单列表)
     */
    Tuple calcTravelAmount(TravelReimbSaveReqVO travelReimb);

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
     * @param travelReimbId 出差报销id
     */
    void submitTask(Long travelReimbId);

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