package com.syj.eplus.module.fms.controller.admin.custclaim.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 19:30
 */
@Schema(description = "管理后台 - 客户认领单 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustClaimPageReq extends PageParam {
    private String code;

    private String companyTitle;

    private String custName;

    private String creator;

    private String custCode;

    private Long managerId;

    private Long companyId;

    private BigDecimal amountMin;

    private BigDecimal amountMax;

    private Integer claimStatus;
}
