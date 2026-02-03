package com.syj.eplus.module.pms.controller.admin.spu.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 商品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpuPageReqVO extends PageParam {

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "产品条码")
    private String barcode;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "品牌编号", example = "20012")
    private Long brandId;

    @Schema(description = "商品分类", example = "28690")
    private Long categoryId;

    @Schema(description = "产品类型", example = "2")
    private String spuType;

    @Schema(description = "计量单位", example = "1")
    private String unitType;

    @Schema(description = "商品状态")
    private Integer onshelfFlag;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "商品说明", example = "随便")
    private String description;

    @Schema(description = "商品说明英文")
    private String descriptionEng;

    @Schema(description = "海关编码id")
    private Long hsCodeId;

    @Schema(description = "是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "产品规格")
    private String spec;

    @Schema(description = "海关编码版本")
    private Integer hsCode_var;

}