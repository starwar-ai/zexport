package com.syj.eplus.module.controller.admin.send.vo.feeshare;

import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Resource;
import java.util.List;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class FeeShareReqVO {

    @Resource(description = "费用归集列表")
    private List<FeeShareReqDTO> feeShare;

    @Schema(description = "寄件id")
    private  Long sendId;

    @Schema(description = "寄件编号")
    private  String businessCode;

}