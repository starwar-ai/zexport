package com.syj.eplus.module.wms.service.stocktake;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import com.syj.eplus.module.wms.convert.stocktake.StocktakeItemConvert;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import com.syj.eplus.module.wms.dal.mysql.stocktake.StocktakeItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.STOCKTAKE_ITEM_NOT_EXISTS;

/**
 * 仓储管理-盘点单-明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StocktakeItemServiceImpl extends ServiceImpl<StocktakeItemMapper,StocktakeItemDO> implements StocktakeItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StocktakeItemMapper stocktakeItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStocktakeItem(StocktakeItemSaveReqVO createReqVO) {
        StocktakeItemDO stocktakeItem = StocktakeItemConvert.INSTANCE.convertStocktakeItemDO(createReqVO);
        // 插入
        stocktakeItemMapper.insert(stocktakeItem);
        // 返回
        return stocktakeItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStocktakeItem(StocktakeItemSaveReqVO updateReqVO) {
        // 校验存在
        validateStocktakeItemExists(updateReqVO.getId());
        // 更新
        StocktakeItemDO updateObj = StocktakeItemConvert.INSTANCE.convertStocktakeItemDO(updateReqVO);
        stocktakeItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteStocktakeItem(Long id) {
        // 校验存在
        validateStocktakeItemExists(id);
        // 删除
        stocktakeItemMapper.deleteById(id);
    }

    private void validateStocktakeItemExists(Long id) {
        if (stocktakeItemMapper.selectById(id) == null) {
            throw exception(STOCKTAKE_ITEM_NOT_EXISTS);
        }
    }

    @Override
    public StocktakeItemRespVO getStocktakeItem(Long id) {
        StocktakeItemDO stocktakeItemDO = stocktakeItemMapper.selectById(id);
        if (stocktakeItemDO == null) {
            return null;
        }
        return StocktakeItemConvert.INSTANCE.convertStocktakeItemRespVO(stocktakeItemDO);
    }

    @Override
    public PageResult<StocktakeItemDO> getStocktakeItemPage(StocktakeItemPageReqVO pageReqVO) {
        return stocktakeItemMapper.selectPage(pageReqVO);
    }

}
