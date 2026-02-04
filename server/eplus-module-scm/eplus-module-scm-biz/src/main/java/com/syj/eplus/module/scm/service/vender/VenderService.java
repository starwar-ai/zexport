package com.syj.eplus.module.scm.service.vender;

import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 供应商信息 Service 接口
 *
 * @author zhangcm
 */
public interface VenderService extends IService<VenderDO> {

    /**
     * 创建供应商信息
     *
     * @param venderInfoSaveReqVO 创建信息
     * @return 编号
     */
    @Transactional(rollbackFor = Exception.class)
    Long createVender(VenderInfoSaveReqVO venderInfoSaveReqVO, boolean isUpdate);

    /**
     * 更新供应商信息
     *
     * @param updateReqVO 更新信息
     */
    void updateVender(@Valid VenderInfoSaveReqVO updateReqVO);

    /**
     * 变更供应商成功后
     *
     * @param vender
     */
    void changeSuccess(VenderInfoRespVO vender);

    /**
     * 删除供应商信息
     *
     * @param id 编号
     */
    void deleteVender(Long id, Boolean deleted);

    /**
     * 获得供应商信息
     *
     * @param req 请求实体
     * @return 供应商信息
     */
    VenderInfoRespVO getVender(ScmVenderDetailReq req);

    /**
     * 获得供应商信息分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商信息分页
     */
    PageResult<VenderRespVO> getVenderPage(VenderPageReqVO pageReqVO);

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
     * @param VenderId 供应商id
     * @param userId   用户id
     */
    void submitTask(Long VenderId, Long userId);


    /**
     * 修改供应商资料审核状态
     *
     * @param venderId    供应商id
     * @param auditStatus 审核状态
     */
    void updateVenderAuditStatus(Long venderId, Integer auditStatus);

    /**
     * 获取供应商精简列表
     *
     * @param pageParam 分页参数
     * @return 供应商分页精简列表
     */
    PageResult<SimpleVenderRespDTO> getSimpleVender(VenderPageReqVO pageParam);

    PageResult<SimpleVenderRespDTO> getSimpleVenderByBuyerUserId(VenderSimpleReqVO reqVO);

    List<SimpleVenderRespDTO> getSimpleVenderRespDTO(String venderName);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 获取业务供应商流程标识
     *
     * @return
     */
    String getBusinessProcessDefinitionKey();

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 批量获取供应商精简列表
     *
     * @param codes 供应商id列表
     * @return 供应商精简列表
     */
    List<SimpleVenderRespDTO> getSimpleVenderRespDTOList(List<Long> codes);

    /**
     * 批量获取供应商精简列表
     *
     * @param codes 供应商id列表
     * @return 供应商精简列表
     */
    List<SimpleVenderRespDTO> getSimpleVenderRespDTOListByCodeList(List<String> codes);

    /**
     * 获取供应商精简详情
     *
     * @param code 供应商编号
     * @return 供应商精简列表
     */
    VenderInfoRespVO getVenderByCode(String code);

    /**
     * 获取供应商编号对应名称缓存
     */
    Map<String, String> getVenderNameCache(String code);

    Map<Long, String> getVenderNameCache(List<Long> idList);

    /**
     * 潜在供应商转正式供应商
     *
     * @param updateReqVO 转正信息
     */
    ServiceException convertVender(VenderInfoSaveReqVO updateReqVO);

    /**
     * 启用供应商
     *
     * @param venderId 供应商id
     */
    void enableVender(Long venderId);

    /**
     * 启用供应商
     *
     * @param venderId 供应商id
     */
    void disableVender(Long venderId);

    /**
     * 根据供应商编号列表获取供应商业务员缓存
     *
     * @param codeList 供应商编号列表
     * @return 供应商业务员缓存
     */
    Map<String, List<UserDept>> getVenderManagerByCodeList(List<String> codeList);


    /**
     * 根据供应商编号和名称模糊搜索供应商列表
     */
    List<VenderDO> getSimpleListByCodeAndName(String vender);

    /**
     * 根据银行抬头获取供应商精简信息
     *
     * @param bankPoc 银行抬头
     * @return 供应商精简信息
     */
    SimpleVenderResp getSimpleVenderRespByBankPoc(String bankPoc);

    /**
     * 根据编号批量获取供应商精简信息
     *
     * @param codeList 供应商编号列表
     * @return 供应商精简信息
     */
    Map<String, SimpleVenderResp> getSimpleVenderMapByCodes(Collection<String> codeList);

