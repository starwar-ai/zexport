package com.syj.eplus.module.oa.service.travelapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.SimpleTravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.travelapp.TravelAppDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 出差申请 Service 接口
 *
 * @author 管理员
 */
public interface TravelAppService {

    /**
     * 创建出差申请
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTravelApp(@Valid TravelAppSaveReqVO createReqVO);

    /**
     * 更新出差申请
     *
     * @param updateReqVO 更新信息
     */
    void updateTravelApp(@Valid TravelAppSaveReqVO updateReqVO);

    /**
     * 删除出差申请
     *
     * @param id 编号
     */
    void deleteTravelApp(Long id);

    /**
     * 获得出差申请
     *
     * @param id 编号
     * @return 出差申请
     */
    TravelAppDO getTravelApp(Long id);

    /**
     * 获得出差申请分页
     *
     * @param pageReqVO 分页查询
     * @return 出差申请分页
     */
    PageResult<TravelAppDO> getTravelAppPage(TravelAppPageReqVO pageReqVO);


    void updateTravelAppResult(Long id, Integer result);


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
     * @param applyerId 申请单id
     * @param userId    用户id
     */
    void submitTask(Long applyerId, Long userId);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 校验企微同步的出差申请是否已存在
     *
     * @param wecomId 企微出差申请id
     * @return
     */
    boolean validateTravelAppExistsByWecomId(String wecomId);

    /**
     * 获得精简出差申请分页（供其他模块筛选）
     *
     * @param pageReqVO 分页查询
     * @return 精简出差申请分页
     */
    PageResult<SimpleTravelAppRespDTO> getSimpleTravelAppPage(SimpleTravelAppPageReqVO pageReqVO);

    /**
     * 根据id列表获取出差申请精简列表
     *
     * @param idList id列表
     * @return 出差申请精简列表
     */
    List<SimpleTravelAppRespDTO> getSimpleTravelAppRespDTOList(List<Long> idList);

    /**
     * 根据id获取出差申请精简实体
     *
     * @param reimbCode 申请单编号
     * @return 出差申请精简实体
     */
    SimpleTravelAppRespDTO getSimpleTravelAppRespDTO(String reimbCode);

}