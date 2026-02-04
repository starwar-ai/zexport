package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/17 15:38
 */
@Data
@Accessors(chain = false)
public class ShipmentCust implements ChangeExInterface {
    /**
     * 主键
     */
    private Long id;
    /**
     * 出货客户主键
     */
    private Long custId;
    /**
     * 出货客户编号
     */
    private String custCode;
    /**
     * 出货客户名称
     */
    private String custName;

    /**
     * 正面唛头
     */
    private String frontShippingMark;

    /**
     * 侧面唛头
     */
    private String sideShippingMark;

    /**
     * 收货人
     */
    private String receivePerson;
    /**
     * 通知人
     */
    private String notifyPerson;

    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;
}
