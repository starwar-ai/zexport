package com.syj.eplus.module.infra.api.paymentitem.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/19 21:10
 */
@Data
@Accessors(chain = false)
public class PaymentItemDTO implements ChangeExInterface {
    private Long id;

    private String code;

    private String name;

    private Long paymentId;

    private String nameEng;

    private Integer dateType;

    private Integer duration;

    List<SystemPaymentPlanDTO> systemPaymentPlanDTOList;

    private Integer changeFlag;

    private String sourceCode;

    private String effectRange;

    private Integer defaultFlag;

    private List<JsonEffectRange> effectRangeList;
}
