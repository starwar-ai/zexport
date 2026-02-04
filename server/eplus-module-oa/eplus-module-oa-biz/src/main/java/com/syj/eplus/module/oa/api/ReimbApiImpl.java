package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/28 14:55
 */
@Service
public class ReimbApiImpl implements ReimbApi {

    @Resource
    private ReimbService reimbService;

    @Override
    public void updatePaymentStatus(PaymentAppDTO paymentAppDTO) {
        reimbService.updatePaymentMessage(paymentAppDTO);
    }

    @Override
    public void batchUpdatePaymentStatus(List<PaymentAppDTO> paymentAppDTOList) {
        reimbService.batchUpdatePaymentStatus(paymentAppDTOList);
    }

    @Override
    public void createReimbDetail(ReimbDetailDTO reimbDetailDTO) {
        reimbService.createReimbDetail(reimbDetailDTO);
    }

    @Override
    public List<ReimbDetailDTO> getReimbDetail() {
        return reimbService.getReimbDetail();
    }
}
