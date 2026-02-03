package com.syj.eplus.module.service.send;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.controller.admin.send.vo.*;
import com.syj.eplus.module.controller.admin.send.vo.feeshare.FeeShareReqVO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendDetailDTO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;

import javax.validation.Valid;
import java.util.List;

/**
 * 寄件 Service 接口
 *
 * @author zhangcm
 */
public interface SendService {

    /**
     * 创建寄件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSend(@Valid SendSaveInfoReqVO createReqVO);

    /**
     * 更新寄件
     *
     * @param updateReqVO 更新信息
     */
    void updateSend(@Valid SendSaveInfoReqVO updateReqVO);

    /**
     * 删除寄件
     *
     * @param id 编号
     */
    void deleteSend(Long id);

    /**
     * 获得寄件
     *
     * @param  req 编号
     * @return 寄件
     */
    SendInfoRespVO getSend(SendDetailReq req);

    /**
     * 获得寄件分页
     *
     * @param pageReqVO 分页查询
     * @return 寄件分页
     */
    PageResult<SendInfoRespVO> getSendPage(SendPageReqVO pageReqVO);

    /**
     * 提交
     *
     * @param id
     */
    Boolean submitSend(Long id);

    /**
     * 上传快递单号
     *
     * @param id
     */
    Boolean uploadNumberSend(Long id, String number,Long venderId);

    Boolean importSend(String batchCode ,Integer overFlag) ;

    /**
     * 编辑费用归集
     */
    void updateFeeShare(FeeShareReqVO updateReqVO)  ;



    Boolean updateCost(SendUpdateCostReqVO sendUpdateCostReqVO);

    void updateSendStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO);

    void updatePayStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO);

    /**
     * 更新寄件归属状态
     * @param id 主键
     * @param status 状态
     */
    void updateBelongFlagById(Long id,Integer status);

    List<SendInfoRespVO> getListByCodeList(String codeList);

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
     * @param id
     * @param userId        用户id
     */
    void submitTask(Long id, Long userId);

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
    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    JsonAmount getFeeShareAmountByCode(String businessCode);

    void updateBelongFlagByCode(String businessKey, Integer status);

    List<EmsSendDetailDTO> getDetailListByCodes(List<String> relationCode);

    /**
     * 作废寄件
     * @param id 寄件id
     */
    void closeSend(Long id);

    /**
     * 修改快递公司
     * @param id 寄件id
     * @param venderId 快递公司id
     */
    void updateVender(Long id, Long venderId);
}