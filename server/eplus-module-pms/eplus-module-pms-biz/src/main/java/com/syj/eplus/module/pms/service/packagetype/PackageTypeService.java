package com.syj.eplus.module.pms.service.packagetype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.*;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 包装方式 Service 接口
 *
 * @author zhangcm
 */
public interface PackageTypeService {

    /**
     * 创建包装方式
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPackageType(@Valid PackageTypeSaveReqVO createReqVO);

    /**
     * 更新包装方式
     *
     * @param updateReqVO 更新信息
     */
    void updatePackageType(@Valid PackageTypeSaveReqVO updateReqVO);

    /**
     * 删除包装方式
     *
     * @param id 编号
     */
    void deletePackageType(Long id);

    /**
     * 获得包装方式
     *
* @param  packageTypeDetailReq 明细请求实体
     * @return 包装方式
     */
PackageTypeRespVO getPackageType( PackageTypeDetailReq packageTypeDetailReq);

    /**
     * 获得包装方式分页
     *
     * @param pageReqVO 分页查询
     * @return 包装方式分页
     */
    PageResult<PackageTypeDO> getPackageTypePage(PackageTypePageReqVO pageReqVO);
    /**
    * 获取流程标识
    *
    * @return 流程标识
    */
    String getProcessDefinitionKey();
    /**
    * 通过任务
    *
    * @param userId 用户编号
    * @param reqDTO  通过请求
    */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
    * 不通过任务
    *
    * @param userId 用户编号
    * @param reqDTO  不通过请求
    */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
    * 提交任务
    *
    * @param id 业务id
    * @param userId    用户id
    */
    void submitTask(Long id, Long userId);

    /**
    * 更新审核状态状态
    *
    * @param auditableId 审核业务id
    * @param auditStatus 审核状态
    */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    List<PackageTypeDO> getList(PackageTypeSimplePageReqVO pageReqVO);

    PackageTypeDO getById(Long id);

    List<PackageTypeDO> getByIdList(List<Long> packageIdList);

    /**
     * 获取所有包装名称缓存
     * @return
     */
    Map<Long,String> getAllNameCache();
}