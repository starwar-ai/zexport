package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 出差报销分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReimbPageReqVO extends PageParam {

    @Schema(description = "表单编码")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "是否还款")
    private Integer repayFlag;

    @Schema(description = "报销类型")
    private Integer reimbType;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "费用说明", example = "随便")
    private String description;

    @Schema(description = "出差申请编号", example = "13687")
    private Long travelAppId;

    @Schema(description = "报销主体", example = "10054")
    private Long companyId;

    @Schema(description = "报销人", example = "6102")
    private Long reimbUserId;

    @Schema(description = "报销部门", example = "28383")
    private Long reimbUserDeptId;

    @Schema(description = "费用归属类型")
    private Integer auxiliaryType;

    @Schema(description = "关联订单id")
    private Long contractId;

    @Schema(description = "报销金额最小值")
    private BigDecimal totalAmountMin;

    @Schema(description = "报销金额最大值")
    private BigDecimal totalAmountMax;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "费用申请id列表")
    private List<Long> applyIdList;

    @Schema(description = "部门数据权限")
    private DeptDataPermissionRespDTO deptDataPermission;

    @Schema(description = "币种", example = "随便")
    private String currency;
}