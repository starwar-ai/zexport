package com.syj.eplus.module.fms.controller.admin.payment.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

/**
 * @Description：付款确认请求参数
 * @Author：du
 * @Date：2024/4/28 18:01
 */
@Data
public class PaymentConfirmReqVO {

    private Long id;


    private JsonAmount amount;

    private Long companyId;
}
