package com.syj.eplus.module.dms.enums.api;

import com.syj.eplus.framework.common.entity.CreatedResponse;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/8 17:37
 */
public interface ShipmentPlanApi {

    /**
     * 创建出运计划单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createShipmentPlan(String createReqVO);
}
