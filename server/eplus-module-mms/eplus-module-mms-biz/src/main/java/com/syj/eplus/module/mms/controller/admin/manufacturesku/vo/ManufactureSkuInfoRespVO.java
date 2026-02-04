package com.syj.eplus.module.mms.controller.admin.manufacturesku.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 加工单产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ManufactureSkuInfoRespVO extends ManufactureSkuRespVO {

    @Schema(description = "子产品列表")
    private List<ManufactureSkuItemRespVO> children;
    
}