package com.syj.eplus.module.dms.enums.api;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.dms.enums.api.dto.DmsForwarderFeeDTO;
import com.syj.eplus.module.dms.enums.api.dto.ForwarderFeeDTO;

import java.util.List;
import java.util.Map;

public interface ForwarderFeeApi {


    void updateStatusByCodeList(DmsForwarderFeeDTO dmsForwarderFeeDTO);

    JsonAmount getFeeShareAmountByCode(String businessCode);

    void updateBelongFlagByCode(String businessKey, Integer value);

    List<ForwarderFeeDTO> getListByPaymentCode(String code);

    Map<String, ForwarderFeeDTO> getListByCodes(List<String> relationCodes);

    Map<String, List<JsonAmount>> getAmountByShipmentCodes(List<String> relationCodes);
}
