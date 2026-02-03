package com.syj.eplus.module.oa.service.feeshare;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.*;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 费用归集 Service 接口
 *
 * @author zhangcm
 */
public interface FeeShareService {

    /**
     * 创建费用归集
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFeeShare(@Valid FeeShareSaveReqVO createReqVO);

    /**
     * 更新费用归集
     *
     * @param updateReqVO 更新信息
     */
    void updateFeeShare(@Valid FeeShareSaveReqVO updateReqVO);
    void updateFeeShareList(@Valid FeeShareListSaveReqVO updateReqVO);

    /**
     * 删除费用归集
     *
     * @param id 编号
     */
    void deleteFeeShare(Long id);

    /**
     * 获得费用归集
     *
     * @param req 请求体
     * @return 费用归集
     */
    FeeShareRespVO getFeeShare(FeeShareDetailReq req);

    /**
     * 获得费用归集分页
     *
     * @param pageReqVO 分页查询
     * @return 费用归集分页
     */
    PageResult<FeeSharePageRespVO> getFeeSharePage(FeeSharePageReqVO pageReqVO);

    void updateFeeShareByDTO(FeeShareReqDTO feeShare);

    List<FeeShareRespDTO> getFeeShareDTO(Integer type, String code,Boolean isPre);

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
     * @param feeShareId 客户id
     * @param userId 用户id
     */
    void submitTask(Long feeShareId, Long userId,String businessCode);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 更新审核状态状态
     *
     * @param businessKey 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(String businessKey, Integer auditStatus, String processInstanceId);

    /**
     * 更新来源单据状态
     * @param businessKey 主键
     */
    void updateSourceFeeShareStatus(String businessKey);

    /**
     * 修改来源单据状态
     * @param businessType 来源单据类型
     * @param BusinessId 来源单据主键
     * @param sourceStatus 来源单据状态
     */
    void updateSourceStatus(Integer businessType,Long BusinessId, Integer sourceStatus);

    /**
     * 多币种转换为人民币
     * @param amountList 多币种金额
     * @return 人民币金额
     */
     JsonAmount getFeeShareAmount(List<JsonAmount> amountList);

    /**
     * 修改归集状态
     * @param businessKey 主键
     * @param status 状态
     */
     void updateStatus(String businessKey, Integer status);

    Boolean isUsedByDescId(FeeShareDescDO desc);

    Map<String, List<FeeShareRespDTO>> getFeeShareByCodeList(Integer value, List<String> codeList);

    /**
     * 修改支付状态
     * @param businessType 来源单据类型
     * @param BusinessCode 来源单据主键
     * @param paymentStatus 来源单据状态
     */
    void updatePaymentStatus(Integer businessType,String BusinessCode, Integer paymentStatus);

    void updateFeeShareByDTOList(List<FeeShareReqDTO> list,boolean isPre);

    void deleteByCodes(FeeShareSourceTypeEnum shipmentForwarderFee, List<String> forwarderFeeCodes);

    List<FeeShareRespDTO> batchGetDetails(Integer isPre,Integer type,List<String> codes);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);

    void deleteById(Integer value, Long reimbId);
}