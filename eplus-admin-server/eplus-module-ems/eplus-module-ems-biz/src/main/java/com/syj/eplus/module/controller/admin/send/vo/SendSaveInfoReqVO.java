package com.syj.eplus.module.controller.admin.send.vo;

import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class SendSaveInfoReqVO extends SendSaveReqVO {

    @Schema(description = "寄件产品列表")
    private List<SendProductSaveReqVO> children;

    @Schema(description = "费用归集")
    private List<FeeShareReqDTO> feeShare;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

}