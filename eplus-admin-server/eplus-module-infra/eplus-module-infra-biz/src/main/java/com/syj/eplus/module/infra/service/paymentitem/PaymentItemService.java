package com.syj.eplus.module.infra.service.paymentitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemPageReqVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemRespVO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.paymentitem.PaymentItemDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 付款方式 Service 接口
 *
 * @author ePlus
 */
public interface PaymentItemService {

    /**
     * 创建付款方式
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPayment(@Valid PaymentItemSaveReqVO createReqVO);

    /**
     * 更新付款方式
     *
     * @param updateReqVO 更新信息
     */
    void updatePayment(@Valid PaymentItemSaveReqVO updateReqVO);

    /**
     * 删除付款方式
     *
     * @param id 编号
     */
    void deletePayment(Long id);

    /**
     * 获得付款方式
     *
     * @param id 编号
     * @return 付款方式
     */
    PaymentItemRespVO getPayment(Long id);

    /**
     * 获得付款方式分页
     *
     * @param pageReqVO 分页查询
     * @return 付款方式分页
     */
    PageResult<PaymentItemDO> getPaymentPage(PaymentItemPageReqVO pageReqVO);

    /**
     * 获得付款方式列表
     *
     * @param idList 编号列表
     * @return 付款方式列表
     */
    List<PaymentItemDTO> getPaymentDTOListByIds(List<Long> idList);

    /**
     * 获得付款方式
     *
     * @param id 编号
     * @return 付款方式
     */
    PaymentItemDTO getPaymentDTOById(Long id);

    /**
     * 获取付款方式缓存
     * @return 付款方式缓存
     */
    Map<Long, PaymentItemDTO> getPaymentDTOMap();
}