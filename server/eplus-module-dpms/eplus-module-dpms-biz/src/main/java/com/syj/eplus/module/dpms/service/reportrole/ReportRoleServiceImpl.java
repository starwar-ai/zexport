package com.syj.eplus.module.dpms.service.reportrole;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRolePageReqVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleRespVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleSaveReqVO;
import com.syj.eplus.module.dpms.convert.reportrole.ReportRoleConvert;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;
import com.syj.eplus.module.dpms.dal.mysql.reportrole.ReportRoleMapper;
import com.syj.eplus.module.dpms.dal.redis.RedisKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.syj.eplus.module.dpms.api.enums.ErrorCodeConstants.REPORT_ROLE_NOT_EXISTS;

/**
 * 报表角色关系表 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class ReportRoleServiceImpl implements ReportRoleService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReportRoleMapper reportRoleMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReportRole(ReportRoleSaveReqVO createReqVO) {
ReportRoleDO reportRole = ReportRoleConvert.INSTANCE.convertReportRoleDO(createReqVO);
        // 插入
        reportRoleMapper.insert(reportRole);
        // 返回
        return reportRole.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReportRole(ReportRoleSaveReqVO updateReqVO) {
        // 校验存在
        validateReportRoleExists(updateReqVO.getId());
        // 更新
ReportRoleDO updateObj = ReportRoleConvert.INSTANCE.convertReportRoleDO(updateReqVO);
        reportRoleMapper.updateById(updateObj);
    }

    @Override
    public void deleteReportRole(Long id) {
        // 校验存在
        validateReportRoleExists(id);
        // 删除
        reportRoleMapper.deleteById(id);
    }

    private void validateReportRoleExists(Long id) {
        if (reportRoleMapper.selectById(id) == null) {
            throw exception(REPORT_ROLE_NOT_EXISTS);
        }
    }
    @Override
public ReportRoleRespVO getReportRole(Long id) {
    ReportRoleDO reportRoleDO = reportRoleMapper.selectById(id);
if (reportRoleDO == null) {
return null;
}
return ReportRoleConvert.INSTANCE.convertReportRoleRespVO(reportRoleDO);
    }

    @Override
    public PageResult<ReportRoleDO> getReportRolePage(ReportRolePageReqVO pageReqVO) {
        return reportRoleMapper.selectPage(pageReqVO);
    }

    @Override
    @DSTransactional
    @CacheEvict(value = RedisKeyConstants.REPORT_ROLE_ID_LIST,
            allEntries = true)
    public void assignRoleReport(Long roleId, Set<Long> reportIds) {
        // 获得角色拥有菜单编号
        Set<Long> dbReportIds = convertSet(reportRoleMapper.selectListByRoleId(roleId), ReportRoleDO::getReportId);
        // 计算新增和删除的菜单编号
        Set<Long> cardIdList = CollUtil.emptyIfNull(reportIds);
        Collection<Long> createReportIds = CollUtil.subtract(cardIdList, dbReportIds);
        Collection<Long> deleteReportIds = CollUtil.subtract(dbReportIds, cardIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createReportIds)) {
            reportRoleMapper.insertBatch(CollectionUtils.convertList(createReportIds, reportId -> {
                ReportRoleDO entity = new ReportRoleDO();
                entity.setRoleId(roleId);
                entity.setReportId(reportId);
                return entity;
            }));
        }
        if (CollUtil.isNotEmpty(deleteReportIds)) {
            reportRoleMapper.deleteListByRoleIdAndCardIds(roleId, deleteReportIds);
        }
    }

}