    List<VenderDO> getVenderByCodeList(Collection<String> codeList);

    List<String> getCodeListByName(String venderName);

    /**
     * 变更供应商
     *
     * @param changeReqVO 变更信息
     */
    Long changeVender(VenderInfoSaveReqVO changeReqVO);

    /**
     * 删除变更供应商
     *
     * @param id
     */
    void deleteChangeVender(Long id);

    /**
     * 删除该供应商id对应的旧供应商
     * * @param id 编号
     */
    void deleteOldVender(Long id);

    /**
     * 获得供应商
     *
     * @param req 请求实体
     * @return 供应商
     */
    VenderInfoRespVO getVenderChange(ScmVenderDetailReq req);

    /**
     * 根据供应商编号获得最新的供应商变更资料
     *
     * @param code
     * @return 供应商
     */
    VenderInfoRespVO getVenderChangeByCode(String code);

    /**
     * 获得旧供应商
     *
     * @param Vender 请求实体
     * @return 旧供应商
     */
    VenderInfoRespVO getOldVender(VenderInfoRespVO Vender);

    /**
     * 获得供应商分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商分页
     */
    PageResult<VenderRespVO> getVenderChangePage(VenderPageReqVO pageReqVO);

    /**
     * 获得变更供应商影响的范围
     *
     * @param changeReqVO
     * @return
     */
    ChangeEffectRespVO getChangeEffect(VenderInfoRespVO changeReqVO);

    /**
     * 更新变更审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 获取变更流程标识
     *
     * @return
     */
    String getChangeProcessDefinitionKey();

    /**
     * 获取变更业务供应商 流程标识
     *
     * @return
     */
    String getBusinessChangeProcessDefinitionKey();

    /**
     * 通过变更任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveChangeTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 不通过变更任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectChangeTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 提交变更任务
     *
     * @param venderId 供应商id
     * @param userId   用户id
     */
    void submitChangeTask(Long venderId, Long userId);

    /**
     * 根据供应商id创建虚拟仓库
     *
     * @param venderId
     */
    void createVenderWareHouse(Long venderId);

    /**
     * 获得供应商变更
     *
     * @param targetCode 影响范围主键
     * @return 供应商变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    /**
     * 校验相似名称
     *
     * @param name 名称
     * @return 匹配名称列表
     */
    List<String> checkName(String name);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    SimpleVenderRespDTO getSimpleVenderRespDTOById(Long venderId);

    /**
     * 获取供应商付款方式
     * @param venderId 供应商主键
     * @return 付款方式列表
     */
    List<VenderPaymentDTO> getVenderPaymentList(Long venderId);

    Map<String, SimpleVenderRespDTO> getSimpleVenderDTOMapByCodes(Collection<String> venderCodeList,Integer containInnerFlag);

    /**
     * 通过供应商编号获取内部主体信息
     * @param venderCode 供应商编号
     * @return 内部主体信息
     */
    SimpleCompanyDTO getCompanyByVenderCode(String venderCode);

    /**
     * 校验供应商是否为内部供应商
     * @param venderCode 供应商编号
     * @return 结果
     */
    boolean checkInnerVender(String venderCode);

    /**
     * 通过编号获取供应商名称及电话
     * @param venderCode 供应商编号
     * @return 结果
     */
    Tuple getVenderNameAndTelByCode(String venderCode);

    /**
     * 过滤审核通过的正式供应商
     * @param venderCodeList 供应商列表
     * @return 供应商列表
     */
    Set<String> getAvailableVenderIdSetByVenderIdList(List<String> venderCodeList);

    /**
     * 根据供应商编号获取付款项
     * @param venderCode 供应商编号
     * @return 付款项列表
     */
    Map<Long,PaymentItemDTO> getPaymentItemDTOListByVenderCode(String venderCode);

    /**
     * 校验供应商是否可用
     * @param codeList 供应商编号列表
     */
    void validateVenderAvailable(List<String> codeList);

    /**
     * 根据供应商编号获取银行抬头
     * @param venderCode 供应商编号
     * @return 银行抬头
     */
    String getBankPocByVenderCode(String venderCode);

    /**
     * 删除供应商资料
     * * @param id 编号
     */
    void managerDeleteVender(Long id);

    void rejectChangeVender(Long id);
}
