package com.syj.eplus.module.oa.api.feeshare;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareItemDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import com.syj.eplus.module.oa.service.feeshare.FeeShareService;
import com.syj.eplus.module.oa.service.feeshareitem.FeeShareItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class FeeShareApiImpl implements FeeShareApi {

    @Resource
    private FeeShareService feeShareService;
    @Resource
    private FeeShareItemService feeShareItemService;
    @Override
    public void updatePreFeeShareByDTOList(List<FeeShareReqDTO> list) {
        feeShareService.updateFeeShareByDTOList(list,true); //预费用归集
    }
    @Override
    public void updateFeeShareByDTOList(List<FeeShareReqDTO> list) {
        feeShareService.updateFeeShareByDTOList(list,false); //费用归集
    }
    @Override
    public void updateFeeShareByDTO(FeeShareReqDTO feeShare) {
        feeShareService.updateFeeShareByDTO(feeShare);//费用归集
    }

    @Override
    public List<FeeShareRespDTO> getFeeShareDTO(FeeShareSourceTypeEnum type, String code) {
        return feeShareService.getFeeShareDTO(type.getValue(),code,false);//费用归集
    }
    @Override
    public List<FeeShareRespDTO> getPreFeeShareDTO(FeeShareSourceTypeEnum type, String code) {
        return feeShareService.getFeeShareDTO(type.getValue(),code,true);//预费用归集
    }
    @Override
    public void updateSourceStatus(Integer businessType, Long BusinessId, Integer sourceStatus) {
        feeShareService.updateSourceStatus(businessType, BusinessId, sourceStatus);
    }

    @Override
    public JsonAmount getFeeShareAmount(List<JsonAmount> amountList) {
        return feeShareService.getFeeShareAmount(amountList);
    }

    @Override
    public Map<String, List<FeeShareRespDTO>> getFeeShareByCodeList(Integer value, List<String> codeList) {
        return  feeShareService.getFeeShareByCodeList(value,codeList);

    }

    @Override
    public void updatePaymentStatus(Integer businessType, String BusinessCode, Integer paymentStatus) {
        feeShareService.updatePaymentStatus(businessType, BusinessCode, paymentStatus);
    }

    @Override
    public void deleteByCodes(FeeShareSourceTypeEnum shipmentForwarderFee, List<String> forwarderFeeCodes) {
        feeShareService.deleteByCodes(shipmentForwarderFee,forwarderFeeCodes);
    }

    @Override
    public List<FeeShareItemDTO> getFeeShareItemByShareId(Long id) {
        List<FeeShareItemDO> items = feeShareItemService.getFeeShareItemByShareId(id);
        return BeanUtils.toBean(items,FeeShareItemDTO.class);
    }

    @Override
    public boolean validateCustExists(String custCode) {
        return feeShareService.validateCustExists(custCode);
    }


}
