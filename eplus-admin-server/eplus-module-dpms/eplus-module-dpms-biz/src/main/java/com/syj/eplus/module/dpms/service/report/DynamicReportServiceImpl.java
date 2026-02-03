package com.syj.eplus.module.dpms.service.report;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportRespVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportSaveReqVO;
import com.syj.eplus.module.dpms.convert.report.ReportConvert;
import com.syj.eplus.module.dpms.dal.dataobject.report.ReportDO;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;
import com.syj.eplus.module.dpms.dal.mysql.report.DynamicReportMapper;
import com.syj.eplus.module.dpms.dal.mysql.reportrole.ReportRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.UNASSIGNED_ROLE;
import static com.syj.eplus.module.dpms.api.enums.ErrorCodeConstants.REPORT_NOT_EXISTS;

/**
 * 报表配置 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class DynamicReportServiceImpl implements DynamicReportService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DynamicReportMapper dynamicReportMapper;

    @Resource
    private PermissionApi permissionApi;

    @Resource
    private ReportRoleMapper reportRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReport(ReportSaveReqVO createReqVO) {
        ReportDO report = ReportConvert.INSTANCE.convertReportDO(createReqVO);
        // 插入
        dynamicReportMapper.insert(report);
        // 返回
        return report.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReport(ReportSaveReqVO updateReqVO) {
        // 校验存在
        validateReportExists(updateReqVO.getId());
        // 更新
        ReportDO updateObj = ReportConvert.INSTANCE.convertReportDO(updateReqVO);
        dynamicReportMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReport(Long id) {
        // 校验存在
        ReportDO reportDO = validateReportExists(id);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        List<ReportDO> reportDOList = dynamicReportMapper.selectList(ReportDO::getUserId, loginUserId);
        // 删除报表时更新排序
        if (CollUtil.isNotEmpty(reportDOList)) {
            List<ReportDO> updateReportList = reportDOList.stream().filter(s -> !s.getId().equals(id))
                    .map(s -> {
                        if (s.getSort() > reportDO.getSort()) {
                            s.setSort(s.getSort() - 1);
                        }
                        return new ReportDO().setId(s.getId()).setSort(s.getSort());
                    }).toList();
            dynamicReportMapper.updateBatch(updateReportList);
        }
        // 删除
        dynamicReportMapper.deleteById(id);
    }

    private ReportDO validateReportExists(Long id) {
        ReportDO reportDO = dynamicReportMapper.selectById(id);
        if (reportDO == null) {
            throw exception(REPORT_NOT_EXISTS);
        }
        return reportDO;
    }

    @Override
    public ReportRespVO getReport(Long id) {
        ReportDO reportDO = dynamicReportMapper.selectById(id);
        if (reportDO == null) {
            return null;
        }
        return ReportConvert.INSTANCE.convertReportRespVO(reportDO);
    }

    @Override
    public PageResult<ReportDO> getReportPage(ReportPageReqVO pageReqVO) {
        return dynamicReportMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchCreateReport(List<Long> reportIds) {
        if (CollUtil.isEmpty(reportIds)){
            return true;
        }
        List<ReportDO> reportDOList = dynamicReportMapper.selectBatchIds(reportIds);
        if (CollUtil.isEmpty(reportDOList)){
            throw exception(REPORT_NOT_EXISTS);
        }
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        Map<Long, ReportDO> reportDOMap = reportDOList.stream().collect(Collectors.toMap(ReportDO::getId, s -> s));
        List<ReportDO> saveList = new ArrayList<>();
        for (int i = 0; i < reportIds.size(); i++) {
            ReportDO reportDO = reportDOMap.get(reportIds.get(i));
            if (Objects.isNull(reportDO)){
                throw exception(REPORT_NOT_EXISTS,reportIds.get(i));
            }
            reportDO.setId(null);
            reportDO.setUserId(loginUserId);
            reportDO.setSort(i);
            saveList.add(reportDO);
        }
        dynamicReportMapper.delete(ReportDO::getUserId, loginUserId);
        return dynamicReportMapper.insertBatch(saveList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ReportRespVO> getReportList() {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        List<ReportDO> reportDOList = dynamicReportMapper.selectList(ReportDO::getUserId, loginUserId);
        if (CollUtil.isNotEmpty(reportDOList)) {
            return ReportConvert.INSTANCE.convertReportRespVOList(reportDOList);
        }
        List<Long> roleIdList = permissionApi.getRoleIdListByUserId(loginUserId);
        if (CollUtil.isEmpty(roleIdList)) {
            throw exception(UNASSIGNED_ROLE);
        }
        List<ReportRoleDO> reportRoleDOList = reportRoleMapper.selectList(ReportRoleDO::getRoleId, roleIdList);
        if (CollUtil.isEmpty(reportRoleDOList)) {
            return null;
        }
        List<Long> reportIdList = reportRoleDOList.stream().map(ReportRoleDO::getReportId).toList();
        reportDOList = dynamicReportMapper.selectBatchIds(reportIdList);
        return ReportConvert.INSTANCE.convertReportRespVOList(reportDOList);
    }

    @Override
    public List<ReportRespVO> getRoleReportList() {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        List<Long> roleIdList = permissionApi.getRoleIdListByUserId(loginUserId);
        if (CollUtil.isEmpty(roleIdList)) {
            throw exception(UNASSIGNED_ROLE);
        }
        List<ReportRoleDO> reportRoleDOList = reportRoleMapper.selectList(ReportRoleDO::getRoleId, roleIdList);
        if (CollUtil.isEmpty(reportRoleDOList)) {
            return null;
        }
        List<Long> reportIdList = reportRoleDOList.stream().map(ReportRoleDO::getReportId).toList();
        List<ReportDO> reportDOList = dynamicReportMapper.selectBatchIds(reportIdList);
        return ReportConvert.INSTANCE.convertReportRespVOList(reportDOList);
    }

    @Override
    public List<ReportRespVO> getAllReportList() {
        List<ReportDO> reportDOList = dynamicReportMapper.selectList(new LambdaQueryWrapperX<ReportDO>().isNull(ReportDO::getUserId));
        if (CollUtil.isEmpty(reportDOList)){
            return List.of();
        }
        return ReportConvert.INSTANCE.convertReportRespVOList(reportDOList);
    }

}