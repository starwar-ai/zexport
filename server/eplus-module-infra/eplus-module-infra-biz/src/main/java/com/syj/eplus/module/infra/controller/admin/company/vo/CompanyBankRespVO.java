package com.syj.eplus.module.infra.controller.admin.company.vo;

import com.syj.eplus.module.infra.dal.dataobject.company.CompanyBank;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 内部法人单位 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CompanyBankRespVO extends CompanyBank {


    @Schema(description = "公司性质")
    private Integer companyNature;


    @Schema(description = "启用标识")
    private Integer enableFlag;


    @Schema(description = "简称")
    private String shortname;

}
