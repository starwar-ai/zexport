package com.syj.eplus.module.wms.service.transferorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderPageReqVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderRespVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.transferorder.TransferOrderDO;

import javax.validation.Valid;

/**
 * 调拨 Service 接口
 *
 * @author du
 */
public interface TransferOrderService {

    /**
     * 创建调拨
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTransferOrder(@Valid TransferOrderSaveReqVO createReqVO);

    /**
     * 更新调拨
     *
     * @param updateReqVO 更新信息
     */
    void updateTransferOrder(@Valid TransferOrderSaveReqVO updateReqVO);

    /**
     * 删除调拨
     *
     * @param id 编号
     */
    void deleteTransferOrder(Long id);

    /**
     * 获得调拨
     *
     * @param id 编号
     * @return 调拨
     */
    TransferOrderRespVO getTransferOrder(Long id);

    /**
     * 获得调拨分页
     *
     * @param pageReqVO 分页查询
     * @return 调拨分页
     */
    PageResult<TransferOrderDO> getTransferOrderPage(TransferOrderPageReqVO pageReqVO);

    /**
     * 提交
     *
     * @param id 调拨单主键
     */
    void submitTransferOrder(Long id);
}