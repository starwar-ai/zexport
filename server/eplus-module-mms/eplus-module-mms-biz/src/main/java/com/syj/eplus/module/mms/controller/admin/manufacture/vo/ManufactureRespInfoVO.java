package com.syj.eplus.module.mms.controller.admin.manufacture.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 加工单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ManufactureRespInfoVO extends ManufactureRespVO {


    @Schema(description = "产品列表")
    @ExcelProperty(value = "产品列表")
    private List<ManufactureSkuSaveInfoReqVO> children;
}