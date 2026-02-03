package com.syj.eplus.module.oa.api.feeshare;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareItemDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;

import java.util.List;
import java.util.Map;


public interface FeeShareApi {


    void updateFeeShareByDTO(FeeShareReqDTO feeShare);
    void updatePreFeeShareByDTOList(List<FeeShareReqDTO> list);
    void updateFeeShareByDTOList(List<FeeShareReqDTO> list);

    List<FeeShareRespDTO> getFeeShareDTO(FeeShareSourceTypeEnum type, String code);
    List<FeeShareRespDTO> getPreFeeShareDTO(FeeShareSourceTypeEnum type, String code);
    /**
     * 修改来源单据状态
     * @param businessType 来源单据类型
     * @param BusinessId 来源单据主键
     * @param sourceStatus 来源单据状态
     */
    void updateSourceStatus(Integer businessType,Long BusinessId, Integer sourceStatus);

    /**
     * 多币种转换为人民币
     * @param amountList 多币种金额
     * @return 人民币金额
     */
    JsonAmount getFeeShareAmount(List<JsonAmount> amountList);

    Map<String, List<FeeShareRespDTO>> getFeeShareByCodeList(Integer value, List<String> codeList);

    /**
     * 修改支付状态
     * @param businessType 来源单据类型
     * @param BusinessCode 来源单据主键
     * @param paymentStatus 来源单据状态
     */
    void updatePaymentStatus(Integer businessType,String BusinessCode, Integer paymentStatus);

    void deleteByCodes(FeeShareSourceTypeEnum shipmentForwarderFee, List<String> forwarderFeeCodes);

    List<FeeShareItemDTO> getFeeShareItemByShareId(Long id);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);
}
