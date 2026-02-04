package com.syj.eplus.module.controller.admin.send.vo.feeshare;

import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class FeeShareSmsRespVO  {

    @Schema(description = "销售合同")
    private SmsContractAllDTO smsContract;

}