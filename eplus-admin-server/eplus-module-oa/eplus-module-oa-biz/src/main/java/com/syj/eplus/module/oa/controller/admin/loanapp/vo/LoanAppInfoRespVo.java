package com.syj.eplus.module.oa.controller.admin.loanapp.vo;

import com.syj.eplus.module.oa.api.dto.SimpleRepayRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/1/31 09:40
 */
@Data
public class LoanAppInfoRespVo extends LoanAppRespVO {

    @Schema(description = "任务id")
    private String processInstanceId;


    @Schema(description = "还款信息")
    private List<SimpleRepayRespDTO> repayList;

}
