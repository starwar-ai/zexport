package com.syj.eplus.module.pms.controller.admin.brand.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 品牌分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BrandPageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "流水号长度")
    private String nameEng;

    @Schema(description = "商品说明")
    private String description;

    @Schema(description = "商品说明英文")
    private String descriptionEng;

    @Schema(description = "是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "品牌类型")
    private Integer brandType;

    @Schema(description = "客户id")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "品牌logo")
    private SimpleFile logo;

}