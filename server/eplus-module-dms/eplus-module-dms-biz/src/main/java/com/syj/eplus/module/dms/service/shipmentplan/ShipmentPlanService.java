package com.syj.eplus.module.dms.service.shipmentplan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.*;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 出运计划单 Service 接口
 *
 * @author du
 */
public interface ShipmentPlanService {

    /**
     * 创建出运计划单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createShipmentPlan(@Valid ShipmentPlanSaveReqVO createReqVO);

    /**
     * 创建出运计划单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createShipmentPlan(String createReqVO);

    /**
     * 更新出运计划单
     *
     * @param updateReqVO 更新信息
     */
    void updateShipmentPlan(@Valid ShipmentPlanSaveReqVO updateReqVO);

    /**
     * 删除出运计划单
     *
     * @param id 编号
     */
    void deleteShipmentPlan(Long id);

    /**
     * 获得出运计划单
     *
     * @param  id 编号
     * @return 出运计划单
     */
    ShipmentPlanRespVO getShipmentPlan(Long id,boolean isUpdate);

    /**
     * 获得出运计划单分页
     *
     * @param pageReqVO 分页查询
     * @return 出运计划单分页
     */
    PageResult<ShipmentPlanRespVO> getShipmentPlanPage(ShipmentPlanPageReqVO pageReqVO);


    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportExcel(Long id, String reportCode, HttpServletResponse response);
    /**
     * 结案
     *
     * @param closeShipmentPlanReq 请求
     */
    void closeShipmentPlan(CloseShipmentPlanReq closeShipmentPlanReq);

    /**
     * 批量获取出运计划明细
     *
     * @param ids 出运计划id列表
     * @return 出运计划明细列表
     */
    List<ShipmentPlanItem> getShipmentPlanItemList(List<Long> ids);

    /**
     * 批量获取出运计划
     *
     * @param ids 出运计划id列表
     * @return 出运计划列表
     */
    List<ShipmentPlanDO> getShipmentPlanList(Collection<Long> ids);

    /**
     * 获取关联单据数量
     *
     * @param id 出运计划id
     * @return 关联单据数量
     */
    RelatedShipmentPlanRespVO getRelatedNum(Long id);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    /**
     * 更新出运计划订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);
}