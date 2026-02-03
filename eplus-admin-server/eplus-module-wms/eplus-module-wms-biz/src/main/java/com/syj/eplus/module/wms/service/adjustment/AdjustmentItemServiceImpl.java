package com.syj.eplus.module.wms.service.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.convert.adjustment.AdjustmentItemConvert;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import com.syj.eplus.module.wms.dal.mysql.adjustment.AdjustmentItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.ADJUSTMENT_ITEM_NOT_EXISTS;

/**
 * 仓储管理-盘库调整单-明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class AdjustmentItemServiceImpl implements AdjustmentItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private AdjustmentItemMapper adjustmentItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdjustmentItem(AdjustmentItemSaveReqVO createReqVO) {
        AdjustmentItemDO adjustmentItem = AdjustmentItemConvert.INSTANCE.convertAdjustmentItemDO(createReqVO);
        // 插入
        adjustmentItemMapper.insert(adjustmentItem);
        // 返回
        return adjustmentItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdjustmentItem(AdjustmentItemSaveReqVO updateReqVO) {
        // 校验存在
        validateAdjustmentItemExists(updateReqVO.getId());
        // 更新
        AdjustmentItemDO updateObj = AdjustmentItemConvert.INSTANCE.convertAdjustmentItemDO(updateReqVO);
        adjustmentItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteAdjustmentItem(Long id) {
        // 校验存在
        validateAdjustmentItemExists(id);
        // 删除
        adjustmentItemMapper.deleteById(id);
    }

    private void validateAdjustmentItemExists(Long id) {
        if (adjustmentItemMapper.selectById(id) == null) {
            throw exception(ADJUSTMENT_ITEM_NOT_EXISTS);
        }
    }

    @Override
    public AdjustmentItemRespVO getAdjustmentItem(Long id) {
        AdjustmentItemDO adjustmentItemDO = adjustmentItemMapper.selectById(id);
        if (adjustmentItemDO == null) {
            return null;
        }
        return AdjustmentItemConvert.INSTANCE.convertAdjustmentItemRespVO(adjustmentItemDO);
    }

    @Override
    public PageResult<AdjustmentItemDO> getAdjustmentItemPage(AdjustmentItemPageReqVO pageReqVO) {
        return adjustmentItemMapper.selectPage(pageReqVO);
    }

}
