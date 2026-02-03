package com.syj.eplus.module.crm.controller.admin.mark.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 唛头 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class MarkRespVO implements ChangeExInterface {

    @Schema(description = "唛头名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("唛头名称")
    private String name;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    private Integer ver;

    @Schema(description = "唛头英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("唛头英文名称")
    private String engName;

    @Schema(description = "主文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主文字唛")
    private String mainMarkText;

    @Schema(description = "主图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主图形唛")
    private String mainMarkPic;

    @Schema(description = "主侧文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主侧文字唛")
    private String mainMarkTextSide;

    @Schema(description = "主侧图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主侧图形唛")
    private String mainMarkPicSide;

    @Schema(description = "内主文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("内主文字唛")
    private String mainMarkTextIn;

    @Schema(description = "内主图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("内主图形唛")
    private String mainMarkPicIn;

    @Schema(description = "内侧文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("内侧文字唛")
    private String mainMarkTextSideIn;

    @Schema(description = "内侧图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("内侧图形唛")
    private String mainMarkPicSideIn;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;
}