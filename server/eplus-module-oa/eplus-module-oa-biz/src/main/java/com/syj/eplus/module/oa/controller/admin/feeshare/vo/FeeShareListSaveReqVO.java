package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 费用归集新增/修改 Request VO")
@Data
public class FeeShareListSaveReqVO {

    List<FeeShareSaveReqVO> feeShare;

    private  Integer submitFlag;

    private Long businessId;
    private String businessCode;
    private Integer businessType;
    private Long companyId;
    private Long exhibitionId;
    private Long projectId;
    private String companyName;
}