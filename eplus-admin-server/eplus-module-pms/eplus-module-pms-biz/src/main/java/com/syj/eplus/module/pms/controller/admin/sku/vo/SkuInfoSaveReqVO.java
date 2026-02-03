package com.syj.eplus.module.pms.controller.admin.sku.vo;

import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 15:31
 */
@Data
public class SkuInfoSaveReqVO extends SkuSaveReqVO {

    @Schema(description = "子产品id列表")
    private List<SimpleSkuSaveReqVO> subProductList;

    @Schema(description = "配件id列表")
    private List<SimpleSkuSaveReqVO> accessoriesList;

    @Schema(description = "供应商报价列表")
    private List<QuoteitemDTO> quoteitemList;

    @Schema(description = "辅料配比列表")
    private List<SkuAuxiliaryDO> skuAuxiliaryList;
}
