package com.syj.eplus.module.sms.api;

import com.syj.eplus.module.sms.api.dto.SaleAuxiliaryAllocationDTO;

import java.util.List;
import java.util.Map;

public interface SaleAuxiliaryAllocationApi {

    /**
     * 根据销售合同编号查询分摊列表
     * @param code
     * @return id为采购合同明细id
     */
    Map<Long, List<SaleAuxiliaryAllocationDTO>> getListBySaleCode(String code);
}
