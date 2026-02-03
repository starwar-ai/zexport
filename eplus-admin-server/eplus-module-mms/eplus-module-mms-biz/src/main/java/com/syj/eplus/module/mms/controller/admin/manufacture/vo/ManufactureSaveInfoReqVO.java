package com.syj.eplus.module.mms.controller.admin.manufacture.vo;


import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class ManufactureSaveInfoReqVO extends ManufactureSaveReqVO {

    @Schema(description = "产品列表")
     private List<ManufactureSkuSaveInfoReqVO> children;

}