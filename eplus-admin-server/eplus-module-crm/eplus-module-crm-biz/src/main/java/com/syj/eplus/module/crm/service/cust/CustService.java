package com.syj.eplus.module.crm.service.cust;

import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.BaseData;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.module.crm.api.cust.dto.*;
import com.syj.eplus.module.crm.controller.admin.cust.vo.*;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 客户资料 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public interface CustService extends IService<CustDO> {

    /**
     * 创建客户资料
     *
     * @param createInfoReqVO 创建信息
     * @return 编号
     */
    Long createCust(CustInfoSaveReqVO createInfoReqVO);

    /**
     * 更新客户资料
     *
     * @param updateReqVO 更新信息
     */
    void updateCust(CustInfoSaveReqVO updateReqVO);

    /**
     * 变更客户资料
     *
     * @param changeReqVO 变更信息
     */
    Long changeCust(CustInfoSaveReqVO changeReqVO);

    /**
     * 变更客户成功后
     *
     * @param cust
     */
    void changeSuccess(CustInfoRespVo cust);

    /**
     * 删除客户资料
     * * @param id 编号
     */
    void deleteCust(Long id, Boolean deleted);

    /**
     * 删除变更客户
     *
     * @param id
     */
    void deleteChangeCust(Long id);

    /**
     * 删除该客户id对应的旧客户资料
     * * @param id 编号
     */
    void deleteOldCust(Long id);

    /**
     * 获得客户资料
     *
     * @param req 请求实体
     * @return 客户资料
     */
    CustInfoRespVo getCust(CrmCustDetailReq req);

    /**
     * 获得客户资料
     *
     * @param req 请求实体
     * @return 客户资料
     */
    CustInfoRespVo getCustChange(CrmCustDetailReq req);

    /**
     * 根据客户编号获得最新的客户变更资料
     *
     * @param code
     * @return 客户资料
     */
    CustInfoRespVo getCustChangeByCode(String code);

    /**
     * 获得旧客户资料
     *
     * @param cust 请求实体
     * @return 旧客户资料
     */
    CustInfoRespVo getOldCust(CustInfoRespVo cust);

    /**
     * 获得客户资料分页
     *
     * @param pageReqVO 分页查询
     * @return 客户资料分页
     */
    PageResult<CustRespVO> getCustPage(CustPageReqVO pageReqVO);

    /**
     * 获得变更客户资料分页
     *
     * @param pageReqVO 分页查询
     * @return 客户资料分页
     */
    PageResult<CustRespVO> getCustChangePage(CustPageReqVO pageReqVO);

    /**
     * 获得变更客户影响的范围
     *
     * @param changeReqVO
     * @return
     */
    ChangeEffectRespVO getChangeEffect(CustInfoRespVo changeReqVO);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqdTO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交任务
     *
     * @param custId 客户id
     * @param userId 用户id
     */
    void submitTask(Long custId, Long userId);

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

    /**
     * 更新变更审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 获取精简客户列表
     *
     * @return
     */
    PageResult<SimpleCustRespDTO> getSimpleCust(CustPageReqVO pageReqVO);

    /**
     * 获取客户id对应名称缓存
     */
    Map<String, String> getCustNameCache(String code);

    /**
     * 批量获取客户精简列表
     *
     * @param idList 客户id列表
     * @return 客户精简列表
     */
    List<SimpleCustRespDTO> getSimpleCustRespDTOList(List<Long> idList);

    /**
     * 模糊查询客户银行信息
     *
     * @param simplePageReqVO 客户名称
     * @return 客户银行信息
     */
    PageResult<SimpleCustRespDTO> getSimpleCustRespDTO(SimplePageReqVO simplePageReqVO);

    /**
     * 潜在客户转正式客户
     *
     * @param updateReqVO 转正信息
     */
    void convertCust(CustInfoSaveReqVO updateReqVO);

    /**
     * 启用客户
     *
     * @param custId 客户id
     */
    void enableCust(Long custId);

    /**
     * 启用客户
     *
     * @param custId 客户id
     */
    void disableCust(Long custId);

    /**
     * 根据银行抬头获取客户精简信息
     *
     * @param bankPoc 银行抬头
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByBankPoc(String bankPoc);

    /**
     * 回写客户抬头
     */
    void createCustTitle(Map<String,Long> notExistCustCodeMap, String custTitle);

    List<CustDO> getCustByCodeList(List<String> codeList);

    List<String> getCodeListByName(String custName);

    /**
     * 校验相似名称
     *
     * @param name 名称
     * @return 匹配名称列表
     */
    List<String> checkName(String name);

    /**
     * 获取变更流程标识
     *
     * @return
     */
    String getChangeProcessDefinitionKey();

    /**
     * 通过变更任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveChangeTask(Long userId, @Valid BpmTaskApproveReqDTO reqdTO);

    /**
     * 不通过变更任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectChangeTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交变更任务
     *
     * @param custId 客户id
     * @param userId 用户id
     */
    void submitChangeTask(Long custId, Long userId);

    /**
     * 获得客户变更
     *
     * @param targetCode 影响范围主键
     * @return 客户变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    String getProcessDefinitionKeyByBusinessId(Long id);

    List<String> getCodeListByCountryCode(Long countryId, String regionCode);

    CustAllDTO getCustByCode(String custCode);

    List<CustDO> getCustomersWithIdGreaterThan(Long lastId);
    List<MarkDO> getMarkListByCustCode(Long custId);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    List<String> getCodeListByNameList(List<String> nameList);

    /**
     * 根据抬头模糊查询银行联系人
     * @param title 抬头
     * @return 银行联系人列表
     */
    List<CustBankAccountDTO> getBankPocListByTitle(String title);

    /**
     * 根据客户编号列表取客户精简信息
     *
     * @param cutCodeList 客户编号列表
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByCode(List<String> cutCodeList);

    /**
     *  根据客户名称模糊查询
     *
     * @param custName 客户名称
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByName(String custName);

    /**
     * 删除认领自动回写的抬头
     * @param payeeEntityIds 认领明细id
     */
    void deleteCustTitle(List<Long> payeeEntityIds);

    /**
     * 校验是否内部客户
     * @param custCode 客户编号
     */
    boolean checkIsInternalCust(String custCode);

    /**
     * 获取内部客户主体
     */
    Long getInternalCompany(String custCode);

    /**
     * 根据客户编号获取收款计划
     * @param custCode 客户编号
     * @return 收款计划
     */
    List<SystemCollectionPlanDTO> getCollectionPlanDTOByCustCode(String custCode);

    /**
     * 根据客户编号获取银行抬头
     * @param custCode 客户编号
     * @return 银行抬头
     */
    String getBankPocByCustCode(String custCode);

    /**
     * 根据客户编号获取联系人信息
     * @param custCode 客户编号
     * @return 联系人姓名
     */
    String getCustPocByCustCode(String custCode);

    /**
     * 删除客户资料
     * * @param id 编号
     */
    void managerDeleteCust(Long id);

    /**
     * 获得客户资料分页
     *
     * @param pageReqVO 分页查询
     * @return 客户资料分页
     */
    PageResult<CustRespVO> custDuplicateCheck(CustPageReqVO pageReqVO);

    Map<Long, CustDO> getCustByIds(List<Long> custIds);

    /**
     * 获取所有内部客户
     * @return
     */
    Map<Long, BaseData> getAllInnerCustCache();

}
