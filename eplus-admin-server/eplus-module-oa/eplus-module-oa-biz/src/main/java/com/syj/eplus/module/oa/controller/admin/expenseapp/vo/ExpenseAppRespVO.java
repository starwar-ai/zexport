package com.syj.eplus.module.oa.controller.admin.expenseapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.entity.JsonExpenseAppItem;
import com.syj.eplus.module.oa.handler.JsonExpenseAppItemListHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 招待费申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExpenseAppRespVO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编号")
    private String spNo;

    @Schema(description = "申请人")
    private Long applyerId;

    @Schema(description = "日期")
    private LocalDateTime expenseDate;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "我司陪同人员")
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> companions;

    @Schema(description = "招待人数")
    private Integer entertainNum;

    @Schema(description = "费用归属订单")
    private String orderNo;

    @Schema(description = "客户等级")
    private String custLevel;

    @Schema(description = "本次招待成效")
    private String entertainEffect;

    @Schema(description = "关联申请单编号")
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> relateAppCode;

    @Schema(description = "明细")
    @TableField(typeHandler = JsonExpenseAppItemListHandler.class)
    private List<JsonExpenseAppItem> children;
}
