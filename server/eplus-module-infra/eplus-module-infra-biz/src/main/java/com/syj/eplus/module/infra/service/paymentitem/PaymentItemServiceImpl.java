package com.syj.eplus.module.infra.service.paymentitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemPageReqVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemRespVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemSaveReqVO;
import com.syj.eplus.module.infra.convert.paymentitem.PaymentItemConvert;
import com.syj.eplus.module.infra.dal.dataobject.paymentitem.PaymentItemDO;
import cn.iocoder.yudao.module.system.dal.dataobject.paymentplan.SystemPaymentPlan;
import com.syj.eplus.module.infra.dal.mysql.paymentitem.PaymentItemMapper;
import cn.iocoder.yudao.module.system.dal.mysql.paymentplan.SystemPaymentPlanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.PAYMENT_NOT_EXISTS;

/**
 * 付款方式 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class PaymentItemServiceImpl implements PaymentItemService {

    @Resource
    private PaymentItemMapper paymentitemMapper;

    @Resource
    private SystemPaymentPlanMapper systemPaymentPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPayment(PaymentItemSaveReqVO createReqVO) {
        // 插入
        PaymentItemDO payment = BeanUtils.toBean(createReqVO, PaymentItemDO.class);
        paymentitemMapper.insert(payment);
        List<SystemPaymentPlanDTO> paymentPlanList = createReqVO.getSystemPaymentPlanList();
        if (CollUtil.isEmpty(paymentPlanList)) {
            return payment.getId();
        }
        List<SystemPaymentPlan> systemPaymentPlans = BeanUtils.toBean(paymentPlanList, SystemPaymentPlan.class);
        systemPaymentPlans.forEach(s -> s.setPaymentId(payment.getId()));
        systemPaymentPlanMapper.insertBatch(systemPaymentPlans);
        // 返回
        return payment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayment(PaymentItemSaveReqVO updateReqVO) {
        // 校验存在
        validatePaymentExists(updateReqVO.getId());
        // 更新
        PaymentItemDO updateObj = BeanUtils.toBean(updateReqVO, PaymentItemDO.class);
        paymentitemMapper.updateById(updateObj);
        List<SystemPaymentPlanDTO> paymentPlanList = updateReqVO.getSystemPaymentPlanList();
        systemPaymentPlanMapper.delete(SystemPaymentPlan::getPaymentId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(paymentPlanList)) {
            List<SystemPaymentPlan> systemPaymentPlans = BeanUtils.toBean(paymentPlanList, SystemPaymentPlan.class);
            systemPaymentPlans.forEach(s -> {
                s.setPaymentId(updateReqVO.getId());
                s.setId(null);
            });
            systemPaymentPlanMapper.insertBatch(systemPaymentPlans);
        }
    }

    @Override
    public void deletePayment(Long id) {
        // 校验存在
        validatePaymentExists(id);
        // 删除
        paymentitemMapper.deleteById(id);
    }

    private void validatePaymentExists(Long id) {
        if (paymentitemMapper.selectById(id) == null) {
            throw exception(PAYMENT_NOT_EXISTS);
        }
    }

    @Override
    public PaymentItemRespVO getPayment(Long id) {
        PaymentItemDO paymentItemDO = paymentitemMapper.selectById(id);
        if (Objects.isNull(paymentItemDO)) {
            return null;
        }
        PaymentItemRespVO paymentItemRespVO = PaymentItemConvert.INSTANCE.convertPaymentResp(paymentItemDO);
        List<SystemPaymentPlan> systemPaymentPlans = systemPaymentPlanMapper.selectList(SystemPaymentPlan::getPaymentId, id);
        List<SystemPaymentPlanDTO> systemPaymentPlanDTOList = PaymentItemConvert.INSTANCE.convertPaymentPlanDTOList(systemPaymentPlans);
        paymentItemRespVO.setSystemPaymentPlanList(systemPaymentPlanDTOList);
        return paymentItemRespVO;
    }

    @Override
    public PageResult<PaymentItemDO> getPaymentPage(PaymentItemPageReqVO pageReqVO) {
        return paymentitemMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PaymentItemDTO> getPaymentDTOListByIds(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<PaymentItemDO> paymentDOList = paymentitemMapper.selectBatchIds(idList);
        List<PaymentItemDTO> paymentItemDTOS = PaymentItemConvert.INSTANCE.convertPaymentDTOList(paymentDOList);
        List<SystemPaymentPlan> systemPaymentPlans = systemPaymentPlanMapper.selectList(SystemPaymentPlan::getPaymentId, idList);
        if (CollUtil.isNotEmpty(systemPaymentPlans)){
            Map<Long, List<SystemPaymentPlan>> paymentPlanMap = systemPaymentPlans.stream().collect(Collectors.groupingBy(SystemPaymentPlan::getPaymentId));
            paymentItemDTOS.forEach(s->{
                List<SystemPaymentPlan> systemPaymentPlansByPaymentPlan = paymentPlanMap.get(s.getId());
                if (CollUtil.isNotEmpty(systemPaymentPlansByPaymentPlan)){
                    s.setSystemPaymentPlanDTOList(BeanUtils.toBean(systemPaymentPlansByPaymentPlan, SystemPaymentPlanDTO.class));
                }
            });
        }
        return paymentItemDTOS;
    }

    @Override
    public PaymentItemDTO getPaymentDTOById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        PaymentItemDO paymentItemDO = paymentitemMapper.selectById(id);
        if (Objects.isNull(paymentItemDO)) {
            return null;
        }
        PaymentItemDTO paymentItemDTO = PaymentItemConvert.INSTANCE.convertPaymentDTO(paymentItemDO);
        List<SystemPaymentPlan> systemPaymentPlans = systemPaymentPlanMapper.selectList(SystemPaymentPlan::getPaymentId, id);
        List<SystemPaymentPlanDTO> systemPaymentPlanDTOList = PaymentItemConvert.INSTANCE.convertPaymentPlanDTOList(systemPaymentPlans);
        paymentItemDTO.setSystemPaymentPlanDTOList(systemPaymentPlanDTOList);
        return paymentItemDTO;
    }

    @Override
    public Map<Long, PaymentItemDTO> getPaymentDTOMap() {
        List<PaymentItemDO> paymentDOList = paymentitemMapper.selectList();
        if (CollUtil.isEmpty(paymentDOList)){
            return Map.of();
        }
        return paymentDOList.stream().collect(Collectors.toMap(PaymentItemDO::getId, PaymentItemConvert.INSTANCE::convertPaymentDTO));
    }

}