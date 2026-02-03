package com.syj.eplus.module.oa.controller.admin.otherreimb.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 其他费用报销详情 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OtherReimbFeeShareRespVO extends FeeShareReqDTO {


}
