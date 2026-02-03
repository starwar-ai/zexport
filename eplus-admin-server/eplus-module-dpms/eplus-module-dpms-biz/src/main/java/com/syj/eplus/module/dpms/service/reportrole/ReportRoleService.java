package com.syj.eplus.module.dpms.service.reportrole;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRolePageReqVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleRespVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;

import javax.validation.Valid;
import java.util.Set;

/**
 * 报表角色关系表 Service 接口
 *
 * @author du
 */
public interface ReportRoleService {

    /**
     * 创建报表角色关系表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReportRole(@Valid ReportRoleSaveReqVO createReqVO);

    /**
     * 更新报表角色关系表
     *
     * @param updateReqVO 更新信息
     */
    void updateReportRole(@Valid ReportRoleSaveReqVO updateReqVO);

    /**
     * 删除报表角色关系表
     *
     * @param id 编号
     */
    void deleteReportRole(Long id);

    /**
     * 获得报表角色关系表
     *
* @param  id 编号 
     * @return 报表角色关系表
     */
ReportRoleRespVO getReportRole(Long id);

    /**
     * 获得报表角色关系表分页
     *
     * @param pageReqVO 分页查询
     * @return 报表角色关系表分页
     */
    PageResult<ReportRoleDO> getReportRolePage(ReportRolePageReqVO pageReqVO);

    /**
     * 设置角色报表
     *
     * @param roleId  角色编号
     * @param reportIds 报表编号集合
     */
    void assignRoleReport(Long roleId, Set<Long> reportIds);
}