package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.LocalDateOnlyConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 采购合同产品模式导出 VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class PurchaseContractProductExportVO {

    @ExcelProperty("采购合同单号")
    private String code;

    @ExcelProperty("采购主体")
    private String companyName;

    @ExcelProperty("供应商")
    private String venderName;

    @ExcelProperty("单据状态")
    private Integer contractStatus;

    @ExcelProperty("采购员")
    private String purchaseUserNickname;

    @ExcelProperty("跟单员")
    private String managerNickname;

    @ExcelProperty(value = "采购日期", converter = LocalDateOnlyConvert.class)
    private LocalDateTime createTime;

    @ExcelProperty("序号")
    private Integer sortNum;

    @ExcelProperty("缩略图")
    private WriteCellData<Void> productImage;

    @ExcelProperty("产品编号")
    private String skuCode;

    @ExcelProperty("客户货号")
    private String cskuCode;

    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @ExcelProperty("品名")
    private String name;

    @ExcelProperty("工厂货号")
    private String venderProdCode;

    @ExcelProperty(value = "到料时间", converter = LocalDateOnlyConvert.class)
    private LocalDateTime planArriveDate;

    @ExcelProperty("验货结果")
    private Integer itemCheckStatus;

    @ExcelProperty(value = "采购单价", converter = AmountConvert.class)
    private JsonAmount withTaxPrice;

    @ExcelProperty("采购数量")
    private Integer quantity;

    @ExcelProperty("入库地点")
    private Integer billStatus;

    @ExcelProperty("待转入库通知数量")
    private Integer unNoticedQuantity;

    @ExcelProperty("实际入库数量")
    private Integer billQuantity;

    @ExcelProperty(value = "采购金额（含税总计）", converter = AmountConvert.class)
    private JsonAmount withTaxTotal;

    @ExcelProperty(value = "采购金额（人民币）", converter = AmountConvert.class)
    private JsonAmount totalPriceRmb;

    @ExcelProperty("采购类型")
    private Integer purchaseType;

    @ExcelProperty("开票通知状态")
    private Integer invoiceStatus;
}
