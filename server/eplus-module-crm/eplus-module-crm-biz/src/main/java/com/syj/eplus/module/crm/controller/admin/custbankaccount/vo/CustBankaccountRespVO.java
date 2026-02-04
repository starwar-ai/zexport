package com.syj.eplus.module.crm.controller.admin.custbankaccount.vo;

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

@Schema(description = "管理后台 - 银行账户信息 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class CustBankaccountRespVO implements ChangeExInterface {

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    private Integer ver;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行账户", requiredMode = Schema.RequiredMode.REQUIRED, example = "4349")
    @ExcelProperty("银行账户")
    private String bankAccount;

    @Schema(description = "是否默认账户0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认账户0-否，1-是")
    private Integer defaultFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "开户行地址")
    @ExcelProperty("开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行账号")
    @ExcelProperty("银行账号")
    private String bankCode;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;
}