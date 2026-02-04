package com.syj.eplus.module.dms.service.declaration;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationPageReqVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationRespVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.enums.api.dto.DeclarationItemDTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 报关单 Service 接口
 *
 * @author du
 */
public interface DeclarationService {

    /**
     * 创建报关单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    CreatedResponse createDeclaration(@Valid DeclarationSaveReqVO createReqVO);

    /**
     * 更新报关单
     *
     * @param updateReqVO 更新信息
     */
    void updateDeclaration(@Valid DeclarationSaveReqVO updateReqVO);

    /**
     * 删除报关单
     *
     * @param id 编号
     */
    void deleteDeclaration(Long id);

    /**
     * 获得报关单
     *
     * @param  id 编号
     * @return 报关单
     */
    DeclarationRespVO getDeclaration(Long id);

    /**
     * 获得报关单分页
     *
     * @param pageReqVO 分页查询
     * @return 报关单分页
     */
    PageResult<DeclarationRespVO> getDeclarationPage(DeclarationPageReqVO pageReqVO);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportExcel(Long id, String exportType , String reportCode, HttpServletResponse response);

    Map<Long, DeclarationItemDTO> getDeclarationListByShipmentId(List<Long> list);

    void updateDeclarationGroupBy(DeclarationSaveReqVO updateReqVO);
}