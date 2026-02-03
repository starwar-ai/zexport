package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/12 9:43
 */
@Data
public class PushOutShipmentPlanReq {
    private Long companyId;

    private String companyName;

    private List<Long> childrenIdList;
}
