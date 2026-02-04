package com.syj.eplus.module.dms.service.commodityinspection;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionPageReqVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionRespVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 商检单 Service 接口
 *
 * @author du
 */
public interface CommodityInspectionService {

    /**
     * 创建商检单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    CreatedResponse createCommodityInspection(@Valid CommodityInspectionSaveReqVO createReqVO);

    /**
     * 更新商检单
     *
     * @param updateReqVO 更新信息
     */
    void updateCommodityInspection(@Valid CommodityInspectionSaveReqVO updateReqVO);

    /**
     * 删除商检单
     *
     * @param id 编号
     */
    void deleteCommodityInspection(Long id);

    /**
     * 获得商检单
     *
    * @param  id 编号
         * @return 商检单
         */
    CommodityInspectionRespVO getCommodityInspection(Long id);

    /**
     * 获得商检单分页
     *
     * @param pageReqVO 分页查询
     * @return 商检单分页
     */
    PageResult<CommodityInspectionRespVO> getCommodityInspectionPage(CommodityInspectionPageReqVO pageReqVO);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportExcel(Long id, String reportCode, HttpServletResponse response);
}