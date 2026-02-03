package com.syj.eplus.module.mms.controller.admin.manufacturesku.vo;


import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 加工单产品新增/修改 Request VO")
@Data
public class ManufactureSkuSaveInfoReqVO extends ManufactureSkuSaveReqVO  {

    @Schema(description = "子产品列表")
   private List<ManufactureSkuItemSaveReqVO> skuItemList;

}