package com.syj.eplus.module.dms.service.settlementform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormPageReqVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormRespVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormSaveReqVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 结汇单 Service 接口
 *
 * @author du
 */
public interface SettlementFormService {

    /**
     * 创建结汇单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    CreatedResponse createSettlementForm(@Valid SettlementFormSaveReqVO createReqVO);

    /**
     * 更新结汇单
     *
     * @param updateReqVO 更新信息
     */
    void updateSettlementForm(@Valid SettlementFormSaveReqVO updateReqVO);

    /**
     * 删除结汇单
     *
     * @param id 编号
     */
    @Deprecated
    void deleteSettlementForm(Long id);

    /**
     * 批量删除结汇单
     * @param idList 批量删除的编号
     */
    void batchDeleteSettlementForm(List<Long> idList);

    /**
     * 获得结汇单
     *
     * @param  id 编号
     * @return 结汇单
     */
    SettlementFormRespVO getSettlementForm(Long id);

    /**
     * 获得结汇单分页
     *
     * @param pageReqVO 分页查询
     * @return 结汇单分页
     */
    PageResult<SettlementFormRespVO> getSettlementFormPage(SettlementFormPageReqVO pageReqVO);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @param exportType 导出类型
     * @param sourceCode 来源编码
     * @param sourceType 来源类型
     * @return
     */
    void exportExcel(Long id, String reportCode, String exportType, String sourceCode, Integer sourceType, HttpServletResponse response);
}