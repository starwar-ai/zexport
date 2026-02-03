package com.syj.eplus.module.dms.api;

import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.dms.enums.api.ShipmentPlanApi;
import com.syj.eplus.module.dms.service.shipmentplan.ShipmentPlanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/8 17:38
 */
@Service
public class ShipmentPlanApiImpl implements ShipmentPlanApi {
    @Resource
    private ShipmentPlanService service;

    @Override
    public List<CreatedResponse> createShipmentPlan(String createReqVO) {
        return service.createShipmentPlan(createReqVO);
    }
}
