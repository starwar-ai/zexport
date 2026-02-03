package com.syj.eplus.module.wms.controller.admin.warehouse.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓库管理-仓库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WarehousePageReqVO extends PageParam {

    @Schema(description = "仓库编码")
    private String code;

    @Schema(description = "仓库名称", example = "李四")
    private String name;

    @Schema(description = "仓库地址")
    private String address;

    @Schema(description = "启用标识  0-否 1-是")
    private Integer enableFlag;

    @Schema(description = "仓管主键", example = "17026")
    private Long managerId;

    @Schema(description = "供应仓标识0-否 1-是")
    private Integer venderFlag;

    @Schema(description = "供应仓类型 1- 在制仓 2-虚拟仓")
    private Integer venderWmsType;

    @Schema(description = "供应商主键", example = "6393")
    private Long venderId;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
