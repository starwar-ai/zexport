package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 15:06
 */
@Data
public class SignBackReq {

    @Schema(description = "采购合同")
    private Long contractId;

    @Schema(description = "回签日期")
    private LocalDateTime signBackDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "回签附件")
    private List<SimpleFile> signBackAnnexList;
}
