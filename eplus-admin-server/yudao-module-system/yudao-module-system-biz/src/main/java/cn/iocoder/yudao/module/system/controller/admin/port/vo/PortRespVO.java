package cn.iocoder.yudao.module.system.controller.admin.port.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 口岸 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PortRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24999")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "是否置顶")
    private Integer topFlag;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "英文名称")
    @ExcelProperty("英文名称")
    private String nameEng;

    @Schema(description = "名称", example = "张三")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "城市")
    @ExcelProperty("城市")
    private String city;

    @Schema(description = "地址")
    @ExcelProperty("地址")
    private String address;

    @Schema(description = "状态")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "国家id", example = "23727")
    @ExcelProperty("国家id")
    private Long countryId;


    @Schema(description = "国家id", example = "23727")
    @ExcelProperty("国家id")
    private String countryName;


    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}