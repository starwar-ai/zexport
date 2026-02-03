package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class SimpleCustSaveResp extends PageParam {
    private Long id;

    private String code;

    private String name;
    //正面唛头
    private String frontShippingMark;
    //侧面唛头
    private String sideShippingMark;
}
