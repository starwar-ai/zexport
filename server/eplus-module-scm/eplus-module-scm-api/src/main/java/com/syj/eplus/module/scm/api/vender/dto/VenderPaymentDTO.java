package com.syj.eplus.module.scm.api.vender.dto;

import com.sun.xml.bind.v2.TODO;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/06/14:31
 * @Description:
 */
@Data
public class VenderPaymentDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 结汇方式编号
     */
    private Long paymentId;

    /**
     * 付款方式名称
     */
    private String paymentName;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 是否缺省
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer defaultFlag = 0;

}
