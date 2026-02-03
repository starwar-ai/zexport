package com.syj.eplus.module.controller.admin.send.vo.feeshare;

import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class FeeShareVendorRespVO   {

    @Schema(description = "客户信息")
    private VenderAllDTO vender;

}