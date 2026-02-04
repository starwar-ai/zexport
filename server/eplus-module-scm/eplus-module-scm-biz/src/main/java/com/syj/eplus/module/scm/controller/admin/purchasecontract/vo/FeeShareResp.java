package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 归集列表 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class FeeShareResp {



    @Schema(description = "发票号")
    private String code;

    @Schema(description = "归集金额")
    private JsonAmount amount;

    @Schema(description = "费用归集说明")
    private String desc;


}
