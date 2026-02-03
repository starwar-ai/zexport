package com.syj.eplus.module.wms.service.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;

import javax.validation.Valid;

/**
 * 仓储管理-入(出)库单-明细 Service 接口
 *
 * @author ePlus
 */
public interface BillItemService extends IService<BillItemDO> {

    /**
     * 创建仓储管理-入(出)库单-明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBillItem(@Valid BillItemSaveReqVO createReqVO);

    /**
     * 更新仓储管理-入(出)库单-明细
     *
     * @param updateReqVO 更新信息
     */
    void updateBillItem(@Valid BillItemSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-入(出)库单-明细
     *
     * @param id 编号
     */
    void deleteBillItem(Long id);

    /**
     * 获得仓储管理-入(出)库单-明细
     *
* @param  id 编号
     * @return 仓储管理-入(出)库单-明细
     */
BillItemRespVO getBillItem(Long id);

    /**
     * 获得仓储管理-入(出)库单-明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-入(出)库单-明细分页
     */
    PageResult<BillItemDO> getBillItemPage(BillItemPageReqVO pageReqVO);
}
