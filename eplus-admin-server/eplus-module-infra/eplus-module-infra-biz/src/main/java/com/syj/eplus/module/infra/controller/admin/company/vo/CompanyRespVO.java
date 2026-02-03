package com.syj.eplus.module.infra.controller.admin.company.vo;

import com.syj.eplus.module.infra.dal.dataobject.company.CompanyBank;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.DictSimpleFileList;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 内部法人单位 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CompanyRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3142")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "单位名称")
    private String name;

    @Schema(description = "公司性质")
    private Integer companyNature;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业英文名称")
    private String companyNameEng;

    @Schema(description = "营业执照")
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile license;

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "企业地址")
    private String companyAddress;

    @Schema(description = "企业英文地址")
    private String companyAddressEng;

    @Schema(description = "管理员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    @Schema(description = "管理员手机号")
    private String managerMobile;

    @Schema(description = "管理员邮箱")
    private String managerMail;

    @Schema(description = "企业电话")
    private String companyTel;

    @Schema(description = "企业传真")
    private String companyFax;

    @Schema(description = "法人")
    private String legalPerson;

    @Schema(description = "启用标识")
    private Integer enableFlag;

    @Schema(description = "公司银行信息列表")
    private List<CompanyBank> companyBankList;

    @Schema(description = "简称")
    private String shortname;

    @Schema(description = "图片")
    private List<DictSimpleFileList> picture;

    @Schema(description = "可用币种")
    private List<String> availableCurrencyList;

    @Schema(description = "税号")
    private String taxNumb;

    @Schema(description = "海关编号")
    private String customsNumber;
}
