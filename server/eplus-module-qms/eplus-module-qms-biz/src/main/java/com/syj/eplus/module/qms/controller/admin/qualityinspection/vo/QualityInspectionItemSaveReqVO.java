package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 验货单-明细新增/修改 Request VO")
@Data
public class QualityInspectionItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10700")
    private Long id;

    @Schema(description = "验货单主键", example = "15845")
    private Long inspectionId;

    @Schema(description = "产品主键", example = "27154")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "验货状态:1:待定 2：成功，3：失败")
    private Integer inspectionStatus;

    @Schema(description = "验货图片")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;

    @Schema(description = "失败描述")
    private String failDesc;

    @Schema(description = "上次失败描述")
    private String lastFailDesc;

    @Schema(description = "待定描述")
    private String pendingDesc;

    @Schema(description = "客户主键", example = "150")
    private Long custId;

    @Schema(description = "客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "验货费用")
    @ExcelProperty("验货费用")
    private JsonAmount amount;

    @Schema(description = "产品总价")
    private JsonAmount totalPrice;

    @Schema(description = "箱数", example = "23869")
    private Integer boxCount;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "处理标识:0-否 1-是")
    private Integer handleFlag;

    @Schema(description = "采购合同明细主键")
    private Long purchaseContractItemId;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "是否分箱")
    private Integer splitBoxFlag;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;
}
