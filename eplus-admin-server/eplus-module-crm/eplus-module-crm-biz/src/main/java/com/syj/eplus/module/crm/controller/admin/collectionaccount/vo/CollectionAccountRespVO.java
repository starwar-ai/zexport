package com.syj.eplus.module.crm.controller.admin.collectionaccount.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Schema(description = "管理后台 - 收款账号 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CollectionAccountRespVO   {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 默认标记
     */
    private Integer defaultFlag;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 内部公司主键
     */
    private Long companyId;
    /**
     * 内部公司银行账号主键
     */
    private Long companyBankId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "公司英文名称")
    private String companyNameEng;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行英文名称")
    private String bankNameEng;

    @Schema(description = "银行地址")
    private String bankAddress;

    @Schema(description = "银行英文地址")
    private String bankAddressEng;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "银行识别代码")
    private String swiftCode;

    @Schema(description = "客户交易币种")
    private String currency;

    @Schema(description = "客户价格条款")
    private String settlementTermType;


}