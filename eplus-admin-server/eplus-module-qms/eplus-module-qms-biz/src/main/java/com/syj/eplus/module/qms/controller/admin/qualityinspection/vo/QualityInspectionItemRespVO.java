package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 验货单-明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QualityInspectionItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10700")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "验货单主键", example = "15845")
    @ExcelProperty("验货单主键")
    private Long inspectionId;

    @Schema(description = "产品主键", example = "27154")
    @ExcelProperty("产品主键")
    private Long skuId;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    @ExcelProperty("产品中文名称")
    private String skuName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "图片")
    @ExcelProperty("图片")
    @CompareField
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "验货状态:1:待定 2：成功，3：失败，4：待验货 5：放行通过", example = "2")
    @ExcelProperty("验货状态:1:待定 2：成功，3：失败，4：待验货 5：放行通过")
    private Integer inspectionStatus;

    @Schema(description = "验货图片")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;

    @Schema(description = "失败描述")
    @ExcelProperty("失败描述")
    private String failDesc;

    @Schema(description = "上次失败描述")
    @ExcelProperty("上次失败描述")
    private String lastFailDesc;

    @Schema(description = "待定描述")
    @ExcelProperty("待定描述")
    private String pendingDesc;

    @Schema(description = "数量")
    @ExcelProperty("数量")
    private Integer quantity;

    @Schema(description = "验货费用")
    @ExcelProperty("验货费用")
    private JsonAmount amount;

    @Schema(description = "产品总价")
    @ExcelProperty("产品总价")
    private JsonAmount totalPrice;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "采购合同明细主键")
    @ExcelProperty("采购合同明细主键")
    private Long purchaseContractItemId;

    @Schema(description = "采购员主键", example = "26783")
    @ExcelProperty("采购员主键")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "王五")
    @ExcelProperty("采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门主键", example = "6217")
    @ExcelProperty("采购员部门主键")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称", example = "王五")
    @ExcelProperty("采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "销售合同号")
    @ExcelProperty("销售合同号")
    private String saleContractCode;

    @Schema(description = "销售员主键", example = "15028")
    @ExcelProperty("销售员主键")
    private Long saleUserId;

    @Schema(description = "销售员姓名", example = "王五")
    @ExcelProperty("销售员姓名")
    private String saleUserName;

    @Schema(description = "销售员部门主键", example = "18061")
    @ExcelProperty("销售员部门主键")
    private Long saleUserDeptId;

    @Schema(description = "销售员部门名称", example = "王五")
    @ExcelProperty("销售员部门名称")
    private String saleUserDeptName;

    @Schema(description = "跟单员主键", example = "4026")
    @ExcelProperty("跟单员主键")
    private Long trackUserId;

    @Schema(description = "跟单员姓名", example = "王五")
    @ExcelProperty("跟单员姓名")
    private String trackUserName;

    @Schema(description = "跟单员部门主键", example = "5837")
    @ExcelProperty("跟单员部门主键")
    private Long trackUserDeptId;

    @Schema(description = "跟单员部门名称", example = "张三")
    @ExcelProperty("跟单员部门名称")
    private String trackUserDeptName;

    @Schema(description = "客户主键", example = "150")
    @ExcelProperty("客户主键")
    private Long custId;

    @Schema(description = "客户编码")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "箱数", example = "23869")
    @ExcelProperty("箱数")
    private Integer boxCount;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "处理标识:0-否 1-是")
    private Integer handleFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "处理状态")
    private Integer handleState;

    private String reworkDesc;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "是否分箱")
    private Integer splitBoxFlag;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "包装方式英文")
    private String packageTypeEngName;

    @Schema(description = "包装方式中文")
    private String packageTypeName;

    @Schema(description = "基础产品编码")
    private String basicSkuCode;
}
