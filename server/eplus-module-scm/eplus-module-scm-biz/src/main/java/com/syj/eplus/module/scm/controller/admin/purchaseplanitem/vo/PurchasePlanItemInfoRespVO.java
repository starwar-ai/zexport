package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanItemInfoRespVO extends PurchasePlanItemRespVO  {


    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;


    @Schema(description = "来源单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("来源单类型")
    private Integer sourceType;


    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    @ExcelIgnore
    private LocalDateTime updateTime;

    @Schema(description = "计划交期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("计划交期")
    @ExcelIgnore
    private LocalDateTime planDate;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    @ExcelIgnore
    private String creator;

    @Schema(description = "创建人名字")
    @ExcelProperty("创建人名字")
    @ExcelIgnore
    private String creatorName;


    @Schema(description = "采购主体")
    @ExcelProperty("采购主体")
    private Long companyId;


    @Schema(description = "采购主体名称", example = "泛太机电")
    @ExcelProperty("采购主体名称")
    private String companyName;


    //合同字段
    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    private String contractCode;

    @Schema(description = "采购量")
    @ExcelProperty("采购量")
    private Integer contractQuantity;

    @Schema(description = "到货量")
    @ExcelProperty("到货量")
    private Integer contractReceivedQuantity;

    @Schema(description = "验货量")
    @ExcelProperty("验货量")
    private Integer contractCheckedQuantity;

    @Schema(description = "退货量")
    @ExcelProperty("退货量")
    private Integer contractReturnedQuantity;

    @Schema(description = "换货量")
    @ExcelProperty("换货量")
    private Integer contractExchangedQuantity;

    @Schema(description = "下单时间")
    @ExcelProperty("下单时间")
    private LocalDateTime contractCreateTime;


    @Schema(description = "预计到料时间")
    @ExcelProperty("预计到料时间")
    private LocalDateTime contractPlanTime;

    @Schema(description = "转合同状态")
    private Integer convertedFlag;
    /**
     * 生成采购单时间
     */
    @CompareField(value = "生成采购单时间")
    private LocalDateTime convertTime;


    @Schema(description = "运费")
    @ExcelProperty(value = "运费", converter = AmountConvert.class)
    private JsonAmount contractFreight;

    @Schema(description = "其他费用")
    @ExcelProperty(value = "其他费用", converter = AmountConvert.class)
    private JsonAmount contractOtherCost;


    @Schema(description = "采购链接")
    @ExcelProperty(value = "采购链接")
    private String purchaseUrl;


    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    @Schema(description = "计量单位")
    @ExcelProperty("计量单位")
    private Integer measureUnit;

    @Schema(description = "产品删除标记")
    @ExcelProperty("产品删除标记")
    private Integer skuDeletedFlag;

    private List<SkuSimpleVO> auxiliarySkuList;

}
