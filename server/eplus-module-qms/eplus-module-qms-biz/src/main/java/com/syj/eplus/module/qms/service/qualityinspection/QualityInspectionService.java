package com.syj.eplus.module.qms.service.qualityinspection;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.qms.api.dto.QualityInspectionReqDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.*;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionItemDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 验货单 Service 接口
 *
 * @author ePlus
 */
public interface QualityInspectionService extends IService<QualityInspectionDO> {

    /**
     * 创建验货单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createQualityInspection(@Valid QualityInspectionSaveReqVO createReqVO);

    /**
     * 更新验货单
     *
     * @param updateReqVO 更新信息
     */
    boolean updateQualityInspection(@Valid QualityInspectionSaveReqVO updateReqVO);

    /**
     * 删除验货单
     *
     * @param id 编号
     */
    void deleteQualityInspection(Long id);

    /**
     * 获得验货单
     *
     * @param qualityInspectionDetailReq 明细请求实体
     * @return 验货单
     */
    QualityInspectionRespVO getQualityInspection(QualityInspectionDetailReq qualityInspectionDetailReq);

    /**
     * 获得验货单分页
     *
     * @param pageReqVO 分页查询
     * @return 验货单分页
     */
    PageResult<QualityInspectionRespVO> getQualityInspectionPage(QualityInspectionPageReqVO pageReqVO);

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
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);
    // ==================== 子表（验货单-明细） ====================

    /**
     * 获得验货单-明细分页
     *
     * @param pageReqVO    分页查询
     * @param inspectionId 验货单主键
     * @return 验货单-明细分页
     */
    PageResult<QualityInspectionItemRespVO> getQualityInspectionItemPage(PageParam pageReqVO, Long inspectionId);

    /**
     * 创建验货单-明细
     *
     * @param qualityInspectionItem 创建信息
     * @return 编号
     */
    Long createQualityInspectionItem(@Valid QualityInspectionItemDO qualityInspectionItem);

    /**
     * 更新验货单-明细
     *
     * @param qualityInspectionItem 更新信息
     */
    void updateQualityInspectionItem(@Valid QualityInspectionItemDO qualityInspectionItem);

    /**
     * 删除验货单-明细
     *
     * @param id 编号
     */
    void deleteQualityInspectionItem(Long id);

    /**
     * 获得验货单-明细
     *
     * @param id 编号
     * @return 验货单-明细
     */
    QualityInspectionItemDO getQualityInspectionItem(Long id);

    /**
     * 获得验货单-明细
     *
     * @param inspectionIdList 编号
     * @return 验货单-明细
     */
    List<QualityInspectionItemDO> listItemByInspectionIdList(List<Long> inspectionIdList);

    /**
     * 根据采购合同主键获取验货单数量
     * @param code 采购合同编号
     * @return 验货单数量
     */
    Long getQualityInspectionNumByPurchaseContractCode(String code);

    List<QualityInspectionDO> getQualityInspectionListByIdList(List<Long> quanlityInspectionIdList);

    Map<Long,List<QualityInspectionItemDO>> getItemListByQualityInspectionIdList(List<Long> quanlityInspectionIdList);

    void setConcessionReleaseStatus(List<Long> ids, Integer value,List<SimpleFile>  annex);

    void setStatus(List<Long> auditableId, List<SimpleFile> annex,String description, List<SimpleFile> picture);

    List<QualityInspectionRespDTO> getDTOList(QualityInspectionReqDTO req);

    /**
     * 返工重验
     * @param reworkReqVO
     * @return
     */
    Boolean reworkQualityInspection(QualityInspectionReworkReqVO reworkReqVO);

    Long checkItemList(List<Long> qualityInspectionItemIdList);

    /**
     * 验货单作废
     * @param id 验货单id
     */
    void closeInspection(Long id);

    Boolean updateQualityInspectionAmount(QualityInspectionSaveAmountReqVO updateReqVO);

    /**
     * 导出验货单 Excel（支持单据维度和产品维度）
     *
     * @param pageReqVO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportQualityInspectionExcel(QualityInspectionPageReqVO pageReqVO, HttpServletResponse response) throws IOException;

    /**
     * 获取精简验货列表
     * @param saleContractItemId 销售明细主键
     * @return 精简验货列表
     */
    List<SimpleQulityInspectionRespVO> getSimpleList(Long saleContractItemId);

    /**
     * 导出Word
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportWord(Long id, String reportCode, Long reportId, Long companyId, HttpServletResponse response);

    /**
     * 导出Excel，明细区块（第9-11行）如有多条明细则向下复制，循环填充每条明细内容，实现明细块重复显示。
     * @param id 验货单id
     * @param reportCode 模板code
     * @param response HttpServletResponse
     */
    void exportExcel(Long id, String reportCode, HttpServletResponse response);
}
