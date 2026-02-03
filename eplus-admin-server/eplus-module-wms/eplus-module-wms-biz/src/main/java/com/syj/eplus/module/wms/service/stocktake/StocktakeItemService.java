package com.syj.eplus.module.wms.service.stocktake;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;

import javax.validation.Valid;

/**
 * 仓储管理-盘点单-明细 Service 接口
 *
 * @author ePlus
 */
public interface StocktakeItemService extends IService<StocktakeItemDO> {

    /**
     * 创建仓储管理-盘点单-明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStocktakeItem(@Valid StocktakeItemSaveReqVO createReqVO);

    /**
     * 更新仓储管理-盘点单-明细
     *
     * @param updateReqVO 更新信息
     */
    void updateStocktakeItem(@Valid StocktakeItemSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-盘点单-明细
     *
     * @param id 编号
     */
    void deleteStocktakeItem(Long id);

    /**
     * 获得仓储管理-盘点单-明细
     *
     * @param id 编号
     * @return 仓储管理-盘点单-明细
     */
    StocktakeItemRespVO getStocktakeItem(Long id);

    /**
     * 获得仓储管理-盘点单-明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-盘点单-明细分页
     */
    PageResult<StocktakeItemDO> getStocktakeItemPage(StocktakeItemPageReqVO pageReqVO);
}
