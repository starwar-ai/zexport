package com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 供应商银行账户 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class VenderBankaccountRespVO implements ChangeExInterface {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14416")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本")
    private Integer ver;

    @Schema(description = "供应商id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17942")
    @ExcelProperty("供应商id")
    private Long venderId;

    @Schema(description = "供应商版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("供应商版本")
    private Integer venderVer;

    @Schema(description = "银行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("银行")
    private String bank;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16402")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "是否默认账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认账号")
    private Integer defaultFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "开户行地址")
    @ExcelProperty("开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

}