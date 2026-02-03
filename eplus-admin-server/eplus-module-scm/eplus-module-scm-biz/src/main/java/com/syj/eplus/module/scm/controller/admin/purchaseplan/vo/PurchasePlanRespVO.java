package com.syj.eplus.module.scm.controller.admin.purchaseplan.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonCompanyPathTypeHandler;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购计划 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27260")
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "计划状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "计划状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.PURCHASE_PLAN_STATUS)
    private Integer planStatus;


    @Schema(description = "客户id", example = "14265")
    @ExcelProperty("客户id")
    @ExcelIgnore
    private Long custId;

    @Schema(description = "客户编号")
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "来源单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "来源单类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.PURCHASE_SOURCE_TYPE)
    private Integer sourceType;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;


    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    @ExcelIgnore
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    @ExcelIgnore
    private String creator;

    @Schema(description = "创建人名字")
    @ExcelProperty("创建人名字")
    @ExcelIgnore
    private String creatorName;

    @Schema(description = "创建人部门")
    @ExcelProperty("创建人部门")
    @ExcelIgnore
    private String creatorDeptName;

    @Schema(description = "采购产品总数量")
    @ExcelProperty("采购产品总数量")
    @ExcelIgnore
    private Integer itemCountTotal;

    @Schema(description = "附件")
    @ExcelProperty("附件")
    @ExcelIgnore
    private List<SimpleFile> annex;

    @Schema(description = "采购主体")
    @ExcelIgnore
    private Long companyId;


    @Schema(description = "采购主体名称", example = "泛太机电")
    @ExcelProperty("采购主体名称")
    private String companyName;


    @Schema(description = "计划日期")
    @ExcelProperty("计划日期")
    private LocalDateTime planDate;


    @Schema(description = "预计交期")
    @ExcelProperty("预计交期")
    private LocalDateTime estDeliveryDate;

    @Schema(description = "是否辅料采购")
    @ExcelProperty(value = "是否辅料采购"  )
    private Integer auxiliaryFlag;
    /**
     * 销售合同主键
     */
    private Long saleContractId;

    /**
     * 销售合同编号
     */
    private String saleContractCode;
    /**
     * 订单路径
     */
    @CompareField(value = "订单路径")
    @TableField(typeHandler = JsonCompanyPathTypeHandler.class)
    private JsonCompanyPath companyPath;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;


    @Schema(description = "自动生成标记")
    private Integer autoFlag;


    /**
     * 业务员
     */
    private UserDept sales;

    /**
     * 数据权限
     */
    private List<UserDept> purchaseUserList;

    @Schema(description = "来源计划id")
    private Long sourcePlanId;

    @Schema(description = "拆分标识")
    private Integer splitFlag;

    @Schema(description = "辅料属于的采购员")
    private List<UserDept> auxiliaryPurchaseUser;

    @Schema(description = "辅料属于的销售员")
    private List<UserDept> auxiliarySales;

    @Schema(description = "辅料属于的跟单员")
    private List<UserDept> auxiliaryManager;

    @Schema(description = "销售类型")
    private Integer saleType;
}
