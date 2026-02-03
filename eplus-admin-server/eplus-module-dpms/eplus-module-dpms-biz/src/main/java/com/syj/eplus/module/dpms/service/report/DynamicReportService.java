package com.syj.eplus.module.dpms.service.report;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportRespVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.report.ReportDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 报表配置 Service 接口
 *
 * @author du
 */
public interface DynamicReportService {

    /**
     * 创建报表配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReport(@Valid ReportSaveReqVO createReqVO);

    /**
     * 更新报表配置
     *
     * @param updateReqVO 更新信息
     */
    void updateReport(@Valid ReportSaveReqVO updateReqVO);

    /**
     * 删除报表配置
     *
     * @param id 编号
     */
    void deleteReport(Long id);

    /**
     * 获得报表配置
     *
     * @param id 编号
     * @return 报表配置
     */
    ReportRespVO getReport(Long id);

    /**
     * 获得报表配置分页
     *
     * @param pageReqVO 分页查询
     * @return 报表配置分页
     */
    PageResult<ReportDO> getReportPage(ReportPageReqVO pageReqVO);

    /**
     * 批量创建报表
     *
     * @param reportIds 报表列表
     * @return 编号
     */
    Boolean batchCreateReport(List<Long> reportIds);

    /**
     * 获得报表配置列表
     *
     * @return 报表配置列表
     */
    List<ReportRespVO> getReportList();

    /**
     * 获取当前角色报表配置列表
     * @return 报表配置列表
     */
    List<ReportRespVO> getRoleReportList();

    /**
     * 获取全部报表配置列表
     * @return 报表配置列表
     */
    List<ReportRespVO> getAllReportList();
}