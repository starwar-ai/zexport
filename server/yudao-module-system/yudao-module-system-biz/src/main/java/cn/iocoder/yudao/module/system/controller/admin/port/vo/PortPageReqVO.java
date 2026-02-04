package cn.iocoder.yudao.module.system.controller.admin.port.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 口岸分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PortPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "国家id", example = "23727")
    private Long countryId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}