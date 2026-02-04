package com.syj.eplus.module.oa.api.dto;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleRepayRespDTO {
    /**
     * 关联单号
     */
    private String code;

    /**
     * 还款方式
     */
    private Integer repayType;

    /**
     * 还款时间
     */
    private LocalDateTime repayTime;

    /**
     * 还款人
     */
    private UserDept repayer;

    /**
     * 报销人id
     */
    private UserDept reimbUser;

    /**
     * 还款金额
     */
    private JsonAmount repayAmount;

    /**
     * 还款状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer repayStatus;

    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 供应商编号
     */
    private String venderCode;

    /**
     * 还款来源类型
     */
    private Integer repaySourceType;

    /**
     * 还款来源编号
     */
    private String repaySourceCode;
}
