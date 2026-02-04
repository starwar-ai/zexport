package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 费用归集分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FeeSharePageReqVO extends PageParam {

    @Schema(description = "来源类型", example = "2")
    private Integer businessType;

    @Schema(description = "来源单编号")
    private String businessCode;

    @Schema(description = "业务部门id", example = "26115")
    private Long deptId;

    @Schema(description = "业务部门名称", example = "李四")
    private String deptName;

    @Schema(description = "费用归属类别", example = "1")
    private Integer feeShareType;

    @Schema(description = "相关方类别", example = "2")
    private Integer relationType;

    @Schema(description = "具体名称id", example = "26364")
    private Long descId;

    @Schema(description = "预归集标记", example = "26364")
    private Integer preCollectionFlag;

    @Schema(description = "具体名称", example = "芋艿")
    private String descName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "状态", example = "芋艿")
    private Integer auditStatus;
    private Integer notAuditStatus;

    @Schema(description = "状态", example = "芋艿")
    private Integer status;

    @Schema(description = "来源单据状态")
    private Integer sourceStatus;

    private UserDept inputUser;

    @Schema(description = "订单类型")
    private Integer orderType;

    @Schema(description = "公司id")
    private Long companyId;

    @Schema(description = "申请人id")
    private String inputUserId;

    @Schema(description = "申请人部门")
    private String inputDeptId;
}