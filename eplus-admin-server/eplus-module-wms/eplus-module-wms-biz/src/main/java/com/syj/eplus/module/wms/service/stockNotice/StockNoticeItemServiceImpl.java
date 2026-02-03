package com.syj.eplus.module.wms.service.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemSaveReqVO;
import com.syj.eplus.module.wms.convert.stockNotice.StockNoticeItemConvert;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.dal.mysql.stockNotice.StockNoticeItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.RENOTICE_ITEM_NOT_EXISTS;

/**
 * 仓储管理-入(出)库通知单-明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StockNoticeItemServiceImpl implements StockNoticeItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StockNoticeItemMapper stockNoticeItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNoticeItem(StockNoticeItemSaveReqVO createReqVO) {
        StockNoticeItemDO noticeItem = StockNoticeItemConvert.INSTANCE.convertNoticeItemDO(createReqVO);
        // 插入
        stockNoticeItemMapper.insert(noticeItem);
        // 返回
        return noticeItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeItem(StockNoticeItemSaveReqVO updateReqVO) {
        // 校验存在
        validateNoticeItemExists(updateReqVO.getId());
        // 更新
        StockNoticeItemDO updateObj = StockNoticeItemConvert.INSTANCE.convertNoticeItemDO(updateReqVO);
        stockNoticeItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteNoticeItem(Long id) {
        // 校验存在
        validateNoticeItemExists(id);
        // 删除
        stockNoticeItemMapper.deleteById(id);
    }

    private void validateNoticeItemExists(Long id) {
        if (stockNoticeItemMapper.selectById(id) == null) {
            throw exception(RENOTICE_ITEM_NOT_EXISTS);
        }
    }

    @Override
    public StockNoticeItemRespVO getNoticeItem(Long id) {
        StockNoticeItemDO stockNoticeItemDO = stockNoticeItemMapper.selectById(id);
        if (stockNoticeItemDO == null) {
            return null;
        }
        return StockNoticeItemConvert.INSTANCE.convertNoticeItemRespVO(stockNoticeItemDO);
    }

    @Override
    public PageResult<StockNoticeItemDO> getNoticeItemPage(StockNoticeItemPageReqVO pageReqVO) {
        return stockNoticeItemMapper.selectPage(pageReqVO);
    }

}
