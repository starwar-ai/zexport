package com.syj.eplus.module.fms.api.receipt;

import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.fms.api.payment.api.receipt.ReceiptApi;
import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.convert.receipt.ReceiptConvert;
import com.syj.eplus.module.fms.service.receipt.ReceiptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/19 16:18
 */
@Service
public class ReceiptApiImpl implements ReceiptApi {
    @Resource
    private ReceiptService receiptService;

    @Override
    public void createReceipt(ReceiptCreateDTO receiptCreateDTO) {
        ReceiptSaveReqVO receiptSaveReqVO = ReceiptConvert.INSTANCE.convertReceiptSaveReqByDTO(receiptCreateDTO);
        if (Objects.nonNull(receiptSaveReqVO)){
            receiptSaveReqVO.setSubmitFlag(SubmitFlagEnum.SUBMIT.getStatus());
        }
        receiptService.createReceipt(receiptSaveReqVO);
    }

    @Override
    public Long getOrderNumByBusinessCode(Integer businessType, String businessCode) {
        return receiptService.getOrderNumByBusinessCode(businessType, businessCode);
    }
}
