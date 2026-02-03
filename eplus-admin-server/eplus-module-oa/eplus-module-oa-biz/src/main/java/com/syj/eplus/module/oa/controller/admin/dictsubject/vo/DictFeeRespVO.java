package com.syj.eplus.module.oa.controller.admin.dictsubject.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 类别配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DictFeeRespVO {

    private Long id;

    @Schema(description = "费用名称")
    @ExcelProperty("费用名称")
    private String feeName;

    @Schema(description = "费用描述")
    @ExcelProperty("费用描述")
    private String feeDesc;

    /**
     * 是否在描述展示
     */
    private Integer showDescFlag;

    /**
     * 是否在费用实际展示
     */
    private Integer showFeeFlag;
}