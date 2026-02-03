package com.syj.eplus.module.wms.controller.admin.warehouse.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓库管理-仓库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WarehouseRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28362")
    private Long id;

    @Schema(description = "仓库编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("仓库编码")
    private String code;

    @Schema(description = "仓库名称", example = "李四")
    @ExcelProperty("仓库名称")
    private String name;

    @Schema(description = "仓库地址")
    @ExcelProperty("仓库地址")
    private String address;

    @Schema(description = "启用标识  0-否 1-是")
    @ExcelProperty(value = "启用标识", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer enableFlag;

    @Schema(description = "仓管主键", example = "17026")
    private List<Long> managerIds;

    @Schema(description = "仓管名称")
    @ExcelProperty(value = "仓管名称")
    private String managerName;

    @Schema(description = "供应仓标识0-否 1-是")
    @ExcelProperty(value = "供应仓标识", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer venderFlag;


    @Schema(description = "供应商编码", example = "6393")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建个人")
    private String creator;

    @Schema(description = "创建个人-姓名")
    @ExcelProperty("创建人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "默认仓库")
    private Integer defaultFlag;
}
